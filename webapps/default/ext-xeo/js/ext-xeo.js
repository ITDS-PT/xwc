// Set ExtJS Timeout for Ajax.
if( Ext.Ajax )
	Ext.Ajax.timeout = 300000;

ExtXeo = function() {};
ExtXeo.frameLess = false;

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
};

ExtXeo.destroyComponents = function( oDNode, oWnd ){
	var x = new Date();
	try {
		ExtXeo.destroyComponents1( oDNode, oWnd );
		if( oDNode )
			oDNode.innerHTML="";
	} catch( e ) {}
};

ExtXeo.destroyComponents1 = function( oDNode, oWnd ) {

    if( !oDNode )
    	return;
    
    if( !oWnd ) 
    	oWnd = window;
    	
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
};

XVW.ErrorDialog = function( sTitle, sMessage, sDetails ) {
	var buttonsDefinition = {ok:'OK', cancel:'Details'};
	if (sDetails === undefined || sDetails == null || sDetails.length == 0){
		buttonsDefinition = {ok:'OK'};
	} 
	
    Ext.MessageBox.show({
       title: sTitle,
       icon: Ext.MessageBox.ERROR,
       msg: sMessage,
       fn: function( btn, text ) {
    		if (btn=='cancel'  ) {
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
       buttons: buttonsDefinition,
       icon: 'error'
   });
};

XVW.ConfirmationDialog = function (sTitle, sMessage, okButtonHandler){
	if (sTitle && sMessage){
		Ext.MessageBox.alert(sTitle,sMessage,okButtonHandler);
	}
};


XVW.beforeApplyHtml = function( oDNode, destroyComponent ) {
	if( destroyComponent ) {
		ExtXeo.destroyComponents( oDNode );
	}
};

ExtXeo.WaitCounter = 0;

XVW.Wait = function( iWaitMode ) {
    if( iWaitMode == '1' )
    {
    	if( !ExtXeo.loadMask ) {
    		ExtXeo.loadMask = new Ext.LoadMask(document.body, {msg: ExtXeo.Messages.SENDING_DATA });
    	}
    	ExtXeo.WaitCounter++;
    	ExtXeo.loadMask.show();
    }
};

XVW.StillWorking = function(message){
	if( ExtXeo.loadMask ) {
		ExtXeo.loadMask.hide();
	}
	ExtXeo.WaitCounter = 0;
	ExtXeo.loadMask = new Ext.LoadMask(document.body, {msg: message });
	ExtXeo.loadMask.show();
};

XVW.ResetWait = function () {
	ExtXeo.WaitCounter = 0;
	window.setTimeout( function () {
		if( ExtXeo.loadMask ) {
			ExtXeo.loadMask = null;
		}
	} , 50 );
	
};


XVW.NoWait = function() { 
	if( ExtXeo.loadMask ) {
		if (ExtXeo.WaitCounter > 0){
			ExtXeo.WaitCounter--;
		} 
		if (ExtXeo.WaitCounter == 0){
			window.setTimeout( "ExtXeo.loadMask.hide();", 50 );
		}
	}
};

XVW.openViewOnElement = function( sFormId, sActionId, sActionValue, renderOnElementId ) {
	XVW.AjaxCommand( sFormId, sActionId, sActionValue, '0', true, document.getElementById( renderOnElementId ) );
};

XVW.openCommandTab = function( sFrameName, sFormId, sActionId, sActionValue, sTabTitle, bClosable ) {
    // Create new Tab
	if( XVW.getXApp() != null ) {
		var tab = XVW.findTabByFrameName( sFrameName );
		if( tab != null ) {
			var XApp = XVW.getXApp();
			XApp.desktop.tabPanel.setActiveTab( tab );
			return;
		}
	    // Submit the form to the new tab
		if (bClosable === undefined)
			bClosable = true;
		this.openTab( sFrameName, sTabTitle, bClosable );
	    if( ExtXeo.frameLess ) {
		    XVW.AjaxCommand( sFormId, sActionId, sActionValue, null, true, document.getElementById( sFrameName ) );
	    }
	    else {
		    var oForm = document.getElementById( sFormId );
		    var sOldTarget = oForm.target;
		    oForm.target = sFrameName; 
	    	XVW.Command( sFormId, sActionId, sActionValue );
		    oForm.target = sOldTarget;
	    }
	}
	else {
		// Redirect the page to complete the action if the viewer is not integrated with an application
	    XVW.Command( sFormId, sActionId, sActionValue );
	}	
};

//var tabdividx = 0;

XVW.openTab = function( sFrameName, sTitle, bClosable ) {
    var tabs;
    var XApp = XVW.getXApp();
    if (bClosable === undefined)
    	bClosable = true;
    if( XApp != null ) {
	    tabs = XApp.desktop.tabPanel;
	    var fnPanel = window.XApp?Ext.Panel:window.parent.Ext.Panel;
	    var formLayout = new fnPanel(  
	        {  
	            title: sTitle?sTitle:'&nbsp;',
	            border:true,
	            frame:false,
	            closable: bClosable,
	            style: "overflow:visible;",
	            html: (ExtXeo.frameLess)?
	            ('<span id="'+sFrameName+'" width="100%" height="'+(Ext.isChrome?'99%':'100%' )+'"></span>'):
	            ('<iframe name="'+sFrameName+'" id="'+sFrameName+'" src="about:blank" scrolling="no" frameBorder="0" width="100%" height="'+(Ext.isChrome?'99%':'100%' )+'"></iframe>')
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
};

XVW.OpenCommandFrame = function( sFrameName, sFormId, sActionId, sActionValue){
    var oForm = document.getElementById( sFormId );
    var sOldTarget = oForm.target;
    oForm.target = sFrameName; 
	XVW.Command( sFormId, sActionId, sActionValue );
    oForm.target = sOldTarget;
};

XVW.openUrlTab = function( sURL, sTitle ) {
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
	            html: (ExtXeo.frameLess)?
	            ("<span width='100%' height='"+(Ext.isChrome?"99%":"100%" )+"'></span>"):
	            ("<iframe name='"+sURL+"' src='"+sURL+"' scrolling='yes' frameBorder='0' width='100%' height='"+(Ext.isChrome?"99%":"100%" )+"'></iframe>")
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
};


XVW.OpenCommandWindow = function( sFrameName, sFormId, sActionId, sActionValue, sWidth, sHeight, sTitle ){
	sFrameName += (new Date() - 1);
    var win = new Ext.Window({
        layout:'fit',
        width: Number(sWidth),
        height:Number(sHeight),
        title: sTitle,
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
};

ExtXeo.layoutMan.unRegister = function( sDomId ) {
	var compArray = ExtXeo.layoutMan.comp;
	delete compArray[ sDomId ];
};

ExtXeo.layoutMan.layoutTimeoutId = null;
ExtXeo.layoutMan.doLayout = function( sViewId ) {
	//window.setTimeout("ExtXeo.layoutMan.doLayout1();",0);
	try {
		var x = new Date();
		if ( "__allViews" ==  sViewId ) {
			// Do Layout for all views
			ExtXeo.layoutMan.layoutTimeoutId = null;		
			for( var v in ExtXeo.layoutMan.comp ) {
				ExtXeo.layoutMan.doLayout1( v );
			}
		}
		else if( sViewId ) {
			ExtXeo.layoutMan.doLayout1( sViewId );
		}
		else {
			if( ExtXeo.layoutMan.layoutTimeoutId  != null )
				window.clearTimeout( ExtXeo.layoutMan.layoutTimeoutId  );
			ExtXeo.layoutMan.layoutTimeoutId = window.setTimeout("ExtXeo.layoutMan.doLayout('__allViews');", 100);
		}
	}
	catch(e) {
		//TODO: Handle do layout errors
	}
};




ExtXeo.layoutMan.doLayout1 = function( sViewId )
{
	var compArray = ExtXeo.layoutMan.comp[ sViewId ];
	var sId;
	/*var oLayouts = new Array(
			['fit-window',ExtXeo.layoutMan.doFitWindow],
			['fit-parent',ExtXeo.layoutMan.doFitParent],
			['form',ExtXeo.layoutMan.doForm]
	);*/
	var oLayouts = ExtXeo.layoutMan.managers;
	
	for( sId in compArray )	{
		var oElem = document.getElementById( sId );
		if( compArray[ sId ] == 'fit-window' && oElem != null ) {
			oElem.style.visibility='';
		}
	}

	var fnLMan;
	for ( var i = 0; i < oLayouts.length; i++) {
		for( sId in compArray )	{
			if( compArray[ sId ] == oLayouts[i][0] ) {
				for (var k = 0 ; k < oLayouts[i][1].length ; k++ ){
					fnLMan = oLayouts[i][1][k];
					var oElem = document.getElementById( sId );
					if( oElem != null && fnLMan !== 'undefined' && fnLMan != null) {
						try{
							fnLMan( oElem );
						} catch (e){
							
						}
					}
				}
			}
		}
	}

};

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
};

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
};

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
};

ExtXeo.layoutMan.managers = 
	new Array(	['fit-window',[ExtXeo.layoutMan.doFitWindow]],
				['fit-parent',[ExtXeo.layoutMan.doFitParent]],
				['form',[ExtXeo.layoutMan.doForm]]);

ExtXeo.layoutMan.registerManager = function (id, funct){
	for (var i = 0 ; i < ExtXeo.layoutMan.managers.length ; i++){
		var index = ExtXeo.layoutMan.managers[i];
		if (index[0] == id){
			ExtXeo.layoutMan.managers[i][1].push(funct);
		}
	}
};

ExtXeo.resizeLayoutExtComp = function( oElem, c ) {
	var x=oElem.parentNode;
	
	while( x != null && x.tagName != 'TD' && x.className.indexOf('xwc-rows-row') == -1  )
		x = x.parentNode;
	if( c.lastSize && c.lastSize.width != c.width ) 
		c.lastSize = { width:0, height:0 };
	c.setWidth( x.clientWidth );

};

ExtXeo.getAbsoulteOffsetTop = function( o ) {
	var t = 0;
	if( o.offsetParent != null && o.offsetParent.offsetTop ) {
		t += ExtXeo.getAbsoulteOffsetTop( o.offsetParent );
	}
	t += o.offsetTop;
	return t;
};

window.onresize = function() {ExtXeo.layoutMan.doLayout(); };

Ext.ns('ExtXeo','ExtXeo.comp');

ExtXeo.comp.BoxComponent = function( config ) {
	this.initialConfig = config;
};
ExtXeo.comp.BoxComponent.prototype.syncSize = function() {
	return;
};

XVW.setTitle = function( sTitle ) {
	//Remove HTML Tags for the alt title
	var altTitle = sTitle.replace(/(<([^>]+)>)/ig,"");
	if( XVW.getXApp() != null ) {
		var tabs = XVW.getXApp().desktop.tabPanel;
		var tabItems = tabs.items;
	    for ( var i = 0; i < tabItems.getCount(); i++) {
	    	var oFrames = tabItems.get(i).el.dom.getElementsByTagName('iframe');
	    	if( oFrames.length > 0 ) {
	    		for ( var k = 0; k < oFrames.length; k++) {
					if( oFrames[k].contentWindow == window ) {
						tabItems.get(i).setTitle( "<div title='"+altTitle+"' >" + sTitle + "</div>" );
						return;
					}
				}
	    	}
		}
	}
};


XVW.canCloseTab = function( sFormId, sCmdId ) {
	XVW.AjaxCommand( sFormId, sCmdId, "" , 1 , true );
	return false;
};

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
						
						var forms = document.getElementsByTagName("form");
						for(var z=0;z<forms.length;z++)
							XVW.disposeView( forms[z], true );
						return;
					}
				}
	    	}
		}
	}
};


