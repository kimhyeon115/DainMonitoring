package com.dain23.management.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dain23.management.mapper.SystemMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {
	
	/* 매퍼 */
	@Autowired
	private SystemMapper systemMapper;
	
	
	/* DMS 설정 컨텐츠 데이터 반환 */
	public Map<String, Object> getDmsSettingFrame() {
		Map<String, Supplier<List<Map<String, Object>>>> suppliers = Map.of(
	        "selectComputerCombo", systemMapper::selectComputerCombo,
	        "selectCompanyCombo", systemMapper::selectCompanyCombo,
	        "selectPlaceCombo", systemMapper::selectPlaceCombo,
	        "selectSensorTypeCombo", systemMapper::selectSensorTypeCombo,
	        "selectCalculationCombo", systemMapper::selectCalculationCombo
	    );

	    Map<String, Object> result = new LinkedHashMap<>();
	    suppliers.forEach((key, supplier) -> result.put(key, supplier.get()));
	    return result;
	}


	
	/* 이동및백업 컨텐츠 데이터 반환 */
	public Map<String, Object> getMoveAndBackupFrame() {
		Map<String, Supplier<List<Map<String, Object>>>> suppliers = Map.of(
	        "selectMoveAndBackup", systemMapper::selectMoveAndBackup,
	        "selectComputer", systemMapper::selectComputerCombo,
	        "selectParsingCode", () -> systemMapper.selectCodeSet(1)
	    );

	    Map<String, Object> result = new LinkedHashMap<>();
	    suppliers.forEach((key, supplier) -> result.put(key, supplier.get()));
	    return result;
	}

}
