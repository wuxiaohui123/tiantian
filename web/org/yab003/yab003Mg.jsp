<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@page import="com.yinhai.sysframework.config.SysConfig"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
String isInsertAa10Agencies = SysConfig.getSysConfig("isInsertAa10Agencies", "true");
%>
<html>
	<head>
		<title>经办机构配置</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body class="no-scrollbar">
	<ta:pageloading/>
	<div id="hintbody" style="display:none;"></div>
	<ta:box id="container" layout="border" layoutCfg="{leftWidth:230}">
			<ta:box position="left" id="box" key="经办机构管理" cssStyle="overflow:auto;background-color:white;">
				<ta:tree id="yab003Tree" async="false"  parentKey="pId" 
    			 		 editable="true" showEditBtn="true" showAddBtn="true" showRemoveBtn="true"
    			 		 onClick="fnMenuClick" beforeRemove="fnBeforeRemoveNode" onRemove="fnRemoveNode"
    			 		 onAdd="addTreeNode"  beforeEdit="fnBeforeEdit" showIcon="true" showLine="true"
    			 		 expandSpeed="fast" />
			</ta:box>
			<ta:box position="center" cols="3" key="数据区分配" cssStyle="padding:10px;">
				<ta:text id="yab003"  display="false" />
				<ta:text id="yab003desc" display="false"/>
				<ta:panel id="centerLeft" key="未分配数据区" fit="true" columnWidth="0.45">
					<ta:datagrid id="unDistrbutedData" fit="true"  selectType="checkbox" forceFitColumns="true" >
						<ta:datagridItem id="codeValue" key="数据区码值"></ta:datagridItem>
						<ta:datagridItem id="codeDESC" key="数据区名称" ></ta:datagridItem>
					</ta:datagrid>
				</ta:panel>
				<ta:box  columnWidth="0.1" cssStyle="margin-left:10px;">
					<div style="width:100%;position: relative;" id="centerdiv">
						<ta:button key="新增" cssStyle="display:block;margin:0 auto" icon="xui-icon-next" isShowIcon="true" onClick="batchAddData()"/>
						<ta:button key="移除" cssStyle="display:block;margin:0 auto;margin-top:10px;" icon="xui-icon-back" isShowIcon="true" onClick="batchDelete()"/>
					</div>
				</ta:box>
				<ta:panel id="centerRight" key="已分配数据区" fit="true" cssStyle="margin-left:10px;"  columnWidth="0.45">
					<ta:datagrid id="yab139Grid" fit="true" forceFitColumns="true" selectType="checkbox">
						<ta:datagridItem id="codeValue" key="数据区码值"></ta:datagridItem>
						<ta:datagridItem id="codeDESC" key="数据区名称" ></ta:datagridItem>
					</ta:datagrid>
				</ta:panel>
			</ta:box>
		</ta:box> 
	</body>
