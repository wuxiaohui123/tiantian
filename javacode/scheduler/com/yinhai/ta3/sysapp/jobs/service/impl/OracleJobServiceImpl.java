package com.yinhai.ta3.sysapp.jobs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yinhai.sysframework.app.domain.Key;
import com.yinhai.sysframework.dto.BaseDTO;
import com.yinhai.sysframework.dto.DTO;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.dto.PrcDTO;
import com.yinhai.sysframework.exception.AppException;
import com.yinhai.sysframework.service.BaseService;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.jobs.service.OracleJobService;

public class OracleJobServiceImpl extends BaseService implements OracleJobService {

	private static final String YXZ = "运行中";
	private static final String YSC = "已删除";
	private static final String ZT = "暂停";

	public String removeJobs(List list) throws AppException {
		Key key = null;
		String jobid = "";
		String broken = "";
		for (int i = 0; i < list.size(); i++) {
			key = (Key) list.get(i);
			jobid = key.getAsString("jobid");
			broken = key.getAsString("broken");

			if ((broken != null) && (ZT.equals(broken))) {
				oracleRemove(jobid);
			} else {
				return "1";
			}
		}
		return "2";
	}

	public void oracleRemove(String jobid) throws AppException {
		DTO dto = new BaseDTO();
		dto.put("jobid", Integer.valueOf(Integer.parseInt(jobid)));
		dao.queryForObject("oracleJob.prc_oracleRemove", dto);
	}

	public String pauseJobs(List list) throws AppException {
		Key key = null;
		String jobid = "";
		String broken = "";
		String next_date = "";
		for (int i = 0; i < list.size(); i++) {
			key = (Key) list.get(i);
			jobid = key.getAsString("jobid");
			broken = key.getAsString("broken");
			next_date = key.getAsString("next_date");

			if ((broken != null) && (YXZ.equals(broken))) {
				oracleBroken(jobid, true, next_date);
			} else {
				return "1";
			}
		}
		return "2";
	}

	public void oracleBroken(String jobid, boolean broken, String next_date) throws AppException {
		PrcDTO prcDTO = new PrcDTO();
		prcDTO.put("jobid", new Long(jobid));
		prcDTO.put("broken", new Boolean(broken).toString());
		prcDTO.put("next_date", next_date);
		dao.callPrc("oracleJob.prc_oracleBroken", prcDTO);
	}

	public Map createJob(ParamDTO dto) throws AppException {
		Map map = new HashMap();
		String jobName = dto.getAsString("jobname");

		String jobid = getSequence("SEQ_DEFAULT");
		String what = dto.getAsString("what");
		String userid = dto.getAsString("userid");
		String next_date = dto.getAsString("next_date");
		String startTime = "";
		if (next_date.length() == 54) {
			startTime = dto.getAsString("next_date");
		} else {
			startTime = "to_date('" + next_date.substring(0, 19) + "','yyyy-MM-dd HH24:mi:ss')";
		}
		String interval = dto.getAsString("interval");
		if (ValidateUtil.isEmpty(jobid)) {
			jobid = (String) dao.queryForObject("oracleJob.createJobId", null);
		}
		PrcDTO prcDTO = new PrcDTO();
		prcDTO.put("prm_jobid", jobid);
		prcDTO.put("prm_jobname", jobName);
		prcDTO.put("prm_what", what);
		prcDTO.put("prm_next_date", startTime);
		prcDTO.put("prm_userid", userid);
		prcDTO.put("prm_interval", interval);

		dao.callPrc("oracleJob.prc_oracleJob", prcDTO);
		map.put("myjobid", prcDTO.get("prm_jobid"));
		return map;
	}

	public String editJob(ParamDTO dto) throws AppException {
		String jobName = dto.getAsString("jobname");
		String jobid = dto.getAsString("jobid");
		String what = dto.getAsString("what");
		String userid = dto.getAsString("userid");
		String next_date = dto.getAsString("next_date");
		String startTime = "";
		if (next_date.length() == 54) {
			startTime = dto.getAsString("next_date");
		} else {
			startTime = "to_date('" + next_date.substring(0, 19) + "','yyyy-MM-dd HH24:mi:ss')";
		}
		String interval = dto.getAsString("interval");
		PrcDTO prcDTO = new PrcDTO();
		prcDTO.put("prm_jobid", jobid);
		prcDTO.put("prm_jobname", jobName);
		prcDTO.put("prm_what", what);
		prcDTO.put("prm_next_date", startTime);
		prcDTO.put("prm_userid", userid);
		prcDTO.put("prm_interval", interval);
		dao.callPrc("oracleJob.prc_oracleChange", prcDTO);
		return "修改成功！";
	}

	public String continueJob(ParamDTO dto) throws AppException {
		String jobid = dto.getAsString("jobid");
		String next_date = dto.getAsString("next_date");
		String startTime = "";
		if (next_date.length() == 54) {
			startTime = dto.getAsString("next_date");
		} else {
			startTime = "to_date('" + next_date.substring(0, 19) + "','yyyy-MM-dd HH24:mi:ss')";
		}
		oracleBroken(jobid, false, startTime);
		return "操作成功！<br>该任务继续执行已启动！";
	}
}
