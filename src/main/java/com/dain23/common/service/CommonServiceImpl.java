package com.dain23.common.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.dain23.common.mapper.CommonMapper;
import com.dain23.common.mapper.SessionMapper;
import com.dain23.common.method.GroupingData;
import com.dain23.common.model.CycleLatestData;
import com.dain23.common.model.EventLatestData;
import com.dain23.common.model.Place;
import com.dain23.common.model.Session;
import com.dain23.common.model.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *	도메인 요청 서비스 클래스
 **/
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {
	
	/* 매퍼 */
	@Autowired
	private SessionMapper sessionMapper;
	@Autowired
	private CommonMapper commonMapper;
	
	
	/* 메인 화면 응답 서비스 */
	public Map<String, Object> makeMain(String placeCode) {
		
		Map<String, Object> result = new LinkedHashMap<>();

		try {
			
			Place place = commonMapper.findPlace(placeCode);
			result.put("place", place);

			result.put("loggers", commonMapper.findLogger(placeCode, place.getId()));

			List<CycleLatestData> latestCycleData = commonMapper.findLatestCycleData(placeCode, place.getId());
			result.put("latestCycleData", GroupingData.groupLoggerIdAndGroupSensorTypeWithoutKey(latestCycleData));
			
			List<EventLatestData> latestEventData = commonMapper.findLatestEventData(placeCode, place.getId());
			result.put("latestEventData", GroupingData.groupLoggerIdAndGroupSensorTypeWithoutKey(latestEventData));

		} catch (Exception e) {
			log.error("서버 오류 발생", e);
		}
		
		return result;
	}
	
	
	
	/* 데이터 화면 응답 서비스 */
	public Map<String, Object> makeData(String placeCode, String select) {
		
		Map<String, Object> result = new LinkedHashMap<>();
		
		try {

			if (select.startsWith(placeCode)) select += ":total";
			result.put("select", select);
			
			Place place = commonMapper.findPlace(placeCode);
			result.put("place", place);
			
			List<Map<String, Object>> navigation = commonMapper.findNavigation(placeCode);
			result.put("systemNavigation", GroupingData.groupSystemNavigation(navigation));
			result.put("basicNavigation", GroupingData.groupBasicNavigation(navigation));

		} catch (Exception e) {
			log.error("서버 오류 발생", e);
		}
		
		return result;
	}
	
	
	
	/* 웹 오픈 여부 검증 */
	public boolean checkPageOpen(String placeCode) {
		return sessionMapper.checkPageOpen(placeCode);
	}
	
	
	
	/* 로그인 검증 */
	public boolean isValidSession(HttpServletRequest request, String placeCode) {
		
		Cookie[] cookies = request.getCookies();

		if (cookies == null) return false;
		for (Cookie cookie : cookies) {
	        if ("DAIN_SESSION_TOKEN".equals(cookie.getName())) {
	            String clientToken = cookie.getValue();
	            Session findToken = sessionMapper.findSession(clientToken);
	            
	            if (findToken != null) {
	                String expiryAtStr = findToken.getExpiryAt();
	                String place = findToken.getPlaceCode();

	                if (expiryAtStr != null) {
	                	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	                	LocalDateTime now = LocalDateTime.now();
	                	LocalDateTime endDt = LocalDateTime.parse(expiryAtStr, formatter);
	                    String token = findToken.getToken();
	                    
	                    if (endDt.isAfter(now) && (place.equals(placeCode) 
	                		|| place.equals("admin") || place.equals("development"))) {
	                    	LocalDateTime expiryAt = now.plusHours(1);
	                    	sessionMapper.updateSession(expiryAt, token);
	                        return true;
	                    }
	                }
	            }
	            break;
	        }
	    }
		return false;
	}
	
	
	
	/* 로그인 검증 및 화면 반환 */
	public String verification(HttpServletRequest request, HttpServletResponse response, 
		Model model, String id, String pw, String placeCode
	) {
		
		if (id.equals("admin")) placeCode = "admin";
		if (id.equals("development")) placeCode = "development";
		
		User findUser = sessionMapper.findUser(placeCode, id, pw);
		if (findUser != null) {
			String token = null;
			int placeId = findUser.getPlaceId();
			int userId = findUser.getUserId();
			int result = 0;
			
			do {
				token = UUID.randomUUID().toString();
				LocalDateTime createdAt = LocalDateTime.now();
				LocalDateTime expiryAt = createdAt.plusHours(1);
				result = sessionMapper.insertSession(token, placeId, userId, createdAt, expiryAt);
			} while (result == 0);
			
			Cookie cookie = new Cookie("DAIN_SESSION_TOKEN", token);
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			response.addCookie(cookie);

			if (!isValidSession(request, placeCode)) return "redirect:patch";
			return "redirect:main";
		}
		
		model.addAttribute("placeCode", placeCode);
		model.addAttribute("status", "로그인 실패");
		
		return String.format("%s/Login", "common");
	}
	
	
	
	/* 로그아웃 및 세션 삭제 */
	public String pageExit(HttpServletRequest request, HttpServletResponse httpServletResponse, String placeCode) {
		
		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("DAIN_SESSION_TOKEN".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}
			}
		}
		
		if (token != null) sessionMapper.deleteSession(token);
		
		return String.format("redirect:/%s/", placeCode);
	}
	
	
	
	/* 만료 세션 삭제 */
	public void clearExpiredSession(int minutes) {
		sessionMapper.deleteExpiredSession(minutes);
	}
	
	
	
	/* 계측 업로드 로그 삭제 */
	public void clearOldUploadLogs() {
		commonMapper.deleteOldUploadLogs();
	}

}
