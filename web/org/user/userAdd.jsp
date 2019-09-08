<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:form id="userForm" fit="true">
	<ta:panel id="userInfo" hasBorder="true" expanded="false" scalable="false" fit="true"
		bodyStyle="padding:10px 20px 10px 10px;overflow:auto;" withButtonBar="true">
		<ta:text id="w_userId" key="用户编号" readOnly="true" span="2" display="none"/>
		<ta:text id="w_loginid" key="登录帐号" required="true" span="2" />
		<ta:box span="2">
			<ta:selectTree fontCss="fnSetFont" url="userMgAction!webQueryAsyncOrgTree.do" required="true" asyncParam="['orgid']"
				selectTreeBeforeClick="fnselecttree"  selectTreeCallback="fnSelectTreeCallback" key="直属部门" nameKey="orgname"
				idKey="orgid" parentKey="porgid" targetDESC="w_orgname"
				treeId="w_orgTree" targetId="w_orgid"  textHelp="直属部门必须为机构和部门，<span style='color:red;font-size: 15px;font-weight: bold;'>红色</span>表示你当前岗位所不能操作的组织"/>
		</ta:box>
		<ta:box span="2">
			<ta:text id="w1_orgid" key="目标部门Id"  display="false"/>
			<ta:text id="w1_orgname" key="附属组织" onClick="showMenu();" readOnly="true" />
			<ta:box id ="menuContent" height="260px" cssClass="ffb" cssStyle="padding:0px;margin:0px;background:white;display:none;width:315px;left:100px;top:28px;overflow:auto;position:absolute;z-index:1000;">
				<ta:panel fit="true" withButtonBar="true" bodyStyle="border:0px;overflow:auto" hasBorder="false">
					<ta:tree id="w1_orgTree" nameKey="orgname" childKey="orgid" parentKey="porgid" async="true" asyncUrl="userMgAction!webQueryAsyncOrgTree.do" fontCss="fnSetFont"
						asyncParam="['orgid']" checkable="true" beforeCheck="fnBeforeCheck" onCheck="onCheck"  chkboxType="{'Y':'','N':''}" />
					<ta:panelButtonBar align="center">
						<ta:button key="清除" icon="xui-icon-reset" id="menuBtn" onClick="fnFsRemove()"></ta:button>
						<ta:button key="关闭" icon="icon-no" id="menuBtn1" onClick="hideMenu()"></ta:button>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
		</ta:box>
		<ta:text id="w_name" key="姓名" required="true" />
		<ta:radiogroup key="性别" collection="SEX" id="w_sex" cols="8" filterOrg="false"/>
		<ta:text id="w_password" key="登录口令" required="true" span="2"
			type="password" />
		<ta:text id="w_rpassword" key="确认口令" required="true" span="2"
			type="password" validType="compare(this.value, ['=', 'w_password'])" />
		<%-- <ta:text id="w_yab003" key="分中心" value="9999" /> --%>
		<%-- <ta:selectInput id="w_yab003" key="分中心" collection="yab003" value="9999"></ta:selectInput> --%>
		<ta:text id="tel" key="移动电话" maxLength="11" validType="mobile" />
		<%-- 新增用户扩展jsp --%>
		<%@include file="/org/orgextend/userMgExtend.jsp" %>
		<%-- <ta:panel key="所属组织" fit="true" withToolBar="true">
			<ta:panelToolBar cssStyle="height:auto;padding:0px">
				<ta:box cols="3">
					<ta:button asToolBarItem="true" key="添加" columnWidth="0.15"
						icon="icon-adduser" onClick="fnAddOrgToOrgList()" />
					<ta:button asToolBarItem="true" key="删除" columnWidth="0.15"
						icon="icon-remove" onClick="fnDelOrgFromOrgList()" />
				</ta:box>
			</ta:panelToolBar>
			<ta:textarea id="orgs" display="none" readOnly="true" />
			<ta:datagrid id="orgidList" fit="true" haveSn="true"
				forceFitColumns="true" selectType="checkbox">
				<ta:datagridItem id="orgname" key="组织名称" />
				<ta:datagridItem id="orgnamepath" key="组织路径" width="240"
					showDetailed="true" />
				<ta:datagridItem id="orgtype" key="组织类型" collection="ORGTYPE"
					sortable="true" width="70" />
							<ta:datagridItem id="mainposition" key="主岗位" width="70"/>
			</ta:datagrid>
		</ta:panel> --%>
		<ta:panelButtonBar>
			<ta:submit id="saveUserBtn" key="保存[S]" icon="icon-add1" hotKey="S" isok="true"
				submitIds="userInfo" url="userMgAction!webUserSave.do"
				successCallBack="fnSaveSuccCb"  />
			<ta:button id="closeWinBtn" key="关闭[X]" hotKey="X"  icon="icon-no" 
				onClick="Base.closeWindow('win');" />
		</ta:panelButtonBar>
	</ta:panel>
