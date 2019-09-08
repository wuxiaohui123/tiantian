(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", 
		        "ta.jquery.ext", 
		        "border" ,
		        "TaUIManager", 
		        "tauipanel", 
		        "fit", 
		        "resizable",
		        "validateBox", 
		        "api.datagrid",
				"api.fieldset",
				"api.forms",
				"api.panel",
				"api.print",
				"api.selectinput",
				"api.taajax",
				"api.tabs",
				"api.tree",
				"api.window",
				"numberBox",
				"menu",
				"tauitabs",
				"moneyInput",
				"autoPercentHeight",
				"hotkeys"], factory);
	} else {
		factory(jQuery);
	}
}(function ($){
    $.fn.taLayout = function (p) {
    	var $container = $(this);
    	function dolayout($element){
    		var layout=$element.attr("layout");
    		if(layout){
	    		if(layout=="border"){
		    		var layoutCfg = $element.attr('layoutCfg');
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		$element.ligerLayout(layoutCfg);
		    	}else if(layout=="tabs"){
		    		var layoutCfg = $element.attr('layoutCfg');  
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		
		    		$element.tauitabs(layoutCfg || {});
		    		
	    		}else if(layout=="accordion"){
	    			
		    		var layoutCfg = $element.attr('layoutCfg');  
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		layoutCfg = $.extend({}, layoutCfg || {}, {fillSpace:	true});
		    		$element.accordion(layoutCfg || {});
		    		
		    		//TODO 改到组件里面去
		    		$(window).resize(function(){
						setTimeout(function(){
							$element.accordion('resize');
						},50);
					});
	    		}else if(layout=="column"){	//  
		    		var cols = $element.attr('cols');  
		    		if(cols && cols>=1){
						var elements = $element.children().not('#pageloading,legend,li,script');
						for(var i = 1;i <= (elements.length); i++){
							var e = $(elements[i-1]);
							if(e.attr("fit")){
								e.tauifitheight();
							}
						}
						//$element.append("<div style=\"clear:both\"></div>")
		    		}
		    	 }
		    	
	    	 }//布局结束 
	    	 //当表单对象被column容器布局的时候如果没有分列，那么fielddiv的margin就要减少
	    	 if($element.hasClass('fielddiv') && (!$element.parent().hasClass('ez-fl') && $element.parent().attr('cols')==null)){
	    	 	$element.css('margin',"6px 2px");
	    	 }
	    	 
    	 	 //创建panel
    	 	if($element.hasClass('panel') && !$element.hasClass('window')){
				$element.tauipanel();
				return;
    	 	}
    	 	if(($element.hasClass('grid') || $element.hasClass('datagrid'))&& ($element.attr('fit')=='true' || $element.hasClass('ez-fl')) && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	if($element[0].tagName.toUpperCase()=='FORM' && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	if($element[0].tagName.toUpperCase()=='TABLE' && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	//pengwei 对于checkboxgroup和radiogroup单击做处理，全选或取消
    	 	if($element.hasClass('checkboxgroup')){
    	 		$element.find('>label.fieldLabel').bind("click",function(){
    	 			if(!$(this).hasClass("checkAll")){
    	 				$element.find('input[type=checkbox]').not("[readonly]").not("[disabled]").each(function(){
    	 					this.checked = true;
    	 					$(this).parent().removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 				});
    	 				$(this).addClass("checkAll");
    	 			}else{
    	 				$element.find('input[type=checkbox]').not("[readonly]").not("[disabled]").each(function(){
    	 					this.checked = false;
    	 					$(this).parent().removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
    	 				});
    	 				$(this).removeClass("checkAll");
    	 			}
    	 		});
    	 		return;
    	 	}
    	 	
    	 	//pengwei 修改后的checkbox,采用切换图片方式
    	 	if($element.hasClass('ta_pw_chkbox')){
    	 		var $in = $element.find("input[type=checkbox]").eq(0);
    	 		if($in.attr("checked") == "checked"){
    	 			if($in.attr("disabled") == "disabled" || $in.attr("readOnly") == "readonly"){
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked-disabled");
    	 			}else{
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 			}	
    	 		}else{
    	 			if($in.attr("disabled") == "disabled" || $in.attr("readOnly") == "readonly")
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-uncheck-disabled");
    	 		}
    	 		
    	 		$element.bind("mousedown.checkbox",function(e){
    	 			if(e.which == 3)return;
    	 			if($in.attr("readOnly") == "readonly" || $in.attr("disabled") == "disabled")return;
    	 			
    	 			input = $element.find("input")[0];
    	 			if($element.hasClass("ta-chk-uncheck")){
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 				input.checked=true;
    	 			}else{
    	 				$element.removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
    	 				input.checked=false;
    	 			}
					
					var fun = $(this).attr("_onClick");
    	 			if(fun != null && fun != "")
    	 				eval(fun);
    	 		});
    	 		return;
    	 	}
    	 	
    	 	if($element.hasClass('radiogroup')){
    	 		$element.find('>label.fieldLabel').click(function(){
    	 			$element.find('input[type=radio]')
    	 				.not("[disabled]")
					    .each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .addClass("ta-radio-uncheck");
    	 		});
    	 		return;
    	 	}
    	 	
    	 	//pengwei 修改后的radio
    	 	if($element.hasClass('ta_pw_radio')){
    	 		var input = $element.find("input[type=radio]")[0];
    	 		var name = $(input).attr("name");
    	 		if(input.checked){
    	 			$("input[name=\""+name+"\"]")
	    	 			.each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .addClass("ta-radio-uncheck");
    	 			
    	 			input.checked=true;
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled"){
    	 				$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked-disabled");
    	 			}else{
    	 				$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked");
    	 			}	
    	 		}else{
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled")
    	 				$element.addClass("ta-radio-uncheck-disabled");
    	 		}
    	 		$element.bind("mousedown.radio",function(e){
    	 			if(e.which == 3)return;
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled")return;
    	 			$("input[name=\""+name+"\"]")
					    .each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .removeClass("ta-radio-checked-disabled")
					    .addClass("ta-radio-uncheck");
    	 			
    	 			input.checked = true;
    	 			$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked"); 
					//修复：readonly时，不触发onclick事件
					var fun = $(this).attr("_onClick");
    	 			if(fun != null && fun != "")
    	 				eval(fun);
    	 		});
    	 		return;
    	 	}
    	 	
    	 	
    	 	//对fieldset点击效果
//    	 	if($element[0].tagName=='FIELDSET'){
//		    	$(">legend",$element).toggle(
//		    		function(){
//						$(this).siblings().hide();
//						var _this = this;
//						setTimeout(function(){
//							$(_this.parentNode).siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
//						},100);
//					},function(){
//						$(this).siblings().show();
//						var _this = this;
//						setTimeout(function(){
//							$(_this.parentNode).siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
//						},100);
//					}
//				);
//    	 	}
    	}
    	
    	
	    var c = $container;
	    //删除body里面的script
	    //c.find("script").remove();
	    
    	dolayout(c);
    	function  bianli(d){
    		var tmp = d.children();
    		if(tmp[0]&&tmp[0].tagName == 'TEXTAREA'){
		    		tmp[0].value = tmp[0].value.replaceAll("<br>","\r\n");
		    }
    		/*if(Base.globvar.developMode){
    			if(tmp.filter('[fit=true]').length>1){
    				alert('请注意:同级容器面板只能有一个容器面板fit设置为true');
    			}
    		}*/
    		if(tmp.length>0){
    			var $fitFistEl = null;
	    		for(var i=0;i<tmp.length;i++){
	    			var tn = tmp[i].tagName && tmp[i].tagName.toLowerCase();
					if("div,body,form,fieldset,table,tbody,tr,td".indexOf(tn)==-1) {
						continue;
					} else {
						var $tmp = $(tmp[i]);
						if ($fitFistEl == null && $tmp.attr("fit") == "true") {
							$fitFistEl = $tmp;
						} else if ($tmp.attr("fit") == "true") {
							$tmp.css("height", $fitFistEl.height());
							//$tmp.removeAttr("fit");
						}
						
	    				dolayout($tmp);
					}
	    			
	    			bianli($(tmp[i]));
	    		}
    		}
    	}
    	bianli(c); 
    	
    	//插件树，表格，下拉框
		if(Ta.core.TaUICreater){
			Ta.core.TaUICreater.create();
		}
    	//init begin
		
		//延迟加载iframe里面 的内容
		/*setTimeout(function(){
			$('iframe',$container).each(function(){
					if($(this).attr('src1'))
						$(this).attr('src',$(this).attr('src1'));
			});
		},50);
		*/
		
		//init end	

		//输入框获取焦点时label变色
		var _fields = $(":input[type!=hidden]",$container);
	    _fields.not('[type=button]').hover(function(){
	    	if($(this).hasClass('ffb-input_163')){
	    		$(this.parentNode.parentNode).addClass('inputHover');
	    	}else{
	    		$(this.parentNode).addClass('inputHover');
	    	}
	    },function(){
	    	if($(this).hasClass('ffb-input_163')){
	    		$(this.parentNode.parentNode).removeClass('inputHover');
	    	}else{
	    		$(this.parentNode).removeClass('inputHover');
	    	}
	    }).focus(function(){
	    	if(Base.globvar.indexStyle == "default"){
	    		if($(this).hasClass('ffb-input_163')){
					$(this.parentNode.parentNode).addClass('inputFocus');
				}else{
					if(this.type=='checkbox'){
						$(this).next().addClass('labelFocus');
						return;
					}
//					if($(this.parentNode).hasClass("fielddiv"))return;
//					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
					else{
						$(this.parentNode).addClass('inputFocus');
					}
				}
	    	}else{
	    		$(this).addClass('inputFocusBorder');
				if($(this).hasClass('ffb-input')){
					$(this.parentNode.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
				}else{
					if(this.type=='checkbox'){
						$(this).next().addClass('labelFocus');
						return;
					}
					if($(this.parentNode).hasClass("fielddiv"))return;
					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
				}
	    	}
		}).blur(function(){
			if(Base.globvar.indexStyle == "default"){
				if($(this).hasClass('ffb-input')){//下拉框
					$(this.parentNode.parentNode).removeClass('inputFocus');
				}else{				
					if(this.type=='checkbox'){
						$(this).next().removeClass('labelFocus');
						return;
					}else{
						$(this.parentNode).removeClass('inputFocus');
					}
				}
			}else{
				$(this).removeClass('inputFocusBorder');
				if($(this).hasClass('ffb-input')){//下拉框
					$(this.parentNode.parentNode.parentNode).find("label[for='"+this.id+"']").removeClass("labelFocus");
				}else{				
					if(this.type=='checkbox'){
						$(this).next().removeClass('labelFocus');
						return;
					}
					if($(this.parentNode).hasClass("fielddiv"))return;
					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").removeClass("labelFocus");
				}
			}
			Ta.util.InputPositon.remove();//字数提示删除
		}).filter('[type=text],[type=textarea]').keyup(function(){
			Ta.util.InputPositon.show(this);//字数提示
		});
		
		//对text,textarea,password,checkbox,radio,button,checkboxgroup,radiogroup做一些处理
		_fields.add("div.checkboxgroup,div.radiogroup",$container).each(function(){
			//对输入框加入校验，必须在TaUICreater之后
			var _$this = $(this);
			if(_$this.attr('required') || _$this.attr('validType'))
				_$this.validatebox();
			//创建numberfield
			if(_$this.hasClass('numberfield')){
				
				_$this.numberbox();
				if(_$this.hasClass("amountfield")){//创建金额输入框
				    var t = {numberRound:_$this.attr('numberRound'),decimalPlace:_$this.attr('precision'), symbol:_$this.attr('amountPre')};
					_$this.moneyInput(t);
				}
			}
			
			//创建日期输入
			if(_$this.hasClass('datefield')){
				_$this.datetimemask(1);
			}
			//创建日期时间输入
			if(_$this.hasClass('datetimefield')){
				_$this.datetimemask(2);
			}
			//创建期号输入
			if(_$this.hasClass('issuefield')){
				_$this.datetimemask(3);
			}
			//创建年月输入
			if(_$this.hasClass('dateMonthfield')){
				_$this.datetimemask(5);
			}
			//创建年输入
			if(_$this.hasClass('dateYearfield')){
				_$this.datetimemask(6);
			}
			//创建年输入
			if(_$this.hasClass('dateFulltimefield')){
				_$this.datetimemask(7);
			}
			//如果是button添加左右键的支持
			if(this.type=='button'||this.type=="submit"){
				$(this).focus(function(){
					var _this = this;
					$(this).bind('keydown.leftright',function(event){
						if(event.keyCode==37){
							var f = Base._getPreFormField(_this.id);
							if(f && f.id)Base.focus(f.id);
						}else if(event.keyCode==39){
							var f = Base._getNextFormField(_this.id);
							if(f && f.id)Base.focus(f.id);
						}
					});
					$("span.button_span",this).addClass("button_focus");
				}).blur(function(){
					$(this).unbind('keydown.leftright');
					$("span.button_span",this).removeClass("button_focus");
				}).hover(function(){
					$("span.button_span",this).addClass("button_hover");
				},function(){
					$("span.button_span",this).removeClass("button_hover");
				});
				//对tabs下面的button不进行热键注册,此类button在jquery91.tauitabs.js中进行注册
				var parentIsTabs = _$this.parents("div[layout='tabs']").length;
				//对可见且不是disabled的按钮注册热键
				if(!_$this.is(':hidden') && !this.disabled && !(parentIsTabs > 0)){
					var _this = this;
					var hotKey = $(this).attr('hotKey');
					if(hotKey && hotKeyregister){
						hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
					}
				}
			}
			if(this.type=='checkbox' && _$this.attr('readOnly')==true){
//				$(this).bind('click.checkboxReadOnly',function(){
//					this.checked = !this.checked;
//				});
				Base._setReadOnly(this.id,true);
			}
			if(this.id){
				Ta.core.TaUIManager.register(this.id,this);
			}
		});
    };

}));