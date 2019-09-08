<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>维度管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar"  style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:tabs fit="true" id="tabs1" hasBorder="false" headPlain="false">
			<ta:tab key="功能选择岗位数据区操作" id="tab1" layout="border" layoutCfg="{leftWidth:340}">
				<ta:box position="left" key="功能权限" cssStyle="overflow:auto;">
					<ta:tree id="menuTree" showLine="true" nameKey="menuname" childKey="menuid" parentKey="pmenuid" async="true" 
						asyncUrl="dataAccessDimensionManagementAction!webGetAsyncMenu.do" asyncParam="['menuid']"  onClick="fnDblClk" 
						editable="true" showAddBtn="false" showEditBtn="false"  showRemoveBtn="false" fontCss="fnCheckYab003"/>
				</ta:box>
				<ta:box position="center" key="">
					<ta:box  cols="3">
						<%-- 存放菜单id --%>
						<ta:text id="menuid" display="false" ></ta:text>
						<ta:text id="menuname" display="false" ></ta:text>
						<%--存放岗位id --%>
						<ta:text id="positionid" display="false" ></ta:text>
						<%-- <ta:text id="positionids" display="false" ></ta:text> --%>
					</ta:box>
					<ta:panel fit="true"  hasBorder="false" key="岗位信息">
		<%-- 			<ta:panel fit="true" withToolBar="true" hasBorder="false" key="岗位信息">
						<ta:panelToolBar>
							<ta:button key="批量分配" onClick="fnDetachClick()" asToolBarItem="true" disabled="true" id="btnClick"></ta:button>
						</ta:panelToolBar> --%>
						<ta:datagrid id="positionGrid" fit="true" haveSn="true" columnFilter="true">
							<ta:datagridItem key="岗位名称" id="positionname" width="150"/>
							<ta:datagridItem key="岗位类型" id="positiontype"  width="100" formatter="fnFtPos" collection="positiontype"/>
							<ta:datagridItem key="所在组织" id="orgnamepath"  showDetailed="true" width="300"/>
							<ta:datagridItem key="操作" icon="icon-yhpurview" click="fnClick" align="center"/>
						</ta:datagrid>
					</ta:panel>
				</ta:box>
			</ta:tab>
			<ta:tab key="岗位批量选择功能菜单数据区操作" id="tab2" layout="border" layoutCfg="{rightWidth:440}">
				<ta:box position="center" key="岗位列表" >
					<ta:box height="40px" cols="4" id="orgbox">
						<ta:selectTree  labelWidth="80"  cssStyle="width:200px" nameKey="orgname" idKey="orgid" parentKey="porgid" selectTreeBeforeClick="fnBeforeClick" fontCss="fnFontCss" 
						 url="dataAccessDimensionManagementAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']" key="部门" targetDESC="orgname"  treeId="orgTree" targetId="orgid" />
						<ta:radiogroup key="子组织岗位"  id="issub" cols="2">
							<ta:radio key="显示" value="0"></ta:radio>
							<ta:radio key="不显示" value="1" checked="true"></ta:radio>
						</ta:radiogroup>
						<ta:selectInput id="positiontype" key="岗位类型" data="[{'id':1,'name':'公有岗位'},{'id':2,'name':'个人岗位'},{'id':3,'name':'委派岗位'},{'id':4,'name':'共享岗位'},{'id':5,'name':'复制岗位'}]"></ta:selectInput>
						<ta:buttonLayout align="left">
							<ta:button key="查询" id="btnQuery" icon="icon-ok" onClick="fnQueryPos()"></ta:button>
						</ta:buttonLayout>
					</ta:box>
					<ta:datagrid id="posGrid" fit="true" haveSn="true" heightDiff="40" columnFilter="true"  onRowClick="fnRowClick">
						<ta:datagridItem id="positionname" key="岗位名称" width="150"/>
						<ta:datagridItem id="positiontype" key="岗位类型" width="100"  formatter="fnFtPos" collection="positiontype"/>
						<ta:datagridItem id="orgnamepath" key="所在组织" width="400" showDetailed="true"/>
						<ta:dataGridToolPaging url="dataAccessDimensionManagementAction!queryPos.do" showExcel="false" submitIds="orgbox"></ta:dataGridToolPaging>
					</ta:datagrid>
				</ta:box>
				<ta:box position="right" key="功能权限" >
					<ta:box fit="true" heightDiff="40" cssStyle="overflow:auto">
						<ta:tree id="menuTree1"  showLine="true"  checkable="true"></ta:tree>
						<ta:text id="p_positionid" display="false"></ta:text>
						<ta:text id="positionname1" display="false"></ta:text>
					</ta:box>
					<ta:buttonLayout>
						<ta:button key="授予数据权限" onClick="fnOpenWin()"></ta:button>
					</ta:buttonLayout>
				</ta:box>
			</ta:tab>
		</ta:tabs>
		
	</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	});
	function fnBeforeClick(treeId, treeNode){
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
	function fnFtPos(row, cell, value, columnDef, dataContext){
		if(dataContext.positiontype == "1"){
			if(dataContext.isshare == "1"){
				return "共享岗位";
			}else if(dataContext.iscopy == "1"){
				return "复制岗位";
			}else{
				return "公有岗位";
			}
		}else{
			return value;
		}
	}
	function fnClick(d,e){
		Base.openWindow("win",Base.getValue("menuname")+"--"+d.positionname,"dataAccessDimensionManagementAction!toDimension.do",{"dto['menuid']":Base.getValue("menuid"),"dto['positionid']":d.positionid},640,"80%",null,null,true);
		Base.setValue("positionid",d.positionid);
	}
	/* function fnDetachClick(){
		var d = Base.getGridSelectedRows("positionGrid");
		if(d && d.length < 1){
			Base.msgTopTip("请至少勾选一个岗位");
		}else{
			Base.openWindow("win","功能权限维度管理","dataAccessDimensionManagementAction!toDetachDimension.do",{},"30%","80%");
			var positionids = "";
			for(var i = 0 ; i < d.length ; i++){
				positionids += d[i].positionid+",";
			}
			positionids = positionids.substring(0, positionids.length-1);
			Base.setValue("positionids",positionids);
		}
	} */
	function fnDblClk(e, treeId, treeNode){
		if(treeNode.useyab003 == '0'){//启用了维度管理
// 			Base.setEnable("btnClick");
			Base.submit("","dataAccessDimensionManagementAction!queryPositionsByMenuId.do",{"dto['menuid']":treeNode.menuid},null,null,function(){
				Base.setValue("menuid",treeNode.menuid);
				Base.setValue("menuname",treeNode.menuname);
				Base.setValue("positionid","");
// 				Base.setValue("positionids","");
			});
		}else{
			Base.msgTopTip("<div class='msgTopTip'>该功能未启用经办机构数据权限管理</div>");
// 			Base.setDisabled("btnClick");
			Base.clearGridData("positionGrid");
			Base.setValue("menuid","");
			Base.setValue("menuidname","");
			Base.setValue("positionid","");
			Base.setValue("positionids","");
		}
	}
	function fnCheckYab003(treeId,treeNode){
		if(treeNode.useyab003 != "0"){
			return {color:"red"};
		}
	}
	function fnQueryPos(){
		Base.submit("orgbox","dataAccessDimensionManagementAction!queryPos.do");
	}
	function fnRowClick(e,data){
		Base.submit("","dataAccessDimensionManagementAction!queryTree.do",{"dto['positionid']":data.positionid},null,null,function(data1){
			Base.recreateTree("menuTree1",null,eval(data1.fieldData.treeData));
			Base.setValue("p_positionid",data.positionid);
			Base.setValue("positionname1",data.positionname);
		});
	}
	function fnOpenWin(){
		var tree = Base.getObj("menuTree1");
		var treeNodes = tree.getCheckedNodes(true);
		if(treeNodes && treeNodes.length < 1){
			Base.alert("请至少勾选一条功能菜单","warn");
			return ;
		}else{
			var str = "";
			for(var i = 0 ; i < treeNodes.length ; i++){
				if( i == treeNodes.length - 1){
					str += "{\"id\":\""+treeNodes[i].id+"\"}";
				}else{
					str += "{\"id\":\""+treeNodes[i].id+"\"},";
				}
			}
			if(str != ""){
				str = "["+str+"]";
			}
			Base.openWindow("win1",Base.getValue("positionname1"),"dataAccessDimensionManagementAction!toDimension1.do",{"positionid":Base.getValue("p_positionid"),"menuids":str},"30%","80%",function(){
				var isdeveloper = Base.getValue("p_isdeveloper");
				if(isdeveloper == "false"){
					Base.hideObj("p_allAccess");
				}
			});
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>