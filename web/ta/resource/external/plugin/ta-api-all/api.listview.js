/**
 * 
 * @param factory
 */
(function(factory) {
	if (typeof define === 'function' && define.amd) {
		define([ "jquery", "TaJsUtil" ], factory);
	} else {
		factory(jQuery);
	}
}(function($) {
	$.extend(true, window, {
		Base : core()
	});

	function core() {
		/**
		 * 
		 */
		function setListViewData(viewId, param) {
			$("#" + viewId + "_listview").listView("setListData", param);
		}

		function refreshListView(viewId) {
			$("#" + viewId + "_listview").listView("refreshDataView");
		}

		return {
			setListViewData : setListViewData,
			refreshListView : refreshListView
		};
	}
}));