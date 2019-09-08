package com.yinhai.sysframework.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Ta3DateAdapter extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public String marshal(Date v) {
		return dateFormat.format(v);
	}

	public Date unmarshal(String v) throws Exception {
		return dateFormat.parse(v);
	}
}
