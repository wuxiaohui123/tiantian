package com.yinhai.ta3.sysapp.consolemg.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.Position;

public interface ModuleMainService extends Service {

	public abstract List getModuleList(ParamDTO paramParamDTO);

	public abstract int addModuleItem(ParamDTO paramParamDTO);

	public abstract int updateModuleItem(ParamDTO paramParamDTO);

	public abstract Map getModuleItem(ParamDTO paramParamDTO);

	public abstract void saveGrant(ParamDTO paramParamDTO, List<Map> paramList);

	public abstract List getGrantList(ParamDTO paramParamDTO);

	public abstract List<Position> getAllPositions(ParamDTO paramParamDTO);
}
