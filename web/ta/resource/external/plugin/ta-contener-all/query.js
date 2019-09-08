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

