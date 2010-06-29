
// Set ExtJS Timeout for Ajax.
if( Ext.Ajax )
	Ext.Ajax.timeout = 300000;

ExtXeo = function() {};

XVW.getXApp = function() {
	var oRet = null;
	if( window.XApp ) {
		oRet = XApp;
	}
	else {
		if( window.parent.XApp )
			oRet = window.parent.XApp;
	}
	return oRet;
}

ExtXeo.destroyComponents = function( oDNode, oWnd ){
	var x = new Date();
	try {
		ExtXeo.destroyComponents1( oDNode, oWnd );
    	oDNode.innerHTML="";
	} catch( e ) {};
}

ExtXeo.destroyComponents1 = function( oDNode, oWnd ){
    if( !oWnd ) oWnd = window;

    if( "FORM" == oDNode.tagName ) {
    	XVW.disposeView( oDNode, true ); 
    }
    
    if( "IFRAME" == oDNode.tagName ) {
    	var xWnd = oDNode.contentWindow;
    	if( xWnd != null ) {
    		ExtXeo.destroyComponents( xWnd.document.body, xWnd );
    	}
    }
    
    var oDNodeChilds = oDNode.childNodes;
    for( var i=0;i<oDNodeChilds.length; i++ ) {
        ExtXeo.destroyComponents1( oDNodeChilds[i], oWnd );
    }

    try {
	    var oExtComp = oWnd.Ext.ComponentMgr.get( oDNode.id );
	    if( oExtComp != null ) {
	        oExtComp.destroy();
	    }
    } catch( e ) {}
}

XVW.ErrorDialog = function( sTitle, sMessage, sDetails ) {
    Ext.MessageBox.show({
       title: sTitle,
       icon: Ext.MessageBox.ERROR,
       msg: sMessage,
       fn: function( btn, text ) {
    		if (btn=='cancel') {
    			var wnd = new Ext.Window( {
        			title: sTitle,
    				layout:'fit',
    				plain: true,
    				modal: true,
    				height: 300,
    				width: 500,
    				autoScroll : true,
    				closable: true,
    				html: '<b color:red>' + sMessage + '</b><br/><pre>' + sDetails + '</pre>' 
    			} );
    			wnd.show();
    		}
       },
       buttons: {ok:'OK', cancel:'Details'},
       icon: 'error'
   });
}


XVW.beforeApplyHtml = function( oDNode, destroyComponent ) {
	if( destroyComponent ) {
		ExtXeo.destroyComponents( oDNode );
	}
}

XVW.Wait = function( iWaitMode ) {
    if( iWaitMode == '1' )
    {
    	if( Ext.isIE ) {
    		// IE Crashes with loadMask
            Ext.MessageBox.show({
                title: ExtXeo.Messages.PROCESSING + '      ',
                msg: ExtXeo.Messages.SENDING_DATA,
                width:300,
                wait:true,
                waitConfig: {interval:200},
                icon:'ext-mb-download', //custom class in msg-box.html
                animEl: 'mb7'
            });
    	}
    	else {
	    	if( !ExtXeo.loadMask ) {
	    		ExtXeo.loadMask = new Ext.LoadMask(document.body, {msg: ExtXeo.Messages.SENDING_DATA });
	    	}
	    	ExtXeo.loadMask.show();
    	}
    }
}


XVW.NoWait = function() { 
	if( Ext.isIE ) {
		if( Ext.MessageBox.getDialog().title == ExtXeo.Messages.PROCESSING + '      ' ) {
			Ext.MessageBox.hide();
		}
	} else {
		if( ExtXeo.loadMask ) {
			window.setTimeout( "ExtXeo.loadMask.hide();", 50 );
		}
	}
}

