/**
 * 首页panel风格菜单
 */
function initChildMenu(o){
	var $menu_panel = $("<div id=menu_container_"+$(o).attr("id")+" class='menu_panel menu_close'>").append("<div class=menu_panel_arrow>").appendTo("body");
	var submenu = [];
	var childList = eval("("+$(o).attr("childMenu")+")");
	for(var i = 0;i<childList.length;i++){
		var m = childList[i];
		submenu.push("<div class=menu_panel_body>");
		submenu.push("<div class=menu_panel_header onclick=\"showMenu('"+m.menuId+"','"+m.menuName+"','"+m.url+"')\"><div class=menu_panel_name>"+m.menuName+"</div></div>");
		if(i == childList.length-1){
			submenu.push("<div class='item_box_color'>");
		}else{
			submenu.push("<div class='item_box_color item_box_border'>");
		}
		if(m.childList){
			var child = m.childList;
			for(var j = 0;j<child.length;j++){
				var c = child[j];
				submenu.push("<div class=menu_panel_item onclick=\"showMenu('"+c.menuId+"','"+c.menuName+"','"+c.url+"')\"><div class=menu_panel_name>"+c.menuName+"</div></div>");
			}
		}
		submenu.push("</div></div>");
	}
	$menu_panel.append(submenu.join("\n"));
	$(".menu_close").bind("mouseleave",function(){
		$(this).hide();
	});
	setPanelHeight(o);
	showChildMenu(o);
}

function showChildMenu(o){
	var $menu = $("#menu_container_"+$(o).attr("id"));
	var w = $(o).width();
	var menu_h = $menu.height();
	$menu.css("left",w);
	var offset_top = $(o).offset().top;
	var window_h = $(window).outerHeight();
	if(window_h-offset_top<menu_h){
	  $menu.css("top",window_h-menu_h);
	}else{
	  $menu.css("top",offset_top);
	}
	var t = $menu.css("top");
	$menu.find(".menu_panel_arrow").css("top",offset_top-parseInt(t)+10);
	$menu.show();
}

function setPanelHeight(o){
	var $menu = $("#menu_container_"+$(o).attr("id"));
	var menu_h = $menu.height();
	$menu.find(".item_box_color").css("height",menu_h-30);
}