package com.yinhai.sysframework.dao.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.transform.BasicTransformerAdapter;

public class TaMapResult extends BasicTransformerAdapter implements Serializable {

	public static final TaMapResult INSTANCE = new TaMapResult();

	public Object transformTuple(Object[] tuple, String[] aliases) {
		Map result = new HashMap(tuple.length);
		String[] t = new String[aliases.length];
		for (int i = 0; i < aliases.length; i++) {
			t[i] = aliases[i].toLowerCase();
		}
		aliases = t;
		for (int i = 0; i < tuple.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				result.put(alias, tuple[i]);
			}
		}
		return result;
	}

	private Object readResolve() {
		return INSTANCE;
	}
}
