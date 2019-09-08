(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","dialog","panel","draggable"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function setSize(target, param){
		$(target).ta3panel('resize');
	}
	
	/**
	 * create and initialize window, the window is created based on panel component 
	 */
	function init(target, options){
		var state = $.data(target, 'window');
		var opts;
		if (state){
			opts = $.extend(state.opts, options);
		} else {
			var t = $(target);
			opts = $.extend({}, $.fn.window.defaults, {
				title: t.attr('title'),
				collapsible: (t.attr('collapsible') ? t.attr('collapsible') == 'true' : undefined),
				minimizable: (t.attr('minimizable') ? t.attr('minimizable') == 'true' : undefined),
				maximizable: (t.attr('maximizable') ? t.attr('maximizable') == 'true' : undefined),
				closable: (t.attr('closable') ? t.attr('closable') == 'true' : undefined),
				closed: (t.attr('closed') ? t.attr('closed') == 'true' : undefined),
				shadow: (t.attr('shadow') ? t.attr('shadow') == 'true' : undefined),
				modal: (t.attr('modal') ? t.attr('modal') == 'true' : undefined)
			}, options);
			//liys add 弹出框宽度增加百分比配置，兼容屏幕分辨率
			if(opts.width && isNaN(opts.width)){
				if(opts.width.lastIndexOf("%") == opts.width.length -1){
					var tem = opts.width.substring(0,opts.width.length-1);
					if(!isNaN(tem)){
						tem = tem/100;
					}
					opts.width = Math.ceil(getPageArea().width * tem);
				}
			}
			//liys add 弹出框高度增加百分比配置，兼容屏幕分辨率
			if(opts.height && isNaN(opts.height)){
				if(opts.height.lastIndexOf("%") == opts.height.length -1){
					var tem = opts.height.substring(0,opts.height.length-1);
					if(!isNaN(tem)){
						tem = tem/100;
					}
					opts.height = Math.ceil(getPageArea().height * tem);
				}
			}
			$(target).attr('title', '');
			state = $.data(target, 'window', {});
		}
		
		// create window
		var win = $(target).ta3panel($.extend({}, opts, {
			border: false,
			doSize: true,	// size the panel, the property undefined in window component
			closed: true,	// close the panel
			cls: 'window',
			headerCls: 'window-header',
			bodyCls: 'window-body',
			onBeforeDestroy: function(){
				if (opts.onBeforeDestroy){
					if (opts.onBeforeDestroy.call(target) == false) return false;
				}
				var state = $.data(target, 'window');
				if (state.shadow) state.shadow.remove();
				if (state.mask) state.mask.remove();
			},
			onClose: function(){
				var state = $.data(target, 'window');
				if (state.shadow) state.shadow.hide();
				if (state.mask) state.mask.hide();
				
				if (opts.onClose) opts.onClose.call(target);
			},
			onOpen: function(){
				var state = $.data(target, 'window');
				if (state.mask){
					state.mask.css({
						display:'block',
						zIndex: $.fn.window.defaults.zIndex++
					});
				}
				if (state.shadow){
					state.shadow.css({
						display:'block',
						zIndex: $.fn.window.defaults.zIndex++,
						left: state.options.left,
						top: state.options.top,
						width: state.window.outerWidth(true),
						height: state.window.outerHeight(true)
					});
				}
				state.window.css('z-index', $.fn.window.defaults.zIndex++);
//				if (state.mask) state.mask.show();
				
				if (opts.onOpen) opts.onOpen.call(target);
			},
			onResize: function(width, height){
				var state = $.data(target, 'window');
				if (state.shadow){
					state.shadow.css({
						left: state.options.left,
						top: state.options.top,
						width: state.window.outerWidth(true),
						height: state.window.outerHeight(true)
					});
				}
				
				if (opts.onResize) opts.onResize.call(target, width, height);
			},
			onMove: function(left, top){
				var state = $.data(target, 'window');
				if (state.shadow){
					state.shadow.css({
						left: state.options.left,
						top: state.options.top
					});
				}
				
				if (opts.onMove) opts.onMove.call(target, left, top);
			},
			onMinimize: function(){
				var state = $.data(target, 'window');
				if (state.shadow) state.shadow.hide();
				if (state.mask) state.mask.hide();
				
				if (opts.onMinimize) opts.onMinimize.call(target);
			},
			onBeforeCollapse: function(){
				if (opts.onBeforeCollapse){
					if (opts.onBeforeCollapse.call(target) == false) return false;
				}
				var state = $.data(target, 'window');
				if (state.shadow) state.shadow.hide();
			},
			onExpand: function(){
				var state = $.data(target, 'window');
				if (state.shadow) state.shadow.show();
				if (opts.onExpand) opts.onExpand.call(target);
			}
		}));
		
		// save the window state
		state.options = win.ta3panel('options');
		state.opts = opts;
		state.window = win.ta3panel('panel');
		
		// create mask
		if (state.mask) state.mask.remove();
		if (opts.modal == true){
			state.mask = $('<div class="window-mask"></div>').appendTo('body');
			state.mask.css({
//				zIndex: $.fn.window.defaults.zIndex++,
				width: getPageArea().width,
				height: getPageArea().height,
				display: 'none'
			});
		}
		
		// create shadow
		if (state.shadow) state.shadow.remove();
		if (opts.shadow == true){
			state.shadow = $('<div class="window-shadow"></div>').insertAfter(state.window);
			state.shadow.css({
//				zIndex: $.fn.window.defaults.zIndex++,
				display: 'none'
			});
		}
		
//		state.window.css('z-index', $.fn.window.defaults.zIndex++);
		
		
		// if require center the window
		if (state.options.left == null){
			var width = state.options.width;
			if (isNaN(width)){
				width = state.window.outerWidth(true);
			}
			state.options.left = ($(window).width() - width) / 2 + $(document).scrollLeft();
		}
		if (state.options.top == null){
			var height = state.window.height();
			if (isNaN(height)){
				height = state.window.outerHeight(true);
			}
			state.options.top = ($(window).height() - height) / 2 + $(document).scrollTop();
		}
		win.window('move');
		
		if (state.opts.closed == false){
			win.window('open');	// open the window
		}
	}
	
	/**
	 * set window drag and resize property
	 */
	function setProperties(target){
		var state = $.data(target, 'window');
		
		state.window.draggable({
			handle: '>div.panel-header>div.panel-title',
			disabled: state.options.draggable == false,
			onStartDrag: function(e){
				if (state.mask) state.mask.css('z-index', $.fn.window.defaults.zIndex++);
				if (state.shadow) state.shadow.css('z-index', $.fn.window.defaults.zIndex++);
				state.window.css('z-index', $.fn.window.defaults.zIndex++);
				
				if (!state.proxy){
					state.proxy = $('<div class="window-proxy"></div>').insertAfter(state.window);
				}
				state.proxy.css({
					display:'none',
					zIndex: $.fn.window.defaults.zIndex++,
					left: e.data.left,
					top: e.data.top,
					width: ($.boxModel==true ? (state.window.outerWidth(true)-(state.proxy.outerWidth(true)-state.proxy.width())) : state.window.outerWidth(true)),
					height: ($.boxModel==true ? (state.window.outerHeight(true)-(state.proxy.outerHeight(true)-state.proxy.height())) : state.window.outerHeight(true))
				});
				setTimeout(function(){
					if (state.proxy) state.proxy.show();
				}, 500);
			},
			onDrag: function(e){
				state.proxy.css({
					display:'block',
					left: e.data.left,
					top: e.data.top
				});
				state.window.css({
					display:'none'
				});
				state.shadow.css({
					display:'none'
				});
				return false;
			},
			onStopDrag: function(e){
				if(e.data.left<0)e.data.left = 0;
				if(e.data.top<0)e.data.top = 0;
				if($(window).height()-e.data.top<30)e.data.top = $(window).height()-30;
				if($(window).width()-e.data.left<60)e.data.left = $(window).width()-60;
				state.options.left = e.data.left;
				state.options.top = e.data.top;
				$(target).window('move');
				state.proxy.remove();
				state.proxy = null;
				state.window.css({
					display:'block'
				});
				state.shadow.css({
					display:'block'
				});
				//如果window被选中需要触发事件，要在这里添加
			}
		});
		
		state.window.resizable({
			disabled: state.options.resizable == false,
			onStartResize:function(e){
				if (!state.proxy){
					state.proxy = $('<div class="window-proxy"></div>').insertAfter(state.window);
				}
				state.proxy.css({
					zIndex: $.fn.window.defaults.zIndex++,
					left: e.data.left,
					top: e.data.top,
					width: ($.boxModel==true ? (e.data.width-(state.proxy.outerWidth(true)-state.proxy.width())) : e.data.width),
					height: ($.boxModel==true ? (e.data.height-(state.proxy.outerHeight(true)-state.proxy.height())) : e.data.height)
				});
			},
			onResize: function(e){
				state.proxy.css({
					left: e.data.left,
					top: e.data.top,
					width: ($.boxModel==true ? (e.data.width-(state.proxy.outerWidth(true)-state.proxy.width())) : e.data.width),
					height: ($.boxModel==true ? (e.data.height-(state.proxy.outerHeight(true)-state.proxy.height())) : e.data.height)
				});
				return false;
			},
			onStopResize: function(e){
				state.options.left = e.data.left;
				state.options.top = e.data.top;
				state.options.width = e.data.width;
				state.options.height = e.data.height;
				setSize(target);
				state.proxy.remove();
				state.proxy = null;
			}
		});
	}
	
	function getPageArea() {
		if (document.compatMode == 'BackCompat') {
			return {
				width: Math.max(document.body.scrollWidth, document.body.clientWidth),
				height: Math.max(document.body.scrollHeight, document.body.clientHeight)
			};
		} else {
			return {
				width: Math.max(document.documentElement.scrollWidth, document.documentElement.clientWidth),
				height: Math.max(document.documentElement.scrollHeight, document.documentElement.clientHeight)
			};
		}
	}
	
	// when window resize, reset the width and height of the window's mask
	$(window).resize(function(){
		$('.window-mask').css({
			width: $(window).width(),
			height: $(window).height()
		});
		setTimeout(function(){
			$('.window-mask').css({
				width: getPageArea().width,
				height: getPageArea().height
			});
		}, 50);
	});
	
	$.fn.window = function(options, param){
		if (typeof options == 'string'){
			switch(options){
			case 'options':
				return $.data(this[0], 'window').options;
			case 'window':
				return $.data(this[0], 'window').window;
			case 'setTitle':
				return this.each(function(){
					$(this).ta3panel('setTitle', param);
				});
			case 'open':
				return this.each(function(){
					$(this).ta3panel('open', param);
				});
			case 'close':
				return this.each(function(){
					$(this).ta3panel('close', param);
				});
			case 'destroy':
				return this.each(function(){
					$(this).ta3panel('destroy', param);
				});
			case 'refresh':
				return this.each(function(){
					$(this).ta3panel('refresh');
				});
			case 'resize':
				return this.each(function(){
					$(this).ta3panel('resize', param);
				});
			case 'move':
				return this.each(function(){
					$(this).ta3panel('move', param);
				});
			case 'maximize':
				return this.each(function(){
					$(this).ta3panel('maximize');
				});
			case 'minimize':
				return this.each(function(){
					$(this).ta3panel('minimize');
				});
			case 'restore':
				return this.each(function(){
					$(this).ta3panel('restore');
				});
			case 'collapse':
				return this.each(function(){
					$(this).ta3panel('collapse', param);
				});
			case 'expand':
				return this.each(function(){
					$(this).ta3panel('expand', param);
				});
			}
		}
		
		options = options || {};
		return this.each(function(){
			init(this, options);
			setProperties(this);
		});
	};
	
	$.fn.window.defaults = {
		zIndex: 9000,
		draggable: true,
		resizable: true,
		shadow: true,
		modal: false,
		
		// window's property which difference from panel
		title: 'New Window',
		collapsible: true,
		minimizable: true,
		maximizable: true,
		closable: true,
		closed: false
	};
}));