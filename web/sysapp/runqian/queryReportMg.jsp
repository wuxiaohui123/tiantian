<%@page import="com.yinhai.sysframework.config.SysConfig"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@taglib prefix="ta" tagdir="/WEB-INF/tags/tatags"%>
<%
 String curSyspathId = SysConfig.getSysConfig("curSyspathId");
%>
<html xmlns="http://www.w3.org/1999/xhtml" style="height:100%">
<head>
<title>润乾报表菜单管理</title>
<%@ include file="/ta/inc.jsp"%>
<style>
			a:link {
			    color: #3764A0;
			    text-decoration:none;
		    }
		    a:visited {
			    color:#00FF00;
			    text-decoration:none;
		    }
		    a:hover {
			    color:#000000;
			    text-decoration:underline;
		    }
		    #selectopposite:active {
			    color:#FFFFFF;
			    text-decoration:none;
		    }
		    #selectall:active {
			    color:#FFFFFF;
			    text-decoration:none;
		    }
		    .checkBoxCss{
		    margin-top: 3px;
		    }
.selectpic {
	display: none;
	position: absolute !important;
	border: 1px solid #8DB2E3;
	width: 400px;
	height: 200px;
	z-index: 100;
	background: #DDD;
	overflow-y: auto;
}

.selectpic div {
	display: inherit;
	float: left;
	height: 22px;
	width: 22px;
	padding: 5px;
}

.selectpic div li {
	display: inherit;
	height: 20px;
	width: 20px;
}

.selectpic li:hover {
	border: 1px solid #8DB2E3;
}

.selectpic li.keyover {
	border: 1px solid #8DB2E3;
}

.selectpic li:active {
	background: yellow;
}

