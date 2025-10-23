package com.dain23.common.model;

import lombok.Data;

/**
 *	세션 데이터
 */
@Data
public class Session {
	private String token;
	private String placeCode;
	private String expiryAt;
	private String level;
}
