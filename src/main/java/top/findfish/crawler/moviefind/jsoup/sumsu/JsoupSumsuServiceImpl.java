package top.findfish.crawler.moviefind.jsoup.sumsu;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.WebPageConstant;
import top.findfish.crawler.util.FindFishUserAgentUtil;
import top.findfish.crawler.util.FindfishStrUtil;
import top.findfish.crawler.moviefind.checkurl.service.InvalidUrlCheckingService;

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
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) throws Exception {
        log.info("-------------->开始爬取 社区动力<--------------------");
        Set<String> firstSearchUrls = new HashSet<>();
        Document doc = JsoupFindfishUtils.getDocument(url, proxyIpAndPort,useProxy);
        AtomicReference<String> formhash = new AtomicReference<>("259f5941");
        Elements formhashElements = doc.getElementsByAttributeValue("name", "formhash");
        if(ObjectUtil.isNotNull(formhashElements)){
            formhashElements.parallelStream().forEach( t -> {
                formhash.set(t.val());
            });
        }
        Document redirectDocument = JsoupFindfishUtils.getRedirectDocument(url, searchMovieName, formhash.get(), proxyIpAndPort,useProxy);
        Elements elements = redirectDocument.select("li").select("a");
        //获取到第一层的中文搜索  继而拿到tid  查询详细电影
        elements.parallelStream().forEach( link ->{
            String linkhref = link.attr("href");
            if (linkhref.startsWith("forum.php?mod")) {
                firstSearchUrls.add("http://520.sumsu.cn/" + linkhref);
                log.info("查询电影名为--> " + searchMovieName + " 获取第一次链接为--> " + linkhref);
            }
        });

        return firstSearchUrls;
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort,Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {

            Document tidDoc = JsoupFindfishUtils.getDocument(secondUrlLxxh, proxyIpAndPort,useProxy);

            if (tidDoc.title().contains("404")) {
                return movieNameAndUrlModels;
            }

            Elements elements = tidDoc.select("a[href]");


            AtomicReference<String> linkhref = null;

            elements.parallelStream().forEach( link -> {
                linkhref.set(link.attr("href"));
                if (linkhref.get().startsWith("https://pan.baidu.com")) {

                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    String baiPan = link.attr("href").toString();
                    movieNameAndUrlModel.setWangPanUrl(baiPan);
                    //这个地方控制 powerBy 字段 截取
                    String movieName = FindfishStrUtil.getsumSuMovieName(tidDoc.title());
                    movieNameAndUrlModel.setMovieName(movieName);
                    movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);

                    //这里有点bug，有概率拿不到密码 例如： 发财日记 ， 存默认2323
                    movieNameAndUrlModel.setWangPanPassword("2323");

                    if (link.parent().text().contains("提取码")) {
                        movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码:")[1].trim());
                    }

                    if (link.parent().text().contains("密码")) {
                        movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码:")[1].trim());
                    }
                    movieNameAndUrlModels.add(movieNameAndUrlModel);
                }

            });

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return movieNameAndUrlModels;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) {
        try {
            Set<String> firstSearchUrls = firstFindUrl(searchMovieName, proxyIpAndPort,useProxy);
            if (CollectionUtil.isNotEmpty(firstSearchUrls)) {
                ArrayList<MovieNameAndUrlModel> movieList = new ArrayList<>();

                firstSearchUrls.parallelStream().forEach( url ->{
                    try {
                        movieList.addAll(getWangPanUrl(url, proxyIpAndPort,useProxy));
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                });
                //筛选爬虫链接
//                    invalidUrlCheckingService.checkUrlMethod("url_movie_sumsu", movieList);
                //插入更新可用数据
//                movieNameAndUrlService.addOrUpdateMovieUrls(movieList, WebPageConstant.LEIFENGJUN_TABLENAME);
//                //删除无效数据
//                movieNameAndUrlService.deleteUnAviliableUrl(movieList, WebPageConstant.LEIFENGJUN_TABLENAME);
//                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
//                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.LEIFENGJUN_TABLENAME, searchMovieName);
//                redisTemplate.opsForValue().set("sumsu:"+ searchMovieName , invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.LEIFENGJUN_TABLENAME, movieNameAndUrlModels, proxyIpAndPort),
//                        Duration.ofHours(2L));

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }

    }

    @Override
    public void checkRepeatMovie() {
        movieNameAndUrlMapper.checkRepeatMovie(WebPageConstant.LEIFENGJUN_TABLENAME);
    }


    /**
     * 数据库初始化 time 循环次数
     *
     * @param times
     * @return
     */
    public List<MovieNameAndUrlModel> firstInitTidSumsuUrl(int times) {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {
            String url = "http://520.sumsu.cn/forum.php?mod=viewthread&tid=" + times + "&highlight=%BD%AB%BE%FC&mobile=2";
//                System.out.println(url);
            HttpHeaders requestSumsuHeaders = new HttpHeaders();
            requestSumsuHeaders.add("User-Agent", FindFishUserAgentUtil.randomUserAgent());
            HttpEntity<String> requestSumsuEntity = new HttpEntity(null, requestSumsuHeaders);
            ResponseEntity<String> resultSumsuResponseEntity = this.restTemplate.exchange(
                    url,
                    HttpMethod.GET, requestSumsuEntity, String.class);

            if (resultSumsuResponseEntity.getStatusCode() == HttpStatus.OK) {
                String tidHtml = resultSumsuResponseEntity.getBody();

                Document tidDoc = Jsoup.parse(tidHtml);
                if (tidDoc.title().contains("404")) {
                    return movieNameAndUrlModels;
                }
                String movieName = FindfishStrUtil.getsumSuMovieName(tidDoc.title());

//                if (movieName.contains("百度云下载链接")) {
//                    movieName = tidDoc.title().split("百度云下载链接")[0].trim();
//                } else if (movieName.contains("Powered by Discuz")) {
//                    movieName = movieName.split("Powered by Discuz")[0].trim();
//                }

                Elements elements = tidDoc.select("strong").select("a");

                if(ObjectUtil.isNull(elements)){
                    return movieNameAndUrlModels;
                }

                elements.parallelStream().forEach( link -> {
                    String linkhref = link.attr("href");
                    if (linkhref.startsWith("pan.baidu.com")) {
                        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                        String baiPan = link.attr("href").toString();
                        movieNameAndUrlModel.setWangPanUrl(baiPan);
                        movieNameAndUrlModel.setMovieName(movieName);
                        movieNameAndUrlModel.setMovieUrl(url);
                        if (link.parent().text().contains("提取码:")) {
                            movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码:")[1].trim());
                        }

                        if (link.parent().text().contains("提取码：")) {
                            movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码：")[1].trim());
                        }

                        if (link.parent().text().contains("密码:")) {
                            movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码:")[1].trim());
                        }

                        if (link.parent().text().contains("密码：")) {
                            movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码：")[1].trim());
                        }


                        movieNameAndUrlModels.add(movieNameAndUrlModel);
                    }
                });


                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels, "url_movie_sumsu");

//                invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying",movieNameAndUrlModels,proxyIp,Integer.valueOf(proxyPort));
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return movieNameAndUrlModels;
    }


}
