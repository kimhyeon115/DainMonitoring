package com.dain23.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dain23.common.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 웹오픈 여부 검증 인터셉터
 */
@Component
public class OpenCheckInterceptor implements HandlerInterceptor {
	
	/* 서비스 */
	@Autowired
	private CommonService commonService;
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		/* 경로(path) 현장코드 추출 */
		String uri = request.getRequestURI();
	    String[] parts = uri.split("/");
	    String placeCode = parts[1];
	    
	    /* 웹페이지 오픈 여부 조회 */
        if (commonService.checkPageOpen(placeCode)) {
            return true;
        }
        
        response.sendRedirect("/" + placeCode + "/patch");
        return false;
	}

}
