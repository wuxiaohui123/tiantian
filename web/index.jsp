<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="com.yinhai.sysframework.util.json.JSonFactory"%>
<%@page import="com.yinhai.sysframework.util.WebUtil"%>
<%@page import="com.yinhai.sysframework.iorg.IUser"%>
<%@page import="com.yinhai.sysframework.iorg.IPosition"%>
<%@page import="com.yinhai.sysframework.util.IConstants"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<%@page import="com.yinhai.sysframework.menu.IMenu"%>
<%
	String curPageUrl=request.getRequestURI();
	curPageUrl = curPageUrl.substring(curPageUrl.lastIndexOf("/")+1);

    String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
	String fixPath = "true".equals(AppManager.getSysConfig("true"))? "min." : "";
	IUser user = WebUtil.getUserInfo(request);
	if (user == null) {
		response.sendRedirect("login.jsp");
		return;
	}
	String menuType = AppManager.getSysConfig("menuType");
	String useLevelOneMenu = AppManager.getSysConfig("useLevelOneMenu");

    List menuList = (List)request.getAttribute("menuList");
    List firstMenuChild = new ArrayList();
    Map firstMenu = new HashMap();
    Map secMenu = new HashMap();
    Map thMenu = new HashMap();
    if(menuList != null && menuList.size() > 0) {
      if(useLevelOneMenu.equals("true")) {
        firstMenu = (Map)menuList.get(0);
        if(menuList.size()>1){
          secMenu = (Map)menuList.get(1);
        }else{
          secMenu = (Map)menuList.get(0);
        }
        if(menuList.size()>2){
          thMenu = (Map)menuList.get(2);
        }else{
          thMenu = (Map)menuList.get(0);
        }
        firstMenuChild = (List)firstMenu.get("childList");
      }else{
        firstMenuChild = menuList;
      }
    }

    String curRoleName = user.getNowPosition() == null ? "无岗位" : user.getNowPosition().getPositionname();
	List<IPosition> positions = WebUtil.getUserPositions(request);
	IPosition mainPosition = WebUtil.getUserInfo(request).getNowPosition();
	long nowPosId = mainPosition.getPositionid();
%>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="X-UA-Compatible" content="IE=8; IE=9; IE=10" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ta3</title>

<link href="indexue/indexresource/css/menu.css" rel="stylesheet" type="text/css" />
<link href="indexue/indexresource/css/menuColor.css" rel="stylesheet" type="text/css" />
<link href="indexue/indexresource/css/index-theme.css" rel="stylesheet" type="text/css" />
<link href="indexue/indexresource/css/index.css" rel="stylesheet" type="text/css" />
<link href="indexue/indexresource/css/skin.css" rel="stylesheet" type="text/css" />
<link href="indexue/indexresource/css/base.css" rel="stylesheet" type="text/css" />
<link href="ta/resource/themes/base/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"/>
<link href="ta/resource/themes/base/helptip/helptip.css" rel="stylesheet" type="text/css" />
<%@include file="/ta/inc-theme.jsp"  %>

