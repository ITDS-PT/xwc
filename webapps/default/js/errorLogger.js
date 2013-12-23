
ErrorLogger = {};

ErrorLogger.showInformation = function(){
	var elements = Ext.getCmp('form:errorList').getSelectionModel().getSelections();
	var username = '';
	var profile = '';
	var view = '';
	var request = '';
	var date = '';
	var host = '';
	var ajax = '';
	var stack = '';
	var bean = '';
	var event = '';
	var custom = '';
	
	if (elements.length > 0){
		for (var i = 0 ; i < elements.length ; i++){
			
			username = elements[i].get("USERNAME");
			view = elements[i].get("VIEW_ID");
			date = elements[i].get("DATE_EVENT");
			stack = elements[i].get("STACK_TRACE");
			bean = elements[i].get("BEAN_CONTEXT");
			event = elements[i].get("EVENT_CONTEXT");
			custom = elements[i].get("CUSTOM_CONTEXT");
			profile = elements[i].get("PROFILE_BOUI");
			host = elements[i].get("HOST");
			ajax = elements[i].get("IS_AJAX");
			request = elements[i].get("REQUEST_ID");
			
			break;
		}
		stack = stack.replace(/(\n)+/g, '<br />');
		bean = bean.replace(/(\n)+/g, '<br />');
		event = event.replace(/(\n)+/g, '<br />');
		custom = custom.replace(/(\n)+/g, '<br />');
		
		Ext.get('user').update(username);
		Ext.get('view').update(view);
		Ext.get('request').update(request);
		Ext.get('date').update(date);
		Ext.get('profile').update(profile);
		Ext.get('host').update(host);
		Ext.get('ajax').update(ajax);
		Ext.get('stack').update(stack);
		Ext.get('bean').update(bean);
		Ext.get('event').update(event);
		Ext.get('custom').update(custom);
		
	}
};

Ext.onReady(function(){
	Ext.getCmp('form:errorList').getSelectionModel().addListener('rowselect',ErrorLogger.showInformation);
});


