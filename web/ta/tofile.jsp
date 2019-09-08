<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.sysframework.util.TagUtil"%>
<%@page import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@page import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%
ResultBean rb = (ResultBean)TagUtil.getResultBean();
//response.setContentType("plainText; charset=UTF-8"); //解决从服务器返回中文乱码问题
response.setContentType("text/html; charset=utf-8");
response.setHeader("pragma", "no-cache");
response.setHeader("cache-control", "no-cache");
if(rb!=null){
	out.print(JSonFactory.bean2json(rb));
}
out.flush();
%>