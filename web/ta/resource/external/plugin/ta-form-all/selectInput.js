/**
 * 下拉框
 * @module flexbox
 */
/**
 * @class flexbox
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","api.forms"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.flexbox = function(div, o) {
		this.cmptype = "flexbox";
		var first = true;
		var timeout = false, // hold timeout ID for suggestion results to
								// appear
		cache = [], // simple array with cacheData key values, MRU is the first
					// element
		cacheData = [], // associative array holding actual cached data
		cacheSize = 0, // size of cache in bytes (cache up to o.maxCacheBytes
						// bytes)
		delim = '\u25CA', // use an obscure unicode character (lozenge) as the
							// cache key delimiter
		scrolling = false, pageSize = o.paging && o.paging.pageSize ? o.paging.pageSize
				: 0, retrievingRemoteData = false, defaultVisibleRows = o.maxVisibleRows > 0 ? o.maxVisibleRows
				: 1, // 李从波修改，预定义的显示的行数，如果用户未指定可视行数，则为默认值
		$div = $(div).css('position', 'relative').css('z-index', 0);
		var arrowClick;
		var $arrow, $refreshCode;
		var disables = [];// @author lins
		var $hdn = $('<input type="hidden"/>').val(o.initialValue).appendTo(
				$div);// TODO 林森修改name，id
		//如果允许 输入其他值,那就不 验证防篡改;
		if(!o.allowInputOtherText){
			var $md5list = $('<input type="hidden"/>').val(o.md5list).appendTo($div);
		}
		
		// if (o.initialValue && o.initialValue != "")
		// else $hdn.val("0");
		// alert(o.initialValue)
		var $input = $('<input autocomplete="off"/>');
		if(Base.globvar.indexStyle == "default"){
			$input.addClass(o.inputClass).addClass("ffb-input_163")
		}else{
			$input.addClass(o.inputClass)
		}
		$input.css('width', o.width).appendTo($div).click(function(e) {
					if (o.watermark !== '' && this.value === o.watermark)
						this.value = '';
					else
						arrowClick();
					// this.select();
				}).change(function() {
					if (typeof o.onChange == "function") {
						o.onChange($input.val(), $hdn.val());
					}
				}).focus(function() {
					if (typeof o.onFocus == "function") {
						o.onFocus($input.val(), $hdn.val());
					}
				}).blur(function(e) {
					if (this.value === '')
						$hdn.val('');
					if (!o.allowInputOtherText)
						clearInput(this.value); // @author lins

					else {// 将值赋值给隐藏框,以便后台获取.liys 20130806
						for ( var i = 0; i < o.source.results.length; i++) {
							if (this.value == o.source.results[i].name) {
								break;
							}
							if (i == o.source.results.length - 1) {// 没有在collection中的将值赋值给隐藏框
								$hdn.val(this.value);
							}
						}
					}
					// setTimeout(function() { if (!$input.data('active'))
					// hideResults(); }, 200);
				}).keydown(processKeyDown);
		// TODO lins 添加属性
		if (o.submitDesc && o.name !== "") {
			var desc = o.name.replace("']", "");
			desc += "_desc']";
			$input.attr('name', desc);
		}
		if (o.name !== "") {
			$hdn.attr('name', o.name); // "dto['" + $div.attr('id') + "']"
			if(!o.allowInputOtherText){
				var desc = o.name.replace("']", "");
				desc += "_md5list']";
				$md5list.attr('name', desc);
			}
		}
		if (o.divId !== "") {
			$hdn.attr('id', o.divId);
			$input.attr('id', o.divId + '_desc');
			if(o.placeholder != null && o.placeholder != ""){
				$input.attr('placeholder',o.placeholder);
				Base.funPlaceholder(document.getElementById(o.divId + '_desc'));
			}
			if(!o.allowInputOtherText){
				$md5list.attr('id', o.divId + '_md5list');
			}
		}
		if (o.required) {
			$input.attr('required', 'true');
		}
		if (o.readonly && o.readonly == "true") {
			o.readonly = true;
		} else {
			o.readonly = false;
		}
		// ******************************
		if (o.initialValue !== '') {
			$input.val(o.initialValue).removeClass('watermark');
		} else {
			if (o.selectFirstValue) {
				$input.val(o.source.results[0][o.displayValue]);
				$hdn.val(o.source.results[0][o.hiddenValue]);
			} else {
				$input.val(o.watermark).addClass('watermark');
			}
		}
		var arrowWidth = 0;
		var refreshcodeWidth = 0;// 孙吉add
		if (o.showArrow && o.showResults) {
			arrowClick = function() {
				if ($ctr.is(':visible')) {
					hideResults();
				} else {
					// $input.focus();
					if (o.watermark !== '' && $input.val() === o.watermark)
						$input.val('');
					else
						$input.select();
					if (timeout)
						clearTimeout(timeout);
					timeout = setTimeout(function() {
						flexbox(1, true, o.arrowQuery);
					}, o.queryDelay);
				}
				first = false;
			};
			$arrow = $('<span></span>').attr('id', $div.attr('id') + '_arrow');
			if(Base.globvar.indexStyle == "default"){
				$arrow.addClass(o.arrowClass).addClass("ffb-arrow_163");
			}else{
				$arrow.addClass(o.arrowClass)
			}
			$arrow.addClass('out').hover(function() {
						$(this).removeClass('out').addClass('over');
					}, function() {
						$(this).removeClass('over').addClass('out');
					}).mousedown(function() {
						$(this).removeClass('over').addClass('active');
					}).mouseup(function() {
						$(this).removeClass('active').addClass('over');
					}).click(arrowClick).appendTo($div);
			arrowWidth = $arrow.width();
			$input.css('width', '100%');
			// 孙吉add 下拉框刷新按钮样式
			if (o.showRefresh == "true") {
				$refreshCode = $('<span class="ffb-refreshcode out">').attr(
						'id', $div.attr('id') + '_refreshCode').hover(
						function() {
							$(this).removeClass('out').addClass('over');
						}, function() {
							$(this).removeClass('over').addClass('out');
						}).mousedown(function() {
					$(this).removeClass('over').addClass('active');
				}).mouseup(function() {
					$(this).removeClass('active').addClass('over');
				}).click(arrowClick).appendTo($div);
				refreshcodeWidth = $refreshCode.width();
			} else {
				$arrow.css('right', -1);
				// $refreshCode.hide();
			}
			// 孙吉add end
		}
		// $input.focus(function(e) {
		// $(this).removeClass('watermark');
		// if(first)arrowClick();
		// })
		if (!o.isFocusShowPanel) {
			$input.unbind("focus");
		}
		if (!o.allowInput) {
			o.selectFirstMatch = false;
			$input.click(arrowClick);
		} // simulate <select> behavior
		var inputPad = $input.outerHeight(true) - $input.height() - 2;
		var inputWidth = $input.outerWidth(true) - 2;
		var top = 21;// ;$input.outerHeight() > 25 ? 25
						// :$input.outerHeight();

		// if (inputPad === 0) {
		// inputWidth += 4;
		// top += 4;
		// }
		// else if (inputPad !== 4) {
		// inputWidth += inputPad;
		// top += inputPad;
		// }

		var $ctr = $('<div></div>').attr('id', $div.attr('id') + '_ctr').css(
				'width', "100%"); // lins 原来为inputWidth
		if(Base.globvar.indexStyle == "default"){
			$ctr.css('left','-9px');
		}else{
			$ctr.css('left','0px');
		}
		$ctr.css('top', top).css('position', 'absolute')// 后加
		.addClass(o.containerClass).appendTo($div).mousedown(function(e) {
			// $input.data('active', true);
		}).hide();
		// 下拉框添加自定义长度
		if (o.widthPercentage) {
			$ctr.css('width', o.widthPercentage);
		}
		var $content = $(
				'<div style="overflow-x:hidden;overflow-y:auto;"></div>')
				.addClass(o.contentClass).appendTo(
						$ctr).scroll(function() {
					scrolling = true;
				});

		var $paging = $('<div></div>').appendTo($ctr);
		// $div.css('height', $input.outerHeight());//lly delete
		if ($.fn.resizable)
			$ctr.resizable({
				handles : 'e',
				maxWidth : 900,
				minWidth : 50,
				onStartResize : function() {// 李从波修改 ，拖动的过程中隐藏选择项，避免误操作，拖动结束后显示
					$content.hide();
				},
				onStopResize : function() {
					$content.width($ctr.width());
					$content.show();
				}
			});// lly add

		function processKeyDown(e) {
			// handle modifiers
			var mod = 0;
			if (typeof (e.ctrlKey) !== 'undefined') {
				if (e.ctrlKey)
					mod |= 1;
				if (e.shiftKey)
					mod |= 2;
			} else {
				if (e.modifiers & Event.CONTROL_MASK)
					mod |= 1;
				if (e.modifiers & Event.SHIFT_MASK)
					mod |= 2;
			}
			// if the keyCode is one of the modifiers, bail out (we'll catch it
			// on the next keypress)
			if (/16$|17$/.test(e.keyCode))
				return; // 16 = Shift, 17 = Ctrl

			var tab = e.keyCode === 9, esc = e.keyCode === 27;
			var tabWithModifiers = e.keyCode === 9 && mod > 0;
			var backspace = e.keyCode === 8; // we will end up extending the
												// delay time for backspaces...

			if (tab)
				if (getCurr())
					selectCurr();

			// handling up/down/escape/right arrow/left arrow requires results
			// to be visible
			// handling enter requires that AND a result to be selected
			if ((/27$|38$|33$|34$/.test(e.keyCode) && $ctr.is(':visible'))
					|| (/13$|40$/.test(e.keyCode)) || !o.allowInput) {

				if (e.preventDefault)
					e.preventDefault();
				if (e.stopPropagation)
					e.stopPropagation();

				e.cancelBubble = true;
				e.returnValue = false;
				switch (e.keyCode) {
				case 38: // up arrow
					prevResult();
					break;
				case 40: // down arrow
					if ($ctr.is(':visible'))
						nextResult();
					else
						flexboxDelay(true);
					break;

				case 13: // TODO 林森屏蔽enter
					if (getCurr())
						selectCurr();
					else {
						var next = Base._getNextFormField(o.divId + "_desc");
						if (next && next.id)
							Base.focus(next.id);
						    $(next).select();
					}
					// else flexboxDelay(true);
					break;
				case 27: // escape
					hideResults();
					break;
				case 34: // page down
					if (!retrievingRemoteData) {
						if (o.paging)
							$('#' + $div.attr('id') + 'n').click();
						else
							nextPage();
					}
					break;
				case 33: // page up
					if (!retrievingRemoteData) {
						if (o.paging)
							$('#' + $div.attr('id') + 'p').click();
						else
							prevPage();
					}
					break;
				default:
					if (!o.allowInput) {
						return;
					}
				}
			} else if (!esc && !tab && !tabWithModifiers) { // skip esc and tab
															// key and any
															// modifiers
				flexboxDelay(false, backspace);
			}
		}

		function flexboxDelay(simulateArrowClick, increaseDelay) {
			if (timeout)
				clearTimeout(timeout);
			var delay = increaseDelay ? o.queryDelay * 5 : o.queryDelay;
			timeout = setTimeout(function() {
				flexbox(1, simulateArrowClick, '');
			}, delay);
		}

		function flexbox(p, arrowOrPagingClicked, prevQuery, pageClick) {// TODO
																			// 林森添加PageClick
			$("body").bind("click.selectinput",
							function(e) {
								var srcobj;
								if ($.browser.msie) {
									srcobj = e.srcElement;
								} else {
									srcobj = e.target;
								}
								if (srcobj
										&& (srcobj.id == $input.attr('id') || srcobj.id == $ctr.attr('id'))) {// 李从波修改，添加鼠标点击的是下拉框的判断
									return;
								}
								hideResults();
								$("body").unbind(".selectinput");
							});
//							.bind("mouseout.selectinput",	function(e) {
//								// add by sun 移到其他位置时就隐藏当前数据显示框
//								var parent = $div;
//								var px = parent.offset().left;
//								var py = parent.offset().top;
//								var ph = parent[0].offsetHeight;
//								var pw = parent[0].offsetWidth;
//								var child = $ctr;
//								var cx = child.offset().left;
//								var cy = child.offset().top;
//								var ch = child[0].offsetHeight;
//								var cw = child[0].offsetWidth;
//								var x = e.pageX;
//								var y = e.pageY;
//								console.log("y" : y)
//								console.log("cy" : cy)
//								console.log("py" : py)
//								var flag = (y < cy) && (x > (px + pw));
//								if (x < px || x > (cx + cw - 5) || y < py || y > (py + ph + ch + 5) || flag) {
//									hideResults();
//								}
//							});

			if (arrowOrPagingClicked)
				prevQuery = '';
			var filterBool = true;
			//update by sun ,将trim去掉了..
			var q = prevQuery && prevQuery.length > 0 ? prevQuery : $input.val();
			// 如果q的长度大于最小弹出输入框数值-

			// TODO 林森添加用于判断显示及过滤方式
			if (q !== "" && !pageClick) {
				if (o.paging) {
					var j, page = o.source.total / pageSize;
					for ( var i = 0; i < o.source.total; i++) {
						//显示的为那种情况,默认为value,add by sun
						if(o.showValue == "key"){
							if (o.source[o.resultsProperty][i][o.hiddenValue] == q ) {
								filterBool = false;
								j = i;
								break;
							}
						}else if(o.showValue == "all"){
							if ((o.source[o.resultsProperty][i][o.hiddenValue] + ":" + o.source[o.resultsProperty][i][o.displayValue] ) == q ) {
								filterBool = false;
								j = i;
								break;
							}
						}else{
							if (o.source[o.resultsProperty][i][o.displayValue] == q) {
								filterBool = false;
								j = i;
								break;
							}
						}
						if (o.source.results[i][o.displayValue] == q) {
							
						}
					}
					if (j) {
						p = parseInt(j / pageSize);
						p += 1;
					}
				}
			} else if (pageClick) {
				q = "";
			}
			/** *********************** */
			if (q.length >= o.minInputChars || arrowOrPagingClicked) {
				if ($content.outerHeight(true) > 0)
					$content.css('height', $content.outerHeight(true));
				if(Base.globvar.indexStyle == "default"){
					//liys,修改新皮肤后，下拉框的宽度和位置调整
					var widthPercent = 1;
					if(o.widthPercentage){
						widthPercent = parseInt(o.widthPercentage,10)/100;
					}
					var pWidth = $content.parents(".fielddiv2").outerWidth(false);
					if(pWidth){
						$content.css('width', pWidth*widthPercent);
						$content.parent("div").css('width', pWidth*widthPercent);
					}
				}
				$content.html('')
				// TODO 林森
				var cached = checkCache(q, p);
				if (o.showAllArrow && cached) {
					$content.css('height', 'auto');
					displayItems(o.source, q);
					showPaging(p, cached.t);
				} else {
					// TODO 林森修改params添加filter属性
					var params = {
						q : q,
						p : p,
						s : pageSize,
						filter : filterBool,
						contentType : 'application/json; charset=utf-8'
					};
					var callback = function(data, overrideQuery, params) {

						if (typeof data == "string") {
							try {
								eval('var data = ' + data);
								// dataAll = data;
							} catch (ex) {
								throw "后台反回json错误";
							}
						}
						if (overrideQuery === true)
							q = overrideQuery; // must compare to boolean
												// because by default, the
												// string value "success" is
												// passed when the jQuery
												// $.getJSON method's callback
												// is called
						var totalResults = parseInt(data[o.totalProperty]);
						var totalSize = displayItems(data, q, params);
						showPaging(p, totalResults);
						$content.css('height', 'auto');
						var $selectDiv = $content.find("." + o.selectClass);
						if ($selectDiv.length > 0) {
							$content.scrollTop($selectDiv[0].offsetTop-7);
						}
						retrievingRemoteData = false;
					};

					if (typeof (o.source) === 'object') {
						if (o.allowInput)
							callback(o.source, null, params);
						else
							callback(o.source);
					} else {
						retrievingRemoteData = true;
						if (o.method.toUpperCase() == 'POST')
							$.post(o.source, params, callback, 'json');
						// else $.getJSON(o.source, params, callback);
						else
							$.ajax({
								url : o.source,
								success : callback,
								data : params
							});
					}
				}
			} else
				hideResults();
		}

		function filter(data, params) {
			if (!params.filter && !$ctr.is(':visible')) {
				params.q = "";
			} // TODO 林森添加过滤判断
			var filtered = {};
			filtered[o.resultsProperty] = [];
			filtered[o.totalProperty] = 0;
			var index = 0;
			// 过滤数据放入filtered+
			for ( var i = 0; i < data[o.resultsProperty].length; i++) {
				var indexOfMatch = data[o.resultsProperty][i][o.displayValue]
						.toLowerCase().indexOf(params.q.toLowerCase());
				var id_indexOfMatch = data[o.resultsProperty][i][o.hiddenValue].toString()
				.toLowerCase().indexOf(params.q.toLowerCase());
				if (data[o.resultsProperty][i][o.pyFilter] != undefined) {
					var pyMatch = data[o.resultsProperty][i][o.pyFilter]
							.toLowerCase().indexOf(params.q.toLowerCase());
					if ((o.matchAny && pyMatch !== -1)
							|| (!o.matchAny && pyMatch === 0)) {
						filtered[o.resultsProperty][index++] = data[o.resultsProperty][i];
						filtered[o.totalProperty] += 1;
						continue;
					}
				} // @author Lins
				if ((o.matchAny && indexOfMatch !== -1) || (o.matchAny && id_indexOfMatch !== -1)
						|| (!o.matchAny && indexOfMatch === 0)) {
					filtered[o.resultsProperty][index++] = data[o.resultsProperty][i];
					filtered[o.totalProperty] += 1;
				}
			}
			// p第几页，s每页几条
			if (o.paging) {
				var start = (params.p - 1) * params.s; // (2-1)*5 = 5
				// 5+5 > 3 ? 11 - 5 : 5
				// 6
				var howMany = (start + params.s) > filtered[o.totalProperty] ? filtered[o.totalProperty]
						- start
						: params.s;
				filtered[o.resultsProperty] = filtered[o.resultsProperty]
						.splice(start, howMany);
			}
			return filtered;
		}

		function showPaging(p, totalResults) {
			$paging.html('').removeClass(o.paging.cssClass); // clear out for
																// threshold
																// scenarios
			if (o.showResults && o.paging && totalResults > pageSize) {
				var pages = totalResults / pageSize;
				if (totalResults % pageSize > 0)
					pages = parseInt(++pages);
				outputPagingLinks(pages, p, totalResults);
			}
		}

		function handleKeyPress(e, page, totalPages) {
			if (/^13$|^39$|^37$/.test(e.keyCode)) {

				if (e.preventDefault)
					e.preventDefault();
				if (e.stopPropagation)
					e.stopPropagation();

				e.cancelBubble = true;
				e.returnValue = false;

				switch (e.keyCode) {
				case 13: // Enter
					if (/^\d+$/.test(page) && page > 0 && page <= totalPages)
						flexbox(page, true);
					else
						alert('Please enter a page number between 1 and '
								+ totalPages);
					// TODO: make this alert a function call, and a customizable
					// parameter
					break;
				case 39: // right arrow
					$('#' + $div.attr('id') + 'n').click();
					break;
				case 37: // left arrow
					$('#' + $div.attr('id') + 'p').click();
					break;
				}
			}
		}

		function handlePagingClick(e) {
			// TODO 林森添加page额外属性
			flexbox(parseInt($(this).attr('page')), true, $input.attr('pq'),
					true); // pq == previous query
			return false;
		}

		function outputPagingLinks(totalPages, currentPage, totalResults) {
			// TODO: make these configurable images
			var first = '&lt;&lt;', prev = '&lt;', next = '&gt;', last = '&gt;&gt;', more = '...';

			$paging.addClass(o.paging.cssClass);

			// set up our base page link element
			var $link = $('<a/>').attr('href', '#').addClass('page').click(
					handlePagingClick), $span = $('<span></span>').addClass(
					'page'), divId = $div.attr('id');

			// show first page
			if (currentPage > 1) {
				$link.clone(true).attr('id', divId + 'f').attr('page', 1).html(
						first).appendTo($paging);
				$link.clone(true).attr('id', divId + 'p').attr('page',
						currentPage - 1).html(prev).appendTo($paging);
			} else {
				$span.clone(true).html(first).appendTo($paging);
				$span.clone(true).html(prev).appendTo($paging);
			}

			if (o.paging.style === 'links') {
				var maxPageLinks = o.paging.maxPageLinks;
				// show page numbers
				if (totalPages <= maxPageLinks) {
					for ( var i = 1; i <= totalPages; i++) {
						if (i === currentPage) {
							$span.clone(true).html(currentPage).appendTo(
									$paging);
						} else {
							$link.clone(true).attr('page', i).html(i).appendTo(
									$paging);
						}
					}
				} else {
					if ((currentPage + parseInt(maxPageLinks / 2)) > totalPages) {
						startPage = totalPages - maxPageLinks + 1;
					} else {
						startPage = currentPage - parseInt(maxPageLinks / 2);
					}

					if (startPage > 1) {
						$link.clone(true).attr('page', startPage - 1)
								.html(more).appendTo($paging);
					} else {
						startPage = 1;
					}

					for ( var i = startPage; i < startPage + maxPageLinks; i++) {
						if (i === currentPage) {
							$span.clone(true).html(i).appendTo($paging);
						} else {
							$link.clone(true).attr('page', i).html(i).appendTo(
									$paging);
						}
					}

					if (totalPages > (startPage + maxPageLinks)) {
						$link.clone(true).attr('page', i).html(more).appendTo(
								$paging);
					}
				}
			} else if (o.paging.style === 'input') {
				var $pagingBox = $('<input/>').addClass('box').click(
						function(e) {
							this.select();
						}).keypress(function(e) {
					return handleKeyPress(e, this.value, totalPages);
				}).val(currentPage).appendTo($paging);
			}

			if (currentPage < totalPages) {
				$link.clone(true).attr('id', divId + 'n').attr('page',
						+currentPage + 1).html(next).appendTo($paging);
				$link.clone(true).attr('id', divId + 'l').attr('page',
						totalPages).html(last).appendTo($paging);
			} else {
				$span.clone(true).html(next).appendTo($paging);
				$span.clone(true).html(last).appendTo($paging);
			}
			var startingResult = (currentPage - 1) * pageSize + 1;
			var endingResult = (startingResult > (totalResults - pageSize)) ? totalResults
					: startingResult + pageSize - 1;

			if (o.paging.showSummary) {
				var summaryData = {
					"start" : startingResult,
					"end" : endingResult,
					"total" : totalResults,
					"page" : currentPage,
					"pages" : totalPages
				};
				var html = o.paging.summaryTemplate.applyTemplate(summaryData);
				$('<br/>').appendTo($paging);
				$('<span></span>').addClass(o.paging.summaryClass).html(html)
						.appendTo($paging);
			}
		}

		function checkCache(q, p) {
			var key = q + delim + p; // use null character as delimiter
			if (cacheData[key]) {
				for ( var i = 0; i < cache.length; i++) { // TODO: is it
															// possible to not
															// loop here?
					if (cache[i] === key) {
						// pull out the matching element (splice), and add it to
						// the beginning of the array (unshift)
						cache.unshift(cache.splice(i, 1)[0]);
						return cacheData[key];
					}
				}
			}
			return false;
		}

		function updateCache(q, p, s, t, data, size) {
			if (o.maxCacheBytes > 0) {
				while (cache.length && (cacheSize + size > o.maxCacheBytes)) {
					var cached = cache.pop();
					cacheSize -= cached.size;
				}
				var key = q + delim + p; // use null character as delimiter
				cacheData[key] = {
					q : q,
					p : p,
					s : s,
					t : t,
					size : size,
					data : data
				}; // add the data to the cache at the hash key location
				cache.push(key); // add the key to the MRU list
				cacheSize += size;
			}
		}

		/**
		 * 构造下拉框 d 的数据
		 */
		function createItems (d, q, params){
			
			var totalSize = 0, itemCount = 0;
			var selectClassRow = [];// TODO 林森添加用于保存可能被选择的列
			// 李从波添加 计算输入框有效上下边界偏移量和上下可容纳行数
			var selectTop, selectBottom;
			// 向上查找，看有无tabs，如果有则只计算离tab的上下偏移量 liys modify
			if ($input.parents('.tabs-panels').size() >= 1) {
				var $tab = $input.parents('.tabs-panels').first();
				selectTop = $input.offset().top - $tab.offset().top;
				selectBottom = $tab.offset().top + $tab.height()
						- $input.offset().top - o.itemHeight;
			} else {
				selectTop = $input.offset().top - $(window).scrollTop();// 得到输入框相对于window的上边界偏移量
				selectBottom = $(window).height() - selectTop - o.itemHeight; // 得到输入框现相对于window下边界的偏移量,输入框的高度为27
			}
			var $topContainer = $div.parent().parent().parent().parent();// selectInput的外部容器
			if ($topContainer && $topContainer.hasClass("slick-viewport")) {// 处理selectInput作为datagridEditor时的情况
				var gridViewportTop = $topContainer.offset().top
						- $(window).scrollTop();// 得到dataGrid可视区域相对于window的上边界偏移量
				var gridViewportBottom = $(window).height() - gridViewportTop
						- $topContainer.height();// 得到dataGrid可视区域相对于window的下边界偏移量
				selectTop = gridViewportTop > 0 ? (selectTop - gridViewportTop)
						: selectTop;// 得到有效的上边界偏移量
				selectBottom = gridViewportBottom > 0 ? (selectBottom - gridViewportBottom)
						: selectBottom;// 得到有效的下边界偏移量
			}
			var topMaxVisibleRows = parseInt(selectTop / o.itemHeight);// 计算上面可以容纳的行数
			var bottomMaxVisibleRows = parseInt(selectBottom / o.itemHeight);// 计算下面可以容纳的行数

			
			var dataAfterFilter = [];
			
			// $hdn.val($input.val());//带查
			if (parseInt(d[o.totalProperty]) === 0 && o.noResultsText
					&& o.noResultsText.length > 0) {
				$content.addClass(o.noResultsClass).html(o.noResultsText);
				$ctr.parent().css('z-index', 11000);// TODO 林森添加
				$ctr.css({
					"top" : 21
				});// 恢复初始扩展方向
				if (o.isAutoExtend) {// 判断是否自动计算下拉选项扩展方向
					if (selectTop > selectBottom) {
						if (bottomMaxVisibleRows == 0) {
							$ctr.css({
								"top" : -21
							}); // 向上弹出
						}
					}
				}
				$ctr.show();
				return;
			} else
				$content.removeClass(o.noResultsClass);// 没有数据显示的时候
			for ( var i = 0; i < d[o.resultsProperty].length; i++) {
				// TODO 林森，过滤显示
				var flag = true; // @author lins
				var data = d[o.resultsProperty][i];
				for ( var j = 0; j < disables.length; j++) {
					if (!o.reverseFilter && disables[j] == data[o.hiddenValue]) {
						flag = false;
						break;
					} else if (o.reverseFilter
							&& disables[j] == data[o.hiddenValue]) {
						dataAfterFilter.push(data);
						// TODO 林森修改添加
						flag = false;
						break;
					} else if (o.reverseFilter) {
						flag = false;
					}
				}
				;
				if (!flag)
					continue;
				dataAfterFilter.push(data);
			}

			var dataAfterFilterPage = {};
			dataAfterFilterPage[o.resultsProperty] = dataAfterFilter;
			var dataAfterFilterPageData = filter(dataAfterFilterPage, params);
			for ( var i = 0; i < dataAfterFilterPageData[o.resultsProperty].length; i++) {
				var data2 = dataAfterFilterPageData[o.resultsProperty][i], // 去得数据
				result = o.resultTemplate.applyTemplate(data2), // 如果有末班包装模板
				exactMatch = q === result, // 判断传入的值是否和当前数据匹配
				selectedMatch = false, hasHtmlTags = false, match = data2[o.displayValue];
				// 对result的高亮样式
				if (!exactMatch && o.highlightMatches && q !== '') {
					var pattern = q, highlightStart = match.toLowerCase()
							.indexOf(q.toLowerCase()), replaceString = '<span class="'
							+ o.matchClass
							+ '">'
							+ match.substr(highlightStart, q.length)
							+ '</span>';
					if (result.match('<(.|\n)*?>')) { // see if the content
														// contains html tags
						hasHtmlTags = true;
						pattern = '(>)([^<]*?)(' + q + ')((.|\n)*?)(<)'; // TODO:
																			// look
																			// for
																			// a
																			// better
																			// way
						replaceString = '$1$2<span class="' + o.matchClass
								+ '">$3</span>$4$6';
					}
					result = result.replace(new RegExp(pattern,
							o.highlightMatchesRegExModifier), replaceString);
				}

				// write the value of the first match to the input box, and
				// select the remainder,
				// but only if autoCompleteFirstMatch is set, and there are no
				// html tags in the response
				if (o.autoCompleteFirstMatch && !hasHtmlTags && i === 0) {
					if (q.length > 0
							&& match.toLowerCase().indexOf(q.toLowerCase()) === 0) {
						$input.attr('pq', q); // pq == previous query
						$hdn.val(data[o.hiddenValue]);
						$input.val(data[o.displayValue]);
						selectedMatch = selectRange(q.length,
								$input.val().length);
					}
				}
				if (!o.showResults)
					return;
				// ****************************************
				$row = $('<div></div>').attr('id', data2[o.hiddenValue]).attr(
						'val', data2[o.displayValue])
				// .addClass('ffb-select-text')
				// .html(result)
				.appendTo($content);
				if (o.islevel == "true") {
					if (data2.level && data2.level != null) {
						$row.css("paddingLeft", 13 * (data2.level - 1) + 6).attr(
								'level', data2.level);
					}
					if (data2.leaf) {
						$row.attr('leaf', data2.leaf);
					}
				}
				 if(typeof o.infomouseover == "function"){
                 	$row.bind("mouseover",o.infomouseover);
                 }
                 if(typeof o.infomouseout == "function"){
                 	$row.bind("mouseout",o.infomouseout);
                 }
				if (o.showKey) {
					$row.html(data2[o.hiddenValue] + ": " + result);
				} else {
					$row.html(result);
				}
				if (exactMatch) {
					// $row.addClass(o.selectClass);
					var isNew = true;
					for ( var k = 0; k < selectClassRow.length; k++) {
						if (selectClassRow[k] == $row)
							isNew = false;
					}
					if (isNew) {
						selectClassRow.push($row);
						itemCount++;
					}
				} else if (!exactMatch
						&& (++itemCount == 1 && o.selectFirstMatch)
						|| selectedMatch) {
					// $row.addClass(o.selectClass);
					var isNew = true;
					for ( var k = 0; k < selectClassRow.length; k++) {
						if (selectClassRow[k] == $row)
							isNew = false;
					}
					if (isNew)
						selectClassRow.push($row);
				}
				totalSize += result.length;
			}
			// TODO 林森添加只选取一行被选中
			if (o.selectFirstMatch && selectClassRow.length > 0) {
				selectClassRow.pop().addClass(o.selectClass);
			}
			if (totalSize === 0) {
				hideResults();
				return;
			}
			$ctr.parent().css('z-index', 11000);
			// 李从波添加，自动计算maxVisibleRows
			if (selectBottom >= selectTop) {
				var result = ([ bottomMaxVisibleRows, defaultVisibleRows, 20,
				    dataAfterFilterPageData[o.resultsProperty].length ]).sort(function(a, b) {
					return a - b;
				})[0]; // 计算最终显示的行数,取最小值
				o.maxVisibleRows = (result == 0) ? 1 : result;
				$ctr.show();
				$ctr.css({
					"top" : 21
				});
			} else {
				// 当存在filter属性和reverseFilter=true,需要判断过滤后的行数和默认行数
				var needVisibleRows;
				if (o.filter) {
					if (o.reverseFilter && disables.length > 0) {
						needVisibleRows = ([ disables.length,
								defaultVisibleRows, 20 ]).sort(function(a, b) {
							return a - b;
						})[0];
					} else {
						needVisibleRows = ([ defaultVisibleRows, 20,
						        dataAfterFilterPageData[o.resultsProperty].length - disables.length ])
								.sort(function(a, b) {
									return a - b;
								})[0];
					}
				} else {
					needVisibleRows = ([ defaultVisibleRows, 20, dataAfterFilterPageData[o.resultsProperty].length ])
							.sort(function(a, b) {
								return a - b;
							})[0]; // 取得最少需要的行数
				}
				if (bottomMaxVisibleRows >= needVisibleRows) {// 判断下面是否可以容纳最小行数
					o.maxVisibleRows = needVisibleRows;
					$ctr.show();
					$ctr.css({
						"top" : 21
					});
				} else {
					var reslult = (topMaxVisibleRows >= needVisibleRows) ? needVisibleRows
							: topMaxVisibleRows;// 使用上面区域，取最小值
					o.maxVisibleRows = (reslult == 0) ? 1 : reslult;
					$ctr.show();
					if (o.maxVisibleRows > 0) {
						var maxHeight = $row.outerHeight(true)
								* o.maxVisibleRows;
						//update by sun;
						if(o.paging){
							var pageSize = o.paging && o.paging.pageSize ? o.paging.pageSize:0;
							var page = o.source.total / pageSize;
							if(page && page >= 1){
								maxHeight = maxHeight + 25;//分页条的高度
							}
						}
						$content.css('max-height', maxHeight + 2);// 李从波修改，添加2像素的高度（选中项的边框）
						if (o.isAutoExtend) {// 判断是否自动计算下拉选项扩展方向
							$ctr.css({
								"top" : -(maxHeight + 2 + 21)
							}); // 向上弹出
						} else {
							$ctr.css({
								"top" : 21
							});
						}

					}
				}
			}
			if (o.maxVisibleRows > 0) {
				var maxHeight = $row.outerHeight(true) * o.maxVisibleRows;
				$content.css('max-height', maxHeight + 2);// 李从波修改，添加2像素的高度（选中项的边框）
			}
			$content.children('div').mouseover(function() {
				$content.children('div').removeClass(o.selectClass);
				$(this).addClass(o.selectClass);
			}).mouseup(function(e) {
				e.cancelBubble = true;
				e.returnValue = false;
				if (e.stopPropagation) {
					e.stopPropagation();
					e.preventDefault();
				}
				selectCurr();
			});
			return totalSize;
		}
		/**
		 * 显示下拉框 d 数据
		 */
		function displayItems(d, q, params) {
			
			// add by sun 在最前面加一个空白选择框
			// if(objResults[0]){
			// }else{
			// var objFirst = {};
			// objFirst.id = "";
			// objFirst.name = "--请选择--";
			// var objresults = d.results;
			// d.results.unshift(objFirst);
			// d.total = d.total + 1;
			//        		
			// }
			//update by sun 如果没有值,存在collection属性.从服务器缓存读取内容
			if((d.total == 0 || !d.total)  && o.collection){
				
				// 李从波添加 计算输入框有效上下边界偏移量和上下可容纳行数
				var selectTop, selectBottom;
				// 向上查找，看有无tabs，如果有则只计算离tab的上下偏移量 liys modify
				if ($input.parents('.tabs-panels').size() >= 1) {
					var $tab = $input.parents('.tabs-panels').first();
					selectTop = $input.offset().top - $tab.offset().top;
					selectBottom = $tab.offset().top + $tab.height()
							- $input.offset().top - o.itemHeight;
				} else {
					selectTop = $input.offset().top - $(window).scrollTop();// 得到输入框相对于window的上边界偏移量
					selectBottom = $(window).height() - selectTop - o.itemHeight; // 得到输入框现相对于window下边界的偏移量,输入框的高度为27
				}
				var $topContainer = $div.parent().parent().parent().parent();// selectInput的外部容器
				if ($topContainer && $topContainer.hasClass("slick-viewport")) {// 处理selectInput作为datagridEditor时的情况
					var gridViewportTop = $topContainer.offset().top
							- $(window).scrollTop();// 得到dataGrid可视区域相对于window的上边界偏移量
					var gridViewportBottom = $(window).height() - gridViewportTop
							- $topContainer.height();// 得到dataGrid可视区域相对于window的下边界偏移量
					selectTop = gridViewportTop > 0 ? (selectTop - gridViewportTop)
							: selectTop;// 得到有效的上边界偏移量
					selectBottom = gridViewportBottom > 0 ? (selectBottom - gridViewportBottom)
							: selectBottom;// 得到有效的下边界偏移量
				}
				var topMaxVisibleRows = parseInt(selectTop / o.itemHeight);// 计算上面可以容纳的行数
				var bottomMaxVisibleRows = parseInt(selectBottom / o.itemHeight);// 计算下面可以容纳的行数

				$content.addClass(o.noResultsClass).html("努力加载中...");
				$ctr.parent().css('z-index', 11000);// TODO 林森添加
				$ctr.css({
					"top" : 21
				});// 恢复初始扩展方向
				if (o.isAutoExtend) {// 判断是否自动计算下拉选项扩展方向
					if (selectTop > selectBottom) {
						if (bottomMaxVisibleRows == 0) {
							$ctr.css({
								"top" : -21
							}); // 向上弹出
						}
					}
				}
				$ctr.show();
				/***/
				//第一次加载本地缓存码表	
				$.ajax({
		           cache: true,
		           type: "POST",
		           url: Base.globvar.basePath+"/sysapp/appCodeCacheAction!getCacheByCollection.do",
		           dataType : "json",
		           data:"collection="+o.collection,
		           async: true,
		           error: function(request) {
		               alert("Connection error");
		           },
		           success: function(data) {
			          
		        	   $content.addClass(o.noResultsClass).html("");
						$content.removeClass(o.noResultsClass);// 没有数据显示的时候
						$ctr.hide();
		        	  var data = eval(data);
		        	  d[o.totalProperty] = data.length;
			          var arrays = [];
			          for(var i in data){
			        	  arrays.push(data[i]);
			          }
			          d[o.resultsProperty] = arrays;
			          if (!d){ // 判断有无数据
							return;
			          }
			          //TODO add by sun
			          createItems(d, q, params);
		           }
		      	 });
				
			}else{
				if (!d){ // 判断有无数据
					return;
				}
				createItems(d, q, params);
			}
		}

		function selectRange(s, l) {
			var tb = $input[0];
			if (tb.createTextRange) {
				var r = tb.createTextRange();
				r.moveStart('character', s);
				r.moveEnd('character', l - tb.value.length);
				r.select();
			} else if (tb.setSelectionRange) {
				tb.setSelectionRange(s, l);
			}
			tb.focus();
			return true;
		}

		String.prototype.applyTemplate = function(d) {
			try {
				if (d === '')
					return this;
				return this.replace(/{([^{}]*)}/g, function(a, b) {
					var r;
					if (b.indexOf('.') !== -1) { // handle dot notation in
													// {}, such as
													// {Thumbnail.Url}
						var ary = b.split('.');
						var obj = d;
						for ( var i = 0; i < ary.length; i++)
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
				alert('Invalid JSON property '
						+ ex
						+ ' found when trying to apply resultTemplate or paging.summaryTemplate.\nPlease check your spelling and try again.');
			}
		};

		function hideResults() {
			$input.data('active', false); // for input blur
			$div.css('z-index', 0);// 林森修改，原为0
			$ctr.hide();
		}

		function getCurr() {
			if (!$ctr.is(':visible'))
				return false;

			var $curr = $content.children('div.' + o.selectClass);

			if (!$curr.length)
				$curr = false;

			return $curr;
		}

		function selectCurr() {
			$curr = getCurr();
			if (o.isMustLeaf == "true") {
				var leaf = $curr.attr('leaf');
				if (leaf == "N") {
					alert("必须选择子节点");
					$hdn.val("");
					$input.val("").focus();
					return;
				}
			}
			if (!isNaN(o.minLevel)) {
				var level = $curr.attr('level');
				if (level < o.minLevel) {
					alert("必须选择大于第" + o.minLevel + "级的节点");
					$hdn.val("");
					$input.val("").focus();
					return;
				}
			}
			if (!isNaN(o.maxLevel)) {
				var level = $curr.attr('level');
				if (level > o.maxLevel) {
					alert("必须选择小于第" + o.maxLevel + "级的节点");
					$hdn.val("");
					$input.val("").focus();
					return;
				}
			}
			if ($curr) {
				var oldHdnVal = $hdn.val();
				$hdn.val($curr.attr('id'));
				//显示的为那种情况,默认为value,add by sun
				if(o.showValue == "key"){
					 $input.val($curr.attr('id')).focus();
				}else if(o.showValue == "all"){
					 $input.val($curr.attr('id')+":"+$curr.attr('val')).focus();
				}else{
					 $input.val($curr.attr('val')).focus();
				}
				hideResults();

				if (o.onSelect && (oldHdnVal != $hdn.val() ) ) {
					o.onSelect($input.val(), $hdn.val());// @author lins
				}
			}
		}

		function supportsGetBoxObjectFor() {
			try {
				document.getBoxObjectFor(document.body);
				return true;
			} catch (e) {
				return false;
			}
		}

		function supportsGetBoundingClientRect() {
			try {
				document.body.getBoundingClientRect();
				return true;
			} catch (e) {
				return false;
			}
		}

		function nextPage() {
			$curr = getCurr();

			if ($curr && $curr.next().length > 0) {
				$curr.removeClass(o.selectClass);

				for ( var i = 0; i < o.maxVisibleRows; i++) {
					if ($curr.next().length > 0) {
						$curr = $curr.next();
					}
				}

				$curr.addClass(o.selectClass);
				var scrollPos = $content.scrollTop();
				$content.scrollTop(scrollPos + $content.height());
			} else if (!$curr)
				$content.children('div:first-child').addClass(o.selectClass);
		}

		function prevPage() {
			$curr = getCurr();

			if ($curr && $curr.prev().length > 0) {
				$curr.removeClass(o.selectClass);

				for ( var i = 0; i < o.maxVisibleRows; i++) {
					if ($curr.prev().length > 0) {
						$curr = $curr.prev();
					}
				}

				$curr.addClass(o.selectClass);
				var scrollPos = $content.scrollTop();
				$content.scrollTop(scrollPos - $content.height());
			} else if (!$curr)
				$content.children('div:last-child').addClass(o.selectClass);
		}

		function nextResult() {
			$curr = getCurr();

			if ($curr && $curr.next().length > 0) {
				$curr.removeClass(o.selectClass).next().addClass(o.selectClass);
				var scrollPos = $content.scrollTop(), curr = $curr[0], parentBottom, bottom, height;
				if (supportsGetBoxObjectFor()) {
					parentBottom = document.getBoxObjectFor($content[0]).y
							+ $content.attr('offsetHeight');
					bottom = document.getBoxObjectFor(curr).y
							+ $curr.attr('offsetHeight');
					height = document.getBoxObjectFor(curr).height;
				} else if (supportsGetBoundingClientRect()) {
					parentBottom = $content[0].getBoundingClientRect().bottom;
					var rect = curr.getBoundingClientRect();
					bottom = rect.bottom;
					height = bottom - rect.top;
				}
				if (bottom >= parentBottom - 20)
					$content.scrollTop(scrollPos + height);
			} else if (!$curr)
				$content.children('div:first-child').addClass(o.selectClass);
		}
		function clearInput(val) {
			var has, hdnValue;
			
			for ( var i = 0; i < o.source[o.totalProperty]; i++) {
				//显示的为那种情况,默认为value,add by sun
				if(o.showValue == "key"){
					if (o.source[o.resultsProperty][i][o.hiddenValue] == val ) {
						has = true;
						hdnValue = o.source[o.resultsProperty][i][o.hiddenValue];
						break;
					}
				}else if(o.showValue == "all"){
					if ((o.source[o.resultsProperty][i][o.hiddenValue] + ":" + o.source[o.resultsProperty][i][o.displayValue] ) == val ) {
						has = true;
						hdnValue = o.source[o.resultsProperty][i][o.hiddenValue];
						break;
					}
				}else{
					if (o.source[o.resultsProperty][i][o.displayValue] == val) {
						has = true;
						hdnValue = o.source[o.resultsProperty][i][o.hiddenValue];
						break;
					}
				}
			}
			if (has) {
				$hdn.val(hdnValue);
				return;
			} else {
				$input.val('');
				$hdn.val('');
			}

		}
		;

		function disableSelect(id) {
			var dis = $(id, $div);
		}
		;
		function prevResult() {
			$curr = getCurr();

			if ($curr && $curr.prev().length > 0) {
				$curr.removeClass(o.selectClass).prev().addClass(o.selectClass);
				var scrollPos = $content.scrollTop(), curr = $curr[0], parent = $curr
						.parent()[0], parentTop, top, height;
				if (supportsGetBoxObjectFor()) {
					height = document.getBoxObjectFor(curr).height;
					parentTop = document.getBoxObjectFor($content[0]).y
							- (height * 2); // TODO: this is not working when i
											// add another control...
					top = document.getBoxObjectFor(curr).y
							- document.getBoxObjectFor($content[0]).y;
				} else if (supportsGetBoundingClientRect()) {
					parentTop = parent.getBoundingClientRect().top;
					var rect = curr.getBoundingClientRect();
					top = rect.top;
					height = rect.bottom - top;
				}
				if (top <= parentTop + 20)
					$content.scrollTop(scrollPos - height);
			} else if (!$curr)
				$content.children('div:last-child').addClass(o.selectClass);
		}
		this.setFocus = function() {
			$input.focus();
		};
		this.getInput = function() {
			return $input;
		};
		/**
		 * 设置值
		 * 
		 * @method setValue
		 * @param {String}
		 *            value 设置值
		 * @author 林森
		 */
		this.setValue = function(value) {
			if (value != "undefined") {
				// if ($.isArray(value)) {setData(value); break;}
				if (typeof value != "string")
					value += "";
				if (typeof o.source == "string") {
					return;
				}
				// if ($.isArray(value)) o.sourse
				var length = o.source[o.resultsProperty].length;
				for ( var i = 0; i < length; i++) {
					if (value == o.source[o.resultsProperty][i][o.hiddenValue]) {
						$input
								.val(o.source[o.resultsProperty][i][o.displayValue]);
						$hdn.val(value);
						return;
					}
					// 李从波添加直接通过属性设值的方式
					if (value == o.source[o.resultsProperty][i][o.displayValue]) {
						$input.val(value);
						$hdn.val(o.source[o.resultsProperty][i][o.hiddenValue]);
						return;
					}
				}
				clearInput(value);
			}
		};
		/**
		 * 取得值
		 * 
		 * @method getValue
		 * @param caseValue {
		 *            0 : input数据 1 : hdn数据 default: 所以数据 }
		 * @return {String} value 取得返回值
		 * @author 林森
		 */
		this.getValue = function(caseValue) {
			var res;
			switch (caseValue) {
			case 0:
				res = $input.val();
				break;
			case 1:
				res = $hdn.val();
				break;
			default:
				res = obj2JSON(o.source[o.resultsProperty]);
				break;
			}
			return res;
		};
		/**
		 * json对象转换成json字符串;
		 * 
		 * @method obj2JSON
		 * @param {ARRAY}
		 *            value 数组对象
		 * @return {string} str 返回json
		 * @author 林森
		 */
		function obj2JSON(value) {
			if (value && typeof value == "object" && value.length > 0) {
				var str = "";
				str += "[";
				for ( var i = 0; i < value.length; i++) {
					str += "{ ";
					var obj = value[i];
					for (j in obj) {
						str += j + ":\"" + obj[j] + "\",";
					}
					str = str.slice(0, -1);
					str += "}";
					if (i != value.length - 1) {
						str += ",";
					}
				}
				str += "]";
				return str;
			}
			return "无数据";
		}
		/**
		 * 设置是否可用
		 * 
		 * @method setEnable
		 * @param {boolean}
		 *            bool
		 * @author 林森
		 */
		this.setEnable = function(bool) {
			if (bool) {
				$input.removeAttr('disabled');
				$hdn.removeAttr('disabled');
				$input.removeAttr("readonly");
				$input.removeClass("textinput readonly");
				$input.parent().parent().removeClass("readonly");
				$arrow.click(arrowClick);
				// $input.click(arrowClick);
			} else {
				$input.val('');
				$hdn.val('');
				$input.attr('disabled', "true").attr("readonly", 'readonly');
				$input.addClass("textinput readonly");
				$hdn.attr('disabled', "true");
				$input.parent().parent().addClass("readonly");
				$arrow.unbind("click");
				if ($ctr.is(':visible')) {
					hideResults();
				}
			}
		};
		/**
		 * 设置是否可读
		 * 
		 * @method readOnly
		 * @param {boolean}
		 *            bool
		 * @author 林森
		 */
		this.readOnly = function(bool) {
			if (!bool) {
				$input.removeAttr('disabled');
				$input.removeAttr('readonly');
				$input.removeClass("textinput readonly");
				$input.parent().parent().removeClass("readonly");
				$arrow.click(arrowClick);
			} else {
				$input.attr('disabled', "true").addClass("textinput readonly")
						.attr('readonly', 'readonly');
				$input.parent().parent().addClass("readonly");
				// $input.addClass("textinput
				// readonly").attr('readonly','readonly');
				// $input.unbind("click");林森注释，还原改readonly导致下拉框可以通过键盘显示下拉面板的问题
				$arrow.unbind("click");
				if ($ctr.is(':visible')) {
					hideResults();
				}
			}
		};
		/**
		 * 重新获取url
		 * 
		 * @method loadff
		 * @param {String}
		 *            url
		 * @author 林森
		 */
		this.loadff = function(url) {
			
			if(url.total > 0 && o.clearData != ""){
				var appendString = {};
				appendString.id = "";
				appendString.name = o.clearData;
				url.results.unshift(appendString);
				url.total = data.total + 1 ;
			}
			
			o.source = url;
			if (o.initialValue == '' && o.selectFirstValue) {
				$input.val(url.results[0][o.displayValue]);
				$hdn.val(url.results[0][o.hiddenValue]);
			} else {
				$input.val("");
				$hdn.val("");
			}
			return this;
		};
		this.setData = function(data) {
			
			if (data) {
				if(data.results.length > 0 && o.clearData != ""){
					var appendString = {};
					appendString.id = "";
					appendString.name = o.clearData;
					data.results.unshift(appendString);
				}
				
				o.source = data;
				o.source.total = data.results.length;
				$input.val("");
				$hdn.val("");
			}
		};
		/**
		 * 设置数据不可选项
		 * 
		 * @method setDisableSelect
		 * @param {String}
		 *            val 数据id
		 * @param {bool}
		 *            isReverseFilter 正向还是反向
		 * @author 林森
		 */
		this.setDisableSelect = function(val, isReverseFilter) {
			disables = [];//清空disables,从新进行过滤
			if (isReverseFilter != undefined) {
				o.reverseFilter = isReverseFilter;
			}
			var s = [];
			if (typeof val == 'string') {
				if (val.indexOf("[") != -1 && $.isArray(eval(val))) {
					s = eval(val);
				} else {
					s = val.split(',');
				}
			} else if ($.isArray(val)) {
				s = val;
			}
			for ( var j = 0; j < s.length; j++) {
				if (checkFilterValue(s[j])) {
					disables.push(s[j]);
				}
			}
		};
		/**
		 * 设置过滤，以某些字符串开头的
		 * 
		 * @method setfilterStartChar
		 * @param {String}
		 *            val 开始的字符串
		 * @param {bool}
		 *            isReverseFilter 正向还是反向
		 * @author liys
		 */
		this.setfilterStartChar = function(val, isReverseFilter) {
			// disables = [];
			if (isReverseFilter != undefined) {
				o.reverseFilter = isReverseFilter;
			}
			var s = [];
			if (typeof val == 'string') {
				if (val.indexOf("[") != -1 && $.isArray(eval(val))) {
					s = eval(val);
				} else {
					s = val.split(',');
				}
			} else if ($.isArray(val)) {
				s = val;
			}
			for ( var i = 0; i < o.source.results.length; i++) {
				for ( var j = 0; j < s.length; j++) {
					if ((o.source.results[i][o.displayValue] + "").indexOf(s[j]) == 0) {
						disables.push(o.source.results[i][o.hiddenValue]);
						break;
					}
				}
			}
		};

		/**
		 * 设置过滤，最少几个字符开始
		 * 
		 * @method fiterValueLengthMin
		 * @param {Number}
		 *            min最小字符串长度
		 * @author liys
		 */
		this.fiterValueLengthMin = function(min) {
			// disables = [];
			for ( var i = 0; i < o.source.results.length; i++) {
				if ((o.source.results[i][o.displayValue] + "").length < min) {
					disables.push(o.source.results[i][o.hiddenValue]);
				}
			}
		};

		/**
		 * 设置过滤，最多几个字符
		 * 
		 * @param {String}
		 *            max最大字符串长度
		 * @author liys
		 */
		this.fiterValueLengthMax = function(max) {
			// disables = [];
			for ( var i = 0; i < o.source.results.length; i++) {
				if ((o.source.results[i][o.displayValue] + "").length > max) {
					disables.push(o.source.results[i][o.hiddenValue]);
				}
			}
		};

		/**
		 * 检查filter的值是否在数据中有对应的id
		 * 
		 * @method checkFilterValue
		 * @param {String}
		 *            id 数据id
		 * @author 李从波
		 */
		function checkFilterValue(id) {
			for ( var i = 0; i < o.source.total; i++) {
				var key = o.source[o.resultsProperty][i][o.hiddenValue];
				if (key == id) {
					return true;
				}
			}
			return false;
		}
		;
		this.clearSelect = function() {
			$input.val('');
			$hdn.val('');
		};
		this.getId = function() {
			return o.divId;
		};

	};

	/**
	 * @author lins
	 */
	// function load(o) {
	// var returnData = {};
	// if (o.url) {
	// $.ajax({url : o.url,
	// async : false,
	// success : function(data) {
	// eval('var d =' + data);
	// returnData[o.resultsProperty] = d;
	// reutrnData[o.totalProperty] = d.length;
	// },
	// type : 'post'
	// });
	// }
	// return returnData;
	// }
	// 在flexbox中，通过o.source来保持数据$input显示框,$hdn为隐框
	$.fn.flexbox = function(source, options) {
		/**
		 * @author lins
		 */
		// var defaults = $.fn.flexbox.defaults;
		// var o = $.extend({}, defaults, options);
		// if (!source || !source[o.resultsProperty] ||
		// !source[o.resultsProperty].length ||source[o.resultsProperty].length
		// == 0) {
		// if (o.url) {
		// source = load(o);
		// }
		// }
		var flexboxes = [];
		// /**************************************/

		try {
			var defaults = $.fn.flexbox.defaults;
			var o = $.extend({}, defaults, options);
			if(Base.globvar.indexStyle == "default"){
				o.containerClass = 'ffb ffb_163 ui-multiselect-menu ui-widget';
			}
			if (!source)
				return;
			
			if(source.length >0 && o.clearData != ""){
				var appendString = {};
				appendString.id = "";
				appendString.name = o.clearData;
				source.unshift(appendString);
			}
			
			o.source = {};
			o.source.results = source;
			
			if (!o.source.total) {
				o.source.total = source.length;
			}
			if (options) {
				o.paging = (options.paging || options.paging == null) ? $
						.extend({}, defaults.paging, options.paging) : false;

				for ( var prop in o.paging) {
					if (defaults.paging[prop] === undefined)
						throw ('Invalid option specified: ' + prop + '\nPlease check your spelling and try again.');
				}

				if (options.displayValue && !options.hiddenValue) {
					o.hiddenValue = options.displayValue;
				}
			}
			this.each(function() {
				var obj = new $.flexbox(this, o);
				if (o.disabled) {
					obj.setEnable(false);
				} else if (o.readonly) {
					obj.readOnly(true);
				} else if (o.filter) {
					obj.setDisableSelect(o.filter);
				}
				flexboxes.push(obj);
			});
			return flexboxes;
		} catch (ex) {
			if (typeof ex === 'object')
				alert(ex.message);
			else
				alert(ex);
		}
	};

	// plugin defaults - added as a property on our plugin function so they can
	// be set independently
	$.fn.flexbox.defaults = {
		method : 'GET', // One of 'GET' or 'POST'
		queryDelay : 0, // num of milliseconds before query is run.
		allowInput : true, // set to false to disallow the user from typing in
							// queries
		// containerClass: 'ffb',
		containerClass : 'ffb ui-multiselect-menu ui-widget',
		contentClass : 'content',
		// selectClass: 'ffb-sel',//lins
		selectClass : 'ffb-select',
		inputClass : 'ffb-input',
		arrowClass : 'ffb-arrow',
		matchClass : 'ffb-match',
		noResultsText : '没有相匹配的项目', // text to show when no results match the
									// query
		noResultsClass : 'ffb-no-results', // class to apply to noResultsText
		showResults : true, // whether to show results at all, or just typeahead
		selectFirstMatch : true, // whether to highlight the first matching
									// value
		autoCompleteFirstMatch : false, // whether to complete the first
										// matching value in the input box
		highlightMatches : true, // whether all matches within the string
									// should be highlighted with matchClass
		highlightMatchesRegExModifier : 'i', // 'i' for case-insensitive, 'g'
												// for global (all occurrences),
												// or combine
		matchAny : true, // for client-side filtering ONLY, match any
							// occurrence of the search term in the result (e.g.
							// "ar" would find "area" and "cart")
		minInputChars : 1, // the minimum number of characters the user must
							// enter before a search is executed
		showArrow : true, // set to false to simulate google suggest
		arrowQuery : '', // the query to run when the arrow is clicked
		onSelect : false, // function to run when a result is selected
		onChange : false,// onchange
		maxCacheBytes : 32768, // in bytes, 0 means caching is disabled
		displayValue : 'name', // json element whose value is displayed on
								// select
		resultTemplate : '{name}', // html template for each row (put json
									// properties in curly braces)
		hiddenValue : 'id', // json element whose value is submitted when form
							// is submitted
		initialValue : '', // what should the value of the input field be when
							// the form is loaded?
		watermark : '', // text that appears when flexbox is loaded, if no
						// initialValue is specified. style with css class
						// '.ffb-input.watermark'
		width : 200, // total width of flexbox. auto-adjusts based on
						// showArrow value
		resultsProperty : 'results', // json property in response that
										// references array of results
		totalProperty : 'total', // json property in response that references
									// the total results (for paging)
		maxVisibleRows : 20, // default is 0, which means it is ignored. use
								// either this, or paging.pageSize
		pyFilter : 'py',// @author lins 拼音过滤
		reverseFilter : false,// @author lins 是否过滤
		allowInputOtherText : false,// @author lins 是否支持输入
		showAllArrow : true,// @author lins 是否显示全部数据
		isAutoExtend : true,// 李从波添加，是否自动计算下拉选项扩展方向
		isFocusShowPanel : true,// lh添加，是否聚焦时展示下拉框
		showKey : false,
		showValue : 'value',//显示的默认值,key只显示key,all显示所有,默认是value;
		md5list : "", // 防篡改的数据集合,默认为"";
		placeholder: "",//提示框的信息;
		clearData :"",//首选项的信息;
		disabled : false,
		readonly : false,
		name : "",
		divId : "",
		required : false,
		submitDesc : true,
		showRefresh : false,
		itemHeight:22,
		paging : {
			style : 'input', // or 'links'
			cssClass : 'paging', // prefix with containerClass (e.g. .ffb
									// .paging)
			pageSize : 1000, // acts as a threshold. if <= pageSize results,
								// paging doesn't appear
			maxPageLinks : 5, // used only if style is 'links'
			showSummary : false, // whether to show 'displaying 1-10 of 200
									// results' text
			summaryClass : 'summary', // class for 'displaying 1-10 of 200
										// results', prefix with containerClass
			summaryTemplate : '{start}-{end} 共 {total} 条' // can use {page}
															// and {pages} as
															// well
		}
	};
}));