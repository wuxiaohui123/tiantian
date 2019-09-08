<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html>
	<head>
		<title>岗位及权限管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar"  style="padding:0px;margin:0px">
		<ta:pageloading/>
			<ta:tabs fit="true" onSelect="fnOnSelect" headPlain="true" id="" hasBorder="false">
				<ta:tab  id="tabUser" layout="border"  layoutCfg="{leftWidth:410}" key="人员权限" selected="true" >
					<ta:box fit="true" position="left"  cssClass="left-themes-color" cssStyle="padding:10px;">	
						<ta:fieldset id="queryUser" cols="2">
							<ta:selectTree url="positionUserMgAction!webQueryAsyncOrgTree.do" labelWidth="65"   fontCss="fnFontCss" selectTreeBeforeClick="fnBeforeClick"  
							idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']"
							 targetDESC="orgname" columnWidth="0.75" treeId="orgTree" targetId="orgid" key="组织"/>
							<ta:box columnWidth="0.25">
								<ta:checkboxgroup  id="isDisDecPositions">
							 		<ta:checkbox checked="true" key="包含子组织" value="0"   />
								</ta:checkboxgroup>
							</ta:box>
							<ta:selectLableText keys="[{\"id\":\"username\",\"key\":\"姓名\"},{\"id\":\"loginids\",\"key\":\"账号\"}]" labelWidth="65" columnWidth="0.75" placeholder="请输入账号或用户名..."/>
							<ta:buttonLayout columnWidth="0.25" align="left">
								<ta:button key="查找" isok="true" onClick="fnQueryUser();"  icon="xui-icon-query"></ta:button>
							</ta:buttonLayout>
						</ta:fieldset>
						<ta:buttonLayout align="left" cssStyle="margin-top:10px;height:32px;padding-left:0px;">
							<ta:selectButton key="批量权限操作">
								<ta:selectButtonItem  id="grantPermissions" key="授予使用权"   onClick="fnBatchPermissions(2)" />
								<ta:selectButtonItem  id="recyclePermissions" key="回收使用权"  onClick="fnBatchPermissions(1)"/>
							</ta:selectButton>
						</ta:buttonLayout>
						<ta:panel fit="true" key="人员列表" cssStyle="margin-top:5px;">
							<ta:datagrid id="userGrid" fit="true" haveSn="true" forceFitColumns="true"  selectType="checkbox" onSelectChange="fnUserSelectChange">
								<ta:datagridItem  id="loginid" key="登录号"   formatter="fnFomatter" hiddenColumn="true" ></ta:datagridItem>
								<ta:datagridItem id="positionname" key="姓名" sortable="true"  click="fnOpenPermission" formatter="fnNameFormatter" ></ta:datagridItem>
								<ta:datagridItem id="orgnamepath" width="231" key="所属组织" click="fnOpenPermission" showDetailed="true" sortable="true"></ta:datagridItem>
								<ta:dataGridToolPaging url="positionUserMgAction!queryUsers.do" showExcel="false" submitIds="queryUser" pageSize="10" />
							</ta:datagrid>
						</ta:panel>
					</ta:box>
					<ta:box position="center" id="perMissionBox" cssStyle="padding:10px;">
							<ta:box cssStyle="height:24px;">
								<ta:text id="per_userid" display="false" />
								<ta:text id="per_effective" display="false" />
								<ta:text id="per_positionid" display="false" />
								<div style="height:24px;">
									<table>
										<tr>
											<td style="width: 35px;font-weight: 700;">组织:</td>
											<td style="width: 250px;font-style: italic;"><label id="per_orgname" ></label></td>
											<td style="width: 35px;font-weight: 700;">姓名:</td>
											<td style="width: 100px;font-style: italic;"><label id="per_name" ></label></td>
										</tr>
									</table>
								</div>
							</ta:box>
							<ta:box fit="true" >
								<ta:box height="50%">
								<ta:panel key="人员权限列表" headerButton="[{'id':'assignPerMission','name':'更改','click':'fnUsePermissionByUser();'}]" fit="true" >
									<ta:datagrid id="perMission" columnFilter="true"  fit="true" forceFitColumns="true" >
										<ta:datagridItem id="menuname" width="250" key="功能名称" formatter="fnMenuname"   />
										<ta:datagridItem id="effecttime" key="有效期" formatter="fnEffecttime"  />
										<ta:datagridItem id="auditstate" key="通过审核" hiddenColumn="true" collection="AUDITSTATE"  />
										<ta:datagridItem width="50" icon="icon-find" align="center" dataAlign="center" key="岗位" click="fnShowPossition" />
	<%--  								<ta:datagridItem key="删除个人权限"  align="center" width="100" icon="icon-remove" click="fnDelPerMission" /> --%>
									</ta:datagrid>
								</ta:panel>
								</ta:box>
								<ta:box height="48%"  cssStyle="margin-top:10px;">
								<ta:panel key="已分配岗位" fit="true" headerButton="[{'id':'assignPos','name':'更改','click':'fnAssignPositionsToUser();'}]" >
									<ta:datagrid id="perPosition" fit="true" columnFilter="true" forceFitColumns="true" >
										<ta:datagridItem id="positionname" key="岗位名称" width="130"  formatter="fnMainPositon" />
										<ta:datagridItem id="orgnamepath" key="所属组织" width="236" showDetailed="true" />
										<ta:datagridItem id="positiontype" key="岗位类型" width="120" formatter="fnPositionType" collection="POSITIONTYPE"   />
										<ta:datagridItem key="设置默认使用岗位" width="50" align="center" dataAlign="center" icon="icon-setting" click="setMainPosition"  />
										<ta:datagridItem key="查看岗位权限" width="50" align="center" dataAlign="center" click="fnQueryPosMission" icon="icon-search"/>
										<ta:datagridItem key="删除" width="50" align="center" click="fnDelPosition" icon="icon-remove" />
									</ta:datagrid>
								</ta:panel>
								</ta:box>
							</ta:box>
					</ta:box>
				</ta:tab>
				<ta:tab id="tabPos" key="岗位权限" layout="border" layoutCfg="{leftWidth:410}">
					<ta:box position="left" cssStyle="padding:10px" >	
						<ta:fieldset id="queryPos"  cols="2">
							<ta:selectTree columnWidth="0.75"  url="positionUserMgAction!webQueryPosTree.do"  labelWidth="33"   fontCss="fnFontCss" selectTreeBeforeClick="fnBeforeClick"  idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']"
							 targetDESC="pos_orgname" treeId="pos_orgTree" targetId="pos_orgid" key="组织"/>
							<ta:box columnWidth="0.25">
								<ta:checkboxgroup id="pos_isDisDecPositions" span="2">
									<ta:checkbox  key="包含子组织" checked="true" value="0" />
								</ta:checkboxgroup>
							</ta:box>
							<ta:text id="positionname" key="岗位"  placeholder="请输入岗位名称..." columnWidth="0.75" labelWidth="33" />
							<ta:buttonLayout columnWidth="0.25" align="left">
								<ta:button key="查找" isok="true" onClick="fnQueryPos();"  icon="xui-icon-query"></ta:button>
							</ta:buttonLayout>
						</ta:fieldset>
						<ta:buttonGroup align="left" cssStyle="margin-top:10px;margin-left:0px;">
							<ta:button key="新增" toolTip="新增岗位" onClick="fnAddPosition()"  isok="true"></ta:button>
							<ta:button id="editPosButton" key="修改" toolTip="修改岗位" disabled="true" onClick="fnGridRowEdit()" ></ta:button>
							<ta:buttonGroupSeparate/>
							<ta:button key="删除"  toolTip="删除岗位" onClick="fnDeletePosition()" id="deletePos"></ta:button>
							<ta:selectButton asToolBarItem="true" key="禁用/启用" id="usePos">
								<ta:selectButtonItem key="禁用" toolTip="逻辑删除岗位" onClick="fnForbiddenPosition()"></ta:selectButtonItem>
								<ta:selectButtonItem key="启用" toolTip="启用禁用岗位" onClick="fnEnablePosition()" ></ta:selectButtonItem>
							</ta:selectButton>
							<ta:selectButton asToolBarItem="true" key="批量权限操作" id="datchPer">
								<ta:selectButtonItem id="grantPosPermissions" key="授予使用权" onClick="fnPosBatchPermissions(2)" />
								<ta:selectButtonItem id="recyclePosPermissions" key="回收使用权" onClick="fnPosBatchPermissions(1)"/>
							</ta:selectButton>
							<ta:buttonGroupSeparate/>
						</ta:buttonGroup>
						<ta:panel key="岗位列表(灰色表示无权进行权限管理，但可以分配人员)" fit="true" hasBorder="true" cssStyle="margin-top:10px;" >
							<ta:datagrid id="positionGrid" forceFitColumns="true" fit="true" onSelectChange="fnSelectChange"  haveSn="true"  selectType="checkbox">
								<ta:datagridItem id="positionname" width="90" key="岗位名称" showDetailed="true" click="fnOpenPosMission" sortable="true" formatter="fnPositionName" />
								<ta:datagridItem id="orgnamepath" width="221" showDetailed="true"  key="所属组织" click="fnOpenPosMission" sortable="true"></ta:datagridItem>
								<ta:dataGridToolPaging url="positionUserMgAction!queryPosition.do" submitIds="queryPos" showExcel="false" pageSize="10" />
							</ta:datagrid>
						</ta:panel>
					</ta:box>	
					<ta:box position="center">
						<ta:box id="posMissionBox" fit="true" cssStyle="overflow:auto;">
							<ta:box height="8%" cssStyle="margin:10px 10px 0px 10px;">
								<ta:text id="pos_positionid" display="false" />
								<ta:text id="pos_positiontype" display="false" />
								<ta:text id="pos_iscopy" display="false" />
								<div>
									<table>
										<tr>
											<td style="width: 62px;font-weight: 700;" align="right">所属组织:</td>
											<td style="width: 250px;font-style: italic;"><label id="pos_orgpath"></label></td>
											<td style="width: 62px;font-weight: 700;">岗位名称:</td>
											<td style="width: 100px;font-style: italic;"><label id="pos_name" ></label></td>
										</tr>
										<tr>
											<td style="width: 62px;font-weight: 700;" align="right">岗位类型:</td>
											<td style="width: 250px;font-style: italic;"><label id="pos_postype" ></label></td>
											<td style="width: 62px;font-weight: 700;">岗位状态:</td>
											<td style="width: 100px;font-style: italic;"><label id="pos_posstatus" ></label></td>
										</tr>
									</table>
								</div>
							</ta:box>
							<ta:box height="50%" cssStyle="margin:0px 10px 10px 10px;">
							<ta:panel key="已分配权限" fit="true" headerButton="[{'id':'pos_MissionBtn','name':'更改','click':'fnUsePermissionByPos();'}]" >
								<ta:datagrid id="posMission" columnFilter="true"  fit="true" forceFitColumns="true" >
									<ta:datagridItem id="menuname" width="450"  key="功能名称" formatter="fnMenuname" />
									<ta:datagridItem id="effecttime"  key="有效期" formatter="fnEffecttime"   />
									<ta:datagridItem id="auditstate"  key="通过审核" hiddenColumn="true" collection="AUDITSTATE"  />
									<ta:datagridItem key="删除" click="fnDelPosMission" align="center" width="50" icon="icon-remove" />
								</ta:datagrid>
							</ta:panel>
							</ta:box>
							<ta:box height="50%" cssStyle="margin:0px 10px 0px 10px;">
							<ta:panel key="岗位下的人员" cssStyle="margin-top:5px;" fit="true" headerButton="[{'id':'pos_UserAssignBtn','name':'更改','click':'fnAssignUser();'},{'id':'pos_UserDelBtn','name':'批量删除','click':'fnDeletePositionUser();'}]" >
								<ta:datagrid id="posUser" haveSn="true" columnFilter="true" forceFitColumns="true" selectType="checkbox"  fit="true" >
									<ta:datagridItem id="loginid"  key="登录号"  />
									<ta:datagridItem id="name" key="姓名" />
									<ta:datagridItem id="orgnamepath" width="350" showDetailed="true" key="所属组织"   />
									<ta:datagridItem key="删除" align="center" width="50" click="fnDeletePositionUserSingle" icon="icon-remove" />
								</ta:datagrid>
							</ta:panel>
							</ta:box>
							<ta:box height="50%" id="sharePosBox" cssStyle="margin:10px 10px 10px 10px;">
							<ta:panel key="共享岗位" fit="true" headerButton="[{'id':'pos_ShareBtn','name':'更改','click':'fnSharePosition();'}]" >
								<ta:datagrid id="posShare" fit="true" columnFilter="true"  >
									<ta:datagridItem id="orgnamepath" width="300" showDetailed="true" key="所属组织"  />
									<ta:datagridItem key="删除" align="center" width="50" click="fnDeleteSharePosition" icon="icon-remove"  />
								</ta:datagrid>
							</ta:panel>
							</ta:box>
						</ta:box>
					</ta:box>
					</ta:tab>
			</ta:tabs>
		<%--<div id="costDiv"
			style="width:400px;height:200px;z-index:9000;
				display:none;position:absolute;border:1px solid #8DB2E3;">
			<ta:panel id="positionPnl" fit="true"  key="人员">
				<ta:datagrid id="positions" fit="true" haveSn="true" forceFitColumns="true" >
					<ta:datagridItem id="positionname" key="岗位名称"  showDetailed="true" width="100"/>
					<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true" width="300"/>
					<ta:datagridItem id="positiontype" key="岗位类型" width="150" formatter="fnPositionType" collection="positiontype" showDetailed="true"/>
				</ta:datagrid>
			</ta:panel>
		</div>
		<div id="closeImg" style="display:none;position:absolute;z-index:9001">
			<img src="ta/resource/themes/ds/icons/icon-cancel.png"/>
		</div>
		<div id="targetImg" style="display:none;position:absolute;z-index:9001">
			<img src="ta/resource/themes/base/slickgrid/images/target.png"/>
		</div> --%>
		<ta:boxComponent height="200px" width="400px" id="b1" arrowPosition="vertical">
			<ta:datagrid id="positions" fit="true" haveSn="true" forceFitColumns="true" >
				<ta:datagridItem id="positionname" key="岗位名称"  showDetailed="true" width="100"/>
				<ta:datagridItem id="orgnamepath" key="组织路径" showDetailed="true" width="150"/>
				<ta:datagridItem id="positiontype" key="岗位类型" width="50" formatter="fnPositionType" collection="positiontype" showDetailed="true"/>
			</ta:datagrid>
		</ta:boxComponent>
	</body>
