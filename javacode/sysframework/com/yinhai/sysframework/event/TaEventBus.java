package com.yinhai.sysframework.event;

import java.util.List;
import java.util.Vector;

public class TaEventBus implements ITaEventBus {

	private List<TaEventListener> listeners = new Vector<>();

	@Override
	public void addListener(TaEventListener listener) {
		if (listeners == null)
			listeners = new Vector<>();
		listeners.add(listener);
	}

	@Override
	public List<TaEventListener> getListeners() {
		return listeners;
	}

	@Override
	public void setListeners(List<TaEventListener> listeners) {
		this.listeners = listeners;
	}
}
