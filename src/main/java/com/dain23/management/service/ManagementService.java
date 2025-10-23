package com.dain23.management.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

public interface ManagementService {
	
	Map<String, Object> makeManagement(String placeCode);
	
	void makeExcelSheet(HttpServletResponse response);

}
