package com.yinhai.sysframework.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;

public class DESCoderUtil {

	public static final String ALGORITHM = "DES";
	public static final String KEY = "reYj6fIsWGE=";

	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		return keyFactory.generateSecret(dks);
	}

	public static byte[] decrypt(byte[] data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(2, k);
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(byte[] data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(1, k);
		return cipher.doFinal(data);
	}

	public static String initKey() throws Exception {
		return initKey(null);
	}

	public static String initKey(String seed) throws Exception {
		SecureRandom secureRandom = (seed == null) ? new SecureRandom() : new SecureRandom(decryptBASE64(seed));
		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
		kg.init(secureRandom);
		return encryptBASE64(kg.generateKey().getEncoded());
	}

	public static byte[] decryptBASE64(String key) {
		return Base64Decoder.decode(key);
	}

	public static String encryptBASE64(byte[] key) {
		return Base64Encoder.encode(key);
	}

	public static String encryptBASE64NO(byte[] data) throws Exception {
		return Base64Encoder.encode(data).replace("\r\n", "");
	}
}
