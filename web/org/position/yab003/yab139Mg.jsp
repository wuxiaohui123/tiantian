<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>经办机构数据区管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar"  style="padding:0px;margin:0px">
		<ta:pageloading/>
<%-- <ta:box height="100%" cols="3">
	<ta:text id="yab003" display="false"></ta:text>
	<ta:box height="100%" columnWidth="0.425">
		<ta:panel fit="true" hasBorder="false" key="可选数据区" cssStyle="margin-right:3px" id="yab139Panel1">
			<ta:datagrid fit="true" id="yab139Grid"  selectType="checkbox" columnFilter="true">
				<ta:datagridItem id="codeValue" key="数据区码值" width="80"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称" width="130" showDetailed="true"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
	</ta:box>
	<ta:box height="100%"  columnWidth="0.15" cssStyle="background:#e1e1e1">
		<ta:box height="20%"></ta:box>
		<ta:box height="20%">
			<ta:buttonLayout>
				<ta:button key="选择" icon="xui-icon-next" isShowIcon="true"  onClick="fnSave()"></ta:button>
			</ta:buttonLayout>
		</ta:box>
		<ta:box height="20%">
			<ta:buttonLayout>
				<ta:button key="取消"  icon="xui-icon-back" isShowIcon="true" onClick="fnDelete()"></ta:button>
			</ta:buttonLayout>
		</ta:box>
		<ta:box height="20%">
			<ta:buttonLayout>
				<ta:button key="关闭"  onClick="parent.Base.closeWindow('win')"></ta:button>
			</ta:buttonLayout>
		</ta:box>
		<ta:box height="20%">
		</ta:box>
	</ta:box>
	<ta:box height="100%"  columnWidth="0.425">
		<ta:panel key="已选数据区" hasBorder="false" id="yab139Panel2" fit="true">
			<ta:datagrid id="yab139Grid2"  selectType="checkbox" columnFilter="true" fit="true">
				<ta:datagridItem id="codeValue" key="数据区码值"  width="80"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称"  width="130" showDetailed="true"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
	</ta:box>
</ta:box> --%>
<ta:box fit="true">
	<ta:text id="yab003" display="false"></ta:text>
	<ta:panel fit="true" hasBorder="false" key="可选数据区"  id="yab139Panel1" withToolBar="true">
		<ta:panelToolBar>
			<ta:button asToolBarItem="true" key="确定" isok="true" onClick="fnSave()"></ta:button>
		</ta:panelToolBar>
		<ta:datagrid fit="true" id="yab139Grid"  selectType="checkbox" columnFilter="true" forceFitColumns="true">
			<ta:datagridItem id="codeValue" key="数据区码值" width="80"></ta:datagridItem>
			<ta:datagridItem id="codeDESC" key="数据区名称" width="130" showDetailed="true"></ta:datagridItem>
		</ta:datagrid>
	</ta:panel>
</ta:box>
</body>
</html>
<script>
$(document).ready(function () {
	$("body").taLayout();
// 	Ta.autoPercentHeight();
});
	function fnSave(){
		var sd = Base.getGridSelectedRows("yab139Grid");
		if(sd && sd.length >=1){
			Base.submit("yab139Grid","yab003MgAction!saveYab139.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
				Base.msgTopTip("保存成功");
				parent.Base.closeWindow("win");
			});
		}else{
			Base.alert("请勾选数据区后再点击选择按钮","warn");
		}
	}
	function fnDelete(){
		var sd = Base.getGridSelectedRows("yab139Grid2");
		if(sd && sd.length >=1){
			Base.submit("yab139Grid2","yab003MgAction!removeYab139.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
				var datarow = {};
				for(var i = 0 ; i < sd.length ; i++){
					datarow.codeValue = sd[i].codeValue;
					datarow.codeDESC = sd[i].codeDESC;
					Base.addGridRow("yab139Grid",datarow);
				}
				Base.deleteGridSelectedRows("yab139Grid2");
				Base.msgTopTip("保存成功");
			});
		}else{
			Base.alert("请勾选数据区后再点击取消按钮","warn");
		}
	}
	
</script>
<%@ include file="/ta/incfooter.jsp"%>