package com.dain23.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dain23.common.TrailingSlashFilter;

import jakarta.servlet.SessionTrackingMode;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private LoginInterceptor loginInterceptor;
	
	@Autowired
	private OpenCheckInterceptor openCheckInterceptor;
	
	
    /* 로그인 인증 제외 경로 */
    private static final List<String> LOGIN_EXCLUDE_PATHS = List.of(

        /* 정적 자원 */
        "/common/**",
        "/error/**",

        /* 로그인 요청 */
        "/**/signIn",

        /* 사이트별 기본 페이지 & 패치 페이지 */
        "/*/",
        "/management/css",
        "/management/js",
        "/management/sneat",
        
        /* 문화재 페이지 */
        "/cheomseongdae/**",
        "/cheongpung/**",
        "/ddm/**",
        "/geunjeongjeon/**",
        "/magoksa/**",
        
        /* 개발 테스트 */
        "/development/**"
    );
    
    
    /* 웹오픈 여부 검증 제외 경로 */
    private static final List<String> CHECK_OPEN_EXCLUDE_PATHS = List.of(
    		
		 /* 정적 자원 */
        "/common/**",
        "/error/**",

        /* 로그인 요청 */
        "/**/signIn",

        /* 사이트별 기본 페이지 & 패치 페이지 */
        "/*/",
        "/*/patch",
        
        /* 기타서버 기능 */
        "/actuator/**",
        "/.well-known/**"
    );
	
	
	/* 세션 검증 예외 패스 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		/* 로그인 검증 */
		registry.addInterceptor(loginInterceptor)
		        .addPathPatterns("/**")
		        .excludePathPatterns(LOGIN_EXCLUDE_PATHS)
		        .order(1);
		
		/* 오픈여부 검증 */
		registry.addInterceptor(openCheckInterceptor)
				.addPathPatterns("/**")
					.excludePathPatterns(CHECK_OPEN_EXCLUDE_PATHS)
					.order(2);
	}
	
	
	/* 세션을 URL 파라미터가 아닌 쿠키 기반으로 강제 */
	@Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> servletContext.setSessionTrackingModes(
                Collections.singleton(SessionTrackingMode.COOKIE)
        );
    }
	
	
	/* Trailing Slash Filter 등록 */
	@Bean
    public FilterRegistrationBean<TrailingSlashFilter> trailingSlashFilter() {
        FilterRegistrationBean<TrailingSlashFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TrailingSlashFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
