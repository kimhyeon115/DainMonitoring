package com.dain23.common.model;

import org.springframework.http.HttpStatus;

import com.dain23.util.Const;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class HeaderEntity {
	
	private final int code;
	private final int messageCd;
	private final String message;

	
	public static HeaderEntity of(HttpStatus status, int messageCd, String message) {
		return HeaderEntity.builder()
				.code(status.value())
				.messageCd(messageCd)
				.message(message)
				.build();
	}
	
	
	public static HeaderEntity fail() {
		return HeaderEntity.builder()
				.code(HttpStatus.OK.value())
				.messageCd(Const.FAIL_CODE)
				.message(Const.FAIL)
				.build();
	}
	
	
	public static HeaderEntity bad() {
		return HeaderEntity.builder()
				.code(HttpStatus.OK.value())
				.messageCd(Const.BAD_REQUEST_CODE )
				.message(Const.BAD_REQUEST)
				.build();
	}
	
}
