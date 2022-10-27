package com.libbytian.pan.findmovie.xiaoyu;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目名: top-findfish-findfish
 * 文件名: IFindMovieInXiaoyou
 * 创建者: HS
 * 创建时间:2022/6/14 14:36
 * 描述: TODO
 */
//@CacheConfig(cacheNames = "xiaoyu")
public interface IFindMovieInXiaoyu extends IService<MovieNameAndUrlModel> {

//    @Cacheable(key = "#movieName", condition = "#movieName != null")
    List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception;
}
