package com.zhuanche.shiro.filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by slaton on 2016/2/19.
 */
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        subject.getSession().removeAttribute(WebUtils.SAVED_REQUEST_KEY);
        WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
        return false;
    }
}
