package com.yinhai.sysframework.iorg;

import java.util.List;

import com.yinhai.sysframework.codetable.domain.AppCode;

public interface IDataAccessApi {

	 String SERVICEKEY = "dataAccessApi";

	 List<AppCode> query(Long menuid, Long positionid, String dimensiontype);

	 void clearCache(Long menuid, Long positionid, String dimensiontype);
}
