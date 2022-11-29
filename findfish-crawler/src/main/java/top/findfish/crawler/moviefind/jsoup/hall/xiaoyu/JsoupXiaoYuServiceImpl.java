package top.findfish.crawler.moviefind.jsoup.hall.xiaoyu;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.CacheConstant;
import top.findfish.crawler.constant.TbNameConstant;
import top.findfish.crawler.constant.WebPageTagConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.PanSourceUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;



@Service("jsoupHallXiaoYuServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupXiaoYuServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;

    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.secondUrl}")
    String XiaoYuHallUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        log.info("-------------->开始爬取 小宇  搜索片名: "+searchMovieName+" <--------------------");
        Set<String> movieList = new HashSet<>();
        String url = XiaoYuHallUrl.concat(WebPageTagConstant.XIAOYU_URL_PARAM.getType()).concat(searchMovieName.trim());
        try {
            Document document = JsoupFindfishUtils.getDocument(url, proxyIpAndPort, useProxy);
            if (document == null){
                log.info("小宇  document == null");
            }

            if (!document.title().contains("- 小宇搜索")){
                return null;
            }

            movieList.add(url);
            return movieList;

        } catch (Exception e) {
            log.error(e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
            return movieList;
        }
    }

    @Override
    public List<MovieNameAndUrlModel> getWangPanUrl(String movieUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();
        Document document = JsoupFindfishUtils.getDocument(movieUrl, proxyIpAndPort, useProxy);

        Elements elements = document.getElementsByClass("open");
        elements.stream().forEach(element -> {
            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
            String movieName = element.text();
            String password = element.attributes().get("data-code");

            if (StringUtils.isNotBlank(password)){
                password = WebPageTagConstant.TIQUMA_CHINA.getType()+password;
            }
            String panUrl = "https://"+ Base64Decoder.decodeStr(element.attributes().get("data-url"));
            String dataId = element.attributes().get("data-id");

            movieNameAndUrlModel.setWangPanUrl(panUrl);
            movieNameAndUrlModel.setMovieName(movieName);
            movieNameAndUrlModel.setWangPanPassword(password);
            movieNameAndUrlModel.setTitleName("视频：");
            String finalMovieUrl = movieUrl.split(WebPageTagConstant.XIAOYU_URL_PARAM.getType())[0];
            movieNameAndUrlModel.setMovieUrl(finalMovieUrl+WebPageTagConstant.XIAOYU_URL_PARAM.getType()+dataId);
            movieNameAndUrlModel.setPanSource(PanSourceUtil.panSource(panUrl));

            list.add(movieNameAndUrlModel);
        });

        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {

        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort, useProxy);
            if (CollectionUtil.isNotEmpty(set)) {
                set.stream().forEach(url -> {
                    try {
                        movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort, useProxy));
                        movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, TbNameConstant.HALL_SECOND_TABLENAME,proxyIpAndPort);
                        CompletableFuture<List<MovieNameAndUrlModel>> completableFuture = CompletableFuture.supplyAsync(() -> movieNameAndUrlMapper.selectMovieUrlByLikeName(TbNameConstant.HALL_SECOND_TABLENAME, searchMovieName));
                        List<MovieNameAndUrlModel> movieNameAndUrlModels = completableFuture.get();
                        completableFuture.thenRun(() -> {
                            try {
                                ArrayList arrayList = new ArrayList();
                                movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel ->{
                                    com.libbytian.pan.system.model.MovieNameAndUrlModel findFishMovieNameAndUrlModel = JSON.parseObject(JSON.toJSONString(movieNameAndUrlModel), com.libbytian.pan.system.model.MovieNameAndUrlModel.class);
                                    arrayList.add(findFishMovieNameAndUrlModel);
                                });

                                redisTemplate.opsForValue().set(CacheConstant.SECOND_HALL_CACHE_NAME.concat(searchMovieName),
                                        arrayList, Duration.ofHours(2L));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("docment is null ->" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }

    }

    @Override
    public void checkRepeatMovie() {
        return;
    }
}
