package com.ruoyi.common.datascope.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ruoyi.common.core.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     *
     * 使用Mybatis-plus执行insert操作这个方法执行
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if(SecurityUtils.getUsername()!=null){
            this.setFieldValByName("createBy",SecurityUtils.getUsername(),metaObject);
            this.setFieldValByName("updateBy",SecurityUtils.getUsername(),metaObject);
        }
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

    /**
     *
     * 使用Mybatis-plus执行update操作这个方法执行
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if(SecurityUtils.getUsername()!=null){
            this.setFieldValByName("updateBy",SecurityUtils.getUsername(),metaObject);
        }
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
