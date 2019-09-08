package com.yinhai.sysframework.sequence;

public interface ISequenceService {

	 String SERVICEKEY = "sequenceService";

	 String getStringSeq(String seqName);

	 Long getLongSeq(String seqName);

	 String getStringSeq();

	 Long getLongSeq();

	 String getSequence(String seqName);
}