</html>
<script  type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		$("#centerdiv").css("top",$(window.document).height()/2-60);
		fnPageGuide(parent.currentBuinessId);
	})
	function fnPageGuide(currentBuinessId){
		$("body").hintTip({
			replay 	: false,
			show 	: true, 
			cookname: currentBuinessId,
			data 	: [
			        {id:$("#hintbody"),
			    	message:"此功能用于构建并管理经办机构树，分配经办机构所能管理的数据区。"
			        },
			        {id:$("#yab003Tree"),
			    	message:"可增删改经办机构。构建经办机构树分为两种形式，通过config.properties配置文件中的isInsertAa10Agencies属性决定：<br/>1.直接从aa10a1视图中获取经办机构（YAB003）以构建树；<br/>2.通过该功能直接新增经办机构"
			        },
			        {id:$("#centerLeft"),
			    	message:"未分配的数据区，可将这些数据区分配到你所选中的经办机构下"
			        },
			        {id:$("#centerRight"),
			    	message:"已分配的数据区，可将这些数据区从你所选中的经办机构下移除"
			        }
				]
		}); 
	}
	
	 //  批量新增
	function batchAddData(){
		var d = Base.getGridSelectedRows("unDistrbutedData");
		if(d && d.length > 0 ){
			Base.submit("unDistrbutedData","agenciesMgAction!batchAddData.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
				Base.submit("","agenciesMgAction!queryCurYab139.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){});	
				Base.deleteGridSelectedRows("unDistrbutedData");
				Base.msgTopTip("<div class='msgTopTip'>新增数据区成功</div>");
			}); 
		}else{
			Base.alert("请勾选你要新增的行数据","warn");
		}
		  
	 }
	 
	//   批量删除数据区
	function batchDelete(){
		var d = Base.getGridSelectedRows("yab139Grid");
		if(d && d.length > 0 ){
			Base.confirm("确定删除所选数据区？",function(yes){ 
				if(yes){
					Base.submit("yab139Grid","agenciesMgAction!batchDelete.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){
						Base.submit("","agenciesMgAction!queryUnDistrbutedData.do",{"dto['yab003']":Base.getValue("yab003")},null,null,function(){});
						Base.deleteGridSelectedRows("yab139Grid");
						Base.msgTopTip("<div class='msgTopTip'>删除数据区成功</div>");
					})
				}
			});
		}else{
			Base.alert("请勾选你要移除的行数据","warn");
		}
		  
	 }
	
	function fnMenuClick(e,treeId,treeNode){
		fnCheck(treeNode,e);    //  已分配数据区
		fnUnDistributedData(treeNode,e);		//  未分配数据区
	}
	
	function fnCheck(data,e){ 
		Base.submit("","agenciesMgAction!queryCurYab139.do",{"dto['yab003']":data.id},null,null,function(){
			Base.setValue("yab003",data.id);
			Base.setValue("yab003desc",data.name);
		});
		return true;
	}
	
	function fnUnDistributedData(data,e){
		Base.submit("","agenciesMgAction!queryUnDistrbutedData.do",{"dto['yab003']":data.id},null,null,function(){
		}); 
		return true;
	}
	
	function addTreeNode(event,treeId, treeNode){
		if("true" == "<%=isInsertAa10Agencies%>") {
			Base.openWindow("addWindow",treeNode.name+"下增加子节点","agenciesMgAction!addYab003.do",{"dto['yab003']":treeNode.id,"dto['getNode']":treeNode.name,"dto['treeNode']":treeNode.id,"dto['treeId']":treeId},"300","250",null,function(){
			},true);
		}else{
			Base.openWindow("addWindow",treeNode.name+"下增加子节点","agenciesMgAction!addYab003.do",{"dto['yab003']":treeNode.id,"dto['getNode']":treeNode.name,"dto['treeNode']":treeNode.id,"dto['treeId']":treeId},"450","400",null,function(){
			},true);
		} 
		
	}
	
	function fnBeforeEdit(treeId, treeNode){
		if("true" == "<%=isInsertAa10Agencies%>" ) {
			Base.openWindow("editWindow",treeNode.name+"树节点修改为","agenciesMgAction!editYab003.do",{"dto['yab003']":treeNode.id,"dto['yab003desc']":treeNode.name},"300","250",null,function(){
				treeNode.name = Base.getValue("yab003desc");
				refleshTree(treeId,treeNode);
			},true);
		}else{
			Base.msgTopTip("由于配置为业务系统自定义管理经办机构，所以不能更改",4000); 
		} 
		return false;
	}
	
	//  对树的一些操作
	function refleshTree(treeId,treeNode){
	    var tree = $.fn.zTree.getZTreeObj(treeId);
	    tree.updateNode(treeNode);
	}
	
	//   删除节点
	function fnBeforeRemoveNode(treeId,treeNode){
		if(confirm("确定移除该经办机构及其子经办机构？")){
			return true;
		}else{
			return false;
		}
	}
	var s = [];
	function fnRemoveNode(event,treeId,treeNode){
	    fnGetNodes(treeNode,s);
		Base.submit("","agenciesMgAction!removeYab003.do",{"dto['yab003p']":Ta.util.obj2string(s)},null,null,function(){
			s = [];
			Base.msgTopTip("删除经办机构成功");
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
</script>
<%@ include file="/ta/incfooter.jsp"%>