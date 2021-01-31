package top.findfish.crawler.moviefind.jsoup.xiaoyou;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.InvalidUrlCheckingService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 项目名: pan
 * 文件名: XiaoYouService
 * 创建者: HS
 * 创建时间:2021/1/18 16:02
 * 描述: TODO
 */
@Service("jsoupXiaoyouServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupXiaoYouServiceImpl implements ICrawlerCommonService {


    private final RedisTemplate redisTemplate;

    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;


    @Value("$(user.xiaoyou.yingmiao)")
    String xiaoyouUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        log.info("-------------->开始爬取 小悠<--------------------");

        Set<String> movieList = new HashSet<>();
        String url = "http://y.yuanxiao.net.cn" + "/?s=" + searchMovieName;

        try {
            Document document = JsoupFindfishUtils.getDocument(url, proxyIpAndPort);

            //拿到查询结果 片名及链接
            Elements elements = document.getElementById("container").getElementsByClass("entry-title");

            for (Element element : elements) {
                String movieUrl = element.select("a").attr("href");
                //判断是一层链接还是两层链接
                if (StringUtils.isBlank(movieUrl)) {
                    movieList.add(url);
                } else {
                    movieList.add(movieUrl);
                }
            }
            return movieList;
        } catch (Exception e) {
            log.error("line 210 ->" + e.getMessage());
            return movieList;
        }
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();


            Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort);
            String movieName = document.getElementsByTag("title").first().text();

            if (movieName.contains("- 小悠家")) {
                movieName = movieName.split("- 小悠家")[0];
            }

            Elements pTagAttr = document.getElementsByTag("p");

            for (Element element : pTagAttr) {
                if (element.select("a").attr("href").contains("pan.baidu")) {
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();

                    if (element.childNodeSize() == 3) {
                        String password = element.childNode(2).toString().replaceAll("&nbsp;", "");
                        movieNameAndUrlModel.setWangPanPassword(password);
                    }

                    //判断片名是否需要拼接
                    int indexName = element.childNode(0).toString().indexOf(".视频：");
                    if (indexName == -1) {
                        movieNameAndUrlModel.setMovieName(movieName);
                    } else {
                        movieNameAndUrlModel.setMovieName(movieName + element.childNode(0).toString().substring(0, indexName));
                    }
                    movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
                    movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                    list.add(movieNameAndUrlModel);
                } else {
                    continue;
                }
        }
        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {

        log.info("-------------->开始爬取 影喵儿<--------------------");
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort);
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    movieNameAndUrlModelList.addAll(getWangPanUrl(url,proxyIpAndPort));
                }
                //更新前从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_xiaoyou", searchMovieName);
                invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou", movieNameAndUrlModels);
                List<MovieNameAndUrlModel> couldBeFindUrls = invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou", movieNameAndUrlModelList);
                if (couldBeFindUrls.size()>0) {
                    //存入数据库
                    movieNameAndUrlService.addOrUpdateMovieUrls(couldBeFindUrls, "url_movie_xiaoyou");
                    //存入redis
                    redisTemplate.opsForHash().put("xiaoyoumovie", searchMovieName, couldBeFindUrls);
                    redisTemplate.expire(searchMovieName, 60, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            log.error("docment is null ->" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }
}
