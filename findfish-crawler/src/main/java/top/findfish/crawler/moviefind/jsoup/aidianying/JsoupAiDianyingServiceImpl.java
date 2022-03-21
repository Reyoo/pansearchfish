package top.findfish.crawler.moviefind.jsoup.aidianying;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.WebPageTagConstant;
import top.findfish.crawler.constant.XiaoYouConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.checkurl.service.InvalidUrlCheckingService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.util.WebPageConstant;

import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @ProjectName: pan
 * @ClassName: AiDianyingService
 * @Package: com.search.pan.system.service
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


    /**
     * 爱电影这部分要先拿到cookies  做验证  第二步再去请求
     * @param searchMovieName
     * @param proxyIpAndPort
     * @return
     * @throws Exception
     */
    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) throws Exception {

        log.info("-------------->开始爬取 爱电影<--------------------");

        Set<String> movieUrlInLxxh = new HashSet();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(lxxhUrl);
        stringBuffer.append("/page/1/?s=");
        stringBuffer.append(URLEncoder.encode(searchMovieName.trim(), "UTF8"));
        Document document = JsoupFindfishUtils.getDocument(stringBuffer.toString(),proxyIpAndPort,useProxy);
        //如果未找到，放弃爬取，直接返回
        if (document.getElementsByClass("entry-title").text().equals("未找到")) {
            log.info("----------------爱电影网站未找到-> " + searchMovieName + " <-放弃爬取---------------");
            return movieUrlInLxxh;
        }
        //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
        Elements attrs = document.getElementsByTag("h2").select("a");
        attrs.parallelStream().forEach( attr -> {
            if(attr.attr("href").contains("wxbxkx") && !attr.attr("href").contains("zysyd")){
                movieUrlInLxxh.add(attr.attr("href").trim().toString());
            }
        });

//         这段代码是考虑到直接跳到页面的情况 注释掉 待验证
//        if (CollectionUtil.isEmpty(movieUrlInLxxh)) {
//            movieUrlInLxxh.add(stringBuffer.toString());
//        }
        return movieUrlInLxxh;
    }


    /**
     * 2022-01-18
     * 该方法为适用搜索页最新规则，HS临时新增titleName、panSource字段
     * 爱电影爬虫逻辑仍有问题，页面内如有多个网盘资源，现规则下只能爬取到一条资源
     * 例如搜索：绝望主妇
     * 等待SQ进行修改。
     */
    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort,Boolean useProxy) throws Exception {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("爱电影--》" + secondUrlLxxh);
        Document secorndDocument = JsoupFindfishUtils.getDocumentBysimulationIe(secondUrlLxxh,proxyIpAndPort,useProxy);
        String movieName = secorndDocument.getElementsByTag("title").first().text();
        //爱电影截掉标题中的固定后缀
        if (movieName.contains(XiaoYouConstant.AIDIANYING_STR_WITH_SGIN.getType())) {
            movieName = movieName.split(XiaoYouConstant.AIDIANYING_STR_WITH_SGIN.getType())[0];
        }
        String finalMovieName = movieName;


        Elements secorndAttr = secorndDocument.getElementsByTag("p");

        //爱电影可能设置了屏蔽高访问IP，为细水长流不建议该站爬取过于频繁
        secorndAttr.stream().forEach(
                element -> {
                    String  panUrl =  element.getElementsByTag("a").attr("href");
                    //因最新资源链接包括迅雷云盘， 所以pan.baidu更新为 pan.
                    //爱电影有概率会存入百度文档，百度文档资源不予爬取，进行过滤
                    if (panUrl.contains("pan.") && !panUrl.contains("https://pan.baidu.com/doc")){
                        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                        movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);
                        //判断是百度网盘还是迅雷云盘
                        if (panUrl.contains(WebPageTagConstant.BAIDU.getType())){
                            movieNameAndUrlModel.setPanSource(WebPageTagConstant.BAIDU_WANGPAN.getType());
                        }else {
                            movieNameAndUrlModel.setPanSource(WebPageTagConstant.XUNLEI_YUNPAN.getType());
                        }
                        movieNameAndUrlModel.setWangPanUrl(panUrl);
                        movieNameAndUrlModel.setMovieName(finalMovieName);
                        //因新增titleName字段，存入titleName
                        if (element.text().startsWith("视频")){
                            movieNameAndUrlModel.setTitleName("视频：");
                        }else {
                            String arr[] =element.text().split("视频");
                            String lastName = arr[0];
                            movieNameAndUrlModel.setTitleName(lastName.replace(".",""));
                        }

                        if (element.childNodeSize() == 3){
                            movieNameAndUrlModel.setWangPanPassword(element.childNode(2).toString().replaceAll("&nbsp;","").trim());
                        }else {
                            movieNameAndUrlModel.setWangPanPassword("");
                        }
                        movieNameAndUrlModelList.add(movieNameAndUrlModel);
                    }
                }
        );

        return movieNameAndUrlModelList;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort,Boolean useProxy) {
        try {
            Set<String> movieUrlInLxxh = firstFindUrl(searchMovieName, proxyIpAndPort,useProxy);
            ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
            log.info("-------------------------开始爬取爱电影 begin ----------------------------");
            for (String secondUrlLxxh : movieUrlInLxxh) {
                movieNameAndUrlModelList.addAll(getWangPanUrl(secondUrlLxxh, proxyIpAndPort,useProxy));
            }
            //如果爬不到资源 直接返回 跳过校验环节
            if (movieNameAndUrlModelList.size() == 0){
                return;
            }
            //插入更新可用数据
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, WebPageConstant.AIDIANYING_TABLENAME,proxyIpAndPort);

            List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(WebPageConstant.AIDIANYING_TABLENAME, searchMovieName);

            ArrayList arrayList = new ArrayList();
            movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel ->{
                MovieNameAndUrlModel findFishMovieNameAndUrlModel = JSON.parseObject(JSON.toJSONString(movieNameAndUrlModel), MovieNameAndUrlModel.class);
                arrayList.add(findFishMovieNameAndUrlModel);
            });

            redisTemplate.opsForValue().set("aidianying::"+ searchMovieName.trim() , arrayList, Duration.ofHours(2L));
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



}
