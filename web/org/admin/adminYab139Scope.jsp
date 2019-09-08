<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>可管理的数据区范围</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />

<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true" withButtonBar="true">
	<ta:text id="positionid" display="false"></ta:text> 
	<ta:text id="userid" display="false"></ta:text> 
	<ta:datagrid fit="true" haveSn="true" id="yab003Grid" selectType="checkbox" forceFitColumns="true" columnFilter="true">
		<ta:datagridItem id="codeValue" key="数据区代码值" width="120" asKey="true"></ta:datagridItem>
		<ta:datagridItem id="codeDESC" key="数据区名称" width="200"></ta:datagridItem>
	</ta:datagrid>
	<ta:panelButtonBar align="right">
		<ta:button id="btnSave" key="保存[S]" hotKey="S" icon="icon-add1" onClick="fnSaveAdminYab003Scope()" isok="true"></ta:button>
		<ta:button id="btnClose" key="关闭[X]" hotKey="X" icon="icon-no" onClick="parent.Base.closeWindow('adminYab003Win')"></ta:button>
	</ta:panelButtonBar>
</ta:panel>
</body>
</html> 
<script>
$(document).ready(function() {
		$("body").taLayout();
		queryInfo();
	}) 
	
	function queryInfo() {
		Base.submit("", "adminUserMgAction!queryTargetUserYab003Scope2.do", {
			"dto['positionid']" : Base.getValue("positionid"),
			"dto['userid']" : Base.getValue("userid")
		}, null, null, function(list) { 
			Base.setSelectRowsByData("yab003Grid", list.fieldData.dlist);
		});
	}
	function fnSaveAdminYab003Scope() {

		var d = Base.getGridSelectedRows("yab003Grid");
		if (d && d.length > 0) {
			Base.submit("yab003Grid", "adminUserMgAction!saveAdminYab003Scope.do", {
				"dto['positionid']" : Base.getValue("positionid")
			}, null, null, function() {
				parent.Base.msgTopTip("<div style='width:180px;height:100px;margin:0 auto;font-size:14px;text-align:center;'>数据区管理范围保存成功!<div>");
				parent.refreshGrid4();
				parent.Base.closeWindow("adminYab003Win");
			}); 
		} else {
			parent.Base.msgTopTip("<div style='width:150px;height:100px;margin:0 auto;font-size:14px;text-align:center;'>请选择数据后再保存</div>");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>