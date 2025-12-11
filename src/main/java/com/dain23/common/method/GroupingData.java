package com.dain23.common.method;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.dain23.management.model.Management;
import com.dain23.common.model.LatestData;

/**
 *	데이터 그룹화 클래스
 **/
public class GroupingData {
	
	/* 로거ID 키: 로거ID 및 센서 타입 기준 그룹 배열화 */
	public static <T extends LatestData> Map<Integer, List<List<T>>> groupLoggerIdAndGroupSensorTypeWithoutKey(List<T> dataList) {

	    Map<Integer, List<T>> loggerGroup = dataList.stream()
	            .collect(Collectors.groupingBy(LatestData::getLoggerId));

	    Map<Integer, List<List<T>>> result = new HashMap<>();

	    for (Map.Entry<Integer, List<T>> entry : loggerGroup.entrySet()) {
	        Integer loggerId = entry.getKey();
	        List<T> list = entry.getValue();

	        Map<Integer, List<T>> sensorTypeGroup = list.stream()
	                .collect(Collectors.groupingBy(LatestData::getGroupOrder));

	        List<List<T>> groupedBySensorType = new ArrayList<>(sensorTypeGroup.values());

	        groupedBySensorType.forEach(sensorList -> {
	            sensorList.sort(Comparator.comparing(LatestData::getSensorCode));

	            BigDecimal sum = BigDecimal.ZERO;
	            for (T row : sensorList) {
	                if (row.getSensorTypeId() == 19) {
	                	try {
	                		BigDecimal val = new BigDecimal(row.getChangedVal());
	                		sum = sum.add(val);
	                		row.setCumulativeVal(sum);
	                    } catch (NumberFormatException e) {}
	                	row.setCumulativeVal(sum);
	                }
	            }
	        });

	        result.put(loggerId, groupedBySensorType);
	    }

	    return result;
	}
	
	
	
	/* 로거ID_센서타입ID 키: 로거ID 및 센서타입ID 기준 그룹 배열화 */
	public static Map<String, Object> groupEveryChartData(List<Map<String, Object>> dataList) {

		String displace = "displace_val";
		String changed = "changed_val";
		String column = changed;

	    Map<String, List<Map<String, Object>>> grouped = dataList.stream()
	        .collect(Collectors.groupingBy(row ->
	            row.get("logger_id") + "_" + row.get("sensor_type_id")));

	    Map<String, Object> chartMap = new LinkedHashMap<>();

	    for (String key : grouped.keySet()) {
	        List<Map<String, Object>> groupData = grouped.get(key);

	        List<String> sensorCodes = groupData.stream()
	            .map(r -> (String) r.get("sensor_code"))
	            .filter(Objects::nonNull)
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());

	        List<String> dateTimes = groupData.stream()
	            .map(r -> formatDateTime(safeDate(r.get("measured_at"))))
	            .filter(Objects::nonNull)
	            .distinct()
	            .sorted()
	            .collect(Collectors.toList());

	        Map<String, Map<String, Double>> dataMatrix = new HashMap<>();
	        for (Map<String, Object> row : groupData) {
	            String dt = formatDateTime(safeDate(row.get("measured_at")));
	            String code = (String) row.get("sensor_code");

	            String sensorTypeId = row.get("sensor_type_id").toString();
	            if (sensorTypeId.equals("15") || sensorTypeId.equals("26")) column = displace;
	            else column = changed;
	            
	            Object valObj = row.get(column);
	            Double val = null;

	            if (valObj != null) {
	                if (valObj instanceof Number) {
	                    val = ((Number) valObj).doubleValue();
	                } else {
	                    try {
	                        val = Double.parseDouble(valObj.toString());
	                    } catch (NumberFormatException e) {
	                        val = null;
	                    }
	                }
	            }

	            dataMatrix
	                .computeIfAbsent(dt, k -> new HashMap<>())
	                .put(code, val);
	        }

	        List<List<Object>> chartData = new ArrayList<>();

	        List<Object> header = new ArrayList<>();
	        header.add("측정일시");
	        header.addAll(sensorCodes);
	        chartData.add(header);

	        for (String dt : dateTimes) {
	            List<Object> row = new ArrayList<>();
	            row.add(dt);
	            for (String code : sensorCodes) {
	                Map<String, Double> sensorValues = dataMatrix.getOrDefault(dt, Collections.emptyMap());
	                row.add(sensorValues.getOrDefault(code, null));
	            }
	            chartData.add(row);
	        }

	        chartMap.put(key, chartData);
	    }
	    
