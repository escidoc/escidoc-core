<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:escidocContentRelation="http://www.escidoc.de/schemas/content-relation/0.1" 
		xmlns:escidocContentRelations="http://www.escidoc.de/schemas/content-relations/0.1" 
		xmlns:zs="http://www.loc.gov/zing/srw/" 
		xmlns:prop="http://escidoc.de/core/01/properties/" 
		xmlns:srel="http://escidoc.de/core/01/structural-relations/" 
		xmlns:xlink="http://www.w3.org/1999/xlink" 
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		>

	<xsl:output method="xml"/>
	
	<xsl:param name="XSLT"/>

	<xsl:template match="zs:searchRetrieveResponse">
		<!-- 
		<xsl:processing-instruction name="xml-stylesheet">href="<xsl:value-of select="processing-instruction()"/>" type="text/css"</xsl:processing-instruction>
		 -->
		<xsl:if test="$XSLT">
			<xsl:processing-instruction name="xml-stylesheet">href="<xsl:value-of select="$XSLT"/>" type="application/xml"</xsl:processing-instruction>
		</xsl:if>
		<!-- one content-relations container for all relations pointing from or to the one this will be included -->
		<rdf:RDF>
			<xsl:attribute name="xml:base"><xsl:value-of select="zs:records/zs:record[1]/zs:recordData/*/@xml:base"/></xsl:attribute>
			<!-- group response records per predicate of the content relations -->
			<xsl:for-each select="zs:records/zs:record[not(zs:recordData/escidocContentRelation:content-relation/escidocContentRelation:subject/@xlink:href = preceding-sibling::*/zs:recordData/escidocContentRelation:content-relation/escidocContentRelation:subject/@xlink:href)]">
				<xsl:variable name="SUBJECT" select="zs:recordData/escidocContentRelation:content-relation/escidocContentRelation:subject/@xlink:href"/>
				<!-- container for all relations with same subject -->
				<rdf:Description>
					<xsl:attribute name="rdf:about"><xsl:value-of select="$SUBJECT"/></xsl:attribute>
					<!-- every content relation with same subject -->
					<xsl:apply-templates select="../zs:record/zs:recordData/escidocContentRelation:content-relation[escidocContentRelation:subject/@xlink:href = $SUBJECT]"/>
				</rdf:Description>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>

	<xsl:template match="zs:record">
		<xsl:apply-templates select="zs:recordData/escidocContentRelation:content-relation"/>
	</xsl:template>

	<xsl:template match="escidocContentRelation:content-relation">
		<!-- split predicate (aka type) into local name and namespace -->
		<xsl:variable name="PREDICATE_LOCAL_NAME">
			<xsl:call-template name="getLocalNameFromURI">
				<xsl:with-param name="URI">
					<xsl:value-of select="escidocContentRelation:type"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="PREDICATE_NAMESPACE">
			<xsl:call-template name="getNamespaceFromURI">
				<xsl:with-param name="URI">
					<xsl:value-of select="escidocContentRelation:type"/>
				</xsl:with-param>
				<xsl:with-param name="SUFFIX" select="$PREDICATE_LOCAL_NAME"/>
			</xsl:call-template>
		</xsl:variable>
		<!-- xsl:value-of select="$PREDICATE_LOCAL_NAME"/ -->
		<!-- create element from predicate-uri and add object-uri and the content relations href, title, and object id -->
		<xsl:element name="{$PREDICATE_LOCAL_NAME}" namespace="{$PREDICATE_NAMESPACE}">
			<xsl:attribute name="rdf:resource"><xsl:value-of select="escidocContentRelation:object/@xlink:href"/></xsl:attribute>
			<xsl:attribute name="xlink:type">simple</xsl:attribute>
			<xsl:attribute name="xlink:title"><xsl:value-of select="@xlink:title"/></xsl:attribute>
			<xsl:attribute name="xlink:href"><xsl:value-of select="@xlink:href"/></xsl:attribute>
			<xsl:attribute name="objid"><xsl:call-template name="getLocalNameFromURI"><xsl:with-param name="URI" select="@xlink:href"/></xsl:call-template></xsl:attribute>
		</xsl:element>
	</xsl:template>

	<!--
		Function "getLocalNameFromURI"

		For given URI, if it contains a '#' the part after the '#' is
		returned. If it does not contain a '#' the part after the 
		last '/' is returned. The return value of this function 
		should be given as SUFFIX to getNamespaceFromURI
		in order to get the URIs namespace.
	-->
	<xsl:template name="getLocalNameFromURI">
		<xsl:param name="URI"/>
		<xsl:choose>
			<xsl:when test="contains($URI, '#')">
				<xsl:value-of select="substring-after($URI,'#')"/>
			</xsl:when>
			<xsl:when test="contains($URI, '/')">
				<xsl:call-template name="getLocalNameFromURI">
					<xsl:with-param name="URI" select="substring-after($URI,'/')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$URI"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Function "getNamespaceFromURI"

		For given URI, if it contains a '#' the part before and the '#' is 
		returned. If it does not contain a '#' the part of the URI that does
		not belong to the namespace must be given as SUFFIX. If 
		SUFFIX is missed and there is no '#' the entire URI is returned.
		The latter case should be treated as error.
	-->
	<xsl:template name="getNamespaceFromURI">
		<xsl:param name="URI"/>
		<xsl:param name="SUFFIX"/>
		<xsl:choose>
			<xsl:when test="contains($URI, '#')">
				<xsl:value-of select="substring-before($URI,'#')"/>
				<xsl:text>#</xsl:text>
			</xsl:when>
			<xsl:when test="$SUFFIX != ''">
				<xsl:value-of select="substring-before($URI, $SUFFIX)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$URI"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
