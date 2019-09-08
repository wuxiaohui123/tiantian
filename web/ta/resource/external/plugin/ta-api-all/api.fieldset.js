/**
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
		};
	}
}
));

