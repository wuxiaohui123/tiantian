/**========================== 表单部分的操作=======================================*/
/**
 * form表单常用方法,调用方式为Base.xxx();
 * @module Base
 * @class forms
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
		/**
		 * 获取当前输入对象的前面一个输入对象或按钮。
		 * @method _getPreFormField
		 * @param {String} curid 当前对象id
		 * @return {Object} 前一个组件对象
		 */
		function _getPreFormField(curid){
			if (curid == null || curid == undefined || curid == "") return;
			var inWin = false;
			if($("#"+curid).parents('div.window-body').length>0){
				inWin = true;
			}
			var ids = Ta.core.TaUIManager.keys();
			var pre = null;
			for(var i=ids.length-1;i>=0;i--){
				if(ids[i]==curid){
					var m = i-1;
					if(i==(ids.length-1)){//当前对象恰好在最后
						m = ids.length-2;
					}
					for(;m>=0;m--){//从当前对象的后面一个开始想要可以置焦点的对象
						var srcobj = Ta.core.TaUIManager.getCmp(ids[m]);
						//寻找非只读，非disabled，非不可见
						if(srcobj.type && !srcobj.disabled && !srcobj.readOnly && !$(srcobj).is(':hidden')){
							  if(inWin){
						    	if($(srcobj).parents('div.window-body').length>0){
						    		pre = srcobj;
									break;
						    	}
						    }else{
								pre = srcobj;
								break;
							}
						}
					}	
				}
			}
			return pre;
		}

		/**
		 * 获取当前输入对象的后面一个输入对象或按钮。
		 * @method _getNextFormField
		 * @param {String} curid 当前id
		 * @return {Object} 后一个组件对象
		 */
		function _getNextFormField(curid){
			if (curid == null || curid == undefined || curid == "") return;
			var inWin = false;
			if($("#"+curid).parents('div.window-body').length>0){
				inWin = true;
			}
			var ids = Ta.core.TaUIManager.keys();
			var next = null;
			for(var i=0;i<ids.length;i++){
				if(ids[i]==curid){
					var m=i+1;
					if(i==(ids.length-1)){//当前对象恰好在最后
						m=0;
					}
					for(;m<ids.length;m++){//从当前对象的后面一个开始想要可以置焦点的对象
						var srcobj = Ta.core.TaUIManager.getCmp(ids[m]);
						//寻找非只读，非disabled，非不可见
						if(srcobj.type && !srcobj.disabled && !srcobj.readOnly && !$(srcobj).is(':hidden')){
						    if(inWin){
						    	if($(srcobj).parents('div.window-body').length>0){
						    		next = srcobj;
									break;
						    	}
						    }else{
								next = srcobj;
								break;
							}
						}
					}				
				}
			}
			return next;
		}
		/**
		 * 根据id获取对象。
		 * @method getObj
		 * @param {String} id 输入对象的id
		 * @return {Object}  有可能为：html元素，tree对象，datagrid对象，selectinput对象
		 */
		function getObj(id){
			var obj = Ta.core.TaUIManager.getCmp(id);
			if(obj)return obj;
			var obj = $("#"+id);
			if(obj[0])
				return obj[0];
			else {
				if(Base.globvar.developMode){
					//alert('没有找到id为'+id+'的对象');
				}
				return null;
			}
		}

		/**
		 * 获取输入对象的label。
		 * @method getFieldLabel
		 * @param {String} id 输入对象id
		 * @return {String} 输入对象label文字
		 */
		function getFieldLabel(id){
			var obj = Base.getObj(id);
			if(!obj)return;
			if(obj.cmptype=='flexbox'){
				return $("#"+obj.getId()).parent().parent().parent().find('>label.fieldLabel').text();
			}else if(obj.tagName){
				if(obj.type && (obj.type=="checkbox" || obj.type=='radio')){
					return $(obj.parentNode).find('>label').text();
				}else if(obj.tagName.toLowerCase()=='input'){
					return $(obj.parentNode.parentNode).find('>label.fieldLabel').text();
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){
					return $(obj).find('>label.fieldLabel').text();
				}
			}
		}
		/**
		 * 设置输入对象的label。
		 * @method setFieldLabel
		 * @param {String} id 输入对象id
		 * @param {String} lablel 新label
		 * @type String 
		 */
		function setFieldLabel(id,label){
			var obj = Base.getObj(id);
			if(!obj)return;
			if(label && label.indexOf("：")==-1){
				label  = label+"：";
			}
			if(obj.cmptype=='flexbox'){
				$("#"+obj.getId()).parent().parent().parent().find('>label.fieldLabel').text(label);
			}else if(obj.tagName){
				if(obj.type && (obj.type=="checkbox" || obj.type=='radio')){
					$(obj.parentNode).find('>label').text(label);
				}else if(obj.tagName.toLowerCase()=='input'){
					$(obj.parentNode.parentNode).find('>label.fieldLabel').text(label);
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){
					$(obj).find('>label.fieldLabel').text(label);
				}
			}
		}

		/**
		 * 设置输入对象为是否只读。
		 * @method _setReadOnly
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} isReadOnly  true 为只读，false为非只读
		 */
		function _setReadOnly(ids,isReadOnly){
			var fnsetReadonly = function(id,isReadOnly){
				var obj = Base.getObj(id);
				if(!obj)return false;
				
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					//obj.readOnly = 'true';
					var $obj = $(obj);
					$obj.attr('readOnly',isReadOnly);
					if(obj.type=="radio"){
						//pengwei 判断radio的readOnly状态
						var $par = $obj.parent();
						if(isReadOnly){
							if($par.hasClass("ta-radio-checked"))
								$par.removeClass("ta-radio-checked").addClass("ta-radio-checked-disabled");
							else if($par.hasClass("ta-radio-uncheck"))
								$par.addClass("ta-radio-uncheck-disabled");
						}else{
							if($par.hasClass("ta-radio-checked-disabled"))
								$par.removeClass("ta-radio-checked-disabled").addClass("ta-radio-checked");
							else if($par.hasClass("ta-radio-uncheck-disabled"))
								$par.removeClass("ta-radio-uncheck-disabled");
						}
							
						//$obj.attr('disabled','disabled');
					}
					if(obj.type=="checkbox"){
						//pengwei 
						var $par = $obj.parent();
						if(isReadOnly){
							if($par.hasClass("ta-chk-checked"))
								$par.removeClass("ta-chk-checked").addClass("ta-chk-checked-disabled");
							else if($par.hasClass("ta-chk-uncheck"))
								$par.removeClass("ta-chk-uncheck").addClass("ta-chk-uncheck-disabled");
						}else{
							if($par.hasClass("ta-chk-checked-disabled"))
								$par.removeClass("ta-chk-checked-disabled").addClass("ta-chk-checked");
							else if($par.hasClass("ta-chk-uncheck-disabled"))
								$par.removeClass("ta-chk-uncheck-disabled").addClass("ta-chk-uncheck");
						}
						
//						var clickValue = $obj.attr("onclick");
//						if((clickValue!=null&&clickValue!="") || ($obj.data("_tempclick")!=""&&$obj.data("_tempclick")!=null)){
//							if(isReadOnly){
//								if(clickValue!=null&&clickValue!=""){
//									$obj.data("_tempclick", clickValue);
//								}
//								$obj.removeAttr('onclick');
//								$obj.unbind("click").bind('click',function() { return false; });
//							}else{
//								if($obj.data("_tempclick")){
//									$obj.unbind('click').bind('click',eval($obj.data("_tempclick")));
//								}else{
//									$obj.unbind('click');
//								}
//							}
//						}else{
//							if(isReadOnly){
//								$obj.bind('click',function() { return false; });
//							}else{
//								$obj.unbind('click');
//							}
//						}
					}
					if(isReadOnly){
						$obj.addClass('readonly');
						$obj.parent("div.fielddiv2").addClass("readonly");
//							if(obj.type=="checkbox"){//针对checkbox在readonly的时候做处理
//							$(obj).unbind('.checkboxReadOnly').bind('click.checkboxReadOnly',function(){
//								this.checked = !this.checked;
//							});
//							}
						if($obj.hasClass("Wdate")){//只读时在时间输入框上面添加一透明div,防止用户点击只读输入框弹出时间选择panel
							if($obj.next(".shadingWdate").length == 0){
								$obj.after($("<div class='shadingWdate'></div>"));
							}else{
								$obj.next(".shadingWdate").show();
							}
						}
					}else{
						$obj.removeClass('readonly');
						$obj.parent("div").removeClass('readonly');
						$(obj).unbind('.checkboxReadOnly');
					}
					
				}else if(obj.cmptype == "flexbox"){
					obj.readOnly(isReadOnly);
				}
			};
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i]!= undefined){
						var o = Base.getObj(ids[i]);
						var $o = $(o);
						if(o && o.tagName && (o.tagName == 'FIELDSET' || o.tagName == "DIV" || o.tagName == "FORM")) {
							$o.find("input.ffb-input").each(function(){
								var a = this.id.replace("_desc", "");
								Base.getObj(a).readOnly(isReadOnly);
							});
							$o.find(":input[type!=hidden] ").not(".ffb-input").not(".datagrid :input").each(function() {
								fnsetReadonly(this.id,isReadOnly);
							});
						} else 
							fnsetReadonly(ids[i],isReadOnly);
					}
				}
			}
		}
		/**
		 * 设置输入对象为是否只读，只读模式下如果组件有值，仍然会向后台传递。
		 * <br/>例如：
		 * <br/>Base.setReadOnly("aac001");
		 * <br/>Base.setReadOnly(["aac001","aac002"]);
		 * <br/>Base.setReadOnly("aac001,aac002");
		 * @method setReadOnly
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]数组，或者逗号,分开如aac001,aac002
		 */
		function setReadOnly(ids){
			Base._setReadOnly(ids,true);
		}

		/**
		 * 设置输入对象为只读。
		 * @method _setEnable
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} enable  true 为disabled=true，false为disabled=false
		 */
		function _setEnable(ids,enable){
		   var fnsetEnable = function(id,enable){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					var $obj = $(obj);
					if(obj.tagName.toLowerCase()=='input' || obj.tagName.toLowerCase()=='textarea'){
						if(obj.type=="radio"){//处理radio的只读可写
							var $par = $obj.parent();
							if(enable){
								if($par.hasClass("ta-radio-checked-disabled"))
									$par.removeClass("ta-radio-checked-disabled").addClass("ta-radio-checked");
								else if($par.hasClass("ta-radio-uncheck-disabled"))
									$par.removeClass("ta-radio-uncheck-disabled");
								
								$obj.removeAttr('disabled');
								$obj.removeAttr('readOnly');
							}else{
								if($par.hasClass("ta-radio-checked"))
									$par.removeClass("ta-radio-checked").addClass("ta-radio-checked-disabled");
								else if($par.hasClass("ta-radio-uncheck"))
									$par.addClass("ta-radio-uncheck-disabled");
								
								$obj.attr('disabled','disabled');
							}
						}if(obj.type=="checkbox"){//处理checkbox的只读可写
							var $par = $obj.parent();
							if(enable){
								if($par.hasClass("ta-chk-checked-disabled"))
									$par.removeClass("ta-chk-checked-disabled").addClass("ta-chk-checked");
								else if($par.hasClass("ta-chk-uncheck-disabled"))
									$par.removeClass("ta-chk-uncheck-disabled");
								
								$obj.removeAttr('disabled');
								$obj.removeAttr('readOnly');
							}else{
								if($par.hasClass("ta-chk-checked"))
									$par.removeClass("ta-chk-checked").addClass("ta-chk-checked-disabled");
								else if($par.hasClass("ta-chk-uncheck"))
									$par.addClass("ta-chk-uncheck-disabled");
								
								$obj.attr('disabled','disabled');
							}
						}else{
							$obj.attr('disabled',!enable);
							if(enable){
								$obj.removeClass('disabled');
								Base._setReadOnly(id,false);//同时把readonly也设置为false
								if($obj.hasClass("Wdate")){//删除时间输入框上面透明的div
									$obj.next(".shadingWdate").remove();
								}
								$obj.parent("div.fielddiv2").removeClass("disabled");
							}else{
								$obj.addClass('disabled');
								$obj.parent("div.fielddiv2").addClass("disabled");
							}
						}
					}else if($obj.hasClass('checkboxgroup') || $obj.hasClass('radiogroup')){
						$obj.find('input').attr('disabled',!enable);
					}else if(obj.tagName.toLowerCase()=='fieldset' || $(obj).hasClass('panel')){
						if(enable)
							Base.hideMask();
						else
							Base.showMask(id,false);
					}else if((obj.tagName.toLowerCase()=='input' &&  obj.type=='button')|| obj.tagName.toLowerCase()=='button'){
						enable?(obj.disabled=false):(obj.disabled=true);
						if(obj.type=="checkbox"){//针对checkbox在readonly的时候做处理
							if($(obj).attr('readOnly')==true){
								$(obj).bind('click.checkboxReadOnly',function(){
									this.checked = !this.checked;
								});
							}
						}
						var hotKey = $(obj).attr('hotKey');
						if(hotKey && hotKeyregister){
							var _this = obj;
							if(enable){
							    if(!hotKeyregister.all_shortcuts[hotKey.toLowerCase()])
									hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
							}else{
								hotKeyregister.remove(hotKey);
							}
						}
					}else if($(obj.parentNode).hasClass('tabs-panels')){//tab页的id
						enable?Base.enableTab(id):Base.disableTab(id);
					}
				}else if(obj.cmptype == "flexbox"){
					obj.setEnable(enable);
				} else if(obj.cmptype == 'taspinner'){
					var spinner = Ta.core.TaUIManager.getCmp(id);
					if(spinner){
						spinner.spinnerImgClick(id,enable);
					}
				} else if (obj.id && (obj.id == "file" || obj.id == "multipleFile")) {
					// 对文件上传组件<ta:fileupload>进行控制
					if (!enable) {
						obj._disabled = true;
						$("#" + id).addClass("disabled");
					} else {
						obj._disabled = false;
						$("#" + id).removeClass("disabled");
					}
				} else if(false){
					//可能为下拉树等非普通输入对象
					
				}
			};
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i]!= undefined){
						var o = Base.getObj(ids[i]);
						var $o = $(o);
						if(o && o.tagName && (o.tagName == 'FIELDSET' || (o.tagName == "DIV" && (!$(o.parentNode).hasClass('tabs-panels'))) || o.tagName == "FORM")) {
							$o.find("input.ffb-input").each(function(){
								var a = this.id.replace("_desc", "");
								Base.getObj(a).setEnable(enable);
							});
							$o.find(":input[type!=hidden] ").not(".ffb-input").not(".datagrid :input").each(function() {
								fnsetEnable(this.id,enable);
							});
						} else 
							fnsetEnable(ids[i],enable);
					}
				}
			}
		}
		/**
		 * 设置输入对象为可用（按钮为可用，输入框为可编辑）。
		 * <br/>例如：
		 * <br/>Base.setEnable("aac001");
		 * <br/>Base.setEnable(["aac001","aac002"]);
		 * <br/>Base.setEnable("aac001,aac002");
		 * @method setEnable
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"] 或者逗号,分开如aac001,aac002
		 */
		function setEnable(ids){
			Base._setEnable(ids,true);
		}

		/**
		 * 设置输入对象为不可用，不可用状态时，组件不会将值传递到后台。
		 * <br/>例如：
		 * <br/>Base.setDisabled("aac001");
		 * <br/>Base.setDisabled(["aac001","aac002"]);
		 * <br/>Base.setDisabled("aac001,aac002");
		 * @method setDisabled
		 * @param {String/Array} ids  输入对象的id或id数组
		 */
		function setDisabled(ids){
			Base._setEnable(ids,false);
		}

		/**
		 * 设置输入对象为必输项。
		 * @method _setIsRequired
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} isRequred  true 为isRequred=true，false为isRequred=false
		 */
		function _setIsRequired(ids,isRequired){
			var fnsetRequired = function(id,isRequired){
				var obj = Base.getObj(id);
				if(!obj)return false;
				
				if(obj.tagName && (obj.tagName.toLowerCase()=="input"||obj.tagName.toLowerCase()=="textarea")){//表明是html元素
					var label = $(obj).parents("div.fielddiv:first").find("label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							
							$(obj).validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$(obj).validatebox('setRequired',false);
						}
					}
				}else if(obj.cmptype == "flexbox"){
					var label = $(Base.getObj(id+"_div")).parents("div.fielddiv:first").find("label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							$("#"+id+"_desc").validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$("#"+id+"_desc").validatebox('setRequired',false);
						}
					}			
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//checkboxgroup,radiogroup
					var label = $(obj).find(">label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							
							$(obj).validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$(obj).validatebox('setRequired',false);
						}
					}			
				}
			};
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					ids[i]?fnsetRequired(ids[i],isRequired):null;
				}
			}
		}
		/**
		 * 设置输入对象为必输。
		 * <br/>例如：
		 * <br/>Base.setRequired("aac001");
		 * <br/>Base.setRequired(["aac001","aac002"]);
		 * <br/>Base.setRequired("aac001,aac002");
		 * @method setRequired
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 */
		function setRequired(ids){
			Base._setIsRequired(ids,true);
		}
		/**
		 * 设置输入对象为非必输。
		 * <br/>例如：
		 * <br/>Base.setDisRequired("aac001");
		 * <br/>Base.setDisRequired(["aac001","aac002"]);
		 * <br/>Base.setDisRequired("aac001,aac002");
		 * @method setDisRequired
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * 
		 */
		function setDisRequired(ids){
			Base._setIsRequired(ids,false);
		}

		/**
		 * 设置输入对象的值 
		 * <br>普通输入框为value字符串
		 * <br>对于checkboxgroup或radiogroup应该为数组
		 * <br>注:需要考虑下了列表,下拉多选,下拉树等组件
		 * <br>如果设置下拉组件，下拉框内的值，数据应为数组，如[{id:'aaa', name:'dddd'},{id:'bb', name:'dddd'}]
		 * @method setValue
		 * @param {String/map} id  输入对象的id 或一个json格式的对象对多个输入框赋值
		 * @param {String} value 值
		 */
		function setValue(id,value){
			//当value类型为String的时候，才做处理
			if(typeof value =='string'){
			  value = $.trim(value);
			}
			var _setValue = function(id,value){
				var temp_id = id+"";
				if(temp_id.substring(0,5) == "_sel_"){
					id = temp_id.substring(5,temp_id.length);
				}
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					if($(obj).hasClass('checkboxgroup')){
						$(obj).find('input[type=checkbox]').each(function(){
							if(typeof value =='string'){
								if(this.disabled == true){}else{
									this.checked = (this.value==value);
									if(this.checked)
										Base._setChecked($(this).parent(), "checkbox");
									else if(!this.checked)
										Base._setUnChecked($(this).parent(), "checkbox");
								}
							}else if(jQuery.isArray(value)){
								var flag = false;
								for(var i = 0; i < value.length; i++) {
									if(value[i] == this.value) flag = true;
								}	
								if(flag){
									this.checked = true;
									Base._setChecked($(this).parent(), "checkbox");
								}else{
									this.checked = false;
									Base._setUnChecked($(this).parent(), "checkbox");
								}
							}
						});
					}else if($(obj).hasClass('radiogroup')){
						$(obj).find('input[type=radio]').each(function(){
								this.checked = (this.value==value);
								if(this.checked)
									Base._setChecked($(this).parent(), "radio");
								else if(!this.checked)
									Base._setUnChecked($(this).parent(), "radio");
						});
						
					}else if($(obj).hasClass('amountfield')){
						var t = {numberRound:$(obj).attr('numberRound'),decimalPlace:$(obj).attr('precision'), symbol:$(obj).attr('amountPre'),setValue:'setValue'};
						$(obj).moneyInput(t,value);
					}else if($(obj).hasClass('numberfield')){
						var options = {};
						$(obj).numberbox(options,value);
					
					}else if(obj.type=="text"|| obj.type=="password" || obj.tagName=='TEXTAREA'){
						if (value != null && typeof value == "string") {
							value = value.replaceAll("\\\\r", "\r");
							value = value.replaceAll("\\\\n", "\n");
							$(obj).val(value);
						} else {
							$(obj).val(value);
						}
						if($(obj).hasClass('datefield')){
					 	  if(value && value.length>10){
					 		  	$(obj).val(value.substring(0,10));
						  }
						}else if($(obj).hasClass('datetimefield')){
					 	  if(value && value.length>19){
					 		  	$(obj).val(value.substring(0,19));
						  }
						}
						
					/************* pengwei 修改  *************/	
					}else if(obj.type=='radio' || obj.type=='checkbox'){
							var $par =  $(obj).parent();
							if (!value){
								obj.checked = false;
								if(obj.type=='radio'){
									//判断调用setValue方法之前，选择框的状态
									Base._setUnChecked($par, "radio");
								}else{
									Base._setUnChecked($par, "checkbox");
								}
							}else if(value && obj.value == value){//当且仅当传入的value和标签的value值相等的时候才选中
								obj.checked = true;
								if(obj.type=='radio'){
									Base._setChecked($par, "radio");
								}else if(obj.type=='checkbox'){
									Base._setChecked($par, "checkbox");
								}
							}
//							else if (value != 0){
//								obj.checked = true;
//							}
//							else if (value == 0){ 
//								obj.checked = false;
//							}	
					}else if($(obj).hasClass('z_pic')){
						obj.src=Base.globvar.contextPath+"/"+value;
					}
				}else if(obj.cmptype == "flexbox"){
						try {
							var a = {};
							if(array){
								a.results = eval(array["_sel_" + id]);
								if (array["_sel_" + id] != null) 
									obj.setData(a);
								obj.setValue(value);
							}else{
								if(value != "" && value.indexOf("[") >=0 && value.indexOf("]") > 0){
									a.results = eval(value);
									obj.setData(a);
								}else if($.isArray(value)){
									a.results = eval(value);
									obj.setData(a);
								}else{
									obj.setValue(value);
								}
							}
						}
						catch (e) {
							obj.setValue(value);
						}
				}else if(obj.cmptype=="taselectpanel"){
					obj.setValue(id,value);	
				}else if(false){
					//TODO 下拉树等的控制
				}
			};
			if(value==null || value=="null" || value=="NULL")
				value="";
			if(typeof id == 'object'){
				var array = {};
				for(var i in id){
					if((i+"").substring(0,5) == "_sel_"){
						array[i] = id[i];
						delete id[i];
					}
				}
//				for(var i = 0 ; i < array.length ; i++){
//					if(typeof id[array[i]]=="string" ){
//						_setValue(array[i],$.trim(id[array[i]]));
//					}else{
//						_setValue(array[i],id[array[i]]);
//					}
//					delete id[array[i]];
//				}
				if($.isEmptyObject(id)){
					if(!$.isEmptyObject(array)){
						for(var j in array){
							_setValue(j,array[j]);
						}
					}
				}else{
					if(!$.isEmptyObject(array)){
						for(var j in array){
							_setValue(j,array[j]);
						}
					}
					for(var i in id){
						if(typeof id[i]=="string" ){
							_setValue(i,$.trim(id[i]));
						}else{
							_setValue(i,id[i]);
						}
					}
				}
			}else{
				_setValue(id,value);
			}
			
		}

		/**
		 *  获取输入对象的值
		 *  普通输入框为字符串
		 *  对于checkboxgroup或radiogroup 返回数组  
		 * @method getValue
		 * @param {String} id  输入对象的id
		 * @type {String/Array} value 值
		 * @return String or Array
		 */
		function getValue(id){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
				if($(obj).hasClass('checkboxgroup')){
					var ret =[];
					$(obj).find('input[type=checkbox]:checked').each(function(){
						ret.push(this.value);
					});
					return ret;
				}else if($(obj).hasClass('radiogroup')){
					return $(obj).find('input[type=radio]:checked').eq(0).val();
				}else if($(obj).hasClass('amountfield')){
					var t = {numberRound:$(obj).attr('numberRound'),decimalPlace:$(obj).attr('precision'), symbol:$(obj).attr('amountPre'),getValue:'getValue'};
					return $(obj).moneyInput(t);
				}else if(obj.type=="text"|| obj.type=="password"  || obj.type=='textarea' || obj.tagName=='TEXTAREA'){
					return $(obj).val();
				}else if(obj.type=='radio' || obj.type=='checkbox'){
					return obj.checked?$(obj).val():null;
				}
			}else if(obj.cmptype == "flexbox"){
				return obj.getValue(1);
			}else if(obj.cmptype == "taselectpanel"){
				return obj.getValue(id);
			}else if(false){
				//TODO 其他组件加入时处理
			}
		}
		/** 
		 * 让某个输入对象获取焦点
		 * @method focus
		 * @param {String} id 对象id
		 * @param {Number} delay 延迟（毫秒）后得到焦点
		 */
		function focus(id,delay){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.cmptype =='flexbox'){
				obj = $("#"+id+"_desc");
			}
			if(delay){
				$(obj).delay(delay).focus();
			}else{
				$(obj).focus();
			}
			//TODO 对下拉列表等其他特殊输入对象的处理
		}

		/**
		 * 显示输入对象或按钮
		 * @method showObj
		 * @param {String/Array} ids 例如: "aac001" 或["aac001","aac002"]， 或者以逗号隔开
		 */
		function showObj(ids){
			var show = function(id){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName && (obj.type=="text" || obj.type=="password" || obj.type=="textarea" || obj.type=="file")){//普通输入框
					if($(obj).parent().hasClass('fielddiv2')){
						$(obj).parent().parent().show().css('visibility','visible');
					}else if($(obj).parent().hasClass('fielddiv')){
						$(obj).parent().show().css('visibility','visible');
					}
					if(obj.type=="textarea"){
						$(obj).show().css('visibility','visible');
					}
				}else if($(obj.parentNode).hasClass('tabs-panels')){//tab
					var tabsc = $(obj.parentNode.parentNode);
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('showTab',id);
					}
				}else if(obj.cmptype =='flexbox'){//对combo处理
					$("#"+id).parent().parent().parent().show().css('visibility','visible');
				}else if($(obj).hasClass('radiogroup') || $(obj).hasClass('checkboxgroup')){
					$(obj).show();
				}else if(obj.tagName && obj.tagName.toLowerCase()=="input" && (obj.type=="checkbox" || obj.type=="radio")){
					$(obj).parent().show().css('visibility','visible');
				}//button
				 else if(obj.tagName && (obj.tagName.toLowerCase()=='button' || (obj.tagName.toLowerCase()=='input' && obj.type=='button' ))){
					$(obj).show().css('visibility','visible');
					var hotKey = $(obj).attr('hotKey');
					if(hotKey && hotKeyregister){
						var _this = obj;
						hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
					}
				}else{
					$(obj).show().css('visibility','visible');
				}
			};
			if(typeof ids=='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					show(ids[i]);
				}
			}	
		}

		/**
		 * 隐藏输入对象
		 * @method hideObj
		 * @param {String/Array} ids 例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 * @param {Boolean} isHold 默认false 不占位，如果为为false就不占位
		 */
		function hideObj(ids,isHold){
			var hide = function(id,isHold){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName && (obj.type=="text" || obj.type=="password" || obj.type=="textarea" || obj.type=="file")){//普通输入框
					if($(obj).parent().hasClass('fielddiv2')){
						if(isHold){
							$(obj).parent().parent().css('visibility','hidden');
						}else{
							$(obj).parent().parent().hide();
						}
					}else if($(obj).parent().hasClass('fielddiv')){
						if(isHold){
							$(obj).parent().css('visibility','hidden');
						}else{
							$(obj).parent().hide();
						}
					}
				}else if($(obj.parentNode).hasClass('tabs-panels')){//tab
					var tabsc = $(obj.parentNode.parentNode);
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('hideTab',id);
					}
				}else if(obj.cmptype =='flexbox'){//combo
					if(isHold)
						$("#"+id).parent().parent().parent().css('visibility','hidden');
					else
						$("#"+id).parent().parent().parent().hide();	
				}else if(obj.tagName && obj.tagName.toLowerCase()=="input" && (obj.type=="checkbox" || obj.type=="radio")){
					if(isHold){
						$(obj).parent().css('visibility','hidden');
					}else{
						$(obj).parent().hide();
					}
				}else{
					if(isHold)
						$(obj).css('visibility','hidden');
					else
						$(obj).hide();		
					var hotKey = $(obj).attr('hotKey');
					if(hotKey && hotKeyregister){
						hotKeyregister.remove(hotKey);
					}	
				}	
			};
			isHold = (isHold===true?true:false);
			
			if(typeof ids=='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					ids[i]?hide(ids[i],isHold):null;
				}
			}	
		}
		/**
		 * 重置form表单，注意关于HIDDEN框在不同浏览器的问题，如在chrome中不会清除hidden类型输入框，IE则会
		 * @method resetForm
		 * @param {String} id form的id 如果不传，默认取得页面第一个form元素
		 */
		function resetForm(formId){
			var form = $("#"+formId)[0];
			if(formId==undefined)form = $('form')[0];
			if(form && form.tagName=='FORM'){
				form.reset();
				Base.clearInvalidStyle(formId);
			}
			
			$(form).find(":checkbox").not("[readonly='readonly']").each(function(){
				if(this.checked) {
					Base._setChecked($(this).parent(), "checkbox");
				}else {
					Base._setUnChecked($(this).parent(), "checkbox");
				}
			});
			
			$(form).find(":radio").not("[readonly='readonly']").each(function(){
				if(this.checked) {
					Base._setChecked($(this).parent(), "radio");
				} else {
					Base._setUnChecked($(this).parent(), "radio");
				}
			});
		}

		/**
		 * 验证TABS下的tab页,当需要提交tabs时必须验证tabs下的每个tab页时需要调用此方法,例如:
		 * Base.submit("tabs1","demo/demoAction!query.do",null,function(){return Base.validateTab("tabs1")})
		 * @method validateTab
		 * @param {String} ids id组成的字符串
		 * @param {Boolean} focusFirst 是否聚焦到第一个验证失败的tab
		 */
		function validateTab(ids,focusFirst){
			if(!ids)return true;
			var firstValid,focusFirst = (focusFirst ===false?false:true);
			
			function validate(id){
				var obj = Base.getObj(id);
				if(!obj)return true;
				if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					var bvalid =  $(obj).validatebox('isValid');
					if(!bvalid && focusFirst && !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else if(obj.cmptype=='flexbox'){
					var bvalid =  $("#"+obj.getId()+"_desc").validatebox('isValid')
					if(!bvalid && focusFirst &&  !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else{
					var bret = true;
					$(obj).find(':input,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						if($(this).is(":hidden") && !$(this).validatebox('isValid')){
							bret = false;
							if(focusFirst && !firstValid &&$(this).css("display")!="none"){
								firstValid = this.id;
							}
						}
					});
					return bret;
				}
			}
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				var bret = true;
				for(var i=0;i<ids.length;i++){
						if(ids[i] && ids[i]!='' && !validate(ids[i]) )bret = false;
				}
				if( focusFirst && firstValid){
					var tabid=$("#"+firstValid).closest(".tabs-panels").children("div").has("#"+firstValid)[0].id;
					Base.activeTab( tabid) ;
					Base.focus(firstValid,100);
					
				}
				return bret;
			}
			return false;
		}
		/**
		 * 对给定范围内的表单进行校验，可以对某些输入对象进行校验，也可以对某个容器内的所有输入对象进行校验
		 * @method validateForm
		 * @param {Stirng/Array} ids 必须传入，需要校验的对象id或容器id  或以数组形式传递多个，例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 * @param {Boolean} focusFirst 是否将焦点置于第一个错误的对象。默认true。
		 */
		function validateForm(ids,focusFirst){
			if(!ids)return true;
			var firstValid,focusFirst = (focusFirst ===false?false:true);
			
			function validate(id){
				var obj = Base.getObj(id);
				if(!obj)return true;
				if($("#"+id).is(':hidden'))return true;
				if(obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					var bvalid =  $(obj).validatebox('isValid');
					if(!bvalid && focusFirst && !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else if(obj.cmptype=='flexbox'){
					var bvalid =  $("#"+obj.getId()+"_desc").validatebox('isValid')
					if(!bvalid && focusFirst &&  !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else{
					var bret = true;
					$(obj).find(':input,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						if(!$(this).is(":hidden") && !$(this).validatebox('isValid')){
							bret = false;
							if(focusFirst && !firstValid ){
								firstValid = this.id;
							}
							return bret;
						}
						if($(this).is(":disabled"))
							bret = true; //disable
					});
					return bret;
				}
			}
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				var bret = true;
				for(var i=0;i<ids.length;i++){
					if(ids[i] && ids[i]!='' && !validate(ids[i]))bret = false;
				}
				if(focusFirst && firstValid){
					Base.focus(firstValid,100);
				}
				return bret;
			}
			return false;
		}

		/**
		 * 将某一个表单对象设置为校验失败
		 * @method setInvalidField
		 * @param {String} id id组件id
		 * @param {String} message 失败信息
		 * @return {Boolean}
		 */
		function setInvalidField(id,message){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
				$(obj).validatebox('makeInvalid',message);
			}else if(obj.cmptype=='flexbox'){
				$("#"+obj.getId()+"_desc").validatebox('makeInvalid',message);
			}else{
				
			}
		}
		/**
		 * 清除表单校验失败的样式
		 * @method clearInvalidStyle
		 * @param {String/Array} ids 必须传入，需要校验的对象id或容器id  或以数组形式传递多个，例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 */
		function clearInvalidStyle(ids){
			var clear = function(id){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					$(obj).validatebox('clear');
				}else if(obj.cmptype=='flexbox'){
					$("#"+obj.getId()+"_desc").validatebox('clear');
				}else{
					$(obj).find(':input:visible,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						$(this).validatebox('clear');
					});
				}		
			}
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i] && ids[i]!='')clear(ids[i]);
				}
			}
		}
		/**
		 * 清除一个id区域的input输入框的值，置checkbox为非选择模式
		 * @method clearData
		 * @param {String} ids 某区域id
		 */
		function clearData(ids) {
			var obj = Base.getObj(ids);
			if(obj) {
				$(obj).find(":input").not(":radio").not(":checkbox").val("");
				$(obj).find(":checkbox").not("[readonly='readonly']").each(function(){
					this.checked = false;
					Base._setUnChecked($(this).parent(), "checkbox");
				});
				
				$(obj).find(":radio").not("[readonly='readonly']").each(function(){
					this.checked = false;
					Base._setUnChecked($(this).parent(), "radio");
				});
			}
		}

		function cancelBubble(event) {
			if (event == null) event = window.event;
			event.cancelBubble = true;
			event.returnValue = false;
			if (event.stopPropagation) {
				event.stopPropagation();
				event.preventDefault();
			}
		}

		/**
		 * 通过身份证号码获取出生年月日
		 * @method getIdCardBirthday
		 * @param {String} idCard 身份证号码
		 * @return {String} yyyy-MM-dd
		 */
		function getIdCardBirthday(idCard) {
		        var year, month, day;
		        // 身份证为15位或者18位
		        if (idCard.length == 15) {
		                year = idCard.substring(6, 8);
		                month = idCard.substring(8, 10);
		                day = idCard.substring(10, 12);
		        } else {
		                year = idCard.substring(6, 10);
		                month = idCard.substring(10, 12);
		                day = idCard.substring(12, 14);
		        }
		        // 按照yyyy-MM-dd自动补齐
		        if (year.length == 2)
		                year = "19" + year;
		        if (month.indexOf("0") == 0)
		                month = month.substring(1);
		        if (day.indexOf("0") == 0)
		                day = day.substring(1);
		        return year + "-" + month + "-" + day;
		}
		/**
		 * 获取身份证号码性别
		 * @method getIdCardGender
		 * @param {String} idCard  身份证号码
		 * @return {String}
		 */
		function getIdCardGender(idCard) {
		        var gender;
		        // 身份证为15位或者18位
		        if (idCard.length == 15) {
		                gender = idCard.substr(14, 1);

		        } else {
		                gender = idCard.substr(16, 1);
		        }
		        return gender % 2 == 0 ? '女' : '男';
		}

		/**
		 * 身份证15to18
		 * @method idcard218
		 * @param {String} sId 身份证号
		 * @return {Boolean} 
		 */
		function idcard218(sId) {
				if (sId.length == 15) {
					if(!/^\d{14}(\d|x)$/i.test(sId)){
						this.message =  "你输入的身份证长度或格式错误";
						return false;
					} else  {
					    sId=sId.substr(0,6)+'19'+sId.substr(6,9)
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
				return sId;
		}

		/**
		 * 设置selectData组件的值
		 * @method setSelectDataValue
		 * @param {String} id, 组件id
		 * @param {String} defaultId 隐藏值
		 * @param {String}  defaultName 显示值
		 */
		function setSelectDataValue(id,defaultId,defaultName) {
			if(id != null && defaultId != null && defaultName != null) {
				if(Base.getObj(id) != null) {
					if(defaultId != ""){
						if(String(defaultId).indexOf(",") > -1) {
							var ids = defaultId.split(",");
							var names = defaultName.split(",");
							for(var i = 0 ; i < ids.length ; i++){
								if(i == 0){
									$("#"+id).parent().prepend("<div class='selectData_leirong'  _id='leirong_"+ids[i]+"'><strong style='margin-right:18px;'>"+names[i]+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
								}else{
									$("#"+id).siblings(".selectData_leirong:last").after("<div class='selectData_leirong' _id='leirong_"+ids[i]+"'><strong  style='margin-right:18px;'>"+names[i]+"</strong><a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
								}
							}
						}else{ 
							$("#"+id).parent().prepend("<div class='selectData_leirong'  _id='leirong_"+defaultId+"'><strong style='margin-right:18px;'>"+defaultName+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
						}
						var leirongWidth = 0;
						var $leirong = $("#selectData_"+id+" div.selectData_leirong"),leirongWidth=0; 
						if($leirong && $leirong.length > 0) {
							for(var i = 0 ; i < $leirong.length ; i++) {
								leirongWidth += $($leirong[i]).outerWidth(true);
							}
						}
						$("#"+id).css("left",leirongWidth); 
					}
				}
			}
			
		}
		/**
		 * 获取经办现场信息
		 * @method getHandleStatus
		 * @param {String} jspUrl jsp路径如/sysapp/user.jsp
		 */
		function getHandleStatus(){
				var data = {};
				var fields = [];
				var lists = [];
				var keys = Ta.core.TaUIManager.keys();
				for(var i = 0;i<keys.length;i++){
					var obj = Base.getObj(keys[i]);
					if(obj.cmptype == 'datagrid'){
						var grid = {};
						grid.id = keys[i];
						var sdata = Base.getGridSelectedRows(keys[i]);
						grid['selData'] = Ta.util.obj2string(sdata);
						var allData = Base.getGridData(keys[i]);
						grid['allData'] = Ta.util.obj2string(allData);
						lists.push(grid);
					}
				}
				data.lists = lists;
				
				$('input').each(function(){
					if ($(this).attr("id")!= null) {
						if($(this).attr('readonly')){
							var singleData = {};
							singleData.id = $(this).attr('id');
							singleData.value = $(this).val();
							singleData.status = 'readonly';
							fields.push(singleData);
						}else if($(this).attr('disabled')){
							var singleData1 = {};
							singleData1.id = $(this).attr('id');
							singleData1.value = $(this).val();
							singleData1.status = 'disabled';
							fields.push(singleData1);
						}else {
							var singleData2 = {};
							singleData2.id = $(this).attr('id');
							singleData2.value = $(this).val();
							singleData2.status = '';
							fields.push(singleData2);
						}
					}
				});
				data.fields = fields;
				var $t = $("<textarea id=\"_dataFields_\" name=\"dto[\'_dataFields_\']\"></textarea>");
				$t.appendTo($('body'));
				$t.hide();
				$t.val(Ta.util.obj2string(data));
				
				var html = document.documentElement.outerHTML;
				//var $html = $(html).find(".slick-viewport").remove();
				var $jsp = $("<textarea id=\"_jspContext_\" name=\"dto[\'_jspContext_\']\"></textarea>");
				$jsp.appendTo($('body'));
				$jsp.hide();
				$jsp.val(html);
				
				Base.submit('_dataFields_,_jspContext_',Base.globvar.basePath + '/system/handleOldDataManagerAction!save.do');
				$t.remove();
				$jsp.remove();
		}

		/**
		 * 显示经办历史现场
		 * @method showHandleStatus
		 * @param {String} userid 用户id
		 * @param {String} time 时间
		 * @param {String} optionid 经办
		 */
		function showHandleStatus(userid, time, optionid) {
			var param = {}; 
			param["dto['id']"] = optionid;
//			param["dto['yae092']"] = userid;
//			param["dto['date']"] = time;
			top.Base.openWindow("optionid","经办现场回顾", Base.globvar.basePath + '/system/handleOldDataManagerAction!doExecutePage.do' ,param,top.innerWidth, top.innerHeight,null,null,true);
		}

		/**
		* 输入框内容全选
		* <br>$(function(){$("input").click(function(Base.selectValue("aac001")))})
		* <br>$(function(){$("input").click(function(Base.selectValue($(this))))})
		* <br>$(function(){$("input").click(function(Base.selectValue(this)))})
		* @param {} obj 可为单个输入框id,如"aac001"，也可以为一个jquery对象或者dom对象，如$("#aac001")，$("#aac001").get(0);	
		*/
		function selectValue(obj) {
			if(obj != null){
				if(typeof obj == "string"){
					$('#'+obj).select();
				}else if(typeof obj == "object"){
					if(obj instanceof jQuery){
						obj.select();
					}else{
						$(obj).select();
					}	
				}else{
					return;
				}
			}else{
				return;
			}
		}

		/**
		 * 获取当前值，作为历史数据
		 * 获取值结构如下
		 * [{ovDto['aac001_old']:'xxxx'}, ovDto['aac001_label']:'xxxx',ovDto['aac001_desc']:'xxxx'},{checkboxgp_old:[1,2,3]}]
		 * 存为零时变量
		 * @method saveOldValue
		 * @param {} aids
		 */
		function saveOldValue(submitIds) {
			if (submitIds == null) return;
			var oldValueObj = [];
			var aids = submitIds.split(',');
			for(var i=0;i<aids.length;i++){
				if(aids[i]==null || aids[i]=='')continue;
				var obj = Base.getObj(aids[i]);
				
				if (obj == undefined) continue;
				
				var $obj = $(obj);
				
				if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
				  (obj.tagName && (obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
					if(obj.cmptype=='flexbox')obj = $("#"+aids[i]);//下拉框
					
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
						var mnyobj = {};
						mnyobj["__id"] = aids[i];
						mnyobj["ovDto['" + aids[i] + "']"] = $("#"+aids[i]+"_hidden").val().trim().replace(":","").replace("*","");
						mnyobj["ovDto['" + aids[i] + "__label']"] = $("#"+aids[i]+"_hidden").parent().parent().find("label").text().trim().replace(":","").replace("*","");
						oldValueObj.push(mnyobj)
					}
					$("#"+aids[i]).find("input").not(":hidden").each(function(){
						if ($(this).hasClass("ffb-input")) {
							var mnyobj = {};
							var id = (this.id).replace("_desc","");
							mnyobj["__id"] = id;
							mnyobj["ovDto['" + id + "']"] = $(this).prev().val().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + id + "__label']"] = $(this).parent().parent().parent().find("label").text().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + id + "__desc']"] = $(this).val().trim().replace(":","").replace("*","");
							oldValueObj.push(mnyobj)
						} else {
							var mnyobj = {};
							mnyobj["__id"] = this.id;
							mnyobj["ovDto['" + this.id + "']"] = $(this).val().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + this.id + "__label']"] = $(this).parent().parent().find("label").text().trim().replace(":","").replace("*","");
							oldValueObj.push(mnyobj)
						}
					})
				}
				else if($obj.hasClass("amountfield")){
					var mnyobj = {};
					mnyobj["__id"] = aids[i];
					mnyobj["ovDto['" + aids[i] + "']"] = $("#"+aids[i]+"_hidden").val().trim().replace(":","").replace("*","");
					mnyobj["ovDto['" + aids[i] + "__label']"] = $("#"+aids[i]+"_hidden").parent().parent().find("label").text().trim().replace(":","").replace("*","");
					oldValueObj.push(mnyobj)
				}
				else{
					alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
					return false;
				}
			}
			document._oldValueObj = oldValueObj;
			return oldValueObj;
		}
		function spinnerImgClick(id){
			var spinner = Ta.core.TaUIManager.getCmp(id);
			if(spinner){
				spinner.spinnerImgClick(id);
			}
		}
		/**
		 * 性能最好的插入排序
		 */
		function insertSort(arr, sortfn, desc) {
		    for (var i = 1; i < arr.length; i++) {
		      var tmp = arr[i],
		          j = i;
		      while (arr[j - 1] > tmp) {
		        arr[j] = arr[j - 1];
		        --j;
		      }
		      arr[j] = tmp;
		    }
		    return arr;
		}

		/**
		 * 针对checkbox和radio，设置选中
		 * @private
		 * @method _setChecked
		 */
		function _setChecked($_d, type) {
			if (type == "checkbox") {
				if($_d.hasClass("ta-chk-uncheck"))
					$_d.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
				else if($_d.hasClass("ta-chk-uncheck-disabled"))
					$_d.removeClass("ta-chk-uncheck-disabled").addClass("ta-chk-checked-disabled");
			}else if (type == "radio") {
				if($_d.hasClass("ta-radio-uncheck"))
					$_d.removeClass("ta-radio-uncheck").addClass("ta-radio-checked");
				else if($_d.hasClass("ta-radio-uncheck-disabled"))
					$_d.removeClass("ta-radio-uncheck-disabled").addClass("ta-radio-checked-disabled");
			}
			
		}

		/**
		 * 针对checkbox和radio，取消选中
		 * @private
		 * @method _setUnChecked
		 */
		function _setUnChecked($_d, type) {
			if (type == "checkbox") {
				if($_d.hasClass("ta-chk-checked"))
					$_d.removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
				else if($_d.hasClass("ta-chk-checked-disabled"))
					$_d.removeClass("ta-chk-checked-disabled").addClass("ta-chk-uncheck-disabled");
			}else if (type == "radio") {
				if($_d.hasClass("ta-radio-checked"))
					$_d.removeClass("ta-radio-checked").addClass("ta-radio-uncheck");
				else if($_d.hasClass("ta-radio-checked-disabled"))
					$_d.removeClass("ta-radio-checked-disabled").addClass("ta-radio-uncheck-disabled");
			}
			
		}
		/**
		 * 文本框的提示文字信息
		 * @method funPlaceholder
		 * @param {Object} dom元素,必须是原生的document元素,不能是jquery的对象;
		 */
		function funPlaceholder(element) {  
			
		    //检测是否需要模拟placeholder  
		    var placeholder = '';  
		     if (element && !("placeholder" in document.createElement("input")) && (placeholder = element.getAttribute("placeholder"))) {  
		         //当前文本控件是否有id, 没有则创建  
		         var idLabel = element.id ;  
		         if (!idLabel) {  
		             idLabel = "placeholder_" + new Date().getTime();  
		             element.id = idLabel;  
		         }  
		         //创建label元素  
		         var eleLabel = document.createElement("label");  
		         eleLabel.htmlFor = idLabel;  
		         eleLabel.style.position = "absolute";  
		         //根据文本框实际尺寸修改这里的margin值  
		         eleLabel.style.margin = "0 0 0 0";  
		         eleLabel.style.color = "graytext";  
		         eleLabel.style.cursor = "text";  		         
		         
		         eleLabel.style.display = "block" ;
		         //插入创建的label元素节点  
		         element.parentNode.insertBefore(eleLabel, element);  
		        
		         //事件  
		         element.onfocus = function() {  		         	
		             eleLabel.innerHTML = "";  
		         };  
		         element.onblur = function() {  
		         	
		             if (this.value === "") {  
		                 eleLabel.innerHTML = placeholder;  
		             }  
		         };
		         //处理点击到label上面,提示不消失
		         eleLabel.onclick = function(){
		        	 element.focus();
		         }
				 element.onpropertychange = function(){  	 						 				 	 						 	 
				 	  if (element.value != "") {  				
				 	  	eleLabel.innerHTML = "";
				 	  }
		         };
		         
		         
		         
		         //样式初始化  
		         if (element.value === "") {  
		             eleLabel.innerHTML = placeholder;  
		         }  
		     }  
		}

		return {
			_getPreFormField : _getPreFormField,
			_getNextFormField : _getNextFormField,
			getObj : getObj,
			getFieldLabel : getFieldLabel,
			setFieldLabel : setFieldLabel,
			_setReadOnly : _setReadOnly,
			setReadOnly : setReadOnly,
			_setEnable : _setEnable,
			setEnable : setEnable,
			setDisabled : setDisabled,
			_setIsRequired : _setIsRequired,
			setRequired : setRequired,
			setDisRequired : setDisRequired,
			setValue : setValue,
			getValue : getValue,
			focus : focus,
			showObj : showObj,
			hideObj : hideObj,
			resetForm : resetForm,
			validateTab : validateTab,
			validateForm : validateForm,
			setInvalidField : setInvalidField,
			clearInvalidStyle : clearInvalidStyle,
			clearData : clearData,
			cancelBubble : cancelBubble,
			getIdCardBirthday : getIdCardBirthday,
			getIdCardGender : getIdCardGender,
			idcard218 : idcard218,
			getHandleStatus : getHandleStatus,
			showHandleStatus : showHandleStatus,
			selectValue : selectValue,
			saveOldValue : saveOldValue,
			spinnerImgClick : spinnerImgClick,
			insertSort : insertSort,
			_setChecked : _setChecked,
			_setUnChecked : _setUnChecked,
			funPlaceholder : funPlaceholder,
			setSelectDataValue : setSelectDataValue
		}
	}
}));


