(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","ztree.excheck.min", "ztree.exedit", "ztree.exhide.min" ], factory);
	} else {
		factory(jQuery);
	}
}(function($){
    $.extend(true, window, {
        SelectTree:SelectTree
    }); 
    function SelectTree(id,targetDESC,treeId, options) {
        var defaults = {
            async: true,
            asyncParam:['id'],
            nameKey:'name',
            idKey:'id',
            parentKey:'pId',
            nodesData:''
        };
        var $selectTree_ul = $("<ul class='ztree'></ul>"),$selectTree_container;
        if(options.cssStyle){
        	$selectTree_container = $("<div style=\""+options.cssStyle+"\"></div>");
        }else{
        	$selectTree_container = $("<div></div>");
        }
        var selectTree_container_id = id;
        function init() {
            options = $.extend({},defaults,options);
            if(targetDESC != "" && targetDESC != undefined && treeId != "" && treeId != undefined){
            	$selectTree_ul.attr('id',treeId);
            	$selectTree_container.attr('id',selectTree_container_id).addClass('selectTreeContainer ffb_163').append($selectTree_ul);
            	if(options.cssClass){
            		$selectTree_container.addClass(options.cssClass);
            	}
            	if(options.height){
            		$selectTree_container.css('height',options.height);
            	}
            	if(options.width){
            		$selectTree_container.css('width',options.width);
            	}
//            	$('#'+targetDESC).after($selectTree_container);
            	$("body").append($selectTree_container);
            }
            var view = {
        		selectedMulti:false,
				autoCancelSelected:false
			};
            if(options.fontCss){
            	view.fontCss = options.fontCss;
            }
            _createSelectTree(view);
            return this;
        }
        function _createSelectTree(view) {
        	var setting = {
					view:view,
					async:{
						url:options.url,
						autoParam:options.asyncParam,
					  	enable:options.async
					},
					data:{
						keep:{
							parent:false,
							leaf:false
						},
						key:{
							children:"children",
							name:options.nameKey
						},
						simpleData:{
							enable:true,
							idKey:options.idKey,
							pIdKey:options.parentKey,
							rootPId:""
						}
					},
					callback:{
						beforeClick:options.selectTreeBeforeClick,
						onClick:options.selectTreeCallback,
						"end":null
					}
				};
			var nodesData = options.nodesData;
			$.fn.zTree.init($("#"+treeId), setting, nodesData);
        }
        $.extend(this, { 
        	"cmptype":'selectTree'
        });
        init();
    }
}));

