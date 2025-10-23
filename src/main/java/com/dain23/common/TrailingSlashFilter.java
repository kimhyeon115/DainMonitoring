package com.dain23.common;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 스프링 필터
 */
public class TrailingSlashFilter implements Filter {
	
	/* 요청 도메인 일관화 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = uri.substring(contextPath.length());
		
		String[] parts = path.split("/");
			
		int nonEmptyCount = 0;
		for (String part : parts) {
			if (!part.isEmpty()) nonEmptyCount++;
		}
		
		if (nonEmptyCount < 2 && !uri.endsWith("/")) {
			String redirectTo = uri + "/";
			res.sendRedirect(redirectTo);
			return;
		}
		
		chain.doFilter(request, response);
	}

}
