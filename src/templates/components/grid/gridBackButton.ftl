<#-- Função Javascript que é o handler do next/back numa Grid -->
function( buttonPressed ) {
	if (buttonPressed !== null && buttonPressed != undefined){
		if (buttonPressed.indexOf("next") > -1){
			XVW.hash.IgnoreHash();
			XVW.hash.AddUrlParameter("gridPage","next");
			XVW.hash.registerCallBack( function () {
				var element = document.getElementById("${this.id}");
				var grid = jQuery(element);
				var previousPage = grid.getGridParam('page') - 1;
				jQuery(element).setGridParam({ page : previousPage}).trigger("reloadGrid"); 
			});	
		}
	}
	
}