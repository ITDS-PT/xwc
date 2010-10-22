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

if(ExtXeo.grid.GroupingView) {
	  Ext.apply(ExtXeo.grid.GroupingView.prototype , {
	    emptyGroupText : '(Nenhum)',
	    groupByText    : 'Agrupar por este campo',
	    showGroupsText : 'Mostrar nos Grupos',
	    loadingMsg	   : 'A Carregar...'  	  
	 });
}

if( Ext.grid.GridFilters) {
	  Ext.apply(Ext.grid.GridFilters.prototype , {
		 filtersText: 'Filtros'	  
});
}	  

if( Ext.grid.filter.ObjectFilter) {
	  Ext.apply(Ext.grid.filter.ObjectFilter.prototype , {
		  yesText: 'Seleccionar valores',
		  noText: 'Não'
	 });
}

if( Ext.grid.filter.DateFilter) {
	  Ext.apply(Ext.grid.filter.DateFilter.prototype , {
		  beforeText: 'Antes de',
		  afterText: 'Depois de',
		  onText: 'Em'
	 });
}

if( Ext.grid.filter.BooleanFilter) {
	  Ext.apply(Ext.grid.filter.BooleanFilter.prototype , {
		  yesText: 'Sim'	,
		  noText: 'Não'
	 });
}

if( Ext.grid.filter.ListFilter) {
	  Ext.apply(Ext.grid.filter.ListFilter.prototype , {
		  loadingText: 'Lendo Dados...'
	 });
}

if( ExtXeo.form.NumberField) {
	  Ext.apply(ExtXeo.form.NumberField.prototype , {
		    minText : "O valor minimo para este campo é {0}",
		    maxText : "O valor máximo para este campo é {0}",
		    nanText : "{0} não é um número válido"
	 });
}