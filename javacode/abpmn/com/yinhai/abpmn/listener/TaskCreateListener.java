package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author wuxiaohui
 * 
 */
public class TaskCreateListener implements EventHandler {

	private static Log logger = LogFactory.getLog(TaskCreateListener.class);

	@Override
	public void handle(ActivitiEvent event) {
		ActivitiEntityEventImpl eventImpl = (ActivitiEntityEventImpl) event;
		TaskEntity taskEntity = (TaskEntity) eventImpl.getEntity();
		logger.debug("create task is " + taskEntity.getName() + " key is:" + taskEntity.getTaskDefinitionKey());
		logger.debug("enter the task create listener ---->" + event.getType().name());
	}

}
