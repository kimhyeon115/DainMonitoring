package com.dain23.management.service;

import java.util.Map;

import com.dain23.common.model.ResponseData;

public interface ManagementAPIService {
	
	ResponseData updateLoggerInfo(Map<String, Object> params);

	ResponseData openOrClosePage(Map<String, Object> params);
	
	Map<String, Object> findSensorInLogger(Map<String, Object> params);
	
	ResponseData makemakeAnalysisData(Map<String, Object> params);
	
	ResponseData makeTransitionData(Map<String, Object> params);
	
}
