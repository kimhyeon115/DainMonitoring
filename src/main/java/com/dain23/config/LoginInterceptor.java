package com.dain23.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.dain23.common.mapper.SessionMapper;
import com.dain23.common.model.Session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 로그인 검증 인터셉터
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
	
	/* 매퍼 */
	@Autowired
	private SessionMapper sessionMapper;
	
	
	/* 로그인 검증 핸들러 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		/* 경로(path) 현장코드 추출 */
		String uri = request.getRequestURI();
	    String[] parts = uri.split("/");
	    String placeCode = parts[1];
	    
	    /* 로그인 여부 검증 */
	    Cookie[] cookies = request.getCookies();
	    if (cookies == null) {
	        response.sendRedirect("/" + placeCode + "/");
	        return false;
	    }
	    
	    for (Cookie cookie : cookies) {
	        if ("DAIN_SESSION_TOKEN".equals(cookie.getName())) {
	            String clientToken = cookie.getValue();
	            Session findToken = sessionMapper.findSession(clientToken);

	            if (findToken != null) {
	                String expiryAtStr = findToken.getExpiryAt();
	                String place = findToken.getPlaceCode();

	                if (expiryAtStr != null) {
	                	LocalDateTime now = LocalDateTime.now();
                        LocalDateTime expiryAt = LocalDateTime.parse(expiryAtStr,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        if (expiryAt.isAfter(now)
                                && (place.equals(placeCode) || place.equals("admin") || place.equals("development"))) {
                            LocalDateTime newExpiry = now.plusHours(1);
                            sessionMapper.updateSession(newExpiry, clientToken);
                            request.setAttribute("userSession", findToken);
                            return true;
                        } else {
                            sessionMapper.deleteSession(findToken.getToken());
                        }
                    }
                }
                break;
	        }
	    }
	    
	    response.sendRedirect("/" + placeCode + "/");
	    return false;
	}

}
