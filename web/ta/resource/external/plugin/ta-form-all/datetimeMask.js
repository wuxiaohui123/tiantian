(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", "WdatePicker"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
     function MaskXtIpt(a, o,opt,type)
        	{
        		o && (o = $(o)) || (o = this);
        		opt || (opt = {});
        		var n, i, j, k, b, s ,x, lp;
        		a || (a = []);
        		
        		o.keyup(function(evt)
        		{
        			n = evt.keyCode;
        			//如果日期框为只读,则不允许进行任何操作
        			if(o.attr('readonly') == true || o.attr('readonly') == 'readonly'){
        				return;
        			}
        			//如果是小键盘输入,则把小键盘转换成大键盘
       			 	if(96<=n && n<=105){
       			 		n = n-48;
       			 	}
        			evt || (evt = window.event || {keyCode:0});
        			 	if(evt.ctrlKey || evt.shiftKey ||evt.altKey|| evt.metaKey){
        			 		return true;
        			 	}
        			 	if(n==13)return true;//回车
        			 	if(n==78){//n 键
        			 		switch(type){
        			 		  case 1:
        			 			this.value = Ta.util.getCurDate();break;
        			 		  case 2:
        			 		    this.value = Ta.util.getCurDateTime();break;
        			 		  case 3:
        			 		    this.value = Ta.util.getCurIssue();break;
        			 		  case 4:
        			 				this.value = String(Ta.util.getCurDateTime()).substring(11,19);break;
        			 		  case 5:
        			 				this.value = Ta.util.getCurDateMonth();break;
        			 		  case 6:
      			 				    this.value = Ta.util.getCurDateYear();break;
        			 		}
        			 		$this = $(this);
        			 		setTimeout(function(){
        			 			$this.focus();
        			 		}, 10);
        			 		return false;
        			 	}
        			 	s = String(this.value)//处理直接月为2变成02,日期4变为04
        			 	if(s.length == 6 && !o.hasClass("issuefield")){
        			 		var lastChar = s.substring(s.length-1,s.length);
			 				if(lastChar >1){
			 					s = s.substring(0,s.length-1) + "0"+lastChar;
			 				}
			 				this.value = s;
			 			}
			 			if(s.length == 9 && !o.hasClass("issuefield")){
			 				var lastChar = s.substring(s.length-1,s.length);
			 				if(lastChar > 3){
			 					s = s.substring(0,s.length-1) + "0"+lastChar;
			 				}
			 				this.value = s;
			 			}
        			 	
        			 	j = (s = String(this.value)).length;
        			 	if( 0 == a.length || (112 <= n && 123 >= n) || 
        			 	    27 == n || 9 == n || 
        			 	    91 == n || 20 == n ||                      
        			 	    18 == n || 17 == n || 16 == n ||        
        			 	    (35 <= n && 46 >= n) )return true;
        			 	if(8 == n){//撤销操作
        			 		if(lp = opt[j]){
        			 			//this.value += lp, j = (s = String(this.value)).length;
        			 			this.value = String(this.value).substring(0,String(this.value).length-1);
        			 			j = (s = String(this.value)).length;
        			 			return true;
        			 		}else{
        			 			return true;
        			 		}
        			 	}
        			 	if(lp = opt[j])this.value += lp, j = (s = String(this.value)).length;
        			 	
        			 	if(0xBD == n)n = 45;
        			 	
        			 	
        			 	/*for(i = 0; i < a.length; i++)
        			 	{
        			 		k = a[i];
        			 		
        			 		if(j >= k.length)return false;
        			 	  b = k[j].test(s += String.fromCharCode(n));
        			 	  if(b)
        			 	  {
        			 	  	j = String(this.value = s).length;        			 	  
        			 	  	if(lp = opt[j])this.value += lp;
        			 	  }
        			 	}*/
        			 	for(i = 0; i < a.length; i++)
        			 	{
        			 		k = a[i];
        			 		
        			 		if(j > k.length)return false;
        			 		if((s.length == 5 && !o.hasClass("issuefield")) || s.length == 8 || s.length == 11 || s.length == 14 || s.length == 17){
        			 			s = s.substring(0,s.length-2);
        			 			s += String.fromCharCode(n);
        			 		}else {
        			 			var lastChar = s.substring(s.length-1,s.length);
        			 			if(isNaN(lastChar)){
        			 				s = s.substring(0,s.length-1);
        			 				s += String.fromCharCode(n);
        			 			}
        			 			
        			 		}
        			 		//s += String.fromCharCode(n);
        			 		if((s.length == 4 && !o.hasClass("issuefield"))|| s.length == 7 && !o.hasClass("dateMonthfield") || s.length == 10 && !o.hasClass("datefield")|| s.length == 13 || s.length == 16){
        			 		  	b = k[j-2].test(s);
        			 		}else{
        			 			b = k[j-1].test(s);
        			 		}
        			 	  check(b,this);
        			 	  
        			 	}
        			 	return false;//}
        			 	
        		
        		function check(b,obj){
			   	  if(b){
				  	j = String(this.value = s).length;
				  	//当设置了dateMonth=true时,屏蔽掉月份后面自动生成的'-'符号      
				  	if(o.attr('maxlength') == 7 && j == 7){
						this.value = s;
						//return false;
					}else{ 			 	  
				  		if((lp = opt[j])){
				  			//防止输入过快而导致错误
							if(obj.value.substring(4,5) != "-" && obj.value.length > 4)
								$(obj).val(obj.value.substring(0,4) + lp);
							if(obj.value.substring(7,8) != "-" && obj.value.length > 7)
								$(obj).val(obj.value.substring(0,7) + lp);
				  		}
				  	}
			 	  }else{//回退操作
			 		  if(j<o.attr('maxlength')){
			 		  	j = String(o.val(s.substring(0,s.length-1))).length;
			 		  	if(lp = opt[j])o.val(o.val()-lp);
			 		  }
			 		  else{
			 		  	s = s.substring(0,s.length-1);
			 		  	j = String(o.val()).length;
			 		  	o.val(s);
			 		  	if(lp = opt[j])o.val(o.val()-lp);
			 		  	//check(k[j-1].test(o.val()));
			 		  }
			 	  }
   				}
       }); 		
   }
   
   jQuery.fn.extend({        	
		datetimemask:function(type){
			 switch (type) {
			 	case 1 ://2001-01-01格式的时间
			 	  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/,
		            /\d{4}[\-\xBD]/, 
		            /\d{4}[\-\xBD][0-1]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/,
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD][0-3]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1]))/]
		          ], this, {4:'-',7:'-'},1);
			 	  if(this[0].value.length>10){
			 		 this[0].value = this[0].value.substring(0,10);
			 	  }
			          break;			 		
			 	case 2 ://2001-01-01 00:00:00 带有时间格式
			 	  MaskXtIpt([
			          [ /\d/, 
			            /\d{2}/, 
			            /\d{3}/, 
			            /\d{4}/,
			            /\d{4}[\-\xBD]/, 
			            /\d{4}[\-\xBD][0-1]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD][0-3]/, 
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1]))/,			            
			            /* 时分秒就加上后面的 */
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) /,/* 空格 */            
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) [0-2]/,/* 小时 */
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3]))/,
			            
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d/,      
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]/,
			            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))[\-\xBD]((0[1-9])|([1-2][0-9])|(3[0-1])) (([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]\d/
			            ]
			          ], this, {4:'-',7:'-',10:' ',13:":",16:":"},2);
				 	  if(this[0].value.length>19){
				 		 this[0].value = this[0].value.substring(0,19);
				 	  }
			          break;
			 	case 3 : //期号，如200101
			 	  MaskXtIpt([
			          [ /\d/, 
			            /\d{2}/, 
			            /\d{3}/, 
			            /\d{4}/,
			            /\d{4}[0-1]/, 
			            /\d{4}((0[1-9])|(1[0-2]))/
			           ]
			      ],this,null,3);
			 	  if(this[0].value.length>6){
			 		 this[0].value = this[0].value.substring(0,6);
			 	  }			 	  
			      break;
			 	case 4 ://00:00:00 带有时间格式
				  MaskXtIpt([
				    [ /[0-2]/,
				      /(([0-1][0-9])|(2[0-3]))/,
				      /(([0-1][0-9])|(2[0-3])):/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d/,      
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]/,
				      /(([0-1][0-9])|(2[0-3])):[0-5]\d:[0-5]\d/
				    ]
				    ], this, {2:':',5:':'},4);
				  if(this[0].value.length>8){
					 this[0].value = this[0].value.substring(0,8);
				  }
				  break;
				  case 5 ://2001-01格式的时间
			 	  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/,
		            /\d{4}[\-\xBD]/, 
		            /\d{4}[\-\xBD][0-1]/, 
		            /\d{4}[\-\xBD]((0[1-9])|(1[0-2]))/
		            ]
		          ], this, {4:'-'},5);
			 	  if(this[0].value.length>7){
			 		 this[0].value = this[0].value.substring(0,7);
			 	  }
			          break;			
				  case 6://1999年份格式
				  MaskXtIpt([
		          [ /\d/, 
		            /\d{2}/, 
		            /\d{3}/, 
		            /\d{4}/
		            ]
		          ], this, null,6);
			 	  if(this[0].value.length>4){
			 		 this[0].value = this[0].value.substring(0,4);
			 	  }
				  break;	
			 }
		}
   });
})); 