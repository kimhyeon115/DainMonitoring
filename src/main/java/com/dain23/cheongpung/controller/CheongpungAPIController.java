package com.dain23.cheongpung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dain23.common.model.ParamBody;
import com.dain23.common.service.CommonAPIService;

/**
 *	API 요청 수신 및 데이터 반환
 **/
@RestController
@RequestMapping("/cheongpung/api")
public class CheongpungAPIController {
	
	/* 현장 코드 */
	private final String placeCode = "cheongpung";
	
	/* 서비스 */
	@Autowired
	private CommonAPIService commonAPIService;
	
	
	/* 메인 화면 데이터 반환 */
	@PostMapping("main")
	public ResponseEntity<?> getEveryGraph() {
		return ResponseEntity.ok(commonAPIService.getMainContent(placeCode));
	}
	
	
	/* 데이터 화면 데이터 반환 */
	@PostMapping("data")
	public ResponseEntity<?> getSensorData(@RequestBody ParamBody body) {
		return ResponseEntity.ok(commonAPIService.getDataContent(placeCode, body));
	}

}
