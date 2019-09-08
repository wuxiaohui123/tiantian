<%@ page import="com.yinhai.sysframework.codetable.service.CodeTableLocator"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.page import="com.yinhai.sysframework.util.WebUtil"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>人员管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:0px;margin:0px">
		<ta:box cols="2">
			<ta:buttonLayout columnWidth="0.45" align="left">
				<ta:button asToolBarItem="true" id="addBtn" key="新增[A]" hotKey="A" icon="icon-adduser" onClick="addUser()"/>
				<ta:button asToolBarItem="true" id="delBtn" key="删除[D]" hotKey="D" icon="btn-delete" disabled="true" onClick="fnDelUser()"/>
<!-- 				<ta:button asToolBarItem="true" id="diabledBtn" key="禁用[E]" hotKey="E" icon="icon-edit" disabled="true" onClick="fnDisUser()"/> -->
				<ta:toolbarItemSeperator/>
				<ta:button asToolBarItem="true" id="resetPassBtn" key="密码重置[P]" hotKey="P" disabled="true" onClick="fnResetPass()"/>
				<ta:button asToolBarItem="true" id="changeDeptBtn" key="批量岗位设置" hotKey="C" icon="icon-organization" onClick="fnBatchPositionSet()"/>
			</ta:buttonLayout>
			<ta:box cols="3" id="queryForm" cssStyle="margin-top: 6px" columnWidth="0.55">
				<ta:selectTree url="#" key="部门" targetDESC="orgname" treeId="orgTree" targetId="orgid" />
				<ta:text id="name" key="用户姓名"/>
				<ta:button key="查询" icon="icon-search" onClick="fnQueryUsers()"/>
			</ta:box>
		</ta:box>
		<ta:panel position="center" key="人员信息" id="userPanel" cssStyle="margin:0 5px 0 5px" expanded="false" fit="true">
			<ta:text id="departId" display="false" />
			<ta:datagrid id="userGd" fit="true" >
				<ta:datagridItem id="userid" asKey="true" key="userid" hiddenColumn="true"/>
				<ta:datagridItem id="loginid" key="登录账号" width="150" showDetailed="true"/>
				<ta:datagridItem id="name" key="姓名" showDetailed="true" width="150"/>
				<ta:datagridItem id="sex" key="性别" width="150" align="center" dataAlign="center" collection="SEX"></ta:datagridItem>
				<ta:datagridItem id="tel" key="移动电话" width="150" align="center" dataAlign="center" ></ta:datagridItem>
				<ta:datagridItem id="setting" key="岗位设置" icon="icon-setting" width="150" align="center" dataAlign="center" ></ta:datagridItem>
				<ta:datagridItem id="editor" key="修改用户" width="150" align="center" dataAlign="center" ></ta:datagridItem>
<!-- 				<ta:datagridItem id="del" key="删除用户" width="150" align="center" dataAlign="center" ></ta:datagridItem> -->
			</ta:datagrid>
