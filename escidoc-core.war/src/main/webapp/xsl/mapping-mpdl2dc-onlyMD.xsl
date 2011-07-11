<?xml version="1.0" encoding="UTF-8"?>
<!--
  $Id$
  XSLT transformation from eSciDoc XSLT Schema to Dublin Core
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:md-profile-escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:types="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	exclude-result-prefixes="xsl md-profile-escidoc types dcterms">

	<xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:param name="ID"/>

	<xsl:template match="*">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="/">
		<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
				xmlns:dc="http://purl.org/dc/elements/1.1/"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				 xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
			<xsl:apply-templates select="*/dc:title"/>
			<xsl:call-template name="creators"/>
			<xsl:apply-templates select="*/dc:subject"/>
			<xsl:apply-templates select="*/dc:description"/>
			<xsl:apply-templates select="*/dcterms:abstract"/>
			<xsl:apply-templates select="*/*[local-name() = 'publishing-info']/dc:publisher"/>
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

			<xsl:apply-templates select="*/dc:identifier"/>

			<xsl:call-template name="source"/>
			<xsl:apply-templates select="*/dc:language"/>
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

    <xsl:template match="dcterms:abstract">
		<!--
			don't do this, doubles description <dc:description>
			<xsl:call-template name="dcSimpleElementContent"> <xsl:with-param
			name="dcElement" select="."/> </xsl:call-template> </dc:description>
		-->
	</xsl:template>
	

	<!-- DC ELEMENTS -->

	<xsl:template match="dc:title">
		<dc:title>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:title>
	</xsl:template>

	<xsl:template match="dc:description">
		<dc:description>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:description>
	</xsl:template>

	<xsl:template match="dc:creator"> </xsl:template>

	<xsl:template match="dc:subject">
		<dc:subject>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:subject>
	</xsl:template>

	<xsl:template match="dc:publisher">
		<dc:publisher>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="." />
			</xsl:call-template>
		</dc:publisher>
	</xsl:template>

	<xsl:template match="dc:contributor"></xsl:template>

	<xsl:template match="dc:date"></xsl:template>

	<xsl:template match="dc:type"></xsl:template>

	<xsl:template match="dc:format"></xsl:template>

	<xsl:template match="dc:identifier">
		<dc:identifier>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:identifier>
	</xsl:template>

	<xsl:template match="dc:source"></xsl:template>

	<xsl:template match="dc:language">
		<dc:language>
			<xsl:call-template name="dcSimpleElementContent">
				<xsl:with-param name="dcElement" select="."/>
			</xsl:call-template>
		</dc:language>
	</xsl:template>

	<xsl:template match="dc:relation"></xsl:template>

	<xsl:template match="dc:coverage"></xsl:template>

	<xsl:template match="dc:rights"></xsl:template>

	<xsl:template name="dcSimpleElementContent">
		<xsl:param name="dcElement"/>
		<xsl:copy-of select="@xml:lang"/>
		<xsl:value-of select="."/>
	</xsl:template>

</xsl:stylesheet>
