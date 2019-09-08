/**
 * datagrid表格常用操作方法,调用方式为Base.xxx();
 * @module Base
 * @class datagrid
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
			getGridSelectedRows : getGridSelectedRows,
			getGridModifiedRows : getGridModifiedRows,
			refreshGrid : refreshGrid,
			getGridData : getGridData,
			_setGridData : _setGridData,
			deleteGridSelectedRows : deleteGridSelectedRows,
			deleteGridRow : deleteGridRow,
			addGridRow : addGridRow,
			addGridRowDown : addGridRowDown,
			addGridRowTo : addGridRowTo,
			getGridRemovedRows : getGridRemovedRows,
			getGridAddedRows : getGridAddedRows,
			clearGridData : clearGridData,
			getGridColumnById : getGridColumnById,
			setGridColumnFormat : setGridColumnFormat,
			setGridColumnHidden : setGridColumnHidden,
			setGridColumnShow : setGridColumnShow,
			gotoGridRow : gotoGridRow,
			setGridPagerUrl : setGridPagerUrl,
			clearGridDirty : clearGridDirty,
			setGridCellData : setGridCellData,
			setGridRowData : setGridRowData,
			submitGridAllData : submitGridAllData,
			setSelectRowsByData : setSelectRowsByData,
			cancelCheckedRowsByArray : cancelCheckedRowsByArray,
			checkedAllData : checkedAllData,
			cancelSelectedAllData : cancelSelectedAllData ,
			addGridColumns : addGridColumns,
			expGridExcel : expGridExcel,
			rebuildGridFromHtml : rebuildGridFromHtml,
			closeMediaGridColumns : closeMediaGridColumns
		};
		
		/**
		 *  获取表格选中行的json对象，如无数据返回null。
		 *  <br/>例如:
		 *  <br/>function() {
		 *  <br/>     var rowData = Base.getGridSelectedRows("grid"); //获得表格选中行的JSON数组
		 *  <br/>     for (var i = 0; i < rowData.length; i ++ ) {
		 *  <br/>     	alert(rowData[i].aac001); //弹出第i行数据的aac001字段内容。
		 *  <br/>     }
		 *  <br/>     var dataStr = Ta.util.obj2string(rowData); //如果要把得到的数据传到后台，必须把json数组转换成字符串。
		 *  <br/>     var param = {};
		 *  <br/>     param["dto['gridselect']"] = dataStr; //定义一个参数对象
		 *  <br/>     Base.submit("xxx", "xxx", param); //通过submit异步提交
		 *  <br/>}
		 * @method getGridSelectedRows
		 * @param {String} gridId 列表ID
		 * @return {Object} 返回JSON 对象，类型如下:
		 * <br/>   [{"aac001":"0001","aac002":"01"},{"aac001":"0002","aac002":"02"}];
		 */
		function getGridSelectedRows(gridId) {
			//TODO 
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				var data = grid.getSelectRowsDataToObj();
				var newData = $.extend(true, [], data);
				return newData;
			} else {
				return $("#" + gridId).datagrid("getChecked");
			}
			return null;
		};
		/**
		 * 获取表格修改行的json对象，此方法只要修改了的数据行都会被返回，如无数据，返回null。
		 * <br/>例如:
		 * <br/> function() {
		 * <br/>   var rowData = Base.getGridModifiedRows("grid"); //获得表格修改行的JSON数组
		 * <br/>   for (var i = 0; i < rowData.length; i ++ ) {
		 * <br/>       alert(rowData[i].aac001); //弹出第i行数据的aac001字段内容。
		 * <br/>   }
		 * <br/>   var dataStr = Ta.util.obj2string(rowData); //如果要把得到的数据传到后台，必须把json数组转换成字符串。
		 * <br/>   var param = {};
		 * <br/>   param["dto['gridselect']"] = dataStr; //定义一个参数对象
		 * <br/>   Base.submit("xxx", "xxx", param); //通过submit异步提交
		 * <br/> }
		 * @method getGridModifiedRows
		 * @param {String} gridId 列表ID
		 * @return {Object} 返回JSON 对象，类型如下:
		 * <br/>  [{"aac001":"0001","aac002":"01"},{"aac001":"0002","aac002":"02"}];
		 */
		function getGridModifiedRows(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.getEditorLock().commitCurrentEdit();
				return grid.getEditorItems();
			}
			return null;
		}
		
		/**
		 * 刷新表格数据。
		 * @method refreshGrid
		 * @param {String} gridId 表格id
		 */
		function refreshGrid(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.getDataView().refresh();
				grid.refreshGrid();
			}
			return true;
		}
		/**
		 * 获取表格所有数据以json数组对象形式返回,如无数据返回空数组。
		 * <br/>例如:  
		 * <br/> function() {
		 * <br/>    var rowData = Base.getGridData("grid"); //获得表格选中行的JSON数组
		 * <br/>    for (var i = 0; i < rowData.length; i ++ ) {
		 * <br/>       	alert(rowData[i].aac001); //弹出第i行数据的aac001字段内容。
		 * <br/>    }
		 * <br/>    var dataStr = Ta.util.obj2string(rowData); //如果要把得到的数据传到后台，必须把json数组转换成字符串。
		 * <br/>    var param = {};
		 * <br/>    param["dto['gridselect']"] = dataStr; //定义一个参数对象
		 * <br/>    Base.submit("xxx", "xxx", param); //通过submit异步提交
		 * <br/> }
		 * @method getGridData
		 * @param {String} gridId 表格id
		 */
		function getGridData(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				return grid.getDataView().getItems();
			}
			return [];
		}

		/**
		 * 设置表格数据。
		 * <br/>例如:data为json对象如：[{"aac001":"0001","aac002":"01"},{"aac001":"0002","aac002":"02"}]
		 * <br/> function setGridData() {
		 * <br/>   var data = [];
		 * <br/>   var row = {};
		 * <br/>   row.aac001 = '0001';
		 * <br/>   row.aac002 = '01';
		 * <br/>   ....
		 * <br/>   data.push(row);
		 * <br/>   var row2  {};
		 * <br/>   row2.aac001 = '0001';
		 * <br/>   row2.aac002 = '01';
		 * <br/>   ....
		 * <br/>   data.push(row2);
		 * <br/>   ....
		 * <br/>   Base._setGridData(data);
		 * <br/> }
		 * @method _setGridData
		 * @param {String}  gridId 表格id
		 * @param {Object} data 给表格设置的数据，json格式
		 * @return {Object} data 
		 */
		function _setGridData(gridId, data) {
			if ($("#" + gridId).is("table")) {
				var $mediaColumn = $("#" + gridId + "_mediaColumn");
				$mediaColumn.appendTo("body").hide(false);
				data.rows = data.list;
				$("#" + gridId).datagrid('loadData',data);
			}
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				//阻止默认选择行被clearDirty()覆盖
				grid.clearDirtyWidthOutPager();
				grid.getDataView().setItems(data);
				grid.getDataView().refresh();
				grid.refreshGrid();
			}
			return data;
		}
		/**
		 * 前台删除表格选择的行，使用场景:提交选择行数据到后台，后台执行删除，成功回调方法，删除界面上数据，而不用重新查一遍数据。
		 * @method deleteGridSelectedRows
		 * @param {String} gridId 表格id
		 */
		function deleteGridSelectedRows(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.deleteDataRows();
				//grid.clearDirty();
			}
		}
		/**
		 * 根据row行号来删除表格选择的行 
		 * <br/>例如:
		 * <br/>function delrow() {
		 * <br/>  Base.deleteGridRow('myGrid', 10);//界面删除第10条数据。
		 * <br/>  Base.submit('myGrid', "xxxxx"); //将删除列异步提交到后台，后台通过getDelete()方法获取。
		 * <br/>}
		 * @method deleteGridRow
		 * @param {String} gridId 表格id
		 * @param {Number} row 要删除数据的行号
		 * @return {Boolean} 删除成功返回true,失败返回false
		 */
		function deleteGridRow(gridId, row) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.deleteRow(row);
				return true;
			} else if ($("#" + gridId).is("table")){
				$("#" + gridId).datagrid("deleteRow", row);
			}
			return false;
		}
		/**
		 * 在表格顶部增加一行，主要用于可编辑表格增加一行编辑，也可以直接添加一行非空数据。
		 * <br/>例如： 
		 * <br/>function addrow() {
		 * <br/>      var row = {}; 
		 * <br/>      row.aac001 = '0001';
		 * <br/>      row.aac002 = '01';
		 * <br/>      row.aac003 = '02';
		 * <br/>      ....
		 * <br/>      Base.addGridRow(gridId,row);
		 * <br/>}
		 * @method addGridRow
		 * @param {String} gridId 表格id
		 * @param {Object} columnValue 行数据对象及值例如{"aac001":"1100012","aac002":"01"}
		 * 
		 */
		function addGridRow(gridId, columnValue) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.addNewRow(columnValue);
			}
			return false;
		}
		/**
		 * 在表格底部增加一行，主要用于可编辑表格增加一行编辑，也可以直接添加一行非空数据。
		 * <br/>例如： 
		 * <br/>function addrow() {
		 * <br/>    var row = {}; 
		 * <br/>    row.aac001 = '0001';
		 * <br/>    row.aac002 = '01';
		 * <br/>    row.aac003 = '02';
		 * <br/>    ....
		 * <br/>    addGridRowDown(gridId,row);
		 * <br/>}
		 * @method addGridRowDown
		 * @param {String} gridId 表格id
		 * @param {Object} columnValue 行数据对象例如{"aac001":"1100012","aac002":"01"}
		 */
		function addGridRowDown(gridId, columnValue) {
			// columnValue = {"title":"aaaa","start":"01"}
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.addNewRowDown(columnValue);
			}
			return false;
		}

		/**
		 * 在表格指定位置增加一行，主要用于可编辑表格增加一行编辑，也可以直接添加一行非空数据。
		 * <br/>例如：
		 * <br/>function addrow() {
		 * <br/>    var row = {}; 
		 * <br/>    row.aac001 = '0001';
		 * <br/>    row.aac002 = '01';
		 * <br/>    row.aac003 = '02';
		 * <br/>    ....
		 * <br/>    addGridRowTo(gridId,row);
		 * <br/>}
		 * @method addGridRowTo
		 * @param {String} gridId 表格id
		 * @param {Object} columnValue 行数据对象例如{"aac001":"1100012","aac002":"01"}
		 * @param {Number} rownum 表格行号
		 */
		function addGridRowTo(gridId, columnValue,rownum) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.addNewRowTo(columnValue,parseInt(rownum));
			}
			return false;
		}
		
		/**
		 * 获取表格删除的行json数组对象，如无数据返回null。
		 * <br/>例如：
		 * <br/>function() {
		 * <br/> 	var rowData = Base.getGridRemovedRows("grid"); //获得表格选中行的JSON数组
		 * <br/> 	for (var i = 0; i < rowData.length; i ++ ) {
		 * <br/> 		alert(rowData[i].aac001); //弹出第i行数据的aac001字段内容。
		 * <br/> 	}
		 * <br/> 	var dataStr = Ta.util.obj2string(rowData); //如果要把得到的数据传到后台，必须把json数组转换成字符串。
		 * <br/> 	var param = {};
		 * <br/> 	param["dto['gridselect']"] = dataStr; //定义一个参数对象
		 * <br/> 	Base.submit("xxx", "xxx", param); //通过submit异步提交
		 * <br/>}
		 * @method getGridRemovedRows
		 * @param {string} gridId 表格id
		 * @return {Object} json 行数组对象，如[{"aac001":"0001","aac002":"01"},{"aac001":"0002","aac002":"02"}]
		 */
		function getGridRemovedRows(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				return grid.getRemovedRows();
			}
			return null;
		}
		/**
		 * 获取表格删除的行json数组对象，如无数据返回null。
		 * <br/>例如：
		 * <br/>function() {
		 * <br/> 	var rowData = Base.getGridAddedRows("grid"); //获得表格选中行的JSON数组
		 * <br/> 	for (var i = 0; i < rowData.length; i ++ ) {
		 * <br/> 		alert(rowData[i].aac001); //弹出第i行数据的aac001字段内容。
		 * <br/> 	}
		 * <br/> 	var dataStr = Ta.util.obj2string(rowData); //如果要把得到的数据传到后台，必须把json数组转换成字符串。
		 * <br/> 	var param = {};
		 * <br/> 	param["dto['gridselect']"] = dataStr; //定义一个参数对象
		 * <br/> 	Base.submit("xxx", "xxx", param); //通过submit异步提交
		 * <br/>}
		 * @method getGridAddedRows
		 * @param {string} gridId 表格id
		 * @return {Object}  json 行数组对象，如[{"aac001":"0001","aac002":"01"},{"aac001":"0002","aac002":"02"}]
		 */
		function getGridAddedRows(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				return grid.getAddRow();
			}
			return null;
		}
		/**
		 * 清除表格数据。
		 * @method clearGridData
		 * @param {String} gridId 表格id
		 */
		function clearGridData(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				Base._setGridData(gridId, []);
				grid.clearDirty();
			}
		}

		/**
		 * 通过列id获取列信息，无数据返回null。
		 * <br/>例如：
		 * <br/>function() {
		 * <br/>		var column = Base.getGridColumnById("grid", "name"); //获得表格选中行的JSON数组
		 * <br/>		alert(column.name); //弹出列数据的表头信息内容：如姓名。
		 * <br/>		alert(column.width); //弹出列宽度：如200。
		 * <br/>		//其它属性参照标签定义时的列属性。
		 * <br/>		//或者通过console.log(column);方法在控制台中，或者调试方法查看column对象的其它属性。
		 * <br/>}
		 * @method getGridColumnById
		 * @param {String} gridId 表格id
		 * @param {String} columnId 列id
		 * @return 列的json对象，如：{id: "AAC001", name: "姓名", field: "name", width: 80, resizable: false, formatter: Slick.Formatters.PercentCompleteBar}
		 */
		function getGridColumnById(gridId, columnId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				return grid.getColumnById(columnId);
			}
			return null;
		}
		/**
		 * 动态设置表格某一列的formatter回调渲染方法。
		 * <br/>例如：
		 * <br/>function colformatter(row, cell, value, columnDef, dataContext) {
		 * <br/>     if (row == 1 && cell == 2) {  //如果是第一行第二列
		 * <br/>          return "<span style='color:red'>*</span>" + value;  //列表显示的值前面加了个红*；
		 * <br/>     } 
		 * <br/>     if (dataContext.name == "lins") {
		 * <br/>           	return "<span style='color:red'>*</span>" + value; //如果当列所在的行中name那个字段的数据为"lins"，则在此列添加小红星。
		 * <br/>     }
		 * <br/>}
		 * <br/>Base.setGridColumnFormat("grid1","aaa100",colformatter);
		 * @method setGridColumnFormat
		 * @param {String} gridId 表格id
		 * @param {String} columnId 列id
		 * @param {Object} formatter 回调函数,默认传参row, cell, value, columnDef, dataContext分别是行号，列号，当前值，列定义，整行数据
		 */
		function setGridColumnFormat(gridId, columnId, formatter) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				var column = grid.getColumnById(columnId);
				column.formatter = formatter;
				grid.getDataView().refresh();
				grid.refreshGrid();
			}

		}
		
		/**
		 * 设置隐藏某一列
		 * @method setGridColumnHidden
		 * @param {String} gridId 表格id
		 * @param {String} columnId 列id
		 */
		function setGridColumnHidden(gridId, columnId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.setColumnHidden(columnId);
			} else {
				$('#' + gridId).datagrid('hideColumn',{field:columnId});
			}
		}
		/**
		 * 设置显示某一列
		 * @method setGridColumnShow
		 * @param {String} gridId 表格id
		 * @param {String} columnId 列id
		 */
		function setGridColumnShow(gridId, columnId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.setColumnShow(columnId);
			} else {
				$('#' + gridId).datagrid('showColumn',{field:columnId});
			}
		}
		/**
		 * 前往表格某一行。
		 * @method gotoGridRow
		 * @param {String} gridId 表格id
		 * @param {Number} row 第几行
		 * @param {Boolean} dopaging 定位行位于页面上方还是下方
		 */
		function gotoGridRow(gridId, row, doPaging) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.scrollRowIntoView(row, doPaging);
				grid.setSelectedRows([row]);
			}
		}

		/**
		 * 更改表格分页url。
		 * @method setGridPagerUrl
		 * @param {String} gridId 表格id
		 * @param {String} url 分页url
		 */
		function setGridPagerUrl(gridId, url) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.getPager().setPagerUrl(url);
			}
		}

		/**
		 * 清空表格脏数据，包括之前选择行数据，修改行数据，添加行数据，并初始化分页数据,通常表格的查询方法中需要在查询数据后调用此方法。
		 * @method clearGridDirty
		 * @param {String} gridId 表格id
		 */
		function clearGridDirty(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				grid.clearDirty();
			}
		}

		/**
		 * 更新指定单元格
		 * @method setGridCellData
		 * @param {String} gridId 表格id
		 * @param {Number} row 指定行
		 * @param {Number} cell 指定列
		 * @param {Object} data 要修改的数据
		 * @example
		 * function setFormat() {
		 *   Base.setGridCellData('grid1',1,1,"aaa");//把grid1里1行1列的数据修改为aaa
		 * }
		 */
		function setGridCellData(gridId, row, cell, data) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				var dataAll = Base.getGridData(gridId);
				var column = grid.getColumns();
				dataAll[row - 1][column[cell].id] = data;
				grid.getDataView().refresh();
				grid.refreshGrid();
			}
		}
		/**
		 * 设定指定行的数据。
		 * @method setGridRowData
		 * @param {String} gridId  表格id
		 * @param {Number} row  行号，从1开始
		 * @param {Object} data  行数据，必须符合json格式，可以只设定你要改变的值，例如{"empid":"122121","empname":"杨天","empsex":"1"}
		 */
		function setGridRowData(gridId, row, data) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				var girdData = Base.getGridData(gridId);
				if(row-1<0  ||  row>girdData.length){
					alert("输入的行号有误!");
					return ;
				}
				for (var key in data) {
					if (key != "__id___") {
						// 设置改变后的值
						girdData[row-1][key] = data[key];
					}
				}
				grid.getDataView().refresh();
				grid.refreshGrid();
			} else if($("#" + gridId).is("table")){
				$("#" + gridId).datagrid('updateRow',{
					index: row,
					row: data
				});
			} else{
				alert("输入的gridId 错误!");
			}
		}

		/**
		 * 提交表格当前页全部数据。
		 * @method submitGridAllData
		 * @param {String} gridId 表格id
		 * @param {String} url action地址
		 * @param {Object} submitIds 其它条件
		 */
		function submitGridAllData(gridId, url, submitIds, _succCallback) {
			var data = Base.getGridData(gridId);
			if (data && data.length > 40000) {
				Base.alert("数据量过大");
				return;
			}
			var param = {};
			param[gridId] = Ta.util.obj2string(data);
			Base.submit(submitIds, url , param, null,false,_succCallback);
		}
		/**
		 * 通过指定数据选择表格行。
		 * @method setSelectRowsByData
		 * @param {String} gridId 表格id
		 * @param {Array} array 数组，例如[{aac001:'aaa'}, {aac004: 1}]
		 */
		function setSelectRowsByData(gridId, array) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) { 
				grid.setCheckedRows(array);
			}
		}

		/**
		 * 通过指定数据取消选择表格行。
		 * @method cancelCheckedRowsByArray
		 * @param {String} gridId 表格id
		 * @param {Array} array 数组，例如[{aac001:'aaa'}, {aac004: 1}]
		 */
		function cancelCheckedRowsByArray(gridId, array) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) { 
				grid.cancelCheckedRowsByArray(array);
			}
		}
		
		/**
		 * 全选，相当于点击全选按钮。
		 * @method checkedAllData
		 * @param {String} gridId 表格id
		 */
		function checkedAllData(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) { 
				grid.checkedAllData();
			}
		}

		/**
		 * 取消，相当于点击全选/取消按钮。
		 * @method cancelSelectedAllData
		 * @param {String} gridId 表格id
		 */
		function cancelSelectedAllData(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) { 
				grid.cancelSelectedAllData();
			}
		}
		/**
		 * 添加表格列
		 * @method addGridColumns
		 * @param {String} gridId
		 * @param {Object} columns json对象{name:'', field:'', id:'': formatter:''}
		 */
		function addGridColumns(gridId, column) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
				if (column.name == undefined || column.id == undefined) return;
				if (column.field == undefined) column.field = column.id;
				var columnsOld = grid.getColumns();
				columnsOld.push(column);
				grid.setColumns(columnsOld);
			}
		}

		function expGridExcel(gridId) {
			var grid = Ta.core.TaUIManager.getCmp(gridId);
			if (grid) {
					var a = grid.getColumns();
					var b = grid.getDataView().getItems();
					var collection  = grid.getOptions().collectionsDataArrayObject;
					var row = [];
					var cell = [];
					var head =[];
					for (var i = 0; i < a.length; i ++ ) {
						if (a[i].id != "_checkbox_selector" && a[i].id != "__no" && !a[i].icon) {
							cell.push("\"" + a[i].id + "\"");
							head.push("\"" + a[i].name + "\"");
						}
					}
					row.push(head);
					for (var i = 0; i < b.length; i ++) {
						var cells=[];
						for (var j = 0; j < cell.length; j ++) {
							var cData = b[i][cell[j].replaceAll("\"","")] == undefined?"":b[i][cell[j].replaceAll("\"","")];
							if (collection != undefined) {
									var collectcell = collection[cell[j].replaceAll("\"","")];
									var b_collected = false;
									if (collectcell && collectcell.length > 0) {
										for (var c = 0; c < collectcell.length ; c ++) {
											if (collectcell[c].id == cData) {
												cells.push("\"" + collectcell[c].name + "\"");
												b_collected = false;
												break;
											} else {
												b_collected = true;
											}
										}
										if (b_collected) {
											cells.push("\"" + cData + "\"");
										}
									} else if (!b_collected) cells.push("\"" + cData + "\"");
							} else cells.push("\"" + cData + "\"");
						}
						row.push(cells);
					}
					var $input = $("<textarea/>").attr("display", "none").val(Ta.util.obj2string(row)).attr("name", "_grid_item_export_excel");
					var $form = $("<form/>")//.attr("enctype","multipart/form-data").attr("accept-charset", "UTF-8")
						.append($input).attr("method", "post")
						.attr("display", "none")
						.appendTo("body")
						.attr("action", Base.globvar.contextPath + "/exportGridDefaultExcel.do").submit().remove();
			}
		}
		function rebuildGridFromHtml() {
			var grid = $(".datagrid");
			grid.each(function() {
				var gridId = $(this).attr("id");
				var columns = [];
				var options = {};
				$(this).find(".slick-header-column").each(function() {
					var column = {};
					column.id = column.field = $(this).attr("field");
					if (column.id == "__no") {
						options.haveSn = "true";
					} else if (column.id == "_checkbox_selector") {
						options.selectType = "checkbox";
					} else {
						column.name = $(this).attr("title");
						var width = $(this).css("width");
						width = width.replace("px", "");
						column.width = Number(width);
						columns.push(column);
					}
				});
				options.onChecked = function() {
					return false;
				};
				var $p = $("#" + gridId).parent();
				$("#" + gridId).remove();
				$("<div/>").attr("id", gridId).css("height", "100%").addClass("datagrid").attr("fit","true").appendTo($p);
				var grid_ = new Slick.Grid("#" + gridId, [], columns, options);
				Ta.core.TaUIManager.register(gridId ,grid_);
			});
		}
		
		/**
		 * 关闭列下拉面版
		 * @method closeMediaGridColumns
		 * @param {String} gridId 表格id
		 */
		function closeMediaGridColumns(gridId){
			var $mediaColumn = $("#" + gridId + "_mediaColumn");
			$("#"+gridId).find(".datagrid-row-collapse").each(function(){
				$(this).trigger('click',this);
			});
			$mediaColumn.appendTo("body").show(false);
		}
		
	}
	
}));
