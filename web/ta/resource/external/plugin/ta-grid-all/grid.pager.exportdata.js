/**
 * 数据导出插件
 * Created by wuxiaohui on 2016/5/8.
 */
(function($){
    $.extend(true,window,{
        YH:{
            ExportData:ExportData
        }
    });
    function ExportData(id,options,grid,arsg) {
        var defaults = {
            title:"数据导出向导",
            width:480,
            height:400,
            isFrame:false,
            steps:["导出模式","导出格式","导出参数","导出字段","附加选项","导出开始"]
        };
        var me,mf, options,eTypeId=1,callType=1,isHasHead=0;
        var fileTable={};
        // 创建本插件常用的表格对象
        function DataGrid(gridId) {
            var dataGrid = new Object();
            dataGrid.id=gridId;
            dataGrid.allData = [];
            dataGrid.headArray = [];
            dataGrid.height=0;
            dataGrid._tableHeight=0;
            dataGrid._tableWidth=0;
            dataGrid.viewReport = {};
            dataGrid._tableview_ = {};
            dataGrid._headview_ = {};
            dataGrid.headView ={};
            dataGrid.tableView = {};
            dataGrid.table ={};
            dataGrid.isShow=false;
            dataGrid.initView = function (headArr,height,isShow) {// 初始化表格
				// ，如[{id:"1",key:"列1",width:"100"},{id:"1",key:"列2"}]
                this.headArray = headArr;
                this.height = height;
                this._tableHeight = parseInt(height)-26-2;
                this.isShow=isShow;
                this._tableview_ = $("<div id='"+gridId+"_div' class='sheetable'/>").css("display",isShow?"block":"none");
                this._headview_ = $("<div id='"+gridId+"_head' class='thead'/>");
                for (var i in headArr){
                    this._tableWidth += parseInt(headArr[i].width)+2;
                    var _td = $("<div id='"+headArr[i].id+"'/>").addClass(headArr[i].Class).width(headArr[i].width).html(headArr[i].key);
                    this._headview_.append(_td);
                }
                this.tableView = $("<div style='overflow-y: scroll;overflow-x:hidden;width: 100%;'/>").height(this._tableHeight);
                this.table = $("<table id='"+gridId+"_table' width='"+this._tableWidth+"px' cellspacing='1px' />");
                this.viewReport = $(this._tableview_).append(this._headview_).append(this.tableView.append(this.table));
            };
            dataGrid.setData = function(rowDataArr){// 设置表格数据
                this.table.empty();
                var _headTd = this._headview_.children("div");
                for (var i in rowDataArr) {
                    var _tr = $("<tr/>");
                    _headTd.each(function(){
                        var _tempW = $(this).width();
                        var _data_ = $(rowDataArr[i][this.id]).width(_tempW+0.5)[0];
                        _tr.append($("<td/>").width(_tempW).html(_data_));
                    });
                    this.table.append(_tr);
                }
            };
            dataGrid.init = function () {// 初始化数据
                var _trs = this.table.find("tr");
                var _headTd = this._headview_.children("div");
                for(var i=0;i<_trs.length;i++){
                    var _tds = $(_trs[i]).find("td");
                    var obj = {"rowNum":i+1};
                    for (var j = 0; j < _tds.length; j++) {
                        var temp0 = $(_tds[j]).find("input");
                        var temp1 = $(_tds[j]).find("div").html();
                        obj[_headTd.get(j).id] = temp0.length>0?temp0.get(0).checked:temp1;
                    }
                    this.allData.push(obj);
                }
            };
            dataGrid.getAllGridData = function (){// 获取表格所有数据
                this.allData.length=0;
                this.init();
                return this.allData;
            };
            dataGrid.getViewReport = function(){// 获取表格展现view
                return this.viewReport;
            };
            dataGrid.getSelectionGridData = function (){// 获取表格中选择的数据
                this.allData.length=0;
                this.init();
                var selectArr = [];
                for ( var i in this.allData) {
                    if(this.allData[i].checkbox==true){
                        selectArr.push(this.allData[i]);
                    }
                }
                return selectArr;
            };
            return dataGrid;
        }
        // 本插件常用的ui组件及函数
        var models = {
            getMessageTipBox: function getMessageTipBox(msg) {
                var _msg = "<div layout='column' class='grid msgbox'><span>"+msg+"</span></div>";
                return _msg;
            },
            getRadio: function(id,key,name,checked,value,cellback) {
                var _$rDiv = $("<div id='" +id+"_radioDiv' style='white-space:nowrap;' class='fielddiv ta_pw_radio'/>");
                _$rDiv = checked ? _$rDiv.addClass("ta-radio-checked"):_$rDiv.addClass("ta-radio-uncheck");
                _$rDiv.click(function () {
                    $(this).removeClass("ta-radio-uncheck").addClass("ta-radio-checked").find("input").get(0).checked = true;
                    $(this).siblings().removeClass("ta-radio-checked").addClass("ta-radio-uncheck").find("input").removeAttr("checked");
                    if(typeof cellback == "function"){
                        cellback(this);
                    }
                });
                var _$rInput = $("<input id='"+id+"' type='radio' name='"+name+"' style='display:none;'/>").val(value);
                _$rInput.get(0).checked = checked;
                return _$rDiv.append(_$rInput).append("<label>"+key+"</label>");
            },
            getRadioGroup:function (id,radioArr) {
                var _rGroupdiv = $("<div id='"+id+"' />");
                var _$rgdiv = $("<div class='fielddiv2' layout='column'/>");
                for(var i in radioArr){
                    _$rgdiv.append(this.getRadio(id,radioArr[i].key,id,(i==0),radioArr[i].value,function (o) {
                        fnRadioCellBack(o);
                    }));
                }
                return _rGroupdiv.append(_$rgdiv).append("<div style='clear:both'></div>");
            },
            getCheckboxInput:function(id,key,cssStyle,checked,callBack){
                var _ckbDiv = $("<div id='chk_"+id+"' style='"+cssStyle+"'/>");
                var _ckbLable = "<lable class='ta-chk-mc' style='padding-left: 18px;'>"+key+"</lable>";
                var _ckbInput = "<input type='checkbox' name='"+id+"' style='display: none'>";
                var _filediv = $("<div class='fielddiv' style='white-space: nowrap'>");
                _ckbInput.checked = checked;
                _filediv = checked?_filediv.addClass("ta-chk-checked"):_filediv.addClass("ta-chk-uncheck");
                _ckbDiv.click(function(){
                    var filediv = $(this).find("div");
                    if(filediv.hasClass("ta-chk-checked")){
                        filediv.removeClass("ta-chk-checked").addClass("ta-chk-uncheck");
                        filediv.find("input").get(0).checked=false;
                    }else{
                        filediv.removeClass("ta-chk-uncheck").addClass("ta-chk-checked");
                        filediv.find("input").get(0).checked=true;
                    }
                    callBack(this);
                });
                return _ckbDiv.append(_filediv.append(_ckbLable).append(_ckbInput));
            },
            getTxtInput:function (id,key,cssStyle,labelWidth,value) {
                var fdiv = $("<div id='"+id+"' class='fielddiv fielddiv_163' style='margin: 6px 2px;'/>").css(cssStyle);
                var label = $("<label class='fieldLabel'/>").width(labelWidth).html(key);
                var finput = $("<div class='fielddiv2'/>").css("margin-left",labelWidth);
                var input = $("<input type='text' class='textinput validatebox-text validatebox-invalid'/>");
                input.attr("name",id).val(value);
                return fdiv.append(label).append(finput.append(input));
            },
            getSelectInput:function (id,key,data,labelWidth,cssStyle) {
                var $selDiv = $("<div class='fielddiv fielddiv_163' style='margin: 6px 2px;'/>").attr('id',id+'_div');
                $selDiv = cssStyle?$selDiv.css(cssStyle):$selDiv;
                var $selLabel = $("<label class='fieldLabel'/>").width(labelWidth).html(key);
                var $div2 = $("<div class='fielddiv2'/>").css('margin-left',labelWidth);
                var $select = $("<select id='"+id+"' name='"+id+"' class='textinput selinput'/>");
                if(jQuery.isArray(data)){
                    for (var i in data){
                        $select.append("<option value='"+data[i].value+"'>"+data[i].key+"</option>");
                    }
                }
                $div2.append($select);
                return $selDiv.append($selLabel).append($div2);
            },
            getButton: function(id,key,style,isOK,cellback){
                var _BtnTitle = "<span class='button_span "+(isOK?'isok':'')+"'>"+key+"</span>";
                var _button = $("<button id='"+id+"' type='button' class='sexybutton_163'/>");
                if(typeof cellback =="function") {
                    _button.click(cellback);
                }
                return _button.css('margin-right', 6).addClass(style).append(_BtnTitle);
            },
            // 创建窗口按钮
            getButtons :function(btnArr){
                var _btnDiv_ = $("<div class='panel-button panelnoborder' style='height: 45px'/>");
                for(var i in btnArr){
                    var _btn = models.getButton(btnArr[i].id,btnArr[i].key,btnArr[i].class,btnArr[i].isOk,btnArr[i].call);
                    _btnDiv_.append(_btn);
                }
                return _btnDiv_;
            },
            setButtonDisabled:function (id) {
                $("#"+id).attr("disabled","disabled");
            }
        };
        // 插件初始化函数
        function init(){
            options = $.extend({}, defaults, options);
            openExportDataWindow();
            me = $('#'+id);
            createExportDataFrom();
            mf = $('#'+id+'_form');
            var _buttonArr = [{id:"_prev",key:"上一步",class:"prev",isOk:true},
                              {id:"_next",key:"下一步",class:"next",isOk:true},
                              {id:"_cancel",key:"取消",class:"",isOk:false,call:function(){
                    if(callType==2){// 开始
                        console.info("执行开始导出函数.....");
                        // TODO 执行开始函数
                        startExportData();
                    }else{// 取消
                        Base.closeWindow(id);
                    }
                }}];
            me.append(models.getButtons(_buttonArr));
            createExportDataStateTab();
            createExportDataContens();
            createExportDataFormDIV1();
            createExportDataFormDIV2();
            createExportDataFormDIV3();
            createExportDataFormDIV4();
            createExportDataFormDIV5();
            createExportDataFormDIV6();
        }
        // 打开窗口
        function openExportDataWindow(){
            Base.openWindow(id, options.title, null, "", options.width, options.height,
                function (){// 加载完成函数
                    exportDataOnLoad();
                },
                function () {// 关闭函数

                }, false, null, {maximizable:false});
        }
        // 创建form表单
        function createExportDataFrom(){
            me.append("<form id='"+id+"_form' method='post'><div id='wizard'/></form>");
        }
        // 创建状态tab标签
        function createExportDataStateTab() {
            var stateUl = $("<ul id='status'/>");
            var states = options.steps;
            for(var i=0;i<states.length;i++){
                var _li = $("<li/>").append("<strong>"+(i+1)+".</strong>"+states[i]);
                _li = (i==0)?_li.addClass("active"):_li;
                stateUl.append(_li);
            }
            stateUl.appendTo($("#wizard"));
        }
        // 创建单列步骤
        function createExportDataContens() {
            var items = $("<div class='items'/>");
            for(var i=1;i<=options.steps.length;i++){
                var _item = $("<div layout='column' class='grid'/>").attr("id",i);
                _item = (i==1)?_item.css("display","block"):_item.css("display","none");
                items.append(_item);
            }
            items.appendTo($('#wizard'));
        }
        // 创建STEP1
        function createExportDataFormDIV1() {
            var meBox1 = $('#1');
            meBox1.append(models.getMessageTipBox("向导可以让你指定导出数据的细节，请选择一个所需的导出模式？"));
            var $fst1 = $("<fieldset id='fst1' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox1);
            $fst1.append($("<legend>导出模式</legend>"));
            $fst1.append(models.getRadioGroup("eModel", [
                {key:'导出当前页',value:'1'},
                {key:'导出选择数据',value:'2'},
                {key:'导出全部数据',value:'3'}]));
        }
        // 创建STEP2
        function createExportDataFormDIV2() {
            var meBox2 = $('#2');
            meBox2.append(models.getMessageTipBox("你要使用哪一种导出格式？"));
            var $fst2 = $("<fieldset id='fst1' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox2);
            $fst2.append($("<legend>导出格式</legend>"));
            $fst2.append(models.getRadioGroup("eType", [
                {key:'文本文件（*.txt）',value:'1'},
                {key:'Excel文件（*.xls）',value:'2'},
                {key:'Excel文件（2007或以上版本）（*.xlsx）',value:'3'}]));
        }
        // 创建STEP3
        function createExportDataFormDIV3() {
            var meBox3 = $("#3");
            meBox3.append(models.getMessageTipBox("请设置一些导出参数。"));
            var $fst3 = $("<fieldset id='fst3' class='feildbox'/>").attr("form",id+"_form").appendTo(meBox3);
            $fst3.append($("<legend>导出参数</legend>"));
            $fst3.append(models.getTxtInput("fileName","文件名称",{},60,""));
            $fst3.append(models.getCheckboxInput("Set0", "包含表格标题", "width:20%;float:left;display:none;",false,function (obj) {
            	var checkbox = $(obj).find("input").get(0);
                if(checkbox.checked){
                	checkbox.value=1;
                    $("#tableTitle").css("visibility","visible");
                }else{
                	checkbox.value=0;
                    $("#tableTitle").css("visibility","hidden");
                }
            }));
            $fst3.append(models.getTxtInput("tableTitle","",{"margin-left":"15px","visibility":"hidden"},100));
            $("#tableTitle").find("label").width(0);
            $fst3.append(models.getCheckboxInput("Set1", "包含列的标题", "width:100%",false,function (obj) {
            	var checkbox = $(obj).find("input").get(0);
            	if(checkbox.checked){
            		checkbox.value=1;
            		isHasHead = 1;
            	}else{
            		checkbox.value=0;
            		isHasHead = 0;
            	}
            }));
            $fst3.append(models.getCheckboxInput("Set2", "包含导出的信息", "width:100%;display:none;",false,function (obj) {
            	var checkbox = $(obj).find("input").get(0);
            	if(checkbox.checked){
            		checkbox.value = 1;
            	}else{
            		checkbox.value = 0;
            	}
            }));
            // $fst3.append("<div></div>");
        }
        // 创建STEP4
        function createExportDataFormDIV4() {
            var meBox4 = $("#4");
            meBox4.append(models.getMessageTipBox("你可以定义导出字段。"));
            var $fst4 = $("<fieldset id='fst4' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox4);
            $fst4.append($("<legend>导出字段</legend>"));
            var checkboxInput = $("<input type='checkbox' style='width:20px;height:26px' title='全选/取消'/>").click(function(){
            	var chbinput = $("#SelectedFile_table").find("input[type='checkbox']");
            	if(this.checked){
            		chbinput.prop("checked",true);
            		chbinput.parent().parent().css("background-color","#ffffd5");
            	}else{
            		chbinput.removeAttr("checked");
            		chbinput.parent().parent().css("background-color","");
            	}
            });
            var headRow = [{id:"checkbox",key:checkboxInput,width:"30px"},
                           {id:"fileNo",key:"字段编号",width:"160px"},
                           {id:"fileName",key:"字段名称",width:"223px"}];
            fileTable = new DataGrid("SelectedFile");
            fileTable.initView(headRow,"200",true);
            var a = grid.getColumns();
            var data = someDataToFileTableData(a);
            fileTable.setData(data);
            var reStable = $("<div style='border:1px solid #6bb2e8;border-radius:6px;height: 200px;'/>");
            $fst4.append(reStable.append(fileTable.getViewReport()));

        }
        // 创建STEP5
        function createExportDataFormDIV5() {
            var meBox5 = $("#5");
            meBox5.append(models.getMessageTipBox("你可以为源定义一些附加的选项。"));
            var $fst5 = $("<fieldset id='fst5' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox5);
            $fst5.append(models.getSelectInput("recode_commas","记录分隔符",
                [{key:"CRLF",value:"1"},
                    {key:"CR",value:"2"},
                    {key:"LF",value:"3"}],80));
            $fst5.append(models.getSelectInput("field_commas","字段分隔符",
                [{key:"定位（Tab）",value:"\t"},
                    {key:"分号（;）",value:";"},
                    {key:"逗号（,）",value:","},
                    {key:"空格",value:" "},
                    {key:"无",value:""}],80));
            $fst5.append(models.getSelectInput("text_commas","文本限定符",
                [{key:"无",value:"T1"},
                    {key:"\"",value:"T2"},
                    {key:"'",value:"T3"},
                    {key:"~",value:"T4"}],80));
            $fst5.append(models.getSelectInput("date_sort","日期排序",
                [{key:"MDY",value:"D1"},
                    {key:"DMY",value:"D2"},
                    {key:"YMD",value:"D3"},
                    {key:"YDM",value:"D4"},
                    {key:"DYM",value:"D5"},
                    {key:"MYD",value:"D6"}],
                100,{float:"left",width:"49%"}));
            $fst5.append(models.getTxtInput("date_commas","日期分隔符",{float:"left",width:"49%"},100,"/"));
            $fst5.append(models.getTxtInput("time_commas","时间分隔符",{float:"left",width:"49%"},100,":"));
            $fst5.append(models.getSelectInput("datetime_sort","时间日期排序",
                [{key:"日期 时间",value:"DT1"},
                    {key:"时间 日期",value:"DT2"},
                    {key:"日期 时间 时区",value:"DT3"},
                    {key:"时间 日期 时区",value:"DT4"},
                    {key:"时间 时区 日期",value:"DT5"}], 100,{float:"left",width:"49%"}));
            $fst5.append(models.getSelectInput("bindata_code","二进制数据编码",
                [{key:"Base64",value:"BDT1"},
                    {key:"无",value:"BDT0"}], 100,{float:"left",width:"49%"}));
        }
        // 创建STEP6
        function createExportDataFormDIV6() {
            var meBox6 = $("#6");
            meBox6.append(models.getMessageTipBox("向导已收集导出时所需的全部信息。点击【开始】按钮进行导出。"));
            var $fst6 = $("<fieldset id='fst6' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox6);
            $fst6.append("<legend>导出开始</legend>");
            $fst6.append("<div id='information' class='info'/>");  
        }

        function someDataToFileTableData(gridData) {
            var someTableDate = [];
            for( var i in gridData){
                var obj = {};
                if (gridData[i].id != "_checkbox_selector" &&
                		gridData[i].id != "__no" && 
                		!gridData[i].icon&&
                		!gridData[i].operate) {
                	obj.checkbox = $("<input type='checkbox'/>").click(function(){
                		if(this.checked){
                			$(this).parent().parent().css("background-color","#ffffd5");
                		}else{
                			$(this).parent().parent().css("background-color","");
                		}
                	});  
                	obj.fileNo = "<div>"+gridData[i].id+"</div>";
                    obj.fileName = "<div>"+gridData[i].name+"</div>";
                    someTableDate.push(obj);
                }
            }
            return someTableDate;
        }
        function fnRadioCellBack(obj) {
            var type = $(obj).find("input")[0].name;
            var value = $(obj).find("input")[0].value;
            if(type=="eModel"){
            	checkExportDataByEmodel(value);
            }
            if(type=="eType"){
            	if(value==1){
            		Base.hideObj("chk_Set0,chk_Set2");
            	}else{
            		Base.showObj("chk_Set0,chk_Set2");
            	}
            }
        }
        function checkExportDataByEmodel(modelId){
        	if(modelId==1){// 导出当前页
            	if(grid.getDataView().getItems().length <= 0){
    				Base.alert("当前页数据不能为空","error",function(){
    					Base.closeWindow(id);
    				});
    				return false;
    			}
        	}else if(modelId==2){// 导出选择数据
        		if(grid.getSelectRowsDataToObj().length < 1){
        			Base.alert('请至少选择一条数据',"error",function(){
            			Base.closeWindow(id);
  					});
        			return false;
        		}
        	}else{// 导出全部数据
        		if(!arsg.sqlStatementName && !arsg.resultType){
        			Base.alert("导出全部数据必须设置sqlStatementName和resultType属性","error",function(){
        				Base.closeWindow(id);
        			});
        			return false;
        		}
        	}
        	return true;
        }
      //根据导入的id获取导入类型描述
        function getExpTypeDescById(tid){
        	if(tid==1){
        		return "文本文件"
        	}else if(tid==2){
        		return "excel 2003文件";
        	}else{
        		return "excel 2007文件";
        	}
        }
        //根据导入的模式id获取模式描述
        function getExpModelDescById(mid){
        	if(mid==1){
        		return "当前页"
        	}else if(mid==2){
        		return "选择数据";
        	}else{
        		return "全部数据";
        	}
        }
        //默认导出
        function exportDefaultGridData(param,mygrid) {
        	var a, b;
        	if(param == 1){//导出当前页
        		a = mygrid.getSelectionGridData();
				b = grid.getDataView().getItems();
        	}else if(param == 2){//导出选择数据
				a = mygrid.getSelectionGridData();
				b = grid.getSelectRowsDataToObj();
				b = $.extend(true, [], b);
			}
			var collection  = grid.getOptions().collectionsDataArrayObject;
			var row = [];
			var cell = [];
			var head =[];
			for (var i = 0; i < a.length; i ++ ) {
					cell.push("\"" + a[i].fileNo + "\"");
					head.push("\"" + a[i].fileName + "\"");
			}
			//是否包含列的标题
			if(isHasHead==1){
				row.push(head);
			}
			for (var i = 0; i < b.length; i ++) {
				var cells=[];
				for (var j = 0; j < cell.length; j ++) {
					var cData = b[i][cell[j].replaceAll("\"","")];
					if(cData == undefined || cData === ""){
						cData = "";
					}else{
						//处理转义字符
						cData = JSON.stringify(cData.toString());
						cData = cData.substring(1, cData.length-1);
					}
					//var cData = b[i][cell[j].replaceAll("\"","")] == undefined?"":b[i][cell[j].replaceAll("\"","")].replaceAll("\"","\\\"");
					if (options.expKeyOrName == true && collection != undefined) {
							var collectcell = collection[cell[j].replaceAll("\"","")];
							var b_collected = false;
							if (collectcell && collectcell.length > 0) {
								for (var c = 0; c < collectcell.length ; c ++) {
									if (collectcell[c].id == cData) {
										//处理转义字符
										var collData = collectcell[c].name;
										collData = JSON.stringify(collData.toString());
										cells.push(collData);
										b_collected = false;
										break;
									} else {
										b_collected = true;
									}
								}
								if (b_collected) {
									cells.push("\"" + cData + "\"");
								}
							} else if (!b_collected){ 
								cells.push("\"" + cData + "\"");
							}
					} else {
						cells.push("\"" + cData + "\"");
					}
				}
				row.push(cells);
			}
			var $input = $("<textarea/>").css("display", "none").val(Ta.util.obj2string(row)).attr("name", "_grid_item_export_excel");
			mf.append($input).attr("action", Base.globvar.contextPath + "/exprotDefaultGridData.do").submit();
			var informational = $("#information");
			informational.append("<p>[信息] [导出] 导出开始</p>");
    		informational.append("<p>[信息] [导出] 导出类型 - "+getExpTypeDescById($("#eType:checked").val())+"</p>");
    		informational.append("<p>[信息] [导出] 导出模型 - "+getExpModelDescById($("#eModel:checked").val())+"</p>");
    		informational.append("<p>[信息] [导出] 导出字段数 - "+a.length+"</p>");
    		informational.append("<p>[信息] [导出] 导出数据量 - "+b.length+"</p>");
    		informational.append("<p>[信息] [导出] 完成 - 成功！</p>");
    		informational.append("<p>---------------------</p>");
    		Base.alert("导出成功！","success",function(){
    			models.setButtonDisabled("_prev");
    			callType = 1;
                $("#_cancel").find("span").html("关闭");
    		});
		}
        // 导出所有数据
		function exportDefaultGridDataAll(mygrid){
			var a = mygrid.getSelectionGridData();
			var collection = [];
			var obj = grid.getCollectionsDataArrayObject();
			if (arsg.expKeyOrName == true) {
				for ( var x in obj) {
					collection.push("\"" + x + "\"");
				}
			}
			var row = [];
			var cell = [];
			var head = [];
			for (var i = 0; i < a.length; i++) {
				cell.push("\"" + a[i].fileNo + "\"");
				head.push("\"" + encodeURI(a[i].fileName) + "\"");
			}
			row.push(head);
			row.push(cell);
			if (arsg.sqlStatementName && arsg.resultType) {
				var sql = [], result = [];
				sql.push("\"" + arsg.sqlStatementName + "\"");
				result.push("\"" + arsg.resultType + "\"");
				row.push(sql);
				row.push(result);
			} else {
				Base.alert("导出全部数据必须设置sqlStatementName和resultType属性", "warn");
				return;
			}
			row.push(collection);
			var $input = $("<textarea id='_gridHead_' name='_gridHead_'/>").css("display", "none").val(Ta.util.obj2string(row));
			$input.appendTo(mf);
			toQuery(grid.getOptions().pagingOptions.submitIds, mf.serialize(), Base.globvar.contextPath + "/exprotAllDataGridData.do");
			var informational = $("#information");
			informational.append("<p>[信息] [导出] 导出开始</p>");
    		informational.append("<p>[信息] [导出] 导出类型 - "+getExpTypeDescById($("#eType:checked").val())+"</p>");
    		informational.append("<p>[信息] [导出] 导出模型 - "+getExpModelDescById($("#eModel:checked").val())+"</p>");
    		informational.append("<p>[信息] [导出] 导出字段数 - "+a.length+"</p>");
    		//informational.append("<p>[信息] [导出] 导出数据量 - "+b.length+"</p>");
    		informational.append("<p>[信息] [导出] 完成 - 成功！</p>");
    		informational.append("<p>---------------------</p>");
    		Base.alert("导出成功！","success",function(){
    			models.setButtonDisabled("_prev");
    			callType = 1;
                $("#_cancel").find("span").html("关闭");
    		});
		}
		//通用查询
		function toQuery(submitIds, paramter, url){
			submitIds = submitIds ? submitIds:"";
			var aids = submitIds.split(',');
		 	//根据ids拼接传递的条件字符串
		 	var queryStr = "";
			var datagridids = [];
			var parm = paramter.split("&");
			for (var n = 0; n < parm.length; n++) {
				var par = parm[n].split("=");
				if (queryStr == "") {
					queryStr += "dto['"+par[0]+"']="+par[1];
				} else {
				    queryStr += "&dto['"+par[0]+"']="+par[1];
				}
			}
			if (aids) {
				for (var i = 0; i < aids.length; i++) {
					if (aids[i] == null || aids[i] == '') {
						continue;
					}
					var obj = Base.getObj(aids[i]);
					var $obj = $(obj);
					if (obj && obj.cmptype != 'datagrid'
							&& ($obj.hasClass('panel') || $obj.hasClass('grid')
									|| obj.cmptype == 'flexbox' || (obj.tagName && (obj.tagName == 'FORM'
									|| obj.tagName == 'FIELDSET'
									|| obj.tagName == 'DIV'
									|| obj.tagName == 'INPUT'
									|| obj.tagName == 'TEXTAREA' || obj.tagName == 'SELECT')))) {
						if (obj.cmptype == 'flexbox') {// 下拉框
							obj = $("#" + aids[i]);
						}
						for (var j = 0; j < aids.length; j++) {// 对ids进行校验，不能父子嵌套
							if (aids[j] == null || aids[j] == '') {
								continue;
							}
							var obj2 = Base.getObj(aids[j]);
							if (obj2.cmptype == 'flexbox') {
								obj2 = $("#" + aids[j]);
							}
							if (i != j && obj2.cmptype != 'datagrid') {// 找到其他对象
								if ($(obj).has($(obj2)).length > 0) {
									alert(aids[j] + "对象在" + aids[i] + "对象里面，指定提交的元素id不能有包含与被包含关系");
									return false;
								}
								if ($(obj2).has($(obj)).length > 0) {
									alert(aids[i] + "对象在" + aids[j] + "对象里面，指定提交的元素id不能有包含与被包含关系");
									return false;
								}
							}
						}
						if (queryStr == "") {
							queryStr += $("#" + aids[i]).taserialize();
						} else {
							queryStr += "&" + $("#" + aids[i]).taserialize();
						}
					} else if (obj.cmptype == 'datagrid') {
						datagridids.push(new String(aids[i]));
						if (queryStr == "") {
							queryStr += $("#" + aids[i]).taserialize();
						} else {
							queryStr += "&" + $("#" + aids[i]).taserialize();
						}
						aids[i] = null;//.splice(i,1);//删除当前id
					} else {
						alert("提交的submit ids只能是panel,fieldset,box,form elements,form,div,datagrid这些元素的id");
						return false;
					}
				}
			}
			//访问Action并提交参数
			location.href=url+"?"+queryStr;
	    }
        // 数据导入向导窗口加载完成执行
        function exportDataOnLoad(){
            $(me).ready(function () {
                $("#wizard").scrollable({
                    size:6,
                    onSeek: function(event,i){ // 切换tab样
                        if(i==5){
                            callType = 2;
                            $("#_cancel").find("span").html("开始");
                        }else{
                            callType = 1;
                            $("#_cancel").find("span").html("取消");
                        }
                        $("#status li").removeClass("active").eq(i).addClass("active");
                    },
                    onBeforeSeek:function(event,i){ // 验证表单
                    	if(i<0||i>5){
                    		return false;
                    	}
                    	if(i==1){
                    		if(checkExportDataByEmodel($("#eModel:checked").val())){
                    			$(".items").children('div').hide();
                                $('#'+(i+1)).show();
                                return true;
                    		}else{
                    			return false;
                    		}
                    	}else if(i==4){
                    		var selectedData = fileTable.getSelectionGridData();
                    		if(selectedData.length<=0){
                    			Base.msgTopTip("<span style='color:red;'>请选择需要导出的字段！</span>",3000,200,50,null,me);
                    			return false;
                    		}else{
                    			$(".items").children('div').hide();
                                $('#'+(i+1)).show();
                    			return true;
                    		}
                    	}else{
                    		$(".items").children('div').hide();
                            $('#'+(i+1)).show();
                    	}
                   }
                });
            });
        }
        // 点击开始执行函数
        function startExportData() {
        	var model = $("#eModel:checked").val();
        	if(model==1||model==2){
        		exportDefaultGridData(model,fileTable);
        	}else{
        		exportDefaultGridDataAll(fileTable);
        	}
        }
        // 调用插件初始化函数
        init();
    }
}(jQuery));