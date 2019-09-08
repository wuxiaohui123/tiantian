package com.yinhai.sysframework.util;

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {

	public String marshal(Timestamp arg0) {
		return DateUtil.datetimeToString(arg0, "yyyy-MM-dd HH:mm:ss");
	}

	public Timestamp unmarshal(String arg0) {
		return DateUtil.stringToSqlTimestamp(arg0);
	}
}
