<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:component-accessor="xalan://de.escidoc.core.sb.xslt.ComponentAccessor" 
		extension-element-prefixes="component-accessor"
 		xmlns:item="http://www.escidoc.de/schemas/item/0.2"			
 		xmlns:metadata="http://www.escidoc.de/metadata/schema/0.1" 
		xmlns:metadata-records="http://www.escidoc.de/schemas/metadatarecords/0.1"
		xmlns:components="http://www.escidoc.de/schemas/components/0.1" 
		xmlns:technical-metadata="http://www.escidoc.de/schemas/technicalmetadata/0.1"
		xmlns:container="http://www.escidoc.de/schemas/container/0.1">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<xsl:call-template name="process"/>
	</xsl:template>

	<xsl:template name="process">
		<escidocItem:item
			xmlns:escidocItem="http://www.escidoc.de/schemas/item/0.2"
			objid="" xmlns:xlink="http://www.w3.org/1999/xlink"
			xml:base="http://localhost:8080">
			<xsl:attribute name="xlink:title">
				<xsl:value-of select="/DOCGRP/DOC/BB/TIGRP/TI"/>
			</xsl:attribute>
			
			<escidocItem:properties xlink:type="simple" xlink:title="Properties"
				xlink:href="/ir/item/escidoc:198/properties">
				<escidocItem:creation-date>2006-12-21T13:15:23.454</escidocItem:creation-date>
				<escidocItem:context xlink:type="simple"
					xlink:title="context of escidoc:198"
					xlink:href="/ir/context/escidoc:378" />
				<escidocItem:content-type xlink:type="simple"
					xlink:title="content-type of escidoc:198"
					xlink:href="/ir/content-type/escidoc:65487" />
				<escidocItem:status>pending</escidocItem:status>
				<escidocItem:creator xlink:type="simple"
					xlink:title="creator of escidoc:198"
					xlink:href="/ir/account/de.escidoc.core.3" />
				<escidocItem:lock-status>unlocked</escidocItem:lock-status>
				<escidocItem:current-version xlink:type="simple"
					xlink:title="current version">
					<escidocItem:number>1</escidocItem:number>
					<escidocItem:date>2006-12-21T13:15:23.485</escidocItem:date>
					<escidocItem:version-status>pending</escidocItem:version-status>
					<escidocItem:valid-status>valid</escidocItem:valid-status>
				</escidocItem:current-version>
				<escidocItem:latest-version xlink:type="simple"
					xlink:title="latest version">
					<escidocItem:number>1</escidocItem:number>
					<escidocItem:date>2006-12-21T13:15:23.485</escidocItem:date>
				</escidocItem:latest-version>
			</escidocItem:properties>
			<escidocResources:resources
				xmlns:escidocResources="http://www.escidoc.de/schemas/resources/0.1"
				xmlns:xlink="http://www.w3.org/1999/xlink"
				xlink:href="/ir/item/escidoc:198/resources"
				xlink:title="Virtual Resources"></escidocResources:resources>
			<escidocMetadataRecords:md-records
				xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.1"
				xmlns:xlink="http://www.w3.org/1999/xlink" xlink:type="simple"
				xlink:href="/ir/item/escidoc:198/md-records"
				xlink:title="Available Metadata">
				<escidocMetadataRecords:md-record
					xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.1"
					xmlns:xlink="http://www.w3.org/1999/xlink" name="escidoc"
					schema="http://www.escidoc-project.de/metadata/schema/0.1/"
					xlink:href="/ir/item/escidoc:198/md-records/md-record/escidoc"
					xlink:title="eSciDoc internal MD Record" xlink:type="simple">
					<escidocMetadata:escidoc
					xmlns:escidocMetadata="http://www.escidoc.de/metadata/schema/0.1">
						<escidocMetadata:genre>Article</escidocMetadata:genre>
						<escidocMetadata:identifier>
							<escidocMetadata:id>
								<xsl:value-of select="/DOCGRP/DOC/BB/SO/DOI"/>
							</escidocMetadata:id>
							<escidocMetadata:id-type>
								DOI
							</escidocMetadata:id-type>
						</escidocMetadata:identifier>
						<escidocMetadata:original-language>
							<xsl:value-of select="/DOCGRP/DOC/@LG"/>
						</escidocMetadata:original-language>

						<xsl:for-each select="/DOCGRP/DOC/BB/BYL/AU">
							<escidocMetadata:creator>
								<escidocMetadata:person>
									<escidocMetadata:identifier>
									<escidocMetadata:id>115885390</escidocMetadata:id>
										<escidocMetadata:id-type>PND</escidocMetadata:id-type>
									</escidocMetadata:identifier>
									<escidocMetadata:complete-name>
										<xsl:value-of select="concat(./PN,' ',./SN)"/>
									</escidocMetadata:complete-name>
									<escidocMetadata:given-name>
										<xsl:value-of select="./PN"/>
									</escidocMetadata:given-name>
									<escidocMetadata:family-name>
										<xsl:value-of select="./SN"/>
									</escidocMetadata:family-name>
								</escidocMetadata:person>
								<escidocMetadata:role>Author</escidocMetadata:role>
							</escidocMetadata:creator>
						</xsl:for-each>

						<escidocMetadata:title>
							<xsl:value-of select="/DOCGRP/DOC/BB/TIGRP/TI"/>
						</escidocMetadata:title>
					</escidocMetadata:escidoc>
				</escidocMetadataRecords:md-record>
			</escidocMetadataRecords:md-records>
			<escidocComponents:components
				xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.1"
				xlink:type="simple" xlink:href="/ir/item/escidoc:198/components"
				xlink:title="Components of Item">
				<escidocComponents:component objid=""
					xlink:type="simple"
					xlink:href="">
					<xsl:attribute name="xlink:title">
						<xsl:value-of select="concat(/DOCGRP/DOC/BB/TIGRP/TI,' as pdf')"/>
					</xsl:attribute>
					<escidocComponents:properties
						xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.1"
						xmlns:xlink="http://www.w3.org/1999/xlink" xlink:title="Properties"
						xlink:type="simple">
						<escidocComponents:description>PDF File</escidocComponents:description>
						<escidocComponents:creation-date>2006-06-21T12:00:00.000+01:00</escidocComponents:creation-date>
						<escidocComponents:status>valid</escidocComponents:status>
						<escidocComponents:visibility>public</escidocComponents:visibility>
						<escidocComponents:creator
							xlink:href="/ir/account/creator" xlink:type="simple"/>
						<escidocComponents:content-category>preprint</escidocComponents:content-category>
						<escidocComponents:file-name>publication1.pdf</escidocComponents:file-name>
						<escidocComponents:mime-type>application/pdf</escidocComponents:mime-type>
						<escidocComponents:file-size>234</escidocComponents:file-size>
					</escidocComponents:properties>
					<escidocComponents:content xlink:type="simple"
						xlink:title="PDF">
						<xsl:attribute name="xlink:href">
							<xsl:value-of select="concat('http://localhost:8082/ir/',/DOCGRP/pdfpath)"/>
						</xsl:attribute>
					</escidocComponents:content>	
				</escidocComponents:component>
			</escidocComponents:components>
		</escidocItem:item>
	</xsl:template>

</xsl:stylesheet>	
