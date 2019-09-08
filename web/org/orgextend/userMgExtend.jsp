<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<ta:number id="age" key="年龄" alignLeft="true" max="200"/>
<ta:date id="birth" key="出生日期" showSelectPanel="true"/>
<ta:selectInput id="job" key="职位" collection="JOB"/>
<ta:number id="qq" key="QQ" alignLeft="true" max="99999999999"/>
<ta:text id="email" key="电子邮箱" validType="email"/>
<ta:text id="weixin" key="微信" maxLength="50"/>
<ta:text id="weibo" key="微博" maxLength="50"/>
<ta:text id="address" key="地址" maxLength="200"/>
<ta:buttonLayout align="right">
  <ta:button id="baclk" key="返回" onClick="fnbaclk()"/>
</ta:buttonLayout>
<script type="text/javascript">
 function fnbaclk(){
	 Base.hideObj("box2");
	 Base.showObj("box1");
 }
</script>