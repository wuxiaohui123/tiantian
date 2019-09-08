<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.ta3.system.org.domain.Org"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
	String orgType_org = Org.ORG_TYPE_ORG;
	String orgType_depart = Org.ORG_TYPE_DEPART;
	String orgType_team = Org.ORG_TYPE_TEAM;
%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>组织管理</title>
		<%@ include file="/ta/inc.jsp"%>
		<style type="text/css">
			 .searchbox{
				padding: 0 0 0 16px;
				width:100px;
				margin:2px 2px 0px 5px;
				color:#CCC;
				border:1px solid #e1e1e1;
				width:200px;
				line-height: 18px;
			 	background: url('<%=path%>/indexue/indexue_default/images/search.png') no-repeat scroll 0 0 transparent !important;
			 } 
		</style>
	</head>
	<body class="no-scrollbar" layout="border" layoutCfg="{leftWidth:340}" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:box position="left" key="组织" cssStyle="overflow:auto;">	
			<div style="backgound:#EEE;height:26px;border-bottom:1px solid #CCC;padding-top:3px;">
	        		<input id="searchbox" class="searchbox" style="height:18px" type="text" value="搜索" />
	        		<button type="button" onclick="fnQuery()">搜索</button>
			</div>
	        <div class="grid" fit="true" style="overflow:auto;">
				<ta:tree id="orgTree" childKey="orgid" nameKey="orgname" parentKey="porgid" fontCss="treeEffective" showLine="true" async="true" asyncUrl="orgMgAction!webQueryAsyncOrgTree.do" asyncParam="['orgid']"
					editable="true" onDblClick="fnDblClk" beforeEdit="fnToEdit" beforeRemove="fnBfRemove" onRemove="fnDelDept" onRightClick="fnOnRightClick"
					keepLeaf="true" keepParent="true" editTitle="编辑当前组织" removeTitle="删除当前组织" addTitle="添加子组织" showAddBtn="true" onAdd="fnAddDept"
					beforeDrop="fnBeforeDrop" onDrop="fnOnDrop" />
	       	</div>
		</ta:box>
		<ta:box position="center">
			<ta:form id="orgForm" fit="true">
			<ta:box id="departPanel" position="center"  cssStyle="padding:30px 10px 0px 10px;"  cols="2"  fit="true">		
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
				<ta:text id="orgmanager"  display="false"/>
				<ta:text id="orgmanager_name" key="组织负责人(正职)" columnWidth="0.6" span="2" labelWidth="115" readOnly="true" popWinBeforeClick="fnPopWinBeforeClick" popWin="true" popWinUrl="orgMgAction!webSelectOrgManager.do" popWinWidth="80%" popWinHeight="80%" popParam="{'managerType':'1'}" popSubmitIds="orgmanager,orgmanager_deputy"/>
				<ta:text id="orgmanager_deputy" display="false"/>
				<ta:text id="orgmanager_deputy_name" key="组织负责人(副职)" columnWidth="0.6" span="2" labelWidth="115" readOnly="true" popWinBeforeClick="fnPopWinBeforeClick" popWin="true" popWinUrl="orgMgAction!webSelectOrgManager.do" popWinWidth="80%" popWinHeight="80%" popParam="{'managerType':'2'}" popSubmitIds="orgmanager,orgmanager_deputy"/>
				<ta:selectInput id="yab003" collection="yab003" key="经办机构" required="true" span="2" labelWidth="115" columnWidth="0.6" filterOrg="false"  onSelect="fnYab139Filter"/>
				<ta:selectInput id="yab139"  key="数据区"  span="2" labelWidth="115" columnWidth="0.6"  displayValue="codeDESC"  hiddenValue="codeValue"  required="true" />
				<ta:radiogroup id="effective" labelWidth="115" key="禁用标志"  cols="2" required="required" span="2" columnWidth="0.6">
					<ta:radio name="dto['effective']" key="禁用" value="1" />
					<ta:radio name="dto['effective']" key="启用" value="0" onClick="fnCheckParent(event)"/>
				</ta:radiogroup>
				<%-- 新增组织扩展jsp --%>
				<%@include file="/org/orgextend/orgMgExtend.jsp" %>
				<ta:buttonLayout>
					<ta:submit  url="orgMgAction!webUpdateOrg.do" isok="true" id="update"  key="保存[S]" onSubmit="fnBeforeUpdateOrg" submitIds="departPanel" hotKey="S" icon="icon-add1" successCallBack="fnRefleshTree" disabled="true"/>
					<ta:submit  url="orgMgAction!webSaveOrg.do" id="save" isok="true" key="保存[S]" onSubmit="fnBeforeSaveOrg" submitIds="orgForm" hotKey="S" icon="icon-add1" successCallBack="fnSaveSuccessCb" display="false" disabled="true"/>
					</ta:buttonLayout>	
			</ta:box>
			</ta:form>
		</ta:box>
		<div id="rm" style="width:150px;font-size:12px;">
			<div id="rm_add" class="btn-app">添加子组织</div>
			<div id="rm_modify">编辑当前组织</div>
			<div id="rm_del">删除当前组织</div>
		</div>
	</body>
