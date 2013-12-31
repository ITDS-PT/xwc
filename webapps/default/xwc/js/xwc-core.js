XVW = function() {};

//Retrieve an HTML Element by id
XVW.get = function (id ) {
	return document.getElementById(id);
};

/**
 * 
 * Properties related to Ajax Requests
 * */
XVW.ajax = function () {};
/**
 * Whether an ajax request can be made
 * */
XVW.ajax.canMakeAjaxRequest = true;
/**
 * The number of attempts made for ajax request 
 * */
XVW.ajax.ajaxRequestCounter = 0;
/**
 * The maximum number of attempts
 * */
XVW.ajax.maxAjaxRequest = 10;
/**
 * Wait period before attempting another Ajax Request (in miliseconds)
 * */
XVW.ajax.ajaxRequestWaitPeriod = 300;

/**
 *  Increments the attempts counter
 * */
XVW.ajax.incrementAjaxCounter = function(){
	XVW.ajax.ajaxRequestCounter++;
};
/**
 * Clear the ajax attempts counter
 * */
XVW.ajax.clearAjaxCounter = function(){
	XVW.ajax.ajaxRequestCounter = 0;
};
/**
 * Returns whether or not we're in conditions to issue a new Ajax Request
 * */
XVW.ajax.canAjaxRequest = function(){
	return XVW.ajax.ajaxRequestCounter < XVW.ajax.maxAjaxRequest && XVW.ajax.canMakeAjaxRequest;
};
/**
 * Block new ajax requests until a response has arrived
 * */
XVW.ajax.blockAjaxRequests = function(){
	XVW.ajax.canMakeAjaxRequest = false;
};
/**
 * Queue an AjaxRequest for later submission
 * */
XVW.ajax.queueAjaxRequest = function( ajaxRequest ){
	window.setTimeout( ajaxRequest , XVW.ajax.ajaxRequestWaitPeriod );
};
/**
 * Enable Ajax Requests
 * */
XVW.ajax.enableAjaxRequests = function(){
	XVW.ajax.canMakeAjaxRequest = true;
	XVW.ajax.clearAjaxCounter();	
};

// Send command to server
XVW.Command = function( sFormId, sActionId, sActionValue, iWaitScreen ) {
    XVW.prv.cmdCntr = 0;
    var oForm = XVW.prv.createCommand( sFormId, sActionId, sActionValue );
    if( oForm != null ) {
		oForm.submit();
    } 
    XVW.prv.removeCommand( sFormId, sActionId );
};

// Call server to render a component
XVW.AjaxRenderComp = function( sFormId, sCompId, bSubmitValues ) {
	return XVW.AjaxCommand( sFormId, ":xvw.render", sCompId, null, bSubmitValues );
};

// Send Command via Ajax
XVW.AjaxCommand = function( sFormId, sActionId, sActionValue, iWaitScreen, bSubmitValues, renderOnElement, queue ) {
	
	if (queue === undefined)
		queue = true;
	
	if (!XVW.ajax.canAjaxRequest() && queue){
		XVW.ajax.incrementAjaxCounter();
		XVW.ajax.queueAjaxRequest( function () {
				XVW.AjaxCommand(sFormId, sActionId, sActionValue, iWaitScreen, bSubmitValues, renderOnElement); 
		} );
		return;
	}
	
	if (queue)
		XVW.ajax.blockAjaxRequests();
	
    if( bSubmitValues == undefined ) {
        bSubmitValues = true;
    }
    window.ts = new Date();

    // Create a button to Submit the action
    XVW.Wait( iWaitScreen );
        
    var oForm   = document.getElementById( sFormId );
    if( oForm != null ) {
    	if( "multipart/form-data" == oForm.enctype ) {
    		return XVW.Command( sFormId, sActionId, sActionValue, iWaitScreen );
    	}
    	
        // Generate Ajax request
        var oParNames = [];
        var oParValues = [];
        
        // Read all form INPUT's
        if( bSubmitValues )
        {
            XVW.AjaxCommand.pFnParseNode( oForm, oParNames, oParValues );
        }
        else {
            // Envia obrigatoriamente o estado da view actual
            var oInput = XVW.prv.getViewStateInput( oForm );
            //var oInput = document.getElementById('javax.faces.ViewState');
            oParNames[oParNames.length] = oInput.name;
            oParValues[oParValues.length] = oInput.value;
        }
        
        // Add the command and parameter
        if( sActionId != null )
        {
            var sId;
            if( sActionId.indexOf(':')==0 ) {
            	sId = sActionId.substring(1);
            }
            else if ( sActionId.indexOf(sFormId+':') == 0 ) {
            	sId = sActionId;
            } else {
            	sId = sFormId + ':' + sActionId;
            }

            var iCnt = oParNames.length;
            oParNames[ iCnt ] = sId;
            if( typeof(sActionValue) != "undefined")
                oParValues[ iCnt ] = sActionValue;
            else
                oParValues[ iCnt ] = '';
        }
        
        // Generate XML for AJAX request
        var reqDoc = XVW.creatXMLDocument();
        
        var eRoot = reqDoc.createElement("xvwAjaxReq");
        var eParam = reqDoc.createElement("parameters");
        
        reqDoc.appendChild( eRoot );
        eRoot.appendChild( eParam );
        
        var oParNode;
        for( var i=0; i < oParNames.length; i++ ) {
            oParNode = reqDoc.createElement( "p" );
            oParNode.setAttribute("name", oParNames[i] );
            oParNode.appendChild( reqDoc.createCDATASection( oParValues[ i ] ) );
            eParam.appendChild( oParNode );
        }
        
        var sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.submitUrl').value;
        submitAjax( sActionUrl, reqDoc, renderOnElement );
        
    }    
};

