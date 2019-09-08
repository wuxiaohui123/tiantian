<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>转移权限</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />

<ta:text id="positionid" display="false"/>
<ta:panel fit="true" hasBorder="false" withButtonBar="true">
	<ta:datagrid fit="true" id="userGrid" haveSn="true" selectType="radio">
		<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true" asKey="true"></ta:datagridItem>
		<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
		<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
		<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
		<ta:datagridItem id="orgnamepath" key="所属路径" width="300"></ta:datagridItem>
	</ta:datagrid>
	<ta:panelButtonBar align="right">
		<ta:button key="保存[S]" onClick="fnSave()" isok="true" icon="icon-add1" hotKey="s"/>
		<ta:button key="关闭[X]" icon="icon-no" onClick="parent.Base.closeWindow('transformWin')" hotKey="x"/>
	</ta:panelButtonBar>
</ta:panel>
</body>
</html>
<script type="text/javascript">
$(document).ready(function() {
		$("body").taLayout();
	}) 
function fnSave(){
	var sdata = Base.getGridSelectedRows("userGrid");
	if(sdata && sdata.length == 1){
		Base.submit("positionid,userGrid","adminUserMgAction!transformAuthority.do",null,false,false,function(){
			parent.Base.msgTopTip("<div style='width:130px;margin:0 auto;font-size:16px;margin-top:5px;text-align:center;'>转移权限成功</div>");
			parent.Base.closeWindow("transformWin");
			parent.Base.submit("","adminUserMgAction!queryAdminMgUsers.do"); 
		});
	}else{
		Base.alert("请选择后再进行转移权限操作","warn");
	}
}
</script>
<%@ include file="/ta/incfooter.jsp"%>