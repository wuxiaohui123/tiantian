/* THESE FORMATTERS & EDITORS ARE JUST SAMPLES! */
/**
 * 编辑框
 * @module Grid
 * @namespace SlickEditor
 */
//selectInput,text,date,number,bool
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil","numberBox","datetimeMask", "selectInput"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
	/**
	  * 创建编辑框
	 * @class SlickEditor
	 * @static
	 * @constructor
	 */
    var SlickEditor = {
    	//validator
		/**
		 * @method notNull
		 * @private
		 */
	    notNull :function (value) {
				if (value == null || value == undefined || !value.length)
					return {valid:false, msg:"不能为空"};
				else
					return {valid:true, msg:null};
		},
		//formart
		/**
		 * @method SelectorCellFormatter
		 * @private
		 */
        SelectorCellFormatter : function(row, cell, value, columnDef, dataContext) {
            return (!dataContext ? "" : row);
        },
        /**
		 * @method SelectInputFormatter
		 * @private
		 */
		SelectInputFormatter : function(row, cell, value, columnDef, dataContext) {
			var reData = value;
			if (!columnDef.editordata) { throw "下拉框数据不正确";}
			var data = columnDef.editordata;
			for (var i = 0; i < data.length; i ++) {
				if (data[i].id == value) {
					reData = data[i].name;
				}
			}
			return reData? reData: "";
		},
		/**
		 * 用于formatter中,金钱格式化,例如:formatter="MoneyFormatter",产生的结果32.32-->￥32.32,3434.32-->￥343,4.32
		 * @method MoneyFormatter
		 * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {String} 格式化后的信息
		 */
		MoneyFormatter : function(row, cell, value, columnDef, dataContext) {
			if(value === "" || value == undefined) {
				return "";
			} else {
				var moneyValue = String(value);
				var t_moneyValue;
				if(moneyValue.indexOf(".")>0){
					t_moneyValue = moneyValue.substring(0,moneyValue.indexOf("."));
					t_p = moneyValue.substring(moneyValue.indexOf("."));
					var re = /(-?\d+)(\d{3})/;
					while (re.test(t_moneyValue)){
						t_moneyValue = t_moneyValue.replace(re, "$1,$2");
					}
					moneyValue = t_moneyValue + t_p;
				}else{
					var re = /(-?\d+)(\d{3})/;
					while (re.test(moneyValue)){
						moneyValue = moneyValue.replace(re, "$1,$2");
					}
				}
				return "<div style='text-align:right;line-height:24px;'>"+ "￥" + moneyValue + "</div>";
			}
		},
		/**
		 * 用于formatter中,金钱格式化,例如:formatter="MoneyFormatterNo$",产生的结果3223.32-->322,3.32
		 * @method MoneyFormatterNo$
		 * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {String} 格式化后的信息
		 */
		MoneyFormatterNo$ : function(row, cell, value, columnDef, dataContext) {
			if(value === "" || value == undefined) {
				return "";
			} else {
				var moneyValue = String(value);
				var t_moneyValue;
				if(moneyValue.indexOf(".")>0){
					t_moneyValue = moneyValue.substring(0,moneyValue.indexOf("."));
					t_p = moneyValue.substring(moneyValue.indexOf("."));
					var re = /(-?\d+)(\d{3})/;
					while (re.test(t_moneyValue)){
						t_moneyValue = t_moneyValue.replace(re, "$1,$2");
					}
					moneyValue = t_moneyValue + t_p;
				}else{
					var re = /(-?\d+)(\d{3})/;
					while (re.test(moneyValue)){
						moneyValue = moneyValue.replace(re, "$1,$2");
					}
				}
				if (moneyValue.indexOf(".") < 0) {
					moneyValue += ".00";
				} else if (moneyValue.substring(moneyValue.indexOf(".")).length < 3) {
					moneyValue += "0";
				}
				return "<div style='text-align:right;line-height:24px;'>"+ moneyValue + "</div>";
			}
		},
		/**
		 * 用于formatter中,数据格式化,百分比,例如:formatter="PercentCompleteCellFormatter",产生的结果32-->32%
		 * @method PercentCompleteCellFormatter
		 * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {String} 格式化后的信息
		 */
        PercentCompleteCellFormatter : function(row, cell, value, columnDef, dataContext) {
            if (value == null || value === "")
                return "-";
            else if (value < 50)
                return "<span style='color:red;font-weight:bold;'>" + value + "%</span>";
            else
                return "<span style='color:green'>" + value + "%</span>";
        },
        /**
         * * 用于formatter中,背景颜色变化,例如:formatter="GraphicalPercentCompleteCellFormatter",根据不同的结果产生不同的背景颜色及长度
         * @method GraphicalPercentCompleteCellFormatter
		 * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {String} 格式化后的信息
         */
        GraphicalPercentCompleteCellFormatter : function(row, cell, value, columnDef, dataContext) {
            if (value == null || value === "")
                return "";

            var color;

            if (value < 30)
                color = "red";
            else if (value < 70)
                color = "silver";
            else
                color = "green";

            return "<span class='percent-complete-bar' style='display:block;height:100%;line-height:24px;background:" + color + ";width:" + value + "%'>"+value+"</span>";
        },
        /**
         * 用于formatter中,例如:formatter="YesNoCellFormatter",空返回No,否则返回Yes
         * @method YesNoCellFormatter
		 * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {String} 格式化后的信息
         */
        YesNoCellFormatter : function(row, cell, value, columnDef, dataContext) {
            return value ? "Yes" : "No";
        },
        /**
         *  用于formatter中,背景图片,例如:formatter="BoolCellFormatter",空返回"",否则返回一张显示"对号"的图片
         * @method BoolCellFormatter
         * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {Object} 格式化后的信息
         */
        BoolCellFormatter : function(row, cell, value, columnDef, dataContext) {
            return value ? "<img src='"+Base.globvar.basePath+"/ta/resource/themes/base/slickgrid/images/tick.png'>" : "";
        },
        /**
         *  用于formatter中,背景图片,例如:formatter="TaskNameFormatter",在value前面显示一张"+"号的图片
         * @method TaskNameFormatter
         * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {Object} 格式化后的信息
         */
        TaskNameFormatter : function(row, cell, value, columnDef, dataContext) {
            // todo:  html encode
            var spacer = "<span style='display:inline-block;height:1px;width:" + (2 + 15 * dataContext["indent"]) + "px'></span>";
            return spacer + " <img src='"+Base.globvar.basePath+"/ta/resource/themes/base/slickgrid/images/expand.gif'>&nbsp;" + value;
        },
        /**
         * 用于formatter中,背景图片,例如:formatter="ResourcesFormatter",dataContext必须有resources字段,且resources是数组,该列的width<50才生效
        * @method ResourcesFormatter
         * @param row 行号
		 * @param cell 列号
		 * @param value 单元格值
		 * @param columnDef 列头信息
		 * @param dataContext 该行数据信息
		 * @returns {Object} 格式化后的信息
         */
        ResourcesFormatter : function(row, cell, value, columnDef, dataContext) {
            var resources = dataContext["resources"];
            if (!resources || resources.length == 0)
                return "";
            if (columnDef.width < 50)
                return (resources.length > 1 ? "<center><img src='"+Base.globvar.basePath+"/ta/resource/themes/base/slickgrid/images/info.gif' " : "<center><img src='"+Base.globvar.basePath+"/ta/resource/themes/base/slickgrid/images/drag-handle.png' ") +
                        " title='" + resources.join(", ") + "'></center>";
            else
                return resources.join(", ");
        },
	//*********************************editor//
		selectInput : function(args, data, options) {
			var $input;
			var inputObj;
            var defaultValue;
            var scope = this;
            this.init = function() {
                $input = $("<div />").appendTo(args.container);
                 if (options.flexboxOption != undefined) {
                    options.flexboxOption.onSelect = function(hid, inp){args.grid.getEditorLock().commitCurrentEdit()};
                 	inputObj = $input.flexbox(data, options.flexboxOption);
                 }
				else  {
					var option = {};
					option.onSelect = function(hid, inp){args.grid.getEditorLock().commitCurrentEdit()};
                 	inputObj = $input.flexbox(data, option);
				}
                 inputObj[0].getInput()
                 	.css("height", 25)
                 	.bind("keydown.nav", function(e) {
                 		if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        } else if (e.keyCode === 13) {
                        	args.grid.navigateRight();
                        }
                    });
                 $(inputObj[0].getInput().next()[0]).css("top",5);
                 inputObj[0].setFocus();
            };

            this.destroy = function() {
                $input.remove();
            };

            this.focus = function() {
            	inputObj[0].setFocus();
              // $input.focus();
            };

            this.getValue = function() {
                return inputObj[0].getValue(0);
            };

            this.setValue = function(val) {
                inputObj[0].setValue(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                inputObj[0].setValue(defaultValue);
                this.defaultValue = defaultValue;
                //$input.select();
            };

            this.serializeValue = function() {
                return inputObj[0].getValue(1);
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };
			//this.onChangefn = options.onChange;
			
            this.isValueChanged = function() {
            	//alert(defaultValue);
//            	if ((!(inputObj[0].getValue(1) == "" && defaultValue == null)) && (inputObj[0].getValue(1) != defaultValue)) {
//            		//if (typeof options.onChange == 'function')
//            		//	options.onChange(args.item, inputObj[0].getValue(1));
//            	}
                return (!(inputObj[0].getValue(1) == "" && defaultValue == null)) && (inputObj[0].getValue(1) != defaultValue);
            };

            this.validate = function() {
                if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }

                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
		},
		treeEditor : function(args, data, options) {
			var $input,$tree,$div;
			var zTreeObj;
            var defaultValue;
            var scope = this;
            this.init = function() {
                $input = $("<input style='height:20px' class='editor-text' type='text'/>").appendTo(args.container);
                $input.bind("focus",function() {
                	if (zTreeObj == null) {
                		$div = $("<div>").appendTo(args.container);
		                $tree = $("<ul id='" + args.column.id + "'class='ztree' style='border:1px solid #aaa;-moz-box-shadow:2px 2px 5px #333333; -webkit-box-shadow:2px 2px 5px #333333; box-shadow:2px 2px 5px #333333;background-color:white;width:150px'>").appendTo($div);
		                var setting = {
		            		view: {
		            			selectedMulti: false
		            		},
		            		data: {
			            		simpleData :{
			            			 enable:true, 
			            			 idKey:"id", 
			            			 pIdKey:"pId", 
			            			 rootPId:null 
			            		},
			            		keep:{ 
			            			parent:false, 
			            			leaf:false 
			            		}
		            		},
			                callback: {
			            		onClick: function(event, treeId, treeNode){
			            			$input.val(treeNode.id)
			            			args.grid.getEditorLock().commitCurrentEdit();
			            		}
			            	}
		            	}
		                zTreeObj = $.fn.zTree.init($tree, setting, data);
                	}
                })
                 
                $input
                	.bind("keydown.nav", function(e) {
                 		if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        } else if (e.keyCode === 13) {
                        	args.grid.navigateRight();
                        }
                    })
                 $input.focus();
            };

            this.destroy = function() {
                $input.remove();
                $.fn.zTree.destroy(args.column.id);
                $div.remove();
            };

            this.focus = function() {
               $input.focus();
            };

            this.getValue = function() {
                return $input.val();
            };

            this.setValue = function(val) {
            	$input.val(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                $input.val(defaultValue);
                this.defaultValue = defaultValue;
                //$input.select();
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };
            this.isValueChanged = function() {
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
                if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }

                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
		},
		selectGrid : function(args, data, options) {
			var grid;
			var container;
			var inputObj;
            var defaultValue;
            var scope = this;
            this.init = function() {
                $container = $("<span />").appendTo(args.container);
                
                var data = options.gridDatafn();
                var columns = options.gridItemfn();
                var option = options.gridOptionfn();
                
                if (option.onEnter && typeof option.onEnter == "function") {
                	var tempfn = option.onEnter;
                	option.onEnter = function(grid) {
                		return function(grid) {
                			tempfn(grid);
                			if (!grid.getIsHidden()){
                			args.grid.navigateRight();}
                		}(grid);
                	};
                }
                
				grid = new SelectGridEditor($container, columns, data, option);
				grid.getInput().focus();
            };
            
            this.destroy = function() {
                $container.remove();
            };
            this.focus = function() {
            	grid.getInput().focus();
            };
            this.getValue = function() {
                return grid.getDescData();
            };
            this.setValue = function(val) {
                grid.setDescData(val);
            };
            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                grid.setDescData(defaultValue);
                this.defaultValue = defaultValue;
            };
            this.serializeValue = function() {
                return grid.getDescData();
            };
            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };
            this.isValueChanged = function() {
            	return true;
                //return (!(inputObj[0].getValue(1) == "" && defaultValue == null)) && (inputObj[0].getValue(1) != defaultValue);
            };
            this.validate = function() {
                if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }
                return {
                    valid: true,
                    msg: null
                };
            };
            this.init();
		},
		selectInputDesc : function(args, data, options) {
			var $input;
			var inputObj;
            var defaultValue;
            var scope = this;
            this.init = function() {
                $input = $("<div />")
                    .appendTo(args.container);
                if (options.flexboxOption != undefined) {
                	options.flexboxOption.allowInputOtherText = true;
                 	inputObj = $input.flexbox(data, options.flexboxOption);
                }
				else                 
                 inputObj = $input.flexbox(data,{allowInputOtherText:true});
                 
                inputObj[0].getInput()
                 	.css("height", 25)
                 	.blur(function(){args.grid.getEditorLock().commitCurrentEdit()})
                 	.bind("keydown.nav", function(e) {
                 		if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        } else if (e.keyCode === 13) {
                        	args.grid.navigateRight();
                        }
                    });
                 inputObj[0].setFocus();
            };

            this.destroy = function() {
                $input.remove();
            };

            this.focus = function() {
            	inputObj[0].setFocus();
              // $input.focus();
            };

            this.getValue = function() {
                return inputObj[0].getValue(0);
            };

            this.setValue = function(val) {
                inputObj[0].setValue(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                inputObj[0].setValue(defaultValue);
                this.defaultValue = defaultValue;
                //$input.select();
            };

            this.serializeValue = function() {
                return inputObj[0].getValue(0);
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return (!(inputObj[0].getValue(0) == "" && defaultValue == null)) && (inputObj[0].getValue(0) != defaultValue);
            };

            this.validate = function() {
                if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }

                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
		},
        text : function(args, data, options) {
            var $input;
            var defaultValue;
            var scope = this;
            var validType = (options.validType == undefined?"":options.validType);
            this.init = function() {
            	var str = "<INPUT type='text'";
            	if(options.required == "true"){
            		str += " required='true'  class='editor-text";
                }else{
                	str += " class='editor-text";
                }
            	if(validType != ""){
            		if(options.validFunction){
            			str += " validatebox-text' validType='"+validType+"'  validFunction='"+options.validFunction+"'/>";
            		}else{
            			str += " validatebox-text' validType='"+validType+"'/>";
            		}
            	}else{
            		str += "'/>";
            	}
                $input = $(str)
                    .appendTo(args.container)
                    .bind("keydown.nav", function(e) {
                 	    if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }else if (e.keyCode === 13) {
                        	args.grid.navigateRight();
                        }
                    })
                    .bind("keyup.nav", function(e) {
                 	    if (typeof options.onKeyup == "function")
                    		options.onKeyup(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }else if (e.keyCode === 13) {
                        	//args.grid.navigateRight();
                        }
                    })
                    .bind("focus.nav", function(e) {
                 	    if (typeof options.onFocus == "function")
                    		options.onFocus(e);
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }else if (e.keyCode === 13) {
                        	//args.grid.navigateRight();
                        }
                    })
                    .blur(function(){args.grid.getEditorLock().commitCurrentEdit()})
                    .focus()
                    .select();
                if(options.validType != ""){
                	$input.validatebox();
                }
            };
            this.destroy = function() {
            	$('.validatebox-tip').remove();
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.getValue = function() {
                return $input.val();
            };

            this.setValue = function(val) {
                $input.val(val);
            };
            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                $input.val(defaultValue);
                $input[0].defaultValue = defaultValue;
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
//            	if ((!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue)) {
//            		if (typeof options.onChange == 'function')
//            			options.onChange(args.item,$input.val());
//            	}
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
//                if (args.column.validator) {
//                    var validationResults = args.column.validator($input.val());
//                    if (!validationResults.valid)
//                        return validationResults;
//                }
            	var valid = true;
            	if($input.hasClass('validatebox-invalid')){
            		valid = false;
            	}
                return {
                    valid: valid,
                    msg: null
                };
            };

            this.init();
        },
        date : function(args, data, options) {
            var $input;
            var defaultValue;
            var scope = this;
            var wdate ;
            this.init = function() {
            	var str = "<input type='text' class='editor-text datefield Wdate' validType='date' maxlength='10'";
            	if(options.required == "true"){
            		str += " required='true'";
            	}
            	str += "/>";
                $input = $(str);
                $input.appendTo(args.container);
                $input.bind("keydown.nav", function(e) {
                        if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                    
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }
                        else if (e.keyCode === 13) {
                        	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                        		
                        	}else{
                        		args.grid.navigateRight();
                        	}
                        }
                    })
                    .focus(function(){
                       	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                    		WdatePicker({
                        		isShowWeek:false,
                        	    el:$input[0],
                        	    errDealMode:1 
                        	});
                    	}
                    })
                    .select().validatebox();
                $input.datetimemask(1);
            };

            this.destroy = function() {
            	$('.validatebox-tip').remove();
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.getValue = function() {
                return $input.val();
            };

            this.setValue = function(val) {
                $input.val(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                $input.val(defaultValue);
                $input[0].defaultValue = defaultValue;
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
                /*if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }*/
            	var valid = true;
            	if($input.hasClass('validatebox-invalid')){
            		valid = false;
            	}
                return {
                    valid: valid,
                    msg: null
                };
            };

            this.init();
        },
        dateTime : function(args, data, options) {
            var $input;
            var defaultValue;
            var scope = this;

            this.init = function() {
            	var str = "<input type='text' class='editor-text datetimefield Wdate' validType='datetime' maxlength='19'";
            	if(options.required == "true"){
            		str += " required='true'";
            	}
            	str += "/>";
                $input = $(str);
                $input.appendTo(args.container);
                $input.bind("keydown.nav", function(e) {
                                        	if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                    
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }else if (e.keyCode === 13) {
                        	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                        		
                        	}else{
                        		args.grid.navigateRight();
                        	}
                        //	args.grid.navigateRight();
                        }
                    })
//                    .bind("keyup.nav", function(e) {
//                 	    if (typeof options.onKeyup == "function")
//                    		options.onKeyup(e);
//                        if (e.keyCode === 37 || e.keyCode === 39) {
//                            e.stopImmediatePropagation();
//                        }else if (e.keyCode === 13) {
////                        	args.grid.navigateRight();
//                        }
//                    })
//                    .bind("focus.nav", function(e) {
//                 	    if (typeof options.onFocus == "function")
//                    		options.onFocus(e);
//                        if (e.keyCode === 37 || e.keyCode === 39) {
//                            e.stopImmediatePropagation();
//                        }else if (e.keyCode === 13) {
////                        	args.grid.navigateRight();
//                        }
//                    })
                    .focus(function(){
                    	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                    		WdatePicker({
                        		isShowWeek:false,
                        	    el:$input[0],
                        	    dateFmt:'yyyy-MM-dd HH:mm:ss',
                        	    errDealMode:1 
                            });
                    	}
                    })
                    .select().validatebox();
                    $input.datetimemask(2);            
            };

            this.destroy = function() {
            	$('.validatebox-tip').remove();
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.getValue = function() {
                return $input.val();
            };

            this.setValue = function(val) {
                $input.val(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                $input.val(defaultValue);
                $input[0].defaultValue = defaultValue;
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
              /*  if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }*/
            	var valid = true;
            	if($input.hasClass('validatebox-invalid')){
            		valid = false;
            	}
                
                return {
                    valid: valid,
                    msg: null
                };
            	
            };

            this.init();
        },
        
        issue : function(args, data, options) {
            var $input;
            var defaultValue;
            var scope = this;

            this.init = function() {
            	var str = "<input type='text' class='editor-text issuefield Wdate' validType='issue' maxlength='6'";
            	if(options.required == "true"){
            		str += " required='true'";
            	}
            	str += "/>";
                $input = $(str);
                $input.appendTo(args.container);
                $input.bind("keydown.nav", function(e) {
                                        	if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                    
                        if (e.keyCode === 37 || e.keyCode === 39) {
                            e.stopImmediatePropagation();
                        }else if (e.keyCode === 13) {
                        	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                        		
                        	}else{
                        		args.grid.navigateRight();
                        	}
                        }
                    })
//                    .bind("keyup.nav", function(e) {
//                 	    if (typeof options.onKeyup == "function")
//                    		options.onKeyup(e);
//                        if (e.keyCode === 37 || e.keyCode === 39) {
//                            e.stopImmediatePropagation();
//                        }else if (e.keyCode === 13) {
////                        	args.grid.navigateRight();
//                        }
//                    })
                    .focus(function(){
                    	if(options.showSelectPanel!=undefined &&options.showSelectPanel==true){
                    		WdatePicker({
                        		isShowWeek:false,
                        	    el:$input[0],
                        	    dateFmt:'yyyyMM',
                        	    errDealMode:1 
                        	});
                    	}
                    })
               //     .blur(function(){args.grid.getEditorLock().commitCurrentEdit()})
                    .select().validatebox();
                    $input.datetimemask(3);
            };

            this.destroy = function() {
            	$('.validatebox-tip').remove();
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.getValue = function() {
                return $input.val();
            };

            this.setValue = function(val) {
                $input.val(val);
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field] || "";
                $input.val(defaultValue);
                $input[0].defaultValue = defaultValue;
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
              /*  if (args.column.validator) {
                    var validationResults = args.column.validator($input.val());
                    if (!validationResults.valid)
                        return validationResults;
                }*/
            	var valid = true;
            	if($input.hasClass('validatebox-invalid')){
            		valid = false;
            	}
                
                return {
                    valid: valid,
                    msg: null
                };
            	
            };

            this.init();
        },

        number : function(args,data, options) {
            var $input;
            var defaultValue;
            var scope = this;

            this.init = function() {
                $input = $("<INPUT type=text class='numberfield  editor-text'/>");
				if (options.max!= undefined) {
					$input.attr("max",options.max)
				}
				if (options.min!= undefined) {
					$input.attr("min",options.min)
				}
				if (options.precition!= undefined) {
					$input.attr("precision", options.precition)
//					$input.addClass("amountfield");
				}
                $input.bind("keydown.nav", function(e) {
                    if (typeof options.onKeydown == "function")
                    		options.onKeydown(e);
                    if (e.keyCode === 37 || e.keyCode === 39) {
                        e.stopImmediatePropagation();
                    }
                });
                $input.bind("keyup.nav", function(e) {
                    if (typeof options.onKeyup == "function")
                    		options.onKeyup(e);
                    if (e.keyCode === 37 || e.keyCode === 39) {
                        e.stopImmediatePropagation();
                    } 
                });
                $input.bind("focus.nav", function(e) {
                    if (typeof options.onFocus == "function")
                    		options.onFocus(e);
                    if (e.keyCode === 37 || e.keyCode === 39) {
                        e.stopImmediatePropagation();
                    } else if (e.keyCode === 13) {
//                        	args.grid.navigateRight();
                    }
                });
				
                $input.appendTo(args.container)
//                if (options.precition!= undefined)
//                	$input.moneyInput(options.precition);
//                else 
                $input.numberbox(options);
                $input.bind("keydown.nav", function(e) {
                	 if (e.keyCode === 13) {
                     	args.grid.navigateRight();
                	 }
                });
                $input
                .blur(function(){
                	args.grid.getEditorLock().commitCurrentEdit()
                	})
                .focus().select();
            };

            this.destroy = function() {
            	$('.validatebox-tip').remove();
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field];
                $input.val(defaultValue);
                $input[0].defaultValue = defaultValue;
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val() || 0;
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
            	var val = ""+$input.val();
            	var b = ""+defaultValue==null?"":defaultValue;
            	var d = (!(val == "" && b == null)) && (val != b);
                return d;
            };

            this.validate = function() {
                if (isNaN($input.val()))
                    return {
                        valid: false,
                        msg: "Please enter a valid integer"
                    };

                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
        },
        bool : function(args, data, options) {
            var $select;
            var defaultValue;
            var scope = this;

            this.init = function() {
                $select = $("<input type='checkbox' class='editor-checkbox'>");
                $select.appendTo(args.container);
                $select
                .blur(function(){args.grid.getEditorLock().commitCurrentEdit()})
                .focus();
            };

            this.destroy = function() {
                $select.remove();
            };

            this.focus = function() {
                $select.focus();
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field];
                if (defaultValue)
                    $select.attr("checked", "checked");
                else
                    $select.removeAttr("checked");
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $select.attr("checked");
            };
            
            this.getValue = function(target){
            	return $(target).val();
            };
            
            this.setValue = function(target,value){
            	$(target).val(value); 
            };
            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return ($select.attr("checked") != defaultValue);
            };

            this.validate = function() {
                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
        },
        StarCellEditor : function(args) {
            var $input;
            var defaultValue;
            var scope = this;

            function toggle(e) {
                if (e.type == "keydown" && e.which != 32) return;

                if ($input.css("opacity") == "1")
                    $input.css("opacity", 0.5);
                else
                    $input.css("opacity", 1);

                e.preventDefault();
                e.stopPropagation();
                return false;
            }

            this.init = function() {
                $input = $("<IMG src='../resource/themes/base/slickgrid/images/bullet_star.png' align=absmiddle tabIndex=0 title='Click or press Space to toggle' />")
                    .bind("click keydown", toggle)
                    .appendTo(args.container)
                    .focus();
            };

            this.destroy = function() {
                $input.unbind("click keydown", toggle);
                $input.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.loadValue = function(item) {
                defaultValue = item[args.column.field];
                $input.css("opacity", defaultValue ? 1 : 0.2);
            };

            this.serializeValue = function() {
                return ($input.css("opacity") == "1");
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return defaultValue != ($input.css("opacity") == "1");
            };

            this.validate = function() {
                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
        },
        /*
         * An example of a "detached" editor.
         * The UI is added onto document BODY and .position(), .show() and .hide() are implemented.
         * KeyDown events are also handled to provide handling for Tab, Shift-Tab, Esc and Ctrl-Enter.
         */
        textArea : function (args, data, options) {
            var $input, $wrapper;
            var defaultValue;
            var scope = this;

            this.init = function() {
                var $container = $("body");

                $wrapper = $("<DIV style='z-index:10000;position:absolute;background:white;padding:5px;border:3px solid gray; -moz-border-radius:10px; border-radius:10px;'/>")
                    .appendTo($container);

                $input = $("<TEXTAREA hidefocus rows=5 style='backround:white;width:250px;height:80px;border:0;outline:0'>")
                    .appendTo($wrapper);

                $("<DIV style='text-align:right'><BUTTON>保存</BUTTON><BUTTON>取消</BUTTON></DIV>")
                    .appendTo($wrapper);

                $wrapper.find("button:first").bind("click", this.save);
                $wrapper.find("button:last").bind("click", this.cancel);
                $input.bind("keydown", this.handleKeyDown);

                scope.position(args.position);
                $input.focus().select();
            };

            this.handleKeyDown = function(e) {
                if (e.which == 13 && e.ctrlKey) {
                    scope.save();
                }
                else if (e.which == 27) {
                    e.preventDefault();
                    scope.cancel();
                }
                else if (e.which == 9 && e.shiftKey) {
                    e.preventDefault();
                    grid.navigatePrev();
                }
                else if (e.which == 9) {
                    e.preventDefault();
                    grid.navigateNext();
                }
            };

            this.save = function() {
                args.commitChanges();
            };

            this.cancel = function() {
                $input.val(defaultValue);
                args.cancelChanges();
            };

            this.hide = function() {
                $wrapper.hide();
            };

            this.show = function() {
                $wrapper.show();
            };

            this.position = function(position) {
                $wrapper
                    .css("top", position.top - 5)
                    .css("left", position.left - 5)
            };

            this.destroy = function() {
                $wrapper.remove();
            };

            this.focus = function() {
                $input.focus();
            };

            this.loadValue = function(item) {
                $input.val(defaultValue = item[args.column.field]);
                $input.select();
                this.defaultValue = defaultValue;
            };

            this.serializeValue = function() {
                return $input.val();
            };

            this.applyValue = function(item,state) {
                item[args.column.field] = state;
            };

            this.isValueChanged = function() {
                return (!($input.val() == "" && defaultValue == null)) && ($input.val() != defaultValue);
            };

            this.validate = function() {
                return {
                    valid: true,
                    msg: null
                };
            };

            this.init();
        }

    };
    
    $.extend(window, SlickEditor);

}));
