<#-- Html for the Window -->
<div id='${this.clientId}'>
	<@xvw_facet />
</div>


<@xvw_script position='footer'>
	 		$(function() { $( XVW.get('${this.clientId}') )
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
	 			
	 			<#-- Layout required for GridPanels -->
	 			ExtXeo.layoutMan.doLayout('${this.requestContext.viewRoot.clientId}'); 
</@xvw_script>