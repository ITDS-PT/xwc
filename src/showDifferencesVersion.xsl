<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <!--  <xsl:template match="/" priority="-1">
      
                <style type="text/css">
                	div.diff
                	{
                		background-color: white;
                	}
                    p {
                        color:#ff9900;
                        font-size: 90%;
                    }
                    h1.diff {
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                    }
                    h2.diff {
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
                    
                    table.main tr.diffRow
                    {
                        color: red;
                        text-decoration: line-through;
                        font-weight:bold;
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
                        text-align:left;
                    }
                    
                    table.list tr.listrow th
                    {
                        text-align: left;
                    }
                    
                    table.list tr.headerrow th
                    {
                        font-weight:bold;
                        text-align:left;
                    }
                    
                    /* Old Value Row*/
                    table.list tr.listrowOld
                    {
                        color: red;
                        background-color: #EDEDED;
                        text-decoration: line-through;
                        font-weight:bold;
                    }
                    
                    /* New Value Row */
                    table.list tr.listrowNew
                    {
                        color: green;
                        background-color: #EDEDED;
                        font-weight:bold;
                    }
                    
                    table.list tr.listrow td
                    {
                        text-align: left;
                    }
                    
                    /* Table of differences */
                    table.differences
                    {
                      background-color: #FFFFFF;
                      width: 100%;
                      font-size: 80%;
                      font-family: Verdana, Times New Roman;
                    }
                    
                    table.differences td.label 
                    {
                        color: #000000;
                        width: 10%;
                    }
                    
                    table.differences td.oldvalue 
                    {
                         color: green;
                         font-weight:bold;
                         background-color: #EDEDED;
                         border-width: 1px;
                         border-style: solid;
                         border-color: #ADADAD;
                    }
                    
                    table.differences td.newvalue 
                    {
                        color: red;
                        font-weight:bold;
                        background-color: #EDEDED;
                        border-width: 1px;
                        border-style: solid;
                        border-color: #ADADAD;
                    }
                </style>
                <div class="diff">
                <xsl:apply-templates select="html/differences" mode="diff"/>
                    <xsl:apply-templates select="html/body/div/*"/>
                </div>
        
        </xsl:template> -->
    
    <xsl:template match="/" priority="-1">
        <!--<html>
            <head>--> 
                <style type="text/css">
                	body
                	{
                		background-color: white;
                	}
                    p {
                        color:#ff9900;
                        font-size: 90%;
                    }
                    h1.title{
                        font-size:150%;
                    }
                    
                    h1 {
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                        font-size: 120%;
                    }
                    h2 {
                        color:#5E5B5B;
                        padding-bottom : 1px;
                        margin-bottom : 1px;
                        font-size: 90%;
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
                    
                    td.innerMain{
                        font-size: 80%;
                        font-family: Verdana, Times New Roman;
                        color: #000000;
                    }
                    
                    table.main td.label {
                        color: #000000;
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
                    
                    tr.diffRow
                    {
                        color: red;
                        text-decoration: line-through;
                        font-weight:bold;
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
                        text-align: left;
                    }
                    
                    table.formProps{
                        font-color: #000000;
                        font-size: 8px;
                        text-align:left;
                    }
                    
                    span.lbl{
                        font-weight:bold;
                    }
                    
                    /* Old Value Row*/
                    table.list tr.listrowOld
                    {
                        color: red;
                        background-color: #EDEDED;
                        text-decoration: line-through;
                        font-weight:bold;
                    }
                    
                    /* New Value Row */
                    table.list tr.listrowNew
                    {
                        color: green;
                        background-color: #EDEDED;
                        font-weight:bold;
                    }
                    
                    table.list tr.listrow td
                    {
                        text-align: left;
                    }
                    
                    /* Table of differences */
                    table.differences
                    {
                      background-color: #FFFFFF;
                      width: 100%;
                      font-size: 80%;
                      font-family: Verdana, Times New Roman;
                    }
                    
                    table.differences td.label 
                    {
                        color: #000000;
                        width: 10%;
                    }
                    
                    
                    
                    table.differences td.oldvalue 
                    {
                         color: red;
                         font-weight:bold;
                         text-decoration: line-through;
                         background-color: #EDEDED;
                         border-width: 1px;
                         border-style: solid;
                         border-color: #ADADAD;
                    }
                    
                    table.differences td.newvalue 
                    {
                        color: green;
                        font-weight:bold;
                        background-color: #EDEDED;
                        border-width: 1px;
                        border-style: solid;
                        border-color: #ADADAD;
                    }
                    
                    white{
                        background-color:white;
                    }
                    <!-- Para que estilos CSS também sejam incluidos, se existirem -->
                    <xsl:value-of select="//style/text()"/>
                </style>
            <!--</head> -->
            <body bgcolor="white">
                <div style="background-color:white">
                <xsl:apply-templates select="html/differences" mode="diff" />
                <xsl:apply-templates select="html/body/div/*"/>
                <hr></hr>
                <xsl:apply-templates select="html/body/div/formEdit/formEditObject">
                </xsl:apply-templates>
                </div>
            </body>
        <!--</html>-->
    </xsl:template>

    <!--<xsl:template match="panel/tabs" priority="-1">
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
    
    <xsl:template match="section/tabs/tab" priority="-1"> </xsl:template>

    <xsl:template match="panel" priority="-2">
        <div>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="section" priority="-1">
        <div class="title">
            <h2 class="diff">
                <xsl:value-of select="@label"/>
            </h2>
        </div>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="errorMessages" priority="-1">
        <xsl:apply-templates/>
    </xsl:template>

    -->
    
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
    
    <xsl:template match="formEdit/tabs" priority="-1">
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
        <h1 class="title">
            <xsl:value-of select="./title/@renderedValue" disable-output-escaping="yes"/>
        </h1>
        
        <xsl:apply-templates select="./*[local-name() != 'formEditObject']"/>
        
        
    </xsl:template>
    
    <xsl:template match="formEditObject" priority='-1'>
        <!--<div>
            <table class="formProps">
            <tr>
            <td><span class="lbl"><xsl:value-of select="./@creationDateLbl"/></span><xsl:text> </xsl:text><xsl:value-of select="./@creationDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="createUser/*"/>)
            <span class="lbl"><xsl:value-of select="./@lastUpdateDateLbl"/></span><xsl:text> </xsl:text><xsl:value-of select="./@lastUpdateDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="lastUpdateUser/*"/>)
            - <xsl:value-of select="./@boui"/>'v<xsl:value-of select="./@version"/>
            </td>
            </tr>
            </table>
            </div>-->
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
    
    
    
    
    <xsl:template match="rows" priority="-1">
        <!-- O início de conteúdo tabular -->
        <table width='{@width}' >
            <xsl:attribute name="cellpadding">
                <xsl:value-of select="./@cellpading"/>
            </xsl:attribute>
            <xsl:attribute name="cellspacing">
                <xsl:value-of select="./@cellspacing"/>
            </xsl:attribute>
            <xsl:copy-of select="colgroup"></xsl:copy-of>
            <xsl:apply-templates select="./*[local-name() != 'colgroup']"/>
        </table>
    </xsl:template>
    
    <xsl:template match="row" priority="-1">
        <xsl:if test="count(./@type) = 1">
            <tr class="diffRow">
                <xsl:apply-templates/>
            </tr>
        </xsl:if>
        <xsl:if test="count(./@type) = 0">
            <tr>
                <xsl:apply-templates/>
            </tr>    
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="cell" priority="-1">
        <td>
            <xsl:attribute name="colspan">
                <xsl:value-of select="@colSpan"/>
            </xsl:attribute>
            <xsl:apply-templates select="./*"/>    
        </td>
    </xsl:template>

    <!--<xsl:template match="attributeLabel" priority="-1">
        <td width="{ancestor::rows/@labelWidth}" class="label">
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

    <xsl:template match="attributeText" priority="-1">
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
        </xsl:template> -->
    
    <xsl:template match="outputHtml">
        <xsl:apply-templates select="./*|text()"/>
    </xsl:template>
    
    <xsl:template match="attributeLabel" priority="-1">
        <xsl:if test="count(ancestor::attribute) > 0">
            <td width="{ancestor::rows/@labelWidth}" class="label">
                <label>
                    <xsl:value-of select="./@text"/>
                </label>
            </td>
        </xsl:if>
        <xsl:if test="count(ancestor::attribute) = 0" >
            <label>
                <xsl:value-of select="./@text"/>
            </label>
        </xsl:if>
        
    </xsl:template>
    
    
    <xsl:template name="checkColSpan">
        <xsl:param name="count"></xsl:param>
        <xsl:param name="total"></xsl:param>
        
        <xsl:if test="$count = 1">
            <xsl:attribute name="colspan">
                <xsl:value-of select="($total * 2 ) - 1"/>
            </xsl:attribute>
        </xsl:if>
        
    </xsl:template>
    
    <xsl:template match="attributeText" priority="-1">
        <!-- ignorei os outros atributos do HTML -->
        <td class="value">
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
    
    
    <xsl:template match="attribute">
        <table class='main'>
            <xsl:copy-of select="colgroup"></xsl:copy-of>
            <tbody>
                <xsl:if test="count(ancestor::row/@type) = 1">
                    <tr class="diffRow">
                        <xsl:apply-templates select="./*[local-name() != 'colgroup']"></xsl:apply-templates>
                    </tr>
                </xsl:if>
                <xsl:if test="count(ancestor::row/@type) = 0">
                    <tr>
                        <xsl:apply-templates select="./*[local-name() != 'colgroup']"></xsl:apply-templates>
                    </tr>    
                </xsl:if>
            </tbody>
        </table>
    </xsl:template>
    
    <!-- Atributos booleanos -->
    <xsl:template match="attributeBoolean" priority="-1">
        <td class="boolean">
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
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributeWordMacro" priority="-1">
        <td class="value">
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    
    <xsl:template match="attributeDateTime" priority="-1">
        <td class="value">
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <!-- Atributos do tipo texto longo -->
    <xsl:template match="attributeTextArea" priority="-1">
        <td class="value">
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>
    
    <!-- Atributos do tipo número -->
    <xsl:template match="attributeNumber" priority="-1">
        <td class="value">
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributeImage" priority="-1">
        <xsl:if test="count(ancestor::cell) > 0">
            <table class='main'>
                <tbody>
                    <tr>
                        <td class="label">
                            <xsl:value-of select="@label"/>
                        </td>
                        <td>
                            <img align="left">
                                <xsl:attribute name="src">
                                    <xsl:value-of select="attributeLabel/@url"/>
                                </xsl:attribute>
                                <xsl:attribute name="width">
                                    <xsl:value-of select="@width"/>
                                </xsl:attribute>
                                <xsl:attribute name="height">
                                    <xsl:value-of select="@height"/>
                                </xsl:attribute>
                            </img>           
                        </td>
                    </tr>
                </tbody>
            </table>
        </xsl:if>
        <xsl:if test="count(ancestor::cell) = 0">
            
            <td class="value">
                <table class='main'>
                    <tbody>
                        <tr>
                            <td class="label">
                                <xsl:value-of select="@label"/>
                            </td>
                            <td>
                                <img>
                                    <xsl:attribute name="src">
                                        <xsl:value-of select="attributeLabel/@url"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="width">
                                        <xsl:value-of select="@width"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="height">
                                        <xsl:value-of select="@height"/>
                                    </xsl:attribute>
                                </img>           
                            </td>
                        </tr>
                    </tbody>
                </table>
            </td>
        </xsl:if>
    </xsl:template>
    
    <!-- Atributos do tipo ficheiro -->
    <xsl:template match="attributeFile" priority="-1">
        <td class="value">
            <a href="javascript:void(0)">
                <xsl:value-of select="./@displayValue"/>
            </a>
        </td>
    </xsl:template>
    
    <!-- Atributos do tipo object -->
    <xsl:template match="attributeNumberLookup" priority="-1">
        <td class="value">
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <xsl:template match="attributeOutput" priority="-1">
        <xsl:if test="count(ancestor::cell) > 0">
            <xsl:value-of select="./@renderedValue"/>
        </xsl:if>
        <xsl:if test="count(ancestor::cell) = 0">
            <td class="value">
                <xsl:value-of select="./@renderedValue"/>
            </td>
        </xsl:if>
        
    </xsl:template>
    
    <!-- Attribute Lov -->
    <xsl:template match="attributeLov" priority="-1">
        <td class="value">
            <xsl:value-of select="./@displayValue"/>
        </td>
    </xsl:template>
    
    <!-- Attribute Password -->
    <xsl:template match="attributePassword" priority="-1"> 
        <td class="value">
            <xsl:value-of select="string('•••••••••••')"/>
        </td>
    </xsl:template>
    
    
    <!-- Editor HTML -->
    <xsl:template match="attributeHtmlEditor" priority="-1">
        <td class="value">
            <xsl:copy-of select="./*|text()"/>
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
        <xsl:if test="./@type = 'listrowOld'">
            <tr class="listrowOld">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>
        </xsl:if>
        <xsl:if test="./@type = 'listrowNew'">
            <tr class="listrowNew">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>
        </xsl:if>
        <xsl:if test="count(./@type) = 0">
            <tr class="listrow">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>        
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="gridcolumn" priority="-1">
        <td>
            <xsl:value-of select="./@displayValue" disable-output-escaping="yes"/>
        </td>
    </xsl:template>
    
    <!-- Grid Top Panel -->
    <xsl:template match="gridPanelTop" priority="-1" mode="diff">
        <table class="list">
            <xsl:apply-templates></xsl:apply-templates>
        </table>    
    </xsl:template>
    
    <xsl:template match="gridTopheaderrow" priority="-1">
        <tr class="headerrow">
            <xsl:apply-templates></xsl:apply-templates>
        </tr>
    </xsl:template>
    
    <xsl:template match="gridTopheadercolumn" priority="-1">
        <th>
            <xsl:apply-templates>
            </xsl:apply-templates>
        </th>
    </xsl:template>
    
    <xsl:template match="gridToprow" priority="-1">
        <xsl:if test="./@type = 'listrowOld'">
            <tr class="listrowOld">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>
        </xsl:if>
        <xsl:if test="./@type = 'listrowNew'">
            <tr class="listrowNew">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>
        </xsl:if>
        <xsl:if test="count(./@type) = 0">
            <tr class="listrow">
                <xsl:apply-templates></xsl:apply-templates>
            </tr>        
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="gridTopcolumn" priority="-1">
        <td>
            <xsl:value-of select="./text()"/>
        </td>
    </xsl:template>


    <xsl:template match="*|@*" priority="-10">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="text()" priority="-10" />
    
    
    <!-- ELEMENTS TO IGNORE -->
    <xsl:template match="toolbar" priority="-1"/>
    <xsl:template match="script" priority="-1"/>
    <xsl:template match="errorMessages" priority="-1"/>
    <xsl:template match="title" priority="-1"/>
    
    



    <xsl:template match="differences" mode="diff">
        <h2 class="diff"><xsl:value-of select="./@label"/></h2>    
        <table class="differences">
        <xsl:for-each select="./attribute">
             <tr>
                 <td class="label">
                     <xsl:value-of select="./@name"/>
                 </td>
                 <td class="oldvalue">
                    <xsl:value-of select="./@newValue"/>
                 </td>
             </tr>
            <tr>
                <td />
                <td class="newvalue">
                    <xsl:value-of select="./@oldValue"/>
                </td>
            </tr>
        </xsl:for-each>
        </table>
        <p></p>
        <xsl:apply-templates mode="diff" select="./gridPanelTop">
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>
