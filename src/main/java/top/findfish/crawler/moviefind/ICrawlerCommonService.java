package top.findfish.crawler.moviefind;


import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;

import java.util.List;
import java.util.Set;

/**
 * 爬虫通用接口类
 */

public interface ICrawlerCommonService {


    /**
     * 获取第一层 url
     * @param searchMovieName
     * @param proxyIpAndPort
     * @return
     * @throws Exception
     */
    Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception;

    /**
     * 获取网盘url list
     *
     * @param secondUrlLxxh
     * @param proxyIpAndPort
     * @return
     * @throws Exception
     */
    List<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception;

    /**
     * 保存网盘到数据库 和reids
     *
     * @param searchMovieName
     * @param proxyIpAndPort
     * @throws Exception
     */
    void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) throws Exception;


}
