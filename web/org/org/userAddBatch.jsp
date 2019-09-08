<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
	<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:240}" style="padding:0px;margin:0px">
		<ta:form id="userForm" fit="true">
		<ta:panel id="userInfo" hasBorder="false" expanded="false" fit="true" bodyStyle="padding:10px 20px 10px 30px;" withButtonBar="true">
			<ta:fieldset key="人员基本信息" cols="2">
				<ta:text id="w_userid" key="用户编号" readOnly="true" span="2"/>
				<ta:text id="w_loginid" key="登录帐号" required="true" span="2" maxLength="10"/>
				<ta:text id="w_name" key="姓名" required="true"  maxLength="10"/>
				<ta:selectInput id="w_sex" key="性别" collection="SEX" required="true"/>
				<ta:text id="w_password" key="登录口令" display="none" required="true" span="2" type="password"/>
				<ta:text id="w_rpassword" key="确认口令" display="none" required="true" span="2" type="password" validType="compare(this.value, ['=', 'w_password'])"/>
				<ta:text id="w_yab003" key="分中心"/>
		<!-- 		<ta:checkbox key="是否锁定" id="islock" value="islock"/> -->
				<ta:text id="w_tel" key="移动电话" maxLength="11" validType="mobile"/>
			</ta:fieldset>
			<ta:panel key="所属组织" fit="true" withToolBar="true">
				<ta:panelToolBar cssStyle="height:auto;padding:0px">
					<ta:box cols="3">
						<ta:box span="2">
							<ta:selectTree url="" key="所属组织" nameKey="orgname" idKey="orgid" parentKey="porgid" targetDESC="w_orgname" treeId="w_orgTree" targetId="w_orgid"/>
						</ta:box>
						<ta:button asToolBarItem="true" key="添加" columnWidth="0.15" icon="icon-adduser" onClick="fnAddOrgToOrgList()"/>
						<ta:button asToolBarItem="true" key="删除" columnWidth="0.15" icon="icon-remove"  onClick="fnDelOrgFromOrgList()"/>
					</ta:box>
				</ta:panelToolBar>
				<ta:textarea id="orgs" display="none" readOnly="true"/>
				<ta:datagrid id="orgidList" fit="true" haveSn="true" forceFitColumns="true" selectType="checkbox">
					<ta:datagridItem id="orgname" key="组织名称" />
					<ta:datagridItem id="orgnamepath" key="组织路径" width="240" showDetailed="true"/>
					<ta:datagridItem id="orgtype" key="组织类型" collection="ORGTYPE" sortable="true" width="70"/>
				</ta:datagrid>
			</ta:panel>
			<ta:panelButtonBar>
				<ta:submit id="saveUserBtn" key="保存[S]" hotKey="S" icon="icon-add1" submitIds="userInfo" url="userMgAction!webBatchOrgsUserSave.do" successCallBack="fnSaveSuccCb"/>
				<ta:button id="closeWinBtn" key="关闭[X]" hotKey="X" icon="icon-no" onClick="Base.closeWindow('win');"/>
			</ta:panelButtonBar>
		</ta:panel>
		</ta:form>
	</body>
<script type="text/javascript">
$(document).ready(function () {
	//$("body").taLayout();
	Base.focus("name");
	Base.setValue("orgTree", "0");
})
/*添加org*/
function fnAddOrgToOrgList() {
	var data = Base.getGridData("orgidList");
	var node = $.fn.zTree.getZTreeObj("w_orgTree").getNodeByParam("orgid", Base.getValue("w_orgid"), null);
	if (node != null && node.orgid != null) {
		for (var i = 0; i < data.length; i ++) {
			if (node.orgid == data[i].orgid) {Base.msgTopTip("<div style='width: 200px;margin: auto 0;text-align: center;line-height: 100px;font-size: 20px;'>已添加</div>", 2200, 200, 100);return;}
		}
		var org = {};
		org.orgname = node.orgname;
		org.orgid = node.orgid;
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