<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>岗位</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar">
		<ta:pageloading/>
		<ta:box fit="true" cols="1" >
			<ta:box fit="true" cssStyle="margin-right:2px">
				<ta:panel id="condition" fit="true" cols="2" withButtonBar="true" bodyStyle="border:0px;">
					<ta:text id="positionid" display="false"/>
					<ta:text id="userids" display="false"/>
					<ta:text id="orgid" display="false"/>
					<ta:panel  columnWidth="0.3" fit="true" cssStyle="margin:10px 0px 20px 10px;">
						<ta:checkbox key="包含子组织" id="isChildren" checked="true" value="isChildren" onClick="fnOrgTreeClick1(this)"/>
						<ta:tree id="orgTree" asyncUrl="positionUserMgAction!webQueryAsyncOrgTree.do" fontCss="fnFontCss" beforeClick="fnBeforeClick" childKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
						 async="true" onClick="fnOrgTreeClick" showLine="true"/>
					</ta:panel>
					<ta:panel columnWidth="0.7" fit="true" cssStyle="margin:10px 10px 20px 10px;" >
						<ta:datagrid fit="true" id="userGrid" haveSn="true" selectType="checkbox" columnFilter="true">
							<ta:datagridItem id="name" key="姓名" align="center" width="90"></ta:datagridItem>
							<ta:datagridItem id="orgnamepath" key="所属组织" align="center"  width="300" showDetailed="true"></ta:datagridItem>
							<ta:dataGridToolPaging url="positionUserMgAction!queryUsersByOrgId.do" submitIds="condition" showExcel="false" pageSize="20" />
						</ta:datagrid>
					</ta:panel>
					<ta:panelButtonBar>
						<ta:button key="保存[S]" hotKey="s" onClick="fnAddPosition()" isok="true"/>
						<ta:button id="remove"  key="关闭[X]" hotKey="x" icon="icon-no" onClick="fnClose();"/>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
		</ta:box>
		
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
// 		var treeObj = $.fn.zTree.getZTreeObj("orgTree");
// 		var nodes = treeObj.getNodes();
// 		if (nodes.length>0) {//默认选中根节点
// 			treeObj.selectNode(nodes[0]);
// 		}
		//默认以根节点进行查询
// 		fnOrgTreeClick1($("#isChildren").get(0));
		Base.submit("positionid","positionUserMgAction!queryUsersByOrgId.do",
				{"dto['orgid']":Base.getValue("orgid"),"dto['userids']":Base.getValue("userids"),"dto['isChildren']":Base.getValue("isChildren")});
	});
	function fnOrgTreeClick(e, tree, treeNode){
		var userids = Base.getValue("userids");
		Base.setValue("orgid",treeNode.orgid);
		Base.submit("positionid","positionUserMgAction!queryUsersByOrgId.do",
				{"dto['orgid']":treeNode.orgid,"dto['userids']":userids,"dto['isChildren']":Base.getValue("isChildren")});
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
		var userids = Base.getValue("userids");
		Base.setValue("orgid",treeNode.orgid);
		Base.submit("positionid","positionUserMgAction!queryUsersByOrgId.do",
				{"dto['orgid']":treeNode.orgid,"dto['userids']":userids,"dto['isChildren']":Base.getValue("isChildren")});
	}
	
	function fnAddPosition(){
		var data = Base.getGridSelectedRows("userGrid");
		if(data && data.length > 0){
			Base.submit("userGrid","positionUserMgAction!saveAssignUsers.do",{"dto['positionid']":Base.getValue('positionid')},null,false,function(){
				Base.deleteGridSelectedRows("userGrid");
				var positionid = Base.getValue('positionid');
				parent.Base.submit("","positionUserMgAction!queryPosMission.do",{"dto['pos_positionid']":positionid},null,null,function(data){
				});
				parent.Base.msgTopTip("添加成功!");
				fnClose();
			});
		}else{
			Base.alert("请选择数据后再进行岗位的添加","warn");
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
	
	/**关闭窗口*/
	function fnClose(){
		parent.Base.closeWindow("assignUser");
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>