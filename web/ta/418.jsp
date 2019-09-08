<%@ page language="java" pageEncoding="UTF-8" errorPage="true"%>
<%
response.setContentType("text/json; charset=UTF-8");
response.addHeader("__exception","1");
out.print(session.getAttribute("_appexceptionmsg"));
session.removeAttribute("_appexceptionmsg");
%>
