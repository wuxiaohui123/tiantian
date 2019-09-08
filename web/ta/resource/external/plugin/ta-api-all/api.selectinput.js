/**==================对下拉框的操作==============*/
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
		};
		
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
