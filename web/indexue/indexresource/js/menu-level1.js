/**
 * 当一级菜单显示在左上角时调用这里面的方法
 */
function menuToBig(o){
	if($(o).hasClass("menuLevel2_left")){
    	$(o).removeClass("menuLevel2_left arrow_left arrow_left_over")
    	       .addClass("menuLevel2_right arrow_right arrow_right_over");
    	$("#menuBox").removeClass("menuBox_small");
        $("#skin-left-bottom").css("width",209);
        $("#skin-top").css("left",210);
        $("#skin-top-left").css("left",210);
        fnInitPage();
    }else if($(o).hasClass("menuLevel2_middle")){
    	$(o).removeClass("menuLevel2_middle arrow_left arrow_left_over")
    	       .addClass("menuLevel2_right arrow_right arrow_right_over");
    	$("#menu_ul>li").find(".menuArrow_m").addClass("menuArrow_s").removeClass("menuArrow_m");
        $("#menuBox").removeClass("menuBox_middle");
        $("#skin-left-bottom").css("width",209);
        $("#skin-top").css("left",210);
        $("#skin-top-left").css("left",210);
        $("#menu_ul .menuIcon").removeClass("menuIcon_middle");
        $("#menu_ul .menuName").removeClass("menuName_middle");
        fnInitPage();
    }
}

function menuToMiddle(){
	$("#menuLevel2").removeClass("menuLevel2_right arrow_right arrow_right_over")
	.addClass("menuLevel2_middle arrow_left arrow_left_over");
	$("#menu_ul>li").find(".menuArrow_s").addClass("menuArrow_m").removeClass("menuArrow_s");
	$("#menuBox").addClass("menuBox_middle");
	$("#menu_ul .menuIcon").addClass("menuIcon_middle");
	$("#menu_ul .menuName").addClass("menuName_middle");
	$("#skin-left-bottom").css("width",105);
	$("#skin-top").css("left",106);
    $("#skin-top-left").css("left",106);
	$("#menuLevel2_middle").hide();
	$("#menuLevel2_small").hide();
	fnInitPage();
}

function menuToSmall(){
	$("#menuLevel2").removeClass("menuLevel2_right arrow_right arrow_right_over")
	.addClass("menuLevel2_left arrow_left arrow_left_over");
	$("#menuBox").addClass("menuBox_small");
	$("#skin-left-bottom").css("width",60);
	$("#skin-top").css("left",61);
    $("#skin-top-left").css("left",61);
	$("#menuLevel2_middle").hide();
	$("#menuLevel2_small").hide();
	fnInitPage();
}