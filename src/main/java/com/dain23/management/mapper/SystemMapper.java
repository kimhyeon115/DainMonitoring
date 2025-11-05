package com.dain23.management.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dain23.management.model.DataEditBoby;
import com.dain23.management.model.InitialBody;

@Mapper
public interface SystemMapper {

	List<Map<String, Object>> selectMoveAndBackup();
	
	List<Map<String, Object>> selectCodeSet(@Param("type") int type);
	
	int upsertMoveAndBackup(@Param("params") Map<String, Object> params);
	
	int removeMoveAndBackup(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectPlaceCombo();
	
	List<Map<String, Object>> selectCompanyCombo();
	
	List<Map<String, Object>> selectComputerCombo();
	
	List<Map<String, Object>> selectLoggerCombo(@Param("placeId") String placeId);
	
	List<Map<String, Object>> selectSensorCombo(@Param("placeId") String placeId);
	
	List<Map<String, Object>> selectSensorLoggerCombo(@Param("placeId") String placeId);
	
	int upsertDmsPlace(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectPlaceId(@Param("code") String code);
	
	int findPlace(@Param("params") Map<String, Object> params);
	
	int upsertDmsUser(@Param("params") Map<String, Object> params, @Param("placeId") int placeId);
	
	int deleteDmsPlace(@Param("params") Map<String, Object> params);
	
	int deleteDmsUser(@Param("id") String id);
	
	int upsertDmsCompany(@Param("params") Map<String, Object> params);
	
	int deleteDmsCompany(@Param("params") Map<String, Object> params);
	
	int findCompany(@Param("params") Map<String, Object> params);
	
	int findComputer(@Param("params") Map<String, Object> params);
	
	int upsertDmsComputer(@Param("params") Map<String, Object> params);
	
	int deleteDmsComputer(@Param("params") Map<String, Object> params);
	
	int upsertDmsLogger(@Param("params") Map<String, Object> params);
	
	int deleteDmsLogger(@Param("params") Map<String, Object> params);
	
	int upsertDmsSensor(@Param("params") Map<String, Object> params);
	
	int deleteDmsSensor(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectSensorTypeCombo();
	
	int upsertDmsSensorType(@Param("params") Map<String, Object> params);
	
	int deleteDmsSensorType(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectSensorTypeSettingCombo(@Param("placeId") String placeId);
	
	int upsertDmsSensorTypeSetting(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectCalculationCombo();
	
	int findCalculation(@Param("params") Map<String, Object> params);
	
	int upsertDmsCalculation(@Param("params") Map<String, Object> params);
	
	int deleteDmsCalculation(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectApplyCalculation(@Param("sensorId") String sensorId);
	
	List<InitialBody> selectSensorInitial(@Param("sensorId") String sensorId);
	
	int insertDmsApplyCalculation(@Param("params") Map<String, Object> params);
	
	int insertDmsCalculationParam(@Param("params") Map<String, Object> params);
	
	int insertDmsSensorInitial(@Param("params") Map<String, Object> params);
	
	void createDataTable(@Param("code") String code);
	
	void updatePlaceCreated(@Param("code") String code);
	
	Map<String, Object> selectSensorTypeSetting(@Param("params") Map<String, Object> params);
	
	int insertSensorTypeSetting(@Param("params") Map<String, Object> params, @Param("nextOrder") int nextOrder);
	
	int findLogger(@Param("params") Map<String, Object> params);
	
	int findSensor(@Param("params") Map<String, Object> params);
	
	int findSensorType(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectEventPlaceCombo();
	
	List<Map<String, Object>> selectEventLoggerCombo(@Param("placeId") String placeId);
	
	List<Map<String, Object>> selectEventSensorCombo(@Param("placeId") String placeId);
	
	int upsertDataDelete(@Param("params") Map<String, Object> params);
	
	int removeDataDelete(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectDataDelete();
	
	Map<String, Object> selectSingleLogger(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectSingleSensor(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectSingleSensorTypeSetting(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectSingleMoveAndBackup(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectSingleDataDelete(@Param("params") Map<String, Object> params);
	
	int selectInitialCalculationCount(@Param("params") Map<String, Object> params);
	
	int updateSensorInitialCalculation(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectApplySensorTypeCombo(@Param("placeId") String placeId);
	
	List<DataEditBoby> selectDataEdit(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectSingleDataEdit(@Param("params") Map<String, Object> params);
	
	int updateDataEdit(@Param("params") Map<String, Object> params);
	
	int removeDataEdit(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectLogger();
	
	List<Map<String, Object>> selectUploadLog();
	
	int selectExecutionCalculationCount(@Param("params") Map<String, Object> params);
	
	int updateSensorNewCalculation(@Param("params") Map<String, Object> params);
	
	int updateLoggerStatus(@Param("params") Map<String, Object> params);
	
}
