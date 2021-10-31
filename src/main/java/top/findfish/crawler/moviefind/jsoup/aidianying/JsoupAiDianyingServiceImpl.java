package top.findfish.crawler.moviefind.jsoup.aidianying;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: AiDianyingService
 * @Author: sun71
 * @Description: 爱电影JSOP爬虫获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */
@Service("jsoupAiDianyingServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class  JsoupAiDianyingServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;
    private final IMovieNameAndUrlService movieNameAndUrlService;

    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception {

        log.info("-------------->开始爬取 爱电影<--------------------");

        Set<String> movieUrlInLxxh = new HashSet();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(lxxhUrl);
        stringBuffer.append("/?s=");
        stringBuffer.append(URLEncoder.encode(searchMovieName.trim(), "UTF8"));
        log.info(stringBuffer.toString());

        Document document = JsoupFindfishUtils.getDocument(stringBuffer.toString(),proxyIpAndPort);

        log.info(document.text());
        //如果未找到，放弃爬取，直接返回
        if (document.getElementsByClass("entry-title").text().equals("未找到")) {
            log.info("----------------爱电影网站未找到-> " + searchMovieName + " <-放弃爬取---------------");
            return movieUrlInLxxh;
        }
        //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
        Elements attr = document.getElementsByTag("h2").select("a");

        if (attr.size() != 0) {
            StringBuffer jumpUrl = new StringBuffer();
            for (Element element : attr) {
                jumpUrl.append(element.attr("href").trim());
//                    log.info("找到调整爱电影-->" +jumpUrl);
                if (jumpUrl.toString().contains(lxxhUrl)) {
                    movieUrlInLxxh.add(jumpUrl.toString());
                }
                jumpUrl.setLength(0);
            }
        }
        //直接获取百度网盘  这段代码可能有问题
        if (movieUrlInLxxh.size() == 0) {
            movieUrlInLxxh.add(stringBuffer.toString());

        }
        return movieUrlInLxxh;
    }

    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("爱电影--》" + secondUrlLxxh);
        Document secorndDocument = JsoupFindfishUtils.getDocumentBysimulationIe(secondUrlLxxh,proxyIpAndPort);
        String titleName = secorndDocument.getElementsByTag("title").first().text();
        Elements secorndAttr = secorndDocument.getElementsByTag("p");
        for (Element element : secorndAttr) {
            String  panUrl =  element.getElementsByTag("a").attr("href");
            if (panUrl.contains("pan.baidu")){
                MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                movieNameAndUrlModel.setWangPanUrl(panUrl);
                if (element.text().startsWith("视频")){
                    movieNameAndUrlModel.setMovieName(titleName);
                }else {
                    String arr[] =element.text().split("视频");
                    String lastName = arr[0];
                    movieNameAndUrlModel.setMovieName(titleName+"  『"+lastName.replace(".","")+"』");
                }
                if (element.childNodeSize() == 3){
                    movieNameAndUrlModel.setWangPanPassword(element.childNode(2).toString().replaceAll("&nbsp;","").trim());
                }else {
                    movieNameAndUrlModel.setWangPanPassword("");
                }
                movieNameAndUrlModelList.add(movieNameAndUrlModel);
            }
        }
        return movieNameAndUrlModelList;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {
        try {
            Set<String> movieUrlInLxxh = firstFindUrl(searchMovieName, proxyIpAndPort);
            ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
            log.info("-------------------------开始爬取爱电影 begin ----------------------------");
            for (String secondUrlLxxh : movieUrlInLxxh) {
                movieNameAndUrlModelList.addAll(getWangPanUrl(secondUrlLxxh, proxyIpAndPort));
            }
            //如果爬不到资源 直接返回 跳过校验环节
            if (movieNameAndUrlModelList.size() == 0){
                return;
            }
            //筛选爬虫链接
//            invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", movieNameAndUrlModelList);
            //插入更新可用数据
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.AIDIANYING_TABLENAME);
            movieNameAndUrlService.deleteUnAviliableUrl(movieNameAndUrlModelList, WebPageConstant.AIDIANYING_TABLENAME);
            //更新后从数据库查询后删除 片名相同但更新中的 无效数据
            List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.AIDIANYING_TABLENAME, searchMovieName);
            redisTemplate.opsForValue().set("aidianying:"+ searchMovieName.trim() ,
                    invalidUrlCheckingService.checkDataBaseUrl(WebPageConstant.AIDIANYING_TABLENAME, movieNameAndUrlModels, proxyIpAndPort),
                    Duration.ofHours(2L));

        } catch (Exception e) {
            try {
                redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
            }catch (Exception ex){
                log.error(ex.getMessage());
            }
            log.error(e.getMessage());
        }
    }

    @Override
    public void checkRepeatMovie() {

    }

//    @Override
//    public void checkRepeatMovie() {
//        movieNameAndUrlMapper.checkRepeatMovie(Constant.AIDIANYING_TABLENAME);
//    }

}

