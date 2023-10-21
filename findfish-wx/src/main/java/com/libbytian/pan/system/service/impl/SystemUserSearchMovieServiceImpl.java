package com.libbytian.pan.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.libbytian.pan.system.mapper.SystemUserSearchMovieMapper;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserSearchMovieServiceImpl extends ServiceImpl<SystemUserSearchMovieMapper, SystemUserSearchMovieModel> implements ISystemUserSearchMovieService {

    private final SystemUserSearchMovieMapper systemUserSearchMovieMapper;


    private final static String DISTRIBUTED_LOCK_SEARCH_COUNT = "DISTRIBUTED_LOCK_SEARCH_COUNT";

    @Value("${falseData.coefficient}")
    private String coefficient;

    @Value("${falseData.defaultDate}")
    private Integer defaultDate;


    @Override
    public void userSearchMovieCountInFindfish(String searchStr)   {
        if (StrUtil.isEmpty(searchStr)) {
            return;
        }
        //字符串去空格
        searchStr = searchStr.trim();

        //先去查询是否有插入此词条的记录
        SystemUserSearchMovieModel systemUserSearchMovieModel = getUserSearchMovieBySearchName(searchStr);
        //2022-05-13 获取当前日期
        LocalDateTime currentDate = LocalDate.now().atTime(0, 0, 0);

        //如果查询回来的结果为空 则插入
        if (systemUserSearchMovieModel == null || systemUserSearchMovieModel.getLastSearchTime().isBefore(currentDate)) {
//            RLock lock = redissonClient.getLock(DISTRIBUTED_LOCK_SEARCH_COUNT);
            boolean isLock;
            try {
//                isLock = lock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
//                if (isLock){
                    SystemUserSearchMovieModel initSystemUserSearchMovieModel = new SystemUserSearchMovieModel();
                    initSystemUserSearchMovieModel.setSearchName(searchStr);
                    initSystemUserSearchMovieModel.setSearchTimes(1);
                    initSystemUserSearchMovieModel .setLastSearchTime(LocalDateTime.now());
                    initSystemUserSearchMovieModel.setSearchAllowed(Boolean.TRUE);

                    if (addUserSearchMovie(initSystemUserSearchMovieModel)) {
                        log.debug("--> " + searchStr + "  新增词条插入成功");
                    } else {
                        log.debug("xxx--> " + searchStr + "  新增词条插入失败");
                    }
//                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }finally {
//                lock.unlock();
            }

        } else {
//            RLock lock = redissonClient.getLock(DISTRIBUTED_LOCK_SEARCH_COUNT);
            boolean isLock;
            try {
//                isLock = lock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
//                if (isLock) {
                    int searcchTimes = systemUserSearchMovieModel.getSearchTimes().intValue() + 1;
                    systemUserSearchMovieModel.getSearchTimes().intValue();
                    systemUserSearchMovieModel.setSearchTimes(searcchTimes);
                    systemUserSearchMovieModel.setLastSearchTime(LocalDateTime.now());
                    if (updateUserSearchMovie(systemUserSearchMovieModel)) {
                        log.debug("--> " + searchStr + "  更新词条插入成功 第" + searcchTimes + "次查询");
                    } else {
                        log.debug("xxx--> " + searchStr + "  更新词条插入失败");
                    }
//                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
//                lock.unlock();
            }
        }
    }


    /**
     * 新增 用户搜索记录
     *
     * @param systemUserSearchMovieModel
     * @return
     */
    @Override
    public Boolean addUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel) {
        int insertSucccess = 0;
        insertSucccess = systemUserSearchMovieMapper.insertUserSearchMovie(systemUserSearchMovieModel);
        if (insertSucccess > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 查询用户搜索记录
     *
     * @param searchName
     * @return
     */
    @Override
    public SystemUserSearchMovieModel getUserSearchMovieBySearchName(String searchName) {
        return systemUserSearchMovieMapper.getUserSearchMovieBySearchName(searchName);
    }

    /**
     * 更新用户搜索记录
     *
     * @param systemUserSearchMovieModel
     * @return
     */
    @Override
    public Boolean updateUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel) {
        int insertSucccess = 0;
        insertSucccess = systemUserSearchMovieMapper.updateUserSearchMovie(systemUserSearchMovieModel);
        if (insertSucccess > 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 根据时间范围 获取用户查询电影名
     * 查询资源热度
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(String beginTime, String endTime) {
        return systemUserSearchMovieMapper.listUserSearchMovieBySearchDateRange(beginTime, endTime);
    }

    @Override
    public Map<String, Object> getFalseDataHotList(Integer date, Integer pageNum, Integer pageSize) {
        //展示当日数据时，在配置文件中控制 date 为0还是1
        date = date ==1 ? defaultDate : date;
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,BigDecimal>> hotList = systemUserSearchMovieMapper.getHotList(date);
        BigDecimal num = new BigDecimal(0);
        BigDecimal count = new BigDecimal(1);
        for (Map<String, BigDecimal> stringIntegerMap : hotList) {
            num = num.add(count);
            //设置 9倍 系数
            stringIntegerMap.put("search_times",stringIntegerMap.get("search_times").multiply(new BigDecimal(coefficient)));
            stringIntegerMap.put("num",num);
        }
        PageInfo pageInfo = new PageInfo(hotList);
        map.put("result",pageInfo);
        return map;
    }

    @Override
    public Map<String, Object> getTrueHotList(Integer date, Integer pageNum, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,BigDecimal>> hotList = systemUserSearchMovieMapper.getHotList(date);
        PageInfo pageInfo = new PageInfo(hotList);
        map.put("result",pageInfo);
        return map;
    }

    @Override
    public Map<String, Object> getEveryList(Integer date, Integer pageNum, Integer pageSize) {
        date = date ==1 ? defaultDate : date;
        Map<String,Object> map = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,BigDecimal>> hotList = systemUserSearchMovieMapper.getHotList(date);
        BigDecimal num = new BigDecimal(0);
        BigDecimal count = new BigDecimal(1);
        for (Map<String, BigDecimal> stringIntegerMap : hotList) {
            num = num.add(count);
            //设置 9倍 系数
            stringIntegerMap.put("every_times",stringIntegerMap.get("every_times").multiply(new BigDecimal(coefficient)));
            stringIntegerMap.put("num",num);
        }
        PageInfo pageInfo = new PageInfo(hotList);
        map.put("result",pageInfo);
        return map;
    }
}
