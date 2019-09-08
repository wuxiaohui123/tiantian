/**
 * tree组件常用方法,调用方式为Base.xxx();
 * @module Base
 * @class tree
 * @static
 */
(function(factory){
	if (typeof define === 'function' && define.amd) {
		define(["jquery","TaJsUtil"], factory);
	} else {
		factory(jQuery);
	}
}(function($){
	$.extend(true, window, {
        Base:core()
    });
	
	function core(){
		return {
			refleshTree : refleshTree,
			expandTree : expandTree,
			collapseTree : collapseTree,
			focusTree : focusTree,
			recreateTree : recreateTree,
			clearTreeData : clearTreeData
		};
		/**
		 * 对树指定节点id的节点进行刷新，主要针对异步树。
		 * @method refleshTree
		 * @param {String} treeId  树id
		 * @param {String} nodeId  节点id
		 */
		function refleshTree(treeId,nodeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			var parentNode = treeObj.getNodeByParam(treeObj.setting.data.simpleData.idKey, nodeId);
			parentNode.isParent = true;
			treeObj.reAsyncChildNodes(parentNode, "refresh");
		}

		/**
		 * 展开整棵树。
		 * @method expandTree
		 * @param {String} treeId  树id
		 */
		function expandTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.expandAll(true);
		}

		/**
		 * 折叠整棵树。
		 * @method collapseTree
		 * @param {String} treeId  树id
		 */
		function collapseTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.expandAll(false);
		}

		/**
		 * 将焦点置于树的根节点上。
		 * @method focusTree
		 * @param {String} treeId  树id
		 */
		function focusTree(treeId){
			var treeObj = Ta.core.TaUIManager.getCmp(treeId);
			treeObj.selectNode(treeObj.getNodeByTId(treeId + "_1"));
		}

		/**
		 * 重构现有的树
		 * @method recreateTree
		 * @param {String} treeId  树id
		 * @param {Array} setting  setting 数组,如果使用树原有的setting,设为null
		 * @param {Array} treeData  节点数据,如果是异步获取的数据，设为null
		 */
		function recreateTree(treeId,setting,treeData){
			   var  tree= Ta.core.TaUIManager.getCmp(treeId);
			   if(!tree){
			      alert("id为"+treeId+"的树不存在!");
			      return;
			   }
			   if(setting==null){
			    setting = {
				view:{
					showIcon:tree.setting.view.showIcon,
					showLine:tree.setting.view.showLine,
					showTitle:tree.setting.view.showTitle,
					expandSpeed:tree.setting.view.expandSpeed,
					fontCss:tree.setting.view.fontCss,
					selectedMulti:tree.setting.view.selectedMulti,
					autoCancelSelected:tree.setting.view.autoCancelSelected
				},
				async:{
					url:tree.setting.async.url,
					autoParam:tree.setting.async.autoParam,
					otherParam:tree.setting.async.otherParam,
					dataFilter:tree.setting.async.dataFilter,
				  	enable:tree.setting.async.enable
				},
				check:{
					chkStyle:tree.setting.check.chkStyle,
					radioType:tree.setting.check.radioType,
					chkboxType:tree.setting.check.chkboxType,
					enable:tree.setting.check.enable
				},	
				data:{
					keep:{
						parent:tree.setting.data.keep.parent,
						leaf:tree.setting.data.keep.leaf
					},
					key:{
						checked:tree.setting.data.key.checked,
						name:tree.setting.data.key.name,
						title:tree.setting.data.key.title,
						childs:"childs"
					},
					simpleData:{
						enable:tree.setting.data.simpleData.enable,
						idKey:tree.setting.data.simpleData.idKey,
						pIdKey:tree.setting.data.simpleData.pIdKey
					}
				},
				
				edit:{
					drag:{
						isMove:tree.setting.edit.drag.isMove,
						isCopy:tree.setting.edit.drag.isCopy,
						inner:tree.setting.edit.drag.inner,
						prev:tree.setting.edit.drag.prev,
						next:tree.setting.edit.drag.next
					},
					showRenameBtn:tree.setting.edit.showRenameBtn,
					showRemoveBtn:tree.setting.edit.showRemoveBtn,
					showAddBtn:tree.setting.edit.showAddBtn,
					showAcceptBtn:tree.setting.edit.showAcceptBtn,
					renameTitle:tree.setting.edit.renameTitle,
					removeTitle:tree.setting.edit.removeTitle,
					addTitle:tree.setting.edit.addTitle,
					acceptTitle:tree.setting.edit.acceptTitle,
					enable:tree.setting.edit.enable
				},
				  
				callback:{
					beforeCheck:tree.setting.callback.beforeCheck,
					onCheck:tree.setting.callback.onCheck,
					beforeRename:tree.setting.callback.beforeRename,
					beforeRemove:tree.setting.callback.beforeRemove,
					beforeDrag:tree.setting.callback.beforeDrag,
					beforeDrop:tree.setting.callback.beforeDrop,
					onRename:tree.setting.callback.onRename,
					onRemove:tree.setting.callback.onRemove,
					onDrap:tree.setting.callback.onDrap,
					onDrop:tree.setting.callback.onDrop,
					beforeClick:tree.setting.callback.beforeClick,
					onClick:tree.setting.callback.onClick,
					beforeDblclick:tree.setting.callback.beforeDblclick,
					onDblClick:tree.setting.callback.onDblClick,
					beforeRightClick:tree.setting.callback.beforeRightClick,
					onRightClick:tree.setting.callback.onRightClick,
					beforeExpand:tree.setting.callback.beforeExpand,
					onExpand:tree.setting.callback.onExpand,
					beforeCollapse:tree.setting.callback.beforeCollapse,
					onCollapse:tree.setting.callback.onCollapse,
					beforeMouseDown:tree.setting.callback.beforeMouseDown,
					onMouseDown:tree.setting.callback.onMouseDown,
					beforeMouseUp:tree.setting.callback.beforeMouseUp,
					onMouseUp:tree.setting.callback.onMouseUp,
					onNodeCreated:tree.setting.callback.onNodeCreated,
					beforeAsync:tree.setting.callback.beforeAsync,
					onAsyncSuccess:tree.setting.callback.onAsyncSuccess,
					onAsyncError:tree.setting.callback.onAsyncError,
					beforeAdd:tree.setting.callback.beforeAdd,
					onAdd:tree.setting.callback.onAdd,
					beforeAccept:tree.setting.callback.beforeAccept,
					onAccept:tree.setting.callback.onAccept,
					"end":null
				    }
			     };
			  }
			   Ta.core.TaUIManager.unregister(treeId);
				var taTree = $.fn.zTree.init($("#"+treeId), setting, treeData);
				Ta.core.TaUIManager.register(treeId, taTree);
			}
		/**
		 * 清空指定树的数据
		 * @method clearTreeData
		 * @param {String} treeId  树id
		 */
		function clearTreeData(treeId){
			 var  tree= Ta.core.TaUIManager.getCmp(treeId);
			   if(!tree){
			      alert("id为"+treeId+"的树不存在!");
			      return;
			   }
			$.fn.zTree.init($("#"+treeId), tree.setting, null);
		}

	}
}));