<script src="ta/resource/external/plugin/ta-core-all/jquery-1.11.0.min.js" type="text/javascript"></script>
<script src="indexue/indexresource/js/index.js" type="text/javascript"></script>
<script src="indexue/indexresource/js/tab.js" type="text/javascript"></script>
<script src="ta/resource/external/plugin/ta-base-all/cookie.js" type="text/javascript"></script>
<script src="ta/resource/external/plugin/ta-tools-all/hint-tip.js" type="text/javascript"></script>
<%
  if(menuType.equals("window")){
%>
<script src="indexue/indexresource/js/index-ui-menu.js" type="text/javascript"></script>
<script src="indexue/indexresource/js/menu-win.js" type="text/javascript"></script>
<%}else{ %>
<script src="indexue/indexresource/js/menu-panel.js" type="text/javascript"></script>
<%} %>
<%
  if(useLevelOneMenu.equals("true")){
%>
<script src="indexue/indexresource/js/menu-level1.js" type="text/javascript"></script>
<%}else{ %>
<script src="indexue/indexresource/js/menu-no-level1.js" type="text/javascript"></script>
<%} %>
<script src="ta/resource/external/plugin/ta-core-all/TaJsUtil.js" type="text/javascript"></script>
<script src="ta/resource/external/plugin/ta-dialog-all/zDialog.js" type="text/javascript"></script>
<script src="ta/resource/external/plugin/ta-dialog-all/zDrag.js" type="text/javascript"></script>
<script src="ta/resource/external/plugin/ta-dialog-all/layer.js" type="text/javascript"></script>
<script type="text/javascript">
	window.baseGlobvar = {};
	window.baseGlobvar.contextPath = "<%=request.getContextPath()%>";
	window.baseGlobvar.basePath = "<%=basePath%>";
	window.baseGlobvar.curPageUrl = "<%=curPageUrl%>";
	window.baseGlobvar.developMode = <%=AppManager.getSysConfig("developMode")%>;
	window.baseGlobvar.pageSize = <%=AppManager.getSysConfig("pageSize")%>;
	window.baseGlobvar.cols_360 = <%=AppManager.getSysConfig("cols_360")%>;
	window.baseGlobvar.columnsWidthsOverView = <%=AppManager.getSysConfig("columnsWidthsOverView")%>;
	window.baseGlobvar.indexStyle="<%=session.getAttribute(IConstants.INDEX_STYLE)%>";
	//lins jquery 1.8- 的兼容方案
	jQuery.browser = {};
    jQuery.browser.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
    jQuery.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
    jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
    jQuery.browser.msie11 = /rv:11.0/.test(navigator.userAgent.toLowerCase());
    jQuery.browser.msie = /msie/.test(navigator.userAgent.toLowerCase()) || jQuery.browser.msie11;
