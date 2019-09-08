<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>维度管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar"  style="padding:0px;margin:0px">
		<ta:pageloading/>
<ta:box height="100%" cols="3">
	<ta:box height="100%" columnWidth="0.425">
		<ta:text id="p_isdeveloper" display="false"></ta:text>
		<ta:text id="menuid" display="false"></ta:text>
		<ta:text id="positionid" display="false"></ta:text>
		<ta:panel fit="true" hasBorder="false" withToolBar="true" withButtonBar="true" key="可选数据区" cssStyle="margin-right:3px" id="yab139Panel1">
			<ta:panelToolBar cssStyle="height:37px;">
				<ta:checkbox key="可查看所有数据区(如果选择此项勾选表格无效)" id="allAccess" value="allAccess" onClick="fnClickCheckbox()"></ta:checkbox>
			</ta:panelToolBar>
			<ta:datagrid fit="true" id="yab003Grid"  selectType="checkbox" columnFilter="true">
				<ta:datagridItem id="codeValue" key="数据区码值" width="80"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称" width="130" showDetailed="true"></ta:datagridItem>
			</ta:datagrid>
			<ta:panelButtonBar>
				<div style="float:left;">如果不做任何选择将无法查询任何数据</div>
			</ta:panelButtonBar>
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
			<ta:datagrid id="yab003Grid2"  selectType="checkbox" columnFilter="true" fit="true">
				<ta:datagridItem id="codeValue" key="数据区码值"  width="80"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称"  width="130" showDetailed="true"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
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
		var menuid = Base.getValue("menuid");
		var positionid = Base.getValue("positionid");
		var allAccess = Base.getValue("allAccess");
		var sd = Base.getGridSelectedRows("yab003Grid");
		if(sd && sd.length >=1){
			Base.submit("yab003Grid","dataAccessDimensionManagementAction!save.do",{"dto['menuid']":menuid,"dto['positionid']":positionid,"dto['allAccess']":allAccess},null,null,function(){
				var datarow = {};
				for(var i = 0 ; i < sd.length ; i++){
					datarow.codeValue = sd[i].codeValue;
					datarow.codeDESC = sd[i].codeDESC;
					Base.addGridRow("yab003Grid2",datarow);
				}
				Base.deleteGridSelectedRows("yab003Grid");
				Base.msgTopTip("保存成功");
			});
		}else{
			Base.alert("请勾选数据区后再点击选择按钮","warn");
		}
	}
	function fnDelete(){
		var menuid = Base.getValue("menuid");
		var positionid = Base.getValue("positionid");
		var allAccess = Base.getValue("allAccess");
		if("allAccess" == allAccess){
			Base.msgTopTip("勾选'可查看所有数据区'后，再取消表格数据区无意义");
		}else{
			var sd = Base.getGridSelectedRows("yab003Grid2");
			if(sd && sd.length >=1){
				Base.submit("yab003Grid2","dataAccessDimensionManagementAction!removeYab139.do",{"dto['menuid']":menuid,"dto['positionid']":positionid},null,null,function(){
					var datarow = {};
					for(var i = 0 ; i < sd.length ; i++){
						datarow.codeValue = sd[i].codeValue;
						datarow.codeDESC = sd[i].codeDESC;
						Base.addGridRow("yab003Grid",datarow);
					}
					Base.deleteGridSelectedRows("yab003Grid2");
					Base.msgTopTip("保存成功");
				});
			}else{
				Base.alert("请勾选数据区后再点击取消按钮","warn");
			}
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