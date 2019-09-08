package com.yinhai.sysframework.service;

import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.sequence.ISequenceService;
import com.yinhai.sysframework.time.ITimeService;

public interface Service extends ITimeService, ISequenceService {

	IDao getDao();

	String getEntityClassName(String fullClassName);
}