<!-- 		    <table class="easyui-datagrid" id="userGd" fit='true' data-options="onClickCell:onUserGdCellClick,rownumbers: true, singleSelect: false, url: 'userMgAction!query.do', method: 'get', onLoadSuccess: onLoadSuccess"> -->
<!-- 		        <thead> -->
<!-- 		            <tr> -->
<!-- 		            	<th data-options="field:'ck',checkbox:true"></th> -->
<!-- 		                <th data-options="field:'userid',width:100,hidden:true">userid</th> -->
<!-- 		                <th data-options="field:'loginid',width:100">登录账号</th> -->
<!-- 		                <th data-options="field:'name',width:80">姓名</th> -->
<!-- 		                <th data-options="field:'orgnamepath',width:300,align:'left'">组织视图</th> -->
<!-- 		                <th data-options="field:'positionname',width:200,align:'left'">岗位列表</th> -->
<!-- 		                <th data-options="field:'positiontype',width:200,align:'left',formatter:formatPositionTypeCollection">岗位类型</th> -->
<!-- 		                <th data-options="field:'sex',width:60,align:'center',formatter:formatSexCollection">性别</th> -->
<!-- 		                <th data-options="field:'tel',width:140">移动电话</th> -->
<!-- 		                <th data-options="field:'setting',width:60,align:'center',formatter:formatSettingIcon">岗位设置</th> -->
<!-- 		                <th data-options="field:'editor',width:60,align:'center',formatter:formatEditorIcon">编辑</th> -->
<!-- 		                <th data-options="field:'del',width:60,align:'center',formatter:formatRemoveIcon">权限查看</th> -->
<!-- 		            </tr> -->
<!-- 		        </thead> -->
<!-- 		    </table> -->
		    <script type="text/javascript">
		        function onLoadSuccess(data){
		        	var loginidMain = null;
		        	var index = 0;
		        	var rowspan = 0;
		        	var merges = [];
		        	for (var i = 0; i < data["rows"].length; i ++) {
		        		if (loginidMain == null) loginidMain = data["rows"][i].loginid;
		        		if (loginidMain == data["rows"][i].loginid){
		        			rowspan ++;
		        		} else {
		        			if (rowspan != 1){
		        				merges.push({rowspan:rowspan,index:index});
		        			}
		        			loginidMain = data["rows"][i].loginid;
		        			rowspan = 1;
			        		index = i;
		        		}
		        		if (data["rows"].length - 1 == i  && rowspan != 1) merges.push({rowspan:rowspan,index:index});
		        	}
		        	<%-- 合并单元格 --%>
		        	var mergeColumns = ['ck','loginid', 'name', 'editor', 'del', 'sex', 'setting', 'tel']
		            for(var i=0; i<merges.length; i++){
		            	for (var aIndex in mergeColumns){
			                $(this).datagrid('mergeCells',{
			                    index: merges[i].index,
			                    field: mergeColumns[aIndex],
			                    rowspan: merges[i].rowspan
			                });
		            	}
		            }
		        }
		        function formatEditorIcon(){
		        	return "<center><div title='单击' class='icon-edit' style='cursor:pointer;height:16px;width:16px;margin-top:3px'></div></center>";
		        }
		        function formatRemoveIcon(){
		        	return "<center><div title='单击' class='icon-remove' style='cursor:pointer;height:16px;width:16px;margin-top:3px'></div></center>";
		        }
		        function formatSettingIcon(){
		        	return "<center><div title='单击' class='icon-setting' style='cursor:pointer;height:16px;width:16px;margin-top:3px'></div></center>";
		        }
		        function formatSexCollection(val, row) {
		        	var collection = <%= CodeTableLocator.getCodeListJson("SEX", WebUtil.getUserInfo(request).getYab003())%>;
		        	for (var i = 0; i < collection.length; i ++) {
		        		if (val == collection[i].id) {
		        			return collection[i].name;
		        		}
		        	}
		        }
		        function formatPositionTypeCollection(val, row) {
		        	var collection = <%= CodeTableLocator.getCodeListJson("POSITIONTYPE", WebUtil.getUserInfo(request).getYab003())%>;
		        	for (var i = 0; i < collection.length; i ++) {
		        		if (val == collection[i].id) {
		        			return collection[i].name;
		        		}
		        	}
		        }
		        function onUserGdCellClick(rowIndex, field, value) {
		        	if (field == "setting") {
		        		$("#userGd").datagrid("selectRow",rowIndex);
		        		var row = $("#userGd").datagrid("getSelected");
		        		function onClose(){
		        			fnQueryUsers();
		        		}
		        		Base.openWindow("win", "岗位设定", "userMgAction!toPosition.do", {"dto['userid']" :row.userid}, 800, 600, null, onClose);
		        	}
		        }
		    </script>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
});
<%-- 查询用户信息 --%>
function fnQueryUsers() {
	Base.submit("orgid,name", "userMgAction!query.do");
// 	$('#userGd').datagrid('load',{
// 		"dto['orgid']": Base.getValue("orgid"),
// 		"dto['name']": Base.getValue("name")
// 	});
}
function fnSlctChg(o) {
	if (o.length == 1) {
		Base.setEnable(["editBtn","delBtn","resetPassBtn","changeDeptBtn"]);
	} else if (o.length > 1) {
		Base.setEnable(["delBtn","resetPassBtn","changeDeptBtn"]);
		Base.setDisabled("editBtn");
	} else {
		Base.setDisabled(["editBtn","delBtn","resetPassBtn","changeDeptBtn"]);
	}
}
function fnBatchPositionSet() {
	var rows = $('#userGd').datagrid('getSelections');
	var submitRows = [];
	for (var i = 0; i < rows.length; i ++) {
		submitRows.push({'userid':rows[i].userid});
	}
	var substr = Ta.util.obj2string(submitRows);
	Base.openWindow("win", "批量授权", "userMgAction!toBatchPosition.do", {"users":substr}, 300, 600);
}
function addUser() {
	Base.openWindow("win", "人员新增", "userMgAction!toSave.do", null, 600, 500, null, 
			function(){
				$('#userGd').datagrid('load',{
					"dto['orgid']": Base.getValue("orgid"),
					"dto['name']": Base.getValue("name")
				});
			}
	);
}
function editUser() {
	var o = Base.getGridSelectedRows("userList");
	Base.openWindow("win", o[0].name + "->人员信息编辑", "userAction!toEdit.do", {"userId":o[0].userId}, 600, 460, null, function(){Base.submit('departId,isDisplayAllChilds', 'userAction!query.do');});
}
function fnResetPass() {
	var o = Base.getGridSelectedRows("userList");
	Base.openWindow("passWin", "密码重置", "userAction!toRestPass.do", {"userId":o[0].userId}, 280, 160);
}
function delUser() {
	var o = Base.getGridSelectedRows("userList");
	if (confirm(o.length > 1 ? "确实要删除这些人员吗？" : "确实要删除该人员吗？"))
		Base.submit("userList", "userAction!delete.do", null, false, false, function(){Base.deleteGridSelectedRows('userList');});
}
function fnGetGridSelectedData(){
	var o = [];
	o = Base.getGridSelectedRows("userList");
	if(o.length == 0){
		Base.alert("请选择一条数据");
		return;
	}else if(o.length > 1){
		Base.alert("只能选择一条数据");
		return;
	}else{
		o = o[0];
		return o;
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>