</html>
<script type="text/javascript">
$(document).ready(function () {
	$("body").taLayout();
	var treeObj = Base.getObj("orgTree");
	$("#rm").menu(); // 构建右键菜单
	// 在树上绑定右键事件
	$("#orgTree").bind('contextmenu', function(e){
		$('#rm').menu('show', {left: e.pageX, top: e.pageY});
		return false;
	});
	//树判断
	$("#searchbox").focus(function(e){
 		if(this.value.trim()=='搜索'){
 			$(this).css('color','#000000');
 			this.value="";
 			e.stopPropagation();
		}
 	}).blur(function(e){
 		if(this.value.trim()==''){
 			$(this).css('color','#CCC');
 			this.value="搜索";
 			e.stopPropagation();
 		}
 	}).keydown(function(e){
 		if(e.keyCode==13){
 			var menuTree = Ta.core.TaUIManager.getCmp('orgTree');
 			var slctNodes = menuTree.getSelectedNodes();
 			e.keyCode = 0;
			e.cancelBubble = true;
			e.returnValue = false;
			if (e.stopPropagation) {
				e.stopPropagation();
				e.preventDefault();
			}
 		}
 	});
//  	.bind("input", function(e) {
//  		if (this.value.trim() != "") {
//  			var menuTree = Ta.core.TaUIManager.getCmp('orgTree');
//  			var nodes = menuTree.getNodesByParamFuzzy("py", this.value);
//  			if(nodes.length == 0)
//  				nodes = menuTree.getNodesByParamFuzzy("orgname", this.value);
//  			if (nodes.length > 0) {
//  				menuTree.selectNode(nodes[0]);
//  			}
//  		}
//  	}).bind("propertychange", function(e) {
//  		if (this.value.trim() != "") {
//  			var menuTree = Ta.core.TaUIManager.getCmp('orgTree');
//  			var nodes = menuTree.getNodesByParamFuzzy("py", this.value);
//  			if(nodes.length == 0)
//  				nodes = menuTree.getNodesByParamFuzzy("orgname", this.value);
//  			if (nodes.length > 0) {
//  				menuTree.selectNode(nodes[0]);
//  			}
//  			Base.setFocus("searchbox", 400);
//  		}
//  	});
});
function fnPopWinBeforeClick(){
	var orgtype = Base.getValue("orgtype");
	if(orgtype && orgtype != ""){
		return true;
	}else{
		Base.alert("请选择组织类型后再选择组织负责人","warn",function(){
			Base.focus("orgtype");
		});
		return false;
	}
}
// 判断组织的禁用状态并提示用户
function fnBeforeSaveOrg(){
	if (Base.getValue("effective") == "1") 
		return confirm("保存的组织为禁用状态，是否继续");
	else return true;
}
//判断父节点是否禁用
function fnCheckParent(e){
	var treeObj = Base.getObj("orgTree");
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	if (node && node != null && node != undefined &&  node.effective == 1){
		//Base.msgTopTip("该组织的上级组织已经禁用，请先启用上级组织", 1500, 300, 300);
		Base.alert("该组织的上级组织已经禁用，请先启用上级组织","warm");
		Base.setValue("effective", "1");
		e.keyCode = 0;
		e.cancelBubble = true;
		e.returnValue = false;
		if (e.stopPropagation) {
			e.stopPropagation();
			e.preventDefault();
		}
	}
}
//检测effect为禁用时，提醒用户确认
function fnBeforeUpdateOrg(){
	if (Base.getValue("effective") == "1") {
		return confirm("禁用组织将禁用子组织，及这些组织下的所有岗位，是否继续");
	}else 
		return true;
}
<%-- 组织样式 --%>
function treeEffective(treeId, treeNode){
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
//刷新树节点的数据
function  fnRefleshTree(data){
   var porgname = Base.getValue("porgname");
    if(porgname){
    	Base.recreateTree("orgTree",null,null);
//         Base.refleshTree("orgTree", 1);
    }else{
        var nodeId = Base.getValue("orgid");
     	var treeObj = Ta.core.TaUIManager.getCmp("orgTree");
	    var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, nodeId);
	    node.orgname= Base.getValue("orgname");
        treeObj.updateNode(node);
   }
   var orgname = Base.getValue("orgname");
   var orgnamepath = Base.getValue("orgnamepath");
   if(orgnamepath.lastIndexOf("/") > 0){
	   var tempNamePath = orgnamepath.substring(0,orgnamepath.lastIndexOf("/")+1);
	   Base.setValue("orgnamepath",tempNamePath + orgname);
   }
}

