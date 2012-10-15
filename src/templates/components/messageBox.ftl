<#if !this.renderedOnClient>

<#--  Criar variável com o tipo de -->

<div id='${this.id}' title='${this.title}' class='xwc-messagebox' style='display:none'>
	<p class='xwc-messagebox-text'>
		<span style='float:left; margin:0 7px 20px 0' 
			<#switch this.messageBoxType>
				<#case "ERROR">
					class='xwc-messagebox-icon ui-icon ui-icon-alert'>
				<#break>	
				<#case "INFO">
					class='xwc-messagebox-icon ui-icon ui-icon-info'>
				<#break>
				<#case "QUESTIOn">
					class='xwc-messagebox-icon ui-icon ui-icon-help'>
				<#break>
				<#case "WARNING">
					class='xwc-messagebox-icon ui-icon ui-icon-notice'>
				<#break>
			</#switch>
		</span>
		${this.message}
	</p>	
</div>
</#if>	

<#if this.showMessageBox>
	<@xvw_script position='footer'>
	 $(function() { 
	 	$( '#${this.id}' )
	 		.dialog({
	 		'height' : 140
	 		,'modal' : true
	 		,'resizable' : true
	 		,'buttons' : 
	 			{
	 			<#list this.children as menu>
	 				'${menu.text}' : function() { ${XVWScripts.getAjaxCommand(menu)}; $(this).dialog('destroy');}
	 				<#if menu_has_next>
	 				,
	 				</#if> 	
	 			</#list> 
	 		}})
	 		.parent( ).addClass('xwc-messagebox-window');
	 	});
	</@xvw_script> 	
</#if>			
					