</html>
<script  type="text/javascript">
function fnOnSelect() {
	Ta.autoPercentHeight();
}
var first = 1;
$(document).ready(function () {
	$("body").taLayout();
	Ta.autoPercentHeight();
	document.body.onclick=function(){
		fnClosePositions();
	};
	fnQueryUser();
	fnQueryPos();
	disablePosBtn();
	disableUserBtn();

	var result = Base.getJson("positionUserMgAction!getOpenPubPosition.do");
	if(result.isopen){
		Base.enableTab("tabPos");
	}else{
		Base.disableTab("tabPos");
	}
	$($(".tabs_163")[1]).click(function(){
		if(result.isopen){
		}else{
			Base.alert("需要管理员开启该功能","warn");
		}
	});
	
	var isAudite = Base.getJson("positionUserMgAction!getIsAudite.do");
	if(isAudite.isAudite){
		Base.setGridColumnShow("perMission", "auditstate");
		Base.setGridColumnShow("posMission", "auditstate");
	}else{
		Base.setGridColumnHidden("perMission","auditstate");
		Base.setGridColumnHidden("posMission","auditstate");
	}
	
	fnPageGuide(parent.currentBuinessId);
	fnInitPage();
});

function fnPageGuide(currentBuinessId){
	var label = $($("#queryUser").find(".selectLabelText_label")[0]).parent();
    var data = [
		  {id:$($(".tabs_163")[1]),
	   	  message:"如需岗位权限,需在config.properties中配置isOpenPubPosition=true开启该功能!"
	      },
	      {id:$(label),
	   	  message:"可以切换姓名和账号以便查询!"
	      }, 
	      {id:$("#userGrid"),
	   	  message:"点击人员可以查询相应的权限和岗位信息!"
	      }, 
	      {id:$("#assignPerMission"),
	   	  message:"可以给该人员分配权限!"
	      }, 
	      {id:$("#perMission"),
	   	  message:"这里包含的是该人员所有岗位拥有的权限!"
	      },
	      {id:$("#assignPos"),
	      message:"可以给该人员分配岗位!"
	      }
	]
	$("body").hintTip({
		replay 	: false,
		show 	: true, 
		cookname: currentBuinessId,
		data 	: data
	}); 
}
/**选择tab是触发的事件*/
function fnInitPage(){

}
//组织树渲染
function fnFontCss(treeId,treeNode){
	if (treeNode.admin != true) return {'color': 'red'};
	return {};
}
//权限判断
function fnBeforeClick(treeId,treeNode){
	if(treeNode.admin != true){
		Base.msgTopTip("你无权操作该组织");
		return false;
	}else{
		return true;
	}
}
/**查询人员*/
function fnQueryUser(){
	Base.submit("queryUser","positionUserMgAction!queryUsers.do",null,null,null,function(){
		var userid =  Base.getValue("per_userid");
		if(userid != ""){
			var array = [{"userid":userid}];
			Base.setSelectRowsByData("userGrid", array);
		}
	});
}
<%-- 表格formatter 用于区别 effective，islock，positiontype 显示不同的颜色 --%>
function fnNameFormatter(row, cell, value, columnDef, dataContext) {
	if (dataContext.effective == 1) {
		return "<span style='color:red;text-decoration:line-through;';>" + value + "</span>";
	}
	if (dataContext.islock == 1) {
		return "<span style='color:yellow;text-decoration:line-through;'>" + value + "</span>";
	}
	if (dataContext.positiontype == 1) {
		return "<span style='color:red'>" + value + "</span>";
	}
	return value;
}
/**打开个人权限岗位信息*/
function fnOpenPermission(data,e){
	Base.gotoGridRow("userGrid", data.row, false);
// 	var obj = (e.target ? e.target : e.srcElement); 
// 	fnFormatterSelect(obj,"#BDC2F3");
	Base.setValue("per_userid", data.userid);
	Base.setValue("per_effective", data.effective);
	Base.setValue("per_positionid", data.positionid);
	$("#per_name").text(data.positionname);
	var param = {"dto['userid']":data.userid};
	Base.submit("queryUser","positionUserMgAction!queryPerMission.do",param,null,null,function(data){
		$("#per_orgname").text(data.fieldData.orgPath);
		enableUserBtn();
	});
}
/**格式化主岗位行*/
function fnMainPositionColor(data){
	if(data.mainposition == 1){
		return "#D7F0FE";
	}
}
/**设置主岗位*/
function setMainPosition(data,e){
	if(data.mainposition == 1){
		Base.alert("\"" + data.positionname + "\"已经为默认使用岗位!","warn");
		return;
	}else{
		Base.confirm("确认设置\""+data.positionname + "\"为默认使用岗位?",function(yes){
			if(yes){
				Base.submit("per_userid","positionUserMgAction!setMainPosition.do",{"dto['positionid']":data.positionid},null,null,function(){
					Base.msgTopTip("设置主岗位成功");
				});
				Base.refreshGrid("perPosition");
			}else{
				
			}
		})
	}
}
/**删除已选岗位*/
function fnDelPosition(data, e){
	if(data.positiontype == "2"){//不能移除个人岗位
		Base.alert("不能移除个人岗位","error");
		return;
	}
	function delPositions(yes) {
		if (yes) {
			Base.submit("per_userid", "positionUserMgAction!removeUserPosition.do", {"dto['positionid']":data.positionid});
			//查询个人权限列表
		}
	}
	Base.confirm("是否删除此岗位",delPositions);
}
/**渲染点击选择后的数据*/
function fnFormatterSelect(obj,color){
	$(obj).parent().siblings().css("backgroundColor","none");
	$(obj).parent().css("backgroundColor",color);
}
/**打开岗位权限信息*/
function fnOpenPosMission(data,e){
// 	var obj = (e.target ? e.target : e.srcElement); 
// 	fnFormatterSelect(obj,"#BDC2F3");
	Base.gotoGridRow("positionGrid", data.row, false);
	Base.setValue("pos_positionid", data.positionid);
	$("#pos_orgpath").text(data.orgnamepath);
	$("#pos_name").text(data.positionname);
	Base.setValue("pos_positiontype", data.positiontype);
	Base.setValue("pos_iscopy", data.iscopy);
	if(data.positiontype==1){
		if(data.iscopy == 1){
			$("#pos_postype").text("复制岗位");
		}else if(data.isshare == 1){
			$("#pos_postype").text("共享岗位");
		}else{
			$("#pos_postype").text("公有岗位");
		}
	}else if(data.positiontype == 2){
		$("#pos_postype").text("个人专属岗位");
	}
	
	if(data.effective == 0){
		$("#pos_posstatus").text("正常");
	}else{
		$("#pos_posstatus").text("无效");
	}
	
	Base.submit("pos_positionid","positionUserMgAction!queryPosMission.do",null,null,null,function(d){
		if(data.iscopy == 1){
			Base.setDisabled("pos_MissionBtn");
		}else{
			enablePosBtn();
		}
		var iscopy = Base.getValue("pos_iscopy");
		if(iscopy == 1){
			Base.hideObj("sharePosBox");
		}else{
			Base.showObj("sharePosBox");
		}
	});
	
}

