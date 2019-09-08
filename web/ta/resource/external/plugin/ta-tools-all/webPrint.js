(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){ 
	$.fn.printForTablePage = function(options){
	//各种属性、参数 
		var defaults = { 
			isVertical:true,//是否纵向打印,true纵向打印,false横向打印
			paper:"A4",//纸张规格
			marginLeft:"0.5",//单位：英寸,左边页边距
			marginRight:"0.5",//单位：英寸,右边页边距
			marginTop:"0.5",//单位：英寸,上边页边距
			marginBottom:"0.3",//单位：英寸,底边页边距
			printButton:"printButton",//打印按钮的ID，页面上无须绑定onclick事件
			headerClass:"header",//页头的样式
			itemClass:"item",//普通行的样式
			isIframePrint:false,//打印页面是否在iframe框架里面
			isAutoPageBreak:false,//是否自动分页
			isContainHeader:false
		} 
		var browser = getNavigatorType();
		
		var $this = $(this);
		
		var options = $.extend(defaults, options); 
		
		window.onload=function(){
			pageSetup(options);
			autoPageBreak(options);
		};
		
		
		
		if("ie" == browser){
			var isIePrint = getQueryString("isIePrint");
			if(isIePrint==1){
				document.all.WebBrowser.ExecWB(7,1);
			}
			$("#"+options.printButton).click(function(){
				if(options.isIframePrint){
					window.open(window.location.href+'?isIePrint=1','','height=600,width=1024,top=0,left=0,toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no');
				}else{
					document.all.WebBrowser.ExecWB(7,1);
				}
			});
		}else if("chrome" == browser){
			$("#"+options.printButton).click(function(){
				var histoty = document.body.innerHTML;
				window.print();
				document.body.innerHTML = histoty;
			});
		}
		//解析浏览器地址栏参数
		function getQueryString(name) {    
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
			var r = window.location.search.substr(1).match(reg);
			if (r != null) return decodeURIComponent(r[2]); 
			return "";
		};
		
		//判断浏览器类型
		function getNavigatorType(){
			var Sys = {};
			var ua = navigator.userAgent.toLowerCase();
			var s;
			(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
			(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
			(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
			(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
			(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
			if(Sys.ie){
				return "ie";
			}else if(Sys.firefox){
				return "firefox";
			}else if(Sys.chrome){
				return "chrome";
			}else if(Sys.opera){
				return "opera";
			}else if(Sys.safari){
				return "safari";
			}else{
				return "";	
			}
		}
		
		//获得IE浏览器版本
        function checkIEV() {
            var X, V, N;
            V = navigator.appVersion;
            N = navigator.appName;
            if (N == "Microsoft Internet Explorer")
                X = parseFloat(V.substring(V.indexOf("MSIE") + 5, V.lastIndexOf("Windows")));
            else
                X = parseFloat(V);
            return X;
        }
		
		//自动分页,解决tr跨页显示的问题
		function autoPageBreak(options){
			if(options.isAutoPageBreak){
				//定义打印页面高度
			var pageHeight = 0;
			if("ie" == browser){
				if(options.isVertical){
					pageHeight = 1020;	
				}else{
					pageHeight = 810;	
				}
			}else if ("chrome" == browser){
				if(options.isVertical){
					pageHeight = 1070;	
				}else{
					pageHeight = 750;
				}
			}
			var h = 0;
			var content = "<table width='100%' cellpadding='0' cellspacing='0' border='0'>";
			$("tr").each(function(i) {
               h += $(this).height();
			    //console.log(h+":"+pageHeight+":"+$(this).height()+":"+i);
			   if( h < pageHeight){
				   content += $(this).prop('outerHTML');
				}else if( h == pageHeight){
					content += $(this).prop('outerHTML');
					content += "</table><table width='100%' cellpadding='0' cellspacing='0' border='0'>";
					if(options.isContainHeader){
						if($(this).next("tr").hasClass(options.itemClass)){
							content += $("tr."+options.headerClass).prop('outerHTML');
							h += $("tr."+options.headerClass).height();
						}
					}
					h = 0;
				}else{
					content += "</table><table width='100%' cellpadding='0' cellspacing='0' border='0'>";
					h = 0;
					if(options.isContainHeader){
						if($(this).hasClass(options.itemClass)){
							content += $("tr."+options.headerClass).prop('outerHTML');
							h += $("tr."+options.headerClass).height();
						}
					}
					content += $(this).prop('outerHTML');
				}
            });	
			content+= "</table>";
			$this.empty().html(content);
			}
		}
		
		//设置网页打印的页眉页脚和页边距
        function pageSetup(options) {
			if("ie" == browser){
				var HKEY_Root, HKEY_Path, HKEY_Key;
            HKEY_Root = "HKEY_CURRENT_USER";
            HKEY_Path = "\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
            try {
                var Wsh = new ActiveXObject("WScript.Shell");
                HKEY_Key = "header";
                //设置页眉（为空）
                //Wsh.RegRead(HKEY_Root+HKEY_Path+HKEY_Key)可获得原页面设置   
                Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");
                HKEY_Key = "footer";
                //设置页脚（为空）   
                Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");

                //这里需要浏览器版本，8.0以下的页边距设置与8.0及以上不一样，注意注册表里的单位是英寸，打印设置中是毫米，1英寸=25.4毫米
                if (checkIEV() < 8.0) {
                    HKEY_Key = "margin_left";
                    //设置左页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.25");
                    HKEY_Key = "margin_right";
                    //设置右页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.25");
                    HKEY_Key = "margin_top";
                    //设置上页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.10");
                    HKEY_Key = "margin_bottom";
                    //设置下页边距   
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.10");
                }
                else {
                    HKEY_Key = "margin_left";
                    //设置左页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginLeft);
                    HKEY_Key = "margin_right";
                    //设置右页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginRight);
                    HKEY_Key = "margin_top";
                    //设置上页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginTop);
                    HKEY_Key = "margin_bottom";
                    //设置下页边距   
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginBottom);
                }
            }
            catch (e) {
                alert("ActiveX控件被禁用,请按下面步骤操作：\n1、请打开浏览器‘工具’菜单/‘选项’/‘安全’下的‘自定义级别’，\n把‘对没有标记为安全的activex控件进行初始化和脚本运行’设置为‘启用’。\n2、刷新本页 ");
            }
			}
            
        }
	}; 
})); 