package com.dain23.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dain23.common.mapper.CommonMapper;
import com.dain23.common.method.GroupingData;
import com.dain23.common.method.ModulateData;
import com.dain23.common.model.EventLatestData;
import com.dain23.common.model.CycleLatestData;
import com.dain23.common.model.Logger;
import com.dain23.common.model.ParamBody;
import com.dain23.common.model.Place;
import com.dain23.common.model.SensorTypeSet;

import lombok.extern.slf4j.Slf4j;

/**
 *	API 요청 서비스 클래스
 **/
@Service
@Slf4j
public class CommonAPIServiceImpl implements CommonAPIService {

	/* 매퍼 */
	@Autowired
	private CommonMapper commonMapper;
	
	
	/* 메인 화면 데이터 요청 서비스 */
	public Map<String, Object> getMainContent(String placeCode) {
		
		Map<String, Object> result = new HashMap<>();
		
		try {
			
			Place place = commonMapper.findPlace(placeCode);
			result.put("place", place);
			
			List<Logger> loggers = commonMapper.findLogger(placeCode, place.getId());
			result.put("loggers", loggers);

			List<CycleLatestData> latestCycleData = commonMapper.findLatestCycleData(placeCode, place.getId());
			result.put("latestCycleData", GroupingData.groupLoggerIdAndGroupSensorTypeWithoutKey(latestCycleData));
			
			List<EventLatestData> latestEventData = commonMapper.findLatestEventData(placeCode, place.getId());
			result.put("latestEventData", GroupingData.groupLoggerIdAndGroupSensorTypeWithoutKey(latestEventData));

			result.put("graphSet", commonMapper.findGraphSet(place.getId()));
			
			List<Map<String, Object>> cycleChartData = new ArrayList<>();
			for (Logger logger : loggers) {
				List<Map<String, Object>> data = commonMapper.findCycleChartData(
					placeCode, logger.getId(), logger.getSearchAt()
				);
				cycleChartData.addAll(data);
			}

			result.put("cycleChartData", GroupingData.groupEveryChartData(cycleChartData));
			
			result.put("eventChartData", GroupingData.groupEveryChartData(commonMapper.findEventChartData(placeCode, place.getId())));

		} catch (Exception e) {
			log.error("서버 오류 발생", e);
			return new HashMap<>();
		}
		
		return result;
	}
	
	
	/* 데이터 화면 데이터 요청 서비스 */
	public Map<String, Object> getDataContent(String placeCode, ParamBody body) {
		Map<String, Object> result = new HashMap<>();

		try {
			Place place = commonMapper.findPlace(placeCode);
			result.put("place", place);

			boolean isLoggerSelect = body.getSelect().startsWith(placeCode);

			if (isLoggerSelect) {
				String[] selectParts = body.getSelect().split(":");
				
				List<SensorTypeSet> sensors = new ArrayList<>();
				if (selectParts[1].equals("total")) {
					sensors = commonMapper.findSensorInLogger(selectParts[0], 0);
				} else {
					int sensorTypeId = Integer.parseInt(selectParts[2]);
					sensors = commonMapper.findSensorInLogger(selectParts[0], sensorTypeId);
				}
				result.put("sensorSet", sensors);

				boolean cycleCheck = (boolean) sensors.get(0).isCycleCheck();
				String valName = cycleCheck ? "changed_val" : "correction_val";
				
				List<Map<String, Object>> data = commonMapper.findSensorInLoggerData(
					placeCode, sensors, body.getStart(), body.getEnd(), valName, 
					body.getLimit(), body.getOffset(),body.isAverage(), body.isOclock(), body.isExcel()
				);

				if (!selectParts[1].equals("total")) data = ModulateData.accumulateRowValues(data);
				result.put("sensorBodyData", data);

				if (selectParts[1].equals("graph")) {
					result.put("chartData", ModulateData.cumulativeGraphData(GroupingData.processAndFilter(data), sensors));
					result.put("cumulativeTableData", ModulateData.cumulativeTableData(GroupingData.processAndFilter(data), sensors));
				}
				
			} else {
				SensorTypeSet sensor = commonMapper.findSensorSet(body.getSelect());
				result.put("sensorSet", sensor);

				if (body.isAverage()) {
					if (!body.isScroll()) {
						List<Map<String, Object>> chart = commonMapper.findSensorMaxAvgChart(
							placeCode, body.getSelect(), body.getStart(), body.getEnd()
						);
						result.put("chartData", ModulateData.modulateMapToList(
							chart, sensor.getSensorCode(), sensor.isCycleCheck(), body.isAverage())
						);
					}
					if (sensor.isCycleCheck()) {
						result.put("sensorBodyData", commonMapper.findSensorCycleMaxAvgData(
							placeCode, sensor.getSensorId(), body.getStart(), body.getEnd(), 
							body.getLimit(), body.getOffset(), body.isExcel())
						);
					} else {
						result.put("sensorBodyData", commonMapper.findSensorEventMaxAvgData(
							placeCode, sensor.getSensorId(), body.getStart(), body.getEnd(), 
							body.getLimit(), body.getOffset(), body.isExcel())
						);
					}
				} else {
					if (!body.isScroll()) {
						List<Map<String, Object>> chart = commonMapper.findSensorBasicChart(
							placeCode, body.getSelect(), body.getStart(), body.getEnd(), sensor.isCycleCheck(), body.isOclock()
						);
						result.put("chartData", ModulateData.modulateMapToList(
							chart, sensor.getSensorCode(), sensor.isCycleCheck(), body.isAverage())
						);
					}
					result.put("sensorBodyData", commonMapper.findSensorBasicData(
						placeCode, sensor.getSensorId(), body.getStart(), body.getEnd(), 
						body.getLimit(), body.getOffset(), body.isOclock(), body.isExcel())
					);
				}
			}
		} catch (Exception e) {
			log.error("서버 오류 발생", e);
			return new HashMap<>();
		}

		return result;
	}
	
}