/**
 * Maximum Number of retries for a request
 */
XVW.maxRetries = 2;

function submitAjax( sActionUrl, reqDoc, renderOnElement, retryCount ) {
    var oXmlReq = XVW.createXMLHttpRequest(  );
    if (retryCount === undefined){
    	retryCount = 0;
    }
    oXmlReq.open('POST', sActionUrl, true );
    oXmlReq.setRequestHeader('Content-Type', 'text/xml');
    oXmlReq.onreadystatechange = function() {
        if( oXmlReq.readyState == 4 ) {
        	var requestEnd = new Date().getTime();
        	var requestOk = false;
        	var jsProcessingInit = 0;
        	var jsProcessingEnd = 0;
            if( oXmlReq.status == 200 ) {
                try
                {
                    window.ts2 = new Date();
                    jsProcessingInit = new Date().getTime(); 
                    XVW.handleAjaxResponse( oXmlReq, renderOnElement );
                    jsProcessingEnd = new Date().getTime();
                    requestOk = true;
                    XVW.NoWait();
                    if (retryCount){
                    	if (retryCount > 0){
                    		XVW.ResetWait();
                    	}
                    }
                }
                catch( e ) {
                	XVW.handleAjaxError( e.message, e.stack  );
                    requestOk = false;
                }
            }
            else if( oXmlReq.status == 401 && oXmlReq.getResponseHeader('login-url') != "" ){
            	XVW.NoWait();
            	XVW.ConfirmationDialog(XVW.Messages.SESSION_EXPIRED_TITLE,XVW.Messages.SESSION_EXPIRED_MESSAGE,
            		function (){window.top.location.href = oXmlReq.getResponseHeader('login-url');});
            } else if (oXmlReq.status == 0){
            	if (retryCount <= XVW.maxRetries){
            		retryCount++;
            		if (retryCount == 1){
            			XVW.StillWorking(XVW.Messages.AJAX_SECOND_TRY);
            		} else if (retryCount == 2){
            			XVW.StillWorking(XVW.Messages.AJAX_THIRD_TRY);
            		}
            		submitAjax(sActionUrl,reqDoc,renderOnElement,retryCount);
            	} else {
            		XVW.NoWait();
            		XVW.ResetWait();
            		XVW.handleAjaxError( oXmlReq.status + " - " + oXmlReq.statusText, oXmlReq.responseText );
            	}
            }
            else {
	           XVW.NoWait();
	           if (retryCount){
	        	   if (retryCount > 0){
	        		   XVW.ResetWait();
	        	   }
	           }
	           XVW.handleAjaxError( oXmlReq.status + " - " + oXmlReq.statusText, oXmlReq.responseText );
            }
            
            if (!XVW.ajax.canAjaxRequest()){
            	XVW.ajax.enableAjaxRequests();
            }
            
            var jsTime = 0;
            if (requestOk){
            	jsTime = jsProcessingEnd - jsProcessingInit;
            }
            
            if (requestOk){
            	XVW.NoWait();
                if (retryCount){
                	if (retryCount > 0){
                		XVW.ResetWait();
                	}
                }
            }
             
            var requestTime = requestEnd - requestStart;
            XVW.logAjaxTiming(requestTime,jsTime);
        }
    };
    var requestStart = new Date().getTime();
    oXmlReq.send( reqDoc );
	
}





