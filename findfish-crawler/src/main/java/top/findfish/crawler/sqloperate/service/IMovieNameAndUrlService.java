package top.findfish.crawler.sqloperate.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;

import java.util.List;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
public interface IMovieNameAndUrlService extends IService<MovieNameAndUrlModel> {


    /**
     * 动态传入table
     * @return
     * @throws Exception
     */
//    List<MovieNameAndUrlModel> findMovieUrl (String tablename,String movieName ,String wangPanUrl) throws  Exception;


    void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tableName,String proxyIpAndPort)  throws Exception;


    int dropMovieUrl (String tableName,MovieNameAndUrlModel movieNameAndUrlModel) throws Exception;


    void deleteUnAviliableUrl(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName);

    void addOrUpdateMovieUrlsWithTitleName(List<MovieNameAndUrlModel> movieNameAndUrlModels, String tableName,String proxyIpAndPort) throws Exception ;
}

