package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.sysapp.domain.Menu;

public interface IInnerControlService extends Service {

	public static final String SERVICEKEY = "innerControlService";

	public abstract List queryAdminByOrgId(Long paramLong1, String paramString, Long paramLong2);

	public abstract List queryPositionByOrgId(Long paramLong1, String paramString, Long paramLong2);

	public abstract List queryLogByAdmin(ParamDTO paramParamDTO);

	public abstract List queryBusinessByOrgId(Long paramLong1, String paramString, Long paramLong2);

	public abstract List queryLogByBusiness(ParamDTO paramParamDTO);

	public abstract List queryLogByMenu(ParamDTO paramParamDTO);

	public abstract Menu getMenu(Long paramLong);

	public abstract List<Menu> getChildMenus(Long paramLong);
}
