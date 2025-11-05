package com.dain23.management.model;

import java.util.List;

import lombok.Data;

/**
 *	업체별 현장 데이터
 */
@Data
public class Management {
	private int placeId;
	private String comShortName;
	private String comFullName;
	private String placeCode;
	private boolean run;
	private boolean open;
	private boolean oldVersion;
	private boolean used;
	private String url;
	private String placeShortName;
	private List<LoggerStatus> loggerStatus;
}
