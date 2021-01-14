package com.trace.mybatis.interceptor;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.eson.common.core.utils.ReflectUtils;
import com.trace.core.TraceContext;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/14
 */

@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
public class TraceMybatisInterceptor implements Interceptor {
    /**
     * sql 最大长度
     */
    private static final int MAX_LENGTH = 10000;

    private Properties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (TraceContext.get() == null) {
            return invocation.proceed();
        }
        else {
            DruidPooledPreparedStatement preparedStatement = (DruidPooledPreparedStatement) invocation.getArgs()[0];
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) ReflectUtils.getFieldValueByName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectUtils.getFieldValueByName(preparedStatementHandler, "mappedStatement");

            String sqlId = mappedStatement.getId();
            String sql = cutLongSql(preparedStatement.getSql());

            return TraceManager.tracingWithReturn(
                    ServiceType.JDBC,
                    sqlId,
                    sql,
                    invocation::proceed
            );
        }
    }


    private String cutLongSql(String sql) {
        sql = sql.replaceAll("[\\s]+", " ");
        if (sql.length() > MAX_LENGTH) {
            return sql.substring(0, MAX_LENGTH);
        }
        else {
            return sql;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
