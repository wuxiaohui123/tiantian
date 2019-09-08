<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>岗位</title>
		<%@ include file="/ta/inc.jsp"%>
		 <script src="<%=basePath%>ta/resource/external/plugin/ta-layout-all/autoPercentHeight.js" type="text/javascript" ></script>
	</head>
	<body id="body1" class="no-scrollbar">
		<ta:pageloading/>
		<ta:box height="100%" cols="3">
			<ta:box height="100%" columnWidth="0.2" cssStyle="border:1px solid #99BBE8;margin-right:2px;overflow:auto">
				<ta:tree id="orgTree" asyncUrl="positionSettingMgAction!webQueryAsyncOrgTree.do" fontCss="fnFontCss" beforeClick="fnBeforeClick" childKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
				 async="true" onClick="fnOrgTreeClick" showLine="true"/>
			</ta:box>
			<ta:box height="100%" columnWidth="0.4" cssStyle="margin-right:2px">
				<ta:panel fit="true" withToolBar="true" key="人员列表" >
					<ta:panelToolBar cssStyle="height:40px;">
						<ta:button key="添加" asToolBarItem="true" icon="icon-add" onClick="fnAddPosition()" cssStyle="margin:6px 3px;float:left;"/>
						<ta:box cssStyle="float:left">
							<ta:checkbox key="显示子部门人员" id="isChildren" value="isChildren" onClick="fnOrgTreeClick1(this)"/>
						</ta:box>
					</ta:panelToolBar>
					<ta:datagrid fit="true" id="userGrid" haveSn="true" selectType="checkbox" columnFilter="true">
						<ta:datagridItem id="name" key="姓名" align="center" width="100"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所在部门路径" align="center"  width="300" showDetailed="true"></ta:datagridItem>
					</ta:datagrid>
				</ta:panel>
			</ta:box>
			<ta:box height="100%" columnWidth="0.4">
				<ta:panel key="已选人员" fit="true" withToolBar="true" position="center">
					<ta:panelToolBar cssStyle="height:40px;">
						<ta:button key="删除" asToolBarItem="true" icon="icon-remove" onClick="fnDeletePosition()" cssStyle="margin:6px 3px;"/>
					</ta:panelToolBar>
					<ta:datagrid fit="true" id="selectedUserGrid" haveSn="true" selectType="checkbox"  columnFilter="true" >
						<ta:datagridItem id="name" key="姓名" align="center"  width="100"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所在部门路径" align="center"  width="300" showDetailed="true"></ta:datagridItem>
					</ta:datagrid>
				</ta:panel>
			</ta:box>
		</ta:box>
		<ta:text id="positionid" display="false"/>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		Ta.autoPercentHeight();
// 		var treeObj = $.fn.zTree.getZTreeObj("orgTree");
// 		var nodes = treeObj.getNodes();
// 		if (nodes.length>0) {//默认选中根节点
// 			treeObj.selectNode(nodes[0]);
// 		}
		//默认以根节点进行查询
// 		fnOrgTreeClick1($("#isChildren").get(0));
	});
	function fnOrgTreeClick(e, tree, treeNode){
		var data = Base.getGridData("selectedUserGrid");
		var str = ""
		if(data && data.length > 0){
			for(var i = 0 ; i < data.length ; i++){
				str += "{\"userid\":"+data[i].userid+"},";
			}
		}
		str = "[" + str.substring(0,str.length-1) + "]";
		Base.submit("positionid","<%=basePath%>org/position/positionSettingMgAction!queryUsersByOrgId.do",
				{"dto['orgid']":treeNode.orgid,"userids":str,"dto['isChildren']":Base.getValue("isChildren")})
	}
	function fnOrgTreeClick1(obj){
		var orgTree = Base.getObj("orgTree");
		var selectedNodes = orgTree.getSelectedNodes();
		var treeNode;
		if(selectedNodes && selectedNodes.length == 1){
			treeNode = selectedNodes[0];
		}else{
			Base.alert("请选择部门后再进行子部门人员的查询","warn");
			$(obj).removeAttr("checked");
			return;
		}
		var data = Base.getGridData("selectedUserGrid");
		var str = ""
		if(data && data.length > 0){
			for(var i = 0 ; i < data.length ; i++){
				str += "{\"userid\":"+data[i].userid+"},";
			}
		}
		str = "[" + str.substring(0,str.length-1) + "]";
		Base.submit("positionid","<%=basePath%>org/position/positionSettingMgAction!queryUsersByOrgId.do",
				{"dto['orgid']":treeNode.orgid,"userids":str,"dto['isChildren']":Base.getValue("isChildren")})
	}
	function fnAddPosition(){
		var data = Base.getGridSelectedRows("userGrid");
		if(data && data.length > 0){
			Base.submit("userGrid","<%=basePath%>org/position/positionSettingMgAction!saveAssignUsers.do",{"dto['positionid']":Base.getValue('positionid')},null,false,function(){
				Base.deleteGridSelectedRows("userGrid");
			});
		}else{
			Base.alert("请选择数据后再进行岗位的添加","warn");
		}
	}
	function fnDeletePosition(){
		var data = Base.getGridSelectedRows("selectedUserGrid");
		if(data && data.length > 0){
			Base.submit("positionid,selectedUserGrid","<%=basePath%>org/position/positionSettingMgAction!removeAssignUsers.do",null,null,false,function(){
				Base.deleteGridSelectedRows("selectedUserGrid");
				fnOrgTreeClick1($("#isChildren").get(0));
			});
		}else{
			Base.alert("请选择数据后再进行岗位的移除","warn");
		}
	}
	//组织树渲染
	function fnFontCss(treeId,treeNode){
		if(treeNode.effective == 1 && !treeNode.admin){
			return {'text-decoration':'line-through','color': 'red'};
		}else if(treeNode.effective == 1 && treeNode.admin){
			return {'text-decoration':'line-through'};
		}else if(treeNode.effective != 1 && !treeNode.admin){
			return {'color': 'red'};
		}else{
			return {};
		} 
	}
	//权限判断
	function fnBeforeClick(treeId,treeNode){
		if(treeNode.effective == 1){ 
			parent.Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
			return false;
		}
		if(treeNode.admin != true){
			parent.Base.msgTopTip("你无权操作该组织");
			return false;
		}else{
			return true;
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>