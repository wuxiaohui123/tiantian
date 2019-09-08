package com.yinhai.sysframework.persistence.ibatis;

import java.sql.Connection;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.parameter.ParameterMap;
import com.ibatis.sqlmap.engine.mapping.result.AutoResultMap;
import com.ibatis.sqlmap.engine.mapping.result.ResultMap;
import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.mapping.statement.SelectStatement;
import com.ibatis.sqlmap.engine.scope.ErrorContext;
import com.ibatis.sqlmap.engine.scope.StatementScope;


@SuppressWarnings("deprecation")
public class CountStatement extends SelectStatement {

	public CountStatement(SelectStatement selectStatement) {
		setId(CountStatementUtil.getCountStatementId(selectStatement.getId()));
		setResultSetType(selectStatement.getResultSetType());
		setFetchSize(1);
		setParameterMap(selectStatement.getParameterMap());
		setParameterClass(selectStatement.getParameterClass());
		setSql(selectStatement.getSql());
		setResource(selectStatement.getResource());
		setSqlMapClient(selectStatement.getSqlMapClient());
		setTimeout(selectStatement.getTimeout());

		ResultMap resultmap = new AutoResultMap(((ExtendedSqlMapClient) getSqlMapClient()).getDelegate(), false);
		resultmap.setId(getId() + "-AutoResultMap");
		resultmap.setResultClass(Long.class);
		resultmap.setResource(getResource());
		setResultMap(resultmap);
	}

	protected void executeQueryWithCallback(StatementScope request, Connection conn, Object parameterObject,
			Object resultObject, RowHandler rowHandler, int skipResults, int maxResults) throws SQLException {
		ErrorContext errorContext = request.getErrorContext();
		errorContext.setActivity("preparing the mapped statement for execution");
		errorContext.setObjectId(getId());
		errorContext.setResource(getResource());

		parameterObject = validateParameter(parameterObject);
		Sql sql = getSql();

		errorContext.setMoreInfo("Check the parameter map.");
		ParameterMap parameterMap = sql.getParameterMap(request, parameterObject);

		request.setParameterMap(parameterMap);

		errorContext.setMoreInfo("Check the result map.");
		ResultMap resultMap = getResultMap();

		errorContext.setMoreInfo("Check the parameter object.");

		Object[] parameters = parameterMap.getParameterObjectValues(request, parameterObject);

		errorContext.setMoreInfo("Check the SQL statement.");
		String sqlString = getSelectCountSqlString(request, parameterObject, sql);

		errorContext.setActivity("executing mapped statement");
		errorContext.setMoreInfo("Check the SQL statement or the result map.");
		RowHandlerCallback callback = new RowHandlerCallback(resultMap, resultObject, rowHandler);
		sqlExecuteQuery(request, conn, sqlString, parameters, skipResults, maxResults, callback);

		errorContext.setMoreInfo("Check the output parameters.");
		if (parameterObject != null) {
			postProcessParameterObject(request, parameterObject, parameters);
		}

		errorContext.reset();
		sql.cleanup(request);
		notifyListeners();
	}

	private String getSelectCountSqlString(StatementScope request, Object parameterObject, Sql sql) {
		String sqlString = sql.getSql(request, parameterObject);
		sqlString = "SELECT COUNT(1) AS count from(" + sqlString + ") a";
		return sqlString;
	}
}
