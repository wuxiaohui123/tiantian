<%@tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@tag import="com.yinhai.sysframework.app.domain.jsonmodel.ResultBean"%>
<%@tag import="java.util.Random"%>
<%@tag import="com.yinhai.sysframework.util.TagUtil"%>
<%--@doc--%>
<%@tag description='文件上传' display-name="uploadify" %>
<%@attribute description='id，必填，唯一' name='id' required='true' type='java.lang.String' %>
<%@attribute description='组件的label标签' name='key' type='java.lang.String' %>
<%@attribute description='需要传递到后台的字段id，例如：formData="aac001,aac002",字段之间用半角逗号隔开' name='formData' type='java.lang.String' %>
<%@attribute description='url地址，必填，namespace+action+method，例如url="demo/fileupload/fileuploadAction!upload.do"' name='url' required='true' type='java.lang.String' %>
<%@attribute description='true/false,表示选择文件后是否自动提交，默认true，表示自动提交' name='auto' type='java.lang.String' %>
<%@attribute description='post/get,提交方式，默认post' name='method' type='java.lang.String' %>
<%@attribute description='true/false,是否允许多文件上传，默认true,表示可以多文件上传' name='multi' type='java.lang.String' %>
<%@attribute description='按钮的文本，默认"选择文件"' name='buttonText' type='java.lang.String' %>
<%@attribute description='限制文件上传大小，可以设置成"KB,MB,GB"，默认"10MB",例如：fileSizeLimit="500KB"' name='fileSizeLimit' type='java.lang.String' %>
<%@attribute description='设置文件上传时显示的数据，有两个选择："上传速度"或者"百分比"，分别对应"speed"和"percentage"' name='progressData' type='java.lang.String' %>
<%@attribute description='hand/arrow鼠标移入按钮时的样式，默认hand' name='buttonCursor' type='java.lang.String' %>
<%@attribute description='限制文件上传类型，例如：fileTypeExts="*.jpg;*.png",表示只允许上传jpg,png为后缀的文件，默认不限制' name='fileTypeExts' type='java.lang.String' %>
<%@attribute description='设置一次性最多上传多少个文件,例如：uploadLimit="10"，表示一次性最多上传10个文件，默认999' name='uploadLimit' type='java.lang.String' %>
<%@attribute description='设置按钮的高度,例如：height="30",默认为20' name='height' type='java.lang.String' %>
<%@attribute description='设置按钮的宽度,例如：width="130",默认为100' name='width' type='java.lang.String' %>
<%@attribute description='自定义按钮样式,是一个class，例如:buttonClass="bClass"' name='buttonClass' type='java.lang.String' %>
<%@attribute description='设置按钮背景图片路径,例如:buttonImage="../upload/a.png"' name='buttonImage' type='java.lang.String' %>
<%@attribute description='true/false,表示在上传完成后是否删除队列中的对应元素。默认是True，即上传完成后就看不到上传文件进度条了' name='removeCompleted' type='java.lang.String' %>
<%@attribute description='表示上传完成后多久删除队列中的进度条，例如：removeTimeout="10",默认为3，即3秒' name='removeTimeout' type='java.lang.String' %>
<%@attribute description='自定义上传和取消上传按钮的样式，是一个class，例如uploadClass="upAndcancelClass"' name='uploadClass' type='java.lang.String' %>
<%@attribute description='设置上传队列中最多所能容纳的文件' name='queueSizeLimit' type='java.lang.String' %>
<%@attribute description='自定义上传队列的样式，是一个class，例如：fileQueueClass="queueClass"' name='fileQueueClass' type='java.lang.String' %>
<%@attribute description='上传按钮的icon，默认为icon-save，例如：icon_up="icon-add"' name='icon_up' type='java.lang.String' %>
<%@attribute description='取消按钮的icon，默认为icon-remove，例如：icon_cancel="icon-cancel"' name='icon_cancel' type='java.lang.String' %>
<%@attribute description='true/false,是否需要组件默认的上传和取消上传按钮，当设置成true的时候，表示需要。默认false，不需要，这个时候需要自己创建上传或者取消按钮，例如创建上传ta:button，onClick="jQuery("#上传组件的id").uploadify("upload","*")",取消上传ta:button,onClick="jQuery("#上传组件的id").uploadify("cancel","*")"' name='upAndcancelButton' type='java.lang.String' %>
<%@attribute description='true/false,是否在上传按钮前面显示上传文件名的只读输入框，默认false,表示不显示,在后台以getDto().getAsString("upload_filename")的形式获取' name='uploadFilenames' type='java.lang.String' %>
<%@attribute description='成功上传的回调函数，每上传成功一个，都会回调，例如：onUploadSuccess="onUsuccess",在js里面function onUsuccess(file, data, response){},file,data,response是三个默认参数' name='onUploadSuccess' type='java.lang.String' %>
<%@attribute description='上传失败的回调函数，每上传失败一次，都会回调，例如：onUploadError="onUerror",在js里面function onUerror(file, errorCode, errorMsg, errorString){},file, errorCode, errorMsg, errorString是四个默认参数' name='onUploadError' type='java.lang.String' %>
<%@attribute description='上传文件名的key' name='filenameKey' type='java.lang.String' %>
<%@attribute description='定义只读文件名框的宽度，比如:inputWidth="50%"，默认40%；也可以定义成px,例如：inputWidth="100px"' name='inputWidth' type='java.lang.String' %>
<%--@doc--%>
<%
   String basePath = request.getContextPath();
   if (null != height) {
	 String temph  = height;
	 temph = temph.endsWith("px")?(temph.substring(0,temph.indexOf("px"))):temph;
	 height = temph;
   }
   if (null != width) {
	 String tempw  = width;
	 tempw = tempw.endsWith("px")?(tempw.substring(0,tempw.indexOf("px"))):tempw;
	 width = tempw;
   }
   String sessionId = request.getSession().getId();
   String span = "1";
   String columnWidth = "1";
