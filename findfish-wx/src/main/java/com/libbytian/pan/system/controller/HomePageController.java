package com.libbytian.pan.system.controller;


import cn.hutool.core.util.ObjectUtil;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import com.libbytian.pan.system.vo.TopNVO;
import com.libbytian.pan.wechat.service.AsyncSearchCachedComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description: 提供个首页 接口
 * @Author : SunQi
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/home")
@Slf4j
public class HomePageController {

    private final ISystemUserSearchMovieService systemUserSearchMovieService;
    private final AsyncSearchCachedComponent asyncSearchCachedService;

    /**
     * 获取topN 搜索接口  暂时给不可控制 条数 默认top10
     * @return
     */
//    @RequestMapping(value = "/topN", method = RequestMethod.GET)
//    public AjaxResult getTopN() {
//        try {
//            List<TopNVO> topNVOList = systemUserSearchMovieService.listTopNSearchRecord(0);
//            return AjaxResult.success(topNVOList);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return AjaxResult.error(e.getMessage());
//        }
//    }

//   http://findfish.top/#/mobileView?searchname=%E5%A5%A5%E7%89%B9%E6%9B%BC&verification=c3VuNzEyN0AxMjYuY29t&type=mobile


    /**
     * 根据大厅,资源名搜索
     * @param search
     * @param searchMovieText
     * @return
     */
    @RequestMapping(path = "/movie/find/{search}/{searchMovieText}", method = RequestMethod.GET)
    public AjaxResult getMovieList(@PathVariable String search ,@PathVariable String searchMovieText ) {
        Map<String, List<MovieNameAndUrlModel>> movieNameAndUrlModels = new HashMap<>();
        ArrayList<MovieNameAndUrlModel> objects = new ArrayList<>();
        try {
            movieNameAndUrlModels = asyncSearchCachedService.searchWord(searchMovieText.trim(), search);
            if (movieNameAndUrlModels == null) {
                return AjaxResult.hide("未找到该资源，请前往其他大厅查看");
            }

            for (String k : movieNameAndUrlModels.keySet()) {
                objects.addAll(movieNameAndUrlModels.get(k));
            }

            return AjaxResult.success(objects);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return AjaxResult.success(movieNameAndUrlModels);
        }
    }



}
