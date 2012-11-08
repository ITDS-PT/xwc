<@xvw_script src='jquery-xeo/xwc-panel.js' id='xwc-panel' />

<div id="${this.clientId}">
	<div id="${this.id}" class='xwc-panel' title='${this.title!}'>	
		<@xvw_facet />
	</div>
</div>	


<@xvw_script position='footer'>
	$(function() {
		$( '#${this.id}' )
		<#if this.visible>
		.show()
		<#else>
		.hide()
		</#if>
		<#if this.collapsible>
			.collapsiblePanel(true);
		<#else>
			.collapsiblePanel(false);	
		</#if>
	});
</@xvw_script>