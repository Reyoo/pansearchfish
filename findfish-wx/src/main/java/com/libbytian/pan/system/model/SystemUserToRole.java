package com.libbytian.pan.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author liugh123
 * @since 2018-06-25
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role")
public class SystemUserToRole extends Model<SystemUserToRole> implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private String userToRoleId;
    /**
     * 用户编号
     */
    @TableField("user_id")
    private String userId;
    /**
     * 角色代号
     */
    @TableField("role_id")
    private String roleId;


    @TableField("user_role_status")
    private Boolean userRoleStatus;

    @TableField(exist = false)
    @Value("false")
    private Boolean checked;


}
