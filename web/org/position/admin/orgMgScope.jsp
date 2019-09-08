<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:panel id="panel1" hasBorder="false" expanded="false" fit="true" withButtonBar="true" bodyStyle="overflow:auto;">
	<ta:tree id="orgMgTree" checkable="true" fontCss="fnSetFont" chkboxType="{'Y':'s','N':'s'}"/>
	<ta:text id="positionid" display="false"/>
	<ta:panelButtonBar>
		<ta:button id="saveScopeOpBtn" key="保存" icon="icon-add1" isok="true" onClick="fnSaveOp('adminMgAction!saveOrgMgScope.do')"/>
		<ta:button id="closeOpBtn" key="关闭" icon="icon-no" onClick="Base.closeWindow('mgScope');" />
	</ta:panelButtonBar>
</ta:panel>
<script>
function fnSaveOp(url) {
	var obj = Base.getObj("orgMgTree");
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
		Base.submit("panel1", url, {"ids":str,"dto['positionid']":Base.getValue("positionid")},null,null,function(){
			Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>分配部门管理成功</div>");
			Base.closeWindow('mgScope');
		});
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>