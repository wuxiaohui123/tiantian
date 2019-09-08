<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
		<ta:panel fit="true" hasBorder="false" withToolBar="true" withButtonBar="true">
			<ta:panelToolBar cssStyle="height:37px;">
				<ta:checkbox key="可查看所有数据区（如果选择此项勾选表格将无效）" id="p_allAccess" value="allAccess"></ta:checkbox>
			</ta:panelToolBar>
			<ta:datagrid fit="true" id="yab003Grid" forceFitColumns="true" selectType="checkbox" columnFilter="true">
				<ta:datagridItem id="codeValue" key="数据区码值" asKey="true"></ta:datagridItem>
				<ta:datagridItem id="codeDESC" key="数据区名称"></ta:datagridItem>
			</ta:datagrid>
			<ta:panelButtonBar>
				<div style="float:left;">如果不做任何选择将无法查询任何数据</div>
				<ta:button key="保存" isok="true" onClick="fnSave()" id="btnSave"></ta:button>
				<ta:button key="返回"  onClick="Base.closeWindow('win1')"></ta:button>
			</ta:panelButtonBar>
		</ta:panel>
		<ta:text id="p_isdeveloper" display="false"></ta:text>
<script>
	function fnSave(){
		var positionid = Base.getValue("p_positionid");
		var allAccess = Base.getValue("p_allAccess")
		var d = Base.getGridSelectedRows("yab003Grid");
		var tree = Base.getObj("menuTree1");
		var treeNodes = tree.getCheckedNodes(true);
		var menustr = "";
		for (var i = 0; i < treeNodes.length; i++) {
			menustr += "{\"id\":\"" + treeNodes[i].id + "\"},";
		}
		if (menustr != "") {
			menustr = "[" + menustr.substr(0, menustr.length - 1) + "]";
		}
		if(allAccess == null && d && d.length < 1){
			Base.confirm("确定取消所有数据权限？",function(yes){
				if(yes){
					Base.submit("yab003Grid","dataAccessDimensionManagementAction!saveAccess.do",{"dto['positionid']":positionid,"dto['allAccess']":allAccess,"menustr":menustr},null,null,function(){
						Base.msgTopTip("保存成功");
						Base.closeWindow('win1');
					});
				}else{
					return;
				}
			});
		}else{
			Base.submit("yab003Grid","dataAccessDimensionManagementAction!saveAccess.do",{"dto['positionid']":positionid,"dto['allAccess']":allAccess,"menustr":menustr},null,null,function(){
				Base.msgTopTip("保存成功");
				Base.closeWindow('win1');
			});
		}
	}
	
</script>
<%@ include file="/ta/incfooter.jsp"%>