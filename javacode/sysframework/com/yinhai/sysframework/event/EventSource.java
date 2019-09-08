package com.yinhai.sysframework.event;

import java.io.Serializable;

import com.yinhai.sysframework.dto.ParamDTO;

public class EventSource implements Serializable {

	private Object target;
	private ParamDTO dto;

	public EventSource(Object target) {
		this.target = target;
	}

	public EventSource(Object target, ParamDTO dto) {
		this.target = target;
		this.dto = dto;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public ParamDTO getDto() {
		return dto;
	}

	public void setDto(ParamDTO dto) {
		this.dto = dto;
	}
}