XVW.AjaxCommand.pFnParseNode = function( oNode, loParNames, loParValues ) {
    var oChild;
    var oChilds = oNode.childNodes;
    for( var i=0 ; i < oChilds.length; i++ ) {

        var iCnt = loParNames.length;
        
        oChild = oChilds[i];
        if( oChild.tagName == 'INPUT' || oChild.tagName == 'SELECT' || oChild.tagName == 'TEXTAREA' )
        {
        	if( oChild.tagName == 'INPUT' ) {
	            if( oChild.type == 'checkbox' || oChild.type == 'radio' )
	            {
	                if( oChild.checked ) {
	                    loParNames[ iCnt ] = oChild.name;
	                    loParValues[ iCnt ] = oChild.value;
	                    iCnt++;
	                }
	                else {
	                    loParNames[ iCnt ] = oChild.name;
	                    loParValues[ iCnt ] = "";
	                    iCnt++;
	                }
	            }
	            else if ( !( oChild.type == 'button' || oChild.type == 'submit') )
	            {
	                loParNames[ iCnt ] = oChild.name;
	                loParValues[ iCnt ] = oChild.value;
	                iCnt++;
	            }
        	}
            else
            {
            	if (oChild.tagName == 'SELECT'){
            		loParNames[ iCnt ] = oChild.name;
            		var children = oChild.children;
            		var append = "";
            		var result = "";
            		for (k = 0 ; k < children.length; k++){
            			var child = children[k];
            			if (child.tagName == 'OPTION' ){
            				if (child.selected){
		            			result += append;
		            			result += child.value;
		            			append = ",";
	            			}
            			}
            		}
            		loParValues[ iCnt ] = result;
            		iCnt++;
            	} else{
            		loParNames[ iCnt ] = oChild.name;
            		loParValues[ iCnt ] = oChild.value;
            		iCnt++;
            	}
            }
        }
        if( oChild.hasChildNodes() ) {
            XVW.AjaxCommand.pFnParseNode( oChild, loParNames, loParValues );
        }
    }
};


// Is not in use for now... it maybe be need in the future
XVW.getViewForm = function( sViewId, sFormId ) {
    var oViewDiv = document.getElementById( sViewId );
    if( oViewDiv != null ) {
        var oFormElms = oViewDiv.getElementsByTagName( "form" );
        for (var i = 0; i < oFormElms.length; i++)  {
            if( oFormElms[i].id == sFormId ) {
                return oFormElms[i];
            }
        }
    }
    return null;    
};

XVW.handleAjaxError = function( sErrorMessage, sDetails ) {
    XVW.ErrorDialog( XVW.Messages.AJAXERROR_TITLE, XVW.Messages.AJAXERROR_MESSAGE + "<br/>" + sErrorMessage, sDetails );
};

// Must be overwriten ti handle error dialogs
XVW.ErrorDialog = function( sTitle, sMessage ) {};

XVW.ConfirmationDialog = function (sTitle, sMessage, okButtonHandler){};

