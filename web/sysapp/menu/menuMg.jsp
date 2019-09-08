<%@page import="com.yinhai.sysframework.config.SysConfig"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<jsp:directive.page import="java.util.Date"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ taglib prefix="ta" tagdir="/WEB-INF/tags/tatags" %>
<%
	String isaudite = SysConfig.getSysConfig("isAudite", "false");
%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
	<head>
		<title>菜单管理</title>
		<%@ include file="/ta/inc.jsp"%>
		<link href="<%=basePath%>ta/resource/themes/2015/zTreeIcons.css" rel="stylesheet" type="text/css" />
	<style>
		.selectpic{
			display: none;
			position: absolute !important;
			border:1px solid #8DB2E3;
			width:400px;
			height:200px;
			z-index:100;
			background:#FFF;
			overflow-y:auto;
		}
		.selectpic div {
			display: inherit;
			float:left;
			height:22px;
			width:22px;
			padding:5px;
		}	
		.selectpic div li{
			display: inherit;
			height:20px;
			width:20px;
		}
		.selectpic li:hover{
			border: 1px solid #8DB2E3;
		}
		.selectpic li.keyover{
			border: 1px solid #8DB2E3;
		}		
		
		.selectpic li:active{
			background:yellow;
		}
		.selectpic li.selected{
			border: 1px solid red;
		}
	</style>
	</head>
	<body layout="border" layoutCfg="{leftWidth:340}" style="padding:0px;margin:0px" class="no-scrollbar">
		<ta:pageloading/>
		<ta:box position="left" >
			<ta:panel id="treePanel" withToolBar="true" hasBorder="false" bodyStyle="overflow:auto" fit="true">
				<ta:panelToolBar align="right">
					<ta:button asToolBarItem="true" isShowIcon="true" key="" id="expand" icon="btn-expand" onClick="Base.expandTree('menuTree');" />
					<ta:button asToolBarItem="true" isShowIcon="true" key="" id="collapse" icon="btn-collapse" onClick="Base.collapseTree('menuTree');"/>
				</ta:panelToolBar>
				<ta:tree id="menuTree" showLine="true" nameKey="menuname" childKey="menuid" parentKey="pmenuid" async="true" asyncUrl="menuMgAction!webGetAsyncMenu.do" asyncParam="['menuid']"
					editable="true" onDblClick="fnDblClk" onRightClick="fnOnRightClick" beforeEdit="fnToEdit" beforeRemove="fnBfRemove" onRemove="fnRemoveMenu"
					keepLeaf="true" keepParent="true" beforeDrop="fnBeforeDrop" onDrop="fnOnDrop" onClick="fnClick"
					editTitle="编辑当前菜单" removeTitle="删除当前菜单" addTitle="添加子菜单"
					showAddBtn="true" onAdd="fnAddMenu"/>
			</ta:panel>
		</ta:box>
		<ta:box position="center" >
			<ta:box id="menuPanel" fit="true" cssStyle="overflow:auto;padding:30px 60px 10px 30px;" layout="column">
				<ta:form id="menuForm" fit="true" cols="2">
					<ta:text  id="menuid" key="功能编号" display="none" span="2"/>
					<ta:text  id="menuname" key="功能名称" maxLength="60" required="true" span="2"  columnWidth="0.6"/>
					<ta:text  id="pmenuid" key="上级功能编号" display="none" span="2"  value="0"/>
					<ta:text  id="menunamepath" display="none" span="2"/>
					<ta:text  id="menuidpath" display="none" required="true" readOnly="true" span="2"/>
					<ta:text  id="pmenuname" key="上级功能" readOnly="true" span="2"  columnWidth="0.6"/>
					<ta:selectInput  required="true" id="menutype" key="菜单类型" span="2" collection="MENUTYPE"  columnWidth="0.6" filterOrg="false"/>
					<ta:text  id="iconSkin" key="菜单图片" span="2"  columnWidth="0.6"/>
					<div class="fielddiv ztree" style="float:left;display:block;"><div><li><span id="imgbt" class="button ico_docu"></span></li></div></div>
					<ta:selectInput   id="syspath" key="系统路径" span="2"  columnWidth="0.6"  required="true" bpopTipMsg="在config.properties中配置，默认值为config中的curSyspathId属性"/>
					<ta:text  id="url" key="功能URL" span="2"  columnWidth="0.6" />
					<ta:text  id="accesstimeel" key="限制时间" span="2"  columnWidth="0.6" />
					<ta:text  id="methods" key="新增需授权方法"  span="2"  columnWidth="0.6" bpopTipPosition="left" textHelpPosition="bottomLeft" textHelpWidth="300" textHelp="该输入框的输入值是以该Action下要进行单独授权的方法名（多个方法名之间以英文逗号分隔）组成， 例如“save,update”，这样该菜单下会再生成2个安全策略为要认证不显示的子菜单。比如：功能url为test/testAction.do,则在该菜单下的jsp页面中的按钮，如果id=“test.testAction.save”，则该按钮就具有了权限控制的功能"/>
					<ta:radiogroup id="consolemodule" cols="2" key="工作台模块">
						<ta:radio key="是" value="0" onClick="fnConsolemoduleClick(0)"></ta:radio>
						<ta:radio key="否" value="1"  onClick="fnConsolemoduleClick(1)" checked="true"></ta:radio>
					</ta:radiogroup>
					<ta:box span="2"></ta:box>
					<ta:radiogroup  id="securitypolicy" collection="POLICY" key="安全策略" cols="2" required="true" filterOrg="false">
					</ta:radiogroup>
					<ta:box span="2"></ta:box>
					<ta:radiogroup key="数据区权限" id="useyab003" cols="2" >
						<ta:radio key="启用" value="0"></ta:radio>
						<ta:radio key="禁用" value="1"></ta:radio>
					</ta:radiogroup>
					<ta:box span="2"></ta:box>
					<ta:radiogroup key="经办人员岗位"  id="isdismultipos" cols="2" required="true" >
						<ta:radio key="明确" value="0"></ta:radio>
						<ta:radio key="不明确" value="1"></ta:radio>
					</ta:radiogroup>
					<ta:box span="2"></ta:box>
					<ta:radiogroup key="权限审核"  id="isaudite" cols="2" required="true" >
						<ta:radio key="需要" value="0"></ta:radio>
						<ta:radio key="不需要" value="1"></ta:radio>
					</ta:radiogroup>
					<ta:box span="2"></ta:box>
					<ta:radiogroup  key="有效性" id="effective" collection="EFFECTIVE" cols="2" required="true" filterOrg="false">
					</ta:radiogroup>
					<ta:text id="menulevel" key="菜单层级" display="false"></ta:text>
					<%--<ta:text id="selectImage" key="选中菜单图片" />--%>
	<!-- 				<ta:selectInput id="type" collection="YAE162" key="菜单类型" span="2" required="true"/> -->
					<%-- <ta:text id="shortId" key="快捷访问码" span="2"/>目前没用到--%>
	<!-- 				<ta:text id="orderId" key="排序号" readOnly="true" required="true" value="0" span="2"/> -->
					<%-- <div span="3">
					<s:debug></s:debug>
					</div> --%>
					<ta:box span="2"></ta:box>
					<ta:buttonLayout align="right" columnWidth="0.6" >
						<ta:submit  isok="true" isIncludeNullFields="true" submitIds="menuPanel" url="menuMgAction!webUpdateMenu.do" id="update" key="保存[S]" hotKey="S" icon="btn-save" successCallBack="function(){Base.refleshTree('menuTree',Base.getValue('pmenuid'));Base.msgTopTip('更新成功')}" disabled="true"/>
						<ta:submit  isok="true" submitIds="menuPanel" url="menuMgAction!webSaveMenu.do" id="save" key="保存[S]" hotKey="S" icon="btn-save" successCallBack="fnSaveSuccessCb" display="false" disabled="true"/>
						<ta:button  key="取消[C]" id="cancel" icon="btn-delete" hotKey="C" onClick="fnDblClk(null, 'menuTree', Base.getValue('menuId')!=''?Base.getObj('menuTree').getNodeByParam('id',Base.getValue('menuId')):Base.getObj('menuTree').getSelectedNodes()[0]);" disabled="true"/>
						<%-- <ta:submit asToolBarItem="true" key="test" id="cance3l" url="menuMgAction!webGetSyncMenu.do" icon="btn-delete" parameter="{'profiling':'yes'}"/> --%>
					</ta:buttonLayout>
				</ta:form>
			</ta:box>
				
		</ta:box>
	<div id="rm" style="width:150px;font-size:12px;">
		<div id="rm_add" class="btn-app">添加子菜单</div>
		<%--<div id="rm_add2">添加到常用菜单</div>--%>
		<div id="rm_modify">修改当前菜单</div>
		<div id="rm_del">删除当前菜单</div>
	</div>
	<div id="seldiv" class="ztree selectpic">
		<div><li><span class="button tree-bug" title="tree-bug"></span></li></div>
		<div><li><span class="button tree-group " title="tree-group"></span></li></div>
		<div><li><span class="button tree-menu" title="tree-menu"></span></li></div>
		<div><li><span class="button tree-organisation" title="tree-organisation"></span></li></div>
		<div><li><span class="button tree-apply " title="tree-apply"></span></li></div>
		<div><li><span class="button tree-car " title="tree-car"></span></li></div>
		<div><li><span class="button tree-clock " title="tree-clock"></span></li></div>
		<div><li><span class="button tree-destop " title="tree-destop"></span></li></div>
		<div><li><span class="button tree-doc " title="tree-doc"></span></li></div>
		<div><li><span class="button tree-duty " title="tree-duty"></span></li></div>
		<div><li><span class="button tree-groups " title="tree-groups"></span></li></div>
		<div><li><span class="button tree-ie " title="tree-ie"></span></li></div>
		<div><li><span class="button tree-letter " title="tree-letter"></span></li></div>
		<div><li><span class="button tree-pdf " title="tree-pdf"></span></li></div>
		<div><li><span class="button tree-phone " title="tree-phone"></span></li></div>
		<div><li><span class="button tree-provider " title="tree-provider"></span></li></div>
		<div><li><span class="button tree-search " title="tree-search"></span></li></div>
		<div><li><span class="button tree-setting " title="tree-setting"></span></li></div>
		<div><li><span class="button tree-star " title="tree-star"></span></li></div>
		<div><li><span class="button tree-tel " title="tree-tel"></span></li></div>
		<div><li><span class="button tree-userm " title="tree-userm"></span></li></div>
		<div><li><span class="button tree-users " title="tree-users"></span></li></div>
		<div><li><span class="button tree-comments " title="tree-comments"></span></li></div>
		<div><li><span class="button tree-database " title="tree-database"></span></li></div>
		<div><li><span class="button tree-image " title="tree-image"></span></li></div>
		<div><li><span class="button tree-plugin " title="tree-plugin"></span></li></div>
		<div><li><span class="button tree-user " title="tree-user"></span></li></div>
		<div><li><span class="button tree-bookmark-new " title="tree-bookmark-new"></span></li></div>
		<div><li><span class="button tree-book-new " title="tree-book-new"></span></li></div>
		<div><li><span class="button tree-contact-new " title="tree-contact-new"></span></li></div>
		<div><li><span class="button tree-document-new " title="tree-document-new"></span></li></div>
		<div><li><span class="button tree-document-open " title="tree-document-open"></span></li></div>
		<div><li><span class="button tree-document-print-preview " title="tree-document-print-preview"></span></li></div>
		<div><li><span class="button tree-document-properties " title="tree-document-properties"></span></li></div>
		<div><li><span class="button tree-document-save " title="tree-document-save"></span></li></div>
		<div><li><span class="button tree-edit-cut " title="tree-edit-cut"></span></li></div>
		<div><li><span class="button tree-edit-delete " title="tree-edit-delete"></span></li></div>
		<div><li><span class="button tree-edit-find " title="tree-edit-find"></span></li></div>
		<div><li><span class="button tree-edit-find-replace " title="tree-edit-find-replace"></span></li></div>
		<div><li><span class="button tree-edit-paste " title="tree-edit-paste"></span></li></div>
		<div><li><span class="button tree-edit-redo " title="tree-edit-redo"></span></li></div>
		<div><li><span class="button tree-edit-select-all " title="tree-edit-select-all"></span></li></div>
		<div><li><span class="button tree-edit-undo " title="tree-edit-undo"></span></li></div>
		<div><li><span class="button tree-emblem-system " title="tree-emblem-system"></span></li></div>
		<div><li><span class="button tree-folder " title="tree-folder"></span></li></div>
		<div><li><span class="button tree-folder-new " title="tree-folder-new"></span></li></div>
		<div><li><span class="button tree-folder-remote " title="tree-folder-remote"></span></li></div>
		<div><li><span class="button tree-folder-saved-search " title="tree-folder-saved-search"></span></li></div>
		<div><li><span class="button tree-input-gaming " title="tree-input-gaming"></span></li></div>
		<div><li><span class="button tree-input-mouse " title="tree-input-mouse"></span></li></div>
		<div><li><span class="button tree-mail-attachment " title="tree-mail-attachment"></span></li></div>
		<div><li><span class="button tree-mail-forward " title="tree-mail-forward"></span></li></div>
		<div><li><span class="button tree-mail-message-new " title="tree-mail-message-new"></span></li></div>
		<div><li><span class="button tree-mail-reply-all " title="tree-mail-reply-all"></span></li></div>
		<div><li><span class="button tree-mail-send-receive " title="tree-mail-send-receive"></span></li></div>
		<div><li><span class="button tree-media-floppy " title="tree-media-floppy"></span></li></div>
		<div><li><span class="button tree-new " title="tree-new"></span></li></div>
		<div><li><span class="button tree-office-address-book " title="tree-office-address-book"></span></li></div>
		<div><li><span class="button tree-office-spreadsheet " title="tree-office-spreadsheet"></span></li></div>
		<div><li><span class="button tree-package-x-generic " title="tree-package-x-generic"></span></li></div>
		<div><li><span class="button tree-preferences-system " title="tree-preferences-system"></span></li></div>
		<div><li><span class="button tree-printer " title="tree-printer"></span></li></div>
		<div><li><span class="button tree-system-lock-screen " title="tree-system-lock-screen"></span></li></div>
		<div><li><span class="button tree-system-search " title="tree-system-search"></span></li></div>
		<div><li><span class="button tree-text-editor " title="tree-text-editor"></span></li></div>
		<div><li><span class="button tree-text-html " title="tree-text-html"></span></li></div>
		<div><li><span class="button tree-user-home " title="tree-user-home"></span></li></div>
		<div><li><span class="button tree-user-trash " title="tree-user-trash"></span></li></div>
		<div><li><span class="button tree-view-refresh " title="tree-view-refresh"></span></li></div>
		<div><li><span class="button btn-setting " title="btn-setting"></span></li></div>
		<div><li><span class="button btn-myplan" title="btn-myplan"></span></li></div>		
		<div><li><span class="button tree_code" title="tree_code"></span></li></div>		
		<div><li><span class="button tree_exception" title="tree_exception"></span></li></div>		
		<div><li><span class="button tree_menumana" title="tree_menumana"></span></li></div>		
		<div><li><span class="button tree_report" title="tree_report"></span></li></div>		
		<div><li><span class="button tree_rightsmana" title="tree_rightsmana"></span></li></div>		
		<div><li><span class="button tree_servers" title="tree_servers"></span></li></div>		
		<div><li><span class="button tree_syspath" title="tree_syspath"></span></li></div>		
		<div><li><span class="button tree_system" title="tree_system"></span></li></div>		
	</div>
	</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
		$(".radiogroup").height("100%");
		var treeObj = Base.getObj("menuTree");
		var treeNode = treeObj.getNodeByTId("menuTree_1");
		treeObj.selectNode(treeNode); // 选中第一个节点
		$("#seldiv").resizable({handles:'se, sw'});
		$("#iconSkin").focus(function(){
			var s = $("#seldiv");
			var $text = $(this);
			s.css({left:($text.offset().left),top:($text.offset().top+22)});
			s.show();
			
			$(document).bind('click.selectdiv__',function(e){
// 				var isie = (document.all) ? true : false;
				var target = e.srcElement || e.target;
// 			    if(document.all ? false : true)target = e.target;
			    if(target != s[0] && target != $text[0]){
					s.hide();
					$(">div li.keyover",s).removeClass('keyover');
					$(document).unbind('.selectdiv__');
				}
			});
			$(">div li span",s).bind('click.selectdiv__',function(){
				$text.val($(this).attr("title"));
				$("#imgbt").removeClass().addClass("button " + $(this).attr("title"));
				$(this.parentNode).addClass('selected');
				$(this.parentNode.parentNode).siblings().find('>li').removeClass('selected');
				$(">div li.keyover",s).removeClass('keyover');
				s.hide();
			});
			$(this).bind('keydown.selectdiv__',function(e){
				if(e.keyCode==39){//->
					var selected = $(">div li.keyover",s);
					if(selected.length==0){
						selected = $(">div:first li",s);
						$(selected[0]).addClass('keyover');
					}else{
						$(selected[0]).removeClass('keyover');
						$(selected[0].parentNode).next().find('>li').addClass('keyover');
					}
					e.keyCode = 0;
					e.cancelBubble = true;
					e.returnValue = false;
		
					// e.stopPropagation works in Firefox.
					if (e.stopPropagation) {
						e.stopPropagation();
						e.preventDefault();
					}					
				}else if(e.keyCode==37){//<-
					var selected = $(">div li.keyover",s);
					if(selected.length==0){
						selected = $(">div:last li",s);
						$(selected[0]).addClass('keyover');
					}else{
						$(selected[0]).removeClass('keyover');
						$(selected[0].parentNode).prev().find('>li').addClass('keyover');
					}
					e.keyCode = 0;
					e.cancelBubble = true;
					e.returnValue = false;
		
					// e.stopPropagation works in Firefox.
					if (e.stopPropagation) {
						e.stopPropagation();
						e.preventDefault();
					}							
				}else if(e.keyCode==13){//enter
					$(">div li.keyover button",s).click();
					$(">div li.keyover",s).removeClass('keyover');
				}else if(e.keyCode==40){//down
					s.show();
				}else if(e.keyCode==27){//esc
					s.hide();
					$(">div li.keyover",s).removeClass('keyover');
				}else if(e.keyCode==8 || e.keyCode==38){//退格取消
					e.keyCode = 0;
					e.cancelBubble = true;
					e.returnValue = false;
		
					// e.stopPropagation works in Firefox.
					if (e.stopPropagation) {
						e.stopPropagation();
						e.preventDefault();
					}
				}
			});
		});
		$("#rm").menu();
		$("#menuTree").bind('contextmenu', function(e){
			$('#rm').menu('show', {left: e.pageX, top: e.pageY});
			return false;
		});
		window.setTimeout("fnPageGuide(parent.currentBuinessId)", 300);
	});
		function fnPageGuide(currentBuinessId){
		    var data = [{id:$("#menuTree_1_a"),
		    	message:"这里选择对菜单进行的操作！"
		        },
		        {id:$("#url"),
		        message:"这里将填写菜单对应在项目中的地址!"
			      },
			      {id:$("#securitypolicy"),
			   	  message:"这里选择使用的安全策略!"
			      }
			]
			$("body").hintTip({
				replay 	: false,
				show 	: true, 
				cookname: currentBuinessId,
				data 	: data
			}); 
	}
	//单击展开树
	function fnClick(e,treeId,treeNode) {
		var treeObj = Base.getObj("menuTree");
		treeObj.expandNode(treeNode, true, false, true);
	}
	// 双击进入菜单编辑状态
	function fnDblClk(e, treeId, treeNode) {
		if (treeNode.menutype == "0") Base.filterSelectInput("menutype", []);
		if (treeNode.menutype == "1") Base.filterSelectInput("menutype", [0,2]);
		if (treeNode.menutype == "2") Base.filterSelectInput("menutype", [1]);
		Base.resetForm("menuForm");
		var treeObj = Base.getObj("menuTree");
		Base.submit("", "menuMgAction!webGetMenu.do", {"dto['menuid']":treeNode.menuid}, null, null, function(){
			Base.hideObj("save");
			Base.showObj("update");
			Base.setEnable(["update","cancel"]); // 将组件设置为可用并不能改变它的隐藏属性，所以前面还要调用showObj()方法
			Base.setValue("pmenuname", treeNode.getParentNode() ? treeNode.getParentNode().menuname : "");
			if (Base.getValue("unselectImage") != "")
				$("#imgbt").removeClass().addClass("button " + treeNode.iconSkin);
			Base.focus("menuname", 100);
			fnConsolemoduleClick(treeNode.consolemodule);
		}, null, false);
		
		if(e){
		    e.keyCode = 0;
		    e.cancelBubble = true;
		    e.returnValue = false;
		    if (e.stopPropagation) {
			   e.stopPropagation();
			   e.preventDefault();
		    }
		}

	}
	// 点击编辑按钮编辑菜单
	function fnToEdit(treeId, treeNode) {
		return fnDblClk(null, treeId, treeNode), false;
	}
	// 添加子菜单
	function fnAddMenu(event, treeId, treeNode) {
		if (treeNode.menutype == "0") Base.filterSelectInput("menutype", []);
		if (treeNode.menutype == "1") Base.filterSelectInput("menutype", [0,2]);
		if (treeNode.menutype == "2") Base.filterSelectInput("menutype", [1]);
		Base.resetForm("menuForm");
		Base.hideObj("update");
		Base.showObj("save");
		Base.setValue("pmenuid", treeNode.menuid);
		Base.setValue("menunamepath", treeNode.menunamepath);
		Base.setValue("pmenuname", treeNode.menuname);
		Base.setValue("menuidpath", treeNode.menuidpath);
		Base.setValue("menulevel",treeNode.menulevel);
		$("#imgbt").removeClass().addClass("button " + "ico_docu");
		Base.setValue("effective", "0");
		Base.setValue("securitypolicy", "1");
		Base.setValue("isdismultipos", "1");
		Base.setValue("syspath",treeNode.syspath);
		Base.setValue("useyab003",1);
		if("true" == "<%=isaudite%>"){//需要权限审核
			Base.setValue("isaudite",0);
		}else{//不需要权限审核
			Base.setValue("isaudite",1);
		}
		Base.setValue("consolemodule","1");
		fnConsolemoduleClick(1);
		//Base.submit("parentMenuId", "menuMgAction!getMaxYAE114.do");
		Base.setEnable("save");
		Base.showObj("save");
		Base.setDisabled("cancel");
		Base.focus("menuname", 100);
	}
	// 添加到常用菜单
	function fnAdd2CommMenu(event, treeId, treeNode) {
		if(treeNode.isParent) {
			Base.alert("该菜单不允许添加到常用菜单！");
		} else {
			Base.submit(null, "comMenuAction!addComMenu.do", {menuid:treeNode.menuid}, false, false);
		}
	}
	// 右键点击菜单事件
	function fnOnRightClick(event, treeId, treeNode) {
		var treeObj = Base.getObj("menuTree");
		$("#rm_add").unbind("click").bind("click", function(){
			fnAddMenu(event, treeId, treeNode);
		});/*
		$("#rm_add2").unbind("click").bind("click", function(){
			fnAdd2CommMenu(event, treeId, treeNode);
		});*/
		$("#rm_modify").unbind("click").bind("click", function(){
			fnDblClk(event, treeId, treeNode)
		});
		$("#rm_del").unbind("click").bind("click", function(){
			if (!treeNode.pmenuid || treeNode.pmenuid == "") {
				Base.alert("不能删除根菜单！", "warn");
			} else if (treeNode.isParent) {
				Base.confirm("删除该菜单会把它的子菜单一并删除，确实要删除吗？", function(yes){
					if(yes)
						fnRemoveMenu(event, treeId, treeNode);
						//Base.submit(null, "menuMgAction!webDeleteMenu.do", {"dto['menuid']":treeNode.menuid}, false, false, function(){Base.refleshTree("menuTree", treeNode.pmenuid);});
				});
			} else {
				Base.confirm("确实要删除该菜单吗？", function(yes){
					if(yes) 
						fnRemoveMenu(event, treeId, treeNode);
						//Base.submit(null, "menuMgAction!webDeleteMenu.do", {menuId:treeNode.menuid}, false, false, function(){Base.refleshTree("menuTree", treeNode.pmenuid);});
				});
			}
		});
		if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
			treeObj.cancelSelectedNode();
		} else if (treeNode) {
			treeObj.selectNode(treeNode);
		}
	}
	// 新增菜单成功后的回调函数
	function fnSaveSuccessCb(data) {
		var node = Base.getObj("menuTree").getNodeByParam("menuid", Base.getValue("pmenuid"));
		if(node.pmenuid && node.pmenuid != 0){//父菜单不为根菜单
			if(node.isleaf == 0){
				Base.refleshTree('menuTree', node.pmenuid);
			}else{
				Base.refleshTree('menuTree', node.menuid);
			}
		}else{
			Base.refleshTree('menuTree', 1);
		}
		if (confirm("新增菜单成功，是否继续新增？")) {
			fnAddMenu(null, "menuTree", node);
		} else {
			Base.hideObj("save");
			Base.showObj("update");
			Base.setEnable(["update","cancel"]);
			Base.setValue("useyab003",1);
		}
	}
	// 判断和提示能否删除菜单
	function fnBfRemove(treeId, treeNode) {
		if (!treeNode.pmenuid || treeNode.pmenuid == "") {
			return Base.alert("不能删除根菜单！", "warn"), false;
		} else if (treeNode.isParent) {
			return confirm("删除该菜单会把它的子菜单一并删除，确实要删除吗？");
		} else {
			return confirm("确实要删除该菜单吗？");
		}
	}
	// 删除菜单
	function fnRemoveMenu(event, treeId, treeNode) {
		Base.submit(null, "menuMgAction!webDeleteMenu.do", {"dto['menuid']":treeNode.menuid}, false, false, function(){
			Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
			Base.resetForm("menuForm");
			Base.setDisabled("save,update,cancel");
			var node = treeNode.getParentNode();
			if(node){
				var children = node.children;
				if(children && children.length > 0){
					Base.refleshTree("menuTree", treeNode.pmenuid);
				}else{
					Base.refleshTree("menuTree", node.pmenuid);
				}
			}else{
				Base.refleshTree("menuTree", 1);
			}
		});
	}
	// 判断和提示能否拖拽调整菜单顺序
	function fnBeforeDrop(treeId, treeNodes, targetNode, moveType) {
		var treeNode = treeNodes[0];
		if (treeNode.pmenuid != targetNode.pmenuid) {
			return Base.alert("非同级菜单间不支持排序！"), false;
		} else if (moveType == "inner") {
			return Base.alert("不支持改变菜单级次！"), false;
		}
		return confirm("是否保存对菜单顺序的修改？");
	}
	// 拖拽调整菜单顺序
	function fnOnDrop(event, treeId, treeNodes, targetNode, moveType) {
		var treeNode = treeNodes[0];
		Base.resetForm("menuForm");
		Base.setDisabled("update,save,cancel");
		var pNode = treeNodes[0].getParentNode();
		var sortid = [];
		for (var i = 0; i < pNode.children.length; i ++) {
			sortid.push({menuid: pNode.children[i].menuid});
		}
		Base.submit(null, "menuMgAction!webSortMenus.do", {sortMenuids:Ta.util.obj2string(sortid)}, false, false, function(){Base.refleshTree('menuTree', treeNode.pmenuid);});
	}
	function fnConsolemoduleClick(type) {
		if(type == null) {
			Base.setEnable("securitypolicy");return;
		}
		switch (Number(type)) {
			case 0:
				Base.setValue("radio_1_securitypolicy", "");
				Base.setValue("radio_4_securitypolicy", "");
				Base.setDisabled("radio_1_securitypolicy,radio_4_securitypolicy");
				break;
			case 1:
				Base.setEnable("securitypolicy");
				break;
			default:
				Base.setEnable("securitypolicy");
		}
	}
</script>
<%@ include file="/ta/incfooter.jsp"%>