<%@page import="com.yinhai.sysframework.iorg.IPosition"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
	String pub = IPosition.POSITION_TYPE_PUBLIC;
	String per = IPosition.POSITION_TYPE_PERSON;
%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>用户岗位设置</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:panel fit="true" key="请选择一种方式操作" titleAlign="left">
			<ta:tabs fit="true" id="orgusertabs"  hasBorder="false" headPlain="true" onSelect="fnSelect">
				<ta:tab key="岗位设置人员" id="positionTab" layout="border" layoutCfg="{leftWidth:240}">
					<ta:box position="left" key="查询" cssClass="left-themes-color" >
						<ta:form id="form1" fit="true" >
							<ta:text id="positionname" key="岗位名称"  labelWidth="70"></ta:text>
							<ta:selectTree url="positionSettingMgAction!webQueryAsyncOrgTree.do" selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss"  idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
							 targetDESC="orgname" treeId="orgTree" targetId="orgid" key="部门"  labelWidth="70"  cssStyle="width:200px" />
							<ta:radiogroup id="isDisDecPositions" key="子部门岗位" cols="2" labelWidth="70">
								<ta:radio key="显示" value="0"/>
								<ta:radio key="不显示" checked="true" value="1"/>
							</ta:radiogroup>
							<ta:text id="positionType" display="false"  value="1"></ta:text>
							<ta:text id="effective" display="false" value="0"></ta:text>
							<ta:buttonLayout align="center">
								<ta:button key="查询" hotKey="q" isok="true" onClick="fnQuery(1)"  icon="xui-icon-query"></ta:button>
								<ta:button key="重置" hotKey="r"  onClick="Base.resetForm('form1')" icon="xui-icon-reset"></ta:button>
							</ta:buttonLayout>
						</ta:form>
					</ta:box>
					<ta:box position="center">
						<ta:datagrid fit="true" id="positionGrid" haveSn="true" rowColorfn="fnColor">
							<ta:datagridItem id="positionname" key="岗位名称" width="150"  showDetailed="true" sortable="true" ></ta:datagridItem>
							<ta:datagridItem id="positiontype" key="岗位类型" width="150"  sortable="true"  formatter="fnPosType"></ta:datagridItem>
							<ta:datagridItem id="orgnamepath" key="所属部门" width="300"  sortable="true"></ta:datagridItem>
							<ta:datagridItem id="assign" key="人员选择" icon="icon-adduser" width="70" align="center" click="fnAssignUser"></ta:datagridItem>
							<ta:dataGridToolPaging url="positionSettingMgAction!queryPubPositions.do" pageSize="200" submitIds="form1"></ta:dataGridToolPaging>
						</ta:datagrid>
					</ta:box>
				</ta:tab>
				<ta:tab key="人员选择岗位" id="userTab"  layout="border" layoutCfg="{leftWidth:240}">
					<ta:box position="left" key="查询" cssClass="left-themes-color" >
						<ta:form id="form2" fit="true" cssStyle="padding-right:20px;">
							<ta:text id="loginids" key="登录号"   labelWidth="70" textHelp="支持多loginid查询,以逗号隔开,例如:developer,test_01,test_02" ></ta:text>
							<ta:text id="username" key="姓名"   labelWidth="70"></ta:text> 
							<ta:selectTree url="positionSettingMgAction!webQueryAsyncOrgTree_p.do" selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss"  idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" 
							 targetDESC="p_orgname" treeId="p_orgTree" targetId="p_orgid" key="部门"  labelWidth="70"  cssStyle="width:200px" />
							<ta:radiogroup id="p_isDisDecPositions" key="子部门人员" cols="2" labelWidth="70">
								<ta:radio key="显示" value="0"/>
								<ta:radio key="不显示" checked="true" value="1"/>
							</ta:radiogroup>
							<ta:text id="p_positionType" display="false"  value="2"></ta:text>
							<ta:text id="p_effective" display="false" value="0"></ta:text>
							<ta:buttonLayout align="center">
								<ta:button key="查询" hotKey="q" isok="true" onClick="fnQuery(2)"  icon="xui-icon-query"></ta:button>
								<ta:button key="重置" hotKey="r"  onClick="Base.resetForm('form2')" icon="xui-icon-reset"></ta:button>
							</ta:buttonLayout>
						</ta:form>
					</ta:box>
					<ta:box position="center">
						<ta:datagrid fit="true" id="userGrid" haveSn="true">
							<ta:datagridItem id="loginid" key="登录号" width="150" ></ta:datagridItem>
							<ta:datagridItem id="positionname" key="姓名" width="150" showDetailed="true" sortable="true"></ta:datagridItem>
							<ta:datagridItem id="sex" key="性别" width="70" collection="sex" ></ta:datagridItem>
							<ta:datagridItem id="orgnamepath" key="所属部门" width="300"  showDetailed="true" sortable="true"></ta:datagridItem>
							<ta:datagridItem id="opPostions" key="岗位设置" icon="xui-icon-add2" width="70" align="center" click="fnAssignPositionsToUser"></ta:datagridItem>
							<ta:dataGridToolPaging url="positionSettingMgAction!queryPubPositions.do" pageSize="200" submitIds="form2"></ta:dataGridToolPaging>
						</ta:datagrid>
					</ta:box>
				</ta:tab>
				
			</ta:tabs>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		//$("#userTab > .l-layout-center").width($("#positionTab > .l-layout-center").width());
		//$("#userTab > .l-layout-center").height($("#positionTab > .l-layout-center").height());
		//$("#userTab").height($("#positionTab").height())
	});
	function fnSelect(){
		$("#orgusertabs").resize();
	}
	function fnColor(data){
		if(data.isshare == 1){
			return "green";
		}else if(data.iscopy == 1){
			return "gray";
		}
	}
	function fnPosType(row, cell, value, columnDef, dataContext){
		if(dataContext.positiontype==1 && dataContext.isshare == 1){
			return "共享岗位";
		}else if(dataContext.positiontype==1 && dataContext.iscopy == 1){
			return "复制岗位";
		}else if(dataContext.positiontype==1){
			return "公有岗位"; 
		}else if(dataContext.positiontype == 2){
			return "个人岗位"; 
		}
	}
	function fnQuery(o){
		if(o==1)
			Base.submit("form1","positionSettingMgAction!queryPubPositions.do");
		else
			Base.submit("form2","positionSettingMgAction!queryPubPositions.do");
	}
	function fnAssignUser(data){
		Base.openWindow("assignUser",data.positionname + "->人员选择","<%=basePath%>org/position/positionSettingMgAction!toAssignUser.do",{"dto['positionid']":data.positionid},"90%","90%",null,null,true);
	}
	function fnAssignPositionsToUser(data){
		Base.openWindow("win", data.positionname + "->岗位设置", "<%=basePath%>org/position/positionSettingMgAction!toAssignPositionsToUser.do", {"dto['userid']" :data.userid}, "90%", "500",function(){
			var treeObj = $.fn.zTree.getZTreeObj("orgTreeUserPosition");
			var nodes = treeObj.getNodes();
			if (nodes.length>0) {//默认选中根节点
				treeObj.selectNode(nodes[0]);
			}
			//默认以根节点进行查询
			//fnClick();
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
	//权限判断
	function fnBeforeClick(treeId,treeNode){
		if(treeNode.effective == 1){ 
			Base.msgTopTip("<div class='msgTopTip'>该组织无效，不能进行查询</div>");
			return false;
		}
		if(treeNode.admin != true){
			Base.msgTopTip("你无权操作该组织");
			return false;
		}else{
			return true;
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>