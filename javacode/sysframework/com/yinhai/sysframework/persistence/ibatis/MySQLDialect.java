package com.yinhai.sysframework.persistence.ibatis;

import org.apache.commons.lang3.StringUtils;

public class MySQLDialect implements Dialect {

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String oldSql, int skipResults, int maxResults) {
		String tmpSql = StringUtils.lowerCase(StringUtils.deleteWhitespace(oldSql));

		boolean sqlFlag = (StringUtils.contains(tmpSql, "orderby")) || (StringUtils.contains(tmpSql, "groupby"))
				|| (StringUtils.contains(tmpSql, "having")) || (StringUtils.contains(tmpSql, "union"))
				|| (StringUtils.contains(tmpSql, "distinct")) || (StringUtils.countMatches(tmpSql, "select") > 1);

		if (!sqlFlag) {
			return "(" + oldSql + ") LIMIT " + skipResults + "," + maxResults;
		}
		return "(" + oldSql + ") LIMIT " + skipResults + "," + maxResults;
	}

	public static void main(String[] args) {
	}

}
