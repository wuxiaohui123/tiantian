(function (factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as anonymous module.
		define(['jquery'], factory);
	} else {
		// Browser globals.
		factory(jQuery);
	}
}(function ($) {

	var pluses = /\+/g;

	function encode(s) {
		return config.raw ? s : encodeURIComponent(s);
	}

	function decode(s) {
		return config.raw ? s : decodeURIComponent(s);
	}

	function stringifyCookieValue(value) {
		return encode(config.json ? JSON.stringify(value) : String(value));
	}

	function parseCookieValue(s) {
		if (s.indexOf('"') === 0) {
			// This is a quoted cookie as according to RFC2068, unescape...
			s = s.slice(1, -1).replace(/\\"/g, '"').replace(/\\\\/g, '\\');
		}

		try {
			// Replace server-side written pluses with spaces.
			// If we can't decode the cookie, ignore it, it's unusable.
			s = decodeURIComponent(s.replace(pluses, ' '));
		} catch(e) {
			return;
		}

		try {
			// If we can't parse the cookie, ignore it, it's unusable.
			return config.json ? JSON.parse(s) : s;
		} catch(e) {}
	}

	function read(s, converter) {
		var value = config.raw ? s : parseCookieValue(s);
		return $.isFunction(converter) ? converter(value) : value;
	}

	var config = $.cookie = function (key, value, options) {

		// Write
		if (value !== undefined && !$.isFunction(value)) {
			options = $.extend({}, config.defaults, options);

			if (typeof options.expires === 'number') {
				var days = options.expires, t = options.expires = new Date();
				t.setDate(t.getDate() + days);
			}

			return (document.cookie = [
				encode(key), '=', stringifyCookieValue(value),
				options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
				options.path    ? '; path=' + options.path : '',
				options.domain  ? '; domain=' + options.domain : '',
				options.secure  ? '; secure' : ''
			].join(''));
		}

		// Read

		var result = key ? undefined : {};

		// To prevent the for loop in the first place assign an empty array
		// in case there are no cookies at all. Also prevents odd result when
		// calling $.cookie().
		var cookies = document.cookie ? document.cookie.split('; ') : [];

		for (var i = 0, l = cookies.length; i < l; i++) {
			var parts = cookies[i].split('=');
			var name = decode(parts.shift());
			var cookie = parts.join('=');

			if (key && key === name) {
				// If second argument (value) is a function it's a converter...
				result = read(cookie, value);
				break;
			}

			// Prevent storing a cookie that we couldn't decode.
			if (!key && (cookie = read(cookie)) !== undefined) {
				result[name] = cookie;
			}
		}

		return result;
	};

	config.defaults = {};

	$.removeCookie = function (key, options) {
		if ($.cookie(key) !== undefined) {
			// Must not alter options, thus extending a fresh object...
			$.cookie(key, '', $.extend({}, options, { expires: -1 }));
			return true;
		}
		return false;
	};

}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	function drag(e){
		var opts = $.data(e.data.target, 'draggable').options;
		
		var dragData = e.data;
		var left = dragData.startLeft + e.pageX - dragData.startX;
		var top = dragData.startTop + e.pageY - dragData.startY;
		
		if (opts.deltaX != null && opts.deltaX != undefined){
			left = e.pageX + opts.deltaX;
		}
		if (opts.deltaY != null && opts.deltaY != undefined){
			top = e.pageY + opts.deltaY;
		}
		
		if (e.data.parnet != document.body) {
			if ($.boxModel == true) {
				left += $(e.data.parent).scrollLeft();
				top += $(e.data.parent).scrollTop();
			}
		}
		
		if (opts.axis == 'h') {
			dragData.left = left;
		} else if (opts.axis == 'v') {
			dragData.top = top;
		} else {
			dragData.left = left;
			dragData.top = top;
		}
	}
	
	function applyDrag(e){
		var opts = $.data(e.data.target, 'draggable').options;
		var proxy = $.data(e.data.target, 'draggable').proxy;
		if (proxy){
			proxy.css('cursor', opts.cursor);
		} else {
			proxy = $(e.data.target);
			$.data(e.data.target, 'draggable').handle.css('cursor', opts.cursor);
		}
		proxy.css({
			left:e.data.left,
			top:e.data.top
		});
	}
	
	function doDown(e){
		var opts = $.data(e.data.target, 'draggable').options;
		
		var droppables = $('.droppable').filter(function(){
			return e.data.target != this;
		}).filter(function(){
			var accept = $.data(this, 'droppable').options.accept;
			if (accept){
				return $(accept).filter(function(){
					return this == e.data.target;
				}).length > 0;
			} else {
				return true;
			}
		});
		$.data(e.data.target, 'draggable').droppables = droppables;
		
		var proxy = $.data(e.data.target, 'draggable').proxy;
		if (!proxy){
			if (opts.proxy){
				if (opts.proxy == 'clone'){
					proxy = $(e.data.target).clone().insertAfter(e.data.target);
				} else {
					proxy = opts.proxy.call(e.data.target, e.data.target);
				}
				$.data(e.data.target, 'draggable').proxy = proxy;
			} else {
				proxy = $(e.data.target);
			}
		}
		
		proxy.css('position', 'absolute');
		drag(e);
		applyDrag(e);
		
		opts.onStartDrag.call(e.data.target, e);
		return false;
	}
	
	function doMove(e){
		
		drag(e);
		if ($.data(e.data.target, 'draggable').options.onDrag.call(e.data.target, e) != false){
			applyDrag(e);
		}
		
		var source = e.data.target;
		$.data(e.data.target, 'draggable').droppables.each(function(){
			var dropObj = $(this);
			var p2 = $(this).offset();
			if (e.pageX > p2.left && e.pageX < p2.left + dropObj.outerWidth(true)
					&& e.pageY > p2.top && e.pageY < p2.top + dropObj.outerHeight(true)){
				if (!this.entered){
					$(this).trigger('_dragenter', [source]);
					this.entered = true;
				}
				$(this).trigger('_dragover', [source]);
			} else {
				if (this.entered){
					$(this).trigger('_dragleave', [source]);
					this.entered = false;
				}
			}
		});
		
		return false;
	}
	
	function doUp(e){
		drag(e);
		
		var proxy = $.data(e.data.target, 'draggable').proxy;
		var opts = $.data(e.data.target, 'draggable').options;
		if (opts.revert){
			if (checkDrop() == true){
				removeProxy();
				$(e.data.target).css({
					position:e.data.startPosition,
					left:e.data.startLeft,
					top:e.data.startTop
				});
			} else {
				if (proxy){
					proxy.animate({
						left:e.data.startLeft,
						top:e.data.startTop
					}, function(){
						removeProxy();
					});
				} else {
					$(e.data.target).animate({
						left:e.data.startLeft,
						top:e.data.startTop
					}, function(){
						$(e.data.target).css('position', e.data.startPosition);
					});
				}
			}
		} else {
			$(e.data.target).css({
				position:'absolute',
				left:e.data.left,
				top:e.data.top
			});
			removeProxy();
			checkDrop();
		}
		
		
		
		opts.onStopDrag.call(e.data.target, e);
		
		function removeProxy(){
			if (proxy){
				proxy.remove();
			}
			$.data(e.data.target, 'draggable').proxy = null;
		}
		
		function checkDrop(){
			var dropped = false;
			$.data(e.data.target, 'draggable').droppables.each(function(){
				var dropObj = $(this);
				var p2 = $(this).offset();
				if (e.pageX > p2.left && e.pageX < p2.left + dropObj.outerWidth(true)
						&& e.pageY > p2.top && e.pageY < p2.top + dropObj.outerHeight(true)){
					if (opts.revert){
						$(e.data.target).css({
							position:e.data.startPosition,
							left:e.data.startLeft,
							top:e.data.startTop
						});
					}
					$(this).trigger('_drop', [e.data.target]);
					dropped = true;
					this.entered = false;
				}
			});
			return dropped;
		}
		
		$(document).unbind('.draggable');
		return false;
	}
	
	$.fn.draggable = function(options){
		if (typeof options == 'string'){
			switch(options){
			case 'options':
				return $.data(this[0], 'draggable').options;
			case 'proxy':
				return $.data(this[0], 'draggable').proxy;
			case 'enable':
				return this.each(function(){
					$(this).draggable({disabled:false});
				});
			case 'disable':
				return this.each(function(){
					$(this).draggable({disabled:true});
				});
			}
		}
		
		return this.each(function(){
//			$(this).css('position','absolute');
			
			var opts;
			var state = $.data(this, 'draggable');
			if (state) {
				state.handle.unbind('.draggable');
				opts = $.extend(state.options, options);
			} else {
				opts = $.extend({}, $.fn.draggable.defaults, options || {});
			}
			
			if (opts.disabled == true) {
				$(this).css('cursor', 'default');
				return;
			}
			
			var handle = null;
            if (typeof opts.handle == 'undefined' || opts.handle == null){
                handle = $(this);
            } else {
                handle = (typeof opts.handle == 'string' ? $(opts.handle, this) : handle);
            }
			$.data(this, 'draggable', {
				options: opts,
				handle: handle
			});
			
			// bind mouse event using event namespace draggable
			handle.bind('mousedown.draggable', {target:this}, onMouseDown);
			handle.bind('mousemove.draggable', {target:this}, onMouseMove);
			
			function onMouseDown(e) {
				if (checkArea(e) == false) return;

				var position = $(e.data.target).position();
				var data = {
					startPosition: $(e.data.target).css('position'),
					startLeft: position.left,
					startTop: position.top,
					left: position.left,
					top: position.top,
					startX: e.pageX,
					startY: e.pageY,
					target: e.data.target,
					parent: $(e.data.target).parent()[0]
				};
				
				$(document).bind('mousedown.draggable', data, doDown);
				$(document).bind('mousemove.draggable', data, doMove);
				$(document).bind('mouseup.draggable', data, doUp);
			}
			
			function onMouseMove(e) {
				if (checkArea(e)){
					$(this).css('cursor', opts.cursor);
				} else {
					$(this).css('cursor', 'default');
				}
			}
			
			// check if the handle can be dragged
			function checkArea(e) {
				var offset = $(handle).offset();
				var width = $(handle).outerWidth(true);
				var height = $(handle).outerHeight(true);
				var t = e.pageY - offset.top;
				var r = offset.left + width - e.pageX;
				var b = offset.top + height - e.pageY;
				var l = e.pageX - offset.left;
				
				return Math.min(t,r,b,l) > opts.edge;
			}
			
		});
	};
	
	$.fn.draggable.defaults = {
			proxy:null,	// 'clone' or a function that will create the proxy object, 
						// the function has the source parameter that indicate the source object dragged.
			revert:false,
			cursor:'move',
			deltaX:null,
			deltaY:null,
			handle: null,
			disabled: false,
			edge:0,
			axis:null,	// v or h
			
			onStartDrag: function(e){},
			onDrag: function(e){},
			onStopDrag: function(e){}
	};
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
// add the jquery instance method
$.fn.drag = function( str, arg, opts ){
	// figure out the event type
	var type = typeof str == "string" ? str : "",
	// figure out the event handler...
	fn = $.isFunction( str ) ? str : $.isFunction( arg ) ? arg : null;
	// fix the event type
	if ( type.indexOf("drag") !== 0 ) 
		type = "drag"+ type;
	// were options passed
	opts = ( str == fn ? arg : opts ) || {};
	// trigger or bind event handler
	return fn ? this.bind( type, opts, fn ) : this.trigger( type );
};

// local refs (increase compression)
var $event = $.event, 
$special = $event.special,
// configure the drag special event 
drag = $special.drag = {
	
	// these are the default settings
	defaults: {
		which: 1, // mouse button pressed to start drag sequence
		distance: 0, // distance dragged before dragstart
		not: ':input', // selector to suppress dragging on target elements
		handle: null, // selector to match handle target elements
		relative: false, // true to use "position", false to use "offset"
		drop: true, // false to suppress drop events, true or selector to allow
		click: false // false to suppress click events after dragend (no proxy)
	},
	
	// the key name for stored drag data
	datakey: "dragdata",
	
	// prevent bubbling for better performance
	noBubble: true,
	
	// count bound related events
	add: function( obj ){ 
		// read the interaction data
		var data = $.data( this, drag.datakey ),
		// read any passed options 
		opts = obj.data || {};
		// count another realted event
		data.related += 1;
		// extend data options bound with this event
		// don't iterate "opts" in case it is a node 
		$.each( drag.defaults, function( key, def ){
			if ( opts[ key ] !== undefined )
				data[ key ] = opts[ key ];
		});
	},
	
	// forget unbound related events
	remove: function(){
		$.data( this, drag.datakey ).related -= 1;
	},
	
	// configure interaction, capture settings
	setup: function(){
		// check for related events
		if ( $.data( this, drag.datakey ) ) 
			return;
		// initialize the drag data with copied defaults
		var data = $.extend({ related:0 }, drag.defaults );
		// store the interaction data
		$.data( this, drag.datakey, data );
		// bind the mousedown event, which starts drag interactions
		$event.add( this, "touchstart mousedown", drag.init, data );
		// prevent image dragging in IE...
		if ( this.attachEvent ) 
			this.attachEvent("ondragstart", drag.dontstart ); 
	},
	
	// destroy configured interaction
	teardown: function(){
		var data = $.data( this, drag.datakey ) || {};
		// check for related events
		if ( data.related ) 
			return;
		// remove the stored data
		$.removeData( this, drag.datakey );
		// remove the mousedown event
		$event.remove( this, "touchstart mousedown", drag.init );
		// enable text selection
		drag.textselect( true ); 
		// un-prevent image dragging in IE...
		if ( this.detachEvent ) 
			this.detachEvent("ondragstart", drag.dontstart ); 
	},
		
	// initialize the interaction
	init: function( event ){ 
		// sorry, only one touch at a time
		if ( drag.touched ) 
			return;
		// the drag/drop interaction data
		var dd = event.data, results;
		// check the which directive
		if ( event.which != 0 && dd.which > 0 && event.which != dd.which ) 
			return; 
		// check for suppressed selector
		if ( $( event.target ).is( dd.not ) ) 
			return;
		// check for handle selector
		if ( dd.handle && !$( event.target ).closest( dd.handle, event.currentTarget ).length ) 
			return;

		drag.touched = event.type == 'touchstart' ? this : null;
		dd.propagates = 1;
		dd.mousedown = this;
		dd.interactions = [ drag.interaction( this, dd ) ];
		dd.target = event.target;
		dd.pageX = event.pageX;
		dd.pageY = event.pageY;
		dd.dragging = null;
		// handle draginit event... 
		results = drag.hijack( event, "draginit", dd );
		// early cancel
		if ( !dd.propagates )
			return;
		// flatten the result set
		results = drag.flatten( results );
		// insert new interaction elements
		if ( results && results.length ){
			dd.interactions = [];
			$.each( results, function(){
				dd.interactions.push( drag.interaction( this, dd ) );
			});
		}
		// remember how many interactions are propagating
		dd.propagates = dd.interactions.length;
		// locate and init the drop targets
		if ( dd.drop !== false && $special.drop ) 
			$special.drop.handler( event, dd );
		// disable text selection
		drag.textselect( false ); 
		// bind additional events...
		if ( drag.touched )
			$event.add( drag.touched, "touchmove touchend", drag.handler, dd );
		else 
			$event.add( document, "mousemove mouseup", drag.handler, dd );
		// helps prevent text selection or scrolling
		if ( !drag.touched || dd.live )
			return false;
	},	
	
	// returns an interaction object
	interaction: function( elem, dd ){
		var offset = $( elem )[ dd.relative ? "position" : "offset" ]() || { top:0, left:0 };
		return {
			drag: elem, 
			callback: new drag.callback(), 
			droppable: [],
			offset: offset
		};
	},
	
	// handle drag-releatd DOM events
	handler: function( event ){ 
		// read the data before hijacking anything
		var dd = event.data;	
		// handle various events
		switch ( event.type ){
			// mousemove, check distance, start dragging
			case !dd.dragging && 'touchmove': 
				event.preventDefault();
			case !dd.dragging && 'mousemove':
				//  drag tolerance, x≤ + y≤ = distance≤
				if ( Math.pow(  event.pageX-dd.pageX, 2 ) + Math.pow(  event.pageY-dd.pageY, 2 ) < Math.pow( dd.distance, 2 ) ) 
					break; // distance tolerance not reached
				event.target = dd.target; // force target from "mousedown" event (fix distance issue)
				drag.hijack( event, "dragstart", dd ); // trigger "dragstart"
				if ( dd.propagates ) // "dragstart" not rejected
					dd.dragging = true; // activate interaction
			// mousemove, dragging
			case 'touchmove':
				event.preventDefault();
			case 'mousemove':
				if ( dd.dragging ){
					// trigger "drag"		
					drag.hijack( event, "drag", dd );
					if ( dd.propagates ){
						// manage drop events
						if ( dd.drop !== false && $special.drop )
							$special.drop.handler( event, dd ); // "dropstart", "dropend"							
						break; // "drag" not rejected, stop		
					}
					event.type = "mouseup"; // helps "drop" handler behave
				}
			// mouseup, stop dragging
			case 'touchend': 
			case 'mouseup': 
			default:
				if ( drag.touched )
					$event.remove( drag.touched, "touchmove touchend", drag.handler ); // remove touch events
				else 
					$event.remove( document, "mousemove mouseup", drag.handler ); // remove page events	
				if ( dd.dragging ){
					if ( dd.drop !== false && $special.drop )
						$special.drop.handler( event, dd ); // "drop"
					drag.hijack( event, "dragend", dd ); // trigger "dragend"	
				}
				drag.textselect( true ); // enable text selection
				// if suppressing click events...
				if ( dd.click === false && dd.dragging )
					$.data( dd.mousedown, "suppress.click", new Date().getTime() + 5 );
				dd.dragging = drag.touched = false; // deactivate element	
				break;
		}
	},
		
	// re-use event object for custom events
	hijack: function( event, type, dd, x, elem ){
		// not configured
		if ( !dd ) 
			return;
		// remember the original event and type
		var orig = { event:event.originalEvent, type:event.type },
		// is the event drag related or drog related?
		mode = type.indexOf("drop") ? "drag" : "drop",
		// iteration vars
		result, i = x || 0, ia, $elems, callback,
		len = !isNaN( x ) ? x : dd.interactions.length;
		// modify the event type
		event.type = type;
		// remove the original event
		event.originalEvent = null;
		// initialize the results
		dd.results = [];
		// handle each interacted element
		do if ( ia = dd.interactions[ i ] ){
			// validate the interaction
			if ( type !== "dragend" && ia.cancelled )
				continue;
			// set the dragdrop properties on the event object
			callback = drag.properties( event, dd, ia );
			// prepare for more results
			ia.results = [];
			// handle each element
			$( elem || ia[ mode ] || dd.droppable ).each(function( p, subject ){
				// identify drag or drop targets individually
				callback.target = subject;
				// force propagtion of the custom event
				event.isPropagationStopped = function(){ return false; };
				// handle the event	
				result = subject ? $event.dispatch.call( subject, event, callback ) : null;
				// stop the drag interaction for this element
				if ( result === false ){
					if ( mode == "drag" ){
						ia.cancelled = true;
						dd.propagates -= 1;
					}
					if ( type == "drop" ){
						ia[ mode ][p] = null;
					}
				}
				// assign any dropinit elements
				else if ( type == "dropinit" )
					ia.droppable.push( drag.element( result ) || subject );
				// accept a returned proxy element 
				if ( type == "dragstart" )
					ia.proxy = $( drag.element( result ) || ia.drag )[0];
				// remember this result	
				ia.results.push( result );
				// forget the event result, for recycling
				delete event.result;
				// break on cancelled handler
				if ( type !== "dropinit" )
					return result;
			});	
			// flatten the results	
			dd.results[ i ] = drag.flatten( ia.results );	
			// accept a set of valid drop targets
			if ( type == "dropinit" )
				ia.droppable = drag.flatten( ia.droppable );
			// locate drop targets
			if ( type == "dragstart" && !ia.cancelled )
				callback.update(); 
		}
		while ( ++i < len )
		// restore the original event & type
		event.type = orig.type;
		event.originalEvent = orig.event;
		// return all handler results
		return drag.flatten( dd.results );
	},
		
	// extend the callback object with drag/drop properties...
	properties: function( event, dd, ia ){		
		var obj = ia.callback;
		// elements
		obj.drag = ia.drag;
		obj.proxy = ia.proxy || ia.drag;
		// starting mouse position
		obj.startX = dd.pageX;
		obj.startY = dd.pageY;
		// current distance dragged
		obj.deltaX = event.pageX - dd.pageX;
		obj.deltaY = event.pageY - dd.pageY;
		// original element position
		obj.originalX = ia.offset.left;
		obj.originalY = ia.offset.top;
		// adjusted element position
		obj.offsetX = obj.originalX + obj.deltaX; 
		obj.offsetY = obj.originalY + obj.deltaY;
		// assign the drop targets information
		obj.drop = drag.flatten( ( ia.drop || [] ).slice() );
		obj.available = drag.flatten( ( ia.droppable || [] ).slice() );
		return obj;	
	},
	
	// determine is the argument is an element or jquery instance
	element: function( arg ){
		if ( arg && ( arg.jquery || arg.nodeType == 1 ) )
			return arg;
	},
	
	// flatten nested jquery objects and arrays into a single dimension array
	flatten: function( arr ){
		return $.map( arr, function( member ){
			return member && member.jquery ? $.makeArray( member ) : 
				member && member.length ? drag.flatten( member ) : member;
		});
	},
	
	// toggles text selection attributes ON (true) or OFF (false)
	textselect: function( bool ){ 
		$( document )[ bool ? "unbind" : "bind" ]("selectstart", drag.dontstart )
			.css("MozUserSelect", bool ? "" : "none" );
		// .attr("unselectable", bool ? "off" : "on" )
		document.unselectable = bool ? "off" : "on"; 
	},
	
	// suppress "selectstart" and "ondragstart" events
	dontstart: function(){ 
		return false; 
	},
	
	// a callback instance contructor
	callback: function(){}
	
};

// callback methods
drag.callback.prototype = {
	update: function(){
		if ( $special.drop && this.available.length )
			$.each( this.available, function( i ){
				$special.drop.locate( this, i );
			});
	}
};

// patch $.event.$dispatch to allow suppressing clicks
var $dispatch = $event.dispatch;
$event.dispatch = function( event ){
	if ( $.data( this, "suppress."+ event.type ) - new Date().getTime() > 0 ){
		$.removeData( this, "suppress."+ event.type );
		return;
	}
	return $dispatch.apply( this, arguments );
};

// event fix hooks for touch events...
var touchHooks = 
$event.fixHooks.touchstart = 
$event.fixHooks.touchmove = 
$event.fixHooks.touchend =
$event.fixHooks.touchcancel = {
	props: "clientX clientY pageX pageY screenX screenY".split( " " ),
	filter: function( event, orig ) {
		if ( orig ){
			var touched = ( orig.touches && orig.touches[0] )
				|| ( orig.changedTouches && orig.changedTouches[0] )
				|| null; 
			// iOS webkit: touchstart, touchmove, touchend
			if ( touched ) 
				$.each( touchHooks.props, function( i, prop ){
					event[ prop ] = touched[ prop ];
				});
		}
		return event;
	}
};

// share the same special event configuration with related events...
$special.draginit = $special.dragstart = $special.dragend = drag;

}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){

	$.extend(true, window, {
		hotKeyregister: hotKeyregister()
    });
	
	function hotKeyregister() {
		var all_shortcuts = {};
		function add(shortcut_combination,callback,opt) {
			//Provide a set of default options
			var default_options = {
				'type':'keydown',
				'propagate':false,
				'disable_in_input':true,
				'target':document,
				'keycode':false
			};
			if(!opt) opt = default_options;
			else {
				for(var dfo in default_options) {
					if(typeof opt[dfo] == 'undefined') opt[dfo] = default_options[dfo];
				}
			}

			var ele = opt.target;
			if(typeof opt.target == 'string') ele = document.getElementById(opt.target);
			var ths = this;
			shortcut_combination = shortcut_combination.toLowerCase();

			//The function to be called at keypress
			var func = function(e) {
				e = e || window.event;
				
				//Find Which key is pressed
				if (e.keyCode)code = e.keyCode;
				else if (e.which)code = e.which;
				var character = String.fromCharCode(code).toLowerCase();
				
				if(opt['disable_in_input']) { //Don't enable shortcut keys in Input, Textarea fields
					var element;
					if(e.target) element=e.target;
					else if(e.srcElement) element=e.srcElement;
					if(element.nodeType==3) element=element.parentNode;

					if(element.tagName == 'INPUT' || element.tagName == 'TEXTAREA'){
						if(e.ctrlKey && "a,z,c,x,y,v".indexOf(character)!=-1){//排除默认对文本的操作快捷
							return;
						}
					}
				}

				if(code == 188) character=","; //If the user presses , when the type is onkeydown
				if(code == 190) character="."; //If the user presses , when the type is onkeydown

				var keys = shortcut_combination.split("+");
				//Key Pressed - counts the number of valid keypresses - if it is same as the number of keys, the shortcut function is invoked
				var kp = 0;
				
				//Work around for stupid Shift key bug created by using lowercase - as a result the shift+num combination was broken
				var shift_nums = {
					"`":"~",
					"1":"!",
					"2":"@",
					"3":"#",
					"4":"$",
					"5":"%",
					"6":"^",
					"7":"&",
					"8":"*",
					"9":"(",
					"0":")",
					"-":"_",
					"=":"+",
					";":":",
					"'":"\"",
					",":"<",
					".":">",
					"/":"?",
					"\\":"|"
				};
				//Special Keys - and their codes
				var special_keys = {
					'esc':27,
					'escape':27,
					'tab':9,
					'space':32,
					'return':13,
					'enter':13,
					'backspace':8,
		
					'scrolllock':145,
					'scroll_lock':145,
					'scroll':145,
					'capslock':20,
					'caps_lock':20,
					'caps':20,
					'numlock':144,
					'num_lock':144,
					'num':144,
					
					'pause':19,
					'break':19,
					
					'insert':45,
					'home':36,
					'delete':46,
					'end':35,
					
					'pageup':33,
					'page_up':33,
					'pu':33,
		
					'pagedown':34,
					'page_down':34,
					'pd':34,
		
					'left':37,
					'up':38,
					'right':39,
					'down':40,
		
					'f1':112,
					'f2':113,
					'f3':114,
					'f4':115,
					'f5':116,
					'f6':117,
					'f7':118,
					'f8':119,
					'f9':120,
					'f10':121,
					'f11':122,
					'f12':123
				};
		
				var modifiers = { 
					shift: { wanted:false, pressed:false},
					ctrl : { wanted:false, pressed:false},
					alt  : { wanted:false, pressed:false},
					meta : { wanted:false, pressed:false}	//Meta is Mac specific
				};
	                        
				if(e.ctrlKey)	modifiers.ctrl.pressed = true;
				if(e.shiftKey)	modifiers.shift.pressed = true;
				if(e.altKey)	modifiers.alt.pressed = true;
				if(e.metaKey)   modifiers.meta.pressed = true;
	                        
				for(var i=0; k=keys[i],i<keys.length; i++) {
					//Modifiers
					if(k == 'ctrl' || k == 'control') {
						kp++;
						modifiers.ctrl.wanted = true;

					} else if(k == 'shift') {
						kp++;
						modifiers.shift.wanted = true;

					} else if(k == 'alt') {
						kp++;
						modifiers.alt.wanted = true;
					} else if(k == 'meta') {
						kp++;
						modifiers.meta.wanted = true;
					} else if(k.length > 1) { //If it is a special key
						if(special_keys[k] == code) kp++;
						
					} else if(opt['keycode']) {
						if(opt['keycode'] == code) kp++;

					} else { //The special keys did not match
						if(character == k) kp++;
						else {
							if(shift_nums[character] && e.shiftKey) { //Stupid Shift key bug created by using lowercase
								character = shift_nums[character]; 
								if(character == k) kp++;
							}
						}
					}
				}
				
				if(kp == keys.length && 
							modifiers.ctrl.pressed == modifiers.ctrl.wanted &&
							modifiers.shift.pressed == modifiers.shift.wanted &&
							modifiers.alt.pressed == modifiers.alt.wanted &&
							modifiers.meta.pressed == modifiers.meta.wanted) {
				    if(Base.globvar && !Base.globvar.isSubmitNow){
						callback(e);
					}
					if(!opt['propagate']) { //Stop the event
						//e.cancelBubble is supported by IE - this will kill the bubbling process.
						e.cancelBubble = true;
						e.returnValue = false;
		
						//e.stopPropagation works in Firefox.
						if (e.stopPropagation) {
							e.stopPropagation();
							e.preventDefault();
						}
						return false;
					}
				}
			};
			all_shortcuts[shortcut_combination] = {
				'callback':func, 
				'target':ele, 
				'event': opt['type']
			};
			//Attach the function with the event
			if(ele.addEventListener) ele.addEventListener(opt['type'], func, false);
			else if(ele.attachEvent) ele.attachEvent('on'+opt['type'], func);
			else ele['on'+opt['type']] = func;
		}
		
		
		function remove(shortcut_combination){
			shortcut_combination = shortcut_combination.toLowerCase();
			var binding = all_shortcuts[shortcut_combination];
			delete(all_shortcuts[shortcut_combination]);
			if(!binding) return;
			var type = binding['event'];
			var ele = binding['target'];
			var callback = binding['callback'];
			//liys修改，事件取消顺序与事件绑定不一致
			if(ele.removeEventListener) ele.removeEventListener(type, callback, false);
			else if(ele.detachEvent) ele.detachEvent('on'+type, callback);
			else ele['on'+type] = false;
		}
		
		return {
			"remove" :remove,
			"add": add,
			"all_shortcuts":all_shortcuts
		}
		
	}
	
}));
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		// AMD. Register as anonymous module.
		define(['jquery'], factory);
	} else {
		// Browser globals.
		factory(jQuery);
	}
}(function( $ ) {
	var	escapeable = /["\\\x00-\x1f\x7f-\x9f]/g,
		meta = {
			'\b': '\\b',
			'\t': '\\t',
			'\n': '\\n',
			'\f': '\\f',
			'\r': '\\r',
			'"' : '\\"',
			'\\': '\\\\'
		};

	/**
	 * jQuery.toJSON
	 * Converts the given argument into a JSON respresentation.
	 *
	 * @param o {Mixed} The json-serializble *thing* to be converted
	 *
	 * If an object has a toJSON prototype, that will be used to get the representation.
	 * Non-integer/string keys are skipped in the object, as are keys that point to a
	 * function.
	 *
	 */
	$.toJSON = typeof JSON === 'object' && JSON.stringify
		? JSON.stringify
		: function( o ) {

		if ( o === null ) {
			return 'null';
		}

		var type = typeof o;

		if ( type === 'undefined' ) {
			return undefined;
		}
		if ( type === 'number' || type === 'boolean' ) {
			return '' + o;
		}
		if ( type === 'string') {
			return $.quoteString( o );
		}
		if ( type === 'object' ) {
			if ( typeof o.toJSON === 'function' ) {
				return $.toJSON( o.toJSON() );
			}
			if ( o.constructor === Date ) {
				var	month = o.getUTCMonth() + 1,
					day = o.getUTCDate(),
					year = o.getUTCFullYear(),
					hours = o.getUTCHours(),
					minutes = o.getUTCMinutes(),
					seconds = o.getUTCSeconds(),
					milli = o.getUTCMilliseconds();

				if ( month < 10 ) {
					month = '0' + month;
				}
				if ( day < 10 ) {
					day = '0' + day;
				}
				if ( hours < 10 ) {
					hours = '0' + hours;
				}
				if ( minutes < 10 ) {
					minutes = '0' + minutes;
				}
				if ( seconds < 10 ) {
					seconds = '0' + seconds;
				}
				if ( milli < 100 ) {
					milli = '0' + milli;
				}
				if ( milli < 10 ) {
					milli = '0' + milli;
				}
				return '"' + year + '-' + month + '-' + day + 'T' +
					hours + ':' + minutes + ':' + seconds +
					'.' + milli + 'Z"';
			}
			if ( o.constructor === Array ) {
				var ret = [];
				for ( var i = 0; i < o.length; i++ ) {
					ret.push( $.toJSON( o[i] ) || 'null' );
				}
				return '[' + ret.join(',') + ']';
			}
			var	name,
				val,
				pairs = [];
			for ( var k in o ) {
				type = typeof k;
				if ( type === 'number' ) {
					name = '"' + k + '"';
				} else if (type === 'string') {
					name = $.quoteString(k);
				} else {
					// Keys must be numerical or string. Skip others
					continue;
				}
				type = typeof o[k];

				if ( type === 'function' || type === 'undefined' ) {
					// Invalid values like these return undefined
					// from toJSON, however those object members
					// shouldn't be included in the JSON string at all.
					continue;
				}
				val = $.toJSON( o[k] );
				pairs.push( name + ':' + val );
			}
			return '{' + pairs.join( ',' ) + '}';
		}
	};

	/**
	 * jQuery.evalJSON
	 * Evaluates a given piece of json source.
	 *
	 * @param src {String}
	 */
	$.evalJSON = typeof JSON === 'object' && JSON.parse
		? JSON.parse
		: function( src ) {
		return eval('(' + src + ')');
	};

	/**
	 * jQuery.secureEvalJSON
	 * Evals JSON in a way that is *more* secure.
	 *
	 * @param src {String}
	 */
	$.secureEvalJSON = typeof JSON === 'object' && JSON.parse
		? JSON.parse
		: function( src ) {

		var filtered = 
			src
			.replace( /\\["\\\/bfnrtu]/g, '@' )
			.replace( /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
			.replace( /(?:^|:|,)(?:\s*\[)+/g, '');

		if ( /^[\],:{}\s]*$/.test( filtered ) ) {
			return eval( '(' + src + ')' );
		} else {
			throw new SyntaxError( 'Error parsing JSON, source is not valid.' );
		}
	};

	/**
	 * jQuery.quoteString
	 * Returns a string-repr of a string, escaping quotes intelligently.
	 * Mostly a support function for toJSON.
	 * Examples:
	 * >>> jQuery.quoteString('apple')
	 * "apple"
	 *
	 * >>> jQuery.quoteString('"Where are we going?", she asked.')
	 * "\"Where are we going?\", she asked."
	 */
	$.quoteString = function( string ) {
		if ( string.match( escapeable ) ) {
			return '"' + string.replace( escapeable, function( a ) {
				var c = meta[a];
				if ( typeof c === 'string' ) {
					return c;
				}
				c = a.charCodeAt();
				return '\\u00' + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
			}) + '"';
		}
		return '"' + string + '"';
	};

}));

(function(e){"function"==typeof define&&define.amd?define(["jquery"],e):e(jQuery)})(function(e){function t(t,s){var a,n,r,o=t.nodeName.toLowerCase();return"area"===o?(a=t.parentNode,n=a.name,t.href&&n&&"map"===a.nodeName.toLowerCase()?(r=e("img[usemap='#"+n+"']")[0],!!r&&i(r)):!1):(/input|select|textarea|button|object/.test(o)?!t.disabled:"a"===o?t.href||s:s)&&i(t)}function i(t){return e.expr.filters.visible(t)&&!e(t).parents().addBack().filter(function(){return"hidden"===e.css(this,"visibility")}).length}e.ui=e.ui||{},e.extend(e.ui,{version:"1.11.1",keyCode:{BACKSPACE:8,COMMA:188,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,LEFT:37,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SPACE:32,TAB:9,UP:38}}),e.fn.extend({scrollParent:function(t){var i=this.css("position"),s="absolute"===i,a=t?/(auto|scroll|hidden)/:/(auto|scroll)/,n=this.parents().filter(function(){var t=e(this);return s&&"static"===t.css("position")?!1:a.test(t.css("overflow")+t.css("overflow-y")+t.css("overflow-x"))}).eq(0);return"fixed"!==i&&n.length?n:e(this[0].ownerDocument||document)},uniqueId:function(){var e=0;return function(){return this.each(function(){this.id||(this.id="ui-id-"+ ++e)})}}(),removeUniqueId:function(){return this.each(function(){/^ui-id-\d+$/.test(this.id)&&e(this).removeAttr("id")})}}),e.extend(e.expr[":"],{data:e.expr.createPseudo?e.expr.createPseudo(function(t){return function(i){return!!e.data(i,t)}}):function(t,i,s){return!!e.data(t,s[3])},focusable:function(i){return t(i,!isNaN(e.attr(i,"tabindex")))},tabbable:function(i){var s=e.attr(i,"tabindex"),a=isNaN(s);return(a||s>=0)&&t(i,!a)}}),e("<a>").outerWidth(1).jquery||e.each(["Width","Height"],function(t,i){function s(t,i,s,n){return e.each(a,function(){i-=parseFloat(e.css(t,"padding"+this))||0,s&&(i-=parseFloat(e.css(t,"border"+this+"Width"))||0),n&&(i-=parseFloat(e.css(t,"margin"+this))||0)}),i}var a="Width"===i?["Left","Right"]:["Top","Bottom"],n=i.toLowerCase(),r={innerWidth:e.fn.innerWidth,innerHeight:e.fn.innerHeight,outerWidth:e.fn.outerWidth,outerHeight:e.fn.outerHeight};e.fn["inner"+i]=function(t){return void 0===t?r["inner"+i].call(this):this.each(function(){e(this).css(n,s(this,t)+"px")})},e.fn["outer"+i]=function(t,a){return"number"!=typeof t?r["outer"+i].call(this,t):this.each(function(){e(this).css(n,s(this,t,!0,a)+"px")})}}),e.fn.addBack||(e.fn.addBack=function(e){return this.add(null==e?this.prevObject:this.prevObject.filter(e))}),e("<a>").data("a-b","a").removeData("a-b").data("a-b")&&(e.fn.removeData=function(t){return function(i){return arguments.length?t.call(this,e.camelCase(i)):t.call(this)}}(e.fn.removeData)),e.ui.ie=!!/msie [\w.]+/.exec(navigator.userAgent.toLowerCase()),e.fn.extend({focus:function(t){return function(i,s){return"number"==typeof i?this.each(function(){var t=this;setTimeout(function(){e(t).focus(),s&&s.call(t)},i)}):t.apply(this,arguments)}}(e.fn.focus),disableSelection:function(){var e="onselectstart"in document.createElement("div")?"selectstart":"mousedown";return function(){return this.bind(e+".ui-disableSelection",function(e){e.preventDefault()})}}(),enableSelection:function(){return this.unbind(".ui-disableSelection")},zIndex:function(t){if(void 0!==t)return this.css("zIndex",t);if(this.length)for(var i,s,a=e(this[0]);a.length&&a[0]!==document;){if(i=a.css("position"),("absolute"===i||"relative"===i||"fixed"===i)&&(s=parseInt(a.css("zIndex"),10),!isNaN(s)&&0!==s))return s;a=a.parent()}return 0}}),e.ui.plugin={add:function(t,i,s){var a,n=e.ui[t].prototype;for(a in s)n.plugins[a]=n.plugins[a]||[],n.plugins[a].push([i,s[a]])},call:function(e,t,i,s){var a,n=e.plugins[t];if(n&&(s||e.element[0].parentNode&&11!==e.element[0].parentNode.nodeType))for(a=0;n.length>a;a++)e.options[n[a][0]]&&n[a][1].apply(e.element,i)}};var s=0,a=Array.prototype.slice;e.cleanData=function(t){return function(i){var s,a,n;for(n=0;null!=(a=i[n]);n++)try{s=e._data(a,"events"),s&&s.remove&&e(a).triggerHandler("remove")}catch(r){}t(i)}}(e.cleanData),e.widget=function(t,i,s){var a,n,r,o,h={},l=t.split(".")[0];return t=t.split(".")[1],a=l+"-"+t,s||(s=i,i=e.Widget),e.expr[":"][a.toLowerCase()]=function(t){return!!e.data(t,a)},e[l]=e[l]||{},n=e[l][t],r=e[l][t]=function(e,t){return this._createWidget?(arguments.length&&this._createWidget(e,t),void 0):new r(e,t)},e.extend(r,n,{version:s.version,_proto:e.extend({},s),_childConstructors:[]}),o=new i,o.options=e.widget.extend({},o.options),e.each(s,function(t,s){return e.isFunction(s)?(h[t]=function(){var e=function(){return i.prototype[t].apply(this,arguments)},a=function(e){return i.prototype[t].apply(this,e)};return function(){var t,i=this._super,n=this._superApply;return this._super=e,this._superApply=a,t=s.apply(this,arguments),this._super=i,this._superApply=n,t}}(),void 0):(h[t]=s,void 0)}),r.prototype=e.widget.extend(o,{widgetEventPrefix:n?o.widgetEventPrefix||t:t},h,{constructor:r,namespace:l,widgetName:t,widgetFullName:a}),n?(e.each(n._childConstructors,function(t,i){var s=i.prototype;e.widget(s.namespace+"."+s.widgetName,r,i._proto)}),delete n._childConstructors):i._childConstructors.push(r),e.widget.bridge(t,r),r},e.widget.extend=function(t){for(var i,s,n=a.call(arguments,1),r=0,o=n.length;o>r;r++)for(i in n[r])s=n[r][i],n[r].hasOwnProperty(i)&&void 0!==s&&(t[i]=e.isPlainObject(s)?e.isPlainObject(t[i])?e.widget.extend({},t[i],s):e.widget.extend({},s):s);return t},e.widget.bridge=function(t,i){var s=i.prototype.widgetFullName||t;e.fn[t]=function(n){var r="string"==typeof n,o=a.call(arguments,1),h=this;return n=!r&&o.length?e.widget.extend.apply(null,[n].concat(o)):n,r?this.each(function(){var i,a=e.data(this,s);return"instance"===n?(h=a,!1):a?e.isFunction(a[n])&&"_"!==n.charAt(0)?(i=a[n].apply(a,o),i!==a&&void 0!==i?(h=i&&i.jquery?h.pushStack(i.get()):i,!1):void 0):e.error("no such method '"+n+"' for "+t+" widget instance"):e.error("cannot call methods on "+t+" prior to initialization; "+"attempted to call method '"+n+"'")}):this.each(function(){var t=e.data(this,s);t?(t.option(n||{}),t._init&&t._init()):e.data(this,s,new i(n,this))}),h}},e.Widget=function(){},e.Widget._childConstructors=[],e.Widget.prototype={widgetName:"widget",widgetEventPrefix:"",defaultElement:"<div>",options:{disabled:!1,create:null},_createWidget:function(t,i){i=e(i||this.defaultElement||this)[0],this.element=e(i),this.uuid=s++,this.eventNamespace="."+this.widgetName+this.uuid,this.options=e.widget.extend({},this.options,this._getCreateOptions(),t),this.bindings=e(),this.hoverable=e(),this.focusable=e(),i!==this&&(e.data(i,this.widgetFullName,this),this._on(!0,this.element,{remove:function(e){e.target===i&&this.destroy()}}),this.document=e(i.style?i.ownerDocument:i.document||i),this.window=e(this.document[0].defaultView||this.document[0].parentWindow)),this._create(),this._trigger("create",null,this._getCreateEventData()),this._init()},_getCreateOptions:e.noop,_getCreateEventData:e.noop,_create:e.noop,_init:e.noop,destroy:function(){this._destroy(),this.element.unbind(this.eventNamespace).removeData(this.widgetFullName).removeData(e.camelCase(this.widgetFullName)),this.widget().unbind(this.eventNamespace).removeAttr("aria-disabled").removeClass(this.widgetFullName+"-disabled "+"ui-state-disabled"),this.bindings.unbind(this.eventNamespace),this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus")},_destroy:e.noop,widget:function(){return this.element},option:function(t,i){var s,a,n,r=t;if(0===arguments.length)return e.widget.extend({},this.options);if("string"==typeof t)if(r={},s=t.split("."),t=s.shift(),s.length){for(a=r[t]=e.widget.extend({},this.options[t]),n=0;s.length-1>n;n++)a[s[n]]=a[s[n]]||{},a=a[s[n]];if(t=s.pop(),1===arguments.length)return void 0===a[t]?null:a[t];a[t]=i}else{if(1===arguments.length)return void 0===this.options[t]?null:this.options[t];r[t]=i}return this._setOptions(r),this},_setOptions:function(e){var t;for(t in e)this._setOption(t,e[t]);return this},_setOption:function(e,t){return this.options[e]=t,"disabled"===e&&(this.widget().toggleClass(this.widgetFullName+"-disabled",!!t),t&&(this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus"))),this},enable:function(){return this._setOptions({disabled:!1})},disable:function(){return this._setOptions({disabled:!0})},_on:function(t,i,s){var a,n=this;"boolean"!=typeof t&&(s=i,i=t,t=!1),s?(i=a=e(i),this.bindings=this.bindings.add(i)):(s=i,i=this.element,a=this.widget()),e.each(s,function(s,r){function o(){return t||n.options.disabled!==!0&&!e(this).hasClass("ui-state-disabled")?("string"==typeof r?n[r]:r).apply(n,arguments):void 0}"string"!=typeof r&&(o.guid=r.guid=r.guid||o.guid||e.guid++);var h=s.match(/^([\w:-]*)\s*(.*)$/),l=h[1]+n.eventNamespace,u=h[2];u?a.delegate(u,l,o):i.bind(l,o)})},_off:function(e,t){t=(t||"").split(" ").join(this.eventNamespace+" ")+this.eventNamespace,e.unbind(t).undelegate(t)},_delay:function(e,t){function i(){return("string"==typeof e?s[e]:e).apply(s,arguments)}var s=this;return setTimeout(i,t||0)},_hoverable:function(t){this.hoverable=this.hoverable.add(t),this._on(t,{mouseenter:function(t){e(t.currentTarget).addClass("ui-state-hover")},mouseleave:function(t){e(t.currentTarget).removeClass("ui-state-hover")}})},_focusable:function(t){this.focusable=this.focusable.add(t),this._on(t,{focusin:function(t){e(t.currentTarget).addClass("ui-state-focus")},focusout:function(t){e(t.currentTarget).removeClass("ui-state-focus")}})},_trigger:function(t,i,s){var a,n,r=this.options[t];if(s=s||{},i=e.Event(i),i.type=(t===this.widgetEventPrefix?t:this.widgetEventPrefix+t).toLowerCase(),i.target=this.element[0],n=i.originalEvent)for(a in n)a in i||(i[a]=n[a]);return this.element.trigger(i,s),!(e.isFunction(r)&&r.apply(this.element[0],[i].concat(s))===!1||i.isDefaultPrevented())}},e.each({show:"fadeIn",hide:"fadeOut"},function(t,i){e.Widget.prototype["_"+t]=function(s,a,n){"string"==typeof a&&(a={effect:a});var r,o=a?a===!0||"number"==typeof a?i:a.effect||i:t;a=a||{},"number"==typeof a&&(a={duration:a}),r=!e.isEmptyObject(a),a.complete=n,a.delay&&s.delay(a.delay),r&&e.effects&&e.effects.effect[o]?s[t](a):o!==t&&s[o]?s[o](a.duration,a.easing,n):s.queue(function(i){e(this)[t](),n&&n.call(s[0]),i()})}}),e.widget;var n=!1;e(document).mouseup(function(){n=!1}),e.widget("ui.mouse",{version:"1.11.1",options:{cancel:"input,textarea,button,select,option",distance:1,delay:0},_mouseInit:function(){var t=this;this.element.bind("mousedown."+this.widgetName,function(e){return t._mouseDown(e)}).bind("click."+this.widgetName,function(i){return!0===e.data(i.target,t.widgetName+".preventClickEvent")?(e.removeData(i.target,t.widgetName+".preventClickEvent"),i.stopImmediatePropagation(),!1):void 0}),this.started=!1},_mouseDestroy:function(){this.element.unbind("."+this.widgetName),this._mouseMoveDelegate&&this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate)},_mouseDown:function(t){if(!n){this._mouseStarted&&this._mouseUp(t),this._mouseDownEvent=t;var i=this,s=1===t.which,a="string"==typeof this.options.cancel&&t.target.nodeName?e(t.target).closest(this.options.cancel).length:!1;return s&&!a&&this._mouseCapture(t)?(this.mouseDelayMet=!this.options.delay,this.mouseDelayMet||(this._mouseDelayTimer=setTimeout(function(){i.mouseDelayMet=!0},this.options.delay)),this._mouseDistanceMet(t)&&this._mouseDelayMet(t)&&(this._mouseStarted=this._mouseStart(t)!==!1,!this._mouseStarted)?(t.preventDefault(),!0):(!0===e.data(t.target,this.widgetName+".preventClickEvent")&&e.removeData(t.target,this.widgetName+".preventClickEvent"),this._mouseMoveDelegate=function(e){return i._mouseMove(e)},this._mouseUpDelegate=function(e){return i._mouseUp(e)},this.document.bind("mousemove."+this.widgetName,this._mouseMoveDelegate).bind("mouseup."+this.widgetName,this._mouseUpDelegate),t.preventDefault(),n=!0,!0)):!0}},_mouseMove:function(t){return e.ui.ie&&(!document.documentMode||9>document.documentMode)&&!t.button?this._mouseUp(t):t.which?this._mouseStarted?(this._mouseDrag(t),t.preventDefault()):(this._mouseDistanceMet(t)&&this._mouseDelayMet(t)&&(this._mouseStarted=this._mouseStart(this._mouseDownEvent,t)!==!1,this._mouseStarted?this._mouseDrag(t):this._mouseUp(t)),!this._mouseStarted):this._mouseUp(t)},_mouseUp:function(t){return this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate),this._mouseStarted&&(this._mouseStarted=!1,t.target===this._mouseDownEvent.target&&e.data(t.target,this.widgetName+".preventClickEvent",!0),this._mouseStop(t)),n=!1,!1},_mouseDistanceMet:function(e){return Math.max(Math.abs(this._mouseDownEvent.pageX-e.pageX),Math.abs(this._mouseDownEvent.pageY-e.pageY))>=this.options.distance},_mouseDelayMet:function(){return this.mouseDelayMet},_mouseStart:function(){},_mouseDrag:function(){},_mouseStop:function(){},_mouseCapture:function(){return!0}}),e.widget("ui.sortable",e.ui.mouse,{version:"1.11.1",widgetEventPrefix:"sort",ready:!1,options:{appendTo:"parent",axis:!1,connectWith:!1,containment:!1,cursor:"auto",cursorAt:!1,dropOnEmpty:!0,forcePlaceholderSize:!1,forceHelperSize:!1,grid:!1,handle:!1,helper:"original",items:"> *",opacity:!1,placeholder:!1,revert:!1,scroll:!0,scrollSensitivity:20,scrollSpeed:20,scope:"default",tolerance:"intersect",zIndex:1e3,activate:null,beforeStop:null,change:null,deactivate:null,out:null,over:null,receive:null,remove:null,sort:null,start:null,stop:null,update:null},_isOverAxis:function(e,t,i){return e>=t&&t+i>e},_isFloating:function(e){return/left|right/.test(e.css("float"))||/inline|table-cell/.test(e.css("display"))},_create:function(){var e=this.options;this.containerCache={},this.element.addClass("ui-sortable"),this.refresh(),this.floating=this.items.length?"x"===e.axis||this._isFloating(this.items[0].item):!1,this.offset=this.element.offset(),this._mouseInit(),this._setHandleClassName(),this.ready=!0},_setOption:function(e,t){this._super(e,t),"handle"===e&&this._setHandleClassName()},_setHandleClassName:function(){this.element.find(".ui-sortable-handle").removeClass("ui-sortable-handle"),e.each(this.items,function(){(this.instance.options.handle?this.item.find(this.instance.options.handle):this.item).addClass("ui-sortable-handle")})},_destroy:function(){this.element.removeClass("ui-sortable ui-sortable-disabled").find(".ui-sortable-handle").removeClass("ui-sortable-handle"),this._mouseDestroy();for(var e=this.items.length-1;e>=0;e--)this.items[e].item.removeData(this.widgetName+"-item");return this},_mouseCapture:function(t,i){var s=null,a=!1,n=this;return this.reverting?!1:this.options.disabled||"static"===this.options.type?!1:(this._refreshItems(t),e(t.target).parents().each(function(){return e.data(this,n.widgetName+"-item")===n?(s=e(this),!1):void 0}),e.data(t.target,n.widgetName+"-item")===n&&(s=e(t.target)),s?!this.options.handle||i||(e(this.options.handle,s).find("*").addBack().each(function(){this===t.target&&(a=!0)}),a)?(this.currentItem=s,this._removeCurrentsFromItems(),!0):!1:!1)},_mouseStart:function(t,i,s){var a,n,r=this.options;if(this.currentContainer=this,this.refreshPositions(),this.helper=this._createHelper(t),this._cacheHelperProportions(),this._cacheMargins(),this.scrollParent=this.helper.scrollParent(),this.offset=this.currentItem.offset(),this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left},e.extend(this.offset,{click:{left:t.pageX-this.offset.left,top:t.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()}),this.helper.css("position","absolute"),this.cssPosition=this.helper.css("position"),this.originalPosition=this._generatePosition(t),this.originalPageX=t.pageX,this.originalPageY=t.pageY,r.cursorAt&&this._adjustOffsetFromHelper(r.cursorAt),this.domPosition={prev:this.currentItem.prev()[0],parent:this.currentItem.parent()[0]},this.helper[0]!==this.currentItem[0]&&this.currentItem.hide(),this._createPlaceholder(),r.containment&&this._setContainment(),r.cursor&&"auto"!==r.cursor&&(n=this.document.find("body"),this.storedCursor=n.css("cursor"),n.css("cursor",r.cursor),this.storedStylesheet=e("<style>*{ cursor: "+r.cursor+" !important; }</style>").appendTo(n)),r.opacity&&(this.helper.css("opacity")&&(this._storedOpacity=this.helper.css("opacity")),this.helper.css("opacity",r.opacity)),r.zIndex&&(this.helper.css("zIndex")&&(this._storedZIndex=this.helper.css("zIndex")),this.helper.css("zIndex",r.zIndex)),this.scrollParent[0]!==document&&"HTML"!==this.scrollParent[0].tagName&&(this.overflowOffset=this.scrollParent.offset()),this._trigger("start",t,this._uiHash()),this._preserveHelperProportions||this._cacheHelperProportions(),!s)for(a=this.containers.length-1;a>=0;a--)this.containers[a]._trigger("activate",t,this._uiHash(this));return e.ui.ddmanager&&(e.ui.ddmanager.current=this),e.ui.ddmanager&&!r.dropBehaviour&&e.ui.ddmanager.prepareOffsets(this,t),this.dragging=!0,this.helper.addClass("ui-sortable-helper"),this._mouseDrag(t),!0},_mouseDrag:function(t){var i,s,a,n,r=this.options,o=!1;for(this.position=this._generatePosition(t),this.positionAbs=this._convertPositionTo("absolute"),this.lastPositionAbs||(this.lastPositionAbs=this.positionAbs),this.options.scroll&&(this.scrollParent[0]!==document&&"HTML"!==this.scrollParent[0].tagName?(this.overflowOffset.top+this.scrollParent[0].offsetHeight-t.pageY<r.scrollSensitivity?this.scrollParent[0].scrollTop=o=this.scrollParent[0].scrollTop+r.scrollSpeed:t.pageY-this.overflowOffset.top<r.scrollSensitivity&&(this.scrollParent[0].scrollTop=o=this.scrollParent[0].scrollTop-r.scrollSpeed),this.overflowOffset.left+this.scrollParent[0].offsetWidth-t.pageX<r.scrollSensitivity?this.scrollParent[0].scrollLeft=o=this.scrollParent[0].scrollLeft+r.scrollSpeed:t.pageX-this.overflowOffset.left<r.scrollSensitivity&&(this.scrollParent[0].scrollLeft=o=this.scrollParent[0].scrollLeft-r.scrollSpeed)):(t.pageY-e(document).scrollTop()<r.scrollSensitivity?o=e(document).scrollTop(e(document).scrollTop()-r.scrollSpeed):e(window).height()-(t.pageY-e(document).scrollTop())<r.scrollSensitivity&&(o=e(document).scrollTop(e(document).scrollTop()+r.scrollSpeed)),t.pageX-e(document).scrollLeft()<r.scrollSensitivity?o=e(document).scrollLeft(e(document).scrollLeft()-r.scrollSpeed):e(window).width()-(t.pageX-e(document).scrollLeft())<r.scrollSensitivity&&(o=e(document).scrollLeft(e(document).scrollLeft()+r.scrollSpeed))),o!==!1&&e.ui.ddmanager&&!r.dropBehaviour&&e.ui.ddmanager.prepareOffsets(this,t)),this.positionAbs=this._convertPositionTo("absolute"),this.options.axis&&"y"===this.options.axis||(this.helper[0].style.left=this.position.left+"px"),this.options.axis&&"x"===this.options.axis||(this.helper[0].style.top=this.position.top+"px"),i=this.items.length-1;i>=0;i--)if(s=this.items[i],a=s.item[0],n=this._intersectsWithPointer(s),n&&s.instance===this.currentContainer&&a!==this.currentItem[0]&&this.placeholder[1===n?"next":"prev"]()[0]!==a&&!e.contains(this.placeholder[0],a)&&("semi-dynamic"===this.options.type?!e.contains(this.element[0],a):!0)){if(this.direction=1===n?"down":"up","pointer"!==this.options.tolerance&&!this._intersectsWithSides(s))break;this._rearrange(t,s),this._trigger("change",t,this._uiHash());break}return this._contactContainers(t),e.ui.ddmanager&&e.ui.ddmanager.drag(this,t),this._trigger("sort",t,this._uiHash()),this.lastPositionAbs=this.positionAbs,!1},_mouseStop:function(t,i){if(t){if(e.ui.ddmanager&&!this.options.dropBehaviour&&e.ui.ddmanager.drop(this,t),this.options.revert){var s=this,a=this.placeholder.offset(),n=this.options.axis,r={};n&&"x"!==n||(r.left=a.left-this.offset.parent.left-this.margins.left+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollLeft)),n&&"y"!==n||(r.top=a.top-this.offset.parent.top-this.margins.top+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollTop)),this.reverting=!0,e(this.helper).animate(r,parseInt(this.options.revert,10)||500,function(){s._clear(t)})}else this._clear(t,i);return!1}},cancel:function(){if(this.dragging){this._mouseUp({target:null}),"original"===this.options.helper?this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper"):this.currentItem.show();for(var t=this.containers.length-1;t>=0;t--)this.containers[t]._trigger("deactivate",null,this._uiHash(this)),this.containers[t].containerCache.over&&(this.containers[t]._trigger("out",null,this._uiHash(this)),this.containers[t].containerCache.over=0)}return this.placeholder&&(this.placeholder[0].parentNode&&this.placeholder[0].parentNode.removeChild(this.placeholder[0]),"original"!==this.options.helper&&this.helper&&this.helper[0].parentNode&&this.helper.remove(),e.extend(this,{helper:null,dragging:!1,reverting:!1,_noFinalSort:null}),this.domPosition.prev?e(this.domPosition.prev).after(this.currentItem):e(this.domPosition.parent).prepend(this.currentItem)),this},serialize:function(t){var i=this._getItemsAsjQuery(t&&t.connected),s=[];return t=t||{},e(i).each(function(){var i=(e(t.item||this).attr(t.attribute||"id")||"").match(t.expression||/(.+)[\-=_](.+)/);i&&s.push((t.key||i[1]+"[]")+"="+(t.key&&t.expression?i[1]:i[2]))}),!s.length&&t.key&&s.push(t.key+"="),s.join("&")},toArray:function(t){var i=this._getItemsAsjQuery(t&&t.connected),s=[];return t=t||{},i.each(function(){s.push(e(t.item||this).attr(t.attribute||"id")||"")}),s},_intersectsWith:function(e){var t=this.positionAbs.left,i=t+this.helperProportions.width,s=this.positionAbs.top,a=s+this.helperProportions.height,n=e.left,r=n+e.width,o=e.top,h=o+e.height,l=this.offset.click.top,u=this.offset.click.left,d="x"===this.options.axis||s+l>o&&h>s+l,c="y"===this.options.axis||t+u>n&&r>t+u,p=d&&c;return"pointer"===this.options.tolerance||this.options.forcePointerForContainers||"pointer"!==this.options.tolerance&&this.helperProportions[this.floating?"width":"height"]>e[this.floating?"width":"height"]?p:t+this.helperProportions.width/2>n&&r>i-this.helperProportions.width/2&&s+this.helperProportions.height/2>o&&h>a-this.helperProportions.height/2},_intersectsWithPointer:function(e){var t="x"===this.options.axis||this._isOverAxis(this.positionAbs.top+this.offset.click.top,e.top,e.height),i="y"===this.options.axis||this._isOverAxis(this.positionAbs.left+this.offset.click.left,e.left,e.width),s=t&&i,a=this._getDragVerticalDirection(),n=this._getDragHorizontalDirection();return s?this.floating?n&&"right"===n||"down"===a?2:1:a&&("down"===a?2:1):!1},_intersectsWithSides:function(e){var t=this._isOverAxis(this.positionAbs.top+this.offset.click.top,e.top+e.height/2,e.height),i=this._isOverAxis(this.positionAbs.left+this.offset.click.left,e.left+e.width/2,e.width),s=this._getDragVerticalDirection(),a=this._getDragHorizontalDirection();return this.floating&&a?"right"===a&&i||"left"===a&&!i:s&&("down"===s&&t||"up"===s&&!t)},_getDragVerticalDirection:function(){var e=this.positionAbs.top-this.lastPositionAbs.top;return 0!==e&&(e>0?"down":"up")},_getDragHorizontalDirection:function(){var e=this.positionAbs.left-this.lastPositionAbs.left;return 0!==e&&(e>0?"right":"left")},refresh:function(e){return this._refreshItems(e),this._setHandleClassName(),this.refreshPositions(),this},_connectWith:function(){var e=this.options;return e.connectWith.constructor===String?[e.connectWith]:e.connectWith},_getItemsAsjQuery:function(t){function i(){o.push(this)}var s,a,n,r,o=[],h=[],l=this._connectWith();if(l&&t)for(s=l.length-1;s>=0;s--)for(n=e(l[s]),a=n.length-1;a>=0;a--)r=e.data(n[a],this.widgetFullName),r&&r!==this&&!r.options.disabled&&h.push([e.isFunction(r.options.items)?r.options.items.call(r.element):e(r.options.items,r.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),r]);for(h.push([e.isFunction(this.options.items)?this.options.items.call(this.element,null,{options:this.options,item:this.currentItem}):e(this.options.items,this.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),this]),s=h.length-1;s>=0;s--)h[s][0].each(i);return e(o)},_removeCurrentsFromItems:function(){var t=this.currentItem.find(":data("+this.widgetName+"-item)");this.items=e.grep(this.items,function(e){for(var i=0;t.length>i;i++)if(t[i]===e.item[0])return!1;return!0})},_refreshItems:function(t){this.items=[],this.containers=[this];var i,s,a,n,r,o,h,l,u=this.items,d=[[e.isFunction(this.options.items)?this.options.items.call(this.element[0],t,{item:this.currentItem}):e(this.options.items,this.element),this]],c=this._connectWith();if(c&&this.ready)for(i=c.length-1;i>=0;i--)for(a=e(c[i]),s=a.length-1;s>=0;s--)n=e.data(a[s],this.widgetFullName),n&&n!==this&&!n.options.disabled&&(d.push([e.isFunction(n.options.items)?n.options.items.call(n.element[0],t,{item:this.currentItem}):e(n.options.items,n.element),n]),this.containers.push(n));for(i=d.length-1;i>=0;i--)for(r=d[i][1],o=d[i][0],s=0,l=o.length;l>s;s++)h=e(o[s]),h.data(this.widgetName+"-item",r),u.push({item:h,instance:r,width:0,height:0,left:0,top:0})},refreshPositions:function(t){this.offsetParent&&this.helper&&(this.offset.parent=this._getParentOffset());var i,s,a,n;for(i=this.items.length-1;i>=0;i--)s=this.items[i],s.instance!==this.currentContainer&&this.currentContainer&&s.item[0]!==this.currentItem[0]||(a=this.options.toleranceElement?e(this.options.toleranceElement,s.item):s.item,t||(s.width=a.outerWidth(),s.height=a.outerHeight()),n=a.offset(),s.left=n.left,s.top=n.top);if(this.options.custom&&this.options.custom.refreshContainers)this.options.custom.refreshContainers.call(this);else for(i=this.containers.length-1;i>=0;i--)n=this.containers[i].element.offset(),this.containers[i].containerCache.left=n.left,this.containers[i].containerCache.top=n.top,this.containers[i].containerCache.width=this.containers[i].element.outerWidth(),this.containers[i].containerCache.height=this.containers[i].element.outerHeight();return this},_createPlaceholder:function(t){t=t||this;var i,s=t.options;s.placeholder&&s.placeholder.constructor!==String||(i=s.placeholder,s.placeholder={element:function(){var s=t.currentItem[0].nodeName.toLowerCase(),a=e("<"+s+">",t.document[0]).addClass(i||t.currentItem[0].className+" ui-sortable-placeholder").removeClass("ui-sortable-helper");return"tr"===s?t.currentItem.children().each(function(){e("<td>&#160;</td>",t.document[0]).attr("colspan",e(this).attr("colspan")||1).appendTo(a)}):"img"===s&&a.attr("src",t.currentItem.attr("src")),i||a.css("visibility","hidden"),a},update:function(e,a){(!i||s.forcePlaceholderSize)&&(a.height()||a.height(t.currentItem.innerHeight()-parseInt(t.currentItem.css("paddingTop")||0,10)-parseInt(t.currentItem.css("paddingBottom")||0,10)),a.width()||a.width(t.currentItem.innerWidth()-parseInt(t.currentItem.css("paddingLeft")||0,10)-parseInt(t.currentItem.css("paddingRight")||0,10)))}}),t.placeholder=e(s.placeholder.element.call(t.element,t.currentItem)),t.currentItem.after(t.placeholder),s.placeholder.update(t,t.placeholder)},_contactContainers:function(t){var i,s,a,n,r,o,h,l,u,d,c=null,p=null;for(i=this.containers.length-1;i>=0;i--)if(!e.contains(this.currentItem[0],this.containers[i].element[0]))if(this._intersectsWith(this.containers[i].containerCache)){if(c&&e.contains(this.containers[i].element[0],c.element[0]))continue;c=this.containers[i],p=i}else this.containers[i].containerCache.over&&(this.containers[i]._trigger("out",t,this._uiHash(this)),this.containers[i].containerCache.over=0);if(c)if(1===this.containers.length)this.containers[p].containerCache.over||(this.containers[p]._trigger("over",t,this._uiHash(this)),this.containers[p].containerCache.over=1);else{for(a=1e4,n=null,u=c.floating||this._isFloating(this.currentItem),r=u?"left":"top",o=u?"width":"height",d=u?"clientX":"clientY",s=this.items.length-1;s>=0;s--)e.contains(this.containers[p].element[0],this.items[s].item[0])&&this.items[s].item[0]!==this.currentItem[0]&&(h=this.items[s].item.offset()[r],l=!1,t[d]-h>this.items[s][o]/2&&(l=!0),a>Math.abs(t[d]-h)&&(a=Math.abs(t[d]-h),n=this.items[s],this.direction=l?"up":"down"));if(!n&&!this.options.dropOnEmpty)return;if(this.currentContainer===this.containers[p])return;n?this._rearrange(t,n,null,!0):this._rearrange(t,null,this.containers[p].element,!0),this._trigger("change",t,this._uiHash()),this.containers[p]._trigger("change",t,this._uiHash(this)),this.currentContainer=this.containers[p],this.options.placeholder.update(this.currentContainer,this.placeholder),this.containers[p]._trigger("over",t,this._uiHash(this)),this.containers[p].containerCache.over=1}},_createHelper:function(t){var i=this.options,s=e.isFunction(i.helper)?e(i.helper.apply(this.element[0],[t,this.currentItem])):"clone"===i.helper?this.currentItem.clone():this.currentItem;return s.parents("body").length||e("parent"!==i.appendTo?i.appendTo:this.currentItem[0].parentNode)[0].appendChild(s[0]),s[0]===this.currentItem[0]&&(this._storedCSS={width:this.currentItem[0].style.width,height:this.currentItem[0].style.height,position:this.currentItem.css("position"),top:this.currentItem.css("top"),left:this.currentItem.css("left")}),(!s[0].style.width||i.forceHelperSize)&&s.width(this.currentItem.width()),(!s[0].style.height||i.forceHelperSize)&&s.height(this.currentItem.height()),s},_adjustOffsetFromHelper:function(t){"string"==typeof t&&(t=t.split(" ")),e.isArray(t)&&(t={left:+t[0],top:+t[1]||0}),"left"in t&&(this.offset.click.left=t.left+this.margins.left),"right"in t&&(this.offset.click.left=this.helperProportions.width-t.right+this.margins.left),"top"in t&&(this.offset.click.top=t.top+this.margins.top),"bottom"in t&&(this.offset.click.top=this.helperProportions.height-t.bottom+this.margins.top)},_getParentOffset:function(){this.offsetParent=this.helper.offsetParent();var t=this.offsetParent.offset();return"absolute"===this.cssPosition&&this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])&&(t.left+=this.scrollParent.scrollLeft(),t.top+=this.scrollParent.scrollTop()),(this.offsetParent[0]===document.body||this.offsetParent[0].tagName&&"html"===this.offsetParent[0].tagName.toLowerCase()&&e.ui.ie)&&(t={top:0,left:0}),{top:t.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:t.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}},_getRelativeOffset:function(){if("relative"===this.cssPosition){var e=this.currentItem.position();return{top:e.top-(parseInt(this.helper.css("top"),10)||0)+this.scrollParent.scrollTop(),left:e.left-(parseInt(this.helper.css("left"),10)||0)+this.scrollParent.scrollLeft()}}return{top:0,left:0}},_cacheMargins:function(){this.margins={left:parseInt(this.currentItem.css("marginLeft"),10)||0,top:parseInt(this.currentItem.css("marginTop"),10)||0}},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}},_setContainment:function(){var t,i,s,a=this.options;"parent"===a.containment&&(a.containment=this.helper[0].parentNode),("document"===a.containment||"window"===a.containment)&&(this.containment=[0-this.offset.relative.left-this.offset.parent.left,0-this.offset.relative.top-this.offset.parent.top,e("document"===a.containment?document:window).width()-this.helperProportions.width-this.margins.left,(e("document"===a.containment?document:window).height()||document.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top]),/^(document|window|parent)$/.test(a.containment)||(t=e(a.containment)[0],i=e(a.containment).offset(),s="hidden"!==e(t).css("overflow"),this.containment=[i.left+(parseInt(e(t).css("borderLeftWidth"),10)||0)+(parseInt(e(t).css("paddingLeft"),10)||0)-this.margins.left,i.top+(parseInt(e(t).css("borderTopWidth"),10)||0)+(parseInt(e(t).css("paddingTop"),10)||0)-this.margins.top,i.left+(s?Math.max(t.scrollWidth,t.offsetWidth):t.offsetWidth)-(parseInt(e(t).css("borderLeftWidth"),10)||0)-(parseInt(e(t).css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left,i.top+(s?Math.max(t.scrollHeight,t.offsetHeight):t.offsetHeight)-(parseInt(e(t).css("borderTopWidth"),10)||0)-(parseInt(e(t).css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top])
},_convertPositionTo:function(t,i){i||(i=this.position);var s="absolute"===t?1:-1,a="absolute"!==this.cssPosition||this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])?this.scrollParent:this.offsetParent,n=/(html|body)/i.test(a[0].tagName);return{top:i.top+this.offset.relative.top*s+this.offset.parent.top*s-("fixed"===this.cssPosition?-this.scrollParent.scrollTop():n?0:a.scrollTop())*s,left:i.left+this.offset.relative.left*s+this.offset.parent.left*s-("fixed"===this.cssPosition?-this.scrollParent.scrollLeft():n?0:a.scrollLeft())*s}},_generatePosition:function(t){var i,s,a=this.options,n=t.pageX,r=t.pageY,o="absolute"!==this.cssPosition||this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])?this.scrollParent:this.offsetParent,h=/(html|body)/i.test(o[0].tagName);return"relative"!==this.cssPosition||this.scrollParent[0]!==document&&this.scrollParent[0]!==this.offsetParent[0]||(this.offset.relative=this._getRelativeOffset()),this.originalPosition&&(this.containment&&(t.pageX-this.offset.click.left<this.containment[0]&&(n=this.containment[0]+this.offset.click.left),t.pageY-this.offset.click.top<this.containment[1]&&(r=this.containment[1]+this.offset.click.top),t.pageX-this.offset.click.left>this.containment[2]&&(n=this.containment[2]+this.offset.click.left),t.pageY-this.offset.click.top>this.containment[3]&&(r=this.containment[3]+this.offset.click.top)),a.grid&&(i=this.originalPageY+Math.round((r-this.originalPageY)/a.grid[1])*a.grid[1],r=this.containment?i-this.offset.click.top>=this.containment[1]&&i-this.offset.click.top<=this.containment[3]?i:i-this.offset.click.top>=this.containment[1]?i-a.grid[1]:i+a.grid[1]:i,s=this.originalPageX+Math.round((n-this.originalPageX)/a.grid[0])*a.grid[0],n=this.containment?s-this.offset.click.left>=this.containment[0]&&s-this.offset.click.left<=this.containment[2]?s:s-this.offset.click.left>=this.containment[0]?s-a.grid[0]:s+a.grid[0]:s)),{top:r-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+("fixed"===this.cssPosition?-this.scrollParent.scrollTop():h?0:o.scrollTop()),left:n-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+("fixed"===this.cssPosition?-this.scrollParent.scrollLeft():h?0:o.scrollLeft())}},_rearrange:function(e,t,i,s){i?i[0].appendChild(this.placeholder[0]):t.item[0].parentNode.insertBefore(this.placeholder[0],"down"===this.direction?t.item[0]:t.item[0].nextSibling),this.counter=this.counter?++this.counter:1;var a=this.counter;this._delay(function(){a===this.counter&&this.refreshPositions(!s)})},_clear:function(e,t){function i(e,t,i){return function(s){i._trigger(e,s,t._uiHash(t))}}this.reverting=!1;var s,a=[];if(!this._noFinalSort&&this.currentItem.parent().length&&this.placeholder.before(this.currentItem),this._noFinalSort=null,this.helper[0]===this.currentItem[0]){for(s in this._storedCSS)("auto"===this._storedCSS[s]||"static"===this._storedCSS[s])&&(this._storedCSS[s]="");this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")}else this.currentItem.show();for(this.fromOutside&&!t&&a.push(function(e){this._trigger("receive",e,this._uiHash(this.fromOutside))}),!this.fromOutside&&this.domPosition.prev===this.currentItem.prev().not(".ui-sortable-helper")[0]&&this.domPosition.parent===this.currentItem.parent()[0]||t||a.push(function(e){this._trigger("update",e,this._uiHash())}),this!==this.currentContainer&&(t||(a.push(function(e){this._trigger("remove",e,this._uiHash())}),a.push(function(e){return function(t){e._trigger("receive",t,this._uiHash(this))}}.call(this,this.currentContainer)),a.push(function(e){return function(t){e._trigger("update",t,this._uiHash(this))}}.call(this,this.currentContainer)))),s=this.containers.length-1;s>=0;s--)t||a.push(i("deactivate",this,this.containers[s])),this.containers[s].containerCache.over&&(a.push(i("out",this,this.containers[s])),this.containers[s].containerCache.over=0);if(this.storedCursor&&(this.document.find("body").css("cursor",this.storedCursor),this.storedStylesheet.remove()),this._storedOpacity&&this.helper.css("opacity",this._storedOpacity),this._storedZIndex&&this.helper.css("zIndex","auto"===this._storedZIndex?"":this._storedZIndex),this.dragging=!1,this.cancelHelperRemoval){if(!t){for(this._trigger("beforeStop",e,this._uiHash()),s=0;a.length>s;s++)a[s].call(this,e);this._trigger("stop",e,this._uiHash())}return this.fromOutside=!1,!1}if(t||this._trigger("beforeStop",e,this._uiHash()),this.placeholder[0].parentNode.removeChild(this.placeholder[0]),this.helper[0]!==this.currentItem[0]&&this.helper.remove(),this.helper=null,!t){for(s=0;a.length>s;s++)a[s].call(this,e);this._trigger("stop",e,this._uiHash())}return this.fromOutside=!1,!0},_trigger:function(){e.Widget.prototype._trigger.apply(this,arguments)===!1&&this.cancel()},_uiHash:function(t){var i=t||this;return{helper:i.helper,placeholder:i.placeholder||e([]),position:i.position,originalPosition:i.originalPosition,offset:i.positionAbs,item:i.currentItem,sender:t?t.element:null}}})});
﻿/**
 * 浏览器支持
 * @module jqueryExt
 * @depends jquery
 */
jQuery.browser = {};
jQuery.browser.mozilla = /firefox/.test(navigator.userAgent.toLowerCase());
jQuery.browser.webkit = /webkit/.test(navigator.userAgent.toLowerCase());
jQuery.browser.opera = /opera/.test(navigator.userAgent.toLowerCase());
jQuery.browser.msie11 = /rv:11.0/.test(navigator.userAgent.toLowerCase());
jQuery.browser.msie = /msie/.test(navigator.userAgent.toLowerCase()) || jQuery.browser.msie11;
/**
 * 表单提交扩展
 * @module jqueryExt
 * @depends jquery,base
 */
jQuery.fn.extend({
	taserialize: function(isIncludeNullFields) {
		return jQuery.param( this.taserializeArray(isIncludeNullFields) );
	},
	taserializeArray: function(isIncludeNullFields) {
		if (isIncludeNullFields == true ? false: Base.globvar.commitNullField == false)
			return this.map(function(){
				//如果是输入对象本身
				var isInput = ((this.tagName && (this.tagName.toLowerCase()=='input'||this.tagName=="TEXTAREA"))?true:false);
				return this.elements ? jQuery.makeArray( this.elements ) : jQuery.makeArray(isInput ? this:$(this).find(':input').get());
			})
			.filter(function(){
				return this.name && !this.disabled &&
					( this.checked || /^(?:select|textarea)/i.test( this.nodeName ) ||
						/^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i.test( this.type ) );
			})
			.map(function( i, elem ){
				var val = jQuery( this ).val();
				return val == null || ((typeof val =='string') && val.trim()=='') ?
					null :
					jQuery.isArray( val ) ?
						jQuery.map( val, function( val, i ){
							return { name: elem.name, value: val.trim().replace( /\r?\n/g, "\r\n" ) };
						}) :
						{ name: elem.name, value: val.trim().replace( /\r?\n/g, "\r\n" ) };
			}).get();
		else 
			return this.map(function(){
				//如果是输入对象本身
				var isInput = ((this.tagName && (this.tagName.toLowerCase()=='input'||this.tagName=="TEXTAREA"))?true:false);
				return this.elements ? jQuery.makeArray( this.elements ) : jQuery.makeArray(isInput ? this:$(this).find(':input').get());
			})
			.filter(function(){
				return this.name && !this.disabled &&
					( this.checked || /^(?:select|textarea)/i.test( this.nodeName ) ||
						/^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i.test( this.type ) );
			}).get();
	}
});
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function(a){
    a.fn.IsBubblePopupOpen = function() {
        var c = null;
        a(this).each(function(d, e) {
            var b = a(e).data("private_jquerybubblepopup_options");
            if (b != null && typeof b == "object" && !a.isArray(b) && !a.isEmptyObject(b) && b.privateVars != null && typeof b.privateVars == "object" && !a.isArray(b.privateVars) && !a.isEmptyObject(b.privateVars) && typeof b.privateVars.is_open != "undefined") {
                c = b.privateVars.is_open ? true: false
            }
            return false
        });
        return c
    };
    a.fn.GetBubblePopupLastDisplayDateTime = function() {
        var b = null;
        a(this).each(function(e, f) {
            var d = a(f).data("private_jquerybubblepopup_options");
            if (d != null && typeof d == "object" && !a.isArray(d) && !a.isEmptyObject(d) && d.privateVars != null && typeof d.privateVars == "object" && !a.isArray(d.privateVars) && !a.isEmptyObject(d.privateVars) && typeof d.privateVars.last_display_datetime != "undefined" && d.privateVars.last_display_datetime != null) {
                b = c(d.privateVars.last_display_datetime)
            }
            return false
        });
        function c(d) {
            return new Date(d * 1000)
        }
        return b
    };
    a.fn.GetBubblePopupLastModifiedDateTime = function() {
        var b = null;
        a(this).each(function(e, f) {
            var d = a(f).data("private_jquerybubblepopup_options");
            if (d != null && typeof d == "object" && !a.isArray(d) && !a.isEmptyObject(d) && d.privateVars != null && typeof d.privateVars == "object" && !a.isArray(d.privateVars) && !a.isEmptyObject(d.privateVars) && typeof d.privateVars.last_modified_datetime != "undefined" && d.privateVars.last_modified_datetime != null) {
                b = c(d.privateVars.last_modified_datetime)
            }
            return false
        });
        function c(d) {
            return new Date(d * 1000)
        }
        return b
    };
    a.fn.GetBubblePopupCreationDateTime = function() {
        var b = null;
        a(this).each(function(e, f) {
            var d = a(f).data("private_jquerybubblepopup_options");
            if (d != null && typeof d == "object" && !a.isArray(d) && !a.isEmptyObject(d) && d.privateVars != null && typeof d.privateVars == "object" && !a.isArray(d.privateVars) && !a.isEmptyObject(d.privateVars) && typeof d.privateVars.creation_datetime != "undefined" && d.privateVars.creation_datetime != null) {
                b = c(d.privateVars.creation_datetime)
            }
            return false
        });
        function c(d) {
            return new Date(d * 1000)
        }
        return b
    };
    a.fn.GetBubblePopupMarkup = function() {
        var b = null;
        a(this).each(function(d, e) {
            var c = a(e).data("private_jquerybubblepopup_options");
            if (c != null && typeof c == "object" && !a.isArray(c) && !a.isEmptyObject(c) && c.privateVars != null && typeof c.privateVars == "object" && !a.isArray(c.privateVars) && !a.isEmptyObject(c.privateVars) && typeof c.privateVars.id != "undefined") {
                b = a("#" + c.privateVars.id).length > 0 ? a("#" + c.privateVars.id).html() : null
            }
            return false
        });
        return b
    };
    a.fn.GetBubblePopupID = function() {
        var b = null;
        a(this).each(function(d, e) {
            var c = a(e).data("private_jquerybubblepopup_options");
            if (c != null && typeof c == "object" && !a.isArray(c) && !a.isEmptyObject(c) && c.privateVars != null && typeof c.privateVars == "object" && !a.isArray(c.privateVars) && !a.isEmptyObject(c.privateVars) && typeof c.privateVars.id != "undefined") {
                b = c.privateVars.id
            }
            return false
        });
        return b
    };
    a.fn.RemoveBubblePopup = function() {
        var b = 0;
        a(this).each(function(d, e) {
            var c = a(e).data("private_jquerybubblepopup_options");
            if (c != null && typeof c == "object" && !a.isArray(c) && !a.isEmptyObject(c) && c.privateVars != null && typeof c.privateVars == "object" && !a.isArray(c.privateVars) && !a.isEmptyObject(c.privateVars) && typeof c.privateVars.id != "undefined") {
                a(e).unbind("managebubblepopup");
                a(e).unbind("setbubblepopupinnerhtml");
                a(e).unbind("setbubblepopupoptions");
                a(e).unbind("positionbubblepopup");
                a(e).unbind("freezebubblepopup");
                a(e).unbind("unfreezebubblepopup");
                a(e).unbind("showbubblepopup");
                a(e).unbind("hidebubblepopup");
                a(e).data("private_jquerybubblepopup_options", {});
                if (a("#" + c.privateVars.id).length > 0) {
                    a("#" + c.privateVars.id).remove()
                }
                b++
            }
        });
        return b
    };
    a.fn.HasBubblePopup = function() {
        var c = false;
        a(this).each(function(d, e) {
            var b = a(e).data("private_jquerybubblepopup_options");
            if (b != null && typeof b == "object" && !a.isArray(b) && !a.isEmptyObject(b) && b.privateVars != null && typeof b.privateVars == "object" && !a.isArray(b.privateVars) && !a.isEmptyObject(b.privateVars) && typeof b.privateVars.id != "undefined") {
                c = true
            }
            return false
        });
        return c
    };
    a.fn.GetBubblePopupOptions = function() {
        var b = {};
        a(this).each(function(c, d) {
            b = a(d).data("private_jquerybubblepopup_options");
            if (b != null && typeof b == "object" && !a.isArray(b) && !a.isEmptyObject(b) && b.privateVars != null && typeof b.privateVars == "object" && !a.isArray(b.privateVars) && !a.isEmptyObject(b.privateVars)) {
                delete b.privateVars
            } else {
                b = null
            }
            return false
        });
        if (a.isEmptyObject(b)) {
            b = null
        }
        return b
    };
    a.fn.SetBubblePopupInnerHtml = function(b, c) {
        a(this).each(function(d, e) {
            if (typeof c != "boolean") {
                c = true
            }
            a(e).trigger("setbubblepopupinnerhtml", [b, c])
        })
    };
    a.fn.SetBubblePopupOptions = function(b) {
        a(this).each(function(c, d) {
            a(d).trigger("setbubblepopupoptions", [b])
        })
    };
    a.fn.ShowBubblePopup = function(b, c) {
        a(this).each(function(d, e) {
            a(e).trigger("showbubblepopup", [b, c, true]);
            return false
        })
    };
    a.fn.ShowAllBubblePopups = function(b, c) {
        a(this).each(function(d, e) {
            a(e).trigger("showbubblepopup", [b, c, true])
        })
    };
    a.fn.HideBubblePopup = function() {
        a(this).each(function(b, c) {
            a(c).trigger("hidebubblepopup", [true]);
            return false
        })
    };
    a.fn.HideAllBubblePopups = function() {
        a(this).each(function(b, c) {
            a(c).trigger("hidebubblepopup", [true])
        })
    };
    a.fn.FreezeBubblePopup = function() {
        a(this).each(function(b, c) {
            a(c).trigger("freezebubblepopup");
            return false
        })
    };
    a.fn.FreezeAllBubblePopups = function() {
        a(this).each(function(b, c) {
            a(c).trigger("freezebubblepopup")
        })
    };
    a.fn.UnfreezeBubblePopup = function() {
        a(this).each(function(b, c) {
            a(c).trigger("unfreezebubblepopup");
            return false
        })
    };
    a.fn.UnfreezeAllBubblePopups = function() {
        a(this).each(function(b, c) {
            a(c).trigger("unfreezebubblepopup")
        })
    };
    a.fn.CreateBubblePopup = function(e) {
        var r = {
            me: this,
            cache: [],
            options_key: "private_jquerybubblepopup_options",
            model_tr: ["top", "middle", "bottom"],
            model_td: ["left", "middle", "right"],
            model_markup: '<div class="{BASE_CLASS} {TEMPLATE_CLASS}"{DIV_STYLE} id="{DIV_ID}"> 									<table{TABLE_STYLE}> 									<tbody> 									<tr> 										<td class="{BASE_CLASS}-top-left"{TOP-LEFT_STYLE}>{TOP-LEFT}</td> 										<td class="{BASE_CLASS}-top-middle"{TOP-MIDDLE_STYLE}>{TOP-MIDDLE}</td> 										<td class="{BASE_CLASS}-top-right"{TOP-RIGHT_STYLE}>{TOP-RIGHT}</td> 									</tr> 									<tr> 										<td class="{BASE_CLASS}-middle-left"{MIDDLE-LEFT_STYLE}>{MIDDLE-LEFT}</td> 										<td class="{BASE_CLASS}-innerHtml"{INNERHTML_STYLE}>{INNERHTML}</td> 										<td class="{BASE_CLASS}-middle-right"{MIDDLE-RIGHT_STYLE}>{MIDDLE-RIGHT}</td> 									</tr> 									<tr> 										<td class="{BASE_CLASS}-bottom-left"{BOTTOM-LEFT_STYLE}>{BOTTOM-LEFT}</td> 										<td class="{BASE_CLASS}-bottom-middle"{BOTTOM-MIDDLE_STYLE}>{BOTTOM-MIDDLE}</td> 										<td class="{BASE_CLASS}-bottom-right"{BOTTOM-RIGHT_STYLE}>{BOTTOM-RIGHT}</td> 									</tr> 									</tbody> 									</table> 									</div>',
            privateVars: {
                id: null,
                creation_datetime: null,
                last_modified_datetime: null,
                last_display_datetime: null,
                is_open: false,
                is_freezed: false,
                is_animating: false,
                is_animation_complete: false,
                is_mouse_over: false,
                is_position_changed: false,
                last_options: {}
            },
            position: "top",
            positionValues: ["left", "top", "right", "bottom"],
            align: "center",
            alignValues: ["left", "center", "right", "top", "middle", "bottom"],
            alignHorizontalValues: ["left", "center", "right"],
            alignVerticalValues: ["top", "middle", "bottom"],
            distance: "20px",
            width: null,
            height: null,
            divStyle: {},
            tableStyle: {},
            innerHtml: null,
            innerHtmlStyle: {},
            tail: {
                align: "center",
                hidden: false
            },
            dropShadow: true,
            alwaysVisible: true,
            selectable: false,
            manageMouseEvents: true,
            mouseOver: "show",
            mouseOverValues: ["show", "hide"],
            mouseOut: "hide",
            mouseOutValues: ["show", "hide"],
            openingSpeed: 250,
            closingSpeed: 250,
            openingDelay: 0,
            closingDelay: 0,
            baseClass: "jquerybubblepopup",
            themeName: "azure",
            themePath: "jquerybubblepopup-themes/",
            themeMargins: {
                total: "13px",
                difference: "10px"
            },
            afterShown: function() {},
            afterHidden: function() {},
            hideElementId: [],
            marginHeight:10//森林皮肤bpop组件离输入框太近，不易点击输入框，liys
        };
        h(e);
        function g(v) {
            var w = {
                privateVars: {},
                width: r.width,
                height: r.height,
                divStyle: r.divStyle,
                tableStyle: r.tableStyle,
                position: r.position,
                align: r.align,
                distance: r.distance,
                openingSpeed: r.openingSpeed,
                closingSpeed: r.closingSpeed,
                openingDelay: r.openingDelay,
                closingDelay: r.closingDelay,
                mouseOver: r.mouseOver,
                mouseOut: r.mouseOut,
                tail: r.tail,
                innerHtml: r.innerHtml,
                innerHtmlStyle: r.innerHtmlStyle,
                baseClass: r.baseClass,
                themeName: r.themeName,
                themePath: r.themePath,
                themeMargins: r.themeMargins,
                dropShadow: r.dropShadow,
                manageMouseEvents: r.manageMouseEvents,
                alwaysVisible: r.alwaysVisible,
                selectable: r.selectable,
                afterShown: r.afterShown,
                afterHidden: r.afterHidden,
                hideElementId: r.hideElementId
            };
            var t = a.extend(false, w, (typeof v == "object" && !a.isArray(v) && !a.isEmptyObject(v) && v != null ? v: {}));
            t.privateVars.id = r.privateVars.id;
            t.privateVars.creation_datetime = r.privateVars.creation_datetime;
            t.privateVars.last_modified_datetime = r.privateVars.last_modified_datetime;
            t.privateVars.last_display_datetime = r.privateVars.last_display_datetime;
            t.privateVars.is_open = r.privateVars.is_open;
            t.privateVars.is_freezed = r.privateVars.is_freezed;
            t.privateVars.is_animating = r.privateVars.is_animating;
            t.privateVars.is_animation_complete = r.privateVars.is_animation_complete;
            t.privateVars.is_mouse_over = r.privateVars.is_mouse_over;
            t.privateVars.is_position_changed = r.privateVars.is_position_changed;
            t.privateVars.last_options = r.privateVars.last_options;
            t.width = (typeof t.width == "string" || typeof t.width == "number") && parseInt(t.width) > 0 ? parseInt(t.width) : r.width;
            t.height = (typeof t.height == "string" || typeof t.height == "number") && parseInt(t.height) > 0 ? parseInt(t.height) : r.height;
            t.divStyle = t.divStyle != null && typeof t.divStyle == "object" && !a.isArray(t.divStyle) && !a.isEmptyObject(t.divStyle) ? t.divStyle: r.divStyle;
            t.tableStyle = t.tableStyle != null && typeof t.tableStyle == "object" && !a.isArray(t.tableStyle) && !a.isEmptyObject(t.tableStyle) ? t.tableStyle: r.tableStyle;
            t.position = typeof t.position == "string" && o(t.position.toLowerCase(), r.positionValues) ? t.position.toLowerCase() : r.position;
            t.align = typeof t.align == "string" && o(t.align.toLowerCase(), r.alignValues) ? t.align.toLowerCase() : r.align;
            t.distance = (typeof t.distance == "string" || typeof t.distance == "number") && parseInt(t.distance) >= 0 ? parseInt(t.distance) : r.distance;
            t.openingSpeed = typeof t.openingSpeed == "number" && parseInt(t.openingSpeed) > 0 ? parseInt(t.openingSpeed) : r.openingSpeed;
            t.closingSpeed = typeof t.closingSpeed == "number" && parseInt(t.closingSpeed) > 0 ? parseInt(t.closingSpeed) : r.closingSpeed;
            t.openingDelay = typeof t.openingDelay == "number" && t.openingDelay >= 0 ? t.openingDelay: r.openingDelay;
            t.closingDelay = typeof t.closingDelay == "number" && t.closingDelay >= 0 ? t.closingDelay: r.closingDelay;
            t.mouseOver = typeof t.mouseOver == "string" && o(t.mouseOver.toLowerCase(), r.mouseOverValues) ? t.mouseOver.toLowerCase() : r.mouseOver;
            t.mouseOut = typeof t.mouseOut == "string" && o(t.mouseOut.toLowerCase(), r.mouseOutValues) ? t.mouseOut.toLowerCase() : r.mouseOut;
            t.tail = t.tail != null && typeof t.tail == "object" && !a.isArray(t.tail) && !a.isEmptyObject(t.tail) ? t.tail: r.tail;
            t.tail.align = typeof t.tail.align != "undefined" ? t.tail.align: r.tail.align;
            t.tail.hidden = typeof t.tail.hidden != "undefined" ? t.tail.hidden: r.tail.hidden;
            t.innerHtml = typeof t.innerHtml == "string" && t.innerHtml.length > 0 ? t.innerHtml: r.innerHtml;
            t.innerHtmlStyle = t.innerHtmlStyle != null && typeof t.innerHtmlStyle == "object" && !a.isArray(t.innerHtmlStyle) && !a.isEmptyObject(t.innerHtmlStyle) ? t.innerHtmlStyle: r.innerHtmlStyle;
            t.baseClass = j(typeof t.baseClass == "string" && t.baseClass.length > 0 ? t.baseClass: r.baseClass);
            t.themeName = typeof t.themeName == "string" && t.themeName.length > 0 ? a.trim(t.themeName) : r.themeName;
            t.themePath = typeof t.themePath == "string" && t.themePath.length > 0 ? a.trim(t.themePath) : r.themePath;
            t.themeMargins = t.themeMargins != null && typeof t.themeMargins == "object" && !a.isArray(t.themeMargins) && !a.isEmptyObject(t.themeMargins) && (typeof parseInt(t.themeMargins.total) == "number" && typeof parseInt(t.themeMargins.difference) == "number") ? t.themeMargins: r.themeMargins;
            t.dropShadow = typeof t.dropShadow == "boolean" && t.dropShadow == true ? true: false;
            t.manageMouseEvents = typeof t.manageMouseEvents == "boolean" && t.manageMouseEvents == true ? true: false;
            t.alwaysVisible = typeof t.alwaysVisible == "boolean" && t.alwaysVisible == true ? true: false;
            t.selectable = typeof t.selectable == "boolean" && t.selectable == true ? true: false;
            t.afterShown = typeof t.afterShown == "function" ? t.afterShown: r.afterShown;
            t.afterHidden = typeof t.afterHidden == "function" ? t.afterHidden: r.afterHidden;
            t.hideElementId = a.isArray(t.hideElementId) ? t.hideElementId: r.hideElementId;
            if (t.position == "left" || t.position == "right") {
                t.align = o(t.align, r.alignVerticalValues) ? t.align: "middle"
            } else {
                t.align = o(t.align, r.alignHorizontalValues) ? t.align: "center"
            }
            for (var u in t.tail) {
                switch (u) {
                case "align":
                    t.tail.align = typeof t.tail.align == "string" && o(t.tail.align.toLowerCase(), r.alignValues) ? t.tail.align.toLowerCase() : r.tail.align;
                    if (t.position == "left" || t.position == "right") {
                        t.tail.align = o(t.tail.align, r.alignVerticalValues) ? t.tail.align: "middle"
                    } else {
                        t.tail.align = o(t.tail.align, r.alignHorizontalValues) ? t.tail.align: "center"
                    }
                    break;
                case "hidden":
                    t.tail.hidden = t.tail.hidden == true ? true: false;
                    break
                }
            }
            return t
        }
        function l(t) {
            if (t == 0) {
                return 0
            }
            if (t > 0) {
                return - (Math.abs(t))
            } else {
                return Math.abs(t)
            }
        }
        function o(v, w) {
            var t = false;
            for (var u in w) {
                if (w[u] == v) {
                    t = true;
                    break
                }
            }
            return t
        }
        function k(t) {
            a(t).each(function() {
                a("<img/>")[0].src = this
            })
        }
        function b(t) {
            if (t.hideElementId && t.hideElementId.length > 0) {
                for (var u = 0; u < t.hideElementId.length; u++) {
                    var v = (t.hideElementId[u].charAt(0) != "#" ? "#" + t.hideElementId[u] : t.hideElementId[u]);
                    a(v).css({
                        visibility: "hidden"
                    })
                }
            }
        }
        function s(u) {
            if (u.hideElementId && u.hideElementId.length > 0) {
                for (var v = 0; v < u.hideElementId.length; v++) {
                    var x = (u.hideElementId[v].charAt(0) != "#" ? "#" + u.hideElementId[v] : u.hideElementId[v]);
                    a(x).css({
                        visibility: "visible"
                    });
                    var w = a(x).length;
                    for (var t = 0; t < w.length; t++) {
                        a(w[t]).css({
                            visibility: "visible"
                        })
                    }
                }
            }
        }
        function m(u) {
            var w = u.themePath;
            var t = u.themeName;
            var v = (w.substring(w.length - 1) == "/" || w.substring(w.length - 1) == "\\") ? w.substring(0, w.length - 1) + "/" + t + "/": w + "/" + t + "/";
            return v + (u.dropShadow == true ? (a.browser.msie ? "ie/": "") : "ie/")
        }
        function j(t) {
            var u = t.substring(0, 1) == "." ? t.substring(1, t.length) : t;
            return u
        }
        function q(u) {
            if (a("#" + u.privateVars.id).length > 0) {
                var t = "bottom-middle";
                switch (u.position) {
                case "left":
                    t = "middle-right";
                    break;
                case "top":
                    t = "bottom-middle";
                    break;
                case "right":
                    t = "middle-left";
                    break;
                case "bottom":
                    t = "top-middle";
                    break
                }
                if (o(u.tail.align, r.alignHorizontalValues)) {
                    a("#" + u.privateVars.id).find("td." + u.baseClass + "-" + t).css("text-align", u.tail.align)
                } else {
                    a("#" + u.privateVars.id).find("td." + u.baseClass + "-" + t).css("vertical-align", u.tail.align)
                }
            }
        }
        function p(v) {
            var H = r.model_markup;
            var F = m(v);
            var x = "";
            var G = "";
            var u = "";
            if (!v.tail.hidden) {
                switch (v.position) {
                case "left":
                    G = "right";
                    u = "{MIDDLE-RIGHT}";
                    break;
                case "top":
                    G = "bottom";
                    u = "{BOTTOM-MIDDLE}";
                    break;
                case "right":
                    G = "left";
                    u = "{MIDDLE-LEFT}";
                    break;
                case "bottom":
                    G = "top";
                    u = "{TOP-MIDDLE}";
                    break
                }
                x = '<img src="' + F + "tail-" + G + "." + (v.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + v.baseClass + '-tail" />'
            }
            var t = r.model_tr;
            var z = r.model_td;
            var K, E, A, J;
            var B = "";
            var y = "";
            var D = new Array();
            for (E in t) {
                A = "";
                J = "";
                for (K in z) {
                    A = t[E] + "-" + z[K];
                    A = A.toUpperCase();
                    J = "{" + A + "_STYLE}";
                    A = "{" + A + "}";
                    if (A == u) {
                        H = H.replace(A, x);
                        B = ""
                    } else {
                        H = H.replace(A, "");
                        B = ""
                    }
                    if (t[E] + "-" + z[K] != "middle-middle") {
                        y = F + t[E] + "-" + z[K] + "." + (v.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif");
                        D.push(y);
                        H = H.replace(J, ' style="' + B + "background-image:url(" + y + ');"')
                    }
                }
            }
            if (D.length > 0) {
                k(D)
            }
            var w = "";
            if (v.tableStyle != null && typeof v.tableStyle == "object" && !a.isArray(v.tableStyle) && !a.isEmptyObject(v.tableStyle)) {
                for (var C in v.tableStyle) {
                    w += C + ":" + v.tableStyle[C] + ";"
                }
            }
            w += (v.width != null || v.height != null) ? (v.width != null ? "width:" + v.width + "px;": "") + (v.height != null ? "height:" + v.height + "px;": "") : "";
            H = w.length > 0 ? H.replace("{TABLE_STYLE}", ' style="' + w + '"') : H.replace("{TABLE_STYLE}", "");
            var I = "";
            if (v.divStyle != null && typeof v.divStyle == "object" && !a.isArray(v.divStyle) && !a.isEmptyObject(v.divStyle)) {
                for (var C in v.divStyle) {
                    I += C + ":" + v.divStyle[C] + ";"
                }
            }
            H = I.length > 0 ? H.replace("{DIV_STYLE}", ' style="' + I + '"') : H.replace("{DIV_STYLE}", "");
            H = H.replace("{TEMPLATE_CLASS}", v.baseClass + "-" + v.themeName);
            H = v.privateVars.id != null ? H.replace("{DIV_ID}", v.privateVars.id) : H.replace("{DIV_ID}", "");
            while (H.indexOf("{BASE_CLASS}") > -1) {
                H = H.replace("{BASE_CLASS}", v.baseClass)
            }
            H = v.innerHtml != null ? H.replace("{INNERHTML}", v.innerHtml) : H.replace("{INNERHTML}", "");
            J = "";
            for (var C in v.innerHtmlStyle) {
                J += C + ":" + v.innerHtmlStyle[C] + ";"
            }
            H = J.length > 0 ? H.replace("{INNERHTML_STYLE}", ' style="' + J + '"') : H.replace("{INNERHTML_STYLE}", "");
            return H
        }
        function f() {
            return Math.round(new Date().getTime() / 1000)
        }
        function c(E, N, x) {
            var O = x.position;
            var K = x.align;
            var z = x.distance;
            var F = x.themeMargins;
            var I = new Array();
            var u = N.offset();
            var t = parseInt(u.top);
            var y = parseInt(u.left);
            var P = parseInt(N.outerWidth(false));
            var L = parseInt(N.outerHeight(false));
            var v = parseInt(E.outerWidth(false));
            var M = parseInt(E.outerHeight(false));
            F.difference = Math.abs(parseInt(F.difference));
            F.total = Math.abs(parseInt(F.total));
            var w = l(F.difference);
            var J = l(F.difference);
            var A = l(F.total);
            var H = m(x);
            switch (K) {
            case "left":
                I.top = O == "top" ? t - M - z + l(w) : t + L + z + w;
                I.left = y + A;
                break;
            case "center":
                var D = Math.abs(v - P) / 2;
                I.top = O == "top" ? t - M - z + l(w)  - r.marginHeight: t + L + z + w + r.marginHeight;
                I.left = v >= P ? y - D: y + D;
                break;
            case "right":
                var D = Math.abs(v - P);
                I.top = O == "top" ? t - M - z + l(w) : t + L + z + w;
                I.left = v >= P ? y - D + l(A) : y + D + l(A);
                break;
            case "top":
                I.top = t + A;
                I.left = O == "left" ? y - v - z + l(J) : y + P + z + J;
                break;
            case "middle":
                var D = Math.abs(M - L) / 2;
                I.top = M >= L ? t - D: t + D;
                I.left = O == "left" ? y - v - z + l(J) - r.marginHeight : y + P + z + J + r.marginHeight;
                break;
            case "bottom":
                var D = Math.abs(M - L);
                I.top = M >= L ? t - D + l(A) : t + D + l(A);
                I.left = O == "left" ? y - v - z + l(J) : y + P + z + J;
                break
            }
            I.position = O;
            if (a("#" + x.privateVars.id).length > 0 && a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").length > 0) {
                a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").remove();
                var G = "bottom";
                var C = "bottom-middle";
                switch (O) {
                case "left":
                    G = "right";
                    C = "middle-right";
                    break;
                case "top":
                    G = "bottom";
                    C = "bottom-middle";
                    break;
                case "right":
                    G = "left";
                    C = "middle-left";
                    break;
                case "bottom":
                    G = "top";
                    C = "top-middle";
                    break
                }
                a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + C).empty();
                a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + C).html('<img src="' + H + "tail-" + G + "." + (x.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + x.baseClass + '-tail" />');
                q(x)
            }
            if (x.alwaysVisible == true) {
                if (I.top < a(window).scrollTop() || I.top + M > a(window).scrollTop() + a(window).height()) {
                    if (a("#" + x.privateVars.id).length > 0 && a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").length > 0) {
                        a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").remove()
                    }
                    var B = "";
                    if (I.top < a(window).scrollTop()) {
                        I.position = "bottom";
                        I.top = t + L + z + w;
                        if (a("#" + x.privateVars.id).length > 0 && !x.tail.hidden) {
                            a("#" + x.privateVars.id).find("td." + x.baseClass + "-top-middle").empty();
                            a("#" + x.privateVars.id).find("td." + x.baseClass + "-top-middle").html('<img src="' + H + "tail-top." + (x.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + x.baseClass + '-tail" />');
                            B = "top-middle"
                        }
                    } else {
                        if (I.top + M > a(window).scrollTop() + a(window).height()) {
                            I.position = "top";
                            I.top = t - M - z + l(w);
                            if (a("#" + x.privateVars.id).length > 0 && !x.tail.hidden) {
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-bottom-middle").empty();
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-bottom-middle").html('<img src="' + H + "tail-bottom." + (x.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + x.baseClass + '-tail" />');
                                B = "bottom-middle"
                            }
                        }
                    }
                    if (I.left < 0) {
                        I.left = 0;
                        if (B.length > 0) {
                            a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + B).css("text-align", "center")
                        }
                    } else {
                        if (I.left + v > a(window).width()) {
                            I.left = a(window).width() - v;
                            if (B.length > 0) {
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + B).css("text-align", "center")
                            }
                        }
                    }
                } else {
                    if (I.left < 0 || I.left + v > a(window).width()) {
                        if (a("#" + x.privateVars.id).length > 0 && a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").length > 0) {
                            a("#" + x.privateVars.id).find("img." + x.baseClass + "-tail").remove()
                        }
                        var B = "";
                        if (I.left < 0) {
                            I.position = "right";
                            I.left = y + P + z + J;
                            if (a("#" + x.privateVars.id).length > 0 && !x.tail.hidden) {
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-middle-left").empty();
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-middle-left").html('<img src="' + H + "tail-left." + (x.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + x.baseClass + '-tail" />');
                                B = "middle-left"
                            }
                        } else {
                            if (I.left + v > a(window).width()) {
                                I.position = "left";
                                I.left = y - v - z + l(J);
                                if (a("#" + x.privateVars.id).length > 0 && !x.tail.hidden) {
                                    a("#" + x.privateVars.id).find("td." + x.baseClass + "-middle-right").empty();
                                    a("#" + x.privateVars.id).find("td." + x.baseClass + "-middle-right").html('<img src="' + H + "tail-right." + (x.dropShadow == true ? (a.browser.msie ? "gif": "png") : "gif") + '" alt="" class="' + x.baseClass + '-tail" />');
                                    B = "middle-right"
                                }
                            }
                        }
                        if (I.top < a(window).scrollTop()) {
                            I.top = a(window).scrollTop();
                            if (B.length > 0) {
                                a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + B).css("vertical-align", "middle")
                            }
                        } else {
                            if (I.top + M > a(window).scrollTop() + a(window).height()) {
                                I.top = (a(window).scrollTop() + a(window).height()) - M;
                                if (B.length > 0) {
                                    a("#" + x.privateVars.id).find("td." + x.baseClass + "-" + B).css("vertical-align", "middle")
                                }
                            }
                        }
                    }
                }
            }
            return I
        }
        function d(u, t) {
            a(u).data(r.options_key, t)
        }
        function n(t) {
            return a(t).data(r.options_key)
        }
        function i(t) {
            var u = t != null && typeof t == "object" && !a.isArray(t) && !a.isEmptyObject(t) ? true: false;
            return u
        }
        function h(t) {
            a(window).resize(function() {
                a(r.me).each(function(u, v) {
                    a(v).trigger("positionbubblepopup")
                })
            });
            a(document).mousemove(function(u) {
                a(r.me).each(function(v, w) {
                    a(w).trigger("managebubblepopup", [u.pageX, u.pageY])
                })
            });
            a(r.me).each(function(v, w) {
                var u = g(t);
                u.privateVars.creation_datetime = f();
                u.privateVars.id = u.baseClass + "-" + u.privateVars.creation_datetime + "-" + v;
                d(w, u);
                a(w).bind("managebubblepopup",
                function(y, C, B) {
                    var N = n(this);
                    if (i(N) && i(N.privateVars) && typeof C != "undefined" && typeof B != "undefined") {
                        if (N.manageMouseEvents) {
                            var E = a(this);
                            var z = E.offset();
                            var L = parseInt(z.top);
                            var H = parseInt(z.left);
                            var F = parseInt(E.outerWidth(false));
                            var K = parseInt(E.outerHeight(false));
                            var J = false;
                            if (H <= C && C <= F + H && L <= B && B <= K + L) {
                                J = true
                            } else {
                                J = false
                            }
                            if (J && !N.privateVars.is_mouse_over) {
                                N.privateVars.is_mouse_over = true;
                                d(this, N);
                                if (N.mouseOver == "show") {
                                	if ($(this).is(":hidden")) return;//林森添加
                                    a(this).trigger("showbubblepopup")
                                } else {
                                    if (N.selectable && a("#" + N.privateVars.id).length > 0) {
                                        var x = a("#" + N.privateVars.id);
                                        var A = x.offset();
                                        var D = parseInt(A.top);
                                        var I = parseInt(A.left);
                                        var G = parseInt(x.outerWidth(false));
                                        var M = parseInt(x.outerHeight(false));
                                        if (I <= C && C <= G + I && D <= B && B <= M + D) {} else {
                                            a(this).trigger("hidebubblepopup")
                                        }
                                    } else {
                                        a(this).trigger("hidebubblepopup")
                                    }
                                }
                            } else {
                                if (!J && N.privateVars.is_mouse_over) {
                                    N.privateVars.is_mouse_over = false;
                                    d(this, N);
                                    if (N.mouseOut == "show") {
                                        a(this).trigger("showbubblepopup")
                                    } else {
                                        if (N.selectable && a("#" + N.privateVars.id).length > 0) {
                                            var x = a("#" + N.privateVars.id);
                                            var A = x.offset();
                                            var D = parseInt(A.top);
                                            var I = parseInt(A.left);
                                            var G = parseInt(x.outerWidth(false));
                                            var M = parseInt(x.outerHeight(false));
                                            if (I <= C && C <= G + I && D <= B && B <= M + D) {} else {
                                                a(this).trigger("hidebubblepopup")
                                            }
                                        } else {
                                            a(this).trigger("hidebubblepopup")
                                        }
                                    }
                                } else {
                                    if (!J && !N.privateVars.is_mouse_over) {
                                        if (N.selectable && a("#" + N.privateVars.id).length > 0 && !N.privateVars.is_animating) {
                                            var x = a("#" + N.privateVars.id);
                                            var A = x.offset();
                                            var D = parseInt(A.top);
                                            var I = parseInt(A.left);
                                            var G = parseInt(x.outerWidth(false));
                                            var M = parseInt(x.outerHeight(false));
                                            if (I <= C && C <= G + I && D <= B && B <= M + D) {} else {
                                                a(this).trigger("hidebubblepopup")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                a(w).bind("setbubblepopupinnerhtml",
                function(A, x, z) {
                    var y = n(this);
                    if (i(y) && i(y.privateVars) && typeof x != "undefined") {
                        y.privateVars.last_modified_datetime = f();
                        if (typeof z == "boolean" && z == true) {
                            y.innerHtml = x
                        }
                        d(this, y);
                        if (a("#" + y.privateVars.id).length > 0) {
                            a("#" + y.privateVars.id).find("td." + y.baseClass + "-innerHtml").html(x);
                            if (y.privateVars.is_animation_complete) {
                                a(this).trigger("positionbubblepopup", [false])
                            } else {
                                a(this).trigger("positionbubblepopup", [true])
                            }
                        }
                    }
                });
                a(w).bind("setbubblepopupoptions",
                function(A, z) {
                    var x = n(this);
                    if (i(x) && i(x.privateVars)) {
                        var y = x;
                        x = g(z);
                        x.privateVars.id = y.privateVars.id;
                        x.privateVars.creation_datetime = y.privateVars.creation_datetime;
                        x.privateVars.last_modified_datetime = f();
                        x.privateVars.last_display_datetime = y.privateVars.last_display_datetime;
                        x.privateVars.is_open = y.privateVars.is_open;
                        x.privateVars.is_freezed = y.privateVars.is_freezed;
                        x.privateVars.last_options = {};
                        d(this, x)
                    }
                });
                a(w).bind("positionbubblepopup",
                function(A, y) {
                    var z = n(this);
                    if (i(z) && i(z.privateVars) && a("#" + z.privateVars.id).length > 0 && z.privateVars.is_open == true) {
                        var x = a("#" + z.privateVars.id);
                        var C = c(x, a(this), z);
                        var B = 2;
                        if (typeof y == "boolean" && y == true) {
                            x.css({
                                top: C.top,
                                left: C.left
                            })
                        } else {
                            switch (z.position) {
                            case "left":
                                x.css({
                                    top:
                                    C.top,
                                    left: (C.position != z.position ? C.left - (Math.abs(z.themeMargins.difference) * B) : C.left + (Math.abs(z.themeMargins.difference) * B))
                                });
                                break;
                            case "top":
                                x.css({
                                    top:
                                    (C.position != z.position ? C.top - (Math.abs(z.themeMargins.difference) * B) : C.top + (Math.abs(z.themeMargins.difference) * B)),
                                    left: C.left
                                });
                                break;
                            case "right":
                                x.css({
                                    top:
                                    C.top,
                                    left: (C.position != z.position ? C.left + (Math.abs(z.themeMargins.difference) * B) : C.left - (Math.abs(z.themeMargins.difference) * B))
                                });
                                break;
                            case "bottom":
                                x.css({
                                    top:
                                    (C.position != z.position ? C.top + (Math.abs(z.themeMargins.difference) * B) : C.top - (Math.abs(z.themeMargins.difference) * B)),
                                    left: C.left
                                });
                                break
                            }
                        }
                    }
                });
                a(w).bind("freezebubblepopup",
                function() {
                    var x = n(this);
                    if (i(x) && i(x.privateVars)) {
                        x.privateVars.is_freezed = true;
                        d(this, x)
                    }
                });
                a(w).bind("unfreezebubblepopup",
                function() {
                    var x = n(this);
                    if (i(x) && i(x.privateVars)) {
                        x.privateVars.is_freezed = false;
                        d(this, x)
                    }
                });
                a(w).bind("showbubblepopup",
                function(x, A, D, G) {
                    var H = n(this);
                    if ((typeof G == "boolean" && G == true && (i(H) && i(H.privateVars))) || (typeof G == "undefined" && (i(H) && i(H.privateVars) && !H.privateVars.is_freezed && !H.privateVars.is_open))) {
                        if (typeof G == "boolean" && G == true) {
                            a(this).trigger("unfreezebubblepopup")
                        }
                        H.privateVars.is_open = true;
                        H.privateVars.is_freezed = false;
                        H.privateVars.is_animating = false;
                        H.privateVars.is_animation_complete = false;
                        if (i(H.privateVars.last_options)) {
                            H = H.privateVars.last_options
                        } else {
                            H.privateVars.last_options = {}
                        }
                        if (i(A)) {
                            var C = H;
                            var F = f();
                            H = g(A);
                            H.privateVars.id = C.privateVars.id;
                            H.privateVars.creation_datetime = C.privateVars.creation_datetime;
                            H.privateVars.last_modified_datetime = F;
                            H.privateVars.last_display_datetime = F;
                            H.privateVars.is_open = true;
                            H.privateVars.is_freezed = false;
                            H.privateVars.is_animating = false;
                            H.privateVars.is_animation_complete = false;
                            H.privateVars.is_mouse_over = C.privateVars.is_mouse_over;
                            H.privateVars.is_position_changed = C.privateVars.is_position_changed;
                            H.privateVars.last_options = {};
                            if (typeof D == "boolean" && D == false) {
                                C.privateVars.last_modified_datetime = F;
                                C.privateVars.last_display_datetime = F;
                                H.privateVars.last_options = C
                            }
                        }
                        d(this, H);
                        b(H);
                        if (a("#" + H.privateVars.id).length > 0) {
                            a("#" + H.privateVars.id).remove()
                        }
                        var y = {};
                        var B = p(H);
                        y = a(B);
                        y.appendTo("body");
                        y = a("#" + H.privateVars.id);
                        y.css({
                            opacity: 0,
                            top: "0px",
                            left: "0px",
                            position: "absolute",
                            display: "block"
                        });
                        if (H.dropShadow == true) {
                            if (a.browser.msie && parseInt(a.browser.version) < 9) {
                                a("#" + H.privateVars.id + " table").addClass(H.baseClass + "-ie")
                            }
                        }
                        q(H);
                        var E = c(y, a(this), H);
                        y.css({
                            top: E.top,
                            left: E.left
                        });
                        if (E.position == H.position) {
                            H.privateVars.is_position_changed = false
                        } else {
                            H.privateVars.is_position_changed = true
                        }
                        d(this, H);
                        var z = setTimeout(function() {
                            H.privateVars.is_animating = true;
                            d(w, H);
                            y.stop();
                            switch (H.position) {
                            case "left":
                                y.animate({
                                    opacity:
                                    1,
                                    left: (H.privateVars.is_position_changed ? "-=": "+=") + H.distance + "px"
                                },
                                H.openingSpeed, "swing",
                                function() {
                                    H.privateVars.is_animating = false;
                                    H.privateVars.is_animation_complete = true;
                                    d(w, H);
                                    if (H.dropShadow == true) {
                                        if (a.browser.msie && parseInt(a.browser.version) > 8) {
                                            y.addClass(H.baseClass + "-ie")
                                        }
                                    }
                                    H.afterShown()
                                });
                                break;
                            case "top":
                                y.animate({
                                    opacity:
                                    1,
                                    top: (H.privateVars.is_position_changed ? "-=": "+=") + H.distance + "px"
                                },
                                H.openingSpeed, "swing",
                                function() {
                                    H.privateVars.is_animating = false;
                                    H.privateVars.is_animation_complete = true;
                                    d(w, H);
                                    if (H.dropShadow == true) {
                                        if (a.browser.msie && parseInt(a.browser.version) > 8) {
                                            y.addClass(H.baseClass + "-ie")
                                        }
                                    }
                                    H.afterShown()
                                });
                                break;
                            case "right":
                                y.animate({
                                    opacity:
                                    1,
                                    left: (H.privateVars.is_position_changed ? "+=": "-=") + H.distance + "px"
                                },
                                H.openingSpeed, "swing",
                                function() {
                                    H.privateVars.is_animating = false;
                                    H.privateVars.is_animation_complete = true;
                                    d(w, H);
                                    if (H.dropShadow == true) {
                                        if (a.browser.msie && parseInt(a.browser.version) > 8) {
                                            y.addClass(H.baseClass + "-ie")
                                        }
                                    }
                                    H.afterShown()
                                });
                                break;
                            case "bottom":
                                y.animate({
                                    opacity:
                                    1,
                                    top: (H.privateVars.is_position_changed ? "+=": "-=") + H.distance + "px"
                                },
                                H.openingSpeed, "swing",
                                function() {
                                    H.privateVars.is_animating = false;
                                    H.privateVars.is_animation_complete = true;
                                    d(w, H);
                                    if (H.dropShadow == true) {
                                        if (a.browser.msie && parseInt(a.browser.version) > 8) {
                                            y.addClass(H.baseClass + "-ie")
                                        }
                                    }
                                    H.afterShown()
                                });
                                break
                            }
                        },
                        H.openingDelay)
                    }
                });
                a(w).bind("hidebubblepopup",
                function(B, x) {
                    var A = n(this);
                    if ((typeof x == "boolean" && x == true && (i(A) && i(A.privateVars) && a("#" + A.privateVars.id).length > 0)) || (typeof x == "undefined" && (i(A) && i(A.privateVars) && a("#" + A.privateVars.id).length > 0 && !A.privateVars.is_freezed && A.privateVars.is_open))) {
                        if (typeof x == "boolean" && x == true) {
                            a(this).trigger("unfreezebubblepopup")
                        }
                        A.privateVars.is_animating = false;
                        A.privateVars.is_animation_complete = false;
                        d(this, A);
                        var y = a("#" + A.privateVars.id);
                        var z = typeof x == "undefined" ? A.closingDelay: 0;
                        var C = setTimeout(function() {
                            A.privateVars.is_animating = true;
                            d(w, A);
                            y.stop();
                            if (A.dropShadow == true) {
                                if (a.browser.msie && parseInt(a.browser.version) > 8) {
                                    y.removeClass(A.baseClass + "-ie")
                                }
                            }
                            switch (A.position) {
                            case "left":
                                y.animate({
                                    opacity:
                                    0,
                                    left: (A.privateVars.is_position_changed ? "+=": "-=") + A.distance + "px"
                                },
                                A.closingSpeed, "swing",
                                function() {
                                    A.privateVars.is_open = false;
                                    A.privateVars.is_animating = false;
                                    A.privateVars.is_animation_complete = true;
                                    d(w, A);
                                    y.css("display", "none");
                                    A.afterHidden()
                                });
                                break;
                            case "top":
                                y.animate({
                                    opacity:
                                    0,
                                    top: (A.privateVars.is_position_changed ? "+=": "-=") + A.distance + "px"
                                },
                                A.closingSpeed, "swing",
                                function() {
                                    A.privateVars.is_open = false;
                                    A.privateVars.is_animating = false;
                                    A.privateVars.is_animation_complete = true;
                                    d(w, A);
                                    y.css("display", "none");
                                    A.afterHidden()
                                });
                                break;
                            case "right":
                                y.animate({
                                    opacity:
                                    0,
                                    left: (A.privateVars.is_position_changed ? "-=": "+=") + A.distance + "px"
                                },
                                A.closingSpeed, "swing",
                                function() {
                                    A.privateVars.is_open = false;
                                    A.privateVars.is_animating = false;
                                    A.privateVars.is_animation_complete = true;
                                    d(w, A);
                                    y.css("display", "none");
                                    A.afterHidden()
                                });
                                break;
                            case "bottom":
                                y.animate({
                                    opacity:
                                    0,
                                    top: (A.privateVars.is_position_changed ? "-=": "+=") + A.distance + "px"
                                },
                                A.closingSpeed, "swing",
                                function() {
                                    A.privateVars.is_open = false;
                                    A.privateVars.is_animating = false;
                                    A.privateVars.is_animation_complete = true;
                                    d(w, A);
                                    y.css("display", "none");
                                    A.afterHidden()
                                });
                                break
                            }
                        },
                        z);
                        A.privateVars.last_display_datetime = f();
                        A.privateVars.is_freezed = false;
                        d(this, A);
                        s(A)
                    }
                })
            })
        }
        return this
    }
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function removeNode(node){
		node.each(function(){
			$(this).remove();
			if ($.browser.msie){
				this.outerHTML = '';
			}
		});
	}
	
	function setSize(target, param){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		var pheader = panel.find('>div.panel-header');
		var pbody = panel.find('>div.panel-body');
		
		if (param){
			if (param.width) opts.width = param.width;
			if (param.height) opts.height = param.height;
			if (param.left != null) opts.left = param.left;
			if (param.top != null) opts.top = param.top;
		}
		
		if (opts.fit == true){
			var p = panel.parent();
			opts.width = p.width();
			opts.height = p.height();
		}
		panel.css({
			left: opts.left,
			top: opts.top
		});
		panel.css(opts.style);
		panel.addClass(opts.cls);
		pheader.addClass(opts.headerCls);
		pbody.addClass(opts.bodyCls);
		
		if (!isNaN(opts.width)){
			if ($.boxModel == true){
				panel.width(opts.width - (panel.outerWidth(true) - panel.width()));
				pheader.width(panel.width() - (pheader.outerWidth(true) - pheader.width()));
				pbody.width(panel.width() - (pbody.outerWidth(true) - pbody.width()));
			} else {
				panel.width(opts.width);
				pheader.width(panel.width());
				pbody.width(panel.width());
			}
		} else {
			panel.width('auto');
			pbody.width('auto');
		}
		if (!isNaN(opts.height)){
//			var height = opts.height - (panel.outerHeight()-panel.height()) - pheader.outerHeight();
//			if ($.boxModel == true){
//				height -= pbody.outerHeight() - pbody.height();
//			}
//			pbody.height(height);
			
			if ($.boxModel == true){
				panel.height(opts.height - (panel.outerHeight(true) - panel.height()));
				pbody.height(panel.height() - pheader.outerHeight(true) - (pbody.outerHeight(true) - pbody.height()));
			} else {
				panel.height(opts.height);
				pbody.height(panel.height() - pheader.outerHeight(true));
			}
		} else {
			pbody.height('auto');
		}
		panel.css('height', null);
		
		opts.onResize.apply(target, [opts.width, opts.height]);
		
		panel.find('>div.panel-body>div').triggerHandler('_resize');
	}
	
	function movePanel(target, param){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		if (param){
			if (param.left != null) opts.left = param.left;
			if (param.top != null) opts.top = param.top;
		}
		panel.css({
			left: opts.left,
			top: opts.top
		});
		opts.onMove.apply(target, [opts.left, opts.top]);
	}
	
	function wrapPanel(target){
		var panel = $(target).addClass('panel-body').wrap('<div class="panel panelnomargin"></div>').parent();
		panel.bind('_resize', function(){
			var opts = $.data(target, 'panel').options;
			if (opts.fit == true){
				setSize(target);
			}
			return false;
		});
		
		return panel;
	}
	
	function addHeader(target){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		removeNode(panel.find('>div.panel-header'));
		if (opts.title && !opts.noheader){
			var header = $('<div class="panel-header"><div class="panel-title">'+opts.title+'</div></div>').prependTo(panel);
			if (opts.iconCls){
				header.find('.panel-title').addClass('panel-with-icon');
				$('<div class="panel-icon"></div>').addClass(opts.iconCls).appendTo(header);
			}
			var tool = $('<div class="panel-tool"></div>').appendTo(header);
			if (opts.closable){
				$('<div class="panel-tool-close">x</div>').appendTo(tool).bind('click', onClose);
			}
			if (opts.maximizable){
				$('<div class="panel-tool-max"></div>').appendTo(tool).bind('click', onMax);
			}
			if (opts.minimizable){
				$('<div class="panel-tool-min"></div>').appendTo(tool).bind('click', onMin);
			}
			if (opts.collapsible){
				$('<div class="panel-tool-collapse" title="收缩"></div>').appendTo(tool).bind('click', onToggle);
			}
			if (opts.tools){
				for(var i=opts.tools.length-1; i>=0; i--){
					var t = $('<div></div>').addClass(opts.tools[i].iconCls).appendTo(tool);
					if (opts.tools[i].handler){
						t.bind('click', eval(opts.tools[i].handler));
					}
				}
			}
			tool.find('div').hover(
				function(){$(this).addClass('panel-tool-over');},
				function(){$(this).removeClass('panel-tool-over');}
			);
			panel.find('>div.panel-body').removeClass('panel-body-noheader');
		} else {
			panel.find('>div.panel-body').addClass('panel-body-noheader');
		}
		
		function onToggle(){
			if ($(this).hasClass('panel-tool-expand')){
				expandPanel(target, true);
			} else {
				collapsePanel(target, true);
			}
			return false;
		}
		
		function onMin(){
			minimizePanel(target);
			return false;
		}
		
		function onMax(){
			if ($(this).hasClass('panel-tool-restore')){
				restorePanel(target);
			} else {
				maximizePanel(target);
			}
			return false;
		}
		
		function onClose(){
			closePanel(target);
			return false;
		}
	}
	
	/**
	 * load content from remote site if the href attribute is defined
	 */
	function loadData(target){
		var state = $.data(target, 'panel');
		if (state.options.href && (!state.isLoaded || !state.options.cache)){
			state.isLoaded = false;
			var pbody = state.panel.find('>div.panel-body');
			pbody.html($('<div class="panel-loading"></div>').html(state.options.loadingMessage));
			pbody.load(state.options.href, null, function(){
				//lly add
				if (typeof require === 'function') {
					require(['domReady'], function (domReady) {
						domReady(function () {
							require(["taLayout"], function(){
								$(pbody).taLayout();
							})
						});
					});
				} else {
					$(pbody).taLayout();
				}
				state.options.onLoad.apply(target, arguments);
				state.isLoaded = true;
			});
		}
	}
	
	function openPanel(target, forceOpen){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		
		if (forceOpen != true){
			if (opts.onBeforeOpen.call(target) == false) return;
		}
		panel.show();
		opts.closed = false;
		opts.onOpen.call(target);
		
		if (opts.maximized == true) maximizePanel(target);
		if (opts.minimized == true) minimizePanel(target);
		if (opts.collapsed == true) collapsePanel(target);
		
		if (!opts.collapsed){
			loadData(target);
		}
	}
	
	function closePanel(target, forceClose){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		
		if (forceClose != true){
			if (opts.onBeforeClose.call(target) == false) return;
		}
		panel.hide();
		opts.closed = true;
		opts.onClose.call(target);
	}
	
	function destroyPanel(target, forceDestroy){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		
		if (forceDestroy != true){
			if (opts.onBeforeDestroy.call(target) == false) return;
		}
		removeNode(panel);
		opts.onDestroy.call(target);
	}
	
	function collapsePanel(target, animate){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		var body = panel.find('>div.panel-body');
		var tool = panel.find('>div.panel-header .panel-tool-collapse');
		if (tool.hasClass('panel-tool-expand'))return;
		body.stop(true, true);	// stop animation
		if (opts.onBeforeCollapse.call(target) == false) return;
		$.data(target, 'panel').original = {
			width: opts.width,
			height: opts.height,
			fit: opts.fit
		};
		setSize(target,{width:opts.width,height:27});
		tool.addClass('panel-tool-expand');
		if (animate == true){
			body.slideUp('normal', function(){
				opts.collapsed = true;
				opts.onCollapse.call(target);
			});
		} else {
			body.hide();
			opts.collapsed = true;
			opts.onCollapse.call(target);
		}
	}
	
	function expandPanel(target, animate){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		var body = panel.find('>div.panel-body');
		var tool = panel.find('>div.panel-header .panel-tool-collapse');
		
		if (!tool.hasClass('panel-tool-expand')) return;
		body.stop(true, true);	// stop animation
		if (opts.onBeforeExpand.call(target) == false) return;
		tool.removeClass('panel-tool-expand');
		var original = $.data(target, 'panel').original;
		opts.width = original.width;
		opts.height = original.height;
		opts.fit = original.fit;
		if (animate == true){
			body.slideDown('normal', function(){
				opts.collapsed = false;
				opts.onExpand.call(target);
				loadData(target);
				setSize(target);
			});
		} else {
			body.show();
			opts.collapsed = false;
			opts.onExpand.call(target);
			loadData(target);
			setSize(target);
		}
	}
	
	function maximizePanel(target){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		var tool = panel.find('>div.panel-header .panel-tool-max');

		if (tool.hasClass('panel-tool-restore')) return;
		
		tool.addClass('panel-tool-restore');
		
		$.data(target, 'panel').original = {
			width: opts.width,
			height: opts.height,
			left: opts.left,
			top: opts.top,
			fit: opts.fit
		};
		opts.left = 0;
		opts.top = 0;
		opts.fit = true;
		setSize(target);
		opts.minimized = false;
		opts.maximized = true;
		$('>div[fit=true],>form[fit=true]',panel.find('>div.panel-body')).triggerHandler('_resize');
		//针对window第一个为panel作为borderlayout的容器的
		$('>div div.l-layout',panel.find('>div.panel-body')).each(function(){
			$(this).triggerHandler('_resize');
		});
		opts.onMaximize.call(target);
	}
	
	function minimizePanel(target){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		panel.hide();
		opts.minimized = true;
		opts.maximized = false;
		$('>div[fit=true],>form[fit=true]',panel.find('>div.panel-body')).triggerHandler('_resize');
		//针对window第一个为panel作为borderlayout的容器的
		$('>div div.l-layout',panel.find('>div.panel-body')).each(function(){
			$(this).triggerHandler('_resize');
		}); 
		opts.onMinimize.call(target);
	}
	
	function restorePanel(target){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		var tool = panel.find('>div.panel-header .panel-tool-max');
		
		if (!tool.hasClass('panel-tool-restore')) return;
		
		panel.show();
		tool.removeClass('panel-tool-restore');
		var original = $.data(target, 'panel').original;
		opts.width = original.width;
		opts.height = original.height;
		opts.left = original.left;
		opts.top = original.top;
		opts.fit = original.fit;
		setSize(target);
		opts.minimized = false;
		opts.maximized = false;
		$('>div[fit=true],>form[fit=true]',panel.find('>div.panel-body')).triggerHandler('_resize');
		//针对window第一个为panel作为borderlayout的容器的
		$('>div div.l-layout',panel.find('>div.panel-body')).each(function(){
			$(this).triggerHandler('_resize');
		});

		opts.onRestore.call(target);
	}
	
	function setBorder(target){
		var opts = $.data(target, 'panel').options;
		var panel = $.data(target, 'panel').panel;
		if (opts.border == true){
			panel.find('>div.panel-header').removeClass('panel-header-noborder');
			panel.find('>div.panel-body').removeClass('panel-body-noborder');
		} else {
			panel.find('>div.panel-header').addClass('panel-header-noborder');
			panel.find('>div.panel-body').addClass('panel-body-noborder');
		}
	}
	
	function setTitle(target, title){
		$.data(target, 'panel').options.title = title;
		$(target).panel('header').find('div.panel-title').html(title);
	}
	
	$(window).unbind('.panel').bind('resize.panel', function(){
		var layout = $('body.layout');
		if (layout.length){
			layout.layout('resize');
		} else {
			$('body>div.panel').triggerHandler('_resize');
		}
	});
	
	$.fn.ta3panel = function(options, param){
		if (typeof options == 'string'){
			switch(options){
			case 'options':
				return $.data(this[0], 'panel').options;
			case 'panel':
				return $.data(this[0], 'panel').panel;
			case 'header':
				return $.data(this[0], 'panel').panel.find('>div.panel-header');
			case 'body':
				return $.data(this[0], 'panel').panel.find('>div.panel-body');
			case 'setTitle':
				return this.each(function(){
					setTitle(this, param);
				});
			case 'open':
				return this.each(function(){
					openPanel(this, param);
				});
			case 'close':
				return this.each(function(){
					closePanel(this, param);
				});
			case 'destroy':
				return this.each(function(){
					destroyPanel(this, param);
				});
			case 'refresh':
				return this.each(function(){
					$.data(this, 'panel').isLoaded = false;
					loadData(this);
				});
			case 'resize':
				return this.each(function(){
					setSize(this, param);
				});
			case 'move':
				return this.each(function(){
					movePanel(this, param);
				});
			case 'maximize':
				return this.each(function(){
					maximizePanel(this);
				});
			case 'minimize':
				return this.each(function(){
					minimizePanel(this);
				});
			case 'restore':
				return this.each(function(){
					restorePanel(this);
				});
			case 'collapse':
				return this.each(function(){
					collapsePanel(this, param);	// param: boolean,indicate animate or not
				});
			case 'expand':
				return this.each(function(){
					expandPanel(this, param);	// param: boolean,indicate animate or not
				});
			}
		}
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'panel');
			var opts;
			if (state){
				opts = $.extend(state.options, options);
			} else {
				var t = $(this);
				opts = $.extend({}, $.fn.ta3panel.defaults, {
					width: (parseInt(t.width()) || undefined),
					height: (parseInt(t.height()) || undefined),
					left: (parseInt(t.css('left')) || undefined),
					top: (parseInt(t.css('top')) || undefined),
					title: t.attr('title'),
					iconCls: t.attr('icon'),
					cls: t.attr('cls'),
					headerCls: t.attr('headerCls'),
					bodyCls: t.attr('bodyCls'),
					href: t.attr('href'),
					cache: (t.attr('cache') ? t.attr('cache') == 'true' : undefined),
					fit: (t.attr('fit') ? t.attr('fit') == 'true' : undefined),
					border: (t.attr('border') ? t.attr('border') == 'true' : undefined),
					noheader: (t.attr('noheader') ? t.attr('noheader') == 'true' : undefined),
					collapsible: (t.attr('collapsible') ? t.attr('collapsible') == 'true' : undefined),
					minimizable: (t.attr('minimizable') ? t.attr('minimizable') == 'true' : undefined),
					maximizable: (t.attr('maximizable') ? t.attr('maximizable') == 'true' : undefined),
					closable: (t.attr('closable') ? t.attr('closable') == 'true' : undefined),
					collapsed: (t.attr('collapsed') ? t.attr('collapsed') == 'true' : undefined),
					minimized: (t.attr('minimized') ? t.attr('minimized') == 'true' : undefined),
					maximized: (t.attr('maximized') ? t.attr('maximized') == 'true' : undefined),
					closed: (t.attr('closed') ? t.attr('closed') == 'true' : undefined)
				}, options);
				t.attr('title', '');
				state = $.data(this, 'panel', {
					options: opts,
					panel: wrapPanel(this),
					isLoaded: false
				});
			}
			
			if (opts.content){
				$(this).html(opts.content);
				if ($.parser){
					$.parser.parse(this);
				}
			}
			
			addHeader(this);
			setBorder(this);
//			loadData(this);
			
			if (opts.doSize == true){
				state.panel.css('display','block');
				setSize(this);
			}
			if (opts.closed == true){
				state.panel.hide();
			} else {
				openPanel(this);
			}
		});
	};
	$.fn.ta3panel.defaults = {
		title: null,
		iconCls: null,
		width: 'auto',
		height: 'auto',
		left: null,
		top: null,
		cls: null,
		headerCls: null,
		bodyCls: null,
		style: {},
		href: null,
		cache: true,
		fit: false,
		border: true,
		doSize: true,	// true to set size and do layout
		noheader: false,
		content: null,	// the body content if specified
		
		collapsible: false,
		minimizable: false,
		maximizable: false,
		closable: false,
		collapsed: false,
		minimized: false,
		maximized: false,
		closed: false,
		
		// custom tools, every tool can contain two properties: iconCls and handler
		// iconCls is a icon CSS class
		// handler is a function, which will be run when tool button is clicked
		tools: [],	
		
		href: null,
		loadingMessage: 'Loading...',
		onLoad: function(){},
		onBeforeOpen: function(){},
		onOpen: function(){},
		onBeforeClose: function(){},
		onClose: function(){},
		onBeforeDestroy: function(){},
		onDestroy: function(){},
		onResize: function(width,height){},
		onMove: function(left,top){},
		onMaximize: function(){},
		onRestore: function(){},
		onMinimize: function(){},
		onBeforeCollapse: function(){},
		onBeforeExpand: function(){},
		onCollapse: function(){},
		onExpand: function(){}
	};
	$.fn.panel = $.fn.ta3panel;
}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	// 注册 组件
    $.extend(true, window, {
        Ta: { 
            Query: toQuery 
        }
    }); 
    /**
     * 构造函数
     * id：dom标签的id
     * options ：参数
     * args ：额外参数，可以为多个
     */
    function toQuery(id, options, args) {
        // 默认参数
        var defaults = {
        		queryFormId:id,
        		queryBtnId:null,
        		resetBtnId:null,
        		toggleBtnId:null,
        		isAutoQuery:false,
        		queryMethod:null,
        		queryHotKey:null,
        		resetHotKey:null,
        		resetGridIds:null,
        		resetCallback:null,
        		targetGrid:null,
        		url:null,
        		otherParam:null,
        		validator:null,
        		autoValidate:false,
        		successCallback:null,
        		failureCallback:null
        };
        ////////////////
        //组件全局变量
        var $g_container;
        //////////////////////////////////////////////////////////////////////////////////////////////
        //初始化方法
        function init() {
            $g_container = $(id); 
            options = $.extend({},defaults,options); //默认option和自订opetion结合
            //TODO 组件初始化
            var  $queryForm=$("#"+options.queryFormId);
            var  $basic_cols=$queryForm.attr("cols");
          //处理basicfield组件的布局
            var  $basicfield=$queryForm.find('div.basicfield');
    		if($basicfield.length==1){
    			var $element=$basicfield.eq(0);
    			  
    				 var   _basicfield_tool=$element.find("div.basicfield-tool");
		    		    var  _bt_columnWidth=$element.attr("toolButtonsColumnWidth");
		    		    if(_bt_columnWidth){
		    		       _basicfield_tool.attr("columnWidth",_bt_columnWidth);
		    		    }
		    		    if($element.attr("cols")==""){
		    		    	$element.attr("cols",$basic_cols);
		    		    }else{
		    		    	$basic_cols= $element.attr("cols");
		    		    }
		    		    
		    		    var _field_divs=$element.find("div.fielddiv");
		    		    var _real_divs = [];
		    		    //pengwei 去除位于group容器中的其他fielddiv
		    		    _field_divs.each(function(){
		    		    	if($(this).parent().parent().hasClass("basicfield"))
		    		    		_real_divs.push($(this));
		    		    });
		    		    var _field_length=_real_divs.length;
		    		    var  _effect_field_col=[];
		    		    var  _count=0;
		    		     for(var i = 0; i<_real_divs.length; i++){
		    		         if(_real_divs[i].css("display")!="none"){
		    		             _effect_field_col.push(_count);
		    		         }
		    		         _count++;
		    		      };
		    		      if(_effect_field_col.length<$basic_cols-1){
		    		            if(_field_length>=1){
		    		               _basicfield_tool.parent().insertAfter(_real_divs[_field_length-1].parent());
		    		            }
		    		        }else{
		    		             _basicfield_tool.parent().insertAfter(_real_divs[_effect_field_col[$basic_cols-2]].parent());
		    		        }
    		} else{
    			  alert("query组件有且仅有一个basicfield子组件!");
    			  return;
    		}
    		//处理addtionfield组件的布局
    		var  $addtionfield=$queryForm.find('div.addtionfield');
    		if($addtionfield.length>0){
    			$addtionfield.each(function(){
    				if($(this).attr("cols")==""){
    					   $(this).attr("cols",$basic_cols);
    		        }
    			});
    		}
            
            //初始化按钮事件
          var _query = $("#"+options.queryBtnId);
      	  var _reset = $("#"+options.resetBtnId);
      	  var _toggle=$("#"+options.toggleBtnId);
      		if(options.queryHotKey && hotKeyregister){
      					hotKeyregister.add(options.queryHotKey,function(){_query.focus();_query.click();return false;});
      	    }
      		  if(options.resetHotKey && hotKeyregister){
      					hotKeyregister.add(options.resetHotKey,function(){_reset.focus();_reset.click();return false;});
      	     }
         _query.bind("click",function(){
      		if(options.isAutoQuery){
      			var  _submitIds="";
      			if(options.targetGrid!=null){
      				_submitIds+=options.targetGrid+",";
      			}
      			_submitIds+=options.queryFormId;
      			 Base.submit(_submitIds,options.url,options.otherParam,options.validator,options.autoValidate,options.successCallback,options.failureCallback);
      		}else{
      	           if (typeof options.validator == "boolean") {
      	                    if(options.validator==false)  return;
      	            }
      	           if (typeof options.queryMethod == "function"){
      	        	 options.queryMethod();
      	          }
      		}
      	});
         _reset.bind("click",function(){
      		  Base.resetForm(options.queryFormId);
      		$queryForm.find(".textinput").each(function(){
      		          var o=$(this);
      		          var selId=o.find("input").eq(0).attr("id");
      		          Base.setValue(selId,"");
      		     });
      		      if(options.resetGridIds!=null){
      		            var  ids=options.resetGridIds.split(",");
      		            for(var  id in ids){
      		                Base.clearGridData(ids[id]);
      		            }
      		      }
      		      if (typeof  options.resetCallback== "function"){
      		    	options.resetCallback();
      		       }      
      	});
      	
      	var    _addtion_field=$queryForm.find("div.addtionfield");
      	   if(_addtion_field.length==0){
      		    _toggle.hide();
      	   }else{
      		   _toggle.bind("click",function(){
      			          var  _icon_span=$("#"+options.toggleBtnId+">span>span>span");
      			           if(_icon_span.hasClass("xui-icon-slidedown")){
      			                 _icon_span.addClass("xui-icon-slideup").removeClass("xui-icon-slidedown");
      			               _addtion_field.show();
      			             $queryForm.nextAll("div.panel").each(function(){
      			               var  _fit=eval($(this).attr("fit"));
      			               if(_fit  &&  _fit==true){
      			                     $(this).tauipanel("resize");
      			                  }
      			               });
      			           $queryForm.nextAll("div.grid").each(function(){
      			                  var _o=$(this);
      			                  var  _grid_fit=eval(_o.attr("fit"));
      			                  if(!_o.hasClass("fielddiv")  &&  !_o.hasClass("addtionfield")   &&  !_o.hasClass("basicfield")){
      			                      if(_grid_fit   &&   _grid_fit==true)_o.tauifitheight();
      			                  }
      			               });
      			            }
      			          else{
      			             _icon_span.addClass("xui-icon-slidedown").removeClass("xui-icon-slideup");
      			             _addtion_field.hide();
      			           $queryForm.nextAll("div.panel").each(function(){
      			               var  _fit=eval($(this).attr("fit"));
      			               if(_fit  &&  _fit==true){
      			                     $(this).tauipanel("resize");
      			                  }
      			               });
      			         $queryForm.nextAll("div.grid").each(function(){
      			                  var _o=$(this);
      			                   var  _grid_fit=eval(_o.attr("fit"));
      			                  if(!_o.hasClass("fielddiv")  &&  !_o.hasClass("addtionfield")   &&  !_o.hasClass("basicfield")){
      			                       if(_grid_fit   &&   _grid_fit==true)_o.tauifitheight();
      			                  }
      			               });
      			      }
      			 }); 
      	   } 
           
        }
        init();//调用初始化方法
    }
})); 


(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	$.extend(true, window, {
		taselectpanel : taselectpanel
	});
	function taselectpanel($spDiv, options) {
		var self = this;
		options = $.extend({
			spanelId : "selectpanel",
			beforeSetVal : ""
		}, options || {});
		function init() {
			var $spDiv = $("#"+options.spanelId+"-spDiv");
			var $content = $("#"+options.spanelId+"-area");
			var $form = $("#"+options.spanelId+"-form");
			
			//pengwei 为输入框绑定鼠标经过和离开事件
			$spDiv.unbind('.selectpanel');
			$spDiv.bind("mouseover",function(){
				$spDiv.addClass("bottom-hide").parent().addClass("ta-sp-hover");
				$content.show();
			}).bind("mouseout",function(){
				$spDiv.removeClass("bottom-hide").parent().removeClass("ta-sp-hover")
				$content.hide();
			});
			
			$content.bind("mouseover",function(){
				$spDiv.addClass("bottom-hide");
				$content.show();
			}).bind("mouseout",function(){
				$spDiv.removeClass("bottom-hide");
				$content.hide();
			});
			var $prov = $content.find("#stock_province_item");
			var $city = $content.find("#stock_city_item");
			var $county = $content.find("#stock_county_item");
			/**
			 * 处理 省 市 县；绑定单击事件：
			 * 1、对隐藏框设值
			 * 2、查询其子项
			 * 3、单击县时对显示框设值，如果有设值前的回调则先执行回调函数beforeSetVal；其返回true才执行设值，false则不设值
			 */
			
			
			/**
			 * 预置json，测试使用；实际采用异步加载数据方式
			 */
			var provs = [{"id":"1","name":"北京"},{"id":"2","name":"天津"},{"id":"3","name":"河北"},{"id":"4","name":"四川"},{"id":"5","name":"云南"},{"id":"6","name":"广东"}];
			
			var citys = [{"id":"1","pid":"1","name":"北京市"},{"id":"2","pid":"2","name":"天津市"},{"id":"3","pid":"3","name":"石家庄市"},{"id":"4","pid":"3","name":"唐山市"},
			             {"id":"5","pid":"4","name":"成都市"},{"id":"6","pid":"4","name":"广安市"},{"id":"7","pid":"5","name":"昆明市"},{"id":"8","pid":"5","name":"玉溪市"},
			             {"id":"9","pid":"6","name":"广州市"},{"id":"10","pid":"6","name":"深圳市"},{"id":"11","pid":"6","name":"佛山市"}];
			
			var countys = [{"id":"1","pid":"1","name":"海淀区"},{"id":"2","pid":"1","name":"朝阳区"},{"id":"3","pid":"2","name":"和平区"},
			              {"id":"4","pid":"3","name":"长安区"},{"id":"5","pid":"4","name":"路南区"},{"id":"6","pid":"5","name":"锦江区"},
			              {"id":"7","pid":"5","name":"金牛区"},{"id":"8","pid":"6","name":"华蓥市"},{"id":"9","pid":"6","name":"岳池县"},
			              {"id":"10","pid":"7","name":"昆明区"},{"id":"11","pid":"8","name":"玉溪县"},{"id":"12","pid":"9","name":"黄埔区"},
			              {"id":"13","pid":"10","name":"罗湖区"},{"id":"1","pid":"1","name":"顺德区"}];
			
			loadAreas(null,provs,$prov);
			//省级
			$prov.find("a").bind("click",function(){
				$content.unbind("mouseout");
				$content.find("#provinceName span").text($(this).text());
				$form.find("#provinceId").val($(this).attr("data-value"));
				
				loadAreas(this,citys,$city);
				$content.find(".tab li a").removeClass("ta-sp-hover");
				$content.find("#cityName").addClass("ta-sp-hover");
				$prov.hide();
				$city.show();
				$county.hide();
				
				//市级
				$city.find("a").bind("click",function(){
					$content.find("#cityName span").text($(this).text());
					$form.find("#cityId").val($(this).attr("data-value"));
					
					loadAreas(this,countys,$county);
					$content.find(".tab li a").removeClass("ta-sp-hover");
					$content.find("#countyName").addClass("ta-sp-hover");
					$prov.hide();
					$city.hide();
					$county.show();
					//区县级
					$county.find("a").bind("click",function(){
						$content.find("#countyName span").text($(this).text());
						$form.find("#countyId").val($(this).attr("data-value"));
						
						var flag = true;
						var beforeSetVal = $spDiv.attr("beforeSetVal");
						if(beforeSetVal !=null && beforeSetVal != "")
							flag = eval(template+"()");
						if(flag){
							var address = $content.find("#provinceName span").text()+""+$content.find("#cityName span").text()+""+$content.find("#countyName span").text();
							$spDiv.find("div").attr("title",address).text(address);
							$content.hide();
						}
					});
				});
			});
			
			//pengwei 给下拉面板的tab绑定单击事件，单击tab就显示该tab对应的div地区内容
			$content.find(".tab").find("a").bind("click",function(){
				$content.unbind("mouseout");
				if(!$(this).hasClass("ta-sp-hover")){
					$content.find(".tab li a").removeClass("ta-sp-hover");
					$(this).addClass("ta-sp-hover");
					var _id = $(this).attr("_id");
					switch(_id){
						case "1": $prov.show();$city.hide();$county.hide();break;
						case "2": $prov.hide();$city.show();$county.hide();break;
						case "3": $prov.hide();$city.hide();$county.show();break;
						default : break;
					}
				}
			});
			
			function loadAreas(obj,data,$level){
				var provList = new Array;
				for(var i = 0;i < data.length; i++){
					var str = "";
					if(obj == null){
						str = "<li><a data-value='"+ data[i].id +"'>"+ data[i].name +"</a></li>";
					}else{
						var oid = $(obj).attr("data-value");
						if(data[i].pid == oid){
							str = "<li><a data-value='"+ data[i].id +"'>"+ data[i].name +"</a></li>";
						}
					}
					provList.push(str);
				}
				$level.find("ul").html(provList.join(" "));
			}
			
			return self;
		}// end init
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taselectpanel'// 将方法注册为公共方法
		});
	}
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function setTitle(target,title,asHtml){
		if (asHtml == true) $(">div >div.panel-title",$(target)).html(title);
		else $(">div >div.panel-title",$(target)).text(title);
	}
	function collapsePanel(target){
		var $panel = $(target);
		var tool = $('>div.panel-header .panel-tool-collapse',$panel);
		if(!tool)return;
		$panel.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideUp(200);
		tool.toggleClass('panel-tool-expand');
	}
	
	function expandPanel(target){
		var $panel = $(target);
		var tool = $('>div.panel-header .panel-tool-collapse',$panel);
		if(tool.hasClass('panel-tool-expand'))return;
		$panel.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideDown(200);		
		tool.toggleClass('panel-tool-expand');
	}
	/**
	 * 让panel的body部分自动随父亲容器的宽高伸展
	 */
	function fitPanel(target){
		var $panel = $(target);
		var $fitContent = $(">div.panel-body",$panel);
		if($fitContent.length==0){
			$fitContent = $panel;
		}else{
			$fitContent = $($fitContent[0]);
		}
		var hh_ = $(">div.panel-header",$panel).outerHeight(true);
		var headerHeight = hh_ ? hh_ : 0;
		var bp_ = $(">div.panel-button",$panel).outerHeight(true);
		var bpHeight = bp_ ? bp_-2: 0;
		var pt_ = $(">div.panel-toolbar",$panel).outerHeight(true);
		var ptHeight = pt_ ? pt_: 0;
		
		var h = 0;
		
        var panelparent = $panel.parent(); 
//        if(panelparent.hasClass("ez-fl")){//当设置有cols属性时，需要向上一层取父容器
//        	panelparent = $panel.parent().parent();
//        }
        h = panelparent.height();
        if (panelparent[0].tagName.toLowerCase() == "body") { 
            h = $(window).height();
            //h -= parseInt($('body').css('paddingTop'));
            h -= parseInt($('body').css('paddingBottom'));
            //h -= parseInt($('body').css('marginTop'));
            h -= parseInt($('body').css('marginBottom'));            
            h -= $panel.offset().top;
            var mb = $panel.css('marginBottom');
            h -= (mb=='auto'?0:parseInt(mb));
        }else if(panelparent.hasClass('window-body')){
        		var windowTop = parseInt($panel.offsetParent().css('top'));
	        	h -= $panel.offset().top - windowTop -24;
	        	var mt = $panel.css('marginTop');
	        	h += (mt=='auto'?0:parseInt(mt));
        }else{
        	if(panelparent.css('position')=='absolute'){
	        	h -= $panel.position().top-parseInt(panelparent.css('paddingTop'));
	        	if(panelparent.parent()[0].tagName.toLowerCase() == "body"){
	        		h -= panelparent.position().top-parseInt($('body').css('paddingTop'));
	        	}
	        }else{
	        	var pall = $panel.prevAll(':visible').not('#pageloading');
	        	if(pall.length>0){
	        		pall.each(function(){
	        			h -= $(this).outerHeight(true);
	        		});
	        	}
	        	var mt = $panel.css('marginTop');
		        h -= (mt=='auto'?0:parseInt(mt));
		        var mb = $panel.css('marginBottom');
		        h -= (mb=='auto'?0:parseInt(mb));
		        if($panel.parents(".l-layout-content").size()==0){
		        	var pb = panelparent.css('paddingBottom');
			        h -= (pb=='auto'?0:parseInt(pb));
		        }
	        }

        }
        h -= parseInt($fitContent.css('paddingTop'));
        h -= parseInt($fitContent.css('paddingBottom'));

        var opts = $.data(target, 'panel');
        
       // h -= 2;//去除边框
        h -= opts.heightDiff;
        h -= 2;
//        alert([h,headerHeight,bpHeight,ptHeight].join(','))
        var minHeight =  Number($(target).attr('minHeight'));
        
        if (h-headerHeight-bpHeight-ptHeight < minHeight){
        	$fitContent.height(minHeight - headerHeight-bpHeight-ptHeight);
        } else 
        	$fitContent.height(h-headerHeight-bpHeight-ptHeight);
       		
       	//$fitContent.triggerHandler('_resize');
//       alert('panel resize  '+$panel[0].id+"   "+$('>form',$fitContent).length);
        $('>div[fit=true] ,>form[fit=true]',$fitContent).triggerHandler('_resize');
        if($fitContent.hasClass('l-layout'))$fitContent.triggerHandler('_resize');//panel直接作为border布局
	}
	function mask(target,param){
	
	}
	$.fn.tauipanel = function(options, param, asHtml){
		if (typeof options == 'string'){
			switch(options){
			case 'setTitle':
				return this.each(function(){
					setTitle(this, param,asHtml);
				});
			case 'collapse':
				return this.each(function(){
					collapsePanel(this, param);
				});
			case 'expand':
				return this.each(function(){
					expandPanel(this, param);
				});
			case 'destroy':
				return this.each(function(){
					destroyPanel(this, param);
				});
			case 'resize':
				return this.each(function(){
					fitPanel(this);
				});
			
			case 'mask':
				return this.each(function(){
					mask(this,param);
				});
			}
		}

		options = options || {};
		return this.each(function(){
				var opts;
				var t = $(this);
				opts = $.extend({}, $.fn.tauipanel.defaults, {
					href: t.attr('href'),
					onLoad: (t.attr('onLoad') ? t.attr('onLoad'): undefined),
					fit:(t.attr('fit')=='true' ? true:false),
					heightDiff: (t.attr('heightDiff') || 0),
					minHeight : (t.attr('minHeight'))
				}, options);
				$.data(this, 'panel', opts);
				if(opts.fit){
					fitPanel(this);
					var $panel = $(this);
					$panel.bind('_resize',function(){
						fitPanel(t[0]);
					});
					if(this.parentNode.tagName.toLowerCase()=='body'){
						$(window).unbind('.tauipanel').bind('resize.tauipanel', function(){
								$panel.triggerHandler('_resize');
						});
					}
//					$(window).bind('resize.tauipanel',function(){alert('tabuipanel on resize')
//        				fitPanel(t[0]);
//        			});
				}
	    	 	$("> div.panel-header > div.panel-tool > div.panel-tool-collapse",t).mouseover(function(){
						$(this).addClass('panel-tool-over');
					}).mouseout(function(){
						$(this).removeClass('panel-tool-over');
					}).click(function(){
						var flag = $(this).hasClass("panel-tool-expand");
						if (!flag) {
							flag = false;
							var $p = $(this.parentNode.parentNode.parentNode);
							$p.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideUp(100);
							setTimeout(function(){
								$p.siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
							},100);
						} else {
							flag = true;
							var $p = $(this.parentNode.parentNode.parentNode);
							$p.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideDown(100);
							setTimeout(function(){
								$p.siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
							},100);
						}
						$(this).toggleClass('panel-tool-expand');
					});
	    	 	//李从波添加缩放控制代码2012.09.07
	    		var  originWidth=0;//初始高宽  z-index  top  left   margin  bodyHeight
	    		var  originHeight=0;
	    		var  originZindex=0;
	    		var  originTop=0;
	    		var  originLeft=0;
	    		var  originMarginTop=0;
	    		var  originMarginBottom=0;
	    		var  originMarginLeft=0;
	    		var  originMarginRight=0;
	    		var  originBodyHeight=0;
	    		var  maxWidth=0;	//最大高宽
	    		var  maxHeight=0;
	    		var  panelBodyBorder=0;//左右边框宽度
	    		var  panelToolbarHeight=0;//工具条的高度
	    		var  panelButtonbarHeight=0;//按钮条的高度
	    		var  isBorderLayout=false;//panel所在页面是否使用了border布局
	    		var  isWidthAuto=false;//宽度是否根据父容器自动计算
	    		var  isHeightAuto=false;//高度是否根据父容器自动计算
	    		var  $handler=null;//border布局下的工具条
	    		var  $borderContainer=$("body").find("div.l-layout-left,div.l-layout-right,div.l-layout-center,div.l-layout-top,div.l-layout-bottom");//border布局下的区域
	    	 	$("> div.panel-header > div.panel-tool > div.panel-tool-max",t).mouseover(function(){
					  $(this).addClass('panel-tool-over');
				}).mouseout(function(){
					  $(this).removeClass('panel-tool-over');
				}).click(function(){
					  var flag = $(this).hasClass("panel-tool-restore");
					  if (!flag) {
							  var $p = $(this.parentNode.parentNode.parentNode);
							  var  $panelBody= $p.find(">div.panel-body");
				    		if(originWidth==0  &&  originHeight==0 ){//初始化 初始高宽  top  left margin z-index等
				    			var  $panelToolbar=$p.find(">div.panel-toolbar");
								var  $panelButtonbar=$p.find(">div.panel-button");
								originWidth=$p.width();
								originHeight=$p.height();
								originTop=$p.css("top");
					    		originLeft=$p.css("left");
								originZindex=$p.css("z-index");
								originMarginTop=$p.css("margin-top");
								originMarginBottom=$p.css("margin-bottom");
								originMarginLeft=$p.css("margin-left");
								originMarginRight=$p.css("margin-right");
								originBodyHeight=$panelBody.height();
								panelBodyBorder=parseInt($panelBody.css("border-left-width").replace("px",""))+parseInt($panelBody.css("border-right-width").replace("px",""));
						    	if(($p.width()+parseInt(originMarginLeft.replace("px",""))+parseInt(originMarginRight.replace("px","")))==$p.parent().width()){
						    		isWidthAuto=true;
						    	}
						    	if($p.attr("fit")=="true"){
						    		isHeightAuto=true;
						    	}
						    	if($panelToolbar.length>0){
						    		panelToolbarHeight=31;
					        	  }
					        	if($panelButtonbar.length>0){
					        		panelButtonbarHeight=40;
					        	}
							}
				    		maxWidth=$(window).width()-2;//获取当前最大高宽
					    	maxHeight=$(window).height()-2;
							$handler=$("body").find("div.l-layout-drophandle-left,div.l-layout-drophandle-right,div.l-layout-drophandle-top,div.l-layout-drophandle-bottom,div.l-layout-collapse-left,div.l-layout-collapse-right").not(":hidden");
							if($borderContainer.length>0 || $handler.length>0){
								$handler.hide();//隐藏border布局下的工具条
								$borderContainer.css({position:"static"});//设置所有的boder容器position 为static
								isBorderLayout=true;
							}
							if(panelToolbarHeight>0  ||  panelButtonbarHeight>0 )$p.removeClass("panelnomargin");//移除no-magin 样式
							var  pIndex=(originZindex=="auto")?0:originZindex;
							$p.css({ position: "fixed",zIndex:9002>pIndex?9002:pIndex,background:"#fcfdfd"});//设置position为fixed 修改z-index显示在最前面  设置背景色盖住下方内容
							$p.animate({//动画开始
				             width:maxWidth,
				             height:maxHeight,
				             top:isBorderLayout==true?-2:(isHeightAuto==true?-1:-2),
				             left:1,
				             marginTop:3,
				             marginBottom:3,
				             marginLeft:0,
				             marginRight:0
				          },{duration:100, complete:function(){
				        	  var  panelBodyHeight=maxHeight-25-panelToolbarHeight-panelButtonbarHeight;//减去标题、 toolbar、 buttonbar的高度得到panelbody高度
				        	  $panelBody.css({height:panelBodyHeight,width:maxWidth-panelBodyBorder});
				        	  $panelBody.find('div[fit=true],form[fit=true],div.grid,div.panel').triggerHandler('_resize');//处理子容器自适应高度
				          }});
					  } else{
						  	var $p = $(this.parentNode.parentNode.parentNode);
							var $panelBody= $p.find(">div.panel-body");
							var pTop=(originTop=="auto")?0:originTop;
							var pLeft=(originLeft=="auto")?0:originLeft;
							$p.css({zIndex:originZindex});
							$p.animate({//动画开始
		   		              width:originWidth,
		   		              height:originHeight,
		   		              top:pTop,
				              left:pLeft,
				              marginTop:originMarginTop,
				              marginBottom:originMarginBottom,
				              marginLeft:originMarginLeft,
					          marginRight:originMarginRight
					          },{duration:100, complete:function(){
					        	  if($borderContainer || $handler){
					        		  $borderContainer.css({position:"absolute"});//恢复原有的设置
					        		  $handler.show();//显示border布局下的工具条
					        	  }
					            if(panelToolbarHeight>0  ||  panelButtonbarHeight>0 )$p.addClass("panelnomargin");//恢复no-margin样式
				        	    $p.css({ position: "relative",background:"none",top:originTop,left:originLeft,width: isWidthAuto==true?"auto":originWidth,height:isHeightAuto==true?"auto":originHeight});//处理缩放后页面缩放panel失效的问题		        		
				        	    $panelBody.css({height:originBodyHeight,width:isWidthAuto==true?"auto":originWidth-panelBodyBorder});//处理panelBody适应父容器高宽
					        	$panelBody.find('div[fit=true],form[fit=true],div.grid,div.panel').triggerHandler('_resize');//处理子容器自适应高度
					          }});
					  }
					  $(this).toggleClass('panel-tool-restore');
				})
		});
	};
	
	$.fn.tauipanel.defaults = {
		href: null,
		onLoad: function(){},
		fit:false,
		heightDiff:0
	};
}));

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
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	/**
	 * wrap dialog and return content panel.
	 */
	function wrapDialog(target){
		var t = $(target);
		t.wrapInner('<div class="dialog-content"></div>');
		var contentPanel = t.find('>div.dialog-content');
		
		contentPanel.css('padding', t.css('padding'));
		t.css('padding', 0);
		
		contentPanel.ta3panel({
			border:false
		});
		
		return contentPanel;
	}
	
	/**
	 * build the dialog
	 */
	function buildDialog(target){
		var opts = $.data(target, 'dialog').options;
		var contentPanel = $.data(target, 'dialog').contentPanel;
		
		$(target).find('div.dialog-toolbar').remove();
		$(target).find('div.dialog-button').remove();
		if (opts.toolbar){
			var toolbar = $('<div class="dialog-toolbar"></div>').prependTo(target);
			for(var i=0; i<opts.toolbar.length; i++){
				var p = opts.toolbar[i];
				if (p == '-'){
					toolbar.append('<div class="dialog-tool-separator"></div>');
				} else {
					var tool = $('<a href="javascript:void(0)"></a>').appendTo(toolbar);
					tool.css('float','left').text(p.text);
					if (p.iconCls) tool.attr('icon', p.iconCls);
					if (p.handler) tool[0].onclick = p.handler;
					tool.linkbutton({
						plain: true,
						disabled: (p.disabled || false)
					});
				}
			}
			toolbar.append('<div style="clear:both"></div>');
		}
		
		if (opts.buttons){
			var buttonsAlgin = "";
			if(opts.buttonsAlgin)buttonsAlgin = " style=\"text-align:"+opts.buttonsAlgin+"\"";
			var buttons = $('<div class="dialog-button" '+buttonsAlgin+'></div>').appendTo(target);
			for(var i=0; i<opts.buttons.length; i++){
				var p = opts.buttons[i];
				//修改成sexybutton
				var icon = "",text="",id="";
				if(p.iconCls)icon = "<span  class='"+p.iconCls+"'>";
				if(p.text)text = p.text;
				if(p.id)id = " id='"+id+"'";
				//var h = '<button'+id+' type="button" class="sexybutton"><span><span>'+icon+text+(icon==''?'':'</span>')+'</span></span></button>';
				var h = "";
				if(p.buttonHighHlight){
					h = '<button'+id+' type="button" class="sexybutton_163" style="margin-right:4px;"><span class="button_span isok">'+text+'</span></button>';
				}else{
					h = '<button'+id+' type="button" class="sexybutton_163" style="margin-right:4px;"><span class="button_span">'+text+'</span></button>';
				}
				var button = $(h).appendTo(buttons);
				button.focus(function(){
					$("span.button_span",this).addClass("button_focus");
				}).blur(function(){
					$("span.button_span",this).removeClass("button_focus");
				});
				if(p.hotKey && hotKeyregister)
					hotKeyregister.add(p.hotKey,function(){button.focus();button.click();return false;});
	
				if (p.handler) button[0].onclick = p.handler;
			}
		}
		
		if (opts.href){
			contentPanel.ta3panel({
				href: opts.href,
				onLoad: opts.onLoad
			});
			
			opts.href = null;
		}
		
		$(target).window($.extend({}, opts, {
			onResize:function(width, height){
				var wbody = $(target).ta3panel('panel').find('>div.panel-body');
				
				contentPanel.ta3panel('resize', {
					width: wbody.width(),
					height: (height=='auto') ? 'auto' :
							wbody.height() - wbody.find('>div.dialog-toolbar').outerHeight(true)
							- wbody.find('>div.dialog-button').outerHeight(true)
				});
				
				if (opts.onResize) opts.onResize.call(target, width, height);
			}
		}));
	}
	
	function refresh(target){
		var contentPanel = $.data(target, 'dialog').contentPanel;
		contentPanel.ta3panel('refresh');
	}
	
	$.fn.dialog = function(options, param){
		if (typeof options == 'string'){
			switch(options){
			case 'options':
				return $(this[0]).window('options');
			case 'dialog':
				return $(this[0]).window('window');
			case 'setTitle':
				return this.each(function(){
					$(this).window('setTitle', param);
				});
			case 'open':
				return this.each(function(){
					$(this).window('open', param);
				});
			case 'close':
				return this.each(function(){
					$(this).window('close', param);
				});
			case 'destroy':
				return this.each(function(){
					$(this).window('destroy', param);
				});
			case 'refresh':
				return this.each(function(){
					refresh(this);
				});
			case 'resize':
				return this.each(function(){
					$(this).window('resize', param);
				});
			case 'move':
				return this.each(function(){
					$(this).window('move', param);
				});
			case 'maximize':
				return this.each(function(){
					$(this).window('maximize');
				});
			case 'minimize':
				return this.each(function(){
					$(this).window('minimize');
				});
			case 'restore':
				return this.each(function(){
					$(this).window('restore');
				});
			case 'collapse':
				return this.each(function(){
					$(this).window('collapse', param);
				});
			case 'expand':
				return this.each(function(){
					$(this).window('expand', param);
				});
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'dialog');
			if (state){
				$.extend(state.options, options);
			} else {
				var t = $(this);
				var opts = $.extend({}, $.fn.dialog.defaults, {
					title:(t.attr('title') ? t.attr('title') : undefined),
					href:t.attr('href'),
					collapsible: (t.attr('collapsible') ? t.attr('collapsible') == 'true' : undefined),
					minimizable: (t.attr('minimizable') ? t.attr('minimizable') == 'true' : undefined),
					maximizable: (t.attr('maximizable') ? t.attr('maximizable') == 'true' : undefined),
					resizable: (t.attr('resizable') ? t.attr('resizable') == 'true' : undefined)
				}, options);
				$.data(this, 'dialog', {
					options: opts,
					contentPanel: wrapDialog(this)
				});
			}
			buildDialog(this);
		});
	};
	
	$.fn.dialog.defaults = {
		title: 'New Dialog',
		href: null,
		collapsible: false,
		minimizable: false,
		maximizable: false,
		resizable: false,
		
		toolbar:null,
		buttons:null,
		buttonsAlgin:null //设置按钮在left，right，center，默认right
	};
}));
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
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
    $.extend(true, window, {
        YH_Window: { 
            Window_Message: WindowMessage
        }
    }); 

    function WindowMessage(options) {
        
        var defaults = {
            height: 200,
            width: 300,
            type: 'slide',
            title: '消息提示',
            text: "无消息",
            speed: 1000
        };
        function init() {
            options = $.extend({},defaults,options); //默认option和自订option结合
            _createMessageWindow(options);
            if(options.display == "false"){
           		$('.message-container').css('display','none');
           }else
            _showType(options.type,options.id);
            $('#message_close').click(function(){
            	_hideType(options.type,options.id);
            });
            if (typeof options.textCallback == "function") { 
       			var callbackData = options.textCallback();
       			$('#message_content').html(callbackData);
       		}
       		if(!isNaN(options.closeTime)){
       			setTimeout(function(){
	       			_hideType(options.type,options.id);
	       		},options.closeTime);
       		}
       		return this;
        }
        function _createMessageWindow(options) {
        	$(document.body)
				.prepend('<div id="'
						+ options.id +'" class="message-container">'
						+ '<div class="message-head">'
						+ '<span id="message_close" class="message-close">×</span>'
						+ '<div class="message-headbody">'
						+ options.title
						+ '</div><div style="clear:both;"></div></div> <div class="message-body"><div id="message_content" >'
						+ options.text + '</div></div></div>');
			$('.message-container').css({'width':options.width,'height':options.height});
			$('#message_content').css({'width':options.width- 17,'height':options.height-50});
        }
        function _showType(type,id){
        	switch (type) {
			case 'slide':
				$("#"+id).slideDown(options.speed);
				break;
			case 'fade':
				$("#"+id).fadeIn(options.speed);
				break;
			case 'show':
				$("#"+id).show(options.speed);
				break;
			default:
				$("#"+id).slideDown(options.speed);
				break;
			}
        }
        function _hideType(type,id){
        	switch (type) {
			case 'slide':
				$("#"+id).slideUp(options.speed);
				break;
			case 'fade':
				$("#"+id).fadeOut(options.speed);
				break;
			case 'show':
				$("#"+id).hide(options.speed);
				break;
			default:
				$("#"+id).slideUp(options.speed);
				break;
			}
        }
        $.extend(this, { 
        	"cmptype":'messageWindow', //组建类型
            "UIVersion": "2.0a1" //组建版本
        });
        init();
    }
})); 


(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
	$.extend(true, window, {
        "Slick": {
            "Event":        Event,
            "EventData":    EventData,
            "Range":        Range,
            "NonDataRow":   NonDataItem,
            "Group":        Group,
            "GroupTotals":  GroupTotals,
            "EditorLock":   EditorLock,
            "GlobalEditorLock": new EditorLock()
        }
    });
    
    function EventData() {
        var isPropagationStopped = false;
        var isImmediatePropagationStopped = false;
        this.stopPropagation = function() {
            isPropagationStopped = true;
        };
        this.isPropagationStopped = function() {
            return isPropagationStopped;
        };
        this.stopImmediatePropagation = function() {
            isImmediatePropagationStopped = true;
        };
        this.isImmediatePropagationStopped = function() {
            return isImmediatePropagationStopped;
        };
    }
    
    function Event() {
        var handlers = [];
        this.subscribe = function(fn) {
            handlers.push(fn);
        };
        this.unsubscribe = function(fn) {
            for (var i = handlers.length - 1; i >= 0; i--) {
                if (handlers[i] === fn) {
                    handlers.splice(i, 1);
                }
            }
        };
        this.notify = function(args, e, scope) {
            e = e || new EventData();
            scope = scope || this;

            var returnValue;
            for (var i = 0; i < handlers.length && !(e.isPropagationStopped() || e.isImmediatePropagationStopped()); i++) {
                returnValue = handlers[i].call(scope, e, args);
            }
            return returnValue;
        };
    }
    
    function Range(fromRow, fromCell, toRow, toCell) {
        if (toRow === undefined && toCell === undefined) {
            toRow = fromRow;
            toCell = fromCell;
        }
        this.fromRow = Math.min(fromRow, toRow);
        this.fromCell = Math.min(fromCell, toCell);
        this.toRow = Math.max(fromRow, toRow);
        this.toCell = Math.max(fromCell, toCell);
        this.isSingleRow = function() {
            return this.fromRow == this.toRow;
        };
        this.isSingleCell = function() {
            return this.fromRow == this.toRow && this.fromCell == this.toCell;
        };
        this.contains = function(row, cell) {
            return row >= this.fromRow && row <= this.toRow &&
                   cell >= this.fromCell && cell <= this.toCell;
        };
        this.toString = function() {
            if (this.isSingleCell()) {
                return "(" + this.fromRow + ":" + this.fromCell + ")";
            }
            else {
                return "(" + this.fromRow + ":" + this.fromCell + " - " + this.toRow + ":" + this.toCell + ")";
            }
        };
    }
    
    function NonDataItem() {
        this.__nonDataRow = true;
    }
    
    function Group() {
        this.__group = true;
        this.__updated = false;
        this.count = 0;
        this.value = null;
        this.title = null;
        this.collapsed = false;
        this.totals = null;
    }
    
    Group.prototype = new NonDataItem();
    Group.prototype.equals = function(group) {
        return this.value === group.value &&
               this.count === group.count &&
               this.collapsed === group.collapsed;
    };
    
    function GroupTotals() {
        this.__groupTotals = true;
        this.group = null;
    }
    GroupTotals.prototype = new NonDataItem();
    function EditorLock() {
        var activeEditController = null;
        this.isActive = function(editController) {
            return (editController ? activeEditController === editController : activeEditController !== null);
        };
        this.activate = function(editController) {
            if (editController === activeEditController) {
                return;
            }
            if (activeEditController !== null) {
                throw "SlickGrid.EditorLock.activate: an editController is still active, can't activate another editController";
            }
            if (!editController.commitCurrentEdit) {
                throw "SlickGrid.EditorLock.activate: editController must implement .commitCurrentEdit()";
            }
            if (!editController.cancelCurrentEdit) {
                throw "SlickGrid.EditorLock.activate: editController must implement .cancelCurrentEdit()";
            }
            activeEditController = editController;
        };
        this.deactivate = function(editController) {
            if (activeEditController !== editController) {
                throw "SlickGrid.EditorLock.deactivate: specified editController is not the currently active one";
            }
            activeEditController = null;
        };
        this.commitCurrentEdit = function() {
            return (activeEditController ? activeEditController.commitCurrentEdit() : true);
        };
        this.cancelCurrentEdit = function cancelCurrentEdit() {
            return (activeEditController ? activeEditController.cancelCurrentEdit() : true);
        };
    }
}));



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
/*******************************************************************************
 * 表格基本功能
 * @module Grid
 * @namespace Slick
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery",
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
			if (navigator.userAgent.indexOf("Chrome") != -1) {
				var xx = getSpell(x);
				var yy = getSpell(y);
				return (xx == yy ? 0 :(xx > yy ? 1 : -1));
			}
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
                GroupItemMetadataProvider: GroupItemMetadataProvider
            }
        }
    });


    /***
     * Provides item metadata for group (Slick.Group) and totals (Slick.Totals) rows produced by the DataView.
     * This metadata overrides the default behavior and formatting of those rows so that they appear and function
     * correctly when processed by the grid.
     *
     * This class also acts as a grid plugin providing event handlers to expand & collapse groups.
     * If "grid.registerPlugin(...)" is not called, expand & collapse will not work.
     *
     * @class GroupItemMetadataProvider
     * @module Data
     * @namespace Slick.Data
     * @constructor
     * @param options
     */
    function GroupItemMetadataProvider(options) {
        var _grid;
        var _defaults = {
            groupCssClass: "slick-group",
            totalsCssClass: "slick-group-totals",
            groupFocusable: true,
            totalsFocusable: false,
            toggleCssClass: "slick-group-toggle",
            toggleExpandedCssClass: "expanded",
            toggleCollapsedCssClass: "collapsed",
            enableExpandCollapse: true
        };

        options = $.extend(true, {}, _defaults, options);


        function defaultGroupCellFormatter(row, cell, value, columnDef, item) {
            if (!options.enableExpandCollapse) {
                return item.title;
            }
            return "<span class='" + options.toggleCssClass + " " +
                    (item.collapsed ? options.toggleCollapsedCssClass : options.toggleExpandedCssClass) +
                    "'></span>" + item.title;
        }

        function defaultTotalsCellFormatter(row, cell, value, columnDef, item) {
            return (columnDef.groupTotalsFormatter && columnDef.groupTotalsFormatter(item, columnDef)) || "";
        }


        function init(grid) {
            _grid = grid;
            _grid.onClick.subscribe(handleGridClick);
            _grid.onKeyDown.subscribe(handleGridKeyDown);

        }

        function destroy() {
            if (_grid) {
                _grid.onClick.unsubscribe(handleGridClick);
                _grid.onKeyDown.unsubscribe(handleGridKeyDown);
            }
        }

        function handleGridClick(e, args) {
            var item = this.getDataItem(args.row);
            if (item && item instanceof Slick.Group && $(e.target).hasClass(options.toggleCssClass)) {
                if (item.collapsed) {
                    this.getData().expandGroup(item.value);
                }
                else {
                    this.getData().collapseGroup(item.value);
                }

                e.stopImmediatePropagation();
                e.preventDefault();
            }
        }

        // TODO:  add -/+ handling
        function handleGridKeyDown(e, args) {
            if (options.enableExpandCollapse && (e.which == 32)) {
                var activeCell = this.getActiveCell();
                if (activeCell) {
                    var item = this.getDataItem(activeCell.row);
                    if (item && item instanceof Slick.Group) {
                        if (item.collapsed) {
                            this.getData().expandGroup(item.value);
                        }
                        else {
                            this.getData().collapseGroup(item.value);
                        }

                        e.stopImmediatePropagation();
                        e.preventDefault();
                    }
                }
            }
        }

        function getGroupRowMetadata(item) {
            return {
                selectable: false,
                focusable: false,//options.groupFocusable,
                cssClasses: options.groupCssClass,
                columns: {
                    0: {
                        colspan: "*",
                        formatter: defaultGroupCellFormatter,
                        editor: null
                    }
                }
            };
        }

        function getTotalsRowMetadata(item) {
            return {
                selectable: false,
                focusable: options.totalsFocusable,
                cssClasses: options.totalsCssClass,
                formatter: defaultTotalsCellFormatter,
                editor: null
            };
        }


        
        return {
            "init":     init,
            "destroy":  destroy,
            "getGroupRowMetadata":  getGroupRowMetadata,
            "getTotalsRowMetadata": getTotalsRowMetadata
        };
    }
}));
/**
 * 分页条
 * 
 * @module Grid
 * @namespace Slick.Controls
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", "numberBox"], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
	 /**
	  * 创建分页条
	 * @class Pager
	 * @static
	 * @constructor
	 * @param {Object} dataView 
	 * @param {Object} grid
	 * @param {Object} $container
	 * @param {Object} options 参数
	 * @param {Array} buttons
	 */
    function SlickGridPager(dataView, grid, $container, options , buttons){
        var $status,$limit, $start; 								//分别为状态栏，条数，开始条数
        var $first = {};
        var $prev = {};
		var $howInput = {};
		var $pageL = {};
		var $next = {};
		var $last = {};
		var $limitInput = {};
		var $count;
		var $defaultCanvas;
		var $fullCanvas;
        var self = this;											//自身
        var count = -1, limit = 400, start = 0;						//记录总条数，每页条数，开始条数
        var def = {													//默认配置
        	pageSize : 400,											//分页大小
        	showCount : true,										//是否显示总条数及最后一页按钮
        	showButton: true,
        	showExcel:true,
        	showDetails:true,
        	expKeyOrName:true                      //导出时是导出码值还是描述值,默认是码值
        }
		/**
		 * 初始化函数，构建UI
		 * @method init
		 * @private
		 */
        function init(){
        	if(isNaN(options.pageSize)){
        		//查询config.properties配置文件中的默认每页记录数
        		var pSize = Base.globvar.pageSize;
	            if(!isNaN(pSize)){
	            	options.pageSize = parseInt(pSize);
	            }
        	}
        	options = $.extend(true, {}, def, options);				//配置
        	//每页显示条数不能查过10W条
        	if(Number(options.pageSize) > 99999){
        		options.pageSize = 99999;
        	}
            constructPagerUI();										//创建界面
        }
        /**
         * 公共方法,用于初始化状态
         * @method setStatus
         */
       
        function setStatus(total) {
        	//$pageL = $("<span>");
        	
        	//$howInput = $("<input>");
        	if (total > 0 || count > 0) {
        		//$start.val("0");
        		count = total == 0 ? count : total;
        		start = Number($start.val());
        		limit = Number($limit.val());
        		updatePager();
        		if($pageL.length==undefined || $howInput.length==undefined) return;
        		$pageL.html("/" + Math.ceil(count/limit));
        		$howInput.val((Math.ceil(start/limit) + 1));
        		if(options.showCount == true) {
        			$count.html("共 " + Math.ceil(count) + "条");
        		}
        	//	$status.text("第" + (Math.ceil(start/limit) + 1 ) + "页/共" + Math.ceil(count/limit) + "页　每页" + limit + "条/共" + count + "条");
        	} else if (count == -1) {
        		//$start.val("0");
        		start = Number($start.val());
        		limit = Number($limit.val());
        		updatePager();
        		//$last.hide();
//        		$pageL.html("页,共 " + Math.ceil(count/limit) + " 页显示 " + Number(start + 1) + " - " + Number(start+ limit) + "条");
        		if($pageL.length==undefined) return;
        		$pageL.html("/" + Math.ceil(count/limit));
        		if(options.showCount == true) {
        			$count.html("共 " + Math.ceil(0) + "条");
        		}
        		//$status.text("第" + start + "条~" + (start + limit) + "条/每页" + limit + "条");
        	}
        	else {
        		start = Number($start.val());
        		limit = Number($limit.val());
        		updatePager();
        	}
        }
        /**
         * 更新显示状态
         */
        function updatePager(){
        	var state = getNavState();
        	if ($first.length){
        		$first.attr("disabled", false).removeClass("slick-icon-page-first-dis").addClass("slick-icon-page-first");
        		if (!state.canGotoFirst) {$first.attr("disabled",true); $first.addClass("slick-icon-page-first-dis").removeClass("slick-icon-page-first")}
        	}
        	if ($last.length){
        		 $last.attr("disabled", false).removeClass("slick-icon-page-last-dis").addClass("slick-icon-page-last");
        		 if (!state.canGotoLast) {$last.attr("disabled",true);$last.addClass("slick-icon-page-last-dis").removeClass("slick-icon-page-last");}
        	}
        	if ($next.length){
        		 $next.attr("disabled", false).removeClass("slick-icon-page-next-dis").addClass("slick-icon-page-next");
        		 if (!state.canGotoNext) {$next.attr("disabled",true);$next.addClass("slick-icon-page-next-dis").removeClass("slick-icon-page-next");}
        	}
        	if ($prev.length){
        		$prev.attr("disabled", false).removeClass("slick-icon-page-prev-dis").addClass("slick-icon-page-prev");
        		if (!state.canGotoPrev) {$prev.attr("disabled",true);$prev.addClass("slick-icon-page-prev-dis").removeClass("slick-icon-page-prev");}
        	}
        }
        /**
         * 动态设置分页url
         * @method setPagerUrl
         * @param url url地址
         */
        function setPagerUrl(url) {
        	options.url = url;
        }
        /**
         * 远程调用
         */
        function load(url, param) {
        	if (typeof options.validateForm == "function") {
        		if (!options.validateForm()) {
        			return;
        		}
        	}
        	var suburl = url ? url : options.url;
        	var submitString = options.submitIds != undefined? options.submitIds +  "," + grid.getGridId(): grid.getGridId();
        	if (options.successCallBack == undefined) {
        		Base.submit(submitString, suburl, param);
        	} else {
        		Base.submit(submitString, suburl, param, null, null, options.successCallBack);
        	}
        }
		/**
		 * 判断是否可以进行一些操作
		 */
		function getNavState() {
			var cannotLeaveEditMode = !Slick.GlobalEditorLock.commitCurrentEdit();
            return {
                canGotoFirst:	!cannotLeaveEditMode && Number($limit.val()) != 0 && Number($start.val())/Number($limit.val())> 0,
                canGotoLast:	!cannotLeaveEditMode && Number($limit.val()) != 0 && Math.ceil(count/limit) > (Math.ceil(start/limit) + 1),
                canGotoPrev:	!cannotLeaveEditMode && Number($limit.val()) != 0&& Number($start.val())/Number($limit.val()) > 0,
                canGotoNext:!cannotLeaveEditMode && Number($limit.val()) != 0 && Math.ceil(count/limit) > (Math.ceil(start/limit) + 1)
            }
        }
        
        function gotoFirst() {
    		updatePager();
    		$start.val(0);
    		//$howInput.val(Math.ceil(start/limit) + 1);
    		load(options.url, options.param);
    		grid.clearDirtyWidthOutPager();
        }

        function gotoLast() {
        	updatePager();
        	var start = (Math.ceil(count/limit) -1) * limit ;
	        $start.val(start);
	        //$howInput.val(Math.ceil(start/limit) + 1);
        	load(options.url, options.param);
        	grid.clearDirtyWidthOutPager();
        }

        function gotoPrev() {
        	updatePager();
        	var num = Number($start.val()) - Number($limit.val());
        	if (num < 0) return;
        	$start.val(num);
        	//$howInput.val(Math.ceil(start/limit) + 1);
        	load(options.url, options.param);
        	grid.clearDirtyWidthOutPager();
        }

        function gotoNext(){
        	if ((Math.ceil(start/limit) + 1) == Math.ceil(count/limit)) return;//用count
        	updatePager();
        	$start.val(Number($start.val()) + Number($limit.val()));
        	//$howInput.val(Math.ceil(start/limit) + 1);
        	load(options.url, options.param);
        	grid.clearDirtyWidthOutPager();
        }
        /**
         * lins默认导出
         */
        function exportDefaultGridData() {
        	var a,b;
        	if(arguments[0] == "dangqian"){//导出当前页
        		a = grid.getColumns();
				b = grid.getDataView().getItems();
        	}else if(arguments[0] == "xuanze"){//导出选择数据
				a = grid.getColumns();
				b = grid.getSelectRowsDataToObj();
				b = $.extend(true, [], b);
				if(b.length < 1){
					Base.alert('请至少选择一条数据');
					return;
				}
			}
			var collection  = grid.getOptions().collectionsDataArrayObject;
			var row = [];
			var cell = [];
			var head =[];
			for (var i = 0; i < a.length; i ++ ) {
				if (a[i].id != "_checkbox_selector" && a[i].id != "__no" && !a[i].icon) {
					cell.push("\"" + a[i].id + "\"");
					head.push("\"" + a[i].name + "\"")
				}
			}
			row.push(head);
			for (var i = 0; i < b.length; i ++) {
				var cells=[];
				for (var j = 0; j < cell.length; j ++) {
					var cData = b[i][cell[j].replaceAll("\"","")];
					if(cData == undefined || cData === ""){
						cData = "";
					}else{
						//处理转义字符
						cData = JSON.stringify(cData.toString());
						cData = cData.substring(1, cData.length-1);
					}
					//var cData = b[i][cell[j].replaceAll("\"","")] == undefined?"":b[i][cell[j].replaceAll("\"","")].replaceAll("\"","\\\"");
					if (options.expKeyOrName == true && collection != undefined) {
							var collectcell = collection[cell[j].replaceAll("\"","")];
							var b_collected = false;
							if (collectcell && collectcell.length > 0) {
								for (var c = 0; c < collectcell.length ; c ++) {
									if (collectcell[c].id == cData) {
										//处理转义字符
										var collData = collectcell[c].name;
										collData = JSON.stringify(collData.toString());
										cells.push(collData);
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
			var $input = $("<textarea/>").attr("display", "none").val(Ta.util.obj2string(row)).attr("name", "_grid_item_export_excel")
			var $form = $("<form/>")//.attr("enctype","multipart/form-data")//.attr("accept-charset", "GBK")
				.append($input).attr("method", "post")
				.attr("display", "none")
				.appendTo("body")
				.attr("action", Base.globvar.contextPath + "/exportGridDefaultExcel.do")
				.submit()
				.remove();
		}
		
		//导出所有数据
		function exportDefaultGridDataAll(){
			if(grid.getDataView().getItems().length == 0){
				Base.alert("数据不能为空");
				return;
			}
			var a = grid.getColumns();
			var collection = [];
			var obj = grid.getCollectionsDataArrayObject();
			if(options.expKeyOrName == true){
				for(var x in obj){
					collection.push("\""+x+"\"");
				}
			}
			var row = [];
			var cell = [];
			var head =[];
			for (var i = 0; i < a.length; i ++ ) {
				if (a[i].id != "_checkbox_selector" && a[i].id != "__no" && !a[i].icon) {
					cell.push("\"" + a[i].id + "\"");
					head.push("\"" + encodeURI(a[i].name) + "\"");
				}
			}
			row.push(head);
			row.push(cell);
			if(options.sqlStatementName && options.resultType){
				var sql = [],result = [];
				sql.push("\""+options.sqlStatementName+"\"");
				result.push("\""+options.resultType+"\"");
				row.push(sql);
				row.push(result);
			}else{
				Base.alert("导出全部数据必须设置sqlStatementName和resultType属性");
				return;
			}
			row.push(collection);
			var $input = $("<textarea id='_gridHead_'/>").css("display", "none").val(Ta.util.obj2string(row)).attr("name", "_gridHead_");
			$input.appendTo("body");
			toQuery("_gridHead_,"+grid.getOptions().pagingOptions.submitIds,Base.globvar.contextPath + "/exportGridDataAllExcel.do");
			$input.remove();
		}
		
		//通用查询
		function  toQuery(submitIds,url)
	   {
	       submitIds = submitIds?submitIds:"";
		   var aids = submitIds.split(',');
		 	//根据ids拼接传递的条件字符串
		 	 var   queryStr="";
			var  datagridids = [];
			if(aids){
				for(var i=0;i<aids.length;i++){
					if(aids[i]==null || aids[i]=='')continue;
					var obj = Base.getObj(aids[i]);
					var $obj = $(obj);
					if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
					  (obj.tagName && (obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
						if(obj.cmptype=='flexbox')obj = $("#"+aids[i]);//下拉框
					  	
						for(var j=0;j<aids.length;j++){//对ids进行校验，不能父子嵌套
							if(aids[j]==null || aids[j]=='')continue;
							var obj2 = Base.getObj(aids[j]);
							if(obj2.cmptype=='flexbox')obj2 = $("#"+aids[j]);
							if(i != j && obj2.cmptype!='datagrid'){//找到其他对象
								
								if($(obj).has($(obj2)).length>0){
									alert(aids[j]+"对象在"+aids[i]+"对象里面，指定提交的元素id不能有包含与被包含关系");
									return false;
								}
								if($(obj2).has($(obj)).length>0){
									alert(aids[i]+"对象在"+aids[j]+"对象里面，指定提交的元素id不能有包含与被包含关系");
									return false;
								}
							}
						}
						if(queryStr=="")
							queryStr += $("#"+aids[i]).taserialize();
						else
							queryStr += "&"+$("#"+aids[i]).taserialize();
					}
					else if(obj.cmptype=='datagrid'){
						datagridids.push(new String(aids[i]));
						if(queryStr=="")
							queryStr += $("#"+aids[i]).taserialize();
						else
							queryStr += "&"+$("#"+aids[i]).taserialize();	
						aids[i]=null;//.splice(i,1);//删除当前id
					}
					else{
						alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
						return false;
					}
				}
			}
			//访问Action并提交参数
			 location.href=url+"?"+queryStr;
	   }
		function fullCanvas() {
			$defaultCanvas.show();
			$fullCanvas.hide();
			var $borderContainer=$("body").find("div.l-layout-left,div.l-layout-right,div.l-layout-center,div.l-layout-top,div.l-layout-bottom");//border布局下的区域
			$handler=$("body").find("div.l-layout-drophandle-left,div.l-layout-drophandle-right,div.l-layout-drophandle-top,div.l-layout-drophandle-bottom,div.l-layout-collapse-left,div.l-layout-collapse-right").not(":hidden");
			if($borderContainer.length>0 || $handler.length>0){
				grid.getContainer().width($(window).width() - 2);
				grid.getContainer().height($(window).height() - 2);
				grid.getContainer().addClass("slick-full-canvas-fixed");
				grid.resizeCanvas(true);
				$handler.hide();//隐藏border布局下的工具条
				$borderContainer.css({position:"static"});//设置所有的boder容器position 为static
			} else {
				grid.getContainer().width($(window).width() - 2);
				grid.getContainer().height($(window).height() - 2);
				grid.getContainer().addClass("slick-full-canvas");
				//grid.getContainer().addClass("slick-full-canvas-fixed");
				//grid.resizeCanvas(true);
				grid.resizeCanvas(true);
			}
		}
		function defaultCanvas() {
			$defaultCanvas.hide();
			$fullCanvas.show();
			var box = grid.getDefaultBox();
			var $borderContainer=$("body").find("div.l-layout-left,div.l-layout-right,div.l-layout-center,div.l-layout-top,div.l-layout-bottom");//border布局下的区域
			if($borderContainer.length>0 || $handler.length>0){
				grid.getContainer().width("100%").height(box.height).removeClass("slick-full-canvas-fixed");
				grid.resizeCanvas(true);
	    		$borderContainer.css({position:"absolute"});//恢复原有的设置
	    		$handler.show();//显示border布局下的工具条
			} else {
				grid.getContainer().width("100%").height(box.height).removeClass("slick-full-canvas");
				grid.resizeCanvas(true);
			}
		}
		/**
		 * 构建ui
		 */
        function constructPagerUI() {
            $container.empty();
            var $nav = $("<div class='slick-pager-nav-dis'/>").appendTo($container);
            if (options.showDetails) {
//          		var $limitPre = $("<span>").html("&nbsp;&nbsp;每页").appendTo($nav);
          		var select = '<select class="slick-pagination-page-list"><option>10</option><option>50</option><option>200</option><option>300</option><option>400</option><option>500</option><option>1000</option></select>';
          		$limitInput = $(select);
          		$limitInput.appendTo($nav);
          		$limitInput.change(function(value){
          			limit = Number($(this).val());
					$limit.val(limit);
					updatePager();
		        	load(options.url, options.param);
		        	grid.clearDirtyWidthOutPager();
          		});
//	            $("<span>").html("条&nbsp").appendTo($nav);
	            $("<span class='tool-separator'/>").appendTo($nav);
          	}
          	if (options.showButton) {
	    		$first = $('<button type="button"/>').addClass("slick-icon-page-button slick-icon-page-first-dis")
	          			.appendTo($nav)
	          			.mouseover(function(){$(this).addClass('x-btn-over')})
	          			.mouseout(function(){$(this).removeClass('x-btn-over')})
	          			.click(gotoFirst).attr("disabled", true);
	    		$prev = $('<button type="button"/>').addClass("slick-icon-page-button slick-icon-page-prev-dis")
	          			.appendTo($nav)
	          			.click(gotoPrev).attr("disabled", true);
	    		$("<span class='tool-separator'/>").appendTo($nav);
          	}		
          	if (options.showDetails) {
	          	$howInput = $("<input>")
	          				.numberbox()
	          				.addClass("slick-pagination-page")
	          				.appendTo($nav)
	          				.val("1")
	          				.change(function() {
	          			          	start = Number(this.value) - 1;
									if (Number(start) == "NaN" || Number(start) < 0){
										$(this).val(1);
								    	Base.alert("请输入正整数");
								    	return;
								    }
									if(start >= $pageL.html().substring(1,$pageL.html().length)){
										Base.alert("超出总页数,将返回最后一页","error");
										$(this).val($pageL.html().substring(1,$pageL.html().length));
										start = Number(this.value) - 1;
									}
									$start.val(start * limit);
									updatePager();
						        	load(options.url, options.param);
						        	grid.clearDirtyWidthOutPager();
							})
	          				.keyup(function(e){
									if(e.keyCode==13){
										$(this).blur();
										if ($.browser.msie) {
											start = Number(this.value) - 1;
											if (Number(start) == "NaN" || Number(start) <= 0){
												$(this).val(1);
										    	Base.alert("请输入正整数");
										    	return;
										    }
											if(start >= $pageL.html().substring(1,$pageL.html().length)){
												Base.alert("超出总页数,将返回最后一页","error");
												$(this).val($pageL.html().substring(1,$pageL.html().length));
												start = Number(this.value) - 1;
											}
											$start.val(start * limit);
											updatePager();
								        	load(options.url, options.param);
								        	grid.clearDirtyWidthOutPager();
									
										}
									}
	          					});;
	            $pageL = $("<span>").addClass("slick-pager-font").html("/1").appendTo($nav);
	            $("<span class='tool-separator'/>").appendTo($nav);
          	}
          	if (options.showButton) {
	    		$next = $('<button type="button"/>').addClass("slick-icon-page-button slick-icon-page-next-dis")
	          			.appendTo($nav)
	          			.click(gotoNext).attr("disabled", true);
	    		$last = $('<button class="slick-icon-page-button slick-icon-page-last-dis" type="button"/>')
	          			.appendTo($nav)
	          			.click(gotoLast).attr("disabled", true);
	    		 $("<span class='tool-separator'/>").appendTo($nav);
          	}

            $limit = $('<input id="'+grid.getGridId()+ '_limit" type="hidden" name="gridInfo[\''+grid.getGridId()+ '_limit\']" value="'+ options.pageSize +'"/>').appendTo($container);
            $start = $('<input id="'+grid.getGridId()+ '_start" type="hidden" name="gridInfo[\''+grid.getGridId()+ '_start\']" value="-1"/>').appendTo($container).val(0);
			
//            var icon_prefix = "<span class='ui-state-default ui-corner-all ui-icon-container'><span class='ui-icon ";
//            var icon_suffix = "' /></span>";

            var $settings = $("<span class='slick-pager-settings'/>").appendTo($container);
            if (options.showCount) {
            	$count = $("<span>").html("共" + 0 + "条&nbsp").appendTo($nav);
            }
			/** 自定义button*/
			if (options.buttons) {
				for (var but = 0; but < options.buttons.length; but ++) {
					options.buttons[but].appendTo($settings);
				}
			}
			
            /**excel按钮**/
			if (options.showExcel) {
	          	//var $exportDefaultExcel = $('<button class="sexybutton toolbarbt" type="button"/>')
		          	//		.html('<span><span><span class="grid-icon-excel">导出</span></span></span>')
		          		//	.appendTo($settings).click(exportDefaultGridData);
		        var $exportDefaultExcel = $('<button class="sexybutton toolbarbt" type="button"/>')
							.html('<span class="icon-excel icon16" title="导出"/>')
							.appendTo($settings);
				var c_id = grid.getGridId() + 'mm';
				var $c = $('<div id="'+c_id+ '" class="slick-pageToolExcelContent"></div>');
				var _dangqian_ = grid.getGridId() + '_dangqian_';
				var _xuanze_ = grid.getGridId() + '_xuanze_';
				var _quanbu_ = grid.getGridId() + '_quanbu_';
				var expButtonsArray = [],expButtonsStr = "";
				if(options.selectExpButtons){
					expButtonsArray = options.selectExpButtons.split(",");
					for(var i = 0 ; i < expButtonsArray.length; i++){
						if(expButtonsArray[i] == 1){
							expButtonsStr += '<div id="'+_dangqian_+ '" class="slick-datagrid-exp" title="导出当前页">导出当前页</div>';
						}else if(expButtonsArray[i] == 2){
							expButtonsStr += '<div id="'+_xuanze_+ '" class="slick-datagrid-exp" title="导出选择数据">导出选择数据</div>';
						}else if(expButtonsArray[i] == 3){
							expButtonsStr += '<div id="'+_quanbu_+'" class="slick-datagrid-exp" title="导出全部数据">导出全部数据</div>';
						}
					}
				}else{
					expButtonsStr += '<div id="'+_dangqian_+ '" class="slick-datagrid-exp" title="导出当前页">导出当前页</div><div id="'+_xuanze_+ '" class="slick-datagrid-exp" title="导出选择数据">导出选择数据</div><div id="'+_quanbu_+'" class="slick-datagrid-exp" title="导出全部数据">导出全部数据</div>';
				}
				
				$c.append(expButtonsStr);
				$exportDefaultExcel.after($c).click(function(e){
					var p = $exportDefaultExcel.position();
					var top = p.top - $c.outerHeight(true);
					$c.css({"top":top,"left":p.left-55-8});
					$c.show();
				});
				$('#'+_dangqian_).click(function(){
					exportDefaultGridData("dangqian");
					$c.hide();
				});
				$('#'+_xuanze_).click(function(){
					exportDefaultGridData("xuanze");
					$c.hide();
				});
				$('#'+_quanbu_).click(function(){
					exportDefaultGridDataAll();
					$c.hide();
				});
			}
			if (options.showToFull || true) {
	          	$defaultCanvas = $('<button class="sexybutton toolbarbt" type="button"/>')
		          			.html('<span class="icon-arrow-in icon16" title="最小化"/>')
		          			.appendTo($settings).css("display" ,"none").click(defaultCanvas);
			}
			if (options.showToFull || true) {
	          	$fullCanvas = $('<button class="sexybutton toolbarbt" type="button"/>')
		          			.html('<span class="icon-arrow-out icon16" title="最大化"/>')
		          			.appendTo($settings).click(fullCanvas);
			}
//			if (options.showButton) {
//          		$(icon_prefix + "ui-icon-seek-first" + icon_suffix).click(gotoFirst).addClass("ui-state-disabled").appendTo($nav);
//         		$(icon_prefix + "ui-icon-seek-prev" + icon_suffix).click(gotoPrev).addClass("ui-state-disabled").appendTo($nav);
//         		$(icon_prefix + "ui-icon-seek-next" + icon_suffix).click(gotoNext).addClass("ui-state-disabled").appendTo($nav);
//			}
//         if (options.showCount)
//            	$(icon_prefix + "ui-icon-seek-end" + icon_suffix).click(gotoLast).addClass("ui-state-disabled").appendTo($nav);
                    
            $container.children().wrapAll("<div class='slick-pager' />");
        }
        /**
         * 重置
         * @method clearDirty
         */
		function clearDirty () {
			$start.val(0);
			count = -1;
			start = 0;
			updatePager();
			setStatus(count);
		}
		$(function(){//点击非导出按钮时，关闭导出选择按钮
			$("body").bind("mousedown", 
			function(event){
				if (!(event.target.id == (grid.getGridId() + 'mm') || $(event.target).parents("#" + grid.getGridId() + 'mm').length > 0)) {
					$("#"+grid.getGridId() + 'mm').hide();
				}
			});
		});
		$.extend(this, {
            "setStatus" : setStatus,
            "setPagerUrl" : setPagerUrl,
            "clearDirty" : clearDirty
        });
        init();
        return self;
    }
	
    // Slick.Controls.Pager
    $.extend(true, window, { Slick: { Controls: { Pager: SlickGridPager }}});
}));

/**
 * 
 * 修改原Radio模式为radiom
 * @author LinSen
 */
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
            "RadioSelectColumn":   RadioSelectColumn
        }
    });


    function RadioSelectColumn(options) {
        var _grid;
        var _self = this;
        var _selectedRowsLookup = {};
        var _defaults = {
            columnId: "_radio_selector",
            cssClass: null,
            toolTip: "Select/Deselect Only One",
            width: 30
        };

        var _options = $.extend(true,{},_defaults,options);

        function init(grid) {
            _grid = grid;
            _grid.onSelectedRowsChanged.subscribe(handleSelectedRowsChanged);
            _grid.onClick.subscribe(handleClick);
        }

        function destroy() {
            _grid.onSelectedRowsChanged.unsubscribe(handleSelectedRowsChanged);
            _grid.onClick.unsubscribe(handleClick);
        }
		//当选择的行改变时，重新渲染
        function handleSelectedRowsChanged(e, args) {
            var selectedRows = _grid.getSelectedRows();
            var lookup = {}, row, i;
            for (i = 0; i < selectedRows.length; i++) {
                row = selectedRows[i];
                lookup[row] = true;
                if (lookup[row] !== _selectedRowsLookup[row]) {
                    _grid.invalidateRow(row);
                    delete _selectedRowsLookup[row];
                }
            }
            for (i in _selectedRowsLookup) {
                _grid.invalidateRow(i);
            }
            _selectedRowsLookup = lookup;
            _grid.render();

        }
		/**
		 * 点击事件判断是不是Radio
		 */
        function handleClick(e, args) {
           if (_grid.getColumns()[args.cell].id === _options.columnId && $(e.target).is(":radio")) {
                // if editing, try to commit
                if (_grid.getEditorLock().isActive() && !_grid.getEditorLock().commitCurrentEdit()) {
                    e.preventDefault();
                    e.stopImmediatePropagation();
                    return;
                }

                if (_selectedRowsLookup[args.row]) {
                    _grid.setSelectedRows($.grep(_grid.getSelectedRows(),function(n) { return n != args.row }));
                }
                else {
                	var radioRow = [];
                	radioRow[0] = args.row;
                    _grid.setSelectedRows(radioRow);
                }
                e.stopPropagation();
                e.stopImmediatePropagation();
            }
        }

        function getColumnDefinition() {
            return {
                id: _options.columnId,
                toolTip: _options.toolTip,
                field: "sel",
                width: _options.width,
                resizable: false,
                sortable: false,
                cssClass: _options.cssClass,
                formatter: RadioSelectionFormatter
            };
        }

        function RadioSelectionFormatter(row, cell, value, columnDef, dataContext) {
            if (dataContext) {
                return _selectedRowsLookup[row]
                        ? "<input type='radio' checked='checked'>"
                        : "<input type='radio'>";
            }
            return null;
        }

        $.extend(this, {
            "init":                         init,
            "destroy":                      destroy,

            "getColumnDefinition":          getColumnDefinition
        });
    }
}));
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
            "RowSelectionModel":    RowSelectionModel
        }
    });

    function RowSelectionModel(options) {
        var _grid;
        var _ranges = [];
        var _self = this;
        var _options;
        var _defaults = {
            selectActiveRow: true
        };

        function init(grid) {
            _options = $.extend(true, {}, _defaults, options);
            _grid = grid;
            _grid.onActiveCellChanged.subscribe(handleActiveCellChange);
            _grid.onKeyDown.subscribe(handleKeyDown);
            _grid.onClick.subscribe(handleClick);
        }

        function destroy() {
            _grid.onActiveCellChanged.unsubscribe(handleActiveCellChange);
            _grid.onKeyDown.unsubscribe(handleKeyDown);
            _grid.onClick.unsubscribe(handleClick);
        }

        function rangesToRows(ranges) {
            var rows = [];
            for (var i = 0; i < ranges.length; i++) {
                for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
                    rows.push(j);
                }
            }
            return rows;
        }

        function rowsToRanges(rows) {
            var ranges = [];
            var lastCell = _grid.getColumns().length - 1;
            for (var i = 0; i < rows.length; i++) {
                ranges.push(new Slick.Range(rows[i], 0, rows[i], lastCell));
            }
            return ranges;
        }

        function getRowsRange(from,to) {
            var i, rows = [];
            for (i = from; i <= to; i++) {
                rows.push(i);
            }
            for (i = to; i < from; i++) {
                rows.push(i);
            }
            return rows;
        }

        function getSelectedRows() {
            return rangesToRows(_ranges);
        }

        function setSelectedRows(rows) {
           setSelectedRanges(rowsToRanges(rows));
        }

        function setSelectedRanges(ranges) {
            _ranges = ranges;
            _self.onSelectedRangesChanged.notify(_ranges);
        }

        function getSelectedRanges() {
            return _ranges;
        }

        function handleActiveCellChange(e, data) {
            if (_options.selectActiveRow && (data.grid.getOptions().selectType === "checkbox") == false) {
                setSelectedRanges([new Slick.Range(data.row, 0, data.row, _grid.getColumns().length - 1)]);
            }
        }

        function handleKeyDown(e) {
            var activeRow = _grid.getActiveCell();
            if (activeRow && e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey && (e.which == 38 || e.which == 40)) {
                var selectedRows = getSelectedRows();
                selectedRows.sort(function(x,y) { return x-y });

                if (!selectedRows.length) {
                    selectedRows = [activeRow.row];
                }

                var top = selectedRows[0];
                var bottom = selectedRows[selectedRows.length - 1];
                var active;

                if (e.which == 40) {
                    active = activeRow.row < bottom || top == bottom ? ++bottom : ++top;
                }
                else {
                    active = activeRow.row < bottom ? --bottom : --top;
                }

                if (active >= 0 && active < _grid.getDataLength()) {
                    _grid.scrollRowIntoView(active);
                    _ranges = rowsToRanges(getRowsRange(top,bottom));
                    setSelectedRanges(_ranges);
                }

                e.preventDefault();
                e.stopPropagation();
            }
        }

        function handleClick(e) {
            var cell = _grid.getCellFromEvent(e);
            if (!cell || !_grid.canCellBeActive(cell.row, cell.cell)) {
                return false;
            }

            var selection = rangesToRows(_ranges);
            var idx = $.inArray(cell.row, selection);

            if (!e.ctrlKey && !e.shiftKey && !e.metaKey) {
                return true;
            } else if ((_grid.getOptions().selectType === "radio") == true) {
            	return true;
            }
            else if (_grid.getOptions().multiSelect) {
                if (idx === -1 && (e.ctrlKey || e.metaKey)) {
                    selection.push(cell.row);
                    _grid.setActiveCell(cell.row, cell.cell);
                }
                else if (idx !== -1 && (e.ctrlKey || e.metaKey)) {
                    selection = $.grep(selection, function(o, i) { return (o !== cell.row); });
                    _grid.setActiveCell(cell.row, cell.cell);
                }
                else if (selection.length && e.shiftKey) {
                    var last = selection.pop();
                    var from = Math.min(cell.row, last);
                    var to = Math.max(cell.row, last);
                    selection = [];
                    for (var i = from; i <= to; i++) {
                        if (i !== last) {
                            selection.push(i);
                        }
                    }
                    selection.push(last);
                    _grid.setActiveCell(cell.row, cell.cell);
                }
            }

            _ranges = rowsToRanges(selection);
            setSelectedRanges(_ranges);
            e.stopImmediatePropagation();

            return true;
        }

        $.extend(this, {
            "getSelectedRows":              getSelectedRows,
            "setSelectedRows":              setSelectedRows,

            "getSelectedRanges":            getSelectedRanges,
            "setSelectedRanges":            setSelectedRanges,

            "init":                         init,
            "destroy":                      destroy,

            "onSelectedRangesChanged":      new Slick.Event()
        });
    }
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	/**
	 * initialize the target menu, the function can be invoked only once
	 */
	function init(target){
		$(target).appendTo('body');
		$(target).addClass('menu-top');	// the top menu
		
		var menus = [];
		adjust($(target));
		
		for(var i=0; i<menus.length; i++){
			var menu = menus[i];
			wrapMenu(menu);
			menu.find('>div.menu-item').each(function(){
				bindMenuItemEvent($(this));
			});
			
			menu.find('div.menu-item').click(function(){
				// only the sub menu clicked can hide all menus
				if (!this.submenu){
					hideAll(target);
				}
				return false;
			});
		}
		
		
		function adjust(menu){
			menus.push(menu);
			menu.find('>div').each(function(){
				var item = $(this);
				var submenu = item.find('>div');
				if (submenu.length){
					submenu.insertAfter(target);
					item[0].submenu = submenu;
					adjust(submenu);
				}
			});
		}
		
		/**
		 * bind menu item event
		 */
		function bindMenuItemEvent(item){
			item.hover(
				function(){
					// hide other menu
					item.siblings().each(function(){
						if (this.submenu){
							hideMenu(this.submenu);
						}
						$(this).removeClass('menu-active');
					});
					
					// show this menu
					item.addClass('menu-active');
					var submenu = item[0].submenu;
					if (submenu){
						var left = item.offset().left + item.outerWidth() - 2;
						if (left + submenu.outerWidth() > $(window).width()){
							left = item.offset().left - submenu.outerWidth() + 2;
						}
						showMenu(submenu, {
							left: left,
							top:item.offset().top - 3
						});
					}
				},
				function(e){
					item.removeClass('menu-active');
					var submenu = item[0].submenu;
					if (submenu){
						if (e.pageX>=parseInt(submenu.css('left'))){
							item.addClass('menu-active');
						} else {
							hideMenu(submenu);
						}
						
					} else {
						item.removeClass('menu-active');
					}
					
				}
			);
		}
		
		/**
		 * wrap a menu and set it's status to hidden
		 * the menu not include sub menus
		 */
		function wrapMenu(menu){
			menu.addClass('menu').find('>div').each(function(){
				var item = $(this);
				if (item.hasClass('menu-sep')){
					item.html('&nbsp;');
				} else {
					var text = item.addClass('menu-item').html();
					item.empty().append($('<div class="menu-text"></div>').html(text));
					var icon = item.attr('icon');
					if (icon){
						$('<div class="menu-icon"></div>').addClass(icon).appendTo(item);
					}
					if (item[0].submenu){
						$('<div class="menu-rightarrow"></div>').appendTo(item);	// has sub menu
					}
					
					if ($.boxModel == true){
						var height = item.height();
						item.height(height - (item.outerHeight() - item.height()));
					}
				}
			});
			menu.hide();
		}
	}
	
	
	
	function onDocClick(e){
		var target = e.data;
		hideAll(target);
		return false;
	}
	
	/**
	 * hide top menu and it's all sub menus
	 */
	function hideAll(target){
		var opts = $.data(target, 'menu').options;
		hideMenu($(target));
		$(document).unbind('.menu');
		opts.onHide.call(target);
		
//		var state = $.data(target, 'menu');
//		if (state){
//			hideMenu($(target));
//			$(document).unbind('.menu');
//			state.options.onHide.call(target);
//		}
		return false;
	}
	
	/**
	 * show the top menu
	 */
	function showTopMenu(target, pos){
		var opts = $.data(target, 'menu').options;
		if (pos){
			opts.left = pos.left;
			opts.top = pos.top;
		}
		showMenu($(target), {left:opts.left,top:opts.top}, function(){
			$(document).bind('click.menu', target, onDocClick);
			opts.onShow.call(target);
		});
	}
	
	function showMenu(menu, pos, callback){
		if (!menu) return;
		
		if (pos){
			menu.css(pos);
		}
		menu.show(1, function(){
			if (!menu[0].shadow){
				menu[0].shadow = $('<div class="menu-shadow"></div>').insertAfter(menu);
			}
			menu[0].shadow.css({
				display:'block',
				zIndex:$.fn.menu.defaults.zIndex++,
				left:menu.css('left'),
				top:menu.css('top'),
				width:menu.outerWidth(),
				height:menu.outerHeight()
			});
			menu.css('z-index', $.fn.menu.defaults.zIndex++);
			
			if (callback){
				callback();
			}
		});
	}
	
	function hideMenu(menu){
		if (!menu) return;
		
		hideit(menu);
		menu.find('div.menu-item').each(function(){
			if (this.submenu){
				hideMenu(this.submenu);
			}
			$(this).removeClass('menu-active');
		});
		
		function hideit(m){
			if (m[0].shadow){
				m[0].shadow.hide();
			}
			m.hide();
			
		}
	}
	
	$.fn.menu = function(options, param){
		if (typeof options == 'string'){
			switch(options){
				case 'show':
					return this.each(function(){
						showTopMenu(this, param);
					});
				case 'hide':
					return this.each(function(){
						hideAll(this);
					});
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'menu');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'menu', {
					options: $.extend({}, $.fn.menu.defaults, options)
				});
				init(this);
			}
			$(this).css({
				left: state.options.left,
				top: state.options.top
			});
		});
	};
	
	$.fn.menu.defaults = {
		zIndex:110000,
		left: 0,
		top: 0,
		onShow: function(){},
		onHide: function(){}
	};
}));

/**
 * 银海 组建开发规范
 * @author lins
 */
//var a = [{menuType:<div class="menu-sep"></div>}, {
//    id:'xxxx', 
//    name : 'xxxx',
//	href:'xxx',
//	icon:'',
//	onClick:function(){} || "functionName", 
//	menuType:"a||checkbox||radio||", 
//	cssStyle:"", 
//	cssClass : "",
//	children : [{
//		id:'xxxx', 
//		name : 'xxxx',
//		icon:'',
//		href:'xxx', 
//		onClick:function(){} || "functionName", 
//		menuType:"a||checkbox||radio||", 
//		cssStyle:"", 
//		cssClass : "",
//		children :[{}]
//	}]
//}]; 

(function($) { //匿名闭包
	// 在window中注册 组建
    $.extend(true, window, {
        YHUI: { //外层名称
            Menu: Menu //内层名称:构造函数
        }
    }); //既是 new YH.UI(...);构造

    /**
     * 构造函数
     * id：dom标签的id
     * options ：参数
     * args ：额外参数，可以为多个
     */
    function Menu(id, data, url, options, dataOptions) {
        // 默认参数,如有必要可定义多个默认参数/////
        var defaults = {
        	width : "140px",
        	dataType : "children"
        };
        
        var dataDefaults = {
        	id : "id",
        	pid : "pid",
			name : 'name',
			href : 'name',
			icon : 'icon',
			text : "text",
			onClick: "onClick" , 
			menuType:"menuType", 
			cssStyle : "cssStyle",
			cssClass : "cssClass",
			children : "children",
			px : "px"
        };
        ////////////////
        //组建全局变量
        var $g_container;
        var $g_menu;
        var _$menuContainers = [];
        //////////////////////////////////////////////////////////////////////////////////////////////
        //初始化方法
        function init() {
            $g_container = $("#" + id); 
            options = $.extend({},defaults,options); //默认option和自订opetion结合
            dataDefaults = $.extend({},dataDefaults,dataOptions); //默认option和自订opetion结合
            
           	setMenuData(data);
            //可调用其他方法
        }// end init
        
        function getRootNode(data) {
        	var min = 999999999999;
        	var num = 0;
        	for (var i = 0 ; i < data.length; i ++ ) {
        		if (min >data[i]) {
        			min = data[i][dataDefaults.id];
        			num = i;
        		}
        	}
        	return data[num];
        }
        
        function convertDataFromIdPid(node) {
        	for (var i = 0 ; i < data.length; i ++) {
	        	if (node[dataDefaults.id] == data[i][dataDefaults.pid]) {
					if (node[dataDefaults.children] == undefined) {
						node[dataDefaults.children] = [];
					}
					node[dataDefaults.children].push(data[i]);
					convertDataFromIdPid(data[i]);
    	    	}
        	}
        }
        
       /**
        * 公共方法1
        * {
			id:'xxxx', 
			name : 'xxxx',
			icon:'',
			href:'xxx', 
			onClick:function(){} || "functionName", 
			menuType:"a||checkbox||radio||", 
			cssStyle:"", 
			cssClass : "",
			children :[{}]
		}
        */
        function createMenuDiv(datas, div) {
        	_$menuContainers.push(div);
        	//if (typeof datas != "array") return;
        	for (var i = 0; i < datas.length; i ++) {
        		var data = datas[i];
        		if (data[dataDefaults.menuType] == "menu-sep") {
        			$('<div class="menu-sep">').appendTo(div);
        		} else {
	        		var divStr = "<div ";
	        		if (data[dataDefaults.id])
	        			divStr += ' id="' + data[dataDefaults.id] + '"';
	        		if (data[dataDefaults.onClick] )
	        			divStr += ' onclick="' + data[dataDefaults.onClick] + '"';
	        		if (data[dataDefaults.icon])
	        			divStr += ' icon="' + data[dataDefaults.icon] + '"';
	        		divStr += ' style="width:140px"/>';
	        		var $divParent = $(divStr).appendTo(div);
	        		if (data[dataDefaults.children] && data[dataDefaults.children].length  > 0) {
	        			data[dataDefaults.children].sort(function(a,b){return a[dataDefaults.px] - b[dataDefaults.px];});
	        			if (data[dataDefaults.text])
	        				$divParent.html("<span>" + data[dataDefaults.text] + "</span>");
	        			var childDivStr = "<div ";
	        			if (data[dataDefaults.cssStyle])
	        				childDivStr += ' cssStyle="' + data[dataDefaults.cssStyle] + '"';
	        			if (data[dataDefaults.cssClass])
	        				childDivStr += ' cssClass="' + data[dataDefaults.cssClass] + '"';
	        			childDivStr += "/>";
	        			var $childDiv  = $(childDivStr).appendTo($divParent);
	        			createMenuDiv(data[dataDefaults.children], $childDiv);
	        		} else {
	        			if (data[dataDefaults.menuType] == "checkbox" || data[dataDefaults.menuType] == "radio") {
							var chra = "<span>" + "<input onClick='event.stopPropagation();' type='" + data[dataDefaults.menuType] +"' name='" + data[dataDefaults.name] + "'/>" + (data[dataDefaults.text]?data[dataDefaults.text]:"") + "</span>";
	        				$divParent.append(chra);
	        			}
	        			else if (data[dataDefaults.href] && data[dataDefaults.text] != undefined)
	        				$divParent.html('<a href="' + data[dataDefaults.href] + '">' + data[dataDefaults.text] + "</a>");
	        			else if (data[dataDefaults.text] != undefined)
	        				$divParent.html(data[dataDefaults.text]);
	        			if (data[dataDefaults.cssStyle])
	        				$divParent.css(data[dataDefaults.cssStyle]);
	        			if (data[dataDefaults.cssClass])
	        				$divParent.addClass(data[dataDefaults.cssClass]);
	        		}
	        	}
        	}
        }
        
        /**
         * 从url读取数据
         */
        function loadDataFromUrl(url, submitIds, param) {
        	if (url == undefined) {
        		return;
        	} else {
        		var data = Base.getJson(submitIds, url, param);
        		setMenuData(data);
        	}
        }
        
        /**
         * 设置菜单数据
         */
        function setMenuData(data) {
        	for (var i = 0; i < _$menuContainers.length; i ++ ) {
        		_$menuContainers[i].remove();
        	}
        	var menuData = [];
        	if (options.dataType == "tree") {
        		 var root = getRootNode(data);
        		 convertDataFromIdPid(root);
        		 menuData.push(root);
        	} else {
        		menuData = data;
        	}
        	$g_menu = $('<div id="'+ id + '_menu">').css("width", options.width).appendTo("body");
        	createMenuDiv(menuData, $g_menu);
            $g_menu.menu();
        	$g_container.click(function(e){
				$($g_menu).menu('show',{
							left: e.pageX,
							top: e.pageY
				});
			});
        }
        /**
         * 
         */
        function extendFn(functionName , functionBody) {
        	this[functionName] = functionBody;
        }
        //////////////////////////////////////////////////////////////////////////////////////////////
        // 注册 Public API
        $.extend(this, { //为this对象
        	"extendFn": extendFn,
        	"cmptype":'menu', //组建类型
            "UIVersion": "1.0", //组建版本
            "setMenuData" : setMenuData,
            "loadDataFromUrl" :loadDataFromUrl
        });
        init();//调用初始化方法
    }
}(jQuery)); // 闭包完


(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){ 
	var COMPLEXIFY_BANLIST = '123456|password|12345678|1234|pussy|12345|dragon|qwerty|696969|mustang|letmein|baseball|master|michael|football|shadow|monkey|abc123|pass|fuckme|6969|jordan|harley|ranger|iwantu|jennifer|hunter|fuck|2000|test|batman|trustno1|thomas|tigger|robert|access|love|buster|1234567|soccer|hockey|killer|george|sexy|andrew|charlie|superman|asshole|fuckyou|dallas|jessica|panties|pepper|1111|austin|william|daniel|golfer|summer|heather|hammer|yankees|joshua|maggie|biteme|enter|ashley|thunder|cowboy|silver|richard|fucker|orange|merlin|michelle|corvette|bigdog|cheese|matthew|121212|patrick|martin|freedom|ginger|blowjob|nicole|sparky|yellow|camaro|secret|dick|falcon|taylor|111111|131313|123123|bitch|hello|scooter|please|porsche|guitar|chelsea|black|diamond|nascar|jackson|cameron|654321|computer|amanda|wizard|xxxxxxxx|money|phoenix|mickey|bailey|knight|iceman|tigers|purple|andrea|horny|dakota|aaaaaa|player|sunshine|morgan|starwars|boomer|cowboys|edward|charles|girls|booboo|coffee|xxxxxx|bulldog|ncc1701|rabbit|peanut|john|johnny|gandalf|spanky|winter|brandy|compaq|carlos|tennis|james|mike|brandon|fender|anthony|blowme|ferrari|cookie|chicken|maverick|chicago|joseph|diablo|sexsex|hardcore|666666|willie|welcome|chris|panther|yamaha|justin|banana|driver|marine|angels|fishing|david|maddog|hooters|wilson|butthead|dennis|fucking|captain|bigdick|chester|smokey|xavier|steven|viking|snoopy|blue|eagles|winner|samantha|house|miller|flower|jack|firebird|butter|united|turtle|steelers|tiffany|zxcvbn|tomcat|golf|bond007|bear|tiger|doctor|gateway|gators|angel|junior|thx1138|porno|badboy|debbie|spider|melissa|booger|1212|flyers|fish|porn|matrix|teens|scooby|jason|walter|cumshot|boston|braves|yankee|lover|barney|victor|tucker|princess|mercedes|5150|doggie|zzzzzz|gunner|horney|bubba|2112|fred|johnson|xxxxx|tits|member|boobs|donald|bigdaddy|bronco|penis|voyager|rangers|birdie|trouble|white|topgun|bigtits|bitches|green|super|qazwsx|magic|lakers|rachel|slayer|scott|2222|asdf|video|london|7777|marlboro|srinivas|internet|action|carter|jasper|monster|teresa|jeremy|11111111|bill|crystal|peter|pussies|cock|beer|rocket|theman|oliver|prince|beach|amateur|7777777|muffin|redsox|star|testing|shannon|murphy|frank|hannah|dave|eagle1|11111|mother|nathan|raiders|steve|forever|angela|viper|ou812|jake|lovers|suckit|gregory|buddy|whatever|young|nicholas|lucky|helpme|jackie|monica|midnight|college|baby|cunt|brian|mark|startrek|sierra|leather|232323|4444|beavis|bigcock|happy|sophie|ladies|naughty|giants|booty|blonde|fucked|golden|0|fire|sandra|pookie|packers|einstein|dolphins|chevy|winston|warrior|sammy|slut|8675309|zxcvbnm|nipples|power|victoria|asdfgh|vagina|toyota|travis|hotdog|paris|rock|xxxx|extreme|redskins|erotic|dirty|ford|freddy|arsenal|access14|wolf|nipple|iloveyou|alex|florida|eric|legend|movie|success|rosebud|jaguar|great|cool|cooper|1313|scorpio|mountain|madison|987654|brazil|lauren|japan|naked|squirt|stars|apple|alexis|aaaa|bonnie|peaches|jasmine|kevin|matt|qwertyui|danielle|beaver|4321|4128|runner|swimming|dolphin|gordon|casper|stupid|shit|saturn|gemini|apples|august|3333|canada|blazer|cumming|hunting|kitty|rainbow|112233|arthur|cream|calvin|shaved|surfer|samson|kelly|paul|mine|king|racing|5555|eagle|hentai|newyork|little|redwings|smith|sticky|cocacola|animal|broncos|private|skippy|marvin|blondes|enjoy|girl|apollo|parker|qwert|time|sydney|women|voodoo|magnum|juice|abgrtyu|777777|dreams|maxwell|music|rush2112|russia|scorpion|rebecca|tester|mistress|phantom|billy|6666|albert|111111|11111111|112233|121212|123123|123456|1234567|12345678|131313|232323|654321|666666|696969|777777|7777777|8675309|987654|abcdef|password1|password12|password123|twitter'.split('|');
	$.fn.extend({
		complexify: function(options, callback) {

			var MIN_COMPLEXITY = 49; // 12 chars with Upper, Lower and Number
			var MAX_COMPLEXITY = 100; //  25 chars, all charsets
			var CHARSETS = [
				// Commonly Used
				////////////////////
				[0x0030, 0x0039], // Numbers
				[0x0041, 0x005A], // Uppercase
				[0x0061, 0x007A], // Lowercase
				[0x0021, 0x002F], // Punctuation
				[0x003A, 0x0040], // Punctuation
				[0x005B, 0x0060], // Punctuation
				[0x007B, 0x007E], // Punctuation
				// Everything Else
				////////////////////
				[0x0080, 0x00FF], // Latin-1 Supplement
				[0x0100, 0x017F], // Latin Extended-A
				[0x0180, 0x024F], // Latin Extended-B
				[0x0250, 0x02AF], // IPA Extensions
				[0x02B0, 0x02FF], // Spacing Modifier Letters
				[0x0300, 0x036F], // Combining Diacritical Marks
				[0x0370, 0x03FF], // Greek
				[0x0400, 0x04FF], // Cyrillic
				[0x0530, 0x058F], // Armenian
				[0x0590, 0x05FF], // Hebrew
				[0x0600, 0x06FF], // Arabic
				[0x0700, 0x074F], // Syriac
				[0x0780, 0x07BF], // Thaana
				[0x0900, 0x097F], // Devanagari
				[0x0980, 0x09FF], // Bengali
				[0x0A00, 0x0A7F], // Gurmukhi
				[0x0A80, 0x0AFF], // Gujarati
				[0x0B00, 0x0B7F], // Oriya
				[0x0B80, 0x0BFF], // Tamil
				[0x0C00, 0x0C7F], // Telugu
				[0x0C80, 0x0CFF], // Kannada
				[0x0D00, 0x0D7F], // Malayalam
				[0x0D80, 0x0DFF], // Sinhala
				[0x0E00, 0x0E7F], // Thai
				[0x0E80, 0x0EFF], // Lao
				[0x0F00, 0x0FFF], // Tibetan
				[0x1000, 0x109F], // Myanmar
				[0x10A0, 0x10FF], // Georgian
				[0x1100, 0x11FF], // Hangul Jamo
				[0x1200, 0x137F], // Ethiopic
				[0x13A0, 0x13FF], // Cherokee
				[0x1400, 0x167F], // Unified Canadian Aboriginal Syllabics
				[0x1680, 0x169F], // Ogham
				[0x16A0, 0x16FF], // Runic
				[0x1780, 0x17FF], // Khmer
				[0x1800, 0x18AF], // Mongolian
				[0x1E00, 0x1EFF], // Latin Extended Additional
				[0x1F00, 0x1FFF], // Greek Extended
				[0x2000, 0x206F], // General Punctuation
				[0x2070, 0x209F], // Superscripts and Subscripts
				[0x20A0, 0x20CF], // Currency Symbols
				[0x20D0, 0x20FF], // Combining Marks for Symbols
				[0x2100, 0x214F], // Letterlike Symbols
				[0x2150, 0x218F], // Number Forms
				[0x2190, 0x21FF], // Arrows
				[0x2200, 0x22FF], // Mathematical Operators
				[0x2300, 0x23FF], // Miscellaneous Technical
				[0x2400, 0x243F], // Control Pictures
				[0x2440, 0x245F], // Optical Character Recognition
				[0x2460, 0x24FF], // Enclosed Alphanumerics
				[0x2500, 0x257F], // Box Drawing
				[0x2580, 0x259F], // Block Elements
				[0x25A0, 0x25FF], // Geometric Shapes
				[0x2600, 0x26FF], // Miscellaneous Symbols
				[0x2700, 0x27BF], // Dingbats
				[0x2800, 0x28FF], // Braille Patterns
				[0x2E80, 0x2EFF], // CJK Radicals Supplement
				[0x2F00, 0x2FDF], // Kangxi Radicals
				[0x2FF0, 0x2FFF], // Ideographic Description Characters
				[0x3000, 0x303F], // CJK Symbols and Punctuation
				[0x3040, 0x309F], // Hiragana
				[0x30A0, 0x30FF], // Katakana
				[0x3100, 0x312F], // Bopomofo
				[0x3130, 0x318F], // Hangul Compatibility Jamo
				[0x3190, 0x319F], // Kanbun
				[0x31A0, 0x31BF], // Bopomofo Extended
				[0x3200, 0x32FF], // Enclosed CJK Letters and Months
				[0x3300, 0x33FF], // CJK Compatibility
				[0x3400, 0x4DB5], // CJK Unified Ideographs Extension A
				[0x4E00, 0x9FFF], // CJK Unified Ideographs
				[0xA000, 0xA48F], // Yi Syllables
				[0xA490, 0xA4CF], // Yi Radicals
				[0xAC00, 0xD7A3], // Hangul Syllables
				[0xD800, 0xDB7F], // High Surrogates
				[0xDB80, 0xDBFF], // High Private Use Surrogates
				[0xDC00, 0xDFFF], // Low Surrogates
				[0xE000, 0xF8FF], // Private Use
				[0xF900, 0xFAFF], // CJK Compatibility Ideographs
				[0xFB00, 0xFB4F], // Alphabetic Presentation Forms
				[0xFB50, 0xFDFF], // Arabic Presentation Forms-A
				[0xFE20, 0xFE2F], // Combining Half Marks
				[0xFE30, 0xFE4F], // CJK Compatibility Forms
				[0xFE50, 0xFE6F], // Small Form Variants
				[0xFE70, 0xFEFE], // Arabic Presentation Forms-B
				[0xFEFF, 0xFEFF], // Specials
				[0xFF00, 0xFFEF], // Halfwidth and Fullwidth Forms
				[0xFFF0, 0xFFFD]  // Specials
			];

			var defaults = {
				minimumChars: 8,
				strengthScaleFactor: 1,
        bannedPasswords: COMPLEXIFY_BANLIST || [],
				banmode: 'strict', // (strict|loose)
        evaluateOnInit: true
			};

			if($.isFunction(options) && !callback) {
				callback = options;
				options = {};
			}

			options = $.extend(defaults, options);

			function additionalComplexityForCharset(str, charset) {
				for (var i = str.length - 1; i >= 0; i--) {
					if (charset[0] <= str.charCodeAt(i) && str.charCodeAt(i) <= charset[1]) {
						return charset[1] - charset[0] + 1;
					}
				}
        return 0;
			}
			
			function inBanlist(str) {
				if (options.banmode === 'strict') {
					for (var i = 0; i < options.bannedPasswords.length; i++) {
            if (options.bannedPasswords[i].indexOf(str) !== -1) {
              return true;
            }
					}
					return false;
				} else {
					return $.inArray(str, options.bannedPasswords) > -1 ? true : false;
				}
			}

      function evaluateSecurity() {
        var password = $(this).val();
        var complexity = 0, valid = false;
        
        // Reset complexity to 0 when banned password is found
        if (!inBanlist(password)) {
        
          // Add character complexity
          for (var i = CHARSETS.length - 1; i >= 0; i--) {
            complexity += additionalComplexityForCharset(password, CHARSETS[i]);
          }
          
        } else {
          complexity = 1;
        }
        
        // Use natural log to produce linear scale
        complexity = Math.log(Math.pow(complexity, password.length)) * (1/options.strengthScaleFactor);

        valid = (complexity > MIN_COMPLEXITY && password.length >= options.minimumChars);

        // Scale to percentage, so it can be used for a progress bar
        complexity = (complexity / MAX_COMPLEXITY) * 100;
        complexity = (complexity > 100) ? 100 : complexity;
        complexity = (password.length > options.minimumChars) ? complexity : 0;
        callback.call(this, valid, complexity);
      }

      if( options.evaluateOnInit ) {
        this.each(function () {
          evaluateSecurity.apply(this);
        });
      }

			return this.each(function () {
        $(this).bind('keyup focus', evaluateSecurity);
			});
			
		}
	});

}));
﻿(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	$.extend(true, window, {
		"UrlEncode": UrlEncode,
		"getSpell": getSpell,
		"getSpellSzm": getSpellSzm
    });

var strGB="啊阿埃挨哎唉哀皑癌蔼矮艾碍爱隘鞍氨安俺按暗岸胺案肮昂盎凹敖熬翱袄傲奥懊澳芭捌扒叭吧笆八疤巴拔跋靶把耙坝霸罢爸白柏百摆佰败拜稗斑班搬扳般颁板版扮拌伴瓣半办绊邦帮梆榜膀绑棒磅蚌镑傍谤苞胞包褒剥薄雹保堡饱宝抱报暴豹鲍爆杯碑悲卑北辈背贝钡倍狈备惫焙被奔苯本笨崩绷甭泵蹦迸逼鼻比鄙笔彼碧蓖蔽毕毙毖币庇痹闭敝弊必辟壁臂避陛鞭边编贬扁便变卞辨辩辫遍标彪膘表鳖憋别瘪彬斌濒滨宾摈兵冰柄丙秉饼炳病并玻菠播拨钵波博勃搏铂箔伯帛舶脖膊渤泊驳捕卜哺补埠不布步簿部怖擦猜裁材才财睬踩采彩菜蔡餐参蚕残惭惨灿苍舱仓沧藏操糙槽曹草厕策侧册测层蹭插叉茬茶查碴搽察岔差诧拆柴豺搀掺蝉馋谗缠铲产阐颤昌猖场尝常长偿肠厂敞畅唱倡超抄钞朝嘲潮巢吵炒车扯撤掣彻澈郴臣辰尘晨忱沉陈趁衬撑称城橙成呈乘程惩澄诚承逞骋秤吃痴持匙池迟弛驰耻齿侈尺赤翅斥炽充冲虫崇宠抽酬畴踌稠愁筹仇绸瞅丑臭初出橱厨躇锄雏滁除楚础储矗搐触处揣川穿椽传船喘串疮窗幢床闯创吹炊捶锤垂春椿醇唇淳纯蠢戳绰疵茨磁雌辞慈瓷词此刺赐次聪葱囱匆从丛凑粗醋簇促蹿篡窜摧崔催脆瘁粹淬翠村存寸磋撮搓措挫错搭达答瘩打大呆歹傣戴带殆代贷袋待逮怠耽担丹单郸掸胆旦氮但惮淡诞弹蛋当挡党荡档刀捣蹈倒岛祷导到稻悼道盗德得的蹬灯登等瞪凳邓堤低滴迪敌笛狄涤翟嫡抵底地蒂第帝弟递缔颠掂滇碘点典靛垫电佃甸店惦奠淀殿碉叼雕凋刁掉吊钓调跌爹碟蝶迭谍叠丁盯叮钉顶鼎锭定订丢东冬董懂动栋侗恫冻洞兜抖斗陡豆逗痘都督毒犊独读堵睹赌杜镀肚度渡妒端短锻段断缎堆兑队对墩吨蹲敦顿囤钝盾遁掇哆多夺垛躲朵跺舵剁惰堕蛾峨鹅俄额讹娥恶厄扼遏鄂饿恩而儿耳尔饵洱二贰发罚筏伐乏阀法珐藩帆番翻樊矾钒繁凡烦反返范贩犯饭泛坊芳方肪房防妨仿访纺放菲非啡飞肥匪诽吠肺废沸费芬酚吩氛分纷坟焚汾粉奋份忿愤粪丰封枫蜂峰锋风疯烽逢冯缝讽奉凤佛否夫敷肤孵扶拂辐幅氟符伏俘服浮涪福袱弗甫抚辅俯釜斧脯腑府腐赴副覆赋复傅付阜父腹负富讣附妇缚咐噶嘎该改概钙盖溉干甘杆柑竿肝赶感秆敢赣冈刚钢缸肛纲岗港杠篙皋高膏羔糕搞镐稿告哥歌搁戈鸽胳疙割革葛格蛤阁隔铬个各给根跟耕更庚羹埂耿梗工攻功恭龚供躬公宫弓巩汞拱贡共钩勾沟苟狗垢构购够辜菇咕箍估沽孤姑鼓古蛊骨谷股故顾固雇刮瓜剐寡挂褂乖拐怪棺关官冠观管馆罐惯灌贯光广逛瑰规圭硅归龟闺轨鬼诡癸桂柜跪贵刽辊滚棍锅郭国果裹过哈骸孩海氦亥害骇酣憨邯韩含涵寒函喊罕翰撼捍旱憾悍焊汗汉夯杭航壕嚎豪毫郝好耗号浩呵喝荷菏核禾和何合盒貉阂河涸赫褐鹤贺嘿黑痕很狠恨哼亨横衡恒轰哄烘虹鸿洪宏弘红喉侯猴吼厚候后呼乎忽瑚壶葫胡蝴狐糊湖弧虎唬护互沪户花哗华猾滑画划化话槐徊怀淮坏欢环桓还缓换患唤痪豢焕涣宦幻荒慌黄磺蝗簧皇凰惶煌晃幌恍谎灰挥辉徽恢蛔回毁悔慧卉惠晦贿秽会烩汇讳诲绘荤昏婚魂浑混豁活伙火获或惑霍货祸击圾基机畸稽积箕肌饥迹激讥鸡姬绩缉吉极棘辑籍集及急疾汲即嫉级挤几脊己蓟技冀季伎祭剂悸济寄寂计记既忌际妓继纪嘉枷夹佳家加荚颊贾甲钾假稼价架驾嫁歼监坚尖笺间煎兼肩艰奸缄茧检柬碱硷拣捡简俭剪减荐槛鉴践贱见键箭件健舰剑饯渐溅涧建僵姜将浆江疆蒋桨奖讲匠酱降蕉椒礁焦胶交郊浇骄娇嚼搅铰矫侥脚狡角饺缴绞剿教酵轿较叫窖揭接皆秸街阶截劫节桔杰捷睫竭洁结解姐戒藉芥界借介疥诫届巾筋斤金今津襟紧锦仅谨进靳晋禁近烬浸尽劲荆兢茎睛晶鲸京惊精粳经井警景颈静境敬镜径痉靖竟竞净炯窘揪究纠玖韭久灸九酒厩救旧臼舅咎就疚鞠拘狙疽居驹菊局咀矩举沮聚拒据巨具距踞锯俱句惧炬剧捐鹃娟倦眷卷绢撅攫抉掘倔爵觉决诀绝均菌钧军君峻俊竣浚郡骏喀咖卡咯开揩楷凯慨刊堪勘坎砍看康慷糠扛抗亢炕考拷烤靠坷苛柯棵磕颗科壳咳可渴克刻客课肯啃垦恳坑吭空恐孔控抠口扣寇枯哭窟苦酷库裤夸垮挎跨胯块筷侩快宽款匡筐狂框矿眶旷况亏盔岿窥葵奎魁傀馈愧溃坤昆捆困括扩廓阔垃拉喇蜡腊辣啦莱来赖蓝婪栏拦篮阑兰澜谰揽览懒缆烂滥琅榔狼廊郎朗浪捞劳牢老佬姥酪烙涝勒乐雷镭蕾磊累儡垒擂肋类泪棱楞冷厘梨犁黎篱狸离漓理李里鲤礼莉荔吏栗丽厉励砾历利傈例俐痢立粒沥隶力璃哩俩联莲连镰廉怜涟帘敛脸链恋炼练粮凉梁粱良两辆量晾亮谅撩聊僚疗燎寥辽潦了撂镣廖料列裂烈劣猎琳林磷霖临邻鳞淋凛赁吝拎玲菱零龄铃伶羚凌灵陵岭领另令溜琉榴硫馏留刘瘤流柳六龙聋咙笼窿隆垄拢陇楼娄搂篓漏陋芦卢颅庐炉掳卤虏鲁麓碌露路赂鹿潞禄录陆戮驴吕铝侣旅履屡缕虑氯律率滤绿峦挛孪滦卵乱掠略抡轮伦仑沦纶论萝螺罗逻锣箩骡裸落洛骆络妈麻玛码蚂马骂嘛吗埋买麦卖迈脉瞒馒蛮满蔓曼慢漫谩芒茫盲氓忙莽猫茅锚毛矛铆卯茂冒帽貌贸么玫枚梅酶霉煤没眉媒镁每美昧寐妹媚门闷们萌蒙檬盟锰猛梦孟眯醚靡糜迷谜弥米秘觅泌蜜密幂棉眠绵冕免勉娩缅面苗描瞄藐秒渺庙妙蔑灭民抿皿敏悯闽明螟鸣铭名命谬摸摹蘑模膜磨摩魔抹末莫墨默沫漠寞陌谋牟某拇牡亩姆母墓暮幕募慕木目睦牧穆拿哪呐钠那娜纳氖乃奶耐奈南男难囊挠脑恼闹淖呢馁内嫩能妮霓倪泥尼拟你匿腻逆溺蔫拈年碾撵捻念娘酿鸟尿捏聂孽啮镊镍涅您柠狞凝宁拧泞牛扭钮纽脓浓农弄奴努怒女暖虐疟挪懦糯诺哦欧鸥殴藕呕偶沤啪趴爬帕怕琶拍排牌徘湃派攀潘盘磐盼畔判叛乓庞旁耪胖抛咆刨炮袍跑泡呸胚培裴赔陪配佩沛喷盆砰抨烹澎彭蓬棚硼篷膨朋鹏捧碰坯砒霹批披劈琵毗啤脾疲皮匹痞僻屁譬篇偏片骗飘漂瓢票撇瞥拼频贫品聘乒坪苹萍平凭瓶评屏坡泼颇婆破魄迫粕剖扑铺仆莆葡菩蒲埔朴圃普浦谱曝瀑期欺栖戚妻七凄漆柒沏其棋奇歧畦崎脐齐旗祈祁骑起岂乞企启契砌器气迄弃汽泣讫掐恰洽牵扦钎铅千迁签仟谦乾黔钱钳前潜遣浅谴堑嵌欠歉枪呛腔羌墙蔷强抢橇锹敲悄桥瞧乔侨巧鞘撬翘峭俏窍切茄且怯窃钦侵亲秦琴勤芹擒禽寝沁青轻氢倾卿清擎晴氰情顷请庆琼穷秋丘邱球求囚酋泅趋区蛆曲躯屈驱渠取娶龋趣去圈颧权醛泉全痊拳犬券劝缺炔瘸却鹊榷确雀裙群然燃冉染瓤壤攘嚷让饶扰绕惹热壬仁人忍韧任认刃妊纫扔仍日戎茸蓉荣融熔溶容绒冗揉柔肉茹蠕儒孺如辱乳汝入褥软阮蕊瑞锐闰润若弱撒洒萨腮鳃塞赛三叁伞散桑嗓丧搔骚扫嫂瑟色涩森僧莎砂杀刹沙纱傻啥煞筛晒珊苫杉山删煽衫闪陕擅赡膳善汕扇缮墒伤商赏晌上尚裳梢捎稍烧芍勺韶少哨邵绍奢赊蛇舌舍赦摄射慑涉社设砷申呻伸身深娠绅神沈审婶甚肾慎渗声生甥牲升绳省盛剩胜圣师失狮施湿诗尸虱十石拾时什食蚀实识史矢使屎驶始式示士世柿事拭誓逝势是嗜噬适仕侍释饰氏市恃室视试收手首守寿授售受瘦兽蔬枢梳殊抒输叔舒淑疏书赎孰熟薯暑曙署蜀黍鼠属术述树束戍竖墅庶数漱恕刷耍摔衰甩帅栓拴霜双爽谁水睡税吮瞬顺舜说硕朔烁斯撕嘶思私司丝死肆寺嗣四伺似饲巳松耸怂颂送宋讼诵搜艘擞嗽苏酥俗素速粟僳塑溯宿诉肃酸蒜算虽隋随绥髓碎岁穗遂隧祟孙损笋蓑梭唆缩琐索锁所塌他它她塔獭挞蹋踏胎苔抬台泰酞太态汰坍摊贪瘫滩坛檀痰潭谭谈坦毯袒碳探叹炭汤塘搪堂棠膛唐糖倘躺淌趟烫掏涛滔绦萄桃逃淘陶讨套特藤腾疼誊梯剔踢锑提题蹄啼体替嚏惕涕剃屉天添填田甜恬舔腆挑条迢眺跳贴铁帖厅听烃汀廷停亭庭挺艇通桐酮瞳同铜彤童桶捅筒统痛偷投头透凸秃突图徒途涂屠土吐兔湍团推颓腿蜕褪退吞屯臀拖托脱鸵陀驮驼椭妥拓唾挖哇蛙洼娃瓦袜歪外豌弯湾玩顽丸烷完碗挽晚皖惋宛婉万腕汪王亡枉网往旺望忘妄威巍微危韦违桅围唯惟为潍维苇萎委伟伪尾纬未蔚味畏胃喂魏位渭谓尉慰卫瘟温蚊文闻纹吻稳紊问嗡翁瓮挝蜗涡窝我斡卧握沃巫呜钨乌污诬屋无芜梧吾吴毋武五捂午舞伍侮坞戊雾晤物勿务悟误昔熙析西硒矽晰嘻吸锡牺稀息希悉膝夕惜熄烯溪汐犀檄袭席习媳喜铣洗系隙戏细瞎虾匣霞辖暇峡侠狭下厦夏吓掀锨先仙鲜纤咸贤衔舷闲涎弦嫌显险现献县腺馅羡宪陷限线相厢镶香箱襄湘乡翔祥详想响享项巷橡像向象萧硝霄削哮嚣销消宵淆晓小孝校肖啸笑效楔些歇蝎鞋协挟携邪斜胁谐写械卸蟹懈泄泻谢屑薪芯锌欣辛新忻心信衅星腥猩惺兴刑型形邢行醒幸杏性姓兄凶胸匈汹雄熊休修羞朽嗅锈秀袖绣墟戌需虚嘘须徐许蓄酗叙旭序畜恤絮婿绪续轩喧宣悬旋玄选癣眩绚靴薛学穴雪血勋熏循旬询寻驯巡殉汛训讯逊迅压押鸦鸭呀丫芽牙蚜崖衙涯雅哑亚讶焉咽阉烟淹盐严研蜒岩延言颜阎炎沿奄掩眼衍演艳堰燕厌砚雁唁彦焰宴谚验殃央鸯秧杨扬佯疡羊洋阳氧仰痒养样漾邀腰妖瑶摇尧遥窑谣姚咬舀药要耀椰噎耶爷野冶也页掖业叶曳腋夜液一壹医揖铱依伊衣颐夷遗移仪胰疑沂宜姨彝椅蚁倚已乙矣以艺抑易邑屹亿役臆逸肄疫亦裔意毅忆义益溢诣议谊译异翼翌绎茵荫因殷音阴姻吟银淫寅饮尹引隐印英樱婴鹰应缨莹萤营荧蝇迎赢盈影颖硬映哟拥佣臃痈庸雍踊蛹咏泳涌永恿勇用幽优悠忧尤由邮铀犹油游酉有友右佑釉诱又幼迂淤于盂榆虞愚舆余俞逾鱼愉渝渔隅予娱雨与屿禹宇语羽玉域芋郁吁遇喻峪御愈欲狱育誉浴寓裕预豫驭鸳渊冤元垣袁原援辕园员圆猿源缘远苑愿怨院曰约越跃钥岳粤月悦阅耘云郧匀陨允运蕴酝晕韵孕匝砸杂栽哉灾宰载再在咱攒暂赞赃脏葬遭糟凿藻枣早澡蚤躁噪造皂灶燥责择则泽贼怎增憎曾赠扎喳渣札轧铡闸眨栅榨咋乍炸诈摘斋宅窄债寨瞻毡詹粘沾盏斩辗崭展蘸栈占战站湛绽樟章彰漳张掌涨杖丈帐账仗胀瘴障招昭找沼赵照罩兆肇召遮折哲蛰辙者锗蔗这浙珍斟真甄砧臻贞针侦枕疹诊震振镇阵蒸挣睁征狰争怔整拯正政帧症郑证芝枝支吱蜘知肢脂汁之织职直植殖执值侄址指止趾只旨纸志挚掷至致置帜峙制智秩稚质炙痔滞治窒中盅忠钟衷终种肿重仲众舟周州洲诌粥轴肘帚咒皱宙昼骤珠株蛛朱猪诸诛逐竹烛煮拄瞩嘱主著柱助蛀贮铸筑住注祝驻抓爪拽专砖转撰赚篆桩庄装妆撞壮状椎锥追赘坠缀谆准捉拙卓桌琢茁酌啄着灼浊兹咨资姿滋淄孜紫仔籽滓子自渍字鬃棕踪宗综总纵邹走奏揍租足卒族祖诅阻组钻纂嘴醉最罪尊遵昨左佐柞做作坐座亍丌兀丐廿卅丕亘丞鬲孬噩丨禺丿匕乇夭爻卮氐囟胤馗毓睾鼗丶亟鼐乜乩亓芈孛啬嘏仄厍厝厣厥厮靥赝匚叵匦匮匾赜卦卣刂刈刎刭刳刿剀剌剞剡剜蒯剽劂劁劐劓冂罔亻仃仉仂仨仡仫仞伛仳伢佤仵伥伧伉伫佞佧攸佚佝佟佗伲伽佶佴侑侉侃侏佾佻侪佼侬侔俦俨俪俅俚俣俜俑俟俸倩偌俳倬倏倮倭俾倜倌倥倨偾偃偕偈偎偬偻傥傧傩傺僖儆僭僬僦僮儇儋仝氽佘佥俎龠汆籴兮巽黉馘冁夔勹匍訇匐凫夙兕亠兖亳衮袤亵脔裒禀嬴蠃羸冫冱冽冼凇冖冢冥讠讦讧讪讴讵讷诂诃诋诏诎诒诓诔诖诘诙诜诟诠诤诨诩诮诰诳诶诹诼诿谀谂谄谇谌谏谑谒谔谕谖谙谛谘谝谟谠谡谥谧谪谫谮谯谲谳谵谶卩卺阝阢阡阱阪阽阼陂陉陔陟陧陬陲陴隈隍隗隰邗邛邝邙邬邡邴邳邶邺邸邰郏郅邾郐郄郇郓郦郢郜郗郛郫郯郾鄄鄢鄞鄣鄱鄯鄹酃酆刍奂劢劬劭劾哿勐勖勰叟燮矍廴凵凼鬯厶弁畚巯坌垩垡塾墼壅壑圩圬圪圳圹圮圯坜圻坂坩垅坫垆坼坻坨坭坶坳垭垤垌垲埏垧垴垓垠埕埘埚埙埒垸埴埯埸埤埝堋堍埽埭堀堞堙塄堠塥塬墁墉墚墀馨鼙懿艹艽艿芏芊芨芄芎芑芗芙芫芸芾芰苈苊苣芘芷芮苋苌苁芩芴芡芪芟苄苎芤苡茉苷苤茏茇苜苴苒苘茌苻苓茑茚茆茔茕苠苕茜荑荛荜茈莒茼茴茱莛荞茯荏荇荃荟荀茗荠茭茺茳荦荥荨茛荩荬荪荭荮莰荸莳莴莠莪莓莜莅荼莶莩荽莸荻莘莞莨莺莼菁萁菥菘堇萘萋菝菽菖萜萸萑萆菔菟萏萃菸菹菪菅菀萦菰菡葜葑葚葙葳蒇蒈葺蒉葸萼葆葩葶蒌蒎萱葭蓁蓍蓐蓦蒽蓓蓊蒿蒺蓠蒡蒹蒴蒗蓥蓣蔌甍蔸蓰蔹蔟蔺蕖蔻蓿蓼蕙蕈蕨蕤蕞蕺瞢蕃蕲蕻薤薨薇薏蕹薮薜薅薹薷薰藓藁藜藿蘧蘅蘩蘖蘼廾弈夼奁耷奕奚奘匏尢尥尬尴扌扪抟抻拊拚拗拮挢拶挹捋捃掭揶捱捺掎掴捭掬掊捩掮掼揲揸揠揿揄揞揎摒揆掾摅摁搋搛搠搌搦搡摞撄摭撖摺撷撸撙撺擀擐擗擤擢攉攥攮弋忒甙弑卟叱叽叩叨叻吒吖吆呋呒呓呔呖呃吡呗呙吣吲咂咔呷呱呤咚咛咄呶呦咝哐咭哂咴哒咧咦哓哔呲咣哕咻咿哌哙哚哜咩咪咤哝哏哞唛哧唠哽唔哳唢唣唏唑唧唪啧喏喵啉啭啁啕唿啐唼唷啖啵啶啷唳唰啜喋嗒喃喱喹喈喁喟啾嗖喑啻嗟喽喾喔喙嗪嗷嗉嘟嗑嗫嗬嗔嗦嗝嗄嗯嗥嗲嗳嗌嗍嗨嗵嗤辔嘞嘈嘌嘁嘤嘣嗾嘀嘧嘭噘嘹噗嘬噍噢噙噜噌噔嚆噤噱噫噻噼嚅嚓嚯囔囗囝囡囵囫囹囿圄圊圉圜帏帙帔帑帱帻帼帷幄幔幛幞幡岌屺岍岐岖岈岘岙岑岚岜岵岢岽岬岫岱岣峁岷峄峒峤峋峥崂崃崧崦崮崤崞崆崛嵘崾崴崽嵬嵛嵯嵝嵫嵋嵊嵩嵴嶂嶙嶝豳嶷巅彳彷徂徇徉後徕徙徜徨徭徵徼衢彡犭犰犴犷犸狃狁狎狍狒狨狯狩狲狴狷猁狳猃狺狻猗猓猡猊猞猝猕猢猹猥猬猸猱獐獍獗獠獬獯獾舛夥飧夤夂饣饧饨饩饪饫饬饴饷饽馀馄馇馊馍馐馑馓馔馕庀庑庋庖庥庠庹庵庾庳赓廒廑廛廨廪膺忄忉忖忏怃忮怄忡忤忾怅怆忪忭忸怙怵怦怛怏怍怩怫怊怿怡恸恹恻恺恂恪恽悖悚悭悝悃悒悌悛惬悻悱惝惘惆惚悴愠愦愕愣惴愀愎愫慊慵憬憔憧憷懔懵忝隳闩闫闱闳闵闶闼闾阃阄阆阈阊阋阌阍阏阒阕阖阗阙阚丬爿戕氵汔汜汊沣沅沐沔沌汨汩汴汶沆沩泐泔沭泷泸泱泗沲泠泖泺泫泮沱泓泯泾洹洧洌浃浈洇洄洙洎洫浍洮洵洚浏浒浔洳涑浯涞涠浞涓涔浜浠浼浣渚淇淅淞渎涿淠渑淦淝淙渖涫渌涮渫湮湎湫溲湟溆湓湔渲渥湄滟溱溘滠漭滢溥溧溽溻溷滗溴滏溏滂溟潢潆潇漤漕滹漯漶潋潴漪漉漩澉澍澌潸潲潼潺濑濉澧澹澶濂濡濮濞濠濯瀚瀣瀛瀹瀵灏灞宀宄宕宓宥宸甯骞搴寤寮褰寰蹇謇辶迓迕迥迮迤迩迦迳迨逅逄逋逦逑逍逖逡逵逶逭逯遄遑遒遐遨遘遢遛暹遴遽邂邈邃邋彐彗彖彘尻咫屐屙孱屣屦羼弪弩弭艴弼鬻屮妁妃妍妩妪妣妗姊妫妞妤姒妲妯姗妾娅娆姝娈姣姘姹娌娉娲娴娑娣娓婀婧婊婕娼婢婵胬媪媛婷婺媾嫫媲嫒嫔媸嫠嫣嫱嫖嫦嫘嫜嬉嬗嬖嬲嬷孀尕尜孚孥孳孑孓孢驵驷驸驺驿驽骀骁骅骈骊骐骒骓骖骘骛骜骝骟骠骢骣骥骧纟纡纣纥纨纩纭纰纾绀绁绂绉绋绌绐绔绗绛绠绡绨绫绮绯绱绲缍绶绺绻绾缁缂缃缇缈缋缌缏缑缒缗缙缜缛缟缡缢缣缤缥缦缧缪缫缬缭缯缰缱缲缳缵幺畿巛甾邕玎玑玮玢玟珏珂珑玷玳珀珉珈珥珙顼琊珩珧珞玺珲琏琪瑛琦琥琨琰琮琬琛琚瑁瑜瑗瑕瑙瑷瑭瑾璜璎璀璁璇璋璞璨璩璐璧瓒璺韪韫韬杌杓杞杈杩枥枇杪杳枘枧杵枨枞枭枋杷杼柰栉柘栊柩枰栌柙枵柚枳柝栀柃枸柢栎柁柽栲栳桠桡桎桢桄桤梃栝桕桦桁桧桀栾桊桉栩梵梏桴桷梓桫棂楮棼椟椠棹椤棰椋椁楗棣椐楱椹楠楂楝榄楫榀榘楸椴槌榇榈槎榉楦楣楹榛榧榻榫榭槔榱槁槊槟榕槠榍槿樯槭樗樘橥槲橄樾檠橐橛樵檎橹樽樨橘橼檑檐檩檗檫猷獒殁殂殇殄殒殓殍殚殛殡殪轫轭轱轲轳轵轶轸轷轹轺轼轾辁辂辄辇辋辍辎辏辘辚軎戋戗戛戟戢戡戥戤戬臧瓯瓴瓿甏甑甓攴旮旯旰昊昙杲昃昕昀炅曷昝昴昱昶昵耆晟晔晁晏晖晡晗晷暄暌暧暝暾曛曜曦曩贲贳贶贻贽赀赅赆赈赉赇赍赕赙觇觊觋觌觎觏觐觑牮犟牝牦牯牾牿犄犋犍犏犒挈挲掰搿擘耄毪毳毽毵毹氅氇氆氍氕氘氙氚氡氩氤氪氲攵敕敫牍牒牖爰虢刖肟肜肓肼朊肽肱肫肭肴肷胧胨胩胪胛胂胄胙胍胗朐胝胫胱胴胭脍脎胲胼朕脒豚脶脞脬脘脲腈腌腓腴腙腚腱腠腩腼腽腭腧塍媵膈膂膑滕膣膪臌朦臊膻臁膦欤欷欹歃歆歙飑飒飓飕飙飚殳彀毂觳斐齑斓於旆旄旃旌旎旒旖炀炜炖炝炻烀炷炫炱烨烊焐焓焖焯焱煳煜煨煅煲煊煸煺熘熳熵熨熠燠燔燧燹爝爨灬焘煦熹戾戽扃扈扉礻祀祆祉祛祜祓祚祢祗祠祯祧祺禅禊禚禧禳忑忐怼恝恚恧恁恙恣悫愆愍慝憩憝懋懑戆肀聿沓泶淼矶矸砀砉砗砘砑斫砭砜砝砹砺砻砟砼砥砬砣砩硎硭硖硗砦硐硇硌硪碛碓碚碇碜碡碣碲碹碥磔磙磉磬磲礅磴礓礤礞礴龛黹黻黼盱眄眍盹眇眈眚眢眙眭眦眵眸睐睑睇睃睚睨睢睥睿瞍睽瞀瞌瞑瞟瞠瞰瞵瞽町畀畎畋畈畛畲畹疃罘罡罟詈罨罴罱罹羁罾盍盥蠲钅钆钇钋钊钌钍钏钐钔钗钕钚钛钜钣钤钫钪钭钬钯钰钲钴钶钷钸钹钺钼钽钿铄铈铉铊铋铌铍铎铐铑铒铕铖铗铙铘铛铞铟铠铢铤铥铧铨铪铩铫铮铯铳铴铵铷铹铼铽铿锃锂锆锇锉锊锍锎锏锒锓锔锕锖锘锛锝锞锟锢锪锫锩锬锱锲锴锶锷锸锼锾锿镂锵镄镅镆镉镌镎镏镒镓镔镖镗镘镙镛镞镟镝镡镢镤镥镦镧镨镩镪镫镬镯镱镲镳锺矧矬雉秕秭秣秫稆嵇稃稂稞稔稹稷穑黏馥穰皈皎皓皙皤瓞瓠甬鸠鸢鸨鸩鸪鸫鸬鸲鸱鸶鸸鸷鸹鸺鸾鹁鹂鹄鹆鹇鹈鹉鹋鹌鹎鹑鹕鹗鹚鹛鹜鹞鹣鹦鹧鹨鹩鹪鹫鹬鹱鹭鹳疒疔疖疠疝疬疣疳疴疸痄疱疰痃痂痖痍痣痨痦痤痫痧瘃痱痼痿瘐瘀瘅瘌瘗瘊瘥瘘瘕瘙瘛瘼瘢瘠癀瘭瘰瘿瘵癃瘾瘳癍癞癔癜癖癫癯翊竦穸穹窀窆窈窕窦窠窬窨窭窳衤衩衲衽衿袂袢裆袷袼裉裢裎裣裥裱褚裼裨裾裰褡褙褓褛褊褴褫褶襁襦襻疋胥皲皴矜耒耔耖耜耠耢耥耦耧耩耨耱耋耵聃聆聍聒聩聱覃顸颀颃颉颌颍颏颔颚颛颞颟颡颢颥颦虍虔虬虮虿虺虼虻蚨蚍蚋蚬蚝蚧蚣蚪蚓蚩蚶蛄蚵蛎蚰蚺蚱蚯蛉蛏蚴蛩蛱蛲蛭蛳蛐蜓蛞蛴蛟蛘蛑蜃蜇蛸蜈蜊蜍蜉蜣蜻蜞蜥蜮蜚蜾蝈蜴蜱蜩蜷蜿螂蜢蝽蝾蝻蝠蝰蝌蝮螋蝓蝣蝼蝤蝙蝥螓螯螨蟒蟆螈螅螭螗螃螫蟥螬螵螳蟋蟓螽蟑蟀蟊蟛蟪蟠蟮蠖蠓蟾蠊蠛蠡蠹蠼缶罂罄罅舐竺竽笈笃笄笕笊笫笏筇笸笪笙笮笱笠笥笤笳笾笞筘筚筅筵筌筝筠筮筻筢筲筱箐箦箧箸箬箝箨箅箪箜箢箫箴篑篁篌篝篚篥篦篪簌篾篼簏簖簋簟簪簦簸籁籀臾舁舂舄臬衄舡舢舣舭舯舨舫舸舻舳舴舾艄艉艋艏艚艟艨衾袅袈裘裟襞羝羟羧羯羰羲籼敉粑粝粜粞粢粲粼粽糁糇糌糍糈糅糗糨艮暨羿翎翕翥翡翦翩翮翳糸絷綦綮繇纛麸麴赳趄趔趑趱赧赭豇豉酊酐酎酏酤酢酡酰酩酯酽酾酲酴酹醌醅醐醍醑醢醣醪醭醮醯醵醴醺豕鹾趸跫踅蹙蹩趵趿趼趺跄跖跗跚跞跎跏跛跆跬跷跸跣跹跻跤踉跽踔踝踟踬踮踣踯踺蹀踹踵踽踱蹉蹁蹂蹑蹒蹊蹰蹶蹼蹯蹴躅躏躔躐躜躞豸貂貊貅貘貔斛觖觞觚觜觥觫觯訾謦靓雩雳雯霆霁霈霏霎霪霭霰霾龀龃龅龆龇龈龉龊龌黾鼋鼍隹隼隽雎雒瞿雠銎銮鋈錾鍪鏊鎏鐾鑫鱿鲂鲅鲆鲇鲈稣鲋鲎鲐鲑鲒鲔鲕鲚鲛鲞鲟鲠鲡鲢鲣鲥鲦鲧鲨鲩鲫鲭鲮鲰鲱鲲鲳鲴鲵鲶鲷鲺鲻鲼鲽鳄鳅鳆鳇鳊鳋鳌鳍鳎鳏鳐鳓鳔鳕鳗鳘鳙鳜鳝鳟鳢靼鞅鞑鞒鞔鞯鞫鞣鞲鞴骱骰骷鹘骶骺骼髁髀髅髂髋髌髑魅魃魇魉魈魍魑飨餍餮饕饔髟髡髦髯髫髻髭髹鬈鬏鬓鬟鬣麽麾縻麂麇麈麋麒鏖麝麟黛黜黝黠黟黢黩黧黥黪黯鼢鼬鼯鼹鼷鼽鼾齄";
var qswhSpell=["a",0,"ai",2,"an",15,"ang",24,"ao",27,"ba",36,"bai",54,"ban",62,"bang",77,"bao",89,"bei",106,"ben",121,"beng",125,"bi",131,"bian",155,"biao",167,"bie",171,"bin",175,"bing",181,"bo",190,"bu",211,"ca",220,"cai",221,"can",232,"cang",239,"cao",244,"ce",249,"ceng",254,"cha",256,"chai",267,"chan",270,"chang",280,"chao",293,"che",302,"chen",308,"cheng",318,"chi",333,"chong",349,"chou",354,"chu",366,"chuai",382,"chuan",383,"chuang",390,"chui",396,"chun",401,"chuo",408,"ci",410,"cong",422,"cou",428,"cu",429,"cuan",433,"cui",436,"cun",444,"cuo",447,"da",453,"dai",459,"dan",471,"dang",486,"dao",491,"de",503,"deng",506,"di",513,"dian",532,"diao",548,"die",557,"ding",564,"diu",573,"dong",574,"dou",584,"du",591,"duan",606,"dui",612,"dun",616,"duo",625,"e",637,"en",650,"er",651,"fa",659,"fan",667,"fang",684,"fei",695,"fen",707,"feng",722,"fo",737,"fou",738,"fu",739,"ga",784,"gai",786,"gan",792,"gang",803,"gao",812,"ge",822,"gei",839,"gen",840,"geng",842,"gong",849,"gou",864,"gu",873,"gua",891,"guai",897,"guan",900,"guang",911,"gui",914,"gun",930,"guo",933,"ha",939,"hai",940,"han",947,"hang",966,"hao",969,"he",978,"hei",996,"hen",998,"heng",1002,"hong",1007,"hou",1016,"hu",1023,"hua",1041,"huai",1050,"huan",1055,"huang",1069,"hui",1083,"hun",1104,"huo",1110,"ji",1120,"jia",1173,"jian",1190,"jiang",1230,"jiao",1243,"jie",1271,"jin",1298,"jing",1318,"jiong",1343,"jiu",1345,"ju",1362,"juan",1387,"jue",1394,"jun",1404,"ka",1415,"kai",1419,"kan",1424,"kang",1430,"kao",1437,"ke",1441,"ken",1456,"keng",1460,"kong",1462,"kou",1466,"ku",1470,"kua",1477,"kuai",1482,"kuan",1486,"kuang",1488,"kui",1496,"kun",1507,"kuo",1511,"la",1515,"lai",1522,"lan",1525,"lang",1540,"lao",1547,"le",1556,"lei",1558,"leng",1569,"li",1572,"lia",1606,"lian",1607,"liang",1621,"liao",1632,"lie",1645,"lin",1650,"ling",1662,"liu",1676,"long",1687,"lou",1696,"lu",1702,"lv",1722,"luan",1736,"lue",1742,"lun",1744,"luo",1751,"ma",1763,"mai",1772,"man",1778,"mang",1787,"mao",1793,"me",1805,"mei",1806,"men",1822,"meng",1825,"mi",1833,"mian",1847,"miao",1856,"mie",1864,"min",1866,"ming",1872,"miu",1878,"mo",1879,"mou",1896,"mu",1899,"na",1914,"nai",1921,"nan",1926,"nang",1929,"nao",1930,"ne",1935,"nei",1936,"nen",1938,"neng",1939,"ni",1940,"nian",1951,"niang",1958,"niao",1960,"nie",1962,"nin",1969,"ning",1970,"niu",1976,"nong",1980,"nu",1984,"nv",1987,"nuan",1988,"nue",1989,"nuo",1991,"o",1995,"ou",1996,"pa",2003,"pai",2009,"pan",2015,"pang",2023,"pao",2028,"pei",2035,"pen",2044,"peng",2046,"pi",2060,"pian",2077,"piao",2081,"pie",2085,"pin",2087,"ping",2092,"po",2101,"pu",2110,"qi",2125,"qia",2161,"qian",2164,"qiang",2186,"qiao",2194,"qie",2209,"qin",2214,"qing",2225,"qiong",2238,"qiu",2240,"qu",2248,"quan",2261,"que",2272,"qun",2280,"ran",2282,"rang",2286,"rao",2291,"re",2294,"ren",2296,"reng",2306,"ri",2308,"rong",2309,"rou",2319,"ru",2322,"ruan",2332,"rui",2334,"run",2337,"ruo",2339,"sa",2341,"sai",2344,"san",2348,"sang",2352,"sao",2355,"se",2359,"sen",2362,"seng",2363,"sha",2364,"shai",2373,"shan",2375,"shang",2391,"shao",2399,"she",2410,"shen",2422,"sheng",2438,"shi",2449,"shou",2496,"shu",2506,"shua",2539,"shuai",2541,"shuan",2545,"shuang",2547,"shui",2550,"shun",2554,"shuo",2558,"si",2562,"song",2578,"sou",2586,"su",2589,"suan",2602,"sui",2605,"sun",2616,"suo",2619,"ta",2627,"tai",2636,"tan",2645,"tang",2663,"tao",2676,"te",2687,"teng",2688,"ti",2692,"tian",2707,"tiao",2715,"tie",2720,"ting",2723,"tong",2733,"tou",2746,"tu",2750,"tuan",2761,"tui",2763,"tun",2769,"tuo",2772,"wa",2783,"wai",2790,"wan",2792,"wang",2809,"wei",2819,"wen",2852,"weng",2862,"wo",2865,"wu",2874,"xi",2903,"xia",2938,"xian",2951,"xiang",2977,"xiao",2997,"xie",3015,"xin",3036,"xing",3046,"xiong",3061,"xiu",3068,"xu",3077,"xuan",3096,"xue",3106,"xun",3112,"ya",3126,"yan",3142,"yang",3175,"yao",3192,"ye",3207,"yi",3222,"yin",3275,"ying",3291,"yo",3309,"yong",3310,"you",3325,"yu",3346,"yuan",3390,"yue",3410,"yun",3420,"za",3432,"zai",3435,"zan",3442,"zang",3446,"zao",3449,"ze",3463,"zei",3467,"zen",3468,"zeng",3469,"zha",3473,"zhai",3487,"zhan",3493,"zhang",3510,"zhao",3525,"zhe",3535,"zhen",3545,"zheng",3561,"zhi",3576,"zhong",3619,"zhou",3630,"zhu",3644,"zhua",3670,"zhuai",3672,"zhuan",3673,"zhuang",3679,"zhui",3686,"zhun",3692,"zhuo",3694,"zi",3705,"zong",3720,"zou",3727,"zu",3731,"zuan",3739,"zui",3741,"zun",3745,"zuo",3747];


function UrlEncode(str){
	var i,c,p,q,ret="",strSpecial="!\"#$%&'()*+,/:;<=>?@[\]^`{|}~%";
	for(i=0;i<str.length;i++){
		if(str.charCodeAt(i)>=0x4e00){
			var p=strGB.indexOf(str.charAt(i));
			if(p>=0){
				q=p%94;
				p=(p-q)/94;
				ret+=("%"+(0xB0+p).toString(16)+"%"+(0xA1+q).toString(16)).toUpperCase();
			}
		}
		else{
			c=str.charAt(i);
			if(c==" ")
				ret+="+";
			else if(strSpecial.indexOf(c)!=-1)
				ret+="%"+str.charCodeAt(i).toString(16);
			else
				ret+=c;
		}
	}
	return ret;
}
function getSpell(str,sp){
	var i,c,t,p,ret="";
	if(sp==null)sp="";
	for(i=0;i<str.length;i++){
		if(str.charCodeAt(i)>=0x4e00){
			p=strGB.indexOf(str.charAt(i));
			if(p>-1&&p<3755){
				for(t=qswhSpell.length-1;t>0;t=t-2)if(qswhSpell[t]<=p)break;
				if(t>0)ret+=qswhSpell[t-1]+sp;
			}
		}else{
			ret+=str.charAt(i);
		}
	}
	return ret.substr(0,ret.length-sp.length);
}
function getSpellSzm(str){
	var i,c,t,p,szm="";
	for(i=0;i<str.length;i++){
		if(str.charCodeAt(i)>=0x4e00){
			p=strGB.indexOf(str.charAt(i));
			if(p>-1&&p<3755){
				for(t=qswhSpell.length-1;t>0;t=t-2)if(qswhSpell[t]<=p)break;
				if(t>0){
					szm += qswhSpell[t-1].substring(0,1);
				}
			}
		}else{
			szm+=str.charAt(i);
		}
	}
	return szm;
}

}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","cookie"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.fn.hintTip = function(options){
		var defaults = {
			replay:false,//是否自动播放
			delayTime:3000,
			show:true,
			cookname:null,
			func: null,
			reID:"xxx",//"hint_tip",
			data:[]
		};
		var opts = $.extend(defaults,options);
		var replay   = opts.replay;
		var delayTime= opts.delayTime;
		var show     = opts.show;
		var cookname = opts.cookname;
		var func     = opts.func;
		var data     = opts.data;
		var reID     = opts.reID;
		
		var screenHeight = $(window).height();
		var bodyHeight   = $(document).height();
		//var s_b = screenHeight/bodyHeight;
		var bodyWidth  = $("body").width();
		var nowbody    = $("body"),url = window.location.href; 
		var time1;
		var time2;
		var hintName   = encodeURI("hint_"+cookname); // encodeURI("hint_"+url);    //"hint_"+url.replace(/\//g, "").replace(/:/g, "");
		var hintValue  = encodeURI("hint_"+cookname); // encodeURI("hint_"+url);    //"hint_"+url.replace(/\//g, "").replace(/:/g, "");
		//初始化
		init();
		//初始化	
		function init(){
			if(show){
				if(!checkHintCookie()){
				    nowbody.append("<div class='hint-mask'" 
				                  +"style='width:"+bodyWidth+"px;height:"+bodyHeight+"px;'></div>");
				    //遍历提示步骤
					stepTip();
				}
			}
			
			$(".hint-tips .hint-contents .hc-message .hm-opts .ho-next").bind("click",function(){
				 
				 	var nowhint = $(this).parent().parent().parent().parent();
				 	var step = parseInt(nowhint.attr("step"));
				 	//当前div如果是最后一个，则不在执行任何下一步操作（隐藏当前窗口 和 寻找下一窗口并且打开）
				 	if(step == data.length-1){
				 	    return;
				 	}
//					nowhint.remove();
				 	nowhint.hide();
					
					var nexthint = $(".hint-tips[step='"+(step+1)+"']");
					nexthint.show();
					var scrollTop = nexthint.offset().top;
					$("body,html").animate({
						scrollTop:scrollTop-200
					});				
			 });
			 
			 //注册点击上一步事件
			 $(".hint-tips .hint-contents .hc-message .hm-opts .ho-pre").bind("click",function(){
			     var nowhint = $(this).parent().parent().parent().parent();
			     var step = parseInt(nowhint.attr("step"));
//			     nowhint.remove();
			     nowhint.hide();
			     
			     var prehint = $(".hint-tips[step='"+(step-1)+"']");
			     prehint.show();
			     var scrollTop = prehint.offset().top;
			     $("body,html").animate({
				 scrollTop:scrollTop-200
			     });
			     
			 });
			 
			 //注册点击关闭向导事件
			 $(".hint-tips .hint-contents .hc-close").bind("click",function(){
				 
			 	 $(".hint-tips").remove();
				 $(".hint-mask").remove();
				 if(!checkHintCookie()){
					 writeCookie(hintName,hintValue);// 暂时关闭
				 }
				 clearTimeout(time1);
				 clearTimeout(time2);
//				 self = null;
			 });
			 
			 //最后一步关闭向导
			 $(".hint-tips .hint-contents .hc-message .hm-opts .ho-last").bind("click",function(){
			 	 $(".hint-tips").remove();
				 $(".hint-mask").remove();
				 if(!checkHintCookie()){
					 writeCookie(hintName,hintValue);
				 }
				 clearTimeout(time1);
				 clearTimeout(time2);
			 });
			 
			 //自动播放
			 if(replay){
				 time1 = setTimeout(autoPlay,delayTime) ;
			 }
			 
			 /**
			  * 重新引导
			  */
			 $("#"+reID).bind("click",function(){
			     
				 var hintCookieArray = new Array();
				 var data = {};
				 hintCookieArray = _getHintCookies("hint_");
				 for(var i = 0;i<hintCookieArray.length;i++){
					 var name = hintCookieArray[i];
				     	 $.cookie(name,null,{path:'/'});
				 }
			 });
			
		};	
		
		$.clearCookieHintArray    = function(currentBid){
			 var data = {};
			 var cookieArray = document.cookie.split(";");
			 var hintCookies = new Array();
			
			 for (var i = 0; i < cookieArray.length; i++) {
				var cookie = cookieArray[i].split("=");// 将名和值分开
				var name = $.trim(cookie[0]);
				var c_start = name.indexOf("hint_")
				if (c_start != -1) {
					if(!currentBid)
						$.cookie(name,null,{path:'/'});
					if(("hint_"+currentBid) == name)
						$.cookie(name,null,{path:'/'});
				}
			 }

		}
		
		
		
		
		//遍历data
		function stepTip(){
			var len = data.length;
			var isOne = false;
			var i,
			    obj,
				id;
				 
			if(len==0){
				return false;
			}
			for(i=0;i<len;i++){
				ditem = data[i];
				id = ditem.id;
				message = ditem.message;
				var child = ditem.child;
				if(typeof id == "string") {
					obj = $("#"+id);
				}
				if(typeof id == "object") {
					obj = id;
				}
				if(!obj[0]){
					continue;
				}else{
					isOne = true;
				}
				createHint(obj,message,i,len,child);
			}
			if(!isOne){
				$(".hint-tips").remove();
				$(".hint-mask").remove();
			}
		};
		
		 /**
		  * 创建提示的div
		  */
		 function createHint(obj,message,i,len,child){
			var message,
				o_height,
				o_width,
				ditem,
				o_x,
				o_y,
				borderHeight,
				borderWidth,
				arrowHeight,
				arrowWidth;
			o_height = obj.outerHeight();
			o_width  = obj.outerWidth();
			
			o_x = obj.offset().left;
			o_y = obj.offset().top;
			if(child == true) {
				o_x = o_x + 140;
				o_y = o_y + 101;
			}
			arrowHeight = 45;//原设置 65 
			var ht_style = "width:"+(350)+	"px;left:"+o_x+"px;top:"+o_y+	"px;min-width:250px;";
			var hc_style = "margin-top:"+(arrowHeight+o_height)+"px;";
			var hb_style = "width:"+o_width   +"px;height:"+o_height   +"px;top:"+-(arrowHeight+o_height)	+"px;left:-1px;";
			
			var div =$("<div class=\"hint-tips\" style="+ht_style+"  step="+i+">"
					   +"<div class=\"hint-contents\" style="+hc_style+">"
					   +"<div class=\"hc-border\" style="+hb_style+"></div>"
                       +"<div class=\"hc-arrow\"></div>"
                       +"<div class=\"hc-close\"></div>"
    	               +"<div class=\"hc-message\">"
        	           +"<div class=\"hm-content\">"+message+"</div>"
                       +"<div class=\"hm-opts\">"
            	       +"<span class=\"ho-next\">下一步</span>"
            	       +"<span class=\"ho-last\">向导结束，进入网站</span>"
            	       +"<span class=\"ho-pre\">上一步</span>"
                       +"</div>"
                       +"</div>"
                       +"</div>"
                       +"</div>");
			nowbody.append(div);
		   
			//箭头 提示内容的位置
			var div_width = div.outerWidth();
			var div_height = div.outerHeight();
			var content_height =div_height - parseInt(div.children(".hint-contents").css("margin-top"));
			 
			var flag_w = div_width + o_x;
			var flag_h = div_height + o_y;
		
			// 2.
			if(flag_w>=bodyWidth&&flag_h<bodyHeight){
				div.css("left",(o_x+o_width-div_width)+"px");
				div.find(".hc-border").css("left",(div_width-o_width)+"px");
				div.find(".hc-arrow").css({"background-position":"0px 0px","left":(div_width-70)+								                "px"});
			}
			// 3.
			if(flag_w>=bodyWidth&&flag_h>=bodyHeight){
				div.css({"left":(o_x+o_width-div_width)+"px","top":(o_y-div_height-arrowHeight)+ "px"});
				div.find(".hc-border").css({"left":(div_width-o_width)+"px","top":(content_height+arrowHeight)});
				div.find(".hc-arrow" ).css({"background-position":"0px -232px",
				"left":(div_width-70)+"px","top":content_height+"px"});
			}
			//4.
			if(flag_w<bodyWidth&&flag_h>=bodyHeight){
				
				div.css({"top":(o_y-div_height-arrowHeight+200)+"px"});
//				div.css({"left":o_width+ arrowHeight + "px"});
				div.find(".hc-border").css({"top":(content_height+arrowHeight-200)});
				div.find(".hc-arrow").css({"background-position":"0px -65px","top":content_height+"px"});//原设置			
				  
//				div.find(".hc-arrow").css({"left":o_x+o_width+"px","top":o_y + arrowHeight + "px"});				
//				div.find(".hc-message").css({"left":o_x+o_width+"px","top":o_y + arrowHeight + "px"});				
			}
			if(i==0){
				div.show();
				$("body,html").animate({
					scrollTop:o_y-200 //*s_b + 100
				});
			}else{
			    div.find(".hm-opts").children(".ho-pre").show();// 只要不是第一次，就显示“上一步”按钮
			}
			
			if(i==(len-1)){
				div.find(".hm-opts").children(".ho-next").hide();
				div.find(".hm-opts").children(".ho-last").show();
			}
		 };
		
		 //注册点击下一步事件
		
		 function autoPlay(){
			
		     var nowhint = $(".hint-tips:visible");//获取当前可见的那个div，首次执行时可见的就是第一个div
		     var step = parseInt(nowhint.attr("step"));
		     
		     $(".hint-tips[step='"+(step)+"'] .hint-contents .hc-message .hm-opts .ho-next").click();
		     if(step==data.length-1){
		    	 clearTimeout(time1);
		    	 return;
		     }
		     time2 = setTimeout(autoPlay,delayTime) ;
		 }
		 
		//向cookie写入数据
		 function writeCookie(hintName,hintValue){
		     
			 var data= {};
			 data["dto['hintName']"]  = hintName;
			 data["dto['hintValue']"] = hintValue;
			 
			 $.cookie(hintName, hintValue, { expires: 365, path: '/' }); 
 

		 };
		 
		 //检查hintcookie是否存在
		 function checkHintCookie(){
			 var cookieValue = getHintCookie(hintName);
			 if(cookieValue==hintValue){
				 return true;
			 }
			 return false;
		 };
		 
		 
		 /**
		  * 获得所有名称包括c_name的cookie
		  */
		 function _getHintCookies(c_name){
			var cookieArray = document.cookie.split(";");
			var hintCookies = new Array();
			for (var i = 0;i<cookieArray.length;i++){
				var cookie = cookieArray[i].split("=");//将名和值分开
				var name   = $.trim(cookie[0]);
				var c_start = name.indexOf(c_name)
				if(c_start != -1){
					hintCookies.push(name);
				}
			}
			
			return hintCookies;
		}
		 
		function getHintCookie(name){
				var cookieArray = document.cookie.split(";"); //得到分割的cookie名值对    
				var cookie = new Object();   
				for (var i=0;i<cookieArray.length;i++){    
				      var arr=cookieArray[i].split("=");       //将名和值分开    
				      
				      if($.trim(arr[0])==$.trim(name))
				    	  return unescape(arr[1]); //如果是指定的cookie，则返回它的值    
				   } 
				   return "";
		};
	}
	
}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){ 
	$.fn.printForTablePage = function(options){
	//各种属性、参数 
		var defaults = { 
			isVertical:true,//是否纵向打印,true纵向打印,false横向打印
			paper:"A4",//纸张规格
			marginLeft:"0.5",//单位：英寸,左边页边距
			marginRight:"0.5",//单位：英寸,右边页边距
			marginTop:"0.5",//单位：英寸,上边页边距
			marginBottom:"0.3",//单位：英寸,底边页边距
			printButton:"printButton",//打印按钮的ID，页面上无须绑定onclick事件
			headerClass:"header",//页头的样式
			itemClass:"item",//普通行的样式
			isIframePrint:false,//打印页面是否在iframe框架里面
			isAutoPageBreak:false,//是否自动分页
			isContainHeader:false
		} 
		var browser = getNavigatorType();
		
		var $this = $(this);
		
		var options = $.extend(defaults, options); 
		
		window.onload=function(){
			pageSetup(options);
			autoPageBreak(options);
		};
		
		
		
		if("ie" == browser){
			var isIePrint = getQueryString("isIePrint");
			if(isIePrint==1){
				document.all.WebBrowser.ExecWB(7,1);
			}
			$("#"+options.printButton).click(function(){
				if(options.isIframePrint){
					window.open(window.location.href+'?isIePrint=1','','height=600,width=1024,top=0,left=0,toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no');
				}else{
					document.all.WebBrowser.ExecWB(7,1);
				}
			});
		}else if("chrome" == browser){
			$("#"+options.printButton).click(function(){
				var histoty = document.body.innerHTML;
				window.print();
				document.body.innerHTML = histoty;
			});
		}
		//解析浏览器地址栏参数
		function getQueryString(name) {    
			var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
			var r = window.location.search.substr(1).match(reg);
			if (r != null) return decodeURIComponent(r[2]); 
			return "";
		};
		
		//判断浏览器类型
		function getNavigatorType(){
			var Sys = {};
			var ua = navigator.userAgent.toLowerCase();
			var s;
			(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
			(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
			(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
			(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
			(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
			if(Sys.ie){
				return "ie";
			}else if(Sys.firefox){
				return "firefox";
			}else if(Sys.chrome){
				return "chrome";
			}else if(Sys.opera){
				return "opera";
			}else if(Sys.safari){
				return "safari";
			}else{
				return "";	
			}
		}
		
		//获得IE浏览器版本
        function checkIEV() {
            var X, V, N;
            V = navigator.appVersion;
            N = navigator.appName;
            if (N == "Microsoft Internet Explorer")
                X = parseFloat(V.substring(V.indexOf("MSIE") + 5, V.lastIndexOf("Windows")));
            else
                X = parseFloat(V);
            return X;
        }
		
		//自动分页,解决tr跨页显示的问题
		function autoPageBreak(options){
			if(options.isAutoPageBreak){
				//定义打印页面高度
			var pageHeight = 0;
			if("ie" == browser){
				if(options.isVertical){
					pageHeight = 1020;	
				}else{
					pageHeight = 810;	
				}
			}else if ("chrome" == browser){
				if(options.isVertical){
					pageHeight = 1070;	
				}else{
					pageHeight = 750;
				}
			}
			var h = 0;
			var content = "<table width='100%' cellpadding='0' cellspacing='0' border='0'>";
			$("tr").each(function(i) {
               h += $(this).height();
			    //console.log(h+":"+pageHeight+":"+$(this).height()+":"+i);
			   if( h < pageHeight){
				   content += $(this).prop('outerHTML');
				}else if( h == pageHeight){
					content += $(this).prop('outerHTML');
					content += "</table><table width='100%' cellpadding='0' cellspacing='0' border='0'>";
					if(options.isContainHeader){
						if($(this).next("tr").hasClass(options.itemClass)){
							content += $("tr."+options.headerClass).prop('outerHTML');
							h += $("tr."+options.headerClass).height();
						}
					}
					h = 0;
				}else{
					content += "</table><table width='100%' cellpadding='0' cellspacing='0' border='0'>";
					h = 0;
					if(options.isContainHeader){
						if($(this).hasClass(options.itemClass)){
							content += $("tr."+options.headerClass).prop('outerHTML');
							h += $("tr."+options.headerClass).height();
						}
					}
					content += $(this).prop('outerHTML');
				}
            });	
			content+= "</table>";
			$this.empty().html(content);
			}
		}
		
		//设置网页打印的页眉页脚和页边距
        function pageSetup(options) {
			if("ie" == browser){
				var HKEY_Root, HKEY_Path, HKEY_Key;
            HKEY_Root = "HKEY_CURRENT_USER";
            HKEY_Path = "\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
            try {
                var Wsh = new ActiveXObject("WScript.Shell");
                HKEY_Key = "header";
                //设置页眉（为空）
                //Wsh.RegRead(HKEY_Root+HKEY_Path+HKEY_Key)可获得原页面设置   
                Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");
                HKEY_Key = "footer";
                //设置页脚（为空）   
                Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "");

                //这里需要浏览器版本，8.0以下的页边距设置与8.0及以上不一样，注意注册表里的单位是英寸，打印设置中是毫米，1英寸=25.4毫米
                if (checkIEV() < 8.0) {
                    HKEY_Key = "margin_left";
                    //设置左页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.25");
                    HKEY_Key = "margin_right";
                    //设置右页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.25");
                    HKEY_Key = "margin_top";
                    //设置上页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.10");
                    HKEY_Key = "margin_bottom";
                    //设置下页边距   
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, "0.10");
                }
                else {
                    HKEY_Key = "margin_left";
                    //设置左页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginLeft);
                    HKEY_Key = "margin_right";
                    //设置右页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginRight);
                    HKEY_Key = "margin_top";
                    //设置上页边距
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginTop);
                    HKEY_Key = "margin_bottom";
                    //设置下页边距   
                    Wsh.RegWrite(HKEY_Root + HKEY_Path + HKEY_Key, options.marginBottom);
                }
            }
            catch (e) {
                alert("ActiveX控件被禁用,请按下面步骤操作：\n1、请打开浏览器‘工具’菜单/‘选项’/‘安全’下的‘自定义级别’，\n把‘对没有标记为安全的activex控件进行初始化和脚本运行’设置为‘启用’。\n2、刷新本页 ");
            }
			}
            
        }
	}; 
})); 
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function init(target){
		$(target).addClass('validatebox-text');
	}
	
	function clearValidate(target){
		$(target).removeClass('validatebox-invalid');
		$(target).parents("div.fielddiv2").css("borderColor","");
		hideTip(target);
	}
	
	function bindEvents(target){
		var box = $(target);
		var tip = $.data(target, 'validatebox').tip;
		if(box.hasClass('checkboxgroup') || box.hasClass('radiogroup')){//针对checkboxgroup
			//box.find(":input:visible[type=checkbox]").unbind('.validatebox').bind(
			box.find(".ta_pw_chkbox").unbind('.validatebox').bind(//pengwei,新checkbox验证
				'click.validatebox',function(){
					if(!validate(target)){
					  showTip(target);
					}
				}
			);
			//box.find(":input:visible[type=radio]").unbind('.validatebox').bind(
			box.find(".ta_pw_radio").unbind('.validatebox').bind(//pengwei,新radio验证
					'click.validatebox',function(){
						if(!validate(target)){
							showTip(target);
						}
					}
			);
			box.find('>label.fieldLabel').unbind('.validatebox').bind('click.validatebox',
				function(){
					if(!validate(target))
						showTip(target);
				}
			);
		}else{
			box.unbind('.validatebox').bind('focus.validatebox', function(){
				var valid = validate(target);
				if(!valid){
					showTip(target);
				}
			}).bind('blur.validatebox', function(){
				validate(target);
				
				hideTip(target);
			}).bind('mouseover.validatebox', function(){
				if (box.hasClass('validatebox-invalid')){
					showTip(target);
				}
			}).bind('mouseout.validatebox', function(){
				if(document.activeElement!=target){
					hideTip(target);
				}
			}).bind('keyup.validatebox',function(){
				if(!validate(target))
					showTip(target);
			});
		}
	}
	
	/**
	 * show tip message.
	 */
	function showTip(target){
		var $input = $(target);
		if($input.hasClass("ffb-input"))return;//让下拉框没有tip提示
		var box;
		if($input.hasClass("checkboxgroup")) {
			box = $input;
		} else {
			box = $input.parent("div.fielddiv2");
		}
		if(box && box.length < 1){//表格验证
			box = $input.parent("div.slick-cell");
		}
		var msg = $.data(target, 'validatebox').message;
		if(msg == $.fn.validatebox.defaults.missingMessage)return;//liys 20140904 如果为默认的提示信息（此项为必输项），不显示
		var tip = $.data(target, 'validatebox').tip;
		if (!tip){
			tip = $(
				'<div class="validatebox-tip">' +
					'<div class="validatebox-tip-content  ui-corner-all ffb_163">' +
					'</div>' +
					'<div class="validatebox-tip-pointer">' +
					'</div>' +
				'</div>'
			).appendTo('body');
			$.data(target, 'validatebox').tip = tip;
		}
		var $c = tip.find('.validatebox-tip-content');
		$c.html(msg);
		var cheight = tip.outerHeight(true);
		if(cheight==0) cheight =17;
		
		var _left = box.offset().left+10;
		var _top = box.offset().top;
		//lins 修改top遮挡问题
//		if (_top > cheight - 3)
//			_top = box.offset().top - cheight;
//		else 
//			_top = box.offset().top + cheight;
		var isright = ($(window).width()-_left)<150?true:false;
		if(_top > cheight - 2){//输入框上边显示
			if(isright){
				var tmp = tip.find('div.validatebox-tip-pointer-right');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-right\">").insertAfter($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}else{
				var tmp = tip.find('div.validatebox-tip-pointer');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer\">").insertAfter($c);
				//tip.find('div.validatebox-tip-pointer').remove();		
			}
			_top = _top - cheight;
		}else{//输入框下边显示
			if(isright){
				var tmp = tip.find('div.validatebox-tip-pointer-topright');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-topright\">").insertBefore($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}else{
				var tmp = tip.find('div.validatebox-tip-pointer-topleft');
				if(tmp.length==0)$("<div class=\"validatebox-tip-pointer-topleft\">").insertBefore($c);
				tip.find('div.validatebox-tip-pointer').remove();	
			}
			_top = _top + box.outerHeight(true);
		}
		tip.css({
			display:'block',
			left:_left ,
			top:_top
		});
		$.data(target, 'validatebox').tip =tip;
		//liys 显示1s后消失
		setTimeout(function(){
			hideTip(target)
		}, 1000);
	}
	
	/**
	 * hide tip message.
	 */
	function hideTip(target){
		var data = $.data(target, 'validatebox');
		if (data && data.tip){
			data.tip.fadeOut(1000);
			setTimeout(function(){
				if(data.tip){
					data.tip.remove();
				}
				data.tip = null;
			}, 1000)
		}
	}
	function makeInvalid(target,tipMessage){
		$(target).addClass('validatebox-invalid');
		$(target).parents("div.fielddiv2").css("borderColor","#c30");
		setTipMessage(target,tipMessage);
	}
	function setTipMessage(target,msg){
		var cachedata = $.data(target, 'validatebox');
		if(!cachedata){
			$.data(target, 'validatebox',{message:msg});
		}else{
			$.data(target, 'validatebox').message = msg;
		}
	}	
	/**
	 * do validate action
	 */
	function validate(target){
		var data = $.data(target, 'validatebox');
		if(!data)return true;
		var opts = data.options;
		var tip = data.tip;
		var value,pSize;
		var box = $(target);
		if (target.id != "") 
			pSize = $("#" + target.id).hasClass("amountfield");
		else	
			pSize = $(target).hasClass("amountfield");
		if ( pSize) {
			value = $("#" + target.id + "_hidden").val();			
		} else {
			value = box.val();
		}
		if(box.hasClass('checkboxgroup') || box.hasClass('radiogroup')){
			value = Base.getValue(target.id);
		}
		
//		$("#console").html($("#console").text()+'</br>,validdate');

		
		// if the box is disabled, skip validate action.
		var disabled = box.attr('disabled'),readonly = box.attr('readOnly');
//		if (disabled == true || disabled == 'true' || readonly==true || readonly=='true'){
//			return true;
//		}
		
		//更改对readonly不做验证的提交判断，改为要验证
		if (disabled == true || disabled == 'true' ){
			return true;
		}
		if (opts.required){
			if (value == '' || (value && value.length && value.length==0) || !value){//value.length==0 针对checkboxgroup的判断
				makeInvalid(target,opts.missingMessage);
				return false;
			}
		}
		if (opts.validType){
			if(opts.validType == "self"){//自定义验证，liys
				if(typeof eval(opts.validFunction) == "function"){
					var selfResults = eval(opts.validFunction)();
					makeInvalid(target,opts.invalidMessage || selfResults.message);
					return selfResults.result;
				}
			}
			var result = /([a-zA-Z_]+)(.*)/.exec(opts.validType);
			var rule = opts.rules[result[1]];
			if (value && rule){
				var param = eval(result[2]);
				if (!rule['validator'](value, param)){
//					box.addClass('validatebox-invalid');
					
					var message = rule['message'];
					if (param){
						for(var i=0; i<param.length; i++){
							message = message.replace(new RegExp("\\{" + i + "\\}", "g"), param[i]);
						}
					}
//					setTipMessage();
//					showTip(target);
					makeInvalid(target,opts.invalidMessage || message);
					return false;
				}
			}
		}
		
		box.removeClass('validatebox-invalid');
		box.parents("div.fielddiv2").css("borderColor","");
		hideTip(target);
		return true;
	}
	function setRequired(target,required){
		var $obj = $(target);
		if(required){
			$obj.attr('required',"true");
			var d = $.data(target, 'validatebox');
			if(d){
				d.options.required = required;
			}else{
				$obj.validatebox();
				$obj.validatebox('clear');
			}
		}else{
			$obj.removeAttr('required');
			var d = $.data(target, 'validatebox');
			if(d){
				d.options.required = false;
			}
			$obj.validatebox('clear');
		}
	}
	$.fn.validatebox = function(options,param){
		if (typeof options == 'string'){
			switch(options){
				case 'clear':
					return this.each(function(){
						clearValidate(this);
					});
				case 'validate':
					return this.each(function(){
						validate(this);
					});
				case 'isValid':
					return validate(this[0]);
				case 'makeInvalid':
					return makeInvalid(this[0],param);
				case 'setRequired':
					return setRequired(this[0],param);				
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'validatebox');
			if (state){
				$.extend(state.options, options);
			} else {
				init(this);
				var t = $(this);
				state = $.data(this, 'validatebox', {
					options: $.extend({}, $.fn.validatebox.defaults, {//LINS ie8下request=“true”失效 添加t.attr('required') == 'required'
						required: (t.attr('required') ? (t.attr('required') == 'true' || t.attr('required') == 'required' || t.attr('required') == true) : undefined),
						validType: (t.attr('validType') || undefined),
						missingMessage: (t.attr("toolTip") || t.attr('missingMessage') || undefined),//liys 829 如果设置了toolTip，则不再提示默认验证错误信息，而是提示toolTip中的信息
						invalidMessage: (t.attr('invalidMessage') || undefined), 
						validFunction: (t.attr('validFunction') || undefined)
					}, options)
				});
			}
			
			bindEvents(this);
			
		});
	};
	
	$.fn.validatebox.defaults = {
		required: false,
		validType: null,
		missingMessage: '此项为必输项!',
		invalidMessage: null,
		
		rules: {
			email:{
				validator: function(value){
					return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(value);
				},
				message: '您输入的不符合email格式要求'
			},
			url: {
				validator: function(value){
					return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
				},
				message: '您输入的不合URL格式要求'
			},
			length: {
				validator: function(value, param){
					//var len = $.trim(value).length;
					
					var len = (value + "").replace(/[^\x00-\xff]/gmi,'pp').length;
					return len >= param[0] && len <= param[1];
				},
				message: '输入的字符长度必须在 {0} 和 {1}之间'
			},
			chinese:{
				validator:function(value){
				  return /^[\u4e00-\u9fa5]+$/.test(value);
				},
				message: '只能输入中文'
			},
			date:{
				validator:function(value,param){
					
					this.message= '按n键自动输入当前时间';
					if(!Ta.util.isDate(value))return false;
					
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期必须在 {0} 到 {1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的日期格式不正确,按n键自动输入当天日期'
			},
			datetime:{
				validator:function(value,param){
					this.message = "您输入的日期时间格式不正确,按n键自动输入当前时间"; 
					if(!Ta.util.isDateTime(value))return false;
					this.message = "输入的日期时间必须在{0}到{1}之间";
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期时间必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期时间必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期时间必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的日期时间格式不正确,按n键自动输入当前时间'
			},
			issue:{
				validator:function(value,param){
					var bformat = /^\d{4}((0[1-9])|(1[0-2]))$/.test(value);
					this.message = "您输入的期号不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					this.message = "输入的期号必须在 {0} 到 {1}之间";
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的期号必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的期号必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的期号必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的期号不正确'
			},
			dateMonth:{
				validator:function(value,param){
					var bformat = /^\d{4}-((0[1-9])|(1[0-2]))$/.test(value);
					this.message = "您输入的年月不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					this.message = "输入的年月必须在 {0} 到 {1}之间";
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的年月必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的年月必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的年月必须在{0}到{1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的年月不正确'
			},
			dateYear:{
				validator:function(value,param){
					var bformat = /^\d{4}/.test(value);
					this.message = "您输入的年份不正确";
					if(!bformat)return false;
					if(!jQuery.isArray(param))return true;
					if(param[0]=='' && param[1]!=''){
						this.message = "输入的日期必须小于或等于{1}";
						return value <= param[1];
					}
					else if(param[0] != '' && param[1]==''){
						this.message = "输入的日期必须大于或等于{0}";
						return value >= param[0];
					}
					else{
						this.message = "输入的日期必须在 {0} 到 {1}之间";
						return(value >= param[0]) && (value <= param[1]);
					}
				},
				message: '您输入的年份不正确'
			},
			zipcode:{
				validator:function(value){
					return /[0-9]\d{5}(?!\d)/.test(value); //修改/[1-9]\d{5}(?!\d)/.test(value);
				},
				message: '您输入的邮编不正确'
			},
			mobile:{
				validator:function(value){
					return /^1[3|4|5|8][0-9]\d{8}$/.test(value);
				},
				message: '您输入的手机号码格式不正确'
			},
			ip:{
				validator:function(value){
					return /^((?:(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(?:25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d))))$/.test(value);
				},
				message: '您输入的IP地址格式不正确'
			},
			integer:{
				validator:function(value){
					return /^-?[1-9]\d*$/.test(value);
				},
				message: '只能输入整数'
			},
			number:{
				validator:function(value,param){
					if(isNaN(value)){
						return false;
					}
					if(!jQuery.isArray(param))return true;
					if(param[0]==='' && param[1]!==''){
						this.message = "输入的数值必须小于或等于{1}";
						return Number(value) <= Number(param[1]);
					}
					else if(param[0] !== '' && param[1]===''){
						this.message = "输入的数值必须大于或等于{0}";
						return Number(value) >= Number(param[0]);
					}
					else{
						this.message = "输入的数值必须在{0}到{1}之间";
						return(Number(value) >= Number(param[0])) && (value <= Number(param[1]));
					}
					return true;
				},
				message: '只能输入数字'
			},
			checkboxgroup:{
				validator:function(value,param){
					var length = value.length;
					return length <= param[1] && length >= param[0] ;
				},
				message: '选的个数必须在{0} 到 {1}之间'
			},
			compare:{
				validator:function(value,param){
					var targetValue = Base.getValue(param[1]);
					var targetLabel = Base.getFieldLabel(param[1]);
					if(!isNaN(value) && !isNaN(targetValue)){//为数字时，转换成数字再比较
						value = Number(value);
						targetValue = Number(targetValue);
					}
					switch(param[0]){
						case '=':
							this.message = "您输入的值必须与["+targetLabel+"]相同";
							return value == targetValue;
						case '>':
							this.message = "您输入的值必须大于["+targetLabel+"]";
							return value > targetValue;
						case '<':
							this.message = "您输入的值必须小于["+targetLabel+"]";
							return value < targetValue;
						case '>=':
							this.message = "您输入的值必须大于或等于["+targetLabel+"]";
							return value >= targetValue;
						case '<=':
							this.message = "您输入的值必须小于或等于["+targetLabel+"]";
							return value <= targetValue;
						case '!=':
							this.message = "您输入的值不能等于["+targetLabel+"]";
							return value != targetValue;
					}
					return true;
				},
				message: '与其他输入项不匹配'
			},
			idcard:{
				validator:function(value){
						var sId = value;
						
						if (sId.length == 15) {
							if(!/^\d{14}(\d|x)$/i.test(sId)){
								this.message =  "你输入的身份证长度或格式错误";
								return false;
							} else  {
							    sId=sId.substr(0,6)+'19'+sId.substr(6,9);
							    sId+= getVCode(sId);
							}
						}
						function getVCode(CardNo17) {
						  var Wi = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
						  var Ai = new Array('1','0','X','9','8','7','6','5','4','3','2');
						  var cardNoSum = 0;
						  for (var i=0; i<CardNo17.length; i++)cardNoSum+=CardNo17.charAt(i)*Wi[i];
						  var seq = cardNoSum%11;
						  return Ai[seq];
						}
						var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"} ;

						var iSum=0 ;
						var info="" ;
						if(!/^\d{17}(\d|x)$/i.test(sId)){
							this.message =  "你输入的身份证长度或格式错误";
							return false;
						}
						sId=sId.replace(/x$/i,"a"); 
						if(aCity[parseInt(sId.substr(0,2))]==null){ 
							this.message =  "你的身份证地区非法";
							return false;
						}
						sBirthday=sId.substr(6,4)+"-"+Number(sId.substr(10,2))+"-"+Number(sId.substr(12,2)); 
						var d=new Date(sBirthday.replace(/-/g,"/")) ;
						if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate())){
							this.message =  "身份证上的出生日期非法";
							return false; 
						}
						for(var i = 17;i>=0;i --) iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11) ;
						if(iSum%11!=1){
							this.message =  "你输入的身份证号非法";
							return false; 
						}
						return true;
				},
				message: '您输入的身份证号非法'
			}
		}
	};
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", "WdatePicker"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
     function MaskXtIpt(a, o,opt,type)
        	{
        		o && (o = $(o)) || (o = this);
        		opt || (opt = {});
        		var n, i, j, k, b, s ,x, lp;
        		a || (a = []);
        		
        		o.keyup(function(evt)
        		{
        			n = evt.keyCode;
        			//如果日期框为只读,则不允许进行任何操作
        			if(o.attr('readonly') == true || o.attr('readonly') == 'readonly'){
        				return;
        			}
        			//如果是小键盘输入,则把小键盘转换成大键盘
       			 	if(96<=n && n<=105){
       			 		n = n-48;
       			 	}
        			evt || (evt = window.event || {keyCode:0});
        			 	if(evt.ctrlKey || evt.shiftKey ||evt.altKey|| evt.metaKey){
        			 		return true;
        			 	}
        			 	if(n==13)return true;//回车
        			 	if(n==78){//n 键
        			 		switch(type){
        			 		  case 1:
        			 			this.value = Ta.util.getCurDate();break;
        			 		  case 2:
        			 		    this.value = Ta.util.getCurDateTime();break;
        			 		  case 3:
        			 		    this.value = Ta.util.getCurIssue();break;
        			 		  case 4:
        			 				this.value = String(Ta.util.getCurDateTime()).substring(11,19);break;
        			 		  case 5:
        			 				this.value = Ta.util.getCurDateMonth();break;
        			 		  case 6:
      			 				    this.value = Ta.util.getCurDateYear();break;
        			 		}
        			 		$this = $(this);
        			 		setTimeout(function(){
        			 			$this.focus();
        			 		}, 10);
        			 		return false;
        			 	}
        			 	s = String(this.value)//处理直接月为2变成02,日期4变为04
        			 	if(s.length == 6 && !o.hasClass("issuefield")){
        			 		var lastChar = s.substring(s.length-1,s.length);
			 				if(lastChar >1){
			 					s = s.substring(0,s.length-1) + "0"+lastChar;
			 				}
			 				this.value = s;
			 			}
			 			if(s.length == 9 && !o.hasClass("issuefield")){
			 				var lastChar = s.substring(s.length-1,s.length);
			 				if(lastChar > 3){
			 					s = s.substring(0,s.length-1) + "0"+lastChar;
			 				}
			 				this.value = s;
			 			}
        			 	
        			 	j = (s = String(this.value)).length;
        			 	if( 0 == a.length || (112 <= n && 123 >= n) || 
        			 	    27 == n || 9 == n || 
        			 	    91 == n || 20 == n ||                      
        			 	    18 == n || 17 == n || 16 == n ||        
        			 	    (35 <= n && 46 >= n) )return true;
        			 	if(8 == n){//撤销操作
        			 		if(lp = opt[j]){
        			 			//this.value += lp, j = (s = String(this.value)).length;
        			 			this.value = String(this.value).substring(0,String(this.value).length-1);
        			 			j = (s = String(this.value)).length;
        			 			return true;
        			 		}else{
        			 			return true;
        			 		}
        			 	}
        			 	if(lp = opt[j])this.value += lp, j = (s = String(this.value)).length;
        			 	
        			 	if(0xBD == n)n = 45;
        			 	
        			 	
        			 	/*for(i = 0; i < a.length; i++)
        			 	{
        			 		k = a[i];
        			 		
        			 		if(j >= k.length)return false;
        			 	  b = k[j].test(s += String.fromCharCode(n));
        			 	  if(b)
        			 	  {
        			 	  	j = String(this.value = s).length;        			 	  
        			 	  	if(lp = opt[j])this.value += lp;
        			 	  }
        			 	}*/
        			 	for(i = 0; i < a.length; i++)
        			 	{
        			 		k = a[i];
        			 		
        			 		if(j > k.length)return false;
        			 		if((s.length == 5 && !o.hasClass("issuefield")) || s.length == 8 || s.length == 11 || s.length == 14 || s.length == 17){
        			 			s = s.substring(0,s.length-2);
        			 			s += String.fromCharCode(n);
        			 		}else {
        			 			var lastChar = s.substring(s.length-1,s.length);
        			 			if(isNaN(lastChar)){
        			 				s = s.substring(0,s.length-1);
        			 				s += String.fromCharCode(n);
        			 			}
        			 			
        			 		}
        			 		//s += String.fromCharCode(n);
        			 		if((s.length == 4 && !o.hasClass("issuefield"))|| s.length == 7 && !o.hasClass("dateMonthfield") || s.length == 10 && !o.hasClass("datefield")|| s.length == 13 || s.length == 16){
        			 		  	b = k[j-2].test(s);
        			 		}else{
        			 			b = k[j-1].test(s);
        			 		}
        			 	  check(b,this);
        			 	  
        			 	}
        			 	return false;//}
        			 	
        		
        		function check(b,obj){
			   	  if(b){
				  	j = String(this.value = s).length;
				  	//当设置了dateMonth=true时,屏蔽掉月份后面自动生成的'-'符号      
				  	if(o.attr('maxlength') == 7 && j == 7){
						this.value = s;
						//return false;
					}else{ 			 	  
				  		if((lp = opt[j])){
				  			//防止输入过快而导致错误
							if(obj.value.substring(4,5) != "-" && obj.value.length > 4)
								$(obj).val(obj.value.substring(0,4) + lp);
							if(obj.value.substring(7,8) != "-" && obj.value.length > 7)
								$(obj).val(obj.value.substring(0,7) + lp);
				  		}
				  	}
			 	  }else{//回退操作
			 		  if(j<o.attr('maxlength')){
			 		  	j = String(o.val(s.substring(0,s.length-1))).length;
			 		  	if(lp = opt[j])o.val(o.val()-lp);
			 		  }
			 		  else{
			 		  	s = s.substring(0,s.length-1);
			 		  	j = String(o.val()).length;
			 		  	o.val(s);
			 		  	if(lp = opt[j])o.val(o.val()-lp);
			 		  	//check(k[j-1].test(o.val()));
			 		  }
			 	  }
   				}
       }); 		
   }
   
   jQuery.fn.extend({        	
		datetimemask:function(type){
			 switch (type) {
			 	case 1 ://2001-01-01格式的时间
			 	  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/,
		            /\d{4}[\-\xBD]/, 
		            /\d{4}[\-\xBD][0-1]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/,
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD][0-3]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1]))/]
		          ], this, {4:'-',7:'-'},1);
			 	  if(this[0].value.length>10){
			 		 this[0].value = this[0].value.substring(0,10);
			 	  }
			          break;			 		
			 	case 2 ://2001-01-01 00:00:00 带有时间格式
			 	  MaskXtIpt([
			          [ /\d/, 
			            /\d{2}/, 
			            /\d{3}/, 
			            /\d{4}/,
			            /\d{4}[\-\xBD]/, 
			            /\d{4}[\-\xBD][0-1]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD][0-3]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1]))/,			            
			            /* 时分秒就加上后面的 */
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) /,/* 空格 */            
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) [0-2]/,/* 小时 */
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3]))/,
			            
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d/,      
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]\d/
			            ]
			          ], this, {4:'-',7:'-',10:' ',13:":",16:":"},2);
				 	  if(this[0].value.length>19){
				 		 this[0].value = this[0].value.substring(0,19);
				 	  }
			          break;
			 	case 3 : //期号，如200101
			 	  MaskXtIpt([
			          [ /\d/, 
			            /\d{2}/, 
			            /\d{3}/, 
			            /\d{4}/,
			            /\d{4}[0-1]/, 
			            /\d{4}((0[1-9])|(1[0-2]))/
			           ]
			      ],this,null,3);
			 	  if(this[0].value.length>6){
			 		 this[0].value = this[0].value.substring(0,6);
			 	  }			 	  
			      break;
			 	case 4 ://00:00:00 带有时间格式
				  MaskXtIpt([
				    [ /[0-2]/,
				      /(([0-1][0-9])|(2[0-3]))/,
				      /(([0-1][0-9])|(2[0-3])):/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d/,      
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]\d/
				    ]
				    ], this, {2:':',5:':'},4);
				  if(this[0].value.length>8){
					 this[0].value = this[0].value.substring(0,8);
				  }
				  break;
				  case 5 ://2001-01格式的时间
			 	  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/,
		            /\d{4}[\-\xBD]/, 
		            /\d{4}[\-\xBD][0-1]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/
		            ]
		          ], this, {4:'-'},5);
			 	  if(this[0].value.length>7){
			 		 this[0].value = this[0].value.substring(0,7);
			 	  }
			          break;			
				  case 6://1999年份格式
				  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/
		            ]
		          ], this, null,6);
			 	  if(this[0].value.length>4){
			 		 this[0].value = this[0].value.substring(0,4);
			 	  }
				  break;	
			 }
		}
   });
})); 
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", "datetimeMask"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
		taissue : taissue
	});
	function taissue($div, options) {
		var self = this;
		options = $.extend({
			txtWidth : 100,/* 文本框大小 */
			disabled : false,
			readOnly : false,
			txtHeight : 20,/* 文本框高度 */
			txtId : "issue",
			txtName : "dto['issue']"
		}, options || {});
		function init() {
			var $input = $("#"+options.txtId);
			
			$input.unbind('.issue');
			$input.bind("click.issue",function(){
				//判断组件是否只读
				if("readonly" === ($input.attr("readOnly"))){
					return;
				}
				$input.datetimemask(3);//调用datetimemask中的期号判断，阻止非数字输入和非期号格式输入
				$input.validatebox('validate');
				var year,begin;
				var val = $input.val();
				var mYear = parseInt(new Date().getFullYear());//获取当前年份
				if(val == "" || val == null){
					year = mYear;
				}else{
					//设置输入框的值长度不低于4位
					switch(val.length){
						case 1:
							val = val+"000";break;
						case 2:
							val = val+"00";break;
						case 3:
							val = val+"0";break;
						default:
							break;
					}
					year = parseInt(val.substring(0,4));
				}
				//计算当前页的开始年份begin
				if((mYear - 9 - year) > 0){//如果输入值不在当前年份这一页
					if((mYear - 9 - year)%10 == 0){//year为当前页开始值
						begin = year;
					}else if((mYear - 9 - year)%10 == 1){//year为当前页最大值
						begin = year - 9;
					}else{
						begin = year - (10 - (mYear - 9 - year)%10);
					}
				}else{
					begin = mYear - 9;
				}
				
				//设定日期长度6位
				if(begin<1000){
					begin = 1004;
				}
				
				loadYears(year,options.txtId,begin,0);//加载年份面板
			});
			//加载年份面板并绑定事件
			function loadYears(year,id,start,flag){
				start = parseInt(start);
				var $yearsDiv = $("#"+id+"_years");
				if($yearsDiv[0]){
					$yearsDiv.remove();
				}
				$yearsDiv = $("<div id=\""+ id +"_years\"></div>");
				$yearsDiv.addClass("issue_years");
				//判断点击的是向上图标还是向下图标
				if(flag == "0"){
					start = start;
				}else if(flag == "1"){
					start = start - 10;
				}else{
					start = start + 10; 
				}
				
				var bgImg = Base.globvar.contextPath
				+ "/ta/resource/themes/base/issue/images/";
				//上箭头
				var $upBtn =  $("<div></div>");
				$upBtn.addClass("issue_up");
				
				var $imgUBtn = $("<input type='image'/>");
				$imgUBtn.css({"height":"10px","border":"0px"})
				.attr("src",bgImg+"up.png")
				.bind("click",function(){
					loadYears(year,id,start,"1");
				})
				.appendTo($upBtn);
				
				$upBtn.appendTo($yearsDiv);
				//下箭头
				var $downBtn =  $("<div></div>");
				$downBtn.addClass("issue_down");
				
				var $imgDBtn = $("<input type='image'/>");
				$imgDBtn.css({"height":"10px","border":"0px"})
				.attr("src",bgImg+"down.png")
				.bind("click",function(){
					loadYears(year,id,start,"2");	
				})
				.appendTo($downBtn);
				
				var content = "";
				for(var i = 0;i < 10;i++){
					content = "<div id=\""+ (start + i) +"\">"+ (start + i) +"</div>";
					$(content).addClass("issue_year").appendTo($yearsDiv).click(function(){
						clickYear(this,id);
					});
				}
				$downBtn.appendTo($yearsDiv);
				$yearsDiv.appendTo($div);
				$("#"+year).addClass("is_selected");
				$div.show();
				$yearsDiv.show();
			}
			//单击年份显示月份面板
			function clickYear(o,id){
				$(o).parent().find("div").removeClass("is_selected");
				$(o).addClass("is_selected");
				var $months = $("#" + id + "_months");
				if($months[0]){
					$months.remove();
				}
				$months = $("<div id=\""+ id +"_months\"></div>");
				$months.addClass("issue_months")
				.css({"left":"62px","width":"172px","height":"226px"});
				
				var $monName = $("<div>月份</div>");
				$monName.addClass("month_title");
				
				var $monDiv = $("<div style='height:100%;'></div>");
				$monName.appendTo($months);
				$monDiv.appendTo($months);
				
				var $monTab = $("<table width=\"100%\" height=\"89%\" align=\"center\" style=\"margin-bottom:10px;\"></table>"); 
				$monTab.appendTo($monDiv);
				var n = 0;
				for(var i=1;i<5;i++){//循环添加tr
					var $conTr = $("<tr></tr>");
					var conTd="";
					$conTr.appendTo($monTab);
					for(var j=1;j<4;j++){//循环添加td
						n = parseInt(n)+1;
						var tempn = n+"";
						if(n < 10){
							tempn = "0" + n;
						}
						conTd = "<td val=\""+ ($(o).attr("id") + tempn) +"\">" + parseInt(n) + "月</td>";
						$(conTd).addClass("is_td").appendTo($conTr).click(function(){
							setValue(this,id);
						});
					}
				}
				$months.insertAfter($("#" + id +"_years"));
				$months.show();
			}
			//单击月份，设值
			function setValue(o,id){
				var value = $(o).attr("val");
				$("#"+id).val(value);
				$("#"+id+"_months").hide();
				$div.hide();
			}
			
			return self;
		}// end init
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taissue'// 将方法注册为公共方法
		});
	}
}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	// 扩展moneyInput到jQuery实例对象中
	$.fn.extend({
		moneyInput:moneyInput
	});
	/**
	 * @params
	 *		options(object)	可选，{decimalPlace:2, symbol:"$", cnBox:"#cnBox"}
	 *		                ，当值为'getValue'时，返回隐藏文本框的值；当值为'setValue'时	     		     *      				，value参数为必须，值为设置文本框和隐藏文本框的值。
	 *		value(string)	可选，和options为'setValue'时配套使用
	 */
	function moneyInput(options, value){
		var scope = this;
		// 显示转换后的大写金额的DOM容器
		var $cnBox;
		// 创建的隐藏文本框的JQUERY对象
		var $hiddenInput;
		// 当前调用的jquery包装的input对象
		var $container;
		// 当前调用的input的DOM节点
		var $input;
		// 隐藏文本框的DOM节点
		var $hidden;
		// 当前input对象的DOM ID
		var container;
		// 用户实际输入的金额
		var money;
		// 货币符号
		var _symbol;
		// 小数位数
		var _decimal;
		// 是否显示中文转换金额
		var showCnMoney = false;
		
		// 控件初始化
		function init(){
			var _options = options;
			var _cnBox = _options.cnBox;
			if (_cnBox != undefined && typeof _cnBox === "string" && _cnBox !== ""){
				if (_cnBox.indexOf("#") === -1){
					_cnBox = "#" + _cnBox;
				}
				$cnBox = $(_cnBox);
				if ($cnBox.length < 1){
					throw new Error("cnBox: " + _cnBox + " does not exist.");
				}
				showCnMoney = true;
			}
			
			$container = scope;
			container = scope[0].id || "";
			if (container === "" || $container.length < 1){
				throw new Error("moneyInput container: " + container + " does not exist.");
			}
			$input = $container.get(0);
			$($input).bind('keypress.moneybox', function(e){
				if ($(this).attr('precision') == 0 && e.which == 46) return false;
				if ($(this).val().indexOf(".") != -1 && $(this).val().length - $(this).val().indexOf(".") > $(this).attr('precision')) {
					return false;
				}
				if (e.which == 45){	//-
					return false;
				} if (e.which == 46) {	//.
					if ($(this).val().indexOf(".", $(this).val().indexOf(".")) != -1) return false;
					return true;
				}
				else if ((e.which >= 48 && e.which <= 57 && e.ctrlKey == false && e.shiftKey == false) || e.which == 0 || e.which == 8) {
					return true;
				} else if (e.ctrlKey == true && (e.which == 99 || e.which == 118)) {
					return true;
				}  else {
					return false;
				}
			});
			$hiddenInput = $("#"+container+"_hidden");
			if ($hiddenInput.length < 1){
				var name = $(scope).attr('name');//
				$(scope).removeAttr('name');
				$hiddenInput = $("<input id=\""+container+"_hidden\" type=\"text\" style=\"display:none\" name=\"" + name + "\" value=\""+$(scope).val()+"\"/>").insertAfter($container);	
				//$hiddenInput = $("<input type='text' name='" + container + "' value='' 	disabled='disabled'></input>");	
				if (showCnMoney){
					$hiddenInput.insertAfter($cnBox);
				} else {
					$hiddenInput.insertAfter($container);
				}	
				// 给文本框绑定事件
				$container.bind("focus", resumeMoney);
				$container.bind("blur", formatMoney);
				$container.bind("keyup", recordMoney);
			}
			$hidden = $hiddenInput.get(0);
			
			_symbol = (_options.symbol == undefined ? "" : _options.symbol);
			_decimal = (_options.decimalPlace == undefined ? 0 : parseInt(_options.decimalPlace));
			_numberRound = (_options.numberRound == undefined ? true:_options.numberRound);
			
			if($input.value.trim()>0){
				var v = $input.value.replaceAll(',','');
				if(_symbol != '')v = v.replaceAll(_symbol,'');
				setValue(v);
			}
		}
		// 文本框获得焦点时清除货币符号显示实际金额
		function resumeMoney(){
			var _money = parseFloat($hidden.value);
			_money = isNaN(_money) ? "" : new Number(_money);
			if (_decimal !== 0 && _money !== ""){
				if(_numberRound == true || _numberRound == "true"){
					_money = _money.toFixed(_decimal).toString();
				}else{
					var nStr = _money.toString();
					var dw = nStr.indexOf(".");
					if(dw<0){
						_money = nStr;
					}else{
						_money = nStr.substring(0, dw + _decimal + 1);
					}
				}
				var dotIndex = _money.indexOf(".");
				if (dotIndex != -1){
					var dotL = _money.substring(0, dotIndex); 
					var dotR = _money.substring(dotIndex, _money.length); 
					if (dotR.indexOf("0") != -1){
						for (var i=dotR.length-1; i>0; i--){
							var c = dotR.charAt(i); 
							if (c === "0"){
								dotR = dotR.slice(0, i);
							} else {
								break;
							}
						}
					}
					dotR = (dotR === "." ? "" : dotR);
					_money = dotL + dotR;
				}
			}
			if(_decimal == 0){
				if(_numberRound == true || _numberRound == "true"){
					_money == ""?0:_money.toFixed(_decimal).toString();
					//_money = _money.toFixed(_decimal).toString();
				}else{
					var nStr = _money.toString();
					var dw = nStr.indexOf(".");
					if(dw<0){
						_money = nStr;
					}else{
						_money = nStr.substring(0, dw + _decimal );
					}
				}
			}
			
			$input.value = ((isNaN(_money) || _money == undefined || _money === "") ? "" : _money);
		}
		// 按照每隔三位显示一个逗号的规律显示格式化后的金额
		function formatMoney(){
			var num = money = ($hidden.value === "") ? "" : parseFloat($hidden.value);
			if(_numberRound == true || _numberRound == "true"){
				num = (num !== "" && String(num.toFixed(_decimal)));
			}else{
				var nStr = num.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var numVal = Number(nStr);
				}else{
					var numVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				num = (num !=="" && String(numVal));
			}
			//如果输入的数字小数位数小于规定的位数,则补'0'
			if(num.toString().indexOf('.')<0){
				if(_decimal >0){
					num = num + '.';
					for(var i = 0; i<_decimal;i++){
						num += '0';
					}
				}
			}else{
				var inputlength = (num.substring(num.indexOf('.'))).length-1;
				if(inputlength<_decimal && inputlength>0){
					for(var i = 0; i<_decimal-inputlength;i++){
						num += '0';
					}
				}
			}
			//小数位不参与格式化，t_num记录小数点（包括小数点）后的数字
			var t_num;
			if(num){
				t_num = num.substring(num.lastIndexOf("."),num.length);
				num = num.substring(0,num.indexOf("."));
			}
			var re = /(-?\d+)(\d{3})/;
			if (money != undefined || money !== ""){
				while (re.test(num)){
					num = num.replace(re, "$1,$2");
				}
			}
			num = num + t_num;
			$input.value = ((money == undefined || money === "") ? "" : _symbol + num);
			if (showCnMoney) cnMoneyFormat();
		}
		// 失去焦点时将实际金额保存到隐藏文本框中
		function recordMoney(op){
			
			var _inputMoney = parseFloat($input.value);
			money = isNaN(_inputMoney) ? "" : _inputMoney;
			_inputMoney = new Number(_inputMoney);
			
			if(_numberRound == true || _numberRound == "true"){
				$hidden.value = ((isNaN($input.value) || isNaN(_inputMoney) || money === "") ? "" : _inputMoney.toFixed(_decimal));
			}else{
				var nStr = _inputMoney.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var _inputVal = Number(nStr);
				}else{
					var _inputVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				$hidden.value = ((isNaN($input.value) || isNaN(_inputMoney) || money === "") ? "" : _inputVal);
			}
		}
		// 显示转换后的中文金额
		function cnMoneyFormat(){ 
			var num = money;
			var cnMoney = "零元整";
			
			var strOutput = "";  
			var strUnit = '仟佰拾亿仟佰拾万仟佰拾元角分';  
			num += "00";  
			var intPos = num.indexOf('.');  
			if (intPos >= 0)  
			num = num.substring(0, intPos) + num.substr(intPos + 1, 2);  
			strUnit = strUnit.substr(strUnit.length - num.length);  
			for (var i=0; i < num.length; i++)  
			strOutput += '零壹贰叁肆伍陆柒捌玖'.substr(num.substr(i,1),1) + strUnit.substr(i,1);  
			cnMoney = strOutput.replace(/零角零分$/, '整').replace(/零[仟佰拾]/g, '零').replace(/零{2,}/g, '零').replace(/零([亿|万])/g, '$1').replace(/零+元/, '元').replace(/亿零{0,3}万/, '亿').replace(/^元/, "零元");
			$cnBox.text((isNaN(money) || money == undefined || money === "") ? "" : cnMoney);
		}
		// 获取隐藏文本框的值
		function getValue (){
			return $hidden.value;
		};
		// 设置隐藏文本框和文本输入框的值
		function setValue (args){
			args = String(args);
			money = args;
			//处理onChange事件获得的值,比如3.99999,得到的值为3.99(precision = 2)
			var num = money = ($hidden.value === "") ? "" : parseFloat($hidden.value);
			if(_numberRound == true || _numberRound == "true"){
				num = (num !== "" && String(num.toFixed(_decimal)));
			}else{
				var nStr = num.toString();
				var dw = nStr.indexOf(".");
				if(dw<0){
					var numVal = Number(nStr);
				}else{
					var numVal = Number(nStr.substring(0, dw + _decimal + 1));
				}
				num = (num !=="" && String(numVal));
			}
			//如果输入的数字小数位数小于规定的位数,则补'0'
					if(num.indexOf('.')<0){
						if(_decimal !== undefined){
							num = num + '.';
							for(var i = 0; i<_decimal;i++){
								num += '0';
							}
						}
					}else{
						var inputlength = (num.substring(num.indexOf('.'))).length-1;
						if(inputlength<_decimal && inputlength>0){
							for(var i = 0; i<_decimal-inputlength;i++){
								num += '0';
							}
						}
					}
			$input.value = _symbol + num;
			$hidden.value = num;
//			$input.value = args;
//			$hidden.value = args;
			$container.blur();
			//formatMoney();
		}
		init();
		if (typeof options.getValue === "string" ||typeof options.setValue === "string"){
			if ("getValue" === options.getValue){
				return getValue();
			} else if("setValue" === options.setValue) {
				if (value != undefined){
					if (value == "") {
						$hidden.value = value;
						$input.value = value;
						return;
					}
					$hidden.value = value;
//					if(options.decimalPlace){
//						if(options.numberRound == true || options.numberRound == "true"){
//							value = Number(value);
//							value = (value !== "" && String(value.toFixed(options.decimalPlace)));
//						}else{
//							var nStr = value.toString();
//							var dw = nStr.indexOf(".");
//							if(dw<0){
//								var numVal = Number(nStr);
//							}else{
//								var numVal = Number(nStr.substring(0, dw + parseInt(options.decimalPlace) + 1));
//							}
//							value = (value !=="" && String(numVal));
//						}
//					}
//					//如果输入的数字小数位数小于规定的位数,则补'0'
//					if(value.indexOf('.')<0){
//						if(options.decimalPlace !== undefined){
//							value = value + '.';
//							for(var i = 0; i<parseInt(options.decimalPlace);i++){
//								value += '0';
//							}
//						}
//					}else{
//						var inputlength = (value.substring(value.indexOf('.'))).length-1;
//						if(inputlength<parseInt(options.decimalPlace) && inputlength>0){
//							for(var i = 0; i<parseInt(options.decimalPlace)-inputlength;i++){
//								value += '0';
//							}
//						}
//					}
//					value = options.symbol + value;
					setValue(value);	
				}
			}
		}
	}
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	function fixValue(target){
		var opts = $.data(target, 'numberbox').options;
		var val = null;
		if (opts.numberRound == true || opts.numberRound == "true") {
			val = parseFloat($(target).val()).toFixed(opts.precision);
		} else {
			var nStr = $(target).val();
			var dw = nStr.indexOf(".");
			if(nStr === ''){
				val = '';
			}else if (dw == -1) {
				val = Number(nStr);
			}
			else val = Number(nStr.substring(0, dw + opts.precision + 1));
		}	
		
		if (isNaN(val)){
			$(target).val('');
			return;
		}
		
		val = val.toString();
		//去除小数点0结尾的数字;
//		var dotIndex = val.indexOf(".");
//		if (dotIndex != -1){
//			var dotL = val.substring(0, dotIndex);
//			var dotR = val.substring(dotIndex, val.length);
//			if (dotR.indexOf("0") != -1){
//				for (var i=dotR.length-1; i>0; i--){
//					var c = dotR.charAt(i); 
//					if (c === "0"){
//						dotR = dotR.slice(0, i);
//					} else {
//						break;
//					}
//				}
//			}
//			dotR = (dotR === "." ? "" : dotR);
//			val = dotL + dotR;
//		}
		
		if (opts.min != null && opts.min != undefined && val < opts.min){
			$(target).val(opts.min.toFixed(opts.precision));
		} else if (opts.max != null && opts.max != undefined && val > opts.max){
			$(target).val(opts.max.toFixed(opts.precision));
		} else {
			$(target).val(val);
		}
		
	}
	
	function bindEvents(target){
		$(target).unbind('.numberbox');
		//console.log($(target).attr('precision'));
		$(target).bind('keypress.numberbox', function(e){
			//alert(e.which);
			if (($(this).attr('precision') == undefined || $(this).attr('precision') == 0) && e.which == 46) return false;
			if ($(this).val().indexOf(".") != -1 && $(this).val().length - $(this).val().indexOf(".") > $(this).attr('precision')) {
				return false;
			}
			if (e.which == 45){	//-
				//只能输入一个"-"号
				if ($(this).val().indexOf("-") != -1) return false;
				//没有"-"号时,当用户输入"-"号,则在最前面添加"-"号
				else {
					$(this).val("-"+$(this).val());
				}
				//return true;
			}
			if($(this).attr('max')){//达到或者超出最大值时，鼠标选中可更改
				/*if(Number($(this).val()) >= Number($(this).attr('max'))){
					return false;
				}*/
			}
			if (e.which == 46) {	//.
				if ($(this).val().indexOf(".", $(this).val().indexOf(".")) != -1) return false;
				return true;
			}else if ((e.which >= 48 && e.which <= 57 && e.ctrlKey == false && e.shiftKey == false) || e.which == 0 || e.which == 8) {
				return true;
			} else if (e.ctrlKey == true && (e.which == 99 || e.which == 118)) {
				return true;
			}  else {
				return false;
			}
		}).bind('paste.numberbox', function(event, a){
			return true;
			//由于chrome兼容性问题，chrome不支持clipboardData，故注释下面的
//			if (window.clipboardData) {
//				var s = clipboardData.getData('text');
//				if (! /\D/.test(s)) {
//					return true;
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
		}).bind('dragenter.numberbox', function(){
			return false;
		}).bind('blur.numberbox', function(){
			fixValue(target);
		}).bind('keydown.numberbox',function(e){ 
			if(e.keyCode==13){ 
				fixValue(target); 
			} 
		});
	}
	
	/**
	 * do the validate if necessary.
	 */
	function validate(target){
		if ($.fn.validatebox){
			var opts = $.data(target, 'numberbox').options;
			$(target).validatebox(opts);
		}
	}
	
	function setDisabled(target, disabled){
		var opts = $.data(target, 'numberbox').options;
		if (disabled){
			opts.disabled = true;
			$(target).attr('disabled', true);
		} else {
			opts.disabled = false;
			$(target).removeAttr('disabled');
		}
	}
	
	$.fn.numberbox = function(options,value){
		if (typeof options == 'string'){
			switch(options){
			case 'disable':
				return this.each(function(){
					setDisabled(this, true);
				});
			case 'enable':
				return this.each(function(){
					setDisabled(this, false);
				});
			}
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'numberbox');
			if (state){
				$.extend(state.options, options);
			} else {
				var t = $(this);
				state = $.data(this, 'numberbox', {
					options: $.extend({}, $.fn.numberbox.defaults, {
						disabled: (t.attr('disabled') ? true : undefined),
						min: (t.attr('min')=='0' ? 0 : parseFloat(t.attr('min')) || undefined),
						max: (t.attr('max')=='0' ? 0 : parseFloat(t.attr('max')) || undefined),
						precision: (parseInt(t.attr('precision')) || undefined),
						numberRound : (t.attr('numberRound'))
					}, options)
				});
				t.removeAttr('disabled');
				$(this).css({imeMode:"disabled"});
			}
			//Base.setValue()时,设值
			if(value !== undefined){
				$(this).val(value);
			}
			setDisabled(this, state.options.disabled);
			fixValue(this);
			bindEvents(this);
			validate(this);
		});
	};
	
	$.fn.numberbox.defaults = {
		disabled: false,
		min: null,
		max: null,
		precision: 0,
		numberRound : 'true'
	};
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
		taspinner : taspinner
	});
	function taspinner(documentDIV, options) {
		var self = this;
		options = $.extend({
			maxValue : 9999999, /* 最大值 */
			minValue : -9999999, /* 最小值 */
			defValue : 1, /* 默认值 */
			addValue : 1,/* 默认增量 */
			txtWidth : 100,/* 文本框大小 */
			readOnly : true,
			txtHeight : 20,/* 文本框高度 */
			txtId : "xx",
			notUse : "false",
			txtName : "dto['xx']",
			defaultSrc : Base.globvar.contextPath
					+ "/ta/resource/themes/base/spinner/images/" /* 默认图片保存路径 */
		}, options || {});
		defaultSrc1 = Base.globvar.contextPath
				+ "/ta/resource/themes/base/spinner/images/" /* 默认图片保存路径 */
		function init() {
			$topDIV = $(documentDIV);
			// id='" + options.txtId + "'
			var strDIV = "<input type='text' name=" + '"' + options.txtName
					+ '"' + " class='textinput spinner' " + "/>"
					+ "<img src='" + defaultSrc1 + "spin-button.png' />";
			$topDIV.append($(strDIV)); // 将input和img添加到父div中
			var $text = $topDIV.find("input");// text标签
			if (options.readOnly == true) {
				$text.attr("readonly", true);
			} else {
				if (options.readOnly == false) {
					$text.attr("readonly", false);
				}
			}
			$text.keyup(function() { // keyup事件处理
				var localVar = parseFloat($text.attr("value")); // 得到当前文本框中的值
				if (isNaN(localVar) || localVar < options.minValue) {
					$text.attr("value", options.defValue);
				} else {
					if (localVar > options.maxValue) {
						$text.attr("value", options.maxValue);
					} else {
						$text.attr("value", localVar);
					}
				}
			}).bind("paste", function() { // CTR+V事件处理
				$(this).val($(this).val().replace(/\D|^0/g, ''));
			}).css("ime-mode", "disabled");
			var $img = $topDIV.find("img:first");// img标签
			$img.height(21).width(20).addClass("imgspinner");

			$text.attr("value", options.defValue);
			$text.unbind("change");
			$text.bind("change", function() {// 添加change事件，防止用户手动更改为非法数据，并恢复为默认值
				var localVar = parseFloat($text.attr("value")); // 得到当前文本框中的值
				if (isNaN(localVar) || localVar > options.maxValue
						|| localVar < options.minValue) { // 检验是否是非法数据,是否大于最大值后小于最小值，如果转换失败就恢复默认值
					$text.attr("value", options.defValue);
				} else {
					$text.attr("value", options.defValue);
				}
			});
			$img.hover(function() {
				$(this).addClass("_handHover");
			}, function() {
				$(this).removeClass("_handHover");
			});
			$img.unbind("click");
			if (options.notUse == "true"){
				spinnerImgClick(options.txtId,false);
			}else{
				if (options.notUse == "false"){
					spinnerImgClick(options.txtId,true);
				}
			}
			return self;
		}// end init
		function spinnerImgClick(id,enable) {
			var $text = $("#"+id+"_divtop").find("input");// text标签
            var $img=$("#"+id+"_divtop").find("img:first");
			if(enable==true){
				$text.removeClass();
				$text.addClass("textinput spinner");
				$img
				.bind(
						"click",
						function(event) {
							if (!event) {
								event = window.event;
							}
							// 这里可以得到鼠标Y坐标
							var pointY = event.pageY;// var pointX =
							// event.pageX;
							var topY = $(this).offset().top;// var
							// leftX=$(this).offset().left;
							var sizeY = $(this).attr("height");
							var localVar = parseFloat($text
									.attr("value"));// 得到当前文本框中的值
							if (isNaN(localVar)) {// 如果转换失败就恢复默认值
								$text.attr("value", options.defValue);
							} else {
								if (2 * (pointY - topY) > sizeY) {// 点击在下方
									$img.attr("src", options.defaultSrc
											+ "spin-down.png");
									if (localVar - options.addValue >= options.minValue) {
										$text.attr("value", FloatSub(
												localVar,
												options.addValue));
									} else {
										if (localVar - options.addValue < options.minValue) {
											$text.attr("value",
													options.minValue);
										}
									}
								} else {
									$img.attr("src", options.defaultSrc
											+ "spin-up.png");
									if (localVar + options.addValue <= options.maxValue) {
										$text.attr("value", FloatAdd(
												localVar,
												options.addValue));
									} else {
										if ((localVar + options.addValue) > options.maxValue) {
											$text.attr("value",
													options.maxValue);
										}
									}
								}
								setTimeout(function() {// 点击后恢复默认图片
									$img.attr("src", options.defaultSrc
											+ "spin-button.png");
								}, 200);
							}
						});
			}else{
				$text.removeClass();
				$text.attr("readonly", true);
				$text.addClass("textinput readonly spinner");
				$img.unbind(); 
			}
		}
		// 精确浮点数加运算
		function FloatAdd(arg1, arg2) {
			var r1, r2, m;
			try {
				r1 = arg1.toString().split(".")[1].length
			} catch (e) {
				r1 = 0
			}
			try {
				r2 = arg2.toString().split(".")[1].length
			} catch (e) {
				r2 = 0
			}
			m = Math.pow(10, Math.max(r1, r2))
			return (arg1 * m + arg2 * m) / m;
		}
		// 精确浮点数减法运算
		function FloatSub(arg1, arg2) {
			var r1, r2, m, n;
			try {
				r1 = arg1.toString().split(".")[1].length
			} catch (e) {
				r1 = 0
			}
			try {
				r2 = arg2.toString().split(".")[1].length
			} catch (e) {
				r2 = 0
			}
			m = Math.pow(10, Math.max(r1, r2));
			// 动态控制精度长度
			n = (r1 >= r2) ? r1 : r2;
			return ((arg1 * m - arg2 * m) / m).toFixed(n);
		}
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taspinner',// 将方法注册为公共方法
			"spinnerImgClick" : spinnerImgClick
		});

	}
}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	 	 $.extend(true, window, {
	        SelectGridEditor: SelectGrid
	    });
	    /**
	     * 构造函数
	     * @param id
	     * @param columns
	     * @param data
	     * @param options
	     */
		function SelectGrid(container, columns, data, options, callback, url) {
			//options属性
			var defaults = {
				gridWidth : 400, //默认grid的宽度
				gridHeight : 100,
				onEnter : false,
				loadFormUrl : false,
				minChar : 5,
				paramName : "dto['id']",
				grid : {haveSn :true}
			} 
			var self = this;
			//容器变量
			var $container = null;
			var $input = null;  //描述框
			var $inputHide = null; //key
			var $gridDiv = null; //装表格的div
			
			var grid = null; //表格
			var keyColumn = null;
			var descColumn = null;
			var hiddenColumn = [];
			var filterColumn = []
			
			//控制变量
			var isHidden = true; //是否显示下拉框
			
			function init() {
				options = $.extend({},defaults,options);
				//初始化组件
				$container = container;//主容器
				
				$container.empty();
				
				$inputHide = $("<input/>").attr("type", "hidden");//初始化隐藏框
				$input = $("<input/>").attr("type", "text").addClass("textinput").css("height","100%").css("width", "100%");//初始化描述框
				$gridDiv = $("<div/>").addClass("slick-selectGrid");
					
				//$gridDiv
				$inputHide.appendTo($container);
				$input.appendTo($container);
				$gridDiv.appendTo($container).hide();
				
				//方法
				_createGrid();//创建表格
				getKeyDescFilterColumns(); //获得KeyDescFilter基本信息
				for(field in hiddenColumn){
					grid.setColumnHidden(hiddenColumn[field]);//设置隐藏列信息
				}//设置隐藏列信息
				if (!options.loadFormUrl) {
					grid.getData().setFilter(filter);
					$input.keydown(onKey).keyup(function(event) {
							if (event.which != 38 && event.which != 40) {
								grid.getData().refresh();
							}
					});//注册事件
				} 

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
                    	grid.navigateUp(true);
                	focusEvent(e);
                } //判断向上
                else if (e.keyCode == 40) {
                	show(true);
                	if (grid.getSelectRowsDataToObj().length === 0) {
		            	grid.setActiveCell(0,0);
						grid.setSelectedRows([0]);
		            } else{
		            	//focusEvent(e);
                   		grid.navigateDown(true);
		            }
                   	focusEvent(e);
                } //判断向下
                else if (e.keyCode == 13) {
                	if (grid.getSelectRowsDataToObj().length === 0) {
		            	grid.setActiveCell(0,0);
						grid.setSelectedRows([0]);
		            } //13时判断是否有选择，否则选择第一条
                	focusEvent(e);
                	if (!isHidden){//如果没有隐藏下拉框 
                		//callback(self, 189, grid.getData().getItemById(grid.getSelectedRowsById()[0]));
                		if (options.onSelect) options.onSelect(grid.getData().getItemById(grid.getSelectedRowsById()[0]));
                		setData(grid.getData().getItemById(grid.getSelectedRowsById()[0]));
                	}
                	if (isHidden) {
                		if (options.onEnter) options.onEnter(self);
//                		var next = Base._getNextFormField(id + "_desc");
//                		if (next) $(next).focus();
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
                	callback(self, 189)
                } else if ($input.val().indexOf(".")==0 || event.keyCode==110 || event.keyCode==190) {
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
				var zz = new RegExp("^" + $input.val());
				var value = null;
				for (var i = 0; i < filterColumn.length; i ++) {
					value = item[filterColumn[i]]
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
						hiddenColumn.push(clmn.field);
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
                	var $parent = $input.parents(".slick-viewport");
                	var viewTop = $parent.offset().top - $(window).scrollTop();
                	if ($parent.height() - ($input.offset().top - viewTop) - 27 < defaults.gridHeight) {
                		var nowTop = (-Number(defaults.gridHeight)-5);
                		$gridDiv.css("top", nowTop + "px");
                		$gridDiv.removeClass("slick-selectGrid-down");
                		$gridDiv.addClass("slick-selectGrid-up");
                	} else {
                		$gridDiv.removeClass("slick-selectGrid-up");
                		$gridDiv.addClass("slick-selectGrid-down");
                	}
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
//					if(srcobj && srcobj.id == $input.attr('id')){
//						return ;
//					}
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
				if (options.onSelect) options.onSelect(data);
				if (typeof callback == "function") callback(self,null,data);
				$gridDiv.hide();
				isHidden = true;
				grid.clearDirty();
			}
			/**
			 * 创建表格
			 */
			function _createGrid() {
				var optionsGrid = options.grid
				var $gridView = $("<div/>").css("height", options.gridHeight).css("width", options.gridWidth).appendTo($gridDiv).resizable({ handles: 'e',minWidth:50});
				grid = new Slick.Grid($gridView, data, columns, optionsGrid);
				grid.onClick.subscribe(clickGrid);
			}
			
			function getInput() {
				return $input;
			}
			init();
			
			$.extend(this, {
				"_grid"		  : grid,
				"getInput"	  : getInput,
				"setDescData" : setDescData,
				"getDescData" : getDescData,
				"getKeyData"  : getKeyData,
				"getGridData" : getGridData,
				"setData"	  : setData,
				"clearData"	  : clearData
			})
		}
	}));
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
						$row.css("paddingLeft", 13 * (data2.level - 1)).attr(
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
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","ztree.excheck.min", "ztree.exedit", "ztree.exhide.min" ], factory);
	} else {
		factory(jQuery);
	}
}(function($){
    $.extend(true, window, {
        SelectTree:SelectTree
    }); 
    function SelectTree(id,targetDESC,treeId, options) {
        var defaults = {
            async: true,
            asyncParam:['id'],
            nameKey:'name',
            idKey:'id',
            parentKey:'pId',
            nodesData:''
        };
        var $selectTree_ul = $("<ul class='ztree'></ul>"),$selectTree_container;
        if(options.cssStyle){
        	$selectTree_container = $("<div style=\""+options.cssStyle+"\"></div>");
        }else{
        	$selectTree_container = $("<div></div>");
        }
        var selectTree_container_id = id;
        function init() {
            options = $.extend({},defaults,options);
            if(targetDESC != "" && targetDESC != undefined && treeId != "" && treeId != undefined){
            	$selectTree_ul.attr('id',treeId);
            	$selectTree_container.attr('id',selectTree_container_id).addClass('selectTreeContainer ffb_163').append($selectTree_ul);
            	if(options.cssClass){
            		$selectTree_container.addClass(options.cssClass);
            	}
            	if(options.height){
            		$selectTree_container.css('height',options.height);
            	}
            	if(options.width){
            		$selectTree_container.css('width',options.width);
            	}
//            	$('#'+targetDESC).after($selectTree_container);
            	$("body").append($selectTree_container);
            }
            var view = {
        		selectedMulti:false,
				autoCancelSelected:false
			};
            if(options.fontCss){
            	view.fontCss = options.fontCss;
            }
            _createSelectTree(view);
            return this;
        }
        function _createSelectTree(view) {
        	var setting = {
					view:view,
					async:{
						url:options.url,
						autoParam:options.asyncParam,
					  	enable:options.async
					},
					data:{
						keep:{
							parent:false,
							leaf:false
						},
						key:{
							children:"children",
							name:options.nameKey
						},
						simpleData:{
							enable:true,
							idKey:options.idKey,
							pIdKey:options.parentKey,
							rootPId:""
						}
					},
					callback:{
						beforeClick:options.selectTreeBeforeClick,
						onClick:options.selectTreeCallback,
						"end":null
					}
				};
			var nodesData = options.nodesData;
			$.fn.zTree.init($("#"+treeId), setting, nodesData);
        }
        $.extend(this, { 
        	"cmptype":'selectTree'
        });
        init();
    }
}));


(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
    $.extend(true, window, {
    	AjaxUpload:AjaxUpload
    }); 
    /**
     * Attaches event to a dom element.
     * @param {Element} el
     * @param type event name
     * @param fn callback This refers to the passed element
     */
    function addEvent(el, type, fn){
        if (el.addEventListener) {
            el.addEventListener(type, fn, false);
        } else if (el.attachEvent) {
            el.attachEvent('on' + type, function(){
                fn.call(el);
	        });
	    } else {
            throw new Error('not supported or DOM not loaded');
        }
    }   
    
    /**
     * Attaches resize event to a window, limiting
     * number of event fired. Fires only when encounteres
     * delay of 100 after series of events.
     * 
     * Some browsers fire event multiple times when resizing
     * http://www.quirksmode.org/dom/events/resize.html
     * 
     * @param fn callback This refers to the passed element
     */
    function addResizeEvent(fn){
        var timeout;
               
	    addEvent(window, 'resize', function(){
            if (timeout){
                clearTimeout(timeout);
            }
            timeout = setTimeout(fn, 100);                        
        });
    }    
    
    // Needs more testing, will be rewriten for next version        
    // getOffset function copied from jQuery lib (http://jquery.com/)
    if (document.documentElement.getBoundingClientRect){
        // Get Offset using getBoundingClientRect
        // http://ejohn.org/blog/getboundingclientrect-is-awesome/
        var getOffset = function(el){
            var box = el.getBoundingClientRect();
            var doc = el.ownerDocument;
            var body = doc.body;
            var docElem = doc.documentElement; // for ie 
            var clientTop = docElem.clientTop || body.clientTop || 0;
            var clientLeft = docElem.clientLeft || body.clientLeft || 0;
             
            // In Internet Explorer 7 getBoundingClientRect property is treated as physical,
            // while others are logical. Make all logical, like in IE8.	
            var zoom = 1;            
            if (body.getBoundingClientRect) {
                var bound = body.getBoundingClientRect();
                zoom = (bound.right - bound.left) / body.clientWidth;
            }
            
            if (zoom > 1) {
                clientTop = 0;
                clientLeft = 0;
            }
            
            var top = box.top / zoom + (window.pageYOffset || docElem && docElem.scrollTop / zoom || body.scrollTop / zoom) - clientTop, left = box.left / zoom + (window.pageXOffset || docElem && docElem.scrollLeft / zoom || body.scrollLeft / zoom) - clientLeft;
            
            return {
                top: top,
                left: left
            };
        };        
    } else {
        // Get offset adding all offsets 
        var getOffset = function(el){
            var top = 0, left = 0;
            do {
                top += el.offsetTop || 0;
                left += el.offsetLeft || 0;
                el = el.offsetParent;
            } while (el);
            
            return {
                left: left,
                top: top
            };
        };
    }
    
    /**
     * Returns left, top, right and bottom properties describing the border-box,
     * in pixels, with the top-left relative to the body
     * @param {Element} el
     * @return {Object} Contains left, top, right,bottom
     */
    function getBox(el){
        var left, right, top, bottom;
        var offset = getOffset(el);
        left = offset.left;
        top = offset.top;
        
        right = left + el.offsetWidth;
        bottom = top + el.offsetHeight;
        
        return {
            left: left,
            right: right,
            top: top,
            bottom: bottom
        };
    }
    
    /**
     * Helper that takes object literal
     * and add all properties to element.style
     * @param {Element} el
     * @param {Object} styles
     */
    function addStyles(el, styles){
        for (var name in styles) {
            if (styles.hasOwnProperty(name)) {
                el.style[name] = styles[name];
            }
        }
    }
        
    /**
     * Function places an absolutely positioned
     * element on top of the specified element
     * copying position and dimentions.
     * @param {Element} from
     * @param {Element} to
     */    
    function copyLayout(from, to){
	    var box = getBox(from);
        
        addStyles(to, {
	        position: 'absolute',                    
	        left : box.left + 'px',
	        top : box.top + 'px',
	        width : from.offsetWidth + 'px',
	        height : from.offsetHeight + 'px'
	    });        
    }

    /**
    * Creates and returns element from html chunk
    * Uses innerHTML to create an element
    */
    var toElement = (function(){
        var div = document.createElement('div');
        return function(html){
            div.innerHTML = html;
            var el = div.firstChild;
            return div.removeChild(el);
        };
    })();
            
    /**
     * Function generates unique id
     * @return unique id 
     */
    var getUID = (function(){
        var id = 0;
        return function(){
            return 'ValumsAjaxUpload' + id++;
        };
    })();        
 
    /**
     * Get file name from path
     * @param {String} file path to file
     * @return filename
     */  
    function fileFromPath(file){
        return file.replace(/.*(\/|\\)/, "");
    }
    
    /**
     * Get file extension lowercase
     * @param {String} file name
     * @return file extenstion
     */    
    function getExt(file){
        return (-1 !== file.indexOf('.')) ? file.replace(/.*[.]/, '') : '';
    }

    function hasClass(el, name){        
        var re = new RegExp('\\b' + name + '\\b');        
        return re.test(el.className);
    }    
    function addClass(el, name){
        if ( ! hasClass(el, name)){   
            el.className += ' ' + name;
        }
    }    
    function removeClass(el, name){
        var re = new RegExp('\\b' + name + '\\b');                
        el.className = el.className.replace(re, '');        
    }
    
    function removeNode(el){
        el.parentNode.removeChild(el);
    }

    /**
     * Easy styling and uploading
     * @constructor
     * @param button An element you want convert to 
     * upload button. Tested dimentions up to 500x500px
     * @param {Object} options See defaults below.
     */
     function AjaxUpload(button, options){
        this._settings = {
            // Location of the server-side upload script
            action: '#',
            // File upload name
            name: 'file',
            // Select & upload multiple files at once FF3.6+, Chrome 4+
            multiple: false,
            // Additional data to send
            data: {},
            // submitids
            submitIds : false,
            // Submit file as soon as it's selected
            autoSubmit: true,
            // The type of data that you're expecting back from the server.
            // html and xml are detected automatically.
            // Only useful when you are using json data as a response.
            // Set to "json" in that case. 
            responseType: "json",
            // Class applied to button when mouse is hovered
            hoverClass: 'hover',
            // Class applied to button when button is focused
            focusClass: 'focus',
            // Class applied to button when AU is disabled
            disabledClass: 'disabled',
            // When user selects a file, useful with autoSubmit disabled
            // You can return false to cancel upload			
            onChange: function(file, extension){
            },
            // Callback to fire before file is uploaded
            // You can return false to cancel upload
            onSubmit: function(file, extension){
            },
            // Fired when file upload is completed
            // WARNING! DO NOT USE "FALSE" STRING AS A RESPONSE!
            onComplete: function(file, response){
            }
        };
        this.id = button;
        // Merge the users options with our defaults
        for (var i in options) {
            if (options.hasOwnProperty(i)){
                this._settings[i] = options[i];
            }
        }
                
        // button isn't necessary a dom element
        if (button.jquery){
            // jQuery object was passed
            button = button[0];
        } else if (typeof button == "string") {
            if (/^#.*/.test(button)){
                // If jQuery user passes #elementId don't break it					
                button = button.slice(1);                
            }
            
            button = document.getElementById(button);
        }
        
        if ( ! button || button.nodeType !== 1){
            throw new Error("Please make sure that you're passing a valid element"); 
        }
                
        if ( button.nodeName.toUpperCase() == 'A'){
            // disable link                       
            addEvent(button, 'click', function(e){
                if (e && e.preventDefault){
                    e.preventDefault();
                } else if (window.event){
                    window.event.returnValue = false;
                }
            });
        }
                    
        // DOM element
        this._button = button;        
        // DOM element                 
        this._input = null;
        // If disabled clicking on button won't do anything
        this._disabled = false;
        
        // if the button was disabled before refresh if will remain
        // disabled in FireFox, let's fix it
        this.enable();        
        
        this._rerouteClicks();
    };
    
    // assigning methods to our class
    AjaxUpload.prototype = {
        setData: function(data){
            this._settings.data = data;
        },
        disable: function(){            
            addClass(this._button, this._settings.disabledClass);
            this._disabled = true;
            
            var nodeName = this._button.nodeName.toUpperCase();            
            if (nodeName == 'INPUT' || nodeName == 'BUTTON'){
                this._button.setAttribute('disabled', 'disabled');
            }            
            
            // hide input
            if (this._input){
                if (this._input.parentNode) {
                    // We use visibility instead of display to fix problem with Safari 4
                    // The problem is that the value of input doesn't change if it 
                    // has display none when user selects a file
                    this._input.parentNode.style.visibility = 'hidden';
                }
            }
        },
        enable: function(){
            removeClass(this._button, this._settings.disabledClass);
            this._button.removeAttribute('disabled');
            this._disabled = false;
            
        },
        /**
         * Creates invisible file input 
         * that will hover above the button
         * <div><input type='file' /></div>
         */
        _createInput: function(){ 
            var self = this;
                        
            var input = document.createElement("input");
            input.setAttribute('type', 'file');
            input.setAttribute('id', this.id);
            input.setAttribute('name', this._settings.name);
            if(this._settings.multiple) input.setAttribute('multiple', 'multiple');
            
            addStyles(input, {
                'position' : 'absolute',
                // in Opera only 'browse' button
                // is clickable and it is located at
                // the right side of the input
                'right' : 0,
                'margin' : 0,
                'padding' : 0,
             //   'fontSize' : '480px',
                // in Firefox if font-family is set to
                // 'inherit' the input doesn't work
                'fontFamily' : 'sans-serif',
                'cursor' : 'pointer',
                'height' : '100%'
            });            
            var div = document.createElement("div");                        
            addStyles(div, {
                'display' : 'block',
                'position' : 'absolute',
                'overflow' : 'hidden',
                'margin' : 0,
                'padding' : 0,                
                'opacity' : 0,
                // Make sure browse button is in the right side
                // in Internet Explorer
                'direction' : 'ltr',
                //Max zIndex supported by Opera 9.0-9.2
                'zIndex': 2147483583
            });
            
            // Make sure that element opacity exists.
            // Otherwise use IE filter            
            if ( div.style.opacity !== "0") {
                if (typeof(div.filters) == 'undefined'){
                    throw new Error('Opacity not supported by the browser');
                }
                div.style.filter = "alpha(opacity=0)";
            }            
            
            addEvent(input, 'change', function(){
                 
                if ( ! input || input.value === ''){                
                    return;                
                }
                            
                // Get filename from input, required                
                // as some browsers have path instead of it          
                var file = fileFromPath(input.value);
                                
                if (false === self._settings.onChange.call(self, file, getExt(file))){
                    self._clearInput();                
                    return;
                }
                
                // Submit form when value is changed
                if (self._settings.autoSubmit) {
                    self.submit();
                }
            });            

            addEvent(input, 'mouseover', function(){
                addClass(self._button, self._settings.hoverClass);
            });
            
            addEvent(input, 'mouseout', function(){
                removeClass(self._button, self._settings.hoverClass);
                removeClass(self._button, self._settings.focusClass);
                
                if (input.parentNode) {
                    // We use visibility instead of display to fix problem with Safari 4
                    // The problem is that the value of input doesn't change if it 
                    // has display none when user selects a file
                    input.parentNode.style.visibility = 'hidden';
                }
            });   
                        
            addEvent(input, 'focus', function(){
                addClass(self._button, self._settings.focusClass);
            });
            
            addEvent(input, 'blur', function(){
                removeClass(self._button, self._settings.focusClass);
            });

            input.height = div.height;
	        div.appendChild(input);
            document.body.appendChild(div);
            this._input = input;
        },
        _clearInput : function(){
            if (!this._input){
                return;
            }            
                             
            // this._input.value = ''; Doesn't work in IE6                               
            removeNode(this._input.parentNode);
            this._input = null;                                                                   
            this._createInput();
            
            removeClass(this._button, this._settings.hoverClass);
            removeClass(this._button, this._settings.focusClass);
        },
        /**
         * Function makes sure that when user clicks upload button,
         * the this._input is clicked instead
         */
        _rerouteClicks: function(){
            var self = this;
            
            // IE will later display 'access denied' error
            // if you use using self._input.click()
            // other browsers just ignore click()

            addEvent(self._button, 'mouseover', function(){
                if (self._disabled){
                    return;
                }
                                
                if ( ! self._input){
	                self._createInput();
                }
                
                var div = self._input.parentNode;                            
                copyLayout(self._button, div);
                div.style.visibility = 'visible';
                                
            });
            
            
            // commented because we now hide input on mouseleave
            /**
             * When the window is resized the elements 
             * can be misaligned if button position depends
             * on window size
             */
            //addResizeEvent(function(){
            //    if (self._input){
            //        copyLayout(self._button, self._input.parentNode);
            //    }
            //});            
                                         
        },
        /**
         * Creates iframe with unique name
         * @return {Element} iframe
         */
        _createIframe: function(){
            // We can't use getTime, because it sometimes return
            // same value in safari :(
            var id = getUID();            
             
            // We can't use following code as the name attribute
            // won't be properly registered in IE6, and new window
            // on form submit will open
            // var iframe = document.createElement('iframe');
            // iframe.setAttribute('name', id);                        
 
            var iframe = toElement('<iframe src="javascript:false;" name="' + id + '" />');
            // src="javascript:false; was added
            // because it possibly removes ie6 prompt 
            // "This page contains both secure and nonsecure items"
            // Anyway, it doesn't do any harm.            
            iframe.setAttribute('id', id);
            
            iframe.style.display = 'none';
            document.body.appendChild(iframe);
            
            return iframe;
        },
        /**
         * Creates form, that will be submitted to iframe
         * @param {Element} iframe Where to submit
         * @return {Element} form
         */
        _createForm: function(iframe){
            var settings = this._settings;
                        
            // We can't use the following code in IE6
            // var form = document.createElement('form');
            // form.setAttribute('method', 'post');
            // form.setAttribute('enctype', 'multipart/form-data');
            // Because in this case file won't be attached to request                    
            var form = toElement('<form method="post" enctype="multipart/form-data"></form>');
                        
            form.setAttribute('target', iframe.name);                                   
            form.style.display = 'none';
            document.body.appendChild(form);
            
            if (settings.submitIds != false && settings.submitIds != undefined) {
	            var submitIds = settings.submitIds.split(",");
	            var queryStr = _doSubmitIds(submitIds);
	            if (queryStr != undefined)
		            form.setAttribute('action', settings.action + "?" + queryStr);
		    	else 
		          	form.setAttribute('action', settings.action);
            }  else 
		          	form.setAttribute('action', settings.action);
            // Create hidden input element for each data key
//            if (submitIds != false && submitIds != undefined) {
//            	if (submitIds.indexOf(",") > 0) {
//            		var aids = submitIds.split(',');
//            		for (var i = 0 ; i < aids[i]; i ++) {
//            			if(aids[i]==null || aids[i]=='') continue;
//						var obj = Base.getObj(aids[i]);
//						if (obj == undefined) continue;
//						var el = document.createElement("input");
//						el.setAttribute('type', 'hidden');
//	                    el.setAttribute('name', $("#" + aids[i]).attr('name'));
//	                    el.setAttribute('value', $("#" + aids[i]).val());
//	                    form.appendChild(el);
//            		}
//            	} else {
//            		var obj = Base.getObj(submitIds);
//					if (obj != undefined) {
//						var el = document.createElement("input");
//						el.setAttribute('type', 'hidden');
//	                    el.setAttribute('name', $("#" + submitIds).attr('name'));
//	                    el.setAttribute('value', $("#" + submitIds).val());
//	                    form.appendChild(el);
//					}
//            	}
//            }
            for (var prop in settings.data) {
                if (settings.data.hasOwnProperty(prop)){
                    var el = document.createElement("input");
                    el.setAttribute('type', 'hidden');
                    el.setAttribute('name', prop);
                    el.setAttribute('value', settings.data[prop]);
                    form.appendChild(el);
                }
            }
            return form;
        },
        /**
         * Gets response from iframe and fires onComplete event when ready
         * @param iframe
         * @param file Filename to use in onComplete callback 
         */
        _getResponse : function(iframe, file){            
            // getting response
            var toDeleteFlag = false, self = this, settings = this._settings;   
               
            addEvent(iframe, 'load', function(){                
                
                if (// For Safari 
                    iframe.src == "javascript:'%3Chtml%3E%3C/html%3E';" ||
                    // For FF, IE
                    iframe.src == "javascript:'<html></html>';"){                                                                        
                        // First time around, do not delete.
                        // We reload to blank page, so that reloading main page
                        // does not re-submit the post.
                        
                        if (toDeleteFlag) {
                            // Fix busy state in FF3
                            setTimeout(function(){
                                removeNode(iframe);
                            }, 0);
                        }
                                                
                        return;
                }
                
                var doc = iframe.contentDocument ? iframe.contentDocument : window.frames[iframe.id].document;
                
                // fixing Opera 9.26,10.00
                if (doc.readyState && doc.readyState != 'complete') {
                   // Opera fires load event multiple times
                   // Even when the DOM is not ready yet
                   // this fix should not affect other browsers
                   return;
                }
                
                // fixing Opera 9.64
                if (doc.body && doc.body.innerHTML == "false") {
                    // In Opera 9.64 event was fired second time
                    // when body.innerHTML changed from false 
                    // to server response approx. after 1 sec
                    return;
                }
                
                var response;
                
                if (doc.XMLDocument) {
                    // response is a xml document Internet Explorer property
                    response = doc.XMLDocument;
                } else if (doc.body){
                    // response is html document or plain text
                    response = doc.body.innerHTML;
                    
                    if (settings.responseType && settings.responseType.toLowerCase() == 'json') {
                        // If the document was sent as 'application/javascript' or
                        // 'text/javascript', then the browser wraps the text in a <pre>
                        // tag and performs html encoding on the contents.  In this case,
                        // we need to pull the original text content from the text node's
                        // nodeValue property to retrieve the unmangled content.
                        // Note that IE6 only understands text/html
                        if (doc.body.firstChild && doc.body.firstChild.nodeName.toUpperCase() == 'PRE') {
                            doc.normalize();
                            response = doc.body.firstChild.firstChild.nodeValue;
                        }
                        
                        if (response) {
                            response = eval("(" + response + ")");
                        } else {
                            response = {};
                        }
                    }
                } else {
                    // response is a xml document
                    response = doc;
                }
                
                settings.onComplete.call(self, file, response);
                
                // Reload blank page, so that reloading main page
                // does not re-submit the post. Also, remember to
                // delete the frame
                toDeleteFlag = true;
                
                // Fix IE mixed content issue
                iframe.src = "javascript:'<html></html>';";
            });            
        },        
        /**
         * Upload file contained in this._input
         */
        submit: function(){                        
            var self = this, settings = this._settings;
            $($("#" + this.id).find("span")[2]).removeClass().addClass("icon-loadding") ;
            if ( ! this._input || this._input.value === ''){                
                return;                
            }
            var file = fileFromPath(this._input.value);
            //var files = fileFromPath(this._input.files);
            // user returned false to cancel upload
            if (false === settings.onSubmit.call(this, file, getExt(file))){
                this._clearInput();                
                return;
            }
            
            // sending request    
            var iframe = this._createIframe();
            var form = this._createForm(iframe);
            
            // assuming following structure
            // div -> input type='file'
            removeNode(this._input.parentNode);            
            removeClass(self._button, self._settings.hoverClass);
            removeClass(self._button, self._settings.focusClass);
                        
            form.appendChild(this._input);
                        
            form.submit();

            // request set, clean up                
            removeNode(form); form = null;                          
            removeNode(this._input); this._input = null;            
            
            // Get response from iframe and fire onComplete event when ready
            this._getResponse(iframe, file);            

            // get ready for next request            
            this._createInput();
        }
    };
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
    $.extend(true, window, {
        Ta: { 
        	autoPercentHeight: toAutoPercentHeight 
        }
    }); 
    function toAutoPercentHeight(options) {
        var defaults = {heightDiff:0};
        function init() {
            options = $.extend({},defaults,options);
            fnComputeHeight();
            $(window).resize(function() {
    			fnComputeHeight();
    		});
        }
        
        function fnComputeHeight(){
        	$("body").find("div.grid").each(function() {
    			var o = $(this);
    			var heightDiff=o.attr("heightDiff");
    			var height = o.attr("height");
    			if (height) {
    				var heightVal = 0;
    				var parent = o.parent();
    				if (height.indexOf("%") != -1) {
    					heightPercent = parseFloat(height) / 100;
    				} else {
    					heightPercent = parseFloat(height);
    				}
    				if (parent[0].tagName.toLowerCase() == "body") {
    					var h = $(window).height()-3-options.heightDiff;
    					h -= parseInt($('body').css('paddingTop'));
    					h -= parseInt($('body').css('paddingBottom'));
    					h -= parseInt($('body').css('marginTop'));
    					h -= parseInt($('body').css('marginBottom'));
    					heightVal = h * heightPercent;
    				} else if(parent[0].tagName.toLowerCase() == "div"  && parent.hasClass("ez-fl") ){
    				    heightVal = parent.parent().height() * heightPercent;
    				}else{
    				    heightVal = parent.height() * heightPercent;
    				}
    				 if(heightDiff)heightVal-=heightDiff;
    				if(heightVal<57 && $(">div.panel",o).length>0){
    				     heightVal=57;
    				}
    				o.height(heightVal);
    				$(">div.panel",o).eq(0).css({"margin":"0px"});
    				o.find('div[fit=true]').triggerHandler('_resize');
    		}
    	});
        }
        
        init();//调用初始化方法
    }
})); 


﻿if (typeof (LigerUIManagers) == "undefined") LigerUIManagers = {};
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
    ///	<param name="$" type="jQuery"></param>

    $.fn.ligerGetLayoutManager = function () {
        return LigerUIManagers[this[0].id + "_Layout"];
    };
    $.fn.ligerRemoveLayoutManager = function () {
        return this.each(function () {
            LigerUIManagers[this.id + "_Layout"] = null;
        });
    };
    $.ligerDefaults = $.ligerDefaults || {};
    $.ligerDefaults.Layout = {
        topHeight: 50,
        bottomHeight: 50,
        leftWidth: 110,
        centerWidth: 300,
        rightWidth: 170,
        InWindow : true,     //是否以窗口的高度为准 height设置为百分比时可用
        heightDiff : 0,     //高度补差
        height:'100%',      //高度
        onHeightChanged: null,
        isLeftCollapse: false,      //初始化时 左边是否隐藏
        isRightCollapse: false,     //初始化时 右边是否隐藏
        allowLeftCollapse: true,      //是否允许 左边可以隐藏
        allowRightCollapse: true,     //是否允许 右边可以隐藏
        allowLeftResize: true,      //是否允许 左边可以调整大小
        allowRightResize: true,     //是否允许 右边可以调整大小
        allowTopResize: true,      //是否允许 头部可以调整大小
        allowBottomResize: true,     //是否允许 底部可以调整大小
        space: 3, //间隔
        hasBorder:true, //设置是否显示边线 lly add
        noLeftCenterBorder:true,
        idSeed:0,
        afterClick :false //林森添加 当单击面板伸缩时点击事件
    };
    $.fn.ligerLayout = function (p) {
        this.each(function () {
            p = $.extend({ }, $.ligerDefaults.Layout, p || {});
            if (this.usedLayout) return;
            var g = {
                init: function () {
                    $("> .l-layout-left .l-layout-header,> .l-layout-right .l-layout-header", g.layout).hover(function () {
                        $(this).addClass("l-layout-header-over");
                    }, function () {
                        $(this).removeClass("l-layout-header-over");

                    });
                    $(".l-layout-header-toggle", g.layout).hover(function () {
                        $(this).addClass("l-layout-header-toggle-over");
                    }, function () {
                        $(this).removeClass("l-layout-header-toggle-over");

                    });
                    $(".l-layout-header-toggle", g.left).click(function () {
                        g.setLeftCollapse(true);
                    });
                    $(".l-layout-header-toggle", g.right).click(function () {
                        g.setRightCollapse(true);
                    });
                    //set top
                    g.middleTop = 0;
                    if (g.top) {
                        g.middleTop += g.top.height();
                        g.middleTop += parseInt(g.top.css('borderTopWidth'));
                        g.middleTop += parseInt(g.top.css('borderBottomWidth'));
                        g.middleTop += p.space;
                    }
                    if (g.left) {
                        g.left.css({ top: g.middleTop });
                        g.leftCollapse.css({ top: g.middleTop });
                    }
                    if (g.center) g.center.css({ top: g.middleTop });
                    if (g.right) {
                        g.right.css({ top: g.middleTop });
                        g.rightCollapse.css({ top: g.middleTop });
                    }
                    //set left
                    if (g.left) g.left.css({ left: 0 });
                    g.onResize();
                },
                setCollapse: function () {

                    g.leftCollapse.hover(function () {
                        $(this).addClass("l-layout-collapse-left-over");
                    }, function () {
                        $(this).removeClass("l-layout-collapse-left-over");
                    });
                    g.leftCollapse.toggle.hover(function () {
                        $(this).addClass("l-layout-collapse-left-toggle-over");
                    }, function () {
                        $(this).removeClass("l-layout-collapse-left-toggle-over");
                    });
                    g.rightCollapse.hover(function () {
                        $(this).addClass("l-layout-collapse-right-over");
                    }, function () {
                        $(this).removeClass("l-layout-collapse-right-over");
                    });
                    g.rightCollapse.toggle.hover(function () {
                        $(this).addClass("l-layout-collapse-right-toggle-over");
                    }, function () {
                        $(this).removeClass("l-layout-collapse-right-toggle-over");
                    });
                    g.leftCollapse.toggle.click(function () {
                        g.setLeftCollapse(false);
                    });
                    g.rightCollapse.toggle.click(function () {
                        g.setRightCollapse(false);
                    });
                    if (g.left && g.isLeftCollapse) {
                        g.leftCollapse.show();
                        g.leftDropHandle && g.leftDropHandle.hide();
                        g.left.hide();
                    }
                    if (g.right && g.isRightCollapse) {
                        g.rightCollapse.show();
                        g.rightDropHandle && g.rightDropHandle.hide();
                        g.right.hide();
                    }
                },
                setLeftCollapse: function (isCollapse, flag) {
                    if (!g.left) return false;
                    g.isLeftCollapse = isCollapse;
                    if (g.isLeftCollapse) {
                        g.leftCollapse.show();
                        g.leftDropHandle && g.leftDropHandle.hide();
                        g.left.hide();
                    }
                    else {
                        g.leftCollapse.hide();
                        g.leftDropHandle && g.leftDropHandle.show();
                        g.left.show();
                    }
                    g.onResize();
                    if (typeof p.afterClick == "function" && flag == undefined) p.afterClick(g, "left"); //林森修改20120910 添加点击收缩后执行方法
                },
                setRightCollapse: function (isCollapse) {
                    if (!g.right) return false;
                    g.isRightCollapse = isCollapse;
                    g.onResize();
                    if (g.isRightCollapse) {
                        g.rightCollapse.show();
                        g.rightDropHandle && g.rightDropHandle.hide();
                        g.right.hide();
                    }
                    else {
                        g.rightCollapse.hide();
                        g.rightDropHandle && g.rightDropHandle.show();
                        g.right.show();
                    }
                    g.onResize();
                    if (typeof p.afterClick == "function") p.afterClick(g,"right");//林森修改20120910 添加点击收缩后执行方法
                },
                addDropHandle: function () {
                    if (g.left && p.allowLeftResize) {
                        g.leftDropHandle = $("<div class='l-layout-drophandle-left "+(p.hasBorder?'':'l-layout-drophandle-left-border')+"'></div>");
                        g.layout.append(g.leftDropHandle);
                        g.leftDropHandle && g.leftDropHandle.show();
                        g.leftDropHandle.mousedown(function (e) {
                            g.start('leftresize', e);
                        });
                    }
                    if (g.right && p.allowRightResize) {
                        g.rightDropHandle = $("<div class='l-layout-drophandle-right "+(p.hasBorder?'':'l-layout-drophandle-right-border')+"'></div>");
                        g.layout.append(g.rightDropHandle);
                        g.rightDropHandle && g.rightDropHandle.show();
                        g.rightDropHandle.mousedown(function (e) {
                            g.start('rightresize', e);
                        });
                    }
                    if (g.top && p.allowTopResize) {
                        g.topDropHandle = $("<div class='l-layout-drophandle-top "+(p.hasBorder?'':'l-layout-drophandle-top-border')+"'></div>");
                        g.layout.append(g.topDropHandle);
                        g.topDropHandle.show();
                        g.topDropHandle.mousedown(function (e) {
                            g.start('topresize', e);
                        });
                    }
                    if (g.bottom  && p.allowBottomResize) {
                        g.bottomDropHandle = $("<div class='l-layout-drophandle-bottom "+(p.hasBorder?'':'l-layout-drophandle-bottom-border')+"'></div>");
                        g.layout.append(g.bottomDropHandle);
                        g.bottomDropHandle.show();
                        g.bottomDropHandle.mousedown(function (e) {
                            g.start('bottomresize', e);
                        });
                    }
                    g.draggingxline = $("<div class='l-layout-dragging-xline'></div>");
                    g.draggingyline = $("<div class='l-layout-dragging-yline'></div>");
                    g.layout.append(g.draggingxline).append(g.draggingyline);
                },
                setDropHandlePosition: function () {
                    if (g.leftDropHandle) {
                        g.leftDropHandle.css({ left: g.left.width() + parseInt(g.left.css('left')), height: g.middleHeight, top: g.middleTop });
                    }
                    if (g.rightDropHandle) {
                        g.rightDropHandle.css({ left: parseInt(g.right.css('left')) - p.space, height: g.middleHeight, top: g.middleTop });
                    }
                    if (g.topDropHandle) {
                        g.topDropHandle.css({ top: g.top.height() + parseInt(g.top.css('top')), width: g.top.width() });
                    }
                    if (g.bottomDropHandle) {
                        g.bottomDropHandle.css({ top: parseInt(g.bottom.css('top')) - p.space, width: g.bottom.width() });
                    }
                },
                onResize: function () {
                    var oldheight = g.layout.height();
                    //set layout height 
                    var h = 0;
                    var windowHeight = $(window).height();
                    var parentHeight = null;
                    if (typeof(p.height) == "string" && p.height.indexOf('%') > 0)
                    {
                        var layoutparent = g.layout.parent(); 
                        if (p.InWindow || layoutparent[0].tagName.toLowerCase() == "body") { 
                            parentHeight = windowHeight; 
                            parentHeight -= parseInt($('body').css('paddingTop'));
                            parentHeight -= parseInt($('body').css('paddingBottom'));
                        }
                        else{ 
							parentHeight = layoutparent.height();
                        }  
						var mt = layoutparent.css('marginTop').toLowerCase();
                        parentHeight -= (mt=='auto'?0:parseInt(mt));
                        var mb = layoutparent.css('marginBottom').toLowerCase();
                        parentHeight -=  (mt=='auto'?0:parseInt(mb));;
                        
                        h =  parentHeight * parseFloat(p.height) * 0.01;   
                        if(p.InWindow || layoutparent[0].tagName.toLowerCase() == "body") {
                            h -= (g.layout.offset().top - parseInt($('body').css('paddingTop')));
                        }
                    } 
                    else
                    { 
                        h = parseInt(p.height);
                    }    
                    
                    h += p.heightDiff;
                    
                    g.layout.height(h);
                    g.layoutHeight = g.layout.height();
                   
                    g.middleWidth = g.layout.width();
                    //可能有问题
                    var x = g.layout.siblings("div:visible").addBack();
                    if(x.parent().hasClass("tabs-panels")){
                    	g.layout.height(x.eq(0).height());
                        g.layoutHeight = g.layout.height();
                        g.middleWidth = x.eq(0).width();
                    }
                    
                    ///////////////////////////////////
                    //lly add
//                    if(g.middleWidth==0){
//                    	g.middleWidth = '100%';
//                    }
                    g.middleHeight = g.layout.height();
                    if (g.top) {
                        g.middleHeight -= g.top.height();
                        g.middleHeight -= parseInt(g.top.css('borderTopWidth'));
                        g.middleHeight -= parseInt(g.top.css('borderBottomWidth'));
                        g.middleHeight -= p.space;
                    }
                    if (g.bottom) {
                        g.middleHeight -= g.bottom.height();
                        g.middleHeight -= parseInt(g.bottom.css('borderTopWidth'));
                        g.middleHeight -= parseInt(g.bottom.css('borderBottomWidth'));
                        g.middleHeight -= p.space;
                    }
                    //specific
                    g.middleHeight -= 0;//2 lly 

                    if (p.onHeightChanged && g.layoutHeight != oldheight) {
                        p.onHeightChanged({ layoutHeight: g.layoutHeight, diff: g.layoutHeight - oldheight, middleHeight: g.middleHeight });
                    }

                    if (g.center) {
                        g.centerWidth = g.middleWidth;
                        if (g.left) {
                            if (g.isLeftCollapse) {
                                g.centerWidth -= g.leftCollapse.width();
                                g.centerWidth -= parseInt(g.leftCollapse.css('borderLeftWidth'));
                                g.centerWidth -= parseInt(g.leftCollapse.css('borderRightWidth'));
                                g.centerWidth -= parseInt(g.leftCollapse.css('left'));
                                g.centerWidth -= p.space;
                            }
                            else {
                                g.centerWidth -= g.leftWidth;
                                g.centerWidth -= parseInt(g.left.css('borderLeftWidth'));
                                g.centerWidth -= parseInt(g.left.css('borderRightWidth'));
                                g.centerWidth -= parseInt(g.left.css('left'));
                                g.centerWidth -= p.space;
                            }
                        }
                        if (g.right) {
                            if (g.isRightCollapse) {
                                g.centerWidth -= g.rightCollapse.width();
                                g.centerWidth -= parseInt(g.rightCollapse.css('borderLeftWidth'));
                                g.centerWidth -= parseInt(g.rightCollapse.css('borderRightWidth'));
                                g.centerWidth -= parseInt(g.rightCollapse.css('right'));
                                g.centerWidth -= p.space;
                                
                            }
                            else {
                                g.centerWidth -= g.rightWidth;
                                g.centerWidth -= parseInt(g.right.css('borderLeftWidth'));
                                g.centerWidth -= parseInt(g.right.css('borderRightWidth'));
                                
                                g.centerWidth -= p.space;
                            }
                        }
                        g.centerLeft = 0;
                        if (g.left) {
                            if (g.isLeftCollapse) {
                                g.centerLeft += g.leftCollapse.width();
                                g.centerLeft += parseInt(g.leftCollapse.css('borderLeftWidth'));
                                g.centerLeft += parseInt(g.leftCollapse.css('borderRightWidth'));
                                g.centerLeft += parseInt(g.leftCollapse.css('left'));
                                g.centerLeft += p.space;
                            }
                            else {
                                g.centerLeft += g.left.width();
                                g.centerLeft += parseInt(g.left.css('borderLeftWidth'));
                                g.centerLeft += parseInt(g.left.css('borderRightWidth'));
                                g.centerLeft += p.space;
                            }
                        }
                        g.center.css({ left: g.centerLeft });
                        g.center.width(g.centerWidth);
                        g.center.height(g.middleHeight);
                        var contentHeight = g.middleHeight;
                        if(g.center.header) contentHeight-= g.center.header.height();
                        contentHeight -= (parseInt(g.center.content.css("paddingTop")) + parseInt(g.center.content.css("paddingBottom")));
                        g.center.content.height(contentHeight);
                    }
                    if (g.left) {
                        g.leftCollapse.height(g.middleHeight);
                        
                        g.left.height(g.middleHeight);
                        //lly add
                        var contentHeight = g.middleHeight;
                        if(g.left.header) contentHeight-= g.left.header.height();
                        g.left.content.height(contentHeight);
                    }
                    if (g.right) {
                        g.rightCollapse.height(g.middleHeight);
                        g.right.height(g.middleHeight);
                        //lly add
                        var contentHeight = g.middleHeight;
                        if(g.right.header) contentHeight-= g.right.header.height();
                        g.right.content.height(contentHeight);
                        
                        //set left
                        g.rightLeft = 0;

                        if (g.left) {
                            if (g.isLeftCollapse) {
                                g.rightLeft += g.leftCollapse.width();
                                g.rightLeft += parseInt(g.leftCollapse.css('borderLeftWidth'));
                                g.rightLeft += parseInt(g.leftCollapse.css('borderRightWidth'));
                                g.rightLeft += p.space;
                            }
                            else {
                                g.rightLeft += g.left.width();
                                g.rightLeft += parseInt(g.left.css('borderLeftWidth'));
                                g.rightLeft += parseInt(g.left.css('borderRightWidth'));
                                g.rightLeft += parseInt(g.left.css('left'));
                                g.rightLeft += p.space;
                            }
                        }
                        if (g.center) {
                            g.rightLeft += g.center.width();
                            g.rightLeft += parseInt(g.center.css('borderLeftWidth'));
                            g.rightLeft += parseInt(g.center.css('borderRightWidth'));
                            g.rightLeft += p.space;
                        }
                        g.right.css({ left: g.rightLeft });
                    }
                    if (g.bottom) {
                        g.bottomTop = g.layoutHeight - g.bottom.height() - 2;
                        g.bottom.css({ top: g.bottomTop });
                    }
                    g.setDropHandlePosition();
					//对内容进行resize
                    $('>div >div.l-layout-content',g.layout).each(function(){$(this).triggerHandler('_resize');});
                    $('>div >div.l-layout-content >div[fit=true],>div >div.l-layout-content >form[fit=true]',g.layout).each(function(){$(this).triggerHandler('_resize');});
                },
                start: function (dragtype, e) {
                    g.dragtype = dragtype;
                    if (dragtype == 'leftresize' || dragtype == 'rightresize') {
                        g.xresize = { startX: e.pageX };
                        g.draggingyline.css({ left: e.pageX - g.layout.offset().left, height: g.middleHeight, top: g.middleTop }).show();
                        $('body').css('cursor', 'col-resize');
                    }
                    else if (dragtype == 'topresize' || dragtype == 'bottomresize') {
                        g.yresize = { startY: e.pageY };
                        g.draggingxline.css({ top: e.pageY - g.layout.offset().top, width: g.layout.width() }).show();
                        $('body').css('cursor', 'row-resize');
                    }
                    else {
                        return;
                    }

                    g.layout.lock.width(g.layout.width());
                    g.layout.lock.height(g.layout.height());
                    g.layout.lock.show();
                    if ($.browser.msie || $.browser.safari)  $('body').bind('selectstart', function () { return false; }); // 不能选择

                    $(document).bind('mouseup', g.stop);
                    $(document).bind('mousemove', g.drag);
                },
                drag: function (e) {
                    if (g.xresize) {
                        g.xresize.diff = e.pageX - g.xresize.startX;
                        g.draggingyline.css({ left: e.pageX - g.layout.offset().left });
                        $('body').css('cursor', 'col-resize');
                    }
                    else if (g.yresize) {
                        g.yresize.diff = e.pageY - g.yresize.startY;
                        g.draggingxline.css({ top: e.pageY - g.layout.offset().top });
                        $('body').css('cursor', 'row-resize');
                    } 
                },
                stop: function (e) {
                    if (g.xresize && g.xresize.diff != undefined) {
                        if (g.dragtype == 'leftresize') {
                            g.leftWidth += g.xresize.diff;
                            g.left.width(g.leftWidth);
                            if (g.center)
                                g.center.width(g.center.width() - g.xresize.diff).css({ left: parseInt(g.center.css('left')) + g.xresize.diff });
                            else if (g.right)
                                g.right.width(g.left.width() - g.xresize.diff).css({ left: parseInt(g.right.css('left')) + g.xresize.diff });
                        }
                        else if (g.dragtype == 'rightresize') {
                            g.rightWidth -= g.xresize.diff;
                            g.right.width(g.rightWidth).css({ left: parseInt(g.right.css('left')) + g.xresize.diff });
                            if (g.center){
                                g.center.width(g.center.width() + g.xresize.diff);
                            }
                            else if (g.left)
                                g.left.width(g.left.width() + g.xresize.diff);
                        }
                    }
                    else if (g.yresize && g.yresize.diff != undefined) {
                        if (g.dragtype == 'topresize') {
                            g.top.height(g.top.height() + g.yresize.diff);
                            g.middleTop += g.yresize.diff;
                            g.middleHeight -= g.yresize.diff;
                            if (g.left) {
                                g.left.css({ top: g.middleTop }).height(g.middleHeight);
                                g.leftCollapse.css({ top: g.middleTop }).height(g.middleHeight);
                            }
	
                            if (g.center) g.center.css({ top: g.middleTop }).height(g.middleHeight);
                           
                            if (g.right) {
                                g.right.css({ top: g.middleTop }).height(g.middleHeight);
                                g.rightCollapse.css({ top: g.middleTop }).height(g.middleHeight);
                            }
                        	g.top.content.height(g.top.height());//李从波添加，改变顶部高度的同时修改顶部内容高度
                        }
                        else if (g.dragtype == 'bottomresize') {
                            g.bottom.height(g.bottom.height() - g.yresize.diff);
                            g.middleHeight += g.yresize.diff;
                            g.bottomTop += g.yresize.diff;
                            g.bottom.css({ top: g.bottomTop });
                            if (g.left) {
                                g.left.height(g.middleHeight);
                                g.leftCollapse.height(g.middleHeight);
                            }
                            if (g.center) g.center.height(g.middleHeight);
                            if (g.right) {
                                g.right.height(g.middleHeight);
                                g.rightCollapse.height(g.middleHeight);
                            }
                        	g.bottom.content.height(g.bottom.height());//李从波添加，改变底部高度的同时修改底部内容高度
                        }
                         //lly add
                        var contentHeight = g.middleHeight;
                        if(g.left && g.left.header) {//李从波修改，添加非空判断，如果左边区域不存在则不修改内容高度
                           contentHeight-= g.left.header.height();
                   		   g.left.content.height(contentHeight);
                        }
                        contentHeight = g.middleHeight;
                   		if(g.center && g.center.header){//李从波修改，添加非空判断，如果中间区域不存在则不修改内容高度
                   		  contentHeight-= g.center.header.height();
                   		  g.center.content.height(contentHeight);
                   		}
                   		contentHeight = g.middleHeight;
                        if(g.right && g.right.header){//李从波修改，添加非空判断，如果右侧区域不存在则不修改内容高度
                        	contentHeight-= g.right.header.height();
                   		    g.right.content.height(contentHeight);
                        }
                    }
                    //lly 对子组件进行resize
//                    if($.fn.tauicmpresize)
//                    	g.layout.tauicmpresize({resizeSelf:false});
                    $('>div >div.l-layout-content',g.layout).each(function(){$(this).triggerHandler('_resize');});
			    	$('>div >div.l-layout-content >div[fit=true],>div >div.l-layout-content >form[fit=true]',g.layout).each(function(){$(this).triggerHandler('_resize');});
                    g.setDropHandlePosition();
                    g.draggingxline.hide();
                    g.draggingyline.hide();
                    g.xresize = g.yresize = g.dragtype = false;
                    g.layout.lock.hide();
                    if ($.browser.msie || $.browser.safari)
                        $('body').unbind('selectstart');
                    $(document).unbind('mousemove', g.drag);
                    $(document).unbind('mouseup', g.stop);
                    $('body').css('cursor', '');
                    if (typeof window._borderLayout_mourseup_userDefinedFn == "function") {
                    	window._borderLayout_mourseup_userDefinedFn(e,g);
                    }
                }
            };
            g.layout = $(this);
            if (!g.layout.hasClass("l-layout"))
                g.layout.addClass("l-layout");
            g.width = g.layout.width();
            //top
            if ($("> div[position=top]", g.layout).length > 0) {
            	//lly add
                g.top = $("> div[position=top]", g.layout).wrap('<div class="l-layout-top" style="top:0px;'+(p.hasBorder?'':'border:0px')+'"></div>').parent();
               // g.top = $("> div[position=top]", g.layout).wrap('<div class="l-layout-top" style="top:0px;"></div>').parent();

                g.top.content = $("> div[position=top]", g.top);
                if (!g.top.content.hasClass("l-layout-content"))
                    g.top.content.addClass("l-layout-content");
                g.topHeight = p.topHeight;
                if (g.topHeight) {
                    g.top.height(g.topHeight);
                    g.top.content.height(g.topHeight);//李从波添加，同时修改顶部内容高度
                }
            }
           
            //bottom
            if ($("> div[position=bottom]", g.layout).length > 0) {
            	//lly add
               g.bottom = $("> div[position=bottom]", g.layout).wrap('<div class="l-layout-bottom" '+(p.hasBorder?'':' style="border:0px"')+'></div>').parent();
                 //g.bottom = $("> div[position=bottom]", g.layout).wrap('<div class="l-layout-bottom"></div>').parent();
                g.bottom.content = $("> div[position=bottom]", g.bottom);
                if (!g.bottom.content.hasClass("l-layout-content"))
                    g.bottom.content.addClass("l-layout-content");

                g.bottomHeight = p.bottomHeight;
                if (g.bottomHeight) {
                    g.bottom.height(g.bottomHeight);
                }

            }
            var leftKey;
            //left
            if ($("> div[position=left]", g.layout).length > 0) {
            	//lly add
                g.left = $("> div[position=left]", g.layout).wrap('<div class="l-layout-left '+(p.noLeftCenterBorder?'noborder':'')+'" style="left:0px;'+(p.hasBorder?'':'border:0px')+'"></div>').parent();
                //g.left = $("> div[position=left]", g.layout).wrap('<div class="l-layout-left" style="left:0px;"></div>').parent();
				
                g.left.content = $("> div[position=left]", g.left);
                if(g.left.content.attr("title") && p.allowLeftCollapse){
	                g.left.header = $('<div class="l-layout-header"><div class="l-layout-header-toggle"></div><div class="l-layout-header-inner"></div></div>');
	                g.left.prepend(g.left.header);
	                g.left.header.toggle = $(".l-layout-header-toggle", g.left.header);
	                
	                if (!g.left.content.hasClass("l-layout-content"))
	                    g.left.content.addClass("l-layout-content");
	                if(!p.allowLeftCollapse) $(".l-layout-header-toggle", g.left.header).remove();
	                
	                //set title
	                var lefttitle = g.left.content.attr("title");
	                leftKey = g.left.content.attr("title");
	                if (lefttitle) {
	                    g.left.content.attr("title", "");
	                    $(".l-layout-header-inner", g.left.header).html(lefttitle);
	                }
	                
	                var leftTitleIcon = g.left.content.attr('titleIcon');
                    if(leftTitleIcon)
                    	g.left.header.addClass(centerTitleIcon) ;
                }
                //set content height
//                if(lefttitle &&  p.allowLeftCollapse){
//                	g.left.content.css('height',g.middleHeight-50);
//                }else if(lefttitle || p.allowLeftCollapse){
//               		g.left.content.css('height',g.middleHeight-25);
//                }else{
//                	g.left.content.css('height','100%');
//                }
                
                //set width
                g.leftWidth = p.leftWidth;
                if (g.leftWidth)
                    g.left.width(g.leftWidth);
            }
            //center
            if ($("> div[position=center]", g.layout).length > 0) {
            	// lly add
                g.center = $("> div[position=center]", g.layout).wrap('<div class="l-layout-center '+(p.noLeftCenterBorder?'noborder':'')+'"'+(p.hasBorder?'':' style="border:0px"')+'></div>').parent();
               // g.center = $("> div[position=center]", g.layout).wrap('<div class="l-layout-center" ></div>').parent();

                g.center.content = $("> div[position=center]", g.center);
                g.center.content.addClass("l-layout-content");
                //set title
                var centertitle = "<span>"+g.center.content.attr("title")+"</span>";
                if (g.center.content.attr("title")) {
                    g.center.content.attr("title", "");
                    g.center.header = $('<div class="l-layout-header"></div>');
                    g.center.prepend(g.center.header);
                    
                  	var centerTitleIcon = g.center.content.attr('titleIcon');
                    if(centerTitleIcon){
                    	centertitle = "<div class='"+centerTitleIcon+"' style='width:20px;height:20px;float:left;margin-top:3px'></div>"+centertitle;
                    }
                    
                    g.center.header.html(centertitle);
     
                }
                //set width
                g.centerWidth = p.centerWidth;
                if (g.centerWidth)
                    g.center.width(g.centerWidth);
            }
            //right
            if ($("> div[position=right]", g.layout).length > 0) {
            	// lly add
                g.right = $("> div[position=right]", g.layout).wrap('<div class="l-layout-right" '+(p.hasBorder?'':' style="border:0px"')+'></div>').parent();
               // g.right = $("> div[position=right]", g.layout).wrap('<div class="l-layout-right"></div>').parent();

                g.right.header = $('<div class="l-layout-header"><div class="l-layout-header-toggle"></div><div class="l-layout-header-inner"></div></div>');
                g.right.prepend(g.right.header);
                g.right.header.toggle = $(".l-layout-header-toggle", g.right.header);
                if(!p.allowRightCollapse) $(".l-layout-header-toggle", g.right.header).remove();
                g.right.content = $("> div[position=right]", g.right);
                if (!g.right.content.hasClass("l-layout-content"))
                    g.right.content.addClass("l-layout-content");

                //set title
                var righttitle = g.right.content.attr("title");
                if (righttitle) {
                    g.right.content.attr("title", "");
                    $(".l-layout-header-inner", g.right.header).html(righttitle);
                }
                //set width
                g.rightWidth = p.rightWidth;
                if (g.rightWidth)
                    g.right.width(g.rightWidth);
            }
            //lock
            g.layout.lock = $("<div class='l-layout-lock'></div>");
            g.layout.append(g.layout.lock);
            //DropHandle
            g.addDropHandle();

            //Collapse
            g.isLeftCollapse = p.isLeftCollapse;
            g.isRightCollapse = p.isRightCollapse;
            g.leftCollapse = $('<div class="l-layout-collapse-left" style="display: none; " title="点击按钮打开"><div class="l-layout-collapse-left-toggle"></div><div class="layout-left-inner"></div></div>');
            if (leftKey) {
                $(".layout-left-inner", g.leftCollapse).html(leftKey);
            }
            g.rightCollapse = $('<div class="l-layout-collapse-right" style="display: none; " title="点击按钮打开"><div class="l-layout-collapse-right-toggle"></div></div>');
            g.layout.append(g.leftCollapse).append(g.rightCollapse);
            g.leftCollapse.toggle = $("> .l-layout-collapse-left-toggle", g.leftCollapse);
            g.rightCollapse.toggle = $("> .l-layout-collapse-right-toggle", g.rightCollapse);
            g.setCollapse();

            //init
            g.init();
            
            $(window).resize(function () {
                g.onResize();
            });//2013-6-13 lins打开注释为了解决在tab页内border不会自动大小的问题
            
			g.layout.bind('_resize',function(){
//				if(g.layout[0].id=='fun22')alert('fun22');
				 g.onResize();
			});
			if(g.layout[0].tagName.toLowerCase()=='body' || g.layout[0].parentNode.tagName.toLowerCase()=='body'){
				$(window).unbind('.ligerLayout').bind('resize.ligerLayout', function(){
						g.layout.triggerHandler('_resize');
				});
			}
            
            if (this.id == undefined) this.id = "LigerUI_" +  $.ligerDefaults.Layout.idSeed++;
            LigerUIManagers[this.id + "_Layout"] = g;
            this.usedLayout = true;
        });
        if (this.length == 0) return null;
        if (this.length == 1) return LigerUIManagers[this[0].id + "_Layout"];
        var managers = [];
        this.each(function() {
            managers.push(LigerUIManagers[this.id + "_Layout"]);
        });
        return managers;
    };
}));
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	/**
	 * 撑满父容器
	 */
	function fitForm(target){
		var $form = $(target);
//		var $tparent = $form.parent();
//		var $chilren = $tparent.children('[fit=true]');
//		if ($chilren.length > 1) {
//			alert($($tparent.children('[fit=true]')[0]).height());
//			if ($.data($tparent, "fitfirst") == "true"){
//				$form.height($($tparent.children('[fit=true]')[0]).height());
//				return;
//			} else {
//				$.data($tparent, "fitfirst", "true");
//			}
//		}
		var $formparent = $form.parent(); 
        h = $formparent.height();
        if ($formparent[0].tagName.toLowerCase() == "body") { 
            h = $(window).height();
            //h -= parseInt($('body').css('paddingTop'));
            h -= parseInt($('body').css('paddingBottom'));
            //h -= parseInt($('body').css('marginTop'));
            h -= parseInt($('body').css('marginBottom'));
            h -= $form.offset().top;
        }else if($formparent.hasClass('window-body')){
        		var windowTop = parseInt($form.offsetParent().css('top'));
	        	h -= ($form.offset().top - windowTop - 24);
        }else{
        	if($formparent.css('position')=='relative' || $formparent.css('position')=='absolute'){
	        	h -= $form.position().top-parseInt($formparent.css('paddingTop'));
	        	if($formparent.parent()[0].tagName.toLowerCase() == "body"){
	        		h-=$formparent.position().top-parseInt($('body').css('paddingTop'));
	        	}
	        }else{
	        	var pall = $form.prevAll(':visible').not('#pageloading');
	        	if(pall.length>0){
	        		pall.each(function(){
	        			h -= $(this).outerHeight(true);
	        		});
	        	}
	        }
        	var mt = $formparent.css('marginTop');
	        h -= (mt=='auto'?0:parseInt(mt));
	        var mb = $formparent.css('marginBottom');
	        h -= (mb=='auto'?0:parseInt(mb));
        }
        h -= parseInt($form.css('paddingTop'));
        h -= parseInt($form.css('paddingBottom'));

        var mb = $form.css('marginBottom');
        h -= (mb=='auto'?0:parseInt(mb));
        h -= $form.css('borderTopWidth')=="medium"?0:parseInt($form.css('borderTopWidth'));
        h -= $form.css('borderBottomWidth')=="medium"?0:parseInt($form.css('borderBottomWidth'));
        var opts = $.data(target, 'fitheight');
        h -= opts.heightDiff;
        
        $form.height(h);
        if(opts.minWidth != 'auto'){
        	if($form.parent().width()<opts.minWidth){
        		$form.width(opts.minWidth);
        	}else{
        		$form.width('auto');
        	}
        }
        $('>div[fit=true],>form[fit=true]',$form).triggerHandler('_resize');
	}
	$.fn.tauifitheight = function(options, param){
		options = options || {};
		return this.each(function(){
				var opts;
				opts = $.extend({}, $.fn.tauifitheight.defaults, {
					fit:($(this).attr('fit')=='true' ? true:false),
					heightDiff: ($(this).attr('heightDiff') || 0),
					minWidth:($(this).attr('minWidth') || 'auto')
				}, options);
	    	 	
				$.data(this, 'fitheight', opts);
				if(opts.fit){
					fitForm(this);
					var $form = $(this);
					$form.bind('_resize',function(){
						fitForm(this);
					});
					if(this.parentNode.tagName.toLowerCase()=='body'){
						$(window).unbind('.tauifitheight').bind('resize.tauifitheight', function(){
								$form.triggerHandler('_resize');
						});
					}
				}
		});
	};
	$.fn.tauifitheight.defaults = {
		fit:false,
		heightDiff:0
	};
}));

(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.fn.resizable = function(options){
		function resize(e){
			var resizeData = e.data;
			var options = $.data(resizeData.target, 'resizable').options;
			if (resizeData.dir.indexOf('e') != -1) {
				var width = resizeData.startWidth + e.pageX - resizeData.startX;
				width = Math.min(
							Math.max(width, options.minWidth),
							options.maxWidth
						);
				resizeData.width = width;
			}
			if (resizeData.dir.indexOf('s') != -1) {
				var height = resizeData.startHeight + e.pageY - resizeData.startY;
				height = Math.min(
						Math.max(height, options.minHeight),
						options.maxHeight
				);
				resizeData.height = height;
			}
			if (resizeData.dir.indexOf('w') != -1) {
				resizeData.width = resizeData.startWidth - e.pageX + resizeData.startX;
				if (resizeData.width >= options.minWidth && resizeData.width <= options.maxWidth) {
					resizeData.left = resizeData.startLeft + e.pageX - resizeData.startX;
				}
			}
			if (resizeData.dir.indexOf('n') != -1) {
				resizeData.height = resizeData.startHeight - e.pageY + resizeData.startY;
				if (resizeData.height >= options.minHeight && resizeData.height <= options.maxHeight) {
					resizeData.top = resizeData.startTop + e.pageY - resizeData.startY;
				}
			}
		}
		
		function applySize(e){
			var resizeData = e.data;
			var target = resizeData.target;
			if (document.compatMode == 'CSS1Compat'){//$.boxModel == true
				$(target).css({
					width: resizeData.width - resizeData.deltaWidth,
					height: resizeData.height - resizeData.deltaHeight,
					left: resizeData.left,
					top: resizeData.top
				});
			} else {
				$(target).css({
					width: resizeData.width,
					height: resizeData.height,
					left: resizeData.left,
					top: resizeData.top
				});
			}
		}
		
		function doDown(e){
			$.data(e.data.target, 'resizable').options.onStartResize.call(e.data.target, e);
			return false;
		}
		
		function doMove(e){
			resize(e);
			if ($.data(e.data.target, 'resizable').options.onResize.call(e.data.target, e) != false){
				applySize(e);
			}
			return false;
		}
		
		function doUp(e){
			resize(e, true);
			applySize(e);
			$(document).unbind('.resizable');
			$.data(e.data.target, 'resizable').options.onStopResize.call(e.data.target, e);
			return false;
		}
		
		return this.each(function(){
			var opts = null;
			var state = $.data(this, 'resizable');
			if (state) {
				$(this).unbind('.resizable');
				opts = $.extend(state.options, options || {});
			} else {
				opts = $.extend({}, $.fn.resizable.defaults, options || {});
			}
			
			if (opts.disabled == true) {
				return;
			}
			
			$.data(this, 'resizable', {
				options: opts
			});
			
			var target = this;
			
			// bind mouse event using namespace resizable
			$(this).bind('mousemove.resizable', onMouseMove)
				   .bind('mousedown.resizable', onMouseDown);
			
			function onMouseMove(e) {
				var dir = getDirection(e);
				if (dir == '') {
					$(target).css('cursor', 'default');
				} else {
					$(target).css('cursor', dir + '-resize');
				}
			}
			
			function onMouseDown(e) {
				var dir = getDirection(e);
				if (dir == '') return;
				
				var data = {
					target: this,
					dir: dir,
					startLeft: getCssValue('left'),
					startTop: getCssValue('top'),
					left: getCssValue('left'),
					top: getCssValue('top'),
					startX: e.pageX,
					startY: e.pageY,
					startWidth: $(target).outerWidth(true),
					startHeight: $(target).outerHeight(true),
					width: $(target).outerWidth(true),
					height: $(target).outerHeight(true),
					deltaWidth: $(target).outerWidth(true) - $(target).width(),
					deltaHeight: $(target).outerHeight(true) - $(target).height()
				};
				$(document).bind('mousedown.resizable', data, doDown);
				$(document).bind('mousemove.resizable', data, doMove);
				$(document).bind('mouseup.resizable', data, doUp);
			}
			
			// get the resize direction
			function getDirection(e) {
				var dir = '';
				var offset = $(target).offset();
				var width = $(target).outerWidth(true);
				var height = $(target).outerHeight(true);
				var edge = opts.edge;
				if (e.pageY > offset.top && e.pageY < offset.top + edge) {
					dir += 'n';
				} else if (e.pageY < offset.top + height && e.pageY > offset.top + height - edge) {
					dir += 's';
				}
				if (e.pageX > offset.left && e.pageX < offset.left + edge) {
					dir += 'w';
				} else if (e.pageX < offset.left + width && e.pageX > offset.left + width - edge) {
					dir += 'e';
				}
				
				var handles = opts.handles.split(',');
				for(var i=0; i<handles.length; i++) {
					var handle = handles[i].replace(/(^\s*)|(\s*$)/g, '');
					if (handle == 'all' || handle == dir) {
						return dir;
					}
				}
				return '';
			}
			
			function getCssValue(css) {
				var val = parseInt($(target).css(css));
				if (isNaN(val)) {
					return 0;
				} else {
					return val;
				}
			}
			
		});
	};
	
	$.fn.resizable.defaults = {
			disabled:false,
			handles:'n, e, s, w, ne, se, sw, nw, all',
			minWidth: 10,
			minHeight: 10,
			maxWidth: 10000,//$(document).width(),
			maxHeight: 10000,//$(document).height(),
			edge:5,
			onStartResize: function(e){},
			onResize: function(e){},
			onStopResize: function(e){}
	};
	
}));
﻿(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", 
		        "ta.jquery.ext", 
		        "border" ,
		        "TaUIManager", 
		        "tauipanel", 
		        "fit", 
		        "resizable",
		        "validateBox", 
		        "api.datagrid",
				"api.fieldset",
				"api.forms",
				"api.panel",
				"api.print",
				"api.selectinput",
				"api.taajax",
				"api.tabs",
				"api.tree",
				"api.window",
				"numberBox",
				"menu",
				"tauitabs",
				"moneyInput",
				"autoPercentHeight",
				"hotkeys"], factory);
	} else {
		factory(jQuery);
	}
}(function ($){
    $.fn.taLayout = function (p) {
    	var $container = $(this);
    	function dolayout($element){
    		var layout=$element.attr("layout");
    		if(layout){
	    		if(layout=="border"){
		    		var layoutCfg = $element.attr('layoutCfg');
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		$element.ligerLayout(layoutCfg);
		    	}else if(layout=="tabs"){
		    		var layoutCfg = $element.attr('layoutCfg');  
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		
		    		$element.tauitabs(layoutCfg || {});
		    		
	    		}else if(layout=="accordion"){
	    			
		    		var layoutCfg = $element.attr('layoutCfg');  
		    		if(layoutCfg && layoutCfg.length>0)layoutCfg =  eval("("+layoutCfg+")");
		    		layoutCfg = $.extend({}, layoutCfg || {}, {fillSpace:	true});
		    		$element.accordion(layoutCfg || {});
		    		
		    		//TODO 改到组件里面去
		    		$(window).resize(function(){
						setTimeout(function(){
							$element.accordion('resize');
						},50);
					});
	    		}else if(layout=="column"){	//  
		    		var cols = $element.attr('cols');  
		    		if(cols && cols>=1){
						var elements = $element.children().not('#pageloading,legend,li,script');
						for(var i = 1;i <= (elements.length); i++){
							var e = $(elements[i-1]);
							if(e.attr("fit")){
								e.tauifitheight();
							}
						}
						//$element.append("<div style=\"clear:both\"></div>")
		    		}
		    	 }
		    	
	    	 }//布局结束 
	    	 //当表单对象被column容器布局的时候如果没有分列，那么fielddiv的margin就要减少
	    	 if($element.hasClass('fielddiv') && (!$element.parent().hasClass('ez-fl') && $element.parent().attr('cols')==null)){
	    	 	$element.css('margin',"6px 2px");
	    	 }
	    	 
    	 	 //创建panel
    	 	if($element.hasClass('panel') && !$element.hasClass('window')){
				$element.tauipanel();
				return;
    	 	}
    	 	if(($element.hasClass('grid') || $element.hasClass('datagrid'))&& ($element.attr('fit')=='true' || $element.hasClass('ez-fl')) && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	if($element[0].tagName.toUpperCase()=='FORM' && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	if($element[0].tagName.toUpperCase()=='TABLE' && $element.attr('fit')=='true'){
    	 		$element.tauifitheight();
    	 		return ;
    	 	}
    	 	//pengwei 对于checkboxgroup和radiogroup单击做处理，全选或取消
    	 	if($element.hasClass('checkboxgroup')){
    	 		$element.find('>label.fieldLabel').bind("click",function(){
    	 			if(!$(this).hasClass("checkAll")){
    	 				$element.find('input[type=checkbox]').not("[readonly]").not("[disabled]").each(function(){
    	 					this.checked = true;
    	 					$(this).parent().removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 				});
    	 				$(this).addClass("checkAll");
    	 			}else{
    	 				$element.find('input[type=checkbox]').not("[readonly]").not("[disabled]").each(function(){
    	 					this.checked = false;
    	 					$(this).parent().removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
    	 				});
    	 				$(this).removeClass("checkAll");
    	 			}
    	 		});
    	 		return;
    	 	}
    	 	
    	 	//pengwei 修改后的checkbox,采用切换图片方式
    	 	if($element.hasClass('ta_pw_chkbox')){
    	 		var $in = $element.find("input[type=checkbox]").eq(0);
    	 		if($in.attr("checked") == "checked"){
    	 			if($in.attr("disabled") == "disabled" || $in.attr("readOnly") == "readonly"){
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked-disabled");
    	 			}else{
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 			}	
    	 		}else{
    	 			if($in.attr("disabled") == "disabled" || $in.attr("readOnly") == "readonly")
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-uncheck-disabled");
    	 		}
    	 		
    	 		$element.bind("mousedown.checkbox",function(e){
    	 			if(e.which == 3)return;
    	 			if($in.attr("readOnly") == "readonly" || $in.attr("disabled") == "disabled")return;
    	 			
    	 			input = $element.find("input")[0];
    	 			if($element.hasClass("ta-chk-uncheck")){
    	 				$element.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
    	 				input.checked=true;
    	 			}else{
    	 				$element.removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
    	 				input.checked=false;
    	 			}
					
					var fun = $(this).attr("_onClick");
    	 			if(fun != null && fun != "")
    	 				eval(fun);
    	 		});
    	 		return;
    	 	}
    	 	
    	 	if($element.hasClass('radiogroup')){
    	 		$element.find('>label.fieldLabel').click(function(){
    	 			$element.find('input[type=radio]')
    	 				.not("[disabled]")
					    .each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .addClass("ta-radio-uncheck");
    	 		});
    	 		return;
    	 	}
    	 	
    	 	//pengwei 修改后的radio
    	 	if($element.hasClass('ta_pw_radio')){
    	 		var input = $element.find("input[type=radio]")[0];
    	 		var name = $(input).attr("name");
    	 		if(input.checked){
    	 			$("input[name=\""+name+"\"]")
	    	 			.each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .addClass("ta-radio-uncheck");
    	 			
    	 			input.checked=true;
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled"){
    	 				$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked-disabled");
    	 			}else{
    	 				$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked");
    	 			}	
    	 		}else{
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled")
    	 				$element.addClass("ta-radio-uncheck-disabled");
    	 		}
    	 		$element.bind("mousedown.radio",function(e){
    	 			if(e.which == 3)return;
    	 			if($(input).attr("readOnly") == "readonly" || $(input).attr("disabled") == "disabled")return;
    	 			$("input[name=\""+name+"\"]")
					    .each(function(){
					    	this.checked = false;
					    })
					    .parent()
					    .removeClass("ta-radio-checked")
					    .removeClass("ta-radio-checked-disabled")
					    .addClass("ta-radio-uncheck");
    	 			
    	 			input.checked = true;
    	 			$element.removeClass("ta-radio-uncheck").addClass("ta-radio-checked"); 
					//修复：readonly时，不触发onclick事件
					var fun = $(this).attr("_onClick");
    	 			if(fun != null && fun != "")
    	 				eval(fun);
    	 		});
    	 		return;
    	 	}
    	 	
    	 	
    	 	//对fieldset点击效果
//    	 	if($element[0].tagName=='FIELDSET'){
//		    	$(">legend",$element).toggle(
//		    		function(){
//						$(this).siblings().hide();
//						var _this = this;
//						setTimeout(function(){
//							$(_this.parentNode).siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
//						},100);
//					},function(){
//						$(this).siblings().show();
//						var _this = this;
//						setTimeout(function(){
//							$(_this.parentNode).siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
//						},100);
//					}
//				);
//    	 	}
    	}
    	
    	
	    var c = $container;
	    //删除body里面的script
	    //c.find("script").remove();
	    
    	dolayout(c);
    	function  bianli(d){
    		var tmp = d.children();
    		if(tmp[0]&&tmp[0].tagName == 'TEXTAREA'){
		    		tmp[0].value = tmp[0].value.replaceAll("<br>","\r\n");
		    }
    		/*if(Base.globvar.developMode){
    			if(tmp.filter('[fit=true]').length>1){
    				alert('请注意:同级容器面板只能有一个容器面板fit设置为true');
    			}
    		}*/
    		if(tmp.length>0){
    			var $fitFistEl = null;
	    		for(var i=0;i<tmp.length;i++){
	    			var tn = tmp[i].tagName && tmp[i].tagName.toLowerCase();
					if("div,body,form,fieldset,table,tbody,tr,td".indexOf(tn)==-1) {
						continue;
					} else {
						var $tmp = $(tmp[i]);
						if ($fitFistEl == null && $tmp.attr("fit") == "true") {
							$fitFistEl = $tmp;
						} else if ($tmp.attr("fit") == "true") {
							$tmp.css("height", $fitFistEl.height());
							//$tmp.removeAttr("fit");
						}
						
	    				dolayout($tmp);
					}
	    			
	    			bianli($(tmp[i]));
	    		}
    		}
    	}
    	bianli(c); 
    	
    	//插件树，表格，下拉框
		if(Ta.core.TaUICreater){
			Ta.core.TaUICreater.create();
		}
    	//init begin
		
		//延迟加载iframe里面 的内容
		/*setTimeout(function(){
			$('iframe',$container).each(function(){
					if($(this).attr('src1'))
						$(this).attr('src',$(this).attr('src1'));
			});
		},50);
		*/
		
		//init end	

		//输入框获取焦点时label变色
		var _fields = $(":input[type!=hidden]",$container);
	    _fields.not('[type=button]').hover(function(){
	    	if($(this).hasClass('ffb-input_163')){
	    		$(this.parentNode.parentNode).addClass('inputHover');
	    	}else{
	    		$(this.parentNode).addClass('inputHover');
	    	}
	    },function(){
	    	if($(this).hasClass('ffb-input_163')){
	    		$(this.parentNode.parentNode).removeClass('inputHover');
	    	}else{
	    		$(this.parentNode).removeClass('inputHover');
	    	}
	    }).focus(function(){
	    	if(Base.globvar.indexStyle == "default"){
	    		if($(this).hasClass('ffb-input_163')){
					$(this.parentNode.parentNode).addClass('inputFocus');
				}else{
					if(this.type=='checkbox'){
						$(this).next().addClass('labelFocus');
						return;
					}
//					if($(this.parentNode).hasClass("fielddiv"))return;
//					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
					else{
						$(this.parentNode).addClass('inputFocus');
					}
				}
	    	}else{
	    		$(this).addClass('inputFocusBorder');
				if($(this).hasClass('ffb-input')){
					$(this.parentNode.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
				}else{
					if(this.type=='checkbox'){
						$(this).next().addClass('labelFocus');
						return;
					}
					if($(this.parentNode).hasClass("fielddiv"))return;
					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").addClass('labelFocus');
				}
	    	}
		}).blur(function(){
			if(Base.globvar.indexStyle == "default"){
				if($(this).hasClass('ffb-input')){//下拉框
					$(this.parentNode.parentNode).removeClass('inputFocus');
				}else{				
					if(this.type=='checkbox'){
						$(this).next().removeClass('labelFocus');
						return;
					}else{
						$(this.parentNode).removeClass('inputFocus');
					}
				}
			}else{
				$(this).removeClass('inputFocusBorder');
				if($(this).hasClass('ffb-input')){//下拉框
					$(this.parentNode.parentNode.parentNode).find("label[for='"+this.id+"']").removeClass("labelFocus");
				}else{				
					if(this.type=='checkbox'){
						$(this).next().removeClass('labelFocus');
						return;
					}
					if($(this.parentNode).hasClass("fielddiv"))return;
					$(this.parentNode.parentNode).find("label[for='"+this.id+"']").removeClass("labelFocus");
				}
			}
			Ta.util.InputPositon.remove();//字数提示删除
		}).filter('[type=text],[type=textarea]').keyup(function(){
			Ta.util.InputPositon.show(this);//字数提示
		});
		
		//对text,textarea,password,checkbox,radio,button,checkboxgroup,radiogroup做一些处理
		_fields.add("div.checkboxgroup,div.radiogroup",$container).each(function(){
			//对输入框加入校验，必须在TaUICreater之后
			var _$this = $(this);
			if(_$this.attr('required') || _$this.attr('validType'))
				_$this.validatebox();
			//创建numberfield
			if(_$this.hasClass('numberfield')){
				
				_$this.numberbox();
				if(_$this.hasClass("amountfield")){//创建金额输入框
				    var t = {numberRound:_$this.attr('numberRound'),decimalPlace:_$this.attr('precision'), symbol:_$this.attr('amountPre')};
					_$this.moneyInput(t);
				}
			}
			
			//创建日期输入
			if(_$this.hasClass('datefield')){
				_$this.datetimemask(1);
			}
			//创建日期时间输入
			if(_$this.hasClass('datetimefield')){
				_$this.datetimemask(2);
			}
			//创建期号输入
			if(_$this.hasClass('issuefield')){
				_$this.datetimemask(3);
			}
			//创建年月输入
			if(_$this.hasClass('dateMonthfield')){
				_$this.datetimemask(5);
			}
			//创建年输入
			if(_$this.hasClass('dateYearfield')){
				_$this.datetimemask(6);
			}
			//创建年输入
			if(_$this.hasClass('dateFulltimefield')){
				_$this.datetimemask(7);
			}
			//如果是button添加左右键的支持
			if(this.type=='button'||this.type=="submit"){
				$(this).focus(function(){
					var _this = this;
					$(this).bind('keydown.leftright',function(event){
						if(event.keyCode==37){
							var f = Base._getPreFormField(_this.id);
							if(f && f.id)Base.focus(f.id);
						}else if(event.keyCode==39){
							var f = Base._getNextFormField(_this.id);
							if(f && f.id)Base.focus(f.id);
						}
					});
					$("span.button_span",this).addClass("button_focus");
				}).blur(function(){
					$(this).unbind('keydown.leftright');
					$("span.button_span",this).removeClass("button_focus");
				}).hover(function(){
					$("span.button_span",this).addClass("button_hover");
				},function(){
					$("span.button_span",this).removeClass("button_hover");
				});
				//对tabs下面的button不进行热键注册,此类button在jquery91.tauitabs.js中进行注册
				var parentIsTabs = _$this.parents("div[layout='tabs']").length;
				//对可见且不是disabled的按钮注册热键
				if(!_$this.is(':hidden') && !this.disabled && !(parentIsTabs > 0)){
					var _this = this;
					var hotKey = $(this).attr('hotKey');
					if(hotKey && hotKeyregister){
						hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
					}
				}
			}
			if(this.type=='checkbox' && _$this.attr('readOnly')==true){
//				$(this).bind('click.checkboxReadOnly',function(){
//					this.checked = !this.checked;
//				});
				Base._setReadOnly(this.id,true);
			}
			if(this.id){
				Ta.core.TaUIManager.register(this.id,this);
			}
		});
    };

}));
﻿/**
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
		}
		
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
			return false
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
				grid.setColumnHidden(columnId)
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
				grid.setColumnShow(columnId)
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
				Base.alert("数据量过大")
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
							head.push("\"" + a[i].name + "\"")
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
					var $input = $("<textarea/>").attr("display", "none").val(Ta.util.obj2string(row)).attr("name", "_grid_item_export_excel")
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
				options.onChecked = function() {return false};
				var $p = $("#" + gridId).parent();
				$("#" + gridId).remove();
				$("<div/>").attr("id", gridId).css("height", "100%").addClass("datagrid").attr("fit","true").appendTo($p);
				var grid_ = new Slick.Grid("#" + gridId, [], columns, options);
				Ta.core.TaUIManager.register(gridId ,grid_);
			})
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
			})
			$mediaColumn.appendTo("body").show(false);
		}
		
	}
	
}));

﻿/**
 * 容器fieldset常用方法，调用方式为Base.xxx();
 * @module Base
 * @class fieldset
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
		
		/**
		 * 收起fieldset,与slideDownFieldset执行一样的效果，当展开的时候点击收起，当收起的时候点击展开。
		 * @method slideUpFieldset
		 * @param {String} fieldsetId fieldset的Id
		 */
		function slideUpFieldset(fieldsetId){
			//TODO 与slideDownFieldset执行一样的效果
			$("#"+fieldsetId +" legend").click();
		}
		/**
		 * 拉下Fieldset,与slideUpFieldset执行一样的效果，当展开的时候点击收起，当收起的时候点击展开。
		 * @method slideDownFieldset
		 * @param {String} fieldsetId fieldset的Id
		 */
		function slideDownFieldset(fieldsetId){	
			$("#"+fieldsetId +" legend").click();
		}
		/**
		 * 给fieldset设置标题。
		 * @method setFieldsetTitle
		 * @param {String} fieldsetId fieldset的Id
		 * @param {String} title 新标题,可以包含html标签
		 */
		function setFieldsetTitle(fieldsetId,title){
			$("#"+fieldsetId +" legend").html(title);
		}
		
		return {
			slideUpFieldset : slideUpFieldset,
			slideDownFieldset : slideDownFieldset,
			setFieldsetTitle : setFieldsetTitle
		}
	}
}
));


﻿/**
 * panel表单常用方法,调用方式为Base.xxx();
 * @module Base
 * @class panel
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
		/**
		 * 收起panel
		 * @method slideUpPanel
		 * @param {String} panelId panel的id
		 */
		function slideUpPanel(panelId){
			$("#"+panelId).tauipanel('collapse');
		}
		/**
		 * 拉下panel
		 * @method slideDownPanel
		 * @param {String} panelId panel的id
		 */
		function slideDownPanel(panelId){
			$("#"+panelId).tauipanel('expand');
		}
		/**
		 * 设置panel的标题
		 * @method setPanelTitle
		 * @param {String} panelId panel的id
		 * @param {String} title panel的新标题，可以包含html标签
		 * @param {Boolean} asHtml 当title中包含了html标签，必须将asHtml设置成true，否则新标题会将html标签直接显示出来
		 */
		function setPanelTitle(panelId,title, asHtml){
			$("#"+panelId).tauipanel('setTitle',title, asHtml);
		}

		return{
			slideUpPanel : slideUpPanel,
			slideDownPanel : slideDownPanel,
			setPanelTitle : setPanelTitle
		}
		
		
	}
}));


﻿/**
 * 润乾报表打印导出方法,调用方式为Base.xxx(),调用这些方法需要保证报表是使用了Ta+3框架的润乾报表模板管理功能将报表上传到数据库中;
 * @module Base
 * @class print
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
			exportAsExcel : exportAsExcel,
			exportAsPdf : exportAsPdf,
			exportAsWord : exportAsWord,
			exportAsText : exportAsText,
			print : print,
			directPrint : directPrint,
			flashPrint : flashPrint,
			openReport : openReport,
			doPrint : doPrint,
			printForTablePage:printForTablePage
		}
		/**
		 * 导出报表为Excel
		 * <br/>var options = {};
		 * <br/>options.saveAsName = "门诊医疗卫生申请表"; 设置导出文件名
		 * <br/>options.paged = false;  设置分页，默认不分页（分页1、不分页0）
		 * <br/>options.formula = false;  设置是否导出公式，默认为否（是1、否0）
		 * <br/>options.excelFormat = "2003";  设置导出格式为2003（2003、2007、OpenXML）
		 * <br/>options.ratio = 80; 设置显示比例
		 * @method exportAsExcel
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他设置
		 * 
		 */
		function exportAsExcel(raqfilename, args, options) {
			var defaultType = {
				action : "3",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}
		/**
		 * 导出报表为PDF
		 * <br/>var options = {};
		 * <br/>options.paged = false; 设置分页，默认分页（分页1、不分页0）
		 * <br/>options.expStyle = "graph"; 设置导出文字默认为图形方式（图形graph，文本text）
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsPdf("DATA1521",args);
		 * @method exportAsPdf
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133"; args[1]= "arg3=0";
		 * @param {Object} options 其他设置
		 * 
		 */
		function exportAsPdf(raqfilename, args, options) {
			var defaultType = {
				action : "6",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * 导出报表为Word
		 * <br/>var options = {};
		 * <br/>options.columns = 2; 报表分栏数
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsWord("DATA1521",args);
		 * @method exportAsWord
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他参数
		 * 
		 */
		function exportAsWord(raqfilename, args, options) {
			var defaultType = {
				action : "7",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}
		/**
		 * 导出报表为Txt
		 * <br/>var options = {};
		 * <br/>options.saveAsName = "数据报表1521"; 
		 * <br/>Base.exportAsText("DATA1521",args);
		 * @method exportAsText
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = [];args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 其他参数
		 * 
		 */
		function exportAsText(raqfilename, args, options) {
			var defaultType = {
				action : "18",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * 预览打印
		 * @method print
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如: var args = [];args[0]= "arg1=YAE133"; args[1]= "arg3=0";
		 * @param {Object} options 保存打印设置（savePrintSetup）,大数据分页传输打印（serverPagedPrint）
		 * 和分栏（columns），如options.savePrintSetup=true
		 */
		function print(raqfilename, args, options) {
			var defaultType = {
				action : "2",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}
	
		/**
		 * 直接打印
		 * @method directPrint
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 * @param {Object} options 保存打印（savePrintSetup）,大数据分页传输打印（serverPagedPrint），选择打印机（needSelectPrinter）、打印机名称（printerName）
		 * 和分栏（columns），如options.savePrintSetup=yes 
		 */
		function directPrint(raqfilename, args, options) {
			var defaultType = {
				action : "30",
				raqid : raqfilename
			}
			Base.doPrint(defaultType, args, options);
		}

		/**
		 * flash打印
		 * @method flashPrint
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Array} args 参数数组
		 * <br/>例如:var args = []; args[0]= "arg1=YAE133";args[1]= "arg3=0";
		 */
		function flashPrint(raqfilename,args) {
			var argStr = "";
			for ( var i in args) {
				if (i > 0) {
					argStr += ";" + args[i];
				} else {
					argStr += args[i];
				}
			}
			Base.submit(null,Base.globvar.contextPath+"/runqian/queryReportAction!flashPrint.do?raq="+raqfilename+"&paramString="+argStr,null,null,false,function(data){
				var cacheId = data.fieldData.cacheId;
				if(null != cacheId){
					var o = {};
					o.ctxPath = Base.globvar.contextPath;
					o.name = "report1";
					o.reportFileName = raqfilename;
					o.cacheId = cacheId;
					rq_flashPrint(o);
				}
			});
		}

		/**
		 * 打开预览报表窗口
		 * @method openReport
		 * @param {String} raqfilename 报表标识(即文件名，无后缀)
		 * @param {Object} options 窗口的宽高，如：options.width=800,options.height=600
		 * @param {String} args 参数串key1=value1;key2=value2;key3=...
		 */
		function openReport(raqfilename,options,args){
			if(!args)args = "";
			var option = {
					name:"报表",
					width:800,
			        height:600
			};
			option = $.extend({},option,options);
			Base.openWindow(raqfilename, option.name, Base.globvar.contextPath+"/runqian/queryReportAction.do?raq="+raqfilename+"&raqParam="+args, 
					null, option.width, option.height, null, null, true);
		}

		/**
		 * 打印导出方法的入口，不要直接调用
		 * @method doPrint
		 * @private
		 * @param {Object} type
		 * @param {Array} args
		 * @param {Object} options
		 */
		function doPrint(type, args ,options) {
			//pengwei新增：判断报表名称不为空
			if(!type.raqid){
				Base.alert("报表名称不能为空!","error");
				return;
			}
			var defaultType = {
				action : "30",
				raqid :  "" 
			}
			defaultType = $.extend({}, defaultType, type);
			var defaultOptions = {
				saveAsName : defaultType.raqid,  //导出文件名称，默认去raq名称
				columns : 0,                     //是否分栏，必须和raq模板里设置的一致
				savePrintSetup : "no",           //是否保存打印设置，默认不开启
				serverPagedPrint : "no",         //大数据分页传输打印，默认不开启
				paged : false,                   //导出是否分页
				formula : false,                 //excel导出是否导出表达式
				excelFormat : "2003",            //excle导出格式，有2003、2007、OpenXML可选
				ratio : 80,                      //导出显示比例，默认80%
				expStyle : "graph",              //pdf导出格式，默认图片格式，text为文本格式
				needSelectPrinter : "no",        //直接打印是否显示打印机选择界面，默认不显示
				printerName : "no"               //直接打印指定打印机，不设置则使用默认打印机
				
			}
			var actionType = defaultType.action;
			//导出pdf默认分页
			if(actionType == "6"){
				defaultOptions.paged = true;
			}
			defaultOptions = $.extend({},defaultOptions,options);
			defaultOptions.paged = defaultOptions.paged?"1":"0";
			defaultOptions.formula = defaultOptions.formula?"1":"0";
			var raqidStr = "&raq=" + defaultType.raqid;
			var argStr = "";
			if (args != undefined) {
				argStr += "&paramString=";
			}
			for ( var i in args) {
				if (i > 0) {
					argStr += ";" + args[i];
				} else {
					argStr += args[i];
				}
			}
			if($("body").find("#printIframe").size() == 0){
			    $('<iframe id="printIframe" width="50" height="50" style="position:absolute;left:-100px;top:-100px"></iframe>"').appendTo("body");
			}
			$("#printIframe").attr("src", Base.globvar.contextPath
					+ "/runqian/queryReportAction!printReport.do?action="
					+ defaultType.action + raqidStr + argStr
					+"&saveAsName="+defaultOptions.saveAsName
					+"&paged="+defaultOptions.paged
					+"&formula="+defaultOptions.formula
					+"&excelFormat="+defaultOptions.excelFormat
					+"&expStyle="+defaultOptions.expStyle
					+"&ratio="+defaultOptions.ratio
					+"&columns="+defaultOptions.columns
					+"&savePrintSetup="+defaultOptions.savePrintSetup
					+"&serverPagedPrint="+defaultOptions.serverPagedPrint
					+"&needSelectPrinter="+defaultOptions.needSelectPrinter
					+"&printerName="+defaultOptions.printerName);
		}
		
		/**
		 * web打印
		 * @method printForTablePage
		 * @param {String} id
		 * @param {Object} options
		 */
		function printForTablePage(id,options) {
			$("#"+id).printForTablePage(options);
		}
	}
}));

﻿/**==================对下拉框的操作==============*/
/**
 * 下拉列表常用操作方法,调用方式为Base.xxx();
 * @class selectinput
 * @module Base
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
			filterSelectInput : filterSelectInput,
			setSelectInputData : setSelectInputData,
			setSelectInputDataWidthJson : setSelectInputDataWidthJson,
			loadSelectInputData : loadSelectInputData
		}
		
		/**
		 * 对下拉框的数据呈现进行过滤
		 * @method filterSelectInput
		 * @param {String} id 下拉列表的id
		 * @param {String/Array} values 需要过滤的数组 例如:"01,02" 或["01","02"]
		 * @param {Boolean} reverse 默认为false,使得下拉框只能选择values范围内的值,如果为true，那么下拉框就显示除了values以外的哪些值。
		 */
		function filterSelectInput(id,values,reverse){
			if(Ta.core.TaUIManager){
				var selectinput = Ta.core.TaUIManager.getCmp(id);
				selectinput.setDisableSelect(values,reverse);
			}
		}
		/**
		 * 给下拉框重新设置数据
		 * <br/>{
		 * <br/> results:[{id : '1', name: "林森", py :"LS"}, 
		 * <br/>		  {id : '2', name: '你好', py :"NH"}, 
		 * <br/>		  {id : '3', name: '银海', py :"YH"}],
		 * <br/> total:3
		 * <br/>}
		 * <br/>py为可选字段，设置了即可使用拼音过滤
		 * @method setSelectInputData
		 * @param {String} id 下拉框的id
		 * @param {String} value json格式的字符串，必须满足示例提供的模板
		 * 
		 */
		function setSelectInputData(id,value){
			if(Ta.core.TaUIManager){
				var selectinput = Ta.core.TaUIManager.getCmp(id);
				if(typeof value==="string"){
					value = eval("("+value+")");
				}
				selectinput.loadff(value);
			}
			
		}
		/**
		 * 给下拉框重新设置json数据
		 * <br/>数据示例：
		 * <br/>[{id : '1', name: "林森", py :"LS"}, {id : '2', name: '你好', py :"NH"}, {id : '3', name: '银海', py :"YH"}]
		 * <br/>py为可选字段，设置了即可使用拼音过滤
		 * @method setSelectInputDataWidthJson
		 * @param {String} id 下拉框的id
		 * @param {String} value json格式的字符串，必须满足示例提供的模板
		 * 
		 */
		function setSelectInputDataWidthJson(id,value){
			if(Ta.core.TaUIManager){
				var selectinput = Ta.core.TaUIManager.getCmp(id);
				   if(typeof value==="string"){
					value = eval(value);
				  }
				   if(value &&  value.length>0){
				       var  selectInputData={
						results :value,
						total : value.length
					  };
				     selectinput.setData(selectInputData);
				  }
			}
		}
		/**
		 * 通过url获取下拉框数据
		 * @method loadSelectInputData
		 * @param {String} id 下拉框的id
		 * @param {String} url action地址
		 * <br/>action  必须返回json数据
		 * <br/>即action方法必须   writeJsonToClient(json);   return null;  
		 * <br/>返回json数据示例:[{id : '1', name: '你好','py':'NH'}, {id : '2', name: '银海','py':'YH'}]
		 * <br/> py为可选字段，设置了即可使用拼音过滤
		 * @param {String} parameter 传递的参数   json格式的字符串
		 * <br/>例如{"dto['id']":"1","dto['type']":"2"}
		 */
		function loadSelectInputData(id,url,parameter){
			if(Ta.core.TaUIManager){
				var selectinput = Ta.core.TaUIManager.getCmp(id);
				var  data=Base.getJson(url,parameter);
				if(data && data.length>0){
					var  selectInputData={
						results :data,
						total : data.length
					};
					selectinput.setData(selectInputData);
				}		
			}
		}
		
	}
}));

﻿/**
 * tab组件常用方法,调用方式为Base.xxx();
 * @module Base
 * @class tabs
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
			activeTab : activeTab,
			enableTab : enableTab,
			disableTab : disableTab,
			closeTab : closeTab,
			setTabTitle : setTabTitle,
			selectTab : selectTab,
			reBuildTabFromHtml : reBuildTabFromHtml
		}
		/**
		 * 设置某一个tab页被选中
		 * @method activeTab
		 * @param {Stirng} tabid tab的id
		 */
		function activeTab(tabid){
			var tabsc = $("#"+tabid).parent().parent();
			if(tabsc.hasClass('tabs-container')){
				tabsc.tauitabs('select',{"id":tabid});
			}
		}

		/**
		 * 设置某个或某几个tab页可用
		 * @method enableTab
		 * @param {String} tabsId
		 * @param {String/Array} tabids tab页的id或id数组
		 */
		function enableTab(tabids){
			if(typeof tabids == 'string'){
				var tabsc = $("#"+tabids).parent().parent();
				if(tabsc.hasClass('tabs-container')){
					tabsc.tauitabs('enableTab',{"tabid":tabids,"enable":true});
				}
			}
			else if(jQuery.isArray(tabids)){
				for(var i=0;i<tabids.length;i++){
					var tabsc = $("#"+tabids[i]).parent().parent();
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('enableTab',{"tabid":tabids[i],"enable":true});
					}
				}
			}	
		}
		/**
		 * 设置某个或某几个tab页不可用
		 * @method disableTab
		 * @param {String/Array} tabids tab页的id或id数组
		 */
		function disableTab(tabids){
			if(typeof tabids == 'string'){
				var tabsc = $("#"+tabids).parent().parent();
				if(tabsc.hasClass('tabs-container')){
					tabsc.tauitabs('enableTab',{"tabid":tabids,"enable":false});
				}
			}
			else if(jQuery.isArray(tabids)){
				for(var i=0;i<tabids.length;i++){
					var tabsc = $("#"+tabids[i]).parent().parent();
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('enableTab',{"tabid":tabids[i],"enable":false});
					}
				}
			}
		}

		/**
		 * 关闭某个或某几个tab页
		 * @method closeTab
		 * @param {String/Array} tabids tab页的id或id数组
		 */
		function closeTab(tabids){
			if(typeof tabids == 'string'){
			 	var tabsc = $("#"+tabids).parent().parent();
				if(tabsc.hasClass('tabs-container')){
					tabsc.tauitabs('close',{"id":tabids});
				}
				
			}
			else if(jQuery.isArray(tabids)){
				for(var i=0;i<tabids.length;i++){
					var tabsc = $("#"+tabids[i]).parent().parent();
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('close',{"id":tabids[i]});
					}
				}
			}
		}
		/**
		 * 给某个tab设置标题
		 * @method setTabTitle
		 * @param {String} tabsId tabs容器的id
		 * @param {String} tabid tab的id
		 * @param {String} title 新标题
		 */
		function setTabTitle(tabid,title){
			var tabsc = $("#"+tabid).parent().parent();
			if(tabsc.hasClass('tabs-container')){
				tabsc.tauitabs('setTitle',{"tabid":tabid,"title":title});
			}
		}

		/**
		 * 设置多个tab页被选中
		 * @method selectTab
		 * @param {Array} ids tab的id集合,数组
		 */
		function selectTab(ids) {
			for(var i = 0; i<ids.length;i++){
				Base.activeTab(ids[i]);
			}
		}

		function reBuildTabFromHtml() {
			var titles = [];
			var divs = [];
			$(".tabs-container").each(function(){
				$(this).find("a span").each(function(){
					titles.push($(this).html());
				})
				$(this).find(".tabs-panels").children().each(function(){
					var tab = {};
					tab.html = $(this).html();
					tab.layout = $(this).attr("layout");
					tab.cols = $(this).attr("cols");
					divs.push(tab);
				})
				$(this).html("");
				for(var i = 0; i < titles.length; i ++) {
					var $div = $("<div>")
					.attr("id", "tab_"+i)
					.attr("title", titles[i])
					.attr("layout", divs[i].layout?divs[i].layout:"column")
					.html(divs[i].html)
					.appendTo($(this));
					if (i == 0)	$div.attr("selected", "true");
				}
			})
		}

	}
}));

﻿/**
 * tree组件常用方法,调用方式为Base.xxx();
 * @module Base
 * @class tree
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
			refleshTree : refleshTree,
			expandTree : expandTree,
			collapseTree : collapseTree,
			focusTree : focusTree,
			recreateTree : recreateTree,
			clearTreeData : clearTreeData
		}
		/**
		 * 对树指定节点id的节点进行刷新，主要针对异步树。
		 * @method refleshTree
		 * @param {String} treeId  树id
		 * @param {String} nodeId  节点id
		 */
		function refleshTree(treeId,nodeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			var parentNode = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, nodeId);
			parentNode.isParent = true;
			treeObj.reAsyncChildNodes(parentNode, "refresh");
		}

		/**
		 * 展开整棵树。
		 * @method expandTree
		 * @param {String} treeId  树id
		 */
		function expandTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.expandAll(true);
		}

		/**
		 * 折叠整棵树。
		 * @method collapseTree
		 * @param {String} treeId  树id
		 */
		function collapseTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.expandAll(false);
		}

		/**
		 * 将焦点置于树的根节点上。
		 * @method focusTree
		 * @param {String} treeId  树id
		 */
		function focusTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.selectNode(treeObj.getNodeByTId(treeId + "_1"));
		}

		/**
		 * 重构现有的树
		 * @method recreateTree
		 * @param {String} treeId  树id
		 * @param {Array} setting  setting 数组,如果使用树原有的setting,设为null
		 * @param {Array} treeData  节点数据,如果是异步获取的数据，设为null
		 */
		function recreateTree(treeId,setting,treeData){
			   var  tree= Ta.core.TaUIManager.getCmp(treeId);
			   if(!tree){
			      alert("id为"+treeId+"的树不存在!");
			      return;
			   }
			   if(setting==null){
			    setting = {
				view:{
					showIcon:tree.setting.view.showIcon,
					showLine:tree.setting.view.showLine,
					showTitle:tree.setting.view.showTitle,
					expandSpeed:tree.setting.view.expandSpeed,
					fontCss:tree.setting.view.fontCss,
					selectedMulti:tree.setting.view.selectedMulti,
					autoCancelSelected:tree.setting.view.autoCancelSelected
				},
				async:{
					url:tree.setting.async.url,
					autoParam:tree.setting.async.autoParam,
					otherParam:tree.setting.async.otherParam,
					dataFilter:tree.setting.async.dataFilter,
				  	enable:tree.setting.async.enable
				},
				check:{
					chkStyle:tree.setting.check.chkStyle,
					radioType:tree.setting.check.radioType,
					chkboxType:tree.setting.check.chkboxType,
					enable:tree.setting.check.enable
				},	
				data:{
					keep:{
						parent:tree.setting.data.keep.parent,
						leaf:tree.setting.data.keep.leaf
					},
					key:{
						checked:tree.setting.data.key.checked,
						name:tree.setting.data.key.name,
						title:tree.setting.data.key.title,
						childs:"childs"
					},
					simpleData:{
						enable:tree.setting.data.simpleData.enable,
						idKey:tree.setting.data.simpleData.idKey,
						pIdKey:tree.setting.data.simpleData.pIdKey
					}
				},
				
				edit:{
					drag:{
						isMove:tree.setting.edit.drag.isMove,
						isCopy:tree.setting.edit.drag.isCopy,
						inner:tree.setting.edit.drag.inner,
						prev:tree.setting.edit.drag.prev,
						next:tree.setting.edit.drag.next
					},
					showRenameBtn:tree.setting.edit.showRenameBtn,
					showRemoveBtn:tree.setting.edit.showRemoveBtn,
					showAddBtn:tree.setting.edit.showAddBtn,
					showAcceptBtn:tree.setting.edit.showAcceptBtn,
					renameTitle:tree.setting.edit.renameTitle,
					removeTitle:tree.setting.edit.removeTitle,
					addTitle:tree.setting.edit.addTitle,
					acceptTitle:tree.setting.edit.acceptTitle,
					enable:tree.setting.edit.enable
				},
				  
				callback:{
					beforeCheck:tree.setting.callback.beforeCheck,
					onCheck:tree.setting.callback.onCheck,
					beforeRename:tree.setting.callback.beforeRename,
					beforeRemove:tree.setting.callback.beforeRemove,
					beforeDrag:tree.setting.callback.beforeDrag,
					beforeDrop:tree.setting.callback.beforeDrop,
					onRename:tree.setting.callback.onRename,
					onRemove:tree.setting.callback.onRemove,
					onDrap:tree.setting.callback.onDrap,
					onDrop:tree.setting.callback.onDrop,
					beforeClick:tree.setting.callback.beforeClick,
					onClick:tree.setting.callback.onClick,
					beforeDblclick:tree.setting.callback.beforeDblclick,
					onDblClick:tree.setting.callback.onDblClick,
					beforeRightClick:tree.setting.callback.beforeRightClick,
					onRightClick:tree.setting.callback.onRightClick,
					beforeExpand:tree.setting.callback.beforeExpand,
					onExpand:tree.setting.callback.onExpand,
					beforeCollapse:tree.setting.callback.beforeCollapse,
					onCollapse:tree.setting.callback.onCollapse,
					beforeMouseDown:tree.setting.callback.beforeMouseDown,
					onMouseDown:tree.setting.callback.onMouseDown,
					beforeMouseUp:tree.setting.callback.beforeMouseUp,
					onMouseUp:tree.setting.callback.onMouseUp,
					onNodeCreated:tree.setting.callback.onNodeCreated,
					beforeAsync:tree.setting.callback.beforeAsync,
					onAsyncSuccess:tree.setting.callback.onAsyncSuccess,
					onAsyncError:tree.setting.callback.onAsyncError,
					beforeAdd:tree.setting.callback.beforeAdd,
					onAdd:tree.setting.callback.onAdd,
					beforeAccept:tree.setting.callback.beforeAccept,
					onAccept:tree.setting.callback.onAccept,
					"end":null
				    }
			     };
			  }
			   Ta.core.TaUIManager.unregister(treeId);
				var taTree = $.fn.zTree.init($("#"+treeId), setting, treeData);
				Ta.core.TaUIManager.register(treeId, taTree);
			}
		/**
		 * 清空指定树的数据
		 * @method clearTreeData
		 * @param {String} treeId  树id
		 */
		function clearTreeData(treeId){
			 var  tree= Ta.core.TaUIManager.getCmp(treeId);
			   if(!tree){
			      alert("id为"+treeId+"的树不存在!");
			      return;
			   }
			$.fn.zTree.init($("#"+treeId), tree.setting, null);
		}

	}
}));

﻿/**
 * 模拟窗口组件常用方法,调用方式为Base.xxx();
 * @module Base
 * @class window
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil","window"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Base:core()
    });
	
	function core(){
		return {
			openWindow : openWindow,
			openWindowWithSubmitIds : openWindowWithSubmitIds,
			closeWindow : closeWindow,
			alert : alert,
			msgTip : msgTip,
			msgTopTip : msgTopTip,
			confirm : confirm,
			prompt : prompt,
			buttonsDialog : buttonsDialog,
			sendMsgToFrame : sendMsgToFrame,
			openTabMenu : openTabMenu,
			showBoxComponent : showBoxComponent,
			wizard:wizard
		}
		/**
		 *
		 * @method  
		 * @param  {boolean} replay 是否自动播放
		 * @param {boolean} flag panel的id
		 * @param {String} name 在cookie中存放的名称，应保证唯一，最好采用当前页面的id
		 * @param {Array} data 导航内容，包含1.所要提示的dom元素的jquery对象；2.提示内容
		 * var data = [object1,object2,[..objectn]]
		 * var object1 = {};
		 * object1.id = "#id" || $("#id")
		 * object1.message = "这是你的下一步提示信息";  
		 * data:[{id:$("ul.tabs li:eq(0)"),
		        	message:"1-这是您的当前页"
		         },
		         {id:$("ul.tabs li:eq(1)"),
		          	message:"2-点击这里，您将进入一个查询页面"
		         },
		         {id:$("ul.tabs li:eq(2)"),
		          	message:"3-点击这里，您将进入一个查询页面"
		         }]
		*  
		*/
		function wizard(replay,flag,name,data){
		    $.hintTip({
				replay:replay,
				show:flag, 
				cookname:name,
				data:data
		    });
		}
		
		
		/**
		 * 打开窗口
		 * @method openWindow
		 * @param {String} id 窗口id
		 * @param {String} title 窗口标题
		 * @param {String} url aciton地址
		 * @param {map} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Number} width 宽度 不要加 px；也可设置成百分比，例如"80%"
		 * @param {Number} height 高度 不要加 px；也可设置成百分比，例如"80%"
		 * @param {Function} onLoad 窗口加载完毕回调，如果useIframe=true的话 这参数不起作用
		 * @param {Funciton} onClose 窗口关闭的时候回调
		 * @param {Boolean} useIframe 是否使用iframe的方式加载，默认为false，iframe方式会存在seesion丢失，应当避免;为true的时候，打开页面为一个完整的jsp页面
		 * @param {String} style 自定义打开窗口css样式
		 * @param {Object} myoptions window的创建参数
		 */
		function openWindow(id,title,url,parameter,width,height,onLoad,onClose,useIframe,style,myoptions){
			var $w = $("<div id=\""+id+"\" "+(style?("style=\""+style+"\""):"")+"></div>");
			$w.appendTo($("body"));
			var options = {};
			
			title ? (options.title = title):null;
			width ? (options.width = width):(options.width = 200);
			height ? (options.height = height):(options.height = 200);
			onLoad ? (options.onLoad = onLoad):null;
			
			options.modal = true;
			options.resizable = false;
			options.minimizable = false;
			options.collapsible  = false;	
			if(parameter){
				if(url.indexOf('?') != -1){
					url += "&"+jQuery.param(parameter);
				}else{
					url += "?"+jQuery.param(parameter);
				}
			}
			if(url.indexOf('?') != -1){
				url += "&_r="+Math.random();
			}else{
				url += "?_r="+Math.random();
			}
			if(url && useIframe){
				options.content = '<iframe type="window" src="'+url+'" frameborder="0" style="width:100%;height:100%"></iframe>';
			}else{
				url ? (options.href = url):null;
			}
			options.onClose =function(_onClose){
				return function(){
					if(_onClose)
						_onClose($w.attr("id"));
					//remove window里面TaUIManager对应的对象
					Ta.core.TaUIManager.removeObjInCantainer($w);
					Base.hideMask();
					$w.window('destroy');
					$w.remove();
				}
			}(onClose);
			if(myoptions)
				$.extend(options,myoptions);
			$w.window(options);
			if(!url){
				onLoad();
			}
		}
		function openWindowWithSubmitIds(id,title,url,submitIds,parameter,width,height,onLoad,onClose,useIframe,style,myoptions,type){
			var paramThis = {};
			if (parameter != undefined) paramThis = parameter; 
			var submitId = submitIds.split(",");
			for (var i = 0 ; i < submitId.length; i ++) {
				if (submitId[i] == "") continue;
				var value = Base.getValue(submitId[i]);
				if (value != undefined && value != "") {
					paramThis[submitId[i]] = value;
					paramThis["dto['" + submitId[i] + "']"] = value;
				}
			}
			switch (type) {
			case "top":
				top._child = window.self;
				top.Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			case "parent":
				parent._child = window.self;
				parent.Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			default:
				Base.openWindow(id,title,url,paramThis,width,height,onLoad,onClose,useIframe,style,myoptions);
				break;
			}
			
		}
		/**
		 * 关闭窗口
		 * @method closeWindow
		 * @param {String} id 窗口id
		 */
		function closeWindow(id){
			setTimeout(function(){	$("#"+id).window('close');}, 1);
		}
		/**
		 * 弹出提示
		 * @method alert
		 * @param {String} msg 提示的信息，可以是html
		 * @param {String} type 提示的图标，不传就无图标。type可以选择如下：success,error,warn,question
		 * @param {Function} callback 回调函数
		 */
		function alert(msg,type,callback){
			var title = "提示",image="",html="";
			switch(type){
				case 'success':
					title="成功提示";
					image = "l-dialog-image-donne";
					break;
				case 'error':
					title="失败提示";
					image = "l-dialog-image-error";
					break;
				case 'warn':
					title="警告";
					image = "l-dialog-image-warn";
					break;
				case 'question':
					title="确认";
					image = "l-dialog-image-question";
					break;
			}
			if(type){
				html = "<div><div class='l-dialog-image "+image+"'></div><div class='l-dialog-msg' >"+msg+"</div></div>";
			}else{
				html = "<div><span>"+msg+"</span><div>";		
			}
			var $w = $(html);
			$w.appendTo($("body"));
			
			$w.dialog({
				title:title,
				width:300,
				height:150,
				//top : '100px',
				modal:true,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
					if(callback)callback();
					Base.hideMask();
				},
				buttonsAlgin:'center',
				buttons:[{
					text:'确定',
					buttonHighHlight:true,
					handler:function(){
						$w.dialog('destroy');
						$w.remove();
						if(callback)callback();
						Base.hideMask();
					}
				}]
			});
			$w.find("div.panel").css("margin",0);
			$w.find('.dialog-button button:first').delay(100).focus();
		}
		/**
		 * @method msgTip
		 * @deprecated 弃用的方法
		 */
		function msgTip(target, message){
		//TODO
			var box = $(target);
			var msg = message //= $.data(target, 'validatebox').message;
			var tip = null;
			if (!tip){
				tip = $(
					'<div class="validatebox-tip">' +
						'<div class="validatebox-tip-content  ui-corner-all">' +
						'</div>' +
						'<div class="validatebox-tip-pointer">' +
						'</div>' +
					'</div>'
				).appendTo('body');
				//$.data(target, 'validatebox').tip = tip;
			}
			var $c = tip.find('.validatebox-tip-content');
			$c.html(msg);
			var cheight = $c.height();
			if(cheight==0) cheight =17;
			tip.css({
				display:'block',
				left:box.offset().left+10 ,
				top:(box.offset().top-cheight-15)
			})
			//$.data(target, 'validatebox').tip =tip;
		}

		/**
		 * 信息展示框,在页面头顶出现一悬浮框,显示信息
		 * @method msgTopTip
		 * @param {String} message 提示的信息，可以是html
		 * @param {Number} time 悬浮框持续时间,毫秒计时,如果不定义此参数或者不是数字,则默认为2s.
		 * @param {Number} width 悬浮框宽度,默认250.
		 * @param {Number} height 悬浮框高度,默认50.
		 * @param {String} style 自定义样式，例如:background:red;font-size:14px;.
		 */
		function msgTopTip(message,time,width,height,style,target){
			var tip = null,left,tempHeight = -63,temptop = 0;
			var metarget = (target==null||target=="")?"body":target;
			temptop = (target==null||target=="")?tempHeight:$(target).offset().top;
			if(time == null || isNaN(time)){
				time = 2000;
			}
			if (!tip){
				style = style==undefined?"":style;
				tip = $('<div class="windowTopMsg"></div>')
				.append('<div class="header" style="height:18px;border-bottom:1px #c6c6c6 solid;"><span class="closeTip" style="color:red;float:right;cursor:pointer">x</span></div><div class="body" style="margin-top:3px;'+style+'">'+
						message+'</div>').css('top',temptop).appendTo(metarget);
				$(".closeTip").click(function(){
					tip.animate({top:tempHeight+"px"},500);
					setTimeout(function(){
						tip.remove();
					},500)
				});
			}
			if(width !== undefined && width !== null && width !=="" && !isNaN(width)){
				left = ($("body").width()-width)/2;
				tip.width(width);
			}else{
				left = ($("body").width()-250)/2;
			}
			tip.css("left",left);
			if(height !== undefined && height !== null && height !=="" && !isNaN(height)){
				tip.height(height);
				tempHeight = -(Number(height) + 13);
			}
			tip.animate({top:temptop+10+"px"},"slow");
			setTimeout(function(){
				tip.animate({top:temptop+"px"},500);
				setTimeout(function(){
					tip.remove();
				},500)
			},time);
		}
		
		/**
		 * 确认框
		 * @method confirm
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Function} fn 
		 * <br/>示例：
		 * <br/>function (yes) { if(yes) alert('11'); });
		 * <br/>"yes"根据你的选择来取值，当你点击"确定"按钮时，yes为true；当你点击"取消"按钮时，yes为false
		 * @param {Object} options json格式，配置文字显示title, buttonOk, buttonCancel
		 * @param {String} hotKeys 字符串，两个按钮的热键,第一个为确认键,第二个为取消键,例如"o,c"表示确认键的快捷键为alt+o,取消为alt+c
		 * @param focus true或flase，为true时焦点在确认按钮上，为flase时焦点在取消按钮上 
		 */
		function confirm(msg,fn,options,hotKeys,focus){
			var $w = $("<div><div class='l-dialog-image l-dialog-image-question'></div><div style='display:block;margin:15px'>"+msg+"</div></div>");
			//var $w = $("<div style='word-break:break-all;overflow:hidden;'><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></div>");
			$w.appendTo($("body"));
			var buttonHotKeys = [];
			if(typeof hotKeys == "string"){
				buttonHotKeys = hotKeys.split(",");
			}
			$w.dialog({
				title: options && options.title? options.title:'确认提示',
				width:350,
				height:150,
				modal:true,
				closable:false,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttonsAlgin:'center',
				buttons:[{
						text: options && options.buttonOk? options.buttonOk:buttonHotKeys[0]?'确定['+buttonHotKeys[0].toUpperCase()+']':'确定',
						buttonHighHlight:true,
						handler:function(){
							$w.dialog('destroy');
							$w.remove();
							if(fn){
								fn(true);
							}
						}
					},{
						text: options && options.buttonCancel?options.buttonCancel:buttonHotKeys[1]?'取消['+buttonHotKeys[1].toUpperCase()+']':'取消',
						handler:function(){
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(false);					
						}
					}
				]
			});
			if(focus){
				$w.find('.dialog-button button:first').focus();
			}else{
				$w.find('.dialog-button button:last').focus();
			}
			//添加热键
			if(buttonHotKeys){
				var buttons = $w.find('.dialog-button button');
				if(buttonHotKeys[0]){
					if(buttonHotKeys[0].length == 1){
						hotKeyregister.add("alt+" + buttonHotKeys[0],function(){buttons.eq(0).focus();buttons.eq(0).click();return false;});
					}else if(buttonHotKeys[0].length > 1){
						hotKeyregister.add(buttonHotKeys[0],function(){buttons.eq(0).focus();buttons.eq(0).click();return false;});
					}
				}
				if(buttonHotKeys[1]){
					if(buttonHotKeys[1].length == 1){
						hotKeyregister.add("alt+" + buttonHotKeys[1],function(){buttons.eq(1).focus();buttons.eq(1).click();return false;});
					}else if(buttonHotKeys[1].length > 1){
						hotKeyregister.add(buttonHotKeys[1],function(){buttons.eq(1).focus();buttons.eq(1).click();return false;});
					}
				}
			}
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var o = e.target || e.srcElement;
					var next = $(o).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var o = e.target || e.srcElement;
					var prev = $(o).prev()[0];
					if(prev)prev.focus();
				}
			});	
			$w.find("div.panel").css("margin",0);
		}

		/**
		 * 接收输入的提示框
		 * @method prompt
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Function} fn 
		 * <br/>示例：
		 * <br/>function (yes,value) { if(yes) alert(value); });
		 * <br/>"yes"根据你的选择来取值，当你点击"确定"按钮时，yes为true；当你点击"取消"按钮时，yes为false。value是你输入的值
		 * @param {String} initValue 初始值
		 */
		function prompt(msg,fn,initValue){
			var $w = $("<div ><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></br><input type=\"text\" style=\"width:300px;margin-left:20px\" id=\"___prompt\" value=\""+(initValue?initValue:"")+"\"/></div>");
			$w.appendTo($("body"));
			$('#___prompt').keydown(function(e){
				if(e.keyCode==13){
					$w.find('.dialog-button button:first').delay(100).focus();
					//setTimeout(function(){$w.find('.dialog-button button:first').focus();},100);
				}
			});
			$w.dialog({
				title:'请输入',
				width:350,
				height:150,
				modal:true,
				closable:false,
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttonsAlgin:'center',
				buttons:[{
						text:'确定',
						buttonHighHlight:true,
						handler:function(){
							var v = $('#___prompt').val();
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(true,v);
						}
					},{
						text:'取消',
						handler:function(){
							var v = $('#___prompt').val();
							$w.dialog('destroy');
							$w.remove();
							if(fn)
								fn(false,v);					
						}
					}
				]
			});
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var next = $(e.srcElement).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var prev = $(e.srcElement).prev()[0];
					if(prev)prev.focus();
				}
			});	
			$('#___prompt').delay(100).focus();
		}

		/**
		 * 多个按钮的提示框,
		 * @method buttonsDialog
		 * @param {String} msg 提示的信息，可以是html
		 * @param {Number} width 宽度，不要加px	
		 * @param {Number} height 高度，不要加px	
		 * @param {Object} buttons 
		 * <br/>示例：
		 * <br/>[
		 * <br/> {text:'确定1',
		 * <br/>  handler:function(){}
		 * <br/>  url:'test/testAction!query1.do'},
		 * <br/> {text:'确定2',
		 * <br/>  handler:function(){}
		 * <br/>  url:'test/testAction!query2.do'}
		 * <br/>]
		 */
		//TODO 可以考虑，某些按钮直接传url的时候做一些事情
		function buttonsDialog(msg,width,height,buttons){
			var $w = $("<div style='word-break:break-all;overflow:hidden;'><span style='margin:10px 2px 2px 10px;'>"+msg+"</span></div>");
			$w.appendTo($("body"));
			var yesOrNo = false;
			var _buttons = buttons;
			if(_buttons){
				for(var i=0;i<_buttons.length;i++){
					if(_buttons[i].handler){
						_buttons[i].handler = function(_click){
							return function(){
									$w.dialog('destroy');
									$w.remove();
									_click();							
								};
						}(_buttons[i].handler);
					}
				}
			}
			$w.dialog({
				title:'选择操作',
				width:(width?width:350),
				height:(height?height:250),
				modal:true,
				closable:false,
				buttonsAlgin:'center',
				onClose:function(){
					$w.dialog('destroy');
					$w.remove();
				},
				buttons:buttons
			});
			$w.keydown(function(e){//让按钮支持左右键选择聚焦
				if(e.keyCode==39){//->
					var next = $(e.srcElement).next()[0];
					if(next)next.focus();
				}else if(e.keyCode==37){//<-
					var prev = $(e.srcElement).prev()[0];
					if(prev)prev.focus();
				}
			});
			$w.find('.dialog-button button:first').delay(100).focus();
		}

		function sendMsgToFrame(type, msg, args){
		    try {
		        var o = {};
		        o.type = type;
		        o.msg = msg;
		        o.args = args;
				o = Ta.util.obj2string(o);
		        window.top.postMessage(o, "*");
		    } 
		    catch (e) {
		  }
		}

		/**
		 * 打开一个首页tab窗口
		 * @method openTabMenu
		 * @param {String} tabid 不可重复，最好取菜单id，若没菜单可自定义
		 * @param {String} title tab名称
		 * @param {String} !url 要访问的地址
		 */
		function openTabMenu(tabid,title,url){
			if (!url)
		   		return;
		   	var tab = $("#tab_" + tabid);
		   	var args = tabid+";"+title+";"+url;
		   	if (tab[0]) {
		   		Base.confirm("页面已打开,是否刷新",function (yes) {
		   			if(yes){
		   				var mainFrame = IndexTab.getTabBd(tabid);
		   				$(mainFrame).attr("src",$(mainFrame).attr("src"));
		   			}
		   			sendMsgToFrame("function", "IndexTab.actTab", tabid+";true");
		   		})
		   	}else{
		   		sendMsgToFrame("function", "IndexTab.addTab", args)
		   	}
		}
		
		/**
		 * 显示boxComponet组建，根据不同的目标对象显示在不同位置
		 * @method showBoxComponent
		 * @param {String} id boxComponet组建的id,必传
		 * @param {String} target 目标对象，为一dom对象，必传
		 */
		function showBoxComponent(id,target) {
			var $target = $(target);
			var bodyHeight = $(document.body).outerHeight(true);
			var bodyWidth = $(document.body).outerWidth(true);
			var $id = $("#"+id);
			var boxHeight = $id.outerHeight(true);
			var boxWidth = $id.outerWidth(true);
			if($target && $target.length == 1) {
				var targetOffset = $target.offset();
				if(targetOffset) {
					var targetTop = targetOffset.top;
					var targetLeft = targetOffset.left;
					var targetHeight = $target.innerHeight();
					var targetWidth = $target.innerWidth();
					var heightDifference = bodyHeight-targetHeight-targetTop;
					var widthDifference = bodyWidth-targetWidth-targetLeft;
					var horizontalP = true,verticalP = true;
					var ap = $id.attr("_position");
					if( ap == "horizontal") {
						heightDifference = bodyHeight - targetTop;
						widthDifference = bodyWidth-targetWidth-targetLeft;
					}else if (ap == "vertical") {
						heightDifference = bodyHeight-targetHeight-targetTop;
						widthDifference = bodyWidth-targetLeft;
					}
//					console.log("bodyWidth:" + bodyWidth +",boxWidth:" + boxWidth +",targetLeft:" + targetLeft + ",targetWidth:" + targetWidth + ",widthDifference:" +widthDifference);
//					console.log("bodyHeight:" + bodyHeight +",boxHeight:" + boxHeight +",targetTop:" + targetTop + ",targetHeight:" + targetHeight + ",heightDifference:" +heightDifference);
//					
					if(heightDifference >= boxHeight) {
						if( ap == "horizontal") {
							$id.css("top",(targetTop + targetHeight - 40 - targetHeight/2));
						}else if(ap == "vertical") {
							$id.css("top",(targetTop + targetHeight + 10));
						}
						verticalP = false;
					} else {
						if( ap == "horizontal") {
							$id.css("top",(targetTop - boxHeight + 50));
						}else if(ap == "vertical") {
							$id.css("top",(targetTop - boxHeight-10));
						}
						verticalP = true;
					}
					if(widthDifference >= boxWidth) {
						if( ap == "horizontal") {
							$id.css("left",targetLeft + targetWidth);
						}else if(ap == "vertical") {
							$id.css("left",targetLeft - 20);
						}
						horizontalP = false;
					} else {
						if( ap == "horizontal") {
							$id.css("left",(targetLeft - boxWidth - 10));
						}else if(ap == "vertical") {
							$id.css("left",(targetLeft - boxWidth + 40));
						}
						horizontalP = true;
					}
					var $boxComponent_b = $("#"+id+" > b");
					if( ap == "horizontal") {
						if( verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_rightBottom");
						}else if( verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_leftBottom");
						}else if( !verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_rightTop");
						}else if( !verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_leftTop");
						}
					}else if( ap == "vertical") {
						if( verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_bottomRight");
						}else if( verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_bottomLeft");
						}else if( !verticalP && horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_topRight");
						}else if( !verticalP && !horizontalP) {
							removeClass_b($boxComponent_b);
							$boxComponent_b.addClass("boxComponent_b_topLeft");
						}
					}
				}
			}
			$id.show();
		}
		function removeClass_b(o){
			o.removeClass("boxComponent_b_topLeft boxComponent_b_topRight boxComponent_b_bottomLeft boxComponent_b_bottomRight boxComponent_b_leftTop boxComponent_b_rightTop boxComponent_b_leftBottom boxComponent_b_rightBottom");
		}
	}
}));

﻿/**========================== 表单部分的操作=======================================*/
/**
 * form表单常用方法,调用方式为Base.xxx();
 * @module Base
 * @class forms
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
		/**
		 * 获取当前输入对象的前面一个输入对象或按钮。
		 * @method _getPreFormField
		 * @param {String} curid 当前对象id
		 * @return {Object} 前一个组件对象
		 */
		function _getPreFormField(curid){
			if (curid == null || curid == undefined || curid == "") return;
			var inWin = false;
			if($("#"+curid).parents('div.window-body').length>0){
				inWin = true;
			}
			var ids = Ta.core.TaUIManager.keys();
			var pre = null;
			for(var i=ids.length-1;i>=0;i--){
				if(ids[i]==curid){
					var m = i-1;
					if(i==(ids.length-1)){//当前对象恰好在最后
						m = ids.length-2;
					}
					for(;m>=0;m--){//从当前对象的后面一个开始想要可以置焦点的对象
						var srcobj = Ta.core.TaUIManager.getCmp(ids[m]);
						//寻找非只读，非disabled，非不可见
						if(srcobj.type && !srcobj.disabled && !srcobj.readOnly && !$(srcobj).is(':hidden')){
							  if(inWin){
						    	if($(srcobj).parents('div.window-body').length>0){
						    		pre = srcobj;
									break;
						    	}
						    }else{
								pre = srcobj;
								break;
							}
						}
					}	
				}
			}
			return pre;
		}

		/**
		 * 获取当前输入对象的后面一个输入对象或按钮。
		 * @method _getNextFormField
		 * @param {String} curid 当前id
		 * @return {Object} 后一个组件对象
		 */
		function _getNextFormField(curid){
			if (curid == null || curid == undefined || curid == "") return;
			var inWin = false;
			if($("#"+curid).parents('div.window-body').length>0){
				inWin = true;
			}
			var ids = Ta.core.TaUIManager.keys();
			var next = null;
			for(var i=0;i<ids.length;i++){
				if(ids[i]==curid){
					var m=i+1;
					if(i==(ids.length-1)){//当前对象恰好在最后
						m=0;
					}
					for(;m<ids.length;m++){//从当前对象的后面一个开始想要可以置焦点的对象
						var srcobj = Ta.core.TaUIManager.getCmp(ids[m]);
						//寻找非只读，非disabled，非不可见
						if(srcobj.type && !srcobj.disabled && !srcobj.readOnly && !$(srcobj).is(':hidden')){
						    if(inWin){
						    	if($(srcobj).parents('div.window-body').length>0){
						    		next = srcobj;
									break;
						    	}
						    }else{
								next = srcobj;
								break;
							}
						}
					}				
				}
			}
			return next;
		}
		/**
		 * 根据id获取对象。
		 * @method getObj
		 * @param {String} id 输入对象的id
		 * @return {Object}  有可能为：html元素，tree对象，datagrid对象，selectinput对象
		 */
		function getObj(id){
			var obj = Ta.core.TaUIManager.getCmp(id);
			if(obj)return obj;
			var obj = $("#"+id);
			if(obj[0])
				return obj[0];
			else {
				if(Base.globvar.developMode){
					//alert('没有找到id为'+id+'的对象');
				}
				return null;
			}
		}

		/**
		 * 获取输入对象的label。
		 * @method getFieldLabel
		 * @param {String} id 输入对象id
		 * @return {String} 输入对象label文字
		 */
		function getFieldLabel(id){
			var obj = Base.getObj(id);
			if(!obj)return;
			var str="";
			if(obj.cmptype=='flexbox'){
				return $("#"+obj.getId()).parent().parent().parent().find('>label.fieldLabel').text();
			}else if(obj.tagName){
				if(obj.type && (obj.type=="checkbox" || obj.type=='radio')){
					return $(obj.parentNode).find('>label').text();
				}else if(obj.tagName.toLowerCase()=='input'){
					return $(obj.parentNode.parentNode).find('>label.fieldLabel').text();
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){
					return $(obj).find('>label.fieldLabel').text();
				}
			}
		}
		/**
		 * 设置输入对象的label。
		 * @method setFieldLabel
		 * @param {String} id 输入对象id
		 * @param {String} lablel 新label
		 * @type String 
		 */
		function setFieldLabel(id,label){
			var obj = Base.getObj(id);
			if(!obj)return;
			var str="";
			if(label && label.indexOf("：")==-1){
				label  = label+"：";
			}
			if(obj.cmptype=='flexbox'){
				$("#"+obj.getId()).parent().parent().parent().find('>label.fieldLabel').text(label);
			}else if(obj.tagName){
				if(obj.type && (obj.type=="checkbox" || obj.type=='radio')){
					$(obj.parentNode).find('>label').text(label);
				}else if(obj.tagName.toLowerCase()=='input'){
					$(obj.parentNode.parentNode).find('>label.fieldLabel').text(label);
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){
					$(obj).find('>label.fieldLabel').text(label);
				}
			}
		}

		/**
		 * 设置输入对象为是否只读。
		 * @method _setReadOnly
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} isReadOnly  true 为只读，false为非只读
		 */
		function _setReadOnly(ids,isReadOnly){
			var fnsetReadonly = function(id,isReadOnly){
				var obj = Base.getObj(id);
				if(!obj)return false;
				
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					//obj.readOnly = 'true';
					var $obj = $(obj)
					$obj.attr('readOnly',isReadOnly);
					if(obj.type=="radio"){
						//pengwei 判断radio的readOnly状态
						var $par = $obj.parent();
						if(isReadOnly){
							if($par.hasClass("ta-radio-checked"))
								$par.removeClass("ta-radio-checked").addClass("ta-radio-checked-disabled");
							else if($par.hasClass("ta-radio-uncheck"))
								$par.addClass("ta-radio-uncheck-disabled");
						}else{
							if($par.hasClass("ta-radio-checked-disabled"))
								$par.removeClass("ta-radio-checked-disabled").addClass("ta-radio-checked");
							else if($par.hasClass("ta-radio-uncheck-disabled"))
								$par.removeClass("ta-radio-uncheck-disabled");
						}
							
						//$obj.attr('disabled','disabled');
					}
					if(obj.type=="checkbox"){
						//pengwei 
						var $par = $obj.parent();
						if(isReadOnly){
							if($par.hasClass("ta-chk-checked"))
								$par.removeClass("ta-chk-checked").addClass("ta-chk-checked-disabled");
							else if($par.hasClass("ta-chk-uncheck"))
								$par.removeClass("ta-chk-uncheck").addClass("ta-chk-uncheck-disabled");
						}else{
							if($par.hasClass("ta-chk-checked-disabled"))
								$par.removeClass("ta-chk-checked-disabled").addClass("ta-chk-checked");
							else if($par.hasClass("ta-chk-uncheck-disabled"))
								$par.removeClass("ta-chk-uncheck-disabled").addClass("ta-chk-uncheck");
						}
						
//						var clickValue = $obj.attr("onclick");
//						if((clickValue!=null&&clickValue!="") || ($obj.data("_tempclick")!=""&&$obj.data("_tempclick")!=null)){
//							if(isReadOnly){
//								if(clickValue!=null&&clickValue!=""){
//									$obj.data("_tempclick", clickValue);
//								}
//								$obj.removeAttr('onclick');
//								$obj.unbind("click").bind('click',function() { return false; });
//							}else{
//								if($obj.data("_tempclick")){
//									$obj.unbind('click').bind('click',eval($obj.data("_tempclick")));
//								}else{
//									$obj.unbind('click');
//								}
//							}
//						}else{
//							if(isReadOnly){
//								$obj.bind('click',function() { return false; });
//							}else{
//								$obj.unbind('click');
//							}
//						}
					}
					if(isReadOnly){
						$obj.addClass('readonly');
						$obj.parent("div.fielddiv2").addClass("readonly");
//							if(obj.type=="checkbox"){//针对checkbox在readonly的时候做处理
//							$(obj).unbind('.checkboxReadOnly').bind('click.checkboxReadOnly',function(){
//								this.checked = !this.checked;
//							});
//							}
						if($obj.hasClass("Wdate")){//只读时在时间输入框上面添加一透明div,防止用户点击只读输入框弹出时间选择panel
							if($obj.next(".shadingWdate").length == 0){
								$obj.after($("<div class='shadingWdate'></div>"))
							}else{
								$obj.next(".shadingWdate").show();
							}
						}
					}else{
						$obj.removeClass('readonly');
						$obj.parent("div").removeClass('readonly');
						$(obj).unbind('.checkboxReadOnly');
					}
					
				}else if(obj.cmptype == "flexbox"){
					obj.readOnly(isReadOnly);
				}
			};
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i]!= undefined){
						var o = Base.getObj(ids[i]);
						var $o = $(o);
						if(o && o.tagName && (o.tagName == 'FIELDSET' || o.tagName == "DIV" || o.tagName == "FORM")) {
							$o.find("input.ffb-input").each(function(){
								var a = this.id.replace("_desc", "");
								Base.getObj(a).readOnly(isReadOnly);
							})
							$o.find(":input[type!=hidden] ").not(".ffb-input").not(".datagrid :input").each(function() {
								fnsetReadonly(this.id,isReadOnly);
							});
						} else 
							fnsetReadonly(ids[i],isReadOnly);
					}
				}
			}
		}
		/**
		 * 设置输入对象为是否只读，只读模式下如果组件有值，仍然会向后台传递。
		 * <br/>例如：
		 * <br/>Base.setReadOnly("aac001");
		 * <br/>Base.setReadOnly(["aac001","aac002"]);
		 * <br/>Base.setReadOnly("aac001,aac002");
		 * @method setReadOnly
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]数组，或者逗号,分开如aac001,aac002
		 */
		function setReadOnly(ids){
			Base._setReadOnly(ids,true);
		}

		/**
		 * 设置输入对象为只读。
		 * @method _setEnable
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} enable  true 为disabled=true，false为disabled=false
		 */
		function _setEnable(ids,enable){
		   var fnsetEnable = function(id,enable){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					var $obj = $(obj);
					if(obj.tagName.toLowerCase()=='input' || obj.tagName.toLowerCase()=='textarea'){
						if(obj.type=="radio"){//处理radio的只读可写
							var $par = $obj.parent();
							if(enable){
								if($par.hasClass("ta-radio-checked-disabled"))
									$par.removeClass("ta-radio-checked-disabled").addClass("ta-radio-checked");
								else if($par.hasClass("ta-radio-uncheck-disabled"))
									$par.removeClass("ta-radio-uncheck-disabled");
								
								$obj.removeAttr('disabled');
								$obj.removeAttr('readOnly');
							}else{
								if($par.hasClass("ta-radio-checked"))
									$par.removeClass("ta-radio-checked").addClass("ta-radio-checked-disabled");
								else if($par.hasClass("ta-radio-uncheck"))
									$par.addClass("ta-radio-uncheck-disabled");
								
								$obj.attr('disabled','disabled');
							}
						}if(obj.type=="checkbox"){//处理checkbox的只读可写
							var $par = $obj.parent();
							if(enable){
								if($par.hasClass("ta-chk-checked-disabled"))
									$par.removeClass("ta-chk-checked-disabled").addClass("ta-chk-checked");
								else if($par.hasClass("ta-chk-uncheck-disabled"))
									$par.removeClass("ta-chk-uncheck-disabled");
								
								$obj.removeAttr('disabled');
								$obj.removeAttr('readOnly');
							}else{
								if($par.hasClass("ta-chk-checked"))
									$par.removeClass("ta-chk-checked").addClass("ta-chk-checked-disabled");
								else if($par.hasClass("ta-chk-uncheck"))
									$par.addClass("ta-chk-uncheck-disabled");
								
								$obj.attr('disabled','disabled');
							}
						}else{
							$obj.attr('disabled',!enable);
							if(enable){
								$obj.removeClass('disabled');
								Base._setReadOnly(id,false)//同时把readonly也设置为false
								if($obj.hasClass("Wdate")){//删除时间输入框上面透明的div
									$obj.next(".shadingWdate").remove();
								}
								$obj.parent("div.fielddiv2").removeClass("disabled");
							}else{
								$obj.addClass('disabled');
								$obj.parent("div.fielddiv2").addClass("disabled");
							}
						}
					}else if($obj.hasClass('checkboxgroup') || $obj.hasClass('radiogroup')){
						$obj.find('input').attr('disabled',!enable);
					}else if(obj.tagName.toLowerCase()=='fieldset' || $(obj).hasClass('panel')){
						if(enable)
							Base.hideMask();
						else
							Base.showMask(id,false);
					}else if((obj.tagName.toLowerCase()=='input' &&  obj.type=='button')|| obj.tagName.toLowerCase()=='button'){
						enable?(obj.disabled=false):(obj.disabled=true);
//						if(obj.type=="checkbox"){//针对checkbox在readonly的时候做处理
//							if($(obj).attr('readOnly')==true){
//								$(obj).bind('click.checkboxReadOnly',function(){
//									this.checked = !this.checked;
//								});
//							}
//						}
						var hotKey = $(obj).attr('hotKey');
						if(hotKey && hotKeyregister){
							var _this = obj;
							if(enable){
							    if(!hotKeyregister.all_shortcuts[hotKey.toLowerCase()])
									hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;})
							}else{
								hotKeyregister.remove(hotKey);
							}
						}
					}else if($(obj.parentNode).hasClass('tabs-panels')){//tab页的id
						enable?Base.enableTab(id):Base.disableTab(id);
					}
				}else if(obj.cmptype == "flexbox"){
					obj.setEnable(enable);
				} else if(obj.cmptype == 'taspinner'){
					var spinner = Ta.core.TaUIManager.getCmp(id);
					if(spinner){
						spinner.spinnerImgClick(id,enable);
					}
				} else if (obj.id && (obj.id == "file" || obj.id == "multipleFile")) {
					// 对文件上传组件<ta:fileupload>进行控制
					if (!enable) {
						obj._disabled = true;
						$("#" + id).addClass("disabled");
					} else {
						obj._disabled = false;
						$("#" + id).removeClass("disabled");
					}
				} else if(false){
					//可能为下拉树等非普通输入对象
					
				}
			};
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i]!= undefined){
						var o = Base.getObj(ids[i]);
						var $o = $(o);
						if(o && o.tagName && (o.tagName == 'FIELDSET' || (o.tagName == "DIV" && (!$(o.parentNode).hasClass('tabs-panels'))) || o.tagName == "FORM")) {
							$o.find("input.ffb-input").each(function(){
								var a = this.id.replace("_desc", "");
								Base.getObj(a).setEnable(enable);
							})
							$o.find(":input[type!=hidden] ").not(".ffb-input").not(".datagrid :input").each(function() {
								fnsetEnable(this.id,enable);
							});
						} else 
							fnsetEnable(ids[i],enable);
					}
				}
			}
		}
		/**
		 * 设置输入对象为可用（按钮为可用，输入框为可编辑）。
		 * <br/>例如：
		 * <br/>Base.setEnable("aac001");
		 * <br/>Base.setEnable(["aac001","aac002"]);
		 * <br/>Base.setEnable("aac001,aac002");
		 * @method setEnable
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"] 或者逗号,分开如aac001,aac002
		 */
		function setEnable(ids){
			Base._setEnable(ids,true);
		}

		/**
		 * 设置输入对象为不可用，不可用状态时，组件不会将值传递到后台。
		 * <br/>例如：
		 * <br/>Base.setDisabled("aac001");
		 * <br/>Base.setDisabled(["aac001","aac002"]);
		 * <br/>Base.setDisabled("aac001,aac002");
		 * @method setDisabled
		 * @param {String/Array} ids  输入对象的id或id数组
		 */
		function setDisabled(ids){
			Base._setEnable(ids,false);
		}

		/**
		 * 设置输入对象为必输项。
		 * @method _setIsRequired
		 * @private
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * @param {Boolean} isRequred  true 为isRequred=true，false为isRequred=false
		 */
		function _setIsRequired(ids,isRequired){
			var fnsetRequired = function(id,isRequired){
				var obj = Base.getObj(id);
				if(!obj)return false;
				
				if(obj.tagName && (obj.tagName.toLowerCase()=="input"||obj.tagName.toLowerCase()=="textarea")){//表明是html元素
					var label = $(obj).parents("div.fielddiv:first").find("label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							
							$(obj).validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$(obj).validatebox('setRequired',false);
						}
					}
				}else if(obj.cmptype == "flexbox"){
					var label = $(Base.getObj(id+"_div")).parents("div.fielddiv:first").find("label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							$("#"+id+"_desc").validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$("#"+id+"_desc").validatebox('setRequired',false);
						}
					}			
				}else if($(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//checkboxgroup,radiogroup
					var label = $(obj).find(">label");
					if(label){
						if(isRequired){
							if($(">span:first",label).text() != '*')
								label.html('<span style="color:red">*</span>'+label.text());
							
							$(obj).validatebox('setRequired',true);
						}else{
							$(">span:first",label).remove();
							$(obj).validatebox('setRequired',false);
						}
					}			
				}
			}
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					ids[i]?fnsetRequired(ids[i],isRequired):null;
				}
			}
		}
		/**
		 * 设置输入对象为必输。
		 * <br/>例如：
		 * <br/>Base.setRequired("aac001");
		 * <br/>Base.setRequired(["aac001","aac002"]);
		 * <br/>Base.setRequired("aac001,aac002");
		 * @method setRequired
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 */
		function setRequired(ids){
			Base._setIsRequired(ids,true);
		}
		/**
		 * 设置输入对象为非必输。
		 * <br/>例如：
		 * <br/>Base.setDisRequired("aac001");
		 * <br/>Base.setDisRequired(["aac001","aac002"]);
		 * <br/>Base.setDisRequired("aac001,aac002");
		 * @method setDisRequired
		 * @param {String/Array} ids  输入对象的id或id数组,例如: "aac001" 或["aac001","aac002"]
		 * 
		 */
		function setDisRequired(ids){
			Base._setIsRequired(ids,false);
		}

		/**
		 * 设置输入对象的值 
		 * <br>普通输入框为value字符串
		 * <br>对于checkboxgroup或radiogroup应该为数组
		 * <br>注:需要考虑下了列表,下拉多选,下拉树等组件
		 * <br>如果设置下拉组件，下拉框内的值，数据应为数组，如[{id:'aaa', name:'dddd'},{id:'bb', name:'dddd'}]
		 * @method setValue
		 * @param {String/map} id  输入对象的id 或一个json格式的对象对多个输入框赋值
		 * @param {String} value 值
		 */
		function setValue(id,value){
			//当value类型为String的时候，才做处理
			if(typeof value =='string'){
			  value = $.trim(value);
			}
			var _setValue = function(id,value){
				var temp_id = id+"";
				if(temp_id.substring(0,5) == "_sel_"){
					id = temp_id.substring(5,temp_id.length);
				}
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
					if($(obj).hasClass('checkboxgroup')){
						$(obj).find('input[type=checkbox]').each(function(){
							if(typeof value =='string'){
								if(this.disabled == true){}else{
									this.checked = (this.value==value);
									if(this.checked)
										Base._setChecked($(this).parent(), "checkbox");
									else if(!this.checked)
										Base._setUnChecked($(this).parent(), "checkbox");
								}
							}else if(jQuery.isArray(value)){
								var flag = false;
								for(var i = 0; i < value.length; i++) {
									if(value[i] == this.value) flag = true;
								}	
								if(flag){
									this.checked = true;
									Base._setChecked($(this).parent(), "checkbox");
								}else{
									this.checked = false;
									Base._setUnChecked($(this).parent(), "checkbox");
								}
							}
						});
					}else if($(obj).hasClass('radiogroup')){
						$(obj).find('input[type=radio]').each(function(){
								this.checked = (this.value==value);
								if(this.checked)
									Base._setChecked($(this).parent(), "radio");
								else if(!this.checked)
									Base._setUnChecked($(this).parent(), "radio");
						});
						
					}else if($(obj).hasClass('amountfield')){
						var t = {numberRound:$(obj).attr('numberRound'),decimalPlace:$(obj).attr('precision'), symbol:$(obj).attr('amountPre'),setValue:'setValue'};
						$(obj).moneyInput(t,value);
					}else if($(obj).hasClass('numberfield')){
						var options = {};
						$(obj).numberbox(options,value);
					
					}else if(obj.type=="text"|| obj.type=="password" || obj.tagName=='TEXTAREA'){
						if (value != null && typeof value == "string") {
							value = value.replaceAll("\\\\r", "\r");
							value = value.replaceAll("\\\\n", "\n");
							$(obj).val(value);
						} else {
							$(obj).val(value);
						}
						if($(obj).hasClass('datefield')){
					 	  if(value && value.length>10){
					 		  	$(obj).val(value.substring(0,10));
						  }
						}else if($(obj).hasClass('datetimefield')){
					 	  if(value && value.length>19){
					 		  	$(obj).val(value.substring(0,19));
						  }
						}
						
					/************* pengwei 修改  *************/	
					}else if(obj.type=='radio' || obj.type=='checkbox'){
							var $par =  $(obj).parent();
							if (!value){
								obj.checked = false;
								if(obj.type=='radio'){
									//判断调用setValue方法之前，选择框的状态
									Base._setUnChecked($par, "radio");
								}else{
									Base._setUnChecked($par, "checkbox");
								}
							}else if(value && obj.value == value){//当且仅当传入的value和标签的value值相等的时候才选中
								obj.checked = true;
								if(obj.type=='radio'){
									Base._setChecked($par, "radio");
								}else if(obj.type=='checkbox'){
									Base._setChecked($par, "checkbox");
								}
							}
//							else if (value != 0){
//								obj.checked = true;
//							}
//							else if (value == 0){ 
//								obj.checked = false;
//							}	
					}else if($(obj).hasClass('z_pic')){
						obj.src=Base.globvar.contextPath+"/"+value;
					}
				}else if(obj.cmptype == "flexbox"){
						try {
							var a = {};
							if(array){
								a.results = eval(array["_sel_" + id]);
								if (array["_sel_" + id] != null) 
									obj.setData(a);
								obj.setValue(value);
							}else{
								if(value != "" && value.indexOf("[") >=0 && value.indexOf("]") > 0){
									a.results = eval(value);
									obj.setData(a);
								}else if($.isArray(value)){
									a.results = eval(value);
									obj.setData(a);
								}else{
									obj.setValue(value);
								}
							}
						}
						catch (e) {
							obj.setValue(value);
						}
				}else if(obj.cmptype=="taselectpanel"){
					obj.setValue(id,value);	
				}else if(false){
					//TODO 下拉树等的控制
				}
			}
			if(value==null || value=="null" || value=="NULL")
				value="";
			if(typeof id == 'object'){
				var array = {};
				for(var i in id){
					if((i+"").substring(0,5) == "_sel_"){
						array[i] = id[i];
						delete id[i];
					}
				}
//				for(var i = 0 ; i < array.length ; i++){
//					if(typeof id[array[i]]=="string" ){
//						_setValue(array[i],$.trim(id[array[i]]));
//					}else{
//						_setValue(array[i],id[array[i]]);
//					}
//					delete id[array[i]];
//				}
				if($.isEmptyObject(id)){
					if(!$.isEmptyObject(array)){
						for(var j in array){
							_setValue(j,array[j]);
						}
					}
				}else{
					if(!$.isEmptyObject(array)){
						for(var j in array){
							_setValue(j,array[j]);
						}
					}
					for(var i in id){
						if(typeof id[i]=="string" ){
							_setValue(i,$.trim(id[i]));
						}else{
							_setValue(i,id[i]);
						}
					}
				}
			}else{
				_setValue(id,value);
			}
			
		}

		/**
		 *  获取输入对象的值
		 *  普通输入框为字符串
		 *  对于checkboxgroup或radiogroup 返回数组  
		 * @method getValue
		 * @param {String} id  输入对象的id
		 * @type {String/Array} value 值
		 * @return String or Array
		 */
		function getValue(id){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.tagName){//表明是html元素，而不是寄存在TaUIManager里面的对象。
				if($(obj).hasClass('checkboxgroup')){
					var ret =[];
					$(obj).find('input[type=checkbox]:checked').each(function(){
						ret.push(this.value);
					});
					return ret;
				}else if($(obj).hasClass('radiogroup')){
					return $(obj).find('input[type=radio]:checked').eq(0).val();
				}else if($(obj).hasClass('amountfield')){
					var t = {numberRound:$(obj).attr('numberRound'),decimalPlace:$(obj).attr('precision'), symbol:$(obj).attr('amountPre'),getValue:'getValue'};
					return $(obj).moneyInput(t);
				}else if(obj.type=="text"|| obj.type=="password"  || obj.type=='textarea' || obj.tagName=='TEXTAREA'){
					return $(obj).val();
				}else if(obj.type=='radio' || obj.type=='checkbox'){
					return obj.checked?$(obj).val():null;
				}
			}else if(obj.cmptype == "flexbox"){
				return obj.getValue(1);
			}else if(obj.cmptype == "taselectpanel"){
				return obj.getValue(id);
			}else if(false){
				//TODO 其他组件加入时处理
			}
		}
		/** 
		 * 让某个输入对象获取焦点
		 * @method focus
		 * @param {String} id 对象id
		 * @param {Number} delay 延迟（毫秒）后得到焦点
		 */
		function focus(id,delay){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.cmptype =='flexbox'){
				obj = $("#"+id+"_desc");
			}
			if(delay){
				$(obj).delay(delay).focus();
			}else{
				$(obj).focus();
			}
			//TODO 对下拉列表等其他特殊输入对象的处理
		}

		/**
		 * 显示输入对象或按钮
		 * @method showObj
		 * @param {String/Array} ids 例如: "aac001" 或["aac001","aac002"]， 或者以逗号隔开
		 */
		function showObj(ids){
			var show = function(id){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName && (obj.type=="text" || obj.type=="password" || obj.type=="textarea" || obj.type=="file")){//普通输入框
					if($(obj).parent().hasClass('fielddiv2')){
						$(obj).parent().parent().show().css('visibility','visible');
					}else if($(obj).parent().hasClass('fielddiv')){
						$(obj).parent().show().css('visibility','visible');
					}
					if(obj.type=="textarea"){
						$(obj).show().css('visibility','visible');
					}
				}else if($(obj.parentNode).hasClass('tabs-panels')){//tab
					var tabsc = $(obj.parentNode.parentNode);
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('showTab',id);
					}
				}else if(obj.cmptype =='flexbox'){//对combo处理
					$("#"+id).parent().parent().parent().show().css('visibility','visible');
				}else if($(obj).hasClass('radiogroup') || $(obj).hasClass('checkboxgroup')){
					$(obj).show();
				}else if(obj.tagName && obj.tagName.toLowerCase()=="input" && (obj.type=="checkbox" || obj.type=="radio")){
					$(obj).parent().show().css('visibility','visible');
				}//button
				 else if(obj.tagName && (obj.tagName.toLowerCase()=='button' || (obj.tagName.toLowerCase()=='input' && obj.type=='button' ))){
					$(obj).show().css('visibility','visible');
					var hotKey = $(obj).attr('hotKey');
					if(hotKey && hotKeyregister){
						var _this = obj;
						hotKeyregister.add(hotKey,function(){_this.focus();_this.click();return false;});
					}
				}else{
					$(obj).show().css('visibility','visible');
				}
				
			}
			if(typeof ids=='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					show(ids[i]);
				}
			}	
		}

		/**
		 * 隐藏输入对象
		 * @method hideObj
		 * @param {String/Array} ids 例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 * @param {Boolean} isHold 默认false 不占位，如果为为false就不占位
		 */
		function hideObj(ids,isHold){
			var hide = function(id,isHold){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName && (obj.type=="text" || obj.type=="password" || obj.type=="textarea" || obj.type=="file")){//普通输入框
					if($(obj).parent().hasClass('fielddiv2')){
						if(isHold){
							$(obj).parent().parent().css('visibility','hidden');
						}else{
							$(obj).parent().parent().hide();
						}
					}else if($(obj).parent().hasClass('fielddiv')){
						if(isHold){
							$(obj).parent().css('visibility','hidden');
						}else{
							$(obj).parent().hide();
						}
					}
				}else if($(obj.parentNode).hasClass('tabs-panels')){//tab
					var tabsc = $(obj.parentNode.parentNode);
					if(tabsc.hasClass('tabs-container')){
						tabsc.tauitabs('hideTab',id);
					}
				}else if(obj.cmptype =='flexbox'){//combo
					if(isHold)
						$("#"+id).parent().parent().parent().css('visibility','hidden');
					else
						$("#"+id).parent().parent().parent().hide();	
				}else if(obj.tagName && obj.tagName.toLowerCase()=="input" && (obj.type=="checkbox" || obj.type=="radio")){
					if(isHold){
						$(obj).parent().css('visibility','hidden');
					}else{
						$(obj).parent().hide();
					}
				}else{
					if(isHold)
						$(obj).css('visibility','hidden');
					else
						$(obj).hide();		
					
					var hotKey = $(obj).attr('hotKey');
					if(hotKey && hotKeyregister){
						var _this = obj;
						hotKeyregister.remove(hotKey);
					}	
					
				}
				
			}
			isHold = (isHold===true?true:false);
			
			if(typeof ids=='string'){
				ids = ids.split(',');
			}
			if(jQuery.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					ids[i]?hide(ids[i],isHold):null;
				}
			}	
		}
		/**
		 * 重置form表单，注意关于HIDDEN框在不同浏览器的问题，如在chrome中不会清除hidden类型输入框，IE则会
		 * @method resetForm
		 * @param {String} id form的id 如果不传，默认取得页面第一个form元素
		 */
		function resetForm(formId){
			var form = $("#"+formId)[0];
			if(formId==undefined)form = $('form')[0];
			if(form && form.tagName=='FORM'){
				form.reset();
				Base.clearInvalidStyle(formId);
			}
			
			$(form).find(":checkbox").not("[readonly='readonly']").each(function(){
				if(this.checked) {
					Base._setChecked($(this).parent(), "checkbox");
				}else {
					Base._setUnChecked($(this).parent(), "checkbox");
				}
			});
			
			$(form).find(":radio").not("[readonly='readonly']").each(function(){
				if(this.checked) {
					Base._setChecked($(this).parent(), "radio");
				} else {
					Base._setUnChecked($(this).parent(), "radio");
				}
			});
		}

		/**
		 * 验证TABS下的tab页,当需要提交tabs时必须验证tabs下的每个tab页时需要调用此方法,例如:
		 * Base.submit("tabs1","demo/demoAction!query.do",null,function(){return Base.validateTab("tabs1")})
		 * @method validateTab
		 * @param {String} ids id组成的字符串
		 * @param {Boolean} focusFirst 是否聚焦到第一个验证失败的tab
		 */
		function validateTab(ids,focusFirst){
			if(!ids)return true;
			var firstValid,focusFirst = (focusFirst ===false?false:true);
			
			function validate(id){
				var obj = Base.getObj(id);
				if(!obj)return true;
				if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					var bvalid =  $(obj).validatebox('isValid');
					if(!bvalid && focusFirst && !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else if(obj.cmptype=='flexbox'){
					var bvalid =  $("#"+obj.getId()+"_desc").validatebox('isValid')
					if(!bvalid && focusFirst &&  !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else{
					var bret = true;
					$(obj).find(':input,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						if($(this).is(":hidden") && !$(this).validatebox('isValid')){
							bret = false;
							if(focusFirst && !firstValid &&$(this).css("display")!="none"){
								firstValid = this.id;
							}
						}
					});
					return bret;
				}
			}
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				var bret = true;
				for(var i=0;i<ids.length;i++){
						if(ids[i] && ids[i]!='' && !validate(ids[i]) )bret = false;
				}
				if( focusFirst && firstValid){
					var tabid=$("#"+firstValid).closest(".tabs-panels").children("div").has("#"+firstValid)[0].id;
					Base.activeTab( tabid) ;
					Base.focus(firstValid,100);
					
				}
				return bret;
			}
			return false;
		}
		/**
		 * 对给定范围内的表单进行校验，可以对某些输入对象进行校验，也可以对某个容器内的所有输入对象进行校验
		 * @method validateForm
		 * @param {Stirng/Array} ids 必须传入，需要校验的对象id或容器id  或以数组形式传递多个，例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 * @param {Boolean} focusFirst 是否将焦点置于第一个错误的对象。默认true。
		 */
		function validateForm(ids,focusFirst){
			if(!ids)return true;
			var firstValid,focusFirst = (focusFirst ===false?false:true);
			
			function validate(id){
				var obj = Base.getObj(id);
				if(!obj)return true;
				if($("#"+id).is(':hidden'))return true;
				if(obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					var bvalid =  $(obj).validatebox('isValid');
					if(!bvalid && focusFirst && !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else if(obj.cmptype=='flexbox'){
					var bvalid =  $("#"+obj.getId()+"_desc").validatebox('isValid')
					if(!bvalid && focusFirst &&  !firstValid){
						firstValid  = id;
					}
					return bvalid;
				}else{
					var bret = true;
					$(obj).find(':input,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						if(!$(this).is(":hidden") && !$(this).validatebox('isValid')){
							bret = false;
							if(focusFirst && !firstValid ){
								firstValid = this.id;
							}
							return bret;
						}
						if($(this).is(":disabled"))
							bret = true; //disable
					});
					return bret;
				}
			}
			
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				var bret = true;
				for(var i=0;i<ids.length;i++){
					if(ids[i] && ids[i]!='' && !validate(ids[i]))bret = false;
				}
				if(focusFirst && firstValid){
					Base.focus(firstValid,100);
				}
				return bret;
			}
			return false;
		}

		/**
		 * 将某一个表单对象设置为校验失败
		 * @method setInvalidField
		 * @param {String} id id组件id
		 * @param {String} message 失败信息
		 * @return {Boolean}
		 */
		function setInvalidField(id,message){
			var obj = Base.getObj(id);
			if(!obj)return false;
			if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
				$(obj).validatebox('makeInvalid',message);
			}else if(obj.cmptype=='flexbox'){
				$("#"+obj.getId()+"_desc").validatebox('makeInvalid',message);
			}else{
				
			}
		}
		/**
		 * 清除表单校验失败的样式
		 * @method clearInvalidStyle
		 * @param {String/Array} ids 必须传入，需要校验的对象id或容器id  或以数组形式传递多个，例如: "aac001" 或"aac001,aac002" 或["aac001","aac002"]，或者以逗号隔开
		 */
		function clearInvalidStyle(ids){
			var clear = function(id){
				var obj = Base.getObj(id);
				if(!obj)return false;
				if(obj.tagName=='INPUT' || $(obj).hasClass('checkboxgroup') || $(obj).hasClass('radiogroup')){//输入框，checkboxgroup，radiogroup
					$(obj).validatebox('clear');
				}else if(obj.cmptype=='flexbox'){
					$("#"+obj.getId()+"_desc").validatebox('clear');
				}else{
					$(obj).find(':input:visible,div.checkboxgroup,div.radiogroup').not('[type=button]').each(function(){
						$(this).validatebox('clear');
					});
				}		
			}
			if(typeof ids =='string'){
				ids = ids.split(',');
			}
			if($.isArray(ids)){
				for(var i=0;i<ids.length;i++){
					if(ids[i] && ids[i]!='')clear(ids[i]);
				}
			}
		}
		/**
		 * 清除一个id区域的input输入框的值，置checkbox为非选择模式
		 * @method clearData
		 * @param {String} ids 某区域id
		 */
		function clearData(ids) {
			var obj = Base.getObj(ids);
			if(obj) {
				$(obj).find(":input").not(":radio").not(":checkbox").val("");
				$(obj).find(":checkbox").not("[readonly='readonly']").each(function(){
					this.checked = false;
					Base._setUnChecked($(this).parent(), "checkbox");
				});
				
				$(obj).find(":radio").not("[readonly='readonly']").each(function(){
					this.checked = false;
					Base._setUnChecked($(this).parent(), "radio");
				});
			}
		}

		function cancelBubble(event) {
			if (event == null) event = window.event;
			event.cancelBubble = true;
			event.returnValue = false;
			if (event.stopPropagation) {
				event.stopPropagation();
				event.preventDefault();
			}
		}

		/**
		 * 通过身份证号码获取出生年月日
		 * @method getIdCardBirthday
		 * @param {String} idCard 身份证号码
		 * @return {String} yyyy-MM-dd
		 */
		function getIdCardBirthday(idCard) {
		        var year, month, day;
		        // 身份证为15位或者18位
		        if (idCard.length == 15) {
		                year = idCard.substring(6, 8);
		                month = idCard.substring(8, 10);
		                day = idCard.substring(10, 12);
		        } else {
		                year = idCard.substring(6, 10);
		                month = idCard.substring(10, 12);
		                day = idCard.substring(12, 14);
		        }
		        // 按照yyyy-MM-dd自动补齐
		        if (year.length == 2)
		                year = "19" + year;
		        if (month.indexOf("0") == 0)
		                month = month.substring(1);
		        if (day.indexOf("0") == 0)
		                day = day.substring(1);
		        return year + "-" + month + "-" + day;
		}
		/**
		 * 获取身份证号码性别
		 * @method getIdCardGender
		 * @param {String} idCard  身份证号码
		 * @return {String}
		 */
		function getIdCardGender(idCard) {
		        var gender;
		        // 身份证为15位或者18位
		        if (idCard.length == 15) {
		                gender = idCard.substr(14, 1);

		        } else {
		                gender = idCard.substr(16, 1);
		        }
		        return gender % 2 == 0 ? '女' : '男';
		}

		/**
		 * 身份证15to18
		 * @method idcard218
		 * @param {String} sId 身份证号
		 * @return {Boolean} 
		 */
		function idcard218(sId) {
				if (sId.length == 15) {
					if(!/^\d{14}(\d|x)$/i.test(sId)){
						this.message =  "你输入的身份证长度或格式错误";
						return false;
					} else  {
					    sId=sId.substr(0,6)+'19'+sId.substr(6,9)
					    sId+= getVCode(sId);
					}
				}
				function getVCode(CardNo17) {
				  var Wi = new Array(7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2,1);
				  var Ai = new Array('1','0','X','9','8','7','6','5','4','3','2');
				  var cardNoSum = 0;
				  for (var i=0; i<CardNo17.length; i++)cardNoSum+=CardNo17.charAt(i)*Wi[i];
				  var seq = cardNoSum%11;
				  return Ai[seq];
				}
				return sId;
		}

		/**
		 * 设置selectData组件的值
		 * @method setSelectDataValue
		 * @param {String} id, 组件id
		 * @param {String} defaultId 隐藏值
		 * @param {String}  defaultName 显示值
		 */
		function setSelectDataValue(id,defaultId,defaultName) {
			if(id != null && defaultId != null && defaultName != null) {
				if(Base.getObj(id) != null) {
					if(defaultId != ""){
						if(String(defaultId).indexOf(",") > -1) {
							var ids = defaultId.split(",");
							var names = defaultName.split(",");
							for(var i = 0 ; i < ids.length ; i++){
								if(i == 0){
									$("#"+id).parent().prepend("<div class='selectData_leirong'  _id='leirong_"+ids[i]+"'><strong style='margin-right:18px;'>"+names[i]+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
								}else{
									$("#"+id).siblings(".selectData_leirong:last").after("<div class='selectData_leirong' _id='leirong_"+ids[i]+"'><strong  style='margin-right:18px;'>"+names[i]+"</strong><a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
								}
							}
						}else{ 
							$("#"+id).parent().prepend("<div class='selectData_leirong'  _id='leirong_"+defaultId+"'><strong style='margin-right:18px;'>"+defaultName+"</strong> <a class='selectData_leirong_a' href='javascript:void(0)' title='点击删除' onclick='fnRemoveSelectData_"+id+"(this)'>x</a></div>");
						}
						var leirongWidth = 0;
						var $leirong = $("#selectData_"+id+" div.selectData_leirong"),leirongWidth=0; 
						if($leirong && $leirong.length > 0) {
							for(var i = 0 ; i < $leirong.length ; i++) {
								leirongWidth += $($leirong[i]).outerWidth(true);
							}
						}
						$("#"+id).css("left",leirongWidth); 
					}
				}
			}
			
		}
		/**
		 * 获取经办现场信息
		 * @method getHandleStatus
		 * @param {String} jspUrl jsp路径如/sysapp/user.jsp
		 */
		function getHandleStatus(){
				var data = {};
				var fields = [];
				var lists = [];
				var keys = Ta.core.TaUIManager.keys();
				for(var i = 0;i<keys.length;i++){
					var obj = Base.getObj(keys[i]);
					if(obj.cmptype == 'datagrid'){
						var grid = {};
						grid.id = keys[i];
						var sdata = Base.getGridSelectedRows(keys[i]);
						grid['selData'] = Ta.util.obj2string(sdata);
						var allData = Base.getGridData(keys[i]);
						grid['allData'] = Ta.util.obj2string(allData);
						lists.push(grid);
					}
				}
				data.lists = lists;
				
				$('input').each(function(){
					if ($(this).attr("id")!= null) {
						if($(this).attr('readonly')){
							var singleData = {};
							singleData.id = $(this).attr('id');
							singleData.value = $(this).val();
							singleData.status = 'readonly';
							fields.push(singleData);
						}else if($(this).attr('disabled')){
							var singleData1 = {};
							singleData1.id = $(this).attr('id');
							singleData1.value = $(this).val();
							singleData1.status = 'disabled';
							fields.push(singleData1);
						}else {
							var singleData2 = {};
							singleData2.id = $(this).attr('id');
							singleData2.value = $(this).val();
							singleData2.status = '';
							fields.push(singleData2);
						}
					}
				});
				data.fields = fields;
				var $t = $("<textarea id=\"_dataFields_\" name=\"dto[\'_dataFields_\']\"></textarea>");
				$t.appendTo($('body'));
				$t.hide();
				$t.val(Ta.util.obj2string(data));
				
				var html = document.documentElement.outerHTML;
				//var $html = $(html).find(".slick-viewport").remove();
				var $jsp = $("<textarea id=\"_jspContext_\" name=\"dto[\'_jspContext_\']\"></textarea>");
				$jsp.appendTo($('body'));
				$jsp.hide();
				$jsp.val(html);
				
				Base.submit('_dataFields_,_jspContext_',Base.globvar.basePath + '/system/handleOldDataManagerAction!save.do');
				$t.remove();
				$jsp.remove();
		}

		/**
		 * 显示经办历史现场
		 * @method showHandleStatus
		 * @param {String} userid 用户id
		 * @param {String} time 时间
		 * @param {String} optionid 经办
		 */
		function showHandleStatus(userid, time, optionid) {
			var param = {}; 
			param["dto['id']"] = optionid;
//			param["dto['yae092']"] = userid;
//			param["dto['date']"] = time;
			top.Base.openWindow("optionid","经办现场回顾", Base.globvar.basePath + '/system/handleOldDataManagerAction!doExecutePage.do' ,param,top.innerWidth, top.innerHeight,null,null,true);
		}

		/**
		* 输入框内容全选
		* <br>$(function(){$("input").click(function(Base.selectValue("aac001")))})
		* <br>$(function(){$("input").click(function(Base.selectValue($(this))))})
		* <br>$(function(){$("input").click(function(Base.selectValue(this)))})
		* @param {} obj 可为单个输入框id,如"aac001"，也可以为一个jquery对象或者dom对象，如$("#aac001")，$("#aac001").get(0);	
		*/
		function selectValue(obj) {
			if(obj != null){
				if(typeof obj == "string"){
					$('#'+obj).select();
				}else if(typeof obj == "object"){
					if(obj instanceof jQuery){
						obj.select();
					}else{
						$(obj).select();
					}	
				}else{
					return;
				}
			}else{
				return;
			}
		}

		/**
		 * 获取当前值，作为历史数据
		 * 获取值结构如下
		 * [{ovDto['aac001_old']:'xxxx'}, ovDto['aac001_label']:'xxxx',ovDto['aac001_desc']:'xxxx'},{checkboxgp_old:[1,2,3]}]
		 * 存为零时变量
		 * @method saveOldValue
		 * @param {} aids
		 */
		function saveOldValue(submitIds) {
			if (submitIds == null) return;
			var oldValueObj = [];
			var aids = submitIds.split(',');
			for(var i=0;i<aids.length;i++){
				if(aids[i]==null || aids[i]=='')continue;
				var obj = Base.getObj(aids[i]);
				
				if (obj == undefined) continue;
				
				var $obj = $(obj);
				
				if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
				  (obj.tagName && (obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
					if(obj.cmptype=='flexbox')obj = $("#"+aids[i]);//下拉框
					
					for(var j=0;j<aids.length;j++){//对ids进行校验，不能父子嵌套
						if(aids[j]==null || aids[j]=='')continue;
						var obj2 = Base.getObj(aids[j]);
						if(obj2.cmptype=='flexbox')obj2 = $("#"+aids[j]);
						if(i != j && obj2.cmptype!='datagrid'){//找到其他对象
							if($(obj).has($(obj2)).length>0){
								alert(aids[j]+"对象在"+aids[i]+"对象里面，指定提交的元素id不能有包含与被包含关系");
								return false;
							}
							if($(obj2).has($(obj)).length>0){
								alert(aids[i]+"对象在"+aids[j]+"对象里面，指定提交的元素id不能有包含与被包含关系");
								return false;
							}
						}
					}
					if ($obj.hasClass("amountfield")){
						var mnyobj = {};
						mnyobj["__id"] = aids[i];
						mnyobj["ovDto['" + aids[i] + "']"] = $("#"+aids[i]+"_hidden").val().trim().replace(":","").replace("*","");
						mnyobj["ovDto['" + aids[i] + "__label']"] = $("#"+aids[i]+"_hidden").parent().parent().find("label").text().trim().replace(":","").replace("*","");
						oldValueObj.push(mnyobj)
					}
					$("#"+aids[i]).find("input").not(":hidden").each(function(){
						if ($(this).hasClass("ffb-input")) {
							var mnyobj = {};
							var id = (this.id).replace("_desc","");
							mnyobj["__id"] = id;
							mnyobj["ovDto['" + id + "']"] = $(this).prev().val().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + id + "__label']"] = $(this).parent().parent().parent().find("label").text().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + id + "__desc']"] = $(this).val().trim().replace(":","").replace("*","");
							oldValueObj.push(mnyobj)
						} else {
							var mnyobj = {};
							mnyobj["__id"] = this.id;
							mnyobj["ovDto['" + this.id + "']"] = $(this).val().trim().replace(":","").replace("*","");
							mnyobj["ovDto['" + this.id + "__label']"] = $(this).parent().parent().find("label").text().trim().replace(":","").replace("*","");
							oldValueObj.push(mnyobj)
						}
					})
				}
				else if($obj.hasClass("amountfield")){
					var mnyobj = {};
					mnyobj["__id"] = aids[i];
					mnyobj["ovDto['" + aids[i] + "']"] = $("#"+aids[i]+"_hidden").val().trim().replace(":","").replace("*","");
					mnyobj["ovDto['" + aids[i] + "__label']"] = $("#"+aids[i]+"_hidden").parent().parent().find("label").text().trim().replace(":","").replace("*","");
					oldValueObj.push(mnyobj)
				}
				else{
					alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
					return false;
				}
			}
			document._oldValueObj = oldValueObj;
			return oldValueObj;
		}
		function spinnerImgClick(id){
			var spinner = Ta.core.TaUIManager.getCmp(id);
			if(spinner){
				spinner.spinnerImgClick(id);
			}
		}
		/**
		 * 性能最好的插入排序
		 */
		function insertSort(arr, sortfn, desc) {
		    for (var i = 1; i < arr.length; i++) {
		      var tmp = arr[i],
		          j = i;
		      while (arr[j - 1] > tmp) {
		        arr[j] = arr[j - 1];
		        --j;
		      }
		      arr[j] = tmp;
		    }
		    return arr;
		}

		/**
		 * 针对checkbox和radio，设置选中
		 * @private
		 * @method _setChecked
		 */
		function _setChecked($_d, type) {
			if (type == "checkbox") {
				if($_d.hasClass("ta-chk-uncheck"))
					$_d.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
				else if($_d.hasClass("ta-chk-uncheck-disabled"))
					$_d.removeClass("ta-chk-uncheck-disabled").addClass("ta-chk-checked-disabled");
			}else if (type == "radio") {
				if($_d.hasClass("ta-radio-uncheck"))
					$_d.removeClass("ta-radio-uncheck").addClass("ta-radio-checked");
				else if($_d.hasClass("ta-radio-uncheck-disabled"))
					$_d.removeClass("ta-radio-uncheck-disabled").addClass("ta-radio-checked-disabled");
			}
			
		}

		/**
		 * 针对checkbox和radio，取消选中
		 * @private
		 * @method _setUnChecked
		 */
		function _setUnChecked($_d, type) {
			if (type == "checkbox") {
				if($_d.hasClass("ta-chk-checked"))
					$_d.removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
				else if($_d.hasClass("ta-chk-checked-disabled"))
					$_d.removeClass("ta-chk-checked-disabled").addClass("ta-chk-uncheck-disabled");
			}else if (type == "radio") {
				if($_d.hasClass("ta-radio-checked"))
					$_d.removeClass("ta-radio-checked").addClass("ta-radio-uncheck");
				else if($_d.hasClass("ta-radio-checked-disabled"))
					$_d.removeClass("ta-radio-checked-disabled").addClass("ta-radio-uncheck-disabled");
			}
			
		}
		/**
		 * 文本框的提示文字信息
		 * @method funPlaceholder
		 * @param {Object} dom元素,必须是原生的document元素,不能是jquery的对象;
		 */
		function funPlaceholder(element) {  
			
		    //检测是否需要模拟placeholder  
		    var placeholder = '';  
		     if (element && !("placeholder" in document.createElement("input")) && (placeholder = element.getAttribute("placeholder"))) {  
		         //当前文本控件是否有id, 没有则创建  
		         var idLabel = element.id ;  
		         if (!idLabel) {  
		             idLabel = "placeholder_" + new Date().getTime();  
		             element.id = idLabel;  
		         }  
		         //创建label元素  
		         var eleLabel = document.createElement("label");  
		         eleLabel.htmlFor = idLabel;  
		         eleLabel.style.position = "absolute";  
		         //根据文本框实际尺寸修改这里的margin值  
		         eleLabel.style.margin = "0 0 0 0";  
		         eleLabel.style.color = "graytext";  
		         eleLabel.style.cursor = "text";  		         
		         
		         eleLabel.style.display = "block" ;
		         //插入创建的label元素节点  
		         element.parentNode.insertBefore(eleLabel, element);  
		        
		         //事件  
		         element.onfocus = function() {  		         	
		             eleLabel.innerHTML = "";  
		         };  
		         element.onblur = function() {  
		         	
		             if (this.value === "") {  
		                 eleLabel.innerHTML = placeholder;  
		             }  
		         };
		         //处理点击到label上面,提示不消失
		         eleLabel.onclick = function(){
		        	 element.focus();
		         }
				 element.onpropertychange = function(){  	 						 				 	 						 	 
				 	  if (element.value != "") {  				
				 	  	eleLabel.innerHTML = "";
				 	  }
		         };
		         
		         
		         
		         //样式初始化  
		         if (element.value === "") {  
		             eleLabel.innerHTML = placeholder;  
		         }  
		     }  
		}

		return {
			_getPreFormField : _getPreFormField,
			_getNextFormField : _getNextFormField,
			getObj : getObj,
			getFieldLabel : getFieldLabel,
			setFieldLabel : setFieldLabel,
			_setReadOnly : _setReadOnly,
			setReadOnly : setReadOnly,
			_setEnable : _setEnable,
			setEnable : setEnable,
			setDisabled : setDisabled,
			_setIsRequired : _setIsRequired,
			setRequired : setRequired,
			setDisRequired : setDisRequired,
			setValue : setValue,
			getValue : getValue,
			focus : focus,
			showObj : showObj,
			hideObj : hideObj,
			resetForm : resetForm,
			validateTab : validateTab,
			validateForm : validateForm,
			setInvalidField : setInvalidField,
			clearInvalidStyle : clearInvalidStyle,
			clearData : clearData,
			cancelBubble : cancelBubble,
			getIdCardBirthday : getIdCardBirthday,
			getIdCardGender : getIdCardGender,
			idcard218 : idcard218,
			getHandleStatus : getHandleStatus,
			showHandleStatus : showHandleStatus,
			selectValue : selectValue,
			saveOldValue : saveOldValue,
			spinnerImgClick : spinnerImgClick,
			insertSort : insertSort,
			_setChecked : _setChecked,
			_setUnChecked : _setUnChecked,
			funPlaceholder : funPlaceholder,
			setSelectDataValue : setSelectDataValue
		}
	}
}));



﻿/**
 * ajax前后台数据交互方法，调用方式为Base.xxx();
 * @module Base
 * @class taajax
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
			_enableButtonLayout : _enableButtonLayout,
			showMask : showMask,
			hideMask : hideMask,
			getJson : getJson,
			submit : submit,
			submitForm : submitForm,
			loadValues : loadValues,
			_ajax : _ajax,
			_doSubmitIds : _doSubmitIds,
			_dealdata : _dealdata,
			compareOldData : compareOldData
		}
		/**
		 * 设置所有按钮所在面板是否可用
		 * @method _enableButtonLayout
		 * @private
		 * @param {boolean} enable
		 */
		function _enableButtonLayout(enable){
			if(enable){
				$("div.panel-button,div.button-panel,div.panel-toolbar").each(function(){
					//$(this).attr('disabled','disabled');
					$("<div class='enableButtonLayout' style='top:"+$(this).offset().top+"px;left:"+$(this).offset().left+"px;height:"+$(this).outerHeight(true)+"px;width:"+$(this).outerWidth(true)+"px'></div>").appendTo($('body'));
				});
				Base.globvar.isSubmitNow = true;
			}else{
				Base.hideMask();
			}
		}
		/**
		 * 让某一个面板出现半透明蒙层，提示：读取中
		 * @method showMask
		 * @param {String} id  面板的id，如果不传或null就是整个页面
		 * @param {Boolean} showLoading  是否显示loading的图片和文字，默认为true，如果设置为false不显示图片和文字
		 */
		function showMask(id,showLoading){
			var height = $(window).height(),width = $(window).width(),top=0,left=0;
			var obj = null;
			if(id && (obj=Base.getObj(id))){
				var $obj = $(obj);
				height = $obj.height();
				width = $obj.width();
				top = $obj.offset().top;
				left = $obj.offset().left;
				
				if($obj.hasClass('panel') && $('>div.panel-header',$obj).length>0){
					top += $('>div.panel-header',$obj).outerHeight(true);
					height -= $('>div.panel-header',$obj).outerHeight(true);
				}else if(obj.tagName.toLowerCase()=='fieldset'){
					top += 22;
					height -=5;
					width +=18;
				}
				
			}
			var loadding = "";
			if(showLoading === false){
			}else{
				loadding = "<div style='left:"+((width-left)/2-20)+"px;top:"+((height-top)/2+10)+"px;width:60px;height:30px;opacity:1;position: absolute;font-size:12px'>读取中...</div>";
			}
			
			$("<div class='enableButtonLayout "+(showLoading===false?"":"loading")+"' style='top:"+top+"px;left:"+left+"px;height:"+height+"px;width:"+width+"px'>"+loadding+"</div>").appendTo($('body'));
		}
		/**
		 * 隐藏蒙层
		 * @method hideMask
		 */
		function hideMask(){
			$("body >div.enableButtonLayout").remove();
			Base.globvar.isSubmitNow = false;
		}
		/**
		 * 同步或异步到后台获取返回json格式的内容，默认同步执行
		 * @method getJson
		 * @param {String} url action地址
		 * @param {Object} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} callback 返回成功后的回调，入参返回的为json对象和XMLHttpRequest对象，
		 *							这里不需要success为true标志，只要后台成功返回json格式的数据都会回调,
		 *							<br>例如：function(data){var dataArray = eval(data.fieldData.datalist);},
		 *							<br>其中，data为后台返回的数据，datalist是你在action中绑定的id(setData('datalist',obj)),
		 *							<br>dataArray是一个数组,访问方式为：dataArray[1].aac003.
		 *							<br>注意:action中也可以setList('grid1',list),但不建议在此使用。 
		 *							
		 * @param {boolean} async 设置是否异步，默认为false异步
		 * @return 返回的json对象
		 			<br>1.当action中以setData('','')返回时，此对象的调用方式和callback里的方式一样
		 			<br>2.当action中以writeJsonToClient(obj)返回时，此对象为一个数组。
		 */
		function getJson(url,parameter,callback,async){
			var ret;
			url = (url.indexOf('?')==-1?(url+"?_r="+Math.random()):(url+"&_r="+Math.random()));
			Base._ajax({
				url:url,
				"type":'POST',
				data:parameter,
				success:function(data){
					if(!$.isArray(data) && !$.isEmptyObject(data)){
						var tempData = data.fieldData,newData = {};
						for(var i in tempData){
							if(i.indexOf("_sel_") == 0){
								newData[i.substring(5)] = tempData[i];
							}else{
								newData[i] = tempData[i];
							}
						}
						data.fieldData = newData;
					}
					ret = data;
					if (typeof callback == "function") {
						callback(data);
					}
				},
				async:async===true?true:false,
				dataType:'json'
			});
			return ret;
		}
		/**
		 * 异步提交表单，action必须返回JSON或者null，此方法不能用于页面跳转，通常用于返回表格数据
		 * <br>能够自动校验表单
		 * <br>能够对后台返回的json进行自动处理。
		 * <br> 处理如下：
		 * <br>  1、有消息自动提示（根据不同类型提示不同类型的提示框）
		 * <br>  2、如果有返回自由格式的内容自动给页面对应的输入对象赋值
		 * <br>  3、如果有列表的值，自动给所有列表更新列表内容
		 * <br>  4、如果有对表单输入对象或按钮的控制内容，自动根据数据进行控制
		 * <br>  5、如果有校验不通过的自动设置不通过的输入对象为校验失败的样式，同时第一个元素获取焦点。
		 * <br>  6、如果有设置焦点的数据，自动给数据对应的对象获取焦点。
		 * @method submit
		 * @param {String} submitIds  需要传递到后台的对象id或容器id,多个id可以用”,“隔开
		 * @param {String} url 提交的地址
		 * @param {Object/String} parameter 入参 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} onSubmit 提交前手动检查，如果返回false将不再提交,必须返回true或false
		 * @param {Boolean} autoValidate 默认false 是否自动调用Base.validateForm对ids对象进行校验，如果校验失败将不再提交
		 * @param {Function} succCallback callback 返回业务成功后的回调，入参返回的为json对象和XMLHttpRequest对象
		 											<br>例如：function(data){alert(data.lists.grid2.list[0].aac003)}，
		 											<br>其中data为返回的json数据，grid2是在action中绑定的id(setList('grid2',list))，通常是jsp中datagrid的id.
		 * @param {Function} failCallback  业务失败回调，入参返回的为json对象和XMLHttpRequest对象
		 */
		function submit(submitIds,url,parameter,onSubmit,autoValidate,_succCallback,_failCallback,isIncludeNullFields,token){
			if (token == null) token = true;
			autoValidate = (autoValidate===false?false:true);
			if((onSubmit && !onSubmit()) || (autoValidate && !Base.validateForm(submitIds))){
				Base.hideMask();
				return false;
			}
			//在300毫秒以内不显示蒙层
			Base._enableButtonLayout(false);
			var showMaskTime = setTimeout(function(){
				Base.showMask("body");
			}, 300);
			submitIds = submitIds?submitIds:"";
			var aids = submitIds.split(',');
			
			var queryStr = _doSubmitIds(aids, parameter,isIncludeNullFields, token);
			if(false){//如果是文件上传
				var form = Base.getObj(submitIds);
				if(aids.length==1 && form && form.tagName=='FORM'){
					$(form).attr('action',url);
					$(form).attr("enctype","multipart/form-data");
					form.submit();
				}else{
					if(Base.globvar.developMode)alert('文件上传只能传入唯一的form元素id');
				}
			}else{
				
				//Base.getHandleStatus
				//根据ids拼接传递的条件字符串
				
				Base._ajax({
					"url":url,
					"data":queryStr,
					"succCallback":function(data,dataType){
						//data.replaceAll("%0D%0A","\r\n");
						clearTimeout(showMaskTime);
						Base._dealdata(data);
						Base.hideMask();
						if(_succCallback)_succCallback(data,dataType);
						Base._enableButtonLayout(false);
					},
					"failCallback":function(data,dataType){
						//data.replaceAll("%0D%0A","\r\n");
						Base._dealdata(data);
						Base.hideMask();
						if(_failCallback)_failCallback(data,dataType);
						Base._enableButtonLayout(false);
					},
					"type":'POST'
					//,
					//"async":(async===false?false:true)
					
					,"dataType":"json"
					
				});
			}
		}
		/**
		 * 同步提交form。
		 * 主要用途，表单提交后要刷新整个页面或跳转到其他页面的时候以及需要使用文件上传功能的时候使用
		 * @method submitForm
		 * @param {String} formId form表单的id ,<b>必传</b> 
		 * @param {Function} onSubmit 提交前执行的函数，如果返回false就不在继续提交表单
		 * @param {Boolean} autoValidate 是否对表单进行自动校验，默认为true。
		 * @param {String} url 提交的url,如果不传，请在form标签里面的aciton属性填写。
		 * @param {String} parameter 参数json格式{"dto['aac001']":"1000001","dto['aac002']":"01"}。
		*/
		function submitForm(formId,onSubmit,autoValidate,url,parameter){
			Base.showMask();
			if(formId){
				form = document.getElementById(formId);
			}else{
				alert('传入formId为空');
				return false;		
			}
			if(!form){
				alert('找不到需要提交的form元素');
				return false;
			}
			if((onSubmit && !onSubmit()) || (autoValidate && !Base.validateForm(formId))){
				Base.hideMask();
				return false;
			}
			if(parameter){
				url += "?" + jQuery.param(parameter);
			}
			$form = $(form);
			if(url){//ie8不识别form.action
				$form.attr("action",url);
			}
			$("div.datagrid").each(function(){
			    var gridId = $(this).attr('id');
				if(!document.getElementById(gridId+'_selected')){
					$form.append("<input type=\"hidden\" id=\""+gridId+"_selected\" name=\"gridInfo['"+gridId+"_selected']\"/>");
					document.getElementById(gridId+'_selected').value=Ta.util.obj2string(Base.getGridSelectedRows(gridId));
				}else{
					document.getElementById(gridId+'_selected').value=Ta.util.obj2string(Base.getGridSelectedRows(gridId));
				}
				
				if(!document.getElementById(gridId+'_modified'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_modified\" name=\"gridInfo['"+gridId+"_modified']\" value=\""+Ta.util.obj2string(Base.getGridModifiedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_modified').value=Ta.util.obj2string(Base.getGridModifiedRows(gridId));
				if(!document.getElementById(gridId+'_removed'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_removed\" name=\"gridInfo['"+gridId+"_removed']\" value=\""+Ta.util.obj2string(Base.getGridRemovedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_removed').value=Ta.util.obj2string(Base.getGridRemovedRows(gridId));
				
				if(!document.getElementById(gridId+'_added'))
					$form.append("<input type=\"hidden\" id=\""+gridId+"_added\" name=\"gridInfo['"+gridId+"_added']\" value=\""+Ta.util.obj2string(Base.getGridAddedRows(gridId))+"\"/>");
				else
					document.getElementById(gridId+'_added').value=Ta.util.obj2string(Base.getGridAddedRows(gridId));
			});
//			var $tempSubmit = null;
//			if ($("#__submitkey__").val() != undefined) {
//				var $form = $(form);
//				var value = $("#__submitkey__").val();
//				$tempSubmit = $("<input type=\"hidden\" " + "value=\"" + value + "\"" + "name=\"__submitkey__\"/>");
//				$form.append($tempSubmit);
//			}
			form.submit();
			
//			if ($tempSubmit != null) {
//				$tempSubmit.remove();
//			}
		}
		/**
		 * 根据某些输入表单的值获取页面数据
		 * 对返回数据的处理如submitform。
		 * @method loadValues
		 * @private
		 * @param {String/Array} submitids 指定作为参数的输入对象的id或name，如果多个请传入输入,如:["aac001","aac002"]
		 * 								   框架会自动获取这些输入对象的值作为参数传递过去。 可以为空。
		 * @param {Object} parameter 手工传入参数 json格式对象，例如:{"dto['aac001']":"1000001","dto['aac002']":"01"}
		 * @param {Function} succCallback 返回业务成功后的回调，入参返回的为json对象和XMLHttpRequest对象
		 * @param {Function} failCallbackak  业务失败或系统异常失败回调，入参XMLHttpRequest对象
		 * @param {boolean} async async 设置是否异步，默认为true
		 * @deprecated
		 */
		function loadValues(submitids,url,parameter,succCallback,failCallback,async){
			
		}
	
		/**
		 * 异步或同步交互
		 * options["succCallback"] 返回的数据里面有success=true  被调用
		 * options["failCallback"] 返回的数据里面有success=false  被调用
		 * 其他配置选项与jQuery.ajax一样
		 * @param {object} options jQuery.ajax的配置项
		 */
		//var tempajax = $.ajax;
		function _ajax(options){

//			url,parameter,succCallback,failCallback,type,async
			var _options = options;
			if(!_options["url"]){
				throw "Base._ajax方法必须传入URL";
			}
			//将url中的中文转换成utf-8
			_options["url"] = encodeURI(_options["url"]);
			var error = false;//http错误
			var _data = null,dataType="";//返回的数据
			var succCallback = options["succCallback"],failCallback = options["failCallback"];
			delete _options["succCallback"];
			delete _options["failCallback"];
			
			_options["complete"] = function(_XMLHTTPRequest,textStatus){
					Base._enableButtonLayout(false);
					if(_XMLHTTPRequest && _XMLHTTPRequest.getResponseHeader){
					    if(_XMLHTTPRequest.getResponseHeader('__timeout')) {
					    	alert('操作提示：会话已经超时，请重新登录!');
				        	top.location.href='index.jsp?randId=' + parseInt(1000*Math.random());
				        	return;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__forbidden')){
				    		Base.alert(['系统访问权限提示：','你目前没有权限访问：',this.url].join(','));
				    		return;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__exception')){
				    		Base._dealdata(eval("("+_XMLHTTPRequest.responseText+")"));
				    		return ;
				    	}
				    	if(_XMLHTTPRequest.getResponseHeader('__samelogin')){
				    		alert('帐号在其他地方登录，您已被迫下线！');
				    		top.location.href='index.jsp?randId=' + parseInt(1000*Math.random());
				    		return ;
				    	}
					}
					if(error){//异常
						if(textStatus==="parsererror"){//jquery解析错误
							alert(['返回的数据格式不满足json格式，解析错误:\n',_data].join(','));
						}else{
							alert(['执行发生异常,可能网络连接失败'].join(','))
						}
					}else{
						if(_data){
							if(_data.success || typeof _data === 'string'){//业务成功success==true或返回的是字符串
								if(succCallback)
									succCallback.call(this,_data,dataType);
								
							}else if(_data.success!= undefined && _data.success.toString().toLowerCase()=="false"){//业务失败
								if(failCallback)
									failCallback.call(this,_data,dataType);
								
							}else{//TODO 其他类型的返回
								
							}
						}
					}

			};
			var success = _options["success"];
			_options["success"] = success?function(data,statusText){
					_data = data;
					success(data,statusText);
				}:function(data,statusText){
				_data = data;
			};
			_options["error"] = function(_XMLHTTPRequest,errmsg,exception){
					//errmsg需要处理timeout/parseerror情况
					//其他异常不特殊告诉，直接显示
					_data = _XMLHTTPRequest.responseText;
					error = true;
			};
			_options["dataFilter"] = function(data,type){
				dataType = type;
				return data;
			};
			_options["beforeSend"] = function(_XMLHTTPRequest){//确保post的时候不会乱码
				_XMLHTTPRequest.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=utf-8"); 
				return true;
			};
			jQuery.ajax(_options);
			//tempajax(_options);
		}
		////////////提取方法
			function _doSubmitIds(aids, parameter, isIncludeNullFields, token) {
				var queryStr = "",datagridids = [];
//				aids.push("_token__tokenflag");
				if(aids){//Base.globvar.developMode
					for(var i=0;i<aids.length;i++){
						if(aids[i]==null || aids[i]=='')continue;
						var obj = Base.getObj(aids[i]);
						if (obj == undefined) continue;
						var $obj = $(obj);
						if(obj && obj.cmptype!='datagrid' && ($obj.hasClass('panel') || $obj.hasClass('grid') || obj.cmptype=='flexbox' ||
						  (obj.tagName && (obj.tagName=='FORM' ||obj.tagName=='FIELDSET' || obj.tagName=='DIV'|| obj.tagName=='INPUT' || obj.tagName=='TEXTAREA' || obj.tagName=='SELECT') ) )){
							if(obj.cmptype=='flexbox'){
								obj = $("#"+aids[i]);//下拉框
								//判断是否提交 _md5list 防篡改
								var tmd5 = $("#"+aids[i]+"_md5list");
								if(tmd5[0]){
									if(queryStr=="")
										queryStr += $("#"+aids[i]+"_md5list").taserialize(isIncludeNullFields);
									else
										queryStr += "&"+$("#"+aids[i]+"_md5list").taserialize(isIncludeNullFields);
								}
							}	
							for(var j=0;j<aids.length;j++){//对ids进行校验，不能父子嵌套
								if(aids[j]==null || aids[j]=='')continue;
								var obj2 = Base.getObj(aids[j]);
								if(obj2.cmptype=='flexbox')obj2 = $("#"+aids[j]);
								if(i != j && obj2.cmptype!='datagrid'){//找到其他对象
									if($(obj).has($(obj2)).length>0){
										alert(aids[j]+"对象在"+aids[i]+"对象里面，指定提交的元素id不能有包含与被包含关系");
										return false;
									}
									if($(obj2).has($(obj)).length>0){
										alert(aids[i]+"对象在"+aids[j]+"对象里面，指定提交的元素id不能有包含与被包含关系");
										return false;
									}
								}
							}
							if ($obj.hasClass("amountfield")){
								if(queryStr=="")
									queryStr += $("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);
								else
									queryStr += "&"+$("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);	
							}
							if(queryStr=="")
								queryStr += $("#"+aids[i]).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("#"+aids[i]).taserialize(isIncludeNullFields);
						 		//queryStr = queryStr.replaceAll("%0D%0A","\r\n");
						}
						else if($obj.hasClass("amountfield")){
							if(queryStr=="")
								queryStr += $("#"+aids[i]+"_hidden").taserializeisIncludeNullFields();
							else
								queryStr += "&"+$("#"+aids[i]+"_hidden").taserialize(isIncludeNullFields);	
							//obj=$("#" + aids[i] + "_hidden");//金额框
						}
						else if(obj.cmptype=='datagrid'){
							datagridids.push(new String(aids[i]));
							if(queryStr=="")
								queryStr += $("#"+aids[i]).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("#"+aids[i]).taserialize(isIncludeNullFields);	
							aids[i]=null;//.splice(i,1);//删除当前id
						}
						else{
							alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
							return false;
						}
					}
				}		
				
				//传表格隐藏信息
				$("div.datagrid").each(function(){
					for(var i=0;i<aids.length;i++){
						if(aids[i] && aids[i]!='' && $("#"+aids[i]).has($(this)).length==0){//不在adis包含的datagrid
							if(queryStr=="")
								queryStr = $("input:hidden[name^=gridInfo]",$(this)).taserialize(isIncludeNullFields);
							else
								queryStr += "&"+$("input:hidden[name^=gridInfo]",$(this)).taserialize(isIncludeNullFields);
						}
						break;// for 循环无用
					}
				});
				//传递表格 added/selected/removed/modified 数据
				for(var i=0;i<datagridids.length;i++){
					var p = {};
					//alert([datagridids[i],Base.getGridSelectedRows(datagridids[i]),Ta.util.obj2string(Base.getGridSelectedRows(datagridids[i]))].join(','));
					p["gridInfo['"+datagridids[i]+"_selected']"] = Ta.util.obj2string(Base.getGridSelectedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_modified']"] = Ta.util.obj2string(Base.getGridModifiedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_removed']"] = Ta.util.obj2string(Base.getGridRemovedRows(datagridids[i]));
					p["gridInfo['"+datagridids[i]+"_added']"] = Ta.util.obj2string(Base.getGridAddedRows(datagridids[i]));
					if(queryStr==""){
						queryStr = jQuery.param(p);
					}else{
						queryStr += "&"+jQuery.param(p);
					}
				}
				if (document._dataSubmitStore && document._dataSubmitStore.length > 0) {
					parameter = $.extend(parameter, document._dataSubmitStore[0]);
				}
				//对queryStr处理
				var parameterOld = Base.compareOldData(aids);
				parameter = $.extend(parameter, parameterOld);
				if(parameter){
					if(queryStr==""){
						queryStr = jQuery.param(parameter);
					}else{
						queryStr += "&"+jQuery.param(parameter);
					}
				}
				return queryStr;
			}


		function _dealdata(data){
				//只有json格式的时候才处理
				if(typeof data != "object")return;
				
				//有fieldData数据的时候
				if(data.fieldData){
//					var params = {},newData = data.fieldData;
//					for(var i in newData){
//						if(i.indexOf("_sel_") == 0){
//							params[i.substring(5)] = newData[i];
//						}else{
//							params[i] = newData[i];
//						}
//					}
					Base.setValue(data.fieldData);
					//data.fieldData = params;
				}
				//有validateErrors数据
				if(data.validateErrors){
					var focus = null,_errors = data.validateErrors;
					for(var fieldId in _errors){
						if(!focus)
							focus = fieldId;
						Base.setInvalidField(fieldId,_errors[fieldId]);
					}
					//如果后台没有设置focus，并且有validateErrors数据的时候就把焦点置于第一个错误的地方
					if(focus && !data.focus){
						data.focus = focus;
					}
				}
				//有lists数据
				if(data.lists){
					var _lists = data.lists;
					for(var list in _lists){
						if (list == "_dataSubmitStore")
							document._dataSubmitStore = _lists[list];
						if (list == "_oldValueObj")
							document._oldValueObj = _lists[list]['list'];
						Base._setGridData(list,_lists[list]);
					}
				}
				//有operation数据
				if(data.operation){
					var _operation = data.operation
					for(var i=0;i<_operation.length;i++){
						var op = _operation[i];
						switch(op.type){
							case 'readonly':
								Base.setReadOnly(op.ids);
								break;
							case 'enable':
								Base.setEnable(op.ids);
								break;
							case 'disabled':
								Base.setDisabled(op.ids);
								break;					
							case 'select_tab':
								Base.selectTab(op.ids);
								break;				
							case 'hide':
								Base.hideObj(op.ids);
								break;
							case 'show':
								Base.showObj(op.ids);
								break;
							case 'unvisible':
								Base.hideObj(op.ids,true);
								break;					
							case 'resetForm':
								Base.resetForm(op.ids[0]);
								break;
							case 'required':
								Base.setRequired(op.ids);
								break;
							case 'disrequired':
								Base.setDisRequired(op.ids);
								break;						
						}
					}
				}
				
				//有msg
				if(data.msg){
					var focus = null;
					if(data.focus){
						focus = function(_fieldId){
							return function(){Base.focus(_fieldId,100);}
						}(data.focus);
					}
					var msg = data.msg;
					var developMode = Base.globvar.developMode;
					if(developMode){
						if(data.errorDetail){
							msg += "&nbsp;&nbsp;&nbsp;<div><a onClick=\"$('<div style=overflow:auto>'+$('#_expwinerrmsg').html()+'</div>').appendTo('body').window({width:600,height:400,title:'详细信息'})\">[查看详细]</a></div><div id='_expwinerrmsg' style='display:none'><hr>"+data.errorDetail+"</div>";
						}
					}
					Base.alert(msg,data.success?'success':'error',focus);
					
				}
				if (data.msgBox) {
					var focus = null;
					if(data.focus){
						focus = function(_fieldId){
							return function(){Base.focus(_fieldId,100);}
						}(data.focus);
					}
					var msg = data.msgBox.msg;
					if(data.errorDetail){
						msg += "&nbsp;&nbsp;&nbsp;<div><a onClick=\"$('<div style=overflow:auto>'+$('#_expwinerrmsg').html()+'</div>').appendTo('body').window({width:600,height:400,title:'详细信息'})\">[查看详细]</a></div><div id='_expwinerrmsg' style='display:none'><hr>"+data.errorDetail+"</div>";
					}
					Base.alert(msg,data.msgBox.msgType,focus);
				}
				//没有msg，但是有focus
				if(!data.msg && data.focus){
					Base.focus(data.focus,50);
				}
				//有topMsg
				if(data.topTipMsg){
					var topTip = data.topTipMsg;
					Base.msgTopTip(topTip.topMsg,topTip.time,topTip.width,topTip.height);
				}else if(data.topMsg && !data.topTipMsg){
					Base.msgTopTip(data.topMsg);
				}
			}
			
			
		function compareOldData(ids){
			var submitparam = {};
			
			if (document._oldValueObj && document._oldValueObj.length >0){
				var oldArray = document._oldValueObj;
				for (var i = 0; i < oldArray.length; i ++) {
					for (var j = 0; j < ids.length; j ++) {
						var $obj = $("#" + ids);
						if (ids[j] == oldArray[i].__id) {
							if ($obj.val() != oldArray[i]["ovDto['" + oldArray[i].__id + "']"]) {
								submitparam = $.extend(submitparam, oldArray[i]);
							}
						} else if ($obj.has(oldArray[i].__id)){
							if ($("#" + oldArray[i].__id).val() != oldArray[i]["ovDto['" + oldArray[i].__id + "']"]) {
								submitparam = $.extend(submitparam, oldArray[i]);
							}
						}
					}
				}
			} else {
				return {};
			}
			delete submitparam.__id;
			return submitparam;
		}

	}
}));

