<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Notes:
 -->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xalan="http://xml.apache.org/xalan"
		xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
		xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:element-type-helper="xalan://de.escidoc.sb.gsearch.xslt.ElementTypeHelper"
		xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
		extension-element-prefixes="lastdate-helper string-helper element-type-helper sortfield-helper">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="gsearchAttributes.xslt"/>
    
	<!-- Include stylesheet that indexes values for permission-filtering -->
	<xsl:include href="permissions.xslt"/>
	
    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">YES</xsl:variable>

	<xsl:variable name="CONTEXTNAME">escidoc</xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">sort</xsl:variable>

	<!-- Paths to Properties -->
	<xsl:variable name="CONTENT_MODEL_PROPERTIESPATH" select="/*[local-name()='content-model']/*[local-name()='properties']"/>

    <!-- Other Paths -->
    <xsl:variable name="CONTENT_MODEL_MDRECORDDEFINITIONPATH" select="/*[local-name()='content-model']/*[local-name()='md-record-definitions']/*[local-name()='md-record-definition']"/>
    <xsl:variable name="CONTENT_MODEL_RESOURCEDEFINITIONPATH" select="/*[local-name()='content-model']/*[local-name()='resource-definitions']/*[local-name()='resource-definition']"/>
    <xsl:variable name="CONTENT_MODEL_CONTENTSTREAMPATH" select="/*[local-name()='content-model']/*[local-name()='content-streams']/*[local-name()='content-stream']"/>

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
			<xsl:when test="$type='content-model'">
				<xsl:call-template name="processContentModel"/>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
        </IndexDocument> 
	</xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlContentModel">
        <xsl:copy-of select="/*[local-name()='content-model']"/>
    </xsl:template>

	<xsl:template name="processContentModel">
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objecttype</xsl:with-param>
			<xsl:with-param name="fieldvalue">content-model</xsl:with-param>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objid</xsl:with-param>
			<xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(/*[local-name()='content-model']/@objid)"/>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
			<xsl:text disable-output-escaping="yes">
				&lt;![CDATA[
			</xsl:text>
				<xsl:call-template name="writeSearchXmlContentModel"/>
			<xsl:text disable-output-escaping="yes">
				]]&gt;
			</xsl:text>
		</IndexField>
		
		<!-- PROPERTIES -->
		<xsl:call-template name="processProperties">
			<xsl:with-param name="path" select="$CONTENT_MODEL_PROPERTIESPATH"/>
		</xsl:call-template>
 			
		<!-- MD-RECORD DEFINITIONS -->
		<xsl:call-template name="processMdRecordDefinitions">
			<xsl:with-param name="path" select="$CONTENT_MODEL_MDRECORDDEFINITIONPATH"/>
		</xsl:call-template>
		
        <!-- RESOURCE DEFINITIONS -->
        <xsl:call-template name="processResourceDefinitions">
            <xsl:with-param name="path" select="$CONTENT_MODEL_RESOURCEDEFINITIONPATH"/>
        </xsl:call-template>
        
        <!-- CONTENT STREAMS -->
        <xsl:call-template name="processContentStreams">
            <xsl:with-param name="path" select="$CONTENT_MODEL_CONTENTSTREAMPATH"/>
        </xsl:call-template>
        
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
        <!-- nametype defines if paths are used for indexnames or elementname only -->
        <!-- can be 'path' or 'element' -->
        <!-- eg first-name or publication.creator.person.first-name -->
        <xsl:param name="nametype"/>
        <xsl:if test="string(text()) and normalize-space(text())!=''">
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$path"/>
                <xsl:with-param name="fieldvalue" select="text()"/>
                <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
            </xsl:call-template>
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname">metadata</xsl:with-param>
                <xsl:with-param name="fieldvalue" select="text()"/>
                <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$indexAttributes='yes'">
            <!-- ITERATE ALL ATTRIBUTES AND WRITE ELEMENT-NAME, ATTRIBUTE-NAME AND ATTRIBUTE-VALUE -->
            <xsl:for-each select="@*">
                <xsl:if test="string(.) and normalize-space(.)!=''
                        and string($path) and normalize-space($path)!=''">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="concat($path,'.',local-name())"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                    </xsl:call-template>
                    <!-- ADDITIONALLY WRITE VALUE IN metadata-index -->
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$CONTEXTNAME"/>
                        <xsl:with-param name="fieldname">metadata</xsl:with-param>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>
        <xsl:for-each select="./*">
            <xsl:variable name="fieldname">
                <xsl:choose>
                    <xsl:when test="$nametype='element'">
                            <xsl:value-of select="local-name()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="string($path) and normalize-space($path)!=''">
                                <xsl:value-of select="concat($path,'.',local-name())"/>
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
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS ALL PROPERTIES -->
    <xsl:template name="processProperties">
        <xsl:param name="path"/>
        <xsl:for-each select="$path">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">no</xsl:with-param>
                <xsl:with-param name="nametype">path</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS MD-RECORD DEFINITIONS -->
    <xsl:template name="processMdRecordDefinitions">
        <xsl:param name="path"/>
        <xsl:for-each select="$path">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">element</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS RESOURCE DEFINITIONS -->
    <xsl:template name="processResourceDefinitions">
        <xsl:param name="path"/>
        <xsl:for-each select="$path">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">element</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS CONTENT-STREAMS -->
    <xsl:template name="processContentStreams">
        <xsl:param name="path"/>
        <xsl:for-each select="$path">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">element</xsl:with-param>
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
		<xsl:if test="string($fieldvalue) and normalize-space($fieldvalue)!=''">
            <xsl:variable name="isDateOrDecimal" select="element-type-helper:isDateOrDecimal($fieldvalue)"/>
			<IndexField termVector="NO">
				<xsl:attribute name="index">
				    <xsl:choose>
                        <xsl:when test="$isDateOrDecimal = true()">
                            <xsl:value-of select="string('UN_TOKENIZED')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$indextype"/>
                        </xsl:otherwise>
				    </xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="store">
					<xsl:value-of select="$store"/>
				</xsl:attribute>
				<xsl:attribute name="IFname">
					<xsl:value-of select="concat($context,'.',$fieldname)"/>
				</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$isDateOrDecimal = true()">
                        <xsl:value-of select="translate($fieldvalue, 'TZ', 'tz')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$fieldvalue"/>
                    </xsl:otherwise>
                </xsl:choose>
				
			</IndexField>
            <xsl:call-template name="writeSortField">
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="fieldname" select="$fieldname"/>
                <xsl:with-param name="fieldvalue" select="$fieldvalue"/>
            </xsl:call-template>
		</xsl:if>
  	</xsl:template>
  		
    <!--  WRITE SORTFIELD -->
    <xsl:template name="writeSortField">
        <xsl:param name="context"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="fieldvalue"/>
        <xsl:if test="string($fieldvalue) 
                    and normalize-space($fieldvalue)!=''
                    and sortfield-helper:checkSortField(concat($SORTCONTEXTPREFIX,'.',$context,'.',$fieldname)) = false()">
            <IndexField termVector="NO" index="UN_TOKENIZED" store="NO">
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($SORTCONTEXTPREFIX,'.',$context,'.',$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="string-helper:getNormalizedString($fieldvalue)"/>
            </IndexField>
        </xsl:if>
    </xsl:template>
       
</xsl:stylesheet>	
