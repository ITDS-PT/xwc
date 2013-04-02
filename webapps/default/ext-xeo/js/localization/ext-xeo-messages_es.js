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
		INVITE_MESSAGE 		: 'Arrastre un encabezado de la columna aquí para agrupar por ella'
			
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
	    aggregateText   	: 'Resúmenes',
	    aggregateSumText    : 'Somat&oacute;rio' ,
	    aggregateAvgText    : 'M&eacute;dia' ,
	    aggregateCountText  : 'Total',
	    restoreDefsText   	: 'Rep&ocirc;r Defini&ccedil;&otilde;es',
	    selectColsText    	: 'Seleccionar columnas',
	    selectCols			: 'Seleccionar columnas',
	    resetDefs	   		: 'Restablecer valores predeterminados',
	    showGroupToolBar 	: 'Mostrar las agrupaciones'
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
	    aggregateText  : 'Los valores agregados',
	    removeGroupText: 'Eliminar los grupos',
	    loadingMsg	   : 'Cargando...'  	  
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
		  , containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'	   
	 });
}

if( Ext.grid.filter.DateFilter) {
	  Ext.apply(Ext.grid.filter.DateFilter.prototype , {
		  beforeText: 'Antes de',
		  afterText: 'Despu&eacute;s de',
		  onText: 'En'
		  , containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'	  
	 });
}

if( Ext.grid.filter.BooleanFilter) {
	  Ext.apply(Ext.grid.filter.BooleanFilter.prototype , {
		  yesText: 'S&iacute;'	,
		  noText: 'No'
		  , containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'	  
	 });
}

if( Ext.grid.filter.ListFilter) {
	  Ext.apply(Ext.grid.filter.ListFilter.prototype , {
		  loadingText: 'Lectura de los datos...'
		  , containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'
	 });
}

if( Ext.grid.filter.NumericFilter) {
	  Ext.apply(Ext.grid.filter.NumericFilter.prototype , {
		  containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'	  
	 });
}

if( Ext.grid.filter.StringFilter) {
	  Ext.apply(Ext.grid.filter.StringFilter.prototype , {
		  containsDataText : 'Contiene datos'
		  , notContainsDataText : 'No contiene datos'	  
	 });
}

if( ExtXeo.form.NumberField) {
	  Ext.apply(ExtXeo.form.NumberField.prototype , {
		    minText : "El valor m&iacute;nimo para este campo es {0}",
		    maxText : "El valor m&aacute;ximo para este campo es {0}",
		    nanText : "{0} no es un n&uacute;mero v&aacute;lido"
	 });
}