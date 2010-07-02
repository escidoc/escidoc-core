<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Notes:
Following index fields are written by this stylesheet:
(Fields are written unstored and untokenized)

-PID (objid without version-identifier but eventually with information if latest-version (LV) or latest-release(LR))

if information about LV or LR is written, additionally write:
(This is used to filter duplicates by HitCollector)
-rootPid (objid without version-identifier)
-type (1 for LR and 0 for LV)

 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:xsltxsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        extension-element-prefixes="string-helper">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <!-- Parameters that get passed while calling this stylesheet-transformation -->
    <xsl:param name="PID_VERSION_IDENTIFIER"/>

    <xsl:template name="processGsearchAttributes">
        <!-- WRITE customized PID in IndexDocument and as IndexField. Important for fedoragsearch to reidentify object  -->
        <xsl:variable name="PID" select="string-helper:removeVersionIdentifier(/*/@objid, $PID_VERSION_IDENTIFIER)"/>
        <xsl:attribute name="PID">
            <xsl:value-of select="$PID"/>
        </xsl:attribute>
        <IndexField IFname="PID" index="UN_TOKENIZED" store="NO" termVector="NO">
            <xsl:value-of select="$PID"/>
        </IndexField>
        <xsl:if test="string($PID_VERSION_IDENTIFIER) and normalize-space($PID_VERSION_IDENTIFIER)!=''">
            <IndexField IFname="rootPid" index="UN_TOKENIZED" store="NO" termVector="NO">
                <xsl:value-of select="string-helper:removeVersionIdentifier(/*/@objid)"/>
            </IndexField>
            <IndexField IFname="type" index="UN_TOKENIZED" store="NO" termVector="NO">
                <xsl:choose>
                    <xsl:when test="$PID_VERSION_IDENTIFIER='LR'">
                        <xsl:value-of select="'1'"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'0'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </IndexField>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>   
