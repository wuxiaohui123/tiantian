package com.yinhai.ta3.organization.service;

import java.util.List;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.service.Service;
import com.yinhai.ta3.system.org.domain.UploadOrgUserVO;

public interface IUploadOrgUserService extends Service {

	public static final String SERVICEKEY = "uploadOrgUserService";

	public abstract void detachUploadOrgAndUser(List<UploadOrgUserVO> paramList, ParamDTO paramParamDTO);

	public abstract void detachUploadOrg(List<UploadOrgUserVO> paramList, ParamDTO paramParamDTO);

	public abstract void detachUploadUser(List<UploadOrgUserVO> paramList, ParamDTO paramParamDTO);
}
