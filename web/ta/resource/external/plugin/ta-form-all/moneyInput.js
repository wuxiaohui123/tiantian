(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	// 扩展moneyInput到jQuery实例对象中
	$.fn.extend({
		moneyInput:moneyInput
	});
	/**
	 * @params
	 *		options(object)	可选，{decimalPlace:2, symbol:"$", cnBox:"#cnBox"}
	 *		                ，当值为'getValue'时，返回隐藏文本框的值；当值为'setValue'时	     		     *      				，value参数为必须，值为设置文本框和隐藏文本框的值。
	 *		value(string)	可选，和options为'setValue'时配套使用
	 */
	function moneyInput(options, value){
		var scope = this;
		// 显示转换后的大写金额的DOM容器
		var $cnBox;
		// 创建的隐藏文本框的JQUERY对象
		var $hiddenInput;
		// 当前调用的jquery包装的input对象
		var $container;
		// 当前调用的input的DOM节点
		var $input;
		// 隐藏文本框的DOM节点
		var $hidden;
		// 当前input对象的DOM ID
		var container;
		// 用户实际输入的金额
		var money;
		// 货币符号
		var _symbol;
		// 小数位数
		var _decimal;
		// 是否显示中文转换金额
		var showCnMoney = false;
		
		// 控件初始化
		function init(){
			var _options = options;
			var _cnBox = _options.cnBox;
			if (_cnBox != undefined && typeof _cnBox === "string" && _cnBox !== ""){
				if (_cnBox.indexOf("#") === -1){
					_cnBox = "#" + _cnBox;
				}
				$cnBox = $(_cnBox);
				if ($cnBox.length < 1){
					throw new Error("cnBox: " + _cnBox + " does not exist.");
				}
				showCnMoney = true;
			}
			
			$container = scope;
			container = scope[0].id || "";
			if (container === "" || $container.length < 1){
				throw new Error("moneyInput container: " + container + " does not exist.");
			}
			$input = $container.get(0);
			$($input).bind('keypress.moneybox', function(e){
				if ($(this).attr('precision') == 0 && e.which == 46) return false;
				if ($(this).val().indexOf(".") != -1 && $(this).val().length - $(this).val().indexOf(".") > $(this).attr('precision')) {
					return false;
				}
				if (e.which == 45){	//-
					return false;
				} if (e.which == 46) {	//.
					if ($(this).val().indexOf(".", $(this).val().indexOf(".")) != -1) return false;
					return true;
				}
				else if ((e.which >= 48 && e.which <= 57 && e.ctrlKey == false && e.shiftKey == false) || e.which == 0 || e.which == 8) {
					return true;
				} else if (e.ctrlKey == true && (e.which == 99 || e.which == 118)) {
					return true;
				}  else {
					return false;
				}
			});
			$hiddenInput = $("#"+container+"_hidden");
			if ($hiddenInput.length < 1){
				var name = $(scope).attr('name');//
				$(scope).removeAttr('name');
				$hiddenInput = $("<input id=\""+container+"_hidden\" type=\"text\" style=\"display:none\" name=\"" + name + "\" value=\""+$(scope).val()+"\"/>").insertAfter($container);	
				//$hiddenInput = $("<input type='text' name='" + container + "' value='' 	disabled='disabled'></input>");	
				if (showCnMoney){
					$hiddenInput.insertAfter($cnBox);
				} else {
					$hiddenInput.insertAfter($container);
				}	
				// 给文本框绑定事件
				$container.bind("focus", resumeMoney);
				$container.bind("blur", formatMoney);
				$container.bind("keyup", recordMoney);
			}
			$hidden = $hiddenInput.get(0);
			
			_symbol = (_options.symbol == undefined ? "" : _options.symbol);
			_decimal = (_options.decimalPlace == undefined ? 0 : parseInt(_options.decimalPlace));
			_numberRound = (_options.numberRound == undefined ? true:_options.numberRound);
			
			if($input.value.trim()>0){
				var v = $input.value.replaceAll(',','');
				if(_symbol != '')v = v.replaceAll(_symbol,'');
				setValue(v);
			}
		}
		// 文本框获得焦点时清除货币符号显示实际金额
		function resumeMoney(){
			var _money = parseFloat($hidden.value);
			_money = isNaN(_money) ? "" : new Number(_money);
			if (_decimal !== 0 && _money !== ""){
				if(_numberRound == true || _numberRound == "true"){
					_money = _money.toFixed(_decimal).toString();
				}else{
					var nStr = _money.toString();
					var dw = nStr.indexOf(".");
					if(dw<0){
						_money = nStr;
					}else{
						_money = nStr.substring(0, dw + _decimal + 1);
					}
				}
				var dotIndex = _money.indexOf(".");
				if (dotIndex != -1){
					var dotL = _money.substring(0, dotIndex); 
					var dotR = _money.substring(dotIndex, _money.length); 
					if (dotR.indexOf("0") != -1){
						for (var i=dotR.length-1; i>0; i--){
							var c = dotR.charAt(i); 
							if (c === "0"){
								dotR = dotR.slice(0, i);
							} else {
								break;
							}
						}
					}
					dotR = (dotR === "." ? "" : dotR);
					_money = dotL + dotR;
				}
			}
			if(_decimal == 0){
				if(_numberRound == true || _numberRound == "true"){
					_money == ""?0:_money.toFixed(_decimal).toString();
					//_money = _money.toFixed(_decimal).toString();
				}else{
					var nStr = _money.toString();
					var dw = nStr.indexOf(".");
					if(dw<0){
						_money = nStr;
					}else{
						_money = nStr.substring(0, dw + _decimal );
					}
				}
			}
			
			$input.value = ((isNaN(_money) || _money == undefined || _money === "") ? "" : _money);
		}
		// 按照每隔三位显示一个逗号的规律显示格式化后的金额
		function formatMoney(){
			var num = money = ($hidden.value === "") ? "" : parseFloat($hidden.value);
			if(_numberRound == true || _numberRound == "true"){
				num = (num !== "" && String(num.toFixed(_decimal)));
			}else{
				var nStr = num.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var numVal = Number(nStr);
				}else{
					var numVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				num = (num !=="" && String(numVal));
			}
			//如果输入的数字小数位数小于规定的位数,则补'0'
			if(num.toString().indexOf('.')<0){
				if(_decimal >0){
					num = num + '.';
					for(var i = 0; i<_decimal;i++){
						num += '0';
					}
				}
			}else{
				var inputlength = (num.substring(num.indexOf('.'))).length-1;
				if(inputlength<_decimal && inputlength>0){
					for(var i = 0; i<_decimal-inputlength;i++){
						num += '0';
					}
				}
			}
			//小数位不参与格式化，t_num记录小数点（包括小数点）后的数字
			var t_num;
			if(num){
				t_num = num.substring(num.lastIndexOf("."),num.length);
				num = num.substring(0,num.indexOf("."));
			}
			var re = /(-?\d+)(\d{3})/;
			if (money != undefined || money !== ""){
				while (re.test(num)){
					num = num.replace(re, "$1,$2");
				}
			}
			num = num + t_num;
			$input.value = ((money == undefined || money === "") ? "" : _symbol + num);
			if (showCnMoney) cnMoneyFormat();
		}
		// 失去焦点时将实际金额保存到隐藏文本框中
		function recordMoney(op){
			
			var _inputMoney = parseFloat($input.value);
			money = isNaN(_inputMoney) ? "" : _inputMoney;
			_inputMoney = new Number(_inputMoney);
			
			if(_numberRound == true || _numberRound == "true"){
				$hidden.value = ((isNaN($input.value) || isNaN(_inputMoney) || money === "") ? "" : _inputMoney.toFixed(_decimal));
			}else{
				var nStr = _inputMoney.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var _inputVal = Number(nStr);
				}else{
					var _inputVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				$hidden.value = ((isNaN($input.value) || isNaN(_inputMoney) || money === "") ? "" : _inputVal);
			}
		}
		// 显示转换后的中文金额
		function cnMoneyFormat(){ 
			var num = money;
			var cnMoney = "零元整";
			
			var strOutput = "";  
			var strUnit = '仟佰拾亿仟佰拾万仟佰拾元角分';  
			num += "00";  
			var intPos = num.indexOf('.');  
			if (intPos >= 0)  
			num = num.substring(0, intPos) + num.substr(intPos + 1, 2);  
			strUnit = strUnit.substr(strUnit.length - num.length);  
			for (var i=0; i < num.length; i++)  
			strOutput += '零壹贰叁肆伍陆柒捌玖'.substr(num.substr(i,1),1) + strUnit.substr(i,1);  
			cnMoney = strOutput.replace(/零角零分$/, '整').replace(/零[仟佰拾]/g, '零').replace(/零{2,}/g, '零').replace(/零([亿|万])/g, '$1').replace(/零+元/, '元').replace(/亿零{0,3}万/, '亿').replace(/^元/, "零元");
			$cnBox.text((isNaN(money) || money == undefined || money === "") ? "" : cnMoney);
		}
		// 获取隐藏文本框的值
		function getValue (){
			return $hidden.value;
		};
		// 设置隐藏文本框和文本输入框的值
		function setValue (args){
			args = String(args);
			money = args;
			//处理onChange事件获得的值,比如3.99999,得到的值为3.99(precision = 2)
			var num = money = ($hidden.value === "") ? "" : parseFloat($hidden.value);
			if(_numberRound == true || _numberRound == "true"){
				num = (num !== "" && String(num.toFixed(_decimal)));
			}else{
				var nStr = num.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var numVal = Number(nStr);
				}else{
					var numVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				num = (num !=="" && String(numVal));
			}
			//如果输入的数字小数位数小于规定的位数,则补'0'
					if(num.indexOf('.')<0){
						if(_decimal !== undefined){
							num = num + '.';
							for(var i = 0; i<_decimal;i++){
								num += '0';
							}
						}
					}else{
						var inputlength = (num.substring(num.indexOf('.'))).length-1;
						if(inputlength<_decimal && inputlength>0){
							for(var i = 0; i<_decimal-inputlength;i++){
								num += '0';
							}
						}
					}
			$input.value = _symbol + num;
			$hidden.value = num;
//			$input.value = args;
//			$hidden.value = args;
			$container.blur();
			//formatMoney();
		}
		init();
		if (typeof options.getValue === "string" ||typeof options.setValue === "string"){
			if ("getValue" === options.getValue){
				return getValue();
			} else if("setValue" === options.setValue) {
				if (value != undefined){
					if (value == "") {
						$hidden.value = value;
						$input.value = value;
						return;
					}
					$hidden.value = value;
//					if(options.decimalPlace){
//						if(options.numberRound == true || options.numberRound == "true"){
//							value = Number(value);
//							value = (value !== "" && String(value.toFixed(options.decimalPlace)));
//						}else{
//							var nStr = value.toString();
//							var dw = nStr.indexOf(".");
//							if(dw<0){
//								var numVal = Number(nStr);
//							}else{
//								var numVal = Number(nStr.substring(0, dw + parseInt(options.decimalPlace) + 1));
//							}
//							value = (value !=="" && String(numVal));
//						}
//					}
//					//如果输入的数字小数位数小于规定的位数,则补'0'
//					if(value.indexOf('.')<0){
//						if(options.decimalPlace !== undefined){
//							value = value + '.';
//							for(var i = 0; i<parseInt(options.decimalPlace);i++){
//								value += '0';
//							}
//						}
//					}else{
//						var inputlength = (value.substring(value.indexOf('.'))).length-1;
//						if(inputlength<parseInt(options.decimalPlace) && inputlength>0){
//							for(var i = 0; i<parseInt(options.decimalPlace)-inputlength;i++){
//								value += '0';
//							}
//						}
//					}
//					value = options.symbol + value;
					setValue(value);	
				}
			}
		}
	}
}));