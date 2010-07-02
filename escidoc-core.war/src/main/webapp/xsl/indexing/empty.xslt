<?xml version="1.0" encoding="UTF-8"?>
<!-- 
This stylesheet has no searchable index-fields: 
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- MAIN TEMPLATE -->
    <xsl:template match="/">
        <xsl:variable name="type">
            <xsl:for-each select="*">
                <xsl:if test="position() = 1">
                    <xsl:value-of select="local-name()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
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
    </xsl:template>

    <!-- WRITE INDEX FOR ITEM -->
    <xsl:template name="processItem">
        <xsl:variable name="PID" select="string-helper:removeVersionIdentifier(/*[local-name()='item']/@objid)"/>
        <!-- START IndexDocument -->
        <IndexDocument>
            <!-- Gsearch needs PID to find object when updating -->
            <xsl:attribute name="PID">
                <xsl:value-of select="$PID"/>
            </xsl:attribute>
            <IndexField IFname="PID" index="UN_TOKENIZED" store="NO">
                <xsl:value-of select="$PID"/>
            </IndexField>
        </IndexDocument>
    </xsl:template>

    <!-- WRITE INDEX FOR CONTAINER -->
    <xsl:template name="processContainer">
        <xsl:variable name="PID" select="string-helper:removeVersionIdentifier(/*[local-name()='container']/@objid)"/>

        <!-- START IndexDocument -->
        <IndexDocument> 
            <!-- Gsearch needs PID to find object when updating -->
            <xsl:attribute name="PID">
                <xsl:value-of select="$PID"/>
            </xsl:attribute>
            <IndexField IFname="PID" index="UN_TOKENIZED" store="NO">
                <xsl:value-of select="$PID"/>
            </IndexField>

        </IndexDocument>
    </xsl:template>

</xsl:stylesheet>   
