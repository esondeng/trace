package com.trace.mybatis.interceptor;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.eson.common.core.utils.TimeUtils;
import com.trace.core.TraceContext;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/14
 */
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
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            String sqlId = mappedStatement.getId();

            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = showSql(configuration, boundSql);

            return TraceManager.tracingWithReturn(
                    ServiceType.JDBC,
                    sqlId,
                    sql,
                    invocation::proceed
            );
        }

    }

    public String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");

        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            }
            else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                    else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }

        return cutLongSql(sql);
    }

    private String getParameterValue(Object obj) {
        if (obj instanceof String) {
            return "'" + obj.toString() + "'";
        }
        else if (obj instanceof Date) {
            return "'" + TimeUtils.formatAsDateTime((Date) obj) + "'";
        }
        else {
            if (obj != null) {
                return obj.toString();
            }
            else {
                return "";
            }
        }
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
