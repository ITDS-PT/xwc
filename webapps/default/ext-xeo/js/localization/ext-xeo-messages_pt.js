Ext.ns('ExtXeo');

ExtXeo.Messages = {
		PROCESSING 		: 'A processar...',
		SENDING_DATA 	: 'A enviar dados, aguarde p.f. ...',
		WELCOME			: 'Bem Vindo',
		TREE_TITLE		: 'Op&ccedil;&otilde;es',
		XEODM_ACTIVE	: "XEODM Activo",
		XEODM_INACTIVE	: "XEODM Inactivo",
		LOGOUT_BTN		: 'Terminar Sess&atilde;o',
		USER_PROPS		: "Utilizador"	
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

if(ExtXeo.grid.GridView) {
	  Ext.apply(ExtXeo.grid.GridView.prototype , {
	    aggregateText   	: 'Resumos',
    	aggregateSumText    : 'Somat&oacute;rio' ,
    	aggregateAvgText    : 'M&eacute;dia' ,
    	aggregateCountText  : 'Total',
        restoreDefsText   	: 'Rep&ocirc;r Defini&ccedil;&otilde;es',
        selectColsText    	: 'Seleccionar Colunas',
        selectCols			: 'Seleccionar colunas',
	    resetDefs	   		: 'Rep&ocirc;r defini&ccedil;&otilde;es'  
	 });
}

if(ExtXeo.grid.ViewGroup) {
	  Ext.apply(ExtXeo.grid.ViewGroup.prototype , {
	    aggregateTbField    : 'Resumo',
	    aggregateTbSumText  : 'Somat&oacute;rio' ,
	    aggregateTbAvgText  : 'M&eacute;dia' ,
	    aggregateTbMinText  : 'M&iacute;nimo',
	    aggregateTbMaxText  : 'M&aacute;ximo'
	 });
}

if(ExtXeo.grid.GroupingView) {
	  Ext.apply(ExtXeo.grid.GroupingView.prototype , {
	    emptyGroupText : '(Nenhum)',
	    groupByText    : 'Agrupar por este campo',
	    showGroupsText : 'Mostrar agrupado',
	    aggregateText  : 'Agregar Valores',
	    removeGroupText: 'Remover grupo',
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
		  noText: 'N&atilde;o'
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
		  noText: 'N&atilde;o'
	 });
}

if( Ext.grid.filter.ListFilter) {
	  Ext.apply(Ext.grid.filter.ListFilter.prototype , {
		  loadingText: 'Lendo Dados...'
	 });
}

if( ExtXeo.form.NumberField) {
	  Ext.apply(ExtXeo.form.NumberField.prototype , {
		    minText : "O valor m&iacute;nimo para este campo &eacute; {0}",
		    maxText : "O valor m&aacute;ximo para este campo &eacute; {0}",
		    nanText : "{0} n&atilde;o &eacute; um n&uacute;mero v&aacute;lido"
	 });
}