XVW.handleAjaxResponse = function( oXmlReq, renderOnElement ) {
	// Handle view Element -- Update ViewId
    var oScriptId;
    var oScriptSrc;
    var oDocElm = oXmlReq.responseXML.documentElement;
    var sViewId     = oDocElm.getAttribute("viewId");
    var bIsPostBack = "true" == oDocElm.getAttribute("isPostBack");
    var oViewDiv    = null;
    
    
    oViewDiv = document.getElementById( sViewId );
    if( !bIsPostBack && oViewDiv == null ) {
        // The result of the ajax request is a new view to render
        // Create the base Element of the view
        
        // Create the place holder for the new view
        oViewDiv = document.createElement( "div" );
        oViewDiv.id = sViewId;
        
        // Comment for non iframe less application
        //var xdiv = document.getElementById('tabdiv' + tabdividx );
        //xdiv.appendChild( oViewDiv );
        
        // Render the viewer in certain area
        if( renderOnElement ) {
        	var parent = renderOnElement.parentNode;
        	if (parent != null){
	        	XVW.beforeApplyHtml( renderOnElement , true );
	        	parent.removeChild( renderOnElement );
	        	//renderOnElement.appendChild( oViewDiv );
	        	parent.appendChild( oViewDiv );
        	}
        } else {
        	document.body.appendChild( oViewDiv );
    	}
    }
    
    // Mozilla have diferent variable contexts in evals.
    // Now header and footer scripts are evaluated in the same bock.
    var sScriptToEval = "";
    
    // Destroy html 
    var oRenderNodeList = oDocElm.getElementsByTagName("render");
    for( var nl1=0;nl1<oRenderNodeList.length; nl1++ )
    {
        var oChildNodes = oRenderNodeList.item(nl1).getElementsByTagName( 'component' );
        for( var i=0; i < oChildNodes.length; i++ ) {
            var oCompNode = oChildNodes.item(i);
            var oCompId = oCompNode.getAttribute("id");
            var oCompDNode = document.getElementById( oCompId );
            if( oCompDNode != null ) {
        		var pNode = oCompDNode.parentNode; 
        		var sNode = oCompDNode.nextSibling;
        		XVW.beforeApplyHtml( oCompDNode, oCompNode.getAttribute("destroy")=='true' );
        		// Check if the placeholder still exists after event
        		// beforeApplyHtml
        		if( document.getElementById(oCompDNode.id) == null ) {
        			var nnode = document.createElement( oCompDNode.tagName );
        			nnode.id = oCompDNode.id;
        			if( sNode != null ) {
        				pNode.insertBefore( nnode, sNode );
        			} else { 
        				pNode.appendChild( nnode );
        			}
        		}
            }
        }
    }
    
    // Handle header Scripts - Run Header Scripts
    var oHeaderScriptNodeList = oDocElm.getElementsByTagName("headerScripts");
    for( var nl3=0;nl3<oHeaderScriptNodeList.length; nl3++ )
    {
        var oScriptNodes = oHeaderScriptNodeList.item(nl3).getElementsByTagName( 'script' );
        for( var i=0; i < oScriptNodes.length; i++ ) {
            var oScriptNode = oScriptNodes.item(i);
            oScriptId = oScriptNode.getAttribute("id");
            oScriptSrc = oScriptNode.getAttribute("src");
            if( oScriptSrc != null ) {
            	XVW.addScriptInclude( oScriptId, oScriptSrc );
            }
            else { 
            	if(oScriptNode.textContent /*Mozilla*/) { sScriptToEval += oScriptNode.textContent + "\n"; }
            	else /*IE*/ { sScriptToEval += oScriptNode.text + "\n";  }
            }

        }            
    }        
    
    if( sScriptToEval != "" )
    {
        try 
        {
            window.eval( sScriptToEval );
        }
        catch( e ) {
            XVW.ErrorDialog( XVW.Messages.AJAXERROR_MESSAGE, "["+oScriptId+"]" +
                e.description
            );
        }
        finally {
        	sScriptToEval = "";
        }
    }
    
    // Handle Render Elements - Render the elements in XML
    var oRenderNodeList = oDocElm.getElementsByTagName("render");
    for( var nl1=0;nl1<oRenderNodeList.length; nl1++ )
    {
        var oChildNodes = oRenderNodeList.item(nl1).getElementsByTagName( 'component' );
        for( var i=0; i < oChildNodes.length; i++ ) {
            var oCompNode = oChildNodes.item(i);
            var oCompId = oCompNode.getAttribute("id");
            var oCompDNode = document.getElementById( oCompId );
            if( oCompDNode != null ) {
                if(oCompNode.textContent /*Mozilla*/) {
            		// If first child is null... there is nothing to render
            		// The component didn't write anything to the output
            		var x = document.createElement('div');
                    try
                    {
                		if( oCompNode.textContent.trim().length > 0 ) {
    	            		x.innerHTML = oCompNode.textContent;
    	            		oCompDNode.parentNode.replaceChild( x.firstChild.nextSibling, oCompDNode );
                		}
                    }
                    catch( e ) {
                        XVW.ErrorDialog( XVW.Messages.AJAXERROR_MESSAGE, "APPLY HTML ["+oCompId+"]" +
                            e.description + "\n" +
                            oCompNode.textContent
                        );    
                    }
                }
                else if( oCompNode.text ) {
            		if( oCompNode.text.length > 0) {
                    	oCompDNode.outerHTML = oCompNode.text;
            		}
                }
            }
        }
    }
    
    // Update client viewstate
    var oVStateNodeList = oDocElm.getElementsByTagName("viewState");
    for( var nl2=0;nl2<oVStateNodeList.length; nl2++ )
    {
        
        // View Element may be obsolet because it may be overwritten when applying outerHTML.
        // Refresh the element reference.
        oViewDiv = document.getElementById( sViewId );
        
        var oViewForms = oViewDiv.getElementsByTagName( 'form' );

        for( var z=0; z < oViewForms.length; z++ ) {
	        var oStateNodes = oVStateNodeList.item(nl2).getElementsByTagName( 'input' );
	        for( var i=0; i < oStateNodes.length; i++ ) {
	            var oStateNode = oStateNodes.item(i);
	            var oStateId = oStateNode.getAttribute("id");
	            var sStateVal = oStateNode.getAttribute("value");
	            
	            //var oStateDNode = document.getElementById( oStateId );
	            var oStateDNode = XVW.getViewInputById( oViewForms[z].id, oStateId );
	            if( oStateDNode != null ) {
	                oStateDNode.value = sStateVal;
	            }
	            else {
	                oStateDNode = document.createElement( "input" );
	                oStateDNode.type = 'hidden';
	                oStateDNode.id = oStateId;
	                oStateDNode.name = oStateId;
	                oStateDNode.value = sStateVal;
                	oViewForms[z].appendChild( oStateDNode );
	            }
	        }
        }
    }        

    // Handle footer Scripts - Run footer scripts
    var oFooterScriptNodeList = oDocElm.getElementsByTagName("footerScripts");
    for( var nl3=0;nl3<oFooterScriptNodeList.length; nl3++ )
    {
        var oScriptNodes = oFooterScriptNodeList.item(nl3).getElementsByTagName( 'script' );
        for( var i=0; i < oScriptNodes.length; i++ ) {
            var oScriptNode = oScriptNodes.item(i);
            oScriptId = oScriptNode.getAttribute("id");
            oScriptSrc = oScriptNode.getAttribute("src");
            if( oScriptSrc != null ) {
            	XVW.addScriptInclude( oScriptId, oScriptSrc );
            }
            else { 
	            if(oScriptNode.textContent /*Mozilla*/) { sScriptToEval += oScriptNode.textContent; }
	            else /*IE*/ { sScriptToEval += oScriptNode.text; }
            }
            sScriptToEval += "\n";
        }            
    }
    
    if( sScriptToEval != "" )
    {
        try 
        {
            window.eval( sScriptToEval );
        }
        catch( e ) {
        		debugger;
        		XVW.reportAjaxErrorBlock(oFooterScriptNodeList);
        		var details = "";
        		if (e.stack !== undefined)
        			details = e.stack;
	            XVW.ErrorDialog( XVW.Messages.AJAXERROR_MESSAGE, "["+oScriptId+"]" +
	            		e.message , details
	            );
        	}
        }

    
    XVW.ajax.enableAjaxRequests();
};

