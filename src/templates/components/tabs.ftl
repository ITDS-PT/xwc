<div id='${this.clientId}' class='xwc-tabs'>
		<#if this.renderTabBar>
			<ul class="xwc-tab-navbar">
		<#else>
			<ul class="xwc-tab-navbar" style='display:none'>
		</#if>	
			<#list this.children as tab>
				<li class='xwc-tab-label' >
					<a href='#${tab.clientId}' id='${tab.clientId}_link' class='xwc-tab-label-link'>
						${tab.label!}
					</a>
				</li>
			</#list>
	    </ul>
	    
        <#list this.children as tab>
    	<div id='${tab.clientId}' class='xwc-tab-content'>
    		<#if this.activeTab = tab.id>
				<@xvw_facet />	        				
    		</#if>
    	</div>
        </#list>    
</div>


<@xvw_script position='footer'>
	  $(function() { 
	 	$( XVW.get('${this.clientId}') ).tabs();
		
		$( XVW.get('${this.clientId}') ).tabs("option","selected",${this.activeTabIndex}); 
		<#if this.renderTabBar>
		 	<#list this.children as tab>
			 	$( XVW.get("${tab.id}_link") ).click(function() { ${xvw.js.ajaxCommand(tab)}; });
		 	</#list>
		</#if> 	 	
}); 
</@xvw_script>