package com.libbytian.pan.system.util;

import com.libbytian.pan.system.aop.CacheSelector;

import java.util.function.Supplier;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: RedisBaseUtil.java
 * @包 路 径： com.libbytian.pan.system.util
 * @版权所有：北京数字认证股份有限公司 (C) 2021
 * @类描述:
 * @版本: V1.0
 * @Author：SunQi
 * @创建时间：2022/10/27 17:35
 */
public class RedisBaseUtil {

    /**
     * 缓存查询模板
     * @param cacheSelector    查询缓存的方法
     * @param databaseSelector 数据库查询方法
     * @return T
     */
    public static <T> T selectCacheByTemplate(CacheSelector<T> cacheSelector, Supplier<T> databaseSelector) {
        try {
            System.out.println("query data from redis ······");
            // 先查 Redis缓存
            T t = cacheSelector.select();
            if (t == null) {
                // 没有记录再查询数据库
                System.err.println("redis 中没有查询到");
                System.out.println("query data from database ······");
                return databaseSelector.get();
            } else {
                return t;
            }
        } catch (Exception e) {
            // 缓存查询出错，则去数据库查询
            e.printStackTrace();
            System.err.println("redis 查询出错");
            System.out.println("query data from database ······");
            return databaseSelector.get();
        }
    }

}
