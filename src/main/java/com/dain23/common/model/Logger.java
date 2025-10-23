package com.dain23.common.model;

import lombok.Data;

/**
 * 로거 데이터
 */
@Data
public class Logger {
	private int id;
	private String code;
	private String name;
	private String location;
	private String firstAt;
	private String lastAt;
	private String searchAt;
}
