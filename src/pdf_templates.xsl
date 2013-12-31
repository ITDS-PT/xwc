<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet exclude-result-prefixes="xs" version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xd:doc xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Apr 3, 2010</xd:p>
            <xd:p><xd:b>Author:</xd:b> Pedro Rio</xd:p>
            <xd:p>
                XSLT to generate a XSL-Formating Objects (XSL-FO) file from a XVW Viewer Form
                so that it can be rendered as a PDF file
            </xd:p>
        </xd:desc>
    </xd:doc>
    
    <!-- Default parameters -->
    <xsl:param name="pageWidth"><xsl:value-of select="string('240mm')"/></xsl:param>
    <xsl:param name="pageHeight"><xsl:value-of select="string('297mm')"/></xsl:param>
    
    
    <!-- The base url for images -->
     <xsl:variable name="baseUrl">
        <xsl:value-of select="/html/head/base/@href"/>
    </xsl:variable>

    
    <!-- ****** Usefull Function Templates -->
    <xsl:template name="checkColSpan">
        <xsl:param name="count"></xsl:param>
        <xsl:param name="total"></xsl:param>
        
        <xsl:if test="$count = 1">
            <xsl:attribute name="number-columns-spanned">
                <xsl:value-of select="$total + 1"/>
            </xsl:attribute>
        </xsl:if>
     </xsl:template>
    
    
    <!--
    ********** Formatting Options *********
     Set of attributes that work like CSS
    -->
    
    <!-- ******* Titles  **********-->
    
    <!-- Main Tab Title -->
    <xsl:attribute-set name="tabTitle">
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="font-size">14px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">10px</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- Main title -->
    <xsl:attribute-set name="formTitle">
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="font-size">18px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">10px</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- A section title -->
    <xsl:attribute-set name="sectionTitle">
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="font-size">12px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">0.5px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">#26252B</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">5</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- Inner Tabs -->
    <xsl:attribute-set name="innerTabTitle">
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="font-size">12px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">0.5px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">5px</xsl:attribute>
        <xsl:attribute name="margin-left">15px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="innerTabContent">
        <xsl:attribute name="margin-left">10px</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- //////// End of titles -->
    
    <!-- ********** Table for grid panel -->
    
    <xsl:attribute-set name="gridPanelTbl">
        <xsl:attribute name="width">100%</xsl:attribute>
        <xsl:attribute name="margin-top">5px</xsl:attribute>
        <xsl:attribute name="margin-top">5px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="gridPanelTblHeader">
        <xsl:attribute name="background-color">#EDEDED</xsl:attribute>
        <xsl:attribute name="font-size">9px</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="padding-top">3px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="gridPanelRow">
        <xsl:attribute name="background-color">#FFFFFF</xsl:attribute>
        <xsl:attribute name="font-size">9px</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="padding-top">3px</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- /////////////////////// -->
    
    
    <!-- ******* Default text -->
    <xsl:attribute-set name="defaultText">
        <xsl:attribute name="font-family">Verdana</xsl:attribute>
        <xsl:attribute name="font-size">10px</xsl:attribute>
    </xsl:attribute-set>
    
   <xsl:attribute-set name="labelFormatting" use-attribute-sets="defaultText">
       <xsl:attribute name="color">black</xsl:attribute>
       <xsl:attribute name="font-size">10px</xsl:attribute>
   </xsl:attribute-set>
    
    <xsl:attribute-set name="attributeTextFormatting" use-attribute-sets="defaultText">
       <xsl:attribute name="color">black</xsl:attribute>
       <xsl:attribute name="background-color">#EDEDED</xsl:attribute>
       <xsl:attribute name="border-color">#ADADAD</xsl:attribute>
       <xsl:attribute name="border-width">1px</xsl:attribute>
       <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">3px</xsl:attribute>
        <xsl:attribute name="margin-right">8px</xsl:attribute>
    </xsl:attribute-set> 
    
    <xsl:attribute-set name="attributeEmptyFormatting" use-attribute-sets="defaultText">
        <xsl:attribute name="color">#EDEDED</xsl:attribute>
        <xsl:attribute name="background-color">#EDEDED</xsl:attribute>
        <xsl:attribute name="border-color">#ADADAD</xsl:attribute>
        <xsl:attribute name="border-width">1px</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">3px</xsl:attribute>
        <xsl:attribute name="margin-right">8px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="formEditObjectLabel" use-attribute-sets="defaultText">
        <xsl:attribute name="border-top-width">0.5px</xsl:attribute>
        <xsl:attribute name="border-top-color">gray</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="font-size">8px</xsl:attribute>
        <xsl:attribute name="color">gray</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- //// END OF CSS -->
    
    <!-- ******* Main Templates ************* -->
    <xsl:template match="/" priority="-1">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <!-- Define as margens a regiões -->
                <fo:simple-page-master master-name="content" page-width="{$pageWidth}" page-height="{$pageHeight}">
                    <fo:region-body margin="3cm"/>
                    <fo:region-before extent="2cm"/>
                    <fo:region-after extent="3cm"/>
                    <fo:region-start extent="2cm"/>
                    <fo:region-end extent="2cm"/>
                </fo:simple-page-master>
                
            </fo:layout-master-set>
            
            <fo:page-sequence master-reference="content">
                <fo:static-content flow-name="xsl-region-after">
                    <xsl:apply-templates select="html/body/div/formEdit/formEditObject"></xsl:apply-templates>
                    <fo:block font-size="12pt" text-align="right">
                        <fo:page-number/> /
                        <fo:page-number-citation ref-id="terminator"/>
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <xsl:apply-templates select="html/body/div/*"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    
    <xsl:template match='formEditObject'>
        <fo:block xsl:use-attribute-sets="formEditObjectLabel">
            <fo:inline font-weight="bold">
                <xsl:value-of select="./@creationDateLbl"/>    
            </fo:inline>
            <xsl:text> </xsl:text><xsl:value-of select="./@creationDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="createUser/*"/>)
            <fo:inline font-weight="bold">
                <xsl:value-of select="./@lastUpdateDateLbl"/>    
            </fo:inline>
            <xsl:text> </xsl:text><xsl:value-of select="./@lastUpdateDate"/><xsl:text> </xsl:text>(<xsl:apply-templates select="lastUpdateUser/*"/>)
                - <xsl:value-of select="./@boui"/>'v<xsl:value-of select="./@version"/>
            </fo:block>
    </xsl:template>
    
    <!-- ***** Tab Templates -->
    
    <xsl:template match="formEdit" priority="-2">
        <fo:block xsl:use-attribute-sets="formTitle">
            <xsl:apply-templates select="title/*"></xsl:apply-templates>
        </fo:block>
        <xsl:apply-templates select="./*[local-name() != 'formEditObject']"/>
        <fo:block id="terminator"/>
    </xsl:template>
    
    <xsl:template match="panel/tabs" priority="-1">
        <xsl:for-each select="tab">
            <fo:block xsl:use-attribute-sets="tabTitle">
                <xsl:value-of select="./@label"/>
            </fo:block>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="formEdit/tabs" priority="-1">
        <xsl:for-each select="tab">
            <fo:block xsl:use-attribute-sets="tabTitle">
                <xsl:value-of select="./@label"/>
            </fo:block>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template match="tab/tabs" priority="-1">
        <xsl:for-each select="tab">
            <fo:block xsl:use-attribute-sets="innerTabTitle">
                <xsl:value-of select="./@label"/>
            </fo:block>
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="panel" priority="-2">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="section" priority="-1">
            <fo:block xsl:use-attribute-sets="sectionTitle">
                <xsl:value-of select="@label"/>
            </fo:block>
        <xsl:apply-templates></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="rows" priority="-1">
        <!-- O início de conteúdo tabular -->
            <fo:block>
                <fo:table font-family="Verdana" font-size="14px">
                    <fo:table-body>
                        <xsl:apply-templates select="./*[local-name() != 'colgroup']"/>
                    </fo:table-body>
                </fo:table>
            </fo:block>
    </xsl:template> 
    
    <xsl:template match="row" priority="-1">
        <!-- Cria uma nova linha numa tabela existente -->
        <fo:table-row>
            <xsl:apply-templates/>
        </fo:table-row> 
    </xsl:template>    
    
    <xsl:template match="cell" priority="-1">
        <xsl:choose>
            <xsl:when test="count(./attribute) > 0 or count(./attributeImage) > 0">
                <xsl:apply-templates></xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="count(./*) > 0 ">
                    <fo:table-cell>
                        <xsl:if test="@colSpan != 0">
                           <xsl:attribute name="number-columns-spanned">
                               <xsl:value-of select="@colSpan"/>
                           </xsl:attribute>
                        </xsl:if>
                        <xsl:apply-templates/>    
                    </fo:table-cell>
                </xsl:if>
                <xsl:if test="count(./*) = 0 ">
                    <fo:table-cell>
                        <fo:block/>    
                    </fo:table-cell>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>      
    
    <!-- A Label for the attributes -->
    <xsl:template match="attributeLabel" priority="-1">
            <fo:block xsl:use-attribute-sets="labelFormatting">
                <xsl:value-of select="@text"/>
            </fo:block>
    </xsl:template>
    
    <xsl:template match="attribute" priority="-1">
        <fo:table-cell width="{ancestor::rows/@labelWidth}">
              <xsl:apply-templates select="attributeLabel"/>  
        </fo:table-cell>
        <fo:table-cell>
            <xsl:if test="(ancestor::cell/@colSpan) != 0">
                <xsl:attribute name="number-columns-spanned">
                    <xsl:value-of select="ancestor::cell/@colSpan + count(ancestor::cell//attributeLabel)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*[local-name() != 'attributeLabel']"></xsl:apply-templates>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Simple textual attribute -->
    <xsl:template match="attributeText" priority="-1">
            <xsl:choose>
                <xsl:when test="string-length(text()) > 0">
                    <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                        <xsl:value-of select="./text()" disable-output-escaping="yes"/>
                    </fo:block>
                </xsl:when>
                <xsl:otherwise>
                    <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                        <xsl:value-of select="string('_')"/>
                    </fo:block>
                </xsl:otherwise>
            </xsl:choose>
            
    </xsl:template>
    
    <xsl:template match="attributeImage" priority="-1">
        <fo:table-cell xsl:use-attribute-sets="labelFormatting">
            <fo:block>
                <xsl:value-of select="./attributeLabel/@text"/>
            </fo:block>
        </fo:table-cell>
        <fo:table-cell>
        <fo:block>
            <xsl:if test="string-length(attributeLabel/@urlPdf) > 0">
            <fo:external-graphic>
                <xsl:attribute name="src">
                    <xsl:value-of select="attributeLabel/@urlPdf"/>
                </xsl:attribute>
                <xsl:if test="@width">
                    <xsl:attribute name="content-width">
                        <xsl:choose>
                            <xsl:when test="contains(@width, 'px')">
                                <xsl:value-of select="@width"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@width, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="@height">
                    <xsl:attribute name="content-height">
                        <xsl:choose>
                            <xsl:when test="contains(@height, 'px')">
                                <xsl:value-of select="@height"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@height, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
            </fo:external-graphic>
            </xsl:if>
        </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    
    <xsl:template match="attributeOutput" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@renderedValue) > 0">
                <fo:block xsl:use-attribute-sets="defaultText" >
                    <xsl:value-of select="@renderedValue" disable-output-escaping="yes"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="defaultText">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    
    <!-- Boolean attribute -->
    <xsl:template match="attributeBoolean" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(text()) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:if test="./text() = string(1)">
                        <xsl:value-of select="string('True')"/>
                    </xsl:if>
                    <xsl:if test="./text() = string(0)">
                        <xsl:value-of select="string('False')"/>
                    </xsl:if>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Atributos do tipo data -->
    <xsl:template match="attributeDate" priority="-1">
        <xsl:if test="count(ancestor::attributeDatetime) = 0">
            <xsl:choose>
                <xsl:when test="string-length(@displayValue) > 0">
                    <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                        <xsl:value-of select="./@displayValue"/>
                    </fo:block>
                </xsl:when>
                <xsl:otherwise>
                    <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                        <xsl:value-of select="string('_')"/>
                    </fo:block>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="attributeDateTime" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Atributos do tipo texto longo -->
    <xsl:template match="attributeTextArea" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(text()) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./text()" disable-output-escaping="yes"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Atributos do tipo número -->
    <xsl:template match="attributeNumber" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(./@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Atributos do tipo ficheiro -->
    <xsl:template match="attributeFile" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="attributeWordMacro" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(text()) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Attribute HTML Editor -->
    <xsl:template match="attributeHtmlEditor" priority="-1">
        <!-- <xsl:call-template name="checkColSpan">
            <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
            <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
        </xsl:call-template> -->    
            <fo:block-container>
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:apply-templates select="./*|text()" ></xsl:apply-templates>
                </fo:block>
            </fo:block-container>
    </xsl:template>
    
    <xsl:template match="outputHtml">
        <xsl:if test="count(ancestor::cell) > 0 ">
                <fo:block>
                    <xsl:apply-templates></xsl:apply-templates>
                </fo:block>
        </xsl:if>
        <xsl:if test="count(ancestor::cell) = 0 ">
            <xsl:apply-templates></xsl:apply-templates> 
        </xsl:if>
        
    </xsl:template>
    
    <!-- HTML Convertion -->
    <!-- ********************************* -->
    <xsl:template match="b">
        <fo:inline font-weight="bold">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="i">
        <fo:inline font-style="italic">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="br">
        <fo:block> </fo:block>
    </xsl:template>
    
    <xsl:template match="hr">
        <fo:block>
            <fo:leader leader-pattern="rule"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="a">
        <xsl:choose>
            <xsl:when test="@name">
                <xsl:if test="not(name(following-sibling::*[1]) = 'h1')">
                    <fo:block line-height="0" space-after="0pt" 
                        font-size="0pt" id="{@name}"/>
                </xsl:if>
            </xsl:when>
            <xsl:when test="@href">
                <fo:basic-link color="blue">
                    <xsl:choose>
                        <xsl:when test="starts-with(@href, '#')">
                            <xsl:attribute name="internal-destination">
                                <xsl:value-of select="substring(@href, 2)"/>
                            </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="external-destination">
                                <xsl:value-of select="@href"/>
                            </xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:apply-templates select="*|text()"/>
                </fo:basic-link>
                <xsl:if test="starts-with(@href, '#')">
                    <xsl:text> on page </xsl:text>
                    <fo:page-number-citation ref-id="{substring(@href, 2)}"/>
                </xsl:if>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="p">
        <fo:block line-height="15pt" space-after="12pt">
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="pre">
        <fo:block font-family="monospace" white-space-collapse="false" wrap-option="no-wrap">
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="ul">
        <fo:list-block provisional-distance-between-starts="1cm"
            provisional-label-separation="0.5cm">
            <xsl:attribute name="space-after">
                <xsl:choose>
                    <xsl:when test="ancestor::ul or ancestor::ol">
                        <xsl:text>0pt</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="start-indent">
                <xsl:variable name="ancestors">
                    <xsl:choose>
                        <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
                            <xsl:value-of select="1 + 
                                (count(ancestor::ol) + 
                                count(ancestor::ul)) * 
                                1.25"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>1</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="concat($ancestors, 'cm')"/>
            </xsl:attribute>
            <xsl:for-each select="./li">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>*</fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block>
                            <xsl:apply-templates select="*|text()"/>
                        </fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </xsl:for-each>
        </fo:list-block>
    </xsl:template>
    
    <xsl:template match="ol">
        <fo:list-block provisional-distance-between-starts="1cm"
            provisional-label-separation="0.5cm">
            <xsl:attribute name="space-after">
                <xsl:choose>
                    <xsl:when test="ancestor::ul or ancestor::ol">
                        <xsl:text>0pt</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>12pt</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="start-indent">
                <xsl:variable name="ancestors">
                    <xsl:choose>
                        <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
                            <xsl:value-of select="1 + 
                                (count(ancestor::ol) + 
                                count(ancestor::ul)) * 
                                1.25"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>1</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="concat($ancestors, 'cm')"/>
            </xsl:attribute>
            <xsl:for-each select="li">
                <fo:list-item>
                    <fo:list-item-label end-indent="label-end()">
                        <fo:block>
                            <xsl:variable name="value-attr">
                                <xsl:choose>
                                    <xsl:when test="../@start">
                                        <xsl:number value="position() + ../@start - 1"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:number value="position()"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            <xsl:choose>
                                <xsl:when test="../@type='i'">
                                    <xsl:number value="$value-attr" format="i. "/>
                                </xsl:when>
                                <xsl:when test="../@type='I'">
                                    <xsl:number value="$value-attr" format="I. "/>
                                </xsl:when>
                                <xsl:when test="../@type='a'">
                                    <xsl:number value="$value-attr" format="a. "/>
                                </xsl:when>
                                <xsl:when test="../@type='A'">
                                    <xsl:number value="$value-attr" format="A. "/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:number value="$value-attr" format="1. "/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:block>
                    </fo:list-item-label>
                    <fo:list-item-body start-indent="body-start()">
                        <fo:block>
                            <xsl:apply-templates select="*|text()"/>
                        </fo:block>
                    </fo:list-item-body>
                </fo:list-item>
            </xsl:for-each>
        </fo:list-block>
    </xsl:template>
    
    <!-- START Tables -->
    <xsl:template match="table">
        <!-- We cannot have tables inside tables in PDF -->
        <xsl:if test="count(ancestor::gridPanel) = 0">
        <fo:table table-layout="fixed">
            <xsl:choose>
                <xsl:when test="@cols">
                    <xsl:call-template name="build-columns">
                        <xsl:with-param name="cols" 
                            select="concat(@cols, ' ')"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <fo:table-column column-width="200pt"/>
                </xsl:otherwise>
            </xsl:choose>
            <fo:table-body>
                <xsl:apply-templates select="*"/>
            </fo:table-body>
        </fo:table>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="build-columns">
        <xsl:param name="cols"/>
        
        <xsl:if test="string-length(normalize-space($cols))">
            <xsl:variable name="next-col">
                <xsl:value-of select="substring-before($cols, ' ')"/>
            </xsl:variable>
            <xsl:variable name="remaining-cols">
                <xsl:value-of select="substring-after($cols, ' ')"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="contains($next-col, 'pt')">
                    <fo:table-column column-width="{$next-col}"/>
                </xsl:when>
                <xsl:when test="number($next-col) > 0">
                    <fo:table-column column-width="{concat($next-col, 'pt')}"/>
                </xsl:when>
                <xsl:otherwise>
                    <fo:table-column column-width="50pt"/>
                </xsl:otherwise>
            </xsl:choose>
            
            <xsl:call-template name="build-columns">
                <xsl:with-param name="cols" select="concat($remaining-cols, ' ')"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="td">
        <xsl:if test="count(ancestor::gridPanel) = 0">
        <fo:table-cell 
            padding-start="3pt" padding-end="3pt"
            padding-before="3pt" padding-after="3pt">
            <xsl:if test="@colspan">
                <xsl:attribute name="number-columns-spanned">
                    <xsl:value-of select="@colspan"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@rowspan">
                <xsl:attribute name="number-rows-spanned">
                    <xsl:value-of select="@rowspan"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@border='1' or 
                ancestor::tr[@border='1'] or
                ancestor::thead[@border='1'] or
                ancestor::table[@border='1']">
                <xsl:attribute name="border-style">
                    <xsl:text>solid</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-color">
                    <xsl:text>black</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-width">
                    <xsl:text>1pt</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <xsl:variable name="align">
                <xsl:choose>
                    <xsl:when test="@align">
                        <xsl:choose>
                            <xsl:when test="@align='center'">
                                <xsl:text>center</xsl:text>
                            </xsl:when>
                            <xsl:when test="@align='right'">
                                <xsl:text>end</xsl:text>
                            </xsl:when>
                            <xsl:when test="@align='justify'">
                                <xsl:text>justify</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>start</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="ancestor::tr[@align]">
                        <xsl:choose>
                            <xsl:when test="ancestor::tr/@align='center'">
                                <xsl:text>center</xsl:text>
                            </xsl:when>
                            <xsl:when test="ancestor::tr/@align='right'">
                                <xsl:text>end</xsl:text>
                            </xsl:when>
                            <xsl:when test="ancestor::tr/@align='justify'">
                                <xsl:text>justify</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>start</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="ancestor::thead">
                        <xsl:text>center</xsl:text>
                    </xsl:when>
                    <xsl:when test="ancestor::table[@align]">
                        <xsl:choose>
                            <xsl:when test="ancestor::table/@align='center'">
                                <xsl:text>center</xsl:text>
                            </xsl:when>
                            <xsl:when test="ancestor::table/@align='right'">
                                <xsl:text>end</xsl:text>
                            </xsl:when>
                            <xsl:when test="ancestor::table/@align='justify'">
                                <xsl:text>justify</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>start</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>start</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <fo:block text-align="{$align}">
                <xsl:apply-templates select="*|text()"/>
            </fo:block>
        </fo:table-cell>
            </xsl:if>
    </xsl:template>
    
    <xsl:template match="th">
        <xsl:if test="count(ancestor::gridPanel) = 0">
        <fo:table-cell
            padding-start="3pt" padding-end="3pt"
            padding-before="3pt" padding-after="3pt">
            <xsl:if test="@border='1' or 
                ancestor::tr[@border='1'] or
                ancestor::table[@border='1']">
                <xsl:attribute name="border-style">
                    <xsl:text>solid</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-color">
                    <xsl:text>black</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="border-width">
                    <xsl:text>1pt</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <fo:block font-weight="bold" text-align="center">
                <xsl:apply-templates select="*|text()"/>
            </fo:block>
        </fo:table-cell>
            </xsl:if>
    </xsl:template>
    
    <xsl:template match="thead">
        <xsl:apply-templates select="tr"/>
    </xsl:template>
    
    <xsl:template match="tr">
        <xsl:if test="count(ancestor::gridPanel) = 0">
        <fo:table-row>
            <xsl:apply-templates select="*|text()"/>
        </fo:table-row>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="span">
        <xsl:apply-templates select="*|text()"/>
    
    </xsl:template>
    
    <xsl:template match="font">
        <xsl:variable name="face">
            <xsl:choose>
                <xsl:when test="@face">
                    <xsl:value-of select="@face"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>sans-serif</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="color">
            <xsl:choose>
                <xsl:when test="@color">
                    <xsl:value-of select="@color"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>black</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="size">
            <xsl:choose>
                <xsl:when test="@size">
                    <xsl:choose>
                        <xsl:when test="contains(@size, 'pt')">
                            <xsl:text>@size</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+1'">
                            <xsl:text>110%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+2'">
                            <xsl:text>120%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+3'">
                            <xsl:text>130%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+4'">
                            <xsl:text>140%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+5'">
                            <xsl:text>150%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+6'">
                            <xsl:text>175%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '+7'">
                            <xsl:text>200%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-1'">
                            <xsl:text>90%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-2'">
                            <xsl:text>80%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-3'">
                            <xsl:text>70%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-4'">
                            <xsl:text>60%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-5'">
                            <xsl:text>50%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-6'">
                            <xsl:text>40%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '-7'">
                            <xsl:text>30%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '1'">
                            <xsl:text>8pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '2'">
                            <xsl:text>10pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '3'">
                            <xsl:text>12pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '4'">
                            <xsl:text>14pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '5'">
                            <xsl:text>18pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '6'">
                            <xsl:text>24pt</xsl:text>
                        </xsl:when>
                        <xsl:when test="@size = '7'">
                            <xsl:text>36pt</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>12pt</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise> 
                    <xsl:text>12pt</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <fo:inline font-size="{$size}" font-family="{$face}" color="{$color}">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
    
    <xsl:template match="div">
        <xsl:apply-templates select="*|text()"></xsl:apply-templates>
    </xsl:template>
    
    <!-- END Tables **********************  -->
    
    <xsl:template match="img">
                <fo:external-graphic>
                        <xsl:attribute name="src">
                            <xsl:if test="starts-with(@src,'http:')">
                                <xsl:value-of select="@src"/>
                            </xsl:if>
                            <xsl:if test="not(starts-with(@src,'http:'))">
                                <xsl:value-of select="concat($baseUrl,@src)"/>
                            </xsl:if>
                        </xsl:attribute>
                        <xsl:if test="@width">
                            <xsl:attribute name="width">
                                <xsl:choose>
                                    <xsl:when test="contains(@width, 'px')">
                                        <xsl:value-of select="@width"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat(@width, 'px')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:if test="@height">
                            <xsl:attribute name="height">
                                <xsl:choose>
                                    <xsl:when test="contains(@height, 'px')">
                                        <xsl:value-of select="@height"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat(@height, 'px')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                        </xsl:if>
                    </fo:external-graphic>
    </xsl:template>
    <!-- ********************************* -->
    
    <!-- Atributos do tipo object -->
    <xsl:template match="attributeNumberLookup" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
     <xsl:template match="bridgeLookup" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="./@displayValue"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- Attribute whose value comes from a list of values -->
    <xsl:template match="attributeLov" priority="-1">
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./@displayValue"/>
            </fo:block>
    </xsl:template>
    
    <!-- Attribute with a password (to not show the value) -->
    <xsl:template match="attributePassword" priority="-1">
        <xsl:choose>
            <xsl:when test="string-length(@displayValue) > 0">
                <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                    <xsl:value-of select="string('•••••••••••')"/>
                </fo:block>
            </xsl:when>
            <xsl:otherwise>
                <fo:block xsl:use-attribute-sets="attributeEmptyFormatting">
                    <xsl:value-of select="string('_')"/>
                </fo:block>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="pieChart">
        <fo:block>
            <fo:external-graphic src="{@urlPdf}">
                <xsl:if test="@width">
                    <xsl:attribute name="width">
                        <xsl:choose>
                            <xsl:when test="contains(@width, 'px')">
                                <xsl:value-of select="@width"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@width, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="@height">
                    <xsl:attribute name="height">
                        <xsl:choose>
                            <xsl:when test="contains(@height, 'px')">
                                <xsl:value-of select="@height"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@height, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
            </fo:external-graphic>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="lineChart">
        <fo:block>
            <fo:external-graphic src="{@urlPdf}">
                <xsl:if test="@width">
                    <xsl:attribute name="width">
                        <xsl:choose>
                            <xsl:when test="contains(@width, 'px')">
                                <xsl:value-of select="@width"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@width, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="@height">
                    <xsl:attribute name="height">
                        <xsl:choose>
                            <xsl:when test="contains(@height, 'px')">
                                <xsl:value-of select="@height"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@height, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
            </fo:external-graphic>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="barChart">
        <fo:block>
            <fo:external-graphic src="{@urlPdf}">
                <xsl:if test="@width">
                    <xsl:attribute name="width">
                        <xsl:choose>
                            <xsl:when test="contains(@width, 'px')">
                                <xsl:value-of select="@width"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@width, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="@height">
                    <xsl:attribute name="height">
                        <xsl:choose>
                            <xsl:when test="contains(@height, 'px')">
                                <xsl:value-of select="@height"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@height, 'px')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:if>
            </fo:external-graphic>
        </fo:block>
    </xsl:template>
    
    <!-- ***** Grid Panel ***** -->
    <xsl:template match="gridPanel" priority="-1">
        <fo:block>
            <fo:table xsl:use-attribute-sets="gridPanelTbl">
                <fo:table-body>
                    <xsl:apply-templates></xsl:apply-templates>
                </fo:table-body>
            </fo:table>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="gridheaderrow" priority="-1">
        <fo:table-row xsl:use-attribute-sets="gridPanelTblHeader">
            <xsl:apply-templates></xsl:apply-templates>
        </fo:table-row>
    </xsl:template>
    
    <xsl:template match="gridheadercolumn" priority="-1">
        <fo:table-cell>
            <fo:block>
                <xsl:value-of select="./text()"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <xsl:template match="gridrow" priority="-1">
        <fo:table-row xsl:use-attribute-sets="gridPanelRow">
            <xsl:apply-templates></xsl:apply-templates>
        </fo:table-row>
    </xsl:template>
    
    <xsl:template match="gridcolumn" priority="-1">
        <fo:table-cell width="{@width}">
            <fo:block-container>
                <fo:block>
                <xsl:value-of select="./text()"/>
                <xsl:apply-templates select="./*"></xsl:apply-templates>
                </fo:block>
            </fo:block-container>
        </fo:table-cell>
    </xsl:template>
    
    <xsl:template match="*|@*" priority="-10">
        <xsl:apply-templates select="*|@*"/>
    </xsl:template>
    
    <xsl:template match="text()" priority="-5">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="script" priority="-1"/>
    <xsl:template match="title" priority="-1"/>
</xsl:stylesheet>