XVW.openCommandTab = function( sFrameName, sFormId, sActionId, sActionValue, sTabTitle ) {
    // Create new Tab
	if( XVW.getXApp() != null ) {
		var tab = XVW.findTabByFrameName( sFrameName );
		if( tab != null ) {
			var XApp = XVW.getXApp();
			XApp.desktop.tabPanel.setActiveTab( tab );
			return;
		}
	    this.openTab( sFrameName, sTabTitle );
	    // Submit the form to the new tab
	    var oForm = document.getElementById( sFormId );
	    
	    var sOldTarget = oForm.target;
	    oForm.target = sFrameName;
	    XVW.Command( sFormId, sActionId, sActionValue );
	    oForm.target = sOldTarget;
//	    XVW.AjaxCommand( sFormId, sActionId, sActionValue );
	}
	else {
		// Redirect the page to complete the action if the viewer is not integrated with an application
	    XVW.Command( sFormId, sActionId, sActionValue );
	}	
}

//var tabdividx = 0;

XVW.openTab = function( sFrameName, sTitle ) {
    var tabs;
    var XApp = XVW.getXApp();
    if( XApp != null ) {
	    tabs = XApp.desktop.tabPanel;
	    var fnPanel = window.XApp?Ext.Panel:window.parent.Ext.Panel;
	    var formLayout = new fnPanel(  
	        {  
	            title: sTitle?sTitle:'&nbsp;',
	            border:true,
	            frame:false,
	            closable: true,
	            style: "overflow:visible;",
	            html: '<iframe name="'+sFrameName+'" src="about:blank" scrolling="no" frameBorder="0" width="100%" height="'+(Ext.isChrome?'99%':'100%' )+'"></iframe>'
	        }
	    );
	    var tab  = tabs.add( formLayout );
	    tab.show();
	    tab.doLayout();
	    tabs.syncSize();
	    XApp.desktop.syncSize();
	    tab = null;
	    tabs = null;
	    formLayout = null;
    }
}


XVW.OpenCommandWindow = function( sFrameName, sFormId, sActionId, sActionValue ){
    sFrameName += (new Date() - 1)
    var win = new Ext.Window({
        layout:'fit',
        width:500,
        height:300,
        closeAction:'hide', 
        plain: true,
        html: '<iframe name="'+sFrameName+'" src="about:blank" frameBorder="0" width="100%" height="100%"></iframe>'
    });
    win.show(this);

    // Submit the form to the new Window
    var oForm = document.getElementById( sFormId );
    var sOldTarget = oForm.target;
    oForm.target = sFrameName;
    XVW.Command( sFormId, sActionId, sActionValue );
    oForm.target = sOldTarget;
    XVW.lookupWindow = win;

};

ExtXeo.layoutMan = function() {};

ExtXeo.layoutMan.comp = {};

ExtXeo.layoutMan.register = function( sViewId, sDomId, sLayoutType ) {
	
	var compArray = ExtXeo.layoutMan.comp[ sViewId ];

	if ( !compArray ) {
		compArray = {};
		compArray = ExtXeo.layoutMan.comp[ sViewId ] = [];
	}
	compArray[ sDomId ] = sLayoutType;
}

ExtXeo.layoutMan.unRegister = function( sDomId ) {
	var compArray = ExtXeo.layoutMan.comp;
	delete compArray[ sDomId ];
}

ExtXeo.layoutMan.layoutTimeoutId = null;
ExtXeo.layoutMan.doLayout = function( sViewId ) {
	//window.setTimeout("ExtXeo.layoutMan.doLayout1();",0);
	var x = new Date();
	if ( "__allViews" ==  sViewId ) {
		// Do Layout for all views
		ExtXeo.layoutMan.layoutTimeoutId = null;		
		for( var v in ExtXeo.layoutMan.comp ) {
			ExtXeo.layoutMan.doLayout1( v );
		}
	}
	else if( sViewId ) {
		ExtXeo.layoutMan.doLayout1( sViewId )
	}
	else {
		if( ExtXeo.layoutMan.layoutTimeoutId  != null )
			window.clearTimeout( ExtXeo.layoutMan.layoutTimeoutId  );
		ExtXeo.layoutMan.layoutTimeoutId = window.setTimeout("ExtXeo.layoutMan.doLayout('__allViews');", 100);
	}
}

