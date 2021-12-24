package top.findfish.crawler.moviefind.jsoup.xiaoyou;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
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
import top.findfish.crawler.constant.WebPageTagConstant;
import top.findfish.crawler.constant.XiaoYouConstant;
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
import java.util.concurrent.CompletableFuture;

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
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        log.debug("-------------->开始爬取 小悠<--------------------");
        Set<String> movieList = new HashSet<>();
        String encode = URLEncoder.encode(searchMovieName.trim(), CharsetUtil.UTF_8);
        String url = xiaoyouUrl.concat(WebPageTagConstant.XIAOYOU_URL_PARAM.getType()).concat(encode);
        try {
            Document document = JsoupFindfishUtils.getDocument(url, proxyIpAndPort, useProxy);
            log.info(document.text());
            //拿到查询结果 片名及链接
            Elements elements = document.getElementsByTag(WebPageTagConstant.HTML_TAG_A.getType());
            elements.stream().forEach(element -> {
                String a = element.select(WebPageTagConstant.HTML_TAG_A.getType()).attr(WebPageTagConstant.HTML_TAG_HREF.getType());
                if (a.contains(XiaoYouConstant.XIAOYOU_URL_CHECKSTMP.getType()) || a.contains(XiaoYouConstant.XIAOYOU_URL_CHECKXUJ.getType())) {
                    if (a.contains("respond") || a.contains("comments")) {
                        return;
                    }
                    movieList.add(a);
                }
            });
            return movieList;
        } catch (Exception e) {
            log.error(e.getMessage());
            return movieList;
        }
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort, Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();
        Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort, useProxy);
        String movieName = document.getElementsByTag(WebPageTagConstant.HTML_TAG_TITLE.getType()).first().text();
        if (movieName.contains(XiaoYouConstant.XIAOYOU_STR_WITH_SGIN.getType())) {
            movieName = movieName.split(XiaoYouConstant.XIAOYOU_STR_WITH_SGIN.getType())[0];
        }
        Elements pTagAttr = document.select(WebPageTagConstant.HTML_TAG_P.getType());

        String finalMovieName = movieName;
        pTagAttr.parallelStream().forEach(element -> {

            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
            if (element.text().contains(XiaoYouConstant.XIAOYOU_SHIPIN.getType())) {
                if (StringUtils.isBlank(element.getElementsByTag(WebPageTagConstant.HTML_TAG_A.getType()).attr(WebPageTagConstant.HTML_TAG_HREF.getType())) || element.text().contains(WebPageTagConstant.ONLINE_SHOW.getType())) {
                    return;
                }
                movieNameAndUrlModel.setWangPanUrl(element.getElementsByTag(WebPageTagConstant.HTML_TAG_A.getType()).attr(WebPageTagConstant.HTML_TAG_HREF.getType()));
            }
            if (element.text().contains(XiaoYouConstant.XIAOYOU_TIQUMA.getType())) {
                movieNameAndUrlModel.setWangPanPassword(element.text().split(XiaoYouConstant.XIAOYOU_TIQUMA.getType())[1].trim())
                        .setMovieUrl(secondUrlLxxh)
                        .setMovieName(finalMovieName);
                list.add(movieNameAndUrlModel);
                return;
            }

            if (StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanUrl())) {
                movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                movieNameAndUrlModel.setMovieName(finalMovieName);
                list.add(movieNameAndUrlModel);
            }

        });

        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort, useProxy);
            if (CollectionUtil.isNotEmpty(set)) {
                set.parallelStream().forEach(url -> {
                    try {
                        movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort, useProxy));
                        movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.XIAOYOU_TABLENAME);
                        //删除无效数据  删除是要做的 删除的主要用处在于电视剧更新 级数问题。 后面应当抓到删除的逻辑 或者定时批量删除
                    /** movieNameAndUrlService.deleteUnAviliableUrl(movieNameAndUrlModelList, WebPageConstant.XIAOYOU_TABLENAME);*/
                        CompletableFuture<List<MovieNameAndUrlModel>> completableFuture = CompletableFuture.supplyAsync(() -> movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.XIAOYOU_TABLENAME, searchMovieName));
                        List<MovieNameAndUrlModel> movieNameAndUrlModels = completableFuture.get();
                        completableFuture.thenRun(() -> {
                            try {
                                redisTemplate.opsForValue().set(XiaoYouConstant.XIAOYOU.getType() + searchMovieName,
                                        invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.XIAOYOU_TABLENAME, movieNameAndUrlModels, proxyIpAndPort), Duration.ofHours(2L));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        log.error(e.getMessage());
//                        e.printStackTrace();
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
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.XIAOYOU_TABLENAME);
    }
}
