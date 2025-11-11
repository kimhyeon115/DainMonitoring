package com.dain23.management.model;

import lombok.Data;

/**
 *  수동 편집 데이터
 **/
@Data
public class DataEditBoby {
	private int id;
	private String placeCode;
	private String placeName;
	private String sensorCode;
	private String sensorName;
	private String measuredAt;
	private double rawVal;
	private double correctionVal;
	private double angleVal;
	private double displaceVal;
	private double initialVal;
	private double changedVal;
	private String used;
}
