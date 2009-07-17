Ext.ns('ExtXeo');

ExtXeo.Messages = function() {
	this.PROCESSING 	= 'A processar...';
	this.SENDING_DATA 	= 'A enviar dados, aguarde p.f. ...';
	this.WELCOME		= 'Bem Vindo';
	this.TREE_TITLE		= 'Opções';
	this.XEODM_ACTIVE	= "XEODM Activo";
	this.XEODM_INACTIVE	= "XEODM Inactivo";
	this.LOGOUT_BTN		= 'Terminar Sessão';
};

if(ExtXeo.PagingToolbar){
	  Ext.apply(ExtXeo.PagingToolbar.prototype, {
	    beforePageText : "P&aacute;gina",
	    afterPageText  : "de {0} ({1})",
	    firstText      : "Primeira P&aacute;gina",
	    prevText       : "P&aacute;gina Anterior",
	    nextText       : "Pr%oacute;xima P&aacute;gina",
	    lastText       : "&Uacute;ltima P&aacute;gina",
	    refreshText    : "Recaregar",
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

