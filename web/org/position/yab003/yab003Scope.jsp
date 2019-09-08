<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>经办机构上下级管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar"  style="padding:0px;margin:0px">
		<ta:pageloading/>
<ta:box height="100%" cols="2">
	<ta:text id="yab003" display="false"></ta:text>
	<ta:box height="100%" >
		<ta:panel fit="true" hasBorder="false" key="可选数据区"  id="yab139Panel1">
			<ta:datagrid fit="true" id="yab003Grids"  selectType="checkbox" columnFilter="true">
				<ta:datagridItem id="codeValue" key="数据区码值" width="80"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称" width="130" showDetailed="true"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
	</ta:box>
	<ta:box height="100%" >
		<ta:tree id="yab003Tree" async="false" editable="true" ></ta:tree>
	</ta:box>
</ta:box>
</body>
</html>
<script>
$(document).ready(function () {
	$("body").taLayout();
	Ta.autoPercentHeight();
});
	function fnSave(){
		var sd = Base.getGridSelectedRows("yab139Grid");
		if(sd && sd.length >=1){
			Base.submit("yab139Grid","yab003MgAction!saveYab139.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
				var datarow = {};
				for(var i = 0 ; i < sd.length ; i++){
					datarow.codeValue = sd[i].codeValue;
					datarow.codeDESC = sd[i].codeDESC;
					Base.addGridRow("yab139Grid2",datarow);
				}
				Base.deleteGridSelectedRows("yab139Grid");
				Base.msgTopTip("保存成功");
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
	function fnClickCheckbox(){
		var menuid = Base.getValue("menuid");
		var positionid = Base.getValue("positionid");
		var allAccess = Base.getValue("allAccess");
		var sd = Base.getGridData("yab003Grid");
		var sd2 = Base.getGridData("yab003Grid2");
		Base.submit("yab003Grid","dataAccessDimensionManagementAction!saveAll.do",{"dto['menuid']":menuid,"dto['positionid']":positionid,"dto['allAccess']":allAccess},null,null,function(){
			var datarow = {};
			if("allAccess" == allAccess){
				if(sd && sd.length >=1){
					for(var i = 0 ; i < sd.length ; i++){
						datarow.codeValue = sd[i].codeValue;
						datarow.codeDESC = sd[i].codeDESC;
						Base.addGridRow("yab003Grid2",datarow);
					}
					Base.clearGridData("yab003Grid");
				}
			}else{
				if(sd2 && sd2.length >=1){
					for(var j = 0 ; j < sd2.length ; j++){
						datarow.codeValue = sd2[j].codeValue;
						datarow.codeDESC = sd2[j].codeDESC;
						Base.addGridRow("yab003Grid",datarow);
					}
					Base.clearGridData("yab003Grid2");
				}
			}
			Base.msgTopTip("保存成功");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>