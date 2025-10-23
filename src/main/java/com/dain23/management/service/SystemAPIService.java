package com.dain23.management.service;

import java.util.Map;

import com.dain23.common.model.ResponseData;

public interface SystemAPIService {

	Map<String, Object> selectComboOfDmsSetting(Map<String, Object> params);
	
	ResponseData upsertMoveAndBackup(Map<String, Object> params);
	
	ResponseData checkDuplicate(Map<String, Object> params);
	
	ResponseData upsertDmsComputer(Map<String, Object> params);
	
	ResponseData upsertDmsCompany(Map<String, Object> params);
	
	ResponseData upsertDmsPlace(Map<String, Object> params);

	ResponseData upsertDmsLogger(Map<String, Object> params);
	
	ResponseData upsertDmsSensor(Map<String, Object> params);
	
	ResponseData insertDmsApplyCalculation(Map<String, Object> params);
	
	ResponseData insertDmsSensorInitial(Map<String, Object> params);
	
	ResponseData upsertDmsSensorType(Map<String, Object> params);
	
	ResponseData upsertDmsSensorTypeSetting(Map<String, Object> params);
	
	ResponseData upsertDmsCalculation(Map<String, Object> params);
	
}
