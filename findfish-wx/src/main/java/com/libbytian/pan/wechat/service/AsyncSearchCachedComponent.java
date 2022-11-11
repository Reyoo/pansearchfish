package com.libbytian.pan.wechat.service;

import com.libbytian.pan.findmovie.FindMovieSelectorService;
import com.libbytian.pan.system.enums.CacheConstant;
import com.libbytian.pan.system.enums.TbNameConstant;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.wechat.service
 * @ClassName: AsyncSearchCachedServiceImpl
 * @Author: sun71
 * @Description: 搜索电影名进Redis
 * @Date: 2020/10/14 16:34
 * @Version: 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@EnableAsync
public class AsyncSearchCachedComponent {


    private final FindMovieSelectorService findMovieSelectorService;


    /**
     * 根据不同表示返回不用结果
     *
     * @param searchMovieText
     * @param search
     * @return
     * @throws Exception
     */
    public Map<String, List<MovieNameAndUrlModel>> searchWord(String searchMovieText, String search) throws Exception {
        Map<String,  List<MovieNameAndUrlModel>> combineResultMap = new HashMap<>();
        switch (search) {
            //a 一号大厅 小悠
            case "one":
                combineResultMap = findMovieSelectorService.listMovies(TbNameConstant.HALL_FIRST_TABLENAME, CacheConstant.FIRST_HALL_CACHE_NAME,searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return combineResultMap;
            //u 二号大厅 小宇
            case "two":
                combineResultMap = findMovieSelectorService.listMovies(TbNameConstant.HALL_SECOND_TABLENAME,CacheConstant.SECOND_HALL_CACHE_NAME,searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return combineResultMap;
            //x 3号大厅
            case "three":
                combineResultMap = findMovieSelectorService.listMovies(TbNameConstant.HALL_THIRD_TABLENAME,CacheConstant.THIRD_HALL_CACHE_NAME,searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return combineResultMap;
            case "four":
                combineResultMap = findMovieSelectorService.listMovies(TbNameConstant.HALL_FOURTH_TABLENAME,CacheConstant.FOURTH_HALL_CACHE_NAME,searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return combineResultMap;
            default:
                return new HashMap<>();

        }
    }
}



