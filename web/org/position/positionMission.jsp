<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head>
		<title>新增修改岗位</title>
		<%@ include file="/ta/inc.jsp"%>
	</head>
	<body id="body1" class="no-scrollbar" style="margin:0;padding:0">
		<ta:pageloading/>
		<ta:panel hasBorder="false" expanded="false" fit="true"  bodyStyle="overflow:auto;">
			<ta:datagrid id="posMissionwin_posMission"  fit="true"  forceFitColumns="true">
				<ta:datagridItem id="menuname" width="300"  key="功能名称" formatter="fnMenuname" />
				<ta:datagridItem id="effecttime"  key="有效期" />
				<ta:datagridItem id="auditstate" hiddenColumn="true" key="通过审核" collection="AUDITSTATE"  />
			</ta:datagrid>
		</ta:panel>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function () {
		$("body").taLayout();
		var isAudite = Base.getJson("positionUserMgAction!getIsAudite.do");
		if(isAudite.isAudite){
			Base.setGridColumnShow("posMissionwin_posMission", "auditstate");
		}else{
			Base.setGridColumnHidden("posMissionwin_posMission","auditstate");
		}
	});
	
/** 格式化菜单名称*/
function fnMenuname(row, cell, value, columnDef, dataContext){
	if(dataContext.menulevel == "1"){
		return value;
	}else{
		var count = (dataContext.menulevel -1) * 1;
		return "<div style='text-indent: "+ count +"em;'><span style='color:#DBDEE4;'>┊┄</span>"+value+"</div>";
	}
}
/** 格式化有效期*/
function fnEffecttime(row, cell, value, columnDef, dataContext){
		return value;
}
</script>
<%@ include file="/ta/incfooter.jsp"%>