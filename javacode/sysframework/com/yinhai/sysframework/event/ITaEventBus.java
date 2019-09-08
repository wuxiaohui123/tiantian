package com.yinhai.sysframework.event;

import java.util.List;

public interface ITaEventBus {

	String SERVICE_KEY = "taEventBus";

	 void setListeners(List<TaEventListener> listeners);

	 List<TaEventListener> getListeners();

	 void addListener(TaEventListener taEventListener);
}
