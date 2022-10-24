package top.findfish.crawler.moviefind.jsoup.sumsu;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.regexp.RE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.checkurl.service.InvalidUrlCheckingService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.moviefind.util.JudgeUrlSourceUtil;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.FindFishUserAgentUtil;
import top.findfish.crawler.util.FindfishStrUtil;
import top.findfish.crawler.util.WebPageConstant;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service("jsoupSumuServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupSumsuServiceImpl implements ICrawlerCommonService {

    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;


    @Value("${user.sumsu.url}")
    String url;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        log.info("-------------->开始爬取 社区动力<--------------------");
        Set<String> firstSearchUrls = new HashSet<>();
        firstSearchUrls.add(url.concat(searchMovieName));
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
                movieNameAndUrlService.addOrUpdateMovieUrls(movieList, WebPageConstant.XIAOYU_TABLENAME, proxyIpAndPort);
//                //删除无效数据
//                movieNameAndUrlService.deleteUnAviliableUrl(movieList, WebPageConstant.LEIFENGJUN_TABLENAME);
//                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.XIAOYU_TABLENAME, searchMovieName);
                redisTemplate.opsForValue().set("xiaoyu::" + searchMovieName, invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.XIAOYU_TABLENAME, movieNameAndUrlModels, proxyIpAndPort),
                        Duration.ofHours(2L));

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }

    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.XIAOYU_TABLENAME);
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
