package com.dubbo.trace.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.trace.core.constants.TraceConstants;
import com.trace.core.enums.ServiceType;
import com.trace.core.manager.TraceManager;

/**
 * @author dengxiaolin
 * @since 2021/01/06
 */
public class TraceFilter implements Filter {

    private static final String EXCLUDE_URL_REGEX = "^/.*\\.(css|js|gif|dmp|png|jpg)$";
    private static final Pattern EXCLUDE_PATTERN = Pattern.compile(EXCLUDE_URL_REGEX);

    private Set<String> excludeUrls = new HashSet<>();
    private Set<String> excludePrefixes = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String exclude = filterConfig.getInitParameter("exclude");
        if (StringUtils.isNotBlank(exclude)) {
            exclude = exclude.trim();
            String[] excludeUrlsConfig = exclude.split(";");

            Arrays.stream(excludeUrlsConfig)
                    .forEach(excludeUrl -> {
                        int index = excludeUrl.indexOf("*");
                        if (index >= 0) {
                            excludePrefixes.add(excludeUrl.substring(0, index));
                        }
                        else {
                            excludeUrls.add(excludeUrl);
                        }
                    });
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        boolean isNotHttpRequest = !(servletRequest instanceof HttpServletRequest);
        if (isNotHttpRequest) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String path = httpServletRequest.getRequestURI();
            if (EXCLUDE_PATTERN.matcher(path).find()) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            String action = path.substring(httpServletRequest.getContextPath().length());
            if (isExclude(path) || isExclude(action)) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            else {
                TraceManager.startSpan(TraceConstants.DUMMY_CONSUMER_CONTEXT, ServiceType.HTTP, path);
                try {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
                finally {
                    TraceManager.endSpan();
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isExclude(String path) {
        if (EXCLUDE_PATTERN.matcher(path).find()) {
            return true;
        }

        if (excludeUrls.contains(path)) {
            return true;
        }

        return excludePrefixes.stream().anyMatch(path::startsWith);
    }
}
