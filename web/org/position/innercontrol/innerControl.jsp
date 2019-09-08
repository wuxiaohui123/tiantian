<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>内控查询</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
<body class="no-scrollbar" style="padding:0px;margin:0px;">
	<ta:pageloading/>
	<ta:box fit="true">
		<ta:fieldset key="过滤条件" id="field1"  cols="5">
			<%-- 1.谁给谁新增了什么岗位 2.谁被谁新增了什么岗位 3.某一个功能都授予了哪些人，谁授予的--%>
			<ta:selectInput id="innerType" key="内控类型"  required="true" data="[{'id':'0','name':'授权行为'},{'id':'1','name':'权限变动'},{'id':'2','name':'功能授权'}]" onSelect="fnSelect" textHelp="选择后将在此处做出解释"></ta:selectInput>
			<ta:box span="4" cols="4" id="pop1" cssStyle="display:none;" >
				<ta:text id="adminPop" key="管理员" required="true" popWin="true" popWinWidth="700" popWinHeight="500" popWinUrl="innerControlAction!adminPop.do"></ta:text>
				<ta:text id="adminPositionId" display="false"></ta:text>
				<ta:text id="businessPop" key="业务人员" required="true" popWin="true" popWinWidth="700" popWinHeight="500" popWinUrl="innerControlAction!businessPop.do"></ta:text>
				<ta:text id="businessPositionId" display="false"></ta:text>
				<%-- <ta:text id="menuPop" key="功能菜单" required="true" popWin="true" popWinWidth="700" popWinHeight="500" popWinUrl="innerControlAction!menuPop.do"></ta:text>
				<ta:text id="menuId" display="false"></ta:text> --%>
				<ta:selectTree targetId="menuid" treeId="menuTree" url="innerControlAction!queryMenus.do" targetDESC="menuname" asyncParam="['menuid']" key="功能菜单" idKey="menuid"
					nameKey="menuname" parentKey="pmenuid" required="true" cssStyle="width:200px"></ta:selectTree>
				<ta:date id="startDate" key="开始时间" showSelectPanel="true" datetime="true"></ta:date>
				<ta:date id="endDate" key="结束时间" showSelectPanel="true" datetime="true"></ta:date>
				<ta:text id="positionPop" key="岗位" required="true" popWin="true" popWinWidth="700" popWinHeight="500" popWinUrl="innerControlAction!positionPop.do" ></ta:text>
				<ta:text id="positionId" display="false"></ta:text>
			</ta:box>
			<ta:buttonLayout span="5" id="btn1" cssStyle="display:none;">
				<ta:button key="查询" onClick="fnQuery()"></ta:button>
			</ta:buttonLayout>
		</ta:fieldset>
		<ta:tabs fit="true" hasBorder="false" headPlain="false" >
			<ta:tab key="业务人员" id="adminTab">
				<ta:datagrid fit="true" haveSn="true" id="grid1" columnFilter="true">
					<ta:datagridItem id="name" key="姓名" width="150"/>
					<ta:datagridItem id="loginid" key="登录名" width="150"/>
					<ta:datagridItem id="sex" key="性别" width="80" collection="sex"/>
					<ta:datagridItem id="orgnamepath" key="所在组织" width="300"/>
					<ta:datagridItem id="optime" key="操作时间" width="150"/>
				</ta:datagrid>
			</ta:tab>
			<ta:tab key="某岗被某人授予" id="businessTab">
				<ta:datagrid fit="true" haveSn="true" id="grid2" columnFilter="true">
					<ta:datagridItem id="usernamepath" key="授权管理员" width="400" showDetailed="true"/>
					<ta:datagridItem id="orgnamepath" key="被授权岗位" width="400" showDetailed="true"/>
					<ta:datagridItem id="optime" key="操作时间" width="150"/>
				</ta:datagrid>
			</ta:tab>
			<ta:tab key="该功能被谁授予给某岗位" id="menuTab">
				<ta:datagrid fit="true" haveSn="true" id="grid3" columnFilter="true">
					<ta:datagridItem id="usernamepath" key="授权管理员" width="400" showDetailed="true"/>
					<ta:datagridItem id="orgnamepath" key="被授权岗位" width="400" showDetailed="true"/>
					<ta:datagridItem id="optime" key="操作时间" width="150"/>
				</ta:datagrid>
			</ta:tab>
		</ta:tabs>
	</ta:box>
</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
});
function fnSelect(value,key){
	Base.showObj("pop1,btn1");
	switch(key){
		case "0":
			Base.showObj("adminPop,positionPop");
			Base.hideObj("businessPop,menuname");
			Base.enableTab("adminTab");
			Base.activeTab("adminTab");
			Base.disableTab("businessTab");
			Base.disableTab("menuTab");
			$("#innerType_div").parent().next().children(".textInfo_content").html("授权行为:某个管理员在某个时间段将某个岗位授予给了某个人");
			break;
		case "1":
			Base.showObj("businessPop");
			Base.hideObj("adminPop,menuname,positionPop");
			Base.enableTab("businessTab");
			Base.activeTab("businessTab");
			Base.disableTab("adminTab");
			Base.disableTab("menuTab");
			$("#innerType_div").parent().next().children(".textInfo_content").html("权限变动:某个业务人员在某个时间段被某个管理员授予了某个岗位");
			break;
		case "2":
			Base.showObj("menuname");
			Base.hideObj("adminPop,businessPop,positionPop");
			Base.enableTab("menuTab");
			Base.activeTab("menuTab");
			Base.disableTab("adminTab");
			Base.disableTab("businessTab");
			$("#innerType_div").parent().next().children(".textInfo_content").html("功能授权:某个功能菜单在某个时间段被某个管理员分配给了某个岗位");
			break;
	}
}
function fnQuery(){
	var d = Base.getValue("innerType");
	switch(d){
		case "0":
			Base.submit("field1","innerControlAction!queryLogByAdmin.do");
			break;
		case "1":
			Base.submit("field1","innerControlAction!queryLogByBusiness.do");
			break;
		case "2":
			Base.submit("field1","innerControlAction!queryLogByMenu.do");
			break;
	}
	
}
</script>
<%@ include file="/ta/incfooter.jsp"%>