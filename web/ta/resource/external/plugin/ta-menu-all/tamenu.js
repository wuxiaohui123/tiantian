/**
 * 
 * @author lins
 */
//var a = [{menuType:<div class="menu-sep"></div>}, {
//    id:'xxxx', 
//    name : 'xxxx',
//	href:'xxx',
//	icon:'',
//	onClick:function(){} || "functionName", 
//	menuType:"a||checkbox||radio||", 
//	cssStyle:"", 
//	cssClass : "",
//	children : [{
//		id:'xxxx', 
//		name : 'xxxx',
//		icon:'',
//		href:'xxx', 
//		onClick:function(){} || "functionName", 
//		menuType:"a||checkbox||radio||", 
//		cssStyle:"", 
//		cssClass : "",
//		children :[{}]
//	}]
//}]; 

(function($) { //匿名闭包
	// 在window中注册 组建
    $.extend(true, window, {
        YHUI: { //外层名称
            Menu: Menu //内层名称:构造函数
        }
    }); //既是 new YH.UI(...);构造

    /**
     * 构造函数
     * id：dom标签的id
     * options ：参数
     * args ：额外参数，可以为多个
     */
    function Menu(id, data, url, options, dataOptions) {
        // 默认参数,如有必要可定义多个默认参数/////
        var defaults = {
        	width : "140px",
        	dataType : "children"
        };
        
        var dataDefaults = {
        	id : "id",
        	pid : "pid",
			name : 'name',
			href : 'name',
			icon : 'icon',
			text : "text",
			onClick: "onClick" , 
			menuType:"menuType", 
			cssStyle : "cssStyle",
			cssClass : "cssClass",
			children : "children",
			px : "px"
        };
        ////////////////
        //组建全局变量
        var $g_container;
        var $g_menu;
        var _$menuContainers = [];
        //////////////////////////////////////////////////////////////////////////////////////////////
        //初始化方法
        function init() {
            $g_container = $("#" + id); 
            options = $.extend({},defaults,options); //默认option和自订opetion结合
            dataDefaults = $.extend({},dataDefaults,dataOptions); //默认option和自订opetion结合
            
           	setMenuData(data);
            //可调用其他方法
        }// end init
        
        function getRootNode(data) {
        	var min = 999999999999;
        	var num = 0;
        	for (var i = 0 ; i < data.length; i ++ ) {
        		if (min >data[i]) {
        			min = data[i][dataDefaults.id];
        			num = i;
        		}
        	}
        	return data[num];
        }
        
        function convertDataFromIdPid(node) {
        	for (var i = 0 ; i < data.length; i ++) {
	        	if (node[dataDefaults.id] == data[i][dataDefaults.pid]) {
					if (node[dataDefaults.children] == undefined) {
						node[dataDefaults.children] = [];
					}
					node[dataDefaults.children].push(data[i]);
					convertDataFromIdPid(data[i]);
    	    	}
        	}
        }
        
       /**
        * 公共方法1
        * {
			id:'xxxx', 
			name : 'xxxx',
			icon:'',
			href:'xxx', 
			onClick:function(){} || "functionName", 
			menuType:"a||checkbox||radio||", 
			cssStyle:"", 
			cssClass : "",
			children :[{}]
		}
        */
        function createMenuDiv(datas, div) {
        	_$menuContainers.push(div);
        	//if (typeof datas != "array") return;
        	for (var i = 0; i < datas.length; i ++) {
        		var data = datas[i];
        		if (data[dataDefaults.menuType] == "menu-sep") {
        			$('<div class="menu-sep">').appendTo(div);
        		} else {
	        		var divStr = "<div ";
	        		if (data[dataDefaults.id])
	        			divStr += ' id="' + data[dataDefaults.id] + '"';
	        		if (data[dataDefaults.onClick] )
	        			divStr += ' onclick="' + data[dataDefaults.onClick] + '"';
	        		if (data[dataDefaults.icon])
	        			divStr += ' icon="' + data[dataDefaults.icon] + '"';
	        		divStr += ' style="width:140px"/>';
	        		var $divParent = $(divStr).appendTo(div);
	        		if (data[dataDefaults.children] && data[dataDefaults.children].length  > 0) {
	        			data[dataDefaults.children].sort(function(a,b){return a[dataDefaults.px] - b[dataDefaults.px];});
	        			if (data[dataDefaults.text])
	        				$divParent.html("<span>" + data[dataDefaults.text] + "</span>");
	        			var childDivStr = "<div ";
	        			if (data[dataDefaults.cssStyle])
	        				childDivStr += ' cssStyle="' + data[dataDefaults.cssStyle] + '"';
	        			if (data[dataDefaults.cssClass])
	        				childDivStr += ' cssClass="' + data[dataDefaults.cssClass] + '"';
	        			childDivStr += "/>";
	        			var $childDiv  = $(childDivStr).appendTo($divParent);
	        			createMenuDiv(data[dataDefaults.children], $childDiv);
	        		} else {
	        			if (data[dataDefaults.menuType] == "checkbox" || data[dataDefaults.menuType] == "radio") {
							var chra = "<span>" + "<input onClick='event.stopPropagation();' type='" + data[dataDefaults.menuType] +"' name='" + data[dataDefaults.name] + "'/>" + (data[dataDefaults.text]?data[dataDefaults.text]:"") + "</span>";
	        				$divParent.append(chra);
	        			}
	        			else if (data[dataDefaults.href] && data[dataDefaults.text] != undefined)
	        				$divParent.html('<a href="' + data[dataDefaults.href] + '">' + data[dataDefaults.text] + "</a>");
	        			else if (data[dataDefaults.text] != undefined)
	        				$divParent.html(data[dataDefaults.text]);
	        			if (data[dataDefaults.cssStyle])
	        				$divParent.css(data[dataDefaults.cssStyle]);
	        			if (data[dataDefaults.cssClass])
	        				$divParent.addClass(data[dataDefaults.cssClass]);
	        		}
	        	}
        	}
        }
        
        /**
         * 从url读取数据
         */
        function loadDataFromUrl(url, submitIds, param) {
        	if (url == undefined) {
        		return;
        	} else {
        		var data = Base.getJson(submitIds, url, param);
        		setMenuData(data);
        	}
        }
        
        /**
         * 设置菜单数据
         */
        function setMenuData(data) {
        	for (var i = 0; i < _$menuContainers.length; i ++ ) {
        		_$menuContainers[i].remove();
        	}
        	var menuData = [];
        	if (options.dataType == "tree") {
        		 var root = getRootNode(data);
        		 convertDataFromIdPid(root);
        		 menuData.push(root);
        	} else {
        		menuData = data;
        	}
        	$g_menu = $('<div id="'+ id + '_menu">').css("width", options.width).appendTo("body");
        	createMenuDiv(menuData, $g_menu);
            $g_menu.menu();
        	$g_container.click(function(e){
				$($g_menu).menu('show',{
							left: e.pageX,
							top: e.pageY
				});
			});
        }
        /**
         * 
         */
        function extendFn(functionName , functionBody) {
        	this[functionName] = functionBody;
        }
        //////////////////////////////////////////////////////////////////////////////////////////////
        // 注册 Public API
        $.extend(this, { //为this对象
        	"extendFn": extendFn,
        	"cmptype":'menu', //组建类型
            "UIVersion": "1.0", //组建版本
            "setMenuData" : setMenuData,
            "loadDataFromUrl" :loadDataFromUrl
        });
        init();//调用初始化方法
    }
}(jQuery)); // 闭包完

