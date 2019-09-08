<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>负责人</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;">
	<ta:pageloading/>
	<ta:box layout="border" layoutCfg="{leftWidth:240}" >
		<ta:box position="left" key="查询" cssStyle="padding:0 10px;" cssClass="left-themes-color" >
			<ta:box fit="true">
				<ta:form id="addAdminForm" fit="true" >
					<ta:text id="loginid" key="登录号" labelWidth="70"/>
					<ta:text id="username" key="姓名" labelWidth="70"/>
					<ta:selectTree targetId="orgid" treeId="orgTree"  cssStyle="width:200px"  labelWidth="70" url="positionMgAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']" 
						idKey="orgid" parentKey="porgid" nameKey="orgname" targetDESC="orgname" key="部门" fontCss="fnFontCss" selectTreeBeforeClick="fnBeforeClick"/>
					<ta:radiogroup id="isSubOrgUsers" key="子部门人员" cols="2" labelWidth="70">
						<ta:radio key="显示" value="0"/>
						<ta:radio key="不显示" checked="true" value="1"/>
					</ta:radiogroup>
					<ta:buttonLayout>
						<ta:button key="查询"  onClick="fnSearch()" isok="true" icon="xui-icon-query"/>
						<ta:button key="重置"  onClick="Base.resetForm('addAdminForm')" icon="xui-icon-reset" />
					</ta:buttonLayout>
				</ta:form>
			</ta:box>
		</ta:box>
		<ta:box position="center" key="人员列表(非管理员)">
			<ta:panel fit="true" withButtonBar="true">
				<ta:datagrid fit="true" id="userGrid" haveSn="true" selectType="checkbox"  forceFitColumns="true">
					<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true" asKey="true"></ta:datagridItem>
					<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
					<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
					<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
					<ta:datagridItem id="orgnamepath" key="所属组织路径" width="300"></ta:datagridItem>
					<ta:dataGridToolPaging url="adminMgAction!queryNoAdminUsers.do" pageSize="200" showExcel="false" submitIds="addAdminForm"></ta:dataGridToolPaging>
				</ta:datagrid>
				<ta:panelButtonBar align="center">
					<ta:button key="选择"  onClick="fnDetachAdd()" isok="true" icon="icon-add1"/>
					<ta:button key="返回"  onClick="parent.Base.closeWindow('addAdmin')" icon="icon-no"/>
				</ta:panelButtonBar>
			</ta:panel>
		</ta:box>
	</ta:box>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
});
function fnSearch() {
	Base.submit("addAdminForm","adminMgAction!queryNoAdminUsers.do");
}
function fnDetachAdd(){
	var sdata = Base.getGridSelectedRows("userGrid");
	if(sdata && sdata.length > 0){
		Base.confirm("确定批量新增这些人员为管理员?",function(yes){
			if(yes){
				Base.submit("userGrid","adminMgAction!addDetachAdminMgUser.do",null,null,null,function(){
					parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>新增管理员成功</div>");
					//Base.deleteGridSelectedRows("userGrid");
					parent.Base.closeWindow("addAdmin");
				});
			}
		})
	}else{
		Base.alert("请选择人员后再进行操作","warn");
	}
}
//新增管理员的组织树渲染
function fnFontCss(treeId,treeNode){
	if(treeNode.effective == 1 && !treeNode.admin){
		return {'text-decoration':'line-through','color': 'red'};
	}else if(treeNode.effective == 1 && treeNode.admin){
		return {'text-decoration':'line-through'};
	}else if(treeNode.effective != 1 && !treeNode.admin){
		return {'color': 'red'};
	}else{
		return {};
	} 
}
//新增管理员界面权限判断
function fnBeforeClick(treeId,treeNode){
	if(treeNode.effective == 1){ 
		Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
		return false;
	}
	if(treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
		return false;
	}else{
		return true;
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>