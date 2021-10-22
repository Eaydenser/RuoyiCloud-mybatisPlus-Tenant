package com.ruoyi.common.datascope.aspect;

import java.lang.reflect.Method;

import com.ruoyi.common.core.utils.SecurityUtils;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.datascope.annotation.DataScope;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.system.api.domain.SysRole;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 数据过滤处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    /**
     * 数据权限过滤表关键字
     */
    public static final String TABLE_SCOPE = "tableScope";

    /**
     * 数据权限过滤表关键字
     */
    public static final String WHERE_INDEX_SCOPE = "indexScope";

    @Autowired
    private TokenService tokenService;

    // 配置织入点
    @Pointcut("@annotation(com.ruoyi.common.datascope.annotation.DataScope)")
    public void dataScopePointCut()
    {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) throws Throwable
    {
        handleDataScope(point);
    }

    protected void handleDataScope(final JoinPoint joinPoint)
    {
        // 获得注解
        DataScope controllerDataScope = getAnnotationLog(joinPoint);
        Object params = joinPoint.getArgs()[0];
        //防注入
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
            baseEntity.getParams().put(TABLE_SCOPE,"");
            baseEntity.getParams().put(WHERE_INDEX_SCOPE,"");
        }
        if (controllerDataScope == null)
        {
            return;
        }
        // 获取当前的用户
        LoginUser loginUser = tokenService.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getSysUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                dataScopeFilter(joinPoint, currentUser, controllerDataScope);
            }
        }
    }

    /**
     * 数据范围过滤
     * 
     * @param joinPoint 切点
     * @param user 用户

     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user,DataScope scope)
    {
        StringBuilder sqlString = new StringBuilder();

        for (SysRole role : user.getRoles())
        {
            String dataScope = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(dataScope))
            {
                sqlString = new StringBuilder();
                break;
            }
            else if (DATA_SCOPE_CUSTOM.equals(dataScope))
            {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", scope.deptTableName(),
                        role.getRoleId()));
            }
            else if (DATA_SCOPE_DEPT.equals(dataScope))
            {
                sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", scope.deptTableName(), user.getDeptId()));
            }
            else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
            {
                sqlString.append(StringUtils.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                        scope.deptTableName(), user.getDeptId(), user.getDeptId()));
            }
            else if (DATA_SCOPE_SELF.equals(dataScope))
            {
                if (StringUtils.isNotBlank(scope.userTableName()))
                {
                    sqlString.append(StringUtils.format(" OR {}.user_id = {} ", scope.userTableName(), user.getUserId()));
                }
                else
                {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(" OR 1=0 ");
                }
            }
        }

        if (StringUtils.isNotBlank(sqlString.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                StringBuffer tbaleString=new StringBuffer();
                if(scope.userTable()){
                    tbaleString.append(" "+scope.userJoinType()+" join");
                    tbaleString.append(" "+scope.userTableName());
                    if(NumberUtils.isCreatable(SecurityUtils.getTenantId())){
                        tbaleString.append(" on "+scope.userTableName()+".tenant_id="+ SecurityUtils.getTenantId());
                    }else{
                        tbaleString.append(" on "+scope.userTableName()+".tenant_id=(select tenant_id from tenant where tenant_url = '"+SecurityUtils.getTenantId()+"')");
                    }
                    if(StringUtils.isNotBlank(scope.userOnFilter())){
                        tbaleString.append(" and "+scope.userOnFilter());
                    }
                    if(StringUtils.isNotBlank(scope.userAndFilter())){
                        tbaleString.append(" and "+scope.userAndFilter());
                    }
                }
                if(scope.deptTable()){
                    tbaleString.append(" "+scope.deptJoinType()+" join");
                    tbaleString.append(" "+scope.deptTableName());
                    if(NumberUtils.isCreatable(SecurityUtils.getTenantId())){
                        tbaleString.append(" on "+scope.deptTableName()+".tenant_id="+ SecurityUtils.getTenantId());
                    }else{
                        tbaleString.append(" on "+scope.deptTableName()+".tenant_id=(select tenant_id from tenant where tenant_url = '"+SecurityUtils.getTenantId()+"')");
                    }
                    if(StringUtils.isNotBlank(scope.deptOnFilter())){
                        tbaleString.append(" and "+scope.deptOnFilter());
                    }
                    if(StringUtils.isNotBlank(scope.deptAndFilter())){
                        tbaleString.append(" and "+scope.deptAndFilter());
                    }
                }
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
                baseEntity.getParams().put(TABLE_SCOPE,tbaleString.toString());
                baseEntity.getParams().put(WHERE_INDEX_SCOPE,scope.whereIndex());
            }
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private DataScope getAnnotationLog(JoinPoint joinPoint)
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null)
        {
            return method.getAnnotation(DataScope.class);
        }
        return null;
    }
}