XVW.reportAjaxErrorBlock = function (oFooterScriptNodeList) {
	var sScriptToEval = "";
	for( var nl3=0;nl3<oFooterScriptNodeList.length; nl3++ )
    {
        var oScriptNodes = oFooterScriptNodeList.item(nl3).getElementsByTagName( 'script' );
        for( var i=0; i < oScriptNodes.length; i++ ) {
            var oScriptNode = oScriptNodes.item(i);
            oScriptId = oScriptNode.getAttribute("id");
            oScriptSrc = oScriptNode.getAttribute("src");
            if( oScriptSrc == null ) {
                if(oScriptNode.textContent /*Mozilla*/) { 
                	sScriptToEval = oScriptNode.textContent; 
                }
	            else /*IE*/ { 
	            	sScriptToEval = oScriptNode.text; 
	            }
	            
	            try {
	            	window.eval(sScriptToEval);
	            } catch (e) {
	            	var url = location.href;
	            	var errorMessage = e.message;
	            	var jsBlock = sScriptToEval;
            		var line = null;
	            	XVW.logJsError(errorMessage,url,line,jsBlock);
	            }
            }
        }            
    }
};

XVW.ajaxResponse = function() {};

XVW.ajaxResponse.createScriptElement = function ( scriptId, scriptContent ){
	var scriptElement = document.createElement('script');
	scriptElement.id = scriptId;
	scriptElement.type = 'text/javascript';
	scriptElement.text = scriptContent;
	return scriptElement;
};

XVW.ajaxResponse.isXmlRequestReady = function ( xmlHttpRequest ){
	return xmlHttpRequest.readyState == 4;
};

XVW.ajaxResponse.includeScriptWithXmlHttp = function (oScriptId, pathToScript){
	if( document.getElementById( oScriptId )==null ) {
		var oXmlReq = XVW.createXMLHttpRequest(  );
	    oXmlReq.open('GET', pathToScript , false );
	    oXmlReq.onreadystatechange = function(){
	      if ( XVW.ajaxResponse.isXmlRequestReady( oXmlReq ) ){
	    	  var head = document.getElementsByTagName('head')[0];
	    	  var script = XVW.ajaxResponse.createScriptElement(oScriptId,oXmlReq.responseText);
	    	  head.appendChild( script );
	      }
	    };
	    oXmlReq.send(null);
	}
};

/**
 * 
 * This function is called to include a javascript source file dinamically within an AjaxRequest, uses
 * XMLHttpRequest to download the script (synchronously) and appends the script content to the head
 * 
 * */
