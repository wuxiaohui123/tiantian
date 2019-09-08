<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html>
	<head>
		<title>新增组织</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:form id="orgForm" fit="true">
		<ta:panel id="orgInfo" hasBorder="false" expanded="false" scalable="false" fit="true"
		bodyStyle="padding:10px 20px 10px 10px;overflow:auto;" withButtonBar="true">
			<ta:box id="departPanel" cssStyle="padding:10px 20px 0px 10px;" fit="true">		
			    <ta:text id="porgid" key="父级组织id" display="false" />
			    <ta:text id="orgid" key="组织id" display="false" />
			    <ta:text id="orglevel" key="父组织层级" display="false" />
			    <ta:text id="isleaf" key="父组织是否为叶子" display="false" />
			    <ta:text id="porgname" key="父级组织" readOnly="true" columnWidth="0.6" span="2" labelWidth="115" />
			    <ta:text id="orgnamepath" key="组织路径" readOnly="true" columnWidth="0.6" span="2" disabled="true" labelWidth="115"/>
				<ta:text id="orgname" key="组织名称" maxLength="60"  required="true" columnWidth="0.6" span="2"  labelWidth="115"/>
				<ta:box span="2" columnWidth="0.6" cols="2">
					<ta:text id="costomno" key="自定义编码" columnWidth="0.5"  maxLength="20" labelWidth="115"  validType="compare(this.value, ['>', 'maxUdi'])"/>
					<ta:text id="maxUdi" key="已使用最大编号" columnWidth="0.5" readOnly="true" maxLength="20" labelWidth="110" />
				</ta:box>
				<ta:selectInput id="orgtype" key="组织类型"  span="2" columnWidth="0.6"  collection="ORGTYPE" required="true" labelWidth="115" onSelect="fnOrgTypeSelect"/>
<!-- 				<ta:text id="orgmanager"  display="false"/> -->
<!-- 				<ta:text id="orgmanager_name" key="组织负责人(正职)" columnWidth="0.6" span="2" labelWidth="115" readOnly="true" popWinBeforeClick="fnPopWinBeforeClick" popWin="true" popWinUrl="orgMgAction!webSelectOrgManager.do" popWinWidth="80%" popWinHeight="80%" popParam="{'managerType':'1'}" popSubmitIds="orgmanager,orgmanager_deputy"/> -->
<!-- 				<ta:text id="orgmanager_deputy" display="false"/> -->
<!-- 				<ta:text id="orgmanager_deputy_name" key="组织负责人(副职)" columnWidth="0.6" span="2" labelWidth="115" readOnly="true" popWinBeforeClick="fnPopWinBeforeClick" popWin="true" popWinUrl="orgMgAction!webSelectOrgManager.do" popWinWidth="80%" popWinHeight="80%" popParam="{'managerType':'2'}" popSubmitIds="orgmanager,orgmanager_deputy"/> -->
				<ta:selectData labelWidth="115" tipContent="输入姓名后查询" defaultId="positionid" id="positionnamelike"  submitid="orgmanager" defaultName="positionname" key="组织负责人(正职)" columnWidth="0.6" span="2" url="orgUserMgAction!queryLikeZhengzhi.do" inputQueryNum="1" titleId="orgnamepath"></ta:selectData>
				<ta:selectData labelWidth="115" tipContent="输入姓名后查询" defaultId="positionid" id="positionnamelike1" submitid="orgmanager_deputy" defaultName="positionname" key="组织负责人(副职)" columnWidth="0.6" span="2" url="orgUserMgAction!queryLikeZhengzhi.do" multiple="true" inputQueryNum="1" titleId="orgnamepath"/>
				
				<ta:selectInput id="yab003" collection="yab003" key="经办机构" required="true" span="2" labelWidth="115" columnWidth="0.6" filterOrg="false"  />
