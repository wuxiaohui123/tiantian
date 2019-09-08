package com.yinhai.sysframework.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.dao.DataAccessException;

public class Md5PasswordEncrypter implements Md5PasswordEncoder {

	private static final String SALT = "-a1b2";

	public String encodePassword(String password, Object salt) throws DataAccessException {
		if (password == null) {
			password = "";
		}
		return Base64Encoder.encode(DigestUtil.md5(password + SALT));
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		return encPass.equals(encodePassword(rawPass, salt));
	}

}
