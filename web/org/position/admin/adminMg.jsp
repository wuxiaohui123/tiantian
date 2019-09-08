<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>管理员管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" >
		<ta:pageloading/>
		<ta:buttonGroup align="left">
			<ta:button key="新增管理员" onClick="addAdminMg()"   isok="true"/>
			<ta:buttonGroupSeparate/>
			<ta:selectButton key="批量使用权限操作">
				<ta:selectButtonItem key="授予权限" onClick="fnBatchPermissions(2)" />
				<ta:selectButtonItem key="回收权限" onClick="fnBatchPermissions(1)" />
			</ta:selectButton>
			<ta:selectButton key="批量授权权限操作">
				<ta:selectButtonItem key="授予权限" onClick="fnBatchPermissions(4)" />
				<ta:selectButtonItem key="回收权限" onClick="fnBatchPermissions(3)" />
			</ta:selectButton>
		</ta:buttonGroup>
		<ta:panel id="panel1" fit="true" hasBorder="false">
			<ta:datagrid fit="true" selectType="checkbox" id="adminMgGrid" haveSn="true"  columnFilter="true">
				<ta:datagridItem id="loginid" key="登录号" width="100"></ta:datagridItem>
				<ta:datagridItem id="name" key="姓名"  width="100"></ta:datagridItem>
				<ta:datagridItem id="orgnamepath" key="所属部门"  showDetailed="true" width="300"></ta:datagridItem>
				<ta:datagridItemOperate showAll="false" id="opt" name="操作">
					<ta:datagridItemOperateMenu name="管理功能权限" icon="a" click="fnAdminUsePermission"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="管理部门范围" icon="a" click="fnOrgMgScope"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="管理数据区范围" icon="a" click="fnYab003MgScope"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="授权权限设置" icon="a" click="fnRePermission"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="权限转移" icon="a" click="fnTransformAuthority"></ta:datagridItemOperateMenu>
					<ta:datagridItemOperateMenu name="移除管理员" icon="a" click="fnDeleteAdminMg"></ta:datagridItemOperateMenu>
				</ta:datagridItemOperate>
				<%-- <ta:datagridItem  key="功能管理权限" align="center" dataAlign="center" icon="icon-setting" width="100" click="fnAdminUsePermission"></ta:datagridItem>
				<ta:datagridItem  key="管理部门范围" align="center" dataAlign="center" icon="icon-organization" width="100" click="fnOrgMgScope"></ta:datagridItem>
				<ta:datagridItem  key="分中心管理范围" align="center" dataAlign="center" icon="icon-yhrole" width="100" click="fnYab003MgScope"></ta:datagridItem>
				<ta:datagridItem  key="授权权限" align="center" dataAlign="center" icon="icon-yhpurview" width="80" click="fnRePermission"></ta:datagridItem>
				<ta:datagridItem  key="转移权限" align="center" dataAlign="center" icon="icon-reload" width="80" click="fnTransformAuthority"></ta:datagridItem>
				<ta:datagridItem  key="移除管理员" align="center" dataAlign="center" icon="icon-remove" width="80" click="fnDeleteAdminMg"></ta:datagridItem> --%>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function addAdminMg(){
		Base.openWindow("addAdmin","新增管理员","adminMgAction!toAddAdminMgUser.do","","70%","90%",null,function(){
			Base.submit("","adminMgAction!queryAdminMgUsers.do");
		},true);
	}
	function fnAdminUsePermission(data,e){
		Base.openWindow("adminUseWin", data.name + "->功能管理权限", "adminMgAction!toFuncAdminUsePoermission.do", {"dto['positionid']":data.positionid,"dto['positionType']":2}, "35%", "80%");
	}
	function fnOrgMgScope(data,e){
		Base.openWindow("mgScope",data.name + "->部门管理范围","adminMgAction!toOrgMgScope.do",{"dto['positionid']":data.positionid},"35%","80%");
	}
	function fnYab003MgScope(data,e){
		Base.openWindow("adminYab003Win",data.name + "->数据区范围","adminMgAction!toAdminYab003Scope.do",{},"35%","80%",
			function(){
				Base.submit("","adminMgAction!queryTargetUserYab003Scope.do",{"dto['positionid']":data.positionid,"dto['userid']":data.userid},null,null,function(list){
					Base.setSelectRowsByData("yab003Grid",list.fieldData.dlist);
				});
			});
	}
	function fnRePermission(data,e) {
		Base.openWindow("grantingWin", data.name + "->功能授权权限", "adminMgAction!toFuncGrantingPurview.do", {"dto['positionid']":data.positionid,"dto['positionType']":2}, "70%", "80%");
	}
	function fnTransformAuthority(data,e) {
		Base.openWindow("transformWin",data.name + "->转移权限","adminMgAction!toTransformAuthority.do",{"dto['positionid']":data.positionid},"60%","90%");
	}
	function fnBatchPermissions(flag){
		var o = Base.getGridSelectedRows("adminMgGrid");	
		if(o.length == 0){
			Base.alert("请选择数据后再进行相关操作","warn");
			return false;
		}else{
			var str = "";
			for(var i = 0 ; i < o.length ; i++){
				str += "{'positionid':'"+o[i].positionid+"'},";
			}
			str ="["+ str.substring(0,str.length-1) + "]";
			switch (flag) {
			case 1:
				Base.openWindow("opWin","批量回收使用权限","<%=basePath%>org/position/adminMgAction!toRecyclePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%");
				break;
			case 2:
				Base.openWindow("opWin","批量授予使用权限","<%=basePath%>org/position/adminMgAction!toGrantUsePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%");
				break;
			case 3:
				Base.openWindow("grantingWin","批量回收授权权限","<%=basePath%>org/position/adminMgAction!toRecycleAuthorityPermissions.do",{"positionids":str,"dto['positionType']":2},"70%","80%");
				break;
			case 4:
				Base.openWindow("grantingWin","批量授予授权权限","<%=basePath%>org/position/adminMgAction!toGrantAuthorityPermissions.do",{"positionids":str,"dto['positionType']":2},"70%","80%");
				break;

			default:
				break;
			}
		}
	}
	function fnDeleteAdminMg(data,e){
		Base.confirm("确定删除管理员-->" + data.name + "?",function(yes){
			if(yes){
				Base.submit("","adminMgAction!removeAdminMgUser.do",{"dto['positionid']":data.positionid},null,null,function(){
					Base.msgTopTip("<div class='msgTopTip'>移除管理员成功</div>");
// 					Base.deleteGridRow("adminMgGrid",data.row);
				})
			}
		})
	}
	function fnSetFont(treeId, treeNode) {
		if(treeNode.policy == 4 || treeNode.policy == 3)
			return {color:"red"};
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>