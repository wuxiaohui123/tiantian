<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false"%>
<jsp:directive.page import="com.yinhai.sysframework.util.IConstants"/>
<%
String ipport = (String)application.getAttribute(IConstants.USE_REAL_SERVER);
if(ipport ==null){
	ipport = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
	application.setAttribute(IConstants.USE_REAL_SERVER,ipport);
}
%>
