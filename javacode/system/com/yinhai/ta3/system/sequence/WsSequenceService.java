package com.yinhai.ta3.system.sequence;

import javax.jws.WebService;

import com.yinhai.sysframework.sequence.ISequenceService;
import com.yinhai.sysframework.sequence.IWsSequenceService;

@WebService
public class WsSequenceService implements IWsSequenceService {

	private ISequenceService sequenceService;

	@javax.jws.WebMethod(exclude = true)
	public void setSequenceService(ISequenceService sequenceService) {
		this.sequenceService = sequenceService;
	}

	public String getStringSeq(String seqName) {
		return sequenceService.getStringSeq(seqName);
	}

	public Long getLongSeq(String seqName) {
		return sequenceService.getLongSeq(seqName);
	}

	public String getSequence(String seqName) {
		return sequenceService.getStringSeq(seqName);
	}

}
