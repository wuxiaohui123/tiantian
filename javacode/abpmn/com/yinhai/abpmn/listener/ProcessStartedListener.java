package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessStartedListener implements EventHandler {

	private static Log logger = LogFactory.getLog(ProcessStartedListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.debug("流程开始了。。。");

	}

}
