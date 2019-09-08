<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="java.util.*"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<head>
<title>异常界面</title>
<style>
#info {
	height: 300px;
	width: 600px;
	position: absolute;
	top: 50px;
	box-shadow: 10px 10px 20px #848484;
	background-color: #fff;
}
#exception_icon {
	position: relative;
	width: 128px;
	height: 128px;
	top: 15px;
	left: 236px;
}
#warning {
	position: relative;
	font-size: 18px;
	font-weight: bold;
	font-family: 'Microsoft Yahei', verdana;
	font-style: italic;
	top: 15px;
	left: 20px;
}
#warning-info {
	position: relative;
	font-size: 22px;
	font-weight: bold;
	font-family: 'Microsoft Yahei', verdana;
	font-style: italic;
	width: 600px;
	text-align: center;
	top: 25px;
}
#goback {
	position: relative;
	font-size: 16px;
	font-family: 'Microsoft Yahei', verdana;
	font-style: italic;
	width: 600px;
	text-align: center;
	top: 55px;
	cursor: pointer;
}
</style>
<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/jquery-1.11.0.min.js" type="text/javascript"></script>
</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;width:100%;height:100%;background-color:#EAEAEA">
	<div id="info">
		<div id="warning">警告</div>
		<img id="exception_icon" src="<%=basePath%>ta/resource/themes/exception.png"/>
		<div id="warning-info">页面访问出现未知异常，请刷新后重试或者联系管理员!</div>
		<div id="goback" onclick="gotoIndex()">&lt;&lt;返回首页</div>
	</div>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		var w = parseInt($(window).width(),10)/2;
		$("#info").css("left",w-300);
	});
	function gotoIndex(){
		window.location.href = "<%=request.getContextPath()%>/indexAction.do";
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
