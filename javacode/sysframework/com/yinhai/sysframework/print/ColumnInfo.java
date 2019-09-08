package com.yinhai.sysframework.print;

import java.io.Serializable;

public class ColumnInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2977135708293307514L;
	private String fieldName;
	private String columnName;
	private String titlecomment;
	private String format;
	private int width = 20;
	private boolean showCode = true;

	public ColumnInfo() {
	}

	public ColumnInfo(String fieldName, String columnName, String titlecomment) {
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.titlecomment = titlecomment;
	}

	public ColumnInfo(String fieldName, String columnName, String titlecomment, String format, boolean showCode) {
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.titlecomment = titlecomment;
		this.format = format;
		this.showCode = showCode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getTitlecomment() {
		return titlecomment;
	}

	public void setTitlecomment(String titlecomment) {
		this.titlecomment = titlecomment;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isShowCode() {
		return showCode;
	}

	public void setShowCode(boolean showCode) {
		this.showCode = showCode;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
