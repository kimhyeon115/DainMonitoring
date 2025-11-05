package com.dain23.management.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dain23.common.model.Session;
import com.dain23.management.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/management/system")
public class SystemController {

	/* 페이지 코드 */
	private String placeCode = "management";
	
	/* 서비스 */
	@Autowired
	private SystemService systemService;
	
	
	
	/* 기본 화면 반환 */
	@GetMapping("")
	public String system(Model model, HttpServletRequest request) {
		
		/* 유저 정보 추출 */
		Session userSession = (Session) request.getAttribute("userSession");
		String user = userSession != null ? (String) userSession.getLevel() : null;
		
		/* 키:데이터 -> 모델에 전달 */
		model.addAttribute("pageName", placeCode);
		model.addAttribute("user", user);
		
		return String.format("%s/Setting", placeCode);
	}
	
	
	
	/* 카테고리 기준 컨텐츠 화면 반환 */
	@GetMapping("content")
	public String content(@RequestParam("category") String category, Model model) {
		
		/* 키:데이터 -> 모델에 전달 */
		model.addAttribute("pageName", placeCode);
		
		/* 카테고리 기준 실행 서비스 정의 */
		Map<String, Runnable> attrMap = Map.of(
			"uploadMeasurements", () -> model.addAllAttributes(systemService.getUploadMeasurementsFrame()),
			"dmsSetting", () -> model.addAllAttributes(systemService.getDmsSettingFrame()),
	        "moveAndBackup", () -> model.addAllAttributes(systemService.getMoveAndBackupFrame()),
	        "dataEdit", () -> model.addAllAttributes(systemService.getDataEditFrame()),
	        "dataDelete", () -> model.addAllAttributes(systemService.getDataDeleteFrame())
	    );

		/* 카테고리 기준 서비스 실행 */
	    attrMap.getOrDefault(category, () -> {}).run();
	    return String.format("management/content/%s", category);
	}
	
}
