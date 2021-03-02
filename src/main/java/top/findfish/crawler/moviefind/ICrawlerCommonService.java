package top.findfish.crawler.moviefind;


import top.findfish.crawler.sqloperate.model.MovieNameAndUrlModel;

import java.util.ArrayList;
import java.util.Set;

/**
 * 爬虫通用接口类
 */

public interface ICrawlerCommonService {


     Set<String> firstFindUrl(String searchMovieName, String proxyIpAndPort) throws Exception;

     ArrayList<MovieNameAndUrlModel> getWangPanUrl(String secondUrlLxxh, String proxyIpAndPort) throws Exception ;

     void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) throws Exception;

     void checkRepeatMovie();

    }
