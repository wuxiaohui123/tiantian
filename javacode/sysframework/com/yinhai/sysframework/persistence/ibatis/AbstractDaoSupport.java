package com.yinhai.sysframework.persistence.ibatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.SqlExecutor;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.dto.PrcDTO;
import com.yinhai.sysframework.exception.PrcException;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.util.ReflectUtil;

public abstract class AbstractDaoSupport extends SqlMapClientDaoSupport implements IDao {

	private SqlExecutor sqlExecutor;

	public int delete(String statementName) throws DataAccessException {
		return super.getSqlMapClientTemplate().delete(statementName);
	}

	public int delete(String statementName, Object obj) throws DataAccessException {
		return super.getSqlMapClientTemplate().delete(statementName, obj);
	}

	public Object insert(String statementName) throws DataAccessException {
		return super.getSqlMapClientTemplate().insert(statementName);
	}

	public Object insert(String statementName, Object obj) throws DataAccessException {
		return super.getSqlMapClientTemplate().insert(statementName, obj);
	}

	public int update(String statementName) throws DataAccessException {
		return super.getSqlMapClientTemplate().update(statementName);
	}

	public int update(String statementName, Object obj) throws DataAccessException {
		return super.getSqlMapClientTemplate().update(statementName, obj);
	}

	public Object queryForObject(String statementName) throws DataAccessException {
		return super.getSqlMapClientTemplate().queryForObject(statementName);
	}

	public Object queryForObject(String statementName, Object obj) throws DataAccessException {
		return super.getSqlMapClientTemplate().queryForObject(statementName, obj);
	}

	public List queryForList(String statementName) throws DataAccessException {
		return super.getSqlMapClientTemplate().queryForList(statementName);
	}

	public List queryForList(String statementName, Object obj) throws DataAccessException {
		return super.getSqlMapClientTemplate().queryForList(statementName, obj);
	}

	public List queryForPage(String statementName, Object obj, int skipResults, int maxResults) {
		return super.getSqlMapClientTemplate().queryForList(statementName, obj, skipResults, maxResults);
	}

	public List queryForPage(String gridId, String statementName, ParamDTO paramDTO) {
		Integer start = Integer.valueOf(paramDTO.getStart(gridId) == null ? 0 : paramDTO.getStart(gridId).intValue());

		Integer limit = Integer.valueOf(paramDTO.getLimit(gridId) == null ? 0 : paramDTO.getLimit(gridId).intValue());

		if ((logger.isErrorEnabled()) && (limit.intValue() == 0)) {
			logger.error("queryForPage start=" + start + ",limit=" + limit);
		}
		return queryForPage(statementName, paramDTO, start.intValue(), limit.intValue());
	}

	public PageBean queryForPageWithCount(String statementName, Object obj, int skipResults, int maxResults) {
		List list = super.getSqlMapClientTemplate().queryForList(statementName, obj, skipResults, maxResults);

		PageBean pb = new PageBean();
		pb.setStart(Integer.valueOf(skipResults));
		pb.setLimit(Integer.valueOf(maxResults));
		pb.setList(list);
		pb.setTotal(new Integer(CountStatementUtil.autoGetTotalCount(statementName, obj, this) + ""));

		return pb;
	}

	public PageBean queryForPageWithCount(String gridId, String statementName, Object obj, ParamDTO dto) {
		Integer skipResults = dto.getStart(gridId) == null ? 0 : dto.getStart(gridId);
		Integer maxResults = dto.getLimit(gridId) == null ? 0 : dto.getLimit(gridId);
		if (logger.isErrorEnabled() && maxResults == 0) {
			logger.error("queryForPageWithCount start=" + skipResults + ",limit=" + maxResults);
		}
		List list = super.getSqlMapClientTemplate().queryForList(statementName, obj, skipResults, maxResults);

		PageBean pb = new PageBean();
		pb.setStart(skipResults);
		pb.setLimit(maxResults);
		pb.setList(list);
		pb.setTotal(new Integer(CountStatementUtil.autoGetTotalCount(statementName, obj, this) + ""));

		return pb;
	}

	public void queryWithRowHandler(String statementName, Object object, RowHandler rowHandler) {
		super.getSqlMapClientTemplate().queryWithRowHandler(statementName, object, rowHandler);
	}

	public void queryWithRowHandler(String statementName, Object obj, RowHandler rowHandler, int skipResults, int maxResults) {
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			@Override
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.queryWithRowHandler(statementName, obj, rowHandler, skipResults, maxResults);
				return null;
			}
		});
	}

	public void callPrc(String prcName, PrcDTO dto) throws PrcException {
		queryForObject(prcName, dto);
		if (dto.getAppCode() == null || !"NOERROR".equalsIgnoreCase(dto.getAppCode())) {

			throw new PrcException(prcName, dto.getAppCode(), dto.getErrorMsg(), dto.getShortMsg());
		}
	}

	public Connection getConnection() throws SQLException {
		return super.getDataSource().getConnection();
	}

	public SqlExecutor getSqlExecutor() {
		return sqlExecutor;
	}

	public void setSqlExecutor(SqlExecutor sqlExecutor) {
		this.sqlExecutor = sqlExecutor;
	}

	public void setEnableLimit(boolean enableLimit) {
		if ((sqlExecutor instanceof LimitSqlExecutor)) {
			((LimitSqlExecutor) sqlExecutor).setEnableLimit(enableLimit);
		}
	}

	public void initialize() throws Exception {
		if (sqlExecutor != null) {
			SqlMapClient sqlMapClient = getSqlMapClientTemplate().getSqlMapClient();
			if ((sqlMapClient instanceof ExtendedSqlMapClient)) {
				ReflectUtil.setFieldValue(((ExtendedSqlMapClient) sqlMapClient).getDelegate(), "sqlExecutor",
						SqlExecutor.class, sqlExecutor);
			}
		}
	}

	
	public int insertBatch(String statementName, List<?> list) throws DataAccessException {
		final List<?> finalList = list;
		final String finalStatementName = statementName;
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (Object object : finalList) {
					executor.insert(finalStatementName, object);
				}
				executor.executeBatch();
				return null;
			}
		});
		return list.size();
	}
	
	public int updateBatch(String statementName, List<?> list) throws DataAccessException {
		final List<?> finalList = list;
		final String finalStatementName = statementName;
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback<Object>(){
			@Override
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (Object object : finalList) {
					executor.update(finalStatementName, object);
				}
				executor.executeBatch();
				return null;
			}
		});
		return list.size();
	}
	
	public int deleteBatch(String statementName, List<?> list) throws DataAccessException{
		final List<?> finalList = list;
		final String finalStatementName = statementName;
		super.getSqlMapClientTemplate().execute(new SqlMapClientCallback<Object>() {
			@Override
			public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
				executor.startBatch();
				for (Object object : finalList) {
					executor.delete(finalStatementName, object);
				}
				executor.executeBatch();
				return null;
			}
		});
		return list.size();
	}
	public int executeBatchByJDBC(final String sql,List<?> paramList) {
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(sql);
			for (Object obj : paramList) {
				Map<String,String> map = (Map<String,String>) obj;
				int mindex = 1;
				for (String key : map.keySet()) {
					ps.setString(mindex, map.get(key).trim());
					mindex++;
				}
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException el2) {
				el2.printStackTrace();
			} catch (Exception el3) {
				el3.printStackTrace();
			}

		}
		return paramList.size();
	}
}
