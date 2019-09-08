<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.yinhai.sysframework.util.WebUtil"%>
<%@page import="com.yinhai.sysframework.iorg.IUser"%>
<%@page import="com.yinhai.sysframework.config.SysConfig"%>
<%@page import="com.yinhai.sysframework.service.AppManager"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	//如果用户已经登录，直接跳到首页
	IUser user = WebUtil.getUserInfo(request);
	if (user != null && null != user.getUserId()) {
		response.sendRedirect("indexAction.do");
		return;
	}
	Date now = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   E");

	request.setAttribute("date", formatter.format(now));

	String ctxPath = request.getContextPath();
	response.addHeader("__timeout", "true");
	boolean developMode = SysConfig
			.getSysconfigToBoolean("developMode");
	String sameLogin = (String) request.getParameter("samelogin");
	String param = request.getQueryString();
	if (param != null) {
		param = param.replaceAll("'", "");
		param = param.replaceAll("\"", "&quot;");
	}
%>
<%
  	String postId=String.valueOf(Math.random());
    request.getSession(false).setAttribute("_POSTID",postId);			
    response.addHeader("Set-Cookie", "POSTID="+postId+";Path="+request.getContextPath()+";HttpOnly");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=8; IE=9; IE=10" />
<link rel="stylesheet" type="text/css" href="indexue/common/base.css" />
<link href="indexue/loginresource/css/master.css" rel="stylesheet" type="text/css" />
<title>ta3登录首</title>
<%
	if (AppManager.getSysConfig("developMode").equals("true")){
%>
 	<script type="text/javascript" src="http://118.112.188.108:8808/s/9c3341304f3839f5043e84f56cc7585c-T/zh_CN6kcypx/64016/15/1.4.25/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=zh-CN&collectorId=671b2d72"></script>
<%	    
	}
%>
</head>
<body>
	<div class="container">
		<!-- top start -->
		<div class="top" id="top">
			<span class="time">今天是:${date}</span> 
			<a onclick="fnSetHome(this.window.location.pathname)" class="home" href="javascript:void(0)">设为首页</a> 
			<a onclick="fnAddFavorite(window.location,document.title)" class="collect" href="javascript:void(0)">加入收藏</a>
		</div>
		<!-- top end -->
		<!-- banner start -->
		<div class="banner" id="banner">
			<img class="banner-img" src="indexue/loginresource/images/dl_top.jpg">
		</div>
		<!-- banner end -->
		<!-- main start -->
		<div class="main" id="main">
			<div class="loginform">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td id="validate" class="validateInfo">公共场所不建议保存密码，以防账号丢失</td>
					</tr>
					<tr>
						<td class="tdText"><span class="fontCn">用户名</span><span class="fontccc">Username</span></td>
					</tr>
					<tr>
						<td><input type="text" id="j_username" onkeyup="usernameKeyup(this)" onkeydown="revertValidate(this)" name="j_username" class="username" border="0"/></td>
					</tr>
					<tr>
						<td class="tdText"><span class="fontCn">密码</span><span class="fontccc">Password</span></td>
					</tr>
					<tr>
						<td><input type="password" id="j_password" onkeyup="passwordKeyup(this)" onkeydown="revertValidate(this)" name="j_password" class="password" border="0" /></td>
					</tr>
					<tr>
						<td class="tdText"><span class="fontCn">验证码</span><span class="fontccc">Code</span></td>
					</tr>
					<tr>
						<td>
						  <input type="text" id="checkCode" onkeyup="checkCodeKeyup(this)" onkeydown="revertValidate(this)" name="checkCode" class="code" border="0" style="float:left;" />
						  <a href="#">
						     <img id="codeimg" onclick="javascript:refeshCode();" src="CaptchaImg" title="点击获取验证码" style="float:left;width:95px;height:34px" />
						  </a>
						</td>
					</tr>
					<tr>
						<td>
						  <a href="#" onclick="fnOpenModifyPsd()" id="changePass">修改密码</a>
						  <input id="remberPass" type="checkbox" style="margin-left:20px;vertical-align:middle;" value="">记住密码</input>
						</td>
					</tr>
					<tr>
					 <td>
					  <input type="button" id="submit" onclick="login()" class="submit" border="0" value="登录" />
					 </td>
					</tr>
				</table>
			</div>
		</div>
		<!-- main end -->
		<!-- footer start -->
		<div class="footer" id="footer">
			技术支持：四川久远银海软件股份有限责任公司<br /> Surport:SiChuan zhongzhi Software Limited By Share Ltd
		</div>
		<!-- footer end -->
	</div>

	<!-- 遮罩层start -->
	<div class="mask" id="mask"></div>
	<!-- 遮罩层end -->

	<!-- 密码修改框 start-->
	<div class="psdmodify" id="psdmodify">
		<div class="title">密码修改<a href="javascript:;" onclick="fnCloseModifyPsd()" class="close" title="关闭"></a></div>
		<form id="psdmodifyform">
			<table cellpadding="0" cellspacing="0" width="0">
				<tr>
					<td class="lable">用户名</td>
					<td><input id="loginId" name="dto['loginId']" required="true" type="text" title="用户名" onkeyup="loginIdKeyup(this)" /></td>
				</tr>
				<tr>
					<td class="lable">密码</td>
					<td><input name="dto['oldPass']" type="password" id="oldPass" required="true" title="密码" onkeyup="oldPassKeyup(this)" /></td>
				</tr>
				<tr>
					<td class="lable">新密码</td>
					<td><input type="password" id="newPass" name="dto['newPass']" required="true" title="新密码" onkeyup="newPassKeyup(this)" /></td>
				</tr>
				<tr>
					<td class="lable">确认密码</td>
					<td><input type="password" id="rpassword" onkeyup="rpasswordKeyup(this)" name="dto['rpassword']" required="true" title="确认密码" /></td>
				</tr>
				<tr>
					<td class="lable">验证码</td>
					<td>
						<input id="checkCodePass" name="dto['checkCodePass']" onkeyup="checkCodePassKeyup(this)" type="text" class="captcha" title="验证码" />
						<img id="codeimgPass" onclick="javascript:refeshCodePass();" src="<%=ctxPath%>/CaptchaImgPass" title="看不清,换一张" />
					</td>
				</tr>
				<tr>
					<td class="lable">&nbsp;</td>
					<td><input type="button" class="submit" border="0" value="确认修改" id="modifyPsd" onclick="fnSavePass()" /></td>
				</tr>
				<tr>
					<td colspan="2" id="errornotice"></td>
				</tr>
			</table>
		</form>
	</div>
	<!-- 		<script type="text/javascript" src="http://118.112.188.108:8808/s/7fe82ab90965234e6bfb4a4f20a764ae-T/zh_CN-ropdih/64016/15/1.4.25/_/download/batch/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector/com.atlassian.jira.collector.plugin.jira-issue-collector-plugin:issuecollector.js?locale=zh-CN&collectorId=671b2d72"></script> -->
