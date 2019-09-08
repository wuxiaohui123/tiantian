<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>功能权限委派</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
	<ta:pageloading />
	<ta:box layout="border" layoutCfg="{leftWidth:250}">
		<ta:box position="left" cssStyle="overflow:auto;">
			<ta:tree id="grantTree" checkable="true"
				chkboxType="{'Y':'ps', 'N':'ps'}" fontCss="fnSetFont"></ta:tree>
		</ta:box>
		<ta:box position="center" cssStyle="padding:10px 10px 5px 10px;">
			<ta:box cols="2">
				<ta:date id="deletegateTime" key="委派截止时间" showSelectPanel="true"
					validNowTime="right" required="true" datetime="true"></ta:date>
			</ta:box>
			<ta:panel fit="true" key="可委派人员" cssStyle="margin-top:10px;"
				hasBorder="false" withButtonBar="true">
				<ta:datagrid fit="true" selectType="radio" haveSn="true"
					id="personalGrid" forceFitColumns="true">
					<ta:datagridItem id="name" key="姓名" width="100"></ta:datagridItem>
					<ta:datagridItem id="orgnamepath" key="组织路径" width="400"
						showDetailed="true"></ta:datagridItem>
				</ta:datagrid>
				<ta:panelButtonBar>
					<ta:button isok="true" key="保存" icon="icon-add1"
						onClick="fnSaveDeletegatePosition()"></ta:button>
					<ta:button key="关闭" icon="icon-no" onClick="fnClose()"></ta:button>
				</ta:panelButtonBar>
			</ta:panel>
		</ta:box>
	</ta:box>
</body>
</html>
<script>
	$(document).ready(function() {
		$("body").taLayout();
	});
	function fnSaveDeletegatePosition() {
		var tree = Base.getObj("grantTree");
		var nodes = tree.getChangeCheckedNodes();
		var user = Base.getGridSelectedRows("personalGrid");
		if (nodes && nodes.length < 1) {
			Base.alert("请选择功能菜单！", "warn");
			return;
		}
		if (user && user.length < 1) {
			Base.alert("请选择人员！", "warn");
			return;
		}
		var len = nodes.length;
		var str = "";
		for (var i = 0; i < len; i++) {
			var node = nodes[i];
			if (node.isyab003 != null) {
				var pnode = node.getParentNode();
				str += "{\"id\":\"" + node.id + "\",\"checked\":"
						+ node.checked + ",\"isyab003\":" + node.isyab003
						+ ",\"menuid\":" + pnode.id + "},";
			} else {
				str += "{\"id\":\"" + node.id + "\",\"checked\":"
						+ node.checked + "},";
			}
		}
		if (str != "") {
			str = "[" + str.substr(0, str.length - 1) + "]";
			var date = Base.getValue("deletegateTime");
			if (date == "" || date == null) {
				Base.alert("请选择委派截止时间！", "warn", function() {
					Base.focus("deletegateTime");
				});
				return;
			}
			Base.submit("","delegatePositionAction!deletegatePosition.do",{"ids" : str,"dto['userid']" : user[0].userid,"dto['deletegateTime']" : date},null,true,function() {
				parent.Base.alert("委派成功","success");
				fnClose();
			});
		}
	}
	function fnClose() {
		parent.Base.closeWindow("add");
	}
	function fnSetFont(treeId, treeNode) {
		if (treeNode.policy == 4 || treeNode.policy == 3)
			return {
				color : "red"
			};
		if (treeNode.isyab003 != null) {
			return {
				color : "blue"
			};
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>