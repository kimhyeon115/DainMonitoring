package com.dain23.cheongpung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dain23.common.service.CommonService;

/**
 *	도메인 요청 수신 및 화면 반환
 **/
@Controller
@RequestMapping("/cheongpung")
public class CheongpungController {
	
	/* 현장 코드 */
	private final String placeCode = "cheongpung";
	
	/* 서비스 */
	@Autowired
	private CommonService commonService;
		
	
	/* 메인으로 리다이렉트 */
	@GetMapping("/")
	public String skipLogin() {
		if (!commonService.checkPageOpen(placeCode)) return "redirect:patch";
		return "redirect:main";
	}
	
	
	/* 메인 화면 구성 반환 */
	@GetMapping("main")
	public String makeMain(Model model) {
        model.addAttribute("model", commonService.makeMain(placeCode));
	    return String.format("%s/Main", placeCode);
	}
	
	
	/* 데이터 화면 구성 반환 */
	@GetMapping("data")
	public String makeData(@RequestParam(value="select") String select, Model model) {
		model.addAttribute("model", commonService.makeData(placeCode, select));
		return String.format("%s/Data", "common");
	}
	
	
	/* 서버점검 화면 반환 */
	@GetMapping("patch")
	public String serverPatch(Model model) {
		if (commonService.checkPageOpen(placeCode)) return "redirect:main";
		model.addAttribute("placeCode", placeCode);
		return String.format("%s/Patch", "common");
	}

}