</body>
<script type="text/javascript">
	window.onload = function(){//等待页面内DOM、图片资源加载完毕后触发执行
    	document.getElementById("j_username").focus();
    	document.getElementById("j_username").value=getCookie("username");
    	document.getElementById("j_password").value=getCookie("password");
	}
	if(top != window.self){
		top.location.href="login.jsp?samelogin=<%=sameLogin%>";
	}
	//账号keyup事件
	function usernameKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("j_password").focus();
			}
		}	
	}
	//密码keyup事件
	function passwordKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("checkCode").focus();
			}
		}	
	}
	//验证码keyup事件
	function checkCodeKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				login();
			}
		}	
	}	
	//修改密码页，用户名keyup事件
	function loginIdKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("oldPass").focus();
			}
		}	
	}
	function oldPassKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("newPass").focus();
			}
		}	
	}
	function newPassKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("rpassword").focus();
			}
		}
	}
	function rpasswordKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				document.getElementById("checkCodePass").focus();
			}
		}
	}
	function checkCodePassKeyup(obj){
		var str=obj.value;
		if(str!=null||str!=""){
			if(event.keyCode==13){
				fnSavePass();
			}
		}
	}
	function setCookie(name,value){
		var Days = 10;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days*24*60*60*1000);
		document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
    }
	function getCookie(name){
		var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
		if(arr=document.cookie.match(reg))
		  return unescape(arr[2]);
		else
		  return null;
    }
	function login() {
		var username = j_username.value;
		var pass = j_password.value;
		if (username == "" || username == undefined) {
			validate.innerHTML = "请输入用户名";
			validate.className = "validateError";
			j_username.style.border = "1px solid #ef7d1b";
			document.getElementById("j_username").focus();
			return;
		}
		if (pass == "" || pass == undefined) {
			validate.innerHTML = "请输入密码";
			validate.className = "validateError";
			j_password.style.border = "1px solid #ef7d1b";
			document.getElementById("j_password").focus();
			return;
		}
		submit.disabled = "disabled";
		submit.value = "登录中...";
		submit.className = submit.className + " disable";
		var xmlhttp;
		if (window.XMLHttpRequest) {
			xmlhttp = new XMLHttpRequest();
		} else {
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.open("POST", "formLoginCheckAction.do?t=" + Math.random(),true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xmlhttp.setRequestHeader("x-requested-with", "XMLHttpRequest");
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				try {
					var reObj = eval("(" + xmlhttp.responseText + ")");
					if (reObj.success) {
						if(document.getElementById("remberPass").checked){
							setCookie("username",username);
							setCookie("password",pass);
						}
						window.location.href = "indexAction.do";
					} else {
						validate.innerHTML = reObj.msg;
						validate.className = "validateError";
						validate.style.border = "1px solid #ef7d1b";
						refeshCode();
					}
				} catch (error) {
					validate.innerHTML("发生异常，请刷新后重试，或联系管理员");
				} finally {
					enableSubmitButton();
				}
			} else if (xmlhttp.readyState == 4) {
				enableSubmitButton();
				refeshCode();
			}
		}
		xmlhttp.send("j_username=" + username + "&j_password=" + pass+ "&checkCode=" + checkCode.value);

	}
	function hasClass(obj, cls) {  
   		return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));  
	}  
	function addClass(obj, cls) {  
   	 if (!this.hasClass(obj, cls)) obj.className += " " + cls;  
	}  
	function removeClass(obj, cls) {  
    	if (hasClass(obj, cls)) {  
        	var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');  
        obj.className = obj.className.replace(reg, ' ');  
    	}
     } 
	
	function fnSavePass() {
		var d = "";
		var loginId=document.getElementById("loginId").value;
		var oldPass=document.getElementById("oldPass").value;
		var newPass=document.getElementById("newPass").value;
		var rpassword=document.getElementById("rpassword").value;
		var checkCodePass=document.getElementById("checkCodePass").value;
		var modifyPsd=document.getElementById("modifyPsd");
		
		
		var errornotice=document.getElementById("errornotice");
		if(loginId == "" || loginId == null || loginId == undefined){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="用户名不能为空";
			document.getElementById("loginId").focus();			
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		if(oldPass == "" || oldPass == null || oldPass == undefined){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="密码不能为空";
			document.getElementById("oldPass").focus();
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		if(newPass == "" || newPass == null || newPass == undefined){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="新密码不能为空";
			document.getElementById("newPass").focus();
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		<%if (!AppManager.getSysConfig("developMode").equals("true")){%>
// 		密码复杂度验证
		if(newPass.length<6){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="新密码不能小于6位";
			document.getElementById("newPass").focus();
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		
		 var ls = 0;
 		if(newPass.match(/([a-z])+/)){
      		ls++; 
  		}
 		if(newPass.match(/([0-9])+/)){
      		ls++; 
 		}
 		if(newPass.match(/([A-Z])+/)){
      		ls++; 
  		}
  		if(newPass.match(/[^a-zA-Z0-9]+/)){
        	ls++;
   		}
   		if(ls<2){
   			addClass(errornotice,"errornotice");
			errornotice.innerHTML="密码复杂度不够，请重新设置";
			document.getElementById("newPass").focus();
			return;
   		}else{
   			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		<%}%>
		
		if(rpassword == "" || rpassword == null || rpassword == undefined){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="确认密码不能为空";
			document.getElementById("rpassword").focus();			
			return;
		}else if(rpassword!= newPass){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="密码必须一致";
			document.getElementById("rpassword").focus();	
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		if(checkCodePass == "" || checkCodePass == null || checkCodePass == undefined){
			addClass(errornotice,"errornotice");
			errornotice.innerHTML="验证码不能为空";
			document.getElementById("checkCodePass").focus();	
			return;
		}else{
			removeClass(errornotice,"errornotice");
			errornotice.innerHTML="";
		}
		modifyPsd.disabled = "disabled";
		modifyPsd.value = "修改中...";
		modifyPsd.className = modifyPsd.className + " disable";
		<%if ("true".equals(SysConfig.getSysConfig("passwordRSA", "false"))) {%>
		var _oldPass=encryptPwd(document.getElementById("oldPass").value);
		var _newPass=encryptPwd(document.getElementById("newPass").value);			
		<%} else {%>
		var _oldPass=document.getElementById("oldPass").value;
		var _newPass=document.getElementById("newPass").value;	
		<%}%>
		d += "dto['loginId']="+document.getElementById("loginId").value+"&dto['oldPass']="+_oldPass+"&dto['newPass']="+_newPass+"&dto['checkCodePass']="+document.getElementById("checkCodePass").value;
		var xhr = new XMLHttpRequest();
		xhr.open("post", "<%=basePath%>system/userPassAction!changePasswordWidthCurrent.do", true);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setRequestHeader("x-requested-with", "XMLHttpRequest");
		xhr.onreadystatechange=function(){
			if (xhr.readyState == 4 && xhr.status==200) { 
					var data = eval("(" + xhr.responseText + ")");
					modifyPsd.disabled = false;
					modifyPsd.value = "确认修改";
					modifyPsd.className = "submit";
					if(data.msgBox){
						alert(data.msgBox.msg);
						if(data.msgBox.msgType !="error"){
							fnCloseModifyPsd();
						}
					}else{
						fnCloseModifyPsd();
					}
			}
			if(xhr.readyState==4){
				refeshCodePass();
			}
		};
		xhr.send(d);
	}
	
// 	function checkRequired($o,val){
// 		if(val == "" || val == null || val == undefined){
// 			$("#errornotice").addClass("errornotice").html($o.attr("title")+"不能为空");
// 			$o.focus();
// 		}
// 	}
	function checkRequired($o,val){
		if(val==""||val==null||val==undefined){
			var error=document.getElementById("errornotice");
			addClass(error, "errornotice");
			error.innerHTML($o.getAttribute("title")+"不能为空");
			$o.focus();
		}
	}

	function fnOpenModifyPsd() {
		var tp = windowHeight() - 342 >= 0 ? (windowHeight() - 342) / 2 : 0;
		var lt = windowWidth() - 402 >= 0 ? (windowWidth() - 402) / 2 : 0;
		//$('#mask').show();
		mask.style.display = "block";
		psdmodify.style.top = tp + "px";
		psdmodify.style.left = lt + "px";
		psdmodify.style.display = "block";
		//$("#psdmodify").css({top:tp,left:lt}).fadeIn();
// 		$("#loginId").focus();
		document.getElementById("loginId").focus();
	}

	function enableSubmitButton() {
		submit.disabled = false;
		submit.value = "登录";
		submit.className = submit.className.replace(new RegExp("(\\s|^)disable(\\s|$)"), "");
	}

	function refeshCode() {
		document.getElementById("codeimg").src = "CaptchaImg?j="+ Math.random();
	}

	function revertValidate(input) {
		input.style.border = "1px solid #ccc";
	}
	//收藏
	function fnAddFavorite(sURL, sTitle) {
		sURL = encodeURI(sURL);
		try {
			window.external.addFavorite(sURL, sTitle);
		} catch (e) {
			try {
				window.sidebar.addPanel(sTitle, sURL, "");
			} catch (e) {
				alert("您的浏览器不支持自动加入收藏功能，请使用Ctrl+D进行添加，或手动在浏览器里进行设置！");
			}
		}
	}
	//设为首页
	
	function fnSetHome(obj,url) {
		if (document.all) {
			try {
			console.log(url);
				document.body.style.behavior = 'url(#default#homepage)';
				document.body.setHomePage(url);
			} catch (e) {
				if (window.netscape) {
					try {
						netscape.security.PrivilegeManager
								.enablePrivilege("UniversalXPConnect");
					} catch (e) {
						alert("抱歉，此操作被浏览器拒绝！\n\n请在浏览器地址栏输入“about:config”并回车然后将[signed.applets.codebase_principal_support]设置为'true'");
					}
				} else {
					alert("抱歉，您所使用的浏览器无法完成此操作。\n\n您需要手动将【" + url + "】设置为首页。");
				}
			}
		} else {
			alert("您的浏览器不支持自动设置页面为首页功能，请您手动在浏览器里设置该页面为首页！");
		}
	}
	//浏览器视口的宽度
	function windowWidth() {
		var de = document.documentElement;
		return self.innerWidth || (de && de.clientWidth)
				|| document.body.clientWidth
	}
	//关闭修改密码窗口
	function fnCloseModifyPsd() {
		mask.style.display = "none";
		psdmodify.style.display = "none";
		document.getElementById("psdmodifyform").reset();
	}
	//浏览器视口的高度
	function windowHeight() {
		var de = document.documentElement;
		return self.innerHeight || (de && de.clientHeight)
				|| document.body.clientHeight;
	}
	//修改密码，验证码
	function refeshCodePass() {
		document.getElementById("codeimgPass").src = "<%=ctxPath%>/CaptchaImgPass?j="
			+ Math.random();
	}
</script>
</html>