package com.yinhai.sysframework.log;

import java.sql.Timestamp;
import java.util.List;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.Service;

public interface ServerExceptionService extends Service {

	 void addServerException(Taserverexceptionlog log);

	 List<Taserverexceptionlog> getList(Timestamp begin, Timestamp end);

	 PageBean getPage(String gridId, Timestamp begin, Timestamp end, int start, int limit);

	 Taserverexceptionlog getTaserverexceptionlog(String id);

	 void delete(String id);

	 PageBean getPageByCount(String gridId, Timestamp begin, Timestamp end, int start, int limit);
}
