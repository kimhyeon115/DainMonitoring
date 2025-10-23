package com.dain23.common.model;

import lombok.Data;

/**
 *	요청 파라미터 데이터
 */
@Data
public class ParamBody {
	private String select;
	private String placeCode;
	private String start;
	private String end;
	private boolean average;
	private boolean oclock;
	private boolean excel;
	private boolean scroll;
	private int limit;
	private int offset;
}
