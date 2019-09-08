package com.yinhai.sysframework.service;

import java.util.Date;

public interface WsService {

	 String getEntityClassName(String fullClassName);

	 String getSysStrTimestamp();

	 Date getSysDate();

	 String getSysStrDate();

	 String getStringSeq(String seqName);

	 Long getLongSeq(String seqName);

	 String getSequence(String seqName);
}
