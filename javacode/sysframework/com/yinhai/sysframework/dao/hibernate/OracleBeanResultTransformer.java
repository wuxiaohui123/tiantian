package com.yinhai.sysframework.dao.hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.ResultTransformer;

public class OracleBeanResultTransformer implements ResultTransformer, Serializable {

	private static final long serialVersionUID = 1L;
	private final Class resultClass;
	private boolean isInitialized;
	private String[] aliases;
	private Setter[] setters;
	private boolean aliasesToLowerCase = false;

	public OracleBeanResultTransformer(Class resultClass) {
		if (resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		isInitialized = false;
		this.resultClass = resultClass;
	}

	public OracleBeanResultTransformer(Class resultClass, boolean aliasesToLowerCase) {
		if (resultClass == null) {
			throw new IllegalArgumentException("resultClass cannot be null");
		}
		isInitialized = false;
		this.resultClass = resultClass;
		this.aliasesToLowerCase = aliasesToLowerCase;
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		if (aliasesToLowerCase) {
			String[] t = new String[aliases.length];
			for (int i = 0; i < aliases.length; i++) {
				t[i] = aliases[i].toLowerCase();
			}
			aliases = t;
		}
		Object result;
		try {
			if (!isInitialized) {
				initialize(aliases);
			} else {
				check(aliases);
			}

			result = resultClass.newInstance();

			for (int i = 0; i < aliases.length; i++) {
				if (setters[i] != null) {
					setters[i].set(result, dealOracleType(setters[i], tuple[i]), null);
				}
			}
		} catch (InstantiationException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		} catch (IllegalAccessException e) {
			throw new HibernateException("Could not instantiate resultclass: " + resultClass.getName());
		}

		return result;
	}

	private Object dealOracleType(Setter setter, Object result) {
		Class c = setter.getMethod().getParameterTypes()[0];
		Object r = result;
		if ((result instanceof BigDecimal)) {
			if (c.getName().equals("java.lang.Long")) {
				r = Long.valueOf(((BigDecimal) result).longValue());
			} else if (c.getName().equals("java.lang.Integer")) {
				r = Integer.valueOf(((BigDecimal) result).intValue());
			} else if (c.getName().equals("java.lang.Double")) {
				r = Double.valueOf(((BigDecimal) result).doubleValue());
			}
		}
		if (((result instanceof Character)) && (c.getName().equals("java.lang.String"))) {
			r = String.valueOf((Character) result);
		}

		return r;
	}

	private void initialize(String[] aliases) {
		PropertyAccessStrategy accessor = PropertyAccessStrategyFieldImpl.INSTANCE;
		this.aliases = new String[aliases.length];
		setters = new Setter[aliases.length];
		for (int i = 0; i < aliases.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				this.aliases[i] = alias;
				PropertyAccess access = accessor.buildPropertyAccess(resultClass, alias);
				setters[i] = access.getSetter();
			}
		}
		isInitialized = true;
	}

	private void check(String[] aliases) {
		if (!Arrays.equals(aliases, this.aliases)) {
			throw new IllegalStateException("aliases are different from what is cached; aliases="
					+ Arrays.asList(aliases) + " cached=" + Arrays.asList(this.aliases));
		}
	}

	public List transformList(List collection) {
		return collection;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}

		OracleBeanResultTransformer that = (OracleBeanResultTransformer) o;

		if (!resultClass.equals(resultClass)) {
			return false;
		}
		if (!Arrays.equals(aliases, aliases)) {
			return false;
		}

		return true;
	}

	public int hashCode() {
		int result = resultClass.hashCode();
		result = 31 * result + (aliases != null ? Arrays.hashCode(aliases) : 0);
		return result;
	}
}
