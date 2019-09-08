package com.yinhai.sysframework.persistence.ibatis;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;

public class LimitSqlExecutor extends SqlExecutor {

	private static final Log logger = LogFactory.getLog(LimitSqlExecutor.class);

	private Dialect dialect;

	private boolean enableLimit = true;

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public boolean isEnableLimit() {
		return enableLimit;
	}

	public void setEnableLimit(boolean enableLimit) {
		this.enableLimit = enableLimit;
	}

	public void executeQuery(StatementScope request, Connection conn, String sql, Object[] parameters, int skipResults,
			int maxResults, RowHandlerCallback callback) throws SQLException {
		if ((skipResults != 0 || maxResults != -999999) && supportsLimit()) {
			sql = dialect.getLimitString(sql, skipResults, maxResults);
			if (logger.isDebugEnabled()) {
				logger.debug(sql);
			}
		}
		super.executeQuery(request, conn, sql, parameters, 0, -999999, callback);
	}

	public boolean supportsLimit() {
		if (enableLimit && dialect != null) {
			return dialect.supportsLimit();
		}
		return false;
	}
}
