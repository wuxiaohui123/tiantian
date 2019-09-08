<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>系统异常详细信息</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" >
		<ta:pageloading/>
		<ta:panel id="pnlMain">
			<ta:text id="type" key="异常类型" readOnly="true"></ta:text>
			<ta:textarea key="详细信息" id="detail" height="400px" readOnly="true">
			</ta:textarea>
		</ta:panel>	
	</body>
</html>
<script type="text/javascript">
	
	
	$(document).ready(function () {
		$("body").taLayout();
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>