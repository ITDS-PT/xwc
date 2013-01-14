<div id='${this.clientId}'>
	<fieldset id='${this.id}' class='ui-widget ui-widget-content xwc-section'>
		<#if (this.label??)> 
		<legend style='ui-widget-header ui-corner-all xwc-legend'>
			${this.label!""}
		</legend>
		<#else>
		<legend class='ui-widget-header ui-corner-all xwc-legend-empty' />
		</#if>
		<@xvw_facet />
	</fieldset>
</div>

<@xvw_script position='footer'>
	 
	 	$('#${this.id}')
	 	<#if this.visible> 
	 		.show();
	 	<#else>	
	 		.hide();
	 	</#if>
	 	
</@xvw_script> 
