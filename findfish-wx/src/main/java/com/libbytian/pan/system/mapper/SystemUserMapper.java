package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUserModel> {


    SystemUserModel getUser(SystemUserModel systemUserModel);

    List<SystemUserModel> listUsers(SystemUserModel systemUserModel);


    void removeUserAll(SystemUserModel user);

    SystemUserModel getUserByUerToTemplate(@Param(value = "templateId")String templateId);

    int insertSystemUser(SystemUserModel systemUserModel);


    int deleteSysUserByUser(SystemUserModel systemUserModel);

    SystemUserModel getUserById(SystemUserModel systemUserModel);

    int updateUserById(SystemUserModel systemUserModel);

    int updateSysUserById(SystemUserModel systemUserModel);

}
