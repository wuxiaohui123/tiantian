/**
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
		};
		
		
	}
}));

