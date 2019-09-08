/**
 * 润乾报表打印导出方法,调用方式为Base.xxx(),调用这些方法需要保证报表是使用了Ta+3框架的润乾报表模板管理功能将报表上传到数据库中;
 * @module Base
 * @class print
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Base:core()
    });
	
	function core(){
		
		return {
			exportAsExcel : exportAsExcel,
			exportAsPdf : exportAsPdf,
			exportAsWord : exportAsWord,
			exportAsText : exportAsText,
			print : print,
			directPrint : directPrint,
			flashPrint : flashPrint,
			openReport : openReport,
			doPrint : doPrint,
			printForTablePage:printForTablePage
		};
		/**
		 * 导出报表为Excel
		 * <br/>var options = {};
		 * <br/>options.saveAsName = "门诊医疗卫生申请表"; 设置导出文件名
		 * <br/>options.paged = false;  设置分页，默认不分页（分页1、不分页0）
		 * <br/>options.formula = false;  设置是否导出公式，默认为否（是1、否0）
		 * <br/>options.excelFormat = "2003";  设置导出格式为2003（2003、2007、OpenXML）
		 * <br/>options.ratio = 80; 设置显示比例
		 * @method exportAsExcel
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他设置
		 * 
		 */
		function exportAsExcel(raqfilename, args, options) {
			var defaultType = {
				action : "3",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}
		/**
		 * 导出报表为PDF
		 * <br/>var options = {};
		 * <br/>options.paged = false; 设置分页，默认分页（分页1、不分页0）
		 * <br/>options.expStyle = "graph"; 设置导出文字默认为图形方式（图形graph，文本text）
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsPdf("DATA1521",args);
		 * @method exportAsPdf
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133"; args[1]= "arg3=0";
		 * @param {Object} options 其他设置
		 * 
		 */
		function exportAsPdf(raqfilename, args, options) {
			var defaultType = {
				action : "6",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * 导出报表为Word
		 * <br/>var options = {};
		 * <br/>options.columns = 2; 报表分栏数
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsWord("DATA1521",args);
		 * @method exportAsWord
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他参数
		 * 
		 */
		function exportAsWord(raqfilename, args, options) {
			var defaultType = {
				action : "7",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}
		/**
		 * 导出报表为Txt
		 * <br/>var options = {};
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsText("DATA1521",args);
		 * @method exportAsText
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他参数
		 * 
		 */
		function exportAsText(raqfilename, args, options) {
			var defaultType = {
				action : "18",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * 预览打印
		 * @method print
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如: var args = [];args[0]= "arg1=YAE133"; args[1]= "arg3=0";
		 * @param {Object} options 保存打印设置（savePrintSetup）,大数据分页传输打印（serverPagedPrint）
		 * 和分栏（columns），如options.savePrintSetup=true
		 */
		function print(raqfilename, args, options) {
			var defaultType = {
				action : "2",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}
	
		/**
		 * 直接打印
		 * @method directPrint
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 保存打印（savePrintSetup）,大数据分页传输打印（serverPagedPrint），选择打印机（needSelectPrinter）、打印机名称（printerName）
		 * 和分栏（columns），如options.savePrintSetup=yes 
		 */
		function directPrint(raqfilename, args, options) {
			var defaultType = {
				action : "30",
				raqid : raqfilename
			};
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * flash打印
		 * @method flashPrint
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 */
		function flashPrint(raqfilename,args) {
			var argStr = "";
			for ( var i in args) {
				if (i > 0) {
					argStr += ";" + args[i];
				} else {
					argStr += args[i];
				}
			}
			Base.submit(null,Base.globvar.contextPath+"/runqian/queryReportAction!flashPrint.do?raq="+raqfilename+"&paramString="+argStr,null,null,false,function(data){
				var cacheId = data.fieldData.cacheId;
				if(null != cacheId){
					var o = {};
					o.ctxPath = Base.globvar.contextPath;
					o.name = "report1";
					o.reportFileName = raqfilename;
					o.cacheId = cacheId;
					rq_flashPrint(o);
				}
			});
		}

		/**
		 * 打开预览报表窗口
		 * @method openReport
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Object} options 窗口的宽高，如：options.width=800,options.height=600
		 * @param {String} args 参数串key1=value1;key2=value2;key3=...
		 */
		function openReport(raqfilename,options,args){
			if(!args)args = "";
			var option = {
					name:"报表",
					width:800,
			        height:600
			};
			option = $.extend({},option,options);
			Base.openWindow(raqfilename, option.name, Base.globvar.contextPath+"/runqian/queryReportAction.do?raq="+raqfilename+"&raqParam="+args, 
					null, option.width, option.height, null, null, true);
		}

		/**
		 * 打印导出方法的入口，不要直接调用
		 * @method doPrint
		 * @private
		 * @param {Object} type
		 * @param {Array} args
		 * @param {Object} options
		 */
		function doPrint(type, args ,options) {
			//pengwei新增：判断报表名称不为空
			if(!type.raqid){
				Base.alert("报表名称不能为空!","error");
				return;
			}
			var defaultType = {
				action : "30",
				raqid :  "" 
			};
			defaultType = $.extend({}, defaultType, type);
			var defaultOptions = {
				saveAsName : defaultType.raqid,  //导出文件名称，默认去raq名称
				columns : 0,                     //是否分栏，必须和raq模板里设置的一致
				savePrintSetup : "no",           //是否保存打印设置，默认不开启
				serverPagedPrint : "no",         //大数据分页传输打印，默认不开启
				paged : false,                   //导出是否分页
				formula : false,                 //excel导出是否导出表达式
				excelFormat : "2003",            //excle导出格式，有2003、2007、OpenXML可选
				ratio : 80,                      //导出显示比例，默认80%
				expStyle : "graph",              //pdf导出格式，默认图片格式，text为文本格式
				needSelectPrinter : "no",        //直接打印是否显示打印机选择界面，默认不显示
				printerName : "no"               //直接打印指定打印机，不设置则使用默认打印机
				
			};
			var actionType = defaultType.action;
			//导出pdf默认分页
			if(actionType == "6"){
				defaultOptions.paged = true;
			}
			defaultOptions = $.extend({},defaultOptions,options);
			defaultOptions.paged = defaultOptions.paged?"1":"0";
			defaultOptions.formula = defaultOptions.formula?"1":"0";
			var raqidStr = "&raq=" + defaultType.raqid;
			var argStr = "";
			if (args != undefined) {
				argStr += "&paramString=";
			}
			for ( var i in args) {
				if (i > 0) {
					argStr += ";" + args[i];
				} else {
					argStr += args[i];
				}
			}
			if($("body").find("#printIframe").size() == 0){
			    $('<iframe id="printIframe" width="50" height="50" style="position:absolute;left:-100px;top:-100px"></iframe>"').appendTo("body");
			}
			$("#printIframe").attr("src", Base.globvar.contextPath
					+ "/runqian/queryReportAction!printReport.do?action="
					+ defaultType.action + raqidStr + argStr
					+"&saveAsName="+defaultOptions.saveAsName
					+"&paged="+defaultOptions.paged
					+"&formula="+defaultOptions.formula
					+"&excelFormat="+defaultOptions.excelFormat
					+"&expStyle="+defaultOptions.expStyle
					+"&ratio="+defaultOptions.ratio
					+"&columns="+defaultOptions.columns
					+"&savePrintSetup="+defaultOptions.savePrintSetup
					+"&serverPagedPrint="+defaultOptions.serverPagedPrint
					+"&needSelectPrinter="+defaultOptions.needSelectPrinter
					+"&printerName="+defaultOptions.printerName);
		}
		
		/**
		 * web打印
		 * @method printForTablePage
		 * @param {String} id
		 * @param {Object} options
		 */
		function printForTablePage(id,options) {
			$("#"+id).printForTablePage(options);
		}
	}
}));
