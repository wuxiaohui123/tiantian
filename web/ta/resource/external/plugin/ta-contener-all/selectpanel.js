(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	
	$.extend(true, window, {
		taselectpanel : taselectpanel
	});
	function taselectpanel($spDiv, options) {
		var self = this;
		options = $.extend({
			spanelId : "selectpanel",
			beforeSetVal : ""
		}, options || {});
		function init() {
			var $spDiv = $("#"+options.spanelId+"-spDiv");
			var $content = $("#"+options.spanelId+"-area");
			var $form = $("#"+options.spanelId+"-form");
			
			//pengwei 为输入框绑定鼠标经过和离开事件
			$spDiv.unbind('.selectpanel');
			$spDiv.bind("mouseover",function(){
				$spDiv.addClass("bottom-hide").parent().addClass("ta-sp-hover");
				$content.show();
			}).bind("mouseout",function(){
				$spDiv.removeClass("bottom-hide").parent().removeClass("ta-sp-hover")
				$content.hide();
			});
			
			$content.bind("mouseover",function(){
				$spDiv.addClass("bottom-hide");
				$content.show();
			}).bind("mouseout",function(){
				$spDiv.removeClass("bottom-hide");
				$content.hide();
			});
			var $prov = $content.find("#stock_province_item");
			var $city = $content.find("#stock_city_item");
			var $county = $content.find("#stock_county_item");
			/**
			 * 处理 省 市 县；绑定单击事件：
			 * 1、对隐藏框设值
			 * 2、查询其子项
			 * 3、单击县时对显示框设值，如果有设值前的回调则先执行回调函数beforeSetVal；其返回true才执行设值，false则不设值
			 */
			
			
			/**
			 * 预置json，测试使用；实际采用异步加载数据方式
			 */
			var provs = [{"id":"1","name":"北京"},{"id":"2","name":"天津"},{"id":"3","name":"河北"},{"id":"4","name":"四川"},{"id":"5","name":"云南"},{"id":"6","name":"广东"}];
			
			var citys = [{"id":"1","pid":"1","name":"北京市"},{"id":"2","pid":"2","name":"天津市"},{"id":"3","pid":"3","name":"石家庄市"},{"id":"4","pid":"3","name":"唐山市"},
			             {"id":"5","pid":"4","name":"成都市"},{"id":"6","pid":"4","name":"广安市"},{"id":"7","pid":"5","name":"昆明市"},{"id":"8","pid":"5","name":"玉溪市"},
			             {"id":"9","pid":"6","name":"广州市"},{"id":"10","pid":"6","name":"深圳市"},{"id":"11","pid":"6","name":"佛山市"}];
			
			var countys = [{"id":"1","pid":"1","name":"海淀区"},{"id":"2","pid":"1","name":"朝阳区"},{"id":"3","pid":"2","name":"和平区"},
			              {"id":"4","pid":"3","name":"长安区"},{"id":"5","pid":"4","name":"路南区"},{"id":"6","pid":"5","name":"锦江区"},
			              {"id":"7","pid":"5","name":"金牛区"},{"id":"8","pid":"6","name":"华蓥市"},{"id":"9","pid":"6","name":"岳池县"},
			              {"id":"10","pid":"7","name":"昆明区"},{"id":"11","pid":"8","name":"玉溪县"},{"id":"12","pid":"9","name":"黄埔区"},
			              {"id":"13","pid":"10","name":"罗湖区"},{"id":"1","pid":"1","name":"顺德区"}];
			
			loadAreas(null,provs,$prov);
			//省级
			$prov.find("a").bind("click",function(){
				$content.unbind("mouseout");
				$content.find("#provinceName span").text($(this).text());
				$form.find("#provinceId").val($(this).attr("data-value"));
				
				loadAreas(this,citys,$city);
				$content.find(".tab li a").removeClass("ta-sp-hover");
				$content.find("#cityName").addClass("ta-sp-hover");
				$prov.hide();
				$city.show();
				$county.hide();
				
				//市级
				$city.find("a").bind("click",function(){
					$content.find("#cityName span").text($(this).text());
					$form.find("#cityId").val($(this).attr("data-value"));
					
					loadAreas(this,countys,$county);
					$content.find(".tab li a").removeClass("ta-sp-hover");
					$content.find("#countyName").addClass("ta-sp-hover");
					$prov.hide();
					$city.hide();
					$county.show();
					//区县级
					$county.find("a").bind("click",function(){
						$content.find("#countyName span").text($(this).text());
						$form.find("#countyId").val($(this).attr("data-value"));
						
						var flag = true;
						var beforeSetVal = $spDiv.attr("beforeSetVal");
						if(beforeSetVal !=null && beforeSetVal != "")
							flag = eval(template+"()");
						if(flag){
							var address = $content.find("#provinceName span").text()+""+$content.find("#cityName span").text()+""+$content.find("#countyName span").text();
							$spDiv.find("div").attr("title",address).text(address);
							$content.hide();
						}
					});
				});
			});
			
			//pengwei 给下拉面板的tab绑定单击事件，单击tab就显示该tab对应的div地区内容
			$content.find(".tab").find("a").bind("click",function(){
				$content.unbind("mouseout");
				if(!$(this).hasClass("ta-sp-hover")){
					$content.find(".tab li a").removeClass("ta-sp-hover");
					$(this).addClass("ta-sp-hover");
					var _id = $(this).attr("_id");
					switch(_id){
						case "1": $prov.show();$city.hide();$county.hide();break;
						case "2": $prov.hide();$city.show();$county.hide();break;
						case "3": $prov.hide();$city.hide();$county.show();break;
						default : break;
					}
				}
			});
			
			function loadAreas(obj,data,$level){
				var provList = new Array;
				for(var i = 0;i < data.length; i++){
					var str = "";
					if(obj == null){
						str = "<li><a data-value='"+ data[i].id +"'>"+ data[i].name +"</a></li>";
					}else{
						var oid = $(obj).attr("data-value");
						if(data[i].pid == oid){
							str = "<li><a data-value='"+ data[i].id +"'>"+ data[i].name +"</a></li>";
						}
					}
					provList.push(str);
				}
				$level.find("ul").html(provList.join(" "));
			}
			
			return self;
		}// end init
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taselectpanel'// 将方法注册为公共方法
		});
	}
}));