XVW.addScriptInclude = function( oScriptId, xsrc ) {
	XVW.ajaxResponse.includeScriptWithXmlHttp(oScriptId,xsrc);
};




XVW.getViewInputById = function( sViewDivId, sInputId ) {
    var oViewDiv = document.getElementById( sViewDivId );
    if( oViewDiv != null ) {
        var oInputs = oViewDiv.getElementsByTagName("input");
        for (var i = 0; i < oInputs.length; i++)  {
            if( oInputs[i].id == sInputId ) {
                return oInputs[i];
            }
        }
    }
    return null;
};

XVW.disposeView = function( oForm, async ) {
	try {
		var sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.resourceUrl').value;
		var sViewId    = XVW.prv.getFormInput( oForm, 'javax.faces.ViewState').value;
		var xmlReq = XVW.createXMLHttpRequest();
	    xmlReq.open( "POST", sActionUrl+"/netgest/bo/xwc/framework/viewers/SystemOperations.xvw?action=closeView&viewId="+sViewId, async );
	    xmlReq.send();
	} catch( e ) {
		//debugger;
	}
};

XVW.disposeAll = function( oForm, async ) {
	try {
		var sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.resourceUrl').value;
		var xmlReq = XVW.createXMLHttpRequest();
	    xmlReq.open( "POST", sActionUrl+"/netgest/bo/xwc/framework/viewers/SystemOperations.xvw?action=releaseAll", async );
	    xmlReq.send();
	} catch( e ) {
		//debugger;
	}
};

XVW.keepAlive = function( oForm ) {
	try {
		var sActionUrl = XVW.prv.getFormInput( oForm, 'xvw.ajax.resourceUrl').value;
		var xmlReq = XVW.createXMLHttpRequest();
	    xmlReq.open( "POST", sActionUrl+"/netgest/bo/xwc/framework/viewers/SystemOperations.xvw?KeepAlive=true", true );
	    xmlReq.onreadystatechange = function() {
			if( xmlReq.readyState==4 ) {
				if( xmlReq.status == 401 && xmlReq.getResponseHeader('login-url') != "" ){
	            	XVW.NoWait();
	            	XVW.ConfirmationDialog(XVW.Messages.SESSION_EXPIRED_TITLE,XVW.Messages.SESSION_EXPIRED_MESSAGE,
	            		function (){window.top.location.href = xmlReq.getResponseHeader('login-url');});
	            }
			}
	    };
	    xmlReq.send();
	} catch( e ) {
//		debugger;
	}
};

XVW.syncView = function( sFormId, iWaitScreen, queue ) {
    XVW.AjaxCommand( sFormId, null, null, iWaitScreen, false, null, queue );
};

XVW.prv = function() {};

XVW.prv.cmdCntr = 0;

XVW.prv.removeCommand = function( sFormId, sActionId ) {
    var oForm   = document.getElementById( sFormId );
    if( oForm != null )
    {
        var sId = 'dyn:'+sFormId+':'+sActionId;
        var oButton;
        oButton = document.getElementById( sId );
        if( oButton != null ) {
            oButton.parentNode.removeChild( oButton );
        }
    }
};

XVW.prv.getFormInput = function( oStartElement, sName ) {
	var oInpElems = oStartElement.getElementsByTagName('input');
	for( var i=0; i < oInpElems.length; i++ ) {
		if( oInpElems[i].name == sName ) 
			return oInpElems[i];
	}
	return null; 
};

XVW.prv.getViewStateInput = function( oElement ) {
    var oChilds = oElement.childNodes;

    for (var i = 0; i < oChilds.length; i++)  {
        if ( oChilds[i].name == 'javax.faces.ViewState' )
            return oChilds[i];

        XVW.prv.getViewStateInput( oChilds[i] );
    }
};



XVW.prv.createCommand = function( sFormId, sActionId, sActionValue ) {
    var oForm   = document.getElementById( sFormId );
    if( oForm != null )
    {
        var sId;

        if( sActionId.indexOf( sFormId + ':' ) == 0  ) {
        	sId = sActionId;
        } else {
        	sId = sFormId + ':' + sActionId;
        }
        var oButton;
        
        oButton = document.getElementById( sId );
        if( sActionId != null || oButton == null || !oButton.form || oButton.form != oForm )
        {
            oButton = document.createElement( 'input' );
            oButton.type='hidden';
            oButton.name = sId;
            oButton.id = 'dyn:' + sFormId + ':' + sActionId;
            if( typeof(sActionValue) != "undefined")
                oButton.value = sActionValue;
            else
                oButton.value = '';
            oForm.appendChild( oButton );
            
            var keepAlive = document.createElement( 'input' );
            keepAlive.type='hidden';
            keepAlive.name = 'CommandSubmit';
            keepAlive.value = 'true';
            oForm.appendChild( keepAlive );
            
            //Colocar uma input hidden com o KeepAlive
        }
    }
    else {
        XVW.ErrorDialog( XVW.Messages.AJAXERROR_TITLE, 'Form ['+sFormId+'] not Found!!!', 'error' );
    }
    return oForm;
};

