<@xvw_script src='jquery-xeo/xwc-panel.js' />

<#if !this.renderedOnClient>
	<div id="${this.clientId}">
		<div id="${this.id}" class='xwc-panel' title='${this.title!}'>	
			<@xvw_facet />
		</div>
	</div>	
</#if>

<@xvw_script position='footer'>
	$(function() {
		$( '#${this.id}' )
		<#if this.collapsible>
			.collapsiblePanel(true);
		<#else>
			.collapsiblePanel(false);	
		</#if>
	});
</@xvw_script>