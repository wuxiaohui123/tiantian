<%@ page pageEncoding="UTF-8"%>
<html>   
 <head>   
<%
response.sendError(401,"无权限访问");
response.addHeader("__forbidden","true");
%>
  <title>访问拒绝</title>   
   <style type="text/css">   
   <!--   
   .STYLE10 {   
    font-family: "黑体";
    font-size: 36px;   
   }   
   -->     
   </style>   
 </head>   
 <body>   
	对不起，您还没有访问当前资源的权限！
 </body>   
</html>  
