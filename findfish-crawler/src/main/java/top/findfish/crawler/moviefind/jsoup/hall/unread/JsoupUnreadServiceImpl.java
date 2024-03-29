package top.findfish.crawler.moviefind.jsoup.hall.unread;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.constant.CacheConstant;
import top.findfish.crawler.constant.WebPageTagConstant;
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

/**
 * @author: QiSun
 * @date: 2021-01-04
 * @Description: 未读影单 爬取类
 */
@Service("jsoupHallUnreadServiceImpl")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class JsoupUnreadServiceImpl implements ICrawlerCommonService {

    private final IMovieNameAndUrlService movieNameAndUrlService;

    private final RedisTemplate redisTemplate;
    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Value("${user.hall.thirdUrl}")
    String unreadUrl;

    @Override
    public Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) throws Exception {

        Set<String> movieUrlSet = new HashSet();
        String encode = URLEncoder.encode(searchMovieName.trim(), "UTF-8");
        String url = unreadUrl + "/?s=" + encode;
        String charset = JsoupFindfishUtils.getCharset(url);
        Document doc = JsoupFindfishUtils.getDocumentWithCharset(url, proxyIpAndPort, useProxy, charset);
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


    /**
     * 2022-01-18
     * 该方法为适用搜索页最新规则，HS临时新增titleName、panSource字段
     * 未读影单爬虫逻辑仍有问题，页面内如有多个网盘资源，现规则下只能爬取到一条资源
     * 例如搜索:绝望主妇
     * 等待SQ进行修改。
     */
    @Override
    public ArrayList<MovieNameAndUrlModel> getWangPanUrl(String movieUrl, String proxyIpAndPort, Boolean useProxy) throws Exception {

        ArrayList<MovieNameAndUrlModel> list = new ArrayList<>();
        String charset = JsoupFindfishUtils.getCharset(movieUrl);
        Document document = JsoupFindfishUtils.getDocumentWithCharset(movieUrl, proxyIpAndPort, useProxy, charset);
        String movieName = document.getElementsByTag("title").text().trim();
        if (movieName.contains("– 未读影单")) {
            movieName = movieName.split("– 未读影单")[0].trim();
        }
        if (StringUtils.isBlank(movieName)) {
            return list;
        }

        Elements pTagAttrs = document.getElementsByClass("entry-content").tagName("div").select("p");
        String finalMovieName = movieName;
        pTagAttrs.stream().forEach(
                pTagAttr -> {
                    if (pTagAttr.text().contains("资源链接点这里")) {
                        String panUrl = pTagAttr.getElementsByTag("a").attr("href");
                        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                        movieNameAndUrlModel.setMovieUrl(movieUrl);
                        movieNameAndUrlModel.setWangPanUrl(panUrl);
                        movieNameAndUrlModel.setMovieName(finalMovieName);
                        //判断是否需要拼接片名
                        if (pTagAttr.text().startsWith("资源链接点这里→")) {
                            movieNameAndUrlModel.setTitleName("视频：");
                        } else {
                            movieNameAndUrlModel.setTitleName(pTagAttr.text().split("资源链接点这里")[0] + "：");
                        }
                        if (pTagAttrs.next().text().contains("密码")) {
                            movieNameAndUrlModel.setWangPanPassword("提取码：" + pTagAttrs.next().text().trim().split("提取码")[1].trim().substring(1, 6).trim());
                        } else if (pTagAttrs.next().text().contains("提取码")) {
                            movieNameAndUrlModel.setWangPanPassword("提取码：" + pTagAttrs.next().text().trim().split("提取码")[1].trim().substring(1, 6).trim());
                        } else {
                            movieNameAndUrlModel.setWangPanPassword("");
                        }

                        if (movieNameAndUrlModel.getWangPanUrl().contains("pan.baidu")) {
                            movieNameAndUrlModel.setPanSource("百度网盘");
                        } else {
                            movieNameAndUrlModel.setPanSource("迅雷云盘");
                        }
                        list.add(movieNameAndUrlModel);
                    }
                }
        );
        if (CollectionUtil.isEmpty(list)) {
            Elements preInnerATagAttrs = document.getElementsByTag("pre");
            preInnerATagAttrs.parallelStream().forEach(
                    preInnerATagAttr -> {
                        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                        movieNameAndUrlModel.setMovieName(finalMovieName);
                        if (preInnerATagAttr.text().contains("资源链接点这里")) {
                            String panUrl = preInnerATagAttr.getElementsByTag("a").attr("href");
                            movieNameAndUrlModel.setMovieUrl(movieUrl);
                            movieNameAndUrlModel.setWangPanUrl(panUrl);
                        }
                        List<Node> nodes = preInnerATagAttr.childNodes();
                        nodes.parallelStream().forEach( node ->{
                            if (node.toString().contains("提取码")) {
                                movieNameAndUrlModel.setWangPanPassword(node.toString());
                            }  else {
                                movieNameAndUrlModel.setWangPanPassword("");
                            }
                        });

                        if (movieNameAndUrlModel.getWangPanUrl().contains(WebPageTagConstant.BAIDU_WANGPAN.getType())){
                            movieNameAndUrlModel.setPanSource(WebPageTagConstant.BAIDU_WANGPAN.getDescription());
                        }else if (movieNameAndUrlModel.getWangPanUrl().contains(WebPageTagConstant.KUAKE_WANGPAN.getType())){
                            movieNameAndUrlModel.setPanSource(WebPageTagConstant.KUAKE_WANGPAN.getDescription());
                        }else {
                            movieNameAndUrlModel.setPanSource(WebPageTagConstant.XUNLEI_YUNPAN.getDescription());
                        }

                        list.add(movieNameAndUrlModel);
                    }
            );
        }
        return list;
    }

    @Override
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort, Boolean useProxy) {
        log.info("-------------->开始爬取 未读  搜索片名: "+searchMovieName+" <--------------------");
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
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, TbNameConstant.HALL_THIRD_TABLENAME,proxyIpAndPort);

                //更新后从数据库查询后删除 片名相同但更新中的 无效数据
                List<MovieNameAndUrlModel> movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName(TbNameConstant.HALL_THIRD_TABLENAME, searchMovieName);

                ArrayList arrayList = new ArrayList();
                movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel ->{
                    com.libbytian.pan.system.model.MovieNameAndUrlModel findFishMovieNameAndUrlModel = JSON.parseObject(JSON.toJSONString(movieNameAndUrlModel), com.libbytian.pan.system.model.MovieNameAndUrlModel.class);
                    arrayList.add(findFishMovieNameAndUrlModel);
                });

                //筛选数据库链接
                redisTemplate.opsForValue().set(CacheConstant.THIRD_HALL_CACHE_NAME.concat(searchMovieName), arrayList, Duration.ofHours(2L));
            }
        } catch (Exception e) {
            log.error("docment is null" + e.getMessage());
            redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
        }
    }

    @Override
    public void checkRepeatMovie() {
        return;
    }


}