(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){ 
    $.extend(true, window, {
        KeyBoard: { 
            SoftKeyBoard: YH_SoftKeyBoard
        }
    }); 

    function YH_SoftKeyBoard(id,options) {
        
        var defaults = {
            title: '软键盘'
        };
		var $writeBox=$('#'+id),
		shift = false,  
        capslock = false;  
        function init() {
            options = $.extend({},defaults,options); //默认option和自订opetion结合
            //检查输入框是否为只读
            var $input = $("#"+id);
		    if($input.hasClass('readonly') || $input.hasClass('disabled')){
		    	return;
		    }
            _createSoftKeyBoard(id);
            $('#keyboard_ul li').hover(function() {  
	        	$(this).addClass('softkeyboard_hover');  
		    }, function() {  
		        $(this).removeClass('softkeyboard_hover');  
		    }).click(function() {  
		        var $this = $(this),  
		       
		        charater = $this.html();  
		        // 一键两意  
		        if($this.hasClass('softkeyboard_symbol')) charater = $('span:visible', $this).html();  
		          
		        // Button detele   
		        if($this.hasClass('softkeyboard_delete')) {  
		            var html = $writeBox.val();  
		            $writeBox.val(html.substring(0, html.length-1));  
		            return false;  
		        };  
		          
		        // Button tab  
		        if($this.hasClass('softkeyboard_tab')) return;  
		          
		        // Button capslock  
		        if($this.hasClass('softkeyboard_capslock')) {  
		            $('.softkeyboard_letter').toggleClass('softkeyboard_uppercase');  
		            capslock = true;  
		            return false;  
		        };  
		          
		        // Button enter  or close
		        if($this.hasClass('softkeyboard_enter') || $this.hasClass('softkeyboard_close')) {
		        	$("#softkeyboard_container").remove(); 
		        	Base.focus(id,300);
		        	return;
		        }  
		          
		        // Button shift  
		        if($this.hasClass('softkeyboard_left-shift') || $this.hasClass('softkeyboard_right-shift')) {  
		            $('.softkeyboard_letter').toggleClass('softkeyboard_uppercase');  
		            $('.softkeyboard_symbol span').toggle();  
		            shift = (shift === true) ? false : true;  
		            capslock = false;  
		            return false;  
		        };  
		          
		        // Button space  
		        if($this.hasClass('softkeyboard_space')) charater = ' ';  
		          
		        // 转换为大写  
		        if($this.hasClass('softkeyboard_uppercase')) charater = charater.toUpperCase();  
		          
		        // 输出所按的键值  
		        $("#"+id).val(Base.getValue(id)+charater);
//		        Base.setValue(id,Base.getValue(id)+charater);
		    });  
        }
        function _createSoftKeyBoard(id){
        	var str = '<div id="softkeyboard_container"> '+ 
    						'<ul id="keyboard_ul">'+  
        						'<li class="softkeyboard_symbol"><span class="off">`</span><span class="softkeyboard_on">~</span></li>'+  
        						'<li class="softkeyboard_symbol"><span class="off">1</span><span class="softkeyboard_on">!</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">2</span><span class="softkeyboard_on">@</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">3</span><span class="softkeyboard_on">#</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">4</span><span class="softkeyboard_on">$</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">5</span><span class="softkeyboard_on">%</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">6</span><span class="softkeyboard_on">^</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">7</span><span class="softkeyboard_on">&</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">8</span><span class="softkeyboard_on">*</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">9</span><span class="softkeyboard_on">(</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">0</span><span class="softkeyboard_on">)</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">-</span><span class="softkeyboard_on">_</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">=</span><span class="softkeyboard_on">+</span></li>'+
						        '<li class="softkeyboard_delete softkeyboard_lastitem">删除</li>'+  
						        '<li class="softkeyboard_tab">tab</li> '+ 
						        '<li class="softkeyboard_letter">q</li>'+  
						        '<li class="softkeyboard_letter">w</li>  '+
						        '<li class="softkeyboard_letter">e</li>'+  
						        '<li class="softkeyboard_letter">r</li> '+ 
						        '<li class="softkeyboard_letter">t</li>'+  
						        '<li class="softkeyboard_letter">y</li> '+ 
						        '<li class="softkeyboard_letter">u</li>'+  
						        '<li class="softkeyboard_letter">i</li>'+  
						        '<li class="softkeyboard_letter">o</li>'+  
						        '<li class="softkeyboard_letter">p</li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">[</span><span class="softkeyboard_on">{</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">]</span><span class="softkeyboard_on">}</span></li> '+ 
						        '<li class="softkeyboard_symbol softkeyboard_lastitem"><span class="off">\</span><span class="softkeyboard_on">|</span></li>'+  
						        '<li class="softkeyboard_capslock">大小写</li>'+  
						        '<li class="softkeyboard_letter">a</li>'+  
						        '<li class="softkeyboard_letter">s</li>'+  
						        '<li class="softkeyboard_letter">d</li>'+  
						        '<li class="softkeyboard_letter">f</li>'+  
						        '<li class="softkeyboard_letter">g</li>'+  
						        '<li class="softkeyboard_letter">h</li>'+  
						        '<li class="softkeyboard_letter">j</li>'+  
						        '<li class="softkeyboard_letter">k</li>'+  
						        '<li class="softkeyboard_letter">l</li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">;</span><span class="softkeyboard_on">:</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">\'</span><span class="softkeyboard_on">"</span></li>'+  
						        '<li class="softkeyboard_enter softkeyboard_lastitem" title="关闭软键盘">回车</li>'+  
						        '<li class="softkeyboard_left-shift" title="大小写及符号切换">shift</li>  '+
						        '<li class="softkeyboard_letter">z</li>'+  
						        '<li class="softkeyboard_letter">x</li>  '+
						        '<li class="softkeyboard_letter">c</li>'+  
						        '<li class="softkeyboard_letter">v</li> '+ 
						        '<li class="softkeyboard_letter">b</li>'+  
						        '<li class="softkeyboard_letter">n</li>'+  
						        '<li class="softkeyboard_letter">m</li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">,</span><span class="softkeyboard_on"><</span></li>'+  
						        '<li class="softkeyboard_symbol"><span class="off">.</span><span class="softkeyboard_on">></span></li>  '+
						        '<li class="softkeyboard_symbol"><span class="off">/</span><span class="softkeyboard_on">?</span></li>'+  
						        '<li class="softkeyboard_right-shift softkeyboard_lastitem" title="大小写及符号切换">shift</li>  '+
						        '<li class="softkeyboard_space softkeyboard_lastitem">space</li>'+  
						        '<li class="softkeyboard_close" title="关闭软键盘">关闭</li>'+
						    '</ul>'+  
						'</div>';
						$('#'+id).after(str);   
        }
        
        $.extend(this, { 
        	"cmptype":'softKeyBoard', //组建类型
            "UIVersion": "2.0a1" //组建版本
        });
        init();
    }
})); 

