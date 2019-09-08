package com.yinhai.ta3.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

public class Test {

	public static void main(String[] paramArrayOfString) {
		NamedCache localNamedCache = CacheFactory.getCache("test");
	}
}
