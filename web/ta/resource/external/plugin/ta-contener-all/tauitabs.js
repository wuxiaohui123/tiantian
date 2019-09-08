(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	// get the left position of the tab element
	function getTabLeftPosition(container, tab) {
		var w = 0;
		var b = true;
		$('>div.tabs-header ul.tabs li', container).each(function(){
			if (this == tab) {
				b = false;
			}
			if (b == true) {
				w += $(this).outerWidth(true);
			}
		});
		return w;
	}
	
	// get the max tabs scroll width(scope)
	function getMaxScrollWidth(container) {
		var header = $('>div.tabs-header', container);
		var tabsWidth = 0;	// all tabs width
		$('ul.tabs li', header).each(function(){
			tabsWidth += $(this).outerWidth(true);
		});
		var wrapWidth = $('.tabs-wrap', header).width();
		var padding = parseInt($('.tabs', header).css('padding-left'));
		
		return tabsWidth - wrapWidth + padding+40;
	}
	
	// set the tabs scrollers to show or not,
	// dependent on the tabs count and width
	function setScrollers(container) {
		var header = $('>div.tabs-header', container);
		var tabsWidth = 0;
		$('ul.tabs li', header).each(function(){
			tabsWidth += $(this).outerWidth(true);
		});
		
		if (tabsWidth > header.width()) {
			$('.tabs-scroller-left', header).css('display', 'block');
			$('.tabs-scroller-right', header).css('display', 'block');
			$('.tabs-wrap', header).addClass('tabs-scrolling');
			
//			if ($.boxModel == true) {
//				$('.tabs-wrap', header).css('left',2);
//			} else {
//				$('.tabs-wrap', header).css('left',0);
//			}
//			var width = header.width()
//				- $('.tabs-scroller-left', header).outerWidth()
//				- $('.tabs-scroller-right', header).outerWidth();
//			$('.tabs-wrap', header).width(width);
//			
//		} else {
//			$('.tabs-scroller-left', header).css('display', 'none');
//			$('.tabs-scroller-right', header).css('display', 'none');
//			$('.tabs-wrap', header).removeClass('tabs-scrolling').scrollLeft(0);
//			$('.tabs-wrap', header).width(header.width());
//			$('.tabs-wrap', header).css('left',0);
//			
		}
	}
	
	// set size of the tabs container
	function setSize(container) {
		//return;
		
		var opts = $.data(container, 'tabs').options;
		var cc = $(container);
//		if (opts.fit == true){
//			var p = cc.parent();
//			opts.width = p.width();
//			opts.height = p.height();
//		}
		
		var header = $('>div.tabs-header', container);
//		if ($.boxModel == true) {
//			var delta = header.outerWidth() - header.width();
//			header.width(cc.width() - delta);
//		} else {
//			header.width(cc.width());
//		}
		
		setScrollers(container);
		
		var panels = $('>div.tabs-panels', container);

		
		if(opts.fit){
			var panelparent = cc.parent(); 
	        var h = panelparent.height();
	        if(h>$(window).height())
	        	h = $(window).height();  
	        if (panelparent[0].tagName.toLowerCase() == "body") { 
	            h = $(window).height();
		        //h -= parseInt($('body').css('paddingTop'));
		        h -= parseInt($('body').css('paddingBottom'));
		        //h -= parseInt($('body').css('marginTop'));
		        h -= parseInt($('body').css('marginBottom'));         
	            h -= cc.offset().top;
	        }else if(panelparent.hasClass('window-body')){
	        	var windowTop = parseInt(cc.offsetParent().css('top'));
	        	h -= cc.offset().top - windowTop -28;
	        }else{
	        	if(panelparent.css('position')=='relative' || panelparent.css('position')=='absolute'){
	        		h -= cc.position().top-parseInt(panelparent.css('paddingBottom'));
//	        		if(panelparent.parent()[0].tagName.toLowerCase() == "body"){
//	        		  h-= panelparent.position().top-parseInt($('body').css('paddingTop'));
//	        		}//2013/6/13 lins 为了解决tab在fit=true的情况下会在下方留下一片空白
	        		if(panelparent.parent()[0].tagName.toLowerCase() == "body"){
	        		  h-= parseInt($('body').css('paddingTop'));
	        		}
	        		
	        	}else{
		        	var pall = cc.prevAll(':visible').not('#pageloading');
		        	if(pall.length>0){
		        		pall.each(function(){
		        			h -= $(this).outerHeight(true);
		        		});
		        	}
		        	var mt = panelparent.css('marginTop');
			        if(mt=='auto')mt = 0;
			        else mt = parseInt(mt);
			        h -= mt;

			        h -= parseInt(panelparent.css('paddingBottom'));
		        	h -= parseInt(panelparent.css('paddingTop'));	 
		        	
			       	var mb = panelparent.css('marginBottom');
			        if(mb=='auto')mb = 0;
			        else mb = parseInt(mb);
			        h -= mb;
	        	}
	        }
	        
	       	var cmb = cc.css('marginBottom');
	        if(cmb=='auto')cmb = 0;
	        else cmb = parseInt(cmb);
	        h -= cmb;
	        h -= header.outerHeight(true);
	        h -= opts.heightDiff;
	        //tabs里面下边框,所以-1
	        panels.height(h-1);
	        
	        $('>div',panels).each(function(){
	        	var $this = $(this);
	        	$(this).height(h-parseInt($this.css('paddingTop'))-parseInt($this.css('paddingBottom')));
	        });
	        
		}else{
			//tabs里面下边框,所以-1
			var height = (opts.height - 1);
		
			if (!isNaN(height)) {
				if ($.boxModel == true) {
					var delta = panels.outerHeight(true) - panels.height();
					height = (height - header.outerHeight(true) - delta) || 'auto';
				} else {
					height = height - header.outerHeight(true);
				}
				panels.css('height', height);
				$('>div',panels).each(function(){
        			var $this = $(this);
        			$(this).height(height-parseInt($this.css('paddingTop'))-parseInt($this.css('paddingBottom')));
	        	});				
			} else {
				panels.height(height);
			}

		}
		//var panelfit = $('>div:visible >div[fit=true],>div:visible >form[fit=true]', panels);
		var panelfit = $('>div:visible  div[fit=true]:first, >div:visible form[fit=true]:first', panels);
//		if (panelfit.has("l-layout-left")) {
//			panelfit.find(">div[fit=true]:first").triggerHandler('_resize');
//		} else 
//		panelfit.each(function(){
//			$(this).triggerHandler('_resize');
//		})
		panelfit.triggerHandler('_resize');
		//$(window).resize();
	}
	
	/**
	 * make the selected tab panel fit layout
	 */
	function fitContent(container){
		return;
		/*var tab = $('>div.tabs-header ul.tabs li.tabs-selected', container);
		if (tab.length){
			var panelId = $.data(tab[0], 'tabs.tab').id;
			var panel = $('#'+panelId);
			var panels = $('>div.tabs-panels', container);
			if (panels.css('height').toLowerCase() != 'auto'){
				if ($.boxModel == true){
					panel.height(panels.height() - (panel.outerHeight()-panel.height()));
					panel.width(panels.width() - (panel.outerWidth()-panel.width()));
				} else {
					panel.height(panels.height());
					panel.width(panels.width());
				}
			}
			$('>div', panel).triggerHandler('_resize');
		}*/
		
	}
	
	// wrap the tabs header and body
	function wrapTabs(container) {
		$(container).addClass('tabs-container');
		$(container).wrapInner('<div class="tabs-panels"/>');
		$('<div class="tabs-header">'
				+ '<div class="tabs-scroller-left"></div>'
				+ '<div class="tabs-scroller-right"></div>'
				+ '<div class="tabs-wrap">'
				+ '<ul class="tabs"></ul>'
				+ '</div>'
				+ '</div>').prependTo(container);
		
		var header = $('>div.tabs-header', container);
		
		$('>div.tabs-panels>div', container).each(function(){
			if (!$(this).attr('id')) {
				$(this).attr('id', 'gen-tabs-panel' + $.fn.tauitabs.defaults.idSeed++);
			}
			
			var options = {
				id: $(this).attr('id'),
				title: $(this).attr('title'),
				content: null,
				href: $(this).attr('href'),
				closable: $(this).attr('closable') == 'true',
				icon: $(this).attr('icon'),
				selected: $(this).attr('selected') !== undefined,
				cache: $(this).attr('cache') == 'false' ? false : true,
				enable: $(this).attr('enable') == 'false' ? false : true
			};
			$(this).attr('title','');
			createTab(container, options);
		});
		
		$('.tabs-scroller-left, .tabs-scroller-right', header).hover(
			function(){$(this).addClass('tabs-scroller-over');},
			function(){$(this).removeClass('tabs-scroller-over');}
		).mousedown(function(){
			$(this).addClass('tabs-scroller-mousedown');
		}).mouseup(function(){
			$(this).removeClass('tabs-scroller-mousedown');
		});
		$(container).bind('_resize', function(){
			var opts = $.data(container, 'tabs').options;
			if (opts.fit == true){
				setSize(container);
				fitContent(container);
			}
			return false;
		});
	}
	
	function setProperties(container){
		var opts = $.data(container, 'tabs').options;
		var header = $('>div.tabs-header', container);
		var panels = $('>div.tabs-panels', container);
		var tabs = $('ul.tabs', header);
		
		if (opts.plain == true) {
			header.addClass('tabs-header-plain');
		} else {
			header.removeClass('tabs-header-plain');
		}
		if (opts.border == true){
			header.removeClass('tabs-header-noborder');
			panels.removeClass('tabs-panels-noborder');
		} else {
			header.addClass('tabs-header-noborder');
			panels.addClass('tabs-panels-noborder');
		}
		$('li', tabs).unbind('.tabs').bind('click.tabs', function(){
			//如果是enable=flase，那么就不执行click
			
			var tabAttr = $.data(this, 'tabs.tab');
			if(!tabAttr.enable)return;
			
			$('.tabs-selected', tabs).removeClass('tabs-selected');
			$(this).addClass('tabs-selected');
			$(this).blur();
			$('>div.tabs-panels>div', container).css('display', 'none');
			
			var wrap = $('.tabs-wrap', header);
			var leftPos = getTabLeftPosition(container, this);
			var left = leftPos - wrap.scrollLeft();
			var right = left + $(this).outerWidth();
			if (left < 0 || right > wrap.innerWidth()) {
				var pos = Math.min(
						leftPos - (wrap.width()-$(this).width()) / 2,
						getMaxScrollWidth(container)
				);
				wrap.animate({scrollLeft:(pos)}, opts.scrollDuration);
			}
			
			
			var panel = $('#' + tabAttr.id);
			//切换时对隐藏tab中的热键进行注销
			var panelSiblings = panel.siblings("div");
			for(var j = 0 ; j < panelSiblings.length; j ++){
				var panelSibling = panelSiblings.eq(j);
				var buttons = panelSibling.find(":button");
				for(var x = 0 ; x < buttons.length; x++){
					var button = buttons.eq(x);
					if(button.attr("hotKey") && hotKeyregister){
						hotKeyregister.remove(buttons.eq(x).attr("hotKey"));
					}
				}
			}
			panel.css('display', 'block');
			//当选择tab页的时候进行热键注册,李永顺修改
			$(":button",panel).each(function(){
				var _this = this;
				var _$this = $(this);
				if(!_$this.is(':hidden') && !this.disabled){
					var _this = this;
					var hotKey = $(this).attr('hotKey');
					if(hotKey && hotKeyregister){
						hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
					}
				}
			});
			
			$('div[fit=true]:first ,form[fit=true]:first',panel).triggerHandler('_resize');
			
			if (tabAttr.href && (!tabAttr.loaded || !tabAttr.cache)) {
				panel.load(tabAttr.href, null, function(){
//					if ($.parser){
//						$.parser.parse(panel);
//					}
					opts.onLoad.apply(this, arguments);
					tabAttr.loaded = true;
				});
			}
			
			fitContent(container);
			opts.onSelect.call(panel, tabAttr.id);
		});
		
		$('a.tabs-close', tabs).unbind('.tabs').bind('click.tabs', function(){
			var elem = $(this).parent()[0];
			var tabAttr = $.data(elem, 'tabs.tab');
			if(!tabAttr.enable)return;
			
			closeTab(container, tabAttr.title,elem);
		});
		
		$('.tabs-scroller-left', header).unbind('.tabs').bind('click.tabs', function(){
			var wrap = $('.tabs-wrap', header);
			var pos = wrap.scrollLeft() - opts.scrollIncrement;
			wrap.animate({scrollLeft:pos}, opts.scrollDuration);
		});
		
		$('.tabs-scroller-right', header).unbind('.tabs').bind('click.tabs', function(){
			var wrap = $('.tabs-wrap', header);
			var pos = Math.min(
					wrap.scrollLeft() + opts.scrollIncrement,
					getMaxScrollWidth(container)
			);
			wrap.animate({scrollLeft:pos}, opts.scrollDuration);
		});
	}
	
	function createTab(container, options) {
		var header = $('>div.tabs-header', container);
		var tabs = $('ul.tabs', header);
		
		var tab = $('<li></li>');
		var tab_span = $('<span></span>').html(options.title);
		var tab_a = $('<a class="tabs-inner '+(options.enable?'':'disabled')+'"></a>')
				.attr('href', 'javascript:void(0)')
				.append(tab_span);
		if(Base.globvar.indexStyle == "default"){
			tab_a.addClass("tabs-inner_163");
			tab.addClass("tabs_163");
		}
		tab.append(tab_a).appendTo(tabs);
		
		if (options.closable) {
			tab_span.addClass('tabs-closable');
			tab_a.after('<a href="javascript:void(0)" class="tabs-close '+(options.enable?'':'disabled')+'"></a>');
		}
		if (options.icon) {
			tab_span.addClass('tabs-with-icon');
			tab_span.after($('<span/>').addClass('tabs-icon').addClass(options.icon));
		}
		if (options.selected) {
			tab.addClass('tabs-selected');
		}
		if (options.content) {
			$('#' + options.id).html(options.content);
		}
		$('#' + options.id).removeAttr('title');
		$.data(tab[0], 'tabs.tab', {
			id: options.id,
			title: options.title,
			href: options.href,
			loaded: false,
			cache: options.cache,
			enable:options.enable
		});
	}
	
	function addTab(container, options) {
		options = $.extend({
			id: null,
			title: '',
			content: '',
			href: null,
			cache: true,
			icon: null,
			closable: false,
			selected: true,
			height: 'auto',
			width: 'auto',
			enable:true
		}, options || {});
		
		if (options.selected) {
			$('.tabs-header .tabs-wrap .tabs li', container).removeClass('tabs-selected');
		}
		options.id = options.id || 'gen-tabs-panel' + $.fn.tauitabs.defaults.idSeed++;
		
		$('<div></div>').attr('id', options.id)
				.attr('title', options.title)
				.css('overflow','auto')
				.height(options.height)
				.width(options.width)
				.appendTo($('>div.tabs-panels', container));
		createTab(container, options);
//		setScrollers(container);
	}
	
	// close a tab with specified title
	function closeTab(container, title,o) {
		var opts = $.data(container, 'tabs').options;
		var elem;
		if(typeof title == "string"){
			if(o){
				elem = o;
			}else{
				elem = $('>div.tabs-header li:has(a span:contains("' + title + '"))', container)[0];
			}
		}else if(typeof title=="object"){
			$('>div.tabs-header li',container).each(function(){
					var tabAttr = $.data(this, 'tabs.tab');		
					if(tabAttr.id==title.id){
						elem = this;
						return false;
					}
			});
		}
		if (!elem) return;
		var tabAttr = $.data(elem, 'tabs.tab');
		var panel = $('#' + tabAttr.id);
		
		if (opts.onClose.call(panel, tabAttr.id) == false) return;
		
		var selected = $(elem).hasClass('tabs-selected');
		$.removeData(elem, 'tabs.tab');
		var frame=$('iframe', panel);
		if(frame.length>0){
			frame[0].contentWindow.document.$ = null;
			frame[0].contentWindow.document.jQuery = null;
			frame[0].contentWindow.Ta = null;
			frame[0].contentWindow.document.write('');
			frame[0].contentWindow.close();
			frame[0] = null;
			frame.remove();
		}
		$(elem).remove();
		panel.remove();
		setSize(container);
		if (selected) {
			selectTab(container);
		} else {
			var wrap = $('>div.tabs-header .tabs-wrap', container);
			var pos = Math.min(
					wrap.scrollLeft(),
					getMaxScrollWidth(container)
			);
			wrap.animate({scrollLeft:pos}, opts.scrollDuration);
		}
		if($.browser.msie){CollectGarbage();}
	}
	// hide a tab with tabid
	function hideTab(container, id) {
		var opts = $.data(container, 'tabs').options;
		var elem;
		$('>div.tabs-header li',container).each(function(){
			var tabAttr = $.data(this, 'tabs.tab');		
			if(tabAttr.id == id){
				elem = this;
				return false;
			}
		});
		if (!elem) return;
		//if($(elem).is(':hidden'))return ;//如果非隐藏tab直接不处理
		if($(elem).hidden)return ;
		var tabAttr = $.data(elem, 'tabs.tab');
		var panel = $('#' + tabAttr.id);
		
		var selected = $(elem).hasClass('tabs-selected');
		$(elem).hide();
		setSize(container);
		if (selected) {
			panel.hide();
			selectTab(container);
		} else {
			panel.hide();
//			var wrap = $('>div.tabs-header .tabs-wrap', container);
//			var pos = Math.min(
//					wrap.scrollLeft(),
//					getMaxScrollWidth(container)
//			);
//			wrap.animate({scrollLeft:pos}, opts.scrollDuration);
		}
	}
	// show a tab with tabid
	function showTab(container, id) {
		var opts = $.data(container, 'tabs').options;
		var elem;
		$('>div.tabs-header li',container).each(function(){
			var tabAttr = $.data(this, 'tabs.tab');		
			if(tabAttr.id == id){
				elem = this;
				return false;
			}
		});
		if (!elem) return;
		if(!$(elem).is(':hidden'))return ;//如果非隐藏tab直接不处理
		
		var tabAttr = $.data(elem, 'tabs.tab');
		var panel = $('#' + tabAttr.id);
		
		$(elem).show();
		setSize(container);
		
		var wrap = $('>div.tabs-header .tabs-wrap', container);
		var pos = Math.min(
				wrap.scrollLeft(),
				getMaxScrollWidth(container)
		);
		wrap.animate({scrollLeft:pos}, opts.scrollDuration);
	}	
	// active the selected tab item, if no selected item then active the first item
	function selectTab(container, title){
		if (title) {
			if(typeof(title) == "string"){
				var elem = $('>div.tabs-header li:has(a span:contains("' + title + '"))', container)[0];
				if (elem) {
					$(elem).trigger('click');
				}
			}else{
				$('>div.tabs-header li',container).each(function(){
					var tabAttr = $.data(this, 'tabs.tab');		
					if(tabAttr.id==title.id){
						$(this).trigger('click');
						return false;
					}
				});
					
			}
		} else {
			var tabs = $('>div.tabs-header ul.tabs', container);
			if ($('.tabs-selected', tabs).length == 0) {
				$('li a', tabs).not('.disabled').not(':hidden').eq(0).parent().trigger('click');
			} else {
				var t = $('.tabs-selected', tabs);
				if(!t.is(':hidden')){
					t.trigger('click');
				}else{
					$('li a', tabs).not('.disabled').not(':hidden').eq(0).parent().trigger('click');
				}
			}
		}
	}
	
	function exists(container, title){
		if(title && typeof(title)!='string'){
			var exist = false;
			$('>div.tabs-header li',container).each(function(){
					var tabAttr = $.data(this, 'tabs.tab');		
					if(tabAttr.id==title.id){
						exist = true;
						return false;
					}
			});
			return exist;
		}
		return $('>div.tabs-header li:has(a span:contains("' + title + '"))', container).length > 0;
	}
	function setTitle(container,tabid, title){
		if(tabid){
			$('>div.tabs-header li',container).each(function(){
					var tabAttr = $.data(this, 'tabs.tab');		
					if(tabAttr.id==tabid){
						$('>a span',$(this)).html(title);
						return false;
					}
			});
		}
	}
	function enableTab(container,tabid,enable){
		if(tabid){
			$('>div.tabs-header li',container).each(function(){
					var tabAttr = $.data(this, 'tabs.tab');	
					if(tabAttr.id==tabid){
						tabAttr.enable = enable;
						$.data(this, 'tabs.tab',tabAttr);
						var $a = $('>a',$(this));
						if(enable){
							if($a.hasClass('disabled'))
								$a.removeClass('disabled');
						}else{
							if(!$a.hasClass('disabled')){
								$a.addClass('disabled');
							}
						}
						return false;
					}
			});
		}		
	}
	$.fn.tauitabs = function(options, param){
		if (typeof options == 'string') {
			switch(options) {
				case 'resize':
					return this.each(function(){
						setSize(this);
					});
				case 'add':
					return this.each(function(){
						addTab(this, param);
						$(this).tauitabs();
					});
				case 'close':
					return this.each(function(){
						closeTab(this, param);
					});
				case 'select':
					return this.each(function(){
						selectTab(this, param);
					});
				case 'exists':
					return exists(this[0], param);
				case 'setTitle':
					return setTitle(this[0], param.tabid, param.title);	
				case 'enableTab':
					return enableTab(this[0], param.tabid, param.enable);
				case 'hideTab':
					return hideTab(this[0],param);		
				case 'showTab':
					return showTab(this[0],param);									
			}
		}
		
		options = options || {};
		
		return this.each(function(){
			var state = $.data(this, 'tabs');
			var opts;
			if (state) {
				opts = $.extend(state.options, options);
				state.options = opts;
			} else {
				var t = $(this);
				opts = $.extend({},$.fn.tauitabs.defaults, {
					width: (parseInt(t.css('width')) || undefined),
					height: (parseInt(t.css('height')) || undefined),
					heightDiff: (t.attr('heightDiff')|| 0),
					fit: (t.attr('fit') ? t.attr('fit') == 'true' : undefined),
					border: (t.attr('border') ? t.attr('border') == 'true' : undefined),
					plain: (t.attr('plain') ? t.attr('plain') == 'true' : undefined),
					onSelect:(t.attr('onSelect') && eval(t.attr('onSelect'))) || function(){},
					onClose:(t.attr('onClose')&& eval(t.attr('onClose'))) || function(){},
					onLoad: t.attr('onLoad')&& eval(t.attr('onLoad')) || function(){}
				}, options);
				wrapTabs(this);
				$.data(this, 'tabs', {
					options: opts
				});
			}
			
			setProperties(this);
			setSize(this);
			var _this = this;
			if(opts.fit && this.parentNode.tagName.toLowerCase()=='body'){
				$(window).unbind('.tauitabs').bind('resize.tauitabs', function(){
					$(_this).triggerHandler('_resize');
				});
			}
			selectTab(this);
		});
	};
	
	$.fn.tauitabs.defaults = {
		width: 'auto',
		height: 'auto',
		heightDiff:0,
		idSeed: 0,
		plain: false,
		fit: false,
		border: true,
		scrollIncrement: 200,
		scrollDuration: 300,
		onLoad: function(){},
		onSelect: function(title){},
		onClose: function(title){}
	};
}));