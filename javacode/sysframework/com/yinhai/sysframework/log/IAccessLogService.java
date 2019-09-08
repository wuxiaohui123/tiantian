package com.yinhai.sysframework.log;

import java.util.Date;

import javax.jws.WebService;

import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.service.WsService;

@WebService
public interface IAccessLogService extends WsService {

	 String SERVICEKEY = "accessLogService";

	 void saveAccessInfo(Long userId, Long positionId, Long menuid, String url, String ispermission);

	 PageBean queryAccessInfo(Date startDate, Date endDate, Integer start, Integer limit, Long userid, Long positionid);
	
}
