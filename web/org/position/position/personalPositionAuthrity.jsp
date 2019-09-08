<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>人员岗位及权限管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:box fit="true" layout="border" layoutCfg="{leftWidth:240}">
			<ta:box position="left" key="查询" cssClass="left-themes-color" >
				<ta:form id="query" fit="true" cssStyle="overflow-y:auto;overflow-x:hidden;padding:0 16px 0 0;">
					<ta:text id="username" key="姓名" labelWidth="70"/>
					<ta:selectTree url="personalPositionMgAction!webQueryAsyncOrgTree.do" cssStyle="width:200px" labelWidth="70" fontCss="fnFontCss" selectTreeBeforeClick="fnBeforeClick"  idKey="orgid" parentKey="porgid" nameKey="orgname" asyncParam="['orgid']"
					 targetDESC="orgname" treeId="orgTree" targetId="orgid" key="部门"/>
					<ta:radiogroup id="isDisDecPositions" key="子部门人员" cols="2" labelWidth="70">
						<ta:radio key="显示" value="0"/>
						<ta:radio key="不显示" checked="true" value="1"/>
					</ta:radiogroup>
					<ta:text id="loginids" key="登录号" span="2" textHelp="支持多loginid查询,以逗号隔开,例如:developer,test_01,test_02"  labelWidth="70"/>
					<ta:radiogroup id="effective"  key="有效标志"  labelWidth="70" cols="3">
						<ta:radio id="effective_yes" key="有效" value="0"/>
						<ta:radio id="effective_no" key="无效" value="1" labelStyle="text-decoration: line-through;"/>
						<ta:radio id="effective_all" key="不限" value="-1" checked="true"/>
					</ta:radiogroup>
					<ta:radiogroup id="sex" key="性别"  labelWidth="70" cols="3">
						<ta:radio id="sex_nan" key="男" value="1" />
						<ta:radio id="sex_nv" key="女" value="2"/>
						<ta:radio id="sex_all" key="不限" value="-1" checked="true"/>
					</ta:radiogroup>
					<ta:radiogroup id="islock"  key="锁定标志" cols="3" labelWidth="70">
						<ta:radio id="islock_yes" key="是" value="1" labelStyle="color:red;"/>
						<ta:radio id="islock_no" key="否" value="0"/>
						<ta:radio id="islock_all" key="不限" value="-1" checked="true"/>
					</ta:radiogroup>
					<ta:buttonLayout>
						<ta:button key="查询"  onClick="fnQueryPositions()" isok="true" icon="xui-icon-query"></ta:button>
						<ta:button key="重置"  onClick="Base.resetForm('query')" icon="xui-icon-reset"></ta:button>
					</ta:buttonLayout>
				</ta:form>
			</ta:box>
			<ta:box position="center" >
				<ta:box cssStyle="height:48px;padding:5px;">
					<ta:buttonGroup align="left">
						<ta:selectButton key="批量权限操作">
							<ta:selectButtonItem  id="grantPermissions" key="授予使用权"   onClick="fnBatchPermissions(2)" />
							<ta:selectButtonItem  id="recyclePermissions" key="回收使用权"  onClick="fnBatchPermissions(1)"/>
						</ta:selectButton>
						<ta:buttonGroupSeparate/>
						<ta:button  id="clonePermissions" key="复制使用权"  onClick="fnClonePermissions()"/>
					</ta:buttonGroup>
				</ta:box>
				<ta:panel  fit="true"  hasBorder="false"  bodyStyle="border:0px">
					<ta:datagrid id="positionPersonalGrid" fit="true" haveSn="true"  onChecked="fnChecked" selectType="checkbox" forceFitColumns="true">
						<ta:datagridItem id="loginid" key="登录号" width="100"   formatter="fnFomatter"></ta:datagridItem>
						<ta:datagridItem id="positionname" key="姓名" width="100"  showDetailed="true" sortable="true"></ta:datagridItem>
						<ta:datagridItem id="sex" key="性别" width="70" collection="sex"  ></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所属部门" width="300"  showDetailed="true" sortable="true"></ta:datagridItem>
						<%-- <ta:datagridItem id="opPostions" key="岗位设置" icon="xui-icon-add2" width="70" align="center" dataAlign="center" click="fnAssignPositionsToUser"></ta:datagridItem> --%>
						<ta:datagridItem id="usePer" key="使用权限" icon="icon-setting" width="70" align="center" click="fnUsePermission"></ta:datagridItem>
						<ta:datagridItem id="username" key="创建人" width="100"  sortable="true" showDetailed="true"></ta:datagridItem>
						<ta:datagridItem id="createtime" key="创建时间" width="150"  dataType="date" sortable="true"></ta:datagridItem>
						<ta:dataGridToolPaging url="personalPositionMgAction!queryPositions.do" submitIds="query" showExcel="true" pageSize="200"  selectExpButtons="1,2"></ta:dataGridToolPaging>
					</ta:datagrid>
				</ta:panel>
			</ta:box>
		</ta:box>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		fnQueryPositions();
	});
	//查询岗位信息
	function fnQueryPositions(){
		Base.submit("query","<%=basePath%>org/position/personalPositionMgAction!queryPositions.do");
	}
	//批量操作权限
	function fnBatchPermissions(flag){
		var o = Base.getGridSelectedRows("positionPersonalGrid");	
		if(o.length == 0){
			Base.alert("请选择数据后再进行相关操作","warn");
			return false;
		}else{
			var str = "";
			for(var i = 0 ; i < o.length ; i++){
				str += "{\"positionid\":"+o[i].positionid+"},";
			}
			str ="["+ str.substring(0,str.length-1) + "]";
			switch (flag) {
			case 1:
				Base.openWindow("opWin","批量回收使用权限","<%=basePath%>org/position/personalPositionMgAction!toRecyclePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%");
				break;
			case 2:
				Base.openWindow("opWin","批量授予使用权限","<%=basePath%>org/position/personalPositionMgAction!toGrantUsePermissions.do",{"positionids":str,"dto['positionType']":2},"35%","80%");
				break;
			case 3:
				Base.openWindow("grantingWin","批量回收授权权限","<%=basePath%>org/position/personalPositionMgAction!toRecycleAuthorityPermissions.do",{"positionids":str,"dto['positionType']":2},"70%","80%");
				break;
			case 4:
				Base.openWindow("grantingWin","批量授予授权权限","<%=basePath%>org/position/personalPositionMgAction!toGrantAuthorityPermissions.do",{"positionids":str,"dto['positionType']":2},"70%","80%");
				break;

			default:
				break;
			}
		}
	}
	function fnClonePermissions() {
		var sdata = Base.getGridSelectedRows("positionPersonalGrid");
		if(sdata && sdata.length > 0){
			Base.openWindow("cloneWin","复制使用权限","personalPositionMgAction!toClonePermissions.do",null,"60%","90%",function(){
				Base.submit("query","personalPositionMgAction!queryPositionsClone.do");
			});
		}else{
			Base.alert("请勾选人员后,再进行复制使用权的操作","warn");
		}
	}
	function fnAssignPositionsToUser(data,e){
		if(data.effective == 1){
			return ;
		}
		Base.openWindow("win", data.positionname + "->岗位设置", "<%=basePath%>org/position/personalPositionMgAction!toAssignPositionsToUser.do", {"dto['userid']" :data.userid}, "90%", "500",function(){
			var treeObj = $.fn.zTree.getZTreeObj("orgTreeUserPosition");
			var nodes = treeObj.getNodes();
			if (nodes.length>0) {//默认选中根节点
				treeObj.selectNode(nodes[0]);
			}
			//取消默认的关闭事件,添加自定义关闭事件
// 			var $tool = $("#win").parent().find("div.panel-tool").eq(0);
// 			$tool.children("div.panel-tool-close").remove();
// 			$tool.append("<div class='panel-tool-close' onclick='fnOnclick()'></div>");
// 			$tool.children("div").css("float","left");
			//默认以根节点进行查询
			fnClick();
		});
	}
	//使用权限
	function fnUsePermission(data,e){
		if(data.effective == 1){
			return ;
		}
		Base.openWindow("opWin", data.positionname + "->功能使用权限", "<%=basePath%>org/position/personalPositionMgAction!toFuncOpPurview.do", {"dto['positionid']":data.positionid,"dto['positionType']":2}, "35%", "80%");
	}
	//授权权限
	function fnRePermission(data,e) {
		if(data.effective == 1){
			return ;
		}
		//if (o.type != "02") return Base.alert("非管理员岗位不能授授权权限。"), false;
		Base.openWindow("grantingWin", data.positionname + "->功能授权权限", "<%=basePath%>org/position/personalPositionMgAction!toFuncGrantingPurview.do", {"dto['positionid']":data.positionid,"dto['positionType']":2}, "70%", "80%");
	}
	//判断表格是否选择数据
	function checkSelectedGridDatas(){
		var o = Base.getGridSelectedRows("positionPerGrid");	
		if(o.length == 0){
			Base.alert("请选择数据后再进行相关操作","warn");
			return false;
		}else{
			return true;
		}
	}
	
	function fnFomatter(row, cell, value, columnDef, dataContext){
		if(dataContext.islock == 1){
			return "<span style='color:red;'>"+value+"</span>";
		}
		if(dataContext.effective == 1){
			return "<span style='text-decoration:line-through;'>"+value+"</span>";
		}
// 		if(dataContext.effective == 1 && dataContext.isDescendant == 0){
// 			return "<span style='color:blue;text-decoration:line-through;'>"+value+"</span>";
// 		}else if(dataContext.effective == 1 && dataContext.isDescendant == null){
// 			return "<span style='text-decoration:line-through;'>"+value+"</span>";
// 		}else if(dataContext.isDescendant == 0 && dataContext.effective == 0){
// 			return "<span style='color:blue;'>"+value+"</span>";
// 		}
		return value;
	}
	//无效人员不允许操作
	function fnChecked(data){
		if(data.effective == 1){
			return false;
		}else{
			return true;
		}
	}
	//自定义关闭岗位设置窗口事件,提示是否设置主岗位
	function fnOnclick(){
		Base.confirm("是否设置或者修改主岗位?",function(yes){
			if(yes){
				return;
			}else{
				Base.closeWindow("win");
			}
		})
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
</script>
<%@ include file="/ta/incfooter.jsp"%>