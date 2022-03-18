package com.libbytian.pan.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * *************************************************************************
 * <p/>
 *
 * @文件名称: TopNVO.java
 * @包 路 径： com.libbytian.pan.system.vo
 * @类描述: 身份证号校验
 * @版本: V1.0
 * @Author:：SunQi
 * @创建时间：2022/3/15 14:04
 * @Description: 首页显示topN
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopNVO implements Serializable {


    /**
     * 查询名称
     */
    String searchName;

    /**
     * 显示顺序
     */
    Integer showOrder;

    /**
     * 显示状态 true 显示 false 隐藏
     */
    Boolean showStatus;

    /**
     * 跳转URL
     */
    String showUrl;


}
