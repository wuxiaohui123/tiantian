package com.yinhai.sysframework.util;

import org.springframework.dao.DataAccessException;

public interface Md5PasswordEncoder {

	 String encodePassword(String str, Object salt) throws DataAccessException;

	 boolean isPasswordValid(String str1, String str2, Object salt) throws DataAccessException;
}
