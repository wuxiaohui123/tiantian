/**
 * 数据导入插件
 * Created by wuxiaohui on 2016/5/8.
 */
(function($){
    $.extend(true,window,{
        YH:{
            ImportData:ImportData
        }
    });
    function ImportData(id,options,grid,arsg) {
        var defaults = {
            title:"数据导入向导",
            width:600,
            height:400,
            isFrame:false,
            steps:["导入类型","数据源","分隔符","附加选项","目标表","结构调整","导入模式","导入开始"]
        };
        var me,mf, options,iTypeid=1,cellType=1;
        var fileTempPath=[];
        var sheetSomeData =[];
        var currentDataBaseTables = [];
        var currentDataBaseTablesAndCol = {};
        var targetDataSelection = [];
        var textFileOneRowStr = "";
        var sheetTable = {},targetTable={};
        var dataBaseSupportField = {"oracle":["CHAR","VARCHAR2","VARCHAR","CLOB","LONG","NUMBER","DATE","TIMESTAMP","BLOB","RAW","LONG RAW"],
            "mysql":["VARCHAR","INT"]
        };
        //创建本插件常用的表格对象
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
            dataGrid.getId = function(){
            	return this.id;
            };
            dataGrid.initView = function (headArr,height,isShow) {//初始化表格 ，如[{id:"1",key:"列1",width:"100"},{id:"1",key:"列2"}]
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
            dataGrid.setData = function(rowDataArr){//设置表格数据
                this.table.empty();
                var _headTd = this._headview_.children("div");
                for (var i in rowDataArr) {
                    var _tr = $("<tr/>");
                    _headTd.each(function(){
                        var _tempW = $(this).width();
                        var _data_ = $(rowDataArr[i][this.id]).width(_tempW)[0];
                        _tr.append($("<td/>").width(_tempW).html(_data_));
                    });
                    this.table.append(_tr);
                }
            };
            dataGrid.init = function () {//初始化数据
                var _trs = this.table.find("tr");
                var _headTd = this._headview_.children("div");
                for(var i=0;i<_trs.length;i++){
                    var _tds = $(_trs[i]).find("td");
                    var obj = {"rowNum":i+1};
                    for (var j = 0; j < _tds.length; j++) {
                        var temp0 = $(_tds[j]).find("input[type='checkbox']");
                        var temp1 = $(_tds[j]).find("input[type='text'],select");
                        obj[_headTd.get(j).id] = temp0.length>0?temp0.get(0).checked:temp1.val();
                    }
                    this.allData.push(obj);
                }
            };
            dataGrid.getAllGridData = function (){//获取表格所有数据
                this.allData.length=0;
                this.init();
                return this.allData;
            };
            dataGrid.getViewReport = function(){//获取表格展现view
                return this.viewReport;
            };
            dataGrid.getSelectionGridData = function (){//获取表格中选择的数据
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
        //本插件常用的ui组件及函数
        var models = {
            setITypeId:function(x){
                iTypeid = x;
            },
            setDisabled:function (id) {
                var fileddiv = $("#"+id+"_div").find(".fielddiv2");
                fileddiv.css({cursor:"not-allowed",background: "#ebebe4"});
                var select = fileddiv.find("select");
                var input = fileddiv.find("input");
                if(select.length!=0){
                    select.attr("disabled","disabled").css("cursor","not-allowed");
                }
                if(input.length!=0){
                    input.attr("disabled","disabled").css("cursor","not-allowed");
                }
            },
            setButtonDisabled:function (id) {
                $("#"+id).attr("disabled","disabled");
            },
            setEnabled:function (id) {
                var fileddiv = $("#"+id+"_div").find(".fielddiv2");
                fileddiv.css({cursor:"",background: ""});
                var select = fileddiv.find("select");
                var input = fileddiv.find("input");
                if(select.length!=0){
                    select.removeAttr("disabled").css("cursor","");
                }
                if(input.length!=0){
                    input.removeAttr("disabled").css("cursor","");
                }
            },
            setSelectInputData:function (selectInputId,Data) {
                var selectInput = $("#"+selectInputId);
                selectInput.empty();
                if(jQuery.isArray(Data)){
                    for ( var i in Data) {
                        var _option = $("<option id='"+Data[i].id+"'>"+Data[i].name+"</option>");
                        _option.data("data",Data[i].data);
                        _option.data("grid",Data[i].grid);
                        _option.data("text",Data[i].titleText);
                        selectInput.append(_option);
                    }
                }
            },
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
            getCheckboxInput:function(id,key,cssStyle,checked,value){
                var _ckbDiv = $("<div style='"+cssStyle+"'></div>");
                var _ckbLable = "<lable class='ta-chk-mc' style='padding-left: 18px;'>"+key+"</lable>";
                var _ckbInput = "<input type='checkbox' name='"+id+"' value='"+value+"' style='display: none'>";
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
            getFileInput:function (id,key,labelWidth,cellback) {
                var $fDiv = $("<div class='fielddiv fielddiv_163' style='margin: 6px 2px;'/>");
                var $fLabel = $("<label class='fieldLabel'/>").width(labelWidth).html(key);
                var $fd = $("<div class='fielddiv2'/>").css("margin-left", labelWidth);
                var $fnInput = $("<input id='"+id+"' name='"+id+"' type='text'/>");
                $fnInput.addClass("textinput validatebox-text validatebox-invalid").width('86%');
                var $flInput = $("<input type='file' id='file' name='file' accept='.txt'/>").width('14.5%');
                $flInput.change(function () {
                    $fnInput.val(this.files.item(0).name);
                    if(typeof cellback == "function"){
                        cellback();
                    }
                });
                var browser = window.navigator.userAgent.toLowerCase();
                if(browser.indexOf("rv:11.0") != -1||browser.indexOf("msie") != -1){
                    $fnInput.width("86%");
                    $flInput.width("14%");
                }else if(browser.indexOf('firefox') != -1){
                    $fnInput.width('89%');
                    $flInput.width('12%');
                }else{

                }
                $fd.append($fnInput).append($flInput);
                return $fDiv.append($fLabel).append($fd);
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
            //创建窗口按钮
            getButtons :function(btnArr){
                var _btnDiv_ = $("<div class='panel-button panelnoborder' style='height: 45px'/>");
                for(var i in btnArr){
                    var _btn = models.getButton(btnArr[i].id,btnArr[i].key,btnArr[i].class,btnArr[i].isOk,btnArr[i].cell);
                    _btnDiv_.append(_btn);
                }
                return _btnDiv_;
            },
            getInputButton:function (id,key,cellback) {
                var _button = $("<input id='"+id+"' value='"+key+"' type='button' class='sexybutton_163'/>");
                if(typeof cellback =="function") {
                    _button.click(cellback);
                }
                return _button;
            },
            getSelectTableOneRow:function(someData,selectValue){
                var resultArr = [];
                for ( var i in someData) {
                    if($(someData[i].sheetName).val()==selectValue){
                        resultArr = someData[i].oneRow;
                    }
                }
                return resultArr;
            },
            getDBfieldSelectInput:function(selectInputId,selectedIndex){
                var _select = $("<select id='"+selectInputId+"'/>");//name='"+selectInputId+"'
                var selOptions = dataBaseSupportField.oracle;
                for ( var i in selOptions) {
                    var _option = $("<option/>").html(selOptions[i]);
                    if(selectedIndex&&selectedIndex==i){
                        _option.attr("selected","selected");
                    }
                    _select.append(_option);
                }
                return _select;
            },
            getSourceFieldSelectInput:function(selectInputId,selectData,filedText){
                var _select = $("<select id='"+selectInputId+"'/>");//name='"+selectInputId+"'
                var selOptions = $.isArray(selectData)?selectData:this.getSelectTableOneRow(sheetSomeData, selectData);
                var _index = $.inArray(filedText,selOptions);
                if(_index==-1){
                    _select.append("<option value='-1' selected='selected'>--请选择字段--</option>");
                }
                for ( var i in selOptions) {
                    var _option = $("<option/>").html(selOptions[i]);
                    if(_index==i){
                        _option.attr("selected","selected");
                    }
                    _select.append(_option);
                }
                return _select;
            },
            SmartMatchingFieldType:function(fieldText){
                var obj = {};
                if(fieldText.indexOf("数")!=-1||fieldText.indexOf("号")!=-1){
                    obj.type="NUMBER";
                    obj.length=32;
                }else if(fieldText.indexOf("名称")!=-1||fieldText.indexOf("值")!=-1){
                    obj.type="VARCHAR2";
                    obj.length=255;
                }else if(fieldText.indexOf("日期")!=-1||fieldText.indexOf("时间")!=-1){
                    obj.type="DATE";
                    obj.length="";
                }else if(fieldText.indexOf("标志")!=-1){
                    obj.type="CHAR";
                    obj.length=1;
                }else{
                    obj.type="VARCHAR2";
                    obj.length=255;
                }
                return obj;
            }
        };
        //插件初始化函数
        function init(){
        	console.log(grid);
            options = $.extend({}, defaults, options);
            openImportDataWindow();
            me = $('#'+id);
            $.ajax({
                url:Base.globvar.contextPath +"/getCurrentDataBaseTablesAndColumns.do",
                dataType:"JSON",
                success:function(data){
                    currentDataBaseTables =data.fieldData.tables;
                    currentDataBaseTablesAndCol = data.fieldData.tableCol;
                }
            });
            createImportDataFrom();
            mf = $('#'+id+'_form');
            var _buttonArr = [{id:"_prev",key:"上一步",class:"prev",isOk:true},
                {id:"_next",key:"下一步",class:"next",isOk:true},{id:"_cancel",key:"取消",class:"",isOk:false,cell:function(){
                    if(cellType==2){//开始
                        console.info("执行开始导入函数.....");
                        //执行开始函数
                        startImportData();
                    }else{//取消
                        Base.closeWindow(id);
                    }
                }}];
            me.append(models.getButtons(_buttonArr));
            createImportDataStateTab();
            createImportDataContens();
            createImportDataFormDIV1();
            createImportDataFormDIV2();
            createImportDataFormDIV3();
            createImportDataFormDIV4();
            createImportDataFormDIV5();
            createImportDataFormDIV6();
            createImportDataFormDIV7();
            createImportDataFormDIV8();
        }
        //打开窗口
        function openImportDataWindow(){
            Base.openWindow(id, options.title, null, "", options.width, options.height,
                function (){//加载完成函数
                    importDataOnLoad();
                },
                function () {//关闭函数
                    deleteServiceTempFiles();
                }, false, null, {maximizable:false});
        }
        //创建form表单
        function createImportDataFrom(){
            me.append("<form id='"+id+"_form' method='post' enctype='multipart/form-data'><div id='wizard'/></form>");
        }
        //创建状态tab标签
        function createImportDataStateTab() {
            var stateUl = $("<ul id='status'/>");
            var states = options.steps;
            for(var i=0;i<states.length;i++){
                var _li = $("<li/>").append("<strong>"+(i+1)+".</strong>"+states[i]);
                _li = (i==0)?_li.addClass("active"):_li;
                stateUl.append(_li);
            }
            stateUl.appendTo($("#wizard"));
        }
        //创建单列步骤
        function createImportDataContens() {
            var items = $("<div class='items'/>");
            for(var i=1;i<=options.steps.length;i++){
                var _item = $("<div layout='column' class='grid'/>").attr("id",i);
                _item = (i==1)?_item.css("display","block"):_item.css("display","none");
                items.append(_item);
            }
            items.appendTo($('#wizard'));
        }
        //创建STEP1
        function createImportDataFormDIV1() {
            var meBox1 = $('#1');
            meBox1.append(models.getMessageTipBox("这个向导允许你指定如何导入数据，你要选择哪种数据导入格式？"));
            var $fst1 = $("<fieldset id='fst1' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox1);
            $fst1.append($("<legend>导入类型</legend>"));
            $fst1.append(models.getRadioGroup("iType", [
                {key:'文本文件（*.txt）',value:'1'},
                {key:'Excel文件（*.xls）',value:'2'},
                {key:'Excel文件（2007或以上版本）（*.xlsx）',value:'3'}]));
        }
        //创建STEP2
        function createImportDataFormDIV2() {
            var meBox2 = $('#2');
            meBox2.append(models.getMessageTipBox("你必须选择一个文件作为数据源。"));
            var $fst2 = $("<fieldset id='fst2' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox2);
            $fst2.append($("<legend>导入数据源</legend>"));
            $fst2.append(models.getFileInput("filename","导入从",45,function(){
                fileUpload();
            }));
            $fst2.append(models.getSelectInput('encode','编码',
                [{key:'UTF-8',value:'UTF-8'},{key:'GBK',value:'GBK'},{key:'GB2312',value:'GB2312'}],45));
            var tablediv = $("<div id='tablediv' style='display:none;'/>");
            tablediv.append($("<span/>").html("表："));
            sheetTable = new DataGrid("sheettable");
            var sheetTableHead = [{id:"checkbox",key:"",width:"30px"},{id:"sheetName",key:"sheet名称",width:"280px"},
                {id:"sheetColumns",key:"列数",width:"67px"},{id:"sheetRows",key:"行数",width:"67px"}];
            sheetTable.initView(sheetTableHead,"130",true);
            var reStable = $("<div class='reStable' style='height: 130px;'/>").append(sheetTable.getViewReport());
            var buttons = $("<div class='buttons'/>").append(
                models.getInputButton("quxuan","全选",function(e){
                    e.stopPropagation();
                    $("#"+sheetTable.id+"_table").find("input").each(function(){
                        $(this).prop("checked", true);
                        $(this).parent().parent().css("background","#517388");
                    });
                })).append(
                models.getInputButton("quxiaoQX","取消全选",function(e){
                    e.stopPropagation();
                    $("#"+sheetTable.id+"_table").find("tr").each(function(){
                        this.style="";
                        $(this).find("input")[0].checked=false;
                    });
                }));
            tablediv.append(reStable).append(buttons).append("<div style='clear: both'></div>");
            $fst2.append(tablediv);
        }
        //创建STEP3
        function createImportDataFormDIV3() {
            var meBox3 = $("#3");
            meBox3.append(models.getMessageTipBox("你的字段要用什么分隔符来分隔？请选择合适的分隔符。"));
            var $fst3 = $("<fieldset id='fst3' class='feildbox'/>").attr("form",id+"_form").appendTo(meBox3);
            $fst3.append($("<legend>字段分隔符</legend>"));
            $fst3.append(models.getSelectInput("recode_commas","记录分隔符",
                [{key:"CRLF",value:"1"},
                    {key:"CR",value:"2"},
                    {key:"LF",value:"3"}],80));
            $fst3.append(models.getSelectInput("field_commas","字段分隔符",
                [{key:"定位（Tab）",value:"\\t"},
                    {key:"分号（;）",value:";"},
                    {key:"逗号（,）",value:","},
                    {key:"空格",value:" "},
                    {key:"无",value:""}],80));
            $fst3.append(models.getSelectInput("text_commas","文本限定符",
                [{key:"无",value:"T1"},
                    {key:"\"",value:"T2"},
                    {key:"'",value:"T3"},
                    {key:"~",value:"T4"}],80));
        }
        //创建STEP4
        function createImportDataFormDIV4() {
            var meBox4 = $("#4");
            meBox4.append(models.getMessageTipBox("你可以为源定义一些附加的选项。"));
            var $fst4 = $("<fieldset id='fst4' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox4);
            $fst4.append($("<legend>附加选项</legend>"));
            $fst4.append(models.getTxtInput("fieldname_hs","字段名行数",{float:"left",width:"32.61%"},100,"1"));
            $fst4.append(models.getTxtInput("frist_datah","第一个数据行",{float:"left",width:"32.61%"},100,"2"));
            $fst4.append(models.getTxtInput("last_datah","最后一个数据行",{float:"left",width:"32.61%"},100));
            var innerFst = $("<fieldset/>").append($("<legend>格式</legend>"));
            innerFst.append(models.getSelectInput("date_sort","日期排序",
                [{key:"MDY",value:"D1"},
                    {key:"DMY",value:"D2"},
                    {key:"YMD",value:"D3"},
                    {key:"YDM",value:"D4"},
                    {key:"DYM",value:"D5"},
                    {key:"MYD",value:"D6"}],
                100,{float:"left",width:"49%"}));
            innerFst.append(models.getTxtInput("date_commas","日期分隔符",{float:"left",width:"49%"},100,"/"));
            innerFst.append(models.getTxtInput("time_commas","时间分隔符",{float:"left",width:"49%"},100,":"));
            innerFst.append(models.getSelectInput("datetime_sort","时间日期排序",
                [{key:"日期 时间",value:"DT1"},
                    {key:"时间 日期",value:"DT2"},
                    {key:"日期 时间 时区",value:"DT3"},
                    {key:"时间 日期 时区",value:"DT4"},
                    {key:"时间 时区 日期",value:"DT5"}], 100,{float:"left",width:"49%"}));
            innerFst.append(models.getSelectInput("bindata_code","二进制数据编码",
                [{key:"Base64",value:"BDT1"},
                    {key:"无",value:"BDT0"}], 100,{float:"left",width:"49%"}));
            $fst4.append(innerFst);
        }
        //创建STEP5
        function createImportDataFormDIV5() {
            var meBox5 = $("#5");
            meBox5.append(models.getMessageTipBox("你可以为源定义一些附加的选项。"));
            var $fst5 = $("<fieldset id='fst5' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox5);
            $fst5.append($("<legend>选择目标表</legend>"));
            var targetTableHead = [{id:"SourceTb",key:"源表",width:"225px"},{id:"TargetTb",key:"目标表",width:"225px"},
                {id:"CreateTb",key:"新建表",width:"82px"}];
            targetTable = new DataGrid("targetTable");
            targetTable.initView(targetTableHead, "198", true);
            var tableDiv = $("<div style='border: 0.5px solid #BDB6B6;border-radius: 4px;height: 198px;'>").append(targetTable.getViewReport());
            $fst5.append(tableDiv);
        }
        //创建STEP6
        function createImportDataFormDIV6() {
            var meBox6 = $("#6");
            meBox6.append(models.getMessageTipBox("这个向导已对表结构运行一些猜测。现在你可以进行调整。"));
            var $fst6 = $("<fieldset id='fst6' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox6);
            $fst6.append("<legend>字段调整</legend>");
            $fst6.append($("<div/>")
                .append(models.getSelectInput("recode_table", "源表", null, 60,  null))
                .append($("<div style='height:28px;'/>")
                    .append("<span style='line-height:28px;padding:0px 10px 0px 17px;'>目标表:</span>")
                    .append("<span id='targetKey'><span>")
                ).append($("<div class='filedadjust'/>")));
            $("#recode_table").change(function(e){
                var _optionData = $("#recode_table>option:selected");
                $("#targetKey").html(_optionData.data("data").TargetTb);
                $(".filedadjust").empty().append(_optionData.data("grid").getViewReport());
                primaryKeyClickHandler();
                $("#6 .msgbox").find("span").html(_optionData.data("text"));
            });

        }
        //创建STEP7
        function createImportDataFormDIV7() {
            var meBox7 = $("#7");
            meBox7.append(models.getMessageTipBox("请选择一个所需的导入模式。"));
            var $fst7 = $("<fieldset id='fst7' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox7);
            $fst7.append("<legend>导入模式</legend>");
            var ImodelDiv = $("<div id='iModel' class='fielddiv2' layout='column' cols='1' style='margin-left: 2px'></div>").appendTo($fst7);
            ImodelDiv.append(models.getRadioGroup("iModel", [
                    {key:"添加：添加记录到目标表",value:"1"},
                    {key:"更新：更新目标和源记录相符的记录",value:"2"},
                    {key:"添加或更新：如果目标存在相同记录，更新它。否则，添加它",value:"3"},
                    {key:"删除：删除目标中和源记录相符的记录",value:"4"},
                    {key:"复制：删除目标全部记录，并从源重新导入",value:"5"}
                ]
            ));
            var subFst = $("<fieldset style='height: 50px'><legend>高级</legend></fieldset>").appendTo($fst7);
            var seniorDiv = $("<div class='advDIV'></div>").appendTo(subFst);
            seniorDiv.append(models.getCheckboxInput("seniorSet1", "用空白字符串替代NULL", "width: 50%",false,"1"));
            seniorDiv.append(models.getCheckboxInput("seniorSet2", "遇到错误继续", "width: 50%",true,"2"));
            seniorDiv.append("<div style='clear: both'></div>");
        }
        //创建STEP8
        function createImportDataFormDIV8() {
            var meBox8 = $("#8");
            meBox8.append(models.getMessageTipBox("我们已收集向导导入数据时所需的全部信息。点击【开始】按钮进行导入。"));
            var $fst8 = $("<fieldset id='fst8' class='feildbox'/>").attr('form',id+'_form').appendTo(meBox8);
            $fst8.append("<legend>导入开始</legend>");
            $fst8.append("<div id='information' class='info'/>");  
        }
        //点击单选框的回调函数
        function fnRadioCellBack(obj) {
            var type = $(obj).find("input")[0].name;
            if(type=="iType"){
                var inputValue = $(obj).find("input").val();
                models.setITypeId(inputValue);
                $("#filename").val("");
                fnRadioCellBackByiType(inputValue);
            }
            if(type=="iModel"){
            	 var imodel = $("#fst7").find("input[name='iModel']:checked").val();
            	 fnRadioCellBackByiTypeAndiModel(iTypeid,imodel);
            }
        }
        function fnRadioCellBackByiType(typeid){
            if(typeid == 1){
                $('#encode_div').show();$('#tablediv').hide();
                $('#file').attr('accept',"text/plain");
                models.setEnabled("recode_commas");
                models.setEnabled("field_commas");
                models.setEnabled("text_commas");
            }else{
                $('#encode_div').hide();$('#tablediv').show();
                $('#file').attr('accept','.xls');
                models.setDisabled("recode_commas");
                models.setDisabled("field_commas");
                models.setDisabled("text_commas");
            }
        }
        
        function fnRadioCellBackByiTypeAndiModel(typeid,modelid){
        	var result = {};	
        	var temp = [];
        	result.bool = true;
        	if(typeid==1&&(modelid==2||modelid==3)){
        		var bool = true;
                var _optionGrid = $("#recode_table>option:selected").data("grid");
                data = getUpdateAndPrimaryArray(_optionGrid);
                if(data.primaryArray.length<=0){
                    var mssage = "<span style='color:red;'>表：【"+_optionGrid.id+"】没有选择主键！</span>";
                    $("#_prev").trigger("click");
                    Base.msgTopTip(mssage,3000,200,50,null,me);
                    bool = false;
                }else if(data.updateArray.length<=0){
                    var mssage = "<span style='color:red;'>表：【"+_optionGrid.id+"】没有选择修改字段！</span>";
                    $("#_prev").trigger("click");
                    Base.msgTopTip(mssage,3000,200,50,null,me);
                    bool = false;
                }
                if(bool){
                	temp.push(data);
                }else{
                	result.bool = bool;
                	return result;
                } 
                result.data = temp;
            }
        	if(typeid!=1&&(modelid==2||modelid==3)){
        		var _option = $("#recode_table>option");
        		for (var i = 0; i < _option.length; i++) {
        			var bool = true;
        			var datagrid = $(_option[i]).data("grid");
        			data = getUpdateAndPrimaryArray(datagrid);
                    if(data.primaryArray.length<=0){
                        var mssage = "<span style='color:red;'>表：【"+datagrid.id+"】没有选择主键！</span>";
                        $("#_prev").trigger("click");
                        Base.msgTopTip(mssage,3000,200,50,null,me);
                        bool = false;
                    }else if(data.updateArray.length<=0){
                        var mssage = "<span style='color:red;'>表：【"+datagrid.id+"】没有选择修改字段！</span>";
                        $("#_prev").trigger("click");
                        Base.msgTopTip(mssage,3000,200,50,null,me);
                        bool = false;
                    }
                    if(bool){
                    	temp.push(data);
                    }else{
                    	result.bool = bool;
                    	return result;
                    } 
				}
        		result.data = temp;
        	}
        	return result;
        }
        function getUpdateAndPrimaryArray(datagrid) {
            var result = {};
            result.updateArray=[];
            result.primaryArray=[];
            var _selectArray = datagrid.getSelectionGridData();
            for(var i in _selectArray){
                if(_selectArray[i].primaryKey){
                    result.primaryArray.push(_selectArray[i].targetField);
                }else{
                    result.updateArray.push(_selectArray[i].targetField);
                }
            }
            return result;
        }
        //文件上传
        function fileUpload(){
            $.ajaxFileUpload({
                url: Base.globvar.contextPath + "/upLoadImportDataFile.do?itypeid="+iTypeid,
                fileElementId:'file',
                secureuri:false,
                dataType: 'JSON',
                success: function(data,status){
                    var obj =  eval('(' + data + ')');
                    if(obj.success == true){
                        fileTempPath.push(obj.fieldData.realPath);
                        if(iTypeid!=1){//excel文件
                            var tabledata = [];
                            sheetSomeData = obj.fieldData.sheetlist;
                            for (var i = 0; i < sheetSomeData.length; i++) {
                                sheetSomeData[i].checkbox="<input type='checkbox'/>";
                                sheetSomeData[i].sheetName="<input type='text' value='"+sheetSomeData[i].sheetName+"' disabled='disabled'/>";
                                sheetSomeData[i].sheetColumns="<input type='text' value='"+sheetSomeData[i].sheetColumns+"' disabled='disabled'/>";
                                sheetSomeData[i].sheetRows="<input type='text' value='"+sheetSomeData[i].sheetRows+"' disabled='disabled'/>";
                                tabledata.push(sheetSomeData[i]);
                            }
                            sheetTable.setData(tabledata);
                            $("#"+sheetTable.id+"_table").find("input").click(function(){
                                var tr = $(this).parent().parent();
                                if(!tr[0].hasAttribute("style")){
                                    $(this).parent().parent().css("background","#517388");
                                }else{
                                    tr.removeAttr("style");
                                }
                            });
                        }else{//文本文件
                            textFileOneRowStr = obj.fieldData.oneline;
                        }
                    }
                },
                error: function(data,status,e){
                    console.error(e);
                }
            });
        }
        //将数据转换为SHEET表的格式数据
        function someDataToTargetTableData(sheetTableData,typeid){
            var targetTableData = [];
            if(typeid!=1){//excel文件
                var _createtb={};
                for (var i = 0; i < sheetTableData.length; i++) {
                    var obj = {};
                    var DataObj = sheetTableData[i];
                    var _select = $("<select/>");
                    var index = $.inArray(DataObj.sheetName, currentDataBaseTables);//返回
                    obj.SourceTb = "<input type='text' disabled='disabled' value='"+DataObj.sheetName+"'/>";
                    if(index!=-1){
                        _select = setSelectOption(_select);
                        _select.find("option").eq(index).attr("selected","selected");
                        _createtb = "<input type='checkbox' disabled='disabled'/>";
                    }else{
                        _select.append("<option selected ='selected'>"+DataObj.sheetName+"</option>");
                        _select = setSelectOption(_select);
                        _createtb = "<input type='checkbox' checked='checked' disabled='disabled'/>";
                    }
                    obj.TargetTb = _select;
                    obj.CreateTb = _createtb;
                    targetTableData.push(obj);
                }
            }else{//txt文件
                var obj = {},_createtb={};
                var _select = $("<select/>");
                var _fileName = $("#filename").val();
                var _realName = _fileName.substring(0,_fileName.indexOf(".txt"));
                obj.SourceTb = "<input type='text' disabled='disabled' value='"+_realName+"'/>";
                var _index = $.inArray(_realName, currentDataBaseTables);
                if(_index!=-1){
                    _select = setSelectOption(_select);
                    _select.find("option").eq(_index).attr("selected","selected");
                    _createtb = "<input type='checkbox' disabled='disabled'/>";
                }else{
                    _select.append("<option selected ='selected'>"+_realName+"</option>");
                    _select = setSelectOption(_select);
                    _createtb = "<input type='checkbox' checked='checked' disabled='disabled'/>";
                }
                obj.TargetTb = _select;
                obj.CreateTb = _createtb;
                targetTableData.push(obj);
            }
            function setSelectOption(obj){
                for (var j = 0; j < currentDataBaseTables.length; j++) {
                    obj.append("<option>"+currentDataBaseTables[j]+"</option>");
                }
                obj.change(function(){
                    var otherTd = obj.parent().siblings();
                    var input = $(otherTd.get(0)).find("input");
                    if(input.val()!=obj.val()){
                        obj.find("option:contains('"+input.val()+"')").remove();
                        $(otherTd.get(1)).find("input").removeAttr("checked");
                    }
                });
                return obj;
            }
            return targetTableData;
        }
        //将数据转换为字段调整的格式数据
        function someDataToTableData(tableData,typeId,selectData){
            var someTableDate = [];
            for ( var i in tableData) {
                var obj = {};
                obj.checkbox="<input type='checkbox' checked='checked'/>";
                obj.primaryKey = "<div class='primaryKey'><input type='checkbox' style='display:none;'/></div>";
                obj.targetField = "<input type='text' value='"+tableData[i]+"' disabled='disabled'/>";
                if(typeId==1){
                    var typeObj = models.SmartMatchingFieldType(tableData[i]);
                    var _index = $.inArray(typeObj.type, dataBaseSupportField.oracle);//返回 index
                    obj.fieldType = models.getDBfieldSelectInput("fieldtype",_index).get(0);
                    obj.fieldLength = "<input type='text' value='"+typeObj.length+"' />";
                    obj.fieldRatio = "<input type='text' value='0'/>";
                }else{
                    obj.sourceField = models.getSourceFieldSelectInput("sourcefield", selectData, tableData[i]);
                }
                someTableDate.push(obj);
            }
            return someTableDate;
        }
        //将数据转换为目标表的格式数据
        function someDataToSelectInputData(targetData){
            var someData = [];
            var hearArr = {"create":[{id:"checkbox",key:"",width:"30px"},{id:"targetField",key:"目标字段",width:"100px"},
                {id:"fieldType",key:"类型",width:"100px"},{id:"fieldLength",key:"长度",width:"100px"},
                {id:"fieldRatio",key:"比例",width:"100px"},{id:"primaryKey",key:"主键",width:"100px"}],
                "nocreate":[{id:"checkbox",key:"",width:"30px"},{id:"targetField",key:"目标字段",width:"201px"},
                    {id:"sourceField",key:"源字段",width:"201px"},{id:"primaryKey",key:"主键",width:"100px"}]
            };
            for ( var i in targetData) {
                var obj = {};
                obj.id = Math.round(Math.random()*100);
                obj.name = targetData[i].SourceTb;
                obj.data = targetData[i];
                var datagrid = new DataGrid(targetData[i].TargetTb);
                if(targetData[i].CreateTb){//新建表
                    datagrid.initView(hearArr.create,"126px",true);
                    var gridData = [];
                    if(iTypeid!=1){//excel文件
                        gridData = models.getSelectTableOneRow(sheetSomeData, targetData[i].SourceTb);
                    }else{
                        var filedComms = $("#field_commas").val();
                        textFileOneRowStr = textFileOneRowStr.replace(/\"/g,"");
                        gridData = textFileOneRowStr.split(filedComms);
                    }
                    var tableData = someDataToTableData(gridData, 1);
                    datagrid.setData(tableData);
                    obj.titleText = "这个向导已对表结构运行一些猜测。现在你可以进行调整。";
                }else{
                    datagrid.initView(hearArr.nocreate,"126px",true);
                    var tableData = [],columns=[];
                    if(iTypeid!=1){//excel文件
                        columns = currentDataBaseTablesAndCol[targetData[i].TargetTb];
                        tableData = someDataToTableData(columns , 2,targetData[i].SourceTb);
                    }else{//文本文件Text
                        var filedComms = $("#field_commas").val();
                        textFileOneRowStr = textFileOneRowStr.replace(/\"/g,"");
                        var textFileOneRowArr = textFileOneRowStr.split(filedComms);
                        columns = currentDataBaseTablesAndCol[targetData[i].TargetTb];
                        tableData = someDataToTableData(columns, 2,textFileOneRowArr);
                    }
                    datagrid.setData(tableData);
                    obj.titleText = "你可以定义字段对应。设置对应字段的源字段和目的字段之间的对应关系。";
                }
                obj.grid =datagrid;
                someData.push(obj);
            }
            return someData;
        }
        //给主键单元格添加单击事件处理
        function primaryKeyClickHandler(){
            $(".primaryKey").click(function(e){
                if($(e.target).hasClass("icon-key-PK")){
                    $(e.target).removeClass("icon-key-PK");
                    $(e.target).find("input").get(0).checked=false;
                }else{
                    $(e.target).addClass("icon-key-PK");
                    $(e.target).find("input").get(0).checked=true;
                }
            });
        }
        //校验字段调整表格的数据输入的完整性和正确性
        function validateTableData() {
            var _result={msg:"",bool:true};
            var _options = $("#recode_table").find("option");
            for (var i = 0; i < _options.length; i++) {
                var _selectData = $(_options[i]).data("grid").getSelectionGridData();
                var _selectObj = $(_options[i]).data("data");
                if(_selectData.length>0){
                    if(!_selectObj.CreateTb){//非新建表
                        var _tempBool = validateTable(_selectData);
                        _result.bool = _tempBool;
                        _result.msg = _tempBool?"":"【"+_selectObj.TargetTb+"】没有选择字段！";
                    }
                }else {
                    _result.msg = "【"+_selectObj.TargetTb+"】没有选择字段！";
                    _result.bool = false;
                }
            }
            function validateTable(tabledata){
                for ( var j in tabledata) {
                    if(tabledata[j].checkbox&&tabledata[j].sourceField==-1){
                        return false;
                    }
                }
                return true;
            }
            return _result;
        }
       
        //数据导入向导窗口加载完成执行
        function importDataOnLoad(){
            $(me).ready(function () {
                $("#wizard").scrollable({
                    size:8,
                    onSeek: function(event,i){ //切换tab样
                        if(i==7){
                            cellType = 2;
                            $("#_cancel").find("span").html("开始");
                        }else{
                            cellType = 1;
                            $("#_cancel").find("span").html("取消");
                        }
                        $("#status li").removeClass("active").eq(i).addClass("active");
                    },
                    onBeforeSeek:function(event,i){ //验证表单
                    	if(i<0||i>7){
                    		return false;
                    	}
                        if(i==2){
                            if($("#filename").val()!=""){//判断文件名称是否为空
                                var _sheetTableSelection = sheetTable.getSelectionGridData();
                                if(iTypeid!=1&&_sheetTableSelection.length<=0){//判断是否选择表名
                                    Base.msgTopTip("<span style='color:red;'>必须输入表名！</span>",3000,200,50,null,me);
                                    return false;
                                }
                                var _targetData = someDataToTargetTableData(_sheetTableSelection,iTypeid);
                                targetTable.setData(_targetData);
                                $(".items").children('div').hide();
                                $('#'+(i+1)).show();
                                return true;
                            }else{//如果文件名称为空，则给出提示
                                Base.msgTopTip("<span style='color:red;'>请选择文件！</span>",3000,200,50,null,me);
                                return false;
                            }
                        }else if(i==5){
                            targetDataSelection = targetTable.getAllGridData();
                            var _selectData = someDataToSelectInputData(targetDataSelection);
                            models.setSelectInputData("recode_table", _selectData);//设置下拉选的值
                            var _optionData = $("#recode_table>option:selected");
                            $("#targetKey").html(_optionData.data("data").TargetTb);
                            $(".filedadjust").empty().append(_optionData.data("grid").getViewReport());
                            primaryKeyClickHandler();
                            $("#6 .msgbox").find("span").html(_optionData.data("text"));
                            $(".items").children('div').hide();
                            $('#'+(i+1)).show();
                        }else if(i==6){
                            var _result = validateTableData();
                            if(_result.bool){
                                $(".items").children('div').hide();
                                $('#'+(i+1)).show();
                                return true;
                            }else{
                                Base.msgTopTip("<span style='color:red;'>"+_result.msg+"</span>",3000,250,40,null,me);
                                return false;
                            }
                        }else if(i==7) {
                        	var imodel = $("#fst7").find("input[name='iModel']:checked").val();
                       	    var result = fnRadioCellBackByiTypeAndiModel(iTypeid,imodel);
                       	    if(result.bool){
                       	    	$(".items").children('div').hide();
                                $('#'+(i+1)).show();
                       	    }
                            return result.bool;
                        }else{
                        	$(".items").children('div').hide();
                            $('#'+(i+1)).show();
                        }
                    }
                });
            });
        }
        //窗口关闭时删除本次的服务器临时文件
        function deleteServiceTempFiles() {
            if(fileTempPath.toString()!=""){
                $.ajax({
                    url:Base.globvar.contextPath +"/deleteTempFiles.do",
                    type:"POST",
                    dataType:"JSON",
                    data:{"files":fileTempPath.toString()},
                    success:function (data) {
                        if(data.success){
                            console.info("删除临时文件成功！");
                        }else{
                            console.info("删除临时文件失败！");
                        }
                    }
                });
            }
        }
        //获取当前的文件临时路径
        function getCurentFileNamePath() {
            var result = "";
            var cusFilename = $("#filename").val();
            for(var i in fileTempPath){
                var temp = fileTempPath[i].substr(fileTempPath[i].indexOf("_")+1,fileTempPath[i].length);
                if(temp==cusFilename){
                    result = fileTempPath[i];
                    break;
                }
            }
            return result;
        }
        //根据导入的id获取导入类型描述
        function getImpTypeDescById(tid){
        	if(tid==1){
        		return "文本文件"
        	}else if(tid==2){
        		return "excel 2003文件";
        	}else{
        		return "excel 2007文件";
        	}
        }
        //根据导入的模式id获取模式描述
        function getImpModelDescById(mid){
        	if(mid==1){
        		return "添加"
        	}else if(mid==2){
        		return "更新";
        	}else if(mid==3){
        		return "添加或更新";
        	}else if(mid==4){
        		return "删除";
        	}else{
        		return "复制";
        	}
        }
        //点击开始执行函数
        function startImportData() {
            $("<input type='text' id='realname' name='realname'/>").val(getCurentFileNamePath()).hide().appendTo(mf);
            var meserstr = mf.serialize(),myPram = "";
            var _parmArr = meserstr.split("&");
            for ( var i in _parmArr) {
                var pram = _parmArr[i].split("=");
                myPram += (i!=0)?"&":"";
                myPram += "dto['"+pram[0]+"']="+pram[1];
            }
            if(iTypeid!=1){//excel
                myPram +="&dto['sheetTable']="+JSON.stringify(sheetTable.getSelectionGridData());
            }
            myPram +="&dto['targetTable']="+JSON.stringify(targetTable.getAllGridData());
            var imodel = $("#fst7").find("input[name='iModel']:checked").val();
            var _options = $("#recode_table").find("option");
            var result = fnRadioCellBackByiTypeAndiModel(iTypeid,imodel);
            for ( var i=0;i<_options.length;i++) {
                var _grid = $(_options[i]).data("grid");
                if(imodel==2||imodel==3){
                	myPram += "&dto['"+_grid.getId()+"updateArray']="+result.data[i].updateArray;
                    myPram += "&dto['"+_grid.getId()+"primaryArray']="+result.data[i].primaryArray;
                }
                myPram +="&dto['"+_grid.id+"']="+JSON.stringify(_grid.getSelectionGridData());
            }
            //执行请求
            $.ajax({
                async:true,//ture同步，false异步
                beforeSend:function (XMLHttpRequest) {
                	Base.showMask(id,true);
                },
                url:Base.globvar.contextPath +"/startImportData.do",
                type:"POST",
                data:myPram,
                success:function(data){
                	if(data.success){
                		Base.hideMask();
                		var jsondata = eval('(' + data.fieldData.info + ')');
                		var informational = $("#information");
                		informational.append("<p>[信息] [导入] 导入开始</p>");
                		informational.append("<p>[信息] [导入] 导入类型 - "+getImpTypeDescById(jsondata.type)+"</p>");
                		informational.append("<p>[信息] [导入] 导入模型 - "+getImpModelDescById(jsondata.model)+"</p>");
                		informational.append("<p>[信息] [导入] 导入数据量 - "+jsondata.size+"</p>");
                		informational.append("<p>[信息] [导入] 导入用时 - "+jsondata.time+" 毫秒</p>");
                		informational.append("<p>[信息] [导入] 完成 - 成功！</p>");
                		informational.append("<p>---------------------</p>");
                		Base.alert(data.msgBox.msg,data.msgBox.msgType,function(){
                			models.setButtonDisabled("_prev");
                			cellType = 1;
                            $("#_cancel").find("span").html("关闭");
                		});
                	}
                },
                error:function(data){
                	console.log(data);
                }
            });
        }
        //调用插件初始化函数
        init();
    }
}(jQuery));