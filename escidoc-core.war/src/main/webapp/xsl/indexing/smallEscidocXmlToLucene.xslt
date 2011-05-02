<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This stylesheet has only one searchable index-field: objectid 
and the field xml_representation, containing the xml of the object for search-result-output.
Mandatory: PID is set into IndexDocument and written as IndexField !!!
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="index/gsearchAttributes.xslt"/>
    
    <!-- MAIN TEMPLATE -->
    <xsl:template match="/">
        <xsl:variable name="type">
            <xsl:for-each select="*">
                <xsl:if test="position() = 1">
                    <xsl:value-of select="local-name()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <!-- START IndexDocument -->
        <IndexDocument>
        	<!-- Call this template immediately after opening IndexDocument-element! -->
        	<!-- Template writes PID -->
        	<xsl:call-template name="processGsearchAttributes"/>

        	<xsl:choose>
            	<xsl:when test="$type='item'">
                	<xsl:call-template name="processItem"/>
            	</xsl:when>
            	<xsl:when test="$type='container'">
                	<xsl:call-template name="processContainer"/>
            	</xsl:when>
            	<xsl:otherwise>
                	<xsl:call-template name="processContainer"/>
            	</xsl:otherwise>
        	</xsl:choose>
        </IndexDocument> 
    </xsl:template>

    <!-- WRITE INDEX FOR ITEM -->
    <xsl:template name="processItem">
        <xsl:variable name="PID" select="string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/')"/>
            <IndexField IFname="objectid" index="UN_TOKENIZED" store="NO">
                <xsl:value-of select="$PID"/>
            </IndexField>

            <!-- Wrtite item.xml as Field xml_representation, gets returned by the search -->
            <IndexField IFname="xml_representation" index="NO" store="YES">
                <xsl:text disable-output-escaping="yes">
                    &lt;![CDATA[
                </xsl:text>
                    <xsl:copy-of select="/*[local-name()='item']"/>
                <xsl:text disable-output-escaping="yes">
                    ]]&gt;
                </xsl:text>
            </IndexField>
    </xsl:template>

    <!-- WRITE INDEX FOR CONTAINER -->
    <xsl:template name="processContainer">
        <xsl:variable name="PID" select="string-helper:getSubstringAfterLast(/*/@*[local-name()='href'], '/')"/>

            <IndexField IFname="objectid" index="UN_TOKENIZED" store="NO">
                <xsl:value-of select="$PID"/>
            </IndexField>

            <!-- Write container.xml as Field xml_representation, gets returned by the search -->
            <IndexField IFname="xml_representation" index="NO" store="YES">
                <xsl:text disable-output-escaping="yes">
                    &lt;![CDATA[
                </xsl:text>
                    <xsl:copy-of select="/*[local-name()='container']"/>
                <xsl:text disable-output-escaping="yes">
                    ]]&gt;
                </xsl:text>
            </IndexField>
    </xsl:template>

</xsl:stylesheet>   
