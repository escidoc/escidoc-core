<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
  ~ for the specific language governing permissions and limitations under the License.
  ~
  ~ When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
  ~ license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
  ~ brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  ~
  ~ CDDL HEADER END
  ~
  ~ Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
  ~ and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
  ~ terms.
  -->

<!--
Notes:
 -->
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xalan="http://xml.apache.org/xalan"
        xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
        xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
        extension-element-prefixes="lastdate-helper string-helper sortfield-helper">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="index/gsearchAttributes.xslt"/>
    
    <!-- Include stylesheet that indexes values for permission-filtering -->
    <xsl:include href="index/permissions.xslt"/>
    
    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">YES</xsl:variable>

    <xsl:variable name="CONTEXTNAME"></xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">/sort</xsl:variable>
    <xsl:variable name="FIELDSEPARATOR">/</xsl:variable>

    <!-- Paths to Properties -->
    <xsl:variable name="CONTEXT_PROPERTIESPATH" select="/*[local-name()='context']/*[local-name()='properties']"/>

    <!-- Path to Admin-Descriptor -->
    <xsl:variable name="CONTEXT_ADMIN_DESCRIPTORPATH" select="/*[local-name()='context']/*[local-name()='admin-descriptors']"/>

    <xsl:template match="/">
        <xsl:variable name="type">
            <xsl:for-each select="*">
                <xsl:if test="position() = 1">
                    <xsl:value-of select="local-name()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <IndexDocument> 
        <!-- Call this template immediately after opening IndexDocument-element! -->
        <xsl:call-template name="processGsearchAttributes"/>
        <xsl:call-template name="processPermissionFilters"/>
        <xsl:choose>
            <xsl:when test="$type='context'">
                <xsl:call-template name="processContext"/>
            </xsl:when>
        </xsl:choose>
        </IndexDocument> 
    </xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlContext">
        <xsl:copy-of select="/*[local-name()='context']"/>
    </xsl:template>

    <xsl:template name="processContext">
		<xsl:variable name="objectType" select="'context'" />
        <IndexField termVector="NO" index="UN_TOKENIZED" IFname="type">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <IndexField termVector="NO" index="UN_TOKENIZED">
            <xsl:attribute name="store">
                <xsl:value-of select="$STORE_FOR_SCAN"/>
            </xsl:attribute>
            <xsl:attribute name="IFname">
                <xsl:value-of select="concat($SORTCONTEXTPREFIX,$FIELDSEPARATOR,'type')"/>
            </xsl:attribute>
            <xsl:value-of select="$objectType"/>
        </IndexField>
        <xsl:call-template name="writeIndexField">
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
            <xsl:with-param name="fieldname">id</xsl:with-param>
            <xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(string-helper:getSubstringAfterLast(/*[local-name()='context']/@*[local-name()='href'], '/'))"/>
            <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
            <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
            <xsl:with-param name="sort">true</xsl:with-param>
        </xsl:call-template>
        <IndexField IFname="aa_xml_representation" index="NO" store="YES" termVector="NO">
            <xsl:text disable-output-escaping="yes">
                &lt;![CDATA[
            </xsl:text>
                <xsl:call-template name="writeSearchXmlContext"/>
            <xsl:text disable-output-escaping="yes">
                ]]&gt;
            </xsl:text>
        </IndexField>
        
        <!-- COMPLETE XML -->
        <xsl:for-each select="./*">
        	<xsl:for-each select="./@*">
            	<xsl:if test="string(.) and normalize-space(.)!=''
                        and (namespace-uri()!='http://www.w3.org/1999/xlink' 
                        or (namespace-uri()='http://www.w3.org/1999/xlink' and local-name()='title'))">
        			<xsl:call-template name="writeIndexField">
            			<xsl:with-param name="context" select="$CONTEXTNAME"/>
            			<xsl:with-param name="fieldname" select="local-name()"/>
            			<xsl:with-param name="fieldvalue" select="."/>
            			<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
            			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                		<xsl:with-param name="sort">true</xsl:with-param>
        			</xsl:call-template>
        		</xsl:if>
        	</xsl:for-each>
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">path</xsl:with-param>
                <xsl:with-param name="sort">true</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>

    </xsl:template>

    <!-- RECURSIVE ITERATION OF ELEMENTS -->
    <!-- ITERATE ALL ELEMENTS AND WRITE ELEMENT-NAME AND ELEMENT-VALUE -->
    <xsl:template name="processElementTree">
        <!-- name of index-field -->
        <xsl:param name="path"/>
        <!-- prefix for index-name -->
        <xsl:param name="context"/>
        <!-- if 'yes', also write attributes as index-fields -->
        <xsl:param name="indexAttributes"/>
        <!-- nametype defines if paths are used for indexnames or elementname only or fixed name-->
        <!-- can be 'path' or 'element' or 'fixed'-->
        <!-- eg first-name or publication.creator.person.first-name -->
        <xsl:param name="nametype"/>
        <!-- if 'true', also write sortable fields -->
        <xsl:param name="sort"/>
        <xsl:if test="string(text()) and normalize-space(text())!=''">
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$path"/>
                <xsl:with-param name="fieldvalue" select="text()"/>
                <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                <xsl:with-param name="sort" select="$sort"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$indexAttributes='yes'">
            <!-- ITERATE ALL ATTRIBUTES AND WRITE ELEMENT-NAME, ATTRIBUTE-NAME AND ATTRIBUTE-VALUE -->
            <!--  EXCEPT FOR XLINK-ATTRIBUTES -->
            <xsl:for-each select="@*">
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!='' 
                        and (namespace-uri()!='http://www.w3.org/1999/xlink'
                        or (namespace-uri()='http://www.w3.org/1999/xlink' and local-name()='title'))">
		            <xsl:variable name="fieldname">
		                <xsl:choose>
		                    <xsl:when test="$nametype='fixed'">
		                    	<xsl:value-of select="$path"/>
		                    </xsl:when>
		                    <xsl:otherwise>
		                    	<xsl:value-of select="concat($path,$FIELDSEPARATOR,local-name())"/>
		                    </xsl:otherwise>
		                </xsl:choose>
		            </xsl:variable>
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="$fieldname"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                		<xsl:with-param name="sort" select="$sort"/>
                    </xsl:call-template>
                </xsl:if>
                <!--  WRITE HREF-ATTRIBUTES AS ID (EXTRACT ID OUT OF HREF) -->
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!='' 
                        and namespace-uri()='http://www.w3.org/1999/xlink'
                        and local-name()='href'">
                	<xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(., '/')"/>
                	<xsl:if test="string($objectId) and normalize-space($objectId)!=''
                        and contains($objectId, ':')">
			            <xsl:variable name="fieldname">
			                <xsl:choose>
			                    <xsl:when test="$nametype='fixed'">
			                    	<xsl:value-of select="$path"/>
			                    </xsl:when>
			                    <xsl:otherwise>
			                    	<xsl:value-of select="concat($path,$FIELDSEPARATOR,'id')"/>
			                    </xsl:otherwise>
			                </xsl:choose>
			            </xsl:variable>
                    	<xsl:call-template name="writeIndexField">
                        	<xsl:with-param name="context" select="$context"/>
                        	<xsl:with-param name="fieldname" select="$fieldname"/>
                        	<xsl:with-param name="fieldvalue" select="$objectId"/>
                        	<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
                        	<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                			<xsl:with-param name="sort" select="$sort"/>
                    	</xsl:call-template>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:for-each select="./*">
            <xsl:variable name="fieldname">
                <xsl:choose>
                    <xsl:when test="$nametype='element'">
                            <xsl:value-of select="local-name()"/>
                    </xsl:when>
                    <xsl:when test="$nametype='fixed'">
                            <xsl:value-of select="$path"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="string($path) and normalize-space($path)!=''">
                                <xsl:value-of select="concat($path,$FIELDSEPARATOR,local-name())"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="local-name()"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="indexAttributes" select="$indexAttributes"/>
                <xsl:with-param name="path" select="$fieldname"/>
                <xsl:with-param name="nametype" select="$nametype"/>
                <xsl:with-param name="sort" select="$sort"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!--  WRITE INDEXFIELD -->
    <xsl:template name="writeIndexField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:param name="indextype"/>
        <xsl:param name="store"/>
        <xsl:param name="sort"/>
        <xsl:if test="string($fieldvalue) and normalize-space($fieldvalue)!=''">
            <IndexField termVector="NO">
                <xsl:attribute name="index">
                	<xsl:value-of select="$indextype"/>
                </xsl:attribute>
                <xsl:attribute name="store">
                    <xsl:value-of select="$store"/>
                </xsl:attribute>
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($context,$FIELDSEPARATOR,$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="$fieldvalue"/>
            </IndexField>
            <xsl:if test="string($sort) and normalize-space($sort)='true'">
            	<xsl:call-template name="writeSortField">
                	<xsl:with-param name="context" select="$context"/>
                	<xsl:with-param name="fieldname" select="$fieldname"/>
                	<xsl:with-param name="fieldvalue" select="$fieldvalue"/>
            	</xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>
        
    <!--  WRITE SORTFIELD -->
    <xsl:template name="writeSortField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:if test="string($fieldvalue) 
                    and normalize-space($fieldvalue)!=''
                    and sortfield-helper:checkSortField(concat($SORTCONTEXTPREFIX,$context,$FIELDSEPARATOR,$fieldname)) = false()">
            <IndexField termVector="NO" index="UN_TOKENIZED" store="NO">
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($SORTCONTEXTPREFIX,$context,$FIELDSEPARATOR,$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="string-helper:getNormalizedString($fieldvalue)"/>
            </IndexField>
        </xsl:if>
    </xsl:template>
       
</xsl:stylesheet>   
