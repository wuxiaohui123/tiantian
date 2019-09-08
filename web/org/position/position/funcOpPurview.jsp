<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:panel id="funpanel" hasBorder="false" expanded="false" fit="true" withButtonBar="true" bodyStyle="overflow:auto;" withToolBar="true">
		<ta:panelToolBar cssStyle="height:55px">
			<div style="color:red;padding:0px 10px;">1.只能授予<span style="font-size: 18px;">通用类</span>菜单和<span style="font-size: 18px;">业务功能类</span>的菜单;<br/><span style="color:green">2.绿色表示超过有效期</span><br/><span style="color:gray">3.灰色表示需要审核</span></div>
		</ta:panelToolBar>
		<ta:tree id="opTree" checkable="true" beforeCheck="fnBfChk" fontCss="fnSetFont" onAccept="fnSetEffectiveDate"
			 editable="true" showAcceptBtn="true" showEditBtn="false" showRemoveBtn="false" showAddBtn="false" acceptTitle="设置权限截止日期"/>
		<ta:text id="usePermissionPositionid" display="false"/>
		<ta:text id="positionType" display="false"/>
		<ta:panelButtonBar>
			<ta:button id="btnEffectiveTime" key="批量设置有效时间" icon="icon-add1"  onClick="fnBatchSetEffectiveDate()"/>
			<ta:button id="personalsaveScopeOpBtn" key="保存" icon="icon-add1" isok="true" onClick="fnSaveOp('personalPositionMgAction!saveRoleScopeAclOperate.do')"/>
			<ta:button id="saveScopeOpBtn" key="保存" icon="icon-add1" isok="true" onClick="fnSaveOp('positionMgAction!saveRoleScopeAclOperate.do')" display="false"/>
			<ta:button id="closeOpBtn" key="关闭" icon="icon-no"  onClick="Base.closeWindow('opWin');" />
		</ta:panelButtonBar>
	</ta:panel>
<script>
function fnSetFont(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return {color:"red"};
	if(treeNode.isyab003 != null ){
		return {color:"blue"};
	}
	if(treeNode.effectivetimeover){
		return {color:"green"};
	}
	if(treeNode.isaudite){
		return {color:"gray"};
	}
}
function fnBfChk(treeId, treeNode) {
	if(treeNode.policy == 4 || treeNode.policy == 3)
		return Base.alert("该功能无需安全认证，所有用户都有权限，即便修改也无效。"), false;
	return true;
}
function fnSaveOp(url) {
	var obj = Base.getObj("opTree");
	var nodes = obj.getChangeCheckedNodes();
	var len = nodes.length;
	if (len == 0) {
		return Base.alert("没有任何改变，不需要保存。"), false;
	}
	var str = "";
	for (var i = 0; i < len; i++) {
		var node = nodes[i];
		if(node.isyab003 != null){
			var pnode = node.getParentNode();
			str += "{\"id\":\"" + node.id + "\",\"checked\":" + node.checked + ",\"isyab003\":"+node.isyab003+",\"menuid\":"+pnode.id+"},";
		}else{
			str += "{\"id\":\"" + node.id + "\",\"checked\":" + node.checked + "},";
		}
		node.checkedOld = node.checked;
	}
	if (str != "") {
		str = "[" + str.substr(0, str.length - 1) + "]";
		Base.submit("funpanel", url, {"ids":str,"dto['positionid']":Base.getValue("usePermissionPositionid")},null,null,function(){
			Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>授权成功!</div>");
			Base.closeWindow("opWin");
		});
	}
}
function fnSetEffectiveDate(e,treeId,treeNode){
	var type = Base.getValue("positionType"),url = "";
	if(type == "1"){//公有岗位
		url = "positionMgAction!toSetEffectiveTime.do";
	}else{//个人岗位
		url = "personalPositionMgAction!toSetEffectiveTime.do";
	}
	Base.openWindow("win","设置有效时间",url,{"menuid":treeNode.id,"positionid":Base.getValue("usePermissionPositionid"),"positiontype":type},300,300,function(){
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
			url = "positionMgAction!toBatchSetEffectiveTime.do";
		}else{//个人岗位
			url = "personalPositionMgAction!toBatchSetEffectiveTime.do";
		}
		Base.openWindow("win","批量设置有效时间",url,{"positionid":Base.getValue("usePermissionPositionid"),"positiontype":type},300,300,function(){
			Base.focus("effectiveTime");
		})
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>