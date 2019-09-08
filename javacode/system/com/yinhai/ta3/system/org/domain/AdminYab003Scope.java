package com.yinhai.ta3.system.org.domain;

import java.io.Serializable;

public class AdminYab003Scope implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2822453580541022866L;
	private AdminYab003ScopeId id;

	public AdminYab003Scope() {
	}

	public AdminYab003Scope(AdminYab003ScopeId id) {
		this.id = id;
	}

	public AdminYab003ScopeId getId() {
		return id;
	}

	public void setId(AdminYab003ScopeId id) {
		this.id = id;
	}
}
