<?xml version="1.0" encoding="UTF-8"?>
<!-- 
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License").  You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END

 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 fuer wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Foerderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
 -->
<!-- 
 XSLT to transform XML Schema defining eSciDoc resources into 
 documentation of elements and attributes of the resource in form 
 of docbook XML.
 XML Schema documents of imported namespaces are loaded from the 
 given ''schemaLocation''. Because prefixes are used to find 
 referenced elements/attributes the use of one prefix for different
 namespaces inside the schema graph will probably cause errors.

 author Frank Schwichtenberg
 -->
<xsl:stylesheet 
		version="2.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		>
	<xsl:output method="xml"/>
    <xsl:param name="schemaLocationBase"/>
    <xsl:param name="schemaLocationBaseReplacement"/>
    
	<xsl:template match="/">
		<xsl:variable name="bindings">
			<!-- test namespaces and include -->
			<bindings>
				<xsl:for-each select="xs:schema/namespace::*">
					<xsl:call-template name="createBindings"/>
				</xsl:for-each>
			</bindings>
		</xsl:variable>
		<!-- xsl:copy-of select="$bindings//binding[prefix = 'md-records']//xs:element[@name = 'md-record']"/-->
		<!-- xsl:copy-of select="$bindings"/ -->
		<chapter>
			<!-- start with first globaly defined element, the rest recursive -->
			<title>Elements and Attributes of <xsl:call-template name="resourceDisplayName">
				<xsl:with-param name="resourceElementName"><xsl:value-of select="xs:schema/xs:element[1]/@name"/></xsl:with-param>
			</xsl:call-template>
			</title>
			<xsl:copy-of select="xs:schema/xs:element[1]/xs:annotation/xs:documentation/para" copy-namespaces="no"/>
			<xsl:apply-templates select="xs:schema/xs:element[1]">
				<xsl:with-param name="bindings" select="$bindings"/>
			</xsl:apply-templates>
		</chapter>
	</xsl:template>
	
	<xsl:template name="resourceDisplayName">
		<xsl:param name="resourceElementName"/>
		<xsl:call-template name="toTitleCase">
			<xsl:with-param name="string"><xsl:value-of select="translate($resourceElementName, '-', ' ')"/></xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="toTitleCase">
		<xsl:param name="string"/>
		<xsl:variable name="word" select="substring-before($string, ' ')"/>
		<xsl:variable name="rest" select="substring(substring-after($string, $word), 2)"/>
		
		<xsl:choose>
			<xsl:when test="string-length($word) > 0">
				<xsl:call-template name="toUpperCase">
					<xsl:with-param name="string"><xsl:value-of select="substring($word, 1, 1)"/></xsl:with-param>
				</xsl:call-template>
				<xsl:value-of select="substring($word, 2)"/>
				<xsl:if test="string-length($rest) > 0">
					<!-- should ever be the case, if $word is not empty than there is a space in $string -->
					<xsl:text> </xsl:text>
					<xsl:call-template name="toTitleCase">
						<xsl:with-param name="string" select="$rest"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!-- there is no space in $string -->
				<xsl:call-template name="toUpperCase">
					<xsl:with-param name="string"><xsl:value-of select="substring($string, 1, 1)"/></xsl:with-param>
				</xsl:call-template>
				<xsl:value-of select="substring($string, 2)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="toUpperCase">
		<xsl:param name="string"/>
		<xsl:value-of select="translate($string, 'abcdefghijklmnopqrstuvwxyzäöü', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
	</xsl:template>
	
	<xsl:template name="createBindings">
		<xsl:variable name="prefix">
			<xsl:value-of select="local-name()"/>
		</xsl:variable>
		<xsl:variable name="NS">
			<xsl:value-of select="."/>
		</xsl:variable>
		<xsl:variable name="theSchemaLocation">
			<xsl:value-of select="replace(//xs:import[@namespace = $NS]/@schemaLocation,$schemaLocationBase,$schemaLocationBaseReplacement)"/>
		</xsl:variable>
		<xsl:if test="$prefix != '' and $theSchemaLocation != ''">
			<xsl:message>Including <xsl:value-of select="$theSchemaLocation"/>
			</xsl:message>
			<xsl:variable name="theSchemaDocument" select="document($theSchemaLocation)"/>
			<binding>
				<prefix>
					<xsl:value-of select="$prefix"/>
				</prefix>
				<namespace>
					<xsl:value-of select="$NS"/>
				</namespace>
				<location>
					<xsl:value-of select="$theSchemaLocation"/>
				</location>
				<content>
					<xsl:copy-of select="$theSchemaDocument"/>
				</content>
			</binding>
			<xsl:for-each select="$theSchemaDocument/xs:schema/namespace::*">
				<xsl:call-template name="createBindings"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="xs:element[@name]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<table frame="all" border="1">
					<xsl:call-template name="elementTableTitle">
						<xsl:with-param name="name" select="@name"/>
						<xsl:with-param name="path" select="$path"/>
					</xsl:call-template>
					<tgroup cols="12" align="left" colsep="1" rowsep="1">
						<xsl:call-template name="elementTableHead">
							<xsl:with-param name="name" select="@name"/>
							<xsl:with-param name="path" select="$path"/>
						</xsl:call-template>
						<tbody>
							<xsl:call-template name="elementTableElementDoc">
								<xsl:with-param name="element" select="."/>
							</xsl:call-template>
							<xsl:apply-templates select="xs:annotation/xs:documentation/comment"/>
							<xsl:apply-templates select="xs:complexType/xs:attribute">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="@name"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
							<xsl:apply-templates select="xs:complexType/xs:attributeGroup">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="@name"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
						</tbody>
					</tgroup>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Element declaration without documentation. [<xsl:value-of select="@name"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="xs:complexType/xs:sequence/xs:element">
			<xsl:with-param name="path">
				<xsl:value-of select="$path"/>
				<xsl:value-of select="@name"/>/</xsl:with-param>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xs:element[@ref]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<table frame="all" border="1">
					<xsl:call-template name="elementTableTitle">
						<xsl:with-param name="name" select="$refLocalName"/>
						<xsl:with-param name="path" select="$path"/>
					</xsl:call-template>
					<tgroup cols="12" align="left" colsep="1" rowsep="1">
						<xsl:call-template name="elementTableHead">
							<xsl:with-param name="name" select="$refLocalName"/>
							<xsl:with-param name="path" select="$path"/>
						</xsl:call-template>
						<tbody>
							<xsl:call-template name="elementTableElementDoc">
								<xsl:with-param name="element" select="."/>
							</xsl:call-template>
							<xsl:apply-templates select="xs:annotation/xs:documentation/comment"/>
							<!-- NO attribute declaration in element refs -->
							<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]/xs:complexType/xs:attribute">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="$refLocalName"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
							<!-- NO attribute declaration in element refs -->
							<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]/xs:complexType/xs:attributeGroup">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="$refLocalName"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
							<!-- attributes defined in global complex type of element -->
							<xsl:variable name="type">
								<xsl:value-of select="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]/@type"/>
							</xsl:variable>
							<xsl:variable name="typePrefix">
								<xsl:value-of select="substring-before($type, ':')"/>
							</xsl:variable>
							<xsl:variable name="typeLocalName">
								<xsl:value-of select="substring-after($type, ':')"/>
							</xsl:variable>
							<xsl:apply-templates select="$bindings//binding[prefix = $typePrefix][1]//xs:complexType[@name = $typeLocalName]/xs:attribute">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="$refLocalName"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
							<xsl:apply-templates select="$bindings//binding[prefix = $typePrefix][1]//xs:complexType[@name = $typeLocalName]/xs:attributeGroup">
								<xsl:with-param name="path">
									<xsl:value-of select="$path"/>
									<xsl:value-of select="$refLocalName"/>/</xsl:with-param>
								<xsl:with-param name="bindings" select="$bindings"/>
							</xsl:apply-templates>
						</tbody>
					</tgroup>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Element reference without documentation. [<xsl:value-of select="$refPrefix"/>:<xsl:value-of select="$refLocalName"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]">
				<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:element[@name = $refLocalName]">
					<xsl:with-param name="path" select="$path"/>
					<xsl:with-param name="bindings" select="$bindings"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="//xs:element[@name = $refLocalName]">
					<xsl:with-param name="path" select="$path"/>
					<xsl:with-param name="bindings" select="$bindings"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="xs:attributeGroup">
		<xsl:param name="path"/>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:attributeGroup[@name = $refLocalName]/xs:attribute">
			<xsl:with-param name="path" select="$path"/>
			<xsl:with-param name="bindings" select="$bindings"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xs:attribute[@name]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:param name="prefix"/>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<xsl:call-template name="elementTableAttributeRow">
					<xsl:with-param name="attribute" select="."/>
					<xsl:with-param name="prefix" select="$prefix"/>
				</xsl:call-template>
				<xsl:apply-templates select="xs:annotation/xs:documentation/comment"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Attribute declaration without documentation. [<xsl:value-of select="@name"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="xs:attribute[@ref]">
		<xsl:param name="path">/</xsl:param>
		<xsl:param name="bindings"/>
		<xsl:variable name="refPrefix">
			<xsl:value-of select="substring-before(@ref, ':')"/>
		</xsl:variable>
		<xsl:variable name="refLocalName">
			<xsl:value-of select="substring-after(@ref, ':')"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="xs:annotation/xs:documentation/*">
				<xsl:call-template name="elementTableAttributeRow">
					<xsl:with-param name="attribute" select="."/>
				</xsl:call-template>
				<xsl:apply-templates select="xs:annotation/xs:documentation/comment"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:comment>Attribute reference without documentation. [<xsl:value-of select="$refPrefix"/>:<xsl:value-of select="$refLocalName"/>][<xsl:value-of select="$path"/>]</xsl:comment>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="$bindings//binding[prefix = $refPrefix][1]//xs:attribute[@name = $refLocalName]">
			<xsl:with-param name="path" select="$path"/>
			<xsl:with-param name="bindings" select="$bindings"/>
			<xsl:with-param name="prefix" select="$refPrefix"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xs:documentation">
		<xsl:if test=".//create">
			<span class="create">
				<xsl:value-of select=".//create"/> on create</span>
		</xsl:if>
		<xsl:if test=".//create and .//update">, </xsl:if>
		<xsl:if test=".//create and not(.//update)">. </xsl:if>
		<xsl:if test=".//update">
			<span class="update">
				<xsl:value-of select=".//update"/> on update. </span>
		</xsl:if>
		<xsl:if test=".//comment">
			<span class="comment">Note: <xsl:value-of select=".//comment"/>
			</span>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*">
		<!-- 
		<found>
			<xsl:attribute name="element"><xsl:value-of select="local-name()"/></xsl:attribute>
			<xsl:for-each select="@*">
				<xsl:copy/>
			</xsl:for-each>
			<xsl:value-of select="."/>
		</found>
		<xsl:apply-templates/>
		-->
	</xsl:template>
	
	<xsl:template name="elementTableTitle">
		<xsl:param name="name"/>
		<xsl:param name="path"/>
		<title>
			<xsl:text>Element &lt;</xsl:text>
			<xsl:value-of select="$name"/>
			<xsl:text>&gt;</xsl:text>
		</title>
	</xsl:template>
	
	<xsl:template name="elementTableHead">
		<xsl:param name="name"/>
		<xsl:param name="path"/>
		<colspec colname="c1"/>
		<colspec colname="c2"/>
		<colspec colname="c3"/>
		<colspec colname="c4"/>
		<colspec colname="c5"/>
		<colspec colname="c6"/>
		<colspec colname="c7"/>
		<colspec colname="c8"/>
		<colspec colname="c9"/>
		<colspec colname="c10"/>
		<colspec colname="c11"/>
		<colspec colname="c12"/>
		<spanspec spanname="all" namest="c1" nameend="c12" align="left"/>
		<spanspec spanname="element" namest="c1" nameend="c2" align="left"/>
		<spanspec spanname="attribute" namest="c3" nameend="c4" align="left"/>
		<spanspec spanname="attribute_comment" namest="c3" nameend="c12" align="left"/>
		<spanspec spanname="create" namest="c5" nameend="c8" align="center"/>
		<spanspec spanname="update" namest="c9" nameend="c12" align="center"/>
		<thead>
			<row>
				<entry spanname="all">
					<xsl:value-of select="$path"/>
					<xsl:value-of select="$name"/>
				</entry>
			</row>
			<row>
				<entry spanname="all">
					<xsl:text/>
				</entry>
			</row>
			<row>
				<entry spanname="element">
									Element
								</entry>
				<entry spanname="attribute">
									Attribute
								</entry>
				<entry spanname="create">create</entry>
				<entry spanname="update">update</entry>
			</row>
		</thead>
	</xsl:template>
	<xsl:template name="elementTableElementDoc">
		<xsl:param name="element"/>
		<row>
			<entry spanname="element">
				<xsl:value-of select="$element/@name"/>
				<xsl:value-of select="$element/@ref"/>
			</entry>
			<entry spanname="attribute"/>
			<entry spanname="create">
				<xsl:value-of select="$element/xs:annotation/xs:documentation/create"/>
			</entry>
			<entry spanname="update">
				<xsl:value-of select="$element/xs:annotation/xs:documentation/update"/>
			</entry>
		</row>
	</xsl:template>
	
	<xsl:template name="elementTableAttributeRow">
		<xsl:param name="attribute"/>
		<xsl:param name="prefix"/>
		<row>
			<entry spanname="element"/>
			<entry spanname="attribute">
				<xsl:if test="$prefix">
					<xsl:value-of select="$prefix"/>:</xsl:if>
				<xsl:value-of select="$attribute/@name"/>
				<xsl:value-of select="$attribute/@ref"/>
			</entry>
			<entry spanname="create">
				<xsl:value-of select="$attribute/xs:annotation/xs:documentation/create"/>
			</entry>
			<entry spanname="update">
				<xsl:value-of select="$attribute/xs:annotation/xs:documentation/update"/>
			</entry>
		</row>
	</xsl:template>
	
	<xsl:template match="comment">
		<row>
		<xsl:choose>
		
			<xsl:when test="../../parent::*[local-name() = 'attribute']">
				<entry spanname="element"/>
				<entry spanname="attribute_comment">
				<para>
					<xsl:value-of select="."/>
				</para>
				</entry>
			</xsl:when>
			<xsl:otherwise>
			<entry spanname="all">
				<para>
					<xsl:value-of select="."/>
				</para>
			</entry>
			</xsl:otherwise>
		</xsl:choose>
		</row>
	</xsl:template>
</xsl:stylesheet>
