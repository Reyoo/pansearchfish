package top.findfish.crawler.moviefind.jsoup;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import top.findfish.crawler.util.FindFishUserAgentUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Jsoup 工具类
 * Created by Silence on 2017/1/25.
 */
@Log4j2
public class JsoupFindfishUtils {


    private static final int TIME_OUT = 10 * 1000;

    private static final String ERROR_DESC = "网址请求失败：";

    /**
     * 直接获取网页 不需要重定向 GET请求
     *
     * @param url
     * @param proxyIpAndPort
     * @return
     */
    public static Document getDocument(String url, String proxyIpAndPort) {
        try {

//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIpAndPort.split(":")[0], Integer.valueOf(proxyIpAndPort.split(":")[1])));
            return Jsoup.connect(url)
                    .timeout(TIME_OUT)
//                    .proxy(proxy)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("User-Agent", FindFishUserAgentUtil.randomUserAgent())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true).get();

        } catch (IOException e) {
            log.error(ERROR_DESC + url);
            return null;
        }
    }


    /**
     * 获取重定向 网页
     *
     * @param url
     * @param proxyIpAndPort
     * @return
     */
    public static Document getRedirectDocument(String url, String searchMovieName, String formhash, String proxyIpAndPort) {
        try {

            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIpAndPort.split(":")[0], Integer.valueOf(proxyIpAndPort.split(":")[1])));
            String userAgent = FindFishUserAgentUtil.randomUserAgent();
            //带着cookies 继续访问
            Connection.Response finalResponse = Jsoup.connect(url)
                    .proxy(proxy)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", userAgent)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("formhash", formhash)
                    .data("srchtxt", searchMovieName)
                    .data("searchsubmit", "yes")
                    .method(Connection.Method.POST)
                    .followRedirects(true).timeout(TIME_OUT).execute();

            log.info("获取到sumu第一层url {}" ,finalResponse.url().toString());

            return getDocument(finalResponse.url().toString(), proxyIpAndPort);
        } catch (IOException e) {
            log.error(ERROR_DESC + url);
            return null;
        }
    }



}