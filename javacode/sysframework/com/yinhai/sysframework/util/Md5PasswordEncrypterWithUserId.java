package com.yinhai.sysframework.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.dao.DataAccessException;

public class Md5PasswordEncrypterWithUserId implements Md5PasswordEncoder {

    private static final String SALT = "-a1b2";

    public String encodePassword(String password, Object salt) throws DataAccessException {
        String loginId = "";
        if (password == null)
            password = "";
        if (salt instanceof String) {
            loginId = salt.toString();
        }
        return Base64Encoder.encode(DigestUtil.md5(loginId + password + SALT));
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
        return encPass.equals(encodePassword(rawPass, salt));
    }

}
