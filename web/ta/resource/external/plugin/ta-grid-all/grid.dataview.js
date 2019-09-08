/**
 * 表格视图
 * @module Grid
 * @namespace Slick.Data
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
    $.extend(true, window, {
        Slick: {
            Data: {
                DataView: DataView,
                Aggregators: {
                    Avg: AvgAggregator, //平均
                    Min: MinAggregator, //最小
                    Max: MaxAggregator,  //最大
                    Sum: SumAggregator
                }
            }
        }
    });


    /***
     * A sample Model implementation.
     * Provides a filtered view of the underlying data.
     *
     * Relies on the data item having an "id" property uniquely identifying it.
     */
    /**
	  * 表格视图
	 * @class DataView
	 * @static
	 * @constructor
	 * @param {Object} options 
	 * @param {Object} grid
	 */
    function DataView(options, grid) {
        var self = this;
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
        var defaults = {
            groupItemMetadataProvider: groupItemMetadataProvider,
            //TODO 林森
            url : null
        };

        
        // private
        var idProperty = "__id___";  // property holding a unique row id
        var items = [];			// data by index
        var rows = [];			// data by row
        var idxById = {};		// indexes by id
        var rowsById = null;	// rows by id; lazy-calculated
        var filter = null;		// filter function
        var updated = null; 	// updated item ids
        var suspend = false;	// suspends the recalculation
        var sortAsc = true;
        var fastSortField;
        var sortComparer;

        // grouping
        var groupingGetter;
        var groupingGetterIsAFn;
        var groupingFormatter;
        var groupingComparer;
        var groups = [];
        var collapsedGroups = {};
        var aggregators;
        var aggregateCollapsed = false;

        var pagesize = 0;
        var pagenum = 0;
        var totalRows = 0;
        
        var total = 0;

        // events
        var onRowCountChanged = new Slick.Event();
        var onRowsChanged = new Slick.Event();
        var onPagingInfoChanged = new Slick.Event();

        options = $.extend(true, {}, defaults, options);
        //分组
		grid.registerPlugin(groupItemMetadataProvider);
		/**
		 * 设置暂停位为true
		 */
        function beginUpdate() {
            suspend = true;
        }
		/**
		 * 设置暂停位为false 并刷新
		 */
        function endUpdate(hints) {
            suspend = false;
            refresh(hints);
        }
		/**
		 * 检测数据项是否有id，并将id保存在idxById 中
		 * @param {number} startingIndex 起始Index
		 */
        function updateIdxById(startingIndex) {
            startingIndex = startingIndex || 0;
            var id;
            for (var i = startingIndex, l = items.length; i < l; i++) {
                id = items[i][idProperty];
                if (id === undefined) {
                    throw "Each data element must implement a unique 'id' property";
                }
                idxById[id] = i;
            }
        }
		/**
		 * 确保id是独一无二的
		 */
        function ensureIdUniqueness() {
            var id;
            for (var i = 0, l = items.length; i < l; i++) {
                id = items[i][idProperty];
                if (id === undefined || idxById[id] !== i) {
                    throw "Each data element must implement a unique 'id' property";
                }
            }
        }
		/**
		 * 得到data
		 * @return {array} items
		 */
        function getItems() {
            return items;
        }
		/**
		 * 设置dataview数据
		 * @param {array} data 传人数据
		 * @param {string} objectIdProperty 指定数据id项名称
		 */
        function setItems(data, objectIdProperty) {
            if (objectIdProperty !== undefined) idProperty = objectIdProperty;
            var tmp = null;
            if(!jQuery.isArray(data)){
            	tmp = data.list || [];
            	total = data.total || 0;
            	//如果分页传分页信息
            	var pager = grid.getPager();
            	if (pager) {
            		pager.setStatus(total);
            	}
            } else {
            	//grid.clearDirty();
            	tmp = data;
            }
            for(var i=0;i<tmp.length;i++){
            	if (!tmp[i].__group) 
            		tmp[i][idProperty] = i;
            }
            items = tmp;
            
            idxById = {};	//设置idxById 为空
            updateIdxById(); //设置idxById，检测id
            ensureIdUniqueness(); //检查id的唯一性
            refresh();			//刷新数据
             //分页:默认选择行,liys添加
            if(typeof grid.getOptions().defaultRows == "function"){//defaultRows为function
	        	var str =  grid.getOptions().defaultRows();
	        	grid.setCheckedRows(str);
	        } else if( grid.getOptions().defaultRows != undefined &&  grid.getOptions().defaultRows.length > 0){//defaultRows为json数组
	        	grid.setCheckedRows( grid.getOptions().defaultRows);
	        }
        }

        function setPagingOptions(args) {
            if (args.pageSize != undefined)
                pagesize = args.pageSize;

            if (args.pageNum != undefined)
                pagenum = Math.min(args.pageNum, Math.ceil(totalRows / pagesize));

            onPagingInfoChanged.notify(getPagingInfo(), null, self);

            refresh();
        }

        function getPagingInfo() {
            return {pageSize:pagesize, pageNum:pagenum, totalRows:totalRows};
        }

        function sort(comparer, ascending) {
            sortAsc = ascending;
            sortComparer = comparer;
            fastSortField = null;
            if (ascending === false) items.reverse();
            items.sort(comparer);
            if (ascending === false) items.reverse();
            for (var i = 0; i < items.length; i ++) {
            	items[i][idProperty] = i;
            }
            idxById = {};
            updateIdxById();
            refresh();
        }

        /***
        * Provides a workaround for the extremely slow sorting in IE.
        * Does a [lexicographic] sort on a give column by temporarily overriding Object.prototype.toString
        * to return the value of that field and then doing a native Array.sort().
        */
        function fastSort(field, ascending) {
            sortAsc = ascending;
            fastSortField = field;
            sortComparer = null;
            var oldToString = Object.prototype.toString;
            Object.prototype.toString = (typeof field == "function")?field:function() { return this[field] };
            // an extra reversal for descending sort keeps the sort stable
            // (assuming a stable native sort implementation, which isn't true in some cases)
            if (ascending === false) items.reverse();
            items.sort();
            Object.prototype.toString = oldToString;
            if (ascending === false) items.reverse();
            idxById = {};
            updateIdxById();
            refresh();
        }

        function reSort() {
            if (sortComparer) {
               sort(sortComparer, sortAsc);
            }
            else if (fastSortField) {
               fastSort(fastSortField, sortAsc);
            }
        }

        function setFilter(filterFn) {
            filter = filterFn;
            refresh();
        }

        function groupBy(valueGetter, valueFormatter, sortComparer) {
            if (!options.groupItemMetadataProvider) {
                options.groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
            }
            groupingGetter = valueGetter;
            groupingGetterIsAFn = typeof groupingGetter === "function";
            groupingFormatter = valueFormatter;
            groupingComparer = sortComparer;
            collapsedGroups = {};
            groups = [];
            refresh();
        }

        function setAggregators(groupAggregators, includeCollapsed) {
            aggregators = groupAggregators;
            aggregateCollapsed = includeCollapsed !== undefined ? includeCollapsed : aggregateCollapsed;
            refresh();
        }

        function getItemByIdx(i) {
            return items[i];
        }

        function getIdxById(id) {
            return idxById[id];
        }

        // calculate the lookup table on first call
        function getRowById(id) {
            if (!rowsById) {
                rowsById = {};
                for (var i = 0, l = rows.length; i < l; ++i) {
                    rowsById[rows[i][idProperty]] = i;
                }
            }

            return rowsById[id];
        }
		/**
		 * 通过id查找item
		 */
        function getItemById(id) {
            return items[idxById[id]];
        }

        function updateItem(id, item) {
            if (idxById[id] === undefined || id !== item[idProperty])
                throw "Invalid or non-matching id";
            items[idxById[id]] = item;
            if (!updated) updated = {};
            updated[id] = true;
            refresh();
        }

        function insertItem(insertBefore, item) {
            items.splice(insertBefore, 0, item);
            updateIdxById(insertBefore);
            refresh();
        }

        function addItem(item) {
            items.push(item);
            updateIdxById(items.length - 1);
            refresh();
        }

        function deleteItem(id) {
            var idx = idxById[id];
            if (idx === undefined) {
                throw "Invalid id";
            }
            delete idxById[id];
            items.splice(idx, 1);
            updateIdxById(idx);
            refresh();
        }
        function getLength() {
            return rows.length;
        }

        function getItem(i) {
        	var row = rows[i];
            return row;
        }
		/**
		 * 得到meta数据，在判断是否被选中处调用
		 */
        function getItemMetadata(i) {
            var item = rows[i];
            if (item === undefined) {
                return null;
            }

            // overrides for group rows
            if (item.__group) {
                return options.groupItemMetadataProvider.getGroupRowMetadata(item);
            }

            // overrides for totals rows
            if (item.__groupTotals) {
                return options.groupItemMetadataProvider.getTotalsRowMetadata(item);
            }
            //关于第一行选中问题注释，问题原因：当第一行选中是，select = false的也被选中颜色
//			if (i === 0) {
//				return {
//	                selectable: true,
//	                //focusable: options.totalsFocusable,
//	                cssClasses: "cell",
//	                //formatter: defaultTotalsCellFormatter,
//	                editor: null
//            	}
//			}
            return null;
        }

        function collapseGroup(groupingValue) {
            collapsedGroups[groupingValue] = true;
            refresh();
        }

        function expandGroup(groupingValue) {
            delete collapsedGroups[groupingValue];
            refresh();
        }

        function getGroups() {
            return groups;
        }

        function extractGroups(rows) {
            var group;
            var val;
            var groups = [];
            var groupsByVal = {};
            var r;
			
            for (var i = 0, l = rows.length; i < l; i++) {
                r = rows[i];
                val = (groupingGetterIsAFn) ? groupingGetter(r) : r[groupingGetter];
                group = groupsByVal[val];
                if (!group) {
                    group = new Slick.Group();
                    group.count = 0;
                    group.value = val;
                    group.rows = [];
                    groups[groups.length] = group;
                    groupsByVal[val] = group;
                }
                group.rows[group.count++] = r;
            }

            return groups;
        }

        // TODO:  lazy totals calculation
        function calculateGroupTotals(group) {
            var r, idx;

            if (group.collapsed && !aggregateCollapsed) {
                return;
            }

            idx = aggregators.length;
            while (idx--) {
                aggregators[idx].init();
            }

            for (var j = 0, jj = group.rows.length; j < jj; j++) {
                r = group.rows[j];
                idx = aggregators.length;
                while (idx--) {
                    aggregators[idx].accumulate(r);
                }
            }
            var t = new Slick.GroupTotals();
            idx = aggregators.length;
            while (idx--) {
                aggregators[idx].storeResult(t);
            }
            t.group = group;
            group.totals = t;
        }

        function calculateTotals(groups) {
            var idx = groups.length;
            while (idx--) {
                calculateGroupTotals(groups[idx]);
            }
        }
		//groups里面存放分组信息
        function finalizeGroups(groups) {
            var idx = groups.length, g;
            while (idx--) {
                g = groups[idx];
                g.collapsed = (g.value in collapsedGroups);
                g.title = groupingFormatter ? groupingFormatter(g) : g.value;
            }
        }

        function flattenGroupedRows(groups) {
            var groupedRows = [], gl = 0, idx, t, g, r;
            for (var i = 0, l = groups.length; i < l; i++) {
                g = groups[i];
                if(grid.getOptions().groupingBy !== "_onlyTotals")
                	groupedRows[gl++] = g;
                if (!g.collapsed) {
                    for (var j = 0, jj = g.rows.length; j < jj; j++) {
                        groupedRows[gl++] = g.rows[j];
                    }
                }

                if (g.totals && (!g.collapsed || aggregateCollapsed)) {
                    groupedRows[gl++] = g.totals;
                }
            }
            return groupedRows;
        }

        function getFilteredAndPagedItems(items, filter) {
            var pageStartRow = pagesize * pagenum;
            var pageEndRow = pageStartRow + pagesize;//分页信息
            var itemIdx = 0, rowIdx = 0, item;
            var newRows = [];

            // filter the data and get the current page if paging
            if (filter) {
                for (var i = 0, il = items.length; i < il; ++i) {
                    item = items[i];

                    if (!filter || filter(item)) {
                        if (!pagesize || (itemIdx >= pageStartRow && itemIdx < pageEndRow)) {
                            newRows[rowIdx] = item;
                            rowIdx ++;
                        }
                        itemIdx++;
                    }
                }
            }
            else {
                newRows = pagesize ? items.slice(pageStartRow, pageEndRow) : items.concat();
                itemIdx = items.length;
            }

            return {totalRows:itemIdx, rows:newRows};
        }

        function getRowDiffs(rows, newRows) {
            var item, r, eitherIsNonData, diff = [];
            for (var i = 0, rl = rows.length, nrl = newRows.length; i < nrl; i++) {
                if (i >= rl) {
                    diff[diff.length] = i;
                }
                else {
                    item = newRows[i];
                    r = rows[i];

                    if ((groupingGetter && (eitherIsNonData = (item.__nonDataRow) || (r.__nonDataRow)) &&
                            item.__group !== r.__group ||
                            item.__updated ||
                            item.__group && !item.equals(r))
                        || (aggregators && eitherIsNonData &&
                            // no good way to compare totals since they are arbitrary DTOs
                            // deep object comparison is pretty expensive
                            // always considering them 'dirty' seems easier for the time being
                            (item.__groupTotals || r.__groupTotals))
                        || item[idProperty] != r[idProperty]
                        || (updated && updated[item[idProperty]])
                        ) {
                        diff[diff.length] = i;
                    }
                }
            }
            return diff;
        }
		/**
		 * 从新计算
		 */
        function recalc(_items, _rows, _filter) {
            rowsById = null;

            var newRows = [];

            var filteredItems = getFilteredAndPagedItems(_items, _filter);
           // grid.trigger(grid.onSelectedRowsChanged, {rows:grid.getSelectedRows(), data:grid.getSelectRowsDataToObj()}, null);
            //通过data获取总量及row
            totalRows = filteredItems.totalRows;
            newRows = filteredItems.rows;
            groups = [];
            if (groupingGetter != null) {
                groups = extractGroups(newRows);
                if (groups.length) {
                    finalizeGroups(groups);
                    if (aggregators) {
                        calculateTotals(groups);
                    }
                    groups.sort(groupingComparer);
                    newRows = flattenGroupedRows(groups);
                }
            }
            var diff = getRowDiffs(_rows, newRows);

            rows = newRows;

            return diff;
        }

        function refresh(isCurrentPage) {
            if (suspend) return; //判断是否为暂停状态
			//初始都为0
            var countBefore = rows.length;
            var totalRowsBefore = totalRows;
			//第一次 rows空，filter空
            var diff = recalc(items, rows, filter); // pass as direct refs to avoid closure perf hit

            // if the current page is no longer valid, go to last page and recalc
            // we suffer a performance penalty here, but the main loop (recalc) remains highly optimized
            if (pagesize && totalRows < pagenum * pagesize) {
                pagenum = Math.floor(totalRows / pagesize);
                diff = recalc(items, rows, filter);
            }

            updated = null;

            if (totalRowsBefore != totalRows) onPagingInfoChanged.notify(getPagingInfo(), null, self);
            if (countBefore != rows.length) onRowCountChanged.notify({previous:countBefore, current:rows.length}, null, self);
            if (diff.length > 0) onRowsChanged.notify({rows:diff}, null, self);
          	if (!isCurrentPage)
            	grid.scrollRowIntoView(0);
        }

		function getTotal() {
			return total;
		}
        return {
            // methods
            "beginUpdate":      beginUpdate,
            "endUpdate":        endUpdate,
            "setPagingOptions": setPagingOptions,
            "getPagingInfo":    getPagingInfo,
            "getItems":         getItems,
            "setItems":         setItems,
            "setFilter":        setFilter,
            "sort":             sort,
            "fastSort":         fastSort,
            "reSort":           reSort,
            "groupBy":          groupBy,
            "setAggregators":   setAggregators,
            "collapseGroup":    collapseGroup,
            "expandGroup":      expandGroup,
            "getGroups":        getGroups,
            "getIdxById":       getIdxById,
            "getRowById":       getRowById,
            "getItemById":      getItemById,
            "getItemByIdx":     getItemByIdx,
            "refresh":          refresh,
            "updateItem":       updateItem,
            "insertItem":       insertItem,
            "addItem":          addItem,
            "deleteItem":       deleteItem,
            
            "updateIdxById":      updateIdxById, //设置idxById，检测id
            "ensureIdUniqueness": ensureIdUniqueness,

            // data provider methods
            "getLength":        getLength,
            "getItem":          getItem,
            "getItemMetadata":  getItemMetadata,

            // events
            "onRowCountChanged":    onRowCountChanged,
            "onRowsChanged":        onRowsChanged,
            "onPagingInfoChanged":  onPagingInfoChanged,
            "getTotal":				getTotal
        };
    }




    function AvgAggregator(field,totalsFormatter) {
        var count;
        var nonNullCount;
        var sum;

        this.init = function() {
            count = 0;
            nonNullCount = 0;
            sum = 0;
        };

        this.accumulate = function(item) {
            var val = item[field];
            count++;
            if (val != null && val != NaN) {
                nonNullCount++;
                sum += 1 * val;
            }
        };

        this.storeResult = function(groupTotals) {
            if (!groupTotals.avg) {
                groupTotals.avg = {};
            }
            if (nonNullCount != 0) {
            	if(totalsFormatter){
            		 groupTotals.avg[field] = totalsFormatter(Number(sum / nonNullCount).toFixed(5));
            	}else{
	                groupTotals.avg[field] = formatterTotals(Number(sum / nonNullCount).toFixed(5));
	            }
	        }
        };
    }

    function MinAggregator(field,totalsFormatter) {
        var min;

        this.init = function() {
            min = null;
        };

        this.accumulate = function(item) {
            var val = item[field];
            if (val != null && val != NaN) {
                if (min == null ||val < min) {
                    min = val;
                }
            }
        };

        this.storeResult = function(groupTotals) {
            if (!groupTotals.min) {
                groupTotals.min = {};
            }
            if(totalsFormatter){
            	 groupTotals.min[field] = totalsFormatter(min);
            }else{
            	groupTotals.min[field] = formatterTotals(min);
            }
        }
    }
    
	function SumAggregator(field,totalsFormatter) {
		var sum;

        this.init = function() {
            sum = 0;
        };

        this.accumulate = function(item) {
            var val = item[field];
            if (val != null && val != NaN) {
                   // sum += Number(val);
                    sum  = Ta.util.floatAdd(sum, Number(val))
            }
        };

        this.storeResult = function(groupTotals) {
            if (!groupTotals.sum) {
                groupTotals.sum = {};
            }
            if(totalsFormatter){
            	groupTotals.sum[field] = totalsFormatter(sum);
            }else
            	groupTotals.sum[field] = formatterTotals(sum);
        };
	}
	
    function MaxAggregator(field,totalsFormatter) {
        var max;

        this.init = function() {
            max = null;
        };

        this.accumulate = function(item) {
            var val = item[field];
            if (val != null && val != NaN) {
                if (max == null ||val > max) {
                    max = val;
                }
            }
        };

        this.storeResult = function(groupTotals) {
            if (!groupTotals.max) {
                groupTotals.max = {};
            }
            if(totalsFormatter){
            	groupTotals.max[field] = totalsFormatter(max);
            }else{
            	groupTotals.max[field] = formatterTotals(max);
            }
        };
    }
    //格式化统计信息
	function formatterTotals(value){
		var moneyValue = String(value);
            if(value !== "" && value != undefined){
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
            }
            return moneyValue;
	}
    // TODO:  add more built-in aggregators
    // TODO:  merge common aggregators in one to prevent needles iterating

}));