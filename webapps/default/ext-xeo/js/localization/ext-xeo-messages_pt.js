Ext.ns('ExtXeo');

ExtXeo.Messages = {
		PROCESSING 		: 'A processar...',
		SENDING_DATA 	: 'A enviar dados, aguarde p.f. ...',
		WELCOME			: 'Bem Vindo',
		TREE_TITLE		: 'Op&ccedil;&otilde;es',
		XEODM_ACTIVE	: "XEODM Activo",
		XEODM_INACTIVE	: "XEODM Inactivo",
		LOGOUT_BTN		: 'Terminar Sess&atilde;o'
};

if(ExtXeo.PagingToolbar){
	  Ext.apply(ExtXeo.PagingToolbar.prototype, {
	    beforePageText : "P&aacute;gina",
	    afterPageText  : "de {0} ({1})",
	    firstText      : "Primeira P&aacute;gina",
	    prevText       : "P&aacute;gina Anterior",
	    nextText       : "Pr&oacute;xima P&aacute;gina",
	    lastText       : "&Uacute;ltima P&aacute;gina",
	    refreshText    : "Recarregar",
	    displayMsg     : "A mostrar {0} - {1} de {2}",
	    emptyMsg       : 'Sem dados para mostrar'
	  });
	}

if(ExtXeo.grid.GroupingView){
	  Ext.apply(ExtXeo.grid.GroupingView , {
	    emptyGroupText : '(Nenhum)',
	    groupByText    : 'Agrupar por este campo',
	    showGroupsText : 'Mostrar nos Grupos',
	    loadingMsg	   : 'A Carregar...'  	  
	 });
}

