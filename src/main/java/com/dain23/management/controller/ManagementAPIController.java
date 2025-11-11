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
import com.dain23.management.service.ManagementAPIService;

@Controller
@RequestMapping("/management/api")
public class ManagementAPIController {
	
	/* 페이지 코드 */
	private String placeCode = "management";
	
	/* 서비스 */
	@Autowired
	private ManagementAPIService managementAPIService;
	
	
	/* 로거 상태 정보 */
	@PostMapping("logger")
	@ResponseBody
	public ResponseData updateLoggerName(@RequestBody Map<String, Object> params) {
		return managementAPIService.updateLoggerInfo(params);
	}
	
	
	
	/* 웹 외부 오픈 여부 */
	@PostMapping("openorclose")
	@ResponseBody
	public ResponseData openOrClosePage(@RequestBody Map<String, Object> params) {
		return managementAPIService.openOrClosePage(params);
	}
	
	
	
	/* 로거내 센서 콤보 조회 */
	@PostMapping("sensorinlogger")
	public String findSensorInLogger(@RequestBody Map<String, Object> params, Model model) {
		model.addAllAttributes(managementAPIService.findSensorInLogger(params));
		return String.format("%s/content/combo", placeCode);
	}
	
	
	
	/* 센서 분석 차트 데이터 조회 */
	@PostMapping("analysisdata")
	@ResponseBody
	public ResponseData makeAnalysisData(@RequestBody Map<String, Object> params) {
		return managementAPIService.makemakeAnalysisData(params);
	}
	
	
	
	/* 센서 추이 차트 데이터 조회 */
	@PostMapping("transitiondata")
	@ResponseBody
	public ResponseData makeTransitionData(@RequestBody Map<String, Object> params) {
		return managementAPIService.makeTransitionData(params);
	}

}
