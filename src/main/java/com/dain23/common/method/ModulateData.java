package com.dain23.common.method;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dain23.common.model.SensorTypeSet;

/**
 *	차트 데이터 정규화
 **/
public class ModulateData {
	
	/* 평균 여부, 싸이클측정 여부 기준 각각 정규화 */
	public static List<List<Object>> modulateMapToList(
		List<Map<String, Object>> dataList, String sensorCode, boolean cycleCheck, boolean average
	) {
		
		List<List<Object>> chartData = new ArrayList<>();

		if (cycleCheck) {
		    chartData.add(Arrays.asList(
		        "측정일시", sensorCode, 
		        "1차관리기준", "1차관리기준", 
		        "2차관리기준", "2차관리기준", 
		        "3차관리기준", "3차관리기준"
		    ));
		} else {
		    chartData.add(Arrays.asList(
		        "측정일시", sensorCode, 
		        "1차관리기준", "2차관리기준", "3차관리기준"
		    ));
		}

		if (dataList.isEmpty()) return chartData;
		
		String dateKey = average ? "measured_date" : "measured_at";
		String valueKey;
		if (average) {
		    valueKey = cycleCheck ? "avg_changed_val" : "avg_displace_val";
		    if (dataList.get(0).get("sensor_type_id").toString().equals("15")) valueKey = "avg_displace_val";
		} else {
		    valueKey = cycleCheck ? "changed_val" : "displace_val";
		    if (dataList.get(0).get("sensor_type_id").toString().equals("15")) valueKey = "displace_val";
		}

		for (Map<String, Object> row : dataList) {
		    String time = row.get(dateKey).toString();
		    if (average) {
		        time += " 12:00:00";
		    }

		    Double value = toDoubleOrNull(row, valueKey);
		    Double c1 = toDoubleOrNull(row, "criteria_val1");
		    Double c2 = toDoubleOrNull(row, "criteria_val2");
		    Double c3 = toDoubleOrNull(row, "criteria_val3");

		    if (cycleCheck) {
		        chartData.add(Arrays.asList(
		            time, value,
		            c1, c1 == null ? null : -c1,
		            c2, c2 == null ? null : -c2,
		            c3, c3 == null ? null : -c3
		        ));
		    } else {
		        chartData.add(Arrays.asList(
		            time, value, c1, c2, c3
		        ));
		    }
		}
		
		return chartData;
	}
	
	
	
	/* null 값 처리 */
	private static Double toDoubleOrNull(Map<String, Object> map, String key) {
	    Object value = map.get(key);
	    if (value == null || value.equals("")) return null;
	    return ((Number) value).doubleValue();
	}
	
	
	
	/* 지중경사계 시스템 전용 테이블 데이터 가공 */
    public static List<Map<String, Object>> accumulateRowValues(List<Map<String, Object>> data) {
    	List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> row : data) {
            Map<String, Object> newRow = new LinkedHashMap<>();
            
            String timeKey = null;
            if (row.containsKey("measured_at")) {
                timeKey = "measured_at";
            } else if (row.containsKey("measurement_date")) {
                timeKey = "measurement_date";
            }
            if (timeKey != null) {
                newRow.put(timeKey, row.get(timeKey));
            }

            List<String> sensorKeys = new ArrayList<>();
            for (String key : row.keySet()) {
                if (!key.equals(timeKey)) {
                    sensorKeys.add(key);
                }
            }
            Collections.sort(sensorKeys);

            BigDecimal running = BigDecimal.ZERO;
            for (String key : sensorKeys) {
                BigDecimal cur = (BigDecimal) row.get(key);
                running = running.add(cur);
                newRow.put(key, running);
            }

            result.add(newRow);
        }
        return result;
    }
    
    
    
    /* 지중경사계 누적그래프 전용 차트 데이터 가공 */
    public static List<List<Object>> cumulativeGraphData(
	    List<Map<String, Object>> data, List<SensorTypeSet> sensors
	) {
	    if (data == null || data.isEmpty()) return Collections.emptyList();
	    
	    List<String> dates = data.stream()
	            .map(row -> {
	                Object date = row.get("measured_at");
	                if (date == null) {
	                    date = row.get("measurement_date");
	                }
	                return date;
	            })
	            .filter(Objects::nonNull)
	            .map(Object::toString)
	            .collect(Collectors.toList());

	    List<List<Object>> chartData = new ArrayList<>();

	    List<Object> dateRow = new ArrayList<>();
	    dateRow.add("심도");
	    dateRow.addAll(dates);
	    chartData.add(dateRow);

	    List<String> sensorKeys = sensors.stream()
	            .map(s -> s.getSensorCode())
	            .sorted()
	            .collect(Collectors.toList());

	    for (int i = 0; i < sensorKeys.size(); i++) {
	        String key = sensorKeys.get(i);
	        SensorTypeSet sensor = sensors.get(i);

	        List<Object> values = new ArrayList<>();

	        int location = 0;
	        if (sensor.getLocation() != null) {
	            try {
	                location = Integer.parseInt(sensor.getLocation().toString());
	            } catch (NumberFormatException e) {
	                location = 0;
	            }
	        }
	        values.add(location);

	        for (Map<String, Object> row : data) {
	            Object val = row.get(key);
	            if (val instanceof BigDecimal) {
	                values.add((BigDecimal) val);
	            } else if (val != null) {
	                try {
	                    values.add(new BigDecimal(val.toString()));
	                } catch (NumberFormatException e) {
	                    values.add(BigDecimal.ZERO);
	                }
	            } else {
	                values.add(BigDecimal.ZERO);
	            }
	        }

	        chartData.add(values);
	    }

	    return chartData;
	}
    
    
    
    /* 지중경사계 누적그래프 전용 테이블 데이터 가공 */
    public static List<List<String>> cumulativeTableData(
		List<Map<String, Object>> data, List<SensorTypeSet> sensors
	) {
    	if (data == null || data.isEmpty()) return Collections.emptyList();
    	
    	List<List<String>> tableData = new ArrayList<>();
    	
    	List<String> dates = data.stream()
	            .map(row -> {
	                Object date = row.get("measured_at");
	                if (date == null) {
	                    date = row.get("measurement_date");
	                }
	                return date;
	            })
	            .filter(Objects::nonNull)
	            .map(Object::toString)
	            .collect(Collectors.toList());
    	
    	List<String> dateRow = new ArrayList<>();
	    dateRow.add("심도(M)");
	    dateRow.addAll(dates);
	    tableData.add(dateRow);
    	
	    for (SensorTypeSet sen : sensors) {
	    	List<String> valRow = new ArrayList<>();
	    	valRow.add(sen.getLocation());
		    for (Map<String, Object> row : data) {
		    		valRow.add((String) row.get(sen.getSensorCode()).toString());
		    }
		    tableData.add(valRow);
	    }
	    
    	return tableData;
    }

}
