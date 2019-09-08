<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>工作台</title>
		<link rel="stylesheet" type="text/css" href="style/base.css" />
		<link rel="stylesheet" type="text/css" href="style/jquery.ui.resizable.css" />
		<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String fixPath = "true".equals(AppManager.getSysConfig("true"))?"min.":"";
request.setAttribute("basePath", basePath);
%>
		<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/base/ta-item-all.css" />
		<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/2015/ta-theme-base.css" />
		<link rel="stylesheet" type="text/css" id="linkskin" href="<%=basePath%>ta/resource/themes/2015/blue/ta-theme.css" />
		<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/jquery-1.11.0.min.js" type="text/javascript"></script>
		<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/ta.jquery.ext.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-core-all/TaJsUtil.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/draggable.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-base-all/event.drag-2.2.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/panel.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-contener-all/tauipanel.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-dialog-all/window.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-dialog-all/windowmessage.js" type="text/javascript"></script> 
		<script src="<%=basePath%>ta/resource/external/plugin/ta-api-all/api.window.js" type="text/javascript"></script>
	<!-- 	<script src="script/jquery-1.8.0.min.js" type="text/javascript"></script> -->
		<script src="script/jquery.json.js" type="text/javascript"></script>
		<script src="script/jquery.ui.core.js" type="text/javascript"></script>
		<script src="script/jquery.ui.widget.js" type="text/javascript"></script>
		<script src="script/jquery.ui.mouse.js" type="text/javascript"></script>
		<script src="script/jquery.ui.resizable.js" type="text/javascript"></script>
		<script src="script/portal.js" type="text/javascript"></script>
		<script src="script/jquery.tinysort.js" type="text/javascript"></script>
		<script src="script/jquery.tinysort.charorder.js" type="text/javascript"></script>
		<script src="script/jquery.sorted.js" type="text/javascript"></script>
		
	</head>
	<body>
		<div class="setting-win" id="settingWin">
			<div class="setting-win-header">
				<ul>
					<li class="setting-win-tab" id="tab01"
						onclick="fnToggleTab('tab01')">
						模块选择
					</li>
					<li class="setting-win-tab" id="tab02"
						onclick="fnToggleTab('tab02')">
						模块自定义
					</li>
				</ul>
				<span class="setting-close" title="关闭" onclick="fnCloseSettingWin()"></span>
			</div>
			<ul>
				<li class="setting-tab-bd" id="tab01_bd">
					<div class="select-box" id="selectBox"></div>
				</li>
				<li class="setting-tab-bd" id="tab02_bd">
					<div class="config-box" id="configBox"></div>
				</li>
			</ul>

		</div>
	</body>
</html>
<script type="text/javascript">
	var pageFlag = "index";
	var G_BASE_PATH = "<%=basePath%>";
	var portal = null;
	var docWidth = $(document).width();
	var docHeight = "";
	$(document).ready(function(){
		portal = new YH.Portal("portalMain",{column:"4:3:3"});
		setTimeout(function(){docHeight = document.documentElement.scrollHeight;}, 1);
	})
	function fnShowSettingWin() {
		//alert([document.body.clientHeight,document.body.scrollHeight,window.screen.availHeight,document.documentElement.clientHeight])
		var settingWin = $("#settingWin");
		portal.showLayer();
		if ( $.browser.msie && parseFloat(navigator.appVersion.split("MSIE")[1]) == 8){
 			settingWin.css({
 				left : (docWidth - settingWin.width()) / 2,
 				opacity : 0
 			}).show().animate(
 					{
 						top : (docHeight - settingWin.height()) / 2,
 						opacity : 1
 					});
		}else{
			settingWin.css({
				left : (docWidth - settingWin.width()) / 2,
				opacity : 0
			}).show().animate(
					{
						top : (document.documentElement.clientHeight - settingWin.height()) / 2,
						opacity : 1
					});
		}
		portal.addSelectList(defaultItems);
		portal.addConfigList();
		fnToggleTab("tab01");
	}
	function fnCloseSettingWin() {
		var settingWin = $("#settingWin");
		settingWin.animate({
			top : document.documentElement.clientHeight,
			opacity : 0
		}, function() {
			settingWin.hide().css({
				top : 0,
				opacity : 0
			});
			portal.hideLayer();
		});
		window["G_COLUMN_INDEX"] = null;
	}
	function fnToggleTab(id) {
		$(".setting-win-tab").removeClass("setting-win-tab-act");
		$("#" + id).addClass("setting-win-tab-act");
		$(".setting-tab-bd").hide();
		$("#" + id + "_bd").show();
	}
	function fnAddCommonMenu(){
		Base.openWindow("addWin", "添加常用菜单", "<%=basePath%>sysapp/commonMenuAction!toAddCommonMenus.do",{},400,450,null,function(){
			location.href= location.href;
		},true);
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>