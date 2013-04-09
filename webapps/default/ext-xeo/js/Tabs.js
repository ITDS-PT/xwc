Ext.ns('ExtXeo');
ExtXeo.Tab = Ext.extend(Ext.Component, {
    minHeight: 0,
    initComponent: function(){
		ExtXeo.Tab.superclass.initComponent.apply(this, arguments);
    },
    onRender: function(){
        //MyScope.superclass.onRender.apply(this, arguments);
    },
    getMinHeight: function() {
    	return this.minHeight;
    },
    setMinHeight: function( minHeight ) {
    	this.minHeight = minHeight;
    },
    setHeight : function( height ) {
    }
});
