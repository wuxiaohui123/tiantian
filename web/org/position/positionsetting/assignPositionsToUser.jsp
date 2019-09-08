<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:panel hasBorder="false" id="userPositionPanel" fit="true" cols="2">
		<ta:text id="userid" display="none"/>
		<ta:box columnWidth="0.2" height="460" cssStyle="overflow:auto;">
			<div style="backgound:#EEE;border-bottom:1px solid #CCC">
        		<input id="searchbox" style="height:18px;margin:3px;color:rgb(204, 204, 204)" type="text" value="搜索，点击回车查询" />
			</div>	
			<ta:tree id="orgTreeUserPosition" onClick="fnOrgTreeClick" fontCss="fnFontCss" beforeClick="fnBeforeClick"
			 showLine="true" async="true" asyncUrl="personalPositionMgAction!webQueryAsyncOrgTree.do" childKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']" />
		</ta:box>
		<ta:box fit="true" columnWidth="0.8">
			<ta:panel key="岗位列表" height="200" withToolBar="true">
				<ta:panelToolBar cssStyle="height:40px">
					<ta:submit cssStyle="float:left;margin:6px 3px;" submitIds="userid,noPositionPerGrid"  url="positionSettingMgAction!saveUserAddPositions.do" asToolBarItem="true" key="添加" icon="icon-add" onSubmit="fnCheckSelectedData" successCallBack="fnAddPositionSuccess"/>
					<ta:box id="ischild" cssStyle="float:left">
						<ta:checkbox key="显示子部门的岗位" id="isDisSubOrgs" value="0" onClick="fnClick()" />
					</ta:box>
				</ta:panelToolBar>
				<ta:datagrid id="noPositionPerGrid" fit="true" haveSn="true"  selectType="checkbox" columnFilter="true" >
					<ta:datagridItem id="positionid" asKey="true" key="岗位id" hiddenColumn="true"></ta:datagridItem>
					<ta:datagridItem id="positionname" key="岗位名称" width="120" showDetailed="true"></ta:datagridItem>
					<ta:datagridItem id="positiontype" key="岗位类型" width="120"  formatter="fnPosType"></ta:datagridItem>
					<ta:datagridItem id="orgnamepath" key="岗位路径" showDetailed="true" width="400"></ta:datagridItem>
				</ta:datagrid>
			</ta:panel>
			<ta:panel height="175" key="已选岗位列表(红色行为主岗位)" >
				<ta:datagrid id="positionPerGrid" fit="true" haveSn="true"  rowColorfn="fnMainPositionColor" columnFilter="true"  >
					<ta:datagridItem id="setMain" key="设置主岗位" icon="icon-setting" click="setMainPosition" width="100"></ta:datagridItem>
					<ta:datagridItem id="positionid" key="岗位id" hiddenColumn="true"></ta:datagridItem>
					<ta:datagridItem id="positionname" key="岗位名称/姓名" width="120" showDetailed="true" ></ta:datagridItem>
					<ta:datagridItem id="positiontype" key="岗位类型" width="120" showDetailed="true"  formatter="fnPosType"></ta:datagridItem>
					<ta:datagridItem id="orgnamepath" key="岗位路径" width="400"  showDetailed="true"></ta:datagridItem>
					<ta:datagridItem id="del" key="删除" width="50" align="center" dataAlign="center" click="fnDelPosition" icon="icon-remove"></ta:datagridItem>
				</ta:datagrid>
			</ta:panel>
		</ta:box>
	</ta:panel>
