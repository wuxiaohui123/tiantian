/**
 * 模拟窗口组件常用方法,调用方式为Base.xxx();
 * @module Base
 * @class window
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil","window"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Base:core()
    });
	
	function core(){
		return {
			openWindow : openWindow,
			openWindowWithSubmitIds : openWindowWithSubmitIds,
			closeWindow : closeWindow,
			alert : alert,
			msgTip : msgTip,
			msgTopTip : msgTopTip,
			confirm : confirm,
			prompt : prompt,
			buttonsDialog : buttonsDialog,
			sendMsgToFrame : sendMsgToFrame,
			openTabMenu : openTabMenu,
			showBoxComponent : showBoxComponent,
			wizard:wizard
		};
		/**
		 *
		 * @method  
		 * @param  {boolean} replay 是否自动播放
		 * @param {boolean} flag panel的id
		 * @param {String} name 在cookie中存放的名称，应保证唯一，最好采用当前页面的id
		 * @param {Array} data 导航内容，包含1.所要提示的dom元素的jquery对象；2.提示内容
		 * var data = [object1,object2,[..objectn]]
		 * var object1 = {};
		 * object1.id = "#id" || $("#id")
		 * object1.message = "这是你的下一步提示信息";  
		 * data:[{id:$("ul.tabs li:eq(0)"),
		        	message:"1-这是您的当前页"
		         },
		         {id:$("ul.tabs li:eq(1)"),
		          	message:"2-点击这里，您将进入一个查询页面"
		         },
		         {id:$("ul.tabs li:eq(2)"),
		          	message:"3-点击这里，您将进入一个查询页面"
		         }]
		*  
		*/
		function wizard(replay,flag,name,data){
		    $.hintTip({
				replay:replay,
				show:flag, 
				cookname:name,
				data:data
		    });
		}
		
		
		/**
		 * 打开窗口
		 * @method openWindow
		 * @param {String} id 窗口id
		 * @param {String} title 窗口标题
		 * @param {String} url aciton地址
		 * @param {map} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Number} width 宽度 不要加 px；也可设置成百分比，例如"80%"
		 * @param {Number} height 高度 不要加 px；也可设置成百分比，例如"80%"
		 * @param {Function} onLoad 窗口加载完毕回调，如果useIframe=true的话 这参数不起作用
		 * @param {Funciton} onClose 窗口关闭的时候回调
		 * @param {Boolean} useIframe 是否使用iframe的方式加载，默认为false，iframe方式会存在seesion丢失，应当避免;为true的时候，打开页面为一个完整的jsp页面
		 * @param {String} style 自定义打开窗口css样式
		 * @param {Object} myoptions window的创建参数
		 */
		function openWindow(id,title,url,parameter,width,height,onLoad,onClose,useIframe,style,myoptions){
			var $w = $("<div id=\""+id+"\" "+(style?("style=\""+style+"\""):"")+"></div>");
			$w.appendTo($("body"));
			var options = {};
			
			title ? (options.title = title):null;
			width ? (options.width = width):(options.width = 200);
			height ? (options.height = height):(options.height = 200);
			onLoad ? (options.onLoad = onLoad):null;
			
			options.modal = true;
			options.resizable = false;
			options.minimizable = false;
			options.collapsible  = false;	
			if(parameter){
				if(url.indexOf('?') != -1){
					url += "&"+jQuery.param(parameter);
				}else{
					url += "?"+jQuery.param(parameter);
				}
			}
			if(url == null || url == ""){
				url = "";
			}else if(url.indexOf('?') != -1){
				url += "&_r="+Math.random();
			}else{
				url += "?_r="+Math.random();
			}
			if(url && useIframe){
				options.content = '<iframe type="window" src="'+url+'" frameborder="0" style="width:100%;height:100%"></iframe>';
			}else{
				url ? (options.href = url):null;
			}
			options.onClose =function(_onClose){
				return function(){
					if(_onClose)
						_onClose($w.attr("id"));
					//remove window里面TaUIManager对应的对象
					Ta.core.TaUIManager.removeObjInCantainer($w);
					Base.hideMask();
					$w.window('destroy');
					$w.remove();
				};
			}(onClose);
			if(myoptions)
				$.extend(options,myoptions);
			$w.window(options);
			if(!url){
				onLoad();
			}
			return $w;
		}
		function openWindowWithSubmitIds(id,title,url,submitIds,parameter,width,height,onLoad,onClose,useIframe,style,myoptions,type){
			var paramThis = {};
			if (parameter != undefined) paramThis = parameter; 
			var submitId = submitIds.split(",");
			for (var i = 0 ; i < submitId.length; i ++) {
				if (submitId[i] == "") continue;
				var value = Base.getValue(submitId[i]);
				if (value != undefined && value != "") {
					paramThis[submitId[i]] = value;
					paramThis["dto['" + submitId[i] + "']"] = value;
				}
			}
			switch (type) {
			case "top":
				top._child = window.self;
				top.Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			case "parent":
				parent._child = window.self;
				parent.Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			default:
				Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			}
			
		}
		/**
		 * 关闭窗口
		 * @method closeWindow
		 * @param {String} id 窗口id
		 */
		function closeWindow(id){
			setTimeout(function(){	$("#"+id).window('close');}, 1);
		}
		/**
		 * 弹出提示
		 * @method alert
		 * @param {String} msg 提示的信息，可以是html
		 * @param {String} type 提示的图标，不传就无图标。type可以选择如下：success,error,warn,question
		 * @param {Function} callback 回调函数
		 */
		function alert(msg,type,callback){
			var title = "提示",image="",html="";
			switch(type){
				case 'success':
					title="成功提示";
					image = "l-dialog-image-donne";
					break;
				case 'error':
					title="失败提示";
					image = "l-dialog-image-error";
					break;
				case 'warn':
					title="警告";
					image = "l-dialog-image-warn";
					break;
				case 'question':
					title="确认";
					image = "l-dialog-image-question";
					break;
			}
			if(type){
				html = "<div><div class='l-dialog-image "+image+"'></div><div class='l-dialog-msg' >"+msg+"</div></div>";
			}else{
				html = "<div><span>"+msg+"</span><div>";		
			}
			var $w = $(html);
			$w.appendTo($("body"));
			
			$w.dialog({
				title:title,
				width:300,
				height:150,
				//top : '100px',
				modal:true,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
					if(callback)callback();
					Base.hideMask();
				},
				buttonsAlgin:'center',
				buttons:[{
					text:'确定',
					buttonHighHlight:true,
					handler:function(){
						$w.dialog('destroy');
						$w.remove();
						if(callback)callback();
						Base.hideMask();
					}
				}]
			});
			$w.find("div.panel").css("margin",0);
			$w.find('.dialog-button button:first').delay(100).focus();
		}
		/**
		 * @method msgTip
		 * @deprecated 弃用的方法
		 */
		function msgTip(target, message){
		//TODO
			var box = $(target);
			var msg = message; //= $.data(target, 'validatebox').message;
			var tip = null;
			if (!tip){
				tip = $('<div class="validatebox-tip">' +
						'<div class="validatebox-tip-content  ui-corner-all"></div>' +
						'<div class="validatebox-tip-pointer"></div>' +'</div>'
				).appendTo('body');
				//$.data(target, 'validatebox').tip = tip;
			}
			var $c = tip.find('.validatebox-tip-content');
			$c.html(msg);
			var cheight = $c.height();
			if(cheight==0) cheight =17;
			tip.css({
				display:'block',
				left:box.offset().left+10 ,
				top:(box.offset().top-cheight-15)
			});
			//$.data(target, 'validatebox').tip =tip;
		}

		/**
		 * 信息展示框,在页面头顶出现一悬浮框,显示信息
		 * @method msgTopTip
		 * @param {String} message 提示的信息，可以是html
		 * @param {Number} time 悬浮框持续时间,毫秒计时,如果不定义此参数或者不是数字,则默认为2s.
		 * @param {Number} width 悬浮框宽度,默认250.
		 * @param {Number} height 悬浮框高度,默认50.
		 * @param {String} style 自定义样式，例如:background:red;font-size:14px;.
		 */
		function msgTopTip(message,time,width,height,style,target){
			var tip = null,left,tempHeight = -63,temptop = 0;
			var metarget = (target==null||target=="")?"body":target;
			temptop = (target==null||target=="")?tempHeight:$(target).offset().top;
			if(time == null || isNaN(time)){
				time = 2000;
			}
			if (!tip){
				style = style==undefined?"":style;
				tip = $('<div class="windowTopMsg"></div>')
				.append('<div class="header" style="height:18px;border-bottom:1px #c6c6c6 solid;"><span class="closeTip" style="color:red;float:right;cursor:pointer">x</span></div><div class="body" style="margin-top:3px;'+style+'">'+
						message+'</div>').css('top',temptop).appendTo(metarget);
				$(".closeTip").click(function(){
					tip.animate({top:tempHeight+"px"},500);
					setTimeout(function(){
						tip.remove();
					},500);
				});
			}
			if(width !== undefined && width !== null && width !=="" && !isNaN(width)){
				left = ($("body").width()-width)/2;
				tip.width(width);
			}else{
				left = ($("body").width()-250)/2;
			}
			tip.css("left",left);
			if(height !== undefined && height !== null && height !=="" && !isNaN(height)){
				tip.height(height);
				tempHeight = -(Number(height) + 13);
			}
			tip.animate({top:temptop+10+"px"},"slow");
			setTimeout(function(){
				tip.animate({top:temptop+"px"},500);
				setTimeout(function(){
					tip.remove();
				},500);
			},time);
		}
		
		/**
		 * 确认框
		 * @method confirm
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Function} fn 
		 * <br/>示例：
		 * <br/>function (yes) { if(yes) alert('11'); });
		 * <br/>"yes"根据你的选择来取值，当你点击"确定"按钮时，yes为true；当你点击"取消"按钮时，yes为false
		 * @param {Object} options json格式，配置文字显示title, buttonOk, buttonCancel
		 * @param {String} hotKeys 字符串，两个按钮的热键,第一个为确认键,第二个为取消键,例如"o,c"表示确认键的快捷键为alt+o,取消为alt+c
		 * @param focus true或flase，为true时焦点在确认按钮上，为flase时焦点在取消按钮上 
		 */
		function confirm(msg,fn,options,hotKeys,focus){
			var $w = $("<div><div class='l-dialog-image l-dialog-image-question'></div><div style='display:block;margin:15px'>"+msg+"</div></div>");
			//var $w = $("<div style='word-break:break-all;overflow:hidden;'><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></div>");
			$w.appendTo($("body"));
			var buttonHotKeys = [];
			if(typeof hotKeys == "string"){
				buttonHotKeys = hotKeys.split(",");
			}
			$w.dialog({
				title: options && options.title? options.title:'确认提示',
				width:350,
				height:150,
				modal:true,
				closable:false,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttonsAlgin:'center',
				buttons:[{
						text: options && options.buttonOk? options.buttonOk:buttonHotKeys[0]?'确定['+buttonHotKeys[0].toUpperCase()+']':'确定',
						buttonHighHlight:true,
						handler:function(){
							$w.dialog('destroy');
							$w.remove();
							if(fn){
								fn(true);
							}
						}
					},{
						text: options && options.buttonCancel?options.buttonCancel:buttonHotKeys[1]?'取消['+buttonHotKeys[1].toUpperCase()+']':'取消',
						handler:function(){
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(false);					
						}
					}
				]
			});
			if(focus){
				$w.find('.dialog-button button:first').focus();
			}else{
				$w.find('.dialog-button button:last').focus();
			}
			//添加热键
			if(buttonHotKeys){
				var buttons = $w.find('.dialog-button button');
				if(buttonHotKeys[0]){
					if(buttonHotKeys[0].length == 1){
						hotKeyregister.add("alt+" + buttonHotKeys[0],function(){buttons.eq(0).focus();buttons.eq(0).click();return false;});
					}else if(buttonHotKeys[0].length > 1){
						hotKeyregister.add(buttonHotKeys[0],function(){buttons.eq(0).focus();buttons.eq(0).click();return false;});
					}
				}
				if(buttonHotKeys[1]){
					if(buttonHotKeys[1].length == 1){
						hotKeyregister.add("alt+" + buttonHotKeys[1],function(){buttons.eq(1).focus();buttons.eq(1).click();return false;});
					}else if(buttonHotKeys[1].length > 1){
						hotKeyregister.add(buttonHotKeys[1],function(){buttons.eq(1).focus();buttons.eq(1).click();return false;});
					}
				}
			}
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var o = e.target || e.srcElement;
					var next = $(o).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var o = e.target || e.srcElement;
					var prev = $(o).prev()[0];
					if(prev)prev.focus();
				}
			});	
			$w.find("div.panel").css("margin",0);
		}

		/**
		 * 接收输入的提示框
		 * @method prompt
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Function} fn 
		 * <br/>示例：
		 * <br/>function (yes,value) { if(yes) alert(value); });
		 * <br/>"yes"根据你的选择来取值，当你点击"确定"按钮时，yes为true；当你点击"取消"按钮时，yes为false。value是你输入的值
		 * @param {String} initValue 初始值
		 */
		function prompt(msg,fn,initValue){
			var $w = $("<div ><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></br><input type=\"text\" style=\"width:300px;margin-left:20px\" id=\"___prompt\" value=\""+(initValue?initValue:"")+"\"/></div>");
			$w.appendTo($("body"));
			$('#___prompt').keydown(function(e){
				if(e.keyCode==13){
					$w.find('.dialog-button button:first').delay(100).focus();
					//setTimeout(function(){$w.find('.dialog-button button:first').focus();},100);
				}
			});
			$w.dialog({
				title:'请输入',
				width:350,
				height:150,
				modal:true,
				closable:false,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttonsAlgin:'center',
				buttons:[{
						text:'确定',
						buttonHighHlight:true,
						handler:function(){
							var v = $('#___prompt').val();
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(true,v);
						}
					},{
						text:'取消',
						handler:function(){
							var v = $('#___prompt').val();
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(false,v);					
						}
					}
				]
			});
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var next = $(e.srcElement).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var prev = $(e.srcElement).prev()[0];
					if(prev)prev.focus();
				}
			});	
			$('#___prompt').delay(100).focus();
		}

		/**
		 * 多个按钮的提示框,
		 * @method buttonsDialog
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Number} width 宽度，不要加px	
		 * @param {Number} height 高度，不要加px	
		 * @param {Object} buttons 
		 * <br/>示例：
		 * <br/>[
		 * <br/> {text:'确定1',
		 * <br/>  handler:function(){}
		 * <br/>  url:'test/testAction!query1.do'},
		 * <br/> {text:'确定2',
		 * <br/>  handler:function(){}
		 * <br/>  url:'test/testAction!query2.do'}
		 * <br/>]
		 */
		//TODO 可以考虑，某些按钮直接传url的时候做一些事情
		function buttonsDialog(msg,width,height,buttons){
			var $w = $("<div style='word-break:break-all;overflow:hidden;'><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></div>");
			$w.appendTo($("body"));
			var yesOrNo = false;
			var _buttons = buttons;
			if(_buttons){
				for(var i=0;i<_buttons.length;i++){
					if(_buttons[i].handler){
						_buttons[i].handler = function(_click){
							return function(){
									$w.dialog('destroy');
									$w.remove();
									_click();							
								};
						}(_buttons[i].handler);
					}
				}
			}
			$w.dialog({
				title:'选择操作',
				width:(width?width:350),
				height:(height?height:250),
				modal:true,
				closable:false,
				buttonsAlgin:'center',
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttons:buttons
			});
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var next = $(e.srcElement).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var prev = $(e.srcElement).prev()[0];
					if(prev)prev.focus();
				}
			});
			$w.find('.dialog-button button:first').delay(100).focus();
		}

		function sendMsgToFrame(type, msg, args){
		    try {
		        var o = {};
		        o.type = type;
		        o.msg = msg;
		        o.args = args;
				o = Ta.util.obj2string(o);
		        window.top.postMessage(o, "*");
		    } 
		    catch (e) {
		  }
		}

		/**
		 * 打开一个首页tab窗口
		 * @method openTabMenu
		 * @param {String} tabid 不可重复，最好取菜单id，若没菜单可自定义
		 * @param {String} title tab名称
		 * @param {String} !url 要访问的地址
		 */
		function openTabMenu(tabid,title,url){
			if (!url)
		   		return;
		   	var tab = $("#tab_" + tabid);
		   	var args = tabid+";"+title+";"+url;
		   	if (tab[0]) {
		   		Base.confirm("页面已打开,是否刷新",function (yes) {
		   			if(yes){
		   				var mainFrame = IndexTab.getTabBd(tabid);
		   				$(mainFrame).attr("src",$(mainFrame).attr("src"));
		   			}
		   			sendMsgToFrame("function", "IndexTab.actTab", tabid+";true");
		   		});
		   	}else{
		   		sendMsgToFrame("function", "IndexTab.addTab", args);
		   	}
		}
		
		/**
		 * 显示boxComponet组建，根据不同的目标对象显示在不同位置
		 * @method showBoxComponent
		 * @param {String} id boxComponet组建的id,必传
		 * @param {String} target 目标对象，为一dom对象，必传
		 */
		function showBoxComponent(id,target) {
			var $target = $(target);
			var bodyHeight = $(document.body).outerHeight(true);
			var bodyWidth = $(document.body).outerWidth(true);
			var $id = $("#"+id);
			var boxHeight = $id.outerHeight(true);
			var boxWidth = $id.outerWidth(true);
			if($target && $target.length == 1) {
				var targetOffset = $target.offset();
				if(targetOffset) {
					var targetTop = targetOffset.top;
					var targetLeft = targetOffset.left;
					var targetHeight = $target.innerHeight();
					var targetWidth = $target.innerWidth();
					var heightDifference = bodyHeight-targetHeight-targetTop;
					var widthDifference = bodyWidth-targetWidth-targetLeft;
					var horizontalP = true,verticalP = true;
					var ap = $id.attr("_position");
					if( ap == "horizontal") {
						heightDifference = bodyHeight - targetTop;
						widthDifference = bodyWidth-targetWidth-targetLeft;
					}else if (ap == "vertical") {
						heightDifference = bodyHeight-targetHeight-targetTop;
						widthDifference = bodyWidth-targetLeft;
					}
//					console.log("bodyWidth:" + bodyWidth +",boxWidth:" + boxWidth +",targetLeft:" + targetLeft + ",targetWidth:" + targetWidth + ",widthDifference:" +widthDifference);
//					console.log("bodyHeight:" + bodyHeight +",boxHeight:" + boxHeight +",targetTop:" + targetTop + ",targetHeight:" + targetHeight + ",heightDifference:" +heightDifference);
//					
					if(heightDifference >= boxHeight) {
						if( ap == "horizontal") {
							$id.css("top",(targetTop + targetHeight - 40 - targetHeight/2));
						}else if(ap == "vertical") {
							$id.css("top",(targetTop + targetHeight + 10));
						}
						verticalP = false;
					} else {
						if( ap == "horizontal") {
							$id.css("top",(targetTop - boxHeight + 50));
						}else if(ap == "vertical") {
							$id.css("top",(targetTop - boxHeight-10));
						}
						verticalP = true;
					}
					if(widthDifference >= boxWidth) {
						if( ap == "horizontal") {
							$id.css("left",targetLeft + targetWidth);
						}else if(ap == "vertical") {
							$id.css("left",targetLeft - 20);
						}
						horizontalP = false;
					} else {
						if( ap == "horizontal") {
							$id.css("left",(targetLeft - boxWidth - 10));
						}else if(ap == "vertical") {
							$id.css("left",(targetLeft - boxWidth + 40));
						}
						horizontalP = true;
					}
					var $boxComponent_b = $("#"+id+" > b");
					if( ap == "horizontal") {
						if( verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_rightBottom");
						}else if( verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_leftBottom");
						}else if( !verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_rightTop");
						}else if( !verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_leftTop");
						}
					}else if( ap == "vertical") {
						if( verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_bottomRight");
						}else if( verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_bottomLeft");
						}else if( !verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_topRight");
						}else if( !verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_topLeft");
						}
					}
				}
			}
			$id.show();
		}
		function removeClass_b(o){
			o.removeClass("boxComponent_b_topLeft boxComponent_b_topRight boxComponent_b_bottomLeft boxComponent_b_bottomRight boxComponent_b_leftTop boxComponent_b_rightTop boxComponent_b_leftBottom boxComponent_b_rightBottom");
		}
	}
}));
