<#-- Html for the Window -->
<div id='${this.id}'>
	<@xvw_facet />
</div>


<@xvw_script position='footer'>
	 		$(function() { $( '#${this.id}' )
	 			.dialog(
	 				{ 'modal' : ${this.modal?string}
	 				  ,'width' : ${this.width}
	 				  ,'height' : ${this.height}
	 				  ,'title' : '${this.title}'
	 				  ,'beforeClose' : 
	 				  	function(event, ui) { 
	 				  		$(this).dialog('destroy').remove();
	 				  		var myId = "${this.requestContext.viewRoot.viewId}:${this.requestContext.viewRoot.instanceId}"; 
	 				  		$(document.getElementById(myId)).remove(); 
	 				  	}
	 				  ,'resize' : 
	 				  	function(event, ui){
	 				  	}
	 				  ,'open' : 
	 				  	function(event, ui){
	 				  	}
	 				 }).parent( ).addClass('xwc-window')
	 				  ;
	 			}); 
</@xvw_script>

<#--




 -->