// 右键点击树节点事件
function fnOnRightClick(event, treeId, treeNode) {
	var treeObj = Base.getObj("orgTree");
	$("#rm_add").unbind("click").bind("click", function(){
		fnAddDept(event, treeId, treeNode);
	});
	$("#rm_modify").unbind("click").bind("click", function(){
		fnDblClk(event, treeId, treeNode)
	});
	$("#rm_del").unbind("click").bind("click", function(){
		if (!treeNode.pId || treeNode.pId == "") {
			Base.alert("不能删除顶级组织！", "warn");
		} else if (treeNode.isParent) {
			Base.confirm("删除该组织会把下面的子组织一并删除，确实要删除吗？", function(yes){
				if(yes) Base.submit(null, "orgMgAction!delete.do", {orgId:treeNode.id}, false, false, function(){Base.refleshTree("orgTree", treeNode.pId);});
			});
		} else {
			Base.confirm("确实要删除该组织吗？", function(yes){
				if(yes) Base.submit(null, "orgMgAction!delete.do", {orgId:treeNode.id}, false, false, function(){Base.refleshTree("orgTree", treeNode.pId);});
			});
		}
	});
	if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
		treeObj.cancelSelectedNode();
	} else if (treeNode) {
		treeObj.selectNode(treeNode);
	}
}
// 双击进入组织信息编辑状态
function fnDblClk(e, treeId, treeNode) {
	Base.resetForm("orgForm");
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你没有该组织的操作权限：无法修改</div>");
		return false;
	} 
	Base.submit(null, "orgMgAction!webGetOrgEditData.do", {"dto['orgid']":treeNode.orgid}, null, null, function(data){
		if(treeNode.getParentNode() != null){
			Base.setValue("porgname", treeNode.getParentNode().orgname);
			//Base.setValue("orglevel", treeNode.getParentNode().orglevel);
			//Base.setValue("isleaf", treeNode.getParentNode().isleaf);
		}
		Base.hideObj("save");
		Base.showObj("update");
		Base.setEnable(["update"]);
	}, null, false);
		if(e && e.stopPropagation){
		  e.stopPropagation();
		}else{
		  window.event.cancelBubble = true;
		}
	return false;
}
// 添加子组织
function fnAddDept(event, treeId, treeNode) {
	if (treeNode.effective == 1){
		Base.msgTopTip("<div class='msgTopTip'>该组织已经禁用：无法添加子组织</div>");
	} else if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>该组织没有操作权限：无法添加子组织</div>");
	} else {
		Base.resetForm("orgForm");
		Base.hideObj("update");
		Base.showObj("save");
		Base.setEnable("orgType,save");
		Base.setValue("porgname", treeNode.orgname);
		Base.setValue("orglevel", treeNode.orglevel);
		Base.setValue("isleaf", treeNode.isleaf);
		Base.setValue("orgnamepath", treeNode.orgnamepath);
		Base.setValue("effective", 0);
		Base.setValue("porgid", treeNode.orgid);
		Base.setValue("yab003",treeNode.yab003);
		Base.setValue("yab139",treeNode.yab139);
		Base.focus("orgname", 100);
		Base.submit("","orgMgAction!webGetMaxCostomNo.do",{"dto['porgid']":treeNode.orgid});
	}
}
// 新增组织成功后的回调函数
function fnSaveSuccessCb(data) {
	var treeObj = Base.getObj("orgTree");
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	node.isParent = true;
	treeObj.updateNode(node);
// 	Base.refleshTree("orgTree", Base.getValue("porgid"));
	 treeObj.addNodes(node, data.fieldData.childOrg,false );
	if (confirm("新增组织成功，是否继续新增？")) {
		fnAddDept(null, "orgTree", Base.getObj("orgTree").getNodeByParam("orgid", Base.getValue("porgid")));
	} else {
		Base.hideObj("save");
		Base.showObj("update");
		Base.setEnable(["update"]);
	}
}
// 点击编辑按钮编辑组织信息
function fnToEdit(treeId, treeNode) {
	return fnDblClk(null, treeId, treeNode), false;
}
// 判断和提示能否删除组织
function fnBfRemove(treeId, treeNode) {
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>你没有操作该组织权限：无法删除该组织</div>");
		return false;
	} 
	if (!treeNode.porgid || treeNode.porgid == "") {
		return Base.alert("不能删除顶级组织！", "warn"), false;
	} else if (treeNode.isParent) {
		return confirm("删除该组织会把下面的子组织一并删除,并且会删除组织相关的一切内容，确实要删除吗？");
	} else {
		return confirm("确实要删除该组织吗，删除组织会删除组织相关的一切内容，确认删除吗？");
	}
}
// 提交删除操作
function fnDelDept(event, treeId, treeNode) {
	if (treeNode.admin != true){
		Base.msgTopTip("<div class='msgTopTip'>该组织没有操作权限：无法删除</div>");
		return false;
	} 
    Base.submit(null, "orgMgAction!webDeleteOrg.do", {"dto['orgid']":treeNode.orgid}, false, false,
   		function(){
   			Base.resetForm("departForm");
   			Base.setDisabled("update,save");
   			Base.msgTopTip("<div class='msgTopTip'>删除组织成功！</div>");
//    			var node = treeNode.getParentNode(); 
   			Base.recreateTree("orgTree",null,null);
			/* if(node){
				var children = node.children;
				if(children && children.length > 0){
					Base.refleshTree("orgTree", treeNode.porgid);
				}else{
					Base.refleshTree("orgTree", node.porgid);
				}
			}else{
				Base.refleshTree("orgTree", 1);
			} */
   		}
    );
}
// 判断和提示能否拖拽调整组织排序
function fnBeforeDrop(treeId, treeNodes, targetNode, moveType) {
	var treeNode = treeNodes[0];
	if (treeNode.porgid != targetNode.porgid) {
		return Base.alert("非同级组织间不支持排序！"), false;
	} else if (moveType == "inner") {
		return Base.alert("不支持改变组织级次！"), false;
	}
	return confirm("是否保存对组织排序的修改？");
}
// 拖拽调整组织排序
function fnOnDrop(event, treeId, treeNodes, targetNode, moveType) {
	Base.resetForm("orgForm");
	Base.setDisabled("update,save");
	var pNode = treeNodes[0].getParentNode();
	var sortid = [];
	for (var i = 0; i < pNode.children.length; i ++) {
		sortid.push({orgid: pNode.children[i].orgid});
	}
	Base.submit(null, "orgMgAction!webSortOrg.do", {sortorgids:Ta.util.obj2string(sortid)} , false, false, function(){Base.refleshTree('orgTree', treeNode.pId);});
}
function fnOrgTypeSelect(value,key){
	var treeObj = Base.getObj("orgTree");
	var pnode = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("porgid"));
	var node = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, Base.getValue("orgid"));
	if(node !=null && node !=undefined &&  node.children){
		var cnodes = node.children;
		for(var i = 0 ; i < cnodes.length; i++){
			if(cnodes[i].orgtype ==  "<%=orgType_org%>"){
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
			if(key != "<%=orgType_team%>"){
				Base.setValue("orgtype","");
				Base.alert("组下只能建组","error",function(){Base.focus("orgtype")});
			}
		}else{
			Base.setValue("orgtype","");
			Base.alert("组织机构类型出错，请联系管理员","error",function(){Base.focus("orgtype")});
		}
	}
}
function fnYab139Filter(value,key){
	Base.submit("","orgMgAction!queryYab139ByYab003.do",{"dto['yab003']":key});
}
</script>
<%@ include file="/ta/incfooter.jsp"%>