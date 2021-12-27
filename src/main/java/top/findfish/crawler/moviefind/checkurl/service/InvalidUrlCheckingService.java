package top.findfish.crawler.moviefind.checkurl.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.service.impl
 * @ClassName: InvalidUrlCheckingService
 * @Author: sun71
 * @Description: 失效链接检测业务类
 * @Date: 2020/12/5 21:17
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InvalidUrlCheckingService {

    private final IMovieNameAndUrlService movieNameAndUrlService;

    /**
     * 判断是否失效、失效则数据库中删除
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
//    public List<MovieNameAndUrlModel> checkUrlMethod(String tableName, List<MovieNameAndUrlModel> movieNameAndUrlModels) throws Exception {
//
//        List<MovieNameAndUrlModel> couldBeFindUrls = new ArrayList<>();
//        if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
//            log.info("入参 电影列表为空 未找到资源");
//            return couldBeFindUrls;
//        } else {
//
//            for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {
//                String wangPanUrl = movieNameAndUrlModel.getWangPanUrl();
//                if (StrUtil.isBlank(wangPanUrl)) {
//                    continue;
//                }
////                Document document = Jsoup.connect(wangPanUrl).proxy(proxyIp,proxyPort).get();
//                Document document = Jsoup.connect(wangPanUrl).get();
//                String title = document.title();
//                //获取html中的标题
//                log.info("title--> :" + title + " 网盘URL --> " + wangPanUrl + " 原资源 --> " + movieNameAndUrlModel.getMovieUrl());
//                if (title.contains("不存在") || title.contains("取消")) {
//                    movieNameAndUrlService.dropMovieUrl(tableName, movieNameAndUrlModel);
//                } else {
//                    couldBeFindUrls.add(movieNameAndUrlModel);
//                }
//            }
//
//            //插入更新可用数据
//            movieNameAndUrlService.addOrUpdateMovieUrls(couldBeFindUrls, tableName);
//
//            log.info("校验完毕");
//            return couldBeFindUrls;
//        }
//    }


    @Async
    public Boolean checkUrlByUrlStr(String url) throws Exception {
        //从URL加载HTML
//        这个地方有问题  不用代理请求百度 问题很大
        Document document = Jsoup.connect(url).get();
        String title = document.title();
        log.debug(title);
        if ("百度网盘-链接不存在".contains(title) || "页面不存在".contains(title)) {
            return true;
        }
        return false;
    }


    /**
     * 数据库查询删除无效数据
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    public ArrayList<MovieNameAndUrlModel> checkDataBaseUrl(String tableName,  List<MovieNameAndUrlModel> movieNameAndUrlModels ,String proxyIpAndPort) throws Exception {

        ArrayList arrayList = new ArrayList();
        movieNameAndUrlModels.parallelStream().forEach(movieNameAndUrlModel ->{
            String wangPanUrl = movieNameAndUrlModel.getWangPanUrl();
            if (StrUtil.isBlank(wangPanUrl)) {
                return;
            }
            Document document = JsoupFindfishUtils.getDocument(wangPanUrl, proxyIpAndPort,false);
            String title = document.title();
            if (StringUtils.isBlank(title)){
                return;
            }
            //获取html中的标题
            log.info("title--> :" + title + " 网盘URL --> " + wangPanUrl + " 原资源 --> " + movieNameAndUrlModel.getMovieUrl());
            if (title.contains("不存在") || title.contains("取消")) {
                try {
                    movieNameAndUrlService.dropMovieUrl(tableName, movieNameAndUrlModel);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                //将有用的信息返回
                arrayList.add(movieNameAndUrlModel);
            }
            log.info("校验完毕");

        });

        return arrayList;
    }
}
