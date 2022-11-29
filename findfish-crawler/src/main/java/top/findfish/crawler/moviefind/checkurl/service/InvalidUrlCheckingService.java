package top.findfish.crawler.moviefind.checkurl.service;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
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
    private final RedisTemplate redisTemplate;


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
     * // TODO: 2022/11/11   删除该方法  统一定时任务处理数据
     */
    public ArrayList<MovieNameAndUrlModel> checkDataBaseUrl(String tableName, List<MovieNameAndUrlModel> movieNameAndUrlModels , String proxyIpAndPort) throws Exception {

        ArrayList arrayList = new ArrayList();
        movieNameAndUrlModels.stream().forEach(movieNameAndUrlModel ->{
            String wangPanUrl = movieNameAndUrlModel.getWangPanUrl();
            if (StrUtil.isBlank(wangPanUrl)) {
                return;
            }
            Document document = JsoupFindfishUtils.getDocument(wangPanUrl, proxyIpAndPort,false);
            if (document == null){
                redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
                return;
            }

            //根据分享人账号判断是否失效，如果有账号说明是有效的
            if (!document.toString().contains("linkusername") && movieNameAndUrlModel.getWangPanUrl().contains("baidu")){
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