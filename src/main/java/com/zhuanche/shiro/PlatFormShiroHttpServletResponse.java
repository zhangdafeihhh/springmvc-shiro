package com.zhuanche.shiro;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpServletResponse;
import org.apache.shiro.web.servlet.ShiroHttpSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wuhui on 2016/2/16.
 */
public class PlatFormShiroHttpServletResponse extends ShiroHttpServletResponse {

	 public PlatFormShiroHttpServletResponse(HttpServletResponse wrapped,
			ServletContext context, ShiroHttpServletRequest request) {
		super(wrapped, context, request);
        this.request = request;
	}

	private static final String DEFAULT_SESSION_ID_PARAMETER_NAME = ShiroHttpSession.DEFAULT_SESSION_ID_NAME;

	 private ShiroHttpServletRequest request = null;
	 
	
	protected String toEncoded(String url, String sessionId) {

        if ((url == null) || (sessionId == null))
            return (url);

        String path = url;
        String query = "";
        String anchor = "";
        int question = url.indexOf('?');
        if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
        }
        int pound = path.indexOf('#');
        if (pound >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
        }
        StringBuilder sb = new StringBuilder(path);
        if (sb.length() > 0) { // session id param can't be first.
            sb.append("?");
            sb.append(DEFAULT_SESSION_ID_PARAMETER_NAME);
            sb.append("=");
            sb.append(sessionId);
        }
        sb.append(anchor);
        sb.append(query);
        return (sb.toString());

    }

	@Override
	protected boolean isEncodeable(String location) {

        if (location == null)
            return (false);

        // Is this an intra-document reference?
        if (location.startsWith("#"))
            return (false);

        // Are we in a valid session that is not using cookies?
        final HttpServletRequest hreq = request;
        final HttpSession session = hreq.getSession(false);
        if (session == null)
            return (false);
        if (hreq.isRequestedSessionIdFromCookie())
            return (false);

        return doIsEncodeable(hreq, session, location);
    }
	
	 private boolean doIsEncodeable(HttpServletRequest hreq, HttpSession session, String location) {
	        // Is this a valid absolute URL?
	        URL url;
	        try {
	            url = new URL(location);
	        } catch (MalformedURLException e) {
	            return (false);
	        }

	        // Does this URL match down to (and including) the context path?
	        if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol()))
	            return (false);
	        if (!hreq.getServerName().equalsIgnoreCase(url.getHost()))
	            return (false);
	        int serverPort = hreq.getServerPort();
	        if (serverPort == -1) {
	            if ("https".equals(hreq.getScheme()))
	                serverPort = 443;
	            else
	                serverPort = 80;
	        }
	        int urlPort = url.getPort();
	        if (urlPort == -1) {
	            if ("https".equals(url.getProtocol()))
	                urlPort = 443;
	            else
	                urlPort = 80;
	        }
	        if (serverPort != urlPort)
	            return (false);

	        String contextPath = getRequest().getContextPath();
	        if (contextPath != null) {
	            String file = url.getFile();
	            if ((file == null) || !file.startsWith(contextPath))
	                return (false);
	            String tok = "?" + DEFAULT_SESSION_ID_PARAMETER_NAME + "=" + session.getId();
	            if (file.indexOf(tok, contextPath.length()) >= 0)
	                return (false);
	        }

	        // This URL belongs to our web application, so it is encodeable
	        return (true);

	    }
	
	

	
	
}
