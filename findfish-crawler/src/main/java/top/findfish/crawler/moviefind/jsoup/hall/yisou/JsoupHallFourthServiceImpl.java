package top.findfish.crawler.moviefind.jsoup.hall.yisou;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.CacheConstant;
import top.findfish.crawler.constant.TbNameConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.FindFishUserAgentUtil;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名: crawler
 * 文件名: JsoupLiLiServiceImpl
 * 创建者: HS
 * 创建时间:2022/1/5 11:16
 * 描述: 莉莉网盘站爬取
 */
@Service("jsoupHallYiSouServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupHallFourthServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.fourthUrl}")
    String hallFourthUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        Set<String> movieUrlSet = new HashSet();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF-8");
        String url = hallFourthUrl.concat(encode);
        movieUrlSet.add(url);
        return movieUrlSet;

    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String movieUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList<>();
        String charset = JsoupFindfishUtils.getCharset(movieUrl);

        try {
            Document document = JsoupFindfishUtils.getDocumentWithCharset(movieUrl, proxyIpAndPort, useProxy, charset);
            // Select all <div class="list"> elements
            Elements listElements = document.select("div.list");
            // Iterate through each <div class="list"> element
            for (Element listElement : listElements) {
                // Select all <li> elements within the current <div class="list">
                Elements liElements = listElement.select("li");

                String movieName = "";
                // Iterate through each <li> element
                for (Element liElement : liElements) {
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    movieNameAndUrlModel.setMovieUrl(movieUrl);
                    // Select the <a> element within the <li>
                    Element aElement = liElement.selectFirst("a");
                    // Select the <p> element within the <li>
                    Element pElement = liElement.selectFirst("li p a");
                    String movieInfo = liElement.select("a").text();

                    if (!movieInfo.contains("链接") && StrUtil.isNotBlank(movieInfo)) {
                        System.out.println("电影名  " + movieInfo);
                        movieName = movieInfo;
                    }
                    if (aElement != null && pElement != null) {
                        String downloadLink = pElement.attr("href");
                        movieNameAndUrlModel.setMovieName(movieName);
                        movieNameAndUrlModel.setWangPanUrl(downloadLink);
                        System.out.println("网盘地址  " + downloadLink);
                        list.add(movieNameAndUrlModel);
                    }

                }

            }

        } catch (Exception e) {
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
            log.error(e.getMessage());
        }
        return list;

    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort, useProxy);

            if (ObjectUtil.isNotEmpty(set)) {
                set.parallelStream().forEach(url -> {
                    try {
                        ArrayList<MovieNameAndUrlModel> wangPanUrl = getWangPanUrl(url, proxyIpAndPort, useProxy);
                        movieNameAndUrlModelList.addAll(wangPanUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if (CollectionUtil.isEmpty(movieNameAndUrlModelList)) {
                    return;
                }
                //插入更新可用数据
//                movieNameAndUrlService.addOrUpdateMovieUrlsWithTitleName(movieNameAndUrlModelList, WebPageConstant.WEIDU_TABLENAME,proxyIpAndPort);
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, TbNameConstant.HALL_FOURTH_TABLENAME, proxyIpAndPort);

                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(TbNameConstant.HALL_FOURTH_TABLENAME, searchMovieName);

                ArrayList arrayList = new ArrayList();
                movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel -> {
                    com.libbytian.pan.system.model.MovieNameAndUrlModel findFishMovieNameAndUrlModel = JSON.parseObject(JSON.toJSONString(movieNameAndUrlModel), com.libbytian.pan.system.model.MovieNameAndUrlModel.class);
                    arrayList.add(findFishMovieNameAndUrlModel);
                });

                //筛选数据库链接
                redisTemplate.opsForValue().set(CacheConstant.FOURTH_HALL_CACHE_NAME.concat(searchMovieName), arrayList, Duration.ofHours(2L));
            }
        } catch (Exception e) {
            log.error("docment is null" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }

    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(TbNameConstant.HALL_FOURTH_TABLENAME);
    }

}
