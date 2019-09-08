package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 流程启动事件监听类
 * @author wuxiaohui
 *
 */
public class EngineCreateListener implements EventHandler{

	private static Log logger = LogFactory.getLog(EngineCreateListener.class);
	@Override
	public void handle(ActivitiEvent event) {
		logger.info("流程引擎启动成功...");
	}

}
