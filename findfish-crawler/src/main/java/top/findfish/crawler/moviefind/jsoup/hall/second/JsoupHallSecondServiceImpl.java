package top.findfish.crawler.moviefind.jsoup.hall.second;


import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.CacheConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.util.JudgeUrlSourceUtil;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.WebPageConstant;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("jsoupHallSecondServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupHallSecondServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.secondUrl}")
    String url;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        Set<String> firstSearchUrls = new HashSet<>();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF-8");
        firstSearchUrls.add(url.concat(encode));
        return firstSearchUrls;
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {
            Document doc = JsoupFindfishUtils.getDocument(secondUrl, proxyIpAndPort, useProxy);
            Elements elements = doc.select("a[class=copy]");
            String movieName = doc.select("a[class=open]").first().childNode(0).toString().trim();
            for (Element element : elements) {
                //pan.xunlei.com/s/VNEu9QHiPI1s4yPxusaYk_muA1?pwd=f9zx#
                //pan.baidu.com/s/10GKqvuLM3zrfJueRVOBJDg?pwd=segt
                String textInfo = element.attr("data-code").trim().split("https://")[1].split(" ")[0];
                MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                movieNameAndUrlModel.setWangPanUrl("https://".concat(textInfo));
                if (textInfo.contains("?")) {
                    movieNameAndUrlModel.setWangPanPassword(textInfo.split("\\?")[1].split("=")[1].substring(0, 4));
                }
                if (movieName != null) {
                    movieNameAndUrlModel.setMovieName(movieName);
                }
                movieNameAndUrlModel.setPanSource(JudgeUrlSourceUtil.getSourceStr(textInfo));
                movieNameAndUrlModel.setMovieUrl(secondUrl);
                movieNameAndUrlModel.setTitleName("视频:");
                movieNameAndUrlModels.add(movieNameAndUrlModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return movieNameAndUrlModels;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        try {
            Set<String> firstSearchUrls = firstFindUrl(searchMovieName, proxyIpAndPort, useProxy);
            if (CollectionUtil.isNotEmpty(firstSearchUrls)) {
                ArrayList<MovieNameAndUrlModel> movieList = new ArrayList<>();
                firstSearchUrls.parallelStream().forEach(url -> {
                    try {
                        movieList.addAll(getWangPanUrl(url, proxyIpAndPort, useProxy));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                });
                //插入更新可用数据
                movieNameAndUrlService.addOrUpdateMovieUrls(movieList, WebPageConstant.HALL_SECOND_TABLENAME, proxyIpAndPort);
//                //删除无效数据
//                movieNameAndUrlService.deleteUnAviliableUrl(movieList, WebPageConstant.LEIFENGJUN_TABLENAME);
//                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.HALL_SECOND_TABLENAME, searchMovieName);
//                redisTemplate.opsForValue().set("xiaoyu::" + searchMovieName, invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.XIAOYU_TABLENAME, movieNameAndUrlModels, proxyIpAndPort),
//                        Duration.ofHours(2L));
                redisTemplate.opsForValue().set(CacheConstant.SECOND_HALL_CACHE_NAME.concat(searchMovieName), movieNameAndUrlModels, Duration.ofHours(2L));

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }

    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.HALL_SECOND_TABLENAME);
    }


    /**
     * 数据库初始化 time 循环次数
     *
     * @param times
     * @return
     */
    public List<MovieNameAndUrlModel> firstInitTidSumsuUrl(int times) {
        return null;
    }


}
