package top.findfish.crawler.moviefind.jsoup.unread;

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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: QiSun
 * @date: 2021-01-04
 * @Description: 未读影单 爬取类
 */
@Service("jsoupUnreadServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupUnReadServiceImpl implements ICrawlerCommonService {

    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final RedisTemplate redisTemplate;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        Set<String> movieUrlSet = new HashSet();

        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF8");
        String url = unreadUrl + "/?s=" + encode;
        Document doc = JsoupFindfishUtils.getDocument(url, proxyIpAndPort);
        log.info(doc.body().toString());

        Elements links = doc.select("a[href]");

        String pUrl = null;
        for (Element link : links) {
            pUrl = link.attr("href");
            if(pUrl.contains("http://www.unreadmovie.com/?p=")&& !pUrl.contains("#")){
                movieUrlSet.add(pUrl);
            }

        }

        return movieUrlSet;

    }


    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();


            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
            movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
;

            Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort);
                String name = document.getElementsByTag("title").text().trim();
            System.out.println(name);
            if (name.contains("– 未读影单")) {
                name = name.split("– 未读影单")[0].trim();
            }

            movieNameAndUrlModel.setMovieName(name);


            Elements attr = document.getElementsByTag("p");
            for (Element passwdelement : attr) {
                if (passwdelement.text().contains("密码")) {
                    movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
                    break;
                }

                if (passwdelement.text().contains("提取码")) {
                    movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
                    break;
                }

            }

            Elements links = document.select("a[href]");
            String wangPanUrl = null;
            for (Element link : links) {
                wangPanUrl = link.attr("href");
                if (wangPanUrl.contains("pan.baidu.com")) {
                    break;
                } else {
                    continue;
                }
            }

            movieNameAndUrlModel.setWangPanUrl(wangPanUrl);
            movieNameAndUrlModelList.add(movieNameAndUrlModel);

        return movieNameAndUrlModelList;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {

        log.info("-------------->开始爬取 未读<--------------------");
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();

        try {

            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort);
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort));
                }

                //更新前从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", searchMovieName);
                invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModels);


                List<MovieNameAndUrlModel> couldBeFindUrls = invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModelList);

                //存入数据库
                movieNameAndUrlService.addOrUpdateMovieUrls(couldBeFindUrls, "url_movie_unread");
                //存入redis
                redisTemplate.opsForHash().put("unreadmovie", searchMovieName, couldBeFindUrls);
            }
        } catch (Exception e) {
            log.error("docment is null" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }

}
