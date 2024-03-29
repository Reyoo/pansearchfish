package top.findfish.crawler.moviefind.jsoup.hall.xiaoyou;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.CacheConstant;
import top.findfish.crawler.constant.WebPageTagConstant;
import top.findfish.crawler.constant.XiaoYouConstant;
import top.findfish.crawler.moviefind.ICrawlerCommonService;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;
import top.findfish.crawler.constant.TbNameConstant;
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
 * 描述: 小悠网盘站爬取
 */
@Service("jsoupHallXiaoYouServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupXiaoYouServiceImpl implements ICrawlerCommonService {

    private final RedisTemplate redisTemplate;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.firstUrl}")
    String XiaoYouHallUrl;

    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {
        log.info("-------------->开始爬取 小悠  搜索片名: "+searchMovieName+" <--------------------");
        Set<String> movieList = new HashSet<>();
        String encode = URLEncoder.encode(searchMovieName.trim(), CharsetUtil.UTF_8);
        String url = XiaoYouHallUrl.concat(WebPageTagConstant.XIAOYOU_URL_PARAM.getType()).concat(encode);

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

    //HS新版本 2022-01-14
    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String movieUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {
        ArrayList<MovieNameAndUrlModel> list = new ArrayList();
        Document document = JsoupFindfishUtils.getDocument(movieUrl, proxyIpAndPort, useProxy);
        String movieName = document.getElementsByTag(WebPageTagConstant.HTML_TAG_TITLE.getType()).first().text();
        if (movieName.contains(XiaoYouConstant.XIAOYOU_STR_WITH_SGIN.getType())) {
            movieName = movieName.split(XiaoYouConstant.XIAOYOU_STR_WITH_SGIN.getType())[0];
        }

        Elements pTagAttr = document.getElementsByClass("entry-content").select(WebPageTagConstant.HTML_TAG_P.getType());

        String finalMovieName = movieName;

        for (Element element : pTagAttr) {

            //小悠 第二层页内包含百度网盘VIP广告，要额外过滤掉
            if (!element.toString().contains(WebPageTagConstant.PANURL.getType()) || element.toString().contains(WebPageTagConstant.PANVIP.getType())){
                continue;
            }
            if (StringUtils.isBlank(element.getElementsByTag(WebPageTagConstant.HTML_TAG_A.getType()).attr(WebPageTagConstant.HTML_TAG_HREF.getType())) || element.text().contains(WebPageTagConstant.ONLINE_SHOW.getType())) {
                continue;
            }

            //判断titleName 该规则为无数次调试后成功匹配规则，请勿随意修改
            List<Node> nodes = element.childNodes();
            //countChild 计数，后续获取指定位数索引使用
            int countChild = 0;
            for (Node node : nodes) {
                //有可能 因为资源 无提取码 在 childNodeSize == 2时 需额外加校验
                if (element.childNodeSize() == 2 && element.childNode(1).toString().contains("https")){
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    movieNameAndUrlModel.setTitleName(element.childNode(0).toString().replaceAll("&nbsp;","").trim());
                    this.setObjectParam(element,movieNameAndUrlModel,movieUrl,finalMovieName,countChild);
                    list.add(movieNameAndUrlModel);
                    break;
                }
                else if (element.childNodeSize() == 1 || element.childNodeSize() == 2){
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    movieNameAndUrlModel.setTitleName(WebPageTagConstant.SHIPIN_CHINA.getType());
                    this.setObjectParam(element,movieNameAndUrlModel,movieUrl,finalMovieName,countChild);
                    list.add(movieNameAndUrlModel);
                    break;
                }
                else if (node.toString().contains(WebPageTagConstant.PANURL.getType())){
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                    String titleName = element.childNode(countChild-1).toString().replaceAll("&nbsp;","").trim();
                    titleName = titleName.equals("") ? WebPageTagConstant.SHIPIN_CHINA.getType():titleName;
                    movieNameAndUrlModel.setTitleName(titleName);
                    this.setObjectParam(element,movieNameAndUrlModel,movieUrl,finalMovieName,countChild);
                    if (StringUtils.isNotBlank(movieNameAndUrlModel.getWangPanUrl())){
                        list.add(movieNameAndUrlModel);
                    }
                    break;
                }
                countChild++;
            }
        }
        //如果list为0，则使用下列规则爬取
        if (list.size() == 0){
            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
            Elements specialSpTagAttr= document.getElementsByClass("detail");
            int i = specialSpTagAttr.get(0).childNodeSize();
            movieNameAndUrlModel.setMovieUrl(movieUrl);
            movieNameAndUrlModel.setMovieName(finalMovieName);
            movieNameAndUrlModel.setWangPanPassword(specialSpTagAttr.get(0).childNode(i - 1).toString().replaceAll("&nbsp;","").trim());
            movieNameAndUrlModel.setTitleName(specialSpTagAttr.get(0).childNode(i - 3).toString().trim());
            movieNameAndUrlModel.setWangPanUrl(specialSpTagAttr.get(0).childNode(i-2).attr(WebPageTagConstant.HTML_TAG_HREF.getType()));
            movieNameAndUrlModel.setPanSource(specialSpTagAttr.get(0).childNode(i-2).childNode(0).toString());
            list.add(movieNameAndUrlModel);
        }
        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = firstFindUrl(searchMovieName, proxyIpAndPort, useProxy);
            if (CollectionUtil.isNotEmpty(set)) {
                set.stream().forEach(url -> {
                    try {
                        movieNameAndUrlModelList.addAll(getWangPanUrl(url, proxyIpAndPort, useProxy));
                        movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, TbNameConstant.HALL_FIRST_TABLENAME,proxyIpAndPort);
                        //删除无效数据  删除是要做的 删除的主要用处在于电视剧更新 级数问题。 后面应当抓到删除的逻辑 或者定时批量删除
                        /** movieNameAndUrlService.deleteUnAviliableUrl(movieNameAndUrlModelList, WebPageConstant.XIAOYOU_TABLENAME);*/
                        CompletableFuture<List<MovieNameAndUrlModel>> completableFuture = CompletableFuture.supplyAsync(() -> movieNameAndUrlMapper.selectMovieUrlByLikeName(TbNameConstant.HALL_FIRST_TABLENAME, searchMovieName));
                        List<MovieNameAndUrlModel> movieNameAndUrlModels = completableFuture.get();
                        completableFuture.thenRun(() -> {
                            try {
                                ArrayList arrayList = new ArrayList();
                                movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel ->{
                                    com.libbytian.pan.system.model.MovieNameAndUrlModel findFishMovieNameAndUrlModel = JSON.parseObject(JSON.toJSONString(movieNameAndUrlModel), com.libbytian.pan.system.model.MovieNameAndUrlModel.class);
                                    arrayList.add(findFishMovieNameAndUrlModel);
                                });

                                redisTemplate.opsForValue().set(CacheConstant.FIRST_HALL_CACHE_NAME.concat(searchMovieName),
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
        movieNameAndUrlMapper.checkRepeatMovie(TbNameConstant.HALL_FIRST_TABLENAME);
    }

    /**
     * 设置网盘对象参数
     * @param element
     * @param movieNameAndUrlModel
     * @param movieUrl
     * @param finalMovieName
     * @param countChild
     */
    public void setObjectParam(Element element, MovieNameAndUrlModel movieNameAndUrlModel, String movieUrl, String finalMovieName, Integer countChild) {

        movieNameAndUrlModel.setWangPanUrl(element.childNode(countChild).attr(WebPageTagConstant.HTML_TAG_HREF.getType()));
        if (StringUtils.isBlank(movieNameAndUrlModel.getWangPanUrl())){
            movieNameAndUrlModel.setWangPanUrl(element.getElementsByTag(WebPageTagConstant.HTML_TAG_A.getType()).attr(WebPageTagConstant.HTML_TAG_HREF.getType()));
        }
        movieNameAndUrlModel.setWangPanPassword("");
        movieNameAndUrlModel.setMovieUrl(movieUrl);
        movieNameAndUrlModel.setMovieName(finalMovieName);

        //判断panSource
        //2022-04-26 新增夸克网盘判断
        if (movieNameAndUrlModel.getWangPanUrl().contains(WebPageTagConstant.BAIDU_WANGPAN.getType())){
            movieNameAndUrlModel.setPanSource(WebPageTagConstant.BAIDU_WANGPAN.getDescription());
        }else if (movieNameAndUrlModel.getWangPanUrl().contains(WebPageTagConstant.KUAKE_WANGPAN.getType())){
            movieNameAndUrlModel.setPanSource(WebPageTagConstant.KUAKE_WANGPAN.getDescription());
        }else {
            movieNameAndUrlModel.setPanSource(WebPageTagConstant.XUNLEI_YUNPAN.getDescription());
        }

        //判断码后的: 是英文还是中文并进行字符过滤
        if (element.text().contains(XiaoYouConstant.XIAOYOU_TIQUMA.getType())) {
            movieNameAndUrlModel.setWangPanPassword(WebPageTagConstant.TIQUMA_CHINA.getType()+element.childNode(countChild+1).toString().split(XiaoYouConstant.XIAOYOU_TIQUMA.getType())[1].replaceAll("&nbsp;","").trim());

        }else if (element.text().contains(XiaoYouConstant.XIAOYOU_TIQUMA_English.getType())){
            movieNameAndUrlModel.setWangPanPassword(WebPageTagConstant.TIQUMA_CHINA.getType()+element.childNode(countChild+1).toString().split(XiaoYouConstant.XIAOYOU_TIQUMA_English.getType())[1].replaceAll("&nbsp;","").trim());
        }
    }

}