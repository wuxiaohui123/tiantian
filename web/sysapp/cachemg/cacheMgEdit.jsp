<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
	<ta:panel id="box1" fit="true" withButtonBar="true" hasBorder="false" bodyStyle="padding:10px 10px 0px 0px;">
			<ta:text id="t_codeType"  key="代码类别" readOnly="true" required="true"/>
			<ta:text id="t_codeTypeDESC"  key="类别名称" readOnly="true" required="true"/>
			<ta:text id="t_codeValue"  key="代码值"  readOnly="true" required="true" maxLength="6"/>
			<ta:text id="t_codeDESC"  key="代码名称"  required="true"/>
			<ta:text id="t_orgId"  key="经办机构" display="false"/>
			<ta:panelButtonBar align="right">
				<ta:button key="保存" icon="icon-save" onClick="fnEdit()" id="editBtn" isok="true"/>
				<ta:button key="保存" icon="icon-save" onClick="fnSave()" id="saveBtn" display="false" isok="true"/>
				<ta:button key="关闭" icon="icon-remove" onClick="Base.closeWindow('edit')"/>
			</ta:panelButtonBar>
	</ta:panel>
<script type="text/javascript">
	function fnEdit(){
		Base.submit("box1","appCodeMainAction!saveEdit.do",{},null,null,function(){
			Base.closeWindow("edit");
		})
	}
	function fnSave(){
		Base.submit("box1","appCodeMainAction!saveEdit.do",{"dto['insertApp']":"1"},null,null,function(){
			Base.closeWindow("edit");
		});
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>