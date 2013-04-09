<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:variable name="baseUrl">
        <xsl:value-of select="string('http://localhost:8888/xeo/')"/>
    </xsl:variable>

    <xsl:attribute-set name="h1Style">
        <xsl:attribute name="style">color:#5E5B5B;padding-bottom : 1px;margin-bottom : 1px;font-weight:bold; font-size:26px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="h1StyleTitle">
        <xsl:attribute name="style">color:#5E5B5B;padding-bottom : 1px;margin-bottom : 1px;font-weight:bold; font-size:36px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="h2Style">
        <xsl:attribute name="style">color:#5E5B5B;padding-bottom : 1px;margin-bottom : 1px;font-size: 22px;font-weight:italic;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainStyle">
        <xsl:attribute name="style">background-color: white;width: 100%;font-size: 80%;font-family: Verdana</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainLabel">
        <xsl:attribute name="style">color: #000000;width: 10%;font-weight:bold;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainValueColumn">
        <xsl:attribute name="style">border-bottom-width: 1px;border-bottom-style: solid;border-color: #000000;</xsl:attribute>
        <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListStyle">
        <xsl:attribute name="style">font-size : 90%;font-family: Verdana;width: 100%;margin-top: 10px;margin-bottom : 5px;
            border-style:solid; border-width:1px;
        </xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListHeaderStyle">
        <xsl:attribute name="style">font-color: #000000;font-weight: bold;font-size: 85%;border-bottom-width:1px; border-bottom-style:solid;</xsl:attribute>
        <xsl:attribute name="bgcolor">#EDEDED</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListRowStyle">
        <xsl:attribute name="style">text-align:left; border-right-width:1px; border-right-style:solid;</xsl:attribute>
        <xsl:attribute name="bgcolor">#FFFFFF</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:template match="/" priority="-1">
        <html>
            <head>
                <BASE href="{$baseUrl}" ></BASE> 
            </head>
            <body>
                <xsl:apply-templates select="html/body/div/*"/>
                <hr></hr>
                <xsl:apply-templates select="html/body/div/formEdit/formEditObject"/>
                
            </body>
        </html>
    </xsl:template>

    <xsl:template match="panel/tabs" priority="-1">
        <xsl:for-each select="tab">
            <div class="tab">
                <h2 xsl:use-attribute-sets="h2Style">
                   
                    <xsl:value-of select="./@label"/>
                </h2>
                <p></p><p></p>
            </div>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="formEdit/tabs" priority="-1">
        <xsl:for-each select="tab">
            <div class="tab">
                <h2 xsl:use-attribute-sets="h2Style">
                    <xsl:value-of select="./@label"/>
                </h2>
                <p></p><p></p>
            </div>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="tab/tabs" priority="-1">
        <xsl:for-each select="tab">
            <div class="tab">
                <h3>
                    <xsl:value-of select="./@label"/>
                </h3>
                <p></p><p></p>
            </div>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="formEdit" priority="-2">
        <h1 xsl:use-attribute-sets="h1StyleTitle">
            <xsl:value-of select="./title/@renderedValue" disable-output-escaping="yes"/>
        </h1>
        <br></br><br></br>
        <xsl:apply-templates select="./*[local-name() != 'formEditObject']"/>
    </xsl:template>


    <xsl:template match="errorMessages">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- 
        
        ******************************
        *******************************
    -->


    <xsl:template match="rows" priority="-1">
        <!-- O início de conteúdo tabular -->
        <table xsl:use-attribute-sets="tableMainStyle">
            <xsl:attribute name="cellpadding">
                <xsl:value-of select="./@cellpading"/>
            </xsl:attribute>
            <xsl:attribute name="cellspacing">
                <xsl:value-of select="./@cellspacing"/>
            </xsl:attribute>
            <!-- <xsl:copy-of select="@*[(local-name() != 'labelPosition') and (local-name() != 'labelWidth')]"/>-->
            <xsl:apply-templates select="./*[local-name() != 'colgroup']"/>
        </table>
    </xsl:template>

       <xsl:template match="row" priority="-1">
        <!-- Cria uma nova linha numa tabela existente -->
        <tr>
            <xsl:apply-templates/>
        </tr>
    </xsl:template>

    <xsl:template match="cell" priority="-1">
        <xsl:apply-templates/>
    </xsl:template>
    
    <!-- Copy all html elements -->
    <xsl:template match="a|br|em|h1|h2|h3|h4|h5|h6|hr|ul|li|table|td|tr|th|thead|tbody|b|i|u|ol|span|font|div|input|option|p|img|pre">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates></xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    <!-- END Copy all html elements -->
    
    <xsl:template match="outputHtml">
        <xsl:apply-templates></xsl:apply-templates>
    </xsl:template>


    <xsl:template match="attributeLabel" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td width="{ancestor::rows/@labelWidth}" xsl:use-attribute-sets="tableMainLabel" >
            <label>
                <xsl:value-of select="@text"/>
            </label>
        </td>
    </xsl:template>
    
    <xsl:template match="formEditObject" priority='-1'>
        <div>
            <table class="formProps">
                <tr>
                    <td><span class="lbl"><xsl:value-of select="./@creationDateLbl"/></span><xsl:text> </xsl:text><xsl:value-of select="./@creationDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="createUser/*"/>)
                        <span class="lbl"><xsl:value-of select="./@lastUpdateDateLbl"/></span><xsl:text> </xsl:text><xsl:value-of select="./@lastUpdateDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="lastUpdateUser/*"/>)
                        - <xsl:value-of select="./@boui"/>'v<xsl:value-of select="./@version"/>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>


    <xsl:template name="checkColSpan">
           <xsl:param name="count"></xsl:param>
            <xsl:param name="total"></xsl:param>
            
            <xsl:if test="$count = 1">
                <xsl:attribute name="colspan">
                       <xsl:value-of select="($total * 2) - 1"/>
                </xsl:attribute>
            </xsl:if>
        
    </xsl:template>

    <xsl:template match="attributeText" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>    
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>

    

    <!-- Atributos booleanos -->
    <xsl:template match="attributeBoolean" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:if test="count(../../cell) = 1">
                <xsl:attribute name="colspan">
                    <xsl:value-of select="../../../@columns * 2"/>
                </xsl:attribute>
            </xsl:if>
            <input type="checkbox" disabled="disabled">
                <xsl:if test="./text() = 1">
                    <xsl:attribute name="checked">
                        <xsl:value-of select="string(checked)"/>
                    </xsl:attribute>
                </xsl:if>
            </input>
        </td>
    </xsl:template>

    <!-- Atributos do tipo data -->
    <xsl:template match="attributeDate" priority="-1">
         <xsl:if test="count(ancestor::attributeDatetime) = 0">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="attributeDatetime" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>

    <!-- Atributos do tipo texto longo -->
    <xsl:template match="attributeTextArea" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>

    <!-- Atributos do tipo número -->
    <xsl:template match="attributeNumber" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>

    <!-- Atributos do tipo ficheiro -->
    <xsl:template match="attributeFile" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributeWordMacro" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributeImage" priority="-1">
        <xsl:apply-templates select="attributeLabel"></xsl:apply-templates>
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <img>
                <xsl:attribute name="src">
                    <xsl:value-of select="attributeLabel/@url"/>
                </xsl:attribute>
            </img>
        </td>
    </xsl:template>

    <!-- Atributos do tipo object -->
    <xsl:template match="attributeNumberLookup" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>

    <!-- Attribute Lov -->
    <xsl:template match="attributeLov" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <!-- Attribute Password -->
    <xsl:template match="attributePassword" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="string('•••••••••••')"/>
        </td>
    </xsl:template>
    
    
    <xsl:template match="attributeOutput" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@renderedValue"/>
        </td>
    </xsl:template>
    
    


    <!-- Editor HTML -->
    <xsl:template match="attributeHtmlEditor" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue" disable-output-escaping="yes"/>
        </td>
    </xsl:template>


    <!-- Render para o GridPanel -->
    <xsl:template match="gridPanel" priority="-1">
        <table xsl:use-attribute-sets="tableListStyle">
            <xsl:apply-templates></xsl:apply-templates>
        </table>    
    </xsl:template>
    
    <xsl:template match="gridheaderrow" priority="-1">
        <tr xsl:use-attribute-sets="tableListRowStyle">
            <xsl:apply-templates></xsl:apply-templates>
        </tr>
    </xsl:template>
    
    <xsl:template match="gridheadercolumn" priority="-1">
        <th xsl:use-attribute-sets="tableListHeaderStyle">
            <xsl:apply-templates>
            </xsl:apply-templates>
        </th>
    </xsl:template>
    
    <xsl:template match="gridrow" priority="-1">
        <tr xsl:use-attribute-sets="tableListRowStyle">
            <xsl:apply-templates></xsl:apply-templates>
        </tr>
    </xsl:template>
    
    <xsl:template match="gridcolumn" priority="-1">
        <td>
            <xsl:if test="string-length(*//text()) > 0">
                <xsl:value-of select="*//text()"/>    
            </xsl:if>
            <xsl:if test="string-length(*//text()) = 0">
                <xsl:value-of select="./@displayValue" disable-output-escaping="yes"/>    
            </xsl:if>
        </td>
    </xsl:template>
    
    

    <!-- copia tudo o resto que não conheço, ou seja html. Funciona pela prioridade baixa e devido à ordem de avaliação dos templates -->
    <xsl:template match="*|@*" priority="-10">
        <!--   <xsl:copy> -->
        <xsl:apply-templates select="*|@*"/>
        <!-- </xsl:copy> -->
    </xsl:template>

    <xsl:template match="toolbar" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>
    
    <xsl:template match="pieChart">
        <div>
            <img>
                <xsl:attribute name="src">
                    <xsl:value-of select="./@urlHtml"/>
                </xsl:attribute>
            </img>
        </div>
    </xsl:template>
    
    <xsl:template match="script" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>
    
    <xsl:template match="title" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>

</xsl:stylesheet>