</script>
</head>
<body style="padding:0px;margin:0px;overflow: hidden;" id="body">
	<div>
		<div id="menuBox" class="menuBox">
		<%
		   if(!useLevelOneMenu.equals("true")){
		 %>
		    <div class="menu_top">
		        <div class="header-left"></div>
		        <div class="header-border"></div>
		    </div>
		 <%}else{ %>
			<div id="menuHeader" class="menuHeader">
				<div id="menuMain" title="<%=firstMenu.get("menuName") %>">
					<div class="menu_main_icon">
					  <img src="indexue/indexresource/images/menu/64x64/<%=firstMenu.get("img") %>.png" class="img_big"/>
					</div>
					<div class="menu_main_text"><%=firstMenu.get("menuName") %></div>
				</div>
				<div id="workPanel" _name="<%=firstMenu.get("menuName") %>" title="<%=firstMenu.get("menuName") %>" url="<%=firstMenu.get("url") %>" onclick="firstMenuclick(this)" childMenu='<%=JSonFactory.bean2json((List)firstMenu.get("childList")) %>'>
					<div id="workPanelIcon" class="m_icon">
					  <img src="indexue/indexresource/images/menu/64x64/<%=firstMenu.get("img") %>.png" width="100%" height="100%"/>
					</div>
				</div>
				<div id="menuSec" _name="<%=secMenu.get("menuName") %>" title="<%=secMenu.get("menuName") %>" url="<%=secMenu.get("url") %>" onclick="firstMenuclick(this)" childMenu='<%=JSonFactory.bean2json((List)secMenu.get("childList")) %>'>
					<div class="m_icon">
					  <img src="indexue/indexresource/images/menu/64x64/<%=secMenu.get("img") %>.png" width="100%" height="100%"/>
					</div>
				</div>
				<div id="menuTh" _name="<%=thMenu.get("menuName") %>" title="<%=thMenu.get("menuName") %>" url="<%=thMenu.get("url") %>" onclick="firstMenuclick(this)" childMenu='<%=JSonFactory.bean2json((List)thMenu.get("childList")) %>'>
					<div class="m_icon">
					  <img src="indexue/indexresource/images/menu/64x64/<%=thMenu.get("img") %>.png" width="100%" height="100%"/>
					</div>
				</div>
				<div class="menuLevel1"></div>
				<div class="menuLevel1_div" id="menuLevel1_div"></div>
			</div>
			<div id="menuHeader2" class="menuHeader2" title="<%=firstMenu.get("menuName") %>">
			  <div class="menuIcon_big">
			    <img src="indexue/indexresource/images/menu/64x64/<%=firstMenu.get("img") %>.png" class="img_smaller"/>
			  </div>
			</div>
			<div id="menuHeader3" class="menuHeader3" title="<%=firstMenu.get("menuName") %>">
			  <div class="menuIcon_bigger">
			    <img src="indexue/indexresource/images/menu/64x64/<%=firstMenu.get("img") %>.png" class="img_bigger"/>
			  </div>
			  <div class="menu_text"><%=firstMenu.get("menuName") %></div>
			</div>
			<%}%>
			<div id="scroll_div" class="scroll_div nui-scroll">
			  <ul style="padding: 0px !important;margin: 0px !important;position: relative;" class="menu_ul" id="menu_ul">
			  <%
			     for(int i = 0;i<firstMenuChild.size();i++){
			       Map secondMenu = (Map)firstMenuChild.get(i);
			       List child = (List)secondMenu.get("childList");
			       String childJson = JSonFactory.bean2json(child);
			       if(!"".equals(childJson) && null!=childJson){
			   %>
			      <li title="<%=secondMenu.get("menuName") %>" id="<%=secondMenu.get("menuId") %>" menuName="<%=secondMenu.get("menuName") %>" onclick="showMenu('<%=secondMenu.get("menuId") %>','<%=secondMenu.get("menuName") %>','<%=secondMenu.get("url") %>')" childMenu='<%=childJson %>'>
					<div class="menuIcon">
					  <img src="indexue/indexresource/images/menu/<%=secondMenu.get("img") %>.png"/>
					</div>
					<div class="menuName"><%=secondMenu.get("menuName") %></div>
					<div class="menuArrow_s"></div>
				  </li>
			    <%}else{%>
				    <li title="<%=secondMenu.get("menuName") %>" id="<%=secondMenu.get("menuId") %>" menuName="<%=secondMenu.get("menuName") %>" onclick="showMenu('<%=secondMenu.get("menuId") %>','<%=secondMenu.get("menuName") %>','<%=secondMenu.get("url") %>')" childMenu='<%=childJson %>'>
						<div class="menuIcon">
						  <img src="indexue/indexresource/images/menu/<%=secondMenu.get("img") %>.png"/>
						</div>
						<div class="menuName"><%=secondMenu.get("menuName") %></div>
						<div class="menuArrow_s menuArrow_h"></div>
					</li>
			     <%}%>
			    <%}%>
			  </ul>
			</div>
			<div class="menuLevel2 menuLevel2_right arrow_right" id="menuLevel2">
			  <div id="menuLevel2_small" class="small_box"></div>
			  <div id="menuLevel2_middle" class="middle_box"></div>
			</div>
		</div>

		<div id="iframeBox" style="float:left;position:relative;z-index:3;">
		   <div class="header">
		       <div class="logo"></div>
			   <ul class="user-header">
				   <li class="header-item" style="margin-top: 16px">
					   <a href="#" class="nav-link">
						   <i class="fa fa-tachometer fa-2x" onclick="fnDropDownMenu(this);" title="换肤"></i>
					   </a>
					   <div class="dropdown-menu hidden-caret">
						   <a id="green" class="dropdown-item" href="#" onclick="fnChangeSkin(this);">Green</a>
						   <a id="blue" class="dropdown-item" href="#" onclick="fnChangeSkin(this);">Blue</a>
						   <a id="pinkblue" class="dropdown-item" href="#" onclick="fnChangeSkin(this);">PinkBlue</a>
						   <a id="flat" class="dropdown-item" href="#" onclick="fnChangeSkin(this);">Flat</a>
					   </div>
				   </li>
				   <li class="header-item" style="margin-top: 16px">
					   <a href="#" class="nav-link">
						   <i class="fa fa-bookmark-o fa-2x" onclick="fnDropDownMenu(this);" title="快捷菜单"></i>
					   </a>
					   <%List<IMenu> list = (List<IMenu>) request.getAttribute("commonMenus");%>
					   <div id="commonMenus" class="dropdown-menu hidden-caret">
						   <%if(list != null && list.size() > 0 ){
							   for(IMenu menu : list) {%>
						          <a class="dropdown-item" href="#" onclick="showMenu('<%=menu.getMenuid()%>','<%=menu.getMenuname()%>','<%=menu.getUrl()%>')"><%=menu.getMenuname()%></a>
						       <%}%>
						   <div class="dropdown-divider"></div>
						   <%}%>
						   <a class="dropdown-item" href="#" onclick="fnSetCommonMenu();"><i class="fa fa-cog"></i> 添加常用菜单</a>
					   </div>
				   </li>
				   <li class="header-item" style="margin-top: 16px">
					   <a href="#" class="nav-link">
						   <i class="fa fa-envelope-o fa-2x" onclick="fnDropDownMenu(this);" title="邮件"></i>
					   </a>
					   <div class="dropdown-menu hidden-caret">
						   <a class="dropdown-item" href="#"> 发送邮件</a>
						   <a class="dropdown-item" href="#"> 查收邮件</a>
						   <div class="dropdown-divider"></div>
						   <a class="dropdown-item" href="#">Action</a>
					   </div>
				   </li>
				   <li class="header-item" style="margin-top: 16px">
					   <a href="#" class="nav-link">
						   <i class="fa fa-bell-o fa-2x" onclick="fnDropDownMenu(this);" title="通知"></i>
						   <span class="notification">3</span>
					   </a>
				   </li>
				   <li class="header-item">
					   <a href="#" class="nav-link profile-pic" onclick="fnDropDownMenu(this);">
						   <img src="indexue/indexresource/images/profile.jpg" alt="user-img" width="36px" class="img-circle"/>
						   <span><%= user.getName() %></span><span class="fa fa-caret-down"/>
					   </a>
					   <ul class="dropdown-menu hidden-caret dropdown-user">
						   <li>
							   <div class="user-box">
								   <div class="u-img"><img src="indexue/indexresource/images/profile.jpg" alt="user"></div>
								   <div class="u-text">
									   <h4><%= user.getName() %></h4>
									   <p class="text-muted"><%= user.getEmail() != null ? user.getEmail() : "" %></p>
									   <a href="#" class="btn btn-rounded" onclick="fnViewUserInfo();">查看资料</a>
								   </div>
							   </div>
						   </li>
						   <%if (positions.size() > 0){%>
							   <div class="dropdown-divider"></div>
							   <%for (IPosition p : positions) {%>
									<a class="dropdown-item" href="#" id="p_<%=p.getPositionid()%>" _id="<%=p.getPositiontype()%>" _name="<%=p.getPositionname() %>" title="组织路径：<%=p.getOrgnamepath()%>" onclick="fnSelectPosition(this);">
										<i class="fa fa-user-circle-o"></i>
										<%=p.getPositionname() %><%if("1".equals(p.getPositiontype())){%>(公有)<%}else if("2".equals(p.getPositiontype())){%>(个人)<%}else if("3".equals(p.getPositiontype())){%>(委派)<%} %>
						   			    <%if(nowPosId == p.getPositionid()){%>
						                   <i class="fa fa-circle"></i>
						   			    <%}%>
									</a>
							   <%}%>
						   <%}%>
						   <div class="dropdown-divider"></div>
						   <a class="dropdown-item" href="#" onclick="openPwChangeWindow();"><i class="fa fa-key"></i> 修改密码</a>
						   <a class="dropdown-item" href="#" onclick="fnUpdateUserInfo();"><i class="fa fa-id-card"></i> 用户设置</a>
						   <div class="dropdown-divider"></div>
						   <a class="dropdown-item" href="#" onclick="reDirect()"><i class="fa fa-question-circle-o"></i> 帮助提示</a>
						   <div class="dropdown-divider"></div>
						   <a class="dropdown-item" href="<%=request.getContextPath()%>/formLogoutAction.do"><i class="fa fa-sign-out"></i> 退出</a>
					   </ul>
				   </li>
			   </ul>


		       <div class="header_tabs">
		         <div class="unselect header-nav" unselectable="on">
					<div style="height: 40px; float: left;">
						<div class="index_menuTab index_menuTab_act" id="tab_01"
							title="我的首页" onclick="fnIndexBtnClick()" _id="01">
							<div class="index_tab_center" style="padding: 0 12px;">
								工作台
							</div>
						</div>
					</div>
					<ul id="indexTabs">
						<li onclick="IndexTab.showTabList(this);" class="tab-list-icon"
							id="tabListIcon">
							<div></div>
						</li>
					</ul>
				</div>
				<div class="header-border"></div>
		       </div>
		   </div>
		   <ul class="tab-list" id="tabList">
			<li class="close-all"
				onclick="IndexTab.closeAllTab();fnIndexBtnClick();">
				<span>关闭所有</span>
			</li>
			<li title="工作台" onclick="fnIndexBtnClick();$('#tabList').fadeOut();"
				id="l_01">
				<span>工作台</span>
			</li>
		    </ul>
		   <div class="iframe_main" id="mainFrameBox">
		     <iframe name="tab_b_01" frameborder=no src="workspace/portal/welcome.jsp" id="tab_b_01" style="width: 100%;height:100%"></iframe>
		   </div>
		</div>
	</div>
	<footer class="skin">
	  <div class="skin-item skin-left-bottom" id="skin-left-bottom"></div>
	  <div class="skin-item skin-top-right"></div>
	  <div class="skin-item skin-top-left" id="skin-top-left"></div>
	  <div class="skin-item skin-top" id="skin-top"></div>
	</footer>
	<%
	   if(useLevelOneMenu.equals("true")){
	%>
	<div class="menu_level1_div" style="display:none;" id="menu_level1_div">
	  <div style="width:12px;height:20px;background:url(indexue/indexresource/images/icon_s_arrow.png) no-repeat;position:absolute;left:-12px;top:20px;z-index:3;"></div>
	  <%
	    for(int i = 0;i<menuList.size();i++){
	      Map map = (Map)menuList.get(i);
	      List child = (List)map.get("childList");
		  String childJson = JSonFactory.bean2json(child);
	  %>
	  <div class="menu_level1_container" title="<%=map.get("menuName") %>">
	    <div id="<%=map.get("menuId") %>" _name="<%=map.get("menuName") %>" url="<%=map.get("url") %>" class="menu_level1_box" childMenu='<%=childJson%>' onclick="firstMenuclick(this)">
	      <div class="menu_level1_icon m_icon">
	        <img src="indexue/indexresource/images/menu/64x64/<%=map.get("img") %>.png" height="48px" width="48px"/>
	      </div>
	    </div>
	    <div class='menu_level1_name'><%=map.get("menuName") %></div>
	  </div>
	  <% }%>
	  <div class="menu_close_icon" id="menu_close"></div>
	</div>
	<% }%>
	<div class="panel panelnomargin window" style="display: none; width: 400px; height: 166px; z-index: 9005; cursor: default; position: absolute;" id="passC">
		<div class="panel-header panel-header-noborder window-header" style="width: 400px;">
			<div class="panel-title" style="cursor: move;">修改密码</div>
			<div class="panel-tool"><div class="panel-tool-close" onclick="fnClosePass()">X</div></div>
		</div>
		<div id="passInfo" class="grid   panel-body panel-body-noborder window-body" layout="column" cols="1" style="padding: 0px; width: 400px; height: 140px;" title="">
			<div class="fielddiv fielddiv_163" style="margin:1px 5px !important;">
				<label for="oldPass" class="fieldLabel">
				<span style="color:red">*</span>
				原密码
				</label>
				<div class="fielddiv2" >
					<input type="password" id="oldPass" name="dto['oldPass']" required="true" class="textinput validatebox-text validatebox-invalid" validtype="length[0,15]" maxlength="15"/>
				</div>
			</div>
			<div class="fielddiv fielddiv_163" style="margin:5px;">
				<label for="newPass" class="fieldLabel">
				<span style="color:red">*</span>
				登录口令
				</label>
				<div class="fielddiv2">
					<input type="password" id="newPass" name="dto['newPass']" required="true" class="textinput validatebox-text" validtype="length[0,15]" maxlength="15"/>
				</div>
			</div>
			<div class="fielddiv fielddiv_163" style="margin:5px;">
				<label for="rpassword" class="fieldLabel">
				<span style="color:red">*</span>
				确认口令
				</label>
				<div class="fielddiv2">
					<input type="password" id="rpassword" name="dto['rpassword']" required="true" class="textinput validatebox-text" validtype="compare(this.value, ['=', 'newPass'])" maxlength="15"/>
				</div>
			</div>
			<div id="tabuttonlayout_div" class="button-panel center" style="border-top:1px solid #c6c6c6;background-color:#FAFAFA" >
				<button id="saveUserBtn" class="sexybutton_163" type="button" onclick="fnSavePass();" value="">
					<span class="button_span isok">
					保存</span>
				</button>
				<button id="closeWinBtn" class="sexybutton_163"  type="button" onclick="fnClosePass()" value="">
					<span class="button_span ">
					关闭</span>
				</button>
			</div>
			<div style="clear:both"></div>
		</div>
	</div>
