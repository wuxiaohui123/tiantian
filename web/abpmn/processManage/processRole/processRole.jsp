<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>列表对话框</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:400}">
	<ta:pageloading />
	<ta:box id="myfieldset" key="流程角色" position="left" cssStyle="padding:10px;">
		<ta:buttonLayout align="left">
			<ta:button key="新增角色" onClick="fnAddGroup()"></ta:button>
			<ta:button key="删除角色" onClick="fnDeleteGroup()"></ta:button>
			<ta:button key="添加人员" onClick="fnAddUser()"></ta:button>
	    	<ta:button key="删除人员" onClick="fnDelUser()"></ta:button>
		</ta:buttonLayout>
		<ta:panel id="group" fit="true" withToolBar="true">
		   <ta:listView id="myGroup" width="100%" height="100%" hasSearch="true" itemClickBgColor="#94e4c2" isAutoSearch="true" itemDisplayTemplate="{groupname}（ID:{groupid}）"
		   searchKey="groupid,groupname" itemIcon="menu_tree-group" itemClick="fnClick"/>
		</ta:panel>
	</ta:box>
	<ta:box position="center" cssStyle="padding:10px">
		<ta:panel key="流程人员"  fit="true">
			<ta:datagrid id="puserGrid" fit="true" haveSn="true" selectType="checkbox" forceFitColumns="true">
				<ta:datagridItem id="groupid" key="GROUPID" hiddenColumn="true"/>
				<ta:datagridItem id="id" key="ID" hiddenColumn="true"/>
				<ta:datagridItem id="name" key="姓名" width="80"/>
				<ta:datagridItem id="sex" key="性别" collection="SEX" width="50"/>
				<ta:datagridItem id="dept" key="部门" width="120"/>
				<ta:datagridItem id="email" key="Email" width="120"/>
				<ta:datagridItem id="tel" key="联系电话" width="120"/>
			</ta:datagrid>
		</ta:panel>
	</ta:box>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
	});
	function fnAddGroup(){
		Base.openWindow("win","新增流程角色","processRoleAction!toAddGroup.do",null,"300","200",null,function(){
			fnQueryGroup();
		},true);
	}
	function fnDeleteGroup(){
		if(groupId){
			Base.submit("","processRoleAction!deleteGroup.do",{"dto['groupId']":groupId},null,false,function(){
				fnQueryGroup();
			});
		}else{
			Base.alert("请选择角色！");
		}
	}
	var groupId = "";
	function fnClick(rowdata){
		groupId = rowdata.groupid;
		Base.submit("puserGrid","processRoleAction!queryRoleUsers.do",{"dto['groupid']":groupId});
	}
	
	function fnAddUser(){
		if(groupId){
			Base.openWindow("win","新增流程人员","processRoleAction!toAddUser.do",
					{"dto['groupId']":groupId},"600","400",null,function(){
						Base.submit("puserGrid","processRoleAction!queryRoleUsers.do",{"dto['groupid']":groupId});
			},true);	
		}
	}
	function fnDelUser(){
		Base.confirm("确定删除选择的流程用户？",function(yes){
			if(yes){
				Base.submit("puserGrid","processRoleAction!deleteRoleUsers.do",null,null,false,function(){
					Base.submit("","processRoleAction!queryRoleUsers.do",{"dto['groupid']":groupId});
				});
			}
		});
		
	}
	function fnQueryGroup(){
		Base.getJson("processRoleAction!getGroups.do",{},function(data){
			var grouplist = data.fieldData.myGroup;
			Base.setListViewData("myGroup",JSON.parse(grouplist));
		}); 
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>
