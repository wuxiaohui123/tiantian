package com.yinhai.ta3.sysapp.scheduler.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.webframework.BaseAction;

/*@Namespace("/scheduler")
@Action(value = "schedulerMgAction", results = { @Result(name = "success", location = "/sysapp/schedule/scheduleManage.jsp"),
		@Result(name = "edit", location = "/sysapp/schedule/scheduleEdit.jsp"),
		@Result(name = "cron", location = "/sysapp/schedule/cronExpression.jsp") })*/
public class ScheduleManageAction extends BaseAction {

	private Scheduler scheduler = (Scheduler) getService("scheduler");

	public String execute() throws Exception {
		List jobList = getDao().queryForList("tascheduler.getAllJobs");
		for (int i = 0; i < jobList.size(); i++) {
			DTO jobDto = (DTO) jobList.get(i);
			jobDto.put("st", new Date(jobDto.getAsLong("st").longValue()).toLocaleString());
			if (jobDto.getAsLong("et").longValue() > 0L)
				jobDto.put("et", new Date(jobDto.getAsLong("et").longValue()).toLocaleString());
			if (jobDto.getAsLong("pt").longValue() > 0L)
				jobDto.put("pt", new Date(jobDto.getAsLong("pt").longValue()).toLocaleString());
			if (jobDto.getAsLong("nt").longValue() > 0L)
				jobDto.put("nt", new Date(jobDto.getAsLong("nt").longValue()).toLocaleString());
		}
		setList("jobList", jobList);
		return "success";
	}

	public String toAdd() throws Exception {
		setData("startTime", new Date().toLocaleString());
		return "edit";
	}

	public String cron() throws Exception {
		return "cron";
	}

	public String getJobs() throws Exception {
		List jobList = getDao().queryForList("tascheduler.getAllJobs");
		for (int i = 0; i < jobList.size(); i++) {
			DTO jobDto = (DTO) jobList.get(i);
			jobDto.put("st", new Timestamp(jobDto.getAsLong("st").longValue()).toLocaleString());
			if (jobDto.getAsLong("et").longValue() > 0L)
				jobDto.put("et", new Timestamp(jobDto.getAsLong("et").longValue()).toLocaleString());
			if (jobDto.getAsLong("pt").longValue() > 0L)
				jobDto.put("pt", new Timestamp(jobDto.getAsLong("pt").longValue()).toLocaleString());
			if (jobDto.getAsLong("nt").longValue() > 0L)
				jobDto.put("nt", new Timestamp(jobDto.getAsLong("nt").longValue()).toLocaleString());
		}
		setList("jobList", jobList);
		return JSON;
	}

	public String addJob() throws Exception {
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}
		ParamDTO dto = getDto();
		String jobName = dto.getAsString("jobName");
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
		String cronExpression = dto.getAsString("cronExpression");
		String jobData = dto.getAsString("jobData");
		String isPause = dto.getAsString("isPause");
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
										.withRepeatCount(repeatCount.intValue()).withMisfireHandlingInstructionNextWithExistingCount()).build();

			} else {

				trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroup).startAt(startTime).endAt(endTime)
						.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing()).build();
			}

			Class jobClass = Class.forName(className);

			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).withDescription(jobDesc).build();

			Map map = new HashMap();
			if (!jobData.equals("")) {
				map = (Map) JSonFactory.json2bean(jobData, HashMap.class);
			}
			map.put("isPause", isPause);
			jobDetail.getJobDataMap().putAll(map);

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception("没有找到类：" + className);
		} catch (ObjectAlreadyExistsException e1) {
			e1.printStackTrace();
			throw new Exception("任务：\"" + jobName + "\"已经存在\"");
		}
		return getJobs();
	}

	public String pauseJob() throws Exception {
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}
		scheduler.pauseTrigger(new TriggerKey(request.getParameter("name"), request.getParameter("group")));

		return getJobs();
	}

	public String stopJob() throws Exception {
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}

		scheduler.unscheduleJob(new TriggerKey(request.getParameter("name"), request.getParameter("group")));

		return getJobs();
	}

	public String resumeJob() throws Exception {
		if (scheduler.isShutdown()) {
			scheduler = ((Scheduler) getService("scheduler"));
		}
		scheduler.resumeTrigger(new TriggerKey(request.getParameter("name"), request.getParameter("group")));

		return getJobs();
	}
}
