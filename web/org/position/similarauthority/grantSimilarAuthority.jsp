<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:box fit="true" cssStyle="padding:10px 10px 0px 10px;">
	<ta:fieldset>
		<div style="color:red;">你在表格中所勾选的岗位将”获取“这些被勾选的资源的使用权限.</div>
	</ta:fieldset>
	<ta:panel id="panel1" hasBorder="true" expanded="false" fit="true" bodyStyle="overflow:auto;" withButtonBar="true" bodyClass="linecolor" cssStyle="margin-top:10px;">
		<ta:tree id="opTree" checkable="true" beforeCheck="fnBfChk" fontCss="fnSetFont"/>
		<ta:text id="positionids" display="false"></ta:text>
		<ta:panelButtonBar cssStyle="border-top:0px;">
			<ta:button id="recyclePermissionsOpBtn" key="保存" icon="icon-add1" isok="true"  onClick="fnRecyclePermissionsOp('similarAuthorityAction!saveSimilarAuthority.do')"/>
			<ta:button id="closeOpBtn" key="关闭" icon="icon-no"  onClick="Base.closeWindow('similar');" />
		</ta:panelButtonBar>
	</ta:panel>
</ta:box>
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
		str += "{\"id\":\"" + checkedNodes[i].id + "\",\"checked\":true},";
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("panel1", url, {"ids":str,"positionids":Base.getValue("positionids")},null,null,function(){
			Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>批量授予使用权限成功</div>");
			Base.closeWindow('similar');
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>