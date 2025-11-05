package com.dain23.common.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dain23.common.model.EventLatestData;
import com.dain23.common.model.CycleLatestData;
import com.dain23.common.model.Logger;
import com.dain23.common.model.Place;
import com.dain23.common.model.SensorTypeSet;

@Mapper
public interface CommonMapper {

	Place findPlace(@Param("placeCode") String placeCode);
	
	List<SensorTypeSet> findGraphSet(@Param("placeId") int placeId);
	
	List<Logger> findLogger(
		@Param("placeCode") String placeCode, @Param("placeId") int placeId
	);
	
	List<CycleLatestData> findLatestCycleData(
		@Param("placeCode") String placeCode, @Param("placeId") int placeId
	);
	
	List<EventLatestData> findLatestEventData(
		@Param("placeCode") String placeCode, @Param("placeId") int placeId
	);
	
	List<Map<String, Object>> findCycleChartData(
		@Param("placeCode") String placeCode, @Param("loggerId") int loggerId, 
		@Param("searchAt") String searchAt
	);
	
	List<Map<String, Object>> findEventChartData(
		@Param("placeCode") String placeCode, @Param("placeId") int placeId
	);
	
	List<Map<String, Object>> findNavigation(@Param("placeCode") String placeCode);
	
	List<SensorTypeSet> findSensorInLogger(
		@Param("loggerCode") String loggerCode, @Param("sensorTypeId") int sensorTypeId
	);
	
	SensorTypeSet findSensorSet(@Param("sensorId") String sensorId);
	
	List<Map<String, Object>> findSensorInLoggerData(
		@Param("placeCode") String placeCode, @Param("sensors") List<SensorTypeSet> sensors,
		@Param("start") String start, @Param("end") String end, @Param("column") String column,
		@Param("limit") int limit, @Param("offset") int offset, @Param("average") boolean average, 
		@Param("oclock") boolean oclock, @Param("excel") boolean excel
	);
	
	List<Map<String, Object>> findSensorBasicChart(
		@Param("placeCode") String placeCode, @Param("sensorId") String sensorId, 
		@Param("start") String start, @Param("end") String end,	
		@Param("cycleCheck") boolean cycleCheck, @Param("oclock") boolean oclock
	);
	
	List<Map<String, Object>> findSensorBasicData(
		@Param("placeCode") String placeCode, @Param("sensorId") int sensorId, 
		@Param("start") String start, @Param("end") String end,
		@Param("limit") int limit, @Param("offset") int offset, 
		@Param("oclock") boolean oclock, @Param("excel") boolean excel
	);
	
	List<Map<String, Object>> findSensorMaxAvgChart(
		@Param("placeCode") String placeCode, @Param("sensorId") String sensorId, 
		@Param("start") String start, @Param("end") String end
	);
	
	List<Map<String, Object>> findSensorEventMaxAvgData(
		@Param("placeCode") String placeCode, @Param("sensorId") int sensorId, 
		@Param("start") String start, @Param("end") String end,	
		@Param("limit") int limit, @Param("offset") int offset,	
		@Param("excel") boolean excel
	);
	
	List<Map<String, Object>> findSensorCycleMaxAvgData(
		@Param("placeCode") String placeCode, @Param("sensorId") int sensorId, 
		@Param("start") String start, @Param("end") String end,	
		@Param("limit") int limit, @Param("offset") int offset,	
		@Param("excel") boolean excel
	);
	
	void deleteOldUploadLogs();
	
}
