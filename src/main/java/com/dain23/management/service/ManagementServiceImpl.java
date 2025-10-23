package com.dain23.management.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dain23.common.method.GroupingData;
import com.dain23.management.mapper.ManagementMapper;
import com.dain23.management.mapper.SystemMapper;
import com.dain23.management.model.LoggerStatus;
import com.dain23.management.model.Management;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {
	
	/* 매퍼 */
	@Autowired
	private ManagementMapper managementMapper;
	
	@Autowired
	private SystemMapper systemMapper;
		
	
	/* 매니지먼트 응답 데이터 반환 */
	public Map<String, Object> makeManagement(String placeCode) {
		
		Map<String, Object> result = new LinkedHashMap<>();
		
		try {
			
			List<Management> management = managementMapper.selectManagementInUse();
			
			List<List<Management>> groupManagement = GroupingData.groupByComShortName(management);
			for (List<Management> managements : groupManagement) {
				for (Management manage : managements) {
					String[] loggerName = managementMapper.selectPlaceLogger(manage.getPlaceId());
					List<LoggerStatus> loggerStatus = new ArrayList<>();

					for (String lg : loggerName) {
						LoggerStatus loggerData = managementMapper.selectLoggerStatus(lg);
						loggerStatus.add(loggerData);
					}
					manage.setLoggerStatus(loggerStatus);
				}
			}
			
			List<Map<String, Object>> computer = systemMapper.selectComputerCombo();
			
			result.put("pageName", placeCode);
			result.put("groupManagement", groupManagement);
			result.put("computer", computer);
			
		} catch (Exception e) {
			log.error("서버 오류 발생");
		}
		
		return result;
	}
	
	
	
	/* 매니지먼트 화면 데이터 엑셀 다운로드 */
	public void makeExcelSheet(HttpServletResponse response) {
		
		try {
			
			/* 다운로드 데이터 변수 */
			List<Map<String, Object>> excelData = managementMapper.selectLoggerDetail();
			if (excelData == null || excelData.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				return;
			}
			
			String encodedFileName = URLEncoder.encode("현장로거_리스트", "UTF-8");
			List<String> header = List.of("현장명", "구분명", "로거명", "CDMA NO", "최초측정일", "수집 PC", "운영상태");
			
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("현장정보");
			
			CellStyle headerStyle = workbook.createCellStyle();
	        Font font = workbook.createFont();
	        font.setBold(true);
	        headerStyle.setFont(font);
	        
	        Row headerRow = sheet.createRow(0);
	        for (int i = 0; i < header.size(); i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(header.get(i));
	            cell.setCellStyle(headerStyle);
	        }
			
	        for (int i = 0; i < excelData.size(); i++) {
	            Row row = sheet.createRow(i + 1);
	            Map<String, Object> rowData = excelData.get(i);

	            for (int j = 0; j < header.size(); j++) {
	                Cell cell = row.createCell(j);
                    String key = header.get(j);
                    Object value = rowData.get(key);
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value != null ? value.toString() : "");
                    }
	            }
	        }
	        
	        int[] columnWidths = {
        	    20 * 256,
        	    40 * 256,
        	    20 * 256,
        	    20 * 256,
        	    15 * 256,
        	    15 * 256,
        	    15 * 256
        	};
	        
	        for (int i = 0; i < header.size(); i++) {
	        	sheet.setColumnWidth(i, columnWidths[i]);
	        }
	        
	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
	        
	        try (ServletOutputStream outputStream = response.getOutputStream()) {
	            workbook.write(outputStream);
	            outputStream.flush();
	        } finally {
	            workbook.close();
	        }
			
		} catch (Exception e) {
			log.error("Excel 생성 중 오류 발생", e);
			try {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Excel 생성 실패");
	        } catch (IOException ioException) {
	            log.error("응답 오류 전송 실패", ioException);
	        }
		}
	}

}
