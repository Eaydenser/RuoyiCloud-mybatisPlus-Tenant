package com.ruoyi.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.utils.SecurityUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.PreAuthorize;
import com.ruoyi.system.domain.Tenant;
import com.ruoyi.system.service.ITenantService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 租户Controller
 * 
 * @author ruoyi
 * @date 2021-04-14
 */
@RestController
@RequestMapping("/tenant")
public class TenantController extends BaseController
{
    @Autowired
    private ITenantService tenantService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 查询租户列表
     */
    @PreAuthorize(hasPermi = "tenant:my",isTenant = "0")
    @GetMapping("/my")
    public TableDataInfo my()
    {
        List<Tenant> list = tenantService.list(new QueryWrapper<>(new Tenant().setTenantAdmin(SecurityUtils.getUserId())));
        return getDataTable(list);
    }

    /**
     * 查询租户列表`
     */
    @PreAuthorize(hasPermi = "system:tenant:list")
    @GetMapping("/list")
    @DataScope(userOnFilter = "sys_user.user_id = tenant.tenant_admin")
    public TableDataInfo list(Tenant tenant)
    {
        startPage();
        List<Tenant> list = tenantService.list(new QueryWrapper<>(tenant));
        List<SysUser> sysUsers = sysUserService.listByIds(
                list.stream().map(Tenant::getTenantAdmin).collect(Collectors.toList())
        );
        sysUsers.forEach(sysUser -> {
            list.forEach(tenant1 -> {
                if(tenant1.getTenantAdmin().equals(sysUser.getUserId()))tenant1.setTenantUser(sysUser);
            });
        });
        return getDataTable(list);
    }

    /**
     * 导出租户列表
     */
    @PreAuthorize(hasPermi = "system:tenant:export")
    @Log(title = "租户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Tenant tenant) throws IOException
    {
        List<Tenant> list = tenantService.list(new QueryWrapper<>(tenant));
        ExcelUtil<Tenant> util = new ExcelUtil<Tenant>(Tenant.class);
        util.exportExcel(response, list, "tenant");
    }

    /**
     * 获取租户详细信息
     */
    @PreAuthorize(hasPermi = "system:tenant:query")
    @GetMapping(value = "/{tenantId}")
    public AjaxResult getInfo(@PathVariable("tenantId") Long tenantId)
    {
        return AjaxResult.success(tenantService.getById(tenantId));
    }

    /**
     * 新增租户
     */
    @PreAuthorize(hasPermi = "system:tenant:add")
    @Log(title = "租户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Tenant tenant)
    {
        return toAjax(tenantService.save(tenant.setTenantAdmin(SecurityUtils.getUserId())));
    }

    /**
     * 修改租户
     */
    @PreAuthorize(hasPermi = "system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Tenant tenant)
    {
        return toAjax(tenantService.updateById(tenant));
    }

    /**
     * 删除租户
     */
    @PreAuthorize(hasPermi = "system:tenant:remove")
    @Log(title = "租户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{tenantIds}")
    public AjaxResult remove(@PathVariable Long[] tenantIds)
    {
        return toAjax(tenantService.removeByIds(Arrays.asList(tenantIds)));
    }
}
