package com.libbytian.pan.findmovie.aidianying;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.findmovie.IFindMovieService;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;




/**
 * @author SunQi
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieInAidianYingImpl  extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel>   {

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    private final StringRedisTemplate redisTemplate;
//
//    @Override
//    public List<MovieNameAndUrlModel> getMoviesByName(String tbName, String movieName) throws Exception {
//        String s = redisTemplate.opsForValue().get(movieName);
//        return null == s ? null : JSON.parseObject(s, List.class);
//    }
}
