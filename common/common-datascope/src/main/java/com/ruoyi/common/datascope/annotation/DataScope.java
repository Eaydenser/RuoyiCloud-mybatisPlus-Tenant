package com.ruoyi.common.datascope.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 * 
 * @author ruoyi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope
{
    /**
     * 在第几个where前插入表
     */
    public String whereIndex() default "0";

    /**
     * 部门表join type
     */
    public String deptJoinType() default "left";

    /**
     * 用户表join type
     */
    public String userJoinType() default "left";

    /**
     * 部门表过滤sql:  (select * from table) left join sys_dept on {}
     *
     */
    public String deptOnFilter() default "sys_user.dept_id = sys_dept.dept_id";

    /**
     * 部门表过滤sql:  (select * from table) left join sys_dept on sys_user.dept_id = sys_dept.dept_id (and){}
     * 如果只需要插部门表就在deptBuildUserFilter里写过滤
     */
    public String deptAndFilter() default "";

    /**
     * 部门表过滤sql:  (select * from table) left join sys_dept on {}
     *
     */
    public String userOnFilter() default "";

    /**
     * 部门表过滤sql:  (select * from table) left join sys_dept on sys_user.dept_id = sys_dept.dept_id (and){}
     * 如果只需要插部门表就在deptBuildUserFilter里写过滤
     */
    public String userAndFilter() default "";

    /**
     * 部门表名称
     */
    public String deptTableName() default "sys_dept";

    /**
     * 用户表名称
     */
    public String userTableName() default "sys_user";

    /**
     * 添加部门表
     */
    public boolean deptTable() default true;

    /**
     * 添加用户表
     */
    public boolean userTable() default true;
}
