(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
    // register namespace
    $.extend(true, window, {
        "Slick": {
            "CheckboxSelectColumn":   CheckboxSelectColumn
        }
    });


    function CheckboxSelectColumn(options) {
        var _grid;
        var notSelected = 0; //保存多少条不能选择
       // var _idProperty = "__id___";
        var _self = this;
        var _selectedRowsLookup = {};
        var _defaults = {
            columnId: "_checkbox_selector",
            cssClass: "slick-cell-checkbox",
            toolTip: "全选/撤销",
            width: 40
        };

        var _options = $.extend(true,{},_defaults,options);

        function init(grid) {
            _grid = grid;
            _grid.onSelectedRowsChanged.subscribe(handleSelectedRowsChanged);
            _grid.onClick.subscribe(handleClick);
            _grid.onHeaderClick.subscribe(handleHeaderClick);
        }

        function destroy() {
            _grid.onSelectedRowsChanged.unsubscribe(handleSelectedRowsChanged);
            _grid.onClick.unsubscribe(handleClick);
            _grid.onHeaderClick.unsubscribe(handleHeaderClick);
        }
		/**
		 * 当选择行改变时
		 */
        function handleSelectedRowsChanged(e, args) {
            var selectedRows = _grid.getSelectedRows(); //获取选择行号
            var lookup = {}, row, i;
            //通过两个for循环排除没有改变的列.如selectLook 1 2 3， look 2 4，invalidate 2,4
            for (i = 0; i < selectedRows.length; i++) {
                row = selectedRows[i]; // row = 选择的行号
                lookup[row] = true;		//设置查找到位true
                if (lookup[row] !== _selectedRowsLookup[row]) {//如果相同不做改变
                    _grid.invalidateRow(row); //当之前的查找项和当前查找项标志不同是，销毁该行
                    delete _selectedRowsLookup[row];//删除之前状态中row 
                }
            }
            //销毁之前选择的row
            for (i in _selectedRowsLookup) {
                _grid.invalidateRow(i);
            }
            
            _selectedRowsLookup = lookup; //设置当前选择数据为最新选择数据
            _grid.render();
            
           var totalsColumns = 0;
           var isGroupBy = false,isTotals = false;
           //如果表格中存在分组
            var groupby = _grid.getOptions().groupingBy;
            if(groupby && groupby != "_onlyTotals"){
            	isGroupBy = true;
            }
            //如果表格中设置有统计信息,表格会多出两列,liys修改
            var columns_ = _grid.getColumns();
            for (var i = 0; i < columns_.length; i ++) {
           		if (columns_[i].totals != undefined) {
           			isTotals = true;
           		}
            };
            //存在分组和统计
            if(isGroupBy && isTotals){
            	totalsColumns = 4;
            }
            //存在分组不存在统计
            if(isGroupBy && !isTotals){
            	totalsColumns = 4;
            }
            //不存在分组存在统计
            if(!isGroupBy && isTotals){
            	totalsColumns = 1;
            }
			//当选种中的数据和数据长度相同时，勾选顶部选择框，添加排除数据为0时的
            if (selectedRows.length == _grid.getDataLength() - notSelected - totalsColumns && _grid.getDataLength() != 0) {
                _grid.updateColumnHeader(_options.columnId, "<input type='checkbox'  class='slick-checkbox-header' checked='checked'>", _options.toolTip);
            }
            else {
                _grid.updateColumnHeader(_options.columnId, "<input type='checkbox'  class='slick-checkbox-header'>", _options.toolTip);
            }
        }

        function handleClick(e, args) {
            // clicking on a row select checkbox
        	//如果点击了单元格，判断是否是checkBox
        	var a;
        	var target = e.srcElement||e.target;
        	var $checkbox;
        	if($(target).hasClass("slick-cell-checkbox")){
        		$checkbox = $(target).children();
        	}else{
        		$checkbox = $(target);
        	}
            if ((a = _grid.getColumns()[args.cell].id) === _options.columnId ) {
                // 如果是编辑状态尝试提交
                if (_grid.getEditorLock().isActive() && !_grid.getEditorLock().commitCurrentEdit()) {
                    e.preventDefault();
                    e.stopImmediatePropagation();
                    return;
                }
				//如果
                if(!$checkbox.attr("disabled")){
                	if (_selectedRowsLookup[args.row]) {
                        _grid.setSelectedRows($.grep(_grid.getSelectedRows(),function(n) { return n != args.row }));
                    } else {
                        _grid.setSelectedRows(_grid.getSelectedRows().concat(args.row));
                    }
                }
                if (typeof _options.onRowSelect  == "function") {
                	_options.onRowSelect(args,e);
                }
                e.stopPropagation();
                e.stopImmediatePropagation();
            }
            notSelected = 0;//清除不能选择计数
        }

        function handleHeaderClick(e, args) {
        	notSelected = 0 ;
            if (args.column && args.column.id == _options.columnId) {
                // if editing, try to commit
                if (_grid.getEditorLock().isActive() && !_grid.getEditorLock().commitCurrentEdit()) {
                    e.preventDefault();
                    e.stopImmediatePropagation();
                    return;
                }

                if ($(e.target).is(":checked")) {
                    var rows = [];
                    for (var i = 0; i < _grid.getDataLength(); i++) {
                    	var rowData = _grid.getDataView().getItemByIdx(i);
                    	if (_grid.getDataView().getItemMetadata(i) != undefined) continue;
                    	if (_options.onChecked) {
		                    if (_options.onChecked(rowData))//通过canSelect传入rowdata判断是否能被选择
	                       		rows.push(i);
	                       	else notSelected ++;			//统计不能被选择的行
                    	} else rows.push(i);
                    }
                    	_grid.setSelectedRows(rows);
                }
                else {
                    _grid.setSelectedRows([]);
                    notSelected = 0 ;					//清空不能被选择的行
                }
                
                if (typeof _options.onRowSelect  == "function") {
                	_options.onRowSelect(args,e);
                }
                e.stopPropagation();
                e.stopImmediatePropagation();
            }
        }

        function getColumnDefinition() {
            return {
                id: _options.columnId,
                name: "<input type='checkbox' class='slick-checkbox-header'>",
                toolTip: _options.toolTip,
                field: "sel",
                width: _options.width,
                resizable: false,
                sortable: false,
                cssClass: _options.cssClass,
                formatter: checkboxSelectionFormatter
            };
        }
		function delete_selectedRowsLookup(row) {
			delete _selectedRowsLookup[row];
		}
        function checkboxSelectionFormatter(row, cell, value, columnDef, dataContext) {
        	var checkAble = true;
//        	_options.canSelect = function (rowData) {
//        		if (rowData["aac001"] == '14437856') return false;
//        		return true;
//        	}
        	if (_options.onChecked)  {
        		checkAble = _options.onChecked(dataContext);
        	}
        	if (!checkAble)
        		return _selectedRowsLookup[row]
        				? "<input type='checkbox' checked='checked' disabled='true'>"
                        : "<input type='checkbox' disabled='true'>";
            if (checkAble && dataContext) {
                return _selectedRowsLookup[row]
                        ? "<input type='checkbox' checked='checked'>"
                        : "<input type='checkbox'>";
            }
            return null;
        }
		function setNull() {
			_selectedRowsLookup = [];
		}
        $.extend(this, {
            "init":                         init,
            "destroy":                      destroy,
			"delete_selectedRowsLookup":	delete_selectedRowsLookup,
            "getColumnDefinition":          getColumnDefinition,
            "setNull":						setNull,
            "handleSelectedRowsChanged" :	handleSelectedRowsChanged 
        });
    }
}));