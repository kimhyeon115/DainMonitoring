package com.dain23.common.model;

import lombok.Data;

/**
 *	센서타입설정 데이터
 */
@Data
public class SensorTypeSet {
	private int placeId;
	private int loggerId;
	private int sensorTypeId;
	private String sensorTypeCode;
	private int sensorId;
	private String sensorCode;
	private String sensorName;
	private String location;
	private String minGauge;
	private String maxGauge;
	private int mainGraphType;
	private int subGraphType;
	private String criteriaVal1;
	private String criteriaVal2;
	private String criteriaVal3;
	private int interval;
	private boolean cycleCheck;
}
