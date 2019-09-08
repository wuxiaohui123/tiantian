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