/**格式化岗位类型*/
function fnPositionType(row, cell, value, columnDef, dataContext){
	if(dataContext.positiontype==1){
		if(dataContext.iscopy == 1){
			return "复制岗位";
		}else if(dataContext.isshare == 1){
			return "共享岗位";
		}else{
			return "公有岗位";
		}
	}else if(dataContext.positiontype == 2){
		return "个人专属岗位";
	}
	return value;
}
/**格式化主岗位*/
function fnMainPositon(row, cell, value, columnDef, dataContext){
	if(dataContext.mainposition == 1){
		return value + "(默认使用)";
	}else{
		return value;
	}
}
/** 格式化有效期*/
function fnEffecttime(row, cell, value, columnDef, dataContext){
	return value;
}

/** 格式化菜单名称*/
function fnMenuname(row, cell, value, columnDef, dataContext){
	if(dataContext.menulevel == "1"){
		return value;
	}else{
		var count = (dataContext.menulevel -1) * 1;
		return "<div style='text-indent: "+ count +"em;'><span style='color:#DBDEE4;'>┊┄</span>"+value+"</div>";
	}
}
function fnFomatter(row, cell, value, columnDef, dataContext){
	if(dataContext.islock == 1){
		return "<span style='color:red;'>"+value+"</span>";
	}
	if(dataContext.effective == 1){
		return "<span style='text-decoration:line-through;'>"+value+"</span>";
	}
	return value;
}

