<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>共享岗位</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="padding:0px;margin:0px;">
		<ta:pageloading/>
		<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true" withButtonBar="true" bodyStyle="overflow:auto;">
			<ta:tree id="shareTree" checkable="true" fontCss="fnFontCss" chkboxType="{'Y':'','N':''}" beforeCheck="fnBeforeCheck"/>
			<%-- 父页面岗位id，即被共享的岗位id --%>
			<ta:text id="positionid" display="false"/>
			<ta:panelButtonBar>
				<ta:button id="saveScopeOpBtn" key="保存[S]" hotKey="s" icon="icon-add1" isok="true"  onClick="fnSaveOp('positionUserMgAction!saveSharePositions.do')"/>
				<ta:button id="closeOpBtn" key="关闭[X]" hotKey="x" icon="icon-no" onClick="parent.Base.closeWindow('sharePosition');" />
			</ta:panelButtonBar>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnFontCss(treeId, treeNode) {
		if(!treeNode.isadmin)
			return {color:"red"};
	}
	function fnBeforeCheck(treeId,treeNode) {
		if(!treeNode.isadmin){
			Base.alert("你无此组织的操作权限","warn");
			return false;
		}else{
			return true;
		}
	}
	function fnSaveOp(url) {
		var shareTree = Base.getObj("shareTree");
		var nodes = shareTree.getChangeCheckedNodes();
		var len = nodes.length;
		if(len == 0){
			return Base.alert("没有任何改变，不需要保存。"), false;
		}
		var str = "";
		for (var i = 0; i < len; i++) {
			str += "{\"id\":\"" + nodes[i].id + "\",\"checked\":" + nodes[i].checked + "},";
			nodes[i].checkedOld = nodes[i].checked;
		}
		if (str != "") {
			str = "[" + str.substr(0, str.length - 1) + "]";
			Base.submit("panel1", url, {"ids":str},null,null,function(){
				parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>授权成功!<div>");
				parent.fnQueryPos();
				parent.Base.submit("","positionUserMgAction!queryPosMission.do",{"dto['pos_positionid']":Base.getValue("positionid")},null,null,function(data){
					parent.enablePosBtn();
				});
				parent.Base.closeWindow('sharePosition');
				
			});
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>