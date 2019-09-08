/**
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