package top.findfish.crawler.moviefind.jsoup.hall.fourth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import top.findfish.crawler.common.HallFourthResult;
import top.findfish.crawler.moviefind.jsoup.hall.fourth.HallFourthDataDetailModel;
import top.findfish.crawler.constant.WebPageTagConstant;
import top.findfish.crawler.constant.XiaoYouConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.util.RestTemplateUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.FindFishUserAgentUtil;
import top.findfish.crawler.util.WebPageConstant;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 项目名: crawler
 * 文件名: JsoupLiLiServiceImpl
 * 创建者: HS
 * 创建时间:2022/1/5 11:16
 * 描述: 莉莉网盘站爬取
 */
@Service("jsoupHallFourthServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupHallFourthServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;

    //    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.fourthUrl}")
    String hallFourthUrl;

    private final static String PAGE_NO_STR = "&pageNo=1";

    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        return null;
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String movieUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {
        return null;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "application/json");
        httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
        httpHeaders.add("User-Agent", FindFishUserAgentUtil.randomUserAgent());
        httpHeaders.add("X-Requested-With", "XMLHttpRequest");
        String url = hallFourthUrl.concat(searchMovieName).concat(PAGE_NO_STR);
        String resultStr = null;
        HallFourthResult hallFourthResult = null;
        try {
            if (useProxy) {
                hallFourthResult = RestTemplateUtils.sendSimpleWithProxy(url, null, httpHeaders, proxyIpAndPort);
            } else {
                hallFourthResult = RestTemplateUtils.sendSimpleWithBody(url, null, HttpMethod.GET, httpHeaders);
            }

            if (hallFourthResult == null) {
                return;
            }
            System.out.println(hallFourthResult.getData().getTotal());
            List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
            List<HallFourthDataDetailModel> resultDataList = hallFourthResult.getData().getList();
            resultDataList.parallelStream().forEach(hallFourthDataDetailModel -> {
//            hallFourthDataDetailModel.get
                hallFourthDataDetailModel.getFileInfos().forEach(fileInfo -> {
                    if (fileInfo.getFileName().contains("epub")) {
                        return;
                    }
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    movieNameAndUrlModel.setMovieName(fileInfo.getFileName());
                    movieNameAndUrlModel.setPanSource(WebPageTagConstant.getValue(hallFourthDataDetailModel.getFrom()));
                    movieNameAndUrlModel.setWangPanUrl(hallFourthDataDetailModel.getUrl());
                    movieNameAndUrlModel.setMovieUrl(url);
                    movieNameAndUrlModel.setTitleName("视频:");
                    movieNameAndUrlModel.setWangPanPassword("");
                    movieNameAndUrlModels.add(movieNameAndUrlModel);
                });
            });


            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels, WebPageConstant.HALL_FOURTH_TABLENAME, proxyIpAndPort);
            redisTemplate.opsForValue().set(XiaoYouConstant.HALL_FOURTH_CACHE.getType() + searchMovieName,
                    movieNameAndUrlModels, Duration.ofHours(2L));
        } catch (Exception e) {
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
            log.error(e.getMessage());
        }
    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.HALL_FOURTH_TABLENAME);
    }

}
