<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Notes:
Following index fields are written by this stylesheet:

items and containers:
    -escidoc.objecttype
    -escidoc.objid
    -escidoc.metadata (contains values of all properties-elements and all elements of md-record with name=escidoc)
    -escidoc.parent
    -xml_representation (unindexed, stored, contains xml for hit)
    -xml_metadata (unindexed, stored, used for highlighting, contains xml of md-record with name=escidoc)
    -all properties-elements and attributes with field-name=full path to element
    -all metadata-elements of md-record with name=escidoc with field-name=full path to element
    
items:
    -all properties-elements and attributes of each component with field-name=full path to element
    -all metadata-elements of md-record with name=escidoc of each component with field-name=full path to element
    -escidoc.fulltext (dependent on variable MINIMAL_FULLTEXT_VISIBILITY, maybe only fulltext of components with visibility=public is indexed)
    -stored_fulltext1-n (unindexed, stored, used for highlighting)
    -stored_filename1-n (unindexed, stored, used for highlighting)
    
containers:
    -escidoc.member
    
 -->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xalan="http://xml.apache.org/xalan"
		xmlns:lastdate-helper="xalan://de.escidoc.sb.gsearch.xslt.LastdateHelper"
		xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
        xmlns:element-type-helper="xalan://de.escidoc.sb.gsearch.xslt.ElementTypeHelper"
		xmlns:sortfield-helper="xalan://de.escidoc.sb.gsearch.xslt.SortFieldHelper"
        xmlns:escidoc-core-accessor="xalan://de.escidoc.sb.gsearch.xslt.EscidocCoreAccessor" 
		extension-element-prefixes="lastdate-helper string-helper element-type-helper sortfield-helper escidoc-core-accessor">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <!-- Include stylesheet that writes important fields for gsearch -->
    <xsl:include href="gsearchAttributes.xslt"/>
    
	<!-- Include stylesheet that indexes values for permission-filtering -->
	<xsl:include href="permissions.xslt"/>
	
    <!-- Parameters that get passed while calling this stylesheet-transformation -->
    <xsl:param name="SUPPORTED_MIMETYPES"/>
    <xsl:param name="INDEX_FULLTEXT_VISIBILITIES"/>

    <!-- Store Fields for Scan-Operation-->
    <xsl:variable name="STORE_FOR_SCAN">YES</xsl:variable>

	<xsl:variable name="CONTEXTNAME">escidoc</xsl:variable>
	<xsl:variable name="COMPONENT_CONTEXTNAME">escidoc.component</xsl:variable>
    <xsl:variable name="SORTCONTEXTPREFIX">sort</xsl:variable>

	<!-- Paths to Metadata -->
	<xsl:variable name="ITEM_METADATAPATH" select="/*[local-name()='item']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
	<xsl:variable name="CONTAINER_METADATAPATH" select="/*[local-name()='container']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
	<xsl:variable name="COMPONENT_METADATAPATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']/*[local-name()='md-records']/*[local-name()='md-record'][@name='escidoc']"/>
	
	<!-- Paths to Properties -->
	<xsl:variable name="ITEM_PROPERTIESPATH" select="/*[local-name()='item']/*[local-name()='properties']"/>
	<xsl:variable name="CONTAINER_PROPERTIESPATH" select="/*[local-name()='container']/*[local-name()='properties']"/>
	<xsl:variable name="COMPONENT_PROPERTIESPATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']/*[local-name()='properties']"/>

    <!-- Paths to Components -->
    <xsl:variable name="COMPONENT_PATH" select="/*[local-name()='item']/*[local-name()='components']/*[local-name()='component']"/>

    <!-- Paths to Members -->
    <xsl:variable name="MEMBERS_PATH" select="/*[local-name()='container']/*[local-name()='struct-map']"/>

    <!-- Public Status -->
    <xsl:variable name="PUBLIC_STATUS" select="/*/*[local-name()='properties']/*[local-name()='public-status']"/>
    <xsl:variable name="STATUS_WITHDRAWN">withdrawn</xsl:variable>

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

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlItem">
        <xsl:copy-of select="/*[local-name()='item']"/>
    </xsl:template>

    <!-- WRITE THE XML THAT GETS RETURNED BY THE SEARCH -->
    <xsl:template name="writeSearchXmlContainer">
        <xsl:copy-of select="/*[local-name()='container']"/>
    </xsl:template>

	<xsl:template name="processItem">
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objecttype</xsl:with-param>
			<xsl:with-param name="fieldvalue">item</xsl:with-param>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objid</xsl:with-param>
			<xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(/*[local-name()='item']/@objid)"/>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
			<xsl:text disable-output-escaping="yes">
				&lt;![CDATA[
			</xsl:text>
				<xsl:call-template name="writeSearchXmlItem"/>
			<xsl:text disable-output-escaping="yes">
				]]&gt;
			</xsl:text>
		</IndexField>
		
		<!-- PROPERTIES -->
		<xsl:call-template name="processProperties">
			<xsl:with-param name="path" select="$ITEM_PROPERTIESPATH"/>
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
		</xsl:call-template>
 			
		<!-- ESCIDOC METADATA -->
		<xsl:call-template name="processMetadata">
			<xsl:with-param name="path" select="$ITEM_METADATAPATH"/>
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
		</xsl:call-template>
		
        <!-- COMPONENT PROPERTIES -->
        <xsl:call-template name="processProperties">
            <xsl:with-param name="path" select="$COMPONENT_PROPERTIESPATH"/>
            <xsl:with-param name="context" select="$COMPONENT_CONTEXTNAME"/>
        </xsl:call-template>
        
		<!-- COMPONENT METADATA -->
        <xsl:call-template name="processMetadata">
            <xsl:with-param name="path" select="$COMPONENT_METADATAPATH"/>
            <xsl:with-param name="context" select="$COMPONENT_CONTEXTNAME"/>
        </xsl:call-template>
		
        <!-- FULLTEXTS -->
        <!-- FULLTEXTS OF ITEMS IN PUBLIC-STATUS WITHDRAWN ARE NOT ACCESSIBLE -->
        <xsl:if test="string($PUBLIC_STATUS) and $PUBLIC_STATUS!=$STATUS_WITHDRAWN">
            <xsl:call-template name="processComponents">
                <xsl:with-param name="num" select="0"/>
                <xsl:with-param name="components" select="$COMPONENT_PATH"/>
                <xsl:with-param name="matchNum" select="1"/>
            </xsl:call-template>
        </xsl:if>
        
        <!-- USER DEFINED INDEXES -->
        <xsl:call-template name="writeUserdefinedIndexes" />
	</xsl:template>

	<xsl:template name="processContainer">
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objecttype</xsl:with-param>
			<xsl:with-param name="fieldvalue">container</xsl:with-param>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<xsl:call-template name="writeIndexField">
			<xsl:with-param name="context" select="$CONTEXTNAME"/>
			<xsl:with-param name="fieldname">objid</xsl:with-param>
			<xsl:with-param name="fieldvalue" select="string-helper:removeVersionIdentifier(/*[local-name()='container']/@objid)"/>
			<xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
			<xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
		</xsl:call-template>
		<IndexField IFname="xml_representation" index="NO" store="YES" termVector="NO">
			<xsl:text disable-output-escaping="yes">
				&lt;![CDATA[
			</xsl:text>
				<xsl:call-template name="writeSearchXmlContainer"/>
			<xsl:text disable-output-escaping="yes">
				]]&gt;
			</xsl:text>
		</IndexField>
		
		<!-- PROPERTIES -->
		<xsl:call-template name="processProperties">
			<xsl:with-param name="path" select="$CONTAINER_PROPERTIESPATH"/>
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
		</xsl:call-template>
 			
		<!-- ESCIDOC METADATA -->
		<xsl:call-template name="processMetadata">
			<xsl:with-param name="path" select="$CONTAINER_METADATAPATH"/>
            <xsl:with-param name="context" select="$CONTEXTNAME"/>
		</xsl:call-template>
		
        <!-- MEMBERS -->
        <xsl:call-template name="processMembers">
            <xsl:with-param name="path" select="$MEMBERS_PATH"/>
        </xsl:call-template>
        
        <!-- USER DEFINED INDEXES -->
        <xsl:call-template name="writeUserdefinedIndexes" />
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

	<!-- PROCESS METADATA -->
	<xsl:template name="processMetadata">
  		<xsl:param name="path"/>
        <xsl:param name="context"/>
		<xsl:for-each select="$path">
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
				<xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
				<xsl:with-param name="nametype">path</xsl:with-param>
			</xsl:call-template>
  		</xsl:for-each>
	</xsl:template>

    <!-- PROCESS ALL PROPERTIES -->
    <xsl:template name="processProperties">
        <xsl:param name="path"/>
        <xsl:param name="context"/>
        <xsl:for-each select="$path">
            <xsl:call-template name="processElementTree">
                <xsl:with-param name="path"/>
                <xsl:with-param name="context" select="$context"/>
                <xsl:with-param name="indexAttributes">yes</xsl:with-param>
                <xsl:with-param name="nametype">path</xsl:with-param>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- PROCESS ALL MEMBERS -->
    <xsl:template name="processMembers">
        <xsl:param name="path"/>
        <xsl:for-each select="$path/*/@objid">
            <xsl:call-template name="writeIndexField">
                <xsl:with-param name="context" select="$CONTEXTNAME"/>
                <xsl:with-param name="fieldname">member</xsl:with-param>
                <xsl:with-param name="fieldvalue" select="."/>
                <xsl:with-param name="indextype">UN_TOKENIZED</xsl:with-param>
                <xsl:with-param name="store" select="$STORE_FOR_SCAN"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- RECURSIVE ITERATION FOR COMPONENTS (FULLTEXTS) -->
    <!-- STORE EVERYTHING IN FIELD fulltext FOR SEARCH-->
    <!-- STORE EACH FULLTEXT IN SEPARATE FIELD stored_fulltext<n> FOR HIGHLIGHTING IF VISIBILITY IS PUBLIC-->
    <!-- ADDITIONALLY STORE HREF OF COMPONENT IN SEPARATE FIELD stored_filename<n> FOR HIGHLIGHTING THE LOCATION OF THE FULLTEXT IF VISIBILITY IS PUBLIC-->
    <!-- ONLY INDEX FULLTEXTS IF MIME_TYPE IS text/xml, application/xml, text/plain, application/msword or application/pdf -->
    <xsl:template name="processComponents" xmlns:xlink="http://www.w3.org/1999/xlink">
        <xsl:param name="num"/>
        <xsl:param name="components"/>
        <xsl:param name="matchNum"/>
        <xsl:variable name="visibility" select="$components[$num]/*[local-name()='properties']/*[local-name()='visibility']"/>
        <xsl:variable name="mime-type">
            <xsl:value-of select="$components[$num]/*[local-name()='properties']/*[local-name()='mime-type']"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="string($mime-type) 
                            and contains($SUPPORTED_MIMETYPES,$mime-type)
                            and ((string($INDEX_FULLTEXT_VISIBILITIES) 
                            and contains($INDEX_FULLTEXT_VISIBILITIES,$visibility))
                            or (string($visibility) and $visibility='public'))">
                <!-- INDEX FULLTEXT -->
                <IndexField index="TOKENIZED" store="YES" termVector="NO">
                    <xsl:attribute name="dsId">
                        <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
                    </xsl:attribute>
                    <xsl:attribute name="IFname">
                        <xsl:value-of select="concat($CONTEXTNAME,'.fulltext')"/>
                    </xsl:attribute>
                    <xsl:attribute name="store">
                        <xsl:value-of select="$STORE_FOR_SCAN"/>
                    </xsl:attribute>
                </IndexField>

                <!-- SEPERATELY STORE EACH FULLTEXT IN DIFFERENT FIELD FOR HIGHLIGHTING -->
                <!-- ONLY WRITE HIGHLIGHT FIELD IF FULLTEXT IS PUBLICLY ACCESSIBLE -->
                <xsl:choose>
	                <xsl:when test="string($visibility) and $visibility='public'">
	                    <IndexField index="NO" store="YES" termVector="NO">
	                        <xsl:attribute name="dsId">
	                            <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
	                        </xsl:attribute>
	                        <xsl:attribute name="IFname">
	                            <xsl:value-of select="concat('stored_fulltext',$matchNum)"/>
	                        </xsl:attribute>
	                    </IndexField>
	
	                    <!-- SEPERATELY STORE FILENAME FOR EACH FULLTEXT FOR HIGHLIGHTING -->
	                    <IndexField index="NO" store="YES" termVector="NO">
	                        <xsl:attribute name="IFname">
	                            <xsl:value-of select="concat('stored_filename',$matchNum)"/>
	                        </xsl:attribute>
	                        <xsl:value-of select="$components[$num]/*[local-name()='content']/@xlink:href"/>
	                    </IndexField>
		                <xsl:if test="$components[$num + 1]">
		                    <xsl:call-template name="processComponents">
		                        <xsl:with-param name="num" select="$num + 1"/>
		                        <xsl:with-param name="components" select="$components"/>
		                        <xsl:with-param name="matchNum" select="$matchNum + 1"/>
		                    </xsl:call-template>
		                </xsl:if>
	                </xsl:when>
	                <xsl:otherwise>
                        <xsl:if test="$components[$num + 1]">
                            <xsl:call-template name="processComponents">
                                <xsl:with-param name="num" select="$num + 1"/>
                                <xsl:with-param name="components" select="$components"/>
                                <xsl:with-param name="matchNum" select="$matchNum"/>
                            </xsl:call-template>
                        </xsl:if>
	                </xsl:otherwise>
	            </xsl:choose>

            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="$components[$num + 1]">
                    <xsl:call-template name="processComponents">
                        <xsl:with-param name="num" select="$num + 1"/>
                        <xsl:with-param name="components" select="$components"/>
                        <xsl:with-param name="matchNum" select="$matchNum"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
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
        
    <!-- USER DEFINED INDEX FIELDS -->
    <xsl:variable name="userdefined-indexes">
        <userdefined-index name="parent">
            <xsl:attribute name="context">
                <xsl:value-of select="$CONTEXTNAME"/>
            </xsl:attribute>
            <xsl:variable name="type">
                <xsl:for-each select="*">
                    <xsl:if test="position() = 1">
                        <xsl:value-of select="local-name()"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:variable>
            
            <xsl:if test="$type='item'">
                <element index="UN_TOKENIZED">
                    <xsl:variable name="objectId" select="/*/@objid"/>
                    <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                        concat('/ir/item/',$objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
                </element>
            </xsl:if>
            <xsl:if test="$type='container'">
                <element index="UN_TOKENIZED">
                    <xsl:variable name="objectId" select="/*/@objid"/>
                    <xsl:value-of select="escidoc-core-accessor:getObjectAttribute(
                        concat('/ir/container/',$objectId, '/resources/parents'),'/parents/parent','href','http://www.w3.org/1999/xlink','false','true')"/>
                </element>
            </xsl:if>
        </userdefined-index>
    </xsl:variable>
</xsl:stylesheet>	
