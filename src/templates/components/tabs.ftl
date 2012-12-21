<div id='${this.clientId}'>
	<div id='${this.id}' class='xwc-tabs'>
		<ul class="xwc-tab-navbar">
			<#list this.children as tab>
				<li class='xwc-tab-label' >
					<a href='#${tab.id}' id='${tab.id}_link' class='xwc-tab-label-link'>
						${tab.label!}
					</a>
				</li>
			</#list>
	    </ul>
        <#list this.children as tab>
        	<div id='${tab.id}' class='xwc-tab-content'>
        	</div>
        </#list>    
		<@xvw_facet />	        			
	</div>
</div>


<@xvw_script position='footer'>
	 $(function() { 
	 	$( '#${this.id}' ).tabs().tabs("option","selected",${this.activeTabIndex});
		 	<#list this.children as tab>
		 	$("#${tab.id}_link").click(function() {
	  			${XVWScripts.getAjaxCommand(tab)};
	  		});
		 	</#list>	
});
</@xvw_script>