XVW.downloadFile = function( sUrl ) {
	var iFrame = document.createElement("iframe");
	iFrame.style.display='none';
	document.body.appendChild(iFrame);
//	iFrame.onreadystatechange=function(){ try { alert( document.readyState ); iFrame.parentNode.removeChild( iFrame )  }catch(e) { } };
	iFrame.src = sUrl;
};

XVW.CommandDownloadFrame = function( sFrameName, sFormId, sActionId, sActionValue ){
    sFrameName += (new Date() - 1);
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
};

XVW.closeWindow = function( sFormId, sWindowId ) {
	
	
	try{
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
			ExtXeo.destroyComponents(wnd.body.dom,window);
			wnd.hide();
			wnd.destroy();
		}
	}
	finally {
		 if (!XVW.ajax.canAjaxRequest())
				XVW.ajax.enableAjaxRequests();
	}
};


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
};


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
};

XVW.findTabByFormId = function ( sFormId ) {
	var ret;
	if( XVW.getXApp() != null ) {
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
	    					ret = tabItems.get(i);
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
};


// Disable back
// window.history.forward(1);

//Disable back
window.history.forward(1);
XVW.MenuCounter = {
	counterRefreshId : 0,
	counters : {},
	registerCounter : function( sUrl, sContId, sNodeId, interval ) {
		this.counters[sNodeId] = { url: sUrl, containerId: sContId, nodeId: sNodeId, interval: interval, lastUpdate: (new Date())-0 };
		window.setInterval( function() { XVW.MenuCounter.updateCounter( sNodeId, interval ); }, interval*1000 );
	},
	updateCounter : function( sNodeId, loop ) {
		var x = null;
		x = this.counters[sNodeId];
		if( !x ) {
			if( !loop && window.parent.XVW.MenuCounter )
				x = window.parent.XVW.MenuCounter.updateCounter( sNodeId, true );
		} else {
			
			var el = document.getElementById( "extxeo-refresh-counters-img" );
			XVW.MenuCounter.counterRefreshId++;
			if( el ) {
				el.src = 'extjs/images/default/grid/wait.gif';
			}
			
			var x = this.counters[sNodeId];
			var xh = XVW.createXMLHttpRequest();
			xh.open( 'GET',x.url + "&KeepAlive=true", true );
			xh.onreadystatechange = function() {
				if( xh.readyState==4 ) {
					if( xh.status == 401 && xh.getResponseHeader('login-url') != "" ){
		            	XVW.NoWait();
		            	XVW.ConfirmationDialog(XVW.Messages.SESSION_EXPIRED_TITLE,XVW.Messages.SESSION_EXPIRED_MESSAGE,
		            		function (){window.top.location.href = xh.getResponseHeader('login-url');});
		            } else {
						try {
							eval( 'var r = ' + xh.responseText );
							var c = Ext.getCmp( x.containerId );
							if(c) {
								var n = c.getNodeById( x.nodeId );
								if(n) n.setText( r.counterHtml );
				    		}
							x.lastUpdate = (new Date())-0;
						}
						catch( e ) {
						}
						XVW.MenuCounter.counterRefreshId--;
						if( XVW.MenuCounter.counterRefreshId == 0 ) {
							var el = document.getElementById( "extxeo-refresh-counters-img" );
							if( el ) {
								el.src = 'extjs/resources/images/default/grid/refresh.gif';
							}
						}
		            }
				}
			};
			xh.send();
		}
	},
	updateCounters : function( loop ) {
		for( x in this.counters ) {
			XVW.MenuCounter.updateCounter( x );
		}
		if( !loop ){
			if( window.parent.XVW.MenuCounter ) {
				window.parent.XVW.MenuCounter.updateCounters( true );
			}
		}	
	}
};




/**
 * http://www.openjs.com/scripts/events/keyboard_shortcuts/
 * Version : 2.01.B
 * By Binny V A
 * License : BSD
 */
shortcut = {
	'all_shortcuts':{},//All the shortcuts are stored in this array
	'add': function(shortcut_combination,callback,opt) {
		//Provide a set of default options
		var default_options = {
			'type':'keydown',
			'propagate':false,
			'disable_in_input':false,
			'target':document,
			'keycode':false
		};
		if(!opt) opt = default_options;
		else {
			for(var dfo in default_options) {
				if(typeof opt[dfo] == 'undefined') opt[dfo] = default_options[dfo];
			}
		}

		var ele = opt.target;
		if(typeof opt.target == 'string') ele = document.getElementById(opt.target);
		var ths = this;
		shortcut_combination = shortcut_combination.toLowerCase();

		//The function to be called at keypress
		var func = function(e) {
			e = e || window.event;
			
			if(opt['disable_in_input']) { //Don't enable shortcut keys in Input, Textarea fields
				var element;
				if(e.target) element=e.target;
				else if(e.srcElement) element=e.srcElement;
				if(element.nodeType==3) element=element.parentNode;

				if(element.tagName == 'INPUT' || element.tagName == 'TEXTAREA') return;
			}
	
			//Find Which key is pressed
			if (e.keyCode) code = e.keyCode;
			else if (e.which) code = e.which;
			var character = String.fromCharCode(code).toLowerCase();
			
			if(code == 188) character=","; //If the user presses , when the type is onkeydown
			if(code == 190) character="."; //If the user presses , when the type is onkeydown

			var keys = shortcut_combination.split("+");
			//Key Pressed - counts the number of valid keypresses - if it is same as the number of keys, the shortcut function is invoked
			var kp = 0;
			
			//Work around for stupid Shift key bug created by using lowercase - as a result the shift+num combination was broken
			var shift_nums = {
				"`":"~",
				"1":"!",
				"2":"@",
				"3":"#",
				"4":"$",
				"5":"%",
				"6":"^",
				"7":"&",
				"8":"*",
				"9":"(",
				"0":")",
				"-":"_",
				"=":"+",
				";":":",
				"'":"\"",
				",":"<",
				".":">",
				"/":"?",
				"\\":"|"
			};
			//Special Keys - and their codes
			var special_keys = {
				'esc':27,
				'escape':27,
				'tab':9,
				'space':32,
				'return':13,
				'enter':13,
				'backspace':8,
	
				'scrolllock':145,
				'scroll_lock':145,
				'scroll':145,
				'capslock':20,
				'caps_lock':20,
				'caps':20,
				'numlock':144,
				'num_lock':144,
				'num':144,
				
				'pause':19,
				'break':19,
				
				'insert':45,
				'home':36,
				'delete':46,
				'end':35,
				
				'pageup':33,
				'page_up':33,
				'pu':33,
	
				'pagedown':34,
				'page_down':34,
				'pd':34,
	
				'left':37,
				'up':38,
				'right':39,
				'down':40,
	
				'f1':112,
				'f2':113,
				'f3':114,
				'f4':115,
				'f5':116,
				'f6':117,
				'f7':118,
				'f8':119,
				'f9':120,
				'f10':121,
				'f11':122,
				'f12':123
			};
	
			var modifiers = { 
				shift: { wanted:false, pressed:false},
				ctrl : { wanted:false, pressed:false},
				alt  : { wanted:false, pressed:false},
				meta : { wanted:false, pressed:false}	//Meta is Mac specific
			};
                        
			if(e.ctrlKey)	modifiers.ctrl.pressed = true;
			if(e.shiftKey)	modifiers.shift.pressed = true;
			if(e.altKey)	modifiers.alt.pressed = true;
			if(e.metaKey)   modifiers.meta.pressed = true;
                        
			for(var i=0, k=keys[i]; i<keys.length; i++) {
				//Modifiers
				if(k == 'ctrl' || k == 'control') {
					kp++;
					modifiers.ctrl.wanted = true;

				} else if(k == 'shift') {
					kp++;
					modifiers.shift.wanted = true;

				} else if(k == 'alt') {
					kp++;
					modifiers.alt.wanted = true;
				} else if(k == 'meta') {
					kp++;
					modifiers.meta.wanted = true;
				} else if(k.length > 1) { //If it is a special key
					if(special_keys[k] == code) kp++;
					
				} else if(opt['keycode']) {
					if(opt['keycode'] == code) kp++;

				} else { //The special keys did not match
					if(character == k) kp++;
					else {
						if(shift_nums[character] && e.shiftKey) { //Stupid Shift key bug created by using lowercase
							character = shift_nums[character]; 
							if(character == k) kp++;
						}
					}
				}
			}
			
			if(kp == keys.length && 
						modifiers.ctrl.pressed == modifiers.ctrl.wanted &&
						modifiers.shift.pressed == modifiers.shift.wanted &&
						modifiers.alt.pressed == modifiers.alt.wanted &&
						modifiers.meta.pressed == modifiers.meta.wanted) {
				callback(e);
	
				if(!opt['propagate']) { //Stop the event
					//e.cancelBubble is supported by IE - this will kill the bubbling process.
					e.cancelBubble = true;
					e.returnValue = false;
	
					//e.stopPropagation works in Firefox.
					if (e.stopPropagation) {
						e.stopPropagation();
						e.preventDefault();
					}
					return false;
				}
			}
		};
		this.all_shortcuts[shortcut_combination] = {
			'callback':func, 
			'target':ele, 
			'event': opt['type']
		};
		//Attach the function with the event
		if(ele.addEventListener) ele.addEventListener(opt['type'], func, false);
		else if(ele.attachEvent) ele.attachEvent('on'+opt['type'], func);
		else ele['on'+opt['type']] = func;
	},

	//Remove the shortcut - just specify the shortcut and I will remove the binding
	'remove':function(shortcut_combination) {
		shortcut_combination = shortcut_combination.toLowerCase();
		var binding = this.all_shortcuts[shortcut_combination];
		delete(this.all_shortcuts[shortcut_combination]);
		if(!binding) return;
		var type = binding['event'];
		var ele = binding['target'];
		var callback = binding['callback'];

		if(ele.detachEvent) ele.detachEvent('on'+type, callback);
		else if(ele.removeEventListener) ele.removeEventListener(type, callback, false);
		else ele['on'+type] = false;
	}
};

Ext.form.ToolBarLabel = Ext.extend(Ext.form.Label,  {
	
	cls : 'xwc-toolbar-label',
	
	initComponent: function(){
	        // Config object has already been applied to 'this' so properties can 
	        // be overriden here or new properties (e.g. items, tools, buttons) 
	        // can be added, eg:
	        Ext.apply(this, {
	        	cls: 'xwc-toolbar-label'
	        });
	 
	         // Call parent (required)
	        Ext.form.ToolBarLabel.superclass.initComponent.apply(this, arguments);
	        
	    }
	
});

