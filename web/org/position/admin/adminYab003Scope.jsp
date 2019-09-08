<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true"  withButtonBar="true">
<ta:text id="positionid" display="false"></ta:text>
	<ta:datagrid fit="true" haveSn="true" id="yab003Grid" selectType="checkbox" forceFitColumns="true" columnFilter="true">
		<ta:datagridItem id="codeValue" key="数据区代码值" width="120" asKey="true"></ta:datagridItem>
		<ta:datagridItem id="codeDESC" key="数据区名称" width="200"></ta:datagridItem>
	</ta:datagrid>
	<ta:panelButtonBar>
		<ta:button id="btnSave" key="保存" icon="icon-add1" onClick="fnSaveAdminYab003Scope()" isok="true"></ta:button>
		<ta:button id="btnClose" key="关闭" icon="icon-no" onClick="Base.closeWindow('adminYab003Win')"></ta:button>
	</ta:panelButtonBar>
</ta:panel>
<script>
	function fnSaveAdminYab003Scope(){
		var d = Base.getGridSelectedRows("yab003Grid");
		if(d && d.length > 0){
			Base.submit("yab003Grid","adminMgAction!saveAdminYab003Scope.do",{"dto['positionid']":Base.getValue("positionid")},null,null,function(){
				Base.msgTopTip("<div class='msgTopTip'>分中心管理范围保存成功</div>");
				Base.closeWindow("adminYab003Win");
			});
		}else{
			Base.msgTopTip("<div class='msgTopTip'>请选择数据后再保存</div>");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>