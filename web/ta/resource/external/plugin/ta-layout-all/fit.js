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
