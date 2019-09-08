package com.yinhai.sysframework.app.domain.jsonmodel;

import java.io.Serializable;

public class TopMsg implements Serializable {
	private String topMsg;
	private int time;
	private int width;
	private int height;

	public TopMsg(String topMsg) {
		this.topMsg = topMsg;
		time = 2000;
		width = 250;
		height = 50;
	}

	public TopMsg(String topMsg, int time, int width, int height) {
		this.topMsg = topMsg;
		if (time <= 0) {
			this.time = 2000;
		} else {
			this.time = time;
		}
		if (width <= 0) {
			this.width = 250;
		} else {
			this.width = width;
		}
		if (height <= 0) {
			this.height = 50;
		} else {
			this.height = height;
		}
	}

	public String getTopMsg() {
		return topMsg;
	}

	public void setTopMsg(String topMsg) {
		this.topMsg = topMsg;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
