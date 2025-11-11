package com.dain23.management.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.dain23.common.model.HeaderEntity;
import com.dain23.common.model.ResponseData;
import com.dain23.management.mapper.ManagementMapper;
import com.dain23.util.Const;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ManagementAPIServiceImpl implements ManagementAPIService {
	
	/* 매퍼 */
	@Autowired
	private ManagementMapper managementMapper;

	
	/* 로거 정보 수정 */
	public ResponseData updateLoggerInfo(Map<String, Object> params) {
		try {
			int success = managementMapper.updateLoggerInfo(params);
			
			int code  = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
	        String message = (success > 0) ? Const.SUCCESS : Const.CONFLICT;
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 웹 외부 오픈 허용 여부 */
	public ResponseData openOrClosePage(Map<String, Object> params) {
		try {
			int success = managementMapper.updateOpenOrClose(params);
			
			int code  = (success > 0) ? Const.SUCCESS_CODE : Const.FAIL_CODE;
	        String message = (success > 0) ? Const.SUCCESS : Const.CONFLICT;
			
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, code, message));
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 로거내 센서 정보 조회 */
	public Map<String, Object> findSensorInLogger(Map<String, Object> params) {
		Map<String, Object> result = new LinkedHashMap<>();
		List<Map<String, Object>> sensors = managementMapper.findSensorInLogger(params);
		result.put("sensors", sensors);
		return result;
	}
	
	
	
	/* 센서 분석 데이터 조회 */
	public ResponseData makemakeAnalysisData(Map<String, Object> params) {
		Map<String, Object> body = new LinkedHashMap<>();
		
		try {
			Map<String, Object> chartData = managementMapper.selectAnalysisData(params);
			body.put("chartData", chartData);
			
			List<Map<String, Object>> totalFail = managementMapper.selectFailRecord(params);
			body.put("totalFail", totalFail);
		
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, Const.SUCCESS_CODE, Const.SUCCESS), body);
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
	
	
	
	/* 센서 추이 데이터 조회 */
	public ResponseData makeTransitionData(Map<String, Object> params) {
		try {
			
			int count = managementMapper.countSensorDataRows(params);
			
			boolean avg = count > 2000;
			
			List<Map<String, Object>> sensorData = avg 
					? managementMapper.selectAvgTransitionData(params)
					: managementMapper.selectTransitionData(params);
			
			Map<String, Object> result = new LinkedHashMap<>();
			result.put("sensorData", sensorData);
		
			return ResponseData.of(HeaderEntity.of(HttpStatus.OK, Const.SUCCESS_CODE, Const.SUCCESS), result);
		} catch (Exception e) {
			return ResponseData.of(HeaderEntity.fail());
		}
	}
}
