package top.findfish.crawler.moviefind.httpclient.aidianying;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.proxy.service.PhantomJsProxyCallService;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.InvalidUrlCheckingService;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: AiDianyingService
 * @Author: sun71
 * @Description: 爱电影爬虫获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AiDianyingService {


    private final RedisTemplate redisTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final PhantomJsProxyCallService phantomJsProxyCallService;


    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    public Set<String> firstFindLxxhUrl(String searchMovieName, String proxyIpAndPort) throws Exception {


        Set<String> movieUrlInLxxh = new HashSet();
        String encode = null;
        try {
            encode = URLEncoder.encode(searchMovieName, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(encode);
        String urlAiDianying = lxxhUrl + "/?s=" + encode;
        //采用phantomJs 无界浏览器形式访问

        WebDriver firstBrowserDriver = null;
        Document document = null;
        try {
            firstBrowserDriver = phantomJsProxyCallService.create(urlAiDianying, proxyIpAndPort);
            System.out.println(firstBrowserDriver.getPageSource());
            document = Jsoup.parse(firstBrowserDriver.getPageSource());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (firstBrowserDriver != null) {
                firstBrowserDriver.close();
            }
        }


        //如果未找到，放弃爬取，直接返回
        if (document.getElementsByClass("entry-title").text().equals("未找到")) {
            log.info("----------------爱电影网站未找到-> " + searchMovieName + " <-放弃爬取---------------");
            return movieUrlInLxxh;
        }
        //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
        Elements attr = document.getElementsByTag("h2").select("a");
        if (attr.size() != 0) {
            for (Element element : attr) {
                String jumpUrl = element.attr("href").trim();
//                    log.info("找到调整爱电影-->" +jumpUrl);
                if (jumpUrl.contains(lxxhUrl)) {
                    movieUrlInLxxh.add(jumpUrl);
                }
            }
        }
        //直接获取百度网盘  这段代码可能有问题
        if (movieUrlInLxxh.size() == 0) {
            movieUrlInLxxh.add(urlAiDianying);

        }
        return movieUrlInLxxh;
    }


    public ArrayList<MovieNameAndUrlModel> getWangPanByLxxh(String secondUrlLxxh, String proxyIpAndPort) throws Exception {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("爱电影--》" + secondUrlLxxh);
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
        WebDriver secorndBrowserDriver = null;
        Document secorndDocument = null;
        try {
            //采用phantomJs 无界浏览器形式访问
            secorndBrowserDriver = phantomJsProxyCallService.create(secondUrlLxxh, proxyIpAndPort);
            System.out.println("----------");
            System.out.println(secorndBrowserDriver.getPageSource());
            secorndDocument = Jsoup.parse(secorndBrowserDriver.getPageSource());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (secorndBrowserDriver != null) {
                secorndBrowserDriver.close();
            }
        }

        movieNameAndUrlModel.setMovieName(secorndDocument.getElementsByTag("title").first().text());
        Elements secorndAttr = secorndDocument.getElementsByTag("p").select("span");
        for (Element element : secorndAttr) {
            for (Element aTag : element.getElementsByTag("a")) {
                String linkhref = aTag.attr("href");
                if (linkhref.contains("pan.baidu.com")) {
                    log.info("这里已经拿到要爬取的url : " + linkhref);
                    movieNameAndUrlModel.setWangPanUrl(linkhref);
                    movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                    break;
                } else {
                    continue;
                }
            }
        }
//     第二种情况 span标签 里没有 url
        if (StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())) {
            Elements urlFinals = secorndDocument.getElementsByTag("p").select("a");
            for (Element urlFianl : urlFinals) {
                String linkhref = urlFianl.attr("href");
                if (linkhref.contains("pan.baidu.com")) {
                    log.info("这里已经拿到要爬取的url : " + linkhref);
                    movieNameAndUrlModel.setWangPanUrl(linkhref);
                    movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                    System.out.println(linkhref);
                    break;
                } else {
                    continue;
                }
            }
        }
        movieNameAndUrlModelList.add(movieNameAndUrlModel);
        return movieNameAndUrlModelList;
    }


    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) throws Exception {
        Set<String> movieUrlInLxxh = firstFindLxxhUrl(searchMovieName, proxyIpAndPort);
        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("-------------------------开始爬取爱电影 begin ----------------------------");


        for (String secondUrlLxxh : movieUrlInLxxh) {
            movieNameAndUrlModelList.addAll(getWangPanByLxxh(secondUrlLxxh, proxyIpAndPort));
        }
        //由于包含模糊查询、这里记录到数据库中做插入更新操作
        movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, "url_movie_aidianying");
        invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", movieNameAndUrlModelList);


    }


}