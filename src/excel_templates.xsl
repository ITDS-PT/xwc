<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>


    <xsl:attribute-set name="h1Style">
        <xsl:attribute name="style">color:#5E5B5B;padding-bottom : 1px;margin-bottom : 1px;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="h2Style">
        <xsl:attribute name="style">color:#5E5B5B;padding-bottom : 1px;margin-bottom : 1px;font-size: 110%;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainStyle">
        <xsl:attribute name="style">background-color: white;width: 100%;font-size: 80%;font-family: Verdana, Times New Roman;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainLabel">
        <xsl:attribute name="style">color: #000000;width: 10%;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableMainValueColumn">
        <xsl:attribute name="style">border-width: 1px;border-style: solid;border-color: #ADADAD;</xsl:attribute>
        <xsl:attribute name="bgcolor">#EDEDED</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListStyle">
        <xsl:attribute name="style">font-size : 90%;font-family: Verdana;width: 100%;margin-top: 10px;margin-bottom : 5px;</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListHeaderStyle">
        <xsl:attribute name="style">font-color: #000000;font-weight: bold;font-size: 85%;</xsl:attribute>
        <xsl:attribute name="bgcolor">#EDEDED</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="tableListRowStyle">
        <xsl:attribute name="style">text-align:center</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:template match="/" priority="-1">
        <html>
            <head>
                    
            </head>
            <body>
                <xsl:apply-templates/>
            </body>
        </html>
    </xsl:template>

    <!-- Tabs de primeiro nível -->
    <xsl:template match="formedit/panel/tabs/tab" priority="-1">
        <div class="tab">
            <h1 xsl:use-attribute-sets="h1Style">
                <xsl:value-of select="./@label"/>
            </h1>
        </div>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Inner Tabs -->
    <xsl:template match="section/tabs/tab" priority="-1"> </xsl:template>

    <xsl:template match="panel" priority="-1">
            <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="section" priority="-1">
        <div>
            <h2 xsl:use-attribute-sets="h2Style">
                <xsl:value-of select="@label"/>
            </h2>
        </div>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="errorMessages">
        <xsl:apply-templates/>
    </xsl:template>


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
            <xsl:apply-templates/>
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


    <xsl:template match="attributelabel" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td width="{ancestor::rows/@labelWidth}" xsl:use-attribute-sets="tableMainLabel" >
            <label>
                <xsl:value-of select="@text"/>
            </label>
        </td>
    </xsl:template>


    <xsl:template name="checkColSpan">
           <xsl:param name="count"></xsl:param>
            <xsl:param name="total"></xsl:param>
            
            <xsl:if test="$count = 1">
                <xsl:attribute name="colspan">
                       <xsl:value-of select="$total * 2"/>
                </xsl:attribute>
            </xsl:if>
        
    </xsl:template>

    <xsl:template match="attributetext" priority="-1">
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

    <!-- Tabs dentro de outras tabs -->
    <xsl:template match="tab/tabs/tab" priority="-1">
        <h2 class="innerTab">
            <xsl:value-of select="@label"/>
        </h2>
        <div class="innerTabDiv">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- Atributos booleanos -->
    <xsl:template match="attributeboolean" priority="-1">
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
    <xsl:template match="attributedate" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributedatetime" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue"/>
        </td>
    </xsl:template>

    <!-- Atributos do tipo texto longo -->
    <xsl:template match="attributetextarea" priority="-1">
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
    <xsl:template match="attributenumber" priority="-1">
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
    <xsl:template match="attributefile" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <a href="#">
                <xsl:value-of select="./@displayvalue"/>
            </a>
        </td>
    </xsl:template>

    <!-- Atributos do tipo object -->
    <xsl:template match="attributenumberlookup" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue"/>
        </td>
    </xsl:template>

    <!-- Attribute Lov -->
    <xsl:template match="attributelov" priority="-1">
        <td xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue"/>
        </td>
    </xsl:template>
    
    <!-- Attribute Password -->
    <xsl:template match="attributepassword" priority="-1">
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


    <!-- Editor HTML -->
    <xsl:template match="attributehtmleditor" priority="-1">
        <td  xsl:use-attribute-sets="tableMainValueColumn">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:value-of select="./@displayvalue"/>
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
            <xsl:value-of select="./@displayvalue"/>
        </td>
    </xsl:template>
    
    

    <!-- copia tudo o resto que não conheço, ou seja html. Funciona pela prioridade baixa e devido à ordem de avaliação dos templates -->
    <xsl:template match="*|@*" priority="-10">
        <!--   <xsl:copy> -->
        <xsl:apply-templates select="*|@*|text()"/>
        <!-- </xsl:copy> -->
    </xsl:template>

    <xsl:template match="toolbar" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>

</xsl:stylesheet>
