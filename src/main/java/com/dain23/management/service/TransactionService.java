package com.dain23.management.service;

import java.util.Map;

import com.dain23.management.model.TransactionCalculation;
import com.dain23.management.model.TransactionInitial;
import com.dain23.management.model.TransactionPlace;
import com.dain23.management.model.TransactionSensor;

public interface TransactionService {
	
	TransactionPlace upsertDmsPlaceDml(Map<String, Object> params);
	
	TransactionSensor upsertDmsSensorDml(Map<String, Object> params);
	
	TransactionCalculation insertDmsApplyCalculationDml(
		Map<String, Object> params, int targetCount
	);
	
	TransactionInitial insertDmsSensorInitialDml(
		Map<String, Object> params, int targetCount
	);

}
