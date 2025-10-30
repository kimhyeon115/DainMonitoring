package com.dain23.util;

/**
 *	프로젝트 상수 코드
 */
public class Const {
	
	/* 정상 처리 */
	public static final int SUCCESS_CODE = 200;
	public static final String SUCCESS = "정상 처리되었습니다.";
	public static final String UPSERT_SUCCESS = "저장이 완료되었습니다.";
	public static final String DELETE_SUCCESS = "삭제가 완료되었습니다.";
	
	
	/* 대상 없음 */
	public static final int NO_TARGET_CODE = 204;
	public static final String NO_TARGET = "처리할 대상이 존재하지 않습니다.";
	
	
	/* 요청 실패 */
	public static final int BAD_REQUEST_CODE = 400;
	public static final String BAD_REQUEST = "요청 파라미터가 올바르지 않습니다.";
	public static final String UPSERT_FAIL = "저장이 실패했습니다.";
	public static final String DELETE_FAIL = "삭제가 실패했습니다.";
	
	
	/* 인증 실패 */
	public static final int UNAUTHORIZED_CODE = 401;
	public static final String UNAUTHORIZED = "유효하지 않는 세션입니다.";
	
	
	/* 중복 충돌 */
	public static final int CONFLICT_CODE = 409;
	public static final String CONFLICT = "이미 존재하는 데이터입니다.";
	
	
	/* 서버 내부 처리 오류 */
	public static final int FAIL_CODE = 500;
	public static final String FAIL = "서버 처리 중 오류가 발생했습니다.";
	
	
	public static final String ACCOUNT_IS_NOT_EXIST = "사용자가 존재하지 않습니다.";
	public static final String PASSWORD_IS_NOT_MATCH = "비밀번호가 일치하지 않습니다.";
	public static final String NEW_PASSWORD_IS_NOT_MATCH = "새 비밀번호가 일치하지 않습니다.";
	
	
	public static final String AVAILABLE = "사용 가능한 값입니다.";
    public static final String UNAVAILABLE = "이미 사용 중인 값입니다.";
    
}
