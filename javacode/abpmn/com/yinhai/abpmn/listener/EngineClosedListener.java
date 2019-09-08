package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EngineClosedListener implements EventHandler {

	private static Log logger = LogFactory.getLog(EngineClosedListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.debug("流程引擎关闭成功...");
	}

}
