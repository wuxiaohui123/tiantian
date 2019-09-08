(function($) {
	$.extend(true, window, {
        Index2015: {
            Menu: Menu
        }
    });
	
	function Menu(container){
		
		var timer;
		
		function init($container){
			var $body = $container.children(".menu_body_3");
			bindMenuEvent($container);
			$body.find(".menu_item").each(function(){
				var $item = $(this);
				bindMenuItemEvent($item);
				if($item.attr("childmenu")){
					var menuList = eval("("+$item.attr("childmenu")+")");
					creatMenu($item,menuList);
				}
			});
		}
		
		function creatMenu(target,data){
			var id = "menu_container_3_"+target.attr("id");
			var c = $("<div class='menu_container_3 menu_close'>").attr("id",id);
			var b = $("<div class=menu_body_3>").appendTo(c);
			var submenu = [];
			submenu.push("<ul>");
			for(var i = 0; i<data.length; i++){
				var m = data[i];
				if(m.childList){
				  submenu.push("<li id='"+m.menuId+"' title='"+m.menuName+"' class='menu_item' childMenu='"+Ta.util.obj2string(m.childList)+"' onclick=\"showMenu('"+m.menuId+"','"+m.menuName+"','"+m.url+"')\">");
				  submenu.push("<div class='menu_item_name'>"+m.menuName+"</div><div class='child_arrow'></div></li>");
				}else{
				  submenu.push("<li class='menu_item' title='"+m.menuName+"' onclick=\"showMenu('"+m.menuId+"','"+m.menuName+"','"+m.url+"')\">");
				  submenu.push("<div class='menu_item_name'>"+m.menuName+"</div></li>");
				}
			}
			submenu.push("</ul>");
			b.append(submenu.join("\n"));
			c.appendTo("body");
			init(c);
		}
		
		function bindMenuEvent(menu){
			menu.bind("mouseenter", function(){
				if (timer){
					clearTimeout(timer);
					timer = null;
				}
			}).bind("mouseleave.menu", function(){
				timer = setTimeout(function(){
					$("body>.menu_container_3").hide();
				}, 200);
			});
		}
		
		function bindMenuItemEvent(item){
			item.bind("mouseenter",function(e){
				item.siblings().each(function(){
					if ($(this).children(".child_arrow").size()>0){
						_hideMenu(this);
					}
					$(this).removeClass('menu_active');
				});
				//showMenu
				$(this).addClass('menu_active');
				if ($(this).children(".child_arrow").size()>0){
					_showMenu(this);
				}
			});
		}
		
		function _showMenu(menuItem){
			var $parent = $(menuItem);
			var $currentMenu = $("#menu_container_3_"+$parent.attr("id"));
			var parent_top = $parent.offset().top;
			var parent_left = $parent.offset().left;
			var window_height = $(window).outerHeight();
			var window_width = $(window).outerWidth();
			var menu_height = $currentMenu.height();
			if(window_height-parent_top<menu_height){
				$currentMenu.css("top",window_height-menu_height);
			}else{
				$currentMenu.css("top",parent_top);
			}
			if(window_width-parent_left-170<170){
				$currentMenu.css("left",parent_left-180);
			}else{
				$currentMenu.css("left",parent_left+160);
			}
			$currentMenu.show();
		}
		
		function _hideMenu(menuItem){
			var $parent = $(menuItem);
			var $currentMenu = $("#menu_container_3_"+$parent.attr("id"));
			$currentMenu.find(".menu_item").each(function(){
				if($(this).children(".child_arrow").size()>0){
					_hideMenu(this);
				}
			});
			$currentMenu.hide();
		}
		
		init(container);
	}
}(jQuery));