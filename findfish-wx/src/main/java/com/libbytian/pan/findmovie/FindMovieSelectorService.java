package com.libbytian.pan.findmovie;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.util.RedisBaseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: FindMovieSelectorService.java
 * @包 路 径： com.libbytian.pan.findmovie
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/27 17:51
 */


@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieSelectorService {

    private final CacheService cacheService;

    private final IFindMovieService findMovieService;


    public List<MovieNameAndUrlModel> listMovies(String tbName, String redisPrefix, String movieName) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = null;
        movieNameAndUrlModels = RedisBaseUtil.selectCacheByTemplate(
                () -> cacheService.getMoviesByName(redisPrefix, movieName),
                () -> {
                    try {
                        return findMovieService.getMoviesByName(tbName,redisPrefix, movieName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return movieNameAndUrlModels;
    }

}
