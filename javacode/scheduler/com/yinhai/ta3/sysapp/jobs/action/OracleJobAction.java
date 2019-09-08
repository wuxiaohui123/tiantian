package com.yinhai.ta3.sysapp.jobs.action;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.sysframework.print.ColumnInfo;
import com.yinhai.sysframework.print.SaveAsInfo;
import com.yinhai.sysframework.util.ExcelFileUtils;
import com.yinhai.sysframework.util.TxtFileUtils;
import com.yinhai.sysframework.util.ValidateUtil;
import com.yinhai.ta3.sysapp.jobs.service.OracleJobService;
import com.yinhai.webframework.BaseAction;

public class OracleJobAction extends BaseAction {

	private OracleJobService oracleJobService = (OracleJobService) getService("oracleJobService");

	private static final String CREATEJOB = "createJob";

	private static final String CONTINUEJOB = "continueJob";

	private static final String VIEWJOBMSGS = "viewjobmsgs";

	public String execute() throws Exception {
		return "success";
	}

	public String queryJob() throws Exception {
		ParamDTO dto = getDto();
		PageBean pageBean = null;
		pageBean = getDao().queryForPageWithCount("oracleJobList", "oracleJob.queryOracleJobList", dto, getDto());
		int pageBeanTotal = pageBean.getTotal().intValue();
		if (pageBeanTotal == 0) {
			setMsg("查询结果为空");
		}
		setList("oracleJobList", pageBean);
		return JSON;
	}

	public String toCreateJob() throws Exception {
		Timestamp nextdate = oracleJobService.getSysTimestamp();
		setData("flag", "1");
		setData("next_date", nextdate);
		return CREATEJOB;
	}

	public String createJob() throws Exception {
		ParamDTO dto = getDto();

		String user = getDto().getUserInfo().getUserId();
		dto.put("userid", user);
		setData(oracleJobService.createJob(dto), true);
		setMsg("新建成功！");
		return JSON;
	}

	public String deleteJobs() throws Exception {
		List list = getSelected("oracleJobList");
		String msg = oracleJobService.removeJobs(list);
		if (msg.equals("1")) {
			setSuccess(false);
			setMsg("要暂停的任务才能删除！");
		} else {
			setMsg("删除成功！");
		}
		return JSON;
	}

	public String pauseJobs() throws Exception {
		List list = getSelected("oracleJobList");
		String msg = oracleJobService.pauseJobs(list);
		if (msg.equals("1")) {
			setSuccess(false);
			setMsg("运行中的任务才能暂停！");
		} else {
			setMsg("暂停成功！");
		}
		return JSON;
	}

	public String toEditJob() throws Exception {
		ParamDTO dto = getDto();

		if ((dto.getAsString("interval").equals("null")) || (dto.getAsString("interval").equals("undefined"))) {
			dto.put("interval", null);
		}
		dto.put("flag", "2");
		dto.put("what", dto.getAsString("what"));
		dto.put("interval", dto.getAsString("interval"));
		dto.put("jobname", dto.getAsString("jobname"));
		dto.put("jobid", dto.getAsString("jobid"));
		dto.put("next_date", dto.getAsString("next_date"));
		setData(dto, true);
		return "createJob";
	}

	public String editJob() throws Exception {
		ParamDTO dto = getDto();

		String user = getDto().getUserInfo().getUserId();
		dto.put("userid", user);
		setMsg(oracleJobService.editJob(dto));
		return JSON;
	}

	public String toContinueJob() throws Exception {
		ParamDTO dto = getDto();

		if ((dto.getAsString("interval").equals("null")) || (dto.getAsString("interval").equals("undefined"))) {
			dto.put("interval", null);
		}
		dto.put("jobid", dto.getAsString("jobid"));
		dto.put("what", dto.getAsString("what"));
		dto.put("interval", dto.getAsString("interval"));
		dto.put("jobname", dto.getAsString("jobname"));
		dto.put("next_date", dto.getAsString("next_date"));
		setData(dto, true);
		return CONTINUEJOB;
	}