ExtXeo.layoutMan.doLayout1 = function( sViewId )
{
	var compArray = ExtXeo.layoutMan.comp[ sViewId ];
	var sId;
	var oLayouts = new Array(
			['fit-window',ExtXeo.layoutMan.doFitWindow],
			['fit-parent',ExtXeo.layoutMan.doFitParent],
			['form',ExtXeo.layoutMan.doForm]
	);
	
	for( sId in compArray )	{
		var oElem = document.getElementById( sId );
		if( compArray[ sId ] == 'fit-window' && oElem != null ) {
			oElem.style.visibility='';
		}
	}

	var fnLMan;
	for ( var i = 0; i < oLayouts.length; i++) {
		fnLMan = oLayouts[i][1];
		for( sId in compArray )	{
			if( compArray[ sId ] == oLayouts[i][0] ) {
				var oElem = document.getElementById( sId );
				if( oElem != null ) {
					fnLMan( oElem );
				}
			}
		}
	}

}

ExtXeo.layoutMan.doFitParent = function( oElem )
{
	var x;
	var pcont;
	var xoffSet = 0;//oElem.offsetTop+(oElem.clientTop*2);
	var loffParent = null;//oElem.offsetParent;
	var lastValidClientHeight = 0;
	
//	if( Ext.isIE && !Ext.isIE8 ) {
//		pcont = oElem.parentNode;
//	} else {
		//pcont = loffParent;
		pcont = oElem.parentNode;
	 
//	}
	
	if( oElem.currentStyle ) {
		x = parseInt(oElem.currentStyle.paddingTop);
		if( !isNaN( x ) ) {
			xoffSet += x;
		}
		x = parseInt(oElem.currentStyle.paddingBottom);
		if( !isNaN( x ) ) {
			xoffSet += x;
		}
		
		x = parseInt(oElem.currentStyle.marginTop);
		if( !isNaN( x ) ) {
			xoffSet += x;
		}
		x = parseInt(oElem.currentStyle.marginBottom);
		if( !isNaN( x ) ) {
			xoffSet += x;
		}
	} else if (window.getComputedStyle) {
		
		x = parseInt(window.getComputedStyle( oElem, null).paddingTop);
		if( !isNaN( x ) ) {
			xoffSet -= x;
		}
		x = parseInt(window.getComputedStyle( oElem, null).paddingBottom);
		if( !isNaN( x ) ) {
			xoffSet -= x;
		}
		
		x = parseInt(window.getComputedStyle( oElem, null).marginTop);
		if( !isNaN( x ) ) {
			xoffSet -= x;
		}
		x = parseInt(window.getComputedStyle( oElem, null).marginBottom);
		if( !isNaN( x ) ) {
			xoffSet -= x;
		}
		
	}
	
	while( pcont != null ) {
		if( pcont.currentStyle ) {
			
			x = parseInt(pcont.currentStyle.paddingTop);
			if( !isNaN( x ) ) {
				xoffSet += x;
			}
			
			x = parseInt(pcont.currentStyle.paddingBottom);
			if( !isNaN( x ) ) {
				xoffSet += x;
			}
			
			x = parseInt(pcont.currentStyle.marginTop);
			if( !isNaN( x ) ) {
				xoffSet += x;
			}
			x = parseInt(pcont.currentStyle.marginBottom);
			if( !isNaN( x ) ) {
				xoffSet += x;
			}
		} else if( window.getComputedStyle ) {
			
			try {
				x = parseInt(window.getComputedStyle( pcont, null).paddingTop);
				if( !isNaN( x ) ) {
					xoffSet -= x;
				}
				x = parseInt(window.getComputedStyle( pcont, null).paddingBottom);
				if( !isNaN( x ) ) {
					xoffSet -= x;
				}
				
				x = parseInt(window.getComputedStyle( pcont, null).marginTop);
				if( !isNaN( x ) ) {
					xoffSet -= x;
				}
				x = parseInt(window.getComputedStyle( pcont, null).marginBottom);
				if( !isNaN( x ) ) {
					xoffSet -= x;
				}
			} catch(e){}
			
		}

		//		if( pcont.clientTop )
//			xoffSet += pcont.clientTop;
		if( pcont.className && pcont.className.indexOf("x-panel") != -1 ) 
			break;
		else if ( typeof (pcont.offsetTop) != 'undefined' && pcont.offsetTop != 0 && loffParent != pcont.offsetParent ) {
			xoffSet += pcont.offsetTop+(pcont.clientTop * 2);
			loffParent = pcont.offsetParent;
		}
		pcont = pcont.parentNode;
	} 

	if( !xvw_isPortal ) {
		if( pcont == null ) {
			pcont=document.body;
			if( xoffSet == 0 ) {
				xoffSet += oElem.offsetTop+(oElem.clientTop*2);
			}
		}
	}
	
	if( pcont != null ) {
		lastValidClientHeight = pcont.clientHeight;

		var c = Ext.ComponentMgr.get( oElem.id );
		var height = lastValidClientHeight - (xoffSet + 2 );
		if( height < 0 ) {
			height=0;
		}
		if( c != null && c.getMinHeight ) {
			height = height < c.getMinHeight()?c.getMinHeight():height;
		}
//		alert( oElem.id + " => " + height  );
		oElem.style.height = height + "px";
		if( c != null ) {
			c.setHeight( height );
		}
	}
	else {
//		if( Ext.isIE ) {
			oElem.style.height = (oElem.offsetHeight + oElem.offsetTop + 2) + "px";
//		}
	}
}

