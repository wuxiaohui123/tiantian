<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>菜单查询</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;">
	<ta:pageloading/>
	<ta:box fit="true">
	</ta:box>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
});
</script>
<%@ include file="/ta/incfooter.jsp"%>