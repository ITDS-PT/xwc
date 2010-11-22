// Send Command via Ajax
if (!window.XVWLoaded) {
	document
			.write('<script id="ext-base" src=".xeodeploy/extjs/adapter/ext/ext-base.js"><\/script>');
	document
			.write('<script id="ext-all" src=".xeodeploy/extjs/ext-all-debug.js"><\/script>');
	document
			.write('<script id="xwc-core" src=".xeodeploy/xwc/js/xwc-core.js"><\/script>');
	document
			.write('<script id="xwc-messages" src=".xeodeploy/xwc/js/localization/xwc-messages_pt.js"><\/script>');
	document
			.write('<script id="ext-xeo" src=".xeodeploy/ext-xeo/js/ext-xeo.js"><\/script>');
	document
			.write('<script id="ExtXeo.grid" src=".xeodeploy/ext-xeo/js/GridPanel.js"><\/script>');
	document
			.write('<script id="xwc-components" src=".xeodeploy/ext-xeo/js/xwc-components.js"><\/script>');
	document
			.write('<script id="ExtXeo.tabs" src=".xeodeploy/ext-xeo/js/Tabs.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter" src=".xeodeploy/extjs/grid/GridFilters.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-filter" src=".xeodeploy/extjs/grid/filter/Filter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-boolean" src=".xeodeploy/extjs/grid/filter/BooleanFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-date" src=".xeodeploy/extjs/grid/filter/DateFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-list" src=".xeodeploy/extjs/grid/filter/ListFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-numeric" src=".xeodeploy/extjs/grid/filter/NumericFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-string" src=".xeodeploy/extjs/grid/filter/StringFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-filter-object" src=".xeodeploy/extjs/grid/filter/ObjectFilter.js"><\/script>');
	document
			.write('<script id="xwc-grid-menu-editable" src=".xeodeploy/extjs/grid/menu/EditableItem.js"><\/script>');
	document
			.write('<script id="xwc-grid-menu-rangemenu" src=".xeodeploy/extjs/grid/menu/RangeMenu.js"><\/script>');
	document
			.write('<script id="ext-all-lang" src=".xeodeploy/extjs/build/locale/ext-lang-pt-min.js"><\/script>');
	document
			.write('<script id="ext-xeo-messages" src=".xeodeploy/ext-xeo/js/localization/ext-xeo-messages_pt.js"><\/script>');
	document
			.write('<script id="ext-xeo-app" src=".xeodeploy/ext-xeo/js/App.js"><\/script>');

	document
			.write('<link id="extjs_css" rel="stylesheet" href=".xeodeploy/extjs/resources/css/ext-all.css"><\/link>');
	document
			.write('<link id="extjs_css1" rel="stylesheet" href=".xeodeploy/ext-xeo/css/ext-xeo.css"><\/link>');
	document
			.write('<link id="ext-xeo-nohtmleditor" rel="stylesheet" href=".xeodeploy/ext-xeo/css/ext-xeo-nohtmleditor.css"><\/link>');
	document
			.write("<script id='xwc_portlet' src='.xeodeploy/xwc/js/xwc-portlet.js'></script>");

}
window.XVWLoaded = true;

