package com.yinhai.ta3.sysapp.jobs.service;

import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.Service;

public interface OracleJobService extends Service {

	public abstract String removeJobs(List paramList) throws AppException;

	public abstract String pauseJobs(List paramList) throws AppException;

	public abstract Map createJob(ParamDTO paramParamDTO) throws AppException;

	public abstract String editJob(ParamDTO paramParamDTO) throws AppException;

	public abstract String continueJob(ParamDTO paramParamDTO) throws AppException;
}