//格式化positionname,禁用的显示废除线,子孙岗位显示蓝色
function fnPositionName(row, cell, value, columnDef, dataContext){
	if(dataContext.effective == 1 && dataContext.isDescendant == 0){
		return "<span style='color:blue;text-decoration:line-through;'>"+value+"</span>";
	}else if(dataContext.effective == 1 && dataContext.isDescendant == null){
		return "<span style='text-decoration:line-through;'>"+value+"</span>";
	}else if(dataContext.isDescendant == 0 && dataContext.effective == 0){
		return "<span style='color:blue;'>"+value+"</span>";
	}else if(dataContext.iscopy == 1) {
		return "<span style='color:#C2B4B4;'>"+value+"</span>";
	}else{
		return value;
	}
}
/**查询岗位*/
function fnQueryPos(){
	Base.submit("queryPos","positionUserMgAction!queryPosition.do",null,null,null,function(){
		var positionid =  Base.getValue("pos_positionid");
		if(positionid != ""){
			var array = [{"positionid":positionid}];
			//Base.setSelectRowsByData("positionGrid", array);
		}
	});
}
/**查询岗位信息*/
function fnOpenPerPosition(data,e){
	//Base.submit("queryPos","positionUserMgAction!queryPositionInfo.do");
}
/**给个人分配权限*/
function fnUsePermissionByUser(){
	var effective = Base.getValue("per_effective");
	var positionid = Base.getValue("per_positionid");
	var orgnamepath = $("#per_orgname").text();
	var userid = Base.getValue("per_userid");
	if(effective == 1){
		Base.alert("不能为无效人员分配权限!!!","error");
		return ;
	}
	Base.openWindow("opPermission", $("#per_name").text() + " ->功能使用权限", "positionUserMgAction!toFuncOpPurview.do", {"dto['positionid']":positionid,"dto['positionType']":2,"dto['orgnamepath']":orgnamepath,"dto['userid']":userid}, "683", "90%",null,null,true);
}


