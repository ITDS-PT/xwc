<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:param name="id"></xsl:param>
    
    <xsl:template match="bo-config">
        <h1><img src="images/xeoboconfig_icon.gif" /> BoConfig</h1>
        
        <div>
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('generalDiv',$id)"></xsl:value-of>
            </xsl:attribute>
        </div>
        <div>
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('generalPanel',$id)"></xsl:value-of>
            </xsl:attribute>
            <table class='relations'>
               <tr>
                   <td>Bodef Directory</td>
                   <td><input type="text" disabled="disabled">
                       <xsl:attribute name="value">
                           <xsl:value-of select="./definitiondir/text()"/>
                       </xsl:attribute>
                   </input></td>
               </tr> 
                <tr>
                    <td>Uidef Directory</td>
                    <td><input type="text" disabled="disabled">
                        <xsl:attribute name="value">
                            <xsl:value-of select="./uidefinitiondir/text()"/>
                        </xsl:attribute>
                    </input></td>
                </tr>
                <tr>
                    <td>WebContext Root</td>
                    <td><input type="text" disabled="disabled">
                        <xsl:attribute name="value">
                            <xsl:value-of select="./webcontextroot/text()"/>
                        </xsl:attribute>
                    </input></td>
                </tr>
                <tr>
                    <td>Encoding</td>
                    <td><input type="text" disabled="disabled">
                        <xsl:attribute name="value">
                            <xsl:value-of select="./encoding/text()"/>
                        </xsl:attribute>
                    </input></td>
                </tr>
            </table>
        
        </div>
        <xsl:apply-templates select="./deployment">
        	<xsl:with-param name="id" select="$id"></xsl:with-param>
        </xsl:apply-templates>
        <xsl:apply-templates select="./threads">
        <xsl:with-param name="id" select="$id"></xsl:with-param>
        </xsl:apply-templates>
        <xsl:apply-templates select="./DataSources">
        	<xsl:with-param name="id" select="$id"></xsl:with-param>
        </xsl:apply-templates>
        <div>
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('otherDiv',$id)"></xsl:value-of>
            </xsl:attribute>
        </div>
        
        <div>
        <xsl:attribute name="id">
                    <xsl:value-of select="concat('otherPanel',$id)"></xsl:value-of>
            </xsl:attribute>
        <xsl:apply-templates select="./mail"></xsl:apply-templates>
        <xsl:apply-templates select="./Repositories"></xsl:apply-templates>
        <xsl:apply-templates select="./wordTemplate"></xsl:apply-templates>
        <xsl:apply-templates select="./win32Client"></xsl:apply-templates>
        </div>
        
       <!--  <script type="text/javascript">
            Ext.onReady(function(){
            var generalPanel = new Ext.Panel({
            title: 'General Definitions',
            collapsible:true,
            allowDomMove : false,
            <xsl:value-of select="concat('renderTo:',&quot;&apos;&quot;,'generalDiv',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            <xsl:value-of select="concat('contentEl:',&quot;&apos;&quot;,'generalPanel',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            autoWidth : true
            });
            });	
        </script>
        
        <script type="text/javascript">
            Ext.onReady(function(){
            var generalPanel = new Ext.Panel({
            title: 'Deployment Configurations',
            collapsible:true,
            allowDomMove : false,
            <xsl:value-of select="concat('renderTo:',&quot;&apos;&quot;,'deploymentDiv',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            <xsl:value-of select="concat('contentEl:',&quot;&apos;&quot;,'deploymentPanel',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            autoWidth : true
            });
            });	
        </script>
        
        <script type="text/javascript">
            Ext.onReady(function(){
            var generalPanel = new Ext.Panel({
            title: 'EJB Thread',
            allowDomMove : false,
            collapsible:true,
            <xsl:value-of select="concat('renderTo:',&quot;&apos;&quot;,'ejbDiv',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            <xsl:value-of select="concat('contentEl:',&quot;&apos;&quot;,'ejbPanel',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            autoWidth : true
            });
            });	
        </script>
        
        <script type="text/javascript">
            Ext.onReady(function(){
            var generalPanel = new Ext.Panel({
            title: 'Data Sources',
            allowDomMove : false,
            collapsible:true,
            <xsl:value-of select="concat('renderTo:',&quot;&apos;&quot;,'sourcesDiv',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            <xsl:value-of select="concat('contentEl:',&quot;&apos;&quot;,'sourcesPanel',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            autoWidth : true
            });
            });	
        </script>
        
        <script type="text/javascript">
            Ext.onReady(function(){
            var generalPanel = new Ext.Panel({
            title: 'Other Configurations',
            allowDomMove : false,
            collapsible:true,
            <xsl:value-of select="concat('renderTo:',&quot;&apos;&quot;,'otherDiv',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            <xsl:value-of select="concat('contentEl:',&quot;&apos;&quot;,'otherPanel',$id,&quot;&apos;&quot;,',')"></xsl:value-of>
            autoWidth : true
            });
            });	
        </script> -->
        
        
    </xsl:template> 
    
    
    <xsl:template match="deployment">
        <div >
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('deploymentDiv',$id)"></xsl:value-of>
            </xsl:attribute>
        </div>
        <div>
           <xsl:attribute name="id">
                    <xsl:value-of select="concat('deploymentPanel',$id)"></xsl:value-of>
            </xsl:attribute>
                <table class='relations'>
                    <tr>
                        <td>Class Directory</td>
                        <td><input type="text" disabled="disabled" size='100%'>
                            <xsl:attribute name="value">
                                <xsl:value-of select="./class_dir/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr> 
                    <tr>
                        <td>Source Directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./src_dir/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Deployed Objects Directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./obj_dir/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Object Compiler Directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./obj_compiler/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Templates Directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./obj_templates/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Deployed JSP directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./obj_deployjspdir/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Libraries Directory</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./lib_dir/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>Tablespace</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./tablespace/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                </table>
            
        </div>
    </xsl:template>
    
    <xsl:template match="threads">
        <div>
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('ejbDiv',$id)"></xsl:value-of>
            </xsl:attribute>
        </div>
        <div>
        <xsl:attribute name="id">
                    <xsl:value-of select="concat('ejbPanel',$id)"></xsl:value-of>
        </xsl:attribute>
           EJB Threads (Type=<xsl:value-of select="./@type"/>)
            
           <table class="relations">
               <tr>
               <th>Name</th>
               <th>Class</th>
               <th>EJB Name</th>
               <th>Interval (ms)</th>
               </tr>
               <xsl:for-each select="./thread">
                   <tr>
                       <td><xsl:value-of select="./@name"/></td>
                       <td><xsl:value-of select="./@class"/></td>
                       <td><xsl:value-of select="./@ejb-name"/></td>
                       <td><xsl:value-of select="./@interval"/></td>
                   </tr>
               </xsl:for-each>
           </table>
        </div>
    </xsl:template>
    
    <xsl:template match="DataSources">
        <div>
        	<xsl:attribute name="id">
                    <xsl:value-of select="concat('sourcesDiv',$id)"></xsl:value-of>
        </xsl:attribute>
        </div>
        <div>
       <xsl:attribute name="id">
                    <xsl:value-of select="concat('sourcesPanel',$id)"></xsl:value-of>
        </xsl:attribute>
        <table class="relations">
            <tr>
                <th>Name</th>
                <th>boql</th>
                <th>Driver</th>
                <th>DML</th>
                <th>DDL</th>
            </tr>
            <xsl:for-each select="./DataSource">
                <tr>
                    <td><xsl:value-of select="./@name"/></td>
                    <td><xsl:value-of select="./@boql"/></td>
                    <td><xsl:value-of select="./Driver/text()"/></td>
                    <td><xsl:value-of select="./DML/text()"/></td>
                    <td><xsl:value-of select="./DDL/text()"/></td>
                </tr>
            </xsl:for-each>
        </table>
       
        </div>
    </xsl:template>
    
    <xsl:template match="authentication">
        <form name="authentication">
            <fieldset>
                <legend>Authentication Configurations</legend>
                <table>
                    <tr>
                        <td>Authentication Class</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./authclass/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr> 
                    <tr>
                        <td>Use Single Sign On (SSO)</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./usesso/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>LDAP Server</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./ldapserver/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>LDAP User DN</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./ldapuserdn/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>LDAP Group DN</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./ldapgroupdn/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>LDAP Admin</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./ldapadmin/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                    <tr>
                        <td>LDAP Admin Password</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./ldapadminpassword/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                </table>
            </fieldset>
        </form>
    </xsl:template>
    
    <xsl:template match="mail">
            <fieldset>
                <legend>Mail Configurations</legend>
                <table class='relations'>
                    <tr>
                        <td>SMTP Host</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./smtphost/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr> 
                    <tr>
                        <td>POP3 Host</td>
                        <td><input type="text" disabled="disabled">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./pophost/text()"/>
                            </xsl:attribute>
                        </input></td>
                    </tr>
                </table>
            </fieldset>
    </xsl:template>
    
    <xsl:template match="Repositories">
        <fieldset>
            <legend>Repositories</legend>
        <table class="relations">
            <tr>
                <th>Name</th>
                <th>Username</th>
                <th>Password</th>
                <th>DataSource</th>
                <th>DataSourceDef</th>
                <th>Schema</th>
                <th>Parent</th>
            </tr>
            <xsl:for-each select="./Repository">
                <tr>
                    <td><xsl:value-of select="./Name/text()"/></td>
                    <td><xsl:value-of select="./UserName/text()"/></td>
                    <td><xsl:value-of select="./Password/text()"/></td>
                    <td><xsl:value-of select="./DataSource/text()"/></td>
                    <td><xsl:value-of select="./DataSourceDef/text()"/></td>
                    <td><xsl:value-of select="./Schema/text()"/></td>
                    <td><xsl:value-of select="./Parent/text()"/></td>
                </tr>
            </xsl:for-each>
        </table>
        </fieldset>
    </xsl:template>
    
    <xsl:template match="wordTemplate">
        <fieldset>
            <legend>Word Template</legend>
        		Microsoft Word Template Directory : <xsl:value-of select="./path"/>
        </fieldset>    
    </xsl:template>
    
    <xsl:template match="win32Client">
        <fieldset>
            <legend>Win 32 Client</legend>
		        Win32 Client Jar
		        <table class="relations">
		        <tr>
		            <td>Name of Jar</td>
		            <td><input type="text" disabled="disabled">
		            <xsl:attribute name="value">
                                <xsl:value-of select="./name/text()"/>
                    </xsl:attribute>
                   </input></td>
		        </tr>
		        <tr>
		            <td>Path to Jar</td>
		            <td><input type="text" disabled="disabled">
		            <xsl:attribute name="value">
                                <xsl:value-of select="./path/text()"/>
                    </xsl:attribute> </input></td>
	            </tr>
	            <tr>
		            <td>Version of Win32 Client </td>
		            <td><input type="text" disabled="disabled">
		            	<xsl:attribute name="value">
                                <xsl:value-of select="./version/text()"/>
                    </xsl:attribute>
		            </input></td>
		            
		        </tr>
		        </table>
        </fieldset>    
    </xsl:template>
    
    <xsl:template match="*"></xsl:template>
</xsl:stylesheet>
