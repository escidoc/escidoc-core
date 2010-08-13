<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xlink="http://www.w3.org/1999/xlink"
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
			xmlns:prop="http://escidoc.de/core/01/properties/">
			
			<escidocItem:properties>
				<!-- ONLY FOR TEST OF XMLDB>
				<srel:created-by>
					<xsl:attribute name="objid">
						<xsl:value-of select="$user"/>
					</xsl:attribute>
				</srel:created-by>		
				<prop:public-status>
					<xsl:value-of select="$status"/>
				</prop:public-status>	
				<END ONLY FOR TEST OF XMLDB -->	
				<srel:context
					objid="escidoc:persistent3" />
				<srel:content-model
					objid="escidoc:persistent4" />
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
				xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9">
				<escidocComponents:component>
					<escidocComponents:properties
						xmlns:version="http://escidoc.de/core/01/properties/version/"
						xmlns:properties="http://escidoc.de/core/01/properties/">
						<properties:description>XML File</properties:description>
						<properties:valid-status>valid</properties:valid-status>
						<properties:visibility>public</properties:visibility>
						<properties:content-category>pre-print</properties:content-category>
						<properties:file-name>
							<xsl:value-of select="concat('EPFull_',$ACCESSIONNUMBER,'.xml')"/>
						</properties:file-name>
						<properties:mime-type>text/xml</properties:mime-type>
					</escidocComponents:properties>
					<escidocComponents:content>
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
						xmlns:version="http://escidoc.de/core/01/properties/version/"
						xmlns:properties="http://escidoc.de/core/01/properties/">
						<properties:description>PDF File</properties:description>
						<properties:valid-status>valid</properties:valid-status>
						<properties:visibility>private</properties:visibility>
						<properties:content-category>pre-print</properties:content-category>
						<properties:file-name>
							<xsl:value-of select="concat('EPFull_',$ACCESSIONNUMBER,'.pdf')"/>
						</properties:file-name>
						<properties:mime-type>application/pdf</properties:mime-type>
					</escidocComponents:properties>
					<escidocComponents:content>
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
				name="escidoc"
				schema="http://www.escidoc-project.de/metadata/schema/0.1/">
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
            <escidocMetadataRecords:md-record
                xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
                name="technical"
                schema="http://www.escidoc-project.de/metadata/schema/0.1/">
                <escidocMetadataProfile:publication type="article" 
                    xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/" 
                    xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" 
                    xmlns:dc="http://purl.org/dc/elements/1.1/" 
                    xmlns:dcterms="http://purl.org/dc/terms/" 
                    xmlns:ec="http://escidoc.mpg.de/metadataprofile/schema/0.1/collection" 
                    xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                    xsi:schemaLocation="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication ../escidoc_publication_profile.xsd">
                    <publication:creator role="author">
                        <e:person>
                            <e:family-name>nichtindexiert</e:family-name>
                            <e:given-name>nichtindexiert</e:given-name>
                            <e:complete-name>nichtindexiert</e:complete-name>
                        </e:person>
                    </publication:creator>

                    <dc:title xml:lang="DE">nichtindexiert</dc:title>
                    <dc:language>DE</dc:language>
                    <dc:identifier xsi:type="eidt:ISSN">nichtindexiert</dc:identifier>
                    <publication:source>
                        <dc:title>nichtindexiert</dc:title>
                    </publication:source>
                    <publication:event>
                        <dc:title>nichtindexiert</dc:title>
                    </publication:event>
                </escidocMetadataProfile:publication>
            </escidocMetadataRecords:md-record>
            <escidocMetadataRecords:md-record
                xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
                name="additional"
                schema="http://www.escidoc-project.de/metadata/schema/0.1/">
                <escidocMetadataProfile:publication type="article" 
                    xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/" 
                    xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" 
                    xmlns:dc="http://purl.org/dc/elements/1.1/" 
                    xmlns:dcterms="http://purl.org/dc/terms/" 
                    xmlns:ec="http://escidoc.mpg.de/metadataprofile/schema/0.1/collection" 
                    xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                    xsi:schemaLocation="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication ../escidoc_publication_profile.xsd">
                    <publication:creator role="author">
                        <e:person>
                            <e:family-name>nichtindexiert</e:family-name>
                            <e:given-name>nichtindexiert</e:given-name>
                            <e:complete-name>nichtindexiert</e:complete-name>
                        </e:person>
                    </publication:creator>

                    <dc:title xml:lang="DE">nichtindexiert</dc:title>
                    <dc:language>DE</dc:language>
                    <dc:identifier xsi:type="eidt:ISSN">nichtindexiert</dc:identifier>
                    <publication:source>
                        <dc:title>nichtindexiert</dc:title>
                    </publication:source>
                    <publication:event>
                        <dc:title>nichtindexiert</dc:title>
                    </publication:event>
                </escidocMetadataProfile:publication>
            </escidocMetadataRecords:md-record>
            <escidocMetadataRecords:md-record name="technical-md">
                <jhove date="2008-02-21" 
                    name="Jhove" release="1.1" 
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                    xmlns="http://jhove.com/metadata/schema/0.5">
                    <properties>
                        <nichtindexiert>nichtindexiert</nichtindexiert>
                    </properties>
                    <md-record>
                        <nichtindexiert>nichtindexiert</nichtindexiert>
                    </md-record>
                    <language>nichtindexiert</language>
                    <identifier>nichtindexiert</identifier>
                    <creator role="nichtindexiert"/>
                    <title>nichtindexiert</title>
                    <alternative>nichtindexiert</alternative>
                    <abstract>nichtindexiert</abstract>
                    <creator>nichtindexiert</creator>
                    <organization-name>nichtindexiert</organization-name>
                    <event><title>nichtindexiert</title></event>
                    <source><title>nichtindexiert</title></source>
                    <event><place>nichtindexiert</place></event>
                    <source><place>nichtindexiert</place></source>
                    <date>2008-09-19T16:11:17+02:00</date>
                    <repInfo uri="http://srv05.mpdl.mpg.de:8080/ir/item/escidoc:2280/components/component/escidoc:2283/content">
                        <reportingModule date="2007-02-13" release="1.2">JPEG-hul</reportingModule>
                        <lastModified>1970-01-01T01:00:00+01:00</lastModified>
                        <size>1120884</size>
                        <format>JPEG</format>
                        <version>1.02</version>
                        <status>Well-Formed and valid</status>
                        <sigMatch>
                            <module>JPEG-hul</module>
                        </sigMatch>
                        <messages>
                            <message offset="42" severity="info">Value offset not word-aligned: 195</message>
                        </messages>
                        <mimeType>image/jpeg</mimeType>
                        <profiles>
                            <profile>JFIF</profile>
                        </profiles>
                        <properties>
                            <property>
                                <name>JPEGMetadata</name>
                                <values arity="List" type="Property">
                                <property>
                                    <name>CompressionType</name>
                                    <values arity="Scalar" type="String">
                                        <value>Huffman coding, Baseline DCT</value>
                                    </values>
                                </property>
                                <property>
                                    <name>Images</name>
                                    <values arity="List" type="Property">
                                        <property>
                                            <name>Number</name>
                                            <values arity="Scalar" type="Integer">
                                                <value>1</value>
                                            </values>
                                        </property>
                                        <property>
                                            <name>Image</name>
                                            <values arity="List" type="Property">
                                                <property>
                                                    <name>NisoImageMetadata</name>
                                                    <values arity="Scalar" type="NISOImageMetadata">
                                                        <value>
                                                            <mix:mix xmlns:mix="http://www.loc.gov/mix/" xsi:schemaLocation="http://www.loc.gov/mix/ http://www.loc.gov/mix/mix.xsd">
                                                                <mix:BasicImageParameters>
                                                                    <mix:Format>
                                                                        <mix:MIMEType>image/jpeg</mix:MIMEType>
                                                                        <mix:ByteOrder>big-endian</mix:ByteOrder>
                                                                        <mix:Compression>
                                                                            <mix:CompressionScheme>6</mix:CompressionScheme>
                                                                        </mix:Compression>
                                                                        <mix:PhotometricInterpretation>
                                                                            <mix:ColorSpace>6</mix:ColorSpace>
                                                                        </mix:PhotometricInterpretation>
                                                                    </mix:Format>
                                                                </mix:BasicImageParameters>
                                                                <mix:ImageCreation>
                                                                <mix:ScanningSystemCapture>
                                                                        <mix:ScanningSystemHardware>
                                                                            <mix:ScannerManufacturer>SONY</mix:ScannerManufacturer>
                                                                        </mix:ScanningSystemHardware>
                                                                    </mix:ScanningSystemCapture>
                                                                </mix:ImageCreation>
                                                                <mix:ImagingPerformanceAssessment>
                                                                    <mix:SpatialMetrics>
                                                                        <mix:SamplingFrequencyUnit>2</mix:SamplingFrequencyUnit>
                                                                        <mix:XSamplingFrequency>300</mix:XSamplingFrequency>
                                                                        <mix:YSamplingFrequency>300</mix:YSamplingFrequency>
                                                                        <mix:ImageWidth>2835</mix:ImageWidth>
                                                                        <mix:ImageLength>3543</mix:ImageLength>
                                                                    </mix:SpatialMetrics>
                                                                    <mix:Energetics>
                                                                        <mix:BitsPerSample>8,8,8</mix:BitsPerSample>
                                                                        <mix:SamplesPerPixel>3</mix:SamplesPerPixel>
                                                                    </mix:Energetics>
                                                                </mix:ImagingPerformanceAssessment>
                                                            </mix:mix>
                                                        </value>
                                                    </values>
                                                </property>             
                                                <property>
                                                    <name>RestartInterval</name>
                                                    <values arity="Scalar" type="Integer">
                                                        <value>355</value>
                                                    </values>
                                                </property>
                                                <property>
                                                    <name>Scans</name>
                                                    <values arity="Scalar" type="Integer">
                                                        <value>1</value>
                                                    </values>
                                                </property>
                                                <property>
                                                    <name>QuantizationTables</name>
                                                    <values arity="List" type="Property">
                                                        <property>
                                                            <name>QuantizationTable</name>
                                                            <values arity="Array" type="Property">
                                                                <property>
                                                                    <name>Precision</name>
                                                                    <values arity="Scalar" type="String">
                                                                        <value>8-bit</value>
                                                                    </values>
                                                                </property>
                                                                <property>
                                                                    <name>DestinationIdentifier</name>
                                                                    <values arity="Scalar" type="Integer">
                                                                        <value>0</value>
                                                                    </values>
                                                                </property>
                                                            </values>
                                                        </property>
                                                    </values>
                                                </property>
                                                <property>
                                                    <name>XMP</name>
                                                        <values arity="Scalar" type="String">
                                                            <value>&lt;x:xmpmeta xmlns:x='adobe:ns:meta/' x:xmptk='XMP toolkit 3.0-28, framework 1.6'&gt;
                                                                    &lt;rdf:RDF xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#' xmlns:iX='http://ns.adobe.com/iX/1.0/'&gt;
                                                                    &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:exif='http://ns.adobe.com/exif/1.0/'&gt;
                                                                    &lt;exif:ExposureTime&gt;10/6400&lt;/exif:ExposureTime&gt;
                                                                    &lt;exif:FNumber&gt;71/10&lt;/exif:FNumber&gt;
                                                                    &lt;exif:ExposureProgram&gt;1&lt;/exif:ExposureProgram&gt;
                                                                    &lt;exif:ExifVersion&gt;0220&lt;/exif:ExifVersion&gt;
                                                                    &lt;exif:DateTimeOriginal&gt;2005-12-15T11:31:13+01:00&lt;/exif:DateTimeOriginal&gt;
                                                                    &lt;exif:DateTimeDigitized&gt;2005-12-15T11:31:13+01:00&lt;/exif:DateTimeDigitized&gt;
                                                                    &lt;exif:CompressedBitsPerPixel&gt;8/1&lt;/exif:CompressedBitsPerPixel&gt;
                                                                    &lt;exif:ExposureBiasValue&gt;0/10&lt;/exif:ExposureBiasValue&gt;
                                                                    &lt;exif:MaxApertureValue&gt;33/16&lt;/exif:MaxApertureValue&gt;
                                                                    &lt;exif:MeteringMode&gt;5&lt;/exif:MeteringMode&gt;
                                                                    &lt;exif:LightSource&gt;255&lt;/exif:LightSource&gt;
                                                                    &lt;exif:FocalLength&gt;323/10&lt;/exif:FocalLength&gt;
                                                                    &lt;exif:FlashpixVersion&gt;0100&lt;/exif:FlashpixVersion&gt;
                                                                    &lt;exif:ColorSpace&gt;1&lt;/exif:ColorSpace&gt;
                                                                    &lt;exif:PixelXDimension&gt;2835&lt;/exif:PixelXDimension&gt;
                                                                    &lt;exif:PixelYDimension&gt;3543&lt;/exif:PixelYDimension&gt;
                                                                    &lt;exif:FileSource&gt;3&lt;/exif:FileSource&gt;
                                                                    &lt;exif:SceneType&gt;1&lt;/exif:SceneType&gt;
                                                                    &lt;exif:CustomRendered&gt;0&lt;/exif:CustomRendered&gt;
                                                                    &lt;exif:ExposureMode&gt;1&lt;/exif:ExposureMode&gt;
                                                                    &lt;exif:WhiteBalance&gt;1&lt;/exif:WhiteBalance&gt;
                                                                    &lt;exif:SceneCaptureType&gt;0&lt;/exif:SceneCaptureType&gt;
                                                                    &lt;exif:Contrast&gt;0&lt;/exif:Contrast&gt;
                                                                    &lt;exif:Saturation&gt;0&lt;/exif:Saturation&gt;
                                                                    &lt;exif:Sharpness&gt;0&lt;/exif:Sharpness&gt;
                                                                    &lt;exif:ISOSpeedRatings&gt;
                                                                     &lt;rdf:Seq&gt;
                                                                      &lt;rdf:li&gt;64&lt;/rdf:li&gt;
                                                                     &lt;/rdf:Seq&gt;
                                                                    &lt;/exif:ISOSpeedRatings&gt;
                                                                   &lt;exif:Flash rdf:parseType='Resource'&gt;
                                                                     &lt;exif:Fired&gt;True&lt;/exif:Fired&gt;
                                                                     &lt;exif:Return&gt;0&lt;/exif:Return&gt;
                                                                     &lt;exif:Mode&gt;1&lt;/exif:Mode&gt;
                                                                     &lt;exif:Function&gt;False&lt;/exif:Function&gt;
                                                                     &lt;exif:RedEyeMode&gt;False&lt;/exif:RedEyeMode&gt;
                                                                    &lt;/exif:Flash&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:pdf='http://ns.adobe.com/pdf/1.3/'&gt;
                                                                   &lt;/rdf:Description&gt;
                                                                  
                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:photoshop='http://ns.adobe.com/photoshop/1.0/'&gt;
                                                                    &lt;photoshop:History&gt;&lt;/photoshop:History&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:tiff='http://ns.adobe.com/tiff/1.0/'&gt;
                                                                    &lt;tiff:Make&gt;SONY&lt;/tiff:Make&gt;
                                                                    &lt;tiff:Model&gt;DSC-F828&lt;/tiff:Model&gt;
                                                                    &lt;tiff:Orientation&gt;1&lt;/tiff:Orientation&gt;
                                                                    &lt;tiff:XResolution&gt;300/1&lt;/tiff:XResolution&gt;
                                                                    &lt;tiff:YResolution&gt;300/1&lt;/tiff:YResolution&gt;
                                                                    &lt;tiff:ResolutionUnit&gt;2&lt;/tiff:ResolutionUnit&gt;
                                                                    &lt;tiff:YCbCrPositioning&gt;2&lt;/tiff:YCbCrPositioning&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:xap='http://ns.adobe.com/xap/1.0/'&gt;
                                                                    &lt;xap:CreateDate&gt;2005-12-15T11:31:13+01:00&lt;/xap:CreateDate&gt;
                                                                    &lt;xap:ModifyDate&gt;2007-02-28T02:34:11+01:00&lt;/xap:ModifyDate&gt;
                                                                    &lt;xap:MetadataDate&gt;2007-02-28T02:34:11+01:00&lt;/xap:MetadataDate&gt;
                                                                   &lt;xap:CreatorTool&gt;Adobe Photoshop CS Windows&lt;/xap:CreatorTool&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:xapMM='http://ns.adobe.com/xap/1.0/mm/'&gt;
                                                                    &lt;xapMM:DocumentID&gt;adobe:docid:photoshop:42b3c5a8-c6ad-11db-9290-fdb8a3196ed0&lt;/xapMM:DocumentID&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                   &lt;rdf:Description rdf:about='uuid:bf7e24a4-c6cb-11db-9290-fdb8a3196ed0'
                                                                    xmlns:dc='http://purl.org/dc/elements/1.1/'&gt;
                                                                    &lt;dc:format&gt;image/jpeg&lt;/dc:format&gt;
                                                                    &lt;dc:description&gt;
                                                                     &lt;rdf:Alt&gt;
                                                                      &lt;rdf:li xml:lang='x-default'&gt;                               &lt;/rdf:li&gt;
                                                                     &lt;/rdf:Alt&gt;
                                                                    &lt;/dc:description&gt;
                                                                   &lt;/rdf:Description&gt;

                                                                  &lt;/rdf:RDF&gt;
                                                                  &lt;/x:xmpmeta&gt;
                                                            </value>
                                                        </values>
                                                    </property>
                                                </values>
                                            </property>
                                        </values>
                                    </property>
                                    <property>
                                        <name>ApplicationSegments</name>
                                        <values arity="List" type="String">
                                            <value>APP0</value>
                                            <value>APP1</value>
                                            <value>APP13</value>
                                            <value>APP1</value>
                                            <value>APP2</value>
                                            <value>APP14</value>
                                        </values>
                                    </property>
                                </values>
                            </property>
                        </properties>
                    </repInfo>
                </jhove>

            </escidocMetadataRecords:md-record>
		</escidocMetadataRecords:md-records>
	</xsl:template>

</xsl:stylesheet>	
