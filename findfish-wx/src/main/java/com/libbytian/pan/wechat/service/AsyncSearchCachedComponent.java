package com.libbytian.pan.wechat.service;

import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
import com.libbytian.pan.findmovie.hall.fourth.IFindMovieHallFourth;
import com.libbytian.pan.findmovie.sumsu.IFindMovieInSumsu;
import com.libbytian.pan.findmovie.unread.IFindMovieInUnread;
import com.libbytian.pan.findmovie.xiaoyou.IFindMovieInXiaoyou;
import com.libbytian.pan.findmovie.xiaoyu.IFindMovieInXiaoyu;
import com.libbytian.pan.findmovie.youjiang.IFindMovieInYoujiang;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
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


//    private final IFindMovieInAiDianYing iFindMovieInAiDianYing;
    private final IFindMovieInUnread iFindMovieInUnread;
    private final IFindMovieInXiaoyou iFindMovieInXiaoyou;
    private final IFindMovieInXiaoyu iFindMovieInXiaoyu;


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
                Map<String, List<MovieNameAndUrlModel>> collectXiaoYou = iFindMovieInXiaoyou.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return collectXiaoYou;
            //u 二号大厅 小宇
            case "two":
                Map<String, List<MovieNameAndUrlModel>> collectXiaoYu = iFindMovieInXiaoyu.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return collectXiaoYu;
            //x 3号大厅
            case "three":

                Map<String, List<MovieNameAndUrlModel>> collectUnread = iFindMovieInUnread.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
//                Map<String, List<MovieNameAndUrlModel>> collectAiDianYing = iFindMovieInAiDianYing.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                //添加未读影单
                combineResultMap.putAll(collectUnread);
                //添加爱电影
//                combineResultMap.putAll(collectAiDianYing);
                return combineResultMap;
            case "four":
                //
//                Map<String, List<MovieNameAndUrlModel>> collectAiDianYing = iFindMovieInAiDianYing.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
//                Map<String, List<MovieNameAndUrlModel>> collectAiDianYing = iFindMovieInAiDianYing.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));



//                combineResultMap.putAll(collectAiDianYing);
                return combineResultMap;
            default:
                return new HashMap<>();

        }

    }
}



