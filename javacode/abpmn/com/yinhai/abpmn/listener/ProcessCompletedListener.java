package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 流程结束事件监听
 * @author wuxiaohui
 *
 */
public class ProcessCompletedListener implements EventHandler {

	private static Log logger = LogFactory.getLog(ProcessCompletedListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.debug("流程结束了。。。");
	}

}
