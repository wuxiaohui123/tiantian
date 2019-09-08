/**
 * Created by wuxiaohui on 2017/4/20.
 */
$.fn.ListView = function(options) {
	// ////////////////Parameter part//////////////
	var defaults = {
		data : [],
		width : 520,
		height : 520,
		itemHeight : 35,
		hasSearch : true,
		isAutoSearch : false,
		searchPlaceholder : "请输入关键字搜索",
		searchKey : "name",
		isItemIcon : true,
		itemIcon : "icon",
		itemDisplayName : "name",
		itemDisplayTemplate : "",
		itemClickBgColor : "#d5ffbf",
		isAsync : false,
		asyncUrl : "",
		paging : {
			enable : true,
			size : 20,
			showSummary : true,
			summaryTemplate : 'Displaying {startRow}-{endRow} of {totalRow} results'
		},
		itemOperation : {
			showEditBtn : false,
			showDeleteBtn : false,
			editUrl : "",
			deleteUrl : ""
		},
		itemClick : function() {
			// TODO override this function
		},
		itemDbClick : function() {
			// TODO override this function
		}
	};
	// /////////////Core part/////////////////////////
	var settings = $.extend(true, defaults, options);
	var localList = [];
	var searchModel = false;
	var _self = $(this);
	// ListView Outer container
	_self.addClass("list-view").width(settings.width).height(settings.height);
	// ListView Mask container
	$("<div class='list-mask'><div class='icon-loadding'/></div>").width(
			settings.width).height(settings.height).appendTo(_self);
	// ListView Data container
	var listContext = $("<div class='list-context'/>");
	// Parse display template
	String.prototype.applyTemplate = function(d) {
		try {
			if (d === '')
				return this;
			return this.replace(/{([^{}]*)}/g, function(a, b) {
				var r;
				if (b.indexOf('.') !== -1) { // handle dot notation in {},
					// such as {Thumbnail.Url}
					var ary = b.split('.');
					var obj = d;
					for (var i = 0; i < ary.length; i++)
						obj = obj[ary[i]];
					r = obj;
				} else
					r = d[b];
				if (typeof r === 'string' || typeof r === 'number')
					return r;
				else
					throw (a);
			});
		} catch (ex) {
			console.error('Invalid JSON property ' + ex
					+ ' found when trying to apply itemDisplayTemplate \n'
					+ 'Please check your spelling and try again.');
		}
	};
	// Array extension part (delete data object in data).
	Array.prototype.removeObj = function(obj) {
		for (var i = 0; i < this.length; i++) {
			if (obj.id === this[i].id) {
				this.splice(i, 1);
				return;
			}
		}
	};
	/** Model public method* */
	_self.ModelTemplate = {
		iconMarginTop : 4,
		calculateIconMarginTop : function() {
			this.iconMarginTop = (settings.itemHeight - 25) / 2;
		},
		bindDataByItem : function(obj, index, hasOperation, itemClick, itemDbClick) {
			if (obj == null || typeof obj === "undefined") {
				return;
			}
			var c = $("<div class='list-item'/>").height(settings.itemHeight);
			c.data(obj).data("index", index).bind("click", obj, itemClick).bind("dbclick", obj, itemDbClick);
			if (settings.isItemIcon) {
				var d = $("<span class='item-icon' style='width: 35px;height: 35px'/>");
				if (settings.itemIcon) {
					d.addClass(settings.itemIcon);
				} else {
					d.addClass(obj.icon);
				}
				c.append(d);
			}
			var e = $("<div class='item-context'/>").height(settings.itemHeight).css("line-height", settings.itemHeight + "px");
			if (settings.itemDisplayTemplate) {
				e.html(settings.itemDisplayTemplate.applyTemplate(obj));
			} else {
				e.html(obj[settings.itemDisplayName]);
			}
			c.append(e);
			if (hasOperation) {
				var operation = $("<div class='item-operation'/>").height(settings.itemHeight).css("line-height", settings.itemHeight + "px");
				if (settings.itemOperation.showEditBtn) {
					e.width(_self.FrontModel.getContextWidth(settings.isItemIcon));
					operation.append($("<div class='icon-edit' title='编辑'/>").css('margin-top', this.iconMarginTop + "px").bind("click", obj, this.dataItemEdit));
					operation.append($("<div class='icon-commit' title='提交'/>").css('margin-top', this.iconMarginTop + "px").bind("click", obj, this.dataItemCommit).hide());
					operation.append($("<div class='icon-close' title='取消'/>").css('margin-top', this.iconMarginTop + "px").bind("click", obj, this.dataItemClose).hide());
				}
				if (settings.itemOperation.showDeleteBtn) {
					operation.append($("<div class='icon-delete' title='删除'/>").css('margin-top', this.iconMarginTop + "px").bind("click", obj, this.dataItemDelete));
				}
				c.append(operation);
			}
			listContext.append(c);
		},
		showLoading : function() {
			_self.find(".list-mask").show();
		},
		hideLoading : function() {
			_self.find(".list-mask").hide();
		},
		dataItemEdit : function(e) {
			var itemContext = $(e.target).parent().prev()[0];
			var itemData = $(itemContext).parent().data();
			if (settings.itemDisplayTemplate) {
				itemContext.innerHTML = "{name}".applyTemplate(itemData);
			}
			itemContext.contentEditable = true;
			itemContext.focus();
			_self.FrontModel.getContextCursor(itemContext);
			$(e.target).parent().find(".icon-edit").hide();
			$(e.target).parent().find(".icon-delete").hide();
			$(e.target).parent().find(".icon-commit").show();
			$(e.target).parent().find(".icon-close").show();
			e.stopPropagation();
		},
		dataItemDelete : function(e) {
			if (confirm("确定删除该条数据吗？")) {
				var itemData = e.data;
				_self.ModelTemplate.obtainDataByAjax(settings.itemOperation.deleteUrl, {id : itemData.modelId}, function(res) {
							if (res.code === 200) {
								if (settings.isAsync) {// asynchronous
									_self.BehindModel.refreshDataView();
								} else {
									localList.removeObj(itemData);
									_self.FrontModel.refreshDataView();
								}
							}
						});
			}
			e.stopPropagation();
		},
		dataItemCommit : function(e) {
			var itemContext = $(e.target).parent().prev()[0];
			var itemData = $(itemContext).parent().data();
			_self.ModelTemplate.submitDataByAjax(settings.itemOperation.editUrl, {id : itemData.modelId,name : itemContext.innerHTML}, function(res) {
						res = JSON.parse(res);
						if (res.code === 200) {
							itemData.name = itemContext.innerHTML;
							$(itemContext).parent().data(itemData);
						}
						_self.ModelTemplate.dataItemClose(e);
					});
			e.stopPropagation();
		},
		dataItemClose : function(e) {
			var itemContext = $(e.target).parent().prev()[0];
			var itemData = $(itemContext).parent().data();
			if (settings.itemDisplayTemplate) {
				itemContext.innerHTML = settings.itemDisplayTemplate.applyTemplate(itemData);
			}
			itemContext.removeAttribute("contentEditable");
			itemContext.blur();
			$(e.target).parent().find(".icon-edit").show();
			$(e.target).parent().find(".icon-delete").show();
			$(e.target).parent().find(".icon-commit").hide();
			$(e.target).parent().find(".icon-close").hide();
			e.stopPropagation();
		},
		obtainDataByAjax : function(url, params, callback) {
			$.getJSON(url, params, callback);
		},
		submitDataByAjax : function(url, params, callback) {
			$.post(url, params, callback);
		}
	};
	/** Front end data model* */
	_self.FrontModel = {
		initModel : function(tempData) {
			listContext.empty();
			localList = tempData;
			this.createDataView();
		},
		updateModel : function(b, e) {
			listContext.empty();
			this.bindListData(b, e);
			var z = {
				startRow : b + 1,
				endRow : e,
				totalRow : localList.length
			};
			this.updatePageInfo(z);
		},
		updateModelBySearch : function(data) {
			listContext.empty();
			this.searchBindData(data);
			this.showOrHidePagination(false);
		},
		createDataView : function() {
			// Paging display needs to be displayed
			if (settings.paging.enable
					&& localList.length > settings.paging.size) {
				this.bindListData(0, settings.paging.size);
				listContext.height(this.getListContentHeight(true));
				_self.append(listContext);
				var z = {
					startRow : 1,
					endRow : settings.paging.size,
					totalRow : localList.length
				};
				this.createPagination(z);
			}
			// No paging display is required
			else {
				this.bindListData(0, localList.length);
				listContext.height(this.getListContentHeight(false));
				_self.append(listContext);
			}
		},
		refreshDataView : function() {
			if (settings.paging.enable) {
				var pageInfo = $(".page-info").data();
				if (pageInfo.startRow === pageInfo.totalRow) {
					this.updateModel(pageInfo.startRow - 1
							- settings.paging.size, pageInfo.endRow - 1);
				} else if (pageInfo.endRow === pageInfo.totalRow) {
					this
							.updateModel(pageInfo.startRow - 1,
									pageInfo.endRow - 1);
				} else {
					this.updateModel(pageInfo.startRow - 1, pageInfo.endRow);
				}
			} else {
				listContext.empty();
				this.bindListData(0, localList.length);
			}
		},
		bindListData : function(m, n) {
			if (m === n) {
				listContext
						.append("<div style='width: 100%;text-align: center;font-style: italic;color: #83bdbd;'>没有相匹配的数据</div>");
				return;
			}
			var i = m, hasOperation = this.hasItemOperation();
			_self.ModelTemplate.calculateIconMarginTop();
			for (i; i < n; i++) {
				var a = localList[i];
				_self.ModelTemplate.bindDataByItem(a, i, hasOperation,
						this.dataItemClick, this.dataItemDbClick);
			}
		},
		hasItemOperation : function() {
			return settings.itemOperation.showEditBtn
					|| settings.itemOperation.showDeleteBtn;
		},
		getContextWidth : function(flag) {
			if (flag) {
				return settings.width - 85 - 35;
			} else {
				return settings.width - 85;
			}
		},
		searchBindData : function(searchData) {
			if (searchData.length === 0) {
				listContext
						.append("<div style='width: 100%;text-align: center;font-style: italic;color: #83bdbd;'>没有相匹配的数据</div>");
				return;
			}
			var hasOperation = this.hasItemOperation();
			_self.ModelTemplate.calculateIconMarginTop();
			for (var i = 0; i < searchData.length; i++) {
				var a = searchData[i];
				_self.ModelTemplate.bindDataByItem(a, hasOperation,
						this.dataItemClick, this.dataItemDbClick);
			}
		},
		getListContentHeight : function(isPage) {
			var searchBoxH = settings.hasSearch ? 40 : 0;
			if (isPage) {
				return settings.height - searchBoxH - 25;
			} else {
				return settings.height - searchBoxH;
			}
		},
		createPagination : function(pinfo) {
			var p = $("<div/>").addClass("pagination"), info = $("<div/>")
					.addClass("page-info");
			var a = $("<div/>").addClass("page-item border-right").attr(
					"title", "首页").html("<div class='page-first'/>").click(
					this.firstPage);
			var b = $("<div/>").addClass("page-item border-right").attr(
					"title", "上一页").html("<div class='page-prev'/>").click(
					this.previousPage);
			$("<div/>").css("float", "left").append(a).append(b).append(
					"<div class='page-left-mask'/>").appendTo(p);
			// Setting paging display information
			if (settings.paging.showSummary) {
				this.getTotalPageAndCurrentPage(pinfo);
				info.data(pinfo).text(
						settings.paging.summaryTemplate.applyTemplate(pinfo));
			}
			p.append(info);
			var c = $("<div/>").addClass("page-item border-left").attr("title",
					"下一页").html("<div class='page-next'/>")
					.click(this.nextPage);
			var d = $("<div/>").addClass("page-item border-left").attr("title",
					"尾页").html("<div class='page-last'/>").click(this.lastPage);
			$("<div/>").css("float", "right").append(c).append(d).append(
					"<div class='page-right-mask'/>").appendTo(p);
			_self.append(p);
		},
		getTotalPageAndCurrentPage : function(pageInfo) {
			var m = pageInfo.totalRow % settings.paging.size;
			var isLastPage = (pageInfo.endRow === pageInfo.totalRow);
			if (m === 0) {
				pageInfo.totalPage = pageInfo.totalRow / settings.paging.size;
			} else {
				pageInfo.totalPage = ((pageInfo.totalRow - m) / settings.paging.size) + 1;
			}
			pageInfo.currentPage = isLastPage ? pageInfo.totalPage
					: (pageInfo.endRow / settings.paging.size);
		},
		updatePageInfo : function(d) {
			if (settings.paging.showSummary) {
				this.getTotalPageAndCurrentPage(d);
				_self.find(".page-info").data(d).text(
						settings.paging.summaryTemplate.applyTemplate(d));
			}
		},
		firstPage : function() {
			_self.FrontModel.updateModel(0, settings.paging.size);
			$(".page-right-mask").hide();
			$(".page-left-mask").show();
		},
		previousPage : function() {
			var index = listContext.children("div:first-child").data("index");
			if (index <= settings.paging.size) {
				$(".page-left-mask").show();
			}
			if (index <= localList.length) {
				$(".page-right-mask").hide();
			}
			_self.FrontModel.updateModel(index - settings.paging.size, index);
		},
		nextPage : function() {
			var index = listContext.children("div:last-child").data("index") + 1;
			if (index >= settings.paging.size) {
				$(".page-left-mask").hide();
			}
			if (index >= (localList.length - settings.paging.size)) {
				$(".page-right-mask").show();
			}
			var endRow = index + settings.paging.size;
			endRow = endRow > localList.length ? localList.length : endRow;
			_self.FrontModel.updateModel(index, endRow);
		},
		lastPage : function() {
			var m = localList.length % settings.paging.size;
			if (m === 0) {
				_self.FrontModel.updateModel(localList.length
						- settings.paging.size, localList.length);
			} else {
				_self.FrontModel.updateModel(localList.length - m,
						localList.length);
			}
			$(".page-left-mask").hide();
			$(".page-right-mask").show();
		},
		autoSearch : function(searchValue) {
			if (searchValue) {
				var resultData = [];
				for (var i = 0; i < localList.length; i++) {
					var temp = localList[i][settings.searchKey] + "";
					if (temp.indexOf(searchValue) >= 0) {
						resultData.push(localList[i]);
					}
				}
				this.updateModelBySearch(resultData);
			} else {
				this.showOrHidePagination(true);
				this.firstPage();
			}
			_self.ModelTemplate.hideLoading();
		},
		showOrHidePagination : function(flag) {
			if (flag) {
				_self.find(".pagination").show();
				listContext.height(this.getListContentHeight(flag));
			} else {
				_self.find(".pagination").hide();
				listContext.height(this.getListContentHeight(flag));
			}
		},
		getContextCursor : function(o) {
			if (document.all) {
				o.range = document.selection.createRange();
				o.range.select();
				o.range.moveStart("character", -1);
			} else {
				o.range = window.getSelection().getRangeAt(0);
				o.range.setStart(o.range.startContainer, o.innerHTML.length);
			}
		},
		dataItemClick : function(e) {
			if (e.target.contentEditable === 'inherit') {
				var item = $(e.target);
				if (item.attr("class") !== "list-item") {
					item = item.parents(".list-item");
				}
				item.siblings().css("background-color", "");
				item.css("background-color", settings.itemClickBgColor);
				settings.itemClick(e.data);
			}
		},
		dataItemDbClick : function(e) {
			settings.itemDbClick(e.data);
		}
	};
	/** Backend data model* */
	_self.BehindModel = {
		initModel : function() {
			var page = {};
			_self.ModelTemplate.showLoading();
			if (settings.paging.enable) {
				page.start = 0;
				page.end = settings.paging.size;
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, page,
						function(data) {
							_self.BehindModel.createDataView(data, true);
						});
			} else {
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, page,
						function(data) {
							_self.BehindModel.createDataView(data, false);
						});
			}
		},
		createDataView : function(data, flag) {
			this.bindListData(data);
			_self.ModelTemplate.hideLoading();
			listContext.height(this.getListContentHeight(flag));
			_self.append(listContext);
			if (flag) {
				var z = {
					startRow : 1,
					endRow : data.end,
					totalRow : data.total
				};
				this.createPagination(z);
			}
		},
		refreshDataView : function() {
			var parameter = {}, isChange = false;
			if (settings.paging.enable) {
				var pageInfo = $(".page-info").data();
				if (pageInfo.totalRow - 1 === pageInfo.startRow) {
					parameter.start = pageInfo.startRow - settings.paging.size
							- 1;
					parameter.end = pageInfo.startRow;
				} else {
					isChange = true;
					parameter.start = pageInfo.startRow - 1;
					parameter.end = pageInfo.endRow;
				}
				if (searchModel) {
					page.value = $("#searchText").val();
				}
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
						parameter, function(data) {
							if (isChange) {
								data.end = data.end - 1;
							}
							_self.BehindModel.updateModel(data, true);
						});
			} else {
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
						parameter, function(data) {
							_self.BehindModel.updateModel(data, false);
						});
			}
		},
		updateModel : function(data, flag) {
			listContext.empty();
			this.bindListData(data);
			_self.ModelTemplate.hideLoading();
			if (flag) {
				var z = {
					startRow : data.start + 1,
					endRow : data.end,
					totalRow : data.total
				};
				this.updatePageInfo(z);
			}
		},
		bindListData : function(data) {
			if (typeof data === "undefined" || data.totalRow === 0
					|| data.list.length === 0) {
				listContext
						.append("<div style='width: 100%;text-align: center;font-style: italic;color: #83bdbd;'>没有相匹配的数据</div>");
				this.showOrHidePagination(false);
				return;
			}
			var hasOperation = this.hasItemOperation();
			_self.ModelTemplate.calculateIconMarginTop();
			for (var i = 0; i < data.list.length; i++) {
				var index = data.start + i;
				_self.ModelTemplate.bindDataByItem(data.list[i], index,
						hasOperation, this.dataItemClick, this.dataItemDbClick);
			}
		},
		hasItemOperation : function() {
			return settings.itemOperation.showEditBtn
					|| settings.itemOperation.showDeleteBtn;
		},
		getListContentHeight : function(isPage) {
			var searchBoxH = settings.hasSearch ? 40 : 0;
			if (isPage) {
				return _self.height() - searchBoxH - 25;
			} else {
				return _self.height() - searchBoxH;
			}
		},
		createPagination : function(pinfo) {
			var p = $("<div/>").addClass("pagination"), info = $("<div/>")
					.addClass("page-info").data(pinfo);
			var a = $("<div/>").addClass("page-item border-right").attr(
					"title", "首页").html("<div class='page-first'/>").click(
					this.firstPage);
			var b = $("<div/>").addClass("page-item border-right").attr(
					"title", "上一页").html("<div class='page-prev'/>").click(
					this.previousPage);
			$("<div/>").css("float", "left").append(a).append(b).append(
					"<div class='page-left-mask'/>").appendTo(p);
			// Setting paging display information
			if (settings.paging.showSummary) {
				this.getTotalPageAndCurrentPage(pinfo);
				info.text(settings.paging.summaryTemplate.applyTemplate(pinfo));
			}
			p.append(info);
			var c = $("<div/>").addClass("page-item border-left").attr("title",
					"下一页").html("<div class='page-next'/>")
					.click(this.nextPage);
			var d = $("<div/>").addClass("page-item border-left").attr("title",
					"尾页").html("<div class='page-last'/>").click(this.lastPage);

			$("<div/>").css("float", "right").append(c).append(d).append(
					"<div class='page-right-mask'/>").appendTo(p);
			_self.append(p);
		},
		getTotalPageAndCurrentPage : function(pageInfo) {
			var m = pageInfo.totalRow % settings.paging.size;
			var isLastPage = (pageInfo.endRow === pageInfo.totalRow);
			if (m === 0) {
				pageInfo.totalPage = pageInfo.totalRow / settings.paging.size;
			} else {
				pageInfo.totalPage = ((pageInfo.totalRow - m) / settings.paging.size) + 1;
			}
			var n = pageInfo.endRow % settings.paging.size;
			pageInfo.currentPage = isLastPage ? pageInfo.totalPage
					: ((n === 0) ? pageInfo.endRow / settings.paging.size
							: (pageInfo.endRow + 1) / settings.paging.size);
		},
		updatePageInfo : function(d) {
			if (settings.paging.showSummary) {
				this.getTotalPageAndCurrentPage(d);
				_self.find(".page-info").data(d).html(
						settings.paging.summaryTemplate.applyTemplate(d));
			}
		},
		firstPage : function() {
			var page = {};
			page.start = 0;
			page.end = settings.paging.size;
			if (searchModel) {
				page.value = $("#searchText").val();
			}
			_self.ModelTemplate.showLoading();
			_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, page,
					function(data) {
						_self.BehindModel.updateModel(data, true);
					});
			$(".page-right-mask").hide();
			$(".page-left-mask").show();
		},
		previousPage : function() {
			var total = $(".page-info").data().totalRow;
			var index = listContext.children("div:first-child").data("index");
			if (index <= settings.paging.size) {
				$(".page-left-mask").show();
			}
			if (index <= total) {
				$(".page-right-mask").hide();
			}
			var parameter = {};
			parameter.start = index - settings.paging.size;
			parameter.end = index;
			if (searchModel) {
				parameter.value = $("#searchText").val();
			}
			_self.ModelTemplate.showLoading();
			_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, parameter,
					function(data) {
						_self.BehindModel.updateModel(data, true);
					});
		},
		nextPage : function(e) {
			var total = $(".page-info").data().totalRow;
			var index = listContext.children("div:last-child").data("index") + 1;
			if (index >= settings.paging.size) {
				$(".page-left-mask").hide();
			}
			if (index >= (total - settings.paging.size)) {
				$(".page-right-mask").show();
			}
			var parameter = {};
			parameter.start = index;
			var endRow = index + settings.paging.size;
			parameter.end = (endRow > total ? total : endRow);
			if (searchModel) {
				parameter.value = $("#searchText").val();
			}
			_self.ModelTemplate.showLoading();
			_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, parameter,
					function(data) {
						_self.BehindModel.updateModel(data, true);
					});
		},
		lastPage : function() {
			var total = $(".page-info").data().totalRow;
			var m = total % settings.paging.size;
			var parameter = {};
			if (m === 0) {
				// The reason for subtracting 1 is that the PageInfo is set up
				// by 1.
				parameter.start = total - settings.paging.size - 1;
				parameter.end = total;
				if (searchModel) {
					parameter.value = $("#searchText").val();
				}
				_self.ModelTemplate.showLoading();
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
						parameter, function(data) {
							_self.BehindModel.updateModel(data, true);
						});
			} else {
				parameter.start = total - m - 1;
				parameter.end = total;
				if (searchModel) {
					parameter.value = $("#searchText").val();
				}
				_self.ModelTemplate.showLoading();
				_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
						parameter, function(data) {
							_self.BehindModel.updateModel(data, true);
						});
			}
			$(".page-left-mask").hide();
			$(".page-right-mask").show();
		},
		showOrHidePagination : function(flag) {
			if (flag) {
				_self.find(".pagination").show();
				listContext.height(this.getListContentHeight(flag));
			} else {
				_self.find(".pagination").hide();
				listContext.height(this.getListContentHeight(flag));
			}
		},
		autoSearch : function(searchValue) {
			if (searchValue) {
				searchModel = true;
				var parameter = {};
				if (settings.paging.enable) {
					parameter.start = 0;
					parameter.end = settings.paging.size;
					parameter.value = searchValue;
					_self.ModelTemplate.showLoading();
					_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
							parameter, function(data) {
								_self.BehindModel.updateModel(data, true);
							});
				} else {
					parameter.value = searchValue;
					_self.ModelTemplate.showLoading();
					_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl,
							parameter, function(data) {
								_self.BehindModel.updateModel(data, false);
							});
				}
			} else {
				searchModel = false;
				this.showOrHidePagination(settings.paging.enable);
				if (settings.paging.enable) {
					this.firstPage();
				} else {
					_self.ModelTemplate.showLoading();
					_self.ModelTemplate.obtainDataByAjax(settings.asyncUrl, {},
							function(data) {
								_self.BehindModel.updateModel(data, false);
							});
				}
			}
		},
		dataItemClick : function(e) {
			if (e.target.contentEditable === 'inherit') {
				var item = $(e.target);
				if (item.attr("class") !== "list-item") {
					item = item.parents(".list-item");
				}
				item.siblings().css("background-color", "");
				item.css("background-color", settings.itemClickBgColor);
				settings.itemClick(e.data);
			}
		},
		dataItemDbClick : function(e) {
			settings.itemDbClick(e.data);
		}
	};
	/** * Initialization* */
	_self.initListView = function() {
		// ListView search part initialization
		if (settings.hasSearch) {
			var sTextW = settings.isAutoSearch ? _self.width() - 15 : _self
					.width() - 106 - 24;
			var html = "<div class='search-text' style='width: " + sTextW
					+ "px'>"
					+ "<input id='searchText' type='text' placeholder='"
					+ settings.searchPlaceholder
					+ "' style='width: 96%'/></div> ";
			if (!settings.isAutoSearch) {
				html += "<div class='search-btn' style='width: 106px'><button id='searchBtn'><span>搜索</span></button></div>";
			}
			_self.append($("<div class='search-box'/>").html(html));
			if (settings.isAutoSearch) {
				$("#searchText").bind("input propertychange", function() {
					_self.ModelTemplate.showLoading();
					if (settings.isAsync) {
						_self.BehindModel.autoSearch(this.value);
					} else {
						_self.FrontModel.autoSearch(this.value);
					}
				});
			} else {
				$("#searchBtn").click(function() {
					var searchValue = $("#searchText").val();
					_self.ModelTemplate.showLoading();
					setTimeout(function() {
						if (settings.isAsync) {
							_self.BehindModel.autoSearch(searchValue);
						} else {
							_self.FrontModel.autoSearch(searchValue);
						}
					}, 1);
				});
			}
		}
		// Data model initialization
		if (settings.isAsync) {// asynchronous
			_self.BehindModel.initModel();
		} else {// synchronization
			_self.FrontModel.initModel(settings.data);
		}
	};
	// Execution initialization
	_self.initListView();

	// /////////////////API part////////////////////
	_self.API = {
		setListViewDataByFrontModel : function(data) {
			if (!settings.isAsync) {
				_self.FrontModel.initModel(data);
			}
		},
		refreshDataViewByModel : function() {
			if (settings.isAsync) {
				_self.BehindModel.refreshDataView();
			} else {
				_self.FrontModel.refreshDataView();
			}
		}
	};

	// External API
	$.fn.listView = function(options, param) {
		if (typeof options === "string") {
			switch (options) {
			case 'setListData':
				_self.API.setListViewDataByFrontModel(param);
				break;
			case 'refreshDataView':
				_self.API.refreshDataViewByModel();
				break;
			default:
				return;
			}
		}
	};
};