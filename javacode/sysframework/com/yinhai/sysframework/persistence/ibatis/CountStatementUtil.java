package com.yinhai.sysframework.persistence.ibatis;

import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.impl.SqlMapExecutorDelegate;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import com.ibatis.sqlmap.engine.mapping.statement.SelectStatement;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.exception.SysLevelException;

@SuppressWarnings("deprecation")
public class CountStatementUtil {

	public static MappedStatement createCountStatement(MappedStatement selectStatement) {
		if ((selectStatement instanceof SelectStatement)) {
			return new CountStatement((SelectStatement) selectStatement);
		}
		throw new SysLevelException("系统异常，请联系管理员:selectStatement对象不是SelectStatement类型。");
	}

	public static String getCountStatementId(String selectStatementId) {
		return "YinHai_" + selectStatementId + "_YinHai_Count";
	}

	public static long autoGetTotalCount(String selectQuery, Object parameterObject, IDao dao) {
		long total = 0L;
		try {
			total = getTotal4Select(selectQuery, parameterObject, dao);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("获取总的记录数错误:" + e.getMessage());
		}
		return total;
	}

	private static long getTotal4Select(String selectQuery, Object object, IDao dao) throws SQLException {
		putCountMappedStatement(selectQuery, dao);
		String countSQL = getCountStatementId(selectQuery);
		Long result = (Long) dao.queryForObject(countSQL, object);
		result = null != result ? result : Long.valueOf(0L);
		return result.longValue();
	}

	private static void putCountMappedStatement(String selectQuery, IDao dao) {
		String countQuery = getCountStatementId(selectQuery);
		SqlMapClient sqlMapClient = dao.getSqlMapClient();
		if (sqlMapClient instanceof ExtendedSqlMapClient) {
			SqlMapExecutorDelegate delegate = ((ExtendedSqlMapClient) sqlMapClient).getDelegate();
			try {
				delegate.getMappedStatement(countQuery);
			} catch (SqlMapException e) {
				MappedStatement selectMap = delegate.getMappedStatement(selectQuery);
				if (null == selectMap)
					throw new AppException("XFACE分页自动获取select count时错误，需要查询的sqlmap id:" + selectQuery
							+ ", 没有在xml文件中配置!");
				MappedStatement ms = createCountStatement(selectMap);
				delegate.addMappedStatement(ms);
			}
		}
	}
}