%>
<%@include file="../columnhead.tag" %>
<div class="fielddiv">
<% if(uploadFilenames != null){%>
<% if(filenameKey != null){%>
	<label for="${id}_filenames" class="fieldLabel">
${filenameKey}:
	</label>
<%}%>
	<div class="fielddiv2" <% if(filenameKey != null){%><%}else{%>style="margin-left:0px"<%}%> >
		<input type="text" id="${id}_filenames" class="textinput readonly uploadify_filename" readonly="readonly" style="width:<% if(inputWidth != null){%>${inputWidth}<%}else{%>40%;<%}%>">
		<%}else{%>
		<div class="fielddiv2" <% if(filenameKey != null){%><%}else{%>style="margin-left:0px"<%}%> >
		<%}%>
		<div  class="uploadify_file" >
		<% if(key != null){%>
		<label for="${id}" class="fieldLabel">
		${key}:
			</label>
		<%}%>
		<input type="file" <% if(id != null){%>id="${id}"<%}%>/>
		<% if(uploadFilenames != null){%>
		<%}else{%>
		<div id="fileQueue_${id}" class="<% if(key != null){%>fileQueue_uploadify_queue<%}else{%>fileQueue_uploadify_queue_noleft<%}%> <% if(fileQueueClass != null){%>${fileQueueClass}<%}%>"></div>
		<%}%>
		</div>
	</div>
	<div id="fileQueue_${id}" class="fileQueue_uploadify_queue <% if(fileQueueClass != null){%>${fileQueueClass}<%}%>"></div>
