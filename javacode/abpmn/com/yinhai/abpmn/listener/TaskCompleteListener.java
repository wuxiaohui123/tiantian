package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskCompleteListener implements EventHandler {

	private static Log logger = LogFactory.getLog(TaskCompleteListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.debug("任务完成了");

	}

}
