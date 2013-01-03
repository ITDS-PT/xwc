
<#if !this.renderedOnClient>
<#-- Toolbar Javascript -->
<@xvw_script position='footer'>
	$(function() {
		<#list this.children as menu>
    		$( '#${menu.id!}_btn' ).button()
				.show()
				<#if menu.disabled>
		 			.attr('disabled', 'disabled')
		 		<#else>
		 			.removeAttr('disabled')	
			 	</#if>
			 	
			 	<#if menu.actionExpression??>
			 		.click(function() { ${XVWScripts.getAjaxCommand(menu)} ;  return false; })
			 	<#else>
			 		.click(function() { return false; })
			 	</#if> 
			 	
			 	<#if menu.visible>
		 			.show();
		 		<#else>
		 			.hide();	
			 	</#if>
			 	
			 	<#-- Draw the icons -->	
				<#if (menu.icon?length > 0) > 
		 			$( '#${menu.id!}_btn' )
						.children('.ui-button-text')
						<#if this.iconPosition == 'left'> 
							.prepend( '<img src="${menu.icon!}" style="display:inline;padding:2px;vertical-align:middle" />');   
						<#else>	
							.prepend( '<img src="${menu.icon!}" style="display:block;padding:2px;vertical-align:middle;margin-left:auto; margin-right:auto" />');  
						</#if> 
		 		</#if> 	
				
		</#list>
	});
</@xvw_script>


<#-- Toolbar HTML -->		    	
<div id='${this.clientId}'>
	<div id='${this.id}' class='ui-widget-header ui-corner-all xwc-toolbar'>
		<#list this.children as menu>
    		<button id='${menu.id!}_btn' class='xwc-toolbar-button'>
    			<span>${menu.text}</span>
    		</button>
		</#list>
	</div>	
</div>

<#else>

	<@xvw_script position='footer'>
	$(function() {
		<#list this.children as menu>
    		$( '#${menu.id}_btn' ).button()
				<#if menu.disabled>
		 			.attr('disabled', 'disabled')
		 		<#else>
		 			.removeAttr('disabled')	
			 	</#if>
				<#if menu.visible>
		 			.show();
		 		<#else>
		 			.hide();	
			 	</#if>
			 	
			 	
			 	
		</#list>
	});
	
	
</@xvw_script>

</#if>			
			
					