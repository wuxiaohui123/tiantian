package com.yinhai.sysframework.security;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

public class OnlineSessionInfo implements Serializable {

	private HttpSession session;
	private String loginId;
	private String useRealServer;
	private String clientIp;
	private String clientPort;
	private String clientExplorer;
	private String clientSystem;

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getClientExplorer() {
		return clientExplorer;
	}

	public void setClientExplorer(String clientExplorer) {
		this.clientExplorer = clientExplorer;
	}

	public String getClientSystem() {
		return clientSystem;
	}

	public void setClientSystem(String clientSystem) {
		this.clientSystem = clientSystem;
	}

	public String getUseRealServer() {
		return useRealServer;
	}

	public void setUseRealServer(String useRealServer) {
		this.useRealServer = useRealServer;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
}
