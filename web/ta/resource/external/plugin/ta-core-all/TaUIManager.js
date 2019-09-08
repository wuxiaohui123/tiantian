
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Ta: { 
           "core": core()
        }
    });
	
	function core(){
		return {
			"TaUICreater" :TaUICreater(),
			"TaUIManager" :TaUIManager()
		};
	}
	
	function TaUIManager(){
		var uis = new Ta.util.Map();
		function register(id,uiObj){
			if(this.getCmp(id)){
				alert('注意:TaUIManager 里已经注册有id为['+id+']的对象');
			}
			uis.put(id,uiObj);
		}
		/**
		 * 获取对象
		 */
		function getCmp(id){
			return uis.get(id);
		}
		/**
		 * 移除注册的对象
		 */
		function unregister(id){
			return uis.remove(id);
		}
		/**
		 * 所有组件的id
		 */
		function keys(){
			return uis.keys();
		}
		
		function removeObjInCantainer($contaner){
			var ids = uis.keys();
			if(ids){
				for(var i=0;i<ids.length;i++){
					var _obj = $("#"+ids[i],$contaner)[0];
					if(_obj){ 
						var _tempObj = uis.get(ids[i]);                                         //lins 20120821
						if (_tempObj.cmptype == "datagrid") _tempObj.destroy();//lins 20120821 添加表格销毁
						this.unregister(ids[i]);
					}
				}
			}
		}
		return {
			"register" :register,
			"getCmp" :getCmp,
			"unregister" :unregister,
			"keys" :keys,
			"removeObjInCantainer" : removeObjInCantainer
		};
	};
	
	
	/**
	 * 用户标签生成的组件创建代码添加到这个里面去，然后框架在talayout执行后统一调用create方法。
	 *标签生成的组件大致如下：
	 * 
	 *Ta.core.TaUICreater.addUI(function(){
	 *	var columns = [];
	 *  //.......
	 *	var grid = new SlickGrid("#mygrid",columns,options);
	 *	TaUIManager.register(grid);
	 *});
	 */
	function TaUICreater(){
		var uiForCreate = [];
		
		function addUI(fn) {
			if(uiForCreate)
				uiForCreate.push(fn);
		}
		function removeAllUI(){
			
		}
		function create() {
			if(uiForCreate){
	        	for (var i = 0; i < uiForCreate.length; i++){
	            	if(uiForCreate[i]){
	           			uiForCreate[i]();
	            		uiForCreate[i] = null;
	            	}
	        	}
	    	}
		}
		
		return {
			"create" :create,
			"removeAllUI" :removeAllUI,
			"addUI" :addUI
		};
	};
}));

