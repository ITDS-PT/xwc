Ext.ns('ExtXeo');

ExtXeo.Messages = {
	PROCESSING 		: 'Procesamiento...',
	SENDING_DATA 	: 'El env&iacute;o de datos, espere p.f. ...',
	WELCOME			: 'Bienvenido',
	TREE_TITLE		: 'Opciones',
	XEODM_ACTIVE	: "XEODM Activo",
	XEODM_INACTIVE	: "XEODM Inactivo",
	LOGOUT_BTN		: 'Cerrar sesi&oacute;n',
	USER_PROPS		: 'Idioma'	
};

ExtXeo.GridMessages = {
		INVITE_MESSAGE 		: 'Arraste o cabeçalho de uma coluna para aqui para agrupar por ela'
			
};

if(ExtXeo.PagingToolbar){
	  Ext.apply(ExtXeo.PagingToolbar.prototype, {
	    beforePageText : "P&aacute;gina",
	    afterPageText  : "de {0} ({1})",
	    firstText      : "Primera p&aacute;gina",
	    prevText       : "P&aacute;gina anterior",
	    nextText       : "P&aacute;gina siguiente",
	    lastText       : "&Uacute;ltima P�gina",
	    refreshText    : "Recarga..",
	    displayMsg     : "Listado {0} - {1} de {2}",
	    emptyMsg       : 'No hay datos para mostrar'
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
	    resetDefs	   		: 'Rep&ocirc;r definiciones' 
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
	    emptyGroupText : '(Ninguno)',
	    groupByText    : 'Grupo por este campo',
	    showGroupsText : 'Mostrar agrupado',
	    aggregateText  : 'Agregar Valores',
	    removeGroupText: 'Remover grupo',
	    loadingMsg	   : 'Una carga...'  	  
	 });
}	

if( Ext.grid.GridFilters) {
		  Ext.apply(Ext.grid.GridFilters.prototype , {
			 filtersText: 'Filtros'	  
	 });
}	  

if( Ext.grid.filter.ObjectFilter) {
	  Ext.apply(Ext.grid.filter.ObjectFilter.prototype , {
		  yesText: 'Seleccione los valores',
		  noText: 'No'
	 });
}

if( Ext.grid.filter.DateFilter) {
	  Ext.apply(Ext.grid.filter.DateFilter.prototype , {
		  beforeText: 'Antes de',
		  afterText: 'Despu&eacute;s de',
		  onText: 'En'
	 });
}

if( Ext.grid.filter.BooleanFilter) {
	  Ext.apply(Ext.grid.filter.BooleanFilter.prototype , {
		  yesText: 'S&iacute;'	,
		  noText: 'No'
	 });
}

if( Ext.grid.filter.ListFilter) {
	  Ext.apply(Ext.grid.filter.ListFilter.prototype , {
		  loadingText: 'Lectura de los datos...'
	 });
}


if( ExtXeo.form.NumberField) {
	  Ext.apply(ExtXeo.form.NumberField.prototype , {
		    minText : "El valor m&iacute;nimo para este campo es {0}",
		    maxText : "El valor m&aacute;ximo para este campo es {0}",
		    nanText : "{0} no es un n&uacute;mero v&aacute;lido"
	 });
}