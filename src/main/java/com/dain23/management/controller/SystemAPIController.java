package com.dain23.management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dain23.common.model.ResponseData;
import com.dain23.management.service.SystemAPIService;

@Controller
@RequestMapping("/management/system/api")
public class SystemAPIController {
	
	/* 페이지 코드 */
	private String placeCode = "management";
	
	/* 서비스 */
	@Autowired
	private SystemAPIService systemAPIService;
	
	
	/* (공용) 타겟 디테일 데이터 정보 */
	@PostMapping("find")
	@ResponseBody
	public ResponseData findTargetOfDetail(@RequestBody Map<String, Object> params) {
		return systemAPIService.selectTargetDetail(params);
	}
	
	
	
	/* DMS 컨텐츠 내의 선택요소 정보 */
	@PostMapping("combo")
	public String selectCombo(@RequestBody Map<String, Object> params, Model model)  {
		
		/* 카테고리 기준 실행 서비스 정의 */
		Map<String, Runnable> attrMap = Map.of(
			"dmsSetting", () -> model.addAllAttributes(systemAPIService.selectComboOfDmsSetting(params)),
			"dataEdit", () -> model.addAllAttributes(systemAPIService.selectComboOfDataEdit(params)),
			"dataDelete", () -> model.addAllAttributes(systemAPIService.selectComboOfDataDel(params))
	    );
		
		attrMap.getOrDefault(params.get("category"), () -> {}).run();
		return String.format("%s/content/combo", placeCode);
	}
	
	
	
	/* DMS 중복사용 여부 */
	@PostMapping("duplicate")
	@ResponseBody
	public ResponseData checkDuplicate(@RequestBody Map<String, Object> params) {
		return systemAPIService.checkDuplicate(params);
	}
	
	
	
	/* DMS PC 정보 */
	@PostMapping("computer")
	@ResponseBody
	public ResponseData computerSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsComputer(params);
	}
	

	
	/* DMS 업체 정보 */
	@PostMapping("company")
	@ResponseBody
	public ResponseData companySave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsCompany(params);
	}
	
	
	
	/* DMS 현장 정보 */
	@PostMapping("place")
	@ResponseBody
	public ResponseData placeSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsPlace(params);
	}
	
	
	
	/* DMS 로거 정보 */
	@PostMapping("logger")
	@ResponseBody
	public ResponseData loggerSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsLogger(params);
	}
	
	
	
	/* DMS 센서 정보 */
	@PostMapping("sensor")
	@ResponseBody
	public ResponseData sensorSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsSensor(params);
	}

	
	
	/* DMS 계산식 적용 정보 */
	@PostMapping("applycalculation")
	@ResponseBody
	public ResponseData applycalculationSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.insertDmsApplyCalculation(params);
	}
	
	
	
	/* DMS 초기치 정보 */
	@PostMapping("sensorinitial")
	@ResponseBody
	public ResponseData sensorinitialSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.insertDmsSensorInitial(params);
	}
	
	
	
	/* DMS 센서타입 정보 */
	@PostMapping("sensortype")
	@ResponseBody
	public ResponseData sensorTypeSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsSensorType(params);
	}
	
	
	
	/* DMS 센서타입설정 정보 */
	@PostMapping("sensortypesetting")
	@ResponseBody
	public ResponseData sensorTypeSettingSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsSensorTypeSetting(params);
	}
	
	
	
	/* DMS 계산식 정보 */
	@PostMapping("calculation")
	@ResponseBody
	public ResponseData calculationSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDmsCalculation(params);
	}
	
	
	
	/* 파일관리 이동및백업 정보 */
	@PostMapping("moveandbackup")
	@ResponseBody
	public ResponseData moveAndBackSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertMoveAndBackup(params);
	}
	
	
	
	/* 수동 편집 */
	@PostMapping("dataedit")
	@ResponseBody
	public ResponseData dataEditSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.updateDataEdit(params);
	}
	
	
	
	/* 자동 삭제 설정 정보 */
	@PostMapping("datadelete")
	@ResponseBody
	public ResponseData dataDeleteSave(@RequestBody Map<String, Object> params) {
		return systemAPIService.upsertDataDelete(params);
	}
	
	
	
	/* 계측 업로드 | 로거 상태 변경 */
	@PostMapping("loggerstatus")
	@ResponseBody
	public ResponseData loggerStatusUpdate(@RequestBody Map<String, Object> params) {
		return systemAPIService.updateLoggerStatus(params);
	}
	
}
