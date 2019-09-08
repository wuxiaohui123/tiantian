<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.yinhai.ta3.system.sysapp.domain.Menu"%>
<%@page import="com.yinhai.sysframework.util.WebUtil"%>
<%@page import="com.yinhai.sysframework.security.IPermissionService"%>
<%@page import="com.yinhai.sysframework.service.ServiceLocator"%>
<%@page import="com.yinhai.sysframework.menu.IMenu"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>常用菜单</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body>
		<div class="index_commonmenu_div">
			<ul>
				<%
				List<IMenu> list = (List<IMenu>)request.getAttribute("commonMenus");
				if(list != null){
					for(int i = 0 ; i < list.size(); i++){
						Menu menu = (Menu)list.get(i); 
				%>
					<li class="commonmenu_item commonmenu" id="commonmenu_<%=menu.getMenuid()%>" title="<%=menu.getMenuname()%>" onclick="fnClickCommonMenu('<%=menu.getMenuid()%>','<%=menu.getUrl()%>','<%=menu.getMenuname()%>','<%=menu.getIsdismultipos()%>')">
					<div class="commonmenu_icon menu_<%=menu.getIconSkin()%>"></div>
					<span class="commonmenu_name"><%=menu.getMenuname()%></span>
					<div class="commonmenu_remove" title="移除常用菜单"></div>
					</li>
				<%
					}
				}
				%>
				<li class="commonmenu_item_add commonmenu"  onclick="fnAddCommonMenu()">
					<div class="commonmenu_icon" title="添加常用菜单"></div>
				</li>
			</ul>
		</div>

	</body>
</html>
<script type="text/javascript">
/** 
 * 功能：解决FireFox无法获取window.event全局对象
 */
function GetEvent(caller){ 
	if(document.all) 
		return window.event; //For IE. 
	if(caller == null || typeof(caller) != "function") 
		return null; 
	while(caller.caller != null){ 
		caller = caller.caller; 
	} 
	return caller.arguments[0]; 
} 
	$(document).ready(function () {
		$(".commonmenu_remove").bind("click",function(e){
			e.stopPropagation();
			var $this = $(this);
			var $li = $this.parent();
			var menuId = "",id=$li.attr("id");
			if(id){
				menuId = id.substring(11);
				$.ajax({
					"data":"dto['menuId']="+menuId,
					"url":"<%=basePath%>sysapp/commonMenuAction!deleteCommonMenu.do",
					"success":function(data){
						$li.remove();
					},
					"type":"POST",
					"dataType":"json"
				});
			}
		});
		$(".commonmenu_item").bind("mouseenter",function(e){
			$("div.commonmenu_remove",$(this)).show();
		});
		$(".commonmenu_item").bind("mouseleave",function(e){
			$("div.commonmenu_remove",$(this)).hide();
		});
	});
	function fnAddCommonMenu(){
		parent.Base.openWindow("addWin", "添加常用菜单", "<%=basePath%>sysapp/commonMenuAction!toAddCommonMenus.do",{},400,450,null,function(){
			location.href= location.href;
		},true);
	}
	function fnClickCommonMenu(id,url,text,isdismultipos){
		var event = GetEvent(fnClickCommonMenu);
    	event.cancelBubble = true;
		event.returnValue = false;
		if (event.stopPropagation) {
			event.stopPropagation();
			event.preventDefault();
		}
    	//判断需要确定岗位才能操作的菜单
 		<%if(((IPermissionService)ServiceLocator.getService(IPermissionService.SERVICEKEY)).isAdministrator(WebUtil.getUserInfo(request).getNowPosition())){%>
 		top.IndexTab.addTab(id, text, url);
 		<%}else{%>
 		if(isdismultipos=="0" && !queryPositionByMenu(event, url,id,text))return ;
 		top.IndexTab.addTab(id, text, url);
 		<%}%>
	}
	function queryPositionByMenu(event, url,menuid,title){
	 	Base.getJson("<%=request.getContextPath()%>/indexAction!getMenuPosition.do",{"menuid":menuid},
	 	function(data){
	 		var html = "";
    	 	if(data.length==1){//如果只有一个岗位有该菜单权限就自动切换到该岗位然后直接打开菜单
    	 		changenowPosition(data[0].positionid,menuid,title,url);
    	 	}else{
    	 		for(var i=0;i<data.length;i++){
    	 			html += "<a href=\"#\" onclick=\"changenowPosition('"+data[i].positionid+"','"+menuid+"','"+title+"','"+url+"')\">"+data[i].positionname+"</a><br>";
    	 		}
    	 		var $tIdSpan = $("#commonmenu_"+menuid);
    	 		var $cp = $("#changePosition");
    	 		if($cp && $cp.length == 1){
    	 			$("#changePoUl").html(html);
    	 			$cp.show().css('top',$tIdSpan.position().top).css("left",$tIdSpan.position().left+$tIdSpan.width());
    	 		}else{
	    	 		var style = "background:#e1e1e1;display:block;position:absolute;left:"+($tIdSpan.position().left+$tIdSpan.width())+"px;top:"+$tIdSpan.position().top+"px;";
	    	 		$("body").append("<div  id='changePosition' style='"+style+"' class='menu-list'><div style='font-weight:bolder;height:20px;line-height:20px;'>选择使用的岗位：</div><ul id='changePoUl'>"+html+"</ul></div>");
    	 		}
				/* $("#commonmenu_"+menuid).CreateBubblePopup({
					selectable: true,
					position : 'right',
					align	 : 'left',
					innerHtml: '选择使用的岗位：<br>'+html,
					themeName: 	'all-orange',
					themePath: 	'ta/resource/themes/base/bubblepop'
				}); */
			}
	 	});
	 	return false;
	 }
	function changenowPosition(positionid,tabid,title,url){
	    
		Base.getJson("<%=request.getContextPath()%>/indexAction!changeNowPosition.do",{"positionid":positionid},function(data){
		    if(data.success){
		    	top.IndexTab.addTab(tabid, title, url);
			}
		});
	 }
</script>
<%@ include file="/ta/incfooter.jsp"%>