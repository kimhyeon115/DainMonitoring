package com.dain23.common.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dain23.common.service.CommonService;

/**
 * 스케줄러
 */
@Component
public class Dain23Scheduler {
	
	@Autowired
	private CommonService commonService;
	
	
	/* 만료 세션 삭제 */
	@Scheduled(cron = "0 0 0 * * *")
	public void cleanExpiredSessions() {
		int expireMinutes = 60;
		commonService.clearExpiredSession(expireMinutes);
	}
	
	
	
	/* 업로드 로그 삭제 */
	@Scheduled(cron = "0 5 0 * * *")
	public void cleanOldUploadLogs() {
		commonService.clearOldUploadLogs();
	}

}
