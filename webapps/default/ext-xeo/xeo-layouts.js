	var xeoUserDisplayName = null;
XEOLayout = function() 
{
    this.desktop = new XEOLayout.ViewPort();    
    
};

XEOLayout.closeWindow = function(  ) {
	ExtXeo.destroyComponents( document.body );
};


XEOLayout.ViewPort = function()
{
    this.toolBar = null; //mainToolBar;
    this.tabPanel = new Ext.TabPanel({
            region:'center',
            id:'app-tabpanel',
            deferredRender:true,
            activeTab:0,
            resizeTabs:true,
            minTabWidth: 100,
            tabWidth:170,
            enableTabScroll:true,
            width:600,
            height:250,
            margins:'0 3 3 0',
            autoDestroy:true,
            style: 'background-color:white',
            defaults: {autoScroll:true},
            listeners: { 
            	'beforeremove': {
            		fn:function( oTabCont, oComp ) {
            			return XEOLayout.onCloseTab( oTabCont, oComp );
            		}
            	} 
            }, 
            items:null
        });
    	
    	var layoutItems = [];
    	if( Ext.get("formMain:tree") ) {
	    	layoutItems = [{
	                            region:'north',
	                            split:false,
	                            border:false,
	                            frame:false,
	                            height: 25,
	                            minSize: 25,
	                            maxSize: 200,
	                            collapsible: false,
	                            hideBorders: true,
	                            margins:'3 3 3 3',
	                            items: [ { html:' <table id="header" width="100%" ><tr><td ><div class="api-title" style="font-family:tahoma,arial,sans-serif;color:white;">'+ ExtXeo.Messages.WELCOME + ' ' + xeoUserDisplayName + '</div></td><td align="right"></td><td width="30px" align="right">'
	                            	+'<img HEIGHT="15px" src="ext-xeo/images/xeo_30.gif"/>  </td></tr></table>'} ]
	                        }
	                ,{
	                    region:'west',
	                    id:'west-panel',
	                    title:
	                    		"<img id='extxeo-refresh-counters-img' onclick='XVW.MenuCounter.updateCounters(true);' style='cursor:pointer;position:relative;top:0px;width:16px;height:16px' align='right' src='extjs/resources/images/default/grid/refresh.gif'>" +
	                    		(window.treeName?treeName: + ExtXeo.Messages.TREE_TITLE ) 
	                    		,
	                    split:true, 
	                    width: 200,
	                    minSize: 175,
	                    maxSize: 400,
	                    collapsible: true,
	                    margins:'0 0 3 3',
	                    animated:true,
	            	    layout: 'fit',
	                    layoutConfig:{
	                        animate:true
	                    },
	                    items: [ layoutTree ]
	                }
	                    ,            
	                    // Aplication Area
	                    this.tabPanel
	                 ];
    	}
    	else {
	    	layoutItems = [           
		        this.tabPanel
		     ];
    	}
    
        XEOLayout.ViewPort.superclass.constructor.call( this,
            {
                layout:'border',
                frame:false,
                border:false,
                items: layoutItems
            }
        );
    };
    
    
Ext.extend(XEOLayout.ViewPort, Ext.Viewport, {
	    openTab : function( sFormName, sCommandName )
	    {
	        XVW.openCommandTab( 'Viewer1'+(new Date()-100), sFormName, sCommandName );
	    },
	    closeTab : function( tab ) 
	    {
	        var tabs = this.findById('app-tabpanel');
	        tabs.remove( tab );        
	    }
	}
);

XEOLayout.onCloseTab = function( oTabCont, oComp ) {
	if( !oComp.forceClose ) {
		var changed = false;
		var x = oComp.el.dom.getElementsByTagName('iframe');
		for( var i=0;!changed && i < x.length; i++ ) {
			try {
				var y = x[i].contentWindow.document.getElementsByName("__isChanged");
				for( var k = 0;k < y.length; k++ ) {
					var cmd = y[i].value;
					if( !x[i].contentWindow.XVW.canCloseTab( cmd.split(':')[0], cmd.split(':')[1] ) ) {
 						oTabCont.activate( oComp );
						return false;
					}
					break;
				}
			}
			catch(e) {
				
			}
		}
    }
};

function xeodmToggleHandler( btn, state ) {
	var oForm = document.getElementsByName("formMain")[0];
	var sActionUrl = null;
	var xmlReq = null;
	if( state ) {
		window.xeodmstate = true;
		sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.resourceUrl').value;
		xmlReq = XVW.createXMLHttpRequest();
	    xmlReq.open( "POST", sActionUrl+"netgest/bo/xwc/components/viewers/XEOViewerOperations.xvw?action=xeodmtoggler&xeodmstate=true", true );
	    xmlReq.send();
		btn.setText( ExtXeo.Messages.XEODM_ACTIVE );
	}
	else {
		window.xeodmstate = false;
		sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.resourceUrl').value;
		xmlReq = XVW.createXMLHttpRequest();
	    xmlReq.open( "POST", sActionUrl+"netgest/bo/xwc/components/viewers/XEOViewerOperations.xvw?action=xeodmtoggler&xeodmstate=false", true );
	    xmlReq.send();
		btn.setText( ExtXeo.Messages.XEODM_INACTIVE );
	}
}
