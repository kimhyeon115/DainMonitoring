package com.dain23.management.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.dain23.common.model.HeaderEntity;
import com.dain23.common.model.ResponseData;
import com.dain23.management.mapper.SystemMapper;
import com.dain23.util.Const;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemAPIServiceImpl implements SystemAPIService {
	
	/* 매퍼 */
	@Autowired
	private SystemMapper systemMapper;
	

	/*  DMS 컨텐츠 콤보박스 데이터 반환 */
	public Map<String, Object> selectComboOfDmsSetting(Map<String, Object> params) {
		String placeId = Objects.toString(params.get("placeId"), null);
	    String sensorId = Objects.toString(params.get("sensorId"), null);
	    
	    Map<String, Supplier<List<Map<String, Object>>>> baseSuppliers = Map.of(
            "computerCombo", systemMapper::selectComputerCombo,
            "companyCombo", systemMapper::selectCompanyCombo,
            "placeCombo", systemMapper::selectPlaceCombo,
            "sensorTypeCombo", systemMapper::selectSensorTypeCombo,
            "calculationCombo", systemMapper::selectCalculationCombo
        );

        Map<String, Object> result = new LinkedHashMap<>();
        baseSuppliers.forEach((key, supplier) -> result.put(key, supplier.get()));

        if (placeId != null && !placeId.isEmpty()) {
            Map<String, Supplier<List<Map<String, Object>>>> placeSuppliers = Map.of(
                "loggerCombo", () -> systemMapper.selectLoggerCombo(placeId),
                "sensorTypeSettingCombo", () -> systemMapper.selectSensorTypeSettingCombo(placeId),
                "sensorLoggerCombo", () -> systemMapper.selectSensorLoggerCombo(placeId),
                "sensorCombo", () -> systemMapper.selectSensorCombo(placeId)
            );
            placeSuppliers.forEach((key, supplier) -> result.put(key, supplier.get()));
        }

        boolean hasSensorId = sensorId != null && !sensorId.isEmpty();

        result.put("applyCalculation",
            hasSensorId ? systemMapper.selectApplyCalculation(sensorId) : Collections.emptyList()
        );
        result.put("sensorInitial",
            hasSensorId ? systemMapper.selectSensorInitial(sensorId) : Collections.emptyList()
        );

        return result;
	}
	
	
	
	/* DMS PK 중복사용 여부 조회 */
	public ResponseData checkDuplicate(Map<String, Object> params) {
		try {
			String type = params.get("type").toString();

			int success = switch (type) {
			    case "computer" 	-> systemMapper.findComputer(params);
			    case "company"  	-> systemMapper.findCompany(params);
			    case "place"    	-> systemMapper.findPlace(params);
			    case "logger"    	-> systemMapper.findLogger(params);
			    case "sensor"		-> systemMapper.findSensor(params);
			    case "sensortype"	-> systemMapper.findSensorType(params);
			    case "calculation" 	-> systemMapper.findCalculation(params);
			    default -> 1;
			};
			boolean available = (success == 0);
	        int code = available ? Const.SUCCESS_CODE : Const.CONFLICT_CODE;
	        String message = available ? Const.AVAILABLE : Const.UNAVAILABLE;

	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* DMS PC 정보 저장,수정,삭제 */
	public ResponseData upsertDmsComputer(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode
		            ? systemMapper.upsertDmsComputer(params)
		            : systemMapper.deleteDmsComputer(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);

	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* DMS 업체 정보 저장,수정,삭제 */
	public ResponseData upsertDmsCompany(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode
					? systemMapper.upsertDmsCompany(params)
					: systemMapper.deleteDmsCompany(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 현장 정보 저장,수정,삭제 */
	public ResponseData upsertDmsPlace(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int successPlace;
			int successUser = 0;

			if (mode) {
				successPlace = systemMapper.upsertDmsPlace(params);
				
				if (successPlace > 0) {
					String placeCode = params.get("code").toString();
					Map<String, Object> placeInfo = systemMapper.selectPlaceId(placeCode);
					
					if (!((Boolean) placeInfo.get("created"))) {
						boolean created;
						try {
							systemMapper.createDataTable(placeCode);
							created = true;
						} catch (Exception e) {
							created = false;
						}
						if (created) systemMapper.updatePlaceCreated(placeCode);
					}
					successUser = systemMapper.upsertDmsUser(params, Integer.parseInt(placeInfo.get("id").toString()));
				}
			} else {
				successPlace = systemMapper.deleteDmsPlace(params);
				successUser = systemMapper.deleteDmsUser(params.get("uid").toString());
			}
			
			boolean success = successPlace > 0 && successUser > 0;
			int code  = success ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);

	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 로거 정보 저장,수정,삭제 */
	public ResponseData upsertDmsLogger(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode 
					? systemMapper.upsertDmsLogger(params)
					: systemMapper.deleteDmsLogger(params);

			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 센서 정보 저장,수정,삭제 */
	public ResponseData upsertDmsSensor(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode 
					? systemMapper.upsertDmsSensor(params)
					: systemMapper.deleteDmsSensor(params);
			
			Map<String, Object> typeSetInfo = systemMapper.selectSensorTypeSetting(params);
			int count = Integer.parseInt(typeSetInfo.get("find_setting").toString());
			if (count == 0) {
				int nextOrder = Integer.parseInt(typeSetInfo.get("next_group_order").toString());
				systemMapper.insertSensorTypeSetting(params, nextOrder);
			}
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 계산식 적용 정보 저장 */
	public ResponseData insertDmsApplyCalculation(Map<String, Object> params) {
		try {
			int successParam = 0;
			int successApply = systemMapper.insertDmsApplyCalculation(params);
			
			List<Map<String, Object>> paramList = (List<Map<String, Object>>) params.get("param");
	        if (paramList != null && !paramList.isEmpty()) {
	            successParam = systemMapper.insertDmsCalculationParam(params);
	        }
			
	        boolean success = successApply > 0 && (paramList == null || paramList.isEmpty() || successParam > 0);
			int code  = success ? Const.SUCCESS_CODE : Const.FAIL_CODE;
	        String message = success ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL;

	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* DMS 초기치 정보 저장 */
	public ResponseData insertDmsSensorInitial(Map<String, Object> params) {
		try {
			int success = systemMapper.insertDmsSensorInitial(params);
			
			int code  = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
	        String message = (success > 0) ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL;
	        
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 센서타입 정보 저장,수정,삭제 */
	public ResponseData upsertDmsSensorType(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode 
					? systemMapper.upsertDmsSensorType(params)
					: systemMapper.deleteDmsSensorType(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 센서타입설정 정보 저장,수정 */
	public ResponseData upsertDmsSensorTypeSetting(Map<String, Object> params) {
		try {
			int success = systemMapper.upsertDmsSensorTypeSetting(params);
			
			int code  = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
	        String message = (success > 0) ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL;
	        
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}



	/* DMS 계산식 정보 저장,수정,삭제 */
	public ResponseData upsertDmsCalculation(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode 
					? systemMapper.upsertDmsCalculation(params)
					: systemMapper.deleteDmsCalculation(params);

			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 파일관리 이동및백업 저장,수정,삭제 */
	public ResponseData upsertMoveAndBackup(Map<String, Object> params) {
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode
					? systemMapper.upsertMoveAndBackup(params)
					: systemMapper.removeMoveAndBackup(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
}
