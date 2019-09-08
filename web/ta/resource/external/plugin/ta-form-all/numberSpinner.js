(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
		taspinner : taspinner
	});
	function taspinner(documentDIV, options) {
		var self = this;
		options = $.extend({
			maxValue : 9999999, /* 最大值 */
			minValue : -9999999, /* 最小值 */
			defValue : 1, /* 默认值 */
			addValue : 1,/* 默认增量 */
			txtWidth : 100,/* 文本框大小 */
			readOnly : true,
			txtHeight : 20,/* 文本框高度 */
			txtId : "xx",
			notUse : "false",
			txtName : "dto['xx']",
			defaultSrc : Base.globvar.contextPath
					+ "/ta/resource/themes/base/spinner/images/" /* 默认图片保存路径 */
		}, options || {});
		defaultSrc1 = Base.globvar.contextPath
				+ "/ta/resource/themes/base/spinner/images/" /* 默认图片保存路径 */
		function init() {
			$topDIV = $(documentDIV);
			// id='" + options.txtId + "'
			var strDIV = "<input type='text' name=" + '"' + options.txtName
					+ '"' + " class='textinput spinner' " + "/>"
					+ "<img src='" + defaultSrc1 + "spin-button.png' />";
			$topDIV.append($(strDIV)); // 将input和img添加到父div中
			var $text = $topDIV.find("input");// text标签
			if (options.readOnly == true) {
				$text.attr("readonly", true);
			} else {
				if (options.readOnly == false) {
					$text.attr("readonly", false);
				}
			}
			$text.keyup(function() { // keyup事件处理
				var localVar = parseFloat($text.attr("value")); // 得到当前文本框中的值
				if (isNaN(localVar) || localVar < options.minValue) {
					$text.attr("value", options.defValue);
				} else {
					if (localVar > options.maxValue) {
						$text.attr("value", options.maxValue);
					} else {
						$text.attr("value", localVar);
					}
				}
			}).bind("paste", function() { // CTR+V事件处理
				$(this).val($(this).val().replace(/\D|^0/g, ''));
			}).css("ime-mode", "disabled");
			var $img = $topDIV.find("img:first");// img标签
			$img.height(21).width(20).addClass("imgspinner");

			$text.attr("value", options.defValue);
			$text.unbind("change");
			$text.bind("change", function() {// 添加change事件，防止用户手动更改为非法数据，并恢复为默认值
				var localVar = parseFloat($text.attr("value")); // 得到当前文本框中的值
				if (isNaN(localVar) || localVar > options.maxValue
						|| localVar < options.minValue) { // 检验是否是非法数据,是否大于最大值后小于最小值，如果转换失败就恢复默认值
					$text.attr("value", options.defValue);
				} else {
					$text.attr("value", options.defValue);
				}
			});
			$img.hover(function() {
				$(this).addClass("_handHover");
			}, function() {
				$(this).removeClass("_handHover");
			});
			$img.unbind("click");
			if (options.notUse == "true"){
				spinnerImgClick(options.txtId,false);
			}else{
				if (options.notUse == "false"){
					spinnerImgClick(options.txtId,true);
				}
			}
			return self;
		}// end init
		function spinnerImgClick(id,enable) {
			var $text = $("#"+id+"_divtop").find("input");// text标签
            var $img=$("#"+id+"_divtop").find("img:first");
			if(enable==true){
				$text.removeClass();
				$text.addClass("textinput spinner");
				$img
				.bind(
						"click",
						function(event) {
							if (!event) {
								event = window.event;
							}
							// 这里可以得到鼠标Y坐标
							var pointY = event.pageY;// var pointX =
							// event.pageX;
							var topY = $(this).offset().top;// var
							// leftX=$(this).offset().left;
							var sizeY = $(this).attr("height");
							var localVar = parseFloat($text
									.attr("value"));// 得到当前文本框中的值
							if (isNaN(localVar)) {// 如果转换失败就恢复默认值
								$text.attr("value", options.defValue);
							} else {
								if (2 * (pointY - topY) > sizeY) {// 点击在下方
									$img.attr("src", options.defaultSrc
											+ "spin-down.png");
									if (localVar - options.addValue >= options.minValue) {
										$text.attr("value", FloatSub(
												localVar,
												options.addValue));
									} else {
										if (localVar - options.addValue < options.minValue) {
											$text.attr("value",
													options.minValue);
										}
									}
								} else {
									$img.attr("src", options.defaultSrc
											+ "spin-up.png");
									if (localVar + options.addValue <= options.maxValue) {
										$text.attr("value", FloatAdd(
												localVar,
												options.addValue));
									} else {
										if ((localVar + options.addValue) > options.maxValue) {
											$text.attr("value",
													options.maxValue);
										}
									}
								}
								setTimeout(function() {// 点击后恢复默认图片
									$img.attr("src", options.defaultSrc
											+ "spin-button.png");
								}, 200);
							}
						});
			}else{
				$text.removeClass();
				$text.attr("readonly", true);
				$text.addClass("textinput readonly spinner");
				$img.unbind(); 
			}
		}
		// 精确浮点数加运算
		function FloatAdd(arg1, arg2) {
			var r1, r2, m;
			try {
				r1 = arg1.toString().split(".")[1].length
			} catch (e) {
				r1 = 0
			}
			try {
				r2 = arg2.toString().split(".")[1].length
			} catch (e) {
				r2 = 0
			}
			m = Math.pow(10, Math.max(r1, r2))
			return (arg1 * m + arg2 * m) / m;
		}
		// 精确浮点数减法运算
		function FloatSub(arg1, arg2) {
			var r1, r2, m, n;
			try {
				r1 = arg1.toString().split(".")[1].length
			} catch (e) {
				r1 = 0
			}
			try {
				r2 = arg2.toString().split(".")[1].length
			} catch (e) {
				r2 = 0
			}
			m = Math.pow(10, Math.max(r1, r2));
			// 动态控制精度长度
			n = (r1 >= r2) ? r1 : r2;
			return ((arg1 * m - arg2 * m) / m).toFixed(n);
		}
		
		init();// 调用初始化方法
		$.extend(this, { // 为this对象
			"cmptype" : 'taspinner',// 将方法注册为公共方法
			"spinnerImgClick" : spinnerImgClick
		});

	}
}));
