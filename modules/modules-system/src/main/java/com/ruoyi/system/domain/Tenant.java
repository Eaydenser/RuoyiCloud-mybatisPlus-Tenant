package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.system.api.domain.SysUser;
import lombok.Data;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 租户对象 tenant
 * 
 * @author ruoyi
 * @date 2021-04-14
 */
@Accessors(chain = true)
@Data
public class Tenant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long tenantId;

    /** 路由地址 */
    @Excel(name = "路由地址")
    private String tenantUrl;

    /** 租户管理员 */
    @Excel(name = "租户管理员")
    private Long tenantAdmin;

    /** 租户名称 */
    @Excel(name = "租户名称")
    private String tenantName;

    /** 预览图 */
    @Excel(name = "预览图")
    private String tenantImg;

    /** 启用状态 */
    @Excel(name = "启用状态")
    private Integer enableFlag;

    @TableField(exist = false)
    private SysUser tenantUser;
}
