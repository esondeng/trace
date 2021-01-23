package com.trace.mybatis.interceptor;

import java.sql.PreparedStatement;
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
import com.eson.common.core.util.ReflectUtils;
import com.trace.core.TraceContext;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;
import com.trace.core.util.TraceUtils;

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
        if (TraceContext.peek() == null) {
            return invocation.proceed();
        }
        else {
            DruidPooledPreparedStatement preparedStatement = (DruidPooledPreparedStatement) invocation.getArgs()[0];
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            PreparedStatementHandler preparedStatementHandler = (PreparedStatementHandler) ReflectUtils.getFieldValueByName(statementHandler, "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectUtils.getFieldValueByName(preparedStatementHandler, "mappedStatement");

            String sql = cutLongSql(showSql(preparedStatement));

            String sqlId = mappedStatement.getId();
            String name = TraceUtils.getSimpleName(sqlId);

            return TraceManager.tracingWithReturn(
                    ServiceType.JDBC,
                    name,
                    sql,
                    invocation::proceed
            );
        }
    }

    private static String showSql(PreparedStatement preparedStatement) {
        String sql = preparedStatement.toString();
        sql = sql.replaceAll("[\\s]+", " ");
        return sql.substring(sql.indexOf(":") + 1).trim();
    }

    private String cutLongSql(String sql) {
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
