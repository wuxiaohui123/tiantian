<%@ page pageEncoding="UTF-8"%>
<%
response.addHeader("__forbidden","true");
out.println("对不起！您不够权限访问请求的资源！");
%>
