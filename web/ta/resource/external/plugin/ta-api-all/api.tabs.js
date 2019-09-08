/**
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
		};
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
				});
				$(this).find(".tabs-panels").children().each(function(){
					var tab = {};
					tab.html = $(this).html();
					tab.layout = $(this).attr("layout");
					tab.cols = $(this).attr("cols");
					divs.push(tab);
				});
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
			});
		}

	}
}));