// Send Command via Ajax
XVW.AjaxCommand = function(sFormId, sActionId, sActionValue, iWaitScreen,
		bSubmitValues, renderOnElement) {

	if (bSubmitValues == undefined) {
		bSubmitValues = true;
	}
	window.ts = new Date();

	// Create a button to Submit the action
	XVW.Wait(iWaitScreen);

	var oForm = document.getElementById(sFormId);
	if (oForm != null) {
		if ("multipart/form-data" == oForm.enctype) {
			return XVW.Command(sFormId, sActionId, sActionValue, iWaitScreen);
		}

		// Generate Ajax request
		var oParNames = [];
		var oParValues = [];

		// Read all form INPUT's
		if (bSubmitValues) {
			XVW.AjaxCommand.pFnParseNode(oForm, oParNames, oParValues);
		} else {
			// Envia o estado da view actual
			var oInput = XVW.prv.getViewStateInput(oForm);
			// var oInput = document.getElementById('javax.faces.ViewState');~
			if (oInput != null) {
				alert(oInput);
				oParNames[oParNames.length] = oInput.name;
				oParValues[oParValues.length] = oInput.value;
			}
		}

		// Add the command and parameter
		if (sActionId != null) {
			var sId;
			if (sActionId.indexOf(':') == 0) {
				sId = sActionId.substring(1);
			} else if (sActionId.indexOf(sFormId + ':') == 0) {
				sId = sActionId;
			} else {
				sId = sFormId + ':' + sActionId;
			}

			var iCnt = oParNames.length;
			oParNames[iCnt] = sId;
			if (typeof (sActionValue) != "undefined")
				oParValues[iCnt] = sActionValue;
			else
				oParValues[iCnt] = '';
		}

		// Generate XML for AJAX request
		var reqDoc = XVW.creatXMLDocument();

		var eRoot = reqDoc.createElement("xvwAjaxReq");
		var eParam = reqDoc.createElement("parameters");

		reqDoc.appendChild(eRoot);
		eRoot.appendChild(eParam);

		var oParNode;
		for ( var i = 0; i < oParNames.length; i++) {
			oParNode = reqDoc.createElement("p");
			oParNode.setAttribute("name", oParNames[i]);
			oParNode.appendChild(reqDoc.createCDATASection(oParValues[i]));
			eParam.appendChild(oParNode);
		}

		// Faz Set da variável que indica que é portlet ajax
		oParNode = reqDoc.createElement("p");
		oParNode.setAttribute("name", "xvw.portletajax");
		oParNode.appendChild(reqDoc.createCDATASection("true"));
		eParam.appendChild(oParNode);

		var sActionUrl = XVW.prv.getFormInput(oForm, 'xvw.ajax.submitUrl').value;
		submitAjax(sActionUrl, reqDoc, renderOnElement);

	}
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
                loParNames[ iCnt ] = oChild.name;
                loParValues[ iCnt ] = oChild.value;
                iCnt++;
            }
        }
        if( oChild.hasChildNodes() ) {
            XVW.AjaxCommand.pFnParseNode( oChild, loParNames, loParValues );
        }
    }
}

