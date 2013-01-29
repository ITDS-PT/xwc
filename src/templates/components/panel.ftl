<@xvw_script src='jquery-xeo/xwc-panel.js' id='xwc-panel' />

<div id="${this.clientId}" class='xwc-panel' title='${this.title!}'>
	<@xvw_facet />
</div>
	


 <@xvw_script position='footer' id='c'>
	$(function() {
		$( XVW.get('${this.clientId}') )
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

<#-- $( XVW.get('mainContent:demoEdit:v211_j_id5')).show().collapsiblePanel(false); -->