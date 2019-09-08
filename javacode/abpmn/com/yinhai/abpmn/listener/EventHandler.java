package com.yinhai.abpmn.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
/**
 * 事件处理接口
 * @author wuxiaohui
 *
 */
public interface EventHandler {

	void handle(ActivitiEvent event);
}
