<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>功能权限委派</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="padding:10px;">
		<ta:pageloading/>
		<div id="hintbody" style="display:none;"></div>
		<ta:buttonLayout align="left" cssStyle="padding-left:0px;">
			<ta:button key="新增委派人员" icon="icon-add1" onClick="fnAdd()" isok="true"></ta:button>
			<ta:button key="批量删除" icon="icon-no" onClick="fnRemove()" disabled="true"  id="btnRemove"></ta:button>
		</ta:buttonLayout>
		<ta:panel fit="true" key="委派岗位列表" cssStyle="margin-top:10px;">
			<ta:datagrid fit="true" selectType="checkbox" haveSn="true" id="deletegatedGrid" onSelectChange="fnSelectChange">
				<ta:datagridItem id="positionname" key="姓名" width="100"></ta:datagridItem>
				<ta:datagridItem id="orgnamepath" key="组织路径" width="400"  showDetailed="true"></ta:datagridItem>
				<ta:datagridItem id="validtime" key="委派截止时间" width="200" dataType="dateTime"></ta:datagridItem>
				<ta:datagridItem id="permissionInfo" key="查看/修改权限" width="100" icon="icon-find" click="fnPermissionInfo"></ta:datagridItem>
				<%-- <ta:datagridItem id="rec" key="回收" width="100" icon="icon-remove" click="fnRemove"></ta:datagridItem> --%>
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		fnPageGuide(parent.currentBuinessId);
	});
	function fnPageGuide(currentBuinessId){
		$("body").hintTip({
			replay 	: false,
			show 	: true, 
			cookname: currentBuinessId,
			data 	: [
			        {id:$("#hintbody"),
				    	message:"此功能用于将当前管理员所拥有的授权权限中的部分功能委派给某个人员，该人员将在特定的时间之内拥有这些功能的使用权限，超过这个特定时间将不再具有使用权限"
				        }
					]
		}); 
	}
	function fnAdd(){
		Base.openWindow("add","新增功能委派权限","delegatePositionAction!addDeletegatePosition.do",{},"80%","80%",null,function(){
			Base.submit("","delegatePositionAction!queryDeletegatePosition.do");
		},true);
	}
	function fnPermissionInfo(data,e){
		Base.openWindow("permissionInfo",data.positionname +"的代理权限","delegatePositionAction!queryPermissionInfoByPositionid.do",{"positionid":data.positionid,"validtime":data.validtime},350,400,function(){
// 			$("#validtime").parent().parent().css("margin-top","1px");
		},function(){
			Base.submit("","delegatePositionAction!queryDeletegatePosition.do");
		});
	}
	function fnRemove(){
		var datas = Base.getGridSelectedRows("deletegatedGrid");
		if(datas && datas.length > 0){
			var positionids = "[";
			for(var i = 0 ; i < datas.length; i++){
				positionids += "{\"positionid\":" +datas[i].positionid+"}";
				if(i < datas.length-1){
					positionids += ",";
				}
			}
			positionids +="]";
			Base.submit("","delegatePositionAction!recycleDeletegatePosition.do",{"positionids":positionids},null,null,function(){
				Base.alert("批量删除成功","success");
				Base.deleteGridSelectedRows("deletegatedGrid");
			});
		}else{
			Base.alert("请选择需要回收的委派人员","warn");
		}
	}
	function fnSelectChange(data){
		if(data && data.length > 0){
			Base.setEnable("btnRemove");
		}else{
			Base.setDisabled("btnRemove");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>