<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>新增修改岗位</title>
		<%@ include file="/ta/inc.jsp"%>
		
	</head>
	<body id="body1" class="no-scrollbar" style="margin:0;padding:0">
		<ta:pageloading/>
		
		<ta:form fit="true" id="form1">
			<ta:panel fit="true" hasBorder="false" expanded="false" withButtonBar="true" cols="1">
				<ta:selectTree url="positionMgAction!webQueryAsyncOrgTree.do" fontCss="fnFontCss" idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
					selectTreeBeforeClick="fnBeforeClick"  targetDESC="orgname" treeId="orgTree" targetId="orgid" key="部门" height="200"/>
				<ta:text id="positionname" key="名称" required="true"/>
				<ta:selectInput id="positioncategory" key="岗位类别" collection="positioncategory" required="true" ></ta:selectInput>
				<%-- 编辑行positionid --%>
				<ta:text id="editpositionid" display="false"/>
				<ta:text id="newPositionId" display="false" />
				<%-- 新增岗位扩展jsp --%>
				<%@include file="/org/orgextend/positionMgExtend.jsp" %>
				<ta:panelButtonBar>
					<ta:button id="save"  key="保存" icon="icon-add1" onClick="fnSave()" isok="true"/>
					<ta:button id="saveEdit"  key="保存" icon="icon-add1" onClick="fnSaveEdit()" display="false" isok="true"/>
					<ta:button id="remove"  key="关闭" icon="icon-no" onClick="fnClose()"/>
				</ta:panelButtonBar>
			</ta:panel>
		</ta:form>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnBeforeClick(treeId, treeNode){
		var isadmin = treeNode.admin;
		if(treeNode.effective == 1){
			parent.Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能在该组织下添加/修改岗位</div>");
			return false;
		}
		if(isadmin && (isadmin==true || isadmin=="true")){
			return true;
		}else{
			parent.Base.msgTopTip("<div class='msgTopTip'>您没有该部门的管理权限,无法在该部门下新增/转移岗位</div>",3000);
			return false;
		}
	}
	function fnClose(){
		parent.Base.closeWindow("addOrEditWin");
	}
	function fnSave(){
		Base.submit("form1","<%=basePath%>org/position/positionMgAction!createPosition.do",null,function(){
				if(Base.getValue("orgname")){
					return true
				}else{
					Base.alert("请选择部门","warn");
					return false
				}
			},true,function(){
				parent.Base.msgTopTip("<div class='msgTopTip'>新增部门成功</div>",2000);
				fnClose();
		});
	}
	function fnSaveEdit(){
		Base.submit("form1","<%=basePath%>org/position/positionMgAction!updatePosition.do",null,null,false,function(data){
			parent.Base.alert("修改岗位成功","success");
			parent.Base.closeWindow("addOrEditWin");
		});
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
	function fnAdd(){
		Base.resetForm("form1");
		Base.hideObj("goOnbtn,operBtn");
	}
// 	function fnAssignUser(){
// 		parent.Base.openWindow("assignUser",Base.getValue("positionname") + "->人员选择","<%=basePath%>org/position/positionMgAction!toAssignUser.do",{"dto['positionid']":Base.getValue("newPositionId")},"90%","90%",null,null,true);
// 	}
	function fnUsePermission(){
		parent.Base.openWindow("opWin", Base.getValue("positionname") + "->功能使用权限", "<%=basePath%>org/position/positionMgAction!toFuncOpPurview.do", {"dto['positionid']":Base.getValue("newPositionId"),"dto['positionType']":1}, "35%", "80%");
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>