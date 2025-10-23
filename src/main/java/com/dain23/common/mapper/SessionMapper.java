package com.dain23.common.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dain23.common.model.Session;
import com.dain23.common.model.User;

@Mapper
public interface SessionMapper {

	boolean checkPageOpen(@Param("placeCode") String placeCode);
	
	Session findSession(@Param("clientToken") String clientToken);
	
	User findUser(
		@Param("placeCode") String placeCode,
		@Param("user") String user,
		@Param("password") String password
	);
	
	int insertSession(
		@Param("token") String token,
		@Param("placeId") int placeId,
		@Param("userId") int userId,
		@Param("createdAt") LocalDateTime createdAt,
		@Param("expiryAt") LocalDateTime expiryAt
	);
	
	int updateSession(
		@Param("expiryAt") LocalDateTime expiryAt,
		@Param("token") String token
	);
	
	int deleteSession(@Param("clientToken") String clientToken);
	
	int deleteExpiredSession(@Param("minutes") int minutes);
	
}
