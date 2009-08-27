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
	    lastText       : "&Uacute;ltima Página",
	    refreshText    : "Recarga..",
	    displayMsg     : "Listado {0} - {1} de {2}",
	    emptyMsg       : 'No hay datos para mostrar'
	  });
	}

if(ExtXeo.grid.GroupingView){
	  Ext.apply(ExtXeo.grid.GroupingView , {
	    emptyGroupText : '(Ninguno)',
	    groupByText    : 'Grupo por este campo',
	    showGroupsText : 'Mostrar en grupos',
	    loadingMsg	   : 'Una carga...'  	  
	 });
}

