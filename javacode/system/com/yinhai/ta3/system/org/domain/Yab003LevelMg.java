package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class Yab003LevelMg implements Serializable {

	private Yab003LevelMgId id;

	public Yab003LevelMg() {
	}

	public Yab003LevelMg(Yab003LevelMgId id) {
		this.id = id;
	}

	public Yab003LevelMgId getId() {
		return id;
	}

	public void setId(Yab003LevelMgId id) {
		this.id = id;
	}
}
