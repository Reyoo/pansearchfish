package top.findfish.crawler.moviefind.jsoup.unread;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
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
import top.findfish.crawler.util.WebPageConstant;

import java.net.URLEncoder;
import java.time.Duration;
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
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) throws Exception {
        Set<String> movieUrlSet = new HashSet();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF8");
        String url = unreadUrl + "/?s=" + encode;
        Document doc = JsoupFindfishUtils.getDocument(url, proxyIpAndPort,useProxy);
        Elements links = doc.getElementsByClass("entry-title");
        if (ObjectUtil.isNull(links)) {
            return movieUrlSet;
        }
        links.parallelStream().forEach(link -> {
            String pUrl = link.select("a").attr("href");
            if (pUrl.contains("http://www.unreadmovie.com/?p=") && !pUrl.contains("#")) {
                movieUrlSet.add(pUrl);
            }
        });
        return movieUrlSet;
    }


    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort,Boolean useProxy) throws Exception {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        Document document = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort,useProxy);
        String movieName = document.getElementsByTag("title").text().trim();
        if (movieName.contains("– 未读影单")) {
            movieName = movieName.split("– 未读影单")[0].trim();
        }
        if (StringUtils.isBlank(movieName)) {
            return movieNameAndUrlModelList;
        }

//        Elements pTagAttr = document.getElementsByClass("entry-content").select("a[href]");
        Elements pTagAttr = document.getElementsByClass("entry-content").tagName("div").select("p");

//        if (pEle.text().contains("资源链接点这里")) {
//            String panUrl = pEle.getElementsByTag("a").attr("href");
//            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
//            movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
//            movieNameAndUrlModel.setWangPanUrl(panUrl);
//            //判断是否需要拼接片名
//            if (pEle.text().startsWith("资源链接点这里→")) {
//                movieNameAndUrlModel.setMovieName(movieName);
//            } else {
//                movieNameAndUrlModel.setMovieName(movieName + "  『" + pEle.text().split("资源链接点这里")[0] + "』");
//            }
////                //java获得href中的值
////                String re = "(?<=(href=\")).*(?=\")";
////                Pattern compile = Pattern.compile(re);
////                Matcher matcher = compile.matcher(pTagAttr.get(i).toString());
////                while(matcher.find()) {
////                    String group = matcher.group();
////                    movieNameAndUrlModel.setWangPanUrl(group);
////                }
//            if (pEle.text().contains("密码") || pTagAttr.get(i + 1).text().contains("提取码")) {
//                movieNameAndUrlModel.setWangPanPassword(pTagAttr.get(i + 1).text().trim());
//            } else {
//                movieNameAndUrlModel.setWangPanPassword("");
//            }
//            movieNameAndUrlModelList.add(movieNameAndUrlModel);
//        }
//
//    });

        for (int i = 0; i < pTagAttr.size(); i++) {
            if (pTagAttr.get(i).text().contains("资源链接点这里")) {

                String panUrl = pTagAttr.get(i).getElementsByTag("a").attr("href");

                MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                movieNameAndUrlModel.setWangPanUrl(panUrl);

                //判断是否需要拼接片名
                if (pTagAttr.get(i).text().startsWith("资源链接点这里→")) {
                    movieNameAndUrlModel.setMovieName(movieName);
                } else {
                    movieNameAndUrlModel.setMovieName(movieName + "  『" + pTagAttr.get(i).text().split("资源链接点这里")[0] + "』");
                }
//                //java获得href中的值
//                String re = "(?<=(href=\")).*(?=\")";
//                Pattern compile = Pattern.compile(re);
//                Matcher matcher = compile.matcher(pTagAttr.get(i).toString());
//                while(matcher.find()) {
//                    String group = matcher.group();
//                    movieNameAndUrlModel.setWangPanUrl(group);
//                }
                if (pTagAttr.get(i + 1).text().contains("密码") || pTagAttr.get(i + 1).text().contains("提取码")) {
                    movieNameAndUrlModel.setWangPanPassword(pTagAttr.get(i + 1).text().trim());
                } else {
                    movieNameAndUrlModel.setWangPanPassword("");
                }
                movieNameAndUrlModelList.add(movieNameAndUrlModel);
            }

        }


        return movieNameAndUrlModelList;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) {

        log.info("-------------->开始爬取 未读<--------------------");
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();

        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort,useProxy);
            if (ObjectUtil.isNotEmpty(set)) {

                set.parallelStream().forEach(url -> {
                    try {
                        movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort,useProxy));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                if (movieNameAndUrlModelList.size() == 0) {
                    return;
                }

                //插入更新可用数据
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.WEIDU_TABLENAME);
                //删除无效数据
                movieNameAndUrlService.deleteUnAviliableUrl(movieNameAndUrlModelList, WebPageConstant.WEIDU_TABLENAME);

                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.WEIDU_TABLENAME, searchMovieName);
                //筛选数据库链接
                redisTemplate.opsForValue().set("unread:" + searchMovieName, invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.WEIDU_TABLENAME, movieNameAndUrlModels,
                        proxyIpAndPort), Duration.ofHours(2L));

            }

        } catch (Exception e) {
            log.error("docment is null" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }

    @Override
    public void checkRepeatMovie() {

    }


}