</body>
<script type="text/javascript">
$(document).ready(function(){
    fnInitPage();
    $(window).resize(fnInitPage);
    bindEvent();
    if("<%=useLevelOneMenu%>" != "true") {
         var t = $("#iframeBox").offset().left;
         $("#iframeBox .logo").css("left",30-t);
         $("#skin-top-left").hide();
    } else {
         initFirstMenu();
    }
    fnPageGuide();
});
function fnPageGuide(){
	var data = [{id:$("#menuLevel1_div"), message:"点击这里，您将能够选择一级菜单!"},
		{id:$("#menu_ul"), message:"与一级菜单相关联的二级菜单!"},
		{id:$("#green"), message:"这里可以进行换肤操作!"},
		{id:$("#user_func_menu"), message:"这里能修改当前登录账户的密码！"},
		{id:$("#commonMenu"), message:"这里能够设置迅速访问的快捷菜单!"},
		{id:$("#reHelpTip_div"), message:"这里可以重新进行引导"}
	];
	$("#iframeBox").hintTip({replay: false, show: true, cookname: currentBuinessId, data: data});
}

function fnDropDownMenu(obj) {
    $(".hidden-caret").hide();
	var dropdownmenu = ($(obj).hasClass('profile-pic')? $(obj) : $(obj).parent()).siblings('.dropdown-menu');
	if (dropdownmenu.hasClass('dropdown-user')){
        dropdownmenu.css("left", $(obj).width() / 2 - dropdownmenu.width() / 2 - 55);
    } else {
        dropdownmenu.css("left", $(obj).width() / 2 - dropdownmenu.width() / 2);
    }
    if (dropdownmenu.is(':hidden')){
		dropdownmenu.show();
	} else {
		dropdownmenu.hide();
	}
}

