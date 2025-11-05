package com.dain23.common.service;

import java.util.Map;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CommonService {
	
	Map<String, Object> makeMain(String placeCode);
	
	Map<String, Object> makeData(String placeCode, String select);
	
	boolean checkPageOpen(String placeCode);

	boolean isValidSession(HttpServletRequest request, String placeCode);
	
	String verification(HttpServletRequest request, HttpServletResponse response,
		Model model, String id, String pw, String placeCode
	);
	
	String pageExit(HttpServletRequest request, 
		HttpServletResponse response, String placeCode
	);
	
	public void clearExpiredSession(int minutes);
	
	public void clearOldUploadLogs();
	
}
