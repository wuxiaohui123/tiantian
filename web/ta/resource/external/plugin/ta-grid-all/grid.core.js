/*******************************************************************************
 * 表格基本功能
 * @module Grid
 * @namespace Slick
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery",
		        "scrollable", 
		        "ajaxfileupload",
		        "grid.base",
		        "grid.dataview",
		        "grid.rowselect",
		        "grid.group",
		        "grid.checkbox",
		        "grid.dataview",
		        "grid.editors",
		        "grid.radioselect",
		        "grid.rowselect",
		        "grid.pager",
		        "grid.pager.expdata",
		        "grid.pager.impdata",
		        "event.drag-2.2",
		        "sortable"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
    $.extend(true, window, {
        Slick: {
            Grid: SlickGrid
        }
    });

    var scrollbarDimensions; 
    /**
	 * @class SlickGrid
	 * @static
	 * @constructor
	 * @param {Object} container 页面上div的id
	 * @param {Array,Object} data 数据对象
	 * @param {Array} columns 列对象
	 * @param {Object} options 参数
	 */
    function SlickGrid(container,data,columns,options) {
        var defaults = {
            headerHeight: 0,// 表格主体离底边的距离
            rowHeight: 25,// 行高
            defaultColumnWidth: 80,// 默认
            enableAddRow: false,// 不忙用
            leaveSpaceForNewRows: false,
            editable: false,
            autoEdit: true,
            enableCellNavigation: true,// 原为true
            enableCellRangeSelection: true,
            enableColumnReorder: false,// 原为true
            asyncEditorLoading: true,
            asyncEditorLoadDelay: 100,//
            forceFitColumns: false,
            // 添加
            columnsWidthsOverView: false,
            enableAsyncPostRender: false,
            asyncPostRenderDelay: 60,
            autoHeight: false,
            editorLock: Slick.GlobalEditorLock,
            showHeaderRow: false,
            headerRowHeight: 25,
            showTopPanel: false,
            topPanelHeight: 30,
            formatterFactory: null,
            editorFactory: null,
            cellFlashingCssClass: "flashing",
            selectedCellCssClass: "selected",
            multiSelect: true,
            enableTextSelectionOnCells: true,
            // TODO 林森添加
            selectType : false, // 是否为checkbox选择模式,
            columnFilter : false,
            showToolpaging : false,
            onSelectChange : null,
            haveSn : false, // 显示可变序列号
            groupingExpression : null,
            serverCvtCode : true,
            groupingBy: false,
            snWidth: 37,
            border : false,
            clickActiveStyle : true,
            headerColumnsRows :1
        };

		// 默认Column的属性
        var columnDefaults = {
            name: "",
            resizable: true,
            sortable: false,
            minWidth: 30,
            rerenderOnResize: false,
            headerCssClass: null,
            align : "center",
            dataAlign : "left"
            // ,
            // TODO 林森
            // cls : "",
            // clsClick : false
        };
		
        var maxSupportedCssHeight;      // browser's breaking point
        var th;                         // virtual height
        var h;                          // real scrollable height
        var ph;                         // page height
        var n;                          // number of pages
        var cj;                         // "jumpiness" coefficient

        var page = 0;                   // current page
        var offset = 0;                 // current page offset
        var scrollDir = 1;

        // private
        var $container;
        var uid = "slickgrid_" + Math.round(1000000 * Math.random());
        var self = this;
        var $headerScroller;
        var $headers;
        var $headerRow, $headerRowScroller;
        var $topPanelScroller;
        var $topPanel;
        var $viewport;
        var $canvas;
        var $style;
        var stylesheet, columnCssRulesL, columnCssRulesR;
        var viewportH, viewportW;
        var viewportHasHScroll;
        var headerColumnWidthDiff, headerColumnHeightDiff, cellWidthDiff, cellHeightDiff;  // padding+border
        var absoluteColumnMinWidth;

        var activePosX;
        var activeRow, activeCell;
        var activeCellNode = null;
        var currentEditor = null;
        var serializedEditorValue;
        var editController;

        var rowsCache = {};
        var renderedRows = 0;
        var numVisibleRows;
        var prevScrollTop = 0;
        var scrollTop = 0;
        var lastRenderedScrollTop = 0;
        var prevScrollLeft = 0;
        var avgRowRenderTime = 10;

        var selectionModel;
        var selectedRows = [];

        var plugins = [];
        var cellCssClasses = {};

        var columnsById = {};
        var sortColumnId;
        var sortAsc = true;

        // async call handles
        var h_editorLoader = null;
        var h_render = null;
        var h_postrender = null;
        var postProcessedRows = {};
        var postProcessToRow = null;
        var postProcessFromRow = null;

        // perf counters
        var counter_rows_rendered = 0;
        var counter_rows_removed = 0;
        
		// TODO 林森添加数据视图
		var dataView = null;
		var selectorTpye = null;
		var columnFilters = {};
		var selectedRowIds = [];
		// var $paging = null;
		var editorItems = []; // 用于保存添加数据
		var delrows = []; // 用于保存删除数据
		var addrows = []; // 用于保存添加数据
		var hiddenColumns = [];
		var pager = null;
		var pId = "__id___";
		var defaultWidth = 0;
		var defaultHeight = 0;
		var hastotals = false;//pxs添加，用于标识表格是否有合计列
		var columnPropertyKey = [];
        // ////////////////////////////////////////////////////////////////////////////////////////////
        // Initialization

        function init() {
            $container = $(container); // 将传入id的标签转换成JQuery对象
            maxSupportedCssHeight  = 10000;
            scrollbarDimensions = {width:17, height:17};
            if(!options.rowHeight){
            	if (Base.globvar.indexStyle == "default") 
                	options.rowHeight = 30;
            }
            options = $.extend({},defaults,options); // 默认option和自订opetion结合
            columnDefaults.width = options.defaultColumnWidth;//默认列宽80

            editController = {//编辑操作
                "commitCurrentEdit": commitCurrentEdit,//提交当前编辑内容
                "cancelCurrentEdit": cancelCurrentEdit//取消当前编辑内容
            };

            $container.addClass(uid);
            
			if(options.border == true) {//表格容器边框
				 $container.css("border", "1px solid #99BBE8");
			}
			// 表的head
            $headerScroller = $("<div class='slick-header' style='overflow:hidden;position:relative;' />").appendTo($container);
            // 用于装head的超长容器
            $headers = $("<div class='slick-header-columns' style='width:10000px; left:-1000px' />").appendTo($headerScroller);

            $topPanelScroller = $("<div class='slick-top-panel-scroller' style='overflow:hidden;position:relative;' />").appendTo($container);
            $topPanel = $("<div class='slick-top-panel' style='width:10000px' />").appendTo($topPanelScroller);
            if (!options.showTopPanel) {
                $topPanelScroller.hide();
            }
            $headerRowScroller = $("<div class='slick-headerrow' style='overflow:hidden;position:relative;' />").appendTo($container);
            $headerRow = $("<div class='slick-headerrow-columns' style='width:10000px;' />").appendTo($headerRowScroller);
            if (!options.showHeaderRow) {
                $headerRowScroller.hide();
            }
			// 表主体
            $viewport = $("<div class='slick-viewport' tabIndex='0' hideFocus>").appendTo($container);
            // 提供一个很高的容器来保存显示数据
            $canvas = $("<div class='slick-grid-canvas' tabIndex='0' hideFocus />").appendTo($viewport);
            // header columns and cells may have different padding/border
			// skewing width calculations (box-sizing, hello?)
            // calculate the diff so we can set consistent sizes
            measureCellPaddingAndBorder();// 设置单元格的padding和border
            // for usability reasons, all text selection in SlickGrid is
			// disabled
            // with the exception of input and textarea elements (selection must
            // be enabled there so that editors work as expected); note that
            // selection in grid cells (grid body) is already unavailable in
            // all browsers except IE
           //disableSelection($headers); // 禁止选择头的字 disable all text selection in
										// header (including input and textarea)

            if (!options.enableTextSelectionOnCells) {
                // disable text selection in grid cells except in input and
				// textarea elements
                // (this is IE-specific, because selectstart event will only
				// fire in IE)
                $viewport.bind("selectstart.ui", function (event) {
                    return $(event.target).is("input,textarea");
                });
            }

            viewportW = parseFloat($.css($container[0], "width", true));// 设置相同宽度
			// ****************TODO 林森添加
           
            
            // 设置为何种选择框
            if (options.selectType == "checkbox") {
            	selectorTpye = new Slick.CheckboxSelectColumn({
		            // cssClass: "slick-cell-checkboxsel",
		            onChecked : options.onChecked,
		            onRowSelect : options.onRowSelect
		        });
            	columns.unshift(selectorTpye.getColumnDefinition());
            	registerPlugin(selectorTpye);
            } else if (options.selectType == "radio") {
            	selectorTpye = new Slick.RadioSelectColumn({
		            // cssClass: "slick-cell-checkboxsel"
		        });
            	columns.unshift(selectorTpye.getColumnDefinition());
            	registerPlugin(selectorTpye);
            }
            
            // 设置选择模型为行选择模型
            setSelectionModel(new Slick.RowSelectionModel({selectActiveRow:true}));
            
            if (options.haveSn) {
            	columns.unshift({ 
            		dataType: "number",
            		id: "__no",
            		resizable: false,
            		selectable: false, 
            		sortable: "true",
            		cssClass: "slick-cell-selection",
            		field: pId,
            		width: options.snWidth
            	});
            }// 添加设置于数据内部的序号
            
            // //////////////////////////////////////////////////////////////
            // 数据处理, 由于构造方法中传入的数据有可能是Array,也可能是pageBean;
            // data.list or data
            var tmp = null;
            if(!jQuery.isArray(data)){
            	tmp = data.list || [];
            } else {
            	tmp = data;
            }
            for(var i = 0; i < tmp.length; i ++){
            	tmp[i][pId] = i; // 循环为tmp添加id;
            }
            // tmp = setData(data);
            // data = tmp;
            dataView = new Slick.Data.DataView(null,self);
            dataView.setFilter(filter);
            if (tmp && typeof tmp == "object" && tmp.length > 0) {
            	dataView.beginUpdate();
				dataView.setItems(tmp);
				dataView.endUpdate();
				data = dataView;
            } else {
            	data = dataView;
            }
            createColumnHeaders();// 对表头进行初始化，包括resize
            setupColumnSort(); // 初始化列排序
            createCssRules();
            resizeAndRender();

            bindAncestorScrollEvents();
            $viewport.bind("scroll.slickgrid", handleScroll);
            $container.bind("_resize.slickgrid", resizeAndRender);
            $headerScroller
                .bind("contextmenu.slickgrid", handleHeaderContextMenu)
                .bind("click.slickgrid", handleHeaderClick);

            $canvas
                .bind("keydown.slickgrid", handleKeyDown)
                .bind("click.slickgrid", handleClick)
                .bind("dblclick.slickgrid", handleDblClick)
                .bind("contextmenu.slickgrid", handleContextMenu)
                .bind("draginit", handleDragInit)
                .bind("dragstart", handleDragStart)
                .bind("drag", handleDrag)
                .bind("dragend", handleDragEnd);

            $canvas.delegate(".slick-row", "mouseenter", handleMouseEnter);
            $canvas.delegate(".slick-row", "mouseleave", handleMouseLeave);
            // 添加列过滤
            if (options.columnFilter) {
	            $(getHeaderRow()).delegate(":input", "change keyup", function(e) {
	                columnFilters[$(this).data("columnId")] = $.trim($(this).val());
	                if (columnFilters.undefined) 
	                	delete columnFilters.undefined;
	                dataView.refresh();
	            });
	            // 注册当列改变及大小变化是调用更新列头方法
	            self.onColumnsReordered.subscribe(function(e, args) {
	                updateHeaderRow();
	            });
	            self.onColumnsResized.subscribe(function(e, args) {
	                updateHeaderRow();
	            });
	            updateHeaderRow();
            }
            
            if (options.showToolpaging) {
            	// 添加分页条
            	var $paging = $("<div id='" + container.toString().replace('#',"") + "_paper_' ></div>").appendTo($container);
            	pager = new Slick.Controls.Pager(dataView, self, $paging, options.pagingOptions);
            }
            // 添加排序
            self.onSort.subscribe(function(e, args) {
	            sortdir = args.sortAsc ? 1 : -1;
	            sortcol = args.sortCol.field;
	            var type = args.sortCol.dataType ? args.sortCol.dataType: "string";

            	if (type == "number")// 数字
                	dataView.sort(comparerNum, args.sortAsc);
                else if (type == "date")// 日期
                	dataView.sort(comparerDate, args.sortAsc);
                else if (type == "dateTime")// 全日期
                	dataView.sort(comparerDate, args.sortAsc);
                else
                	dataView.sort(comparer, args.sortAsc);
	          
	        });
	        // //////-------------------------------------
	        dataView.onRowCountChanged.subscribe(function(e,args) {
				updateRowCount();
                render();
			});
	
			dataView.onRowsChanged.subscribe(function(e,args) {
				// invalidateAllRows();
				render();
	        });
	        self.onSelectedRowsChanged.subscribe(function(e,a,b,c) {
                selectedRowIds = [];
                var rows = self.getSelectedRows();
                for (var i = 0, l = rows.length; i < l; i++) {
                    var item = dataView.getItem(rows[i]);
                    // alert(item.__group)
                    if (item) {
                    	if (item.__group) continue;
                    	selectedRowIds.push(item[pId]);
                    }
                }
            });
            // 注册通过id来选择行
            dataView.onRowsChanged.subscribe(function(e,args) {
				self.invalidateRows(args.rows);
				self.render();

				if (selectedRowIds.length > 0) {
					var selRows = [];
					for (var i = 0; i < selectedRowIds.length; i++){
						var idx = dataView.getRowById(selectedRowIds[i]);
						if (idx != undefined)
							selRows.push(idx);
					}

					self.setSelectedRows(selRows);
				}
			});
			
			function gridGroping(columnId, trueVlue) {
				return 
			}
			// 如果有分组
            if (options.groupingBy) {
            	if (options.groupingBy == "_onlyTotals"){
            		 dataView.groupBy(
		                options.groupingBy,
		                function (g) {
		                    return   "<span style='color:green'>(共" + g.count + " 项)</span>";
		                },
		                function (a, b) {
		                    return a.value - b.value;
		                }
		            );
            	} else {
            		 dataView.groupBy(
		                options.groupingBy,
		                function (g) {
		                	var value = "";
		                	for (var i = 0; i < columns.length; i ++) {
		                		if (columns[i].id == options.groupingBy) {
		                			value = columns[i].name
		                		}
		                	}
		                	var trueCollection  = getCollectionsDataArrayObject()[options.groupingBy] == undefined ? []:getCollectionsDataArrayObject()[options.groupingBy];
		                	var trueValue = g.value;
		                	for (var i = 0; i < trueCollection.length; i ++) {
		                		if (trueCollection[i].id == g.value) {
									if (trueCollection[i].name != null) {
										trueValue = trueCollection[i].name;
										break;
									}
								}
		                	}
		                    return value + ":  " + trueValue + "  <span style='color:green'>(共" + g.count + " 项)</span>";
		                },
		                function (a, b) {
		                    return a.value - b.value;
		                }
		            );
            	}
	           
	            var totals = [];
	            for (var i = 0; i < columns.length; i ++) {
                		if (columns[i].totals != undefined) {
                			switch (columns[i].totals) {
                				case "avg" : 
                					if(typeof columns[i].totalsFormatter == "function"){
                						totals.push(new Slick.Data.Aggregators.Avg(columns[i].id,columns[i].totalsFormatter));
                					}else{
                						totals.push(new Slick.Data.Aggregators.Avg(columns[i].id));
                					}
                					break;
                				case "max" : 
                					if(typeof columns[i].totalsFormatter == "function"){
                						totals.push(new Slick.Data.Aggregators.Max(columns[i].id,columns[i].totalsFormatter));
                					}else{
                						totals.push(new Slick.Data.Aggregators.Max(columns[i].id));
                					}
                					break;
                				case "min" : 
                					if(typeof columns[i].totalsFormatter == "function"){
                						totals.push(new Slick.Data.Aggregators.Min(columns[i].id,columns[i].totalsFormatter));
                					}else{
                						totals.push(new Slick.Data.Aggregators.Min(columns[i].id));
                					}
                					break;
                				case "sum" : 
                					if(typeof columns[i].totalsFormatter == "function"){
                						totals.push(new Slick.Data.Aggregators.Sum(columns[i].id,columns[i].totalsFormatter));
                					}else{
                						totals.push(new Slick.Data.Aggregators.Sum(columns[i].id));
                					}
                					break;
                			}
                		}
	            };
	            dataView.setAggregators(totals, true);
				dataView.collapseGroup(0);
  				dataView.endUpdate();
            }
            // 提示框
            initTip();
             // 默认选择行
            if(typeof options.defaultRows == "function"){// defaultRows为function
	        	var str = options.defaultRows();
	        	setCheckedRows(str);
	        } else if(options.defaultRows != undefined && options.defaultRows.length > 0){// defaultRows为json数组
	        	setCheckedRows(options.defaultRows);
	        }
            
            //pxs添加，用于在初始化是构建表格合计列，由于没法正常获取合计列行号，因此延迟加载
//            setTimeout(function(){creatTotalRows();},500);
            // *******
// var tempColumn = [];
// for (var i = 1; i < columns.length; i ++) {
// tempColumn.push(columns[i]);
// }
// setColumns(tempColumn);
            //初始化event
            if (options.defaultEvent != null && options.defaultEvent.length >0){
            	var defaultEvent = options.defaultEvent;
            	for (var i = 0; i < defaultEvent.length; i ++) {
            		var eventName = defaultEvent[i].name;
            		var handler = defaultEvent[i].handler;
            		if (options.eventHandler != null && options.eventHandler[handler] != null)
            			self[eventName].subscribe(options.eventHandler[handler]);
            		else 
            			self[eventName].subscribe(self[handler]);
            	}
            }
        }// init
        
        /**
		 * 勾选所有数据，相当于点击全选checkbox
		 */
        function checkedAllData(){
        	var defaultData = self.getDataView().getItems();
        	var checkedRowsData = [];
        	if(defaultData){
        		for(var i = 0 ; i < defaultData.length ; i++){
					checkedRowsData.push(defaultData[i][pId]);
				}
        	}
        	selectionModel.setSelectedRanges(rowsToRanges(checkedRowsData));
        }
        /**
		 * 取消全选
		 */
        function cancelSelectedAllData(){
        	setSelectedRows([]);
        }
        function setCheckedRows(obj){
        	var checkedRows = eval(obj);
        	if(checkedRows){
	        	if(options.selectType == "checkbox" || options.selectType == "radio"){
	        		// 所有数据
		        	var defaultData = self.getDataView().getItems();
		        	if(defaultData){
		        		// 需要选择的行
						var checkedRowsData = [];
						for(var i = 0;i<defaultData.length;i++){
							var d = defaultData[i];
							for(var j = 0;j<checkedRows.length;j++){
								var checkedRow = checkedRows[j];
								// l为json对象里的属性个数,yy为需要相等的列个数
								var l = 0,yy = 0;
								for(var x in checkedRow){
									l++;
									if(d[x] == checkedRow[x]){
										yy++;
									}
								}
								// 当json对象都满足条件时,将该行压入需要选择的行数组中
								if(yy == l){
									checkedRowsData.push(d[pId]);
								}
							}
						}
						// selectedRows.concat(checkedRowsData)
						// checkedRowsData =
						// checkedRowsData.concat(selectedRows);
						// setSelectedRows(selectedRows);
						selectionModel.setSelectedRanges(rowsToRanges(checkedRowsData));
					}
				}
			}
        }
        /*
		 * 根据数组取消某些选择
		 */
        function cancelCheckedRowsByArray(obj){
        	var checkedRows = eval(obj);
        	if(checkedRows){
	        	if(options.selectType == "checkbox"){
		        	var defaultData = getSelectRowsDataToObj();
		        	if(defaultData){
		        		// 需要取消的行
						var checkedRowsData = [];
						var selectedRowsData = [];
						for(var i = 0 ; i < defaultData.length ; i++){
							selectedRowsData.push(defaultData[i]._row_);
						}
						for(var i = 0;i<defaultData.length;i++){
							var d = defaultData[i];
							for(var j = 0;j<checkedRows.length;j++){
								var checkedRow = checkedRows[j];
								// l为json对象里的属性个数,yy为需要相等的列个数
								var l = 0,yy = 0;
								for(var x in checkedRow){
									l++;
									if(d[x] == checkedRow[x]){
										yy++;
									}
								}
								// 当json对象都满足条件时,将该行压入需要选择的行数组中
								if(yy == l){
									checkedRowsData.push(d['_row_']);
									break;
								}
							}
						}
						Array.prototype.indexOf = function(val) {
			            for (var i = 0; i < this.length; i++) {
			                if (this[i] == val) return i;
			            }
			            return -1;
				        };
				        Array.prototype.remove = function(val) {
				            var index = this.indexOf(val);
				            if (index > -1) {
				                this.splice(index, 1);
				            }
				        };
				        for(var i = 0 ; i < checkedRowsData.length; i++){
				        	selectedRowsData.remove(checkedRowsData[i]);
				        }
						// selectedRows.concat(checkedRowsData)
						// checkedRowsData =
						// checkedRowsData.concat(selectedRows);
						setSelectedRows(selectedRowsData);
						// selectionModel.setSelectedRanges(rowsToRanges(checkedRowsData));
					}
				}
			}
        }
        
        function cancelCheckedRowByData(data) {
        	var s = getSelectRowsDataToObj();
        	var select = [], flag = true;
    			for (var j = 0; j < s.length; j ++) {
		        	for (var i = 0; i < data.length; i ++) {
	        			for (var obj in data[i]) {
		    				if (s[j][obj] == data[i][obj]) {
	    						flag = false;
	    						break;
	    					}
	    				}
					}
    				if (flag) select.push(s[j]._row_);
    				flag = true;
        	}
        	setSelectedRows(select);
        }
        function addSelectRowsByData(data) {
        	alert("待开发")
        	var checkedRows = [];
        	if (typeof data != "string") {
        		checkedRows = eval(data);
        	} else {
        		checkedRows = data;
        	}
        	var checkedRowsData = [];
        	var defaultData = self.getDataView().getItems();
    		for(var i = 0; i < defaultData.length; i++){
				var d = defaultData[i];
				for(var j = 0; j < checkedRows.length; j++){
					var checkedRow = checkedRows[j];
							// l为json对象里的属性个数,yy为需要相等的列个数
					var l = 0,yy = 0;
					for(var x in checkedRow){
						l++;
						if(d[x] == checkedRow[x]){
							yy++;
						}
					}
							// 当json对象都满足条件时,将该行压入需要选择的行数组中
					if(yy == l){
						var flag = false;
						for (var selects = 0 ; selects < selectedRows.length; selects ++) {
							if (d[pId] == selectedRows[0]) {
								flag = true;
							}
						}
						if (!flag)
							checkedRowsData.push(d[pId]);
					}
				}
			}
			selectedRows = selectedRows.concat(checkedRowsData);
			setSelectedRows(selectedRows);
        }
        
        function initTip() {
    		function context(e,o){
				  e.preventDefault();
				  var columns =  self.getColumns();
				  var cellInfo = self.getCellFromEvent(e);
			      var rowData = self.getDataItem(cellInfo.row);
			      var cellData = rowData[self.getColumns()[cellInfo.cell].field];
			      var collectionData = self.getCollectionsDataArrayObject();
			      if(collectionData){
			    	 var collectionCell = collectionData[self.getColumns()[cellInfo.cell].field];
			    	if(collectionCell){
			    		for(var i = 0 ; i < collectionCell.length ; i++){
			    			if(collectionCell[i].id == cellData){
			    				cellData = collectionCell[i].name;
			    			}
			    		}
			    	 }
			      }
			      cellData = cellData == undefined ? "" : cellData; 
			      var columnName = self.getColumns()[cellInfo.cell].name;
			      columnName = columnName == undefined ? "": columnName;
			      var columnId = self.getColumns()[cellInfo.cell].id;
				  for (var i = 0 ; i < columns.length; i ++) {
				  	if (columns[i].showDetailed == true) {
				  		  if (columnId == columns[i].id) {
				  		  	if ($("#" + self.getGridId() + "_tips").length == 0 ) {
					  			$("<div id='" + self.getGridId() + "_tips'  class='slick-showdetail'/>")
					  			.html("<span style='word-break:break-all'><div style='font-weight:bolder'>" +columnName + ":</div>"+cellData + "</span>")
					  			.appendTo($container);
					  		} else {
					  			$("#" + self.getGridId() + "_tips").find("span").html("<div style='font-weight:bolder'>" +columnName + ":</div>"+cellData);
					  		}
					  		$("#" + self.getGridId() + "_tips").css({"left":e.clientX+4,"top":e.clientY+4,"position":"fixed"}); 
					  		$("#" + self.getGridId() + "_tips").show();
					  		break;
				  		  } else {
				  		  	$("#" + self.getGridId() + "_tips").hide();
				  		  }
				  	}
				  }
				}
			self.onMouseEnter.subscribe(context);
			function over() {
				$("#" + self.getGridId() + "_tips").hide();	
			}
			self.onMouseLeave.subscribe(over);
        }
        /**
		 * 汉字排序
		 * 
		 * @author 林森
		 */
        function comparer(a,b) {
			var x = a[sortcol] != undefined ? a[sortcol] : "" , y = b[sortcol] != undefined ? b[sortcol]: "";
			
			// if (typeof x == "number" && typeof y == "number")
			// return (x == y ? 0 :(x > y ? 1 : -1));
			// if (typeof x == "string" && typeof x == "string" )
			// x.substring(0,1).localeCompare(y.substring(0,1))
            // var i = x.localeCompare(y);
            // if (i > 0 ) return 1;
            // else if(i< 0) return -1;
            // else return 1;
			// alert(navigator.userAgent)
//			if (navigator.userAgent.indexOf("Chrome") != -1) {
//				var xx = getSpell(x);
//				var yy = getSpell(y);
//				return (xx == yy ? 0 :(xx > yy ? 1 : -1));
//			}
			return x.localeCompare(y);

		}
		/**
		 * 数字排序
		 * 
		 * @author 林森
		 */
		function comparerNum(a,b) {
			var x = Number(a[sortcol]) ? Number(a[sortcol]): -999999999999, y = Number(b[sortcol]) ? Number(b[sortcol]): -999999999999;
			return (x == y ? 0 : (x > y ? 1 : -1));
			
		}
		function comparerDate(a,b) {
			var dataX = a[sortcol];
			var dataY = b[sortcol];
			
			var x = dataX ? Number(dataX.replaceAll("-","").replaceAll(":","").replaceAll(" ","")): -999999999999, 
				y = dataY ? Number(dataY.replaceAll("-","").replaceAll(":","").replaceAll(" ","")): -999999999999;
			// alert("x :" + x + " data:" + dataX + " y :" + y);
			return (x == y ? 0 : (x > y ? 1 : -1));
			
		}
		// *****************/
        function registerPlugin(plugin) {
            plugins.unshift(plugin);
            plugin.init(self);
        }
        /**
		 * 撤销插件，通过调用插件的 destroy(self)方法 self为当前grid
		 * 
		 * @method
		 */
        function unregisterPlugin(plugin) {
            for (var i = plugins.length; i >= 0; i--) {
                if (plugins[i] === plugin) {
                    if (plugins[i].destroy) {
                        plugins[i].destroy();
                    }
                    plugins.splice(i, 1);
                    break;
                }
            }
        }
		/**
		 * 通过设置选择模式
		 */
        function setSelectionModel(model) {
	        if (selectionModel) {
	            selectionModel.onSelectedRangesChanged.unsubscribe(handleSelectedRangesChanged);
	            if (selectionModel.destroy) {
	                selectionModel.destroy();
	            }
            }

            selectionModel = model;
            if (selectionModel) {
                selectionModel.init(self);
                selectionModel.onSelectedRangesChanged.subscribe(handleSelectedRangesChanged);
            }
        }

        function getSelectionModel() {
            return selectionModel;
        }

        function getCanvasNode() {
            return $canvas[0];
        }
		/**
		 * 测量scrollbar的width和height 暂时不知道其用途
		 */
        function measureScrollbar() {
            // / <summary>
            // / Measure width of a vertical scrollbar
            // / and height of a horizontal scrollbar.
            // / </summary
            // / <returns>
            // / { width: pixelWidth, height: pixelHeight }
            // / </returns>
            var $c = $("<div style='position:absolute; top:-10000px; left:-10000px; width:100px; height:100px; overflow:scroll;'></div>").appendTo("body");
            var dim = { width: $c.width() - $c[0].clientWidth, height: $c.height() - $c[0].clientHeight };
            $c.remove();
            return dim;
        }
        
		/**
		 * 得到Columns的width
		 */
        function getRowWidth() {
            var rowWidth = 0;
            var i = columns.length;
            while (i--) {
                rowWidth += (columns[i].width || columnDefaults.width);
            }
            return rowWidth;
        }

        function setCanvasWidth(width) {
            $canvas.width(width);
            viewportHasHScroll = (width > viewportW - scrollbarDimensions.width);
        }

        function disableSelection($target) {
            // / <summary>
            // / Disable text selection (using mouse) in
            // / the specified target.
            // / </summary
            if ($target && $target.jquery) {
                $target
                    .attr('unselectable', 'on')
                    .css('MozUserSelect', 'none')
                    .bind('selectstart.ui', function() { return false; }); // from
																			// jquery:ui.core.js
																			// 1.7.2
            }
        }
		/**
		 * 通过div测试得到最大css高度
		 */
        function getMaxSupportedCssHeight() {
            var increment = 10000;
            var supportedHeight = increment;
            // FF reports the height back but still renders blank after ~6M px
            var testUpTo =  100000;
            var div = $("<div style='display:none' />").appendTo(document.body);

            while (supportedHeight <= testUpTo) {
                div.css("height", supportedHeight + increment);
                if (div.height() !== supportedHeight + increment)
                    break;
                else
                    supportedHeight += increment;
            }

            div.remove();
            return supportedHeight;
        }

        // TODO: this is static. need to handle page mutation.
        function bindAncestorScrollEvents() {
            var elem = $canvas[0];
            while ((elem = elem.parentNode) != document.body) {
                // bind to scroll containers only
                if (elem == $viewport[0] || elem.scrollWidth != elem.clientWidth || elem.scrollHeight != elem.clientHeight)
                    $(elem).bind("scroll.slickgrid", handleActiveCellPositionChange);
            }
        }

        function unbindAncestorScrollEvents() {
            $canvas.parents().unbind("scroll.slickgrid");
        }
		/**
		 * 更新ColumnHeadr
		 * 
		 * @param
		 */
        function updateColumnHeader(columnId, title, toolTip) {
        	// 阻止有checkbox时,getColumnIndex(columnId)为undefined报错
// if(columnId === '_checkbox_selector'){}
// else{
	            var idx = getColumnIndex(columnId);
	            if(idx === undefined) return;
	            var $header = $headers.children().eq(idx);
	            if ($header) {
	                columns[idx].name = title;
	                columns[idx].toolTip = toolTip;
	                $header
	                    .attr("title", toolTip || title || "")
	                    .children().eq(0).html(title);
	            }
// }
        }

        function getHeaderRow() {
            return $headerRow[0];
        }

        function getHeaderRowColumn(columnId) {
            var idx = getColumnIndex(columnId);
            var $header = $headerRow.children().eq(idx);
            return $header && $header[0];
        }
		/**
		 * 创建Column头
		 */
        function createColumnHeaders(reorder) {
        	var opt = options;
            var i;
            var $tableTrs;
            function hoverBegin() {
                $(this).addClass("slick-header-column-hover");// 鼠标进入显示
            }
            function hoverEnd() {
                $(this).removeClass("slick-header-column-hover");// 鼠标离开显示
            }
            $headers.empty(); // 移除所有header容器的子节点
            $headerRow.empty();
            columnsById = {};
            //if (reorder == true)
            if (columns[0] && columns[0].possation == undefined) {
	            for (var j = 0 ; j < columns.length; j ++) {
	            	if (columns[j].possation == undefined) {
	                	columns[j].possation = j;
	                }
	            }
            }
			columns = columns.sort(
			function(a,b){
				if (a.possation != undefined && b.possation != undefined)
					return a.possation - b.possation
				else return -1;
			});
            for (i = 0; i < columns.length; i++) {
            	
            	if (columns[i].propertyKey == true) {
            		columnPropertyKey.push(columns[i].field);
            	}
            	
                var m = columns[i] = $.extend({},columnDefaults,columns[i]);// 将每一个column和默认的取继承
                columnsById[m.id] = i;
                
                //
                var headerStr = ["<div class='ui-state-default slick-header-column"];
                if (m.field == "sel") headerStr.push(" slick-cell-checkbox ");
                headerStr.push(m.headerCssClass || "");
                headerStr.push("' id='");
                headerStr.push(uid);
                headerStr.push(m.id);
                headerStr.push("' title='");
                headerStr.push(m.toolTip || m.name.replaceAll("<br/>","") || "");
                headerStr.push("' field='");
                headerStr.push(m.id);
                headerStr.push("' style='width:");
                headerStr.push(m.width - headerColumnWidthDiff);
                headerStr.push("px;");
                if(m.headerBackgroundColor){
                	headerStr.push("background:")
                	headerStr.push(m.headerBackgroundColor);
                	headerStr.push(";");
                }
                if (m.align){
                	headerStr.push("text-align:")
                	headerStr.push(m.align);	
                	headerStr.push(";");	
				}
                
            	if(opt.headerColumnsRows){
            		headerStr.push("height:");
            		headerStr.push(22 * opt.headerColumnsRows);
            		headerStr.push("px;");
            	}
                headerStr.push("'>");
                if (m.editor) {
                	if(opt.headerColumnsRows && opt.headerColumnsRows != 1){
                		headerStr.push("<span class='slick-column-name slick-icon-edit' style='padding-left: 15px;white-space:normal;word-break:break-all;display:block;height:15px;'>");
                		headerStr.push(m.name);
                		headerStr.push("</span>");
                	}else{
                		headerStr.push("<span class='slick-column-name slick-icon-edit' style='padding-left: 15px;display:block;height:15px;'>");
                		headerStr.push(m.name);
                		headerStr.push("</span>");
                	}
                } else {
                	if(opt.headerColumnsRows && opt.headerColumnsRows != 1){
                		headerStr.push("<span class='slick-column-name' style='white-space:normal;word-break:break-all;'>")
                		headerStr.push(m.name);
                		headerStr.push("</span>");
                	}else{
                		headerStr.push("<span class='slick-column-name'>");
                		headerStr.push(m.name);
                		headerStr.push("</span>");
                	}
                }
                if (m.sortable) {
                	headerStr.push("<span class='slick-sort-indicator' />"); // 添加用于排序的小span
                }
                if (m.collection && opt.serverCvtCode) {
                	headerStr.push('<input type="hidden"  name="gridInfo[\'');
                	headerStr.push(self.getGridId());
                	headerStr.push('_displayCode\']" value="');
                	headerStr.push(m.id);
                	headerStr.push('`');
                	headerStr.push(m.collection);
                	headerStr.push('" />');
                }
                headerStr.push("</div>");
                
                var header = $(headerStr.join("")).data("fieldId", m.id)
				
                //if (opt.enableColumnReorder || m.sortable) {
                    header.hover(hoverBegin, hoverEnd);
                //}

                if (opt.showHeaderRow) {
                	$("<div class='ui-state-default slick-headerrow-column c" + i + "'></div>").appendTo($headerRow);
                }
                header.appendTo($headers);
            }
           // setSortColumn(sortColumnId, sortAsc);
            setTimeout(function(){setupColumnResize();},1500);
            if (opt.enableColumnReorder) {
                setupColumnReorder();
            }
        }
       
        function setupColumnSort() {
            $headers.click(function(e) {
                if ($(e.target).hasClass("slick-resizable-handle")) {
                    return;
                }

                var $col = $(e.target).closest(".slick-header-column");
                if (!$col.length)
                    return;

                var column = columns[getColumnIndex($col.data("fieldId"))];
                if (column.sortable) {
                    if (!getEditorLock().commitCurrentEdit())
                        return;

                    if (column.id === sortColumnId) {
                        sortAsc = !sortAsc;
                    }
                    else {
                        sortColumnId = column.id;
                        sortAsc = true;
                    }

                    setSortColumn(sortColumnId,sortAsc);
                    
                    /*
					 * 排序时触发事件
					 */
                    trigger(self.onSort, {sortCol:column,sortAsc:sortAsc});
                    // 如果排序时,有选择行,则去除掉选择的行;
                    cancelSelectedAllData();
                }
            });
        }

        function setupColumnReorder() {
            $headers.sortable({
                containment: "parent",
                axis: "x",
                cursor: "default",
                tolerance: "intersection",
                helper: "clone",
                placeholder: "slick-sortable-placeholder ui-state-default slick-header-column",
                forcePlaceholderSize: true,
                start: function(e, ui) { $(ui.helper).addClass("slick-header-column-active"); },
                beforeStop: function(e, ui) { $(ui.helper).removeClass("slick-header-column-active"); },
                stop: function(e) {
                    if (!getEditorLock().commitCurrentEdit()) {
                        $(this).sortable("cancel");
                        return;
                    }
                    
                    var reorderedIds = $headers.sortable("toArray");
                    var reorderedColumns = [];
                    //var p = null;
                    var index = 0;
                    for (var i = 0; i < reorderedIds.length; i++) {
                        var column = columns[getColumnIndex(reorderedIds[i].replace(uid,""))];
                        for (var j = 0; j < hiddenColumns.length ; j ++){
                        	if (hiddenColumns[j] && hiddenColumns[j].possation == index){
                        		index = hiddenColumns[j].possation + 1;
                        	}
                        }
                        column.possation = index;
                        reorderedColumns.push(column);
                        index ++;
//                        if (prePossation > column.possation && p == null) {
//                    		p = reorderedColumns[i].possation;
//                    	}
//                        var prePossation = column.possation;
                    }
                    
                    setColumns(reorderedColumns, true);

                    trigger(self.onColumnsReordered, {});
                    e.stopPropagation();
                    setupColumnResize();
                }
            });
        }
		/**
		 * 设置并渲染表格
		 */
        function setupColumnResize() {
            var $col, j, c, pageX, columnElements, minPageX, maxPageX, firstResizable, lastResizable, originalCanvasWidth;
            columnElements = $headers.children();
            columnElements.find(".slick-resizable-handle").remove();
            columnElements.each(function(i,e) {
                if (columns[i] && columns[i].resizable) {
                    if (firstResizable === undefined) { firstResizable = i; }
                    lastResizable = i;
                }
            });
            if (firstResizable === undefined) {
                return;
            }
            columnElements.each(function(i,e) {
                if (i < firstResizable || (options.forceFitColumns && i >= lastResizable)) { return; }
                $col = $(e);
                $("<div class='slick-resizable-handle' />")
                    .appendTo(e)
                    .bind("dragstart", function(e,dd) {
                        if (!getEditorLock().commitCurrentEdit()) { return false; }
                        pageX = e.pageX;
                        $(this).parent().addClass("slick-header-column-active");
                        var shrinkLeewayOnRight = null, stretchLeewayOnRight = null;
                        // lock each column's width option to current width
                        columnElements.each(function(i,e) { columns[i].previousWidth = $(e).outerWidth(true); });
                        if (options.forceFitColumns) {
                            shrinkLeewayOnRight = 0;
                            stretchLeewayOnRight = 0;
                            // colums on right affect maxPageX/minPageX
                            for (j = i + 1; j < columnElements.length; j++) {
                                c = columns[j];
                                // TODO 
                                if (c.resizable) {
                                    if (stretchLeewayOnRight !== null) {
                                        if (c.maxWidth) {
                                            stretchLeewayOnRight += c.maxWidth - c.previousWidth;
                                        }
                                        else {
                                            stretchLeewayOnRight = null;
                                        }
                                    }
                                    shrinkLeewayOnRight += c.previousWidth - Math.max(c.minWidth || 0, absoluteColumnMinWidth);
                                }
                            }
                        }
                        var shrinkLeewayOnLeft = 0, stretchLeewayOnLeft = 0;
                        for (j = 0; j <= i; j++) {
                            // columns on left only affect minPageX
                            c = columns[j];
                            if (c.resizable) {
                                if (stretchLeewayOnLeft !== null) {
                                    if (c.maxWidth) {
                                        stretchLeewayOnLeft += c.maxWidth - c.previousWidth;
                                    }
                                    else {
                                        stretchLeewayOnLeft = null;
                                    }
                                }
                                shrinkLeewayOnLeft += c.previousWidth - Math.max(c.minWidth || 0, absoluteColumnMinWidth);
                            }
                        }
                        if (shrinkLeewayOnRight === null) { shrinkLeewayOnRight = 100000; }
                        if (shrinkLeewayOnLeft === null) { shrinkLeewayOnLeft = 100000; }
                        if (stretchLeewayOnRight === null) { stretchLeewayOnRight = 100000; }
                        if (stretchLeewayOnLeft === null) { stretchLeewayOnLeft = 100000; }
                        maxPageX = pageX + Math.min(shrinkLeewayOnRight, stretchLeewayOnLeft);
                        minPageX = pageX - Math.min(shrinkLeewayOnLeft, stretchLeewayOnRight);
                        originalCanvasWidth = $canvas.width();
                    })
                    .bind("drag", function(e,dd) {
                        var actualMinWidth, d = Math.min(maxPageX, Math.max(minPageX, e.pageX)) - pageX, x, ci;
                        if (d < 0) { // shrink column
                            x = d;
                            for (j = i; j >= 0; j--) {
                                c = columns[j];
                                if (c.resizable) {
                                    actualMinWidth = Math.max(c.minWidth || 0, absoluteColumnMinWidth);
                                    if (x && c.previousWidth + x < actualMinWidth) {
                                        x += c.previousWidth - actualMinWidth;
                                        c.width = actualMinWidth;
                                    } else {
                                        c.width = c.previousWidth + x;
                                        x = 0;
                                    }
                                }
                            }

                            if (options.forceFitColumns) {
                                x = -d;
                                for (j = i + 1; j < columnElements.length; j++) {
                                    c = columns[j];
                                    if (c.resizable) {
                                        if (x && c.maxWidth && (c.maxWidth - c.previousWidth < x)) {
                                            x -= c.maxWidth - c.previousWidth;
                                            c.width = c.maxWidth;
                                        } else {
                                            c.width =  c.previousWidth + x;
                                            x = 0;
                                        }
                                    }
                                }
                            } else if (options.syncColumnCellResize) {
                                setCanvasWidth(originalCanvasWidth + d);
                            }
                        } else { // stretch column
                            x = d;
                            for (j = i; j >= 0; j--) {
                                c = columns[j];
                                if (c.resizable) {
                                    if (x && c.maxWidth && (c.maxWidth - c.previousWidth < x)) {
                                        x -= c.maxWidth - c.previousWidth;
                                        c.width = c.maxWidth;
                                    } else {
                                        c.width = c.previousWidth + x;
                                        x = 0;
                                    }
                                }
                            }

                            if (options.forceFitColumns) {
                                x = -d;
                                for (j = i + 1; j < columnElements.length; j++) {
                                    c = columns[j];
                                    if (c.resizable) {
                                        actualMinWidth = Math.max(c.minWidth || 0, absoluteColumnMinWidth);
                                        if (x && c.previousWidth + x < actualMinWidth) {
                                            x += c.previousWidth - actualMinWidth;
                                            c.width = actualMinWidth;
                                        } else {
                                            c.width = c.previousWidth + x;
                                            x = 0;
                                        }
                                    }
                                }
                            } else if (options.syncColumnCellResize) {
                                setCanvasWidth(originalCanvasWidth + d);
                            }
                        }
                        applyColumnHeaderWidths();
                        if (options.syncColumnCellResize) {
                            applyColumnWidths();
                        }
                    })
                    .bind("dragend", function(e,dd) {
                        var newWidth; 
                        $(this).parent().removeClass("slick-header-column-active");
// var td = $("#hiddens>td");
                        for (j = 0; j < columnElements.length; j++) {
                            c = columns[j];
                            newWidth = $(columnElements[j]).outerWidth(true);
// test
// if (j == 0) {
// $(td[j]).width(newWidth - 3);
// } else
// $(td[j]).width(newWidth);
// ////
                            if (c.previousWidth !== newWidth && c.rerenderOnResize) {
                                invalidateAllRows();
                            }
                        }
                        applyColumnWidths();
                        resizeCanvas();
                        trigger(self.onColumnsResized, {});
                    });
                });
        }

        function getVBoxDelta($el) {
            var p = ["borderTopWidth", "borderBottomWidth", "paddingTop", "paddingBottom"];
            var delta = 0;
            $.each(p, function(n,val) { delta += parseFloat($el.css(val)) || 0; });
            return delta;
        }
		/**
		 * 测量单元格padding&&border 设置absoluteColumnMinWidth
		 */
        // TODO HEADERS lins
        function measureCellPaddingAndBorder() {
        	//var r = $("<div class='slick-row'/>").appendTo($canvas);
            var h = ["borderLeftWidth", "borderRightWidth", "paddingLeft", "paddingRight"];
            var v = ["borderTopWidth", "borderBottomWidth", "paddingTop", "paddingBottom"];
            //var el = $("<div class='ui-state-default slick-header-column'>-</div>").appendTo($headers);
            var e2 = $("<div class='slick-cell'></div>").appendTo($canvas);
            headerColumnWidthDiff = headerColumnHeightDiff = 9;
            cellWidthDiff = cellHeightDiff = 0;
            for (var i  = 0; i < h.length ; i ++) {
            	//headerColumnWidthDiff += parseFloat(el.css(h[i])) || 0;
            	//headerColumnHeightDiff += parseFloat(el.css(v[i])) || 0;
            	cellWidthDiff += parseFloat(e2.css(h[i])) || 0;
            	cellHeightDiff += parseFloat(e2.css(v[i])) || 0;
            }
           // el.remove();
            e2.remove();
           // r.remove();
            absoluteColumnMinWidth = headerColumnWidthDiff > cellWidthDiff ?headerColumnWidthDiff:cellWidthDiff;
        }
		/**
		 * 在head部分添加css样式
		 */
        function createCssRules() {
            $style = $("<style type='text/css' rel='stylesheet' />").appendTo($("head"));
            var rowHeight = (options.rowHeight - cellHeightDiff);

            var rules = [
                "." + uid + " .slick-header-column { left: 1000px; }",
                "." + uid + " .slick-top-panel { height:" + options.topPanelHeight + "px; }",
                "." + uid + " .slick-headerrow-columns { height:" + options.headerRowHeight + "px; }",
                "." + uid + " .slick-cell { height:" + rowHeight + "px; }",
                "." + uid + " .slick-row { width:" + getRowWidth() + "px; height:" + options.rowHeight + "px; }",
                "." + uid + " .lr { float:none; position:absolute; }"
            ];

            var rowWidth = getRowWidth();
            var x = 0, w;
            for (var i=0; i<columns.length; i++) {
                w = columns[i].width;

                rules.push("." + uid + " .l" + i + " { left: " + x + "px; }");
                rules.push("." + uid + " .r" + i + " { right: " + (rowWidth - x - w) + "px; }");
                rules.push("." + uid + " .c" + i + " { width:" + (w - cellWidthDiff) + "px; }");

                x += columns[i].width;
            }

            if ($style[0].styleSheet) { // IE
                $style[0].styleSheet.cssText = rules.join(" ");
            }
            else {
                $style[0].appendChild(document.createTextNode(rules.join(" ")));
            }

            var sheets = document.styleSheets;
            for (var i=0; i<sheets.length; i++) {
                if ((sheets[i].ownerNode || sheets[i].owningElement) == $style[0]) {
                    stylesheet = sheets[i];
                    break;
                }
            }
        }
// function createCssRules() {
// $style = $("<style type='text/css' rel='stylesheet' />").appendTo($("head"));
// var rowHeight = (options.rowHeight - cellHeightDiff);
// var rules = [
// "." + uid + " .slick-header-column { left: 1000px; }",
// "." + uid + " .slick-top-panel { height:" + options.topPanelHeight + "px; }",
// "." + uid + " .slick-headerrow-columns { height:" + options.headerRowHeight +
// "px; }",
// "." + uid + " .slick-cell { height:" + rowHeight + "px; }",
// "." + uid + " .slick-row { height:" + options.rowHeight + "px; }"
// ];
//			
// for (var i = 0; i < columns.length; i++) {
// rules.push("." + uid + " .l" + i + " { }");
// rules.push("." + uid + " .r" + i + " { }");
// }
// if ($style[0].styleSheet) { // IE
// $style[0].styleSheet.cssText = rules.join(" ");
// } else {
// $style[0].appendChild(document.createTextNode(rules.join(" ")));
// }
// }
        function findCssRule(selector) {
            var rules = (stylesheet.cssRules || stylesheet.rules);

            for (var i=0; i<rules.length; i++) {
                if (rules[i].selectorText == selector)
                    return rules[i];
            }

            return null;
        }

        function removeCssRules() {
            $style.remove();
            stylesheet = null;
        }

        function destroy() {
            getEditorLock().cancelCurrentEdit();

            trigger(self.onBeforeDestroy, {});

            for (var i = 0; i < plugins.length; i++) {
                unregisterPlugin(plugins[i]);
            }

            if (options.enableColumnReorder && $headers.sortable)
                $headers.sortable("destroy");

            unbindAncestorScrollEvents();
            $container.unbind(".slickgrid");
            removeCssRules();

            $canvas.unbind("draginit dragstart dragend drag");
            $container.empty().removeClass(uid);
        }


        // ////////////////////////////////////////////////////////////////////////////////////////////
        // General

        function trigger(evt, args, e) {
            e = e || new Slick.EventData();
            args = args || {};
            args.grid = self;
            return evt.notify(args, e, self);
        }

        function getEditorLock() {
            return options.editorLock;
        }

        function getEditController() {
            return editController;
        }

        function getColumnIndex(id) {
            return columnsById[id];
        }
		
        function autosizeColumns() {
			if (viewportW <= 0 ) resizeCanvas();
			//bug，最大化表格自适应
			viewportW = parseFloat($.css($container[0], "width", true));
            if(viewportW == 0){
            	if($container.parent().width() != 0 && $container.parent().width() < 10000){
            		viewportW = $container.parent().width();
            	}else if($container.parents("div.tabs-panels").length > 0){
            		if($container.parents(".ez-fl").size()>0){
            			viewportW = $container.parents("div.tabs-panels").eq(0).width()*$container.parents(".ez-fl").width()/100;
            		}else{
            			viewportW = $container.parents("div.tabs-panels").eq(0).width()-12;
            		}
            	}
            }
            var i, c,
                widths = [],
                shrinkLeeway = 0,
                availWidth = (options.autoHeight ? viewportW : viewportW - scrollbarDimensions.width), // with
																										// AutoHeight,
																										// we
																										// do
																										// not
																										// need
																										// to
																										// accomodate
																										// the
																										// vertical
																										// scroll
																										// bar
                total = 0,
                existingTotal = 0;

            for (i = 0; i < columns.length; i++) {
                c = columns[i];
                widths.push(c.width);
                existingTotal += c.width;
                shrinkLeeway += c.width - Math.max(c.minWidth || 0, absoluteColumnMinWidth);
            }

            total = existingTotal;
            invalidateAllRows();
            // shrink
            while (total > availWidth) {
                if (!shrinkLeeway) { return; }
                var shrinkProportion = (total - availWidth) / shrinkLeeway;
                for (i = 0; i < columns.length && total > availWidth; i++) {
                    c = columns[i];
                    if (!c.resizable || c.minWidth === c.width || c.width === absoluteColumnMinWidth) { continue; }
                    var shrinkSize = Math.floor(shrinkProportion * (c.width - Math.max(c.minWidth || 0, absoluteColumnMinWidth))) || 1;
                    total -= shrinkSize;
                    widths[i] -= shrinkSize;
                }
            }
			if (total <= 0) return;
            // grow
            var previousTotal = total;
            while (total < availWidth) {
                var growProportion = availWidth / total;
                for (i = 0; i < columns.length && total < availWidth; i++) {
                    c = columns[i];
                    if (!c.resizable || c.maxWidth <= c.width) { continue; }
                    var growSize = Math.min(Math.floor(growProportion * c.width) - c.width, (c.maxWidth - c.width) || 1000000) || 1;
                    total += growSize;
                    widths[i] += growSize;
                }
                if (previousTotal == total) break; // if total is not changing,
													// will result in infinite
													// loop
                previousTotal = total;
            }

            for (i=0; i<columns.length; i++) {
                columns[i].width = widths[i];
            }

            applyColumnHeaderWidths();
            applyColumnWidths();
            resizeCanvas();
            //过滤框的resize
            updateHeaderRow();
        }

        function applyColumnHeaderWidths() {
            var h;
            for (var i = 0, headers = $headers.children(), ii = headers.length; i < ii; i++) {
                h = $(headers[i]);
                if (h.width() !== columns[i].width - headerColumnWidthDiff) {
                    h.width(columns[i].width - headerColumnWidthDiff);
                }
            }
        }

        function applyColumnWidths() {
            var rowWidth = getRowWidth();
            var x = 0, w, rule;
            for (var i = 0; i < columns.length; i++) {
                w = columns[i].width;

                rule = findCssRule("." + uid + " .c" + i);
                rule.style.width = (w - cellWidthDiff) + "px";

                rule = findCssRule("." + uid + " .l" + i);
                rule.style.left = x + "px";

                rule = findCssRule("." + uid + " .r" + i);
                rule.style.right = (rowWidth - x - w) + "px";

                x += columns[i].width;
            }

            rule = findCssRule("." + uid + " .slick-row");
            rule.style.width = (rowWidth > 0?rowWidth : 0 )+ "px";
        }
		// TODO SETSORT
        /**
		 * 添加一些排序样式
		 */
        function setSortColumn(columnId, ascending) {
            sortColumnId = columnId;
            sortAsc = ascending;
            var columnIndex = getColumnIndex(sortColumnId);// 得到此id的列序号

            $headers.children().removeClass("slick-header-column-sorted");
            $headers.find(".slick-sort-indicator").removeClass("slick-sort-indicator-asc slick-sort-indicator-desc");

            if (columnIndex != null) {
                $headers.children().eq(columnIndex)
                    .addClass("slick-header-column-sorted")
                    .find(".slick-sort-indicator")
                        .addClass(sortAsc ? "slick-sort-indicator-asc" : "slick-sort-indicator-desc");
            }
        }
		
        function handleSelectedRangesChanged(e, ranges) {
            selectedRows = [];
            var hash = {};
            for (var i = 0; i < ranges.length; i++) {
                for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
                    if (!hash[j]) {  // prevent duplicates
                        selectedRows.push(j);
                    }
                    hash[j] = {};
                    for (var k = ranges[i].fromCell; k <= ranges[i].toCell; k++) {
                        if (canCellBeSelected(j, k)) {
                            hash[j][columns[k].id] = options.selectedCellCssClass;
                        }
                    }
                }
            }

            setCellCssStyles(options.selectedCellCssClass, hash);

       		if (typeof options.onSelectChange == "function") { // lins添加onselectchange
       			options.onSelectChange(getSelectRowsDataToObj(), getSelectedRows().length);
       		}
            trigger(self.onSelectedRowsChanged, {rows:getSelectedRows(), data:getSelectRowsDataToObj()}, e);
        }

        function getColumns() {
            return columns;
        }
        function getHiddenColumns(){
        	return hiddenColumns;
        }
        
        function setColumns(columnDefinitions, reorder) {
            columns = columnDefinitions;
            invalidateAllRows();
            createColumnHeaders(reorder);
            removeCssRules();
            createCssRules();
            resizeAndRender();
            handleScroll();
	        if (options.columnFilter) {
	            $(getHeaderRow()).delegate(":input", "change keyup", function(e) {
	                columnFilters[$(this).data("columnId")] = $.trim($(this).val());
	                dataView.refresh();
	            });
	            updateHeaderRow();
	        }
        }

        function getOptions() {
            return options;
        }

        function setOptions(args) {
            if (!getEditorLock().commitCurrentEdit()) {
                return;
            }

            makeActiveCellNormal();

            if (options.enableAddRow !== args.enableAddRow) {
                invalidateRow(getDataLength());
            }

            options = $.extend(options,args);

            render();
        }

        function setData(newData,scrollToTop) {
            invalidateAllRows();
            var tmp = null;
             if(!jQuery.isArray(newData)){
            	tmp = newData.list || [];
            }
            for(var i=0;i<tmp.length;i++){
            	tmp[i][pId] = i;
            }
            data = tmp;
            if (scrollToTop)
                scrollTo(0);
            return tmp;
        }
		
        function getData() {
            return data;
        }

        function getDataLength() {
            if (data.getLength) {
                return data.getLength();
            }
            else {
                return data.length;
            }
        }
		/**
		 * 获得数据内容
		 */
        function getDataItem(i) {
            if (data.getItem) {
                return data.getItem(i);
            } else if (data.getData) {
            	return data.getData(i);
            }
            else {
                return data[i];
            }
        }

        function getTopPanel() {
            return $topPanel[0];
        }

        function showTopPanel() {
            options.showTopPanel = true;
            $topPanelScroller.slideDown("fast", resizeCanvas);
        }

        function hideTopPanel() {
            options.showTopPanel = false;
            $topPanelScroller.slideUp("fast", resizeCanvas);
        }

        function showHeaderRowColumns() {
            options.showHeaderRow = true;
            $headerRowScroller.slideDown("fast", resizeCanvas);
        }

        function hideHeaderRowColumns() {
            options.showHeaderRow = false;
            $headerRowScroller.slideUp("fast", resizeCanvas);
        }

        // ////////////////////////////////////////////////////////////////////////////////////////////
        // Rendering / Scrolling
		// TODO SCROLLTO
        function scrollTo(y) {
            var oldOffset = offset;

            page = Math.min(n-1, Math.floor(y / ph));
            offset = Math.round(page * cj);
            var newScrollTop = y - offset;

            if (offset != oldOffset) {
                var range = getVisibleRange(newScrollTop);// 获取可渲染区域
                cleanupRows(range.top,range.bottom);
                updateRowPositions();
            }

            if (prevScrollTop != newScrollTop) {
                scrollDir = (prevScrollTop + oldOffset < newScrollTop + offset) ? 1 : -1;
                $viewport[0].scrollTop = (lastRenderedScrollTop = scrollTop = prevScrollTop = newScrollTop);

                trigger(self.onViewportChanged, {});
            }
        }

        function defaultFormatter(row, cell, value, columnDef, dataContext) {
            return (value === null || value === undefined) ? "" : value;
        }

        function getFormatter(row, column) {
            var rowMetadata = data.getItemMetadata && data.getItemMetadata(row);

            // look up by id, then index
            var columnOverrides = rowMetadata &&
                    rowMetadata.columns &&
                    (rowMetadata.columns[column.id] || rowMetadata.columns[getColumnIndex(column.id)]);

            return (columnOverrides && columnOverrides.formatter) ||
                    (rowMetadata && rowMetadata.formatter) ||
                    column.formatter ||
                    (options.formatterFactory && options.formatterFactory.getFormatter(column)) ||
                    defaultFormatter;
        }

        function getEditor(row, cell) {
            var column = columns[cell];
            var rowMetadata = data.getItemMetadata && data.getItemMetadata(row);
            var columnMetadata = rowMetadata && rowMetadata.columns;

            if (columnMetadata && columnMetadata[column.id] && columnMetadata[column.id].editor !== undefined) {
                return columnMetadata[column.id].editor;
            }
            if (columnMetadata && columnMetadata[cell] && columnMetadata[cell].editor !== undefined) {
                return columnMetadata[cell].editor;
            }

            return column.editor || (options.editorFactory && options.editorFactory.getEditor(column));
        }
		/**
		 * important !!!!!!!!!!!!!!!!!!!!!!!!!!!!! 不要随意修改
		 * 
		 * @author lins
		 */
        function appendRowHtml(stringArray, row) {
// var test = new Date();
            var d = getDataItem(row);
            var dataLoading = row < getDataLength() && !d;
            var cellCss;
            var rowCss = "slick-row odd" ;

            var metadata = data.getItemMetadata && data.getItemMetadata(row);
                        
            if (metadata && metadata.cssClasses) {
                rowCss += " " + metadata.cssClasses;
            }
			if (typeof options.rowColorfn == "function") {
				var datColor = options.rowColorfn(data.getItem(row));
				if (datColor) {
					stringArray.push("<div class='ui-widget-content " + rowCss + "' row='" + row + "' style='background-color:" + datColor + ";top:" + (options.rowHeight*row-offset) + "px'>");
				} else {
	            	stringArray.push("<div class='ui-widget-content " + rowCss + "' row='" + row + "' style='top:" + (options.rowHeight*row-offset) + "px'>");
				}
			} else {
            	stringArray.push("<div class='ui-widget-content " + rowCss + "' row='" + row + "' style='top:" + (options.rowHeight*row-offset) + "px'>");
			}

            var colspan;
           // var rowHasColumnData = metadata && metadata.columns;
            
            for (var i=0, cols=columns.length; i<cols; i++) {
                var m = columns[i];
                colspan = getColspan(row, i);  
				if (d.__group != true) {
	                cellCss = "slick-cell lr l" + i + " r" + Math.min(columns.length -1, i + colspan - 1) + (m.cssClass ? " " + m.cssClass : "");
				} else 
	                cellCss = "slick-cell lr l" + i + " r" + Math.min(columns.length -1, i + colspan - 1);
					
					
                if (row === activeRow && i === activeCell) {
                    cellCss += (" active");
                }

                for (var key in cellCssClasses) {
                    if (cellCssClasses[key][row] && cellCssClasses[key][row][m.id]) {
                        cellCss += (" " + cellCssClasses[key][row][m.id]);
                    }
                }
				if ((m.dataType == "number" || m.dataType == "date") && d.__group != true && d.__groupTotals != true) {
					if (m.dataAlign != undefined)
 						stringArray.push("<div class='" + cellCss + "' style='text-align:" + m.dataAlign + "'>");
 					else 
 						stringArray.push("<div class='" + cellCss + "' style='text-align:right'>");
                } else if(m.dataAlign != undefined && d.__group != true && d.__groupTotals != true) {
                	stringArray.push("<div class='" + cellCss + "' style='text-align:" + m.dataAlign + "'>");
				} else if(m.totalsAlign !=undefined && d.__groupTotals == true){
					stringArray.push("<div class='" + cellCss + "' style='text-align:"+m.totalsAlign+"'>");
				} else{
					stringArray.push("<div class='" + cellCss + "'>");
				}
                if (d) {
                    var htmlData = getFormatter(row, m)(row, i, d[m.field], m, d);
                	if (m.dataType == "date"){
                		htmlData = htmlData.substring(0,10);
                	}
                	if (m.id === "__no" && d.__group != true && d.__groupTotals != true) {
                		htmlData = Number(row) + 1; 
                	} else 
                    	htmlData = htmlData;
                    stringArray.push(htmlData);
                } 
                // 添加列column图标事件
                //liys修改,有totals时,在totals行不添加图标
                if (m.operate == true) {
                	var operateMenus = [];
                	operateMenus.push("<center><div class='slick-item-operate' row='");
                	operateMenus.push(row);
                	operateMenus.push("' ><span class='slick-item-operate-button'>");
                	operateMenus.push("<b class='slick-item-operate-icon'></b></span><span class='slick-item-operate-text'></span></div></center>");
                	stringArray.push(operateMenus.join(""));
                }
                if (m.icon && !d.__groupTotals) {
            		var center = "<center>"
            		center +=  "<div title='单击' class='" + m.icon + "' style='cursor:pointer;height:16px;width:16px;margin-top:3px'></div>"
                	center += "</center>"
                	stringArray.push(center);
                }
                stringArray.push("</div>");

                if (colspan)
                    i += (colspan - 1);
            }

            stringArray.push("</div>");
        }

        function cleanupRows(rangeToKeep) {
            for (var i in rowsCache) {
                if (((i = parseInt(i, 10)) !== activeRow) && (i < rangeToKeep.top || i > rangeToKeep.bottom)) {
                    removeRowFromCache(i);
                }
            }
        }

        function invalidate() {
           updateRowCount();
           invalidateAllRows();
           render();
        }

        function invalidateAllRows() {
            if (currentEditor) {
                makeActiveCellNormal();
            }
            for (var row in rowsCache) {
                removeRowFromCache(row);
            }
        }

        function removeRowFromCache(row) {
            var node = rowsCache[row];
            if (!node) { return; }
            $canvas[0].removeChild(node);

            delete rowsCache[row];
            delete postProcessedRows[row];
            renderedRows--;
            counter_rows_removed++;
        }

        function invalidateRows(rows) {
            var i, rl;
            if (!rows || !rows.length) { return; }
            scrollDir = 0;
            for (i=0, rl=rows.length; i<rl; i++) {
                if (currentEditor && activeRow === i) {
                    makeActiveCellNormal();
                }

                if (rowsCache[rows[i]]) {
                    removeRowFromCache(rows[i]);
                }
            }
        }

        function invalidateRow(row) {
            invalidateRows([row]);
        }

        function updateCell(row,cell) {
            var cellNode = getCellNode(row,cell);
            if (!cellNode) {
                return;
            }

            var m = columns[cell], d = getDataItem(row);
            if (currentEditor && activeRow === row && activeCell === cell) {
                currentEditor.loadValue(d);
            }
            else {
                cellNode.innerHTML = d ? getFormatter(row, m)(row, cell, d[m.field], m, d) : "";
                invalidatePostProcessingResults(row);
            }
        }

        function updateRow(row) {
            if (!rowsCache[row]) { return; }

            $(rowsCache[row]).children().each(function(i) {
                var m = columns[i];
                if (row === activeRow && i === activeCell && currentEditor) {
                    currentEditor.loadValue(getDataItem(activeRow));
                }
                else if (getDataItem(row)) {
                	if(m.id == "__no"){
                		this.innerHTML = getFormatter(row, m)(row, i, getDataItem(row)[m.field] + 1, m, getDataItem(row));
                	}else if(m.icon){// liys修改，编辑表格后，icon列图标消失
                		this.innerHTML = "<center><div title='单击' class='" + m.icon + "' style='cursor:pointer;height:16px;width:16px;margin-top:3px'></div></center>"
                	} else
                		this.innerHTML = getFormatter(row, m)(row, i, getDataItem(row)[m.field], m, getDataItem(row));
                }
                else {
                    innerHTML = "";
                }
            });

            invalidatePostProcessingResults(row);
        }

        function getViewportHeight() {
        	var off = 37;//liys修改，datagrid高度问题，以前为25
        	if (options.showToolpaging) off = 64;
            return parseFloat($.css($container[0], "height", true)) -
                options.headerHeight -
                getVBoxDelta($headers) -
                (options.showTopPanel ? options.topPanelHeight + getVBoxDelta($topPanelScroller) : 0) -
                (options.showHeaderRow ? options.headerRowHeight + getVBoxDelta($headerRowScroller) : 0) - off;
        }

        function resizeCanvas(isNotDefault) {
            if (options.autoHeight) {
                viewportH = options.rowHeight * (getDataLength() + (options.enableAddRow ? 1 : 0) + (options.leaveSpaceForNewRows? numVisibleRows - 1 : 0));
            }
            else {
                viewportH = getViewportHeight();
            }

            numVisibleRows = Math.ceil(viewportH / options.rowHeight);
            viewportW = parseFloat($.css($container[0], "width", true));
            if(viewportW == 0){
            	if($container.parent().width() != 0 && $container.parent().width() < 10000){
            		viewportW = $container.parent().width();
            	}else if($container.parents("div.tabs-panels").length > 0){
            		if($container.parents(".ez-fl").size()>0){
            			viewportW = $container.parents("div.tabs-panels").eq(0).width()*$container.parents(".ez-fl").width()/100;
            		}else{
            			viewportW = $container.parents("div.tabs-panels").eq(0).width()-12;
            		}
            	}
            }
            // liys,如果存在表头换行,则重新计算高度
            if(options.headerColumnsRows && !isNaN(options.headerColumnsRows) && options.headerColumnsRows > 1){
            	viewportH = viewportH - 22*(options.headerColumnsRows-1);
        	}
            $viewport.height(viewportH);
            var w = 0, i = columns.length;
            while (i--) {
                w += columns[i].width;
            }
            setCanvasWidth(w);

            updateRowCount();
            render();
            if (!isNotDefault) {
	            var box = absBox($container[0]).width;
		        defaultWidth = absBox($container[0]).width;
		        defaultHeight = absBox($container[0]).height;
            }
        }

        
        function resizeAndRender() {
            if (options.forceFitColumns) {
            	// majie,判断datagriditem的宽度是否缩减显示
            	if(!Base.globvar.columnsWidthsOverView){
            		 autosizeColumns();
                }else{
                	if(Base.globvar.columnsWidthsOverView){
                		var totalWidth=0
                		for (i = 0; i < columns.length; i++) {
                            c = columns[i];
                            totalWidth += c.width;
                        }
                		if(totalWidth>viewportW){
                			resizeCanvas();
                		}else{
                			 autosizeColumns();
                		}
                	}
            	}
               
            } else {
                resizeCanvas();
            }
        }

        function updateRowCount() {
            var newRowCount = getDataLength() + (options.enableAddRow?1:0) + (options.leaveSpaceForNewRows?numVisibleRows-1:0);
            var oldH = h;

            // remove the rows that are now outside of the data range
            // this helps avoid redundant calls to .removeRow() when the size of
			// the data decreased by thousands of rows
            var l = options.enableAddRow ? getDataLength() : getDataLength() - 1;
            for (var i in rowsCache) {
                if (i >= l) {
                    removeRowFromCache(i);
                }
            }
            th = Math.max(options.rowHeight * newRowCount, viewportH - scrollbarDimensions.height);
            if (th < maxSupportedCssHeight) {
                // just one page
                h = ph = th;
                n = 1;
                cj = 0;
            }
            else {
                // break into pages
                h = maxSupportedCssHeight;
                ph = h / 100;
                n = Math.floor(th / ph);
                cj = (th - h) / (n - 1);
            }

            if (h !== oldH) {
            	// TODO 林森添加
            	// liys,如果存在表头换行,则重新计算高度
            	if(options.headerColumnsRows && !isNaN(options.headerColumnsRows) && options.headerColumnsRows > 1){
            		h = h - 22*(options.headerColumnsRows-1);
            	}
            	if (options.showToolpaging){
            		// h = h - 10;
                	$canvas.css("height", h);
            	} else 
                	$canvas.css("height", h);
                scrollTop = $viewport[0].scrollTop;
            }

            var oldScrollTopInRange = (scrollTop + offset <= th - viewportH);

            if (th == 0 || scrollTop == 0) {
                page = offset = 0;
            }
            else if (oldScrollTopInRange) {
                // maintain virtual position
                scrollTo(scrollTop+offset);
            }
            else {
                // scroll to bottom
                scrollTo(th-viewportH);
            }

            if (h != oldH && options.autoHeight) {
                resizeCanvas();
            }
        }

        function getVisibleRange(viewportTop) {
            if (viewportTop == null)
                viewportTop = scrollTop;

            return {
                top: Math.floor((scrollTop+offset)/options.rowHeight),
                bottom: Math.ceil((scrollTop+offset+viewportH)/options.rowHeight)
            };
        }

        function getRenderedRange(viewportTop) {
            var range = getVisibleRange(viewportTop);
            var buffer = Math.round(viewportH/options.rowHeight);
            var minBuffer = 3;

            if (scrollDir == -1) {
                range.top -= buffer;
                range.bottom += minBuffer;
            }
            else if (scrollDir == 1) {
                range.top -= minBuffer;
                range.bottom += buffer;
            }
            else {
                range.top -= minBuffer;
                range.bottom += minBuffer;
            }

            range.top = Math.max(0,range.top);
            range.bottom = Math.min(options.enableAddRow ? getDataLength() : getDataLength() - 1,range.bottom);

            return range;
        }
		/**
		 * 渲染行
		 */
        function renderRows(range) {
            var i, l,
                parentNode = $canvas[0],
                rowsBefore = renderedRows,
                stringArray = [],
                rows = [],
                startTimestamp = new Date(),
                needToReselectCell = false;

            for (i = range.top; i <= range.bottom; i++) {
                if (rowsCache[i]) { continue; }
                renderedRows++;
                rows.push(i);
                var sn = i; // lins添加用于计算sn号
                appendRowHtml(stringArray, i, sn);
                if (activeCellNode && activeRow === i) {
                    needToReselectCell = true;
                }
                counter_rows_rendered++;
            }

            var x = document.createElement("div");
// var test = new Date();
            x.innerHTML = stringArray.join("");
// alert(-(test.getTime()- new Date().getTime()));
// for (var sa = 0; sa < stringArray.length; sa ++)
// $(x).append(stringArray[sa]);
            for (i = 0, l = x.childNodes.length; i < l; i++) {
                rowsCache[rows[i]] = parentNode.appendChild(x.firstChild);
            }

            if (needToReselectCell) {
                activeCellNode = getCellNode(activeRow,activeCell);
            }

            if (renderedRows - rowsBefore > 5) {
                avgRowRenderTime = (new Date() - startTimestamp) / (renderedRows - rowsBefore);
            }
            
        }

        function startPostProcessing() {
            if (!options.enableAsyncPostRender) { return; }
            clearTimeout(h_postrender);
            h_postrender = setTimeout(asyncPostProcessRows, options.asyncPostRenderDelay);
        }

        function invalidatePostProcessingResults(row) {
            delete postProcessedRows[row];
            postProcessFromRow = Math.min(postProcessFromRow,row);
            postProcessToRow = Math.max(postProcessToRow,row);
            startPostProcessing();
        }

        function updateRowPositions() {
            for (var row in rowsCache) {
                rowsCache[row].style.top = (row*options.rowHeight-offset) + "px";
            }
        }
		/**
		 * 渲染
		 */
        function render() {
// var test = new Date();
            var visible = getVisibleRange();
            var rendered = getRenderedRange();

            var row = dataView.getItems().length;
            var d = getDataItem(row)
            if(d){
            	if(d.__group != true && d.__groupTotals == true){
            		var stringArray = [];
                	var d = getDataItem(row);
                    var dataLoading = row < getDataLength() && !d;
                    var cellCss;
                    var rowCss = "slick-row odd" ;

                    var metadata = data.getItemMetadata && data.getItemMetadata(row);
                                
                    if (metadata && metadata.cssClasses) {
                        rowCss += " " + metadata.cssClasses;
                    }

                    stringArray.push("<div class='ui-widget-content " + rowCss + "' row='" + row + "'>");

                    var colspan;
                   // var rowHasColumnData = metadata && metadata.columns;
                    
                    for (var i=0, cols=columns.length; i<cols; i++) {
                        var m = columns[i];
                        colspan = getColspan(row, i);  
        				if (d.__group != true) {
        	                cellCss = "slick-cell lr l" + i + " r" + Math.min(columns.length -1, i + colspan - 1) + (m.cssClass ? " " + m.cssClass : "");
        				} else 
        	                cellCss = "slick-cell lr l" + i + " r" + Math.min(columns.length -1, i + colspan - 1);
        					
        					
                        if (row === activeRow && i === activeCell) {
                            cellCss += (" active");
                        }

                        for (var key in cellCssClasses) {
                            if (cellCssClasses[key][row] && cellCssClasses[key][row][m.id]) {
                                cellCss += (" " + cellCssClasses[key][row][m.id]);
                            }
                        }
        				if(m.totalsAlign !=undefined && d.__groupTotals == true){
        					stringArray.push("<div class='" + cellCss + "' style='text-align:"+m.totalsAlign+"'>");
        				} else{
        					stringArray.push("<div class='" + cellCss + "'>");
        				}
                        if (d) {
                            var htmlData = getFormatter(row, m)(row, i, d[m.field], m, d);
                        	if (m.dataType == "date"){
                        		htmlData = htmlData.substring(0,10);
                        	}
                        	if (m.id === "__no" && d.__group != true && d.__groupTotals != true) {
                        		htmlData = Number(row) + 1; 
                        	} else 
                            	htmlData = htmlData;
                            stringArray.push(htmlData);
                        } 
                        stringArray.push("</div>");

                        if (colspan)
                            i += (colspan - 1);
                    }
                    stringArray.push("</div>");
                    if(stringArray.length>0){
                    	 $topPanel.html(stringArray.join(""));
                         $topPanelScroller.show();
                    }
            	}
            	
            }
            
            // remove rows no longer in the viewport
            cleanupRows(rendered);

            // add new rows
            renderRows(rendered);

            postProcessFromRow = visible.top;
            postProcessToRow = Math.min(options.enableAddRow ? getDataLength() : getDataLength() - 1, visible.bottom);
            startPostProcessing();

            lastRenderedScrollTop = scrollTop;
            h_render = null;
// alert(-(test.getTime()- new Date().getTime()));
        }

        function handleScroll() {
            scrollTop = $viewport[0].scrollTop;
            var scrollLeft = $viewport[0].scrollLeft;
            var scrollDist = Math.abs(scrollTop - prevScrollTop);

            if (scrollLeft !== prevScrollLeft) {
                prevScrollLeft = scrollLeft;
                $headerScroller[0].scrollLeft = scrollLeft;
                $topPanelScroller[0].scrollLeft = scrollLeft;
                $headerRowScroller[0].scrollLeft = scrollLeft;
            }

            if (scrollDist) {
                scrollDir = prevScrollTop < scrollTop ? 1 : -1;
                prevScrollTop = scrollTop;

                // switch virtual pages if needed
                if (scrollDist < viewportH) {
                    scrollTo(scrollTop + offset);
                }
                else {
                    var oldOffset = offset;
                    page = Math.min(n - 1, Math.floor(scrollTop * ((th - viewportH) / (h - viewportH)) * (1 / ph)));
                    offset = Math.round(page * cj);
                    if (oldOffset != offset)
                        invalidateAllRows();
                }

                if (h_render)
                    clearTimeout(h_render);
				// 重绘
                if (Math.abs(lastRenderedScrollTop - scrollTop) < viewportH)
                    render();
                else
                    h_render = setTimeout(render, 50);

                trigger(self.onViewportChanged, {});
            }
			/*
			 * 如果注册了onscroll在这里调用
			 */
            trigger(self.onScroll, {scrollLeft:scrollLeft, scrollTop:scrollTop});
        }

        function asyncPostProcessRows() {
            while (postProcessFromRow <= postProcessToRow) {
                var row = (scrollDir >= 0) ? postProcessFromRow++ : postProcessToRow--;
                var rowNode = rowsCache[row];
                if (!rowNode || postProcessedRows[row] || row>=getDataLength()) { continue; }

                var d = getDataItem(row), cellNodes = rowNode.childNodes;
                for (var i=0, j=0, l=columns.length; i<l; ++i) {
                    var m = columns[i];
                    if (m.asyncPostRender) { m.asyncPostRender(cellNodes[j], postProcessFromRow, d, m); }
                    ++j;
                }

                postProcessedRows[row] = true;
                h_postrender = setTimeout(asyncPostProcessRows, options.asyncPostRenderDelay);
                return;
            }
        }

        function addCellCssStyles(key,hash) {
            if (cellCssClasses[key]) {
                throw "addCellCssStyles: cell CSS hash with key '" + key + "' already exists.";
            }

            cellCssClasses[key] = hash;

            var node;
            for (var row in rowsCache) {
                if (hash[row]) {
                    for (var columnId in hash[row]) {
                        node = getCellNode(row, getColumnIndex(columnId));
                        if (node) {
                            $(node).addClass(hash[row][columnId]);
                        }
                    }
                }
            }
        }

        function removeCellCssStyles(key) {
            if (!cellCssClasses[key]) {
                return;
            }

            var node;
            for (var row in rowsCache) {
                if (cellCssClasses[key][row]) {
                    for (var columnId in cellCssClasses[key][row]) {
                        node = getCellNode(row, getColumnIndex(columnId));
                        if (node) {
                            $(node).removeClass(cellCssClasses[key][row][columnId]);
                        }
                    }
                }
            }

            delete cellCssClasses[key];
        }

        function setCellCssStyles(key,hash) {
            removeCellCssStyles(key);
            addCellCssStyles(key,hash);
        }

        function flashCell(row, cell, speed) {
        	row = Number(row);
        	cell = Number(cell);
            speed = Number(speed) || 100;
            if (rowsCache[row]) {
                var $cell = $(getCellNode(row,cell));

                function toggleCellClass(times) {
                    if (!times) return;
                    setTimeout(function() {
                        $cell.queue(function() {
                            $cell.toggleClass(options.cellFlashingCssClass).dequeue();
                            toggleCellClass(times-1);
                        });
                    },
                    speed);
                }

                toggleCellClass(6);
            }
        }

        // ////////////////////////////////////////////////////////////////////////////////////////////
        // Interactivity

        function handleDragInit(e,dd) {
            var cell = getCellFromEvent(e);
            if (!cell || !cellExists(cell.row, cell.cell)) {
                return false;
            }

            retval = trigger(self.onDragInit, dd, e);
            if (e.isImmediatePropagationStopped()) {
                return retval;
            }

            // if nobody claims to be handling drag'n'drop by stopping immediate
			// propagation,
            // cancel out of it
            return false;
        }

        function handleDragStart(e,dd) {
            var cell = getCellFromEvent(e);
            if (!cell || !cellExists(cell.row, cell.cell)) {
                return false;
            }

            var retval = trigger(self.onDragStart, dd, e);
            if (e.isImmediatePropagationStopped()) {
                return retval;
            }

            return false;
        }

        function handleDrag(e,dd) {
            return trigger(self.onDrag, dd, e);
        }

        function handleDragEnd(e,dd) {
            trigger(self.onDragEnd, dd, e);
        }

        function handleKeyDown(e) {
            trigger(self.onKeyDown, {}, e);
            var handled = e.isImmediatePropagationStopped();

            if (!handled) {
                if (!e.shiftKey && !e.altKey && !e.ctrlKey) {
                    if (e.which == 27) {
                        if (!getEditorLock().isActive()) {
                            return; // no editing mode to cancel, allow bubbling
									// and default processing (exit without
									// cancelling the event)
                        }
                        cancelEditAndSetFocus();
                    }
                    else if (e.which == 37) {
                        navigateLeft();
                    }
                    else if (e.which == 39) {
                        navigateRight();
                    }
                    else if (e.which == 38) {
                        navigateUp();
                    }
                    else if (e.which == 40) {
                        navigateDown();
                    }
                    else if (e.which == 9) {
                        navigateNext();
                    }
                    else if (e.which == 13) {
                        if (options.editable) {
                            if (currentEditor) {
                                // adding new row
                                if (activeRow === getDataLength()) {
                                    navigateDown();
                                }
                                else {
                                    commitEditAndSetFocus();
                                }
                            } else {
                                if (getEditorLock().commitCurrentEdit()) {
                                	// 林森修改20120905 只支持编辑框回车焦点
                                	var i = activeCell;
                                	for (;i < columns.length; i ++) {
	                                    if (makeActiveCellEditable() == false) {
	                                    	navigateNext();
	                                    } else break;
                                    }
                                    // makeActiveCellEditable(); 原版本
                                } 
                            }
                        } 
                    }
                    else
                        return;
                }
                else if (e.which == 9 && e.shiftKey && !e.ctrlKey && !e.altKey) {
                    navigatePrev();
                }
                else
                    return;
            }

            // the event has been handled so don't let parent element
			// (bubbling/propagation) or browser (default) handle it
            e.stopPropagation();
            e.preventDefault();
            try {
                e.originalEvent.keyCode = 0; // prevent default behaviour for
												// special keys in IE browsers
												// (F3, F5, etc.)
            }
            catch (error) {} // ignore exceptions - setting the original
								// event's keycode throws access denied
								// exception for "Ctrl" (hitting control key
								// only, nothing else), "Shift" (maybe others)
        }
		// TODO
        function handleClick(e) {
        	if ($outDiv) $outDiv.remove();
            var cell = getCellFromEvent(e);
            if (!cell || (currentEditor !== null && activeRow == cell.row && activeCell == cell.cell)) {
                return;
            }
            var datac = $.extend({},getDataItem(cell.row),{row:cell.row, cell:cell.cell, _gridId :getGridId()} );
            
            var click = columns[cell.cell].click;
            
            if (click) {
            	columns[getCellFromEvent(e).cell].click(datac,e);
                e.stopImmediatePropagation();
            }
            
            var operate = columns[cell.cell].operate;
            if (operate) {
            	showOperateItem(columns[cell.cell].operateMenus, datac, e);
                e.stopImmediatePropagation();
            }
            
            var returnValue = trigger(self.onClick, datac, e); // 行单击事件触发
            // alert(returnValue);
            // if ( returnValue === false) return ;
            if (options.onChecked)
            	if (!options.onChecked(getDataItem(cell.row))) return;
            if (e.isImmediatePropagationStopped()) {
                return;
            }
			// edit
            if (canCellBeActive(cell.row, cell.cell)) {
                if (!getEditorLock().isActive() || getEditorLock().commitCurrentEdit()) {
                    scrollRowIntoView(cell.row,false);
                    if(options.clickActiveStyle){
                    	setActiveCellInternal(getCellNode(cell.row,cell.cell), (cell.row === getDataLength()) || options.autoEdit);
                    }
                }
            }
        }
        var $outDiv = null;//用于显示操作面板
        function showOperateItem(menus, datac ,e){
        	var $target = $(e.target);
        	if ($target.hasClass("slick-cell") || $target.is("center")) return;
        	var offset = {};
    		if ($target.hasClass("slick-item-operate-text")){
    			offset = $target.prev(".slick-item-operate-button").offset();
        	} else {
        		offset = $target.closest(".slick-item-operate-button").offset();
        	}
    		if (!offset || offset.top == 0 || offset.left == 0) return;
        	if ($outDiv) {$outDiv.remove()}
        	var innerMenu = [];
        	innerMenu.push("<div class='slick-ffb ui-multiselect-menu ui-widget'  style='width:132px ;top: 21px; position: absolute; display: block;'>");
    		innerMenu.push("<div style='overflow-x: hidden; overflow-y: auto; height: auto;' class='content'>")
    		innerMenu.push("</div>");
    		$outDiv = $(innerMenu.join("")).appendTo($viewport);
    		$outDiv.bind("mouseleave", function(event){
    			$outDiv.remove();
    			return false;
    		});
        	for (var i = 0; i < menus.length; i ++){
        		var item = [];
        			item.push("<div class='slick-ffb-item' style='cursor: pointer;'>");
        			item.push(menus[i].name);
        			item.push("</div>");
        		var click = menus[i].click;
        		function outClick(datac, e){
        			var innerClick = click;
        			return function (){innerClick(datac, e);$outDiv.remove();}
         		}
        		var $item = $(item.join("")).bind("click", outClick(datac, e));
        		$item.appendTo($outDiv);
        	}
        	var offsetParent = $viewport.offset();
        	$outDiv.css("top", offset.top - offsetParent.top + 5 + $viewport[0].scrollTop)
        	$outDiv.css("left", offset.left - offsetParent.left - $outDiv.width() + $viewport[0].scrollLeft);
        	$("body").bind("click.showOperateItemBody",
					function(e) {
						var srcobj;
						if ($.browser.msie) {
							srcobj = e.srcElement;
						} else {
							srcobj = e.target;
						}
						if (srcobj && srcobj === $outDiv  ) {
							return;
						}
						$outDiv.remove();
						$("body").unbind(".showOperateItemBody");
		    			return false;
				});
        }

        function handleContextMenu(e) {
            var $cell = $(e.target).closest(".slick-cell", $canvas);
            if ($cell.length === 0) { return; }

            // are we editing this cell?
            if (activeCellNode === $cell[0] && currentEditor !== null) { return; }

            trigger(self.onContextMenu, {}, e);
        }
		// TODO 单元格双击事件
        function handleDblClick(e) {
        	// alert("aa")
            var cell = getCellFromEvent(e);
            if (!cell || (currentEditor !== null && activeRow == cell.row && activeCell == cell.cell)) {
                return;
            }
			var datac = $.extend({},getDataItem(cell.row),{row:cell.row, cell:cell.cell} );
            trigger(self.onDblClick, datac, e);
            if (e.isImmediatePropagationStopped()) {
                return;
            }
		 	// 如果支持修改
            if (options.editable) {
                gotoCell(cell.row, cell.cell, true);
            }
            if(options.clickActiveStyle){
            }else{
            	// 表格双击去掉选择单元格样式
	            if(activeCellNode){
	            	makeActiveCellNormal();
	                $(activeCellNode).removeClass("active");
	            }
            }
            
        }

        function handleHeaderContextMenu(e) {
            var $header = $(e.target).closest(".slick-header-column", ".slick-header-columns");
            var column = $header && columns[self.getColumnIndex($header.data("fieldId"))];
            trigger(self.onHeaderContextMenu, {column: column}, e);
        }

        function handleHeaderClick(e) {
            var $header = $(e.target).closest(".slick-header-column", ".slick-header-columns");
            var column = $header && columns[self.getColumnIndex($header.data("fieldId"))];
            trigger(self.onHeaderClick, {column: column}, e);
        }

        function handleMouseEnter(e) {
            trigger(self.onMouseEnter, {}, e);
        }

        function handleMouseLeave(e) {
            trigger(self.onMouseLeave, {}, e);
        }

        function cellExists(row,cell) {
            return !(row < 0 || row >= getDataLength() || cell < 0 || cell >= columns.length);
        }

        function getCellFromPoint(x,y) {
            var row = Math.floor((y+offset)/options.rowHeight);
            var cell = 0;

            var w = 0;
            for (var i=0; i<columns.length && w<x; i++) {
                w += columns[i].width;
                cell++;
            }

            if (cell < 0) {
                cell = 0;
            }

            return {row:row,cell:cell-1};
        }

        function getCellFromNode(node) {
            // read column number from .l1 or .c1 CSS classes
            var cls = /l\d+/.exec(node.className) || /c\d+/.exec(node.className);
            if (!cls)
                throw "getCellFromNode: cannot get cell - " + node.className;
            return parseInt(cls[0].substr(1, cls[0].length-1), 10);
        }

        function getCellFromEvent(e) {
        	var target = e.target || e.srcElement;
            var $cell = $(target).closest(".slick-cell", $canvas);
            if (!$cell.length)
                return null;

            return {
                row: $cell.parent().attr("row") | 0,
                cell: getCellFromNode($cell[0])
            };
        }

        function getCellNodeBox(row,cell) {
             if (!cellExists(row,cell))
                 return null;

             var y1 = row * options.rowHeight - offset;
             var y2 = y1 + options.rowHeight - 1;
             var x1 = 0;
             for (var i=0; i<cell; i++) {
                 x1 += columns[i].width;
             }
             var x2 = x1 + columns[cell].width;

             return {
                 top: y1,
                 left: x1,
                 bottom: y2,
                 right: x2
             };
         }

        // ////////////////////////////////////////////////////////////////////////////////////////////
        // Cell switching

        function resetActiveCell() {
            setActiveCellInternal(null,false);
        }

        function setFocus() {
            // IE tries to scroll the viewport so that the item being focused is
			// aligned to the left border
            // IE-specific .setActive() sets the focus, but doesn't scroll
            if ($.browser.msie) {
                $canvas[0].setActive();
            }
            else {
                $canvas[0].focus();
            }
        }

        function scrollActiveCellIntoView() {
            if (activeCellNode) {
                var left = $(activeCellNode).position().left,
                    right = left + $(activeCellNode).outerWidth(true),
                    scrollLeft = $viewport.scrollLeft(),
                    scrollRight = scrollLeft + $viewport.width();

                if (left < scrollLeft)
                    $viewport.scrollLeft(left);
                else if (right > scrollRight)
                    $viewport.scrollLeft(Math.min(left, right - $viewport[0].clientWidth));
            }
        }

        function setActiveCellInternal(newCell, editMode) {
            if (activeCellNode !== null) {
                makeActiveCellNormal();
                $(activeCellNode).removeClass("active");
            }

            var activeCellChanged = (activeCellNode !== newCell);
            activeCellNode = newCell;

            if (activeCellNode != null) {
                activeRow = parseInt($(activeCellNode).parent().attr("row"));
                activeCell = activePosX = getCellFromNode(activeCellNode);

                $(activeCellNode).addClass("active");

                if (options.editable && editMode && isCellPotentiallyEditable(activeRow,activeCell)) {
                    clearTimeout(h_editorLoader);

                    if (options.asyncEditorLoading) {
                        h_editorLoader = setTimeout(function() { makeActiveCellEditable(); }, options.asyncEditorLoadDelay);
                    }
                    else {
                        makeActiveCellEditable();
                    }
                }
                else {
                      setFocus();
                }
            }
            else {
                activeRow = activeCell = null;
            }

            if (activeCellChanged) {
                scrollActiveCellIntoView();
                trigger(self.onActiveCellChanged, getActiveCell());
            }
        }

        function clearTextSelection() {
            if (document.selection && document.selection.empty) {
                document.selection.empty();
            }
            else if (window.getSelection) {
                var sel = window.getSelection();
                if (sel && sel.removeAllRanges) {
                    sel.removeAllRanges();
                }
            }
        }

        function isCellPotentiallyEditable(row, cell) {
            // is the data for this row loaded?
            if (row < getDataLength() && !getDataItem(row)) {
                return false;
            }

            // are we in the Add New row? can we create new from this cell?
            if (columns[cell].cannotTriggerInsert && row >= getDataLength()) {
                return false;
            }

            // does this cell have an editor?
            if (!getEditor(row, cell)) {
                return false;
            }

            return true;
        }

        function makeActiveCellNormal() {
            if (!currentEditor) { return; }
            trigger(self.onBeforeCellEditorDestroy, {editor:currentEditor});
            currentEditor.destroy();
            currentEditor = null;

            if (activeCellNode) {
                $(activeCellNode).removeClass("editable invalid");

                if (getDataItem(activeRow)) {
                    var column = columns[activeCell];
                    activeCellNode.innerHTML = getFormatter(activeRow, column)(activeRow, activeCell, getDataItem(activeRow)[column.field], column, getDataItem(activeRow));
                    invalidatePostProcessingResults(activeRow);
                }
            }

            // if there previously was text selected on a page (such as selected
			// text in the edit cell just removed),
            // IE can't set focus to anything else correctly
            if ($.browser.msie) { clearTextSelection(); }

            getEditorLock().deactivate(editController);
        }

        function makeActiveCellEditable(editor) {
            if (!activeCellNode) { return; }
            if (!options.editable) {
                throw "Grid : makeActiveCellEditable : should never get called when options.editable is false";
            }

            // cancel pending async call if there is one
            clearTimeout(h_editorLoader);
			// 林森修改20120905 只支持编辑框回车焦点
            var isEditor = isCellPotentiallyEditable(activeRow,activeCell);
            if (!isEditor) {
                return isEditor;
            }
			// if (!isCellPotentiallyEditable(activeRow,activeCell)) {
			// return;
			// }*
            var columnDef = columns[activeCell];
            var item = getDataItem(activeRow);

            if (trigger(self.onBeforeEditCell, {row:activeRow, cell:activeCell, item:item, column:columnDef}) === false) {
                setFocus();
                return;
            }

            getEditorLock().activate(editController);
            $(activeCellNode).addClass("editable");

            // don't clear the cell if a custom editor is passed through
            if (!editor) {
            	activeCellNode.innerHTML = "";
            }

            currentEditor = new (editor || getEditor(activeRow, activeCell))({
                grid: self,
                gridPosition: absBox($container[0]),
                position: absBox(activeCellNode),
                container: activeCellNode,
                column: columnDef,
                item: item || {},
                commitChanges: commitEditAndSetFocus,
                cancelChanges: cancelEditAndSetFocus
            }, columns[activeCell].editordata, columns[activeCell]);

            if (item)
                currentEditor.loadValue(item);

            serializedEditorValue = currentEditor.serializeValue();

            if (currentEditor.position)
                handleActiveCellPositionChange();
        }

        function commitEditAndSetFocus() {
            // if the commit fails, it would do so due to a validation error
            // if so, do not steal the focus from the editor
            if (getEditorLock().commitCurrentEdit()) {
                  setFocus();

// if (options.autoEdit) {
// navigateDown();
// }
            }
        }

        function cancelEditAndSetFocus() {
            if (getEditorLock().cancelCurrentEdit()) {
                  setFocus();
            }
        }

        function absBox(elem) {
            var box = {top:elem.offsetTop, left:elem.offsetLeft, bottom:0, right:0, width:$(elem).outerWidth(true), height:$(elem).outerHeight(true), visible:true};
            box.bottom = box.top + box.height;
            box.right = box.left + box.width;

            // walk up the tree
            var offsetParent = elem.offsetParent;
            while ((elem = elem.parentNode) != document.body) {
                if (box.visible && elem.scrollHeight != elem.offsetHeight && $(elem).css("overflowY") != "visible")
                    box.visible = box.bottom > elem.scrollTop && box.top < elem.scrollTop + elem.clientHeight;

                if (box.visible && elem.scrollWidth != elem.offsetWidth && $(elem).css("overflowX") != "visible")
                    box.visible = box.right > elem.scrollLeft && box.left < elem.scrollLeft + elem.clientWidth;

                box.left -= elem.scrollLeft;
                box.top -= elem.scrollTop;

                if (elem === offsetParent) {
                    box.left += elem.offsetLeft;
                    box.top += elem.offsetTop;
                    offsetParent = elem.offsetParent;
                }

                box.bottom = box.top + box.height;
                box.right = box.left + box.width;
            }

            return box;
        }

        function getActiveCellPosition(){
            return absBox(activeCellNode);
        }

        function getGridPosition(){
            return absBox($container[0]);
        }

        function handleActiveCellPositionChange() {
            if (!activeCellNode) return;
            var cellBox;

            trigger(self.onActiveCellPositionChanged, {});

            if (currentEditor) {
                cellBox = cellBox || getActiveCellPosition();
                if (currentEditor.show && currentEditor.hide) {
                    if (!cellBox.visible)
                        currentEditor.hide();
                    else
                        currentEditor.show();
                }

                if (currentEditor.position)
                    currentEditor.position(cellBox);
            }
        }

        function getCellEditor() {
            return currentEditor;
        }

        function getActiveCell() {
            if (!activeCellNode)
                return null;
            else
                return {row: activeRow, cell: activeCell};
        }

        function getActiveCellNode() {
            return activeCellNode;
        }

        function scrollRowIntoView(row, doPaging) {
        	// row --; //因为编号从1开始，故加1
        	var rowInt = parseInt(row);
            var rowAtTop = rowInt * options.rowHeight;
            var rowAtBottom = (rowInt + 1) * options.rowHeight - viewportH + (viewportHasHScroll?scrollbarDimensions.height:0);

            // need to page down?
            if ((rowInt + 1) * options.rowHeight > scrollTop + viewportH + offset) {
                scrollTo(doPaging ? rowAtTop : rowAtBottom);
                render();
            }

            // or page up?
            else if (rowInt * options.rowHeight < scrollTop + offset) {
                scrollTo(doPaging ? rowAtBottom : rowAtTop);
                render();
            }
        }

        function getColspan(row, cell) {
            var metadata = data.getItemMetadata && data.getItemMetadata(row);
            if (!metadata || !metadata.columns) {
                return 1;
            }

            var columnData = metadata.columns[columns[cell].id] || metadata.columns[cell];
            var colspan = (columnData && columnData.colspan);
            if (colspan === "*") {
                colspan = columns.length - cell;
            }
            return (colspan || 1);
        }

        function findFirstFocusableCell(row) {
            var cell = 0;
            while (cell < columns.length) {
                if (canCellBeActive(row, cell)) {
                    return cell;
                }
                cell += getColspan(row, cell);
            }
            return null;
        }

        function findLastFocusableCell(row) {
            var cell = 0;
            var lastFocusableCell = null;
            while (cell < columns.length) {
                if (canCellBeActive(row, cell)) {
                    lastFocusableCell = cell;
                }
                cell += getColspan(row, cell);
            }
            return lastFocusableCell;
        }

        function gotoRight(row, cell, posX) {
            if (cell >= columns.length -1) {
            	row ++;
            	cell = -1;
                // return null;
            }
			// alert(row+ " ：" +cell)
            do {
                cell += getColspan(row, cell);
            }
            while (cell < columns.length && !canCellBeActive(row, cell));

            if (cell < columns.length) {
                return {
                    "row": row,
                    "cell": cell,
                    "posX": cell
                };
            }
            return null;
        }

        function gotoLeft(row, cell, posX) {
            if (cell <= 0) {
                return null;
            }

            var firstFocusableCell = findFirstFocusableCell(row);
            if (firstFocusableCell === null || firstFocusableCell >= cell) {
                return null;
            }

            var prev = {
                "row": row,
                "cell": firstFocusableCell,
                "posX": firstFocusableCell
            };
            var pos;
            while (true) {
                pos = gotoRight(prev.row, prev.cell, prev.posX);
                if (!pos) {
                    return null;
                }
                if (pos.cell >= cell) {
                    return prev;
                }
                prev = pos;
            }
        }

        function gotoDown(row, cell, posX) {
            var prevCell;
            while (true) {
                if (++row >= getDataLength() + (options.enableAddRow ? 1 : 0)) {
                    return null;
                }

                prevCell = cell = 0;
                while (cell <= posX) {
                    prevCell = cell;
                    cell += getColspan(row, cell);
                }

                if (canCellBeActive(row, prevCell)) {
                    return {
                        "row": row,
                        "cell": prevCell,
                        "posX": posX
                    };
                }
            }
        }

        function gotoUp(row, cell, posX) {
            var prevCell;
            while (true) {
                if (--row < 0) {
                    return null;
                }

                prevCell = cell = 0;
                while (cell <= posX) {
                    prevCell = cell;
                    cell += getColspan(row, cell);
                }

                if (canCellBeActive(row, prevCell)) {
                    return {
                        "row": row,
                        "cell": prevCell,
                        "posX": posX
                    };
                }
            }
        }

        function gotoNext(row, cell, posX) {
            var pos = gotoRight(row, cell, posX);
            if (pos) {
                return pos;
            }

            var firstFocusableCell = null;
            while (++row < getDataLength() + (options.enableAddRow ? 1 : 0)) {
                firstFocusableCell = findFirstFocusableCell(row);
                if (firstFocusableCell !== null) {
                    return {
                        "row": row,
                        "cell": firstFocusableCell,
                        "posX": firstFocusableCell
                    };
                }
            }
            return null;
        }

        function gotoPrev(row, cell, posX) {
            var pos;
            var lastSelectableCell;
            while (!pos) {
                pos = gotoLeft(row, cell, posX);
                if (pos) {
                    break;
                }
                if (--row < 0) {
                    return null;
                }

                cell = 0;
                lastSelectableCell = findLastFocusableCell(row);
                if (lastSelectableCell !== null) {
                    pos = {
                        "row": row,
                        "cell": lastSelectableCell,
                        "posX": lastSelectableCell
                    };
                }
            }
            return pos;
        }

        function navigateRight() {
            navigate("right");
        }

        function navigateLeft() {
            navigate("left");
        }

        function navigateDown(commitEditor) {
            navigate("down",commitEditor);
        }

        function navigateUp(commitEditor) {
            navigate("up",commitEditor);
        }

        function navigateNext() {
            navigate("next");
        }

        function navigatePrev() {
            navigate("prev");
        }

        function navigate(dir,commitEditor) {
            if (!activeCellNode || !options.enableCellNavigation) { return; }
            if (commitEditor != true)
            	if (!getEditorLock().commitCurrentEdit()) { return; }

            var stepFunctions = {
                "up":       gotoUp,
                "down":     gotoDown,
                "left":     gotoLeft,
                "right":    gotoRight,
                "prev":     gotoPrev,
                "next":     gotoNext
            };
            var stepFn = stepFunctions[dir];
            var pos = stepFn(activeRow, activeCell, activePosX);
            if (pos) {
                var isAddNewRow = (pos.row == getDataLength());
                scrollRowIntoView(pos.row, !isAddNewRow);
                setActiveCellInternal(getCellNode(pos.row, pos.cell), isAddNewRow || options.autoEdit);
                activePosX = pos.posX;
            }
        }

        function getCellNode(row, cell) {
            if (rowsCache[row]) {
                var cells = $(rowsCache[row]).children();
                var nodeCell;
                for (var i = 0; i < cells.length; i++) {
                    nodeCell = getCellFromNode(cells[i]);
                    if (nodeCell === cell) {
                        return cells[i];
                    }
                    else if (nodeCell > cell) {
                        return null;
                    }

                }
            }
            return null;
        }

        function setActiveCell(row, cell) {
            if (row > getDataLength() || row < 0 || cell >= columns.length || cell < 0) {
                return;
            }

            if (!options.enableCellNavigation) {
                return;
            }

            scrollRowIntoView(row,false);
            setActiveCellInternal(getCellNode(row,cell),false);
        }

        function canCellBeActive(row, cell) {
            if (!options.enableCellNavigation || row >= getDataLength() + (options.enableAddRow ? 1 : 0) || row < 0 || cell >= columns.length || cell < 0) {
                return false;
            }

            var rowMetadata = data.getItemMetadata && data.getItemMetadata(row);
            if (rowMetadata && typeof rowMetadata.focusable === "boolean") {
                return rowMetadata.focusable;
            }

            var columnMetadata = rowMetadata && rowMetadata.columns;
            if (columnMetadata && columnMetadata[columns[cell].id] && typeof columnMetadata[columns[cell].id].focusable === "boolean") {
                return columnMetadata[columns[cell].id].focusable;
            }
            if (columnMetadata && columnMetadata[cell] && typeof columnMetadata[cell].focusable === "boolean") {
                return columnMetadata[cell].focusable;
            }

            if (typeof columns[cell].focusable === "boolean") {
                return columns[cell].focusable;
            }

            return true;
        }
		/**
		 * 判断单元格是否能被选中
		 */
        function canCellBeSelected(row, cell) {
            if (row >= getDataLength() || row < 0 || cell >= columns.length || cell < 0) {
                return false;
            }

            var rowMetadata = data.getItemMetadata && data.getItemMetadata(row);
            if (rowMetadata && typeof rowMetadata.selectable === "boolean") {
                return rowMetadata.selectable;
            }

            var columnMetadata = rowMetadata && rowMetadata.columns && (rowMetadata.columns[columns[cell].id] || rowMetadata.columns[cell]);
            if (columnMetadata && typeof columnMetadata.selectable === "boolean") {
                return columnMetadata.selectable;
            }

            if (typeof columns[cell].selectable === "boolean") {
                return columns[cell].selectable;
            }

            return true;
        }

        function gotoCell(row, cell, forceEdit) {
            if (!canCellBeActive(row, cell)) {
                return;
            }

            if (!getEditorLock().commitCurrentEdit()) { return; }

            scrollRowIntoView(row,false);

            var newCell = getCellNode(row, cell);

            // if selecting the 'add new' row, start editing right away
            setActiveCellInternal(newCell, forceEdit || (row === getDataLength()) || options.autoEdit);

            // if no editor was created, set the focus back on the grid
            if (!currentEditor) {
                setFocus();
            }
        }

        
        // ////////////////////////////////////////////////////////////////////////////////////////////
        // IEditor implementation for the editor lock
		
        function commitCurrentEdit() {
            var item = getDataItem(activeRow);
            var column = columns[activeCell];
			var cellNode = getActiveCellNode();
            if (currentEditor) {
            	var defaultValue = currentEditor.defaultValue;
                if (currentEditor.isValueChanged()) {
                    var validationResults = currentEditor.validate();
                    if (validationResults.valid) {
                        if (activeRow < getDataLength()) {
                            var editCommand = {
                                row: activeRow,
                                cell: activeCell,
                                editor: currentEditor,
                                serializedValue: currentEditor.serializeValue(),
                                prevSerializedValue: serializedEditorValue,
                                execute: function() {
                                    this.editor.applyValue(item,this.serializedValue);
                                    updateRow(this.row);
                                },
                                undo: function() {
                                    this.editor.applyValue(item,this.prevSerializedValue);
                                    updateRow(this.row);
                                }
                            };
                            if (options.editCommandHandler) {
                                makeActiveCellNormal();
                                options.editCommandHandler(item,column,editCommand);

                            }
                            else {
                                editCommand.execute();
                                makeActiveCellNormal();
                            }
                            var isDurty = false;
                            for (var ie = 0 ;ie < editorItems.length; ie ++) {
                            	if (editorItems[ie][pId] == item[pId]) {
									editorItems.splice(ie,1,item);
									isDurty = true;
									break;
									
                            	}
                            }
                            if (!isDurty  && editorItems.length != 0)editorItems.push(item);
                            if (editorItems.length ==0) editorItems.push(item);
                            if (editCommand.serializedValue != serializedEditorValue) {
                            	$(cellNode).addClass("slick-icon-edit-column");
                            }
                            var  column = getColumns()[activeCell];
                            if (column != undefined && typeof column.onChange == "function") {
                            	column.onChange({row: activeRow,cell: activeCell,item: item},item[column.id],defaultValue);
                            }
                            trigger(self.onCellChange, {
                                row: activeRow,
                                cell: activeCell,
                                item: item
                            });
                        }
                        else {
                            var newItem = {};
                            currentEditor.applyValue(newItem,currentEditor.serializeValue());
                            makeActiveCellNormal();
                            trigger(self.onAddNewRow, {item:newItem, column:column});
                        }
                        // refreshGrid();
						// getDataView().refresh(true);//用于刷新合计20121203 lins
						// TODO 图标列问题
						// render();
                        // check whether the lock has been re-acquired by event
						// handlers
                        return !getEditorLock().isActive();
                    }
                    else {
                        // TODO: remove and put in onValidationError handlers in
						// examples
                        $(activeCellNode).addClass("invalid");
                       // $(activeCellNode).stop(true,true).effect("highlight",
						// {color:"red"}, 300);

                        trigger(self.onValidationError, {
                            editor: currentEditor,
                            cellNode: activeCellNode,
                            validationResults: validationResults,
                            row: activeRow,
                            cell: activeCell,
                            column: column
                        });

                        currentEditor.focus();
                        return validateFaild();
                    }
                }

                makeActiveCellNormal();
            }
        // getDataView().refresh();
            return true;
        }
		function validateFaild() {
			var item = getDataItem(activeRow);
            var column = columns[activeCell];
			var cellNode = getActiveCellNode();
			$(activeCellNode).addClass("invalid");
            $(activeCellNode).stop(true,true);// .effect("highlight",
												// {color:"red"}, 300);
			var validationResults = currentEditor.validate();
            trigger(self.onValidationError, {
                editor: currentEditor,
                validationResults: validationResults,
                row: activeRow,
                cell: activeCell,
                column: column
            });

            currentEditor.focus();
            return false;
		}
        function cancelCurrentEdit() {
            makeActiveCellNormal();
            return true;
        }

        function rowsToRanges(rows) {
            var ranges = [];
            var lastCell = columns.length - 1;
            for (var i = 0; i < rows.length; i++) {
                ranges.push(new Slick.Range(rows[i], 0, rows[i], lastCell));
            }
            return ranges;
        }
        
        function getSelectedRows() {
            if (!selectionModel) {
                throw "Selection model is not set";
            }
            return selectedRows;
        }
        
		function getSelectedRowsById() {
			if (!selectionModel)
				throw "";
			return selectedRowIds;
		}
        function setSelectedRows(rows) {
        	// var dataItems = data.getItems();
            if (!selectionModel) {
                throw "Selection model is not set";
            }
            selectionModel.setSelectedRanges(rowsToRanges(rows));
        }

		// TODO 林森自定以方法
        /**
		 * 过滤数据
		 */
        function filter(item) {
            for (var columnId in columnFilters) { // 判断
                if (columnId !== undefined && columnFilters[columnId] !== "") {
                    var c = getColumns()[getColumnIndex(columnId)];
                   // var zz = new RegExp("^" + columnFilters[columnId]);
                    if (item[c.field]|| item[c.field] === 0) {
                    	if (options.collectionsDataArrayObject) {// 从码表里面判断
                    		var collection = options.collectionsDataArrayObject[c.field]; //
			        		if (collection) {
			        			for (var i = 0; i < collection.length; i ++) {
									if (collection[i].id == item[c.field].toString()) {
										var field = collection[i].name
										if (field.indexOf(columnFilters[columnId]) == -1) { // zz.test(field);
				                        	return false;
				                    	}
									}
								}
			        		} else {
		                    	var field = item[c.field].toString();
		                    	if (field.indexOf(columnFilters[columnId]) == -1) {// zz.test(field);
		                        	return false;
		                    	}
				        	}
			        	}
                    } else return false;
                }
            }
            return true;
        }
        /**
		 * 获得码表数组
		 */
        function getCollectionsDataArrayObject() {
        	return options.collectionsDataArrayObject;
        }
        function getColumnById(columnId) {
        	for (var i = 0; i < columns.length; i ++) {
        		if (columns[i].field == columnId) {
        			return columns[i];
        		}
        	}
        	return null;
        }
        function getEditorItems() {
        	bl:for (var i =0; i < editorItems.length; i ++) {
        		for (var j = 0; j < addrows.length; j ++) {
        			if (editorItems[i][pId] == addrows[j][pId]) {
        				addrows.splice(j,1,editorItems[i]);
        				editorItems.splice(i,1);
        				if (editorItems.length == 0) break bl;
        			}
        		}
        	}
        	return editorItems;
        }
        /**
		 * 添加用于过滤的列框
		 */
        function updateHeaderRow() {
        	//var columns = columns;
            for (var i = 0; i < columns.length; i++) {
            	var column = columns[i];
                if (column.id !== "_checkbox_selector" && column.id !=="_radio_selector" && column.id !=="__no" && !column.icon && column.operate != true) {
                    var header = getHeaderRowColumn(column.id);
                    $(header).empty();
                    $("<input type='text' title='过滤框' class='slick-grid-filter-input'>")
                        .data("columnId", columns[i].id)
                        .width($(header).width() - 14)
                        .val(columnFilters[columns[i].id])
                        .css("margin-top" ,"3px")
                        .css("border","1px solid #7F9DB9")
                        .appendTo(header);
                }
            }
        }
        /**
		 * 动态更新数据 针对数组
		 * 
		 * @param data
		 *            修改内容
		 * @param row
		 *            修改的行号
		 * @param name
		 *            修改的列名
		 */
        function updataData(data, row, name) {
        	var currentData = this.getData();
        	var currentName;
        	if (row < 0 || row > this.getDataLength()) {
        		return;
        	} else {
        		var d = currentData[row];
        		for (currentName in d) {
        			if (currentName == name.toLowerCase())
        				d[currentName] = data;
        		}
        		updateRow(row);
        	}
        }
        /**
		 * 取得其中选择的行的数据，以JSON格式返回
		 * 
		 * @param {ARRAY}
		 *            columns 指定选择的列
		 * @return {String} str The JSON string
		 * @author 林森
		 */
        function getSelectRowsDataToJSON(columns) {
        	var c = [], str = "";
        	c = getSelectedRows();
        	if (c.length === 0) {
        		c = data;
        	}// 待修改
        	str = columnToJSON(c,columns);
        	return str;
        }
        /**
		 * 返回
		 */
        function getSelectRowsDataToObj(propertyKey) {
        	var select = [], objs = [];
        	select = getSelectedRows();
        	for (var i = 0; i< select.length; i ++) {
        		// delete select[i].__id___;
	        		var objNew = getDataItem(select[i]);
	        		//先判断取得的数据是否存在;
	        		if(objNew){
	        			if (columnPropertyKey.length != 0 ) {
	    	        		var rowObj = {};
	    	        		for (var j in objNew) {
	    	        			for (var jj = 0; jj < columnPropertyKey.length; jj ++) {
	    		        			if (j == columnPropertyKey[jj]) {
	    		        				rowObj[j] = objNew[j];
	    		        			}
	    	        			}
	    	        		}
	            			rowObj._row_ = select[i];
	            			objs.push(rowObj);
	            		} else {
	            			objNew._row_ = select[i]; // lins 添加行号，20121203
	            			objs.push(objNew);
	            		}
	        		}
        		
        		// delete objNew.__id___;
        	}
        	return objs;
        }
        // function columnToObj(select)
        /**
		 * 将行数据转换为JSON
		 * 
		 * @param {array}
		 *            select 选择的行
		 * @param {array]
		 *            columns 保留列
		 * @return {string} str JSON
		 */
        function columnToJSON(select, columns) {
        	var str = "[";
        	for (var i = 0; i < select.length; i ++) {
        		str += "{ ";
        		var obj = getDataItem(select[i]);
        		for (j in obj) {
        			if (columns && columns.length > 0) {
        				for (var k = 0; k < columns.length; k ++) {
        					if (columns[k] == j) {
        						str += j + ":\"" + obj[j] + "\",";
        					}
        				}
        			} else str += j + ":\"" + obj[j] + "\",";
        		}
        		str = str.slice(0,-1);
        		str += "}";
        		if (i != select.length - 1) {
        			str += ",";
        		}
        	}
        	str +="]";
        	return str;
        }
        function itemToJSON(item, columns) {
        	var str = "[";
        	for (var i = 0; i < item.length; i ++) {
        		str += "{ ";
        		var obj = item[i];
        		for (j in obj) {
        			if (columns && columns.length > 0) {
        				for (var k = 0; k < columns.length; k ++) {
        					if (columns[k] == j) {
        						str += j + ":\"" + obj[j] + "\",";
        					}
        				}
        			} else str += j + ":\"" + obj[j] + "\",";
        		}
        		str = str.slice(0,-1);
        		str += "}";
        		if (i != item.length - 1) {
        			str += ",";
        		}
        	}
        	str +="]";
        	return str;
        }
        /**
		 * 得到dataView
		 * 
		 * @return {object} dataView 数据视图对象
		 * @author 林森
		 */
        function getDataView() {
        	return dataView;
        }
        /**
		 * 得到data
		 * 
		 * @return {array}
		 */
        function getDataByDataView() {
        	if (dataView) {
        		var reData = dataView.getItems();
        		return reData;
        	} else {
        		return {};
        	}
        }
        function getDataByDataViewWithoutId() {
        	if (dataView) {
        		var reData = dataView.getItems();
        		for (var i = 0; i < reData.length; i ++) {
        			delete reData[i][pId];
        		}
        		return reData;
        	} else {
        		return {};
        	}
        }
    	/**
		 * 删除选中行
		 * 
		 * @return {str}删除行数据00:1C:50:D7:90:5A
		 * @param {array}
		 *            columns 列
		 * @author 林森
		 */
        function deleteDataRows() {
        	var c = [], str,d = [];
        	c = getSelectedRows();
        	d = getSelectedRowsById();
        	var dataView = getDataView();
        	for (var i = 0, l = c.length; i < l; i ++) {
        		var data = dataView.getItemById(d[i]);
        		delrows.push(data);
        		
        		// TODO 判断editor和add有没有
        		dataView.deleteItem(data[pId]);
        		removeRowFromCache(c[i]);
        		if (options.selectType == "checkbox"){
        			if (selectorTpye)
            			selectorTpye.delete_selectedRowsLookup(c[i]);
        		}
        	}
        	setSelectedRows(0);
        	getDataView().refresh();
        	refreshGrid();
        	return str;
        }
        /**
		 * 通过row删除一行数据
		 */
        function deleteRow(row) {
        	if (row == "undefined") return false;
			var data = dataView.getItem(row); 
			if (data._addflag == null){
				delrows.push(data);
				for (var i = 0; i < editorItems.length; i ++) {
					if (editorItems[i][pId] == data.pId) 
						editorItems.splice(i,1);
				}
			} else {
				for (var i = 0; i < addrows.length; i ++) {
					if (addrows[i]["_addflag"] == data._addflag) 
						addrows.splice(i,1);
				}
			}
			
			dataView.deleteItem(data[pId]);
    		removeRowFromCache(row);
    		if (options.selectType == "checkbox"){
    			if (selectorTpye)
        			selectorTpye.delete_selectedRowsLookup(row);
    		}
    		setSelectedRows(0);
        	getDataView().refresh();
        	refreshGrid();
        }
        /**
		 * 刷新表格数据重新渲染
		 * 
		 * @param
		 * @author 林森
		 */
        function refreshGrid(param) {
        	getDataView().refresh();
        	updateRowCount();
        	invalidateAllRows();
        	render();
        }
        /**
		 * 新增一行改进版
		 * 
		 * @param columnValue
		 *            默认数据，如下所示：{"aac001":"1100012","aac002":"01"}
		 */
        function addNewRow(columnValue) {
        	var data = getDataByDataView(); // 得到data 带 id
        	var object = {}
        	if (data.length === 0) {
        		object[pId] = 0;
        	} else {
        		var last = data[data.length - 1]; // 得到最后一个id
        		var addLast = addrows[addrows.length -1];
        		if (addrows.length >0 && addLast[pId] > last[pId])
        			object[pId] = addLast[pId] + 1; // 下一个数据id+
        		else 
        			object[pId] = last[pId] + 1; 
        	}
        	for (var j in columnValue) {
        		object[j] = columnValue[j];
        		
// if (columnValue && columnValue[j]) {
// object[j] = columnValue[j];
// } else if (j !== pId){
// object[j] = ""
// }
        	}
        	data.splice(0, 0, object);
        	// data.push(object);
        	object._addflag = new Date().getTime();
        	addrows.push(object);
        	idxById = {};   // 设置idxById 为空
            getDataView().updateIdxById();
            getDataView().ensureIdUniqueness(); 
        	getDataView().refresh();
        	refreshGrid();
        	scrollRowIntoView(data.length);
        }
                
        function addNewRowTo(columnValue,rownum) {
        	var data = getDataByDataView(); //得到data 带 id
        	var object = {}
        	if (data.length === 0) {
        		object[pId] = 0;
        	} else {
        		var last = data[data.length - 1]; //得到最后一个id
        		var addLast = addrows[addrows.length -1];
        		if (addrows.length >0 && addLast[pId] > last[pId])
        			object[pId] = addLast[pId] + 1; //下一个数据id+
        		else 
        			object[pId] = last[pId] + 1; 
        	}
        	for (var j in columnValue) {
        		object[j] = columnValue[j];
        	}
        	data.splice(rownum, 0, object);
        	//data.push(object);
        	addrows.push(object);
        	idxById = {};   //设置idxById 为空
            getDataView().updateIdxById();
            getDataView().ensureIdUniqueness(); 
        	getDataView().refresh();
        	refreshGrid();
        	scrollRowIntoView(data.length);
        }
        function addNewRowDown(columnValue) {
        	var data = getDataByDataView(); // 得到data 带 id
        	var object = {}
        	if (data.length === 0) {
        		object[pId] = 0;
        	} else {
        		var last = data[data.length - 1]; // 得到最后一个id
        		var addLast = addrows[addrows.length -1];
        		if (addrows.length >0 && addLast[pId] > last[pId])
        			object[pId] = addLast[pId] + 1; // 下一个数据id+
        		else 
        			object[pId] = last[pId] + 1; 
        	}
        	for (var j in columnValue) {
        		object[j] = columnValue[j];
        	}
        	data.push(object);
        	// data.push(object);
        	object._addflag = new Date().getTime();
        	addrows.push(object);
        	idxById = {};   // 设置idxById 为空
            getDataView().updateIdxById();
            getDataView().ensureIdUniqueness(); 
        	getDataView().refresh();
        	refreshGrid();
        	scrollRowIntoView(data.length);
        }
        function getPager() {
        	return pager;
        }
        function getAddRow() {
        	bl :for (var j = 0; j < addrows.length; j ++) {
        		for (var i =0; i < editorItems.length; i ++) {
        			if (editorItems[i][pId] == addrows[j][pId]) {
        				addrows.splice(j,1,editorItems[i]);
        				editorItems.splice(i,1);
        				if (addrows.length == 0) break bl;
        			}
        		}
        	}
        	return addrows;
        }
        function getRemovedRows() {
        	return delrows;
        }
        function getGridId() {
        	if (typeof container == 'string')
        		return container.replace("#","");
        	else 
        		return container;
        }
        
        function clearDirty() {
        	setSelectedRows([]);
        	editorItems = [];
        	addrows = [];
        	delrows = [];
        	columnFilters = {};
        	if (pager && pager != undefined)
        		pager.clearDirty();
        	// columnFilters = {};
        	$headerRow.find("input").each(function(i){$(this).val("")});
        	// if (getPager() != null ) getPager().constructPagerUI();
        	trigger(self.onSelectedRowsChanged, {rows:getSelectedRows(), data:getSelectRowsDataToObj()},null);
// if (selectorTpye) {
// selectorTpye.setNull();
// }
        }
       function clearDirtyWidthOutPager() {
       		setSelectedRows([]);
        	editorItems = [];
        	addrows = [];
        	delrows = [];
        	columnFilters = {};
        	// pager.clearDirty();
        	// columnFilters = {};
        	$headerRow.find("input").each(function(i){$(this).val("")});
        	// if (getPager() != null ) getPager().constructPagerUI();
        	trigger(self.onSelectedRowsChanged, {rows:getSelectedRows(), data:getSelectRowsDataToObj()},null);
// if (selectorTpye) {
// selectorTpye.setNull();
// }
// setSelectedRows([]);
// editorItems = [];
// addrows = [];
// delrows = [];
// //columnFilters = {};
// //$headerRow.find("input").each(function(i){$(this).val("")});
// trigger(self.onSelectedRowsChanged, {rows:getSelectedRows(),
// data:getSelectRowsDataToObj()},null);
// // if (selectorTpye) {
// // selectorTpye.setNull();
// // }
        }
        function getContainer() {
        	return $container;
        }
        function getDefaultBox() {
        	return {width:defaultWidth, height:defaultHeight};
        }
        /**
		 * 设置隐藏
		 */
        function setColumnHidden(id) {
        	if (id == null || id.length == 0) return;
        	var columnNew = []
        	if (id != null && id instanceof Array) {
       			loop : for (var j = 0; j < columns.length; j ++) {
       				for (var i = 0; i < id.length; i ++) {
                		if (columns[j].field == id[i]) {
                			hiddenColumns.push(columns[j]);
                			continue loop;
                		}
                	}
       				columnNew.push(columns[j]);
        		}
        	} else {
        		for (var i = 0; i < columns.length; i ++) {
            		if (columns[i].field == id) {
            			hiddenColumns.push(columns[i]);
            			continue;
            		}
            		columnNew.push(columns[i]);
            	}
        	}
        	setColumns(columnNew);
        }
        /**
		 * 设置显示
		 */
        function setColumnShow(id) {
        	var columnNew = getColumns();
        	for (var i = 0; i < hiddenColumns.length; i ++) {
        		if (hiddenColumns[i].field == id) {
        			columnNew.push(hiddenColumns[i]);
        			// columnNew.splice(hiddenColumns[i].columnPossation, 0,
					// hiddenColumns[i]);
        			hiddenColumns.splice(i,1);
        			break;
        		}
        	}
        	setColumns(columnNew);
        }
        
        
        function showGridItemOperate(e, options){
        	  e.preventDefault();
			  var columns =  self.getColumns();
			  var cellInfo = self.getCellFromEvent(e);
		      $("#operation" + cellInfo.row).show();
        }
        
        function hideGridItemOperate(e, options){
      	  	  e.preventDefault();
			  var columns =  self.getColumns();
			  var cellInfo = self.getCellFromEvent(e);
        }
        
        // ////////////////////////////////////////////////////////////////////////////////////////////
        // Public API

        $.extend(this, {
        	"cmptype":'datagrid',
            "slickGridVersion": "2.0a1",
			
            // Events
            /**
			 * 滚动sroll触发事件
			 * @event onScroll
			 * @param {object} obj
			 *            {scrollLeft:scrollLeft, scrollTop:scrollTop} 入参对象
			 */
            "onScroll":                     new Slick.Event(),
            /**
			 * 排序时触发事件，传人如下对象
			 * @event onSort
			 * @param {object} obj
			 *            {sortCol:column,sortAsc:sortAsc} 入参对象
			 */
            "onSort":                       new Slick.Event(),
            /**
			 * 点击列head右键时触发事件
			 * @event onHeaderContextMenu
			 * @param {object} obj
			 *            {column: column} 入参对象
			 */
            "onHeaderContextMenu":          new Slick.Event(),
            
            /**
			 * 单击列头时触发
			 * @event onHeaderClick
			 * @param {object} obj
			 *            {column: column} 入参对象
			 */
            "onHeaderClick":                new Slick.Event(),
            /**
			 * 鼠标进入事件
			 * @event onMouseEnter
			 */
            "onMouseEnter":                 new Slick.Event(),
            /**
			 * 鼠标离开事件
			 * @event onMouseLeave
			 */
            "onMouseLeave":                 new Slick.Event(),
            /**
			 * 单击表格事件
			 * @event onClick
			 * @param {Object} obj
			 *            {row:cell.row, cell:cell.cell} 入参
			 */
            "onClick":                     		 new Slick.Event(),
            /**
			 * 双击表格事件
			 * 
			 * @event onDblClick
			 * @param {Object} obj
			 *            {row:cell.row, cell:cell.cell} 入参
			 */
            "onDblClick":                  	 new Slick.Event(),
            /**
			 * 右键单击事件
			 * 
			 * @event onContextMenu
			 */
            "onContextMenu":               new Slick.Event(),
            /**
			 * 键盘事件，如需屏蔽自带事件需下设置 e.stopImmediatePropagation()
			 * 
			 * @event onKeyDown
			 */
            "onKeyDown":                     new Slick.Event(),
            
            "onAddNewRow":                  new Slick.Event(),
            /**
			 * 数据验证错误时触发
			 * 
			 * @event onValidationError
			 * @param {object} obj {
			 *            editor: currentEditor, cellNode: activeCellNode,
			 *            validationResults: validationResults, row: activeRow,
			 *            cell: activeCell, column: column } 默认传参
			 */
            "onValidationError":            new Slick.Event(),
            
            "onViewportChanged":            new Slick.Event(),
            
            /**
			 * 表头渲染时触发
			 * 
			 * @event onColumnsReordered
			 */
            "onColumnsReordered":           new Slick.Event(),
            
            /**
			 * 表头发生resize时触发
			 * 
			 * @event onColumnsResized
			 */
            "onColumnsResized":             new Slick.Event(),
            /**
			 * 表头发生resize时触发
			 * 
			 * @event onColumnsResized
			 */
            "onCellChange":                 new Slick.Event(),
            /**
			 * 修改前触发
			 * 
			 * @event onBeforeEditCell
			 * @param {Object} obj
			 *            {row:activeRow, cell:activeCell, item:item,
			 *            column:columnDef} 入参
			 */
            "onBeforeEditCell":             new Slick.Event(),
            /**
			 * 编辑完成时调用
			 * 
			 * @event onBeforeCellEditorDestroy
			 * @param {object} obj
			 *            {editor:currentEditor} 默认入参
			 */
            "onBeforeCellEditorDestroy":    new Slick.Event(),
            /**
			 * 表格销毁触发
			 * 
			 * @event onBeforeDestroy
			 */
            "onBeforeDestroy":              new Slick.Event(),
            /**
			 * 活动单元格改变时调用
			 * 
			 * @event onActiveCellChanged getActiveCell()
			 */
            "onActiveCellChanged":          new Slick.Event(),
            /**
			 * 选中单元格位置变化时触发
			 * 
			 * @event onActiveCellPositionChanged
			 */
            "onActiveCellPositionChanged":  new Slick.Event(),
            /**
			 * 拖动初始化
			 */
            "onDragInit":                   new Slick.Event(),
            "onDragStart":                  new Slick.Event(),
            "onDrag":                       new Slick.Event(),
            "onDragEnd":                    new Slick.Event(),
            /**
			 * {rows:getSelectedRows()}
			 */
            "onSelectedRowsChanged":        new Slick.Event(),
			"onCellClick":					new Slick.Event(),
            // Methods
            /**
			 * 注册插件，通过调用插件的 init(self)方法 self为当前grid
			 * 
			 * @method registerPlugin
			 * @param plugin
			 *            {object} 插件对象
			 */
            "registerPlugin":               registerPlugin,
            
            /**
			 * 撤销插件，通过调用插件的 destroy(self)方法 self为当前grid
			 * 
			 * @method unregisterPlugin
			 * @param plugin
			 *            {object} 插件对象
			 */
            "unregisterPlugin":             unregisterPlugin,
            
            /**
			 * 得到当前的列对象
			 * 
			 * @method getColumns
			 */
            "getColumns":                   getColumns,
           	/**
             * 得到当前的隐藏列对象
             * @method getHiddenColumns
             */
            "getHiddenColumns":              getHiddenColumns,
            
            /**
			 * 设置新的表头对象，并重构表格
			 * 
			 * @method setColumns
			 * @param {object}
			 *            columnDefinitions 表格列对象
			 */
            "setColumns":                   setColumns,
            
            /**
			 * 通过ID得到列索引号，就是第几列 columnsById[column.id] = i;
			 * 
			 * @method getColumnIndex
			 * @private
			 * @param {String}
			 *            ID 列id
			 */
            "getColumnIndex":               getColumnIndex,
            
            /**
			 * 动态设置ColumHeader的名称和提示
			 * 
			 * @method updateColumnHeader
			 * @param {String}
			 *            columnId 列id
			 * @param {String}
			 *            title 列标题
			 * @param {String}
			 *            toolTip 鼠标移动到列上时的提示内容
			 */
            "updateColumnHeader":           updateColumnHeader,
            
            /**
			 * 设置升降序列样式
			 * 
			 * @method setSortColumn
			 * @private
			 * @param columnId
			 *            列Id
			 * @param ascending
			 *            升降序
			 */
            "setSortColumn":                setSortColumn,
            
            /**
			 * 自动设置列宽度
			 * 
			 * @method autosizeColumns
			 */
            "autosizeColumns":              autosizeColumns,
            
            /**
			 * 获取表格的所有参数对象
			 * 
			 * @method getOptions
			 * @return {Object} options 参数对象
			 */
            "getOptions":                   getOptions,
            
            /**
			 * 设置表格参数
			 * 
			 * @method setOptions
			 * @param {Object}
			 *            options 参数对象
			 */
            "setOptions":                   setOptions,
            
            /**
			 * 得到整个data
			 * 
			 * @method getData
			 * @return {Array} data 表格所有数据，如果是dataview对，则获取dataview
			 */
            "getData":                      getData,
            
            /**
			 * 得到数据长度
			 * 
			 * @method getDataLength
			 * @return {int} length数据长度
			 */
            "getDataLength":                getDataLength,
            
            /**
			 * 通过id获取某行数据
			 * 
			 * @method getDataItem
			 * @param {String}
			 *            id 行数据的唯一id
			 */
            "getDataItem":                  getDataItem,
            
            /**
			 * 设置整行数据
			 * 
			 * @method setData
			 * @param {Array}
			 *            data 表格数据
			 */
            "setData":                      setData,
            
           	/**
			 * 获得selectmodel
			 * 
			 * @method getSelectionModel
			 * @private
			 */
            "getSelectionModel":            getSelectionModel,
            
            /**
			 * 自定义选择模式，通过调用自定义选择模式中init方法初始化 并为 onSelectedRangesChanged
			 * 事件申明handleSelectedRangesChanged方法
			 * 
			 * @param model
			 * @private
			 * @method setSelectionModel
			 */
            "setSelectionModel":            setSelectionModel,
            
            /**
			 * 得到当前选中的行号，若要使用此方法，必须设置selectionModel
			 * 
			 * @method getSelectedRows
			 * @return {Array} data返回选择行的数据对象
			 */
            "getSelectedRows":              getSelectedRows,
             /**
				 * 通过行号选择数据列
				 * 
				 * @method setSelectedRows
				 * @return {Array} 需要选中的行，如[1,2,3]
				 */
            "setSelectedRows":              setSelectedRows,
            
			/**
			 * 渲染表格
			 * 
			 * @method render
			 * @private
			 */
            "render":                       render,
            
            /**
			 * 使所有行无效
			 * 
			 * @method invalidate
			 * @private
			 */
            "invalidate":                   invalidate,
            
            /**
			 * 使row行无效
			 * 
			 * @method invalidateRow
			 * @param {int} row 第几行
			 * @private
			 */
            "invalidateRow":                invalidateRow,
            
            /**
			 * 使一些行无效
			 * 
			 * @method invalidateRows
			 * @param {Array} rows 行号
			 */
            "invalidateRows":               invalidateRows,
            
            /**
			 * 使所以行无效，但重新渲染
			 * 
			 * @method invalidateAllRows
			 */
            "invalidateAllRows":            invalidateAllRows,
            
            /**
			 * 更新row行的cell个单元格
			 * @method updateCell
			 * @param row
			 *            第几行从0开始
			 * @param cell
			 *            第几列从0开始
			 */
            "updateCell":                   updateCell,
            
            /**
			 * 更新row行整条数据
			 * @method updateRow
			 * @param row 第几行从0开始
			 */
            "updateRow":                    updateRow,
            
            /**
			 * 得到当前显示区域
			 * 
			 * @return {top:
			 *         Math.floor((scrollTop+offset)/options.rowHeight),bottom:
			 *         Math.ceil((scrollTop+offset+viewportH)/options.rowHeight)}
			 * @method getViewport
			 */
            "getViewport":                  getVisibleRange,
            /**
			 * @method resizeCanvas
			 * @private
			 */
            "resizeCanvas":                 resizeCanvas,
            /**
			 * @method updateRowCount
			 * @private
			 */
            "updateRowCount":               updateRowCount,
            /**
			 * 滚动到row行
			 * 
			 * @param {boolean} doPaging true/false
			 * @method scrollRowIntoView
			 */
            "scrollRowIntoView":            scrollRowIntoView,
            /**
			 * 得到数据显示区域
			 * 
			 * @return canvas[0]
			 * @method getCanvasNode
			 */
            "getCanvasNode":                getCanvasNode,
			
            /**
			 * 通过X,Y坐标得到单元格
			 * 
			 * @method getCellFromPoint
			 * @param {int} x
			 *            x坐标
			 * @param {int} y
			 *            y坐标
			 * @return {Object} {row:row,cell:cell}
			 */
            "getCellFromPoint":             getCellFromPoint,
            
            /**
			 * 通过event来得到单元格
			 * 
			 * @method getCellFromEvent
			 * @param e
			 *            事件对象
			 * @return
			 */ 
             
            "getCellFromEvent":             getCellFromEvent,
            /**
			 * 得到活动的单元格
			 * 
			 * @method getActiveCell
			 * @return {object} cell 第几行第几列，如 { row: row cell: cell }
			 */
            "getActiveCell":                getActiveCell,
            /**
			 * 设置活动单元格选中
			 * 
			 * @method getActiveCell
			 * @param row
			 * @param cell
			 */
            "setActiveCell":                setActiveCell,
            /**
			 * 获取当前活动的单元格div对象
			 * 
			 * @method getActiveCellNode
			 * @return {DOM} activeCellNode
			 */
            "getActiveCellNode":            getActiveCellNode,
            
            /**
			 * 获取当前活动的单元格盒子模型
			 * 
			 * @method getActiveCellPosition
			 * @return {Object} box 包含box.top,box.bottom,box.left,box.right
			 */
            "getActiveCellPosition":        getActiveCellPosition,
            "resetActiveCell":              resetActiveCell,
            /**
			 * 使单元格可编辑
			 * 
			 * @method makeActiveCellEditable
			 * @param {Object}
			 *            editor 编辑器
			 */
            "editActiveCell":               makeActiveCellEditable,
            /**
			 * 获取当前默认编辑器
			 * 
			 * @method getCellEditor
			 * @return {Object} editor 编辑器
			 */ 
            "getCellEditor":                getCellEditor,
            "getCellNode":                  getCellNode,
            "getCellNodeBox":               getCellNodeBox,
            /**
			 * 判断当前单元格是否被选中
			 * 
			 * @method canCellBeSelected
			 * @return {Boolean} true/false true为选中，false为未选中
			 */ 
            "canCellBeSelected":            canCellBeSelected,
            /**
			 * 判断当前单元格是否被选中（单元格选中）
			 * 
			 * @method canCellBeActive
			 * @return {Boolean} true/false true为选中，false为未选中
			 */ 
            "canCellBeActive":              canCellBeActive,
            /**
			 * 向前一个单元格定位
			 * 
			 * @method navigatePrev
			 */
            "navigatePrev":                 navigatePrev,
            /**
			 * 定位到下一个单元格
			 * 
			 * @method navigateNext
			 */
            "navigateNext":                 navigateNext,
            /**
			 * 向上定位一个单元格
			 * 
			 * @method navigateUp
			 */
            "navigateUp":                   navigateUp,
            /**
			 * 向下定位一个单元格
			 * 
			 * @method navigateDown
			 */
            "navigateDown":                 navigateDown,
            /**
			 * 向左定位一个单元格
			 * 
			 * @method navigateLeft
			 */
            "navigateLeft":                 navigateLeft,
            /**
			 * 向右定位一个单元格
			 * 
			 * @method navigateRight
			 */
            "navigateRight":                navigateRight,
            /**
			 * 通过行号和列号定位到单元格
			 * 
			 * @method gotoCell
			 * @param {int}
			 *            row 第几行
			 * @param {int}
			 *            cell 第几列
			 */
            "gotoCell":                     gotoCell,
            
            /**
			 * 通过行号和列号定位到单元格
			 * 
			 * @method getTopPanel
			 * @return {$} 返回topPanel的jquery对象
			 */
            "getTopPanel":                  getTopPanel,
            
            /**
			 * 显示顶部panel
			 * 
			 * @method showTopPanel
			 */
            "showTopPanel":                 showTopPanel,
            
            /**
			 * 隐藏顶部panel
			 * 
			 * @method hideTopPanel
			 */
            "hideTopPanel":                 hideTopPanel,
            
            /**
			 * 显示顶部列panel
			 * 
			 * @method showHeaderRowColumns
			 */
            "showHeaderRowColumns":         showHeaderRowColumns,
            /**
			 * 隐藏顶部列panel
			 * 
			 * @method showHeaderRowColumns
			 */
            "hideHeaderRowColumns":         hideHeaderRowColumns,
            
            /**
			 * 通过行号和列号定位到单元格
			 * 
			 * @method getHeaderRow
			 * @return {$} 返回HeaderRow的jquery对象
			 */
            "getHeaderRow":                 getHeaderRow,
            "getHeaderRowColumn":           getHeaderRowColumn,
            "getGridPosition":              getGridPosition,
            /**
			 * 单元格红框闪烁
			 * 
			 * @method getHeaderRow
			 * @param {int}
			 *            row 行
			 * @param {int}
			 *            cell 列
			 * @param {int}
			 *            speed 闪烁速度
			 */
            "flashCell":                    flashCell,
            "addCellCssStyles":             addCellCssStyles,
            "setCellCssStyles":             setCellCssStyles,
            "removeCellCssStyles":          removeCellCssStyles,
			"updataData" : 					updataData, // lins
            "destroy":                      destroy,

            // IEditor implementation
            "getEditorLock":                getEditorLock,
            "getEditController":            getEditController,
            // TODO 林森添加
            "getDataView":					getDataView,
            "getDataByDataView":			getDataByDataView,
            "getSelectRowsDataToJSON":		getSelectRowsDataToJSON,
            "deleteDataRows":				deleteDataRows,
            "addNewRow":					addNewRow,
            "addNewRowTo":				    addNewRowTo,
            "addNewRowDown":				addNewRowDown,
             /**
				 * 获取当前表格的id
				 * 
				 * @method getGridId
				 * @return {String} id 表格id
				 */
            "getGridId":					getGridId,
            /**
			 * 刷新表格
			 * 
			 * @method refreshGrid
			 */
            "refreshGrid":					refreshGrid,
            "getEditorItems":				getEditorItems,
            "clearDirty":					clearDirty,
            "getSelectRowsDataToObj":		getSelectRowsDataToObj,
            "getAddRow" : 					getAddRow,
            "getRemovedRows":				getRemovedRows,
            "deleteRow":					deleteRow,
            "getSelectedRowsById":			getSelectedRowsById,
            "setColumnHidden":				setColumnHidden,
            "setColumnShow":				setColumnShow,
            "getPager":						getPager,
            "getColumnById":				getColumnById ,
            "getCollectionsDataArrayObject":getCollectionsDataArrayObject,
            "clearDirtyWidthOutPager" :clearDirtyWidthOutPager,
            "setCheckedRows" 	: setCheckedRows,
            "getContainer" : getContainer,
            "getDefaultBox" : getDefaultBox,
            "addSelectRowsByData":addSelectRowsByData,
            "cancelCheckedRowByData": cancelCheckedRowByData,
            /*
			 * 根据数组对象取消某些选择 @param {Array} datas 需要取消选择的数据数组
			 */
            "cancelCheckedRowsByArray":cancelCheckedRowsByArray,
            /**
			 * 勾选所有数据，相当于点击全选checkbox
			 * 
			 * @method checkedAllData
			 */
            "checkedAllData" : checkedAllData,
            /**
			 * 取消所有选中数据
			 * 
			 * @method cancelSelectedAllData
			 */
            "cancelSelectedAllData":cancelSelectedAllData,
            "showGridItemOperate" : showGridItemOperate,
            "hideGridItemOperate" : hideGridItemOperate
        });
        init();
    }
}));
