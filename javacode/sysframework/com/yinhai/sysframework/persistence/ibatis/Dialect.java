package com.yinhai.sysframework.persistence.ibatis;

public interface Dialect {

	 boolean supportsLimit();

	 String getLimitString(String oldSql, int skipResults, int maxResults);
}
