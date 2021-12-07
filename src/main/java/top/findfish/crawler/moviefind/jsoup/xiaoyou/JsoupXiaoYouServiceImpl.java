package top.findfish.crawler.moviefind.jsoup.xiaoyou;


import cn.hutool.core.util.StrUtil;
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
import top.findfish.crawler.util.WebPageConstant;
import top.findfish.crawler.util.InvalidUrlCheckingService;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Value("${user.xiaoyou.url}")
    String xiaoyouUrl;

    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {

        log.info("-------------->开始爬取 小悠<--------------------");

        Set<String> movieList = new HashSet<>();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF8");
        String url = "http://888.xuj.cool/" + "/?s=" + encode;


        try {

            Document document = JsoupFindfishUtils.getDocument(url, proxyIpAndPort);
//            Document document = Jsoup.connect(url).get();

            //拿到查询结果 片名及链接
             Elements elements = document.getElementsByTag("a");

            elements.stream().forEach(element -> {
                String a = element.select("a").attr("href");

                if (a.contains("yuanxiao.net.cn/STMP/20") || a.contains("xuj.cool/STMP/20")) {
                    movieList.add(a);
                }

//                if (a.contains(xiaoyouUrl) && !a.contains("#") && !a.contains("category") && a.length() != 25) {
//                    movieList.add(a);
//                }
            });

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
//        Document document = Jsoup.connect(secondUrlLxxh).get();



        String movieName = document.getElementsByTag("title").first().text();

        if (movieName.contains("- 小悠家")) {
            movieName = movieName.split("- 小悠家")[0];
        }


        Elements pTagAttr = document.select("p");

        for (Element element : pTagAttr) {
            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
//                System.out.println(element.text());
            if (element.text().contains("视频")) {
                System.out.println(element.text());
                if (StringUtils.isBlank(element.getElementsByTag("a").attr("href")) || element.text().contains("在线播放")){
                    break;
                }
                movieNameAndUrlModel.setWangPanUrl(element.getElementsByTag("a").attr("href"));

            }

            if (element.text().contains("提取码：")) {
//                    System.out.println(element.text());
                movieNameAndUrlModel.setWangPanPassword(element.text().split("提取码：")[1].trim());
                movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                movieNameAndUrlModel.setMovieName(movieName);
                list.add(movieNameAndUrlModel);
                break;
            }


            if (StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanUrl())) {
                movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                movieNameAndUrlModel.setMovieName(movieName);
                list.add(movieNameAndUrlModel);
            }
        }

        return list;
    }


    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {

        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort);
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort));
                }

                //筛选爬虫链接
//                invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou", movieNameAndUrlModelList);
                //插入更新可用数据
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.XIAOYOU_TABLENAME);

                //删除无效数据
                movieNameAndUrlService.deleteUnAviliableUrl(movieNameAndUrlModelList, WebPageConstant.XIAOYOU_TABLENAME);

                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.XIAOYOU_TABLENAME, searchMovieName);

                //筛选数据库链接
                redisTemplate.opsForValue().set("xiaoyou:" + searchMovieName, invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.XIAOYOU_TABLENAME, movieNameAndUrlModels, proxyIpAndPort), Duration.ofHours(2L));

            }
        } catch (Exception e) {
            log.error("docment is null ->" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.XIAOYOU_TABLENAME);
    }
}
