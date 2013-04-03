<xsl:stylesheet
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
   xmlns:xlink="http://www.w3.org/1999/xlink" 
   xmlns:mets="http://www.loc.gov/METS/"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:fits="http://hul.harvard.edu/ois/xml/ns/fits/fits_output" 
   xmlns:textmd="info:lc/xmlns/textmd-v3" 
   xmlns:audiomd="http://www.loc.gov/audioMD/" 
   xmlns:marc="http://www.loc.gov/MARC21/slim" 
   xmlns:mix="http://www.loc.gov/mix/v20" 
   xmlns:vmd="http://www.loc.gov/videoMD/" 
   xmlns:premis="info:lc/xmlns/premis-v2" 
   version="1.0">
	<xsl:template match="/">
		<html>
		<head>
			<title>Metadata</title>
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
				textarea{
					width: 50%;
					height: 200;
				}
			</style>
		</head>
		<body>
			<xsl:if test="/dc:dublin-core">
				<p><b>Dublin core metadata record</b>(<a href="http://dublincore.org/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/dc:dublin-core" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/marc:record">
				<p><b>Marc21 metadata record</b>(<a href="http://www.loc.gov/marc/marcdocz.html">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/marc:record" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/fits:fits">
				<p><b>FITS metadata record</b>(<a href="https://code.google.com/p/fits/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/fits:fits" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/textmd:textMD">
				<p><b>TextMD metadata record</b>(<a href="http://www.loc.gov/standards/textMD/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/textmd:textMD" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/audiomd:audio">
				<p><b>AudioMD metadata record</b>(<a href="http://www.loc.gov/standards/amdvmd/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/audiomd:audio" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/vmd:video">
				<p><b>Video MD metadata record</b>(<a href="http://www.loc.gov/standards/amdvmd/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/vmd:video" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/mix:mix">
				<p><b>NISO/Mix metadata record</b>(<a href="http://www.loc.gov/standards/mix/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/mix:mix" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/premis:premis">
				<p><b>PREMIS provenance metadata record</b>(<a href="http://www.loc.gov/standards/premis/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/premis:premis" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
			<xsl:if test="/premis:rights">
				<p><b>PREMIS rights metadata record</b>(<a href="http://www.loc.gov/standards/premis/">Documentation</a>)</p>
				<xsl:variable name="nodes" select="/premis:rights" />
				<textarea>
	   				<xsl:copy-of select="$nodes"/>
	   			</textarea>
			</xsl:if>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>