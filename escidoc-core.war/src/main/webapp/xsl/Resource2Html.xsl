<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	version="1.0" xmlns:xlink="http://www.w3.org/1999/xlink">
	<xsl:output encoding="iso-8859-1" indent="yes" method="html" />

	<!-- display serialized java.util.Properties nicely -->
	<xsl:template match="/properties">
		<xsl:for-each select="entry">
			<ul>
				<b>
					<xsl:value-of select="@key" />
					:
				</b>
				<xsl:value-of select="./text()" />
			</ul>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="/">
		<html>
			<head>
				<title>eSciDoc<xsl:if test="*/@xlink:title != ''"> - <xsl:value-of select="*/@xlink:title"/></xsl:if><xsl:if test="*/@xlink:href"> - <xsl:value-of select="*/@xlink:href"/></xsl:if></title>
				<script language="JavaScript" type="text/javascript" src="/js/shownhide.js">
					<!-- 
					<xsl:attribute name="src"><xsl:value-of select="*/@xml:base" />/js/shownhide.js</xsl:attribute>
					-->
				</script>
			</head>
			<body bgcolor="#eeeeee">
				<img src="/images/escidoc-banner.jpg" />
				<br />
				<!-- 
				<xsl:choose>
					<xsl:when test="item-list-refs">
						<xsl:for-each select="item-list-refs">
							<xsl:call-template name="item-list-refs" />
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
				 -->
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="item-ref-list|item-list-refs">
		<xsl:variable name="theBase">
			<xsl:value-of select="@xml:base" />
		</xsl:variable>
		<h2>
			<xsl:value-of select="@xlink:title" />
		</h2>
		<xsl:for-each select="*">
			<li>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$theBase" />
						<xsl:value-of select="@xlink:href" />
					</xsl:attribute>
					<xsl:value-of select="@objid" />
				</a>
			</li>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="exception">
		<h1><xsl:value-of select="title"/></h1>
		<blockquote>
		<P><xsl:value-of select="class"/></P>
		<xsl:variable name="UID">message<xsl:value-of select="count(ancestor::*)"/></xsl:variable>
		<P>
			<xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID"/>');</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="$UID"/>
			</xsl:attribute>
			<xsl:attribute name="style">border: 0px solid #000000;</xsl:attribute>
			<font style="color: #0000ff;">Message</font>
		</P>
		<pre style="display: none; visibility: hidden;">
			<xsl:attribute name="id"><xsl:value-of select="$UID"/>-body</xsl:attribute>
			<xsl:value-of select="message"/>
		</pre>
		
		<xsl:variable name="UID2">stacktrace<xsl:value-of select="count(ancestor::*)"/></xsl:variable>
		<P>
			<xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID2"/>');</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="$UID2"/>
			</xsl:attribute>
			<xsl:attribute name="style">border: 0px solid #000000;</xsl:attribute>
			<font style="color: #0000ff;">Stack Trace</font>
		</P>
		<pre style="display: none; visibility: hidden;">
			<xsl:attribute name="id"><xsl:value-of select="$UID2"/>-body</xsl:attribute>
			<xsl:value-of select="stack-trace"/>
		</pre>
		
		<xsl:variable name="UID3">more<xsl:value-of select="count(ancestor::*)"/></xsl:variable>
		<xsl:if test="cause/exception">
			<P>
				<xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID3"/>');</xsl:attribute>
				<xsl:attribute name="id">
					<xsl:value-of select="$UID3"/>
				</xsl:attribute>
				<xsl:attribute name="style">border: 0px solid #000000;</xsl:attribute>
				<font style="color: #0000ff;">Nested Exceptions</font>
			</P>
			<pre style="display: none; visibility: hidden;">
				<xsl:attribute name="id"><xsl:value-of select="$UID3"/>-body</xsl:attribute>
				<xsl:apply-templates select="cause/exception"/>
			</pre>
		</xsl:if>
		</blockquote>
	</xsl:template>
	
	<!-- 
	<xsl:template match="rdf:RDF">
		<h3>From RDF representation:</h3>
		<ul>
			<xsl:apply-templates/>
		</ul>
	</xsl:template>
	<xsl:template match="rdf:Description">
		<li><a><xsl:attribute name="href">/ir/item/<xsl:value-of select="substring-after(@rdf:about, '/')"/></xsl:attribute><xsl:value-of select="*[local-name() = 'title']"/></a>
			<ul>
				<li>created by<xsl:text> </xsl:text><b><xsl:value-of select="*[local-name() = 'created-by-title']"/></b></li>
				<li>status is<xsl:text>  </xsl:text><b><xsl:value-of select="*[local-name() = 'latest-version-status']"/></b>
					<xsl:if test="*[local-name() = 'public-status' and text() = 'released']">
						and is <b>public available</b>
					</xsl:if>
				</li>
				<li>known PIDs:
				<xsl:for-each select="*[local-name() = 'identifier']">
					&#160;&#160;&#160;<b><xsl:value-of select="."/></b>
				</xsl:for-each>
				</li>
			</ul>
		</li>
	</xsl:template>
	 -->
	 
	<xsl:template match="identifier">
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:variable name="theBase">
			<xsl:value-of select="@xml:base" />
		</xsl:variable>
		<!-- Base-URL is <xsl:value-of select="$theBase"/> -->
		<h3>
			<xsl:value-of select="@xlink:title" />
			(
			<xsl:value-of select="local-name()" />
			<xsl:choose>
				<xsl:when test="@objid">
					-
					<xsl:value-of select="@objid" />
				</xsl:when>
				<xsl:when test="@name">
					-
					<xsl:value-of select="@name" />
				</xsl:when>
				<xsl:when test="@id">
					-
					<xsl:value-of select="@id" />
				</xsl:when>
			</xsl:choose>
			)
		</h3>
		<xsl:if test="./*/*[local-name() = 'version']">
			<p>
				This version:
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$theBase" />
						<xsl:value-of
							select="./*/*[local-name() = 'version']/@xlink:href" />
					</xsl:attribute>
					<xsl:value-of
						select="./*/*[local-name() = 'version']/@xlink:href" />
				</a>
				(
				<xsl:value-of
					select="./*/*[local-name() = 'version']/*[local-name() = 'date']" />
				)
				<xsl:if test="./*/*[local-name() = 'latest-version']">
					<br />
					Latest version:
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$theBase" />
							<xsl:value-of
								select="./*/*[local-name() = 'latest-version']/@xlink:href" />
						</xsl:attribute>
						<xsl:value-of
							select="./*/*[local-name() = 'latest-version']/@xlink:href" />
					</a>
					(
					<xsl:value-of
						select="./*/*[local-name() = 'latest-version']/*[local-name() = 'date']" />
					)
				</xsl:if>
				<xsl:if test="./*/*[local-name() = 'latest-release']">
					<br />
					Latest release:
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$theBase" />
							<xsl:value-of
								select="./*/*[local-name() = 'latest-release']/@xlink:href" />
						</xsl:attribute>
						<xsl:value-of
							select="./*/*[local-name() = 'latest-release']/@xlink:href" />
					</a>
					(
					<xsl:value-of
						select="./*/*[local-name() = 'latest-release']/*[local-name() = 'date']" />
					)
				</xsl:if>
			</p>
		</xsl:if>
		<ul>
			<xsl:for-each select="./*[@xlink:href]">
				<xsl:variable name="contentLink" select="@xlink:href"/>
				<li>
					<a>
						<xsl:attribute name="href">
							<xsl:if test="not(starts-with(@xlink:href, 'http'))">
								<xsl:value-of select="$theBase" />
							</xsl:if>
							<xsl:value-of select="@xlink:href" />
						</xsl:attribute>
						<xsl:value-of select="local-name()" />
					</a>
					<xsl:if test="@predicate">
						- <xsl:value-of select="@predicate"/>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="@xlink:title">
							-
							<xsl:value-of select="@xlink:title" />
						</xsl:when>
						<xsl:when test="@objid">
							-
							<xsl:value-of select="@objid" />
						</xsl:when>
						<xsl:when test="@name">
							-
							<xsl:value-of select="@name" />
						</xsl:when>
						<xsl:when test="@id">
							-
							<xsl:value-of select="@id" />
						</xsl:when>
					</xsl:choose>
					<!-- xsl:if test="local-name()='content'" -->
					<xsl:if test="@storage = 'internal-managed'">
						<xsl:variable name = "mimeType" select="//prop:mime-type"/>
						<xsl:if test="starts-with($mimeType, 'image')">
					<br /> ( Image service: 
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="$theBase" />
							<xsl:value-of select="$contentLink" />
							/digilib/digimage
						</xsl:attribute>
						digimage
					</a>)
						</xsl:if>
					</xsl:if>
					<xsl:if
						test="local-name() = 'current-version' or local-name() = 'version'">
						<xsl:for-each select="./*">
							<xsl:call-template name="keynval" />
						</xsl:for-each>
					</xsl:if>
				</li>
			</xsl:for-each>
		</ul>
		<xsl:for-each select="./*[not(@xlink:href)]">
			<xsl:call-template name="keynval" />
		</xsl:for-each>
	</xsl:template>

	<!--
		<xsl:template match="mdr:md-record">
		<h3><xsl:value-of select="@xlink:title"/> (<xsl:value-of select="local-name()"/>
		<xsl:choose>
		<xsl:when test="@objid"> - <xsl:value-of select="@objid"/></xsl:when>
		<xsl:when test="@name"> - <xsl:value-of select="@name"/></xsl:when>
		<xsl:when test="@id"> - <xsl:value-of select="@id"/></xsl:when>
		</xsl:choose>)</h3>
		<xsl:call-template name="keynval"/>
		</xsl:template>
	-->

	<xsl:template name="keynval">
		<ul>
			<b>
				<xsl:value-of select="local-name()" />
				:
			</b>
			<xsl:choose>
				<xsl:when test="@xlink:href">
					<a>
						<xsl:attribute name="href">
							<xsl:value-of select="@xlink:href" />
						</xsl:attribute>
						<xsl:value-of select="@xlink:href" />
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="./text()" />
					<xsl:for-each select="@*">
						<xsl:text> @</xsl:text>
						<b><xsl:value-of select="local-name()"/></b>
						<xsl:text>=</xsl:text>
						<xsl:value-of select="."/>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
			<br />
			<xsl:for-each select="./*">
				<xsl:call-template name="keynval" />
			</xsl:for-each>
		</ul>
	</xsl:template>

	<!--
		<xsl:template match="*">
		<xsl:param name="theBase"/>
		<xsl:value-of select="local-name()"/>: <a>
		<xsl:attribute name="href">
		<xsl:value-of select="$theBase"/>
		<xsl:value-of select="@xlink:href"/>
		</xsl:attribute> link </a><br/>
		</xsl:template>
	-->

</xsl:stylesheet>
