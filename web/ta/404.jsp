<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>您访问的页面不存在</title>
</head>
<body>
	<div style="width:100%;text-align: center">
		<table style="border:solid 1px #ccc;width:580px" align="center">
			<tr>
				<td colspan="2" style="background-color: #aaa;border-bottom: solid 1px #ccc;height:36px;color:white;font-weight: bold;">消息提示</td>
			</tr>
			<tr>
				<td valign="top" width="200"><img src="<%=basePath %>/ta/themes/base/imgview/page-alert.png" alt="警告"/></td>
				<td width="*">
					<h2 style="color:red">您访问的页面不存在!</h2>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<h3>出错代码：404</h3>
					错误发生页面是：<%=basePath %>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>