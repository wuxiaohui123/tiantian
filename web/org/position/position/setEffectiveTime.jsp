<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:panel fit="true" withButtonBar="true" id="effectiveTimePanel" cols="1">
		<ta:text id="p_positionid" display="false"></ta:text>
		<ta:text id="p_positionType" display="false"></ta:text>
		<ta:text id="p_menuid" display="false"></ta:text>
		<ta:text id="positionids" display="false"></ta:text>
		<ta:date id="effectiveTime" key="有效时间" showSelectPanel="true" validNowTime="right"  cssStyle="margin:2px"></ta:date>
		<ta:panelButtonBar>
			<ta:button key="保存" isok="true" id="btnSave" onClick="fnSaveEffectiveDate()"></ta:button>
			<ta:button key="保存" isok="true"  id="btnBatchSave" display="false" onClick="fnBatchSaveEffectiveDate()"></ta:button>
			<ta:button key="保存" isok="true"  id="btnPositionsSave" display="false" onClick="fnPositionsSaveEffectiveDate()"></ta:button>
			<ta:button key="保存" isok="true"  id="btnPositionsBatchSave" display="false" onClick="fnPositionsBatchSaveEffectiveDate()"></ta:button>
			<ta:button key="取消" onClick="Base.closeWindow('win')"></ta:button>
		</ta:panelButtonBar>
	</ta:panel>
<script>
// top.effectiveTimeAll = [];
function fnSaveEffectiveDate(){
	var url = "",type = Base.getValue("p_positionType");
	if(type == "1"){//公有岗位
		url = "positionMgAction!saveEffectiveTimePanel.do";
	}else{//个人岗位
		url = "personalPositionMgAction!saveEffectiveTimePanel.do";
	}
	Base.submit("effectiveTimePanel",url,{},null,null,function(){
		Base.msgTopTip("<div class='msgTopTip'>设置有效时间成功</div>");
		Base.closeWindow("win");
	},null,true);
}
function fnBatchSaveEffectiveDate(){
	var tree = Base.getObj("opTree");
	var nodes = tree.getCheckedNodes(true);
	var url = "",type = Base.getValue("p_positionType");
	if(type == "1"){//公有岗位
		url = "positionMgAction!batchSaveEffectiveTimePanel.do";
	}else{//个人岗位
		url = "personalPositionMgAction!batchSaveEffectiveTimePanel.do";
	}
	var menuids = "[";
	if(nodes && nodes.length > 0){
		for(var i = 0 ; i < nodes.length ; i++){
			menuids += "{\"menuid\":"+nodes[i].id+"},";
		}
	}
	menuids = menuids.substring(0, menuids.length-1);
	menuids += "]";
	Base.submit("effectiveTimePanel",url,{"menuids":menuids},null,null,function(){
		Base.msgTopTip("<div class='msgTopTip'>设置有效时间成功</div>");
		Base.closeWindow("win");
	},null,true);
}
function fnPositionsSaveEffectiveDate(){
	var url = "",type = Base.getValue("p_positionType");
	if(type == "1"){//公有岗位
		url = "positionMgAction!savePositionsEffectiveTimePanel.do";
	}else{//个人岗位
		url = "personalPositionMgAction!savePositionsEffectiveTimePanel.do";
	}
	Base.submit("effectiveTimePanel",url,{"positionids":Base.getValue("positionids")},null,null,function(){
		Base.msgTopTip("<div class='msgTopTip'>设置有效时间成功</div>");
		Base.closeWindow("win");
	},null,true);
}
function fnPositionsBatchSaveEffectiveDate(){
	var tree = Base.getObj("opTree");
	var nodes = tree.getCheckedNodes(true);
	var menuids = "[";
	if(nodes && nodes.length > 0){
		for(var i = 0 ; i < nodes.length ; i++){
			menuids += "{\"menuid\":"+nodes[i].id+"},";
		}
	}
	menuids = menuids.substring(0, menuids.length-1);
	menuids += "]";
	var url = "",type = Base.getValue("p_positionType");
	if(type == "1"){//公有岗位
		url = "positionMgAction!savePositionsBatchEffectiveTimePanel.do";
	}else{//个人岗位
		url = "personalPositionMgAction!savePositionsBatchEffectiveTimePanel.do";
	}
	Base.submit("effectiveTimePanel",url,{"positionids":Base.getValue("positionids"),"menuids":menuids},null,null,function(){
		Base.msgTopTip("<div class='msgTopTip'>设置有效时间成功</div>");
		Base.closeWindow("win");
	},null,true);
}
</script>
<%@ include file="/ta/incfooter.jsp"%>