function reDirect(){
	//Base.msgTopTip("<div class='msgTopTip'>重新导航成功</div>");
	if(window.frames["tab_b_"+currentBuinessId].window.$(".hint-tips").length>0){
		return;
	}
	$.clearCookieHintArray(currentBuinessId);
	if(currentBuinessId != "01"){
		var fun = window.frames["tab_b_"+currentBuinessId].window.fnPageGuide;
		if(fun && typeof(fun) == "function") {
				window.frames["tab_b_"+currentBuinessId].window.fnPageGuide(currentBuinessId);
		} else {
			alert("该页面没有引导");
		}
        //window.frames["tab_b_"+currentBuinessId].window.fnPageGuide();
        //window.fnPageGuide();
	} else {
		fnPageGuide();
	}
}
function fnSetCommonMenu() {
	IndexTab.actTab("01", true);
	window.frames["tab_b_01"].window.fnAddCommonMenu();
	$("#used_menu").fadeOut();
}

function fnSelectPosition(o) {
	var $o = $(o);
	if (confirm("确认切换岗位吗？")) {
		var positionid = $o.attr("id").substring(2);
		$.ajax({
			"data" : "__positionId=" + positionid + "&positionid=" + positionid,
			"url" : "commonAction.do",
			"success" : function(data) {
				$("#positionChangeName").html($(o).attr("_name"));
				$("#selectPosition").hide();
			},
			"type" : "POST",
			"dataType" : "json"
		});
		//更改菜单样式
		$o.parent("ul").find("span").removeClass('pos-now');
		$o.find("span").addClass('pos-now');
	} else {
		$("#selectPosition").hide();
	}
}