//Redefine Command
XVW.Command = function(sFormId, sActionId, sActionValue, iWaitScreen,
		bSubmitValues, renderOnElement) {

	if (bSubmitValues == undefined) {
		bSubmitValues = true;
	}
	window.ts = new Date();

	// Create a button to Submit the action
	XVW.Wait(iWaitScreen);

	var oForm = document.getElementById(sFormId);
	if (oForm != null) {
		if ("multipart/form-data" == oForm.enctype) {
			return XVW.Command(sFormId, sActionId, sActionValue, iWaitScreen);
		}

		// Generate Ajax request
		var oParNames = [];
		var oParValues = [];

		// Read all form INPUT's
		if (bSubmitValues) {
			XVW.AjaxCommand.pFnParseNode(oForm, oParNames, oParValues);
		} else {
			// Envia o estado da view actual
			var oInput = XVW.prv.getViewStateInput(oForm);
			// var oInput = document.getElementById('javax.faces.ViewState');~
			if (oInput != null) {
				alert(oInput);
				oParNames[oParNames.length] = oInput.name;
				oParValues[oParValues.length] = oInput.value;
			}
		}

		// Add the command and parameter
		if (sActionId != null) {
			var sId;
			if (sActionId.indexOf(':') == 0) {
				sId = sActionId.substring(1);
			} else if (sActionId.indexOf(sFormId + ':') == 0) {
				sId = sActionId;
			} else {
				sId = sFormId + ':' + sActionId;
			}

			var iCnt = oParNames.length;
			oParNames[iCnt] = sId;
			if (typeof (sActionValue) != "undefined")
				oParValues[iCnt] = sActionValue;
			else
				oParValues[iCnt] = '';
		}

		// Generate XML for AJAX request
		var reqDoc = XVW.creatXMLDocument();

		var eRoot = reqDoc.createElement("xvwAjaxReq");
		var eParam = reqDoc.createElement("parameters");

		reqDoc.appendChild(eRoot);
		eRoot.appendChild(eParam);

		var oParNode;
		for ( var i = 0; i < oParNames.length; i++) {
			oParNode = reqDoc.createElement("p");
			oParNode.setAttribute("name", oParNames[i]);
			oParNode.appendChild(reqDoc.createCDATASection(oParValues[i]));
			eParam.appendChild(oParNode);
		}

		// Faz Set da variável que indica que é portlet ajax
		oParNode = reqDoc.createElement("p");
		oParNode.setAttribute("name", "xvw.portletajax");
		oParNode.appendChild(reqDoc.createCDATASection("true"));
		eParam.appendChild(oParNode);

		var sActionUrl = XVW.prv.getFormInput(oForm, 'xvw.ajax.submitUrl').value;
		submitAjax(sActionUrl, reqDoc, renderOnElement);

	}
}
//XVW.Command = function(sFormId, sActionId, sActionValue, iWaitScreen,
//		bSubmitValues, renderOnElement) {
//	if (!renderOnElement) {
//		renderOnElement = document.getElementById("resultViewerDiv");
//	}
//
//	// XVW.Command(sFormId, sActionId, sActionValue, iWaitScreen );
//	if (bSubmitValues == undefined) {
//		bSubmitValues = true;
//	}
//	window.ts = new Date();
//
//	// Create a button to Submit the action
//	XVW.Wait(iWaitScreen);
//
//	var oForm = document.getElementById(sFormId);
//	if (oForm != null) {
//		if ("multipart/form-data" == oForm.enctype) {
//			return XVW.Command(sFormId, sActionId, sActionValue, iWaitScreen);
//		}
//
//		// Generate Ajax request
//		var oParNames = [];
//		var oParValues = [];
//
//		// Read all form INPUT's
//		if (bSubmitValues) {
//			XVW.AjaxCommand.pFnParseNode(oForm, oParNames, oParValues);
//		} else {
//			// Envia obrigatoriamente o estado da view actual
//			var oInput = XVW.prv.getViewStateInput(oForm);
//			// var oInput = document.getElementById('javax.faces.ViewState');
//			oParNames[oParNames.length] = oInput.name;
//			oParValues[oParValues.length] = oInput.value;
//		}
//
//		// Add the command and parameter
//		if (sActionId != null) {
//			var sId;
//			if (sActionId.indexOf(':') == 0) {
//				sId = sActionId.substring(1);
//			} else if (sActionId.indexOf(sFormId + ':') == 0) {
//				sId = sActionId;
//			} else {
//				sId = sFormId + ':' + sActionId;
//			}
//
//			var iCnt = oParNames.length;
//			oParNames[iCnt] = sId;
//			if (typeof (sActionValue) != "undefined")
//				oParValues[iCnt] = sActionValue;
//			else
//				oParValues[iCnt] = '';
//		}
//
//		// Generate XML for AJAX request
//		var reqDoc = XVW.creatXMLDocument();
//
//		var eRoot = reqDoc.createElement("xvwAjaxReq");
//		var eParam = reqDoc.createElement("parameters");
//
//		reqDoc.appendChild(eRoot);
//		eRoot.appendChild(eParam);
//
//		var oParNode;
//		for ( var i = 0; i < oParNames.length; i++) {
//			oParNode = reqDoc.createElement("p");
//			oParNode.setAttribute("name", oParNames[i]);
//			oParNode.appendChild(reqDoc.createCDATASection(oParValues[i]));
//			eParam.appendChild(oParNode);
//		}
//
//		// Faz Set da variável que indica que é portlet ajax
//		oParNode = reqDoc.createElement("p");
//		oParNode.setAttribute("name", "xvw.portletajax");
//		oParNode.appendChild(reqDoc.createCDATASection("true"));
//		eParam.appendChild(oParNode);
//
//		var sActionUrl = XVW.prv.getFormInput(oForm, 'xvw.ajax.submitUrl').value;
//		/*
//		 * if( renderOnElement ) { var children =
//		 * renderOnElement.getElementsByTagName('div'); if(children.length > 0) {
//		 * ExtXeo.destroyComponents1(children[0]); }
//		 * renderOnElement.innerHTML=""; } submitAjax( sActionUrl, reqDoc,
//		 * renderOnElement );
//		 */
//		submitAjaxPortlet(sActionUrl, reqDoc, renderOnElement)
//	}
//}

function loadViewer(url, divId) {
	XVW.AjaxCommand("portletForm", null, null, '0', true, document
			.getElementById(divId));
}