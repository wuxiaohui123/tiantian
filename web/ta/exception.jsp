<%@ page language="java" pageEncoding="UTF-8" errorPage="true"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>

<%

if("true".equals(AppManager.getSysConfig("developMode")))
	System.out.println("\n"+request.getAttribute("exceptionStack"));

Exception e = ((Exception)request.getAttribute("exception"));
String msg  ="抱歉！功能出现异常错误",errors = "";
Throwable a = null;
if(null != e){
	a = e.getCause();
}

if(null != a && null != a.getCause()){
	msg=a.getCause().getMessage();
}else{
	msg = e.toString();
	
}

if(null != msg){
	msg = msg.replaceAll("\"","'").replaceAll(":"," ").replaceAll( "<", "&lt;")
	.replaceAll( ">", "&gt;")
	.replaceAll( " ", "&nbsp;")
	.replaceAll( "\n", "").replaceAll( "\r", "");
} 
String detail = (request.getAttribute("exceptionStack")==null?"":(String)request.getAttribute("exceptionStack"));
detail = detail.replaceAll("\"","'").replaceAll(":"," ").replaceAll( "<", "&lt;")
.replaceAll( ">", "&gt;")
.replaceAll( " ", "&nbsp;")
.replaceAll( "\n", "<br>").replaceAll( "\r", "").replaceAll("	","");

response.sendError(417,"业务异常："+msg);
response.setContentType("text/json; charset=UTF-8");
session.setAttribute("_exceptionmsg","{\"success\":false,\"msg\":\""+msg+"\",\"errorDetail\":\""+detail+"\"}");
%>
