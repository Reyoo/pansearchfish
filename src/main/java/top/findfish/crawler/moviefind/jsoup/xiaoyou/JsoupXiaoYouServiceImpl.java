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
import top.findfish.crawler.util.Constant;
import top.findfish.crawler.util.InvalidUrlCheckingService;

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


    @Value("$(user.xiaoyou.yingmiao)")
    String xiaoyouUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        log.info("-------------->开始爬取 小悠<--------------------");

        Set<String> movieList = new HashSet<>();
        String url = "http://a.yuanxiao.net.cn" + "/?s=" + searchMovieName;

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


         Elements pTagAttr = document.select("a[href]");

            for (Element element : pTagAttr) {


                if (element.parentNode().childNodeSize()>1){

                if (element.parentNode().childNode(1).attr("href").contains("pan.baidu")){

                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();


                    if (element.parentNode().childNodeSize() >= 3) {
                        movieNameAndUrlModel.setWangPanPassword(element.parentNode().childNode(2).toString().replaceAll("&nbsp;","").trim());
                    }else {
                        movieNameAndUrlModel.setWangPanPassword("");
                    }

                    //判断片名是否需要拼接
                    int indexName = element.parentNode().childNode(0).toString().indexOf(".视频");
                    if (indexName != -1) {
                        movieNameAndUrlModel.setMovieName(movieName +"  『"+ element.parentNode().childNode(0).toString().substring(0, indexName)+"』");
                    }
                    else if (!element.parentNode().childNode(0).toString().contains("视频")){
                        movieNameAndUrlModel.setMovieName(movieName+"  『"+element.parentNode().childNode(0).toString().replaceAll("：","")+"』");
                    }
                    else {
                        movieNameAndUrlModel.setMovieName(movieName);
                    }


                    movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
                    movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                    list.add(movieNameAndUrlModel);


//                    //判断片名是否需要拼接
//                    int indexName = element.parentNode().childNode(0).toString().indexOf(".视频");
//                    if (indexName == -1) {
//                        movieNameAndUrlModel.setMovieName(movieName);
//                    } else {
//                        movieNameAndUrlModel.setMovieName(movieName +"  『"+ element.parentNode().childNode(0).toString().substring(0, indexName)+"』");
//                    }
//
//                    //非视频资源更新片名，例如：小说，漫画等形式资源
//                    if (!element.parentNode().childNode(0).toString().contains("视频")){
//                        movieNameAndUrlModel.setMovieName(movieName+element.parentNode().childNode(0).toString().replaceAll(":",""));
//                    }
//
//                    movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
//                    movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
//                    list.add(movieNameAndUrlModel);

                }
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
                    movieNameAndUrlModelList.addAll(getWangPanUrl(url,proxyIpAndPort));
                }

                //筛选爬虫链接
//                invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou", movieNameAndUrlModelList);
                //插入更新可用数据
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, Constant.XIAOYOU_TABLENAME);

                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(Constant.XIAOYOU_TABLENAME, searchMovieName);

                //筛选数据库链接
                redisTemplate.opsForValue().set("xiaoyou:"+ searchMovieName , invalidUrlCheckingService.checkDataBaseUrl(Constant.XIAOYOU_TABLENAME, movieNameAndUrlModels), Duration.ofHours(2L));

            }
        } catch (Exception e) {
            log.error("docment is null ->" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(Constant.XIAOYOU_TABLENAME);
    }
}
