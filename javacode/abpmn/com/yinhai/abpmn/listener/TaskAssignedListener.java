package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskAssignedListener implements EventHandler {

	private static Log logger = LogFactory.getLog(TaskAssignedListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.debug("任务签收了");

	}

}
