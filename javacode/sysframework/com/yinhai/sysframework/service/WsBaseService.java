package com.yinhai.sysframework.service;

import java.util.Date;

import javax.jws.WebMethod;

import com.yinhai.sysframework.config.SysConfig;
import com.yinhai.sysframework.sequence.ISequenceService;
import com.yinhai.sysframework.time.ITimeService;
import com.yinhai.sysframework.util.ValidateUtil;

public class WsBaseService implements WsService {

	protected ITimeService timeService;
	protected ISequenceService sequenceService;

	public String getSysStrTimestamp() {
		return timeService.getSysStrTimestamp();
	}

	public Date getSysDate() {
		return timeService.getSysDate();
	}

	public String getSysStrDate() {
		return timeService.getSysStrDate();
	}

	public String getStringSeq(String seqName) {
		if (ValidateUtil.isEmpty(seqName)) {
			return sequenceService.getStringSeq();
		}
		return sequenceService.getStringSeq(seqName);
	}

	public Long getLongSeq(String seqName) {
		if (ValidateUtil.isEmpty(seqName)) {
			return sequenceService.getLongSeq();
		}
		return sequenceService.getLongSeq(seqName);
	}

	public String getSequence(String seqName) {
		return getStringSeq(seqName);
	}

	@WebMethod(exclude = true)
	public void setTimeService(ITimeService timeService) {
		this.timeService = timeService;
	}

	@WebMethod(exclude = true)
	public void setSequenceService(ISequenceService sequenceService) {
		this.sequenceService = sequenceService;
	}

	public String getEntityClassName(String fullClassName) {
		return SysConfig.getSysConfig(fullClassName, fullClassName);
	}

}
