package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author: QiSun
 * @date: 2021-01-29
 * @Description: 统计一段时间用户搜索电影名词记录
 */

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemUserSearchMovieService extends IService<SystemUserSearchMovieModel> {




    void userSearchMovieCountInFindfish(String searchStr);

    /**
     * 新增 用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    Boolean addUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel);

    /**
     * 查询用户搜索记录
     * @param searchName
     * @return
     */
    SystemUserSearchMovieModel getUserSearchMovieBySearchName(String searchName);

    /**
     * 更新用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    Boolean updateUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel);

    /**
     * 根据时间范围 获取用户查询电影名
     * @param beginTime
     * @param endTime
     * @return
     */
    List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(String beginTime,String endTime);

    /**
     * 根据时间范围查询 搜索频率最高的10条记录
     */
//    List<TopNVO>  listTopNSearchRecord(Integer topLimit);


    /**
     * 热榜假数据
     * 外加膨胀系数
     * @param date
     * @param pageNum
     * @param pageSize
     * @return
     */
    Map<String,Object> getFalseDataHotList(Integer date,Integer pageNum,Integer pageSize);


    /**
     * 获取热榜
     * @param date
     * @return
     */
    Map<String,Object> getTrueHotList(Integer date,Integer pageNum,Integer pageSize);


    Map<String,Object> getEveryList(Integer date,Integer pageNum,Integer pageSize);

    List<SystemUserSearchMovieModel> getOtherList();

}
