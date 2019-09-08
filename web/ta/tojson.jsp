<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@page import="com.yinhai.sysframework.util.TagUtil"%>
<%@page import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%
ResultBean rb = (ResultBean)TagUtil.getResultBean();
response.setContentType("text/json; charset=UTF-8"); //解决从服务器返回中文乱码问题
if(rb!=null){
	String restr = JSonFactory.bean2json(rb);
	out.print(restr);
}
out.flush();
%>