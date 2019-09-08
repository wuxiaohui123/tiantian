<%@page import="com.yinhai.ta3.system.org.domain.ManagerMg"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
String managerTypeChief = ManagerMg.MANAGER_TYPE_CHIEF;
String managerTypeDeputy = ManagerMg.MANAGER_TYPE_DEPUTY;
%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>负责人</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:0px;margin:0px;">
		<ta:pageloading/>
		<ta:box fit="true" layout="border" layoutCfg="{leftWidth:300}">
			<ta:box position="left" key="查询">
				<ta:box fit="true" heightDiff="40" id="userInfo" cssStyle="padding:10px">
					<ta:text id="managerType" display="false"/>
					<ta:text id="chief" display="false"/>
					<ta:text id="deputies" display="false"/>
					<ta:text id="loginid" key="登录号" labelWidth="50"/>
					<ta:text id="username" key="姓名" labelWidth="50"/>
					<ta:selectTree url="orgMgAction!webQueryAsyncOrgTree.do.do" labelWidth="50" idKey="orgid" nameKey="orgname" parentKey="porgid" asyncParam="['orgid']" fontCss="fnFontCss"
						 targetDESC="orgname" treeId="orgTree" targetId="orgid" key="部门" selectTreeBeforeClick="fnBeforeClick"/>
				</ta:box>
				<ta:buttonLayout align="center">
					<ta:button key="查询" isok="true" onClick="fnClick()" icon="xui-icon-query"/>
					<ta:button key="重置"  onClick="fnReset()" icon="xui-icon-reset"/>
				</ta:buttonLayout>
			</ta:box>
			<ta:box position="center" key="人员列表">
				<ta:panel fit="true"  withButtonBar="true">
					<ta:datagrid fit="true" id="grid1" haveSn="true"  selectType="checkbox" forceFitColumns="true" >
						<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
						<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
						<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所属组织路径" width="300" showDetailed="true"></ta:datagridItem>
					</ta:datagrid>
					<ta:panelButtonBar align="center">
						<ta:button key="保存"  isok="true" onClick="fnSave()" icon="icon-add1"/>
						<ta:button key="关闭" icon="icon-no" onClick="fnClose()"/>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
		</ta:box>
		<%-- <ta:box height="80%" cols="2">
			<ta:box height="90%">
				<ta:panel fit="true" key="人员列表" withButtonBar="true">
					<ta:datagrid fit="true" id="grid1" haveSn="true"  selectType="checkbox" forceFitColumns="true" >
						<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
						<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
						<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所属组织路径" width="300" showDetailed="true"></ta:datagridItem>
					</ta:datagrid>
					<ta:panelButtonBar align="center">
						<ta:button id="btnSelect" key="选择" icon="xui-icon-next" onClick="fnSelect()" />
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
			<ta:box height="90%">
				<ta:panel key="已选择人员" fit="true" withButtonBar="true">
					<ta:datagrid fit="true" id="grid2" haveSn="true"  selectType="checkbox" forceFitColumns="true" >
						<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true"></ta:datagridItem>
						<ta:datagridItem id="loginid" key="登录号"></ta:datagridItem>
						<ta:datagridItem id="name" key="姓名"></ta:datagridItem>
						<ta:datagridItem id="sex" key="性别" collection="sex"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所属组织路径" width="300" showDetailed="true"></ta:datagridItem>
					</ta:datagrid>
					<ta:panelButtonBar align="center">
						<ta:button id="btnDelete" key="移除" icon="xui-icon-back" onClick="fnDelete()"/>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
		</ta:box> --%>
		<%-- <ta:box height="10%">
			<ta:buttonLayout align="center">
				<ta:button key="保存"  isok="true" onClick="fnSave()"/>
				<ta:button key="关闭"  onClick="fnClose()"/>
			</ta:buttonLayout>
		</ta:box> --%>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		Ta.autoPercentHeight();
		fnClick();
	});
	function fnClick(){
		Base.submit("userInfo,grid1","orgMgAction!webGetUserInfo.do",{},null,null,function(data){
			if(judgeManagerType()){
				var chief = Base.getValue("chief");
				if(chief){
					Base.setSelectRowsByData("grid1",[{"positionid":chief}]);
				}
			}else{
				var arr = [];
				var deputies = Base.getValue("deputies");
				if(deputies){
					deputies = deputies.split(",");
					for(var i = 0 ; i < deputies.length; i++){
						arr.push({"positionid":deputies[i]});
					}
					Base.setSelectRowsByData("grid1",arr);
				}
			}
			//Base.setSelectRowsByData("grid1",arr);
		});
	}
	function fnReset(){
		$("#userInfo input").val("");
	}
	function fnSelect(){
		var sData = Base.getGridSelectedRows("grid1");
		if(sData && sData.length > 0){
			if(judgeManagerType()){
				if(sData.length > 1){
					Base.alert("正职负责人只能有一位");
				}else{
					var sData2= Base.getGridData("grid2");
					if(sData2 && sData2.length > 0){
						Base.alert("正职负责人只能有一位，请先移除正职负责人");
						return;
					}
					var data = sData[0];
					var param = {};
					param.positionid = data.positionid;
					param.loginid = data.loginid;
					param.name = data.name;
					param.sex = data.sex;
					param.orgnamepath = data.orgnamepath;
					Base.addGridRowDown("grid2",param);
					Base.deleteGridSelectedRows("grid1");
					
				}
			}else{//副职，则可以有多个负责人
				
				for(var i = 0 ; i < sData.length; i++){
					var param = {};
					var data = sData[i];
					param.positionid = data.positionid;
					param.loginid = data.loginid;
					param.name = data.name;
					param.sex = data.sex;
					param.orgnamepath = data.orgnamepath;
					Base.addGridRowDown("grid2",param);
					Base.deleteGridSelectedRows("grid1");
				}
				
			}
		}else{
			if(judgeManagerType()){
				parent.Base.setValue("orgmanager_name","");
				parent.Base.setValue("orgmanager","");
				parent.Base.closeWindow("w_orgmanager_name");
			}else{
				parent.Base.setValue("orgmanager_deputy_name","");
				parent.Base.setValue("orgmanager_deputy","");
				parent.Base.closeWindow("w_orgmanager_deputy_name");
			}
		}
	}
	function fnClose(){
		if(judgeManagerType()){
			parent.Base.closeWindow("w_orgmanager_name");
		}else{
			parent.Base.closeWindow("w_orgmanager_deputy_name");
		}
	}
	function fnDelete(){
		var sData = Base.getGridSelectedRows("grid2");
		if(sData && sData.length > 0){
			for(var i=0; i < sData.length; i++){
				var param = {};
				var data = sData[i];
				param.positionid = data.positionid;
				param.loginid = data.loginid;
				param.name = data.name;
				param.sex = data.sex;
				param.orgnamepath = data.orgnamepath;
				Base.addGridRowDown("grid1",param);
				Base.deleteGridSelectedRows("grid2");
			}
		}
	}
	function fnSave(){
		var datas = Base.getGridSelectedRows("grid1");
		if(judgeManagerType()){
			if(datas){
				if(datas.length != 1){
					Base.msgTopTip("<div class='msgTopTip'>正职人员只能有一位</div>");
				}else{
					var data = datas[0];
					parent.Base.setValue("orgmanager_name",data.name);
					parent.Base.setValue("orgmanager",data.positionid);
					parent.Base.closeWindow("w_orgmanager_name");
				}
			}else{
				parent.Base.setValue("orgmanager_name","");
				parent.Base.setValue("orgmanager","");
				parent.Base.closeWindow("w_orgmanager_name");
			}
		}else{
			if(datas){
				var d_name = [],d=[];
				for(var i = 0 ; i < datas.length; i++){
					d_name.push(datas[i].name);
					d.push(datas[i].positionid);
				}
				parent.Base.setValue("orgmanager_deputy_name",d_name.join(","));
				parent.Base.setValue("orgmanager_deputy",d.join(","));
				parent.Base.closeWindow("w_orgmanager_deputy_name");
			}else{
				parent.Base.setValue("orgmanager_deputy_name","");
				parent.Base.setValue("orgmanager_deputy","");
				parent.Base.closeWindow("w_orgmanager_deputy_name");
			}
		}
	}
	function judgeManagerType(){
		var managerType = Base.getValue("managerType");
		if(managerType == <%=managerTypeChief%>){
			return true;
		}else{
			return false;
		}
	}
	//组织树渲染
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
	//权限判断
	function fnBeforeClick(treeId,treeNode){
		if(treeNode.effective == 1){ 
			parent.Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
			return false;
		}
		if(treeNode.admin != true){
			parent.Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
			return false;
		}else{
			return true;
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>