package com.yinhai.abpmn.listener;

import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

import com.yinhai.sysframework.service.ServiceLocator;
import com.yinhai.sysframework.util.StringUtil;
/**
 * 全局事件监听类
 * @author wuxiaohui
 *
 */
public class GlobalEventListener implements ActivitiEventListener {

	private Map<String, String> eventHandlers;

	@Override
	public boolean isFailOnException() {
		return false;
	}

	@Override
	public void onEvent(ActivitiEvent event) {
		String beanId = eventHandlers.get(event.getType().name());
		if(StringUtil.isNotEmpty(beanId)){
			EventHandler eventHandler = ServiceLocator.getService(beanId, EventHandler.class);
			eventHandler.handle(event);
		}
	}

	public Map<String, String> getEventHandlers() {
		return eventHandlers;
	}

	public void setEventHandlers(Map<String, String> eventHandlers) {
		this.eventHandlers = eventHandlers;
	}
}
