package com.dain23.common.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 *	정각 타입 마지막 데이터
 */
@Data
public class CycleLatestData implements LatestData {
	private int loggerId;
	private int sensorId;
	private String sensorCode;
	private String sensorName;
	private int sensorTypeId;
	private String sensorTypeName;
	private String location;
	private String initialVal;
	private String displaceVal;
	private String changedVal;
	private String criteriaVal1;
	private int groupOrder;
	
	private BigDecimal cumulativeVal;
	
	@Override public Integer getLoggerId() {return loggerId;}
	@Override public Integer getSensorTypeId() {return sensorTypeId;}
	@Override public String getSensorCode() {return sensorCode;}
	@Override public Integer getGroupOrder() {return groupOrder;}
	@Override public BigDecimal getCumulativeVal() { return cumulativeVal; }
    @Override public void setCumulativeVal(BigDecimal sum) { this.cumulativeVal = sum; }
}
