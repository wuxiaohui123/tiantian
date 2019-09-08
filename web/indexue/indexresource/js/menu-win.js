/**
 * 首页windows风格菜单
 */
function initChildMenu(o){
  var container = $("<div id=menu_container_"+$(o).attr("id")+" class='menu_container_3 menu_close'>").append("<div class=menu_arrow>").appendTo("body");
  var menuBody = $("<div class=menu_body_3>").append("<div class=menu_3_header><div>"+$(o).attr("menuName")+"</div></div>").appendTo(container);
  var submenu = [];
  submenu.push("<ul>");
  var childList = eval("("+$(o).attr("childMenu")+")");
  for(var i = 0;i<childList.length;i++){
    var m = childList[i];
    if(m.childList){
      submenu.push("<li id='"+m.menuId+"' title='"+m.menuName+"' class='menu_item' childMenu='"+Ta.util.obj2string(m.childList)+"' onclick=\"showMenu('"+m.menuId+"','"+m.menuName+"','"+m.url+"')\">");
      submenu.push("<div class='menu_item_name'>"+m.menuName+"</div><div class='child_arrow'></div></li>");
    }else{
      submenu.push("<li class='menu_item' title='"+m.menuName+"' onclick=\"showMenu('"+m.menuId+"','"+m.menuName+"','"+m.url+"')\">");
      submenu.push("<div class='menu_item_name'>"+m.menuName+"</div></li>");
    }
  }
  submenu.push("</ul>");
  menuBody.append(submenu.join("\n"));
  showChildMenu(o);
  //初始化4级菜单
  Index2015.Menu(container);
  $(".menu_container_3").mouseover(function(){
    clearTimeout(s);
    s = null;
  });
}

function showChildMenu(o){
  var $menu = $("#menu_container_"+$(o).attr("id"));
  var w = $(o).width();
  $menu.css("left",w-10);
  var offset_top = $(o).offset().top;
  var window_h = $(window).outerHeight();
  var menu_h = $menu.height();
  if(window_h-offset_top<menu_h){
    $menu.css("top",window_h-menu_h);
  }else{
    $menu.css("top",offset_top-29);
  }
  var t = $menu.css("top");
  $menu.find(".menu_arrow").css("top",offset_top-parseInt(t)+10);
  $menu.show();
}