<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<div class="left-themes-color" style="color:red;padding:0px 10px;">你在表格中所勾选的岗位将<span style="font-size: 18px;">获取</span>这些被勾选的资源的使用权限.</div>
<ta:panel id="grantpanel" hasBorder="false" expanded="false" fit="true" bodyStyle="overflow:auto;" heightDiff="38">
	<ta:tree id="opTree" checkable="true" beforeCheck="fnBfChk" fontCss="fnSetFont" onAccept="fnSetEffectiveDate"
			 editable="true" showAcceptBtn="true" showEditBtn="false" showRemoveBtn="false" showAddBtn="false" acceptTitle="设置权限截止日期"/>
	<ta:textarea id="grantPermissionsPositionids" display="false"></ta:textarea>
	<ta:text id="positionType" display="false"/>
</ta:panel>
<ta:buttonLayout align="right" cssClass="left-themes-color" cssStyle="border-top:1px solid #e6e6e6">
	<ta:button id="btnEffectiveTime" key="批量设置有效时间" icon="icon-add1"  onClick="fnBatchSetEffectiveDate()"/>
	<ta:button id="personalrecyclePermissionsOpBtn" key="保存" icon="icon-add1" isok="true" onClick="fnRecyclePermissionsOp('personalPositionMgAction!grantUsePermissions.do')"/>
	<ta:button id="recyclePermissionsOpBtn" key="保存" isok="true"  icon="icon-add1" onClick="fnRecyclePermissionsOp('positionMgAction!grantUsePermissions.do')"/>
	<ta:button id="closeOpBtn" key="关闭" icon="icon-no" onClick="Base.closeWindow('opWin');" />
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
			str += "{\"id\":\"" + node.id + "\",\"checked\":true,\"menuid\":"+pnode.id+",\"isyab003\":"+node.isyab003+"},";
		}else{
			str += "{\"id\":\"" + node.id + "\",\"checked\":true},";
		}
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("grantpanel", url, {"ids":str,"positionids":Base.getValue("grantPermissionsPositionids")},null,null,function(){
			Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>批量授予使用权限成功</div>");
			Base.closeWindow('opWin');
		});
	}
}
function fnSetEffectiveDate(e,treeId,treeNode){
	var type = Base.getValue("positionType"),url = "";
	if(type == "1"){//公有岗位
		url = "positionMgAction!toPositionsSetEffectiveTime.do";
	}else{//个人岗位
		url = "personalPositionMgAction!toPositionsSetEffectiveTime.do";
	}
	Base.openWindow("win","设置有效时间",url,{"menuid":treeNode.id,"positionids":Base.getValue("grantPermissionsPositionids"),"positiontype":type},300,300,function(){
		Base.focus("effectiveTime");
	})
}
function fnBatchSetEffectiveDate(){
	var tree = Base.getObj("opTree");
	var nodes = tree.getCheckedNodes(true);
	if(nodes && nodes.length <= 0){
		Base.alert("请勾选需要设置有效时间的菜单","warn");
	}else{
		var type = Base.getValue("positionType"),url = "";
		if(type == "1"){//公有岗位
			url = "positionMgAction!toPositionsBatchSetEffectiveTime.do";
		}else{//个人岗位
			url = "personalPositionMgAction!toPositionsBatchSetEffectiveTime.do";
		}
		Base.openWindow("win","批量设置有效时间",url,{"positionids":Base.getValue("grantPermissionsPositionids"),"positiontype":type},300,300,function(){
			Base.focus("effectiveTime");
		})
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>