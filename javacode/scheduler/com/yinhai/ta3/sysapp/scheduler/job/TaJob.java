package com.yinhai.ta3.sysapp.scheduler.job;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.core.io.ClassPathResource;

import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.json.JSonFactory;
import com.yinhai.ta3.sysapp.scheduler.service.JobLogService;
import com.yinhai.ta3.sysapp.scheduler.service.JobRMIService;

@DisallowConcurrentExecution
public class TaJob implements Job {

	private static Log logger = LogFactory.getLog(TaJob.class);

	protected JobLogService jobLogService = (JobLogService) ServiceLocator.getService("jobLogService");

	protected static String appCtxPath;

	public static String CONSTANTS_COMPLETED = "COMPLETED";

	public static String CONSTANTS_NOPAUSE = "0";
	public static String CONSTANTS_PAUSE = "1";
	public static String CONSTANTS_PAUSE_WS = "2";
	public static String CONSTANTS_PAUSE_BS = "3";

	static {
		InputStream is = null;
		try {
			ClassPathResource classPathResource = new ClassPathResource("quartz.properties");
			is = new BufferedInputStream(classPathResource.getInputStream());
			Properties props = new Properties();
			props.load(is);
			Iterator<Map.Entry<Object, Object>> it = props.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> m = (Map.Entry) it.next();
				if (m.getKey().equals("app_name"))
					appCtxPath = "/" + m.getValue();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}

	public String getServerAddr(JobExecutionContext context) {
		String jobkey = context.getJobDetail().getKey().toString();
		String addr = jobkey.substring(0, jobkey.lastIndexOf("."));
		return addr;
	}

	public String getServiceId(JobExecutionContext context) {
		String trikey = context.getTrigger().getKey().toString();
		String serviceId = trikey.substring(0, trikey.indexOf("."));
		return serviceId;
	}

	public void log(JobExecutionContext context, String isSuccess, String msg) {
		String jobkey = context.getJobDetail().getKey().toString();
		String addr = jobkey.substring(0, jobkey.lastIndexOf("."));
		String jobName = jobkey.substring(jobkey.lastIndexOf(".") + 1);
		String serviceId = getServiceId(context);
		Date firedTime = context.getFireTime();
		Map map = new HashMap();
		map.put("job_name", jobName);
		map.put("address", addr);
		map.put("service_id", serviceId);
		map.put("fired_time", firedTime);
		map.put("success", isSuccess);
		map.put("log_msg", msg);
		jobLogService.insertJobLog(map);
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("TaJob are running...");
		String serviceId = getServiceId(context);
		String addr = getServerAddr(context);
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(JobRMIService.class);
		factory.setAddress("http://" + addr + appCtxPath + "/services/jobRMIService?wsdl");

		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		String jsonData = "";
		String isPause = CONSTANTS_PAUSE;
		if (jobData != null) {
			isPause = jobData.getString("isPause");
			if ((isPause == null) || (isPause.equals("")))
				isPause = CONSTANTS_PAUSE;
			jsonData = JSonFactory.bean2json(jobData);
		}
		try {
			JobRMIService client = (JobRMIService) factory.create();

			Client proxy = ClientProxy.getClient(client);
			HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
			HTTPClientPolicy policy = new HTTPClientPolicy();

			conduit.setClient(policy);

			String result = client.rmi(serviceId, addr, jsonData);

			if (CONSTANTS_COMPLETED.equals(result)) {
				log(context, "1", "业务执行完毕");
				try {
					context.getScheduler().pauseTrigger(context.getTrigger().getKey());
				} catch (SchedulerException e1) {
					e1.printStackTrace();
				}
			} else if (result != null) {
				log(context, "0", result);
				if ((isPause.equals(CONSTANTS_PAUSE)) || (isPause.equals(CONSTANTS_PAUSE_BS))) {
					try {
						context.getScheduler().pauseTrigger(context.getTrigger().getKey());
					} catch (SchedulerException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				log(context, "1", "");
			}
		} catch (WebServiceException e) {
			e.printStackTrace();
			log(context, "0", e.getMessage());
			if ((isPause.equals(CONSTANTS_PAUSE)) || (isPause.equals(CONSTANTS_PAUSE_WS))) {
				try {
					context.getScheduler().pauseTrigger(context.getTrigger().getKey());
				} catch (SchedulerException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
