package com.yinhai.sysframework.dao.hibernate;

import java.util.HashSet;
import java.util.Set;

public class Updater<T> {

	private T bean;

	public Updater(T bean) {
		this.bean = bean;
	}

	public Updater(T bean, UpdateMode mode) {
		this.bean = bean;
		this.mode = mode;
	}

	public Updater<T> setUpdateMode(UpdateMode mode) {
		this.mode = mode;
		return this;
	}

	public Updater<T> include(String property) {
		includeProperties.add(property);
		return this;
	}

	public Updater<T> exclude(String property) {
		excludeProperties.add(property);
		return this;
	}

	public boolean isUpdate(String name, Object value) {
		if (mode == UpdateMode.MAX)
			return !excludeProperties.contains(name);
		if (mode == UpdateMode.MIN)
			return includeProperties.contains(name);
		if (mode == UpdateMode.MIDDLE) {
			if (value != null) {
				return !excludeProperties.contains(name);
			}
			return includeProperties.contains(name);
		}

		return true;
	}

	private Set<String> includeProperties = new HashSet();

	private Set<String> excludeProperties = new HashSet();

	private UpdateMode mode = UpdateMode.MIDDLE;

	public static enum UpdateMode {
		MAX, MIN, MIDDLE;

		private UpdateMode() {
		}
	}

	public T getBean() {
		return (T) bean;
	}

	public Set<String> getExcludeProperties() {
		return excludeProperties;
	}

	public Set<String> getIncludeProperties() {
		return includeProperties;
	}
}
