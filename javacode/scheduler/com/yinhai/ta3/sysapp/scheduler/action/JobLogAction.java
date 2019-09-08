package com.yinhai.ta3.sysapp.scheduler.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.yinhai.sysframework.dto.DTO;
import com.yinhai.sysframework.dto.ParamDTO;
import com.yinhai.sysframework.persistence.PageBean;
import com.yinhai.ta3.sysapp.scheduler.service.JobLogService;
import com.yinhai.webframework.BaseAction;

@Namespace("/scheduler")
@Action(value = "jobLogAction", results = { @Result(name = "success", location = "/sysapp/schedule/jobLog.jsp"),
		@Result(name = "detail", location = "/sysapp/schedule/jobDetail.jsp") })
public class JobLogAction extends BaseAction {

	private JobLogService service = (JobLogService) getService("jobLogService");

	private Scheduler scheduler = (Scheduler) getService("scheduler");

	public String execute() throws Exception {
		return "success";
	}

	public String query() throws Exception {
		PageBean list = getDao().queryForPageWithCount("logs", "tascheduler.getJobLogs", getDto(), getDto());
		setList("logs", list);
		return "tojson";
	}

	public String clearSuccLog() throws Exception {
		service.clearSuccLog();
		return query();
	}

	public String toCheck() throws Exception {
		Map map = new HashMap();
		map.put("job_name", request.getParameter("job_name"));
		map.put("job_group", request.getParameter("job_group"));
		map.put("trigger_group", request.getParameter("trigger_group"));
		DTO jobDto = (DTO) getDao().queryForObject("tascheduler.getJobByKey", map);
		jobDto.put("jobName", jobDto.get("jobname"));
		jobDto.put("jobGroup", jobDto.get("jobgroup"));
		jobDto.put("jobDesc", jobDto.get("jobdesc"));
		jobDto.put("triggerType", jobDto.get("tritype"));
		if ("CRON".equals(jobDto.get("triggerType"))) {
			jobDto.put("cronExpression", getDao().queryForObject("tascheduler.getCronExp", map));
		} else {
			DTO repeatInfo = (DTO) getDao().queryForObject("tascheduler.getRepeatInfo", map);
			jobDto.put("repeatCount", repeatInfo.get("repeat_count"));
			jobDto.put("repeatInterval", repeatInfo.get("repeat_interval"));
		}
		jobDto.put("triggerGroup", jobDto.get("trigroup"));
		jobDto.put("startTime", new Timestamp(jobDto.getAsLong("st").longValue()).toLocaleString());
		if (jobDto.getAsLong("et").longValue() > 0L)
			jobDto.put("endTime", new Timestamp(jobDto.getAsLong("et").longValue()).toLocaleString());
		setData(jobDto, true);
		setData("isSuccess", request.getParameter("success"));
		setData("log_id", request.getParameter("log_id"));
		return "detail";
	}

	public String resumeJob() throws Exception {
		if (!isLatestLog(getDto())) {
			setSuccess(false);
			setMsg("只能对该任务�?���?��日志信息做处理！");
			setDisabled("resumeBtn,newBtn");
			return "tojson";
		}
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}
		String trigger_name = "tri_" + getDto().get("jobName");
		String trigger_group = getDto().getAsString("triggerGroup");
		scheduler.resumeTrigger(new TriggerKey(trigger_name, trigger_group));
		setDisabled("resumeBtn,newBtn");
		setMsg("任务已恢复�?");
		return "tojson";
	}

	public String newJob() throws Exception {
		if (!isLatestLog(getDto())) {
			setSuccess(false);
			setMsg("只能对该任务�?���?��日志信息做处理！");
			setDisabled("resumeBtn,newBtn");
			return "tojson";
		}
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}
		ParamDTO dto = getDto();
		String jobName = "new_" + dto.getAsString("jobName");
		String jobGroup = dto.getAsString("jobGroup");
		String jobDesc = dto.getAsString("jobDesc");
		String triggerName = "tri_" + jobName;
		String triggerGroup = dto.getAsString("triggerGroup");
		Date startTime = dto.getAsTimestamp("startTime");
		Date endTime = dto.getAsTimestamp("endTime");
		Integer repeatCount = dto.getAsInteger("repeatCount");
		Integer repeatInterval = dto.getAsInteger("repeatInterval");
		String className = "com.yinhai.ta3.sysapp.scheduler.job.TaJob";
		String triggerType = dto.getAsString("triggerType");
		String cronExpression = request.getParameter("cronExpression");
		try {
			Trigger trigger = null;
			if (triggerType.equals("1")) {
				trigger = TriggerBuilder
						.newTrigger()
						.withIdentity(triggerName, triggerGroup)
						.startAt(startTime)
						.endAt(endTime)
						.withSchedule(
								SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(repeatInterval.intValue())
										.withRepeatCount(repeatCount.intValue())).build();

			} else {

				trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).startAt(startTime).endAt(endTime)
						.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
			}

			Class jobClass = Class.forName(className);

			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).withDescription(jobDesc).build();

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception("没有找到类：" + className);
		} catch (ObjectAlreadyExistsException e1) {
			e1.printStackTrace();
			throw new Exception("任务：\"" + jobName + "\"已经存在\"");
		}
		setDisabled("resumeBtn,newBtn");
		setMsg("新的任务 " + jobName + " 已启动�?");
		return "tojson";
	}

	private boolean isLatestLog(DTO dto) {
		Map<String, String> map = new HashMap();
		map.put("log_id", dto.getAsString("log_id"));
		map.put("job_name", dto.getAsString("jobName"));
		if (getDao().queryForObject("tascheduler.getLatestLog", map) == null) {
			return false;
		}
		return true;
	}
}
