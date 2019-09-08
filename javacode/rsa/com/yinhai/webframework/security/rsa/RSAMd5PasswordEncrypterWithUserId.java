package com.yinhai.webframework.security.rsa;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataAccessException;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.util.Md5PasswordEncoder;

public class RSAMd5PasswordEncrypterWithUserId implements Md5PasswordEncoder {

	private static final String SALT = "-a1b2";

	public String encodePassword(String password, Object salt) throws DataAccessException {
		String loginid = "";
		if (password == null)
			password = "";
		if ("true".equals(SysConfig.getSysConfig("passwordRSA", "false"))) {
			try {
				password = RSAUtils.descryptWidthEncode(password);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if ((null != salt) && ((salt instanceof String))) {
			loginid = salt.toString();
		}
		String passwordSaltStr = loginid + password + SALT;

		byte[] encryptedPassword = Base64.encodeBase64(DigestUtils.md5(passwordSaltStr));

		return new String(encryptedPassword);
	}

	public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
		return encPass.equals(encodePassword(rawPass, salt));
	}
}
