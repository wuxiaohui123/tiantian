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
	<ta:panel hasBorder="true" id="userPositionPanel"  withButtonBar="true" fit="true" cols="2">
		<ta:panel id="form1" columnWidth="0.41" cssStyle="margin:10px 0px 20px 10px;" fit="true" cols="2">
				<ta:text id="open_userid" display="none"/>
				<ta:text id="positionids" display="none"/>
				<ta:selectTree columnWidth="0.75" url="positionUserMgAction!webQueryAsyncOrgTree.do" selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss"  idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
				 targetDESC="open_orgname" treeId="orgTreeUserPosition" targetId="open_orgid" key="组织"  labelWidth="70" />
				<ta:box columnWidth="0.25">
					<ta:checkboxgroup span="2">
						<ta:checkbox id="isDisSubOrgs"  key="包含子组织" value="0" />
					</ta:checkboxgroup>
				</ta:box>
				<ta:text id="open_positionname" columnWidth="0.75"  key="岗位名称"  labelWidth="70"></ta:text>
				<ta:buttonLayout span="2">
					<ta:button key="查询" hotKey="q" isok="true" onClick="fnClick();"  icon="xui-icon-query"></ta:button>
					<ta:button key="重置" hotKey="r"  onClick="Base.resetForm('form1')" icon="xui-icon-reset"></ta:button>
				</ta:buttonLayout>
		</ta:panel>
		<ta:panel fit="true" columnWidth="0.59" cssStyle="margin:10px 10px 20px 10px;">
			<ta:datagrid id="noPositionPerGrid" fit="true" haveSn="true"  selectType="checkbox" columnFilter="true" >
				<ta:datagridItem id="positionid" asKey="true" key="岗位id" hiddenColumn="true"></ta:datagridItem>
				<ta:datagridItem id="positionname" key="岗位名称"  showDetailed="true"></ta:datagridItem>
				<ta:datagridItem id="positiontype" key="岗位类型" formatter="fnPositionType"  collection="POSITIONTYPE"></ta:datagridItem>
				<ta:datagridItem id="orgnamepath" key="岗位路径" showDetailed="true" width="250"></ta:datagridItem>
				<ta:dataGridToolPaging url="positionUserMgAction!getPubPositionsNoCurUseridByOrgId.do" submitIds="form1" showExcel="false" pageSize="20" />
			</ta:datagrid>
		</ta:panel>
		<ta:panelButtonBar>
			<ta:submit submitIds="open_userid,noPositionPerGrid" isok="true" url="positionUserMgAction!saveUserAddPositions.do"  key="保存[S]" hotKey="s" icon="icon-add" onSubmit="fnCheckSelectedData" successCallBack="fnAddPositionSuccess"/>
			<ta:button key="关闭[X]" hotKey="x" onClick="parent.Base.closeWindow('win');"/>
		</ta:panelButtonBar>
	</ta:panel>
	</body>
</html>
<script>
$(document).ready(function () {
	$("body").taLayout();
	Base.submit("form1", "positionUserMgAction!getPubPositionsNoCurUseridByOrgId.do");
});
//岗位选择判断
function fnCheckSelectedData(){
	var data = Base.getGridSelectedRows("noPositionPerGrid");
	if(data && data.length > 0)
		return true;
	else{
		Base.alert("请选择岗位后再添加岗位","warn");
		return false;
	}
		
}
//点击树节点,查询岗位
function fnClick(){
	
	var orgTree = $.fn.zTree.getZTreeObj("orgTreeUserPosition");
	var selectedNodes = orgTree.getSelectedNodes();
	var treeNode;
	if(selectedNodes && selectedNodes.length == 1){
		treeNode = selectedNodes[0];
	}
	
	var orgidpath = "";
	if(treeNode){
		orgidpath = treeNode.orgidpath;
	}
	Base.submit("form1", "positionUserMgAction!getPubPositionsNoCurUseridByOrgId.do", {"dto['orgidpath']":orgidpath})
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
/**格式化岗位类型*/
function fnPositionType(row, cell, value, columnDef, dataContext){
	if(dataContext.positiontype==1){
		if(dataContext.iscopy == 1){
			return "复制岗位";
		}else if(dataContext.isshare == 1){
			return "共享岗位";
		}else{
			return "公有岗位";
		}
	}else if(dataContext.positiontype == 2){
		return "个人专属岗位";
	}
	return value;
}
//添加岗位成功回调
function fnAddPositionSuccess(){
	Base.deleteGridSelectedRows("noPositionPerGrid");
	var param = {"dto['userid']":Base.getValue("open_userid")};
	parent.Base.submit("","positionUserMgAction!queryPerMission.do",param);
	parent.Base.msgTopTip("添加成功!");
	parent.Base.closeWindow('win');
}

</script>
<%@ include file="/ta/incfooter.jsp"%>