<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>功能访问日志</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
		<ta:pageloading/>
		<ta:box cols="3" id="box1">
			<ta:date id="startDate" key="开始时间" showSelectPanel="true"></ta:date>
			<ta:date id="endDate" key="结束时间" showSelectPanel="true"></ta:date>
			<ta:buttonLayout align="left">
				<ta:button key="查询" onClick="fnQuery()"></ta:button>
			</ta:buttonLayout>
		</ta:box>
		<ta:panel key="功能访问日志" fit="true" hasBorder="false" titleAlign="left">
			<ta:datagrid id="accessGrid"  fit="true" haveSn="true"  columnFilter="true" forceFitColumns="true" >
				<ta:datagridItem key="姓名" id="name"  width="100" />
				<ta:datagridItem key="岗位路径" id="positionnamepath" sortable="true" showDetailed="true" width="300"/>
				<ta:datagridItem key="菜单路径" id="menunamepath"  sortable="true" showDetailed="true" width="300"/>
				<ta:datagridItem key="时间" id="accesstime" sortable="true" width="150"/>
				<ta:datagridItem key="系统" id="sysflag"/>
				<ta:dataGridToolPaging url="accessLogAction!queryAccess.do" pageSize="1000" submitIds="box1" selectExpButtons="1,2"></ta:dataGridToolPaging>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnQuery(){
		Base.submit("box1","accessLogAction!queryAccess.do");
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>