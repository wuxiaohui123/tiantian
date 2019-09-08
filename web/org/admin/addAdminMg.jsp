<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<html>
<head>
<title>新增管理员</title>
<%@ include file="/ta/inc.jsp"%>
</head>
<body class="no-scrollbar">
<ta:pageloading />
<ta:panel withButtonBar="true" hasBorder="false" id="addAdminForm" fit="true" > 
	<ta:box cssStyle="padding:10px;" cols="2" fit="true">
		<ta:text id="isfastsetting" display="false" value="1"/>
		<ta:text id="username" key="姓名" placeholder="请输入姓名..." labelWidth="70" />
		<ta:buttonLayout align="left">
				<ta:button key="查询[Q]" onClick="fnSearch()" isok="true" hotKey="Q" icon="xui-icon-query" />
		</ta:buttonLayout> 
		<ta:box fit="true" id="contentDIV" cols="2" span="2" cssStyle="margin-top:10px;">
			<ta:box fit="true" columnWidth="0.6">
				<ta:panel id="manInfoBox"  key="人员列表(非管理员)"  fit="true">
					<ta:datagrid  id="userGrid" fit="true" forceFitColumns="true"	haveSn="true"  onRowClick="fnSetFastPermissionOptions">
						<ta:datagridItem id="loginid" 	 	key="登录号" width="80">	</ta:datagridItem>
						<ta:datagridItem id="name" 			key="姓名" width="80">	</ta:datagridItem>
			 			<ta:datagridItem id="orgnamepath" 	key="所属路径" width="600" showDetailed="true"></ta:datagridItem>   
				 		<ta:dataGridToolPaging url="adminUserMgAction!queryNoAdminUsers.do" pageSize="200" showExcel="false" submitIds="addAdminForm"></ta:dataGridToolPaging>
					</ta:datagrid>  
				</ta:panel>  
			</ta:box> 
			  
			<ta:box id="fastSetBox" fit="true" cssStyle="overflow: auto;margin-left:10px;" columnWidth="0.4">
				<ta:panel id="fastsettingpanel" hasBorder="true" fit="true" bodyStyle="background:white;"> 
					<div style="height:50%;overflow:hidden;width:100%;border-bottom:1px dashed #ccc;line-height:25px;">
						<div style="float:left;width:50%;">
							<div class="title">快速设置默认管理范围</div>
							<div id="fastsetorg" style="padding:0px 2.5%;min-height:25px;" ></div>
						</div> 
						<div style="width:45%;float:right;padding:0px 2.5%;text-indent: 20px;">
							管理员只能授权自己所能管理的组织，如果该组织及其子组织不在管理员所能管理组织下，则不能进行授权
						</div>
					</div> 
					<div style="height:50%;overflow:hidden;width:100%;line-height:25px;">
						<div style="float:left;width:50%;">
							<div class="title">快速设置数据区管理范围</div>
							<div id="yab139s" style="width:100%;float:left;padding:0px 2.5%;min-height:25px;"></div>
						</div>
						<div style="width:45%;float:right;padding:0px 2.5%;text-indent: 20px;">
							管理员只能授权自己所能管理的数据区，如果这些数据区不包括该人员所在组织的经办机构默认数据区，则不能进行授权，只能授权包含的数据区
						</div> 
					</div>
				</ta:panel>
				<!--<ta:box cssStyle="height:30px">
					 <a href="javascript:;" class="fastsetting"  onclick="fnToggleFastSetting()">快速设置权限</a>
					<span id="tooltipMsg" style="width: 150px;font-style:italic;color:red;position: absolute;top: 1px;left: 150px;padding-top: 8px;"></span> 
				</ta:box>
				-->
			</ta:box> 
		</ta:box>
	</ta:box>
	<ta:panelButtonBar>
		<ta:button key="保存[S]" onClick="fnDetachAdd()" isok="true" icon="icon-add1" hotKey="S"/>
		<ta:button key="关闭[X]" onClick="parent.Base.closeWindow('addAdmin')" icon="icon-no" hotKey="X"/>
	</ta:panelButtonBar>