<script>
//岗位选择判断
function fnCheckSelectedData(){
	var data = Base.getGridSelectedRows("noPositionPerGrid");
	if(data && data.length > 0)
		return true;
	else{
		Base.alert("请选择岗位后再添加岗位","warn");
		return false;
	}
		
}
//点击树节点,查询岗位
function fnOrgTreeClick(e, tree, treeNode){
	//已选岗位,用于查询岗位时,排除已选岗位,只查询出未分配岗位
	var selectedData = Base.getGridData("positionPerGrid");
	var str = "";
	if(selectedData && selectedData.length > 0){
		for(var i = 0 ; i < selectedData.length ; i++){
			str += selectedData[i].positionid;
			if( i < selectedData.length - 1){
				str += ",";
			}
		}
	}
	Base.submit("userid,ischild", "positionSettingMgAction!getPubPositionsNoCurUseridByOrgId.do", {"dto['orgidpath']":treeNode.orgidpath,"dto['orgid']":treeNode.orgid,"dto['positionids']":str})
}
//删除已选岗位
function fnDelPosition(data, e){
	if(data.positiontype == "2"){//不能移除个人岗位
		Base.alert("不能移除个人岗位","error");
		return;
	}
	function delPositions(yes) {
		if (yes) {
			Base.submit("userid", "positionSettingMgAction!removeUserPosition.do", {"dto['positionid']":data.positionid});
		}
	}
	Base.confirm("是否删除此岗位",delPositions);
}
//添加岗位成功回调
function fnAddPositionSuccess(){
	function success(){
		Base.deleteGridSelectedRows("noPositionPerGrid");
	}
	Base.submit("userid", "positionSettingMgAction!getPubPositionsCurUserid.do",null,null,null,success);
}
//checkbox点击事件,相当于点击树节点查询
function fnClick(event){
	var orgTree = Base.getObj("orgTreeUserPosition");
	var selectedNodes = orgTree.getSelectedNodes();
	var treeNode;
	if(selectedNodes && selectedNodes.length == 1){
		treeNode = selectedNodes[0];
		fnOrgTreeClick(event,null,treeNode);
	}else{
		Base.alert("请选择部门后再进行子部门人员的查询","warn");
		return;
	}
}
//设置主岗位
function setMainPosition(data,e){
	if(data.mainposition == 1){
		Base.alert("\"" + data.positionname + "\"已经为主岗位!");
		return;
	}else{
		Base.confirm("确认设置\""+data.positionname + "\"为主岗位?",function(yes){
			if(yes){
				Base.submit("userid","positionSettingMgAction!setMainPosition.do",{"dto['positionid']":data.positionid},null,null,function(){
					Base.alert("设置主岗位成功","success");
				});
				Base.refreshGrid("positionPerGrid");
			}else{
				
			}
		})
	}
}
//格式化主岗位行
function fnMainPositionColor(data){
	if(data.mainposition == 1){
		return "red";
	}
}
//搜索
$(function(){
	$("#searchbox").focus(function(e){
 		if(this.value.trim()=='搜索，点击回车查询'){
 			$(this).css('color','#000000');
 			this.value="";
 			e.stopPropagation();
		}
 	}).blur(function(e){
 		if(this.value.trim()==''){
 			$(this).css('color','#CCC');
 			this.value="搜索，点击回车查询";
 			e.stopPropagation();
 		}
 	}).keydown(function(e){
 		if(e.keyCode==13){
 			var orgTree = Ta.core.TaUIManager.getCmp('orgTreeUserPosition');
 			var slctNodes = orgTree.getSelectedNodes();
 			if (slctNodes.length > 0) {
 				fnOrgTreeClick(event, orgTree, slctNodes[0]);
 			}
 			e.keyCode = 0;
			e.cancelBubble = true;
			e.returnValue = false;

			if (e.stopPropagation) {
				e.stopPropagation();
				e.preventDefault();
			}
 		}
 	}).bind("input", function(e) {
 		if (this.value.trim() != "") {
 			var orgTree = Ta.core.TaUIManager.getCmp('orgTreeUserPosition');
 			var nodes = orgTree.getNodesByParamFuzzy("py", this.value);
 			if(nodes.length == 0)
 				nodes = orgTree.getNodesByParamFuzzy("orgname", this.value);
 			if (nodes.length > 0) {
 				orgTree.selectNode(nodes[0]);
 				var $this = $(this);
 				setTimeout(function(){$this.focus()},200);
 			}
 		}
 	}).bind("propertychange", function(e) {
 		if (event.propertyName && event.propertyName == "value" && this.value.trim() != "") {
 			var orgTree = Ta.core.TaUIManager.getCmp('orgTreeUserPosition');
 			var nodes = orgTree.getNodesByParamFuzzy("py", this.value);
 			if(nodes.length == 0)
 				nodes = orgTree.getNodesByParamFuzzy("orgname", this.value);
 			if (nodes.length > 0) {
 				orgTree.selectNode(nodes[0]);
 				var $this = $(this);
 				setTimeout(function(){$this.focus()},200);
 			}
 		}
 	});
});
</script>
<%@ include file="/ta/incfooter.jsp"%>