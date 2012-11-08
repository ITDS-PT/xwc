	<div id='${this.clientId}'>
		<select style='width:100%' id='${this.id}' name='${this.clientId}'>
			<#list this.lovMap?keys as item>
				<#if (this.lovMap[item] == this.value!)>
					<option value='${item}' selected='selected'>${this.lovMap[item]}</option>
				<#else>
					<option value='${item}'>${this.lovMap[item]}</option>	
				</#if>
			</#list>	
		</select>
	</div>

<@xvw_script position='footer'>
	$("#${this.id}")
	<#if this.visible>
		.show()
	<#else>
		.hide()
	</#if>
	
	<#if this.disabled>
		.attr('disabled', 'disabled'); 
	<#else>	
		.removeAttr('disabled');
	</#if>
	
	$("#${this.id}").val('${this.value!""}');
		

</@xvw_script>