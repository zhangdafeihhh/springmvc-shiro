package com.zhuanche.shiro;

import com.zhuanche.util.DESUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;

/**
 * Created by wuhui on 2016/2/16.
 */
public class PlatformShiroFilterFactoryBean extends ShiroFilterFactoryBean {

    private String desSecret;

    private String localLoginUrl;

	@Override
	protected AbstractShiroFilter createInstance() throws Exception {


        SecurityManager securityManager = getSecurityManager();
        if (securityManager == null) {
            String msg = "SecurityManager property must be set.";
            throw new BeanInitializationException(msg);
        }

        if (!(securityManager instanceof WebSecurityManager)) {
            String msg = "The security manager does not implement the WebSecurityManager interface.";
            throw new BeanInitializationException(msg);
        }

        FilterChainManager manager = createFilterChainManager();

        //Expose the constructed FilterChainManager by first wrapping it in a
        // FilterChainResolver implementation. The AbstractShiroFilter implementations
        // do not know about FilterChainManagers - only resolvers:
        PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
        chainResolver.setFilterChainManager(manager);

        //Now create a concrete ShiroFilter instance and apply the acquired SecurityManager and built
        //FilterChainResolver.  It doesn't matter that the instance is an anonymous inner class
        //here - we're just using it because it is a concrete AbstractShiroFilter instance that accepts
        //injection of the SecurityManager and FilterChainResolver:
        return new PlatformSpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
    }

    public void setDesSecret(String desSecret) {
        this.desSecret = desSecret;
    }

    public void setLocalLoginUrl(String localLoginUrl) {
        this.localLoginUrl = localLoginUrl;
    }

//    @Override
//    public void setLoginUrl(String loginUrl) {
//
//        super.setLoginUrl(loginUrl);
//    }

    @Override
    public String getLoginUrl() {
        if (StringUtils.isNotBlank(desSecret)) {
            try {
                return super.getLoginUrl() + "?service=" + DESUtil.encodeHex(localLoginUrl,desSecret);
            } catch (Exception e) {

            }
        }
        return super.getLoginUrl();
    }
}
