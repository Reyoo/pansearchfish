package com.libbytian.pan.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserSearchMovieMapper;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import com.libbytian.pan.system.vo.TopNVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserSearchMovieServiceImpl extends ServiceImpl<SystemUserSearchMovieMapper, SystemUserSearchMovieModel> implements ISystemUserSearchMovieService {

    private final SystemUserSearchMovieMapper systemUserSearchMovieMapper;
    private final RedissonClient redissonClient;

    private final static String DISTRIBUTED_LOCK_SEARCH_COUNT = "DISTRIBUTED_LOCK_SEARCH_COUNT";
    private final static String HOME_PAGE_URL = "http://findfish.top/home/movie/find/a/";


    @Override
    public void userSearchMovieCountInFindfish(String searchStr)   {
        if (StrUtil.isEmpty(searchStr)) {
            return;
        }
        //先去查询是否有插入此词条的记录
        SystemUserSearchMovieModel systemUserSearchMovieModel = getUserSearchMovieBySearchName(searchStr);
        //2022-05-13 获取当前日期
        LocalDateTime currentDate = LocalDate.now().atTime(0, 0, 0);

        //如果查询回来的结果为空 则插入
        if (systemUserSearchMovieModel == null || systemUserSearchMovieModel.getLastSearchTime().isBefore(currentDate)) {
            RLock lock = redissonClient.getLock(DISTRIBUTED_LOCK_SEARCH_COUNT);
            boolean isLock;
            try {
                isLock = lock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
                if (isLock){
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
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }finally {
                lock.unlock();
            }

        } else {

            RLock lock = redissonClient.getLock(DISTRIBUTED_LOCK_SEARCH_COUNT);
            boolean isLock;
            try {
                isLock = lock.tryLock(500, 15000, TimeUnit.MILLISECONDS);
                if (isLock) {
                    int searcchTimes = systemUserSearchMovieModel.getSearchTimes().intValue() + 1;
                    systemUserSearchMovieModel.getSearchTimes().intValue();
                    systemUserSearchMovieModel.setSearchTimes(searcchTimes);
                    systemUserSearchMovieModel.setLastSearchTime(LocalDateTime.now());
                    if (updateUserSearchMovie(systemUserSearchMovieModel)) {
                        log.debug("--> " + searchStr + "  更新词条插入成功 第" + searcchTimes + "次查询");
                    } else {
                        log.debug("xxx--> " + searchStr + "  更新词条插入失败");
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            } finally {
                lock.unlock();
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
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(String beginTime, String endTime) {
        return systemUserSearchMovieMapper.listUserSearchMovieBySearchDateRange(beginTime, endTime);
    }


    @Override
    public List<TopNVO> listTopNSearchRecord(Integer topLimit) {
        QueryWrapper<SystemUserSearchMovieModel> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().orderByAsc(SystemUserSearchMovieModel::getLastSearchTime);
        queryWrapper.last("limit 20");
        List<SystemUserSearchMovieModel> systemUserSearchMovieModels = systemUserSearchMovieMapper.selectList(queryWrapper);
        List<TopNVO> topNVOList = new ArrayList<>();
        systemUserSearchMovieModels.forEach( t ->{
            topNVOList.add(TopNVO.builder().searchName(t.getSearchName()).showOrder(1).showStatus(true).showUrl(HOME_PAGE_URL.concat(t.getSearchName())).build());
        });
        return topNVOList;
    }

    @Override
    public List<Map.Entry<String, Integer>> getHotList(Integer date) {

        List<SystemUserSearchMovieModel> hotList = systemUserSearchMovieMapper.getHotList(date);

        //分组求和
        Map<String, Integer> collect = hotList.stream().collect(Collectors.groupingBy(SystemUserSearchMovieModel::getSearchName, Collectors.summingInt(SystemUserSearchMovieModel::getSearchTimes)));

        hotList.clear();

        // 由于HashMap不属于list子类，所以无法使用Collections.sort方法来进行排序，所以我们将hashmap中的entryset取出放入一个ArrayList中
        List<Map.Entry<String, Integer>> list = new ArrayList<>(collect.entrySet());

        // 根据entryset中value的值，对ArrayList中的entryset进行排序，最终达到我们对hashmap的值进行排序的效果
        Collections.sort(list, (o2, o1) -> {
            // 升序排序
            return o1.getValue().compareTo(o2.getValue());
        });

        return list;
    }
}