//初始化一级菜单颜色
function initFirstMenu() {
	var arr = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 ];
	arr = mess(arr);
	var menuLen = <%=menuList.size()%>;
	var col;
	if (menuLen < 4) {
		col = menuLen;
	} else {
		if (menuLen % 2 == 0) {
			col = menuLen / 2;
		} else {
			col = menuLen / 2 + 0.5;
		}
	}
	var w = 60 * col + 8 * col * 2 + 10 + 2;
	$("#menu_level1_div").css("width", w);
	$("#menu_level1_div .menu_level1_box").each(function(i) {
		$(this).addClass("color_" + arr[i]);
	});
}
var s;
//给左边菜单栏绑定鼠标移入事件，移入后显示下级菜单
function bindEvent() {
	$("#menu_ul li").mouseenter(function() {
		var o = this;
		s = setTimeout(function() {
			var id = $(o).attr("id");
			$(o).siblings().each(function() {
				if ($(this).attr("childMenu")) {
					hideChildMenu(this);
				}
			});
			if ($(o).attr("childMenu") && $(o).attr("childMenu") != "null") {
				if ($("#menu_container_" + id).size() > 0) {
					showChildMenu(o);
				} else {
					initChildMenu(o);
				}
			}
		}, 200);
	}).mouseleave(function(e) {
		clearTimeout(s);
		s = null;
	});
	//当鼠标点击非弹出菜单处，菜单消失
	$(document).mousedown(function(e) {
		var ev = e ? e : $.event.fix(window.event || e);
		var target = ev.target || ev.srcElement;
		if (!$(target).hasClass("menu_container_3")) {
			if ($(target).parents(".menu_container_3").size() == 0) {
				$(".menu_container_3").hide();
			}
		}
		if (!$(target).hasClass("menu_panel")) {
			if ($(target).parents(".menu_panel").size() == 0) {
				$(".menu_panel").hide();
			}
		}
		if (!$(target).hasClass("used_menu") && !$(target).hasClass("link_div")) {
			if ($(target).parents(".used_menu").size() == 0) {
				$("#used_menu").fadeOut();
			}
		}
		if (!$(target).hasClass("func_menu") && !$(target).hasClass("link_div")) {
			if ($(target).parents(".func_menu").size() == 0) {
				$("#func_menu").fadeOut();
			}
		}
		//右上角菜单
		if ($(target).parents(".header-item").size() == 0) {
			$(".hidden-caret").each(function () {
				$(this).hide();
			});
		}
	});
}
//隐藏弹出菜单方法
function hideChildMenu(o) {
	var $menu = $("#menu_container_" + $(o).attr("id"));
	$menu.hide();
}
//左边菜单展开收起事件
$(function() {
	$("#menuLevel2").click(function(e) {
		menuToBig(this);
		e.stopImmediatePropagation();
	}).hover(function(e) {
		if ($(this).hasClass("arrow_right")) {
			$(this).removeClass("arrow_right").addClass("arrow_right_over");
			$("#menuLevel2_middle").show();
			$("#menuLevel2_small").show();
		} else {
			$(this).removeClass("arrow_left").addClass("arrow_left_over");
		}
	}, function() {
		if ($(this).hasClass("arrow_right_over")) {
			$(this).removeClass("arrow_right_over").addClass("arrow_right");
			$("#menuLevel2_middle").hide();
			$("#menuLevel2_small").hide();
		} else {
			$(this).removeClass("arrow_left_over").addClass("arrow_left");
		}
	});

	$("#menuLevel2_middle").click(function(e) {
		menuToMiddle();
		e.stopImmediatePropagation();
	});

	$("#menuLevel2_small").click(function(e) {
		menuToSmall();
		e.stopImmediatePropagation();
	});
	//打开常用菜单面板事件
	$("#commonMenu").click(function(e) {
		var box = $("#used_menu");
		if (box.css("display") == "none") {
			box.fadeIn();
		} else {
			box.fadeOut();
		}
		$("#func_menu").fadeOut();
	});
	//打开用户功能面板事件
	$("#user_func_menu").click(function(e) {
		var box = $("#func_menu");
		if (box.css("display") == "none") {
			box.fadeIn();
		} else {
			box.fadeOut();
		}
		$("#used_menu").fadeOut();
	});
});

