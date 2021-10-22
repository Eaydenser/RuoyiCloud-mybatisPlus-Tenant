package com.ruoyi.common.datascope.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ruoyi.common.core.utils.SecurityUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.datascope.injector.methods.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.lang3.math.NumberUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@MapperScan("com.ruoyi.**.mapper")
@Configuration
public class MybatisPlusConfiguration {

    private static ArrayList<String> filterTable=new ArrayList<>();

    @PostConstruct
    public void init(){
        //过滤表设置，以以下头部开始的表都过滤
        filterTable.add("tenant");
    }
    /**
     * 新多租户插件配置,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存万一出现问题
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                if(NumberUtils.isCreatable(SecurityUtils.getTenantId())){
                    return new LongValue(SecurityUtils.getTenantId());
                }else{
                    return new HexValue("(select tenant_id from tenant where tenant_url = '"+SecurityUtils.getTenantId()+"')");
                }
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                long count = filterTable.stream().filter(t -> t.toUpperCase().startsWith(tableName.toUpperCase())).count();
                return count>0;
            }
        }){
            @Override
            protected Column getAliasColumn(Table table) {
                StringBuilder column = new StringBuilder();
                if (table.getAlias() != null) {
                    column.append(table.getAlias().getName()).append(StringPool.DOT);
                }else{
                    column.append(table.getName()).append(StringPool.DOT);
                }
                column.append(getTenantLineHandler().getTenantIdColumn());
                return new Column(column.toString());
            }
        });
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(blockAttackInnerInterceptor());
        return interceptor;
    }

    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型为mysql
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        return paginationInnerInterceptor;
    }

    @Bean
    public ISqlInjector iSqlInjector() {

        return new GeneralMybatisPlusSqlInjector();
    }

    public class GeneralMybatisPlusSqlInjector extends DefaultSqlInjector {

        @Override
        public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
            return Stream.of(
                    new Insert(),
                    new Delete(),
                    new DeleteByMap(),
                    new DeleteById(),
                    new DeleteBatchByIds(),
                    new Update(),
                    new UpdateById(),
                    new SelectById(),
                    new SelectBatchByIds(),
                    new SelectByMap(),
                    new SelectOne(),
                    new SelectCount(),
                    new SelectMaps(),
                    new SelectMapsPage(),
                    new SelectObjs(),
                    new SelectList(),
                    new SelectPage()
            ).collect(toList());
        }
    }

    @Bean
    public MyDataScopeInterceptor myDataScopeInterceptor() {
        return new MyDataScopeInterceptor();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }

    @Bean
    public BlockAttackInnerInterceptor blockAttackInnerInterceptor() {
        return new BlockAttackInnerInterceptor();
    }

    @Bean
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }
}