.selectpic li.selected {
	border: 1px solid red;
}
.fielddiv_163 .fielddiv2 {
    margin: 0px;
}
</style>
</head>
<body class="no-scrollbar" layout="border"  layoutCfg="{leftWidth:240}"
	style="padding:0px;margin:0px;">
	<ta:pageloading />
	<ta:box position="left" key="首先添加子菜单或编辑报表类菜单"  >
		<ta:panel id="treePanel" withToolBar="true" fit="true" 
			hasBorder="false" bodyStyle="overflow:auto;">
			<ta:panelToolBar align="right">
				<ta:button asToolBarItem="true" key="" id="expand" icon="btn-expand"
					onClick="Base.expandTree('menuTree');" />
				<ta:button asToolBarItem="true" key="" id="collapse"
					icon="btn-collapse" onClick="Base.collapseTree('menuTree');" />
			</ta:panelToolBar>
			<ta:tree id="menuTree" showLine="true" async="true" nameKey="menuname" childKey="menuid" parentKey="pmenuid"
				asyncUrl="../sysapp/menuMgAction!webGetAsyncMenu.do" asyncParam="['menuid']"
				editable="true" onDblClick="fnDblClk" onRightClick="fnOnRightClick"
				beforeEdit="fnToEdit" beforeRemove="fnBfRemove"
				onRemove="fnRemoveMenu" keepLeaf="true" keepParent="true"
				beforeDrop="fnBeforeDrop" onDrop="fnOnDrop" editTitle="编辑当前菜单"
				removeTitle="删除当前菜单" addTitle="添加子菜单" showAddBtn="true"
				onAdd="fnAddMenu" />
		</ta:panel>
	</ta:box>
	<ta:box position="center">
		<ta:form id="menuForm" fit="true">
			<ta:panel id="menuPanel" withToolBar="true" fit="true" bodyStyle="padding:3px;" hasBorder="false" layout="column" >
				<ta:panelToolBar align="left">
					<ta:button  onClick="updateMenu()" id="update" key="保存设置[S]" hotKey="S"
						icon="btn-save"  disabled="true" />
					<ta:button  onClick="saveMenu()" id="save" key="保存设置[S]" hotKey="S"
						icon="btn-save"  display="false" disabled="true" />
					<span id="tips_1" style="color:#0099CC;margin-left:5%"><FONT style="font-family: 微软雅黑;font-size:13px;font-weight:bold">Tips：编辑菜单配置  -> 选择报表类型 -> 输入报表id -> [新增报表]-> [保存设置]</FONT></span>
				</ta:panelToolBar>
				<ta:tableView width="100%" id="rqbbtable" tableCssStyle="margin-top:10px;" hasOuterBorder="true" key="菜单相关配置">
					<ta:tr >
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="功能名称">
						</ta:td>
						<ta:td cssStyle="text-align: left" colspan="3">
							<ta:text id="menuid" key="功能编号" display="false"  />
							<ta:text id="menuname"  required="true"/>
						</ta:td>
					</ta:tr>
					<ta:tr >
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="上级功能">
						</ta:td>
						<ta:td cssStyle="text-align: left" colspan="3">
							<ta:text id="pmenuid" display="none" value="0"/>
				            <ta:text id="menunamepath" display="none"/>
				            <ta:text id="menuidpath" display="none"/>
				            <ta:text id="menulevel" key="菜单层级" display="false"></ta:text>
				            <ta:text id="effective" key="是否可用" display="false"></ta:text>
				            <ta:text id="isdismultipos" key="菜单层级" display="false"></ta:text>
							<ta:text id="pmenuname"  required="true" readOnly="true"/>
						</ta:td>
					</ta:tr>
					<ta:tr>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="菜单图片">
						</ta:td>
						<ta:td cssStyle="text-align: left">
							<ta:text  id="iconSkin"  cssStyle="float:left;width:80%;"/>
							<div class="fielddiv ztree" style="float:left;display:block;">
								<div>
									<li><span type="button" id="imgbt" class="button ico_docu"></span>
									</li>
								</div>
							</div>
						</ta:td>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="系统路径">
						</ta:td>
						<ta:td >
							<ta:selectInput id="syspath" required="true" bpopTipMsg="在config.properties中配置，默认值为config中的curSyspathId属性"/>	
						</ta:td>
					</ta:tr>
					<ta:tr>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="菜单类型">
						</ta:td>
						<ta:td cssStyle="text-align: left">
							<ta:selectInput id="menutype" collection="MENUTYPE" required="true" value="2" />
						</ta:td>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="安全策略">
						</ta:td>
						<ta:td>
							<ta:selectInput id="securitypolicy" collection="POLICY" filterOrg="false" required="true" value="1" />	
						</ta:td>
					</ta:tr>
					<ta:tr >
						<ta:td id="bbxgpz" align="center" cssStyle="padding:10px;" cssClass="tableView-title" colspan="4"  content="报表相关配置"/>
					</ta:tr>
					<ta:tr>
						<ta:td align="right"  cssStyle="background-color:#E3ECF8;"
						key="报表类型" />
						<ta:td cssStyle="text-align: left" >
								<ta:selectInput id="raqtype" value="1"  onSelect="fnIsViewParam" filter="2" collection="RAQTYPE" key="" />
						</ta:td>
						<ta:td colspan="2"  cssStyle="text-align: left">
							<ta:button id ="addBbBtn" cssStyle="" key="新增报表[A]" onClick="fnAddBb()" hotKey="A" icon="xui-icon-tableAdd" disabled="false"/>
						</ta:td>
					</ta:tr>
					<ta:tr>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="报表标识">
						</ta:td>
						<ta:td >
							<ta:text id="raqfilename" onClick="fnShowTree_1()"  />
							<div id="DropdownTreeBackground_1" style="display:none;position:absolute;z-index:99999;height:210px;width:180px;background-color:white;border:1px solid;overflow:auto;">
								<ta:tree id="dropdownTree_1" async="true" asyncUrl="queryReportMgAction!getAsyncData_1.do" asyncParam="['id']" onClick="fnSlctTargetProcType_1"/>
							</div>
							<ta:text id="praqfilename"  display="false" onClick="fnShowTree_0();"/>
							<div id="DropdownTreeBackground_0" style="display:none;position:absolute;z-index:99999;height:210px;width:180px;background-color:white;border:1px solid;overflow:auto;">
								<ta:tree id="dropdownTree_0" async="false" asyncParam="['id']" onClick="fnSlctTargetProcType_0"/>
							</div>
						</ta:td>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="报表显示比例" >
						</ta:td>
						<ta:td cssStyle="text-align: left">
							<ta:number id="scaleexp" precision="2" toolTip="Tips：如设置为1.5,报表按150%放大" labelWidth="0"/>	
						</ta:td>
					</ta:tr>
					<ta:tr>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="报表名称">
						</ta:td>
						<ta:td >
							<ta:text id="raqname" readOnly="true"/>
						</ta:td>
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="每页显示数">
						</ta:td>
						<ta:td >
							<ta:number id="limited" toolTip="Tips：如果报表属性设置为按行分页，该设置才生效" labelWidth="0"/>	
						</ta:td>
					</ta:tr>
					<ta:tr cssStyle="display:none">
						<ta:td align="right" cssStyle="background-color:#E3ECF8;" key="功能URL">
						</ta:td>
						<ta:td colspan="3">
							<ta:text id="url" display="false"/>
							<ta:textarea id="functionUrl"  height="60px"  readOnly="true" cssStyle="text-indent:5px;"/>
						</ta:td>
					</ta:tr>
					<ta:tr >
						<ta:td align="right"  cssStyle="background-color:#E3ECF8;" >
							<ta:box cssStyle="padding-left:20px;padding-right:10px;color: #3764A0;" >
								<ta:box cssStyle="float:right;">
									<a  id="selectopposite"  onclick="fnSelectOpposite()" style="font-size: 14px;font-family: 微软雅黑;font-weight:bold;cursor:pointer">反选</FONT></a>
								</ta:box>
								<ta:box cssStyle="float:right;padding-right:20px">
									<a  id="selectall"    onclick="fnSelectAll()" style="font-size: 14px;font-family: 微软雅黑;font-weight:bold;cursor:pointer">全选</FONT> </a>
								</ta:box>
							</ta:box>
						</ta:td>
						<ta:td colspan="3">
							<ta:box >
								<ta:box cssStyle="width:75px;float:left"><ta:checkbox id="needprint" key="是否打印" value="1"/></ta:box>
								<ta:box cssStyle="width:120px;float:left"><ta:checkbox id="needsaveasexcel" key="是否保存为EXECL" value="1"/></ta:box>
								<ta:box cssStyle="width:84px;float:left"><ta:checkbox id="needsaveasexcel2007" key="EXECL2007" value="1"/></ta:box>
								<ta:box cssStyle="width:45px;float:left"><ta:checkbox id="needsaveaspdf" key="PDF" value="1"/></ta:box>
								<ta:box cssStyle="width:52px;float:left"><ta:checkbox id="needsaveasword" key="WORD" value="1"/></ta:box>
								<ta:box cssStyle="width:50px;float:left"><ta:checkbox id="needsaveastext" key="TEXT" value="1"/></ta:box>
							</ta:box>
						</ta:td>
					</ta:tr>
				</ta:tableView>
				<ta:panel key="菜单已配置报表" heightDiff="13" fit="true" cssStyle="margin:3px 0px 0px 0px">
					<ta:datagrid id="rqbbGrid" snWidth="30"  columnFilter="true"  haveSn="true" fit="true" > 
						<ta:datagridItem id="deleteBtn" key="删除" width="45" align="center" click="fnDelRow"  icon="xui-icon-tableDelete"/>
						<ta:datagridItem id="raqfilename" key="报表标识" align="center" showDetailed="true" width="180"/>
						<ta:datagridItem id="raqname" key="报表名称" align="center" showDetailed="true" width="180"/>
						<ta:datagridItem id="raqtype" key="报表类型" align="center" dataAlign="center" width="80" collection="RAQTYPE"/>
						<ta:datagridItem id="isgroup" key="是否分页" align="center" dataAlign="center" width="80" collection="ISGROUP"/>
						<ta:datagridItem id="limited" key="每页显示数" align="center"  width="80"/>
						<ta:datagridItem id="scaleexp" key="缩放比例" align="center"  width="80"/>
						<ta:datagridItem id="needprint" key="是否打印"  align="center" dataAlign="center" width="80" collection="ISGROUP"/>
						<ta:datagridItem id="needsaveasexcel" key="是否保存为EXCEL" dataAlign="center" align="center" width="120" collection="ISGROUP"/>
						<ta:datagridItem id="needsaveasexcel2007" key="EXECL2007" dataAlign="center" align="center" width="80" collection="ISGROUP"/>
						<ta:datagridItem id="needsaveaspdf" key="PDF"  align="center" dataAlign="center" width="45" collection="ISGROUP"/>
						<ta:datagridItem id="needsaveasword" key="WORD"  align="center" dataAlign="center" width="45" collection="ISGROUP"/>
						<ta:datagridItem id="needsaveastext" key="TEXT"  align="center" dataAlign="center" width="45" collection="ISGROUP"/>
					</ta:datagrid>
				</ta:panel>
			</ta:panel>
		</ta:form>
	</ta:box>
	<div id="rm" style="width:150px;font-size:12px;">
		<div id="rm_add" class="btn-app">添加子菜单</div>
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
		<div><li><span class="button tree-group " title="tree-group"></span></li></div>
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
	</div>
	<ta:box cssStyle="visibility:hidden;height:1px" ></ta:box>
