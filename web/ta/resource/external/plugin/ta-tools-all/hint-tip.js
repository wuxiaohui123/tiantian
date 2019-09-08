(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","cookie"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.fn.hintTip = function(options){
		var defaults = {
			replay:false,//是否自动播放
			delayTime:3000,
			show:true,
			cookname:null,
			func: null,
			reID:"xxx",//"hint_tip",
			data:[]
		};
		var opts = $.extend(defaults,options);
		var replay   = opts.replay;
		var delayTime= opts.delayTime;
		var show     = opts.show;
		var cookname = opts.cookname;
		var func     = opts.func;
		var data     = opts.data;
		var reID     = opts.reID;
		
		var screenHeight = $(window).height();
		var bodyHeight   = $(document).height();
		//var s_b = screenHeight/bodyHeight;
		var bodyWidth  = $("body").width();
		var nowbody    = $("body"),url = window.location.href; 
		var time1;
		var time2;
		var hintName   = encodeURI("hint_"+cookname); // encodeURI("hint_"+url);    //"hint_"+url.replace(/\//g, "").replace(/:/g, "");
		var hintValue  = encodeURI("hint_"+cookname); // encodeURI("hint_"+url);    //"hint_"+url.replace(/\//g, "").replace(/:/g, "");
		//初始化
		init();
		//初始化	
		function init(){
			if(show){
				if(!checkHintCookie()){
				    nowbody.append("<div class='hint-mask'" 
				                  +"style='width:"+bodyWidth+"px;height:"+bodyHeight+"px;'></div>");
				    //遍历提示步骤
					stepTip();
				}
			}
			
			$(".hint-tips .hint-contents .hc-message .hm-opts .ho-next").bind("click",function(){
				 
				 	var nowhint = $(this).parent().parent().parent().parent();
				 	var step = parseInt(nowhint.attr("step"));
				 	//当前div如果是最后一个，则不在执行任何下一步操作（隐藏当前窗口 和 寻找下一窗口并且打开）
				 	if(step == data.length-1){
				 	    return;
				 	}
//					nowhint.remove();
				 	nowhint.hide();
					
					var nexthint = $(".hint-tips[step='"+(step+1)+"']");
					nexthint.show();
					var scrollTop = nexthint.offset().top;
					$("body,html").animate({
						scrollTop:scrollTop-200
					});				
			 });
			 
			 //注册点击上一步事件
			 $(".hint-tips .hint-contents .hc-message .hm-opts .ho-pre").bind("click",function(){
			     var nowhint = $(this).parent().parent().parent().parent();
			     var step = parseInt(nowhint.attr("step"));
//			     nowhint.remove();
			     nowhint.hide();
			     
			     var prehint = $(".hint-tips[step='"+(step-1)+"']");
			     prehint.show();
			     var scrollTop = prehint.offset().top;
			     $("body,html").animate({
				 scrollTop:scrollTop-200
			     });
			     
			 });
			 
			 //注册点击关闭向导事件
			 $(".hint-tips .hint-contents .hc-close").bind("click",function(){
				 
			 	 $(".hint-tips").remove();
				 $(".hint-mask").remove();
				 if(!checkHintCookie()){
					 writeCookie(hintName,hintValue);// 暂时关闭
				 }
				 clearTimeout(time1);
				 clearTimeout(time2);
//				 self = null;
			 });
			 
			 //最后一步关闭向导
			 $(".hint-tips .hint-contents .hc-message .hm-opts .ho-last").bind("click",function(){
			 	 $(".hint-tips").remove();
				 $(".hint-mask").remove();
				 if(!checkHintCookie()){
					 writeCookie(hintName,hintValue);
				 }
				 clearTimeout(time1);
				 clearTimeout(time2);
			 });
			 
			 //自动播放
			 if(replay){
				 time1 = setTimeout(autoPlay,delayTime) ;
			 }
			 
			 /**
			  * 重新引导
			  */
			 $("#"+reID).bind("click",function(){
			     
				 var hintCookieArray = new Array();
				 var data = {};
				 hintCookieArray = _getHintCookies("hint_");
				 for(var i = 0;i<hintCookieArray.length;i++){
					 var name = hintCookieArray[i];
				     	 $.cookie(name,null,{path:'/'});
				 }
			 });
			
		};	
		
		$.clearCookieHintArray    = function(currentBid){
			 var data = {};
			 var cookieArray = document.cookie.split(";");
			 var hintCookies = new Array();
			
			 for (var i = 0; i < cookieArray.length; i++) {
				var cookie = cookieArray[i].split("=");// 将名和值分开
				var name = $.trim(cookie[0]);
				var c_start = name.indexOf("hint_")
				if (c_start != -1) {
					if(!currentBid)
						$.cookie(name,null,{path:'/'});
					if(("hint_"+currentBid) == name)
						$.cookie(name,null,{path:'/'});
				}
			 }

		}
		
		
		
		
		//遍历data
		function stepTip(){
			var len = data.length;
			var isOne = false;
			var i,
			    obj,
				id;
				 
			if(len==0){
				return false;
			}
			for(i=0;i<len;i++){
				ditem = data[i];
				id = ditem.id;
				message = ditem.message;
				var child = ditem.child;
				if(typeof id == "string") {
					obj = $("#"+id);
				}
				if(typeof id == "object") {
					obj = id;
				}
				if(!obj[0]){
					continue;
				}else{
					isOne = true;
				}
				createHint(obj,message,i,len,child);
			}
			if(!isOne){
				$(".hint-tips").remove();
				$(".hint-mask").remove();
			}
		};
		
		 /**
		  * 创建提示的div
		  */
		 function createHint(obj,message,i,len,child){
			var message,
				o_height,
				o_width,
				ditem,
				o_x,
				o_y,
				borderHeight,
				borderWidth,
				arrowHeight,
				arrowWidth;
			o_height = obj.outerHeight();
			o_width  = obj.outerWidth();
			
			o_x = obj.offset().left;
			o_y = obj.offset().top;
			if(child == true) {
				o_x = o_x + 140;
				o_y = o_y + 101;
			}
			arrowHeight = 45;//原设置 65 
			var ht_style = "width:"+(350)+	"px;left:"+o_x+"px;top:"+o_y+	"px;min-width:250px;";
			var hc_style = "margin-top:"+(arrowHeight+o_height)+"px;";
			var hb_style = "width:"+o_width   +"px;height:"+o_height   +"px;top:"+-(arrowHeight+o_height)	+"px;left:-1px;";
			
			var div =$("<div class=\"hint-tips\" style="+ht_style+"  step="+i+">"
					   +"<div class=\"hint-contents\" style="+hc_style+">"
					   +"<div class=\"hc-border\" style="+hb_style+"></div>"
                       +"<div class=\"hc-arrow\"></div>"
                       +"<div class=\"hc-close\"></div>"
    	               +"<div class=\"hc-message\">"
        	           +"<div class=\"hm-content\">"+message+"</div>"
                       +"<div class=\"hm-opts\">"
            	       +"<span class=\"ho-next\">下一步</span>"
            	       +"<span class=\"ho-last\">向导结束，进入信息平台</span>"
            	       +"<span class=\"ho-pre\">上一步</span>"
                       +"</div>"
                       +"</div>"
                       +"</div>"
                       +"</div>");
			nowbody.append(div);
		   
			//箭头 提示内容的位置
			var div_width = div.outerWidth();
			var div_height = div.outerHeight();
			var content_height =div_height - parseInt(div.children(".hint-contents").css("margin-top"));
			 
			var flag_w = div_width + o_x;
			var flag_h = div_height + o_y;
		
			// 2.
			if(flag_w>=bodyWidth&&flag_h<bodyHeight){
				div.css("left",(o_x+o_width-div_width)+"px");
				div.find(".hc-border").css("left",(div_width-o_width)+"px");
				div.find(".hc-arrow").css({"background-position":"0px 0px","left":(div_width-70)+								                "px"});
			}
			// 3.
			if(flag_w>=bodyWidth&&flag_h>=bodyHeight){
				div.css({"left":(o_x+o_width-div_width)+"px","top":(o_y-div_height-arrowHeight)+ "px"});
				div.find(".hc-border").css({"left":(div_width-o_width)+"px","top":(content_height+arrowHeight)});
				div.find(".hc-arrow" ).css({"background-position":"0px -232px",
				"left":(div_width-70)+"px","top":content_height+"px"});
			}
			//4.
			if(flag_w<bodyWidth&&flag_h>=bodyHeight){
				
				div.css({"top":(o_y-div_height-arrowHeight+200)+"px"});
//				div.css({"left":o_width+ arrowHeight + "px"});
				div.find(".hc-border").css({"top":(content_height+arrowHeight-200)});
				div.find(".hc-arrow").css({"background-position":"0px -65px","top":content_height+"px"});//原设置			
				  
//				div.find(".hc-arrow").css({"left":o_x+o_width+"px","top":o_y + arrowHeight + "px"});				
//				div.find(".hc-message").css({"left":o_x+o_width+"px","top":o_y + arrowHeight + "px"});				
			}
			if(i==0){
				div.show();
				$("body,html").animate({
					scrollTop:o_y-200 //*s_b + 100
				});
			}else{
			    div.find(".hm-opts").children(".ho-pre").show();// 只要不是第一次，就显示“上一步”按钮
			}
			
			if(i==(len-1)){
				div.find(".hm-opts").children(".ho-next").hide();
				div.find(".hm-opts").children(".ho-last").show();
			}
		 };
		
		 //注册点击下一步事件
		
		 function autoPlay(){
			
		     var nowhint = $(".hint-tips:visible");//获取当前可见的那个div，首次执行时可见的就是第一个div
		     var step = parseInt(nowhint.attr("step"));
		     
		     $(".hint-tips[step='"+(step)+"'] .hint-contents .hc-message .hm-opts .ho-next").click();
		     if(step==data.length-1){
		    	 clearTimeout(time1);
		    	 return;
		     }
		     time2 = setTimeout(autoPlay,delayTime) ;
		 }
		 
		//向cookie写入数据
		 function writeCookie(hintName,hintValue){
		     
			 var data= {};
			 data["dto['hintName']"]  = hintName;
			 data["dto['hintValue']"] = hintValue;
			 
			 $.cookie(hintName, hintValue, { expires: 365, path: '/' }); 
 

		 };
		 
		 //检查hintcookie是否存在
		 function checkHintCookie(){
			 var cookieValue = getHintCookie(hintName);
			 if(cookieValue==hintValue){
				 return true;
			 }
			 return false;
		 };
		 
		 
		 /**
		  * 获得所有名称包括c_name的cookie
		  */
		 function _getHintCookies(c_name){
			var cookieArray = document.cookie.split(";");
			var hintCookies = new Array();
			for (var i = 0;i<cookieArray.length;i++){
				var cookie = cookieArray[i].split("=");//将名和值分开
				var name   = $.trim(cookie[0]);
				var c_start = name.indexOf(c_name)
				if(c_start != -1){
					hintCookies.push(name);
				}
			}
			
			return hintCookies;
		}
		 
		function getHintCookie(name){
				var cookieArray = document.cookie.split(";"); //得到分割的cookie名值对    
				var cookie = new Object();   
				for (var i=0;i<cookieArray.length;i++){    
				      var arr=cookieArray[i].split("=");       //将名和值分开    
				      
				      if($.trim(arr[0])==$.trim(name))
				    	  return unescape(arr[1]); //如果是指定的cookie，则返回它的值    
				   } 
				   return "";
		};
	}
	
}));
