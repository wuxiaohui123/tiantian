/**
 * @author HareLi.3077
 */
(function($){
    $.extend(true, window, {
        YH: {
            Portal: Portal
        }
    });
    function Portal(id, options, args){
        var defaults = {
            column: "1:1:1",
            target: "body"
        };
        var me, options, result = {}, g_data = {}, g_save_data = {};
        // 初始化
        function init(){
            options = $.extend({}, defaults, options);
            me = $("#" + id);
            _addMainBox();
            $.getJSON(G_BASE_PATH + "sysapp/portalAction!getPortalInfo.do?pageFlag=" + pageFlag, function(data){
                if (data) {
                    var columnsWidth = options.column
                    if (data.location) {
                        columnsWidth = data.location.columnsWidth;
                    }
                    _setColumns(columnsWidth);
                    window["defaultItems"] = data.defaultItems;
                    _addGAllItems(data.defaultItems);
                    if (null == data.location || data.location.length == 0) { // 如果没有获取到用户的配置信息，显示默认的模块
                        _addDefaultItems(data.defaultItems);
                    }
                    else {
                        _addItems(data.location.columnsData);
                    }
                    _sortItems();
                    _resizeItemHeight();
                    _resizeColumnWidth();
                }
            });
        }
        function _addGAllItems(items){
            window["allItems"] = {};
            for (var j = 0; j < items.length; j++) {
                window["allItems"][items[j].moduleid] = items[j];
            }
        }
        //添加选择框列表
        function _addSelectList(items){
            var selectBox = $("#selectBox");
            selectBox.html("");
            var html = "", item;
            for (var i = 0; i < items.length; i++) {
                var slectClass = "";
                item = items[i];
                if ($("#" + item.moduleid)[0]) 
                    slectClass = "select-item-selected";
                html += '<a class="select-item ' + slectClass + '" id="btn_' + item.moduleid + '" onclick="addItem(\'' + item.moduleid + '\',' + window["G_COLUMN_INDEX"] + ')">' + item.modulename + '</a>';
            }
            selectBox.append(html);
        }
        //在无配置的情况下，添加默认项
        function _addDefaultItems(items){
            var len = items.length, tmp = 0, columns = $(".portal-column"), columnsLen = columns.length;
            var rows = Math.floor(len / columnsLen) + 1;
            var item = {}, defaultItems = [];
            for (var j = 0; j < len; j++) {
                if (items[j].moduledefault == 1) {
                    defaultItems.push(items[j]);
                }
            }
            for (var i = 0; i < defaultItems.length; i++) {
                tmp = i % columnsLen;
                item["id"] = defaultItems[i].moduleid;
                item["title"] = defaultItems[i].modulename;
                item["url"] = defaultItems[i].moduleurl;
                var html = _getItemTemplate(item);
                html = $(html);
                html.find(".portal-item-body").height(defaultItems[i].moduleheight - 22);
                html.height(defaultItems[i].moduleheight);
                columns.eq(tmp).append(html);
            }
            _addResize();
            _genItemData();
        }
        //添加最外层容器
        function _addMainBox(){
            $('<div id="portalMain"/>').appendTo(options.target);
        }
        //添加单个模块
        window.addItem = _addItem;
        function _addItem(id, columnIndex){
            var p = $("#" + id);
            if (p[0]) {
                p.remove();
                $("#btn_" + id).removeClass("select-item-selected");
            }
            else {
                var index = $("#btn_" + id).index();
                var item = window["allItems"][id];
                var o = {
                    id: item.moduleid,
                    title: item.modulename,
                    url: item.moduleurl
                };
                var columns = $(".portal-column"), columnsLen = columns.length;
                var tmp = columnIndex == null ? index % columnsLen : columnIndex;
                var html = _getItemTemplate(o);
                html = $(html);
                html.height(item.moduleheight);
                html.find(".portal-item-body").height(item.moduleheight - 22);
                columns.eq(tmp).append(html);
                $("#btn_" + id).addClass("select-item-selected");
                _addResize();
            }
            //$("#layer").height(document.body.scrollHeight);
            _genItemData();
            _addConfigList();
            _resizeItemHeight();
        }
        // 添加多个模块
        function _addItems(items){
            var item, html = "", portalMain = $("#portalMain"), columnDOM;
            for (var i = 0; i < items.length; i++) {
                item = allItems[items[i].id];
                if (!item) 
                    continue;
                var o = {
                    id: item.moduleid,
                    title: item.modulename,
                    url: item.moduleurl
                };
                html = _getItemTemplate(o); // 获取HTML模版
                html = $(html);
                html.find(".portal-item-body").height(items[i].height - 22);
                html.height(items[i].height).attr("_height", items[i].height).attr("_index", items[i].index);
                columnDOM = portalMain.find(".portal-column").eq(items[i].columnindex).append(html);// 获取所在列
            }
            _addResize();
        }
        // 添加列
        function _setColumns(cfg){
            var arr = (cfg || "1:1:1").split(":"), sum = 0;
            html = '<div class="portal-column"></div>', tmpArr = [], oneW = 0;
            for (var i = 0; i < arr.length; i++) { // 获取总数
                sum += parseFloat(arr[i]);
            }
            for (var j = 0; j < arr.length; j++) { // 计算每列宽度 ,百分比,生成列
                oneW = 100 / sum;
                tmpArr.push(oneW * parseInt(arr[j]));
                $(html).attr("id", "column" + j).width((oneW * arr[j]) + "%").hover(function(e){
                    /*    if (!e.stopPropagation) 
                     window.event.cancelBubble = true;
                     else
                     e.stopPropagation();
                     console.log($(e.srcElement || e.target).html());
                     */
                    if (!$(this).find("#addIcon")[0]) {
                        $('<div class="add-icon" id="addIcon"/>').click(function(){
                            window["G_COLUMN_INDEX"] = $(this).parent(".portal-column").index();
                            fnShowSettingWin();
                        }).appendTo($(this)).fadeIn();
                    }
                }, function(){
                    $(this).find(".add-icon").remove();
                }).appendTo("#portalMain");
            }
            
        }
        // 构造HTML模版
        function _getItemTemplate(item){
            var url = item.url;
            if (url.indexOf("http://") == -1 && url.indexOf("https://") == -1) {
                url = G_BASE_PATH + item.url;
            }
            var html = '';
            html += '<div class="portal-item" id="' + item.id + '">';
            html += '	<div class="portal-item-top">';
            html += '		<a href="javascript:void(0)"  class="portal-item-title">' + item.title + '</a>';
            html += '		<div class="portal-item-tools">';
            html += '			<a href="javascript:void(0)" class="portal-item-setting"/>';
            html += '			<a href="javascript:void(0)" class="portal-item-fresh" onclick="reloadIframe(\'' + item.id + '\')"/>';
            html += '			<a href="javascript:void(0)" class="portal-item-close" onclick="_removeItem(\'' + item.id + '\')"/>';
            html += '		</div>';
            html += '	</div>';
            html += '	<div class="portal-item-body">';
            html += '		<iframe frameborder=no scrolling="no" id="iframe_' + item.id + '" src="' + url + '" />';
            html += '	</div>';
            html += '</div>';
            return html;
        }
        window._setFrameHeight = function(id){
            var ifm = document.getElementById(id);
            var subWeb = document.frames ? document.frames[id].document : ifm.contentDocument;
            if (ifm != null && subWeb != null) {
                ifm.height = subWeb.body.scrollHeight;
            }
        }
        //刷新iframe内容
        window.reloadIframe = _reloadIframe;
        function _reloadIframe(id){
            var iframe = $("#" + id + " iframe");
            iframe.attr("src", iframe.attr("src"));
        }
        // 关闭模块
        window._removeItem = function(id){
            $("#" + id).fadeOut(300, function(){
                $(this).remove();
                delete g_data[id];
                _genItemData();
                _resizeItemHeight();
            });
        }
        // 最小化模块
        window._closeItem = function(id, o){
            var item = $("#" + id);
            var bd = item.find(".portal-item-body");
            if (bd.css("display") == "block") {
                bd.hide();
                item.height(22);
            }
            else {
                bd.show();
                item.height(item.attr("_height"));
            }
            $(o).toggleClass("portal-item-title-close");
            _resizeItemHeight();
        }
        // 添加模块内容
        function _getPortalContent(items){
            var item;
            if (!items || items.length == 0) 
                return;
            for (var i = 0; i < items.length; i++) {
                item = items[i];
                try {
                    var html = '<iframe id="iframe_' + i + '" src="' + G_BASE_PATH + item.url + '" />';
                    // onload="_setFrameHeight(\'iframe_' + i + '\');"/>';
                    $("#" + item.id + " .portal-item-body").append(html);
                } 
                catch (e) {
                }
            }
            
        }
        // 拖动时增加蒙层
        function _addLayer(){
            var layer = $("#layer");
            if (layer[0]) 
                return layer.show();
            else {
            	if ( $.browser.msie && parseFloat(navigator.appVersion.split("MSIE")[1]) == 8){
            		layer = $('<div id="layer" class="portal-layer"/>').height(docHeight);
            	}else{
            		layer = $('<div id="layer" class="portal-layer"/>').height(document.body.scrollHeight || (document.documentElement.scrollHeight));
            	}
                layer.show().appendTo("body");
            }
        }
        function _hideLayer(){
            $("#layer").fadeOut();
        }
        // 刷新模块数据
        function _updateItemData(id, data){
            var tmp = g_data[id]
            for (var k in data) {
                tmp[k] = data[k];
            }
            g_data[id] = tmp;
        }
        //添加自定义配置页面的item
        function _addConfigList(){
            var columns = $(".portal-column"), o, _column, _item, html;
            var configBox = $("#configBox");
            configBox.html("");
            columns.each(function(i){
                _column = $(this);
                o = {};
                html = "";
                html = $('<div class="config-box-column"/>').css({
                    width: ((100 / columns.length)) + "%"
                });
                _column.find(".portal-item").each(function(){
                    _item = $(this);
                    o["id"] = _item.attr("id");
                    o["title"] = _item.find(".portal-item-title").text();
                    html.append(_getConfigTemplate(o))
                });
                html.appendTo(configBox);
            });
        }
        //构造配置项HTML模版
        function _getConfigTemplate(item){
            var html = "";
            html += '<div class="config-box-item" id="c_' + item.id + '" _id="' + item.id + '">';
            html += '	<a class="move-up" onclick="moveItem(\'' + item.id + '\',\'up\')"></a>';
            html += '	<a class="move-down" onclick="moveItem(\'' + item.id + '\',\'down\')"></a>';
            html += '	<a class="move-left" onclick="moveItem(\'' + item.id + '\',\'left\')"></a>';
            html += '	<a class="move-right" onclick="moveItem(\'' + item.id + '\',\'right\')"></a>';
            html += item.title;
            html += "</div>";
            return html;
        }
        window.moveItem = _moveItem;
        //上下左右移动
        function _moveItem(id, pos){
            var thum = $("#c_" + id);
            var portal = $("#" + id);
            switch (pos) {
                case "up":
                    if (thum.parent(".config-box-column").find(".config-box-item").length > 1) {
                        if (thum.index() == 0) 
                            return;
                        thum.insertBefore(thum.prev());
                        portal.insertBefore(portal.prev());
                    }
                    else {
                        return;
                    }
                    break;
                case "down":
                    var len = thum.parent(".config-box-column").find(".config-box-item").length;
                    if (len > 1) {
                        if (thum.index() == len - 1) 
                            return;
                        thum.insertAfter(thum.next());
                        portal.insertAfter(portal.next());
                    }
                    else {
                        return;
                    }
                    break;
                case "left":
                    var column = thum.parent(".config-box-column");
                    var prevColumn = column.prev();
                    if (prevColumn[0]) {
                        thum.appendTo(prevColumn);
                        portal.appendTo(portal.parent(".portal-column").prev());
                    }
                    break;
                case "right":
                    var column = thum.parent(".config-box-column");
                    var nextColumn = column.next();
                    if (nextColumn[0]) {
                        thum.appendTo(nextColumn);
                        portal.appendTo(portal.parent(".portal-column").next());
                    }
                    break;
                    
                default:
                    break;
            }
            _genItemData();
            _resizeItemHeight();
        }
        // 模块排序
        function _sortItems(){
            $(".portal-column").each(function(i){
                $(this).find(">div").tsort('', {
                    attr: '_index'
                });
            });
            _addResize();
        }
        //设置可拖动大小
        function _addResize(){
            $(".portal-item").resizable({
                handles: "s",
                minHeight: "100",
                start: function(){
                    $(this).addClass("resize-start");
                },
                stop: function(){
                    $(this).attr("_height", $(this).height());
                    //设置内容高度
                    $(this).find(".portal-item-body").height($(this).height() - 22);
                    $(this).removeClass("resize-start");
                    _genItemData();
                }
            });
        }
        function _resizeColumnWidth(){
            var _tW, _nW, _oW;
            $(".portal-column").each(function(i){
                if ($(this).index() + 1 != $(".portal-column").size()) { //排除最后一个
                    $(this).resizable({
                        handles: "e",
                        minWidth: "150",
                        maxWidth: "",
                        start: function(){
                            $(this).addClass("column-resize-start");
                            $(this).next(".portal-column").addClass("column-resize-start-next");
                            _tW = $(this).outerWidth(true);//当前列宽度
                            _nW = $(this).next(".portal-column").outerWidth(true);//右侧宽度
                            _oW = $("#portalMain").width() - _tW - _nW; //剩余一个的宽度
                            $(this).resizable("option", "maxWidth", _tW + _nW - 150);
                        },
                        stop: function(){
                            $(this).removeClass("column-resize-start");
                            $(this).next(".portal-column").removeClass("column-resize-start-next");
                            _genItemData();
                        },
                        resize: function(){
                            //拖动时，仅拖动列右侧的列宽度跟随变化(百分比)
                            _tW = $(this).outerWidth(true);
                            _nW = $(this).next(".portal-column").outerWidth(true);
                            $(this).css({
                                width: (_tW / $("#portalMain").width()) * 100 + "%"
                            });
                            $(this).next(".portal-column").css({
                                width: (($("#portalMain").width() - _oW - $(this).outerWidth(true)) / $("#portalMain").width()) * 100 + "%"
                            });
                        }
                    });
                }
            });
        }
        // 生成自定义配置数据
        window.genItemData = _genItemData;
        function _genItemData(id, data){
            var _t;
            g_data = {};
            $(".portal-item").each(function(){
                _t = $(this);
                g_data[_t.attr("id")] = {
                    id: _t.attr("id"),
                    index: _t.index(),
                    height: _t.height(),
                    columnindex: _t.parent(".portal-column").index()
                }
            });
            //分析列宽，计算比例
            var tmpArr = [];
            $(".portal-column").each(function(){
                tmpArr.push($(this).width() / $("#portalMain").width());
            });
            g_save_data["columnsWidth"] = tmpArr.join(":");
            window.setTimeout(_saveItemPosInfo, 199);
        }
        // 保存模块信息
        function _saveItemPosInfo(){
            var saveData = [];
            for (var k in g_data) {
                saveData.push(g_data[k]);
            }
            g_save_data["columnsData"] = saveData;
            $.post(G_BASE_PATH + "sysapp/portalAction!savePortalInfo.do", {
                "dto['location']": $.toJSON(g_save_data),
                "dto['pageFlag']": pageFlag
            }, function(){
            
            });
        }
        //设置统一列高和边框
        function _resizeItemHeight(flag){
            var h = [];
            $(".portal-column").each(function(){
                var tmp = 0;
                $(this).find(".portal-item").each(function(){
                    tmp += $(this).outerHeight(true);
                });
                h[$(this).attr("id")] = tmp;
                h.push(tmp)
            });//height(document.body.scrollHeight);
            //通过排序找出最高的一列最为三列的高度
            h = $(h).sorted({
                by: function(a){
                    return a["0"];
                },
                reversed: true
            });
            $(".portal-column").height(h[0]);
            //    if (flag) {
            //        $(".portal-column").each(function(i){
            //             if (i != 0 && $(this).find(".portal-item").length > 0) {
            //                $(this).addClass("portal-column-border");
            //            }
            //       })
        
            //  }
        
        }
        
        $.extend(this, {
            "showLayer": _addLayer,
            "hideLayer": _hideLayer,
            "addSelectList": _addSelectList,
            "addItem": _addItem,
            "addConfigList": _addConfigList
        });
        init();
    }
}(jQuery));
