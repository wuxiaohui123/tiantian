package com.yinhai.sysframework.persistence.ibatis;

import org.apache.commons.lang3.StringUtils;

public class OracleDialect implements Dialect {

    public boolean supportsLimit() {
        return true;
    }

    public String getLimitString(String oldSql, int skipResults, int maxResults) {
        int endRownumber = skipResults + maxResults;

        String tmpSql = StringUtils.lowerCase(StringUtils.deleteWhitespace(oldSql));
        boolean sqlFlag = StringUtils.contains(tmpSql, "orderby") || StringUtils.contains(tmpSql, "groupby")
                || StringUtils.contains(tmpSql, "having") || StringUtils.contains(tmpSql, "union")
                || StringUtils.contains(tmpSql, "distinct") || StringUtils.countMatches(tmpSql, "select") > 1;

        if (!sqlFlag) {
            if (StringUtils.contains(tmpSql, "where")) {
                tmpSql = null;
                return "select * from (select rownum as myrownum,c.* from (" + oldSql + " and rownum<=" + endRownumber
                        + ") c) where myrownum>" + skipResults;
            }

            tmpSql = null;
            return "select * from (select rownum as myrownum,c.* from (" + oldSql + " where rownum<=" + endRownumber
                    + ") c) where myrownum>" + skipResults;
        }

        tmpSql = null;
        return "select * from (select rownum as myrownum,c.* from (" + oldSql + ") c)where myrownum<=" + endRownumber
                + " and myrownum>" + skipResults;
    }

    public static void main(String[] args) {
    }

}