ExtXeo.layoutMan.doFitWindow = function( oElem )
{
	//Portal... doest have fit
	
	if( !xvw_isPortal ) {
		var d = document.documentElement;
		var iOffSet = ExtXeo.getAbsoulteOffsetTop( oElem );
		var iNewH = d.clientHeight - iOffSet - 15;
		if( iNewH > 0 )
		oElem.style.height = iNewH + "px";
	}
}

ExtXeo.layoutMan.doForm = function( oElem )
{
	var c;
	c = Ext.ComponentMgr.get( oElem.id );
	if( c != null && c.setWidth ) {
		ExtXeo.resizeLayoutExtComp( oElem, c );
	}
	else {
		var oc = oElem.childNodes;
		for ( var i = 0; i < oc.length; i++) {
			ExtXeo.layoutMan.doForm( oc[i] );
		}
	}
}

ExtXeo.resizeLayoutExtComp = function( oElem, c ) {
	var x=oElem.parentNode;
	
	while( x != null && x.tagName != 'TD' && x.className.indexOf('xwc-rows-row') == -1  )
		x = x.parentNode;
	if( c.lastSize && c.lastSize.width != c.width ) 
		c.lastSize = { width:0, height:0 };
	c.setWidth( x.clientWidth );

}

ExtXeo.getAbsoulteOffsetTop = function( o ) {
	var t = 0;
	if( o.offsetParent != null && o.offsetParent.offsetTop ) {
		t += ExtXeo.getAbsoulteOffsetTop( o.offsetParent );
	}
	t += o.offsetTop;
	return t;
}

window.onresize = function() {ExtXeo.layoutMan.doLayout() };

Ext.ns('ExtXeo','ExtXeo.comp');

ExtXeo.comp.BoxComponent = function( config ) {
	this.initialConfig = config;
}
ExtXeo.comp.BoxComponent.prototype.syncSize = function() {
	return;
}

