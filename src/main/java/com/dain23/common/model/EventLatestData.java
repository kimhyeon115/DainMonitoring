package com.dain23.common.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 *	이벤트 타입 마지막 데이터
 */
@Data
public class EventLatestData implements LatestData {
	private	int loggerId;
	private int sensorId;
	private String sensorCode;
	private int sensorTypeId;
	private String sensorTypeName;
	private String lastMeasuredAt;
	private String maxChangedVal;
	private String todayDataCount;
	private int groupOrder;
	
	private String changedVal;
    private BigDecimal cumulativeVal;
	
	@Override public Integer getLoggerId() {return loggerId;}
	@Override public Integer getSensorTypeId() {return sensorTypeId;}
	@Override public String getSensorCode() {return sensorCode;}
	@Override public Integer getGroupOrder() {return groupOrder;}
    @Override public String getChangedVal() { return changedVal; } 
    @Override public BigDecimal getCumulativeVal() { return cumulativeVal; }
    @Override public void setCumulativeVal(BigDecimal sum) { this.cumulativeVal = sum; }
}
