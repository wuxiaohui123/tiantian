<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.sysframework.util.IConstants"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String fixPath = "true".equals(AppManager.getSysConfig("true"))?"min.":"";
request.setAttribute("basePath", basePath);
request.setAttribute("path", path);
%>
<meta http-equiv="pragma" content="no-cache"/>
<meta http-equiv="cache-control" content="no-cache"/>
<meta http-equiv="expires" content="0"/>
<meta http-equiv="X-UA-Compatible" content="IE=8; IE=9; IE=10" />
<%if (AppManager.getSysConfig("developMode").equals("true")){%>
	<%@ include file="inc-req.jsp" %>
<%} else  {%>
    <%@ include file="inc-req.jsp" %>
<%} %>
<%@ include file="/appinc.jsp" %>
