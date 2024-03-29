package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目名: pan
 * 文件名: SystemKeywordMapper
 * 创建者: HS
 * 创建时间:2020/12/14 16:07
 * 描述: TODO
 */
@Mapper
public interface SystemKeywordMapper extends BaseMapper<SystemKeywordModel> {


    SystemKeywordModel keywordByUser(@Param(value = "username") String username);

    /**
     * 更新用户关联关键字
     * @param systemKeywordModel
     */
    void updateKeyword(SystemKeywordModel systemKeywordModel);

    /**
     * 新增用户关联关键字
     * @param systemKeywordModel
     * @return
     */
    int insertKeyword(SystemKeywordModel systemKeywordModel);


    /**
     * 根据用户删除关键字
     * @param systemUserModel
     * @return
     */
    int deleteKeywordByUser(SystemUserModel systemUserModel);

}