</ta:panel>  
</body>
</html>
<script type="text/javascript">
// left:750px;
	$(document).ready(function() {
		$("body").taLayout();
 		fnSearch();
	}) 
	//人员查询(非管理员)
	function fnSearch() {
		clearFastDiv(); 
		Base.submit("addAdminForm", "adminUserMgAction!queryNoAdminUsers.do");
		
	} 
	//是否进行快速设置权限
	function fnToggleFastSetting(){
	
		var option = Base.getValue("isfastsetting");
		if(option == "0"){
// 			Base.showObj("fastsettingpanel");
// 			$("manInfoBox").height("100px");			 
			Base.showObj("fastSetBox");
			$("a.fastsetting").css({background:"url('../resource/images/0002.png') no-repeat right center"});
			Base.setValue("isfastsetting","1");
			
			$("#manInfoBox").attr("heightDiff", "180");
			$("#manInfoBox").taLayout();
		}else{
// 			Base.hideObj("fastsettingpanel");
// 			$("#manInfoBox").height("350px");
//			$("#manInfoBox").css("height","210px");
// 			$("#userGrid .slick-viewport").css("height","220px");
// 			$("#userGrid .slick-grid-canvas").css("height","220px");
			
// 			$("fastSetBox").css("display","none");
			Base.hideObj("fastSetBox");
			$("a.fastsetting").css({background:"url('../resource/images/0001.png') no-repeat right center"});
			Base.setValue("isfastsetting","0");
			$("#manInfoBox").attr("heightDiff", "30");
			$("#manInfoBox").taLayout();
		}
// 		$("#contentDIV").resize();
// 		$("body").taLayout();
// 		$("body").tauipanel();
 		
// 		Base.rebuildGridFromHtml();
		
	}
	//点击人员时设置权限快速设置里面的选项
	function fnSetFastPermissionOptions(e,rowdata){
		//setHightLightSelectedRow(e,"#ffffd5");//蓝色64A9E9
		
		clearFastDiv();  
		var positionid  = rowdata.positionid;
		var name 		= rowdata.name; 
		queryInfo(positionid,name);
	} 
	/** 
	* 高亮显示选中行
	**/
	function setHightLightSelectedRow(e,color){
		var obj = (e.target ? e.target : e.srcElement); 
		$(obj).parent().siblings().css("backgroundColor","none");
		$(obj).parent().css("backgroundColor",color);
	}
	/**
	* 清空
	**/
	function clearFastDiv(){
		$("#fastsetorg").html("");
		$("#yab139s").html("");
	}
	
	//点击保存按钮触发的事件
	function fnDetachAdd(){
		var rowData = Base.getGridSelectedRows("userGrid"); //获得表格选中行的JSON数组
		if(rowData.length == 0){
			Base.msgTopTip("请选择一个管理员!");
			return;
		}
		
		var option = Base.getValue("isfastsetting");
		//不保存默认设置里的"数据区" 和  "组织范围"
		if(option == "0"){
			Base.submit(null,"adminUserMgAction!addAdminUser.do",{"dto['positionid']":rowData[0].positionid},null,false,function(){
				Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:5px;text-align:center;'>保存成功</div>");
				parent.Base.submit("", "adminUserMgAction!queryAdminMgUsers.do");
				parent.Base.closeWindow("addAdmin");
			});
		}else{ 
			var orgid = $("#orgid:checked").val();
			var yab139s = "";
			$("input[name='yab139']:checked").each(function(i){//遍历每一个名字为yab139的复选框，其中选中的执行函数   
				if(i==0){
					yab139s += $(this).val();//将选中的值添加到数组yab139s中    
				}else{
					yab139s += ","+$(this).val();//将选中的值添加到数组yab139s中    
				}
	        });
	        
	        var param = {"dto['positionid']":rowData[0].positionid,"dto['orgid']":orgid,"dto['yab139s']":yab139s};
			Base.submit(null,"adminUserMgAction!addAdminUser.do",param,null,false,function(){
				parent.Base.msgTopTip("<div style='width:160px;margin:0 auto;font-size:16px;margin-top:5px;text-align:center;'>保存成功</div>");
				parent.Base.submit("", "adminUserMgAction!queryAdminMgUsers.do");
				parent.Base.closeWindow("addAdmin");
			});
		}
	}
	
	function queryInfo(positionid,name){
		Base.showMask();
		 $.ajax({  
	        type:"post",  
	        url:"<%=path%>/org/admin/adminUserMgAction!queryForFastPermissionSettion.do",// 跳转到 action   
	        data:{             
	            "dto['positionid']":positionid
	        },  
	        dataType:"json",  
	        success:function(data){
	        	Base.hideMask();
	        	var yab139CurList = data.yab139CurList;//当前用户拥有的管理权限
	        	
	        	var arrOrg = data.org.orgnamepath.split("/")
				var org = "<input id='orgid' type='checkbox' name='orgid' checked='checked' value='"+data.org.orgid+"'/>默认为"+name+"所属部门-->"+ arrOrg[arrOrg.length-1] +"及子部门";
				$("#fastsetorg").html(org);

				var yab139s = "";
				
				for(i=0;i<data.datas.length;i++){  //目标用户拥有的默认数据区--				
					for(j=0;j<yab139CurList.length;j++){//当前用户（管理员）拥有的数据区权限
						if(data.datas[i].codeValue == yab139CurList[j].codeValue){
							yab139s += "<input id='yab139'"+i+"  type='checkbox' name='yab139' checked='checked' value='"+data.datas[i].codeValue+"'/>"+data.datas[i].codeDesc+"&nbsp;&nbsp;";
							break;						
						}
						if(j== yab139CurList.length - 1)
							yab139s += "<input id='yab139'"+i+"  type='checkbox' name='yab139'  value='"+data.datas[i].codeValue+"' disabled='disabled' />"+data.datas[i].codeDesc+"&nbsp;&nbsp;";
					}
					
				}
				$("#yab139s").html(yab139s);
			},
	        error : function() {  
	        	Base.hideMask();
// 	             alert("请求异常！");  
	        }  
	});  
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>