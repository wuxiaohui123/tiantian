<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:panel hasBorder="false" id="userPositionPanel" layout="border" layoutCfg="{leftWidth:300,bottomHeight:300,InWindow:false}" fit="true">
		<ta:text id="userid" display="none"/>
		<ta:box position="left" key="部门" cssStyle="overflow:auto;">			
			<ta:tree id="orgTreeUserPosition" onClick="fnOrgTreeClick" showLine="true" async="true" asyncUrl="orgMgAction!getAsyncOrgData.do" asyncParam="['id']"/>
		</ta:box>
		<ta:panel position="center" key="岗位列表" fit="true" withToolBar="true">
			<ta:panelToolBar>
				<ta:submit submitIds="userid,noPositionPerGrid"  url="userMgAction!addUserPositions.do" asToolBarItem="true" key="添加" icon="icon-add" successCallBack="fnAddPositionSuccess"/>
			</ta:panelToolBar>
			<ta:datagrid id="noPositionPerGrid" fit="true" haveSn="true" forceFitColumns="true" selectType="checkbox" rowColorfn="fnNowPositionColor">
				<ta:datagridItem id="positionid" asKey="true" key="岗位id" hiddenColumn="true"></ta:datagridItem>
				<ta:datagridItem id="positionname" key="岗位名称" width="260" showDetailed="true"></ta:datagridItem>
				<ta:datagridItem id="orgnamepath" key="岗位路径" showDetailed="true" width="300" collectionData="[{'id':2,'name':'个人岗位'},{'id':1,'name':'公有岗位'}]"></ta:datagridItem>
				<ta:datagridItem id="positiontype" key="岗位类型" width="150" align="center" dataAlign="center" collectionData="[{'id':2,'name':'个人岗位'},{'id':1,'name':'公有岗位'}]"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
		<ta:panel hasBorder="false" position="bottom" fit="true" key="已选岗位列表">
			<ta:datagrid id="positionPerGrid" fit="true" haveSn="true" forceFitColumns="true" rowColorfn="fnNowPositionColor">
				<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true"></ta:datagridItem>
				<ta:datagridItem id="positionname" key="岗位名称" width="200" showDetailed="true"></ta:datagridItem>
				<ta:datagridItem id="orgnamepath" key="岗位路径" width="400"  showDetailed="true"></ta:datagridItem>
				<ta:datagridItem id="positiontype" key="岗位类型" width="200" align="center" dataAlign="center" collectionData="[{'id':2,'name':'个人岗位'},{'id':1,'name':'公有岗位'}]"></ta:datagridItem>
				<ta:datagridItem id="del" key="删除" width="50" align="center" dataAlign="center" click="fnDelPosition" icon="icon-remove"></ta:datagridItem>
			</ta:datagrid>
		</ta:panel>
	</ta:panel>
<script>
$(function () {
	//Base.focus("name");
})
function fnOrgTreeClick(e, tree, treeNode){
	Base.submit("userid", "userMgAction!queryUserPositionByOrg.do", {"dto['orgidpath']":treeNode.orgidpath,"dto['orgid']":treeNode.id})
}
function fnDelPosition(data, e){
	function delPositions(yes) {
		if (yes) {
			function success(){
				//TODO lins 可能无法选中
				Base.deleteGridSelectedRows("positionPerGrid");
			}
			Base.submit("userid", "userMgAction!removeUserPosition.do", {"dto['positionid']":data.positionid}, null ,null, success);
		}
	}
	Base.confirm("是否删除此岗位",delPositions);
}
function fnAddPositionSuccess(){
	function success(){
		Base.deleteGridSelectedRows("noPositionPerGrid");
	}
	Base.submit("userid", "userMgAction!queryUserHadPosition.do",null,null,null,success);
}
function fnNowPositionColor(data){
	if(data.positionid == Base.getValue("nowpositionid")){
		return "red";
	}
	//公有岗位
	if(data.positiontype == "1"){
		return "rgb(232, 223, 246)";
	}
	//个人岗位
	if(data.positiontype == "2"){
		return "rgb(209, 248, 167)";
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>