XVW.setTitle = function( sTitle ) {
	if( XVW.getXApp() != null ) {
		var tabs = XVW.getXApp().desktop.tabPanel;
		var tabItems = tabs.items;
	    for ( var i = 0; i < tabItems.getCount(); i++) {
	    	var oFrames = tabItems.get(i).el.dom.getElementsByTagName('iframe');
	    	if( oFrames.length > 0 ) {
	    		for ( var k = 0; k < oFrames.length; k++) {
					if( oFrames[k].contentWindow == window ) {
						tabItems.get(i).setTitle( "<div title='"+sTitle.replace(/'/g,"")+"' >" + sTitle + "</div>" );
						return;
					}
				}
	    	}
		}
	}
}

XVW.closeView = function( sId ) {
	var xapp = XVW.getXApp();
	if( xapp != null ) {
		var tabs = xapp.desktop.tabPanel;
		var tabItems = tabs.items;
	    for ( var i = 0; i < tabItems.getCount(); i++) {
	    	var oFrames = tabItems.get(i).el.dom.getElementsByTagName('iframe');
	    	if( oFrames.length > 0 ) {
	    		for ( var k = 0; k < oFrames.length; k++) {
					if( oFrames[k].contentWindow == window ) {
						tabItems.get(i).forceClose = true;
						tabs.remove( tabItems.get(i) );
						return;
					}
				}
	    	}
		}
	}
}



XVW.downloadFile = function( sUrl ) {
	var iFrame = document.createElement("iframe");
	iFrame.style.display='none';
	document.body.appendChild(iFrame);
//	iFrame.onreadystatechange=function(){ try { alert( document.readyState ); iFrame.parentNode.removeChild( iFrame )  }catch(e) { } };
	iFrame.src = sUrl;
}

XVW.CommandDownloadFrame = function( sFrameName, sFormId, sActionId, sActionValue ){
    sFrameName += (new Date() - 1)
    //html: '<iframe name="'+sFrameName+'" src="about:blank" frameBorder="0" width="100%" height="100%"></iframe>'
    var iFrame = document.createElement("iframe");
    iFrame.style.display='none';
    iFrame.name = sFrameName;
    document.body.appendChild(iFrame);
    iFrame.src = "about:blank";

    // Submit the form to the new Window
    var oForm = document.getElementById( sFormId );
    var sOldTarget = oForm.target;
    oForm.target = sFrameName;
    oForm.action =  XVW.prv.getFormInput( oForm, 'xvw.ajax.submitUrl').value;
    XVW.Command( sFormId, sActionId, sActionValue );
    oForm.target = sOldTarget;
};


XVW.syncView = function( sFormId, iWaitScreen ) {
	var wnd = XVW.findFormWindow( sFormId );
	if( wnd ) {
		wnd.XVW.AjaxCommand( sFormId, null, null, iWaitScreen, false );
	}
}

XVW.closeWindow = function( sFormId, sWindowId ) {
	var formWnd = window;
	var wnd = Ext.ComponentMgr.get( sWindowId );
	if( wnd == null ) {
		formWnd = window.parent;
		if( formWnd && formWnd.Ext ) {
			wnd = formWnd.Ext.ComponentMgr.get( sWindowId );
		}
	}
	
	if( wnd == null ) {
		formWnd = XVW.findFormWindow( sFormId );
		if( formWnd && formWnd.Ext ) {
			wnd = formWnd.Ext.ComponentMgr.get( sWindowId );
		}
	}
	if( wnd ) {
		wnd.hide();
		wnd.destroy();
	}
}


XVW.findFormWindow = function ( sFormId ) {
	var ret;
	var form = document.getElementById( sFormId );
	if( form != null )  {
		ret = window;
	} else if( XVW.getXApp() != null ) {
	 	var tabs = XVW.getXApp().desktop.tabPanel; 
		var tabItems = tabs.items;
	    for ( var i = 0; i < tabItems.getCount(); i++) {
	    	var oFrames = tabItems.get(i).el.dom.getElementsByTagName('iframe');
	    	if( oFrames.length > 0 ) {
	    		for ( var k = 0; k < oFrames.length; k++) {
	    			var frame = oFrames[k];
	    			if( frame ) {
	    				form = frame.contentWindow.document.getElementById( sFormId );
	    				if( form != null ) {
	    					ret = frame.contentWindow;
	    					break;
	    				}
					}
				}
	    	}
	    	if( ret ) {
	    		break;
	    	}
		}
	}
	return ret;
}


XVW.findTabByFrameName = function ( sFrameName ) {
	if( XVW.getXApp() != null ) {
	 	var tabs = XVW.getXApp().desktop.tabPanel; 
		var tabItems = tabs.items;
	    for ( var i = 0; i < tabItems.getCount(); i++) {
	    	var oFrames = tabItems.get(i).el.dom.getElementsByTagName('iframe');
	    	if( oFrames.length > 0 ) {
	    		for ( var k = 0; k < oFrames.length; k++) {
					if( oFrames[k].name == sFrameName ) {
						return tabItems.get(i);
					}
				}
	    	}
		}
	}
	return null;
}




//Localizacao
/*

Date.monthNames =
 ["Janeiro",
  "Fevereiro",
  "Março",
  "Abril",
  "Maio",
  "Junho",
  "Julho",
  "Agosto",
  "Setembro",
  "Outubro",
  "Novembro",
  "Dezembro"];

Date.dayNames =
 ["Domingo",
  "Segunda",
  "Terça",
  "Quarta",
  "Quinta",
  "Sexta",
  "Sabado",
  ];
  
Ext.apply(Ext.grid.GridView.prototype, {
sortAscText: "Ordem ascendente",
sortDescText: "Ordem descendente",
lockText: "Bloquear Coluna",
unlockText: "Desbloquear Coluna",
columnsText: "Colunas"
});

Ext.apply(Ext.DatePicker.prototype, {
  todayText : "Hoje",
  todayTip : "{0} (barra de espaçõs)",
  minText : "Data máxima.",
  maxText : "Data minima.",
  format : "d/m/y",
  disabledDaysText : "",
  disabledDatesText : "",
  monthNames : Date.monthNames,
  dayNames : Date.dayNames,
  nextText: "Próximo Mês (Ctrl + Seta para a direita)",
  prevText: "Mês Anterior (Ctrl + Seta para a esquerda)",
  monthYearText: "Escolha de Ano (Ctrl + Para cima/Para baixo para mudar o ano)",
  startDay: 1 // Week start on Monday
});

Ext.apply(Ext.form.DateField.prototype, {
  format: "d/m/y"
});

Ext.MessageBox.buttonText = {
  ok : "OK",
  cancel : "Cancelar",
  yes : "Sim",
  no : "Não" 
};

Ext.apply(Ext.PagingToolbar.prototype, {
  beforePageText : "Página",
  afterPageText : "de {0}",
  firstText : "Primeria Página",
  prevText : "Página Anterior",
  nextText : "Página Seguinte",
  lastText : "Última Página",
  refreshText : "Actualizar"
});

Ext.apply(Ext.form.ComboBox.prototype, {
  loadingText: "A Carregar..."
});

*/

//Disable back
window.history.forward(1);

XVW.MenuCounter = {
	counters : {},
	registerCounter : function( sUrl, sContId, sNodeId, interval ) {
		this.counters[sNodeId] = { url: sUrl, containerId: sContId, nodeId: sNodeId, interval: interval, lastUpdate: (new Date())-0 };
		window.setInterval( function() { XVW.MenuCounter.updateCounter( sNodeId, interval ) }, interval*1000 )
	},
	updateCounter : function( sNodeId, loop ) {
		var x = null;
		x = this.counters[sNodeId];
		if( !x ) {
			if( !loop && window.parent.XVW.MenuCounter )
				x = window.parent.XVW.MenuCounter.updateCounter( sNodeId, true )
		} else {
			var x = this.counters[sNodeId];
			var xh = XVW.createXMLHttpRequest();
			xh.open( 'GET',x.url, true );
			xh.onreadystatechange = function() {
				if( xh.readyState==4 ) {
					eval( 'var r = ' + xh.responseText);
					var c = Ext.getCmp( x.containerId );
					if(c) {
						var n = c.getNodeById( x.nodeId );
						if(n) n.setText( r.counterHtml );
		    		}
					x.lastUpdate = (new Date())-0;
				}
			}
			xh.send();
		}
	},
	updateCounters : function( loop ) {
		for( x in this.counters ) {
			XVW.MenuCounter.updateCounter( x );
		}
		if( !loop )
			if( window.parent.XVW.MenuCounter ) {
				window.parent.XVW.MenuCounter.updateCounters( true );
			}
	}
}