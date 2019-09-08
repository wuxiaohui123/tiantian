package com.yinhai.sysframework.sequence;

import javax.jws.WebService;

@WebService
public interface IWsSequenceService {

    String SERVICEKEY = "wsSequenceService";

    String getStringSeq(String seqName);

    Long getLongSeq(String seqName);

    String getSequence(String seqName);
}