</ta:form>
<script>
	$(function() {
		Base.focus("w_loginid");
		Base.setValue("w_sex", "1");
	})
	function fnFsRemove(){
		var zTree = $.fn.zTree.getZTreeObj("w1_orgTree");
		zTree.checkAllNodes(false);
		var cityObj = $("#w1_orgname");
		cityObj.attr("value", "");
		var targetDepartId1 = $("#w1_orgid");
		targetDepartId1.attr("value", "");
		Base.hideObj("menuContent");
	}
	function fnBeforeCheck(treeId, treeNode) {
		var w_orgid = Base.getValue("w_orgid");
		if(w_orgid && w_orgid != ""){
			if(treeNode.orgid == w_orgid){
				Base.alert("直属部门已经是该部门，附属部门不能选择为直属部门","warn");
				return false;
			}
		}else{
			Base.alert("请先选择直属部门","warn");
			return false;
		}
		if(!treeNode.admin){
			Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
			return false;
		}
		return true;
// 		var zTree = $.fn.zTree.getZTreeObj("w1_orgTree");
// 		zTree.checkNode(treeNode, !treeNode.checked, null, true);
// 		return false;
	}
	
	function onCheck(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("w1_orgTree"),
		nodes = zTree.getCheckedNodes(true),
		v = "",hv = "";
		
		for (var i=0, l=nodes.length; i<l; i++) {
			v += nodes[i].orgname + ",";
			hv += nodes[i].orgid + ",";
		}
		if (v.length > 0 ) v = v.substring(0, v.length-1);
		if (hv.length > 0 ) hv = hv.substring(0, hv.length-1);
		var cityObj = $("#w1_orgname");
		cityObj.attr("value", v);
		var targetDepartId1 = $("#w1_orgid");
		targetDepartId1.attr("value", hv);
	}
	function showMenu() {
		$("#menuContent").slideDown("fast");

		$("body").bind("mousedown", onBodyDown);
	}
	function hideMenu() {
		$("#menuContent").fadeOut("fast");
		$("body").unbind("mousedown", onBodyDown);
	}
	function onBodyDown(event) {
		if (!(event.target.id == "menuBtn" || event.target.id == "w1_orgname" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
			hideMenu();
		}
	}
	/*添加org*/
	function fnAddOrgToOrgList() {
		var data = Base.getGridData("orgidList");
		var node = $.fn.zTree.getZTreeObj("w_orgTree").getNodeByParam("id",
				Base.getValue("w_orgid"), null);
		if (node != null && node.id != null) {
			for ( var i = 0; i < data.length; i++) {
				if (node.id == data[i].orgid) {
					Base.msgTopTip("<div style='width: 200px;margin: auto 0;text-align: center;line-height: 100px;font-size: 20px;'>已添加</div>",
									2200, 200, 100);
					return;
				}
			}
			var org = {};
			org.orgname = node.name;
			org.orgid = node.id;
			org.orgtype = node.orgtype;
			org.orgnamepath = node.orgnamepath;
			data.push(org);
			Base._setGridData("orgidList", data);
			var json = Ta.util.obj2string(data);
			$("#orgs").val(json);
		}
	}
	function fnDelOrgFromOrgList() {
		Base.deleteGridSelectedRows("orgidList");
		var data = Base.getGridData("orgidList");
		var json = Ta.util.obj2string(data);
		$("#orgs").val(json);
	}
	function fnSaveSuccCb() {
		if (confirm("保存成功，是否继续新增人员？")) {
			Base.resetForm("userForm");
			Base.focus("name");
		} else {
			Base.closeWindow('win');
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>