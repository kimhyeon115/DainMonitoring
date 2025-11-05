package com.dain23.management.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dain23.management.mapper.SystemMapper;
import com.dain23.management.model.TransactionCalculation;
import com.dain23.management.model.TransactionInitial;
import com.dain23.management.model.TransactionPlace;
import com.dain23.management.model.TransactionSensor;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TransactionServiceImpl implements TransactionService {
	
	/* 매퍼 */
	@Autowired
	private SystemMapper systemMapper;
	
	
	/* DMS 현장 정보 저장,수정,삭제 (트랜잭션 보장) */
	public TransactionPlace upsertDmsPlaceDml(Map<String, Object> params) {

		boolean mode = Boolean.parseBoolean(params.get("mode").toString());
		int successPlace;
		int successUser = 0;
		
		TransactionPlace result = new TransactionPlace();

		if (mode) {
			successPlace = systemMapper.upsertDmsPlace(params);
			
			if (successPlace > 0) {
				String placeCode = params.get("code").toString();
				Map<String, Object> placeInfo = systemMapper.selectPlaceId(placeCode);
				
				result.setPlaceCode(placeCode);
				
				if (!((Boolean) placeInfo.get("created"))) {
                    result.setNeedsTableCreation(true);
                }
				
				successUser = systemMapper.upsertDmsUser(params, Integer.parseInt(placeInfo.get("id").toString()));
			}
		} else {
			successUser = systemMapper.deleteDmsUser(params.get("uid").toString());
			successPlace = systemMapper.deleteDmsPlace(params);
		}
		
		if (successPlace <= 0 || successUser <= 0) {
            throw new RuntimeException("저장 실패: 성공 카운트 부족.");
        }

        return result;
	}
	
	
	
	/* DMS 센서 정보 저장,수정,삭제 (트랜잭션 보장) */
	public TransactionSensor upsertDmsSensorDml(Map<String, Object> params) {
		
		boolean mode = Boolean.parseBoolean(params.get("mode").toString());
		int success;
		success = mode 
                ? systemMapper.upsertDmsSensor(params)
                : systemMapper.deleteDmsSensor(params);
		
		if (success > 0) {
            Map<String, Object> typeSetInfo = systemMapper.selectSensorTypeSetting(params);
            int count = Integer.parseInt(typeSetInfo.get("find_setting").toString());

            if (count == 0) {
                int nextOrder = Integer.parseInt(typeSetInfo.get("next_group_order").toString());
                systemMapper.insertSensorTypeSetting(params, nextOrder);
            }
        }
		
		if (success <= 0) {
            throw new RuntimeException("저장 실패: 성공 카운트 부족.");
        }
		
		TransactionSensor result = new TransactionSensor();
        result.setSuccessCount(success);
        return result;
	}
	
	
	
	/* DMS 계산식 적용 정보 저장 (트랜잭션 보장) */
	public TransactionCalculation insertDmsApplyCalculationDml(Map<String, Object> params, int targetCount) {
		
		int successParam = 0;
		int successApply = systemMapper.insertDmsApplyCalculation(params);
		List<Map<String, Object>> paramList = (List<Map<String, Object>>) params.get("param");
		
		if (paramList != null && !paramList.isEmpty()) {
	        successParam = systemMapper.insertDmsCalculationParam(params);
	    }
		
		int successUpdate = systemMapper.updateSensorNewCalculation(params);
		
		boolean successDml = successApply > 0 && 
                (paramList == null || paramList.isEmpty() || successParam > 0) &&
                (successUpdate == targetCount);
	    
	    if (!successDml) {
	        throw new RuntimeException("저장 실패: 성공 카운트 부족.");
	    }
	    
	    TransactionCalculation result = new TransactionCalculation();
	    result.successApply = successApply;
	    result.successParam = successParam;
	    result.paramList = paramList;
	    return result;
	}
	
	
	
	/* DMS 초기치 정보 저장 (트랜잭션 보장) */
	public TransactionInitial insertDmsSensorInitialDml(Map<String, Object> params, int targetCount) { 
	    
	    int successInsert = systemMapper.insertDmsSensorInitial(params);
	    int successUpdate = systemMapper.updateSensorInitialCalculation(params); 
	    
	    boolean successDml = (successInsert > 0) &&
	                         (successUpdate == targetCount);
	    
	    if (!successDml) {
	        throw new RuntimeException("저장 실패: 성공 카운트 부족.");
	    }

	    TransactionInitial result = new TransactionInitial();
	    result.successInsert = successInsert;
	    result.successUpdate = successUpdate;
	    return result;
	}
	
}
