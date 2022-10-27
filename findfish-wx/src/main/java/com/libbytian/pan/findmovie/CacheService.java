package com.libbytian.pan.findmovie;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;

import java.util.List;

public interface CacheService {


    /**
     *  从缓存中获取电影名称
     * @param redisPrefix
     * @param movieName
     * @return
     * @throws Exception
     */
    List<MovieNameAndUrlModel> getMoviesByName(String redisPrefix, String movieName) throws Exception;


}
