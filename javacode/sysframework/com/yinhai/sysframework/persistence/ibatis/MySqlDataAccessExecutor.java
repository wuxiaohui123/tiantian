package com.yinhai.sysframework.persistence.ibatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.ibatis.sqlmap.engine.mapping.statement.RowHandlerCallback;
import com.ibatis.sqlmap.engine.scope.StatementScope;
import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.iorg.IDataAccessApi;
import com.yinhai.sysframework.iorg.IUser;
import com.yinhai.sysframework.menu.IMenu;
import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.WebUtil;

public class MySqlDataAccessExecutor extends LimitSqlExecutor {

	public void executeQuery(StatementScope request, Connection conn, String sql, Object[] parameters, int skipResults,
			int maxResults, RowHandlerCallback callback) throws SQLException {
		try {
			IUser user = WebUtil.getUserInfo(ServletActionContext.getRequest());
			IMenu menu = WebUtil.getCurrentMenu(ServletActionContext.getRequest());
			if (user != null && menu != null) {
				IDataAccessApi service = (IDataAccessApi) ServiceLocator.getService("dataAccessApi");
				List<AppCode> list = service.query(menu.getMenuid(), user.getNowPosition().getPositionid(), "yab003");
				Statement stmt = conn.createStatement();
				stmt.execute("delete from T_YAB003");
				for (AppCode appCode : list) {
					String tsql = "insert into T_YAB003(YAB003) values(" + appCode.getCodeValue() + ")";
					stmt.addBatch(tsql);
				}
				stmt.executeBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.executeQuery(request, conn, sql, parameters, skipResults, maxResults, callback);
	}
}
