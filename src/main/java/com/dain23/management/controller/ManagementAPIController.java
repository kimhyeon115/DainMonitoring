package com.dain23.management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dain23.common.model.ResponseData;
import com.dain23.management.service.ManagementAPIService;

@RestController
@RequestMapping("/management/api")
public class ManagementAPIController {
	
	/* 서비스 */
	@Autowired
	private ManagementAPIService managementAPIService;
	
	
	/* 로거 상태 정보 */
	@PostMapping("logger")
	public ResponseData updateLoggerName(@RequestBody Map<String, Object> params) {
		return managementAPIService.updateLoggerInfo(params);
	}
	
	
	/* 웹 외부 오픈 여부 */
	@PostMapping("openorclose")
	public ResponseData openOrClosePage(@RequestBody Map<String, Object> params) {
		return managementAPIService.openOrClosePage(params);
	}

}
