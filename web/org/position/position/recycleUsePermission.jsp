<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<div class="left-themes-color" style="color:red;padding:0px 10px">你在表格中所勾选的岗位将<span style="font-size: 18px;">失去</span>这些被勾选的资源的使用权限.</div>
<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true"  bodyStyle="overflow:auto;" heightDiff="38">
	<ta:tree id="opTree" checkable="true" chkboxType="{'Y':'s','N':''}" onCheck="fnTreeChg1" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	<ta:textarea id="recyclePermissionsPositionids" display="false"/>
	<ta:text id="positionType" display="false"/>
</ta:panel>
<ta:buttonLayout align="right" cssClass="left-themes-color" cssStyle="border-top:1px solid #e6e6e6">
		<ta:button id="personalrecyclePermissionsOpBtn" key="保存" icon="icon-add1" isok="true"  onClick="fnRecyclePermissionsOp('personalPositionMgAction!recyclePermissions.do')"/>
		<ta:button id="recyclePermissionsOpBtn" key="保存" icon="icon-add1" isok="true"  onClick="fnRecyclePermissionsOp('positionMgAction!recyclePermissions.do')"/>
		<ta:button id="closeOpBtn" key="关闭" icon="icon-no" onClick="Base.closeWindow('opWin');" />
</ta:buttonLayout>
<script>
function fnTreeChg1(event, treeId, treeNode) {
	var opTree = Base.getObj("opTree");
	var ptreeNode = opTree.getNodeByParam("id", treeNode.pId);
	var pcheckbox = $("#" + treeId + "_" + treeNode.pId + "_check");
	if(ptreeNode){
		if (!treeNode.checked && ptreeNode.checked)
			pcheckbox.click();
	}
}
function fnSetFont(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return {color:"red"};
}
function fnBfChk(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return Base.alert("该功能无需安全认证，所有用户都有权限，即便修改也无效。"), false;
	return true;
}

function fnRecyclePermissionsOp(url){
	var obj = Base.getObj("opTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var checkedNodes = obj.getCheckedNodes(true);
	var str = "";
	for (var i = 0; i < len; i++) {
		nodes[i].checkedOld = nodes[i].checked;
	}
	for(var i = 0 ; i < checkedNodes.length; i++){
		var node = checkedNodes[i];
		if(node.isyab003 != null){
			var pnode = node.getParentNode();
			str += "{\"permissionid\":\"" + node.id + "\",\"isyab003\":\""+node.isyab003+"\",\"menuid\":\""+pnode.id+"\"},";
		}else{
			str += "{\"permissionid\":\"" + node.id + "\"},";
		}
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("panel1", url, {"ids":str,"positionids":Base.getValue("recyclePermissionsPositionids")},null,null,function(){
			Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>批量回收使用权限成功</div>");
			Base.closeWindow('opWin');
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>