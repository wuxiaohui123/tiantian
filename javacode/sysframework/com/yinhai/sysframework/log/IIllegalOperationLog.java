package com.yinhai.sysframework.log;

import java.util.Date;
import java.util.List;

import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.OrgLogInfoVO;

public interface IIllegalOperationLog  extends Service {

	String SERVICEKEY = "illegalOperationLog";
	String ILLEGAL = "1";
	  
	void saveIllegalOperationLog(Long batchNo, Long userid, Long positionid, String opttype, String opobjecttype, Long optid, String changcontent);
	  
	List<OrgLogInfoVO> queryIllegalOperationLog(Date startDate, Date endDate);
	  
	void saveIllegalOperationMenuLog(Long batchNo, Long userid, Long positionid, String opttype, String opobjecttype, Long menuid, Long opPositionid);
}
