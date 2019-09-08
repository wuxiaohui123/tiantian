package com.yinhai.webframework.security.rsa;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.security.PrivateKey;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RSAUtils {

	private static PrivateKey pk;

	static {
		try {
			pk = RSATools.getKeyPair().getPrivate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] decrypt(byte[] raw) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());

		if (pk == null)
			pk = RSATools.getKeyPair().getPrivate();
		cipher.init(2, pk);
		int blockSize = cipher.getBlockSize();
		ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
		int j = 0;

		while (raw.length - j * blockSize > 0) {
			bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
			j++;
		}
		return bout.toByteArray();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if ((hexString == null) || (hexString.equals(""))) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = ((byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[(pos + 1)])));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String descryptWidthEncode(String p) throws Exception {
		byte[] en_result = null;
		try {
			en_result = hexStringToBytes(p);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return p;
		}
		StringBuffer sb = new StringBuffer(new String(decrypt(en_result)));
		return URLDecoder.decode(sb.reverse().toString(), "UTF-8");
	}
}
