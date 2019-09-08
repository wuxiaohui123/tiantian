(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function init(target){
		$(target).addClass('validatebox-text');
	}
	
	function clearValidate(target){
		$(target).removeClass('validatebox-invalid');
		$(target).parents("div.fielddiv2").css("borderColor","");
		hideTip(target);
	}
	
	function bindEvents(target){
		var box = $(target);
		var tip = $.data(target, 'validatebox').tip;
		if(box.hasClass('checkboxgroup') || box.hasClass('radiogroup')){//针对checkboxgroup
			//box.find(":input:visible[type=checkbox]").unbind('.validatebox').bind(
			box.find(".ta_pw_chkbox").unbind('.validatebox').bind(//pengwei,新checkbox验证
				'click.validatebox',function(){
					if(!validate(target)){
					  showTip(target);
					}
				}
			);
			//box.find(":input:visible[type=radio]").unbind('.validatebox').bind(
			box.find(".ta_pw_radio").unbind('.validatebox').bind(//pengwei,新radio验证
					'click.validatebox',function(){
						if(!validate(target)){
							showTip(target);
						}
					}
			);
			box.find('>label.fieldLabel').unbind('.validatebox').bind('click.validatebox',
				function(){
					if(!validate(target))
						showTip(target);
				}
			);
		}else{
			box.unbind('.validatebox').bind('focus.validatebox', function(){
				var valid = validate(target);
				if(!valid){
					showTip(target);
				}
			}).bind('blur.validatebox', function(){
				validate(target);
				
				hideTip(target);
			}).bind('mouseover.validatebox', function(){
				if (box.hasClass('validatebox-invalid')){
					showTip(target);
				}
			}).bind('mouseout.validatebox', function(){
				if(document.activeElement!=target){
					hideTip(target);
				}
			}).bind('keyup.validatebox',function(){
				if(!validate(target))
					showTip(target);
			});
		}
	}
	
	/**
	 * show tip message.
	 */
	function showTip(target){
		var $input = $(target);
		if($input.hasClass("ffb-input"))return;//让下拉框没有tip提示
		var box;
		if($input.hasClass("checkboxgroup")) {
			box = $input;
		} else {
			box = $input.parent("div.fielddiv2");
		}
		if(box && box.length < 1){//表格验证
			box = $input.parent("div.slick-cell");
		}
		var msg = $.data(target, 'validatebox').message;
		if(msg == $.fn.validatebox.defaults.missingMessage)return;//liys 20140904 如果为默认的提示信息（此项为必输项），不显示
		var tip = $.data(target, 'validatebox').tip;
		if (!tip){
			tip = $(
				'<div class="validatebox-tip">' +
					'<div class="validatebox-tip-content  ui-corner-all ffb_163">' +
					'</div>' +
					'<div class="validatebox-tip-pointer">' +
					'</div>' +
				'</div>'
			).appendTo('body');
			$.data(target, 'validatebox').tip = tip;
		}
		var $c = tip.find('.validatebox-tip-content');
		$c.html(msg);
		var cheight = tip.outerHeight(true);
		if(cheight==0) cheight =17;
		
		var _left = box.offset().left+10;
		var _top = box.offset().top;
		//lins 修改top遮挡问题
//		if (_top > cheight - 3)
//			_top = box.offset().top - cheight;
//		else 
//			_top = box.offset().top + cheight;
		var isright = ($(window).width()-_left)<150?true:false;
		if(_top > cheight - 2){//输入框上边显示
			if(isright){
				var tmp = tip.find('div.validatebox-tip-pointer-right');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-right\">").insertAfter($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}else{
				var tmp = tip.find('div.validatebox-tip-pointer');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer\">").insertAfter($c);
				//tip.find('div.validatebox-tip-pointer').remove();		
			}
			_top = _top - cheight;
		}else{//输入框下边显示
			if(isright){
				var tmp = tip.find('div.validatebox-tip-pointer-topright');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-topright\">").insertBefore($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}else{
				var tmp = tip.find('div.validatebox-tip-pointer-topleft');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-topleft\">").insertBefore($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}
			_top = _top + box.outerHeight(true);
		}
		tip.css({
			display:'block',
			left:_left ,
			top:_top
		});
		$.data(target, 'validatebox').tip =tip;
		//liys 显示1s后消失
		setTimeout(function(){
			hideTip(target)
		}, 1000);
	}
	
	/**
	 * hide tip message.
	 */
	function hideTip(target){
		var data = $.data(target, 'validatebox');
		if (data && data.tip){
			data.tip.fadeOut(1000);
			setTimeout(function(){
				if(data.tip){
					data.tip.remove();
				}
				data.tip = null;
			}, 1000)
		}
	}
	function makeInvalid(target,tipMessage){
		$(target).addClass('validatebox-invalid');
		$(target).parents("div.fielddiv2").css("borderColor","#c30");
		setTipMessage(target,tipMessage);
	}
	function setTipMessage(target,msg){
		var cachedata = $.data(target, 'validatebox');
		if(!cachedata){
			$.data(target, 'validatebox',{message:msg});
		}else{
			$.data(target, 'validatebox').message = msg;
		}
	}	
	/**
	 * do validate action
	 */
	function validate(target){
		var data = $.data(target, 'validatebox');
		if(!data)return true;
		var opts = data.options;
		var tip = data.tip;
		var value,pSize;
		var box = $(target);
		if (target.id != "") 
			pSize = $("#" + target.id).hasClass("amountfield");
		else	
			pSize = $(target).hasClass("amountfield");
		if ( pSize) {
			value = $("#" + target.id + "_hidden").val();			
		} else {
			value = box.val();
		}
		if(box.hasClass('checkboxgroup') || box.hasClass('radiogroup')){
			value = Base.getValue(target.id);
		}
		
//		$("#console").html($("#console").text()+'</br>,validdate');

		
		// if the box is disabled, skip validate action.
		var disabled = box.attr('disabled'),readonly = box.attr('readOnly');
//		if (disabled == true || disabled == 'true' || readonly==true || readonly=='true'){
//			return true;
//		}
		
		//更改对readonly不做验证的提交判断，改为要验证
		if (disabled == true || disabled == 'true' ){
			return true;
		}
		if (opts.required){
			if (value == '' || (value && value.length && value.length==0) || !value){//value.length==0 针对checkboxgroup的判断
				makeInvalid(target,opts.missingMessage);
				return false;
			}
		}
		if (opts.validType){
			if(opts.validType == "self"){//自定义验证，liys
				if(typeof eval(opts.validFunction) == "function"){
					var selfResults = eval(opts.validFunction)();
					makeInvalid(target,opts.invalidMessage || selfResults.message);
					return selfResults.result;
				}
			}
			var result = /([a-zA-Z_]+)(.*)/.exec(opts.validType);
			var rule = opts.rules[result[1]];
			if (value && rule){
				var param = eval(result[2]);
				if (!rule['validator'](value, param)){
//					box.addClass('validatebox-invalid');
					
					var message = rule['message'];
					if (param){
						for(var i=0; i<param.length; i++){
							message = message.replace(new RegExp("\\{" + i + "\\}", "g"), param[i]);
						}
					}
//					setTipMessage();
//					showTip(target);
					makeInvalid(target,opts.invalidMessage || message);
					return false;
				}
			}
		}
		
		box.removeClass('validatebox-invalid');
		box.parents("div.fielddiv2").css("borderColor","");
		hideTip(target);
		return true;
	}
	function setRequired(target,required){
		var $obj = $(target);
		if(required){
			$obj.attr('required',"true");
			var d = $.data(target, 'validatebox');
			if(d){
				d.options.required = required;
			}else{
				$obj.validatebox();
				$obj.validatebox('clear');
			}
		}else{
			$obj.removeAttr('required');
			var d = $.data(target, 'validatebox');
			if(d){
				d.options.required = false;
			}
			$obj.validatebox('clear');
		}
	}
	$.fn.validatebox = function(options,param){
		if (typeof options == 'string'){
			switch(options){
				case 'clear':
					return this.each(function(){
						clearValidate(this);
					});
				case 'validate':
					return this.each(function(){
						validate(this);
					});
				case 'isValid':
					return validate(this[0]);
				case 'makeInvalid':
					return makeInvalid(this[0],param);
				case 'setRequired':
					return setRequired(this[0],param);				
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'validatebox');
			if (state){
				$.extend(state.options, options);
			} else {
				init(this);
				var t = $(this);
				state = $.data(this, 'validatebox', {
					options: $.extend({}, $.fn.validatebox.defaults, {//LINS ie8下request=“true”失效 添加t.attr('required') == 'required'
						required: (t.attr('required') ? (t.attr('required') == 'true' || t.attr('required') == 'required' || t.attr('required') == true) : undefined),
						validType: (t.attr('validType') || undefined),
						missingMessage: (t.attr("toolTip") || t.attr('missingMessage') || undefined),//liys 829 如果设置了toolTip，则不再提示默认验证错误信息，而是提示toolTip中的信息
						invalidMessage: (t.attr('invalidMessage') || undefined), 
						validFunction: (t.attr('validFunction') || undefined)
					}, options)
				});
			}
			
			bindEvents(this);
			
		});
	};
	
	$.fn.validatebox.defaults = {
		required: false,
		validType: null,
		missingMessage: '此项为必输项!',
		invalidMessage: null,
		
		rules: {
			email:{
				validator: function(value){
					return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(value);
				},
				message: '您输入的不符合email格式要求'
			},
			url: {
				validator: function(value){
					return /^(https?|ftp|www):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
				},
				message: '您输入的不合URL格式要求 例如 http://www.baidu.com'
			},
			length: {
				validator: function(value, param){
					//var len = $.trim(value).length;
					
					var len = (value + "").replace(/[^\x00-\xff]/gmi,'pp').length;
					return len >= param[0] && len <= param[1];
				},
				message: '输入的字符长度必须在 {0} 和 {1}之间'
			},
			chinese:{
				validator:function(value){
				  return /^[\u4e00-\u9fa5]+$/.test(value);
				},
				message: '只能输入中文'
			},
			//sss
			tel:{
				validator:function(value){
				  return /^((0\d{2,3}-\d{7,8})|(1[35847]\d{9}))$/.test(value);
				},
				message: '只能为11位手机号或11位固话,格式XXX-XXXXXXXX或XXXX-XXXXXXX'
			},
			cell:{
				validator:function(value){
				  return /^(0\d{2,3}-\d{7,8})$/.test(value);
				},
				message: '只能11位固话格式,格式为XXX-XXXXXXXX或XXXX-XXXXXXX'
			},
			//sss
			date:{
				validator:function(value,param){
					
					this.message= '按n键自动输入当前时间';
					if(!Ta.util.isDate(value))return false;
					
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期必须在 {0} 到 {1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的日期格式不正确,按n键自动输入当天日期'
			},
			datetime:{
				validator:function(value,param){
					this.message = "您输入的日期时间格式不正确,按n键自动输入当前时间"; 
					if(!Ta.util.isDateTime(value))return false;
					this.message = "输入的日期时间必须在{0}到{1}之间";
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期时间必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期时间必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期时间必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的日期时间格式不正确,按n键自动输入当前时间'
			},
			issue:{
				validator:function(value,param){
					var bformat = /^\d{4}((0[1-9])|(1[0-2]))$/.test(value);
					this.message = "您输入的期号不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					this.message = "输入的期号必须在 {0} 到 {1}之间";
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的期号必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的期号必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的期号必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的期号不正确'
			},
			dateMonth:{
				validator:function(value,param){
					var bformat = /^\d{4}-((0[1-9])|(1[0-2]))$/.test(value);
					this.message = "您输入的年月不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					this.message = "输入的年月必须在 {0} 到 {1}之间";
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的年月必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的年月必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的年月必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的年月不正确'
			},
			dateYear:{
				validator:function(value,param){
					var bformat = /^\d{4}/.test(value);
					this.message = "您输入的年份不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期必须在 {0} 到 {1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的年份不正确'
			},
			zipcode:{
				validator:function(value){
					return /[0-9]\d{5}(?!\d)/.test(value); //修改/[1-9]\d{5}(?!\d)/.test(value);
				},
				message: '您输入的邮编不正确'
			},
			mobile:{
				validator:function(value){
					return /^1[3|4|5|7|8][0-9]\d{8}$/.test(value);
				},
				message: '您输入的手机号码格式不正确'
			},
			ip:{
				validator:function(value){
					return /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/.test(value);
				},
				message: '您输入的IP地址格式不正确'
			},
			integer:{
				validator:function(value){
					return /^-?[1-9]\d*$/.test(value);
				},
				message: '只能输入整数'
			},
			number:{
				validator:function(value,param){
					if(isNaN(value)){
						return false;
					}
					if(!jQuery.isArray(param))return true;
					if(param[0]==='' && param[1]!==''){
						this.message = "输入的数值必须小于或等于{1}";
						return Number(value) <= Number(param[1]);
					}
					else if(param[0] !== '' && param[1]===''){
						this.message = "输入的数值必须大于或等于{0}";
						return Number(value) >= Number(param[0]);
					}
					else{
						this.message = "输入的数值必须在{0}到{1}之间";
						return(Number(value) >= Number(param[0])) && (value <= Number(param[1]));
					}
					return true;
				},
				message: '只能输入数字'
			},
			checkboxgroup:{
				validator:function(value,param){
					var length = value.length;
					return length <= param[1] && length >= param[0] ;
				},
				message: '选的个数必须在{0} 到 {1}之间'
			},
			compare:{
				validator:function(value,param){
					var targetValue = Base.getValue(param[1]);
					var targetLabel = Base.getFieldLabel(param[1]);
					if(!isNaN(value) && !isNaN(targetValue)){//为数字时，转换成数字再比较
						value = Number(value);
						targetValue = Number(targetValue);
					}
					switch(param[0]){
						case '=':
							this.message = "您输入的值必须与["+targetLabel+"]相同";
							return value == targetValue;
						case '>':
							this.message = "您输入的值必须大于["+targetLabel+"]";
							return value > targetValue;
						case '<':
							this.message = "您输入的值必须小于["+targetLabel+"]";
							return value < targetValue;
						case '>=':
							this.message = "您输入的值必须大于或等于["+targetLabel+"]";
							return value >= targetValue;
						case '<=':
							this.message = "您输入的值必须小于或等于["+targetLabel+"]";
							return value <= targetValue;
						case '!=':
							this.message = "您输入的值不能等于["+targetLabel+"]";
							return value != targetValue;
					}
					return true;
				},
				message: '与其他输入项不匹配'
			},
			idcard:{
				validator:function(value){
						var sId = value;
						
						if (sId.length == 15) {
							if(!/^\d{14}(\d|x)$/i.test(sId)){
								this.message =  "你输入的身份证长度或格式错误";
								return false;
							} else  {
							    sId=sId.substr(0,6)+'19'+sId.substr(6,9);
							    sId+= getVCode(sId);
							}
						}
						function getVCode(CardNo17) {
						  var Wi = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
						  var Ai = new Array('1','0','X','9','8','7','6','5','4','3','2');
						  var cardNoSum = 0;
						  for (var i=0; i<CardNo17.length; i++)cardNoSum+=CardNo17.charAt(i)*Wi[i];
						  var seq = cardNoSum%11;
						  return Ai[seq];
						}
						var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"} ;

						var iSum=0 ;
						var info="" ;
						if(!/^\d{17}(\d|x)$/i.test(sId)){
							this.message =  "你输入的身份证长度或格式错误";
							return false;
						}
						sId=sId.replace(/x$/i,"a"); 
						if(aCity[parseInt(sId.substr(0,2))]==null){ 
							this.message =  "你的身份证地区非法";
							return false;
						}
						sBirthday=sId.substr(6,4)+"-"+Number(sId.substr(10,2))+"-"+Number(sId.substr(12,2)); 
						var d=new Date(sBirthday.replace(/-/g,"/")) ;
						if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate())){
							this.message =  "身份证上的出生日期非法";
							return false; 
						}
						for(var i = 17;i>=0;i --) iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11) ;
						if(iSum%11!=1){
							this.message =  "你输入的身份证号非法";
							return false; 
						}
						return true;
				},
				message: '您输入的身份证号非法'
			}
		}
	};
}));