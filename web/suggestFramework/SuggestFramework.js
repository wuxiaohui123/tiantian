
/**
 * Suggest Framework
 * Copyright (c) 2005-06 Matthew Ratzloff <matt@builtfromsource.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 2009-09-03 邓祥静修改
 * 1.修改获取显示数据由json提供
 * 2.增加了滚动条
 * 3.增加一行数据可以不显示下拉选择框
 * 4.增加了函数回调
 * 5.增加键盘方向键处理
 * 6.增加没有数据处理
 * 7.增加选择行的指定填充录入框
 * 8.增加在回调行数中可以返回选择行的全部数据
 * 9.增加是否需要隐藏列 如果列名最后一位是*的标识隐藏[20120613]
 *   比如new Array(new Array('方案编号','名称','有效标志*') 有效标志不会显示，但能获取到数据
 */
var g_Suggest = null;
var targetid = null;
var sfw = new Array();
function fnCatch(e) {
	try {
		var szStr = "";
		var oCl = fnCatch.caller;
		var rg = /function\s*([^\(]*)/m, j = 0;
		while (null != oCl && 40 > j) {
			var arrT = rg.exec(oCl);
			if (1 < arrT.length) {
				if (0 < szStr.length) {
					szStr = "==[\u8c03\u7528]==>" + szStr;
				}
				szStr = arrT[1] + szStr;
			}
			oCl = oCl.caller;
		}
		szStr += "\n\n\u53d1\u751f\u9519\u8bef\u6d88\u606f\u4e3a\uff1a" + e.message;
		alert(szStr);
	}
	catch (e) {
	}
}
function sfwCreate(instance, dto) {
	if (sfw[instance].name && sfw[instance].action) {
		sfw[instance].inputContainer = document.getElementById(sfw[instance].name);
		sfw[instance].inputContainer.autocomplete = "off";
		sfw[instance].inputContainer.onblur       = function() { 
			if(targetid == 'mydiv'){
				sfwShowOutput(instance);
				Base.focus(dto);
			}else{
				sfwHideOutput(instance); 
			}
		
		};
		sfw[instance].inputContainer.onclick      = function() { sfwShowOutput(instance,dto); };
		sfw[instance].inputContainer.onfocus = function () {
			sfwShowOutput(instance);
		};
		sfw[instance].inputContainer.onkeypress = function (event) {
			if (sfwGetKey(event) == 13) {
				return false;
			}
		};
		sfw[instance].inputContainer.onkeydown = function (event) {
			sfwProcessKeys(instance, event);
		};
		if (null == document.getElementById(sfw[instance].name + "_list")) {
			sfw[instance].outputContainer = document.createElement("div");
			sfw[instance].outputContainer.id = sfw[instance].name + "_list";
			sfw[instance].outputContainer.className = "SuggestFramework_List";
			sfw[instance].outputContainer.style.height = sfw[instance].height + "px";
			//sfw[instance].outputContainer.style.overflowY = "auto";
			//sfw[instance].outputContainer.style.overflowX = "auto";
			sfw[instance].outputContainer.style.position = "absolute";
			sfw[instance].outputContainer.style.zIndex = "55555";
			sfw[instance].outputContainer.style.width = sfw[instance].width + "px";
			//sfw[instance].outputContainer.style.width = "300px";
			sfw[instance].outputContainer.style.wordWrap = "break-word";
			sfw[instance].outputContainer.style.cursor = "default";
			sfw[instance].inputContainer.parentNode.insertBefore(sfw[instance].outputContainer, sfw[instance].inputContainer.nextSibling);
			sfw[instance].inputContainer.parentNode.insertBefore(document.createElement("br"), sfw[instance].outputContainer);
			if (sfw[instance].columns > 1 && sfw[instance].capture > 1) {
				sfw[instance].hiddenInput = document.createElement("input");
				sfw[instance].hiddenInput.id = "_" + sfw[instance].name;
				sfw[instance].hiddenInput.name = "_" + sfw[instance].name;
				sfw[instance].hiddenInput.type = "hidden";
				sfw[instance].inputContainer.parentNode.insertBefore(sfw[instance].hiddenInput, sfw[instance].inputContainer.nextSibling);
			}
		} else {
			sfw[instance].outputContainer = document.getElementById(sfw[instance].name + "_list");
			sfw[instance].hiddenInput = document.getElementById("_" + sfw[instance].name);
		}
		sfwHideOutput(instance);
		sfwThrottle(instance);
	} else {
		throw "Suggest Framework Error: Instance \"" + sfw[instance].name + "\" not initialized";
	}
}
function sfwGetKey(e) {
	return ((window.event) ? window.event.keyCode : e.which);
}
function sfwHideOutput(instance) {
	sfw[instance].outputContainer.style.display = "none";
}
function sfwHighlight(instance, index) {
	sfw[instance].suggestionsIndex = index;
	for (var i in sfw[instance].suggestions) {
		if (i == "ccp" || i == "each" || i == "indexOf") {
			continue;
		}
		var suggestionColumns = document.getElementById(sfw[instance].name + "_suggestions[" + i + "]").getElementsByTagName("td");
		for (var j in suggestionColumns) {
			suggestionColumns[j].className = "SuggestFramework_Normal";
		}
		
	}
	var suggestionColumns = document.getElementById(sfw[instance].name + "_suggestions[" + sfw[instance].suggestionsIndex + "]").getElementsByTagName("td");
	for (var i in suggestionColumns) {
		suggestionColumns[i].className = "SuggestFramework_Highlighted";
	}
	if (sfw[instance].suggestions.length == 1 && sfw[instance].oneRowVisible == false) {
		sfwSelectThis(instance);
	}
}
function sfwIsHidden(instance) {
	return ((sfw[instance].outputContainer.style.display == "none") ? true : false);
}
function sfwProcessKeys(instance, e) {
	var keyDown = 40;
	var keyUp = 38;
	var keyTab = 9;
	var keyEnter = 13;
	var keyEscape = 27;
	var keyLeft = 37;
	var keyRight = 39;
	if (!sfwIsHidden(instance)) {
		switch (sfwGetKey(e)) {
		  case keyDown:
			sfwSelectNext(instance);
			return;
		  case keyRight:
			sfwSelectNext(instance);
			return;
		  case keyUp:
			sfwSelectPrevious(instance);
			return;
		  case keyLeft:
			sfwSelectPrevious(instance);
			return;
		  case keyEnter:
			sfwHideOutput(instance);
		  case keyTab:
			sfwSelectThis(instance);
			return;
		  case keyEscape:
			sfwHideOutput(instance);
			return;
		  default:
			sfwHideOutput(instance);
			return;
		}
	}
}
function sfwSelectThis(instance, index) {
//	try {
		if (sfw[instance].rowcount == 1) {
			if (sfw[instance].columns > 1 && sfw[instance].capture > 1) {
				sfw[instance].hiddenInput.value = sfw[instance].suggestions[sfw[instance].suggestionsIndex][sfw[instance].capture - 1];
			}
			if (!isNaN(index)) {
				sfw[instance].suggestionsIndex = index;
			}
			var selection = sfw[instance].suggestions[sfw[instance].suggestionsIndex];
			if (sfw[instance].columns > 1) {
				g_Suggest = selection;
				//下面两句徐进修改，要求采用这种模式，第一个字段必须是输入的字段
				selection = selection[sfw[instance].selectColumn];
				//selection = g_Suggest[0];
			}
			sfw[instance].inputContainer.value = selection;
			sfw[instance].previous = selection;

			sfwHideOutput(instance);
//			try {
				if ("function" == typeof sfw[instance].callBackFun) {
					sfw[instance].callBackFun();
				} else {
					eval(sfw[instance].callBackFun);
				}
//			}
//			catch (e) {
//				alert("\u6267\u884c\u56de\u8c03\u51fd\u6570: [" + callBackFun + "]\u65f6\u53d1\u751f\u5f02\u5e38:" + e.message);
//			}
		}
//	}
//	catch (e) {
//		fnCatch(e);
//	}
}
function fnGetSuggestValue() {
	return g_Suggest;
}

function sfwSelectNext(instance) {
	try {
		sfwSetTextSelectionRange(instance);
		if (typeof sfw[instance].suggestions[(sfw[instance].suggestionsIndex + 1)] != "undefined") {
			if (typeof sfw[instance].suggestions[sfw[instance].suggestionsIndex] != "undefined") {
				document.getElementById(sfw[instance].name + "_suggestions[" + sfw[instance].suggestionsIndex + "]").className = "SuggestFramework_Normal";
			}
			sfw[instance].suggestionsIndex++;
		//增加键盘方向键的滚动页面显示 2010-03-10
			var count1 = parseInt(sfw[instance].height /30) - 1;
			var _sug_table = document.getElementById(sfw[instance].name + "_sug_table");
			var _sug_tab_row = _sug_table.rows[sfw[instance].suggestionsIndex];
			_sug_tab_row.scrollIntoView(true);
			/*原来是计划翻页才滚动，由于高度可以自定义就取消了20120613
		if (sfw[instance].suggestionsIndex%count1 ==0)
		{
			var _sug_table = document.getElementById(sfw[instance].name+"_sug_table");
			var _sug_tab_row =_sug_table.rows[sfw[instance].suggestionsIndex];
			_sug_tab_row.scrollIntoView(true);
		}*/
			sfwHighlight(instance, sfw[instance].suggestionsIndex);
		}
	}
	catch (e) {
		fnCatch(e);
	}
}
function sfwSelectPrevious(instance) {
	try {
		sfwSetTextSelectionRange(instance);
		if (typeof sfw[instance].suggestions[(sfw[instance].suggestionsIndex - 1)] != "undefined") {
			if (typeof sfw[instance].suggestions[sfw[instance].suggestionsIndex] != "undefined") {
				document.getElementById(sfw[instance].name + "_suggestions[" + sfw[instance].suggestionsIndex + "]").className = "SuggestFramework_Normal";
			}
			sfw[instance].suggestionsIndex--;
		//增加键盘方向键的滚动页面显示 2010-03-10
			var _sug_table = document.getElementById(sfw[instance].name + "_sug_table");
			var _sug_tab_row = _sug_table.rows[sfw[instance].suggestionsIndex];
			_sug_tab_row.scrollIntoView(true);
			sfwHighlight(instance, sfw[instance].suggestionsIndex);
			//sfwHighlight(instance, sfw[instance].suggestionsIndex);
		}
	}
	catch (e) {
		fnCatch(e);
	}
}
function sfwSetTextSelectionRange(instance, start, end) {
	try {
		if (!start) {
			var start = sfw[instance].inputContainer.value.length;
		}
		if (!end) {
			var end = sfw[instance].inputContainer.value.length;
		}
		if (sfw[instance].inputContainer.setSelectionRange) {
			sfw[instance].inputContainer.setSelectionRange(start, end);
		} else {
			if (sfw[instance].inputContainer.createTextRange) {
				var selection = sfw[instance].inputContainer.createTextRange();
				selection.moveStart("character", start);
				selection.moveEnd("character", end);
				selection.select();
			}
		}
	}
	catch (e) {
		fnCatch(e);
	}
}
function sfwShowOutput(instance)
{
	dtoname = sfw[instance].name;
	if (document.getElementById(dtoname).disabled==true||document.getElementById(dtoname).readOnly==true)
	{
	}else{
		if(typeof sfw[instance].suggestions != "undefined" && sfw[instance].suggestions.length>1)
			sfw[instance].outputContainer.style.display = "block";
	}
		
};
function sfwSuggest(instance, list) {
	sfw[instance].suggestions = list;
	sfw[instance].suggestionsIndex = -1;
	sfw[instance].outputContainer.innerHTML = "";
	var rowcount = 0;
	//=============锁定表头1=========================
	var table1 = "<div id=\"mydiv1\" style=\"width:100%;\" ><table style=\"width:100%;overflow:hidden;\" id=\"" + sfw[instance].name + "_sug_table1\"  cellspacing=\"0\" cellpadding=\"0\" >";

	if (sfw[instance].heading && sfw[instance].suggestions.length) {
		//var heading = sfw[instance].suggestions.shift();
		var thead1 = "<thead>";
	
		 thead1 += "</thead></table></div>";
		table1 += thead1;
	}
	//=============锁定表头1=========================
	var table = "<div id=\"mydiv\" style=\"width:100%;\"  ><table style=\"width:100%;overflow:hidden;\" id=\"" + sfw[instance].name + "_sug_table\"  cellspacing=\"0\" cellpadding=\"0\" >";
	var hiddenCol = "";
	if (sfw[instance].heading && sfw[instance].suggestions.length) {
		var heading = sfw[instance].suggestions.shift();
		var thead = "<thead>";
		var headingContainer = "<tr>";
		for (var i = 0; i < sfw[instance].columns; i++) {
			var value = (String)((sfw[instance].columns > 1) ? heading[i] : heading);
			var column = "<td class=\"SuggestFramework_Heading\"";
			//var column = '<td  class="TABLEFORM" ';
			if (sfw[instance].columns > 1 && i == sfw[instance].columns - 1) {
				
				//20120613 增加是否需要隐藏列 如果列明最后一位是*的标识隐藏
				//alert(value+"["+value.indexOf("*")+"]");
				if (value.indexOf("*") > -1) {
					column += " style=\"display:none\"";
					hiddenCol += ";" + i + ";";
				} else {
					column += " style=\"text-align: center\"";
				}
			}
			column += ">" + value.trim() + "</td>";
			headingContainer += column;
		}
		headingContainer += "</tr>";
		thead += headingContainer;
		thead += "</thead>";
		table += thead;
	}
	var tbody = "<tbody>";
	for (var i in sfw[instance].suggestions) {
		if (i == "ccp" || i == "each" || i == "indexOf") {
			continue;
		}
		var suggestionContainer = "<tr id=\"" + sfw[instance].name + "_suggestions[" + i + "]\">";
		for (var j = 0; j < sfw[instance].columns; j++) {
			var value = (String)((sfw[instance].columns > 1) ? sfw[instance].suggestions[i][j] : sfw[instance].suggestions[i]);
			var column = "<td class=\"SuggestFramework_Normal\"";
			//alert("i:"+i+"["+value);
			//var column = '<td class="TABLEFORM" ';
			if (sfw[instance].columns > 1 && j == sfw[instance].columns - 1) {
				//column += ' style="text-align: right"';
				if (hiddenCol.indexOf(";" + j + ";") > -1) {
					//20120613 增加是否需要隐藏列 如果列明最后一位是*的标识隐藏
					column += " style=\"display:none\"";
				} else {
					column += " style=\"text-align: left\"";
				}
			}
			column += ">" + value.trim() + "</td>";
			suggestionContainer += column;
			rowcount = 1;
		}
		suggestionContainer += "</tr>";
		table += suggestionContainer;
	}
	tbody += "</tbody>";
	table += tbody;
	table += "</table></div>";
	
	var selectGrid = table1+table;
	
	sfw[instance].outputContainer.innerHTML = selectGrid;
	
	for (var i in sfw[instance].suggestions) {
		if (i == "ccp" || i == "each" || i == "indexOf") {
			continue;
		}
		var row = document.getElementById(sfw[instance].name + "_suggestions[" + i + "]");
		row.onmouseover = new Function("sfwHighlight(" + instance + ", " + i + ")");
		row.onmousedown = new Function("sfwSelectThis(" + instance + ", " + i + ")");
	}
	sfwShowOutput(instance);
	
	//设置焦点20120613 chrome和ie8处理不同
	Base.focus(sfw[instance].name);
	if (rowcount == 1) {
		sfw[instance].rowcount = 1;
		sfwHighlight(instance, 0);
	} else {
		sfw[instance].rowcount = 0;
		alert("\u6ca1\u6709\u83b7\u53d6\u5230\u6570\u636e,\u8bf7\u91cd\u65b0\u5f55\u5165");
		sfw[instance].inputContainer.value = "";
		sfw[instance].previous = "";
	}
		//=============锁定表头2=========================
	var thead_ = $("#mydiv").find("thead").html();
	var tds_  = $("#mydiv").find("tr:first");
	 $("#mydiv1").css({"width":sfw[instance].width + "px"});
	 $("#mydiv").css({"height":sfw[instance].height + "px","width":sfw[instance].width + "px","overflow":"auto"});
	
	$("#mydiv1").find("thead").html(thead_) ;
	//alert($("#mydiv").find("tbody").find("tr > td:eq(0) ").text());
	var td_len="";
	var td_height = tds_.find("td:eq(0)").height();
	for(var i = 0 ;i<tds_.children().length-1;i++){
	td_len=tds_.find("td:eq("+i+")").width();
	//让数据的列宽与表头保持一致
		$("#mydiv1").find("td:eq("+i+")").css({"width":td_len-6+"px","height":td_height});
		$("#mydiv").find("tbody").find("tr > td:eq("+i+")").css({"width":td_len+"px","height":td_height});
	}
	$("#mydiv").find("thead").html("");
	
	//================锁定表头2=====================
	
}
//延时查询，自动执行
function sfwThrottle(instance) {
	//setTimeout("sfwQuery(" + instance + ")", sfw[instance].delay);
}
/*
 * instance1 有下拉提示的文本的序号，代表一个实例
 * what      是代表一个对象ac01(可以自动填上姓名、身份证),ab01（可以自动填上单位名称）,kb01（可以自动填上医院名称）
 * dtoname   有下拉提示的文本的名称(property)
 * width     下拉提示框的宽度
 * height    下拉提示框的高度
 * columns   下拉提示框中显示几列
 * callBackFun 选中后执行的回调函数
 * selectColumn 将选中的列表中的第几列填入录入框中 默认为0
 * oneRowVisible 当提示的数据只有一行时是否显示 false为不显示，默认为要显示
 * */

/*
 * RPC事件通过回车触发事件,主要是页面全灰的情况下
 */
function enterCase(id){
	$('#'+id).keydown(function(event){
		if(event.keyCode == 13){
			$('#'+id).blur();
			$('#'+id).focus();
		}
	})
}

function initializeSuggestFramework(instance1, what, dtoname, width, height, columns, callBackFun, selectColumn, oneRowVisible) {
	   enterCase(dtoname);
		var body = document.body;//找到页面body用于绑定，其实也可以绑定document
		function addHandler(element,type,handler){//给页面元素添加事件的通用方法，不理解的可以死记硬背下。写法是固定的。
			if(element.addEventListener){// !IE//非IE下用addEventListener给元素添加事件
				element.addEventListener(type,handler,false);
			} else if(element.attachEvent){// IE//IE下要用 attachEvent给元素添加事件
				element.attachEvent('on'+type,handler);
			} else {//DOM0级//都不支持的时候 就用这种方法 给元素添加事件
				element['on'+type] = handler;
			}
		}
		addHandler(body,'mouseover',function(event){
			var event = event||window.event;
			var target = event.target||event.srcElement;//找到真正触发事件的元素，
			targetid = target.id;
			//Base.setValue('q_idcard',targetid);
			//这里取了触发元素的id，并传递给 文本框 方便查看。//文本框的id也是能被捕获到的。
		});
	
	
	function getAttributeByName(node, attributeName) {
		if (typeof NamedNodeMap != "undefined") {
			if (node.attributes.getNamedItem(attributeName)) {
				return node.attributes.getNamedItem(attributeName).value;
			}
		} else {
			return node.getAttribute(attributeName);
		}
	}
	var inputElements = document.getElementsByTagName("input");
	try {
		for (var instance = 0; instance < inputElements.length; instance++) {
			if (getAttributeByName(inputElements[instance], "name") == "dto['" + dtoname + "']" && getAttributeByName(inputElements[instance], "type") == "text") {
				sfw[instance1] = new Object();
				sfw[instance1].action = getAttributeByName(inputElements[instance], "name");
				sfw[instance1].capture = 2;
				sfw[instance1].columns = columns;
				sfw[instance1].delay = 1000;
				sfw[instance1].heading = true;
				//sfw[instance1].name    = getAttributeByName(inputElements[instance], "name");
				sfw[instance1].name = dtoname;
				sfw[instance1].width = width;
				sfw[instance1].height = height;
				sfw[instance1].what = what;
				if (callBackFun != undefined) {
					sfw[instance1].callBackFun = callBackFun;
				} else {
					sfw[instance1].callBackFun = "";
				}
				if (selectColumn != undefined) {
					sfw[instance1].selectColumn = selectColumn;
				} else {
					sfw[instance1].selectColumn = 0;
				}
				if (oneRowVisible != undefined) {
					sfw[instance1].oneRowVisible = oneRowVisible;
				} else {
					sfw[instance1].oneRowVisible = true;
				}
				sfwCreate(instance1, dtoname);
			}
		}
	}
	catch (e) {
		fnCatch(e);
	}
}
/*
 * instance 有下拉提示的文本的序号，代表一个实例
 * url      获取数据的url，例如"<%=path%>/g02Action!getAaa201.do"
 * param    参数{"dto['aaz100_mc']":phrase,"dto['aaa143']":"1"}
 */
function suggestQuery(instance, url, param) {
	sfwThrottle(instance);
	var phrase = sfw[instance].inputContainer.value;
	if (phrase == "" || phrase == sfw[instance].previous) {
		return;
	}
	sfw[instance].previous = phrase;
	if (event != null) {
		event.cancelBubble = true;
		event.returnValue = false;
	}
	var outstring = "";
	Base.submit(null, url, param, null, false, function (data) {
		outstring = data.fieldData.data;
		sfwSuggest(instance, eval(outstring));
	
	});
}

/*
 * instance 有下拉提示的文本的序号，代表一个实例
 * method   方法名
 * param    检索条件
 * isCallBackFn : 调用是否继续调用回调函数true调用false不调用
 */
function sfwQueryUtil(instance,method,param,isCallBackFn) {
	suggestQuery(instance,myPath()+"/process/synthesis/suggestFrameworkAction!"+method+".do",{"dto['jstj']":param},isCallBackFn);
}

/*
 * instance 有下拉提示的文本的序号，代表一个实例
 * method   方法名
 * param    检索条件
 * isCallBackFn : 调用是否继续调用回调函数true调用false不调用
 */
function sfwQueryUtilDY(instance,method,param,isCallBackFn,aae140) {
	suggestQuery(instance,myPath()+"/process/synthesis/suggestFrameworkAction!"+method+".do",{"dto['jstj']":param,"dto['aae140']":aae140},isCallBackFn);
}

/**
*获取basePath
*/
function myPath() {
	var pathName = document.location.pathname;
	var index = pathName.substr(1).indexOf("/");
	var result = pathName.substr(0, index + 1);
	return result;
}
/*
 * instance 有下拉提示的文本的序号，代表一个实例
 * method   方法名
 * param    检索条件
 */
function sfwQueryUtil(instance,method,param) {
	suggestQuery(instance,myPath()+"/process/synthesis/suggestFrameworkAction!"+method+".do",{"dto['jstj']":param});
}
function sfwQueryUtil_newRPC(instance,method,param) {
	suggestQuery(instance,myPath()+"/process/synthesis/suggestFrameworkAction!"+method+".do",param);
}

/**
 * instance 有下拉提示的文本的序号，代表一个实例
 * method   方法名
 * param    检索条件
 * isCallBackFn : 调用是否继续调用回调函数true调用false不调用
 */
function sfwQueryUtilForYl(instance,method,param,isCallBackFn) {
	suggestQuery(instance,myPath()+"/process/synthesis/suggestFrameworkAction!"+method+".do",param,isCallBackFn);
}
/**
*rapName 报表名   xxx.raq
*param  参数   数组格式,如['aab001=11111','aae140=110']
*isTrue 是否显示页面预览 true/false
*/
function printRaq(rapName,param,isTrue){
	var param_str = "";
	if(isTrue){
		for(var i =0 ;i < param.length;i++){
			param_str = param_str + param[i];
			if(i != param.length - 1){
				param_str = param_str + "&";
			}
		}
		var toUrl=myPath()+"/runqian/reportJsp/showReport.jsp?raq=/"+rapName+"&"+param_str ;
	    Base.openWindow("dayin","打印",toUrl,{},"80%","80%",null,null,true);				
	}else{
		for(var i =0 ;i < param.length;i++){
			param_str = param_str + param[i];
			if(i != param.length - 1){
				param_str = param_str + ";";
			}
		}
		if($("body").find("#report1_printIFrame").size() == 0){
		    $('<iframe id="report1_printIFrame" name="report1_printIFrame" width="100" height="100" style="position:absolute;left:-100px;top:-100px"></iframe>"').appendTo("body");
		}
		$("#report1_printIFrame").attr("src",myPath()+"/reportServlet?action=2&name=report1&reportFileName= "+rapName+"&"
			      + "srcType=file&savePrintSetup=yes&appletJarName=runqian/runqianReport4Applet.jar%2Crunqian/dmGraphApplet.jar&"
			      + "serverPagedPrint=no&mirror=no&paramString="+param_str);
	}
}

/**
 * @param instance 有下拉提示的文本的序号，代表一个实例
 * @param method   方法名
 * @param param    检索条件
 * @param isCallBackFn : 调用是否继续调用回调函数true调用false不调用
 * @param localData 本地数据
 * 
 * 注:此方法主要用于医疗业务的三目数据和病种数据 
 */
function sfwQueryUtilForMedicare(instance,method,param,isCallBackFn,localData)
{
	//如果没有参数则直接返回
	if (param == null || param == "") return;
	// 输入的字段全部大写与数据库匹配
	param = param.toUpperCase();
	//本地数据不为空
	if (localData != null)
	{
		localData.replace(/\s/g,''); // 删除所有的空格和换行符
		var data =  eval(localData);
		var thrData = [];
		//列表头
		thrData.push(data[0]);
		//数据长度
		var length = data.length;
		var lengthZ = length/2;
		var data1 = "";
		var data2 = "";
		for (var i = 1; i <= lengthZ; i ++) 
		{
			//取到20条就不再取
			if (thrData.length > 100) break;
			//从数据开始处获取匹配数据
			for (var j = 0; j < data[i].length; j ++) 
			{
				data1 = data[i][j];
				data1 = data1.toUpperCase();
				if (data1.indexOf(param) != -1)
				{
					thrData.push(data[i]);
					break;
				}
			}
			//当i=length/2时,避免数据被重复取
			if(length - i > lengthZ)
			{
				//从数据结尾处获取匹配数据
				for (var j = 0; j < data[length - i].length; j ++) 
				{
					data2 = data[length - i][j];
					data2 = data2.toUpperCase();
					if (data2.indexOf(param) != -1)
					{
						thrData.push(data[length - i]);
						break;
					}
				}
			}
		}
		suggestQueryForMedicare(instance,myPath()+"/process/comm/suggestFrameworkAction!"+method+".do",{"dto['jstj']":param},isCallBackFn,thrData);
	}	
}

/**
 * @param instance 有下拉提示的文本的序号，代表一个实例
 * @param url      获取数据的url，例如"<%=path%>/g02Action!getAaa201.do"
 * @param param    参数{"dto['aaz100_mc']":phrase,"dto['aaa143']":"1"}
 * @param thrData  匹配数据 
 * 注:此方法主要用于医疗业务的三目数据和病种数据 
 */
function suggestQueryForMedicare(instance, url, param,isCallBackFn,thrData) 
{
	sfwThrottle(instance);
	var phrase = sfw[instance].inputContainer.value;
	if (phrase == "" || phrase == sfw[instance].previous) return;
	sfw[instance].previous = phrase;
	if (event != null) 
	{
		event.cancelBubble = true;
		event.returnValue = false;
	}
	var outstring = "";
	if (thrData != null)
	{
		setTimeout(function (){sfwSuggest(instance, thrData,isCallBackFn)}, 20);
	} else 
	{
		//Base.submit(null, url, param, null, false, function (data) {
			//var outstring = data.fieldData.data;
			//sfwSuggest(instance, eval(outstring),isCallBackFn);
		//});
	}
}


