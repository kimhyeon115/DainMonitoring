package com.dain23.jungdong.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dain23.common.service.CommonService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *	도메인 요청 수신 및 화면 반환
 **/
@Controller
@RequestMapping("/jungdong")
public class JungdongController {
	
	/* 현장 코드 */
	private final String placeCode = "jungdong";
	
	/* 서비스 */
	@Autowired
	private CommonService commonService;
		
	
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
		model.addAttribute("model", commonService.makeMain(placeCode));
		return String.format("%s/Main", placeCode);
	}
	
	
	/* 데이터 화면 구성 반환 */
	@GetMapping("data")
	public String makeData(@RequestParam(value = "select") String select, Model model) {
		model.addAttribute("model", commonService.makeData(placeCode, select));
		return String.format("%s/Data", "common");
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
	
	
	/* 서버점검 화면 반환 */
	@GetMapping("patch")
	public String serverPatch(Model model) {
		if (commonService.checkPageOpen(placeCode)) return "redirect:main";
		model.addAttribute("placeCode", placeCode);
		return String.format("%s/Patch", "common");
	}

}
