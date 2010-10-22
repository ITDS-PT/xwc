Ext.ns('ExtXeo');

ExtXeo.Messages = {
	PROCESSING 		: 'Procesamiento...',
	SENDING_DATA 	: 'El env&iacute;o de datos, espere p.f. ...',
	WELCOME			: 'Bienvenido',
	TREE_TITLE		: 'Opciones',
	XEODM_ACTIVE	: "XEODM Activo",
	XEODM_INACTIVE	: "XEODM Inactivo",
	LOGOUT_BTN		: 'Cerrar sesi&oacute;n'
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

if(ExtXeo.grid.GroupingView) {
	  Ext.apply(ExtXeo.grid.GroupingView.prototype , {
	    emptyGroupText : '(Ninguno)',
	    groupByText    : 'Grupo por este campo',
	    showGroupsText : 'Mostrar en grupos',
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
		  afterText: 'Despu�s de',
		  onText: 'En'
	 });
}

if( Ext.grid.filter.BooleanFilter) {
	  Ext.apply(Ext.grid.filter.BooleanFilter.prototype , {
		  yesText: 'S�'	,
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
		    minText : "El valor m�nimo para este campo es {0}",
		    maxText : "El valor m�ximo para este campo es {0}",
		    nanText : "{0} no es un n�mero v�lido"
	 });
}