/*
 * Put application in a wait Form
 * iWaitMode - 1 - Wait Dialog, 2 - Silent
 * Must be overwritten to the current look and feel
 */
XVW.Wait = function( iWaitMode ) {};

XVW.StillWorking = function ( message) {}; 

XVW.ResetWait = function () {};
/*
 * Clear the wait state of the application
 * Must be overwritten to the current look and feel
 */
XVW.NoWait = function() {};


XVW.creatXMLDocument = function() {
	var xmlDoc;
	try
	{
		if( window.ActiveXObject )
		{
			xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
	    }
		else {
			try {
				// FF3 Work around, by the default the enconding is ISO-8859-1
				xmlDoc = new DOMParser().parseFromString("<?xml version='1.0' encoding='UTF-8'?><dummy/>","application/xml");
				xmlDoc.removeChild( xmlDoc.documentElement );
			} catch(e) {
				xmlDoc = document.implementation.createDocument("","",null);
			}
		}
	}
    catch(e)
    {
        XVW.ErrorDialog( XVW.SysMsg.XMLDOC_ERROR, e.message );
    }
    if( xmlDoc !== null ) {
    	xmlDoc.createProcessingInstruction("xml", "version='1.0' encoding='UTF-8'");    	
    }
    return xmlDoc;
};

XVW.createXMLHttpRequest = function()
{
    var e1 = null;
    var e2 = null;
    var req = null;  
    if(window.XMLHttpRequest) {  
    	try {
            req = new XMLHttpRequest();   
        } catch (e) {
            e2 = e;
        }
    } else {
    	if (window.ActiveXObject)  {
            try {
        	req  = new ActiveXObject('Microsoft.XMLHTTP');      
            } catch (e) {
                e2 = e;
            }
        }
    }
    if( req === null ) {
        XVW.ErrorDialog( XVW.SysMsg.XMLHTTP_ERROR, e.message );
    }
    return req;
};

/**
 * Logs Javascript Errors to the server
 * */
XVW.logJsError = function (errorMessage, url, line, jsBlock){
	
	debugger;
	var sActionUrl = "";
	var sUrl = "";
	var forms = document.getElementsByTagName('form');
	for (var i = 0 ; i < forms.length ; i++ ){
		sActionUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.resourceUrl').value;
		sUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.submitUrl').value;
		if (sActionUrl !== null && sActionUrl !== undefined && sActionUrl.length > 0)
			break;
	}
	
	if (url == undefined || url == null){
		url = "";
	}
	
	if (sUrl == ""){
		sUrl = document.location.href;
	}
	
	var loggerUrl = sActionUrl + "netgest/bo/xwc/framework/viewers/JsErrorLogOperations.xvw";
	  var parameters = "?action=log&JS_ERROR_MESSAGE=" + encodeURI(errorMessage)
	      + "&VIEW_ID=" + encodeURI(sUrl)
	      + "&LINE=" + encodeURI(line)
	      + "&JS_FILE=" + encodeURI(url)
	      + "&USER_AGENT=" + encodeURI(navigator.userAgent);
	  
	  if (jsBlock !== null && jsBlock !== undefined){
		  parameters += ("&JS_ERROR_BLOCK=" + encodeURI(jsBlock));
	  }
	  
	  XVW.NoWait();
	 
	  /** Send error to server */
	  var xhr = XVW.createXMLHttpRequest();
	  xhr.open( "GET", loggerUrl + parameters, true );
	  xhr.send();
};

/**
 * Logs Ajax load times
 */
XVW.logAjaxTiming = function (requestTime, scriptProcessingTime){
	if (requestTime == 0)
		return ;
	
	var sActionUrl = "";
	var sUrl = "";
	var forms = document.getElementsByTagName('form');
	
	for (var i = 0 ; i < forms.length ; i++ ){
		sActionUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.resourceUrl').value;
		sUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.submitUrl').value;
		if (sActionUrl !== null && sActionUrl !== undefined && sActionUrl.length > 0)
			break;
	}
	var loggerUrl = sActionUrl + "netgest/bo/xwc/framework/viewers/AjaxTimingOperations.xvw";

	var parameters = "?action=log"
	      + "&VIEW_ID=" + encodeURI(sUrl)
	      + "&USER_AGENT=" + encodeURI(navigator.userAgent)
	      + "&REQUEST_TIME=" + parseInt(requestTime)
	      + "&PROCESS_TIME=" + parseInt(scriptProcessingTime);
	      
	 //FALTA AQUI POR OS TEMPOS     
	/** Send error to server */
	var xhr = XVW.createXMLHttpRequest();
	xhr.open( "GET", loggerUrl + parameters, true );
	xhr.send();
};

