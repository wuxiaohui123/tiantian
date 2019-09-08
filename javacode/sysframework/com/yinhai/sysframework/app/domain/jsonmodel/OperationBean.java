package com.yinhai.sysframework.app.domain.jsonmodel;

import java.io.Serializable;

import org.springframework.util.Assert;

public class OperationBean implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String READONLY = "readonly";
	public static final String ENABLE = "enable";
	public static final String DISABLED = "disabled";
	public static final String ENABLE_TAB = "enable_tab";
	public static final String DISABLE_TAB = "disabled_tab";
	public static final String SELECT_TAB = "select_tab";
	public static final String HIDE = "hide";
	public static final String SHOW = "show";
	public static final String UNVISIBLE = "unvisible";
	public static final String RESETFORM = "resetform";
	public static final String REQUIRED = "required";
	public static final String DISREQUIRED = "disrequired";
	public String type;
	private String[] ids;
	private String[] params;

	public OperationBean() {
	}

	public OperationBean(String type, String ids) {
		Assert.notNull(ids, "ids不能空");
		this.type = type;

		this.ids = ids.split(",");
	}

	public OperationBean(String type, String ids, String params) {
		Assert.notNull(ids, "ids不能空");
		this.type = type;
		this.ids = ids.split(",");
		this.params = params.split(",");
	}

	public OperationBean(String type, String[] ids, String[] params) {
		this.type = type;
		this.ids = ids;
		this.params = params;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public static String arrayTojsArray(String[] a) {
		if (a == null)
			return "[]";
		StringBuilder str = new StringBuilder();
		str.append("[");
		for (int i = 0; i < a.length; i++) {
			if (i > 0) {
				str.append(",");
			}
			if (a[i] == null) {
				str.append("null");
			} else
				str.append("\"").append(a[i].replaceAll("\"", "\\\\\"")).append("\"");
		}
		str.append("]");
		return str.toString();
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"type\":\"").append(getType()).append("\",").append("\"ids\":[");

		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				if (i > 0)
					sb.append(",");
				sb.append("\"").append(ids[i]).append("\"");
			}
		}
		sb.append("],\"params\":[");
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				if (i > 0)
					sb.append(",");
				sb.append("\"").append(params[i]).append("\"");
			}
		}
		sb.append("]}");
		return sb.toString();
	}
}
