package com.dain23.common.model;

import java.math.BigDecimal;

/**
 *	마지막 데이터
 */
public interface LatestData {
	Integer getLoggerId();
	Integer getSensorTypeId();
	String getSensorCode();
	Integer getGroupOrder();
	String getChangedVal();
	
	BigDecimal getCumulativeVal();
	void setCumulativeVal(BigDecimal sum);
}
