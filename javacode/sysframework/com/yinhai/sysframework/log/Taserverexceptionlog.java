package com.yinhai.sysframework.log;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.yinhai.sysframework.app.domain.BaseDomain;
import com.yinhai.sysframework.util.TimestampAdapter;

public class Taserverexceptionlog extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4841625887874868449L;
	private String id;
	private String ipaddress;
	private String type;
	private byte[] content;
	private String contentStr;
	private Timestamp time;
	private String syspath;
	private String clientip;
	private String url;
	private String menuid;
	private String menuname;
	private String useragent;

	public Taserverexceptionlog() {
	}

	public Taserverexceptionlog(String ipaddress, String type, byte[] content, Timestamp time, String syspath) {
		this.ipaddress = ipaddress;
		this.type = type;
		this.content = content;
		this.time = time;
		this.syspath = syspath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getContentStr() {
		return contentStr;
	}

	public void setContentStr(String contentStr) {
		this.contentStr = contentStr;
	}

	@XmlJavaTypeAdapter(TimestampAdapter.class)
	public Timestamp getTime() {
		return time;
	}

	public void setTime(@XmlJavaTypeAdapter(TimestampAdapter.class) Timestamp time) {
		this.time = time;
	}

	public String getSyspath() {
		return syspath;
	}

	public void setSyspath(String syspath) {
		this.syspath = syspath;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMenuid() {
		return menuid;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public String getUseragent() {
		return useragent;
	}

	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}

	public Taserverexceptionlog(String id, String ipaddress, String type, byte[] content, String contentStr,
			Timestamp time, String syspath, String clientip, String url, String menuid, String menuname,
			String useragent) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.type = type;
		this.content = content;
		this.contentStr = contentStr;
		this.time = time;
		this.syspath = syspath;
		this.clientip = clientip;
		this.url = url;
		this.menuid = menuid;
		this.menuname = menuname;
		this.useragent = useragent;
	}
}
