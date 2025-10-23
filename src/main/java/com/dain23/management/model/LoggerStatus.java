package com.dain23.management.model;

import lombok.Data;

/**
 *	로거 상태 데이터
 */
@Data
public class LoggerStatus {
	private int loggerId;
	private String placeCode;
	private String loggerCode;
	private String loggerName;
	private String cdmaNo;
	private String installDt;
	private String uploadedAt;
	private int pcId;
	private String pcName;
}
