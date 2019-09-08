<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:text id="positionid"  display="false"></ta:text>
<ta:panel fit="true"  withButtonBar="true" cols="2" hasBorder="false">
	<ta:box span="2">
		<ta:date id="validtime" validNowTime="right" datetime="true" showSelectPanel="true" required="true" key="委派截止时间"></ta:date>
	</ta:box>
	<ta:box cssStyle="overflow:auto;" fit="true" span="2">
		<ta:tree id="grantTree" checkable="true" chkboxType="{'Y':'ps', 'N':'ps'}" fontCss="fnSetFont" ></ta:tree>
	</ta:box>
	<ta:panelButtonBar cssStyle="padding-bottom:10px;">
		<ta:button isok="true" key="保存" icon="icon-add1" onClick="fnSavePermissions()"></ta:button>
		<ta:button  key="关闭" icon="icon-no" onClick="fnClose()"></ta:button>
	</ta:panelButtonBar>
</ta:panel>
<script>
	function fnSavePermissions(){
		var tree = Base.getObj("grantTree");
		var nodes = tree.getChangeCheckedNodes();
		var len = nodes.length;
		var str = "";
		for (var i = 0; i < len; i++) {
			var node = nodes[i];
			if(node.isyab003 != null){
				var pnode = node.getParentNode();
				str += "{\"id\":\"" + node.id + "\",\"checked\":" + node.checked + ",\"isyab003\":"+node.isyab003+",\"menuid\":"+pnode.id+"},";
			}else{
				str += "{\"id\":\"" + node.id + "\",\"checked\":" + node.checked + "},";
			}
		}
		if (str != "") {
			str = "[" + str.substr(0, str.length - 1) + "]";
			Base.submit("validtime", "delegatePositionAction!updateDeletegatePositionPermissions.do", {"ids":str,"dto['positionid']":Base.getValue("positionid")},null,true,function(){
				Base.alert("修改委派权限成功","success");
				fnClose();
			});
		}else{
			Base.submit("validtime", "delegatePositionAction!updateDeletegatePositionPermissions.do", {"ids":[],"dto['positionid']":Base.getValue("positionid")},null,true,function(){
				Base.alert("修改委派权限成功","success");
				fnClose();
			});
		}
	}
	function fnClose(){
		Base.closeWindow("permissionInfo");
	}
	function fnSetFont(treeId, treeNode) {
		if(treeNode.policy == 4 || treeNode.policy == 3)
			return {color:"red"};
		if(treeNode.isyab003 != null ){
			return {color:"blue"};
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>