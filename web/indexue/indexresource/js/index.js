//左上角一级菜单事件
$(function(){
  $("#menuLevel1_div").click(function(){
    if($("#menu_level1_div").css("display")=="none"){
      $("#menu_level1_div").css("left",210).show();
    }else{
      $("#menu_level1_div").hide();
    }
  });
  
  $("#menu_close").click(function(e){
    $("#menu_level1_div").hide();
  });
  
  $("#menuHeader2").click(function(e){
    $("#menu_level1_div").css("left",60).show();
  });
  
  $("#menuHeader3").click(function(e){
	$("#menu_level1_div").css("left",105).show();
  });
  
  $("#scroll_div").hover(function(){
	  $(this).addClass("scroll_over");
  },function(){
	  $(this).removeClass("scroll_over");
  });
  
  $("#logout").hover(function(){
	  $(this).find("a").css("color","#fff");
  },function(){
	  $(this).find("a").css("color","#000");
  });
  
  $(document).mousedown(function(e){
    var ev = e ? e : $.event.fix(window.event || e);
    var target = ev.target || ev.srcElement;
    if(!$(target).hasClass("menu_level1_div") && !$(target).hasClass("menuLevel1_div") && !$(target).parent().hasClass("menuHeader2") && !$(target).hasClass("menuHeader2")){
      if($(target).parents("#menu_level1_div").size()==0){
        $("#menu_level1_div").hide();
	  }
    }
  });
});
//首页初始化方法，计算关键容器宽高
function fnInitPage(){
  var h = parseInt($(window).height(),10);
  var w = parseInt($(window).width(),10)-parseInt($("#menuBox").width(),10)-1;
  var t = (h-60)/2;
  $("#menuBox").css("height",h);
  $("#iframeBox").css("height",h);
  $("#iframeBox").css("width",w);
  $("#mainFrameBox").css("height",h-90);
  $("#mainFrameBox").css("width",w);
  $("#menuLevel2").css("top",t);
  IndexTab.setPreantWidth();
}
//随机打乱数组里的元素，用于左上角一级菜单颜色随机分配
function mess(arr){ 
  var _floor = Math.floor, _random = Math.random, 
  len = arr.length, i, j, arri, 
  n = _floor(len/2)+1; 
  while( n-- ){ 
    i = _floor(_random()*len); 
    j = _floor(_random()*len); 
    if( i!==j ){ 
      arri = arr[i]; 
      arr[i] = arr[j]; 
      arr[j] = arri; 
    } 
  } 
  i = _floor(_random()*len); 
  arr.push.apply(arr, arr.splice(0,i)); 
  return arr; 
} 
// 初始化一级菜单
function initMainMenuBody(){
  var len = $("#menuMain .menu_main_text").text().length;
  if(len>5){
    $("#menuMain .img_big").addClass("img_smaller");
    $("#menuMain .menu_main_text").addClass("text_smaller");
    $("#menuHeader3 .menu_text").addClass("text_smaller");
    $("#menuHeader3 img").addClass("img_big").removeClass("img_bigger");
  }else{
    $("#menuMain .img_big").removeClass("img_smaller");
    $("#menuMain .menu_main_text").removeClass("text_smaller");
    $("#menuHeader3 .menu_text").removeClass("text_smaller");
    $("#menuHeader3 img").addClass("img_bigger").removeClass("img_big");
  }
}
//一级菜单点击事件，刷新左边二级菜单 
function firstMenuclick(o){
  $("#menu_level1_div").hide();
  var img = $(o).find(".m_icon>img").attr("src");
  var menuName = $(o).parent().find(".menu_level1_name").text();
  if(!menuName){
	  menuName = $(o).attr("_name");
  }
  $("#menuMain .menu_main_icon>img").attr("src",img);
  $("#menuMain .menu_main_text").text(menuName);
  
  $("#menuHeader2 img").attr("src",img);
  
  $("#menuHeader3 img").attr("src",img);
  $("#menuHeader3 .menu_text").text(menuName);
  initMainMenuBody();
  var childMenu = $(o).attr("childMenu");
  if(childMenu){
    var secMenu = [];
    var childList = eval("("+$(o).attr("childMenu")+")");
    for(var i = 0;i<childList.length;i++){
      var obj = childList[i];
      var s = "<li id='"+obj.menuId+"' menuName='"+obj.menuName+"' childMenu='"+Ta.util.obj2string(obj.childList)+
             "' onclick=\"showMenu('"+obj.menuId+"','"+obj.menuName+"','"+obj.url+"')\" title='"+obj.menuName+"'>";
      if($("#menuBox").hasClass("menuBox_middle")){
        s += "<div class='menuIcon menuIcon_middle'><img src='"+"indexue/indexresource/images/menu/"+obj.img+".png"+
            "'/></div><div class='menuName menuName_middle'>"+obj.menuName+"</div>";
        if(null!=obj.childList && obj.childList.length>0){
        	s += "<div class='menuArrow_m'></div></li>";
        }else{
        	s += "<div class='menuArrow_m menuArrow_h'></div></li>";
        }
      }else{
        s += "<div class='menuIcon'><img src='"+"indexue/indexresource/images/menu/"+obj.img+".png"+
             "'/></div><div class='menuName'>"+obj.menuName+"</div>";
        if(null!=obj.childList && obj.childList.length>0){
        	s += "<div class='menuArrow_s'></div></li>";
        }else{
        	s += "<div class='menuArrow_s menuArrow_h'></div></li>";
        }
      }
      secMenu.push(s);
    }
    $("#menu_ul").empty().append(secMenu.join("\n"));
    bindEvent();
  }
  if($(o).attr("url") != null){
	  showMenu($(o).attr("id"),$(o).attr("_name"),$(o).attr("url"));
  }
}
//打开菜单
function showMenu(tabid,title,url){
  if (url=="null")
     return;
  if(!url)
	  return;
  var tab = $("#tab_" + tabid);
  if (tab[0]) {
    if(confirm("页面已打开,是否刷新")){
      var mainFrame = IndexTab.getTabBd(tabid);
      $(mainFrame).attr("src",$(mainFrame).attr("src"));
    }
    IndexTab.actTab(tabid, true);
  }else{
    IndexTab.addTab(tabid, title, url);
  }
  $(".menu_close").hide();
}
//打开工作台
function fnIndexBtnClick() {
  IndexTab.actTab('01');
}
//打开修改密码窗口
function openPwChangeWindow() {
  var h = $(window).outerHeight(true);
  var w = $(window).outerWidth(true);
  $("body").append("<div class='window-mask' style='width:"+w+"px;height:"+h+"px'></div>");
  $("#passC").css({"top":(h-$("#passC").height())/2,"left":(w-$("#passC").width())/2});
  $("#passC").show();
  $("#oldPass").focus();
  $("#passInfo input").val("");
  $("#func_menu").fadeOut();
}
//关闭修改密码窗口
function fnClosePass(){
  $("#passC").hide();
  $("div.window-mask").remove();
}
//设置常用菜单
function fnSetCommonMenu(){
  IndexTab.actTab("01", true);
  window.frames["tab_b_01"].window.fnAddCommonMenu();
  $("#used_menu").fadeOut();
}
//切换岗位
function fnSelectPosition(o){
  var $o = $(o);
  if(confirm("确认切换岗位吗？")){
    var positionid = $o.attr("id").substring(2);
    $.ajax({
      "data":"__positionId="+positionid+"&positionid="+positionid,
      "url":"commonAction.do",
      "success":function(data){
        $("#positionChangeName").html($(o).attr("_name"));
        $("#selectPosition").hide();
      },
      "type":"POST",
      "dataType":"json"
    });
    //更改菜单样式
    $o.parent("ul").find("span").removeClass('pos-now');
    $o.find("span").addClass('pos-now');
  }else{
    $("#selectPosition").hide();
  }
}
//显示切换岗位菜单方法
function fnShowChildMenu(o){
  $("#roles").css({
	top:$(o).offset().top,
	left:$(o).offset().left + $(o).outerWidth(true)
  }).show();
}