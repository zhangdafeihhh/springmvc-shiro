package com.zhuanche.shiro;

import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by wuhui on 2016/2/16.
 */
public class PlatformSpringShiroFilter extends AbstractShiroFilter{

	protected PlatformSpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
        super();
        if (webSecurityManager == null) {
            throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
        }
        setSecurityManager(webSecurityManager);
        if (resolver != null) {
            setFilterChainResolver(resolver);
        }
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doFilterInternal(ServletRequest servletRequest,
			ServletResponse servletResponse, final FilterChain chain)
			throws ServletException, IOException {

        Throwable t = null;

        try {
            final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
            final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

            final Subject subject = createSubject(request, response);

            //noinspection unchecked
            subject.execute(new Callable() {
                public Object call() throws Exception {
                    updateSessionLastAccessTime(request, response);
                    executeChain(request, response, chain);
                    return null;
                }
            });
        } catch (ExecutionException ex) {
            t = ex.getCause();
        }

        if (t != null) {
            if (t instanceof ServletException) {
                throw (ServletException) t;
            }
            if (t instanceof IOException) {
                throw (IOException) t;
            }
            //otherwise it's not one of the two exceptions expected by the filter method signature - wrap it in one:
            String msg = "Filtered request failed.";
            throw new ServletException(msg, t);
        }
    }

	@Override
	protected ServletResponse prepareServletResponse(ServletRequest request,
			ServletResponse response, FilterChain chain) {
        ServletResponse toUse = response;
        if (!isHttpSessions() && (request instanceof ShiroHttpServletRequest) &&
                (response instanceof HttpServletResponse)) {
            //the ShiroHttpServletResponse exists to support URL rewriting for session ids.  This is only needed if
            //using Shiro sessions (i.e. not simple HttpSession based sessions):
            toUse = wrapServletResponse((HttpServletResponse) response, (ShiroHttpServletRequest) request);
        }
        return toUse;
    }
	
	protected ServletResponse wrapServletResponse(HttpServletResponse orig, ShiroHttpServletRequest request) {
        return new PlatFormShiroHttpServletResponse(orig, getServletContext(), request);
    }
	
	
}
