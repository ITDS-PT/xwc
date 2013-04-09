XEOFormLayout = function( parentComponent )
{
    this.parentComponent = parentComponent;
    
    this.toolbarMenu = new Ext.menu.Menu({
        id: 'mainMenu',
        items: [
            {
                text: 'I like Ext',
                scope: this
            },
            {
                text: 'Ext for jQuery'
            },
            {
                text: 'I donated!'
            }
        ]
    });

    this.toolBar = new Ext.Toolbar( 
            {
                autoHeight: true,
                autoWidth: true,
                items:[
                        {
                            split: true,
                            text:'Guardar ',
                            menu: this.toolbarMenu  // assign menu by instance
                        },
                        {
                            text:'Fechar ',
                            scope:this,
                            handler: this.close
                        },
                        {
                            text:'Abrir Janela ',
                            scope:this,
                            handler: this.openSubObject
                        }                        
                    ]
            });    
    
    this.formPanel = new Ext.FormPanel({
        url:'save-form.php',
        frame:false,
        bodyStyle:'padding:5px',
        border: true,
        items: [
                
                    new Ext.TabPanel(
                        {
                            tabPosition:'bottom',
                            height:500,
                            activeTab:0,
                            items:[
                                    {
                                        title:'Geral',
                                        layout: 'table',
                                        layoutConfig: { columns: 2, extraCls: 'formTable' },
                                        border:true,
                                        extraCls: 'formTable',
                                        items:[{
                                                layout:'form',
                                                defaultType: 'textfield',
                                                border:false,
                                                anchor:'95%',
                                                items: 
                                                [{
                                                        fieldLabel: 'First Name',
                                                        name: 'first',
                                                        allowBlank:false
                                                }]
                                            },
                                            {
                                                layout:'form',
                                                defaultType: 'textfield',
                                                border:false,
                                                items: 
                                                [{
                                                        fieldLabel: 'First Name',
                                                        name: 'first',
                                                        allowBlank:false
                                                }]
                                            }                
                                                ]
                                            }
                                    ]
                            })
                    ]});
    
    XEOFormLayout.superclass.constructor.call( this,
                {
                    anchor:'100%',
                    title:'hello',
                    border:false,
                    frame:false,
                    closable: true,
                    html: ''                    
                } 
    );
    this.doLayout();
}

Ext.extend(XEOFormLayout, Ext.Panel,
{                 

            close : function()
            {
                this.parentComponent.closeTab( this ); 
            },
            
            openSubObject : function()
            {
                // create the window on the first click and reuse on subsequent clicks
                var win = new Ext.Window({
                        width:300,
                        height:300,
                        closeAction:'hide',
                        plain: true,
                        buttons: [{
                            text:'Submit',
                            disabled:true
                        },{
                            text: 'Close',
                            handler: function()
                            {
                                win.hide();
                            }
                        }]
                    });
                
                win.show(this.formPanel);
            }
                
        }
 );
