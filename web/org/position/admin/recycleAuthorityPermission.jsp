<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<div  style="color:red;padding-left:10px;">注：这两种授权都是针对管理员，如甲乙两个管理员<br/>(1)甲收回了乙左边的权限,则乙右边的权限也跟着被回收;乙不再有授权权限和相对应的再授权权限;<br/>(2)甲收回了乙右边的权限,则左边的权限不受影响;乙不再具有相应的再授权权限<br/>(3)勾选表示回收</div>
<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true"  bodyStyle="overflow:auto;border:0px" cols="2" heightDiff="38">
	<ta:panel  key="授权权限" hasBorder="false" expanded="false" cssStyle="margin-right:2px;" bodyStyle="border-bottom:0px;">
		<ta:tree id="grantingTree1" checkable="true" chkboxType="{'Y':'s','N':''}" checkedKey="checked1" onCheck="fnTreeChg1" onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	</ta:panel>
	<ta:panel  key="再授权权限" hasBorder="false" expanded="false" cssStyle="margin-left:2px;" bodyStyle="border-bottom:0px;">
		<ta:tree id="grantingTree2" checkable="true" chkboxType="{'Y':'s','N':''}" checkedKey="checked2" onCheck="fnTreeChg2" onExpand="fnSyncExpand" onCollapse="fnSyncCollapse" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	</ta:panel>
	<ta:textarea id="recycleAuthorityPermissionsPositionids" display="false"></ta:textarea>
</ta:panel>
<ta:buttonLayout align="right" cssClass="left-themes-color" cssStyle="border-top:1px solid #e6e6e6">
	<ta:button id="saveRoleGrantingBtn" key="保存" icon="icon-add1" isok="true" onClick="fnSaveGranting('adminMgAction!recycleAuthorityPermissions.do')"/>
	<ta:button id="closeGrantingBtn" key="关闭" icon="icon-no" onClick="Base.closeWindow('grantingWin');"/>
</ta:buttonLayout>
<script>
function fnSetFont(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return {color:"red"};
}
function fnBfChk(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return Base.alert("该功能无需安全认证，所有用户都有权限，即便修改也无效。"), false;
	return true;
}
function fnSaveGranting(url) {
	var tree1 = Base.getObj("grantingTree1");
	var tree2 = Base.getObj("grantingTree2");
	var nodes1 = tree1.getChangeCheckedNodes();
	var nodes2 = tree2.getChangeCheckedNodes();
	var checkedNodes1 = tree1.getCheckedNodes(true);
	var checkedNodes2 = tree2.getCheckedNodes(true);
	var len1 = nodes1.length;
	var len2 = nodes2.length;
	if (len1 == 0 && len2 == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for(var m = 0; m < checkedNodes1.length; m++){
		str += "{\"id\":\"" + checkedNodes1[m].id + "\",\"checked\":\"true\",\"permission\":\"1\"},";
	}
	for(var n = 0; n < checkedNodes2.length; n++){
		str += "{\"id\":\"" + checkedNodes2[n].id + "\",\"checked\":\"true\",\"authrity\":\"1\"},";
	}
	for (var i = 0; i < len1; i++) {
		nodes1[i].checkedOld = nodes1[i].checked1;
	}
	for (var j = 0; j < len2; j++) {
		nodes2[j].checkedOld = nodes2[j].checked2;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("panel1", url, {"ids":str,"positionids":Base.getValue("recycleAuthorityPermissionsPositionids")},null,null,function(){
			Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>批量回收权限成功</div>");
		});
	}
	
}
function fnSyncExpand(event, treeId, treeNode) {
	var treeObj;
	if (treeId == "grantingTree1") {
		treeObj = Base.getObj("grantingTree2");
	} else {
		treeObj = Base.getObj("grantingTree1");
	}
	treeObj.expandNode(treeObj.getNodeByParam("id", treeNode.id), true, false, false);
}
function fnSyncCollapse(event, treeId, treeNode) {
	var treeObj;
	if (treeId == "grantingTree1") {
		treeObj = Base.getObj("grantingTree2");
	} else {
		treeObj = Base.getObj("grantingTree1");
	}
	treeObj.expandNode(treeObj.getNodeByParam("id", treeNode.id), false, false, false);
}
function fnTreeChg1(event, treeId, treeNode) {
	var tree1 = Base.getObj(treeId);
	var ptree1Node =  tree1.getNodeByParam("id", treeNode.pId);
	var p1checkbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	var tree2 = Base.getObj("grantingTree2");
	var treeNode2 = tree2.getNodeByParam("id", treeNode.id);
	var checkbox2 = $("#" + treeNode2.tId + "_check");
	if(treeNode2){
		if (treeNode.checked1 && !treeNode2.checked2)
			checkbox2.click();
	}
	if(ptree1Node){
		if (!treeNode.checked1 && ptree1Node.checked1)
			p1checkbox.click();
	}
	
}
function fnTreeChg2(event, treeId, treeNode) {
	var tree1 = Base.getObj("grantingTree1");
	var treeNode1 = tree1.getNodeByParam("id", treeNode.id);
	var checkbox1 = $("#" + treeNode1.tId + "_check");
	var tree2 = Base.getObj(treeId);
	var ptree2Node =  tree2.getNodeByParam("id", treeNode.pId);
	var p2checkbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	if(ptree2Node){
		if (!treeNode.checked2 && ptree2Node.checked2)
			p2checkbox.click();
	}
	if(treeNode1){
		if (!treeNode.checked2 && treeNode1.checked1)
			checkbox1.click();
// 		else if (treeNode.isParent && treeNode.checked2 && treeNode1.checked1) {
// 			var children = treeNode1.children;
// 			fnIterateNode(children);
// 		}
	}
}
// function fnIterateNode(children) {
// 	for (var i = 0; i < children.length; i++) {
// 		if (!children[i].checked1) {
// 			$("#" + children[i].tId + "_check").click();
// 		}
// 		if (children[i].isParent) fnIterateNode(children[i].children);
// 	}
// }
</script>
<%@ include file="/ta/incfooter.jsp"%>