/**删除个人权限*/
function fnDelPerMission(data,e){
	if(data.positiontype !== "2"){
		Base.alert("不能删除其他岗位的权限!!!","error");
		return false;
	}
	function delUserMission(yes) {
		if (yes) {
			Base.submit("per_userid", "positionUserMgAction!getChildData.do",{"dto['idpath']":data.menuidpath,"dto['positionid']":data.positionid,"dto['positionType']":data.positiontype},null,null,function(){
				Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>回收使用权限成功</div>");
			});
		}
	}
	Base.confirm("是否删除此权限",delUserMission);
}
	/**人员批量操作*/
	function fnBatchPermissions(flag){
		var o = Base.getGridSelectedRows("userGrid");	
		if(o.length == 0){
			Base.alert("请选择数据后再进行相关操作","warn");
			return false;
		}else{
			Base.setValue("per_userid",o[0].userid);
			var str = "";
			for(var i = 0 ; i < o.length ; i++){
				str += "{\"positionid\":"+o[i].positionid+"},";
			}
			str ="["+ str.substring(0,str.length-1) + "]";
			switch (flag) {
			case 1:
				Base.openWindow("opWin","批量回收使用权限","positionUserMgAction!toRecyclePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%",null,null,true);
				break;
			case 2:
				Base.openWindow("opWin","批量授予使用权限","positionUserMgAction!toGrantUsePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%",null,null,true);
				break;
			default:
				break;
			}
		}
	}
	/**给人员分配岗位*/
	function fnAssignPositionsToUser(){
		var positionname = $("#per_name").text();
		var userid = Base.getValue("per_userid");
		//已选岗位,用于查询岗位时,排除已选岗位,只查询出未分配岗位
		var selectedData = Base.getGridData("perPosition");
		var str = "";
		if(selectedData && selectedData.length > 0){
			for(var i = 0 ; i < selectedData.length ; i++){
				str += selectedData[i].positionid;
				if( i < selectedData.length - 1){
					str += ",";
				}
			}
		}
		Base.openWindow("win", positionname + "->岗位设置", "positionUserMgAction!toAssignPositionsToUser.do", {"dto['userid']" :userid,"dto['positionids']":str}, "820", "500",null,null,true);
	}
	/**新增岗位*/
	function fnAddPosition(){
		Base.openWindow("addOrEditWin","新增岗位","positionUserMgAction!toAddPosition.do",null,400,300,null,function(){
			//关闭窗口更新表格
			fnQueryPos();
		},true);
	}
	/**表格选中行改变事件*/
	function fnSelectChange(rowsData,n){
		if(n > 0) {
			for(var i = 0 ; i < rowsData.length ; i++) {
				if(rowsData[i].iscopy == 1) {
					Base.setDisabled("editPosButton,deletePos,usePos,datchPer");
					break;
				}
				if(i == rowsData.length - 1) {
					Base.setEnable("editPosButton,deletePos,usePos,datchPer");
				}
				if(n != 1) {
					Base.setDisabled("editPosButton");
				}
			}
		}
		if(n >= 2) {
			disablePosBtn();
		}
	}
	/**编辑岗位*/
	function fnGridRowEdit(){
		var o = Base.getGridSelectedRows("positionGrid");
		if(o != null){
			var param = {};
			param["dto['positionid']"] = o[0].positionid;
			Base.openWindow("addOrEditWin","编辑岗位","positionUserMgAction!editPosition.do",param,400,300,null,function(){
				fnQueryPos()
			},true);
		}
	}
	/**删除岗位*/
	function fnDeletePosition(){
		//判断是否符合
		var flag = checkSelectedGridDatas();
		var selectData = Base.getGridSelectedRows("positionGrid");
		var bool = false;
		var targetPos = Base.getValue("pos_positionid");
		for(var i = 0;i<selectData.length;i++){
			if(targetPos == selectData[i].positionid){
				bool = true;
				break;
			}
		}
		if(flag){
			Base.confirm("确定删除所选岗位?",function(yes){
				if(yes){
					Base.submit("positionGrid","positionUserMgAction!deletePositions.do",null,null,false,function(){
						//Base.deleteGridSelectedRows("positionGrid");
						if(bool){
							Base.setValue("pos_positionid","");
							$("#pos_orgpath").text("");
							$("#pos_name").text("");
							$("#pos_postype").text("");
							$("#pos_posstatus").text("");
							Base.clearGridData("posMission");
							Base.clearGridData("posUser");
							Base.clearGridData("posShare");
							disablePosBtn();
						}
						fnQueryPos();
						Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
					},function(){
						
					});
				}
			},{"title":"删除所选岗位"});
		}
	}
	/**禁用岗位*/
	function fnForbiddenPosition(){
		//判断是否勾选数据
		var flag = checkSelectedGridDatas();
		if(flag){
			Base.confirm("确定禁用所选岗位?",function(yes){
				if(yes){
					Base.submit("positionGrid","positionUserMgAction!unUsePosition.do",null,null,false,function(){
						Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>禁用成功</div>");
						fnQueryPos();
					},function(){
						Base.alert("禁用失败","error");
					});
				}
			},{"title":"禁用所选岗位"});
		}
	}
	/**启用岗位*/
	function fnEnablePosition(){
		//判断是否勾选数据
		var flag = checkSelectedGridDatas();
		if(flag){
			Base.confirm("确定启用所选岗位?",function(yes){
				if(yes){
					Base.submit("positionGrid","positionUserMgAction!usePosition.do",null,null,false,function(){
						Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>启用成功</div>");
						fnQueryPos();
					},function(){
						Base.alert("启用失败","error");
					});
				}
			},{"title":"启用所选岗位"});
		}
	}
	/**岗位批量操作权限*/
	function fnPosBatchPermissions(flag){
		var bool = checkSelectedGridDatas();
		var o = Base.getGridSelectedRows("positionGrid");	
		if(bool){
			var str = "";
			for(var i = 0 ; i < o.length ; i++){
				str += "{\"positionid\":"+o[i].positionid+"},";
			}
			str ="["+ str.substring(0,str.length-1) + "]";
			switch (flag) {
			case 1:
				Base.openWindow("opWin","批量回收使用权限","positionUserMgAction!toRecyclePermissions.do",{"positionids":str,"dto['positionType']":1},"35%","80%",null,null,true);
				break;
			case 2:
				Base.openWindow("opWin","批量授予使用权限","positionUserMgAction!toGrantUsePermissions.do",{"positionids":str,"dto['positionType']":1},"35%","80%",null,null,true);
				break;
			default:
				break;
			}
		}
	}
	
	/**给岗位分配使用权限*/
	function fnUsePermissionByPos(){
		var iscopy = Base.getValue("pos_iscopy");
		if(iscopy == 1){
			Base.alert("不能给复制岗位分配使用权限!!!","warn");
			return;
		}
		var positionid = Base.getValue("pos_positionid");
		Base.openWindow("opPermission", $("#pos_name").text() + " ->功能使用权限", "positionUserMgAction!toFuncOpPurview.do", {"dto['positionid']":positionid,"dto['positionType']":1}, "683", "80%",null,null,true);
	}
	/**查看岗位权限*/
	function fnQueryPosMission(data,e){
		Base.openWindow("posMissionwin", data.positionname + " ->功能使用权限", "positionUserMgAction!toPosMissionWindow.do", {"dto['positionid']":data.positionid,"dto['positionType']":data.positiontype}, "35%", "80%",null,null,true);
	}
	/**删除岗位权限*/
	function fnDelPosMission(data,e){
		var iscopy = Base.getValue("pos_iscopy");
		if(iscopy == 1){
			Base.alert("不能删除复制岗位下的权限!!!","warn");
			return;
		}
		var positiontype = Base.getValue("pos_positiontype");
	
		function delPosMission(yes) {
			if (yes) {
				Base.submit("pos_positionid", "positionUserMgAction!delPosMissionChildData.do",{"dto['idpath']":data.menuidpath,"dto['positionType']":positiontype},null,null,function(){
					Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>回收使用权限成功</div>");
				});
			}
		}
		Base.confirm("是否删除此权限",delPosMission);
		
	}
	/**岗位选择人员*/
	function fnAssignUser(){
		var positionid = Base.getValue("pos_positionid");
		var data = Base.getGridData("posUser");
		var userids = ""
		if(data && data.length > 0){
			for(var i = 0 ; i < data.length ; i++){
				userids += data[i].userid + ",";
			}
		}
		userids = userids.substring(0,userids.length-1);
		Base.openWindow("assignUser",$("#pos_name").text() + "->人员选择","positionUserMgAction!toAssignUser.do",{"dto['positionid']":positionid,"dto['userids']":userids},"820","90%",null,null,true);
	}
	/**移除岗位下的人员*/
	function fnDeletePositionUser(){
		var data = Base.getGridSelectedRows("posUser");
		if(data && data.length > 0){
			Base.confirm("确定移除人员?",function(yes){
				if(yes){
					Base.submit("posUser","positionUserMgAction!removeAssignUsers.do",{"dto['positionid']":Base.getValue("pos_positionid")},null,false,function(){
						Base.deleteGridSelectedRows("posUser");
						Base.msgTopTip("移除人员成功");
					});
				}
			},{"title":"移除人员"});
			
		}else{
			Base.alert("请选择数据后再进行岗位的移除","warn");
		}
	}
	/**移除单个人员*/
	function fnDeletePositionUserSingle(data,e){
		
		Base.confirm("确定移除人员?",function(yes){
			if(yes){
				Base.submit("posUser","positionUserMgAction!removeAssignUsersBySingle.do",{"dto['positionid']":Base.getValue("pos_positionid"),"dto['userid']":data.userid},null,false,function(){
					Base.deleteGridRow("posUser", data.row);
					Base.msgTopTip("移除人员成功");
				});
			}
		},{"title":"移除人员"});
		
	}
	/**共享岗位*/
	function fnSharePosition(){
		var positionid = Base.getValue("pos_positionid");
		Base.openWindow("sharePosition",$("#pos_name").text() + "->共享岗位","positionUserMgAction!toSharePosition.do",{"dto['positionid']":positionid},"40%","90%",null,null,true);
	}
	
	/**判断表格选择数据是否符合*/
	function checkSelectedGridDatas(){
		var o = Base.getGridSelectedRows("positionGrid");
		if(o.length == 0){
			Base.alert("请选择数据后再进行相关操作","warn");
			return false;
		}else{
			for(var i=0;i<o.length;i++){
				var positype = o[i].positiontype;
				var copy = o[i].iscopy;
				//复制岗位不能操作
				if(positype == 1 && copy == 1){
					var msg = o[i].orgnamepath + "-->" + o[i].positionname;
					Base.alert("复制岗位不允许操作!!!","warn");
					return false;
				}
			}
			return true;
		}
		
	}
	
	/**删除共享岗位下的复制岗位*/
	function fnDeleteSharePosition(data,e){
		//判断是否符合
		Base.confirm("确定删除所选岗位?",function(yes){
			if(yes){
				Base.submit("pos_positionid","positionUserMgAction!deleteSharePositions.do",{"dto['orgid']":data.orgid},null,false,function(){
					Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
					fnQueryPos();
				},function(){
					
				});
			}
		},{"title":"删除所选岗位"});
	}
	/**显示岗位表格*/
	function fnShowPossition(data,e){
		/**var src=event.srcElement?event.srcElement:event.target;
		var classname=$(src).attr('class');
		if(classname!="icon-find"){
			return;	
		}	
		var title="["+data.menuname+"]的岗位信息";
		Base.setPanelTitle("positionPnl",title,false);
		Base.submit("","positionUserMgAction!queryPositionHaveMenuByUserid.do",{"dto['userid']": Base.getValue("per_userid"),"dto['menuid']":data.menuid});
		show(e); */
		
		var target=event.srcElement?event.srcElement:event.target;
		var classname=$(target).attr('class');
		if(classname!="icon-find"){
			return;
		}	
		Base.showBoxComponent("b1",target);
		Base.submit("","positionUserMgAction!queryPositionHaveMenuByUserid.do",{"dto['userid']": Base.getValue("per_userid"),"dto['menuid']":data.menuid});
	}
	/**隐藏岗位表格显示*/
	function fnClosePositions(){
		$("#costDiv").hide();
		$("#closeImg").hide();
		$("#targetImg").hide();
	}
	//获取元素的纵坐标 
	function getTop(e){ 
		var offset=e.offsetTop; 
		if(e.offsetParent!=null){
			offset+=getTop(e.offsetParent); 
		} 
		return offset;	 
	} 
	/**展示岗位列表在固定位置*/
	function show(event){
		var src=event.srcElement?event.srcElement:event.target;
		
		var top=getTop(src);
		var left=$(src).offset().left;
		$("#targetImg").css("left", left-420 + "px").css("top", top + "px").hide();
		if (top + 200+10 > $(document).height()) {
				top = top - 200 ;
			}
		$("#costDiv").css("left", left-400 + "px").css("top", top + "px").show();
		$("#closeImg").css("left", left-20 + "px").css("top", top+5 + "px").show();
	}
	/**禁用岗位tab下的按钮*/
	function disablePosBtn (){
		Base.setDisabled("pos_MissionBtn,pos_UserAssignBtn,pos_UserDelBtn,pos_ShareBtn");
	}
	/**启用岗位tab下的按钮*/
	function enablePosBtn (){
		Base.setEnable("pos_MissionBtn,pos_UserAssignBtn,pos_UserDelBtn,pos_ShareBtn");
	}
	/**禁用 人员tab 下的按钮*/
	function disableUserBtn(){
		Base.setDisabled("assignPos");
		Base.setDisabled("assignPerMission");
	}
	/**启用 人员tab 下的按钮*/
	function enableUserBtn(){
		Base.setEnable("assignPos");
		Base.setEnable("assignPerMission");
	}
	function fnUserSelectChange(datas,n) {
		if( n != 1) {
			disableUserBtn();
			Base.clearGridData("perMission");
			Base.clearGridData("perPosition");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>