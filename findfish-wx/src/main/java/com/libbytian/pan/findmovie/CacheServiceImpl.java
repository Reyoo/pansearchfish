package com.libbytian.pan.findmovie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: CacheServiceImpl.java
 * @包 路 径： com.libbytian.pan.findmovie
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/27 17:41
 */

@Service("cacheService")
public class CacheServiceImpl implements CacheService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<MovieNameAndUrlModel> getMoviesByName(String redisPrefix, String movieName) throws Exception {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = (ArrayList)redisTemplate.opsForValue().get(redisPrefix.concat(movieName));
//        List<MovieNameAndUrlModel> movieNameAndUrlModels = JSON.parseArray(JSON.toJSONString(cacheResult), MovieNameAndUrlModel.class);
        return null == movieNameAndUrlModels ? null : movieNameAndUrlModels;
    }
}

