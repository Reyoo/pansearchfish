package top.findfish.crawler.sqloperate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.findfish.crawler.moviefind.jsoup.JsoupFindfishUtils;
import top.findfish.crawler.sqloperate.mapper.MovieNameAndUrlMapper;
import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;
import top.findfish.crawler.sqloperate.service.IMovieNameAndUrlService;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MovieNameAndUrlServiceImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IMovieNameAndUrlService {


    private final MovieNameAndUrlMapper movieNameAndUrlMapper;
    private final RedisTemplate redisTemplate;
    private static Lock lock = new ReentrantLock();
//    private final InvalidUrlCheckingService invalidUrlCheckingService;

//    @Override
//    public List<MovieNameAndUrlModel> findMovieUrl(String tablename, String movieName,String wangPanUrl) throws Exception {
//        return movieNameAndUrlMapper.selectMovieUrlByName(tablename, movieName);
//    }

    /**
     * 插入更新操作、如果数据库中 不存在 则插入、如果存在 则更新  由于分表、每个爬虫资源影单单独一套 Controller Servcie Mapper
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    @Override
    public void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName,String proxyIpAndPort) throws Exception {
        if (CollectionUtil.isEmpty(movieNameAndUrlModels)) {
            return;
        }

        movieNameAndUrlModels.parallelStream().forEach(t -> {
                    lock.lock();
                    try {
                        if (StrUtil.isBlank(t.getMovieName())) {
                            return;
                        }
                        //查询库内是否有该电影名的数据
                        List<MovieNameAndUrlModel> movieSize = movieNameAndUrlMapper.selectMovieUrlByName(tableName, t.getMovieName().trim(),t.getTitleName(),t.getPanSource());
                        if (movieSize.size() == 1) {
                        //如果只查询到唯一一条数据 则更新
                            movieNameAndUrlMapper.updateUrlMovieUrl(tableName, t);
                            log.info("更新电影列表-->" + t);
                        //如果查询到多条 则校验URL
                        }else if (movieSize.size() >1){
                            try {
                                movieSize.stream().forEach(movieNameAndUrlModel ->{

                                    if (movieNameAndUrlModel.getPanSource().contains("迅雷")){
                                        return;
                                    }

                                    Document document = JsoupFindfishUtils.getDocument(movieNameAndUrlModel.getWangPanUrl(), proxyIpAndPort,false);
                                    if (document == null){
                                        redisTemplate.opsForHash().delete("use_proxy", proxyIpAndPort);
                                        return;
                                    }
                                    //根据分享人账号判断是否失效，如果有账号说明是有效的
                                    if (!document.toString().contains("linkusername")){
                                        try {
                                            movieNameAndUrlMapper.deleteUrlMovieUrls(tableName, movieNameAndUrlModel);
                                        } catch (Exception e) {
                                            log.error(e.getMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                    log.info("校验完毕");

                                });

                                log.info("查询到多条记录，校验URL-->" + t);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else {
                            movieNameAndUrlMapper.insertMovieUrl(tableName, t);
                            log.info("插入电影列表-->" + t);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();
                    }
                }
        );
    }

    @Override
    public int dropMovieUrl(String tableName, MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(tableName, movieNameAndUrlModel);
    }

    @Override
    public void deleteUnAviliableUrl(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName) {
        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {
            movieNameAndUrlMapper.deleteUnAviliableUrl(tableName, movieNameAndUrlModel.getMovieUrl());
        }
    }

    @Override
    public void addOrUpdateMovieUrlsWithTitleName(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName,String proxyIpAndPort) throws Exception {
        if (CollectionUtil.isEmpty(movieNameAndUrlModels)) {
            return;
        }

        movieNameAndUrlModels.parallelStream().forEach(t -> {
                    if (StrUtil.isBlank(t.getMovieName())) {
                        return;
                    }

            //查询库内是否有该电影名的数据
            List<MovieNameAndUrlModel> movieSize = movieNameAndUrlMapper.selectMovieUrlByName(tableName, t.getMovieName().trim(),t.getTitleName(),t.getPanSource());
            if (movieSize.size() == 1) {
                //如果只查询到唯一一条数据 则更新
                movieNameAndUrlMapper.updateUrlMovieUrl(tableName, t);
                log.info("更新电影列表-->" + t);
                //如果查询到多条 则校验URL
            }else if (movieSize.size() >1){
                try {
//                    invalidUrlCheckingService.checkDataBaseUrl(tableName,movieSize,proxyIpAndPort);
                    log.info("查询到多条记录，校验URL-->" + t);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else {
                movieNameAndUrlMapper.insertMovieUrl(tableName, t);
                log.info("插入电影列表-->" + t);
            }
                }
        );
    }

}