/**
 * Logs Request Timing (uses performance.timing) to the server to register performance
 * */
XVW.logTiming = function (timing){
	if (timing === null || timing === undefined){
		return ;
	}
	
	var sActionUrl = "";
	var sUrl = "";
	var forms = document.getElementsByTagName('form');
	for (var i = 0 ; i < forms.length ; i++ ){
		sActionUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.resourceUrl').value;
		sUrl = XVW.prv.getFormInput( forms[i], 'xvw.ajax.submitUrl').value;
		if (sActionUrl !== null && sActionUrl !== undefined && sActionUrl.length > 0)
			break;
	}
	var loggerUrl = sActionUrl + "netgest/bo/xwc/framework/viewers/TimingOperations.xvw";
	var parameters = "?action=log"
	      + "&VIEW_ID=" + encodeURI(sUrl)
	      + "&USER_AGENT=" + encodeURI(navigator.userAgent)
	  	  + "&NAVIGATIONSTART="	+ parseInt(timing.navigationStart)
		+ "&UNLOADEVENTSTART="+ parseInt(timing.unloadEventStart)
		+ "&UNLOADEVENTEND="+ parseInt(timing.unloadEventEnd)
		+ "&REDIRECTSTART="+ parseInt(timing.redirectStart)
		+ "&REDIRECTEND="+ parseInt(timing.redirectEnd)
		+ "&FETCHSTART="+ parseInt(timing.fetchStart)
		+ "&DOMAINLOOKUPSTART="+ parseInt(timing.domainLookupStart)
		+ "&DOMAINLOOKUPEND="+ parseInt(timing.domainLookupEnd)
		+ "&CONNECTSTART="+ parseInt(timing.connectStart)
		+ "&CONNECTEND="+ parseInt(timing.connectEnd)
		+ "&SECURECONNECTIONSTART="+ parseInt(timing.secureConnectionStart)
		+ "&REQUESTSTART="+ parseInt(timing.requestStart)
		+ "&RESPONSESTART="+ parseInt(timing.responseStart)
		+ "&RESPONSEEND="+ parseInt(timing.responseEnd)
		+ "&DOMLOADING="+ parseInt(timing.domLoading)
		+ "&DOMINTERACTIVE="+ parseInt(timing.domInteractive)
		+ "&DOMCONTENTLOADEDEVENTSTART="+ parseInt(timing.domContentLoadedEventStart)
		+ "&DOMCONTENTLOADEDEVENTEND="+ parseInt(timing.domContentLoadedEventEnd)
		+ "&DOMCOMPLETE="+ parseInt(timing.domComplete)
		+ "&LOADEVENTSTART="+ parseInt(timing.loadEventStart)
		+ "&LOADEVENTEND="+ parseInt(timing.loadEventEnd);
	 
	  /** Send error to server */
	  var xhr = XVW.createXMLHttpRequest();
	  xhr.open( "GET", loggerUrl + parameters, true );
	  xhr.send();
};

/**
 * Attach the Error Logger to the onError event
 * */
window.onerror = function(errorMessage, url, line) {
	  XVW.logJsError(errorMessage,url,line,null);
	  return true;
};

/**
 * Associate the load event with the function to register performance timing
 * make it with a small delay after the load event
 * */
(function(window){
	
	//Firefox, Chrome, IE9+
	if (window.addEventListener){
		if (window.performance){
			window.addEventListener('load', function (){
				window.setTimeout( function(){XVW.logTiming(window.performance.timing);} , 300);
				}, false);
		}
	} else {
		//IE 7,8 or IE in compability mode
		if (window.attachEvent){
			if (window.performance){
				window.attachEvent('onload', function (){
					window.setTimeout( function(){XVW.logTiming(window.performance.timing);} , 300);
				}, false);
			}
		}
	}
	
	
	
})(window);

XVW.beforeApplyHtml = function( oDNode ) {};




// XVW Events ........
XVW.events = function() {};
XVW.events.beforeSubmit = [];
XVW.events.afterSubmit = [];
XVW.events.beforeAjaxRequest = [];
XVW.events.readyAjaxRequest = [];
XVW.events.afterAjaxRequest = [];

