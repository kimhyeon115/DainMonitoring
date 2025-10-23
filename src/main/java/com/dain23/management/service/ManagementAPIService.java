package com.dain23.management.service;

import java.util.Map;

import com.dain23.common.model.ResponseData;

public interface ManagementAPIService {
	
	ResponseData updateLoggerInfo(Map<String, Object> params);

	ResponseData openOrClosePage(Map<String, Object> params);
	
}