<!-- 				<ta:selectInput id="yab139"  key="数据区"  span="2" labelWidth="115" columnWidth="0.6"  displayValue="codeDESC"  hiddenValue="codeValue"  required="true" /> -->
				<ta:radiogroup id="effective" labelWidth="115" key="禁用标志"  cols="2" required="required" span="2" columnWidth="0.6">
					<ta:radio name="dto['effective']" key="禁用" value="1" />
					<ta:radio name="dto['effective']" key="启用" value="0" onClick="fnCheckParent(event)"/>
				</ta:radiogroup>
				<%-- 新增组织扩展jsp --%>
				<%@include file="/org/orgextend/orgMgExtend.jsp" %>
			</ta:box>
			<ta:panelButtonBar>
				<ta:submit  url="orgUserMgAction!addOrg.do" id="save" isok="true" key="保存[S]" onSubmit="fnBeforeSaveOrg" submitIds="orgForm" hotKey="S" icon="icon-add1" successCallBack="fnSaveSuccessCb" />
					<ta:button id="close" key="关闭[C]" hotKey="C" onClick="fnClose()"/>
			</ta:panelButtonBar>
			</ta:panel>
			</ta:form>
	</body>
</html>
<script  type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	Base.focus("orgname", 100);
// 	Base.setSelectDataValue("positionnamelike",data.fieldData.orgmanager,data.fieldData.orgmanager_name);  
// 	Base.setSelectDataValue("positionnamelike1",data.fieldData.orgmanager_deputy,data.fieldData.orgmanager_deputy_name); 
})
//关闭窗口
function fnClose(){
	parent.Base.closeWindow("newWin");
}
// 新增组织成功后的回调函数
function fnSaveSuccessCb(data) {
	var treeObj = parent.$.fn.zTree.getZTreeObj("orgTree");
	Base.msgTopTip("新增组织成功");
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	node.isParent = true;
	treeObj.updateNode(node);
// 	Base.refleshTree("orgTree", Base.getValue("porgid"));
	treeObj.addNodes(node, data.fieldData.childOrg,false );
	parent.Base.msgTopTip("新增组织成功");
	parent.Base.closeWindow("newWin");
}
// 判断组织的禁用状态并提示用户
function fnBeforeSaveOrg(){
	if (Base.getValue("effective") == "1") 
		return confirm("保存的组织为禁用状态，是否继续");
	else return true;
}
//选择组织类型
function fnOrgTypeSelect(value,key){
	var treeObj = parent.$.fn.zTree.getZTreeObj("orgTree");
	var pnode = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("orgid"));
	if(node !=null && node !=undefined &&  node.children){
		var cnodes = node.children;
		for(var i = 0 ; i < cnodes.length; i++){
			if(cnodes[i].orgtype ==  '<%=orgType_org%>'){
				if(key != "<%=orgType_org%>"){
					Base.setValue("orgtype","");
					Base.alert("该组织下有机构，因此该组织只能是机构","error",function(){Base.focus("orgtype")});
					break;
				}
			}else if(cnodes[i].orgtype == "<%=orgType_depart%>"){
				if(key == "<%=orgType_team%>"){
					Base.setValue("orgtype","");
					Base.alert("该组织下有部门，因此该组织只能是机构或者部门","error",function(){Base.focus("orgtype")});
					break;
				}
			}
		}
	}
	if(pnode != null && pnode != undefined){
		if(pnode.orgtype == "<%=orgType_org%>"){
	// 		if(key != "<%=orgType_depart%>"){
	// 			Base.setValue("orgtype","");
	// 			Base.alert("机构下面只能建部门","error",function(){Base.focus("orgtype")});
	// 		}
		}else if(pnode.orgtype == "<%=orgType_depart%>"){
			if(key == "<%=orgType_org%>"){
				Base.setValue("orgtype","");
				Base.alert("部门下只能建部门或者组","error",function(){Base.focus("orgtype")});
			}
		}else if(pnode.orgtype == "<%=orgType_team%>"){
			if(key != "<%=orgType_team%>") {
					Base.setValue("orgtype", "");
					Base.alert("组下只能建组", "error", function() {
						Base.focus("orgtype")
					});
				}
			} else {
				Base.setValue("orgtype", "");
				Base.alert("组织机构类型出错，请联系管理员", "error", function() {
					Base.focus("orgtype")
				});
			}
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>