package com.yinhai.sysframework.persistence.ibatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.dto.PrcDTO;
import com.yinhai.sysframework.exception.PrcException;
import com.yinhai.sysframework.persistence.PageBean;

@SuppressWarnings({"rawtypes", "deprecation"})
public interface IDao {

    int delete(String statementName) throws DataAccessException;

    int delete(String statementName, Object paramObject) throws DataAccessException;

    Object insert(String statementName) throws DataAccessException;

    Object insert(String statementName, Object obj) throws DataAccessException;

    int update(String statementName) throws DataAccessException;

    int update(String statementName, Object obj) throws DataAccessException;

    Object queryForObject(String statementName) throws DataAccessException;

    Object queryForObject(String statementName, Object obj) throws DataAccessException;

    List queryForList(String statementName) throws DataAccessException;

    List queryForList(String statementName, Object obj) throws DataAccessException;

    List queryForPage(String statementName, Object obj, int skipResults, int maxResults);

    List queryForPage(String gridId, String statementName, ParamDTO paramDTO);

    PageBean queryForPageWithCount(String statementName, Object obj, int skipResults, int maxResults);

    PageBean queryForPageWithCount(String gridId, String statementName, Object obj, ParamDTO dto);

    void queryWithRowHandler(String paramString, Object paramObject, RowHandler paramRowHandler);

    void queryWithRowHandler(String statementName, Object obj, RowHandler rowHandler, int skipResults, int maxResults);

    void callPrc(String prcName, PrcDTO dto) throws PrcException;

    Connection getConnection() throws SQLException;

    SqlMapClient getSqlMapClient();


    SqlMapClientTemplate getSqlMapClientTemplate();

    int saveOldFieldData(ParamDTO pdto, BaseDomain domainClass, String menuId, String menuName);

    int insertBatch(String paramString, List<?> paramList) throws DataAccessException;

    int updateBatch(String paramString, List<?> paramList) throws DataAccessException;

    int deleteBatch(String paramString, List<?> paramList) throws DataAccessException;

    int executeBatchByJDBC(final String sql, List<?> paramList) throws DataAccessException;
}
