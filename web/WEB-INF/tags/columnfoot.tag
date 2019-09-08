<%if (jspContext.getAttribute("doColumnLayout") !=null) {%>
</div>

<% if (jspContext.getAttribute("bpopTipMsg") != null){%>
	<script type="text/javascript">
	(function(factory){
		if (typeof require === 'function') {
			require(["jquery","bubblepopup"], factory);
		} else {
			factory(jQuery);
		}
	}(function($){
		$('#${id}').CreateBubblePopup({
			  selectable: false,
			  innerHtml : "${bpopTipMsg}",
			  width : "${bpopTipWidth}",
			  height : "${bpopTipHeight}",
			  position : "${bpopTipPosition}",
			  themePath: 	Base.globvar.contextPath + '/ta/resource/themes/base/bubblepop'
		});
	}));
	</script>
	<%}%>
<%} %>