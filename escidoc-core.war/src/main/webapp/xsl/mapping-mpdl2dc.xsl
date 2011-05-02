<?xml version="1.0" encoding="UTF-8"?>
<!--
  $Id$
  XSLT transformation from eSciDoc XSLT Schema to Dublin Core
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:md-records="http://www.escidoc.de/schemas/metadatarecords/0.3" xmlns:md-profile-escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:types="http://escidoc.mpg.de/metadataprofile/schema/0.1/types" exclude-result-prefixes="xsl md-records md-profile-escidoc types">
	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>
	<xsl:param name="ID"></xsl:param>
	<xsl:template match="*">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="/*/*/md-records:md-record[@name='escidoc'] | /md-records:md-record[@name='escidoc']">
		<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:prefix-dc="http://purl.org/dc/elements/1.1/">
			<xsl:copy-of select="*/dc:title" copy-namespaces="no"/>
			<xsl:call-template name="creators"/>
			<xsl:copy-of select="*/dc:subject" copy-namespaces="no"/>
			<xsl:copy-of select="*/dc:description" copy-namespaces="no"/>
			<xsl:copy-of select="*/dcterms:abstract" copy-namespaces="no"/>
			<xsl:copy-of select="*/*[local-name() = 'publishing-info']/dc:publisher" copy-namespaces="no"/>
			<xsl:call-template name="contributors"/>
			<xsl:choose>
				<xsl:when test="string-length(*/dc:date) > 1">
					<dc:date>
						<xsl:value-of select="*/dc:date"/>
					</dc:date>
				</xsl:when>
				<xsl:when test="string-length(*/dcterms:created) > 1">
					<dc:date>
						<xsl:value-of select="*/dcterms:created"/>
					</dc:date>
				</xsl:when>
				<xsl:when test="string-length(*/dcterms:modified) > 1">
					<dc:date>
						<xsl:value-of select="*/dcterms:modified"/>
					</dc:date>
				</xsl:when>
				<xsl:when test="string-length(*/dcterms:dateSubmitted) > 1">
					<dc:date>
						<xsl:value-of select="*/dcterms:dateSubmitted"/>
					</dc:date>
				</xsl:when>
				<xsl:when test="string-length(*/dcterms:dateAccepted) > 1">
					<dc:date>
						<xsl:value-of select="*/dcterms:dateAccepted"/>
					</dc:date>
				</xsl:when>
				<xsl:when test="string-length(*/dcterms:issued) > 1">
					<dc:date>
						<xsl:value-of select="*/dcterms:issued"/>
					</dc:date>
				</xsl:when>
			</xsl:choose>
			<!--
			   type => will be defined later
			   format => will be defined later
			-->
			<xsl:copy-of select="*/dc:identifier" copy-namespaces="no"/>
			<xsl:if test="$ID"><dc:identifier><xsl:value-of select="$ID"/></dc:identifier></xsl:if>
			<xsl:call-template name="source"/>
			<xsl:copy-of select="*/dc:language" copy-namespaces="no"/>
			<!--
			   relation => will be defined later
			   coverage => will be defined later
			   rights => will be defined later
			-->
		</oai_dc:dc>
	</xsl:template>
	<xsl:template name="creators">
		<xsl:for-each select="*/*[local-name() = 'creator' and @role='author'][1]">
			<dc:creator>
				<xsl:choose>
					<xsl:when test="./types:person">
						<xsl:value-of select="./types:person/types:given-name"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="./types:person/types:family-name"/>
					</xsl:when>
					<xsl:when test="./types:organization">
						<xsl:value-of select="./types:organization/types:organization-name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</dc:creator>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="contributors">
		<xsl:for-each select="*/*[local-name() = 'creator' and @role!='author']">
			<dc:contributor>
				<xsl:choose>
					<xsl:when test="./types:person">
						<xsl:value-of select="./types:person/types:given-name"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="./types:person/types:family-name"/>
					</xsl:when>
					<xsl:when test="./types:organization">
						<xsl:value-of select="./types:organization/types:organization-name"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="."/>
					</xsl:otherwise>
				</xsl:choose>
			</dc:contributor>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="source">
		<xsl:for-each select="*/*[local-name() = 'source'][1]">
			<dc:source>
				<xsl:for-each select="./dc:title/@*">
					<xsl:copy/>
				</xsl:for-each>
				<xsl:value-of select="./dc:title"/>
			</dc:source>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
