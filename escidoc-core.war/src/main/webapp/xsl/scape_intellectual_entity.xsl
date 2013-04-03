<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:mets="http://www.loc.gov/METS/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:xlink="http://www.w3.org/1999/xlink" 
   version="1.0">
	<xsl:template match="/">
		<html>
		<head>
			<title>Intellectual Entity <xsl:value-of select="//@OBJID" /></title>
			<style>
				body {
					font-family:sans-serif;
					line-height:0.8em;
					background-image:url('/images/scape.png');
					background-repeat:no-repeat;
					background-position:right top;
					padding-left:10px;
				}
				h1 {
					font-size:15pt;
					color: 0e7a9e;
					padding-top: 20px;
				}
				a:link,
				a:visited,
				a:active{
					color:#0e7a9e;
					text-decoration:none;
				}
				
				a:hover{
					color:#0e7a9e;
					text-decoration:underline;					
				}
			</style>
		</head>
		<body>
			<xsl:variable name="entity_id" select="//@OBJID" />
			<h1><xsl:value-of select="$entity_id" /></h1>
			<p><b><a href="/scape/metadata/{$entity_id}/DESCRIPTIVE/1">Descriptive metadata (Dublin core metadata)</a></b></p>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:title != ''">
				<p style="padding-left:10px">Title: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:title" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:identifier != ''">
				<p style="padding-left:10px">Identifer: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:identifier" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:subject != ''">
				<p style="padding-left:10px">Subject: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:subject" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:description != ''">
				<p style="padding-left:10px">Description: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:description" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:type != ''">
				<p style="padding-left:10px">Type: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:type" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:source != ''">
				<p style="padding-left:10px">Source: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:source" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:relation != ''">
				<p style="padding-left:10px">Relation: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:relation" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:coverage != ''">
				<p style="padding-left:10px">Coverage: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:coverage" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:creator != ''">
				<p style="padding-left:10px">Creator: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:creator" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:publisher != ''">
				<p style="padding-left:10px">Publisher: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:publisher" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:constributor != ''">
				<p style="padding-left:10px">Contributor: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:constributor" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:rights != ''">
				<p style="padding-left:10px">Rights: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:rights" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:date != ''">
				<p style="padding-left:10px">Date: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:date" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:format != ''">
				<p style="padding-left:10px">Format: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:format" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:language != ''">
				<p style="padding-left:10px">Language: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:language" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:provenance != ''">
				<p style="padding-left:10px">Provenance: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:provenance" /></p>
			</xsl:if>
			<xsl:if test="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:rights != ''">
				<p style="padding-left:10px">Rights: <xsl:value-of select="/mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:dublin-core/dc:rights" /></p>
			</xsl:if>
			<xsl:for-each select="/mets:mets/mets:structMap/mets:div/mets:div">
				<xsl:variable name="rep_id" select="./@ID" />
				<p style="padding-left:10px"><b>Representation: <xsl:value-of select="$rep_id"/></b></p>
				<p style="padding-left:20px"><a href="/scape/metadata/{$rep_id}/TECHNICAL/1">Technical metadata</a></p>
				<p style="padding-left:20px"><a href="/scape/metadata/{$rep_id}/SOURCE/1">Source metadata</a></p>
				<p style="padding-left:20px"><a href="/scape/metadata/{$rep_id}/PROVENANCE/1">Provenance metadata</a></p>
				<p style="padding-left:20px"><a href="/scape/metadata/{$rep_id}/RIGHTS/1">Rights metadata</a></p>
				<xsl:for-each select="./mets:fptr">
					<xsl:variable name="file_id" select="./@FILEID" />
					<xsl:variable name="url" select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$file_id]/mets:FLocat/@xlink:href" />
					<p style="padding-left:30px"><a href="{$url}"><b>File: <xsl:value-of select="$file_id" /></b></a></p>
					<p style="padding-left:40px"><a href="/scape/metadata/{$file_id}/TECHNICAL/1">Technical metadata</a></p>
					<xsl:for-each select="/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$file_id]/mets:stream">
						<xsl:variable name="bs_id" select="./@ID" />
						<p style="padding-left:50px"><a href="/scape/bitstream/{$bs_id}"><b>Bit stream: <xsl:value-of select="$bs_id" /></b></a></p>
						<p style="padding-left:60px"><a href="/scape/metadata/{$bs_id}/TECHNICAL/1">Technical metadata</a></p>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:for-each>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>