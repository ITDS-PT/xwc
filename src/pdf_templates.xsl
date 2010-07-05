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
        <xsl:attribute name="font-family">Geneva</xsl:attribute>
        <xsl:attribute name="font-size">22px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">10px</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- A section title -->
    <xsl:attribute-set name="sectionTitle">
        <xsl:attribute name="font-family">Geneva</xsl:attribute>
        <xsl:attribute name="font-size">14px</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="border-bottom-width">0.5px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">#26252B</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="margin-bottom">5</xsl:attribute>
    </xsl:attribute-set>
    
    <!-- Inner Tabs -->
    <xsl:attribute-set name="innerTabTitle">
        <xsl:attribute name="font-family">Geneva</xsl:attribute>
        <xsl:attribute name="font-size">15px</xsl:attribute>
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
        <xsl:attribute name="margin-top">5px</xsl:attribute>
        <xsl:attribute name="margin-top">5px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="gridPanelTblHeader">
        <xsl:attribute name="background-color">#EDEDED</xsl:attribute>
        <xsl:attribute name="font-size">10px</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-family">Geneva</xsl:attribute>
        <xsl:attribute name="padding-top">3px</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="gridPanelRow">
        <xsl:attribute name="background-color">#EDEDED</xsl:attribute>
        <xsl:attribute name="font-size">10px</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
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
    
    <!-- //// END OF CSS -->
    
    <!-- ******* Main Templates ************* -->
    <xsl:template match="/" priority="-1">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <!-- Define as margens a regiões -->
                <fo:simple-page-master master-name="content" page-width="210mm" page-height="297mm">
                    <fo:region-body margin="3cm"/>
                    <fo:region-before extent="2cm"/>
                    <fo:region-after extent="2cm"/>
                    <fo:region-start extent="2cm"/>
                    <fo:region-end extent="2cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            
            <fo:page-sequence master-reference="content">
                <fo:flow flow-name="xsl-region-body">
                    <xsl:apply-templates></xsl:apply-templates>
                 </fo:flow>
            </fo:page-sequence>
            </fo:root>
    </xsl:template>
    
    <!-- ***** Tab Templates -->
    <xsl:template match="formedit/panel/tabs/tab" priority="-1">
            <fo:block xsl:use-attribute-sets="tabTitle">
                <xsl:value-of select="./@label"/>
            </fo:block>
        <xsl:apply-templates></xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="tab/tabs/tab" priority="-1">
        <fo:block xsl:use-attribute-sets="innerTabTitle">
            <xsl:value-of select="./@label"/>
        </fo:block>
        <fo:block xsl:use-attribute-sets="innerTabContent">
            <xsl:apply-templates/>
        </fo:block>
    </xsl:template>
    
    <!-- //// END OF TAB TEMPLATES -->
    
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
                    <!--<fo:table-column column-width="15%"/>
                    <fo:table-column column-width="85%"/>-->
                    <fo:table-body>
                    <xsl:apply-templates></xsl:apply-templates>
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
        <xsl:apply-templates/>
    </xsl:template>      
    
    <!-- A Label for the attributes -->
    <xsl:template match="attributelabel" priority="-1">
        <fo:table-cell>
            <fo:block xsl:use-attribute-sets="labelFormatting">
                <xsl:value-of select="@text"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Simple textual attribute -->
    <xsl:template match="attributetext" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>    
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="@text"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Boolean attribute -->
    <xsl:template match="attributeboolean" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>    
               <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                   <xsl:if test="./text() = string(1)">
                       <xsl:value-of select="string('True')"/>
                   </xsl:if>
                   <xsl:if test="./text() = string(0)">
                       <xsl:value-of select="string('False')"/>
                   </xsl:if>
               </fo:block> 
        </fo:table-cell>
    </xsl:template>
    
    <!-- Atributos do tipo data -->
    <xsl:template match="attributedate" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting" >
                <xsl:value-of select="./@displayvalue"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Atributos do tipo texto longo -->
    <xsl:template match="attributetextarea" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./text()"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Atributos do tipo número -->
    <xsl:template match="attributenumber" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./text()"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Atributos do tipo ficheiro -->
    <xsl:template match="attributefile" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./@displayvalue"/>
            </fo:block>
        </fo:table-cell>
     </xsl:template>
    
    <!-- Atributos do tipo object -->
    <xsl:template match="attributenumberlookup" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./@displayvalue"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Attribute whose value comes from a list of values -->
    <xsl:template match="attributelov" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="./@displayvalue"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
    <!-- Attribute with a password (to not show the value) -->
    <xsl:template match="attributepassword" priority="-1">
        <fo:table-cell>
            <xsl:call-template name="checkColSpan">
                <xsl:with-param name="count"><xsl:value-of select="count(../../cell)"/></xsl:with-param>
                <xsl:with-param name="total"><xsl:value-of select="../../../@columns"/></xsl:with-param>
            </xsl:call-template>
            <fo:block xsl:use-attribute-sets="attributeTextFormatting">
                <xsl:value-of select="string('•••••••••••')"/>
            </fo:block>
        </fo:table-cell>
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
        <fo:table-cell>
            <fo:block>
                <xsl:value-of select="text()"/>
            </fo:block>
        </fo:table-cell>
    </xsl:template>
    
</xsl:stylesheet>
