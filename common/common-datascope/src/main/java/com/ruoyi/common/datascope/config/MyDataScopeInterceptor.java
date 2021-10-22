package com.ruoyi.common.datascope.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.*;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MyDataScopeInterceptor extends AbstractSqlParserHandler implements Interceptor {

    public Object metaObjectgetValue(MetaObject metaObject,String str){
        Object paramsObj=null;
        try {
            paramsObj = metaObject.getValue(str);
        }catch (ReflectionException e){

        }catch (BindingException e){

        }catch (UnsupportedOperationException e){

        }catch (Exception e){

        }
        return paramsObj;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Map<String,Object> sqlMap = new HashMap<>();
        Boolean hasSqlLogs = false;
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        MappedStatement mappedStatement =
                (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        Object paramsObj = null;
        if(paramsObj == null)paramsObj=metaObjectgetValue(metaObject,"delegate.parameterHandler.parameterObject.params");
        if(paramsObj == null)paramsObj=metaObjectgetValue(metaObject,"delegate.parameterHandler.parameterObject.ew.entity.params");
        if(paramsObj == null)paramsObj=metaObjectgetValue(metaObject,"delegate.parameterHandler.parameterObject.param1.entity.params");
        if(paramsObj != null && paramsObj instanceof HashMap){
            String dataScope = ((HashMap<String, String>) paramsObj).get("dataScope");
            String tableScope = ((HashMap<String, String>) paramsObj).get("tableScope");
            String indexScope = ((HashMap<String, String>) paramsObj).get("indexScope");
            if(StringUtils.isNotBlank(dataScope) && StringUtils.isNotBlank(tableScope) && StringUtils.isNotBlank(indexScope) && NumberUtils.isCreatable(indexScope)){
                BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
                String oldSql = boundSql.getSql();
                System.err.println("old sql :"+oldSql);
                //新sql
                StringBuffer newSql = new StringBuffer();
                int i=0;
                int whereIndex=-1;
                while(i<=Integer.valueOf(indexScope)){
                    whereIndex=oldSql.toUpperCase().indexOf("WHERE",whereIndex);
                    if (whereIndex!=-1) {
                        whereIndex+="WHERE".length();
                    }
                    i++;
                }
                if(whereIndex!=-1 && oldSql.indexOf(tableScope)==-1 && oldSql.indexOf(dataScope)==-1){
                    newSql.append(oldSql.substring(0,whereIndex-6));
                    newSql.append(tableScope);
                    newSql.append(oldSql.substring(whereIndex-6,whereIndex));
                    newSql.append(" 1=1");
                    newSql.append(dataScope);
                    newSql.append(" and ");
                    newSql.append(oldSql.substring(whereIndex,oldSql.length()));
                    BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(),newSql.toString(), boundSql.getParameterMappings(), boundSql.getParameterObject());
                    //MappedStatement不需要替换，可能版本原因吧
                    //MappedStatement newMappedStatement = copyMappedStatement(ms, new MySqlSource(newBoundSql));
                    //invocation.getArgs()[0] = newMappedStatement;
                    //这是关键
                    metaObject.setValue("delegate.boundSql",newBoundSql);
                    System.err.println("new sql :"+newBoundSql.getSql());
                }
            }
        }
        return invocation.proceed();

    }

    /**
     * 生成拦截对象的代理
     *
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    class MySqlSource implements SqlSource {
        private BoundSql boundSql;

        public MySqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object o) {
            return boundSql;
        }

    }

    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource sqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), sqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());

        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public void setProperties(Properties properties) {

    }
}