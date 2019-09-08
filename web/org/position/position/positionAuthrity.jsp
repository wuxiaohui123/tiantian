<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>岗位及权限管理</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="padding:0px;margin:0px">
		<ta:pageloading/>
		<ta:box fit="true" layout="border" layoutCfg="{leftWidth:240}">
			<ta:box position="left" key="查询" cssClass="left-themes-color" >
				<ta:form id="query" fit="true"  cssStyle="overflow-y:auto;padding:0 16px 0 0;overflow-x:hidden;">
					<ta:text id="positionname" key="岗位名称" cssInput="min-width:100px" labelWidth="70"/>
					<ta:selectTree asyncParam="['orgid']"  cssStyle="width:200px"  url="positionMgAction!webQueryAsyncOrgTree.do" labelWidth="70" fontCss="fnFontCss" selectTreeBeforeClick="fnBeforeClick"  idKey="orgid" parentKey="porgid" nameKey="orgname"
						 targetDESC="orgname" treeId="orgTree" targetId="orgid" key="部门"/>
					<ta:radiogroup id="isDisDecPositions" key="子部门岗位" cols="2" labelWidth="70">
						<ta:radio key="显示" value="0"/>
						<ta:radio key="不显示" checked="true" value="1"/>
					</ta:radiogroup>
					<ta:radiogroup id="effective"  key="有效标志"  labelWidth="70" cols="3">
						<ta:radio id="effective_yes" key="有效" value="0"/>
						<ta:radio id="effective_no" key="无效" value="1" labelStyle="text-decoration: line-through;"/>
						<ta:radio id="effective_all" key="不限" value="-1" checked="true"/>
					</ta:radiogroup>
					<ta:buttonLayout>
						<ta:button key="查询"  onClick="fnQueryPositions()" isok="true" icon="xui-icon-query"></ta:button>
						<ta:button key="重置"  onClick="Base.resetForm('query')" icon="xui-icon-reset"></ta:button>
					</ta:buttonLayout>
				</ta:form>
			</ta:box>
			<ta:box position="center" >
				<ta:box cssStyle="height:48px;padding:5px;" cols="1">
					<ta:buttonGroup align="left">
						<ta:button key="新增岗位[A]" toolTip="新增岗位" onClick="fnAddPosition()" hotKey="a" isok="true"></ta:button>
						<ta:buttonGroupSeparate/>
						<ta:selectButton key="岗位操作" >
							<ta:selectButtonItem key="禁用" toolTip="逻辑删除岗位" onClick="fnForbiddenPosition()"></ta:selectButtonItem>
							<ta:selectButtonItem key="启用" toolTip="启用禁用岗位" onClick="fnEnablePosition()" ></ta:selectButtonItem>
							<ta:selectButtonItem key="删除选中" toolTip="删除选中" onClick="fnDeletePosition()"></ta:selectButtonItem>
						</ta:selectButton>
						<ta:selectButton key="批量权限操作" >
							<ta:selectButtonItem id="grantPermissions" key="授予使用权" onClick="fnBatchPermissions(2)" />
							<ta:selectButtonItem id="recyclePermissions" key="回收使用权" onClick="fnBatchPermissions(1)"/>
						</ta:selectButton>
					</ta:buttonGroup>
				</ta:box>
				<ta:panel  fit="true" hasBorder="false" bodyStyle="border:0px;">
					<ta:datagrid id="positionPerGrid" fit="true" haveSn="true"  selectType="checkbox" forceFitColumns="true">
						<ta:datagridItem id="positionname" key="岗位名称" width="150" showDetailed="true" sortable="true" formatter="fnPositionName"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="所属部门" width="300"  sortable="true"></ta:datagridItem>
						<ta:datagridItemOperate showAll="false" id="opt" name="操作">
							<ta:datagridItemOperateMenu name="编辑" icon="a" click="fnGridRowEdit"></ta:datagridItemOperateMenu>
							<ta:datagridItemOperateMenu name="功能权限" icon="a" click="fnUsePermission"></ta:datagridItemOperateMenu>
							<ta:datagridItemOperateMenu name="共享岗位设置" icon="a" click="fnSharePosition"></ta:datagridItemOperateMenu>
						</ta:datagridItemOperate>
						<ta:datagridItem id="username" key="创建人" width="100"  showDetailed="true" sortable="true"></ta:datagridItem>
						<ta:datagridItem id="createtime" key="创建时间" width="150"  dataType="date" sortable="true"></ta:datagridItem>
						<ta:dataGridToolPaging url="positionMgAction!queryPositions.do" submitIds="query" showExcel="false" pageSize="200"></ta:dataGridToolPaging>
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
		Base.submit("query","<%=basePath%>org/position/positionMgAction!queryPositions.do");
	}
	
	//新增岗位
	function fnAddPosition(){
		Base.openWindow("addOrEditWin","新增岗位","<%=basePath%>org/position/positionMgAction!addPosition.do",null,400,300,null,function(){
			//关闭窗口更新表格
			fnQueryPositions();
		},true);
	}
	//物理删除岗位
	function fnDeletePosition(){
		var flag = checkSelectedGridDatas();
		if(flag){
			Base.confirm("确定删除所选岗位?",function(yes){
				if(yes){
					Base.submit("positionPerGrid","<%=basePath%>org/position/positionMgAction!deletePositions.do",null,null,false,function(){
						Base.deleteGridSelectedRows("positionPerGrid");
						Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
					},function(){
						
					});
				}
			},{"title":"删除所选岗位"});
		}
	}
	//禁用岗位
	function fnForbiddenPosition(){
		//判断是否勾选数据
		var flag = checkSelectedGridDatas();
		if(flag){
			Base.confirm("确定禁用所选岗位?",function(yes){
				if(yes){
					Base.submit("positionPerGrid","<%=basePath%>org/position/positionMgAction!unUsePosition.do",null,null,false,function(){
						Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>禁用成功</div>");
						fnQueryPositions();
					},function(){
						Base.alert("禁用失败","error");
					});
				}
			},{"title":"禁用所选岗位"});
		}
	}
	//启用岗位
	function fnEnablePosition(){
		//判断是否勾选数据
		var flag = checkSelectedGridDatas();
		if(flag){
			Base.confirm("确定启用所选岗位?",function(yes){
				if(yes){
					Base.submit("positionPerGrid","<%=basePath%>org/position/positionMgAction!usePosition.do",null,null,false,function(){
						Base.msgTopTip("<div style='width:70px;margin:0 auto;font-size:16px;margin-top:15px;text-align:center;'>启用成功</div>");
						fnQueryPositions();
					},function(){
						Base.alert("启用失败","error");
					});
				}
			},{"title":"启用所选岗位"});
		}
	}
	//批量操作权限
	function fnBatchPermissions(flag){
		var o = Base.getGridSelectedRows("positionPerGrid");	
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
				Base.openWindow("opWin","批量回收使用权限","<%=basePath%>org/position/positionMgAction!toRecyclePermissions.do",{"positionids":str,"dto['positionType']":1},"35%","80%");
				break;
			case 2:
				Base.openWindow("opWin","批量授予使用权限","<%=basePath%>org/position/positionMgAction!toGrantUsePermissions.do",{"positionids":str,"dto['positionType']":1},"35%","80%");
				break;
			case 3:
				Base.openWindow("grantingWin","批量回收授权权限","<%=basePath%>org/position/positionMgAction!toRecycleAuthorityPermissions.do",{"positionids":str,"dto['positionType']":1},"70%","80%");
				break;
			case 4:
				Base.openWindow("grantingWin","批量授予授权权限","<%=basePath%>org/position/positionMgAction!toGrantAuthorityPermissions.do",{"positionids":str,"dto['positionType']":1},"70%","80%");
				break;

			default:
				break;
			}
		}
	}
	//编辑
	function fnGridRowEdit(data,e){
		var param = {};
		param["dto['positionid']"] = data.positionid;
		param["dto['row']"] = data.row;//存储所要编辑的岗位的行数
		Base.openWindow("addOrEditWin","编辑岗位","<%=basePath%>org/position/positionMgAction!editPosition.do",param,400,300,null,function(){
			//更新表格
			fnQueryPositions();
		},true);
	}
	//人员选择
	function fnAssignUser(data,e){
		Base.openWindow("assignUser",data.positionname + "->人员选择","<%=basePath%>org/position/positionMgAction!toAssignUser.do",{"dto['positionid']":data.positionid},"90%","90%",null,null,true);
	}
	//共享岗位
	function fnSharePosition(data,e){
		Base.openWindow("sharePosition",data.positionname + "->共享岗位","<%=basePath%>org/position/positionMgAction!toSharePosition.do",{"dto['positionid']":data.positionid},"40%","90%",null,null,true);
	}
	//使用权限
	function fnUsePermission(data,e){
		Base.openWindow("opWin", data.positionname + "->功能使用权限", "<%=basePath%>org/position/positionMgAction!toFuncOpPurview.do", {"dto['positionid']":data.positionid,"dto['positionType']":1}, "35%", "80%");
	}
	//授权权限
	function fnRePermission(data,e) {
		//if (o.type != "02") return Base.alert("非管理员岗位不能授授权权限。"), false;
		Base.openWindow("grantingWin", data.positionname + "->功能授权权限", "<%=basePath%>org/position/positionMgAction!toFuncGrantingPurview.do", {"dto['positionid']":data.positionid,"dto['positionType']":1}, "70%", "80%");
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
	//格式化positionname,禁用的显示废除线,子孙岗位显示蓝色
	function fnPositionName(row, cell, value, columnDef, dataContext){
		if(dataContext.effective == 1 && dataContext.isDescendant == 0){
			return "<span style='color:blue;text-decoration:line-through;'>"+value+"</span>";
		}else if(dataContext.effective == 1 && dataContext.isDescendant == null){
			return "<span style='text-decoration:line-through;'>"+value+"</span>";
		}else if(dataContext.isDescendant == 0 && dataContext.effective == 0){
			return "<span style='color:blue;'>"+value+"</span>";
		}else{
			return value;
		}
	}
	//组织树渲染
	function fnFontCss(treeId,treeNode){
		if (treeNode.admin != true) return {'color': 'red'};
		return {};
	}
	//权限判断
	function fnBeforeClick(treeId,treeNode){
		if(treeNode.admin != true){
			Base.msgTopTip("<div class='msgTopTip'>你无权操作该组织</div>");
			return false;
		}else{
			return true;
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>