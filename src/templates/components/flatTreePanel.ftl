<@xvw_script position='header' id='xvw-flattree' >
/**
 * 
 * Shows or Hides a single tree panel menu
 * 
 * */
XVW.showHideMenu = function (elemParent){
	var elem = $(elemParent).next();
	elem.toggle();
	if (elem.hasClass('xwc-tree-panel-highlighted'))
		elem.removeClass('xwc-tree-panel-highlighted');
	else
		elem.addClass('xwc-tree-panel-highlighted');
		
}
</@xvw_script>



<div id='${this.clientId}' style='width:${this.width}' class='xwc-flat-treepanel-container'>
	<#list this.children as menu>
		<ul class='xwc-flat-treepanel-first-level-group'>
			<li class='xwc-flat-treepanel-first-level'>
			<a href='javascript:void(0)' onClick='XVW.showHideMenu(this)'>
				${menu.text}
			</a>
			<ul class='xwc-flat-treepanel-group'>
			<#list menu.children as menu2>
				<li class='xwc-flat-treepanel-menu-option'>
					<a href='javascript:void(0)' onClick='XVW.showHideMenu(this)'>
						${menu2.text}
					</a>
					<ul class='xwc-flat-treepanel-group'>
						<#list menu2.children as menu3>
							<li class='xwc-flat-treepanel-level2-menu'>
								<a href='javascript:void(0)' onClick='XVW.showHideMenu(this)'>
								${menu3.text}</a>		
							</li>
						</#list>
					</ul>
				</li>						
			</#list>								
			</ul>
		</ul>
	</#list>
</div>