<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@ page language="java" pageEncoding="UTF-8" errorPage="true"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<%@page import="com.yinhai.sysframework.exception.AppException"%>
<%@page import="com.yinhai.sysframework.exception.PrcException"%>
<%@page import="com.yinhai.sysframework.exception.IllegalInputAppException"%>
<%@page import="java.util.List"%>

<%
Exception e = (Exception)request.getAttribute("exception");
String msg  ="",errors = "";

if(e instanceof PrcException){
	
	msg =  reStringToHtml(((PrcException)e).getShortMsg());
	
}else if(e instanceof IllegalInputAppException){
	IllegalInputAppException ia = (IllegalInputAppException)e;
	msg = reStringToHtml(ia.getMessage());
	
	List<AppException> list = ia.getExceptions();
	
	errors = ",\"validateErrors\":{";

	for(int i=0; list!=null && i<list.size();i++){
		AppException ae = list.get(i);
		String aeFieldName = reStringToHtml(ae.getFieldName());
		String aeMsg = reStringToHtml(ae.getMessage());
		if(i==0){
			errors += (aeFieldName)==null?" ":"\""+ aeFieldName +"\"" + ":\""+aeMsg+"\"";
		}else{
			errors += ","+aeFieldName==null?" ":"\""+aeFieldName+"\"" + ":\""+aeMsg+"\"";	
		}
	}
	errors += "}";
} else if(e instanceof AppException){
	
	AppException ae = (AppException)e;
	msg = reStringToHtml(ae.getMessage());
	if(ae.getFieldName()!=null){
		errors = ",\"validateErrors\":{\""+reStringToHtml(ae.getFieldName())+"\" :\""+msg+"\"}";
	}
}else{
	msg=reStringToHtml(e.getMessage());
}
if("true".equals(AppManager.getSysConfig("developMode")))
	System.out.println("\n"+request.getAttribute("exceptionStack"));

response.setContentType("text/json; charset=UTF-8");

String detail = (request.getAttribute("exceptionStack")==null?"":(String)request.getAttribute("exceptionStack"));
detail = reStringToHtml(detail);

session.setAttribute("_appexceptionmsg","{\"success\":false,\"msg\":\""+msg+"\""+errors+",\"errorDetail\":\""+detail+"\"}");
response.sendError(418,msg);

%>

<%!
	public String reStringToHtml(String string) {
		 if(string != null) {
			string = StringUtils.replace(string, "\"", "'");
			string = StringUtils.replace(string, "<", "&lt;");
			string = StringUtils.replace(string, ">", "&gt;");
			string = StringUtils.replace(string, " ", "&nbsp;");
			string = StringUtils.replace(string, "\n", "<br>");
			string = StringUtils.replace(string, "\r", "");
		 }
		 return string;
	}
%>
