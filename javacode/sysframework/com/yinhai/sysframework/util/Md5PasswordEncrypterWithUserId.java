package com.yinhai.sysframework.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.dao.DataAccessException;

public class Md5PasswordEncrypterWithUserId implements Md5PasswordEncoder {

    private static final String SALT = "-a1b2";

    @Override
    public String encodePassword(String password, Object salt) throws DataAccessException {
        String loginId = "";
        if (password == null)
            password = "";
        if (salt instanceof String) {
            loginId = salt.toString();
        }
        return Base64Encoder.encode(DigestUtil.md5(loginId + password + SALT));
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) throws DataAccessException {
        return encPass.equals(encodePassword(rawPass, salt));
    }

    public static void main(String[] args) {
        Md5PasswordEncrypterWithUserId encrypter = new Md5PasswordEncrypterWithUserId();
        System.out.println(encrypter.encodePassword("1","developer"));
    }
}
