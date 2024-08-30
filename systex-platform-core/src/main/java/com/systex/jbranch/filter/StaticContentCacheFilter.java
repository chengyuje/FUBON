package com.systex.jbranch.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class StaticContentCacheFilter implements Filter {

	private FilterConfig filterConfig;
	
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
    	
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
    	Enumeration<String> paramsName = filterConfig.getInitParameterNames();
    	while (paramsName.hasMoreElements()) {
			String name = paramsName.nextElement();
			String value = filterConfig.getInitParameter(name);
			httpResponse.setHeader(name, value);
		}
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	
    	this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }	
	
}
