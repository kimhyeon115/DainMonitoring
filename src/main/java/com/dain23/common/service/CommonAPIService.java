package com.dain23.common.service;

import java.util.Map;

import com.dain23.common.model.ParamBody;

public interface CommonAPIService {
	
	Map<String, Object> getMainContent(String placeCode);
	
	Map<String, Object> getDataContent(String placeCode, ParamBody body);
	
}
