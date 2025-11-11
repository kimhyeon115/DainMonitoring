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
import com.dain23.management.model.TransactionPlace;
import com.dain23.management.model.TransactionSensor;
import com.dain23.util.Const;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemAPIServiceImpl implements SystemAPIService {
	
	/* 매퍼 */
	@Autowired
	private SystemMapper systemMapper;
	
	@Autowired
	private TransactionService transactionService;
	
	
	/* (공용) 타겟 데이터 정보 조회 및 반환 */
	public ResponseData selectTargetDetail(Map<String, Object> params) {

		String type = Objects.toString(params.get("type"), null);
		String id = Objects.toString(params.get("id"), null);
		if (type == null || id == null) return ResponseData.of(HeaderEntity.bad());
		
		try {
			Map<String, Object> body = switch (type) {
				case "DMSlogger" -> systemMapper.selectSingleLogger(params);
				case "DMSSensor" -> systemMapper.selectSingleSensor(params);
				case "DMSsensortypesetting" -> systemMapper.selectSingleSensorTypeSetting(params);
				case "moveandbackup" -> systemMapper.selectSingleMoveAndBackup(params);
				case "dataedit" -> systemMapper.selectSingleDataEdit(params);
			    case "datadelete" 	-> systemMapper.selectSingleDataDelete(params);
			    default -> null;
			};

			boolean available = (body != null);
	        int code = available ? Const.SUCCESS_CODE : Const.NO_TARGET_CODE;
	        String message = available ? Const.SUCCESS : Const.NO_TARGET;
	
	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message), body);
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
 	}
	
	

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
        TransactionPlace transactionPlace = null;
        
        try {
        	transactionPlace = transactionService.upsertDmsPlaceDml(params);
            
            if (transactionPlace.isNeedsTableCreation()) {
                systemMapper.createDataTable(transactionPlace.getPlaceCode()); 
                systemMapper.updatePlaceCreated(transactionPlace.getPlaceCode());
            }
            
            boolean mode = Boolean.parseBoolean(params.get("mode").toString());
            String message = mode ? Const.UPSERT_SUCCESS : Const.DELETE_SUCCESS;
            return ResponseData.of(HeaderEntity.of(HttpStatus.OK, Const.SUCCESS_CODE, message));
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
		
		TransactionSensor transactionSensor = null;
		
		try {
			transactionSensor = transactionService.upsertDmsSensorDml(params);
			
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
            int success = transactionSensor.getSuccessCount();
            
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
			int targetCount = systemMapper.selectExecutionCalculationCount(params);
			
			if (targetCount > 5000) {
				return ResponseData.of(HeaderEntity.of(HttpStatus.OK, Const.BAD_REQUEST_CODE,
	                "적용 대상 데이터가 " + targetCount + "건입니다.\n" +
	                "5000건을 초과하므로 기간을 나누어 다시 설정하세요."));
			}
			
			transactionService.insertDmsApplyCalculationDml(params, targetCount);
			
			int code = Const.SUCCESS_CODE;
	        String message = Const.UPSERT_SUCCESS;
	        return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* DMS 초기치 정보 저장 */
	public ResponseData insertDmsSensorInitial(Map<String, Object> params) {
		try {
			int targetCount = systemMapper.selectInitialCalculationCount(params);
			
			if (targetCount > 5000) {
				return ResponseData.of(HeaderEntity.of(HttpStatus.OK, Const.BAD_REQUEST_CODE,
	                "적용 대상 데이터가 " + targetCount + "건입니다.\n" +
	                "5000건을 초과하므로 기간을 나누어 다시 설정하세요."));
			}
			
			transactionService.insertDmsSensorInitialDml(params, targetCount);
	        
	        int code = Const.SUCCESS_CODE;
	        String message = Const.UPSERT_SUCCESS;
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
	
	
	
	/*  수동 편집 컨텐츠 콤보박스 데이터 반환 */
	public Map<String, Object> selectComboOfDataEdit(Map<String, Object> params) {
		String placeId = Objects.toString(params.get("placeId"), null);

        Map<String, Object> result = new LinkedHashMap<>();

        if (placeId != null && !placeId.isEmpty()) {
            Map<String, Supplier<List<Map<String, Object>>>> placeSuppliers = Map.of(
                "applySensorTypeCombo", () -> systemMapper.selectApplySensorTypeCombo(placeId),
                "sensorCombo", () -> systemMapper.selectSensorCombo(placeId)
            );
            placeSuppliers.forEach((key, supplier) -> result.put(key, supplier.get()));
        }
        
        String search = Objects.toString(params.get("search"), null);
        String placeCode = Objects.toString(params.get("placeCode"), null);
        String sensorId = Objects.toString(params.get("sensorId"), null);
        
        if (search == null || sensorId == null || placeCode == null) {
        	result.put("dataEdit", Collections.emptyList());
        } else {
        	result.put("dataEdit", systemMapper.selectDataEdit(params));
        }
        
        return result;
	}
	
	
	
	/*  자동 삭제 컨텐츠 콤보박스 데이터 반환 */
	public Map<String, Object> selectComboOfDataDel(Map<String, Object> params) {
		String placeId = Objects.toString(params.get("placeId"), null);
		
        Map<String, Object> result = new LinkedHashMap<>();

        if (placeId != null && !placeId.isEmpty()) {
            Map<String, Supplier<List<Map<String, Object>>>> placeSuppliers = Map.of(
                "loggerEventCombo", () -> systemMapper.selectEventLoggerCombo(placeId),
                "sensorEventCombo", () -> systemMapper.selectEventSensorCombo(placeId)
            );
            placeSuppliers.forEach((key, supplier) -> result.put(key, supplier.get()));
        }

        return result;
	}
	
	
	
	/* 데이터 관리 수동 편집 수정,삭제 */
	public ResponseData updateDataEdit(Map<String, Object> params) {
		
		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());

			int success = mode
					? systemMapper.updateDataEdit(params)
					: systemMapper.removeDataEdit(params);

			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 데이터 관리 수동 편집 데이터 사용여부 수정 */
	public ResponseData updateDataStatus(Map<String, Object> params) {
		
		try {
			int success = systemMapper.updateDataStatus(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL;
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 데이터 관리 자동 삭제 저장,수정,삭제 */
	public ResponseData upsertDataDelete(Map<String, Object> params) {

		try {
			boolean mode = Boolean.parseBoolean(params.get("mode").toString());
			int success = mode
					? systemMapper.upsertDataDelete(params)
					: systemMapper.removeDataDelete(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = mode
		            ? (success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL)
		            : (success > 0 ? Const.DELETE_SUCCESS : Const.DELETE_FAIL);
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 스케줄러 | 계측 업로드 로거 상태 수정 */
	public ResponseData updateLoggerStatus(Map<String, Object> params) {

		try {
			int success = systemMapper.updateLoggerStatus(params);
			
			int code = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
			String message = success > 0 ? Const.UPSERT_SUCCESS : Const.UPSERT_FAIL;
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
}
