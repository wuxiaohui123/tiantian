<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>查看岗位</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:240}" style="padding:0px;margin:0px">
		<ta:datagrid id="positions" fit="true" haveSn="true" forceFitColumns="true">
				<ta:datagridItem id="positionname" key="岗位名称" formatter="fnNameFormatter" showDetailed="true" width="100"/>
				<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true" width="300"/>
				<ta:datagridItem id="positiontype" key="岗位类型" width="150" collection="positiontype" showDetailed="true"/>
		</ta:datagrid>
	</body>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	Base.focus("name");
})
function fnNameFormatter(row, cell, value, columnDef, dataContext) {
	if (dataContext.effective == 1) {
		return "<span style='color:red;text-decoration:line-through;';>" + value + "</span>";
	}
	if (dataContext.islock == 1) {
		return "<span style='color:yellow;text-decoration:line-through;'>" + value + "</span>";
	}
	if (dataContext.positiontype == 1) {
		return "<span style='color:red'>" + value + "</span>";
	}
	return value;
}
</script>
<%@ include file="/ta/incfooter.jsp"%>