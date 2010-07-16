<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/" priority="-1">
        <html>
            <head>
                <BASE href="../../" ></BASE>
                <!-- As CSS desta página -->
                <style type="text/css" media="screen">
                	body
                	{
                		background-color: white;
                	}
                    p {
                        color:#ff9900;
                        font-size: 90%;
                    }
                    h1 {
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                    }
                    h2 {
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                        font-size: 110%;
                    }
                    h2.innerTab {
                        margin-left: 10px;
                        padding-left: 5px;
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                        font-size: 100%;
                    }
                    div.innerTabDiv {
                        border-top:1px solid #171330;
                        margin-left: 10px;
                        padding-left: 5px;
                    }
                    div.tab {
                        border-bottom:2px solid #171330;
                        margin-bottom: 1px;
                        padding-bottom: 1px;
                    }
                    div.title {
                        border-bottom:1px solid #26252B;
                        margin-bottom: 1px;
                        padding-bottom: 1px;
                    }
                    h3 {
                        color:#0000FF;
                    }
                    table.main {
                        background-color: #FFFFFF;
                        width: 100%;
                        font-size: 80%;
                        font-family: Verdana, Times New Roman;
                    }
                    table.main td.label {
                        color: #000000;
                        width: 10%;
                    }
                    table.main td.boolean {
                        background-color: #FFFFFF;
                    }
                    table.main td.value {
                        background-color: #EDEDED;
                        border-width: 1px;
                        border-style: solid;
                        border-color: #ADADAD;
                    }
                    
                    table.list
                    {
                        font-size : 90%;
                        font-family: Verdana;
                        width: 100%;
                        margin-top: 10px;
                        margin-bottom : 5px;
                    }
                    
                    table.list tr.headerrow
                    {
                        background-color: #EDEDED;
                        font-color: #0A2136;
                        font-weight: bold;
                        font-size: 85%;
                    }
                    
                    table.list tr.listrow
                    {
                        background-color: #EDEDED;
                        font-color: #000000;
                        font-size: 85%;
                    }
                    
                    table.list tr.listrow th
                    {
                        text-align: center;
                    }
                    
                    table.list tr.listrow td
                    {
                        text-align: center;
                    }
                </style>
            </head>
            <body>
            <a href='javascript:void(0)' onClick="window.print();">
                <img src="ext-xeo/images/print-icon.png" alt="Print" width="16" height="16"/>
            </a><br/><br/>
            
                <xsl:apply-templates select="html/body/div/*"/>
                <!-- <xsl:value-of select="html/body/div/@id"/> -->
            </body>
        </html>
    </xsl:template>

    <!-- Tabs de primeiro nível -->
    <xsl:template match="panel/tabs" priority="-1">
        <xsl:for-each select="tab">
         <div class="tab">
             <h1>
                 <xsl:value-of select="./@label"/>
             </h1>
         </div>
         <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="tab/tabs" priority="-1">
        <xsl:for-each select="tab">
            <div class="tab">
                <h2>
                    <xsl:value-of select="./@label"/>
                </h2>
            </div>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="formEdit" priority="-2">
        <xsl:value-of select="./title/text()" disable-output-escaping="yes"/>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Inner Tabs -->
    <xsl:template match="section/tabs/tab" priority="-1"> </xsl:template>

    <xsl:template match="panel" priority="-2">
        <div>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- 
    As secções traduzem-se por fieldsets
    -->
    <xsl:template match="section" priority="-1">
        <div class="title">
            <h2>
                <xsl:value-of select="@label"/>
            </h2>
        </div>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="errorMessages" priority="-1">
        <!-- a definir pelo JPC -->
        <!-- agora processa qualquer filho lá dentro .. -->
        <xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="rows" priority="-1">
        <!-- O início de conteúdo tabular -->
        <table class="main">
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


    <xsl:template match="attributeLabel" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td width="{ancestor::rows/@labelWidth}" class="label">
            <label>
                <xsl:value-of select="./@text"/>
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

    <xsl:template match="attributeText" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td class="value">
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
    <xsl:template match="attributeBoolean" priority="-1">
        <td class="boolean">
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
        <td class="value">
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
    
    <xsl:template match="attributeDateTime" priority="-1">
        <td class="value">
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
        <td class="value">
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
        <td class="value">
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
        <td class="value">
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count">
                    <xsl:value-of select="count(../../cell)"/>
                </xsl:with-param>
                <xsl:with-param name="total">
                    <xsl:value-of select="../../../@columns"/>
                </xsl:with-param>
            </xsl:call-template>
            <a href="#">
                <xsl:value-of select="./@displayValue"/>
            </a>
        </td>
    </xsl:template>

    <!-- Atributos do tipo object -->
    <xsl:template match="attributeNumberLookup" priority="-1">
        <td class="value">
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
        <td class="value">
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
        <td class="value">
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
    <xsl:template match="attributeHtmlEditor" priority="-1">
        <td class="value">
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

    <!-- Render para o GridPanel -->
    <xsl:template match="gridPanel" priority="-1">
        <table class="list">
            <xsl:apply-templates></xsl:apply-templates>
        </table>    
    </xsl:template>
    
    <xsl:template match="gridheaderrow" priority="-1">
        <tr class="headerrow">
            <xsl:apply-templates></xsl:apply-templates>
        </tr>
    </xsl:template>
    
    <xsl:template match="gridheadercolumn" priority="-1">
        <th>
            <xsl:apply-templates>
            </xsl:apply-templates>
        </th>
    </xsl:template>
    
    <xsl:template match="gridrow" priority="-1">
        <tr class="listrow">
            <xsl:apply-templates></xsl:apply-templates>
        </tr>
    </xsl:template>
    
    <xsl:template match="gridcolumn" priority="-1">
        <td>
            <xsl:value-of select="./@displayValue" disable-output-escaping="yes"/>
        </td>
    </xsl:template>
    
    

    <!-- copia tudo o resto que não conheço, ou seja html. Funciona pela prioridade baixa e devido à ordem de avaliação dos templates -->
    <xsl:template match="*|@*" priority="-10">
        <!--   <xsl:copy> -->
            <!-- <xsl:apply-templates select="*|@*|text()"/> -->
        <!-- </xsl:copy> -->
    </xsl:template>
    
    <xsl:template match="toolbar" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>
    
    <xsl:template match="script" priority="-1">
        <!-- Não se faz render da toolbar -->
    </xsl:template>

</xsl:stylesheet>
