package com.yinhai.sysframework.persistence.ibatis;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.dto.ParamDTO;

public interface SaveOldFieldService {

	int saveOldFieldData(ParamDTO paramParamDTO, BaseDomain paramBaseDomain, String paramString1, String paramString2);
}
