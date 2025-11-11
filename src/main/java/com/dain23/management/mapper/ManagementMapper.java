package com.dain23.management.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dain23.management.model.LoggerStatus;
import com.dain23.management.model.Management;

@Mapper
public interface ManagementMapper {
	
	List<Management> selectManagementInUse();
	
	String[] selectPlaceLogger(@Param("placeId") int placeId);
	
	LoggerStatus selectLoggerStatus(@Param("loggerCode") String loggerCode);
	
	int updateLoggerInfo(@Param("params") Map<String, Object> params);
	
	int updateOpenOrClose(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectLoggerDetail();
	
	List<Map<String, Object>> findSensorInLogger(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectTransitionData(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectAvgTransitionData(@Param("params") Map<String, Object> params);
	
	int countSensorDataRows(@Param("params") Map<String, Object> params);
	
	Map<String, Object> selectAnalysisData(@Param("params") Map<String, Object> params);
	
	List<Map<String, Object>> selectFailRecord(@Param("params") Map<String, Object> params);

}
