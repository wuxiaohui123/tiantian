<%@page language="java" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="<%=basePath%>ta/resource/themes/2015/ta-theme-base.css" />
<link rel="stylesheet" type="text/css" id="linkskin" href="<%=basePath%>ta/resource/themes/2015/blue/ta-theme.css" />
<script type="text/javascript">
function setCookie(name, value) {
	var exp = new Date();  
    exp.setTime(exp.getTime() + 365*30*24*60*60*1000);  
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
}

function getCookie(cookieName) {
	var strCookie = document.cookie;
    var arrCookie = strCookie.split("; ");
    for(var i = 0; i < arrCookie.length; i++){
        var arr = arrCookie[i].split("=");
        if(cookieName == arr[0]){
            return arr[1];
        }
    }
    return "";
}
function fnChangeSkin(obj){
	var thisskin = getCookie("nowskin");
	if (obj == null) {
		thisskin=(thisskin == undefined||thisskin=="")?"blue":thisskin;
		linkskin.href = "<%=basePath%>ta/resource/themes/2015/"+thisskin+"/ta-theme.css";
	} else {
		linkskin.href = "<%=basePath%>ta/resource/themes/2015/" + obj.id+ "/ta-theme.css";
		setCookie("nowskin", obj.id);
	}
	if (obj != null){
        obj.parentNode.style.display = 'none';
    }
}
fnChangeSkin(null);
</script>
