<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>相似权限授权</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="margin:0px;padding:10px;">
		<ta:pageloading/>
		<div id="hintbody" style="display:none;"></div>
		<ta:box height="100%" cols="2">
			<ta:box height="100%" columnWidth="0.3">
				<ta:panel fit="true" key="第一步：选择同时拥有某几个功能权限的岗位(或人员)" titleAlign="left" withButtonBar="true" >
					<ta:box cssStyle="overflow:auto;" fit="true" id="hintleft">
						<ta:tree id="authorityTree" checkable="true" chkboxType="{'Y':'ps', 'N':'ps'}"></ta:tree>
					</ta:box>
					<ta:panelButtonBar cssStyle="border:0px;">
						<ta:button id="btnQuery" key="查找" onClick="fnQuery()"  icon="xui-icon-query"></ta:button>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
			<ta:box height="100%" columnWidth="0.7" cssStyle="margin-left:10px;">
				<ta:panel fit="true" key="第二步：选择需要授权的岗位(或人员)" titleAlign="left" withButtonBar="true">
					<ta:datagrid fit="true" haveSn="true" id="positionGrid" selectType="checkbox" forceFitColumns="true">
						<ta:datagridItem id="positionname" key="岗位名称" width="200"></ta:datagridItem>
						<ta:datagridItem id="positiontype" key="岗位类型" collection="POSITIONTYPE" width="150"></ta:datagridItem>
						<ta:datagridItem id="orgnamepath" key="组织路径" width="400"></ta:datagridItem>
					</ta:datagrid>
					<ta:panelButtonBar cssStyle="border:0px;">
						<ta:button id="btnEdit" key="授权"  onClick="fnToSimilarAuthority()" icon="icon-add1" isok="true"></ta:button>
					</ta:panelButtonBar>
				</ta:panel>
			</ta:box>
		</ta:box>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		Ta.autoPercentHeight();
		fnPageGuide(parent.currentBuinessId);
	});
	function fnPageGuide(currentBuinessId){
		$("body").hintTip({
			replay 	: false,
			show 	: true, 
			cookname: currentBuinessId,
			data 	: [
			        {id:$("#hintbody"),
				    	message:"此功能可给具有某些相同功能使用权限的岗位，授予其他一些共同的权限"
				        },
				        {id:$("#hintleft"),
				    	message:"勾选菜单，点击[查询]将在右边表格中查询出具有这些菜单使用权限的所有岗位"
				        },
				        {id:$("#positionGrid"),
				        message:"勾选表格中的某些岗位，点击[授权]将弹出授权界面对勾选岗位授予一些共同的权限"
					    }
					]
		}); 
	}
	function fnQuery(){
		var grid = Base.getObj("authorityTree");
		var nodes = grid.getChangeCheckedNodes();
		var len = nodes.length;
		var str = "";
		for (var i = 0; i < len; i++) {
			str += "{\"id\":\"" + nodes[i].id + "\",\"checked\":" + nodes[i].checked + "},";
		}
		if (str != "") {
			str = "[" + str.substr(0, str.length - 1) + "]";
			Base.submit("", "similarAuthorityAction!querypositionsByAuthorities.do", {"ids":str});
		}
	}
	function fnToSimilarAuthority(){
		var datas = Base.getGridSelectedRows("positionGrid");
		if(datas && datas.length > 0){
			var str = "";
			for(var i = 0; i < datas.length ; i++){
				str += "{\"id\":\"" + datas[i].positionid + "\",\"type\":" + datas[i].positiontype + "},";
			}
			if (str != "") {
				str = "[" + str.substr(0, str.length - 1) + "]";
				Base.openWindow("similar","相似权限授权","similarAuthorityAction!toSimilarAuthority.do",{"positionids":str},"35%","80%");
			}
		}else{
			Base.alert("请选择岗位后再进行授权","warn");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>