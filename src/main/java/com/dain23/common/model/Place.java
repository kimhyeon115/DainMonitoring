package com.dain23.common.model;

import lombok.Data;

/**
 *	현장 데이터
 */
@Data
public class Place {
	private int id;
	private String code;
	private String shortName;
	private String fullName;
	private String installDt;
}