	    return chartMap;
	}

	
	
	/* 일시 타입 검증 */
	private static Date safeDate(Object obj) {
	    if (obj instanceof Timestamp) {
	        return new Date(((Timestamp) obj).getTime());
	    } else if (obj instanceof Date) {
	        return (Date) obj;
	    } else {
	        throw new IllegalArgumentException("measured_at 필드가 Date나 Timestamp 타입이 아닙니다: " + obj);
	    }
	}
	 
	
	 
	 /* 현재 일시 생성 */
	 public static String formatDateTime(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

	
	 
	/* 로거ID 기준 그룹 배열화 */ 
	public static List<List<Map<String, Object>>> groupSystemNavigation(List<Map<String, Object>> navigation) { 
        Map<Integer, List<Map<String, Object>>> grouped = navigation.stream() 
            .collect(Collectors.groupingBy(m -> (Integer) m.get("logger_id"))); 
        
        List<List<Map<String, Object>>> sortedGroups = grouped.values().stream() 
            .map(group -> group.stream() 
                .sorted(Comparator.comparing((Map<String, Object> m) -> {
                    String code = (String) m.get("sensor_code");
                    if (code == null || code.indexOf('_') == -1) {
                        return "ZZZ";
                    }
                    return code.substring(0, code.indexOf('_')); 
                })
                .thenComparingInt(m -> {
                    String code = (String) m.get("sensor_code");
                    try {
                        int start = code.indexOf('_') + 1;
                        int end = code.lastIndexOf('_');
                        String numberPart = (end != -1 && end > start) ? code.substring(start, end) : code.substring(start);
                        
                        return Integer.parseInt(numberPart);
                    } catch (Exception e) {
                        return Integer.MAX_VALUE; 
                    }
                })
                .thenComparing(m -> (String) m.get("sensor_code")))
            .collect(Collectors.toList())) 
            .collect(Collectors.toList());

        List<List<Map<String, Object>>> result = sortedGroups.stream()
            .sorted(Comparator.comparing(group -> {
                return (String) group.get(0).get("logger_code");
            }))
            .collect(Collectors.toList());

        return result;
	}
	
	
	 
	/* 로거ID 기준 그룹 배열화 */
	public static List<List<Map<String, Object>>> groupBasicNavigation(List<Map<String, Object>> navigation) {
		Map<Integer, List<Map<String, Object>>> grouped = navigation.stream()
			.collect(Collectors.groupingBy(m -> (Integer) m.get("sensor_type_id")));

		List<List<Map<String, Object>>> result = grouped.entrySet().stream()
				.sorted(Comparator.comparingInt(entry -> {
	            Map<String, Object> firstRow = entry.getValue().get(0);
	            return (Integer) firstRow.get("group_order");
	    	}))
	        .map(entry -> entry.getValue().stream()
	            .sorted(Comparator.comparing(m -> (String) m.get("sensor_code")))
	            .collect(Collectors.toList()))
	        .collect(Collectors.toList());

		return result;
	}
	
	
	
	/* 가장 첫번째 레코드의 측정일시의 시분초가 동일한 레코드들만 추출 및 내림차순 정렬 */
	public static List<Map<String, Object>> processAndFilter(List<Map<String, Object>> data) {
		
	    if (data == null || data.isEmpty()) {
	        return Collections.emptyList();
	    }

	    boolean hasMeasurementDate = data.stream().anyMatch(row -> row.containsKey("measurement_date"));

	    if (hasMeasurementDate) {
	        return data;
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    String baseStr = (String) data.get(0).get("measured_at");
	    
	    if (baseStr == null) {
	        return Collections.emptyList();
	    }

	    LocalDateTime baseLdt = LocalDateTime.parse(baseStr, formatter);
	    int baseHour = baseLdt.getHour();
	    int baseMinute = baseLdt.getMinute();
	    int baseSecond = baseLdt.getSecond();

	    return data.stream()
	        .filter(row -> {
	            String tsStr = (String) row.get("measured_at");
	            if (tsStr == null) return false;

	            LocalDateTime ldt = LocalDateTime.parse(tsStr, formatter);
	            return ldt.getHour() == baseHour &&
	                   ldt.getMinute() == baseMinute &&
	                   ldt.getSecond() == baseSecond;
	        })
	        .sorted((a, b) -> {
	            LocalDateTime dtA = LocalDateTime.parse((String) a.get("measured_at"), formatter);
	            LocalDateTime dtB = LocalDateTime.parse((String) b.get("measured_at"), formatter);
	            return dtB.compareTo(dtA);
	        })
	        .collect(Collectors.toList());
	}
	
	
	
	/* 업체 약식명칭 기준 그룹 배열화 */
	public static List<List<Management>> groupByComShortName(List<Management> management) {
	    Map<Object, List<Management>> comNameMap = management.stream()
	        .collect(Collectors.groupingBy(ms -> ms.getComShortName(), TreeMap::new, Collectors.toList()));

	    for (List<Management> list : comNameMap.values()) {
	        list.sort(Comparator.comparing(Management::getComFullName));
	    }

	    return new ArrayList<>(comNameMap.values());
	}
	 
}
