package com.dain23.management.service;

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
	
}
