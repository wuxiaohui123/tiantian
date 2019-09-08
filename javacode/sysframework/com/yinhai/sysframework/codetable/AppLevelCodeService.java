package com.yinhai.sysframework.codetable;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.yinhai.sysframework.dto.DTO;

public interface AppLevelCodeService {

	public abstract String getCodeLableDesc(String paramString1, String paramString2);

	public abstract List getAll(Map paramMap);

	public abstract List getAppCodeByMap(Map paramMap);

	public abstract String getAppCodeDescByCodeTypeCodeValue(String paramString1, String paramString2);

	public abstract String getAppCodeDescByCodeTypeCodeValue(String paramString1, String paramString2,
			String paramString3);

	public abstract String getAppCodeDescByCodeTypeCodeValue(String paramString1, String paramString2,
			String paramString3, String paramString4);

	public abstract void loadAppCodesToServletContext(DTO paramDTO, ServletContext paramServletContext);

	public abstract void updateAppCodesToServletContext(DTO paramDTO, ServletContext paramServletContext);
}
