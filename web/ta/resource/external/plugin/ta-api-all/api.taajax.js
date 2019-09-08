/**
 * ajax前后台数据交互方法，调用方式为Base.xxx();
 * @module Base
 * @class taajax
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
			_enableButtonLayout : _enableButtonLayout,
			showMask : showMask,
			hideMask : hideMask,
			getJson : getJson,
			submit : submit,
			submitForm : submitForm,
			loadValues : loadValues,
			_ajax : _ajax,
			_doSubmitIds : _doSubmitIds,
			_dealdata : _dealdata,
			compareOldData : compareOldData
		};
		/**
		 * 设置所有按钮所在面板是否可用
		 * @method _enableButtonLayout
		 * @private
		 * @param {boolean} enable
		 */
		function _enableButtonLayout(enable){
			if(enable){
				$("div.panel-button,div.button-panel,div.panel-toolbar").each(function(){
					//$(this).attr('disabled','disabled');
					$("<div class='enableButtonLayout' style='top:"+$(this).offset().top+"px;left:"+$(this).offset().left+"px;height:"+$(this).outerHeight(true)+"px;width:"+$(this).outerWidth(true)+"px'></div>").appendTo($('body'));
				});
				Base.globvar.isSubmitNow = true;
			}else{
				Base.hideMask();
			}
		}
		/**
		 * 让某一个面板出现半透明蒙层，提示：读取中
		 * @method showMask
		 * @param {String} id  面板的id，如果不传或null就是整个页面
		 * @param {Boolean} showLoading  是否显示loading的图片和文字，默认为true，如果设置为false不显示图片和文字
		 */
		function showMask(id,showLoading){
			var height = $(window).height(),width = $(window).width(),top=0,left=0;
			var obj = null;
			if(id && (obj=Base.getObj(id))){
				var $obj = $(obj);
				height = $obj.height();
				width = $obj.width();
				top = $obj.offset().top;
				left = $obj.offset().left;
				
				if($obj.hasClass('panel') && $('>div.panel-header',$obj).length>0){
					top += $('>div.panel-header',$obj).outerHeight(true);
					height -= $('>div.panel-header',$obj).outerHeight(true);
				}else if(obj.tagName.toLowerCase()=='fieldset'){
					top += 22;
					height -=5;
					width +=18;
				}
				
			}
			var loadding = "";
			if(showLoading === false){
			}else{
				loadding = "<div style='left:"+((width-left)/2-20)+"px;top:"+((height-top)/2+10)+"px;width:60px;height:30px;opacity:1;position: absolute;font-size:12px'>读取中...</div>";
			}
			
			$("<div class='enableButtonLayout "+(showLoading===false?"":"loading")+"' style='top:"+top+"px;left:"+left+"px;height:"+height+"px;width:"+width+"px'>"+loadding+"</div>").appendTo($('body'));
		}
		/**
		 * 隐藏蒙层
		 * @method hideMask
		 */
		function hideMask(){
			$("body >div.enableButtonLayout").remove();
			Base.globvar.isSubmitNow = false;
		}
		/**
		 * 同步或异步到后台获取返回json格式的内容，默认同步执行
		 * @method getJson
		 * @param {String} url action地址
		 * @param {Object} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} callback 返回成功后的回调，入参返回的为json对象和XMLHttpRequest对象，
		 *							这里不需要success为true标志，只要后台成功返回json格式的数据都会回调,
		 *							<br>例如：function(data){var dataArray = eval(data.fieldData.datalist);},
		 *							<br>其中，data为后台返回的数据，datalist是你在action中绑定的id(setData('datalist',obj)),
		 *							<br>dataArray是一个数组,访问方式为：dataArray[1].aac003.
		 *							<br>注意:action中也可以setList('grid1',list),但不建议在此使用。 
		 *							
		 * @param {boolean} async 设置是否异步，默认为false异步
		 * @return 返回的json对象
		 			<br>1.当action中以setData('','')返回时，此对象的调用方式和callback里的方式一样
		 			<br>2.当action中以writeJsonToClient(obj)返回时，此对象为一个数组。
		 */
		function getJson(url,parameter,callback,async){
			var ret;
			url = (url.indexOf('?')==-1?(url+"?_r="+Math.random()):(url+"&_r="+Math.random()));
			Base._ajax({
				url:url,
				"type":'POST',
				data:parameter,
				success:function(data){
					if(!$.isArray(data) && !$.isEmptyObject(data)){
						var tempData = data.fieldData,newData = {};
						for(var i in tempData){
							if(i.indexOf("_sel_") == 0){
								newData[i.substring(5)] = tempData[i];
							}else{
								newData[i] = tempData[i];
							}
						}
						data.fieldData = newData;
					}
					ret = data;
					if (typeof callback == "function") {
						callback(data);
					}
				},
				async:async===true?true:false,
				dataType:'json'
			});
			return ret;
		}
		/**
		 * 异步提交表单，action必须返回JSON或者null，此方法不能用于页面跳转，通常用于返回表格数据
		 * <br>能够自动校验表单
		 * <br>能够对后台返回的json进行自动处理。
		 * <br> 处理如下：
		 * <br>  1、有消息自动提示（根据不同类型提示不同类型的提示框）
		 * <br>  2、如果有返回自由格式的内容自动给页面对应的输入对象赋值
		 * <br>  3、如果有列表的值，自动给所有列表更新列表内容
		 * <br>  4、如果有对表单输入对象或按钮的控制内容，自动根据数据进行控制
		 * <br>  5、如果有校验不通过的自动设置不通过的输入对象为校验失败的样式，同时第一个元素获取焦点。
		 * <br>  6、如果有设置焦点的数据，自动给数据对应的对象获取焦点。
		 * @method submit
		 * @param {String} submitIds  需要传递到后台的对象id或容器id,多个id可以用”,“隔开
		 * @param {String} url 提交的地址
		 * @param {Object/String} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} onSubmit 提交前手动检查，如果返回false将不再提交,必须返回true或false
		 * @param {Boolean} autoValidate 默认false 是否自动调用Base.validateForm对ids对象进行校验，如果校验失败将不再提交
		 * @param {Function} succCallback callback 返回业务成功后的回调，入参返回的为json对象和XMLHttpRequest对象
		 											<br>例如：function(data){alert(data.lists.grid2.list[0].aac003)}，
		 											<br>其中data为返回的json数据，grid2是在action中绑定的id(setList('grid2',list))，通常是jsp中datagrid的id.
		 * @param {Function} failCallback  业务失败回调，入参返回的为json对象和XMLHttpRequest对象
		 */
		function submit(submitIds,url,parameter,onSubmit,autoValidate,_succCallback,_failCallback,isIncludeNullFields,token){
			if (token == null) token = true;
			autoValidate = (autoValidate===false?false:true);
			if((onSubmit && !onSubmit()) || (autoValidate && !Base.validateForm(submitIds))){
				Base.hideMask();
				return false;
			}
			//在300毫秒以内不显示蒙层
			Base._enableButtonLayout(false);
			var showMaskTime = setTimeout(function(){
				Base.showMask("body");
			}, 300);
			submitIds = submitIds?submitIds:"";
			var aids = submitIds.split(',');
			
			var queryStr = _doSubmitIds(aids, parameter,isIncludeNullFields, token);
			if(false){//如果是文件上传
				var form = Base.getObj(submitIds);
				if(aids.length==1 && form && form.tagName=='FORM'){
					$(form).attr('action',url);
					$(form).attr("enctype","multipart/form-data");
					form.submit();
				}else{
					if(Base.globvar.developMode)alert('文件上传只能传入唯一的form元素id');
				}
			}else{
				
				//Base.getHandleStatus
				//根据ids拼接传递的条件字符串
				
				Base._ajax({
					"url":url,
					"data":queryStr,
					"succCallback":function(data,dataType){
						//data.replaceAll("%0D%0A","\r\n");
						clearTimeout(showMaskTime);
						Base._dealdata(data);
						Base.hideMask();
						if(_succCallback)_succCallback(data,dataType);
						Base._enableButtonLayout(false);
					},
					"failCallback":function(data,dataType){
						//data.replaceAll("%0D%0A","\r\n");
						Base._dealdata(data);
						Base.hideMask();
						if(_failCallback)_failCallback(data,dataType);
						Base._enableButtonLayout(false);
					},
					"type":'POST'
					//,
					//"async":(async===false?false:true)
					
					,"dataType":"json"
					
				});
			}
		}
		/**
		 * 同步提交form。
		 * 主要用途，表单提交后要刷新整个页面或跳转到其他页面的时候以及需要使用文件上传功能的时候使用
		 * @method submitForm
		 * @param {String} formId form表单的id ,<b>必传</b> 
		 * @param {Function} onSubmit 提交前执行的函数，如果返回false就不在继续提交表单
		 * @param {Boolean} autoValidate 是否对表单进行自动校验，默认为true。
		 * @param {String} url 提交的url,如果不传，请在form标签里面的aciton属性填写。
		 * @param {String} parameter 参数json格式{"dto['aac001']":"1000001","dto['aac002']":"01"}。
		*/
		function submitForm(formId,onSubmit,autoValidate,url,parameter){
			Base.showMask();
			if(formId){
				form = document.getElementById(formId);
			}else{
				alert('传入formId为空');
				return false;		
			}
			if(!form){
				alert('找不到需要提交的form元素');
				return false;
			}
			if((onSubmit && !onSubmit()) || (autoValidate && !Base.validateForm(formId))){
				Base.hideMask();
				return false;
			}
			if(parameter){
				url += "?" + jQuery.param(parameter);
			}
			$form = $(form);
			if(url){//ie8不识别form.action
				$form.attr("action",url);
			}
			$("div.datagrid").each(function(){
			    var gridId = $(this).attr('id');
				if(!document.getElementById(gridId+'_selected')){
					$form.append("<input type=\"hidden\" id=\""+gridId+"_selected\" name=\"gridInfo['"+gridId+"_selected']\"/>");
					document.getElementById(gridId+'_selected').value=Ta.util.obj2string(Base.getGridSelectedRows(gridId));
				}else{
					document.getElementById(gridId+'_selected').value=Ta.util.obj2string(Base.getGridSelectedRows(gridId));
				}
				
				if(!document.getElementById(gridId+'_modified'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_modified\" name=\"gridInfo['"+gridId+"_modified']\" value=\""+Ta.util.obj2string(Base.getGridModifiedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_modified').value=Ta.util.obj2string(Base.getGridModifiedRows(gridId));
				if(!document.getElementById(gridId+'_removed'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_removed\" name=\"gridInfo['"+gridId+"_removed']\" value=\""+Ta.util.obj2string(Base.getGridRemovedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_removed').value=Ta.util.obj2string(Base.getGridRemovedRows(gridId));
				
				if(!document.getElementById(gridId+'_added'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_added\" name=\"gridInfo['"+gridId+"_added']\" value=\""+Ta.util.obj2string(Base.getGridAddedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_added').value=Ta.util.obj2string(Base.getGridAddedRows(gridId));
			});
//			var $tempSubmit = null;
//			if ($("#__submitkey__").val() != undefined) {
//				var $form = $(form);
//				var value = $("#__submitkey__").val();
//				$tempSubmit = $("<input type=\"hidden\" " + "value=\"" + value + "\"" + "name=\"__submitkey__\"/>");
//				$form.append($tempSubmit);
//			}
			form.submit();
			
//			if ($tempSubmit != null) {
//				$tempSubmit.remove();
//			}
		}
		/**
		 * 根据某些输入表单的值获取页面数据
		 * 对返回数据的处理如submitform。
		 * @method loadValues
		 * @private
		 * @param {String/Array} submitids 指定作为参数的输入对象的id或name，如果多个请传入输入,如:["aac001","aac002"]
		 * 								   框架会自动获取这些输入对象的值作为参数传递过去。 可以为空。
		 * @param {Object} parameter 手工传入参数 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} succCallback 返回业务成功后的回调，入参返回的为json对象和XMLHttpRequest对象
		 * @param {Function} failCallbackak  业务失败或系统异常失败回调，入参XMLHttpRequest对象
		 * @param {boolean} async async 设置是否异步，默认为true
		 * @deprecated
		 */
		function loadValues(submitids,url,parameter,succCallback,failCallback,async){
			
		}
	
		/**
		 * 异步或同步交互
		 * options["succCallback"] 返回的数据里面有success=true  被调用
		 * options["failCallback"] 返回的数据里面有success=false  被调用
		 * 其他配置选项与jQuery.ajax一样
		 * @param {object} options jQuery.ajax的配置项
		 */
		//var tempajax = $.ajax;
		function _ajax(options){

//			url,parameter,succCallback,failCallback,type,async
			var _options = options;
			if(!_options["url"]){
				throw "Base._ajax方法必须传入URL";
			}
			//将url中的中文转换成utf-8
			_options["url"] = encodeURI(_options["url"]);
			var error = false;//http错误
			var _data = null,dataType="";//返回的数据
			var succCallback = options["succCallback"],failCallback = options["failCallback"];
			delete _options["succCallback"];
			delete _options["failCallback"];
			
			_options["complete"] = function(_XMLHTTPRequest,textStatus){
					Base._enableButtonLayout(false);
					if(_XMLHTTPRequest && _XMLHTTPRequest.getResponseHeader){
					    if(_XMLHTTPRequest.getResponseHeader('__timeout')) {
					    	alert('操作提示：会话已经超时，请重新登录!');
				        	top.location.href='index.jsp?randId=' + parseInt(1000*Math.random());
				        	return;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__forbidden')){
				    		Base.alert(['系统访问权限提示：','你目前没有权限访问：',this.url].join(','));
				    		return;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__exception')){
				    		Base._dealdata(eval("("+_XMLHTTPRequest.responseText+")"));
				    		return ;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__samelogin')){
				    		alert('帐号在其他地方登录，您已被迫下线！');
				    		top.location.href='index.jsp?randId=' + parseInt(1000*Math.random());
				    		return ;
				    	}
					}
					if(error){//异常
						if(textStatus==="parsererror"){//jquery解析错误
							alert(['返回的数据格式不满足json格式，解析错误:\n',_data].join(','));
						}else{
							alert(['执行发生异常,可能网络连接失败'].join(','));
						}
					}else{
						if(_data){
							if(_data.success || typeof _data === 'string'){//业务成功success==true或返回的是字符串
								if(succCallback)
									succCallback.call(this,_data,dataType);
								
							}else if(_data.success!= undefined && _data.success.toString().toLowerCase()=="false"){//业务失败
								if(failCallback)
									failCallback.call(this,_data,dataType);
								
							}else{//TODO 其他类型的返回
								
							}
						}
					}

			};
			var success = _options["success"];
			_options["success"] = success?function(data,statusText){
					_data = data;
					success(data,statusText);
				}:function(data,statusText){
				_data = data;
			};
			_options["error"] = function(_XMLHTTPRequest,errmsg,exception){
					//errmsg需要处理timeout/parseerror情况
					//其他异常不特殊告诉，直接显示
					_data = _XMLHTTPRequest.responseText;
					error = true;
			};
			_options["dataFilter"] = function(data,type){
				dataType = type;
				return data;
			};
			_options["beforeSend"] = function(_XMLHTTPRequest){//确保post的时候不会乱码
				_XMLHTTPRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8"); 
				return true;
			};
			jQuery.ajax(_options);
			//tempajax(_options);
		}
		////////////提取方法
			function _doSubmitIds(aids, parameter, isIncludeNullFields, token) {
				var queryStr = "",datagridids = [];
//				aids.push("_token__tokenflag");
				if(aids){//Base.globvar.developMode
					for(var i=0;i<aids.length;i++){
						if(aids[i]==null || aids[i]=='')continue;
						var obj = Base.getObj(aids[i]);
						if (obj == undefined) continue;
						var $obj = $(obj);
						if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
						  (obj.tagName && (obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
							if(obj.cmptype=='flexbox'){
								obj = $("#"+aids[i]);//下拉框
								//判断是否提交 _md5list 防篡改
								var tmd5 = $("#"+aids[i]+"_md5list");
								if(tmd5[0]){
									if(queryStr=="")
										queryStr += $("#"+aids[i]+"_md5list").taserialize(isIncludeNullFields);
									else
										queryStr += "&"+$("#"+aids[i]+"_md5list").taserialize(isIncludeNullFields);
								}
							}	
							for(var j=0;j<aids.length;j++){//对ids进行校验，不能父子嵌套
								if(aids[j]==null || aids[j]=='')continue;
								var obj2 = Base.getObj(aids[j]);
								if(obj2.cmptype=='flexbox')obj2 = $("#"+aids[j]);
								if(i != j && obj2.cmptype!='datagrid'){//找到其他对象
									if($(obj).has($(obj2)).length>0){
										alert(aids[j]+"对象在"+aids[i]+"对象里面，指定提交的元素id不能有包含与被包含关系");
										return false;
									}
									if($(obj2).has($(obj)).length>0){
										alert(aids[i]+"对象在"+aids[j]+"对象里面，指定提交的元素id不能有包含与被包含关系");
										return false;
									}
								}
							}
							if ($obj.hasClass("amountfield")){
								if(queryStr=="")
									queryStr += $("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);
								else
									queryStr += "&"+$("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);	
							}
							if(queryStr=="")
								queryStr += $("#"+aids[i]).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("#"+aids[i]).taserialize(isIncludeNullFields);
						 		//queryStr = queryStr.replaceAll("%0D%0A","\r\n");
						}
						else if($obj.hasClass("amountfield")){
							if(queryStr=="")
								queryStr += $("#"+aids[i]+"_hidden").taserializeisIncludeNullFields();
							else
								queryStr += "&"+$("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);	
							//obj=$("#" + aids[i] + "_hidden");//金额框
						}
						else if(obj.cmptype=='datagrid'){
							datagridids.push(new String(aids[i]));
							if(queryStr=="")
								queryStr += $("#"+aids[i]).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("#"+aids[i]).taserialize(isIncludeNullFields);	
							aids[i]=null;//.splice(i,1);//删除当前id
						}
						else{
							alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
							return false;
						}
					}
				}		
				
				//传表格隐藏信息
				$("div.datagrid").each(function(){
					for(var i=0;i<aids.length;i++){
						if(aids[i] && aids[i]!='' && $("#"+aids[i]).has($(this)).length==0){//不在adis包含的datagrid
							if(queryStr=="")
								queryStr = $("input:hidden[name^=gridInfo]",$(this)).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("input:hidden[name^=gridInfo]",$(this)).taserialize(isIncludeNullFields);
						}
						break;// for 循环无用
					}
				});
				//传递表格 added/selected/removed/modified 数据
				for(var i=0;i<datagridids.length;i++){
					var p = {};
					//alert([datagridids[i],Base.getGridSelectedRows(datagridids[i]),Ta.util.obj2string(Base.getGridSelectedRows(datagridids[i]))].join(','));
					p["gridInfo['"+datagridids[i]+"_selected']"] = Ta.util.obj2string(Base.getGridSelectedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_modified']"] = Ta.util.obj2string(Base.getGridModifiedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_removed']"] = Ta.util.obj2string(Base.getGridRemovedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_added']"] = Ta.util.obj2string(Base.getGridAddedRows(datagridids[i]));
					if(queryStr==""){
						queryStr = jQuery.param(p);
					}else{
						queryStr += "&"+jQuery.param(p);
					}
				}
				if (document._dataSubmitStore && document._dataSubmitStore.length > 0) {
					parameter = $.extend(parameter, document._dataSubmitStore[0]);
				}
				//对queryStr处理
				var parameterOld = Base.compareOldData(aids);
				parameter = $.extend(parameter, parameterOld);
				if(parameter){
					if(queryStr==""){
						queryStr = jQuery.param(parameter);
					}else{
						queryStr += "&"+jQuery.param(parameter);
					}
				}
				return queryStr;
			}


		function _dealdata(data){
				//只有json格式的时候才处理
				if(typeof data != "object")return;
				
				//有fieldData数据的时候
				if(data.fieldData){
//					var params = {},newData = data.fieldData;
//					for(var i in newData){
//						if(i.indexOf("_sel_") == 0){
//							params[i.substring(5)] = newData[i];
//						}else{
//							params[i] = newData[i];
//						}
//					}
					Base.setValue(data.fieldData);
					//data.fieldData = params;
				}
				//有validateErrors数据
				if(data.validateErrors){
					var focus = null,_errors = data.validateErrors;
					for(var fieldId in _errors){
						if(!focus)
							focus = fieldId;
						Base.setInvalidField(fieldId,_errors[fieldId]);
					}
					//如果后台没有设置focus，并且有validateErrors数据的时候就把焦点置于第一个错误的地方
					if(focus && !data.focus){
						data.focus = focus;
					}
				}
				//有lists数据
				if(data.lists){
					var _lists = data.lists;
					for(var list in _lists){
						if (list == "_dataSubmitStore")
							document._dataSubmitStore = _lists[list];
						if (list == "_oldValueObj")
							document._oldValueObj = _lists[list]['list'];
						Base._setGridData(list,_lists[list]);
					}
				}
				//有operation数据
				if(data.operation){
					var _operation = data.operation;
					for(var i=0;i<_operation.length;i++){
						var op = _operation[i];
						switch(op.type){
							case 'readonly':
								Base.setReadOnly(op.ids);
								break;
							case 'enable':
								Base.setEnable(op.ids);
								break;
							case 'disabled':
								Base.setDisabled(op.ids);
								break;					
							case 'select_tab':
								Base.selectTab(op.ids);
								break;				
							case 'hide':
								Base.hideObj(op.ids);
								break;
							case 'show':
								Base.showObj(op.ids);
								break;
							case 'unvisible':
								Base.hideObj(op.ids,true);
								break;					
							case 'resetForm':
								Base.resetForm(op.ids[0]);
								break;
							case 'required':
								Base.setRequired(op.ids);
								break;
							case 'disrequired':
								Base.setDisRequired(op.ids);
								break;						
						}
					}
				}
				
				//有msg
				if(data.msg){
					var focus = null;
					if(data.focus){
						focus = function(_fieldId){
							return function(){Base.focus(_fieldId,100);};
						}(data.focus);
					}
					var msg = data.msg;
					var developMode = Base.globvar.developMode;
					if(developMode){
						if(data.errorDetail){
							msg += "&nbsp;&nbsp;&nbsp;<div><a onClick=\"$('<div style=overflow:auto>'+$('#_expwinerrmsg').html()+'</div>').appendTo('body').window({width:600,height:400,title:'详细信息'})\">[查看详细]</a></div><div id='_expwinerrmsg' style='display:none'><hr>"+data.errorDetail+"</div>";
						}
					}
					Base.alert(msg,data.success?'success':'error',focus);
					
				}
				if (data.msgBox) {
					var focus = null;
					if(data.focus){
						focus = function(_fieldId){
							return function(){Base.focus(_fieldId,100);};
						}(data.focus);
					}
					var msg = data.msgBox.msg;
					if(data.errorDetail){
						msg += "&nbsp;&nbsp;&nbsp;<div><a onClick=\"$('<div style=overflow:auto>'+$('#_expwinerrmsg').html()+'</div>').appendTo('body').window({width:600,height:400,title:'详细信息'})\">[查看详细]</a></div><div id='_expwinerrmsg' style='display:none'><hr>"+data.errorDetail+"</div>";
					}
					Base.alert(msg,data.msgBox.msgType,focus);
				}
				//没有msg，但是有focus
				if(!data.msg && data.focus){
					Base.focus(data.focus,50);
				}
				//有topMsg
				if(data.topTipMsg){
					var topTip = data.topTipMsg;
					Base.msgTopTip(topTip.topMsg,topTip.time,topTip.width,topTip.height);
				}else if(data.topMsg && !data.topTipMsg){
					Base.msgTopTip(data.topMsg);
				}
			}
			
			
		function compareOldData(ids){
			var submitparam = {};
			
			if (document._oldValueObj && document._oldValueObj.length >0){
				var oldArray = document._oldValueObj;
				for (var i = 0; i < oldArray.length; i ++) {
					for (var j = 0; j < ids.length; j ++) {
						var $obj = $("#" + ids);
						if (ids[j] == oldArray[i].__id) {
							if ($obj.val() != oldArray[i]["ovDto['" + oldArray[i].__id + "']"]) {
								submitparam = $.extend(submitparam, oldArray[i]);
							}
						} else if ($obj.has(oldArray[i].__id)){
							if ($("#" + oldArray[i].__id).val() != oldArray[i]["ovDto['" + oldArray[i].__id + "']"]) {
								submitparam = $.extend(submitparam, oldArray[i]);
							}
						}
					}
				}
			} else {
				return {};
			}
			delete submitparam.__id;
			return submitparam;
		}

	}
}));