	public String continueJob() throws Exception {
		ParamDTO dto = getDto();

		String user = getDto().getUserInfo().getUserId();
		dto.put("userid", user);
		setMsg(oracleJobService.continueJob(dto));
		return JSON;
	}

	public String showDetailJob() throws Exception {
		ParamDTO dto = getDto();
		String group = dto.getAsString("jobgroup");
		dto.put("group", group);
		setData(dto, true);
		return VIEWJOBMSGS;
	}

	public String queryDetailJobMsgs() throws Exception {
		ParamDTO dto = getDto();

		String flag = dto.getAsString("flag");
		PageBean pageBeanSuccess = null;
		PageBean pageBeanError = null;

		if (flag.equals("1")) {
			pageBeanSuccess = getDao().queryForPageWithCount("jobSuccessMsgList", "oracleJob.getJobDomainsSuccess", dto, getDto());
			int pageBeanTotalSuccess = pageBeanSuccess.getTotal().intValue();
			if (pageBeanTotalSuccess == 0) {
				setMsg("查询结果为空");
			}
			setList("jobSuccessMsgList", pageBeanSuccess);
		} else if (flag.equals("2")) {
			pageBeanError = getDao().queryForPageWithCount("jobErrorMsgList", "oracleJob.getJobDomainsError", dto, getDto());
			int pageBeanTotalError = pageBeanError.getTotal().intValue();
			if (pageBeanTotalError == 0) {
				setMsg("查询结果为空");
			}
			setList("jobErrorMsgList", pageBeanError);
		} else {
			pageBeanSuccess = getDao().queryForPageWithCount("jobSuccessMsgList", "oracleJob.getJobDomainsSuccess", dto, getDto());
			pageBeanError = getDao().queryForPageWithCount("jobErrorMsgList", "oracleJob.getJobDomainsError", dto, getDto());
			int pageBeanTotalSuccess = pageBeanSuccess.getTotal().intValue();
			int pageBeanTotalError = pageBeanError.getTotal().intValue();
			if ((pageBeanTotalSuccess == 0) && (pageBeanTotalError == 0)) {
				setMsg("查询结果为空");
			}
			setList("jobSuccessMsgList", pageBeanSuccess);
			setList("jobErrorMsgList", pageBeanError);
		}
		return JSON;
	}

	private void export(HttpServletRequest request, HttpServletResponse response, String fileName, String colMetaStr, List list, Class c)
			throws Exception {
		String CONTENT_TYPE = "*/*";
		fileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
		response.setContentType(CONTENT_TYPE);
		response.setHeader("Content-disposition", "attachment;filename=" + fileName);

		if (ValidateUtil.isNotEmpty(list)) {
			SaveAsInfo saveAsInfo = new SaveAsInfo(c);
			saveAsInfo.setDomainList(list);
			saveAsInfo.setFileName(fileName);
			saveAsInfo.setViewTitle(true);
			saveAsInfo.setShowCode(false);

			List columnMetas = new ArrayList();
			ColumnInfo columnMeta = null;

			StringTokenizer entrys = new StringTokenizer(colMetaStr, "^");
			while (entrys.hasMoreTokens()) {
				StringTokenizer items = new StringTokenizer(entrys.nextToken(), "`");

				columnMeta = new ColumnInfo();
				columnMeta.setColumnName(items.nextToken());
				columnMeta.setFieldName(columnMeta.getColumnName());
				columnMeta.setTitlecomment(items.nextToken());
				columnMetas.add(columnMeta);
			}
			saveAsInfo.setColumnList(columnMetas);
			ByteArrayOutputStream bos = null;
			if (fileName.indexOf(".xls") > 0) {
				bos = ExcelFileUtils.saveAsDomainListToExcelFile(request, response, saveAsInfo);
			} else {
				bos = TxtFileUtils.saveAsDomainListToTxtFile(request, response, saveAsInfo);
			}

			DataOutputStream out = new DataOutputStream(response.getOutputStream());

			bos.writeTo(out);
			out.flush();
			out.close();
			bos.flush();
			bos.close();
		}
	}
}