</body>
</html>
<script type="text/javascript">
	$(document).ready(function() {
		$("body").taLayout();
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
				var target = e.srcElement || e.target;
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
		$("caption.tableView-title").css("margin-bottom", "10px");
		$("#bbxgpz").removeClass("table-td-cell");
		$("#menuForm").bind("mousedown", 
				function(event){
					if (!(event.target.id == "DropdownTreeBackground_1" || $(event.target).parents("#DropdownTreeBackground_1").length > 0||
						  event.target.id == "DropdownTreeBackground_0" || $(event.target).parents("#DropdownTreeBackground_0").length > 0
						  )) {
						fnHideTree();
					}
				});
		$("#raqfilename,#praqfilename").attr("readonly","readonly");
		Base.showMask("menuForm",false);
		$("input[type='checkbox']").addClass("checkBoxCss");
	});
	var selectFlag = true;
	//全选中
  	function fnSelectAll(){
		if(selectFlag){
			Base.setValue({"needsaveasexcel":"1","needsaveasexcel2007":"1","needsaveasword":"1","needsaveastext":"1","needsaveaspdf":"1","needprint":"1"});
		}
  	}
	//反选
	function fnSelectOpposite(){
		if(selectFlag){
		    checkedChange("needsaveasexcel");
		    checkedChange("needsaveasexcel2007");
		    checkedChange("needsaveasword");
		    checkedChange("needsaveastext");
		    checkedChange("needsaveaspdf");
		    checkedChange("needprint");
		}
	}
	function checkedChange(id){
	    var checked = Base.getObj(id).checked;		
	    if(checked){
		  Base.setValue(id,"");
	    }else{
		  Base.setValue(id,"1");
	    }
	}
	//点击是否展示分页
	function fnIsViewParam(){
		var raqtype = Base.getValue("raqtype");
		if(raqtype == "0"){
			selectFlag = false;
			Base.setReadOnly("needsaveasexcel,needsaveasexcel2007,needsaveasword,needsaveastext,needsaveaspdf,needprint");
			Base.setValue({"needsaveasexcel":"","needsaveasexcel2007":"","needsaveasword":"","needsaveastext":"","needsaveaspdf":"","needprint":""});
			Base.setValue("limited","");
			Base.setValue("raqfilename","");
			Base.showObj("praqfilename");
			Base.hideObj("raqfilename");
		}else if(raqtype == "1"){
			selectFlag = true;
			Base.setValue("praqfilename","");
			Base.showObj("raqfilename");
			Base.hideObj("praqfilename");
			Base.setEnable("needsaveasexcel,needsaveasexcel2007,needsaveasword,needsaveastext,needsaveaspdf,needprint");
		}
	}
	
	// 双击进入菜单编辑状态
	function fnDblClk(event, treeId, treeNode) {
	    if (treeNode.menutype == "0") Base.filterSelectInput("menutype", []);
		if (treeNode.menutype == "1") Base.filterSelectInput("menutype", [0,2]);
		if (treeNode.menutype == "2") Base.filterSelectInput("menutype", [1]);
		Base.resetForm("menuForm");
		Base.submit(null, "queryReportMgAction!getMenuInfoById.do", {"dto['menuid']":treeNode.menuid}, null, null, function(){
			Base.hideMask("menuForm");
			Base.setValue("limited","");
			Base.setValue("raqtype","1");
			Base.hideObj("save");
			Base.showObj("update");
			Base.setEnable(["update","addBbBtn","isGroup"]); 
			Base.setValue("praqfilename","");
			Base.showObj("raqfilename");
			Base.hideObj("praqfilename");
			Base.setValue("pmenuname", treeNode.getParentNode() ? treeNode.getParentNode().menuname : "");
			if (Base.getValue("iconSkin") != ""){
				$("#imgbt").removeClass().addClass("button " + treeNode.iconSkin);
			}
		},function(){
			Base.alert("不能对非报表类菜单编辑！","error",function(){Base.showMask("menuForm",false);});
			Base.clearGridData("rqbbGrid");
		}, false);
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
		Base.setValue("menutype", "0");
		Base.setValue("effective", "0");
		Base.setValue("securitypolicy", "1");
		Base.setValue("isdismultipos", "1");
		Base.setValue("syspath","<%=curSyspathId%>");
		Base.setEnable(["save","addBbBtn"]);
		Base.setValue("raqtype","1");
		Base.setValue("limited","");
		Base.clearGridData("rqbbGrid");
		Base.hideMask("menuForm");
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
		});
		$("#rm_modify").unbind("click").bind("click", function(){
			fnDblClk(event, treeId, treeNode)
		});
		$("#rm_del").unbind("click").bind("click", function(){
			if (!treeNode.pmenuid || treeNode.pmenuid == "") {
				Base.alert("不能删除根菜单！", "warn");
			} else if (treeNode.isParent) {
				if(confirm("删除该菜单会把它的子菜单一并删除，确实要删除吗？")){
				    fnRemoveMenu(event, treeId, treeNode)
				};
			} else {
				if(confirm("确实要删除该菜单吗？")){
				    fnRemoveMenu(event, treeId, treeNode)
				};
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
		//fnBtnCallBack();
		Base.refleshTree('menuTree', Base.getValue('pmenuid'));
		Base.alert("新增报表菜单成功！","success");
	}
	// 编辑菜单成功后的回调函数
	function fnUpdateSuccessCb(){
		//fnBtnCallBack();
		Base.refleshTree('menuTree',Base.getValue('pmenuid'));
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
		Base.submit(null, "queryReportMgAction!delete.do", {menuId:treeNode.menuid}, false, false, function(){
		      Base.msgTopTip("<div class='msgTopTip'>删除成功</div>");
		      Base.showMask("menuForm");
		      Base.setDisabled("save,update");
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
		Base.setDisabled("update,save");
		var pNode = treeNodes[0].getParentNode();
		var sortid = [];
		for (var i = 0; i < pNode.children.length; i ++) {
			sortid.push({menuid: pNode.children[i].menuid});
		}
		Base.submit(null, "menuMgAction!webSortMenus.do", {sortMenuids:Ta.util.obj2string(sortid)}, false, false, function(){Base.refleshTree('menuTree', treeNode.pmenuid);});
	}
	//动态构建functionurl
	function changeUrl(rk){
		if(rk.trim()=='')return;
		var allurl = 'system/queryReportAction.do?raq=';
		var url =Base.getValue('url');
		if(url && url.trim()!=''){
			allurl = url+','
		}
		var functionUrl =Base.getValue('functionUrl');
		if(functionUrl&& functionUrl.trim()!=''){
			allurl = functionUrl+','
		}
		Base.setValue('functionUrl',allurl+rk);
	}
	//新增保存回调函数
	function fnBtnCallBack(){
		var menuid = Base.getValue("menuid");
		Base.submit(null, "queryReportMgAction!getMenuBBList.do", {"dto['menuid']":menuid});
	}
	//删除单行报表
	function fnDelRow(o){
		Base.confirm("确认删除吗?",function(btn, callback,options) {
			if (btn) {	
				Base.deleteGridRow('rqbbGrid',o.row);
				Base.refreshGrid('rqbbGrid');
			}
		});
	}
	//是否分页点击事件
	function fnIsGroup(){
		var isgroup = Base.getValue('isGroup');
		if(isgroup){
			Base.setEnable('limited');
		}else{
			Base.setReadOnly('limited');
			Base.setDisabled('limited');
			Base.setValue('limited','');
		}
	}
	
	//新增报表
	function fnAddBb(){
		var rk = Base.getValue('raqfilename')||Base.getValue('praqfilename');
		var type = Base.getValue('raqtype');
		var name = Base.getValue('raqname');
		var isgroup = Base.getValue('isGroup');
		var limited = Base.getValue('limited');
		var scaleexp = Base.getValue('scaleexp');
		if(rk.trim()==''){
			return Base.alert("请输入报表id！","question"),false;
		}
		if(type.trim()==''){
			return Base.alert("请选择报表类型！","question"),false;
		}
		var rqbbGridData = Base.getGridData('rqbbGrid');
		if(type == '0'||type == '3'){
			var isMore = false;
			for(var i = 0 ; i< rqbbGridData.length; i++){
				var row = rqbbGridData[i];
				if(row.raqtype == type){
					return Base.alert("自由报表、参数报表，菜单只能有一个！","question"),false;
				}
			}
		}
		for(var i = 0 ; i< rqbbGridData.length; i++){
			var row = rqbbGridData[i];
			if(row.raqfilename == rk){
				return Base.alert("相同的报表，菜单只能有一个！","question"),false;
			}
		}
		//添加数据到grid中
		var addData = {};
		addData["raqfilename"] = rk;
		addData["raqtype"] = type;
		addData["raqname"] = name;
		if(isgroup&&limited==""){
			return Base.alert("你没有设置每页显示数！","question"),false;
		}
		if(type == '1'){
			addData["isgroup"] = (isgroup?"1":"0");
			addData["limited"] = limited;
			addData["scaleexp"] = scaleexp;
			var needsaveasexcel = Base.getValue("needsaveasexcel");
			addData["needsaveasexcel"] = (needsaveasexcel?"1":"0");
			var needsaveasexcel2007 = Base.getValue("needsaveasexcel2007");
			addData["needsaveasexcel2007"] = (needsaveasexcel2007?"1":"0");
			var needsaveaspdf = Base.getValue("needsaveaspdf");
			addData["needsaveaspdf"] = (needsaveaspdf?"1":"0");
			var needsaveasword = Base.getValue("needsaveasword");
			addData["needsaveasword"] = (needsaveasword?"1":"0");
			var needsaveastext = Base.getValue("needsaveastext");
			addData["needsaveastext"] = (needsaveastext?"1":"0");
			var needprint = Base.getValue("needprint");
			addData["needprint"] = (needprint?"1":"0");
		}
		Base.addGridRow('rqbbGrid',addData);
	}
	//隐藏报表树
	function fnHideTree() {
		$("#DropdownTreeBackground_1").fadeOut("fast");
		$("#DropdownTreeBackground_0").fadeOut("fast");
	}
	//展示参数报表树
	function fnShowTree_0() {
		var obj = $("#praqfilename");
		var offset = obj.offset();
		$("#DropdownTreeBackground_0").css({left:offset.left-245 + "px", top:offset.top+23+ "px"}).slideDown("fast");
	}
	//选择目标参数报表事件
	function  fnSlctTargetProcType_0(event, treeId, treeNode){
		Base.setValue("praqfilename", treeNode.id);
		Base.setValue("raqname",treeNode.name.substring(treeNode.name.lastIndexOf('(')+1,treeNode.name.lastIndexOf(')')));
		fnHideTree();
	}
	//展示数据报表树
	function fnShowTree_1() {
		var obj = $("#raqfilename");
		var offset = obj.offset();
		$("#DropdownTreeBackground_1").css({left:offset.left-245 + "px", top:offset.top+23+ "px"}).slideDown("fast");
	}
	//选择目标数据报表事件
	function  fnSlctTargetProcType_1(event, treeId, treeNode){
		var type = treeNode.type;
		if(type != "1"){
			Base.alert("只能选择主报表！","warn");
		}else{
			Base.setValue("raqfilename", treeNode.id);
			Base.setValue("raqname",treeNode.name.substring(treeNode.name.lastIndexOf('(')+1,treeNode.name.lastIndexOf(')')));
			fnHideTree();
		}
	}
	//新增当前菜单
	function saveMenu(){
		var rqbbGridData = Base.getGridData('rqbbGrid');
		if(rqbbGridData == null ||rqbbGridData.length == 0){
			return Base.alert("报表至少配置一个！","question"),false;
		}
		var param = {};
		param.bbList = Ta.util.obj2string(rqbbGridData);
		Base.submit("menuPanel","queryReportMgAction!save.do",param,null,null,function(){
			fnSaveSuccessCb();
		});
	}
	//更新当前菜单
	function updateMenu(){
		var rqbbGridData = Base.getGridData('rqbbGrid');
		if(rqbbGridData == null ||rqbbGridData.length == 0){
			return Base.alert("报表至少配置一个！","question"),false;
		}
		var bbList = Base.getGridData('rqbbGrid');
		var param = {};
		param.bbList = Ta.util.obj2string(bbList);
		Base.submit("menuPanel","queryReportMgAction!update.do",param,null,null,function(){
			fnUpdateSuccessCb();
		});
	}
	
</script>
<%@ include file="/ta/incfooter.jsp"%>