<% if(upAndcancelButton != null){%>
<span>
<button id="submit_${id}" class="sexybutton" type="button" onclick="jQuery('#${id}').uploadify('upload','*')">
	<span>
		<span>
			<span class="<% if(icon_up != null){%>${icon_up}<%}else{%>icon-save<%}%>">
					 开始上传
			</span>
		</span>
	</span>
</button>
<button id="cancel_${id}" class="sexybutton" type="button" onclick="jQuery('#${id}').uploadify('cancel','*')">
	<span>
		<span>
			<span class="<% if(icon_cancel != null){%>${icon_cancel}<%}else{%>icon-remove<%}%>">
					 取消上传
			</span>
		</span>
	</span>
</button>
</span>

<%}%>
</div>
<%@include file="../columnfoot.tag" %>
<script type="text/javascript">
//$(document).ready(function () {
(function(factory){
	if (typeof require === 'function') {
		require(["jquery", "TaUIManager", "uploadify"], factory);
	} else {
		factory(jQuery);
	}
}(function($){

Ta.core.TaUICreater.addUI(
		function(){
	<% if(formData != null){%>
   var params = {};
   var datas = [];
   datas = '${formData}'.split(',');
   for(var i = 0 ; i < datas.length ; i++){
   		params["dto['"+datas[i]+"']"] = Base.getValue(datas[i]);
   }
   <%}%>
    var path = Base.globvar.contextPath;
	$("#${id}").uploadify({
             'swf'       :path + '/ta/resource/themes/base/uploadify/uploadify.swf',
             'uploader'         :path + '<% if(url != null){%>/${url}<%}%>;jsessionid="<%=sessionId%>"',
             'queueID'        : 'fileQueue_${id}',
             <% if(buttonClass != null){%>
             'buttonClass' : '${buttonClass}',
             <%}%>
             <% if(buttonImage != null){%>
             'buttonImage' : '${buttonImage}',
             <%}%>
             <% if(removeCompleted != null){%>
             'removeCompleted' : ${removeCompleted},
             <%}%>
             <% if(removeTimeout != null){%>
             'removeTimeout' : ${removeTimeout},
             <%}%>
             'fileObjName'   : '${id}',
             'auto'           : <% if(auto != null){%>${auto}<%}else{%>true<%}%>,
             'method' : '<% if(method != null){%>${method}<%}else{%>post<%}%>',
             'multi' : <% if(multi != null){%>${multi}<%}else{%>true<%}%>,
             'buttonText' : '<% if(buttonText != null){%>${buttonText}<%}else{%>选择文件<%}%>',
             <% if(progressData != null){%>
             'progressData':'${arameters.progressData}',
             <%}%>
             'fileSizeLimit' : '<% if(fileSizeLimit != null){%>${fileSizeLimit}<%}else{%>10MB<%}%>',
             <% if(buttonCursor != null){%>
             'buttonCursor' : '${buttonCursor}',
             <%}%>
             <% if(fileTypeExts != null){%>
             'fileTypeExts' : '${fileTypeExts}',
             <%}%>
             'onClearQueue' : function(queueItemCount) {
		           alert(queueItemCount + ' 个文件已经从队列中移除');
		       } ,
		       <% if(formData != null){%>
		       'formData':params,
		       <%}%>
		       <% if(uploadLimit != null){%>
		       'uploadLimit':${uploadLimit},
		       <%}%>
		       <% if(queueSizeLimit != null){%>
		       'queueSizeLimit':${queueSizeLimit},
		       <%}%>
		       <% if(height != null){%>
		       'height':'<%=height%>',
		       <%}%>
		       <% if(width != null){%>
		       'width':'<%=width%>',
		       <%}%>
		       <% if(formData != null){%>
			   'onUploadStart' : function(file) {
				   for(var i = 0 ; i < datas.length ; i++){
				   		params["dto['"+datas[i]+"']"] = Base.getValue(datas[i]);
				   }
				   params["dto['upload_filename']"] = Base.getValue('${id}_filenames');
				   params["dto['upload_filesize']"] = file.size;
	               $("#${id}").uploadify("settings", "formData", params);
		        },
			   <%}%>
			   <% if(onUploadSuccess != null){%>
			    'onUploadSuccess' : ${onUploadSuccess},
			   <%}%>
			    <% if(onUploadError != null){%>
			    'onUploadError' : ${onUploadError},
			   <%}%>
			   'onQueueComplete':function(file){
			   		Base.setValue('${id}_filenames','');
			   },
		        'onSelect' : function(file){
		        	<% if(filenameKey != null){%>
		        	$('#fileQueue_${id}').width($('#${id}_filenames').width() + $('#${id}').width());
		        	<%}else{%>
		        	$('#fileQueue_${id}').width($('#${id}').parent('div').width()-$('#${id}').parent('div').children('label').width() + $('#${id}').width()).css('left',0);
		        	<%}%>
		        	var upload_filenames = Base.getValue('${id}_filenames');
		        	if(upload_filenames != ""){
		        		Base.setValue('${id}_filenames',upload_filenames+','+file.name);
		        	}else{
		        		Base.setValue('${id}_filenames',file.name);
		        	}
		        }
         });
//})

//Ta.core.TaUIManager.register("${id}",grid_${id});
});
//requirejs
}));
</script>