package com.dain23.management.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dain23.common.service.CommonService;
import com.dain23.management.service.ManagementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 *	도메인 요청 수신 및 화면 반환
 **/
@Controller
@RequestMapping("/management")
public class ManagementController {
	
	/* 페이지 코드 */
	private String placeCode = "management";
	
	/* 서비스 */
	@Autowired
	private CommonService commonService;
	@Autowired
	private ManagementService managementService;
	
	
	/* 로그인 화면 구성 반환 */
	@GetMapping("/")
	public String makeLogin(HttpServletRequest request, Model model) {
		if (commonService.isValidSession(request, placeCode)) {
			if (!commonService.checkPageOpen(placeCode)) return "redirect:patch";
			return "redirect:main";
		} else {
			return String.format("%s/Login", "common");
		}
	}
	
	
	/* 메인 화면 구성 반환 */
	@GetMapping("main")
	public String makeMain(Model model) {
		model.addAttribute("model", managementService.makeManagement(placeCode));			
		return String.format("%s/Main", placeCode);
	}
	
	
	/* 로그인 인증 및 화면 반환 */
	@PostMapping("signIn")
	public String connection(@RequestParam("id") String id, @RequestParam("pw") String pw,
		HttpServletRequest request, HttpServletResponse response, Model model
	) {
		return commonService.verification(request, response, model, id, pw, placeCode);
	}
	
	
	/* 로그아웃 및 화면 반환 */
	@GetMapping("signOut")
	public String disconnect(HttpServletRequest request, HttpServletResponse response) {
		return commonService.pageExit(request, response, placeCode);
	}
	
	
	/* 엑세 다운로드 반환 */
	@PostMapping("excel")
	public void clientLoggerDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (!commonService.isValidSession(request, placeCode)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		managementService.makeExcelSheet(response);
	}

	
	/* 서버점검 화면 반환 */
	@GetMapping("patch")
	public String serverPatch(Model model) {
		if (commonService.checkPageOpen(placeCode)) return "redirect:main";
		model.addAttribute("placeCode", placeCode);
		return String.format("%s/Patch", "common");
	}
	
}
