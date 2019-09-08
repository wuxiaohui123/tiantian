(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	function fixValue(target){
		var opts = $.data(target, 'numberbox').options;
		var val = null;
		if (opts.numberRound == true || opts.numberRound == "true") {
			val = parseFloat($(target).val()).toFixed(opts.precision);
		} else {
			var nStr = $(target).val();
			var dw = nStr.indexOf(".");
			if(nStr === ''){
				val = '';
			}else if (dw == -1) {
				val = Number(nStr);
			}
			else val = Number(nStr.substring(0, dw + opts.precision + 1));
		}	
		
		if (isNaN(val)){
			$(target).val('');
			return;
		}
		
		val = val.toString();
		//去除小数点0结尾的数字;
//		var dotIndex = val.indexOf(".");
//		if (dotIndex != -1){
//			var dotL = val.substring(0, dotIndex);
//			var dotR = val.substring(dotIndex, val.length);
//			if (dotR.indexOf("0") != -1){
//				for (var i=dotR.length-1; i>0; i--){
//					var c = dotR.charAt(i); 
//					if (c === "0"){
//						dotR = dotR.slice(0, i);
//					} else {
//						break;
//					}
//				}
//			}
//			dotR = (dotR === "." ? "" : dotR);
//			val = dotL + dotR;
//		}
		
		if (opts.min != null && opts.min != undefined && val < opts.min){
			$(target).val(opts.min.toFixed(opts.precision));
		} else if (opts.max != null && opts.max != undefined && val > opts.max){
			$(target).val(opts.max.toFixed(opts.precision));
		} else {
			$(target).val(val);
		}
		
	}
	
	function bindEvents(target){
		$(target).unbind('.numberbox');
		//console.log($(target).attr('precision'));
		$(target).bind('keypress.numberbox', function(e){
			//alert(e.which);
			if (($(this).attr('precision') == undefined || $(this).attr('precision') == 0) && e.which == 46) return false;
			if ($(this).val().indexOf(".") != -1 && $(this).val().length - $(this).val().indexOf(".") > $(this).attr('precision')) {
				return false;
			}
			if (e.which == 45){	//-
				//只能输入一个"-"号
				if ($(this).val().indexOf("-") != -1) return false;
				//没有"-"号时,当用户输入"-"号,则在最前面添加"-"号
				else {
					$(this).val("-"+$(this).val());
				}
				//return true;
			}
			if($(this).attr('max')){//达到或者超出最大值时，鼠标选中可更改
				/*if(Number($(this).val()) >= Number($(this).attr('max'))){
					return false;
				}*/
			}
			if (e.which == 46) {	//.
				if ($(this).val().indexOf(".", $(this).val().indexOf(".")) != -1) return false;
				return true;
			}else if ((e.which >= 48 && e.which <= 57 && e.ctrlKey == false && e.shiftKey == false) || e.which == 0 || e.which == 8) {
				return true;
			} else if (e.ctrlKey == true && (e.which == 99 || e.which == 118)) {
				return true;
			}  else {
				return false;
			}
		}).bind('paste.numberbox', function(event, a){
			return true;
			//由于chrome兼容性问题，chrome不支持clipboardData，故注释下面的
//			if (window.clipboardData) {
//				var s = clipboardData.getData('text');
//				if (! /\D/.test(s)) {
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
		}).bind('dragenter.numberbox', function(){
			return false;
		}).bind('blur.numberbox', function(){
			fixValue(target);
		}).bind('keydown.numberbox',function(e){ 
			if(e.keyCode==13){ 
				fixValue(target); 
			} 
		});
	}
	
	/**
	 * do the validate if necessary.
	 */
	function validate(target){
		if ($.fn.validatebox){
			var opts = $.data(target, 'numberbox').options;
			$(target).validatebox(opts);
		}
	}
	
	function setDisabled(target, disabled){
		var opts = $.data(target, 'numberbox').options;
		if (disabled){
			opts.disabled = true;
			$(target).attr('disabled', true);
		} else {
			opts.disabled = false;
			$(target).removeAttr('disabled');
		}
	}
	
	$.fn.numberbox = function(options,value){
		if (typeof options == 'string'){
			switch(options){
			case 'disable':
				return this.each(function(){
					setDisabled(this, true);
				});
			case 'enable':
				return this.each(function(){
					setDisabled(this, false);
				});
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'numberbox');
			if (state){
				$.extend(state.options, options);
			} else {
				var t = $(this);
				state = $.data(this, 'numberbox', {
					options: $.extend({}, $.fn.numberbox.defaults, {
						disabled: (t.attr('disabled') ? true : undefined),
						min: (t.attr('min')=='0' ? 0 : parseFloat(t.attr('min')) || undefined),
						max: (t.attr('max')=='0' ? 0 : parseFloat(t.attr('max')) || undefined),
						precision: (parseInt(t.attr('precision')) || undefined),
						numberRound : (t.attr('numberRound'))
					}, options)
				});
				t.removeAttr('disabled');
				$(this).css({imeMode:"disabled"});
			}
			//Base.setValue()时,设值
			if(value !== undefined){
				$(this).val(value);
			}
			setDisabled(this, state.options.disabled);
			fixValue(this);
			bindEvents(this);
			validate(this);
		});
	};
	
	$.fn.numberbox.defaults = {
		disabled: false,
		min: null,
		max: null,
		precision: 0,
		numberRound : 'true'
	};
}));