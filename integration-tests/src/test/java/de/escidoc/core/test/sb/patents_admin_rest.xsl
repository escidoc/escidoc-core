<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:string-helper="xalan://de.escidoc.core.test.sb.StringHelper" 
		extension-element-prefixes="string-helper"
 		xmlns:item="http://www.escidoc.de/schemas/item/0.10"			
 		xmlns:metadata="http://www.escidoc.de/metadata/schema/0.1" 
		xmlns:metadata-records="http://www.escidoc.de/schemas/metadatarecords/0.5"
		xmlns:components="http://www.escidoc.de/schemas/components/0.9" 
		xmlns:container="http://www.escidoc.de/schemas/container/0.9"
		exclude-result-prefixes="string-helper item container components">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="fulltextPath" select="fulltextPath"/>
    <xsl:param name="titleAppendix" select="titleAppendix"/>
    <xsl:param name="alternativeAppendix" select="alternativeAppendix"/>
	<xsl:param name="createdate" select="createdate"/>
	<xsl:param name="modifydate" select="modifydate"/>
	<xsl:param name="submitdate" select="submitdate"/>
	<xsl:param name="acceptdate" select="acceptdate"/>
	<xsl:param name="publishdate" select="publishdate"/>
	<xsl:param name="issueddate" select="issueddate"/>
	<xsl:param name="chineseString" select="chineseString"/>
	<xsl:param name="user" select="user"/>
	<xsl:param name="status" select="status"/>
	<xsl:param name="docnum" select="docnum"/>
    <xsl:param name="specialLetter" select="specialLetter"/>
	<xsl:variable name="ACCESSIONNUMBER" select="//PATDOC/@DNUM"/>
	
	<xsl:template match="/">
		<xsl:call-template name="process"/>
	</xsl:template>

	<xsl:template name="process">
		<escidocItem:item
			xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.10"
			xmlns:srel="http://escidoc.de/core/01/structural-relations/"
			xmlns:prop="http://escidoc.de/core/01/properties/"
			xmlns:xlink="http://www.w3.org/1999/xlink"
			xlink:type="simple"
			xml:base="http://localhost:8080">
			<xsl:attribute name="xlink:title">
				<xsl:value-of select="//B542[1]"/>
			</xsl:attribute>
			
			<escidocItem:properties xlink:type="simple">
				<srel:context xlink:type="simple"
					xlink:href="/ir/context/escidoc:persistent3" />
				<srel:content-model xlink:type="simple"
					xlink:href="/cmm/content-model/escidoc:persistent4" />
				<properties:content-model-specific xmlns:properties="http://escidoc.de/core/01/properties/">
					<element1>cms-test1</element1>
					<element2>cms-test2</element2>
				</properties:content-model-specific>
			</escidocItem:properties>
				
			<!-- WRITE METADATA -->
			<xsl:call-template name="writeMetadata">
				<xsl:with-param name="extraData">true</xsl:with-param>
			</xsl:call-template>
			<escidocComponents:components
				xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9"
				xlink:type="simple" xlink:href="/ir/item/escidoc:198/components"
				xlink:title="Components of Item" xml:base="http://localhost:8080">
				<escidocComponents:component>
					<escidocComponents:properties
						xmlns:properties="http://escidoc.de/core/01/properties/"
						xmlns:version="http://escidoc.de/core/01/properties/version/"
						xmlns:xlink="http://www.w3.org/1999/xlink"
						xlink:type="simple">
						<properties:description>XML File</properties:description>
						<properties:valid-status>valid</properties:valid-status>
						<properties:visibility>public</properties:visibility>
						<properties:content-category>pre-print</properties:content-category>
						<properties:file-name>
							<xsl:value-of select="concat('EPFull_',$ACCESSIONNUMBER,'.xml')"/>
						</properties:file-name>
						<properties:mime-type>text/xml</properties:mime-type>
					</escidocComponents:properties>
					<escidocComponents:content xlink:type="simple">
						<xsl:attribute name="xlink:href">
							<xsl:value-of select="concat($fulltextPath,'xml/EPFull_',$ACCESSIONNUMBER,'.xml')"/>
						</xsl:attribute>
						<xsl:attribute name="storage">internal-managed</xsl:attribute>
					</escidocComponents:content>	
					<xsl:call-template name="writeMetadata">
						<xsl:with-param name="prefix">comp</xsl:with-param>
						<xsl:with-param name="extraData">false</xsl:with-param>
					</xsl:call-template>
				</escidocComponents:component>
				<escidocComponents:component>
					<escidocComponents:properties
						xmlns:properties="http://escidoc.de/core/01/properties/"
						xmlns:version="http://escidoc.de/core/01/properties/version/"
						xmlns:xlink="http://www.w3.org/1999/xlink"
						xlink:type="simple">
						<properties:description>PDF File</properties:description>
						<properties:valid-status>valid</properties:valid-status>
						<properties:visibility>private</properties:visibility>
						<properties:content-category>pre-print</properties:content-category>
						<properties:file-name>
							<xsl:value-of select="concat('EPFull_',$ACCESSIONNUMBER,'.pdf')"/>
						</properties:file-name>
						<properties:mime-type>application/pdf</properties:mime-type>
					</escidocComponents:properties>
					<escidocComponents:content xlink:type="simple">
						<xsl:attribute name="xlink:href">
							<xsl:value-of select="concat($fulltextPath,'pdf/EPFull_',$ACCESSIONNUMBER,'.pdf')"/>
						</xsl:attribute>
						<xsl:attribute name="storage">internal-managed</xsl:attribute>
					</escidocComponents:content>	
					<xsl:call-template name="writeMetadata">
						<xsl:with-param name="prefix">comp</xsl:with-param>
						<xsl:with-param name="extraData">false</xsl:with-param>
					</xsl:call-template>
				</escidocComponents:component>
			</escidocComponents:components>
		</escidocItem:item>
	</xsl:template>

	<!-- WRITE METADATA SEGMENT -->
	<xsl:template name="writeMetadata">
  		<xsl:param name="prefix"/>
  		<xsl:param name="extraData"/>
		<escidocMetadataRecords:md-records
			xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5">
			<escidocMetadataRecords:md-record
				xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
				xmlns:xlink="http://www.w3.org/1999/xlink" name="escidoc"
				schema="http://www.escidoc-project.de/metadata/schema/0.1/"
				xlink:title="eSciDoc internal MD Record" xlink:type="simple">
				<escidocMetadataProfile:publication type="article" 
      				xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/" 
      				xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" 
					xmlns:dc="http://purl.org/dc/elements/1.1/" 
					xmlns:dcterms="http://purl.org/dc/terms/" 
					xmlns:ec="http://escidoc.mpg.de/metadataprofile/schema/0.1/collection" 
					xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
					xsi:schemaLocation="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication ../escidoc_publication_profile.xsd">
					<xsl:for-each select="//SNM">
						<publication:creator role="author">
							<e:person>
								<e:family-name>
									<xsl:value-of select="substring-before(.,',')"/>
									<!-- xsl:value-of select="string-helper:getSplitPart(.,',',0)"/ -->
								</e:family-name>
								<e:given-name>
									<xsl:value-of select="substring(.,string-length(substring-before(., ','))+3)"/>
									<!-- xsl:value-of select="string-helper:getSplitPart(.,',',1)"/ -->
								</e:given-name>
								<e:complete-name>
									<xsl:value-of select="concat($prefix, .)"/>
								</e:complete-name>
							</e:person>
						</publication:creator>
					</xsl:for-each>
					<xsl:if test="$extraData='true'">
						<!-- Two extra creator-segments to test org-unit path list -->
        				<publication:creator role="artist">
          					<e:person>
            					<e:complete-name>Hans Meier</e:complete-name>
            					<e:family-name>Meier</e:family-name>
            					<e:given-name>Hans</e:given-name>
            					<e:organization>
              						<e:organization-name>Test Organization</e:organization-name>
              						<e:address>Max-Planck-Str. 1</e:address>
              						<e:identifier>escidoc:persistent26</e:identifier>
           	 					</e:organization>
          					</e:person>
        				</publication:creator>
        				<publication:creator role="author">
            				<e:organization>
              					<e:organization-name>Standalone Org</e:organization-name>
              					<e:address>Hermann-von-Helmholtz Platz 1</e:address>
              					<e:identifier>escidoc:persistent37</e:identifier>
           	 				</e:organization>
        				</publication:creator>
        			</xsl:if>

					<dc:title>
						<xsl:attribute name="xml:lang">
							<xsl:value-of select="//B541[1]"/>
						</xsl:attribute>
						<xsl:value-of select="concat($prefix, //B542[1], ' ', $titleAppendix)"/>
					</dc:title>
					<dc:language>
						<xsl:value-of select="//B541[1]"/>
					</dc:language>
					<dc:identifier xsi:type="eidt:ISSN">
						<xsl:value-of select="concat($prefix, $ACCESSIONNUMBER)"/>
					</dc:identifier>
					<xsl:if test="string(//B542[2]) and normalize-space(//B542[2])!=''">
						<dcterms:alternative>
							<xsl:attribute name="xml:lang">
								<xsl:value-of select="//B541[2]"/>
							</xsl:attribute>
							<xsl:value-of select="concat($specialLetter, $prefix, //B542[2], ' ', $alternativeAppendix)"/>
						</dcterms:alternative>
					</xsl:if>
					<xsl:if test="string($createdate) and normalize-space($createdate)!=''">
						<dcterms:created>
							<xsl:value-of select="concat($createdate, 'T07:00:00.000+01:00')"/>
						</dcterms:created>
					</xsl:if>
					<xsl:if test="string($modifydate) and normalize-space($modifydate)!=''">
						<dcterms:modified>
							<xsl:value-of select="concat($modifydate, 'T08:00:00.000+01:00')"/>
						</dcterms:modified>
					</xsl:if>
					<xsl:if test="string($submitdate) and normalize-space($submitdate)!=''">
						<dcterms:dateSubmitted>
							<xsl:value-of select="concat($submitdate, 'T09:00:00.000+01:00')"/>
						</dcterms:dateSubmitted>
					</xsl:if>
					<xsl:if test="string($acceptdate) and normalize-space($acceptdate)!=''">
						<dcterms:dateAccepted>
							<xsl:value-of select="concat($acceptdate, 'T10:00:00.000+01:00')"/>
						</dcterms:dateAccepted>
					</xsl:if>
					<xsl:if test="string($publishdate) and normalize-space($publishdate)!=''">
						<publication:published-online>
							<xsl:value-of select="concat($publishdate, 'T11:00:00.000+01:00')"/>
						</publication:published-online>
					</xsl:if>
					<xsl:if test="string($issueddate) and normalize-space($issueddate)!=''">
						<dcterms:issued>
							<xsl:value-of select="concat($issueddate, 'T12:00:00.000+01:00')"/>
						</dcterms:issued>
					</xsl:if>
					<xsl:if test="$docnum=0">
        				<publication:source>
          					<dc:title>Test of UTF8: Ümlaut € test@etc @etc. &#1088;&#1091;&#1089;&#1089;&#1082;&#1080;&#1077;</dc:title>
        				</publication:source>
        				<publication:source>
          					<dc:title>Regextest $3</dc:title>
        				</publication:source>
                        <publication:source>
                            <dc:title>
                                Faces (album)
                                From Wikipedia, the free encyclopedia
                                Faces is a double-LP by R&amp;B artists Earth, Wind &amp; Fire,which was released in 1980
                            </dc:title>
                        </publication:source>
                        <publication:source>
                            <dc:title><![CDATA[Faces (album)
                                From Wikipedia, the free encyclopedia
                                Faces is a double-LP by A&C artists Dark, Air & Hot,which was released in 1980]]>
                            </dc:title>
                        </publication:source>
					</xsl:if>
        			<publication:source type="articlesource">
          				<dc:title>
							<xsl:value-of select="$chineseString"/>
						</dc:title>
        			</publication:source>
        			<publication:source type="articlesource1">
          				<dc:title>
          					<xsl:value-of select="concat($prefix, 'TitelSourceVO')"/>
          				</dc:title>
        			</publication:source>
        			<publication:event>
          				<dc:title>
          					<xsl:value-of select="concat($prefix, 'TitelEventVO')"/>
						</dc:title>
        			</publication:event>
				</escidocMetadataProfile:publication>
			</escidocMetadataRecords:md-record>
		</escidocMetadataRecords:md-records>
	</xsl:template>

</xsl:stylesheet>	
