(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery", "datetimeMask"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
		taissue : taissue
	});
	function taissue($div, options) {
		var self = this;
		options = $.extend({
			txtWidth : 100,/* 文本框大小 */
			disabled : false,
			readOnly : false,
			txtHeight : 20,/* 文本框高度 */
			txtId : "issue",
			txtName : "dto['issue']"
		}, options || {});
		function init() {
			var $input = $("#"+options.txtId);
			
			$input.unbind('.issue');
			$input.bind("click.issue",function(){
				//判断组件是否只读
				if("readonly" === ($input.attr("readOnly"))){
					return;
				}
				$input.datetimemask(3);//调用datetimemask中的期号判断，阻止非数字输入和非期号格式输入
				$input.validatebox('validate');
				var year,begin;
				var val = $input.val();
				var mYear = parseInt(new Date().getFullYear());//获取当前年份
				if(val == "" || val == null){
					year = mYear;
				}else{
					//设置输入框的值长度不低于4位
					switch(val.length){
						case 1:
							val = val+"000";break;
						case 2:
							val = val+"00";break;
						case 3:
							val = val+"0";break;
						default:
							break;
					}
					year = parseInt(val.substring(0,4));
				}
				//计算当前页的开始年份begin
				if((mYear - 9 - year) > 0){//如果输入值不在当前年份这一页
					if((mYear - 9 - year)%10 == 0){//year为当前页开始值
						begin = year;
					}else if((mYear - 9 - year)%10 == 1){//year为当前页最大值
						begin = year - 9;
					}else{
						begin = year - (10 - (mYear - 9 - year)%10);
					}
				}else{
					begin = mYear - 9;
				}
				
				//设定日期长度6位
				if(begin<1000){
					begin = 1004;
				}
				
				loadYears(year,options.txtId,begin,0);//加载年份面板
			});
			//加载年份面板并绑定事件
			function loadYears(year,id,start,flag){
				start = parseInt(start);
				var $yearsDiv = $("#"+id+"_years");
				if($yearsDiv[0]){
					$yearsDiv.remove();
				}
				$yearsDiv = $("<div id=\""+ id +"_years\"></div>");
				$yearsDiv.addClass("issue_years");
				//判断点击的是向上图标还是向下图标
				if(flag == "0"){
					start = start;
				}else if(flag == "1"){
					start = start - 10;
				}else{
					start = start + 10; 
				}
				
				var bgImg = Base.globvar.contextPath
				+ "/ta/resource/themes/base/issue/images/";
				//上箭头
				var $upBtn =  $("<div></div>");
				$upBtn.addClass("issue_up");
				
				var $imgUBtn = $("<input type='image'/>");
				$imgUBtn.css({"height":"10px","border":"0px"})
				.attr("src",bgImg+"up.png")
				.bind("click",function(){
					loadYears(year,id,start,"1");
				})
				.appendTo($upBtn);
				
				$upBtn.appendTo($yearsDiv);
				//下箭头
				var $downBtn =  $("<div></div>");
				$downBtn.addClass("issue_down");
				
				var $imgDBtn = $("<input type='image'/>");
				$imgDBtn.css({"height":"10px","border":"0px"})
				.attr("src",bgImg+"down.png")
				.bind("click",function(){
					loadYears(year,id,start,"2");	
				})
				.appendTo($downBtn);
				
				var content = "";
				for(var i = 0;i < 10;i++){
					content = "<div id=\""+ (start + i) +"\">"+ (start + i) +"</div>";
					$(content).addClass("issue_year").appendTo($yearsDiv).click(function(){
						clickYear(this,id);
					});
				}
				$downBtn.appendTo($yearsDiv);
				$yearsDiv.appendTo($div);
				$("#"+year).addClass("is_selected");
				$div.show();
				$yearsDiv.show();
			}
			//单击年份显示月份面板
			function clickYear(o,id){
				$(o).parent().find("div").removeClass("is_selected");
				$(o).addClass("is_selected");
				var $months = $("#" + id + "_months");
				if($months[0]){
					$months.remove();
				}
				$months = $("<div id=\""+ id +"_months\"></div>");
				$months.addClass("issue_months")
				.css({"left":"62px","width":"172px","height":"226px"});
				
				var $monName = $("<div>月份</div>");
				$monName.addClass("month_title");
				
				var $monDiv = $("<div style='height:100%;'></div>");
				$monName.appendTo($months);
				$monDiv.appendTo($months);
				
				var $monTab = $("<table width=\"100%\" height=\"89%\" align=\"center\" style=\"margin-bottom:10px;\"></table>"); 
				$monTab.appendTo($monDiv);
				var n = 0;
				for(var i=1;i<5;i++){//循环添加tr
					var $conTr = $("<tr></tr>");
					var conTd="";
					$conTr.appendTo($monTab);
					for(var j=1;j<4;j++){//循环添加td
						n = parseInt(n)+1;
						var tempn = n+"";
						if(n < 10){
							tempn = "0" + n;
						}
						conTd = "<td val=\""+ ($(o).attr("id") + tempn) +"\">" + parseInt(n) + "月</td>";
						$(conTd).addClass("is_td").appendTo($conTr).click(function(){
							setValue(this,id);
						});
					}
				}
				$months.insertAfter($("#" + id +"_years"));
				$months.show();
			}
			//单击月份，设值
			function setValue(o,id){
				var value = $(o).attr("val");
				$("#"+id).val(value);
				$("#"+id+"_months").hide();
				$div.hide();
			}
			
			return self;
		}// end init
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taissue'// 将方法注册为公共方法
		});
	}
}));
