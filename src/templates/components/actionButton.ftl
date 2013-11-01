
<#if !this.renderedOnClient>
	<div id='${this.clientId}'>
		<button id='${this.clientId}_btn' class='xwc-actionbutton' style='min-width;${this.width}px'>
			${this.label}
		</button>
	</div>

	<@xvw_script position='footer'>
		 $(function() { 
		 
		 	<#-- Add The Javascript to create the button -->
		 	$( XVW.get('${this.clientId}_btn') ).button().click( function () { ${xvw.js.command(this.getTarget(),this)}; return false; })
		 		<#if this.disabled>
		 			.attr('disabled', 'disabled') 
		 		<#else>	
					.removeAttr('disabled')
				</#if>
				<#if this.visible>
		 			.show() 
		 		<#else>	
					.hide()
				</#if>
		 		;
		 	
		 	<#-- Render the Button ICON --> 
		 	<#if (this.image?length > 0) && (!this.renderedOnClient )>
		 			$( XVW.get('${this.clientId}_btn') )
						.children('.ui-button-text')
						<#if this.iconPosition == 'left'> 
							.prepend( '<img src="${this.image}" style="display:inline;padding:2px;vertical-align:middle" />');   
						<#else>	
							.prepend( '<img src="${this.image}" style="display:block;padding:2px;vertical-align:middle;margin-left:auto; margin-right:auto" />');  
						</#if> 
		 	</#if>		 
		 	
		 	}); 
		 
	</@xvw_script>


<#else>

	<@xvw_script position='footer'>
	$(function() {
		$( XVW.get('${this.clientId}_btn') ).button()
		 		<#if this.disabled>
		 			.attr('disabled', 'disabled') 
		 		<#else>	
					.removeAttr('disabled')
				</#if>
				<#if this.visible>
		 			.show() 
		 		<#else>	
					.hide()
				</#if>
		 		;
	});
	
	
</@xvw_script>

</#if>			
		    	
		    	
		    	
