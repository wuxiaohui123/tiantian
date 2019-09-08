(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
    $.extend(true, window, {
        YH_Window: { 
            Window_Message: WindowMessage
        }
    }); 

    function WindowMessage(options) {
        
        var defaults = {
            height: 200,
            width: 300,
            type: 'slide',
            title: '消息提示',
            text: "无消息",
            speed: 1000
        };
        function init() {
            options = $.extend({},defaults,options); //默认option和自订option结合
            _createMessageWindow(options);
            if(options.display == "false"){
           		$('.message-container').css('display','none');
           }else
            _showType(options.type,options.id);
            $('#message_close').click(function(){
            	_hideType(options.type,options.id);
            });
            if (typeof options.textCallback == "function") { 
       			var callbackData = options.textCallback();
       			$('#message_content').html(callbackData);
       		}
       		if(!isNaN(options.closeTime)){
       			setTimeout(function(){
	       			_hideType(options.type,options.id);
	       		},options.closeTime);
       		}
       		return this;
        }
        function _createMessageWindow(options) {
        	$(document.body)
				.prepend('<div id="'
						+ options.id +'" class="message-container">'
						+ '<div class="message-head">'
						+ '<span id="message_close" class="message-close">×</span>'
						+ '<div class="message-headbody">'
						+ options.title
						+ '</div><div style="clear:both;"></div></div> <div class="message-body"><div id="message_content" >'
						+ options.text + '</div></div></div>');
			$('.message-container').css({'width':options.width,'height':options.height});
			$('#message_content').css({'width':options.width- 17,'height':options.height-50});
        }
        function _showType(type,id){
        	switch (type) {
			case 'slide':
				$("#"+id).slideDown(options.speed);
				break;
			case 'fade':
				$("#"+id).fadeIn(options.speed);
				break;
			case 'show':
				$("#"+id).show(options.speed);
				break;
			default:
				$("#"+id).slideDown(options.speed);
				break;
			}
        }
        function _hideType(type,id){
        	switch (type) {
			case 'slide':
				$("#"+id).slideUp(options.speed);
				break;
			case 'fade':
				$("#"+id).fadeOut(options.speed);
				break;
			case 'show':
				$("#"+id).hide(options.speed);
				break;
			default:
				$("#"+id).slideUp(options.speed);
				break;
			}
        }
        $.extend(this, { 
        	"cmptype":'messageWindow', //组建类型
            "UIVersion": "2.0a1" //组建版本
        });
        init();
    }
})); 

