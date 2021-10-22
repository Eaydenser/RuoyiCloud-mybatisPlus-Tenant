    package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.core.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TenantMapper;
import com.ruoyi.system.domain.Tenant;
import com.ruoyi.system.service.ITenantService;

/**
 * 租户Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-14
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements ITenantService
{

}
