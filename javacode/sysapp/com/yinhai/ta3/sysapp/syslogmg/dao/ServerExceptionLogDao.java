package com.yinhai.ta3.sysapp.syslogmg.dao;

import com.yinhai.sysframework.log.Taserverexceptionlog;
import com.yinhai.sysframework.persistence.PageBean;

import javax.jws.WebService;
import java.sql.Timestamp;
import java.util.List;

@WebService
public interface ServerExceptionLogDao {

	///////////////////////// Oracle/Mysql 数据库/////////////////////////
	void insert(Taserverexceptionlog taserverexceptionlog);

	List<Taserverexceptionlog> getList(Timestamp timestamp1, Timestamp timestamp2);

	Taserverexceptionlog getTaserverexceptionlog(String str);

	void delete(String str);

	PageBean getPage(String str, Timestamp timestamp1, Timestamp timestamp2, int int1, int int2);

	PageBean getPageByCount(String str, Timestamp timestamp1, Timestamp timestamp2, int int1, int int2);
}
