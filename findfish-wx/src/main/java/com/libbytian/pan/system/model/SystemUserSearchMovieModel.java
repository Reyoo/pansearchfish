package com.libbytian.pan.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: QiSun
 * @date: 2021-01-29
 * @Description: 用户电影search
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName("user_movie_search")
public class SystemUserSearchMovieModel extends Model<SystemUserSearchMovieModel> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id",type = IdType.AUTO)
    Integer id;


    /**
     * 搜索关键字频率
     */
    @TableField("search_times")
    String searchName;


    /**
     * 搜索关键字频率
     */
    @TableField("search_times")
    Integer searchTimes;

    /**
     * 最后搜索时间
     */
    @TableField("last_searchtime")
    LocalDateTime lastSearchTime;


    /**
     * 是否放开爬虫此条记录
     */
    @TableField("search_allowed")
    Boolean searchAllowed;


}