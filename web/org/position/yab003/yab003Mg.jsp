<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>经办机构管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" >
		<ta:pageloading/>
		<ta:tabs fit="true" hasBorder="false" onSelect="fnTabSelect">
			<ta:tab key="经办机构管理数据区范围" id="yab139Tab">
				<ta:box layout="border" layoutCfg="{rightWidth:400,allowRightCollapse:false,allowRightResize:false}">
					<ta:box position="center" key="经办机构管理数据区范围<span style='color:red'>(单击操作)</span>">
						<ta:datagrid fit="true" id="yab003Grid"  selectType="radio" haveSn="true"  forceFitColumns="true" onRowClick="fnRowClick" columnFilter="true" onChecked="fnCheck">
							<ta:datagridItem id="codeValue" key="经办机构码值"></ta:datagridItem>
							<ta:datagridItem id="codeDESC" key="经办机构名称"></ta:datagridItem>
						</ta:datagrid>
					</ta:box>
					<ta:box position="right" >
						<ta:panel key="数据区范围" hasBorder="false" fit="true" withToolBar="true">
							<ta:panelToolBar>
								<ta:button asToolBarItem="true" key="新增" onClick="fnAddYab139()"></ta:button>
								<ta:button asToolBarItem="true" key="移除" onClick="fnRemoveYab139()"></ta:button>
							</ta:panelToolBar>
							<ta:datagrid id="yab139Grid" fit="true" haveSn="true"  forceFitColumns="true" selectType="checkbox">
								<ta:datagridItem id="codeValue" key="数据区码值"></ta:datagridItem>
								<ta:datagridItem id="codeDESC" key="数据区名称"></ta:datagridItem>
							</ta:datagrid>
						</ta:panel>
					</ta:box>
				</ta:box>
				<ta:text id="yab003" display="false"></ta:text>
				<ta:text id="yab003desc" display="false"></ta:text>
			</ta:tab>
			<ta:tab key="经办机构上下级管理" id="yab003Tab">
				<ta:text id="firstTree" display="false" value="no"></ta:text>
				<ta:box fit="true" layout="border" layoutCfg="{leftWidth:300,allowLeftCollapse:false,allowLeftResize:false}">
					<ta:box position="left" key="经办机构树">
						<ta:tree id="yab003Tree" async="false" editable="true" showEditBtn="false" showAddBtn="false" showRemoveBtn="true" parentKey="pId"  beforeRemove="fnBeforeRemoveYab003" onRemove="fnYab003Remove"></ta:tree>
					</ta:box>
					<ta:panel position="center" key="经办机构" withToolBar="true" fit="true" hasBorder="false">
						<ta:panelToolBar>
							<ta:button key="添加" asToolBarItem="true" onClick="fnSaveYab003()"></ta:button>
						</ta:panelToolBar>
						<ta:datagrid id="yab003Grid2" fit="true" haveSn="true" selectType="checkbox">
							<ta:datagridItem id="codeValue" key="经办机构码值" width="100"></ta:datagridItem>
							<ta:datagridItem id="codeDESC" key="经办机构名称" width="150"></ta:datagridItem>
						</ta:datagrid>
					</ta:panel>
				</ta:box>
			</ta:tab>
		</ta:tabs>
		
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
	});
	function fnBeforeRemoveYab003(treeId,treeNode){
		if(confirm("确定移除该经办机构及其子经办机构？")){
			return true;
		}else{
			return false;
		}
	}
	var s = [];
	function fnYab003Remove(event,treeId,treeNode){
		fnGetNodes(treeNode,s);
		Base.submit("","yab003MgAction!removeYab003.do",{"dto['yab003s']":Ta.util.obj2string(s)},null,null,function(){
			s = [];
		});
	}
	function fnGetNodes(node,s){
		s.push({"id":node.id});
		var childNodes = node.children;
		if(childNodes && childNodes.length >= 1){
			for(var i = 0 ; i < childNodes.length ; i++){
				fnGetNodes(childNodes[i],s);
			}
		}
	}
	function fnSaveYab003(){
		var firstTree = Base.getValue("firstTree");
		var d = Base.getGridSelectedRows("yab003Grid2");
		if(firstTree == "yes"){
			if(d && d.length != 1){
				Base.alert("只能选择一个根节点","warn");
			}else if(d && d.length == 1){
				Base.submit("yab003Grid2","yab003MgAction!saveYab003.do",{"dto['firstTree']":"true"},null,null,function(){
					Base.recreateTree("yab003Tree",null,[{"id":d[0].codeValue,"pId":null,"name":d[0].codeDESC}]);
					Base.setValue("firstTree", "no");
					Base.deleteGridSelectedRows("yab003Grid2");
				});
			}
		}else{
			var yab003Tree = Base.getObj("yab003Tree");
			var s = yab003Tree.getSelectedNodes();
			if(s && s.length == 1){
				if(d && d.length > 0){
					Base.submit("yab003Grid2","yab003MgAction!saveYab003.do",{"dto['pyab003']":s[0].id},null,null,function(){
						var newNodes = [];
						for(var i = 0 ; i < d.length ; i++){
							newNodes.push({"id":d[i].codeValue,"name":d[i].codeDESC});
						}
						yab003Tree.addNodes(s[0],newNodes,true);
						Base.deleteGridSelectedRows("yab003Grid2");
					});
				}else{
					Base.alert("请在表格中至少勾选一个经办机构","warn");
				}
			}else{
				Base.alert("请在经办机构树上选择一个父经办机构","warn");
			}
			
		}
	}
	function fnTabSelect(tabid){
		if(tabid == "yab003Tab"){
			var tree = Base.getObj("yab003Tree");
			var d = tree.getNodes();
			if(d && d.length == 0){
				Base.alert("请在右边表格中选择经办机构根节点","warn");
				Base.setValue("firstTree", "yes");
			}
		}
	}
	function fnAddYab139(){
		Base.openWindow("win",Base.getValue("yab003desc")+"-->数据区操作","yab003MgAction!queryYab139.do",{"dto['yab003']":Base.getValue("yab003")},"400","80%",null,function(){
			Base.submit("","yab003MgAction!queryCurYab139.do",{"dto['yab003']":Base.getValue("yab003")});
		},true);
	}
	function fnRemoveYab139(){
		var d = Base.getGridSelectedRows("yab139Grid");
		if(d && d.length > 0 ){
			Base.confirm("确定删除所选数据区？",function(yes){
				if(yes){
					Base.submit("yab139Grid","yab003MgAction!removeYab139.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
						Base.deleteGridSelectedRows("yab139Grid");
						Base.msgTopTip("<div class='msgTopTip'>删除数据区成功</div>");
					})
				}
			});
		}
	}
	function fnRowClick(e,data){
		fnCheck(data,e);
		
	}
	function fnCheck(data,e){
		if(e == undefined){
			Base.submit("","yab003MgAction!queryCurYab139.do",{"dto['yab003']":data.codeValue},null,null,function(){
				Base.setValue("yab003",data.codeValue);
				Base.setValue("yab003desc",data.codeDESC);
			});
			return true;
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>