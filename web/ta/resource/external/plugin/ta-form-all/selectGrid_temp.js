(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	 	 $.extend(true, window, {
	        SelectGrid: SelectGrid
	    });
	    /**
	     * 构造函数
	     * @param id
	     * @param columns
	     * @param data
	     * @param options
	     */
		function SelectGrid(id, columns, data, options, callback, url) {
			//options属性
			var defaults = {
				gridWidth : 500, //默认grid的宽度
				gridHeight : 300,
				onEnter : false,
				loadFormUrl : false,
				minChar : 5,
				paramName : "dto['id']"
			} ;
			var self = this;
			//容器变量
			var $container = null;
			var $input = null;  //描述框
			var $inputHide = null; //key
			var $gridDiv = null; //装表格的div
			
			var grid = null; //表格
			var keyColumn = null;
			var descColumn = null;
			var hiddenColumn = null;
			var filterColumn = [];
			
			//控制变量
			var isHidden = true; //是否显示下拉框
			
			function init() {
				options = $.extend({},defaults,options);
				//初始化组件
				$container = $("#" + id).empty();//主容器
				$inputHide = $("<input/>").attr("type", "hidden").attr("id", id + "_key").attr("name","dto['"+id+"']");//初始化隐藏框
				$input = $("<input/>").attr("type", "text").attr("id", id + "_desc").addClass("textinput");//初始化描述框
				$gridDiv = $("<div/>")
					.css("border-style","outset")
					.css("float","left")
					.css("position","absolute")
					.css("background-color","white")
					.css("top",25)
					.css("z-index","99999")
					.attr("id", id + "_div");
				//$gridDiv
				$inputHide.appendTo($container);
				$input.appendTo($container);
				$gridDiv.appendTo($container).hide();
				
				//方法
				_createGrid();//创建表格
					getKeyDescFilterColumns(); //获得KeyDescFilter基本信息
					grid.setColumnHidden(hiddenColumn);//设置隐藏列信息
				if (!options.loadFormUrl) {
					grid.getData().setFilter(filter);
					$input.keydown(onKey).keyup(function(event) {
							$input.val($input.val());
	//						grid.clearDirty();
							if (event.which != 38 && event.which != 40) {
								grid.getData().refresh();
							}
					});//注册事件
				} else {
					$input.keydown(onKey).keyup(function(event) {
							if (event.keyCode <=47 ||  event.keyCode >= 96) return;
							var input = $input.val();
							if (input.length > options.minChar) {
								var paramA = {};
								paramA[options.paramName] = input;
								loadUrl(url, paramA);
							}
					});
				}
				//setTimeout(function() {$input.focus();},50);
				//注册事件

				return self;
			}
			///////////////////////////////////
			//data
			/**
			 * 设置数据，清空表格状态
			 */
			function setData(data) {
				if (data) {
					if (data[descColumn]) 
						$input.val(data[descColumn]);
					if (data[keyColumn]) 
						$inputHide.val(data[keyColumn]);
				} else {
					$input.val("");
					$inputHide.val("");
				}
				grid.clearDirty();
			}
			function getDescData() {
				return $input.val();
			}
			function getKeyData() {
				return $inputHide.val();
			}
			function setDescData(value) {
				$input.val(value);
			}
			function setKeyData() {
				$inputHide.val(value);
			}
			function clearData() {
				//alert($input.val())
				$input.val("");
				$inputHide.val("");
				grid.clearDirty();
			}
			function getGridData() {
				return data;
			}
			
			function loadUrl(url, parameter) {
				var data = Base.getJson(url, parameter);
				grid.clearDirty();
				grid.getDataView().setItems(data);
				grid.getDataView().refresh();
				grid.refreshGrid();
			}
			///////////////////////////////////end data///////////
			/**
			 * 键盘事件
			 */
			function onKey(e) {
               if (e.keyCode == 38) {
               		if (grid.getSelectRowsDataToObj().length === 0) {
		            	grid.setActiveCell(0,0);
						grid.setSelectedRows([0]);
		            } else 
                    	grid.navigateUp();
                	focusEvent(e);
                } //判断向上
                else if (e.keyCode == 40) {
                	show(true);
                	if (grid.getSelectRowsDataToObj().length === 0) {
		            	grid.setActiveCell(0,0);
						grid.setSelectedRows([0]);
		            } else
                   		grid.navigateDown();
                   	focusEvent(e);
                } //判断向下
                else if (e.keyCode == 13) {
                	if (grid.getSelectRowsDataToObj().length === 0) {
		            	grid.setActiveCell(0,0);
						grid.setSelectedRows([0]);
		            } //13时判断是否有选择，否则选择第一条
                	focusEvent(e);
                	if (!isHidden){//如果没有隐藏下拉框 
                		callback(self, 189, grid.getData().getItemById(grid.getSelectedRowsById()[0]));
                		setData(grid.getData().getItemById(grid.getSelectedRowsById()[0]));
                	}
                	if (isHidden) {
                		if (options.onEnter) options.onEnter(self);
                		var next = Base._getNextFormField(id + "_desc");
                		if (next) $(next).focus();
                		return;
                	}//如果下拉框关闭，至焦点到下一个组件
                	show(isHidden);
                	grid.clearDirty();
                } else if (e.keyCode == 27) {
                	show(false);
                } else if (e.keyCode == 191) {
                	show(false);
                	callback(self, 191);
                } else if (e.keyCode == 189) {
                	show(false);
                	callback(self, 189);
                } else if ($("#"+id+"_desc").val().indexOf(".")==0 || event.keyCode==110 || event.keyCode==190) {
                	show(false);
                } else {
                	if (isHidden) {
                		show(true);
                	}//按其它任一键打开面板
                	callback(self);
                }
                //alert(e.keyCode);
                
			}
			/**
			 * 过滤算法
			 */
			function filter(item) {
				if (filterColumn.length === 0 ) {
					return true;
				}
//	var data = new Date();
//				grid.setSelectedRows([0]);
//	$("#jzh").val($("#jzh").val()+ " ,"+(new Date()- data));
				var zz = new RegExp($input.val());
				var value = null;
				for (var i = 0; i < filterColumn.length; i ++) {
					value = item[filterColumn[i]];
					if (zz.test(value)) {
						return true;
					}
				}
				return false;
			}
			/**
			 * 设KeyDescFilter列
			 */
			function getKeyDescFilterColumns() {
				for (var i = 0; i < columns.length ; i ++) {
					var clmn = columns[i];
					if (clmn.keyColumn && clmn.keyColumn === true) {
						keyColumn = clmn.field;
					} 
					if(clmn.descColumn && clmn.descColumn === true) {
						descColumn = clmn.field;
					}
					if(clmn.hiddenColumn && clmn.hiddenColumn === true) {
						hiddenColumn = clmn.field;
					}
					if(clmn.filterColumn) {
						filterColumn.push(clmn.field);
					}
				}
				if (keyColumn == null) {
					throw "必须指定keyColumn ";
				}
				if (descColumn == null) {
					throw "必须指定descColumn ";
				}
			}
			
			/**
			 * 对键盘事件焦点的处理
			 */
			function focusEvent(e) {
				if (!isHidden)
                	setTimeout(function() {$input.focus();},10);
				e.cancelBubble = true;
				e.returnValue = false;
				if (e.stopPropagation) {
					e.stopPropagation();
					e.preventDefault();
				}
			}
			/**
			 * 显示隐藏下拉框
			 */
			function show(isShow){
				//if (isShow == null || isShow == "undefined") isShow = true;
				if (isHidden && isShow) {
					grid.getData().refresh();
//					grid.setActiveCell(0,0);
//					grid.setSelectedRows([0]);
                	setTimeout(function() {$input.focus();},10);
					$gridDiv.show();
					isHidden = false;
				} else if(!isShow) {
					$gridDiv.hide();
					grid.clearDirty();
					isHidden = true;
				}
				$("body").bind("click.selectgriddiv",function(e){
					var srcobj;
					if ($.browser.msie) {
						srcobj = e.srcElement;
					} else {
						srcobj = e.target;
					}
					if(srcobj && srcobj.id == $input.attr('id')){
						return ;
					}
					$gridDiv.hide();
					grid.clearDirty();
					isHidden = true;
					$("body").unbind(".selectgriddiv");
				});
			}
			
			/**
			 * 单击表格事件
			 */
			function clickGrid(e, data) {
				setData(data);
				focusEvent(e);
				if (typeof callback == "function") callback(self,null,data);
				$gridDiv.hide();
				isHidden = true;
				grid.clearDirty();
			}
			/**
			 * 创建表格
			 */
			function _createGrid() {
				var optionsGrid = options.grid;
				var $gridView = $("<div/>").attr("id", id + "_grid").css("height", options.gridHeight).css("width", options.gridWidth).appendTo($gridDiv).resizable({ handles: 'e',minWidth:50});
				grid = new Slick.Grid("#" + id + "_grid", data, columns, optionsGrid);
				grid.onClick.subscribe(clickGrid);
			}
			init();
			
			$.extend(this, {
				"_grid"		  : grid,
				"getDescData" : getDescData,
				"getKeyData"  : getKeyData,
				"getGridData" : getGridData,
				"clearData"	  : clearData
			});
		}
	}));