window.onload = function() {
	/********************绑定接收数据事件**********************/
	if (window.attachEvent) {
		window.attachEvent("onmessage", receiveMsg);
	} else {
		window.addEventListener("message", receiveMsg, true);
	}
};
//消息事件回调函数
function receiveMsg(e) {
	e = e || window.event;
	var o = eval("(" + e.data + ")");
	if (o.type == "function") {
		var fun = eval(o.msg);
		var args = o.args || [];
		if (typeof args == "string") {
			args = args.split(";");
		}
		if (typeof fun == "function")
			fun.apply(fun, args);
	}
}
//获取常用菜单
function getCommonMenu() {
  $.ajax({"url" : "<%=basePath%>indexAction!getCommonMenu.do",
	  "type" : "POST",
	  "dataType" : "json",
	  "success" : function(data) {
			  var menus = eval("(" + data.fieldData.commonMenu + ")");
			  var $div = $("#commonMenus");
			  $div.empty();
			  var a = [];
			  if (menus.length > 0) {
				  for (var m of menus){
					  a.push("<a class='dropdown-item' href='#' onclick=\"showMenu('" + m.menuid + "','" + m.menuname + "','" + m.url + "');\">" + m.menuname + "</a>");
				  }
				  a.push("<div class='dropdown-divider'></div>");
			  }
			  $div.append(a.join("\n"));
			  $div.append("<a class='dropdown-item' href='#' onclick='fnSetCommonMenu();'><i class='fa fa-cog'></i> 添加常用菜单</a>")
	  }
  });
}
//查看资料
function fnViewUserInfo() {
	layer.open({
		type: 2,
		title: ["用户基本信息","background-color:#efe5d;font-size:12px;"],
		fix: true,
        area: ["600px", "450px"],
		content: "<%=basePath%>org/userMgAction!toUserInfo.do",
		cancel: function(index) {
			layer.close(index);
		}
	});
}
//编辑用户信息
function fnUpdateUserInfo() {
	layer.open({
		type: 2,
		title: ["用户基本信息","background-color:#efe5d;font-size:12px;"],
		fix: true,
		area: ["600px", "450px"],
		content: "<%=basePath%>org/userMgAction!toUserInfo.do",
		cancel: function(index) {
			layer.close(index);
		}
	});
}
//保存密码
function fnSavePass() {
	var oldPass = $("#oldPass").val();
	var newPass = $("#newPass").val();
	var rpassword = $("#rpassword").val();
	if (oldPass == "") {
		alert("原密码不能为空！");
		return;
	}
	if (newPass == "") {
		alert("新密码不能为空！");
		return;
	}
	if (rpassword == "") {
		alert("密码确认不能为空！");
		return;
	}
	if (newPass == rpassword) {
		var d = "dto['oldPass']=" + oldPass + "&dto['newPass']=" + newPass + "&indexChangePass=1";
		$.ajax({"url" : "<%=basePath%>system/userPassAction!changePasswordWidthCurrent.do",
			"data":d,
			"success": function(data) {
				 if (data.msgBox) {
					alert(data.msgBox.msg);
					if(data.msgBox.msgType !="error"){
						fnClosePass();
					}
				 } else {
					fnClosePass();
				 }
			},
			"type":"POST",
			"dataType":"json"
        });
  } else {
    alert("两次输入的密码不一致，请检查！");
  }
}
function _fnCloseMenu(){}
</script>
<script type="text/javascript">
setInterval(function(){
	$.ajax({type : "post",
			url : "<%=basePath%>log/RequestSessionLogAction!request.do",
			dataType : "json",
			success:function(data) {
				if(data.fieldData.noSession){
					$("#logout a").trigger("click");
				}
			}
		});
	},60000
);
</script>
</html>
<%@ include file="/ta/incfooter.jsp"%>