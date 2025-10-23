package com.dain23.common.model;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ResponseData {
	
	private HeaderEntity header;
	private Map<String, Object> body;
	
	@Builder
	public ResponseData(HeaderEntity header, Map<String, Object> body) {
		this.header = header;
		this.body = body;
	}
	
	public static ResponseData of(HeaderEntity header, Map<String, Object> body) {
        return ResponseData.builder()
                .header(header)
                .body(body)
                .build();
    }

    public static ResponseData of(HeaderEntity header) {
        return ResponseData.builder()
                .header(header)
                .build();
    }

}
