package com.yinhai.sysframework.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.yinhai.sysframework.app.domain.VO;

public class ReportData implements Serializable {

	private Map params;
	private Collection ds;
	private String reportName;

	public ReportData(String reportName, Map params, Collection ds) {
		this.ds = ds;
		this.params = params;
		this.reportName = reportName;
	}

	public ReportData(String reportName, Map params, VO vo) {
		ds = new ArrayList();
		ds.add(vo);
		this.params = params;
		this.reportName = reportName;
	}

	public ReportData(String reportName, Map params, Map map) {
		ds = new ArrayList();
		ds.add(map);
		this.params = params;
		this.reportName = reportName;
	}

	public Collection getDs() {
		return ds;
	}

	public void setDs(Collection ds) {
		this.ds = ds;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public void addParams(String key, Object value) {
		params.put(key, value);
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
}
