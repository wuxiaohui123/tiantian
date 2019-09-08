<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<div class="left-themes-color" style="color:red;">该功能只针对"管理员",指对管理员分配<span style="font-size: 18px;">系统管理类</span>菜单的使用权限</div>
<ta:box id="addpanel" fit="true" cols="1" cssStyle="overflow:auto;" heightDiff="38">
	<ta:tree id="adminTree" checkable="true" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
	<ta:text id="adminPositionid" display="false"/>
	<ta:text id="positionType" display="false"/>
</ta:box>
<ta:buttonLayout align="right" cssClass="left-themes-color" cssStyle="border-top:1px solid #e6e6e6">
	<ta:button id="saveBtn" key="保存"  icon="icon-add1" isok="true" onClick="fnSaveOp('adminMgAction!saveAdminUsePermission.do')"/>
	<ta:button id="closeOpBtn" key="关闭" icon="icon-no" onClick="Base.closeWindow('adminUseWin');" />
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
function fnSaveOp(url) {
	var obj = Base.getObj("adminTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for (var i = 0; i < len; i++) {
		str += "{\"id\":\"" + nodes[i].id + "\",\"checked\":" + nodes[i].checked + "},";
		nodes[i].checkedOld = nodes[i].checked;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("addpanel", url, {"ids":str,"dto['positionid']":Base.getValue("adminPositionid")},null,null,function(){
			Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>授权成功!</div>");
			Base.closeWindow("adminUseWin");
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>