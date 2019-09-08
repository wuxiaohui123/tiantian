package com.yinhai.sysframework.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.codetable.domain.AppCode;
import com.yinhai.sysframework.codetable.service.CodeTableLocator;
import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.persistence.ibatis.IDao;
import com.yinhai.sysframework.sequence.ISequenceService;
import com.yinhai.sysframework.time.ITimeService;

public class BaseService implements Service {

	protected IDao dao;
	protected ITimeService timeService;
	protected ISequenceService sequenceService;

	public Timestamp getSysTimestamp() {
		return timeService.getSysTimestamp();
	}

	public Date getSysDate() {
		return timeService.getSysDate();
	}

	public Long getLongSeq(String seqName) {
		return sequenceService.getLongSeq(seqName);
	}

	public Long getLongSeq() {
		return sequenceService.getLongSeq();
	}

	public String getStringSeq(String seqName) {
		return sequenceService.getStringSeq(seqName);
	}

	public String getSequence(String seqName) {
		return getStringSeq(seqName);
	}

	public String getStringSeq() {
		return sequenceService.getStringSeq();
	}

	public IDao getDao() {
		return dao;
	}

	public void setDao(IDao dao) {
		this.dao = dao;
	}

	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	public void setSequenceService(ISequenceService sequenceService) {
		this.sequenceService = sequenceService;
	}

	public String getEntityClassName(String fullClassName) {
		return SysConfig.getSysConfig(fullClassName, fullClassName);
	}

	public String getSysStrTimestamp() {
		return timeService.getSysStrTimestamp();
	}

	public String getSysStrDate() {
		return timeService.getSysStrDate();
	}

	public String getCodeDesc(String codeType, String codeValue, String orgId) {
		return CodeTableLocator.getCodeDesc(codeType, codeValue, orgId);
	}

	public List<AppCode> getCodeList(String codeType, String orgId) {
		return CodeTableLocator.getCodeList(codeType, orgId);
	}
}
