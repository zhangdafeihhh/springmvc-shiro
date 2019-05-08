package com.zhuanche.common.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class RequestCommonDataFilter  extends OncePerRequestFilter {


    private static final Logger logger = LoggerFactory.getLogger(RequestCommonDataFilter.class);

    // log 追踪ID
    private final static String TRACE_KEY = "reqId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //先从param里取，没有的话从header里取，还没有的话再创建
        String reqId = request.getParameter("x_requestId");
        if(reqId==null || "".equals(reqId.trim())  ) {
            reqId = request.getHeader("x_requestId");
        }
        if(reqId==null || "".equals(reqId.trim())  ) {
            reqId =   UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(TRACE_KEY, reqId);

        //TODO 增加其它共性的逻辑
        filterChain.doFilter(request, response);

        MDC.remove(TRACE_KEY);


    }
}