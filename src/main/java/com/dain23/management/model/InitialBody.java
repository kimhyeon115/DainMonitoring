package com.dain23.management.model;

import lombok.Data;

/**
 *  센서 초기치 데이터
 **/
@Data
public class InitialBody {
	private double rawVal;
	private double displaceVal;
	private String fromDt;
	private String toDt;
}
