<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Notes:
-each metadata-field is indexed as own field ($CONTEXTNAME<md-elementname>) and additionally into field $CONTEXTNAMEmetadata
-each property-field is indexed as own field ($CONTEXTNAME<property-elementname>)
-store=yes: 
	-all fields for highlighting: xml_metadata
	-all fields for display: xml_representation
	-all fields for sorting
	-just all fields, except PID and sortfields, this is because scan-operation needs stored fields
-!!all fields are stored because of the scan-request!!
-sorting can be done for all fields that are stored.
-additional sortfields can be defined in variable sortfields
-additional compound indexfields can be defined in variable userdefined-indexes

-
 -->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xalan="http://xml.apache.org/xalan"
		xmlns:language-helper="xalan://de.escidoc.sb.gsearch.xslt.LanguageHelper" 
		xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
		xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:element-type-helper="xalan://de.escidoc.sb.gsearch.xslt.ElementTypeHelper"
		xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
		xmlns:escidoc-core-accessor="xalan://de.escidoc.sb.gsearch.xslt.EscidocCoreAccessor" 
		extension-element-prefixes="language-helper lastdate-helper string-helper element-type-helper sortfield-helper escidoc-core-accessor">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	
    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="index/gsearchAttributes.xslt"/>
    
    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">YES</xsl:variable>

	<xsl:variable name="CONTEXTNAME"></xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">sort.</xsl:variable>

	<!-- Paths to Metadata -->
	<xsl:variable name="METADATAPATH" select="/*[local-name()='organizational-unit']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
	
	<!-- Paths to Properties -->
	<xsl:variable name="PROPERTIESPATH" select="/*[local-name()='organizational-unit']/*[local-name()='properties']"/>

	<!-- Name of Properties that have to get indexed-->
	<xsl:variable name="PROPERTY_ELEMENTS"> creation-date public-status has-children </xsl:variable>

	<!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
	<xsl:template name="writeSearchXmlOrgUnit">
		<xsl:copy-of select="/*[local-name()='organizational-unit']"/>
	</xsl:template>

    <!-- MAIN TEMPLATE -->
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
		<xsl:choose>
			<xsl:when test="$type='organizational-unit'">
				<xsl:call-template name="processOrgUnit"/>
			</xsl:when>
		</xsl:choose>
		</IndexDocument>
	</xsl:template>

    <!-- WRITE INDEX FOR ORG UNIT -->
	<xsl:template name="processOrgUnit">
		<xsl:variable name="PID" select="string-helper:getSubstringAfterLast(/*[local-name()='organizational-unit']/@*[local-name()='href'], '/')"/>
			<xsl:call-template name="writeIndexField">
				<xsl:with-param name="context" select="$CONTEXTNAME"/>
				<xsl:with-param name="fieldname">objid</xsl:with-param>
				<xsl:with-param name="fieldvalue" select="$PID"/>
				<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
				<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
			</xsl:call-template>
            <!-- Wrtite item.xml as Field xml_representation, gets returned by the search -->
            <!--  DONT CHANGE THIS!! -->
			<IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
				<xsl:text disable-output-escaping="yes">
					&lt;![CDATA[
				</xsl:text>
					<xsl:call-template name="writeSearchXmlOrgUnit"/>
				<xsl:text disable-output-escaping="yes">
					]]&gt;
				</xsl:text>
			</IndexField>
			
			<!-- INDEX PROPERTIES -->
			<xsl:call-template name="processProperties">
				<xsl:with-param name="path" select="$PROPERTIESPATH"/>
				<xsl:with-param name="elements" select="$PROPERTY_ELEMENTS"/>
			</xsl:call-template>
  			
			<!-- INDEX ESCIDOC METADATA -->
			<xsl:call-template name="processMetadata">
				<xsl:with-param name="path" select="$METADATAPATH"/>
			</xsl:call-template>
			
			<!-- WRITE SORT FIELDS -->
			<xsl:for-each select="xalan:nodeset($sortfields)/sortfield">
				<xsl:if test="./@type='organizational-unit'">
					<xsl:call-template name="writeSortField">
						<xsl:with-param name="context" select="$CONTEXTNAME"/>
						<xsl:with-param name="fieldname" select="./@name"/>
						<xsl:with-param name="fieldvalue" select="./@path"/>
					</xsl:call-template>
				</xsl:if>
  			</xsl:for-each>
  			
  			<!-- WRITE USER DEFINED INDEXES -->
			<xsl:call-template name="writeUserdefinedIndexes" />
	</xsl:template>

	<!-- RECURSIVE ITERATION OF ELEMENTS -->
	<!-- ITERATE ALL ELEMENTS AND WRITE ELEMENT-NAME AND ELEMENT-VALUE -->
	<xsl:template name="processElementTree">
        <!-- name of index-field -->
  		<xsl:param name="path"/>
        <!-- prefix for index-name -->
  		<xsl:param name="context"/>
  		<!-- nametype defines if paths are used for indexnames or elementname only -->
  		<!-- eg first-name or publication.creator.person.first-name -->
  		<!-- can be 'path' or 'element' -->
  		<xsl:param name="nametype"/>
		<xsl:if test="string(text()) and normalize-space(text())!=''">
			<xsl:call-template name="writeIndexField">
				<xsl:with-param name="context" select="$context"/>
				<xsl:with-param name="fieldname" select="$path"/>
				<xsl:with-param name="fieldvalue" select="text()"/>
				<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
				<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
			</xsl:call-template>
            <!-- ADDITIONALLY WRITE VALUE IN metadata-index -->
			<xsl:call-template name="writeIndexField">
				<xsl:with-param name="context" select="$context"/>
				<xsl:with-param name="fieldname">metadata</xsl:with-param>
				<xsl:with-param name="fieldvalue" select="text()"/>
				<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
				<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
			</xsl:call-template>
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
				<xsl:with-param name="path" select="$fieldname"/>
				<xsl:with-param name="nametype" select="$nametype"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

	<!-- PROCESS METADATA -->
	<xsl:template name="processMetadata">
  		<xsl:param name="path"/>
		<xsl:for-each select="$path">
            <!-- INDEX FIELD xml_metadata IS USED FOR HIGHLIGHTING -->
            <!-- DONT CHANGE THIS -->
			<IndexField IFname="xml_metadata" index="NO" store="YES" termVector="NO">
				<xsl:text disable-output-escaping="yes">
					&lt;![CDATA[
				</xsl:text>
					<xsl:copy-of select="."/>
				<xsl:text disable-output-escaping="yes">
					]]&gt;
				</xsl:text>
			</IndexField>
			<xsl:call-template name="processElementTree">
				<xsl:with-param name="path"/>
				<xsl:with-param name="context" select="$CONTEXTNAME"/>
				<xsl:with-param name="nametype">element</xsl:with-param>
			</xsl:call-template>
  		</xsl:for-each>
	</xsl:template>

	<!-- PROCESS CERTAIN PROPERTIES -->
	<!-- Dont index all, only the ones stated in variable $elements -->
	<xsl:template name="processProperties">
  		<xsl:param name="path"/>
  		<xsl:param name="elements"/>
		<xsl:for-each select="$path">
			<xsl:for-each select="./*">
				<xsl:if test="not(string($elements)) or normalize-space($elements)=''
							or contains($elements,concat(' ',local-name(),' '))
							or contains($elements,concat(' ',local-name(),'/'))">
						<xsl:if test="not(string($elements)) or normalize-space($elements)=''
							or contains($elements,concat(' ',local-name(),' '))">
							<xsl:call-template name="writeIndexField">
								<xsl:with-param name="context" select="$CONTEXTNAME"/>
								<xsl:with-param name="fieldname" select="local-name()"/>
								<xsl:with-param name="fieldvalue" select="text()"/>
								<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
								<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="not(string($elements)) or normalize-space($elements)=''
							or contains($elements,concat(' ',local-name(),'/'))">
							<xsl:variable name="elementname" select="local-name()" />
							<xsl:for-each select="@*">
								<xsl:if test="contains($elements,concat(' ',$elementname,'/@',local-name(),' '))">
									<xsl:call-template name="writeIndexField">
										<xsl:with-param name="context" select="$CONTEXTNAME"/>
										<xsl:with-param name="fieldname" select="concat($elementname,'.',local-name())"/>
										<xsl:with-param name="fieldvalue" select="."/>
										<xsl:with-param name="indextype">TOKENIZED</xsl:with-param>
										<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
									</xsl:call-template>
								</xsl:if>
							</xsl:for-each>
						</xsl:if>
				</xsl:if>
			</xsl:for-each>
  		</xsl:for-each>
	</xsl:template>

	<!--  WRITE INDEXFIELD -->
    <!-- AUTOMATICALLY CALLS writeSortField -->
    <!-- IF FIELDVALUE IS DATE OR DECIMAL, WRITE INDEXFIELD UN_TOKENIZED -->
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
					<xsl:value-of select="concat($context,$fieldname)"/>
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
                    and sortfield-helper:checkSortField(concat($SORTCONTEXTPREFIX,$context,$fieldname)) = false()">
            <IndexField termVector="NO" index="UN_TOKENIZED" store="NO">
                <xsl:attribute name="IFname">
                    <xsl:value-of select="concat($SORTCONTEXTPREFIX,$context,$fieldname)"/>
                </xsl:attribute>
                <xsl:value-of select="string-helper:getNormalizedString($fieldvalue)"/>
            </IndexField>
        </xsl:if>
    </xsl:template>
        
	<!-- WRITE USERDEFINED INDEX -->
	<xsl:template name="writeUserdefinedIndexes">
		<xsl:for-each select="xalan:nodeset($userdefined-indexes)/userdefined-index">
			<xsl:variable name="index-name" select="./@name"/>
			<xsl:variable name="context" select="./@context"/>
			<xsl:for-each select="./element">
		    	<xsl:if test="string(.) and normalize-space(.)!=''">
                    <xsl:call-template name="writeIndexField">
                        <xsl:with-param name="context" select="$context"/>
                        <xsl:with-param name="fieldname" select="$index-name"/>
                        <xsl:with-param name="fieldvalue" select="."/>
                        <xsl:with-param name="indextype" select="./@index"/>
                        <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
                    </xsl:call-template>
				</xsl:if>
			</xsl:for-each>
  		</xsl:for-each>
  	</xsl:template>
  		
	<!-- SORTFIELDS -->
	<xsl:variable name="sortfields">
	</xsl:variable>
	
	<!-- USER DEFINED INDEX FIELDS -->
	<xsl:variable name="userdefined-indexes">

        <!-- USER DEFINED INDEX: metadata -->
		<userdefined-index name="metadata">
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<xsl:for-each select="$METADATAPATH//*[local-name()='identifier']">
				<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
				<xsl:if test="string($idtype) 
						and normalize-space($idtype)!=''">
					<element index="TOKENIZED">
						<xsl:value-of select="concat($idtype,':',.)"/>
					</element>
					<element index="TOKENIZED">
						<xsl:value-of select="concat($idtype,' ',.)"/>
					</element>
				</xsl:if>
			</xsl:for-each>
			<element index="TOKENIZED">
				<xsl:value-of select="string-helper:getSubstringAfterLast(/*[local-name()='organizational-unit']/@*[local-name()='href'], '/')"/>
			</element>
		</userdefined-index>

        <!-- USER DEFINED INDEX: parent.objid -->
		<userdefined-index name="parent.objid">
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<xsl:for-each select="/*[local-name()='organizational-unit']/*[local-name()='parents']/*[local-name()='parent']/@*[local-name()='href']">
				<element index="TOKENIZED">
                    <xsl:value-of select="string-helper:getSubstringAfterLast(., '/')"/>
				</element>
			</xsl:for-each>
		</userdefined-index>

        <!-- USER DEFINED INDEX: ancestor-organization-pid -->
		<userdefined-index name="ancestor-organization-pid">
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<element index="TOKENIZED">
                <xsl:variable name="objectId" select="string-helper:getSubstringAfterLast(/*[local-name()='organizational-unit']/@*[local-name()='href'], '/')"/>
                <xsl:if test="string($objectId) and normalize-space($objectId)!=''">
                    <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                        concat('/oum/organizational-unit/',$objectId,'/resources/path-list'),'/organizational-unit-path-list/organizational-unit-path/organizational-unit-ref','href','http://www.w3.org/1999/xlink','false','true')"/>
                </xsl:if>
			</element>
		</userdefined-index>

        <!-- USER DEFINED INDEX: any-identifier -->
		<userdefined-index name="any-identifier">
			<xsl:attribute name="context">
				<xsl:value-of select="$CONTEXTNAME"/>
			</xsl:attribute>
			<element index="TOKENIZED">
				<xsl:value-of select="string-helper:getSubstringAfterLast(/*[local-name()='organizational-unit']/@*[local-name()='href'], '/')"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="$PROPERTIESPATH/*[local-name()='pid']"/>
			</element>
			<element index="TOKENIZED">
				<xsl:value-of select="$PROPERTIESPATH/*[local-name()='latest-release']/*[local-name()='pid']"/>
			</element>
			<xsl:for-each select="$METADATAPATH//*[local-name()='identifier']">
				<xsl:variable name="idtype" select="string-helper:getSubstringAfterLast(./@*[local-name()='type'],':')" />
				<xsl:if test="string($idtype) 
						and normalize-space($idtype)!=''">
					<element index="TOKENIZED">
						<xsl:value-of select="concat($idtype,':',.)"/>
					</element>
					<element index="TOKENIZED">
						<xsl:value-of select="concat($idtype,' ',.)"/>
					</element>
				</xsl:if>
			</xsl:for-each>
		</userdefined-index>

        <!-- USER DEFINED INDEX: created-by.name -->
        <userdefined-index name="created-by.name">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:variable name="objectId" select="string-helper:getSubstringAfterLast($PROPERTIESPATH/*[local-name()='created-by']/@*[local-name()='href'], '/')"/>
                <xsl:if test="string($objectId) and normalize-space($objectId)!=''">
                    <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                        concat('/aa/user-account/',$objectId),'/user-account/properties/name','','','false','false')"/>
                </xsl:if>
            </element>
        </userdefined-index>

        <!-- USER DEFINED INDEX: last-modification-date -->
        <userdefined-index name="last-modification-date">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <element index="TOKENIZED">
                <xsl:value-of select="/*[local-name()='organizational-unit']/@last-modification-date"/>
            </element>
        </userdefined-index>
	</xsl:variable>

</xsl:stylesheet>	
