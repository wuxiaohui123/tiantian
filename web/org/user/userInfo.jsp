<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.yinhai.sysframework.util.WebUtil" %>
<%@ page import="com.yinhai.sysframework.iorg.IUser" %>
<%
    IUser userInfo = WebUtil.getUserInfo(request);
%>
<html>
<head>
    <title>用户信息</title>
    <style type="text/css">
        .user-box {
            width: 96%;
            height: 96%;
            margin: 10px;
        }

        .field-box {
            height: 28px;
            line-height: 28px;
            padding: 5px;
            margin-top: 5px;
            background-color: #f3f3f3;
            border-radius: 5px;
        }

        label {
            width: 180px;
        }

        span {
            color: grey;
        }

        .field-item-left {
            float: left;
        }

        .field-item-right {
            float: right;
            width: 280px;
        }
    </style>
</head>
<body>
<div class="user-box">
    <div class="field-box">
        <div class="field-item-left">
            <label>名称：</label>
            <span><%=userInfo.getName()%></span>
        </div>
        <div class="field-item-right">
            <label>性别：</label>
            <span><%=userInfo.getSex().equals("1") ? "男" : "女"%></span>
        </div>
    </div>
    <div class="field-box">
        <div class="field-item-left">
            <label>出生日期：</label>
            <span><%=userInfo.getBirth() != null ? userInfo.getBirth() : "" %></span>
        </div>
        <div class="field-item-right">
            <label>年龄：</label>
            <span><%=userInfo.getAge() != null ? userInfo.getAge() : ""%></span>
        </div>
    </div>
    <div class="field-box">
        <label>职位：</label>
        <span><%=userInfo.getJob() != null ? userInfo.getJob() : ""%></span>
    </div>
    <div class="field-box">
        <div class="field-item-left">
            <label>手机：</label>
            <span><%=userInfo.getTel() != null ? userInfo.getTel() : "" %></span>
        </div>
        <div class="field-item-right">
            <label>办公电话：</label>
            <span><%=userInfo.getOfficetel() != null ? userInfo.getOfficetel() : ""%></span>
        </div>
    </div>
    <div class="field-box">
        <label>邮箱：</label>
        <span><%=userInfo.getEmail() != null ? userInfo.getEmail() : ""%></span>
    </div>
    <div class="field-box">
        <label>QQ：</label>
        <span><%=userInfo.getQq() != null ? userInfo.getQq() : ""%></span>
    </div>
    <div class="field-box">
        <label>微信：</label>
        <span><%=userInfo.getWeixin() != null ? userInfo.getWeixin() : ""%></span>
    </div>
    <div class="field-box">
        <label>微博：</label>
        <span><%=userInfo.getWeibo() != null ? userInfo.getWeibo() : ""%></span>
    </div>
    <div class="field-box">
        <label>地址：</label>
        <span><%=userInfo.getAddress() != null ? userInfo.getAddress() : ""%></span>
    </div>
</div>
</body>
</html>
