var IndexTab = {
    //添加tab
    addTab: function(id, text, url, reload){
        var tab = IndexTab.getTab(id);
        //若已经存在，直接激活
        if (tab) 
            return IndexTab.actTab(id, true);
        var tArr = [];
        tArr.push("<li  class=\"index_menuTab index_menuTab_noact\">");
        tArr.push("<div  class=\"index_tab_center\" title=\"" + text + "\">" + text + "</div>");
        tArr.push("<a href=\"#\" onclick=\"IndexTab.closeTab('" + id + "')\">×</a>");
        tArr.push("</li>");
        var html = $(tArr.join(""));
        html.bind("click", function(){
            IndexTab.actTab(id);
        }).hover(function(){
            $(this).find("a").show();
        }, function(){
            $(this).find("a").hide();
        });
        var indexTabs = $("#indexTabs");
        html.attr("id", "tab_" + id).attr("_id", id).insertBefore("#tabListIcon");
        /*indexTabs.append(html.attr({
         "id": "tab_" + id,
         "_id": id
         }));*/
        //添加iframe
        var tabBody = $("<iframe   class='mainFrame' frameborder=\"0\"/>").attr("id", "tab_b_" + id).attr("name", "tab_b_" + id);
        if (reload) 
            tabBody.attr("reload", true);
        tabBody.css({
            width: "100%",
            height: $("#mainFrameBox").height()
        });
        if (url) {
        	if(url.indexOf("?") > 0){
        		tabBody.attr("src", url+"&___businessId="+id);
        	}else{
        		tabBody.attr("src", url+"?___businessId="+id);
        	}
        }
            
        $("#mainFrameBox").append(tabBody);
        //加入下拉
        IndexTab.addTabList(id, text, false);
        IndexTab.actTab(id,true);//flag设置成true表示为第一次打开菜单
        //右键菜单
        IndexTab.addContextmenu();
        //宽度计算
        IndexTab.setPreantWidth();
        window.setTimeout(IndexTab.setPreantWidth, 199);
    },
    //激活tab
    actTab: function(id, flag, reload){
        var tab = $(IndexTab.getTab(id));
        if (tab.hasClass("index_menuTab_act")) 
            return;
        $(".index_menuTab").removeClass("index_menuTab_act").addClass("index_menuTab_noact");
        tab.removeClass("index_menuTab_noact").addClass("index_menuTab_act");
        var ifrm = $("#mainFrameBox iframe").hide();
        $("#tab_b_" + id).show();
        if (id != currentBuinessId) {
            currentBuinessId = id;
            var param = "___businessId="+id;
            if(!flag){
            	//param += "&__common=__common";
	            $.ajax({
	            	"data":param,
	            	"url":"commonAction.do",
	            	"type":"POST",
					"dataType":"json"
	            });
            }
           // Base.getJson('commonAction.do', param);
        }
        fnTabChgResize(id);
        window["ACT_TAB"] = id;
        var curIframe = document.getElementById("tab_b_" + id);
        if ($(curIframe).attr("reload")) 
            curIframe.contentWindow.fnOnload();
        //$("#menuPath").html(g_path[id]);
        //添加圆点  list-item-act
        $("#tabList li").removeClass("list-item-act");
        $("#l_" + id).addClass("list-item-act");
    },
    //关闭tab
    closeTab: function(id){
        if (!id) 
            return;
        var tab = $(IndexTab.getTab(id));
        //获取待激活tab页
        if (tab.hasClass("index_menuTab_act")) {
            IndexTab.actTab($(tab.next(".index_menuTab")[0] || tab.prev(".index_menuTab")[0] || $("#tab_01")[0]).attr("_id"));
        }
        $("#tab_b_" + id).remove();
        tab.remove();
        //session
        //Base.getJson('sessionResourceAction!endBusiness.do?timeStamp='+new Date().getTime(),{'___businessId':id},null,true);
        IndexTab.addTabList(id, null, true);
        if ($("#indexTabs .index_menuTab").length == 0) {
            $("#tabListIcon").hide();
            $("#tabList").fadeOut();
            fnIndexBtnClick();
        }
        IndexTab.setPreantWidth();
        window.setTimeout(IndexTab.setPreantWidth, 199);
    },
    //获取tab
    getTab: function(id){
        return document.getElementById("tab_" + id);
    },
    //获取tab body
    getTabBd: function(id){
        return document.getElementById("tab_b_" + id);
    },
    //设置indextabs容器宽度
    setPreantWidth: function(){
        var tabListWidth = 28;
        var indexTabs = $("#indexTabs"), w = tabListWidth, m = 0;
        indexTabs.find(".index_menuTab .index_tab_center").each(function(i){
            w += parseFloat($(this).attr("_width")); //向上取整，解决各个浏览器对小数px处理不一致的问题
            m = i + 1;
        });
        if (m > 0) {
            var maxWidth = $(window).width() - (112 + 14 + 14+200); //112:“工作台”宽度,12:左边留白,12:右边留白,27:下拉操作按钮宽度,240:右边操作栏
            var tabs = indexTabs.find(".index_tab_center");
            if (w >= maxWidth) {
                w = maxWidth;
                var curWidth = ((maxWidth - tabListWidth) / tabs.length);
                tabs.width(curWidth - 25).attr("_width", curWidth); // 21为 padding 和 border
            }
            else {
                tabs.width(100).attr("_width", 100 + 12 + 12 + 2 );//100：宽度,10:分别是左右padding,2:左右边框,-1:向左的margin 
            }
            indexTabs.width(w);
        }
    },
    addTabList: function(id, name, remove){
        if (remove) {
            $("#l_" + id).remove();
        }
        else {
            html = '<li title="' + name + '" onclick="IndexTab.actTab(\'' + id + '\');$(\'#tabList\').fadeOut();" id="l_' + id + '"><span>' + name + '</span><b class="list-li-close" title="关闭该菜单" onclick="fnListliClose(this)">×</b></li>'
            $(html).appendTo("#tabList");
        }
        if ($(".index_menuTab").length > 0) 
            $("#tabListIcon").show();
    },
    showTabList: function(o){
        var target = $(o);
        var pos = target.offset();
        $("#tabList").css({
            "top": (pos.top) + target.height() + 1,
            "left": pos.left - $("#tabList").width() + target.width() - 210
        }).show();
    },
    closeAllTab: function(){
        $("#indexTabs .index_menuTab").each(function(){
            IndexTab.closeTab($(this).attr("_id"));
        });
        $("#tabListIcon").hide();
        $("#tabList").fadeOut();
    },
    addContextmenu: function(){
        $(".index_menuTab").bind('contextmenu', function(e){
            var $c = $("#tabRightClickDiv");
            var xx = e.clientX || e.originalEvent.x || e.originalEvent.layerX || 0;
            var yy = e.clientY ||e.originalEvent.y || e.originalEvent.layerY || 0; 
            if ($c && $c.length == 1) {
                $c.css({
                    "left": xx,
                    "top": yy
                }).show();
            }
            else {
                var html = "";
                html += '<ul class="menu-list" id="tabRightClickDiv">';
                html += '<li onclick="IndexTab.fnRefresh()"><span>刷新</span></li>';
                html += '<li onclick="IndexTab.fnTabRightCloseAll()"><span>关闭所有</span></li>';
                html += '<li onclick="IndexTab.fnTabRightCloseOther()"><span>关闭其他</span></li>';
                html += '</ul>';
                $c = $(html);
                $("body").append($c);
                $c.css({
                    "left": xx,
                    "top": yy
                }).show();
            }
            $c.hover(function(){
                //$(this).hide();
            }, function(){
                $(this).hide();
            });
            rightTab = this;
            return false;
        });
    },
    fnTabRightCloseAll: function(){
        IndexTab.closeAllTab();
        $("#tabRightClickDiv").hide();
    },
    fnTabRightCloseOther: function(o){
        $("#indexTabs .index_menuTab").each(function(){
            if ($(this).attr("_id") == $(rightTab).attr("_id")) {
                IndexTab.actTab($(rightTab).attr("_id"));
            }
            else 
                IndexTab.closeTab($(this).attr("_id"));
        });
        $("#tabRightClickDiv").hide();
    },
    fnRefresh:function(){
    	$("#indexTabs .index_menuTab").each(function(){
            if ($(this).attr("_id") == $(rightTab).attr("_id")) {
                IndexTab.actTab($(rightTab).attr("_id"));
                var Kid = $(this).attr("_id");
    	        var src = $("#tab_b_"+Kid).attr("src");
    	        $("#tab_b_"+Kid).attr("src",src);
            }   
        });
    	$("#tabRightClickDiv").hide();
    }
}
var currentBuinessId = "01";
function fnListliClose(obj, event){
    event = event || window.event;
    var target = "";
    // 如果传入了事件对象，那么就是非ie浏览器  
    if (event && event.stopPropagation) {
        //因此它支持W3C的stopPropagation()方法  
        event.stopPropagation();
        target = event.target;
    }
    else {
        //否则我们使用ie的方法来取消事件冒泡  
        window.event.cancelBubble = true;
        target = event.srcElement;
    }
    var li_id = $(target).parent("li").attr("id");
    IndexTab.closeTab(li_id.substring(2));
    $("#tabList").hide();
}

function fnTabChgResize(id){
    var h = $("#tab_b_" + id).height();
    $('#indexMain').height((h > 480 ? h : 480));
}

$(document).ready(function(){
    $(document).bind('mousedown', function(event){
        if (!($(event.target).parents().andSelf().is('#tabList'))) {
            $("#tabList").fadeOut();
        }
    });
    $(document).bind('mousedown', function(event){
        if (!($(event.target).parents().andSelf().is('#settingBox'))) {
            $("#settingBox").fadeOut();
        }
    });
    $(document).bind('mousedown', function(event){
        if (!($(event.target).parents().andSelf().is('#user'))) {
           $("#user,#roles").fadeOut()
        }
    });
    $("#tabList").hover(function(){
        //$(this).hide();
    }, function(){
        $(this).hide();
    });
});
$(window).resize(function(){
    setTimeout(function(){
    	$("#mainFrameBox >iframe").height($("#mainFrameBox").height());
    },100);
    IndexTab.setPreantWidth();
});
