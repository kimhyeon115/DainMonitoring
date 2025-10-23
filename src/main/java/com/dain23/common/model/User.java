package com.dain23.common.model;

import lombok.Data;

/**
 *	사용자 데이터
 */
@Data
public class User {
	private int userId;
	private int placeId;
	private String placeCode;
	private String user;
	private String level;
}
