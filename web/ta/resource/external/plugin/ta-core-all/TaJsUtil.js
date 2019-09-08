/**
 * Ta+3框架JS工具类，调用方式Ta.util.xxx();
 * @module Ta
 * @class util
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Ta: { 
            util: util()
        }
    });
	function util(){
		/*针对string的扩展*/
		String.prototype.trim = function() {
			return $.trim(this);
		};
		String.prototype.replaceAll = function(s1,s2){   
			return this.replace(new RegExp(s1,"gm"),s2);   
		};
		return {
			//map使用需呀new出来
			"Map" : Map,
			"InputPositon" : InputPositon(),
			"isDate" : isDate,
			"isDateTime" : isDateTime,
			"getCurDate" : getCurDate,
			"getCurDateMonth":getCurDateMonth,
			"getCurIssue" : getCurIssue,
			"getCurDateYear" : getCurDateYear,
			"getCurIssue" : getCurIssue,
			"obj2string" : obj2string,
			"moneyFormat" : moneyFormat,
			"cnMoneyFormat" : cnMoneyFormat,
			"floatAdd" : floatAdd,
			"getCurDateTime":getCurDateTime	
		};
	}
	/**
	 * MAP对象，实现MAP功能 接口： size() 获取MAP元素个数 isEmpty() 判断MAP是否为空 clear() 删除MAP所有元素
	 * put(key, value) 向MAP中增加元素（key, value) remove(key)
	 * 删除指定KEY的元素，成功返回True，失败返回False get(key) 获取指定KEY的元素值VALUE，失败返回NULL
	 * element(index) 获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
	 * containsKey(key) 判断MAP中是否含有指定KEY的元素 containsValue(value) 判断MAP中是否含有指定VALUE的元素
	 * values() 获取MAP中所有VALUE的数组（ARRAY） keys() 获取MAP中所有KEY的数组（ARRAY） 例子： var map =
	 * new Ta.util.Map(); map.put("key", "value"); var val = map.get("key")
	 * @method Map
	 */
	function Map(){
		var elements = new Array();
		// 获取MAP元素个数
		function size(){
			return elements.length;
		}
		// 判断MAP是否为空
		function isEmpty(){
			return (elements.length < 1);
		}
		// 删除MAP所有元素
		function clear(){
			elements = new Array();
		}
		function put(_key, _value){
			elements.push({
				key : _key,
				value : _value
			});
		}
		// 删除指定KEY的元素，成功返回True，失败返回False
		function remove(_key) {
			var bln = false;
			try {
				for (var i = 0; i < elements.length; i++) {
					if (elements[i].key == _key) {
						elements.splice(i, 1);
						return true;
					}
				}
			} catch (e) {
				bln = false;
			}
			return bln;
		}
		// 获取指定KEY的元素值VALUE，失败返回NULL
		function get(_key) {
			try {
				for (var i = 0; i < elements.length; i++) {
					if (elements[i].key == _key) {
						return elements[i].value;
					}
				}
			} catch (e) {
				return null;
			}
			return null;
		}
		// 获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
		function element(_index) {
			if (_index < 0 || _index >= elements.length) {
				return null;
			}
			return elements[_index];
		}
		// 判断MAP中是否含有指定KEY的元素
		function containsKey(_key){
			var bln = false;
			try {
				for (var i = 0; i < elements.length; i++) {
					if (elements[i].key == _key) {
						bln = true;
					}
				}
			} catch (e) {
				bln = false;
			}
			return bln;
		}
		// 判断MAP中是否含有指定VALUE的元素
		function containsValue(_value) {
			var bln = false;
			try {
				for (var i = 0; i < elements.length; i++) {
					if (elements[i].value == _value) {
						bln = true;
					}
				}
			} catch (e) {
				bln = false;
			}
			return bln;
		}
		// 获取MAP中所有VALUE的数组（ARRAY）
		function values() {
			var arr = new Array();
			for (var i = 0; i < elements.length; i++) {
				arr.push(elements[i].value);
			}
			return arr;
		}
		// 获取MAP中所有KEY的数组（ARRAY）
		function keys() {
			var arr = new Array();
			for (var i = 0; i < elements.length; i++) {
				arr.push(elements[i].key);
			}
			return arr;
		}
		
		return {
			"size":size,
			"isEmpty": isEmpty,
			"clear":clear,
			"put" :put,
			"remove":remove,
			"get": get,
			"element":element,
			"containsKey":containsKey,
			"containsValue":containsValue,
			"values":values,
			"keys": keys
		};
	}
	//map end
	/**
	 * 判断是否为日期格式。
	 * @method isDate
	 * @param {String} dateval 目标串
	 */
	function isDate(dateval) {
		var arr = new Array();
		if (dateval.length != 10)
			return false;
		if (dateval.indexOf("-") != -1) {
			arr = dateval.toString().split("-");
		} else if (dateval.indexOf("/") != -1) {
			arr = dateval.toString().split("/");
		} else {
			return false;
		}
		if (arr.length != 3)
			return false;
		// yyyy-mm-dd || yyyy/mm/dd
		if (arr[0].length == 4) {
			var date = new Date(arr[0], arr[1] - 1, arr[2]);
			if (date.getFullYear() == arr[0] && date.getMonth() == arr[1] - 1
					&& date.getDate() == arr[2]) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断是否为日期时间格式。
	 * @method isDateTime
	 * @param {String} dateval 目标串
	 */
	function isDateTime(dateval) {
		if (dateval.length != 19)
			return false;
		var arr = dateval.split(' ');
		if (!Ta.util.isDate(arr[0])) {
			return false;
		}
		var atime = arr[1].split(':');
		if (atime.length != 3)
			return false;
		if (atime[0] > 24 || atime[1] >= 60 || atime[2] >= 60) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取当前日期YYYY-MM-DD。
	 * @method getCurDate
	 */
	function getCurDate() {
		var d = new Date();
		var ret = d.getFullYear() + "-";
		ret += ("00" + (d.getMonth() + 1)).slice(-2) + "-";
		ret += ("00" + d.getDate()).slice(-2);
		return ret;
	}
	
	/**
	 * 获取当前日期YYYY-MM。
	 * @method getCurDateMonth
	 */
	function getCurDateMonth() {
		var d = new Date();
		var ret = d.getFullYear() + "-";
		ret += ("00" + (d.getMonth() + 1)).slice(-2);
		return ret;
	}
	/**
	 * 获取当前时间YYYY-MM-DD HH:MM:SS。
	 * @method getCurDateTime
	 */
	function getCurDateTime(){
		var d = new Date();
		var ret = d.getFullYear() + "-";
		ret += ("00" + (d.getMonth() + 1)).slice(-2) + "-";
		ret += ("00" + d.getDate()).slice(-2) + " ";
		ret += ("00" + d.getHours()).slice(-2) + ":";
		ret += ("00" + d.getMinutes()).slice(-2) + ":";
		ret += ("00" + d.getSeconds()).slice(-2);
		return ret;
	}
	/**
	 * 获取当前期号YYYYMM。
	 * @method getCurIssue
	 */
	function getCurIssue() {
		var d = new Date();
		var ret = d.getFullYear();
		ret += ("0" + (d.getMonth() + 1));
		return ret;
	}
	/**
	 * 获取当前年份YYYY。
	 * @method getCurDateYear
	 */
	function getCurDateYear() {
		var d = new Date();
		var ret = d.getFullYear();
		return ret;
	}
	
	function InputPositon(){
		var _style = {};
		function show(elem) {
			var p = getInputPositon(elem);
			var k = elem.value.replace(/[^\x00-\xff]/gmi, 'pp').length;// 将中文转换成pp后再计算长度
			// var k = $.trim(elem.value).length;//liys修改
			if (k == 0)
				return;
			var s = document.getElementById('__inputcharshow');
			if (!s) {
				var tmp = $('<div id="__inputcharshow" style="position:absolute;height:20px; background: #F6CC87;border:1px solid #A6C9E2;z-index:99999999;"></div>');
				tmp.appendTo('body');
				s = tmp[0];
			}
			if (p.bottom < 50) {
				if (/msie/.test(navigator.userAgent.toLowerCase())) {
					s.style.top = p.bottom + 37 + 'px';
				} else {
					s.style.top = p.bottom + 6 + 'px';
				}

			} else {
				if (/msie/.test(navigator.userAgent.toLowerCase())) {
					s.style.top = p.bottom - 43 + 31 + 'px';
				} else {
					s.style.top = p.bottom - 43 + 'px';
				}
			}
			s.style.left = p.left + 'px';
			s.style.display = 'block';
			if (typeof s.innerText == "undefined")
				s.textContent = k;
			else
				s.innerText = k;
		}
		
		function remove() {
			$("#__inputcharshow").remove();
		}
		/**
		 * 获取输入光标在页面中的坐标
		 * @method getInputPositon
		 * @param {HTMLElement} elem
		 *            输入框元素
		 * @return {Object} 返回left和top,bottom
		 */
		function getInputPositon(elem) {
			if (document.selection) { // IE Support
				elem.focus();
				// liys修改
				var topObj = window.top.document.getElementById('header');// 获取顶层id=header的元素
				var $topObj = $(topObj);
				var top1 = $topObj.height();// header高度
				var leftObj = window.top.document.getElementById('layout1');// 获取顶层id=layout1的元素
				var $leftObj = $(leftObj);
				var topPanel = $($leftObj.find('.l-layout-header')[0]);// 顶部
				var $topPanel = $(topPanel);
				top1 = top1 + $topPanel.height();
				var showleft = $($leftObj.find('.l-layout-left')[0]);// 左侧显示
				var hiddenleft = $($leftObj.find('.l-layout-collapse-left')[0]);// 左侧隐藏
				var drophandlewidth = $(
						$leftObj.find('.l-layout-drophandle-left')[0]).width();
				var left1;
				if (showleft.css('display') == 'none') {
					left1 = hiddenleft.width() + drophandlewidth;
				} else {
					left1 = showleft.width() + drophandlewidth;
				}

				var Sel = document.selection.createRange();
				return {
					left : $(document).scrollLeft() + Sel.boundingLeft - left1,
					top : $(document).scrollTop() + Sel.boundingTop - top1,
					bottom : $(document).scrollTop() + Sel.boundingTop
							- Sel.boundingHeight - top1
				// left: Sel.boundingLeft,
				// top: Sel.boundingTop,
				// bottom: Sel.boundingTop + Sel.boundingHeight
				};
			} else {
				var cloneDiv = '{$clone_div}', cloneLeft = '{$cloneLeft}', cloneFocus = '{$cloneFocus}', cloneRight = '{$cloneRight}';
				var none = '<span style="white-space:pre-wrap;"> </span>';
				var div = elem[cloneDiv] || document.createElement('div'), focus = elem[cloneFocus]
						|| document.createElement('span');
				var text = elem[cloneLeft] || document.createElement('span');
				var offset = _offset(elem), index = _getFocus(elem), focusOffset = {
					left : 0,
					top : 0
				};

				if (!elem[cloneDiv]) {
					elem[cloneDiv] = div, elem[cloneFocus] = focus;
					elem[cloneLeft] = text;
					div.appendChild(text);
					div.appendChild(focus);
					document.body.appendChild(div);
					focus.innerHTML = '|';
					focus.style.cssText = 'display:inline-block;width:0px;overflow:hidden;z-index:-100;word-wrap:break-word;word-break:break-all;';
					div.className = _cloneStyle(elem);
					div.style.cssText = 'visibility:hidden;display:inline-block;position:absolute;z-index:-100;word-wrap:break-word;word-break:break-all;overflow:hidden;';
				};
				div.style.left = _offset(elem).left + "px";
				div.style.top = _offset(elem).top + "px";
				var strTmp = elem.value.substring(0, index).replace(/</g, '<')
						.replace(/>/g, '>').replace(/\n/g, '<br/>').replace(/\s/g,
								none);
				text.innerHTML = strTmp;

				focus.style.display = 'inline-block';
				try {
					focusOffset = _offset(focus);
				} catch (e) {
				};
				focus.style.display = 'none';
				return {
					left : focusOffset.left,
					top : focusOffset.top,
					bottom : focusOffset.bottom
				};
			}
		}

		// 克隆元素样式并返回类
		function _cloneStyle(elem, cache) {
			if (!cache && elem['${cloneName}'])
				return elem['${cloneName}'];
			var className, name, rstyle = /^(number|string)$/;
			var rname = /^(content|outline|outlineWidth)$/; // Opera: content;
															// IE8:outline &&
			var cssText = [], sStyle = elem.style;

			for (name in sStyle) {
				if (!rname.test(name)) {
					val = _getStyle(elem, name);
					if (val !== '' && rstyle.test(typeof val)) { // Firefox 4
						name = name.replace(/([A-Z])/g, "-$1").toLowerCase();
						cssText.push(name);
						cssText.push(':');
						cssText.push(val);
						cssText.push(';');
					};
				};
			};
			cssText = cssText.join('');
			elem['${cloneName}'] = className = 'clone' + (new Date).getTime();
			_addHeadStyle('.' + className + '{' + cssText + '}');
			return className;
		}

		// 向页头插入样式
		function _addHeadStyle(content) {
			var style = _style[document];
			if (!style) {
				style = _style[document] = document.createElement('style');
				document.getElementsByTagName('head')[0].appendChild(style);
			};
			style.styleSheet && (style.styleSheet.cssText += content)
					|| style.appendChild(document.createTextNode(content));
		}
		
		// 获取最终样式
		function _getStyle(elem, name){
			if ('getComputedStyle' in window){
				return getComputedStyle(elem, null)[name];
			} else {
				return elem.currentStyle[name];
			}
		}
		// 获取光标在文本框的位置
		/**
		 * 获取光标在文本框的位置。
		 * @method _getFocus
		 * @private
		 */
		function _getFocus(elem) {
			var index = 0;
			if (document.selection) {// IE Support
				elem.focus();
				var Sel = document.selection.createRange();
				if (elem.nodeName === 'TEXTAREA') {// textarea
					var Sel2 = Sel.duplicate();
					Sel2.moveToElementText(elem);
					var index = -1;
					while (Sel2.inRange(Sel)) {
						Sel2.moveStart('character');
						index++;
					}
					;
				} else if (elem.nodeName === 'INPUT') {// input
					Sel.moveStart('character', -elem.value.length);
					index = Sel.text.length;
				}
			} else if (elem.selectionStart || elem.selectionStart == '0') { // Firefox
																			// support
				index = elem.selectionStart;
			}
			return (index);
		}

		// 获取元素在页面中位置
		function _offset(elem) {
			var box = elem.getBoundingClientRect(), doc = elem.ownerDocument, body = doc.body, docElem = doc.documentElement;
			var clientTop = docElem.clientTop || body.clientTop || 0, clientLeft = docElem.clientLeft
					|| body.clientLeft || 0;
			var top = box.top + (self.pageYOffset || docElem.scrollTop) - clientTop, left = box.left
					+ (self.pageXOffset || docElem.scrollLeft) - clientLeft;
			return {
				left : left,
				top : top,
				right : left + box.width,
				bottom : top + box.height
			};
		}
		
		return {
			"show":show,
			"remove":remove,
			"getInputPositon" :getInputPositon,
			"_cloneStyle":_cloneStyle,
			"_addHeadStyle":_addHeadStyle,
			"_getStyle":_getStyle,
			"_getFocus":_getFocus,
			"_offset":_offset
		};
	}
	/**
	 * 将json对象转换为string
	 * @method obj2string
	 * @param {Object} o json对象
	 * @return {string}
	 */
	function obj2string(o){
		if (o == null || o == 'undefined')
			return null;
		var r = [];
		if (typeof o == "string")return o;
		
		if (typeof o == "object") {
			if (!jQuery.isArray(o)) {
				for ( var i in o) {
					if (typeof o[i] == 'string' || typeof o[i] == 'number') {
						if (o[i] != undefined) {
							r.push("\""+ i + "\":\"" + o[i].toString().replace(/\"/g, "\\\"") + "\"");
						} else {
							r.push("\"" + i + "\":null");
						}
					} else {
						r.push("\"" + i + "\":" + Ta.util.obj2string(o[i]));
					}
				}
				
				if (!!document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
					r.push("toString:" + o.toString.toString());
				}
				
				r = "{" + r.join() + "}";
			} else {
				for (var i = 0; i < o.length; i++)
					r.push(Ta.util.obj2string(o[i]));
				r = "[" + r.join() + "]";
			}
			return r;
		}
		return o.toString();
	}
	
	/**
	 * 将数字转换为金额显示，每三位逗号隔开
	 * @method moneyFormat
	 * @param {Number} money 数字
	 * @param {Number} decimal 小数位
	 * @param {string} symbol 金额前缀，如￥或$
	 */
	function moneyFormat(money, decimal, symbol) {
		if (!money || isNaN(money))
			return "";
		var num = parseFloat(money);
		num = String(num.toFixed(decimal ? decimal : 0));
		var re = /(-?\d+)(\d{3})/;
		while (re.test(num)) {
			num = num.replace(re, "$1,$2");
		}
		return symbol ? symbol + num : num;
	}
	
	/**
	 * 将数字转换为中文的金额
	 * @method cnMoneyFormat
	 * @param {Number} money 数字
	 */
	function cnMoneyFormat(money) {
		var cnMoney = "零元整";
		var strOutput = "";
		var strUnit = '仟佰拾亿仟佰拾万仟佰拾元角分';
		money += "00";
		var intPos = money.indexOf('.');
		if (intPos >= 0) {
			money = money.substring(0, intPos) + money.substr(intPos + 1, 2);
		}
		strUnit = strUnit.substr(strUnit.length - money.length);
		for (var i = 0; i < money.length; i++) {
			strOutput += '零壹贰叁肆伍陆柒捌玖'.substr(money.substr(i, 1), 1)
					+ strUnit.substr(i, 1);
		}
		cnMoney = strOutput.replace(/零角零分$/, '整').replace(/零[仟佰拾]/g, '零').replace(
				/零{2,}/g, '零').replace(/零([亿|万])/g, '$1').replace(/零+元/, '元')
				.replace(/亿零{0,3}万/, '亿').replace(/^元/, "零元");
		return cnMoney;
	}
	/**
	 * 计算两个浮点数相加的结果。
	 * @method floatAdd
	 * @param {Number} arg1 Number对象
	 * @param {Number} arg2 Number对象
	 * @return {Number}
	 */
	function floatAdd(arg1, arg2) {
		var r1, r2, m;
		try {
			r1 = arg1.toString().split(".")[1].length;
		} catch (e) {
			r1 = 0;
		}
		try {
			r2 = arg2.toString().split(".")[1].length;
		} catch (e) {
			r2 = 0;
		}
		m = Math.pow(10, Math.max(r1, r2));
		return ((arg1 * m + arg2 * m) / m).toFixed((m.toString()).length - 1);
	}
}));
