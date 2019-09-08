<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
<head>
<title>YOURTITLE</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body style="margin:0px;padding:1px;" class="no-scrollbar">
	<div id='pageloading'></div>
	<ta:datagrid id="yab139Grid" fit="true" haveSn="true"
		forceFitColumns="true">
		<ta:datagridItem id="codeValue" key="数据区代码" />
		<ta:datagridItem id="codeDESC" key="数据区名称" />
	</ta:datagrid>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
</script>
<%@ include file="/ta/incfooter.jsp"%>