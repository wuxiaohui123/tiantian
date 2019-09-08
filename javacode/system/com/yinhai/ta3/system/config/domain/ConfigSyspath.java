package com.yinhai.ta3.system.config.domain;

import com.yinhai.sysframework.config.IConfigSyspath;

public class ConfigSyspath implements IConfigSyspath {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2514357822933392408L;
	private Long serialid;
	private String id;
	private String name;
	private String url;
	private String py;
	private String cursystem;

	public ConfigSyspath() {
	}

	public ConfigSyspath(String id, String name, String url, String cursystem) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.cursystem = cursystem;
	}

	public ConfigSyspath(String id, String name, String url, String py, String cursystem) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.py = py;
		this.cursystem = cursystem;
	}

	public Long getSerialid() {
		return serialid;
	}

	public void setSerialid(Long serialid) {
		this.serialid = serialid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPy() {
		return py;
	}

	public void setPy(String py) {
		this.py = py;
	}

	public String getCursystem() {
		return cursystem;
	}

	public void setCursystem(String cursystem) {
		this.cursystem = cursystem;
	}

}
