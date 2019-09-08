(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	function setTitle(target,title,asHtml){
		if (asHtml == true) $(">div >div.panel-title",$(target)).html(title);
		else $(">div >div.panel-title",$(target)).text(title);
	}
	function collapsePanel(target){
		var $panel = $(target);
		var tool = $('>div.panel-header .panel-tool-collapse',$panel);
		if(!tool)return;
		$panel.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideUp(200);
		tool.toggleClass('panel-tool-expand');
	}
	
	function expandPanel(target){
		var $panel = $(target);
		var tool = $('>div.panel-header .panel-tool-collapse',$panel);
		if(tool.hasClass('panel-tool-expand'))return;
		$panel.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideDown(200);		
		tool.toggleClass('panel-tool-expand');
	}
	/**
	 * 让panel的body部分自动随父亲容器的宽高伸展
	 */
	function fitPanel(target){
		var $panel = $(target);
		var $fitContent = $(">div.panel-body",$panel);
		if($fitContent.length==0){
			$fitContent = $panel;
		}else{
			$fitContent = $($fitContent[0]);
		}
		var hh_ = $(">div.panel-header",$panel).outerHeight(true);
		var headerHeight = hh_ ? hh_ : 0;
		var bp_ = $(">div.panel-button",$panel).outerHeight(true);
		var bpHeight = bp_ ? bp_-2: 0;
		var pt_ = $(">div.panel-toolbar",$panel).outerHeight(true);
		var ptHeight = pt_ ? pt_: 0;
		
		var h = 0;
		
        var panelparent = $panel.parent(); 
//        if(panelparent.hasClass("ez-fl")){//当设置有cols属性时，需要向上一层取父容器
//        	panelparent = $panel.parent().parent();
//        }
        h = panelparent.height();
        if (panelparent[0].tagName.toLowerCase() == "body") { 
            h = $(window).height();
            //h -= parseInt($('body').css('paddingTop'));
            h -= parseInt($('body').css('paddingBottom'));
            //h -= parseInt($('body').css('marginTop'));
            h -= parseInt($('body').css('marginBottom'));            
            h -= $panel.offset().top;
            var mb = $panel.css('marginBottom');
            h -= (mb=='auto'?0:parseInt(mb));
        }else if(panelparent.hasClass('window-body')){
        		var windowTop = parseInt($panel.offsetParent().css('top'));
	        	h -= $panel.offset().top - windowTop -24;
	        	var mt = $panel.css('marginTop');
	        	h += (mt=='auto'?0:parseInt(mt));
        }else{
        	if(panelparent.css('position')=='absolute'){
	        	h -= $panel.position().top-parseInt(panelparent.css('paddingTop'));
	        	if(panelparent.parent()[0].tagName.toLowerCase() == "body"){
	        		h -= panelparent.position().top-parseInt($('body').css('paddingTop'));
	        	}
	        }else{
	        	var pall = $panel.prevAll(':visible').not('#pageloading');
	        	if(pall.length>0){
	        		pall.each(function(){
	        			h -= $(this).outerHeight(true);
	        		});
	        	}
	        	var mt = $panel.css('marginTop');
		        h -= (mt=='auto'?0:parseInt(mt));
		        var mb = $panel.css('marginBottom');
		        h -= (mb=='auto'?0:parseInt(mb));
		        if($panel.parents(".l-layout-content").size()==0){
		        	var pb = panelparent.css('paddingBottom');
			        h -= (pb=='auto'?0:parseInt(pb));
		        }
	        }

        }
        h -= parseInt($fitContent.css('paddingTop'));
        h -= parseInt($fitContent.css('paddingBottom'));

        var opts = $.data(target, 'panel');
        
       // h -= 2;//去除边框
        h -= opts.heightDiff;
        h -= 2;
//        alert([h,headerHeight,bpHeight,ptHeight].join(','))
        var minHeight =  Number($(target).attr('minHeight'));
        
        if (h-headerHeight-bpHeight-ptHeight < minHeight){
        	$fitContent.height(minHeight - headerHeight-bpHeight-ptHeight);
        } else 
        	$fitContent.height(h-headerHeight-bpHeight-ptHeight);
       		
       	//$fitContent.triggerHandler('_resize');
//       alert('panel resize  '+$panel[0].id+"   "+$('>form',$fitContent).length);
        $('>div[fit=true] ,>form[fit=true]',$fitContent).triggerHandler('_resize');
        if($fitContent.hasClass('l-layout'))$fitContent.triggerHandler('_resize');//panel直接作为border布局
	}
	function mask(target,param){
	
	}
	$.fn.tauipanel = function(options, param, asHtml){
		if (typeof options == 'string'){
			switch(options){
			case 'setTitle':
				return this.each(function(){
					setTitle(this, param,asHtml);
				});
			case 'collapse':
				return this.each(function(){
					collapsePanel(this, param);
				});
			case 'expand':
				return this.each(function(){
					expandPanel(this, param);
				});
			case 'destroy':
				return this.each(function(){
					destroyPanel(this, param);
				});
			case 'resize':
				return this.each(function(){
					fitPanel(this);
				});
			
			case 'mask':
				return this.each(function(){
					mask(this,param);
				});
			}
		}

		options = options || {};
		return this.each(function(){
				var opts;
				var t = $(this);
				opts = $.extend({}, $.fn.tauipanel.defaults, {
					href: t.attr('href'),
					onLoad: (t.attr('onLoad') ? t.attr('onLoad'): undefined),
					fit:(t.attr('fit')=='true' ? true:false),
					heightDiff: (t.attr('heightDiff') || 0),
					minHeight : (t.attr('minHeight'))
				}, options);
				$.data(this, 'panel', opts);
				if(opts.fit){
					fitPanel(this);
					var $panel = $(this);
					$panel.bind('_resize',function(){
						fitPanel(t[0]);
					});
					if(this.parentNode.tagName.toLowerCase()=='body'){
						$(window).unbind('.tauipanel').bind('resize.tauipanel', function(){
								$panel.triggerHandler('_resize');
						});
					}
//					$(window).bind('resize.tauipanel',function(){alert('tabuipanel on resize')
//        				fitPanel(t[0]);
//        			});
				}
	    	 	$("> div.panel-header > div.panel-tool > div.panel-tool-collapse",t).mouseover(function(){
						$(this).addClass('panel-tool-over');
					}).mouseout(function(){
						$(this).removeClass('panel-tool-over');
					}).click(function(){
						var flag = $(this).hasClass("panel-tool-expand");
						if (!flag) {
							flag = false;
							var $p = $(this.parentNode.parentNode.parentNode);
							$p.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideUp(100);
							setTimeout(function(){
								$p.siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
							},100);
						} else {
							flag = true;
							var $p = $(this.parentNode.parentNode.parentNode);
							$p.find(">div.panel-toolbar,>div.panel-body,>div.panel-button").slideDown(100);
							setTimeout(function(){
								$p.siblings('div[fit=true],form[fit=true]').triggerHandler('_resize');
							},100);
						}
						$(this).toggleClass('panel-tool-expand');
					});
	    	 	//李从波添加缩放控制代码2012.09.07
	    		var  originWidth=0;//初始高宽  z-index  top  left   margin  bodyHeight
	    		var  originHeight=0;
	    		var  originZindex=0;
	    		var  originTop=0;
	    		var  originLeft=0;
	    		var  originMarginTop=0;
	    		var  originMarginBottom=0;
	    		var  originMarginLeft=0;
	    		var  originMarginRight=0;
	    		var  originBodyHeight=0;
	    		var  maxWidth=0;	//最大高宽
	    		var  maxHeight=0;
	    		var  panelBodyBorder=0;//左右边框宽度
	    		var  panelToolbarHeight=0;//工具条的高度
	    		var  panelButtonbarHeight=0;//按钮条的高度
	    		var  isBorderLayout=false;//panel所在页面是否使用了border布局
	    		var  isWidthAuto=false;//宽度是否根据父容器自动计算
	    		var  isHeightAuto=false;//高度是否根据父容器自动计算
	    		var  $handler=null;//border布局下的工具条
	    		var  $borderContainer=$("body").find("div.l-layout-left,div.l-layout-right,div.l-layout-center,div.l-layout-top,div.l-layout-bottom");//border布局下的区域
	    	 	$("> div.panel-header > div.panel-tool > div.panel-tool-max",t).mouseover(function(){
					  $(this).addClass('panel-tool-over');
				}).mouseout(function(){
					  $(this).removeClass('panel-tool-over');
				}).click(function(){
					  var flag = $(this).hasClass("panel-tool-restore");
					  if (!flag) {
							  var $p = $(this.parentNode.parentNode.parentNode);
							  var  $panelBody= $p.find(">div.panel-body");
				    		if(originWidth==0  &&  originHeight==0 ){//初始化 初始高宽  top  left margin z-index等
				    			var  $panelToolbar=$p.find(">div.panel-toolbar");
								var  $panelButtonbar=$p.find(">div.panel-button");
								originWidth=$p.width();
								originHeight=$p.height();
								originTop=$p.css("top");
					    		originLeft=$p.css("left");
								originZindex=$p.css("z-index");
								originMarginTop=$p.css("margin-top");
								originMarginBottom=$p.css("margin-bottom");
								originMarginLeft=$p.css("margin-left");
								originMarginRight=$p.css("margin-right");
								originBodyHeight=$panelBody.height();
								panelBodyBorder=parseInt($panelBody.css("border-left-width").replace("px",""))+parseInt($panelBody.css("border-right-width").replace("px",""));
						    	if(($p.width()+parseInt(originMarginLeft.replace("px",""))+parseInt(originMarginRight.replace("px","")))==$p.parent().width()){
						    		isWidthAuto=true;
						    	}
						    	if($p.attr("fit")=="true"){
						    		isHeightAuto=true;
						    	}
						    	if($panelToolbar.length>0){
						    		panelToolbarHeight=31;
					        	  }
					        	if($panelButtonbar.length>0){
					        		panelButtonbarHeight=40;
					        	}
							}
				    		maxWidth=$(window).width()-2;//获取当前最大高宽
					    	maxHeight=$(window).height()-2;
							$handler=$("body").find("div.l-layout-drophandle-left,div.l-layout-drophandle-right,div.l-layout-drophandle-top,div.l-layout-drophandle-bottom,div.l-layout-collapse-left,div.l-layout-collapse-right").not(":hidden");
							if($borderContainer.length>0 || $handler.length>0){
								$handler.hide();//隐藏border布局下的工具条
								$borderContainer.css({position:"static"});//设置所有的boder容器position 为static
								isBorderLayout=true;
							}
							if(panelToolbarHeight>0  ||  panelButtonbarHeight>0 )$p.removeClass("panelnomargin");//移除no-magin 样式
							var  pIndex=(originZindex=="auto")?0:originZindex;
							$p.css({ position: "fixed",zIndex:9002>pIndex?9002:pIndex,background:"#fcfdfd"});//设置position为fixed 修改z-index显示在最前面  设置背景色盖住下方内容
							$p.animate({//动画开始
				             width:maxWidth,
				             height:maxHeight,
				             top:isBorderLayout==true?-2:(isHeightAuto==true?-1:-2),
				             left:1,
				             marginTop:3,
				             marginBottom:3,
				             marginLeft:0,
				             marginRight:0
				          },{duration:100, complete:function(){
				        	  var  panelBodyHeight=maxHeight-25-panelToolbarHeight-panelButtonbarHeight;//减去标题、 toolbar、 buttonbar的高度得到panelbody高度
				        	  $panelBody.css({height:panelBodyHeight,width:maxWidth-panelBodyBorder});
				        	  $panelBody.find('div[fit=true],form[fit=true],div.grid,div.panel').triggerHandler('_resize');//处理子容器自适应高度
				          }});
					  } else{
						  	var $p = $(this.parentNode.parentNode.parentNode);
							var $panelBody= $p.find(">div.panel-body");
							var pTop=(originTop=="auto")?0:originTop;
							var pLeft=(originLeft=="auto")?0:originLeft;
							$p.css({zIndex:originZindex});
							$p.animate({//动画开始
		   		              width:originWidth,
		   		              height:originHeight,
		   		              top:pTop,
				              left:pLeft,
				              marginTop:originMarginTop,
				              marginBottom:originMarginBottom,
				              marginLeft:originMarginLeft,
					          marginRight:originMarginRight
					          },{duration:100, complete:function(){
					        	  if($borderContainer || $handler){
					        		  $borderContainer.css({position:"absolute"});//恢复原有的设置
					        		  $handler.show();//显示border布局下的工具条
					        	  }
					            if(panelToolbarHeight>0  ||  panelButtonbarHeight>0 )$p.addClass("panelnomargin");//恢复no-margin样式
				        	    $p.css({ position: "relative",background:"none",top:originTop,left:originLeft,width: isWidthAuto==true?"auto":originWidth,height:isHeightAuto==true?"auto":originHeight});//处理缩放后页面缩放panel失效的问题		        		
				        	    $panelBody.css({height:originBodyHeight,width:isWidthAuto==true?"auto":originWidth-panelBodyBorder});//处理panelBody适应父容器高宽
					        	$panelBody.find('div[fit=true],form[fit=true],div.grid,div.panel').triggerHandler('_resize');//处理子容器自适应高度
					          }});
					  }
					  $(this).toggleClass('panel-tool-restore');
				})
		});
	};
	
	$.fn.tauipanel.defaults = {
		href: null,
		onLoad: function(){},
		fit:false,
		heightDiff:0
	};
}));
