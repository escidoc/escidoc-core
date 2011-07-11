<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:exsl="http://exslt.org/common"
  extension-element-prefixes="exsl"
  exclude-result-prefixes="exsl"
	version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:import href="docbook-xsl-1.75.2/fo/docbook.xsl" />
	<xsl:param name="section.autolabel" select="1"></xsl:param>
	<xsl:param name="section.label.includes.component.label"
		select="1">
	</xsl:param>
	<xsl:param name="draft.mode" select="'no'"></xsl:param>

	<xsl:param name="body.font.family" select="'sans-serif'"></xsl:param>

	<xsl:template match="title"
		mode="chapter.titlepage.recto.auto.mode">
		<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format"
			xsl:use-attribute-sets="chapter.titlepage.recto.style"
			margin-left="{$title.margin.left}" font-size="18pt"
			font-weight="bold" font-family="{$title.font.family}">
			<xsl:call-template name="component.title">
				<xsl:with-param name="node"
					select="ancestor-or-self::chapter[1]" />
			</xsl:call-template>
		</fo:block>
	</xsl:template>

	<xsl:attribute-set name="section.title.level1.properties">
		<xsl:attribute name="font-size">16pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="section.title.level2.properties">
		<xsl:attribute name="font-size">14pt</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="section.title.level3.properties">
		<xsl:attribute name="font-size">12pt</xsl:attribute>
	</xsl:attribute-set>

<xsl:template match="para[@role='pagebreak']">
      <xsl:apply-templates />
       <fo:block break-after="page">&#160;</fo:block>
  </xsl:template>

	<!-- Our custom titlepage -->
	<xsl:template name="book.titlepage.recto">
		<fo:block>
			<fo:table table-layout="fixed" width="100%">
				<fo:table-column />
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="1in">
							<fo:block>
								<xsl:apply-templates
									mode="book.titlepage.recto.mode" select="bookinfo/mediaobject[@id='m1']" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="1in" padding-top="1in">
							<fo:block text-align="center">
								<xsl:choose>
									<xsl:when test="bookinfo/title">
										<xsl:apply-templates
											mode="book.titlepage.recto.auto.mode"
											select="bookinfo/title" />
									</xsl:when>
									<xsl:when test="title">
										<xsl:apply-templates
											mode="book.titlepage.recto.auto.mode" select="title" />
									</xsl:when>
								</xsl:choose>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="0.5in" padding-top="1in">
							<fo:block text-align="center">
								<xsl:choose>
									<xsl:when
										test="bookinfo/subtitle">
										<xsl:apply-templates
											mode="book.titlepage.recto.auto.mode"
											select="bookinfo/subtitle" />
									</xsl:when>
									<xsl:when test="subtitle">
										<xsl:apply-templates
											mode="book.titlepage.recto.auto.mode" select="subtitle" />
									</xsl:when>
								</xsl:choose>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<!-- <fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="1in" padding-top="0.1in"> 
							<fo:block font-family="sans-serif,Symbol,ZapfDingbats" font-weight="bold" font-size="20.736pt" text-align="center">
								{BUILD.INTERFACE} Interface
							</fo:block>
						</fo:table-cell>
					</fo:table-row> -->

					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="0.1in">
							<fo:block text-align="center"
								font-family="{$title.fontset}">
								Version {BUILD.VERSION}
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<!--  <fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="0.1in">
							<fo:block text-align="center"
								font-family="{$title.fontset}">
								Author
								<xsl:apply-templates
									mode="book.titlepage.recto.mode"
									select="bookinfo/revhistory/revision[1]/authorinitials" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>-->
					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="0.1in">
							<fo:block text-align="center"
								font-family="{$title.fontset}">
								Build date {BUILD.DATE}
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="1"
							padding-bottom="1in">
							<fo:block>
								<xsl:apply-templates
									mode="book.titlepage.recto.mode" select="bookinfo/mediaobject[@id='m2']" />
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block text-align="center" font-family="{$title.fontset}">
			<xsl:apply-templates mode="book.titlepage.recto.mode"
				select="bookinfo/copyright" />
		</fo:block>
	</xsl:template>


    <!-- TODO uncomment this to incliude Revision history -->
	<!-- <xsl:template name="book.titlepage.verso">
		<fo:block>
			<fo:table table-layout="fixed" width="100%"
				xsl:use-attribute-sets="revhistory.table.properties">
				<fo:table-column column-number="1" column-width="1cm" />
				<fo:table-column column-number="2" column-width="2.5cm" />
				<fo:table-column column-number="3" column-width="1cm" />
				<fo:table-column column-number="4"
					column-width="proportional-column-width(1)" />
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell number-columns-spanned="3"
							padding-bottom="0.5cm"
							xsl:use-attribute-sets="revhistory.table.cell.properties">
							<fo:block
								xsl:use-attribute-sets="revhistory.title.properties">
								<xsl:call-template name="gentext">
									<xsl:with-param name="key"
										select="'RevHistory'" />
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<xsl:for-each
						select="bookinfo/revhistory/revision">
						<fo:table-row>
							<fo:table-cell number-columns-spanned="1"
								xsl:use-attribute-sets="revhistory.table.cell.properties">
								<fo:block text-align="left">
									<xsl:choose>
										<xsl:when test="revnumber">
											<xsl:apply-templates
												mode="book.titlepage.recto.auto.mode" select="revnumber" />
										</xsl:when>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="1"
								xsl:use-attribute-sets="revhistory.table.cell.properties">
								<fo:block text-align="left">
									<xsl:choose>
										<xsl:when test="date">
											<xsl:apply-templates
												mode="book.titlepage.recto.auto.mode" select="date" />
										</xsl:when>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="1"
								xsl:use-attribute-sets="revhistory.table.cell.properties">
								<fo:block text-align="left">
									<xsl:choose>
										<xsl:when
											test="authorinitials">
											<xsl:apply-templates
												mode="book.titlepage.recto.auto.mode"
												select="authorinitials" />
										</xsl:when>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell number-columns-spanned="1"
								xsl:use-attribute-sets="revhistory.table.cell.properties">
								<fo:block text-align="left">
									<xsl:choose>
										<xsl:when test="revremark">
											<xsl:apply-templates
												mode="book.titlepage.recto.auto.mode" select="revremark" />
										</xsl:when>
									</xsl:choose>
								</fo:block>
							</fo:table-cell>
						</fo:table-row>
					</xsl:for-each>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template> -->
	<!-- TODO end -->

	<xsl:template name="header.content">
		<xsl:param name="pageclass" select="''" />
		<xsl:param name="sequence" select="''" />
		<xsl:param name="position" select="''" />
		<xsl:param name="gentext-key" select="''" />

		<xsl:variable name="candidate">
			<!-- sequence can be odd, even, first, blank -->
			<!-- position can be left, center, right -->
			<xsl:choose>

				<xsl:when
					test="$sequence = 'odd' and $position = 'right'">
					<!--<fo:retrieve-marker retrieve-class-name="section.head.marker" 
						retrieve-position="first-including-carryover"
						retrieve-boundary="page-sequence"/>-->
					<xsl:value-of
						select="ancestor-or-self::book/bookinfo/subtitle" />
				</xsl:when>	

				<xsl:when
					test="$sequence = 'even' and $position = 'left'">
					<fo:retrieve-marker
						retrieve-class-name="section.head.marker"
						retrieve-position="first-including-carryover"
						retrieve-boundary="page-sequence" />
				</xsl:when>

				<xsl:when
					test="$sequence = 'first' and $position = 'left'">
				</xsl:when>

				<xsl:when
					test="$sequence = 'first' and $position = 'right'">
				</xsl:when>

				<xsl:when
					test="$sequence = 'first' and $position = 'center'">
					<xsl:value-of
						select="ancestor-or-self::book/bookinfo/corpauthor" />
				</xsl:when>

				<xsl:when
					test="$sequence = 'blank' and $position = 'left'">
					<fo:page-number />
				</xsl:when>

				<xsl:when
					test="$sequence = 'blank' and $position = 'center'">
					<xsl:text>
						This page intentionally left blank
					</xsl:text>
				</xsl:when>

				<xsl:when
					test="$sequence = 'blank' and $position = 'right'">
				</xsl:when>

			</xsl:choose>
		</xsl:variable>

		<!-- Does runtime parameter turn off blank page headers? -->
		<xsl:choose>
			<xsl:when
				test="$sequence='blank' and $headers.on.blank.pages=0">
				<!-- no output -->
			</xsl:when>
			<!-- titlepages have no headers -->
			<xsl:when test="$pageclass = 'titlepage'">
				<!-- no output -->
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$candidate" />
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	  <!-- Hyphenate monospaced text -->
  <xsl:template name="inline.monoseq">
     <xsl:param name="content">
       <xsl:apply-templates/>
     </xsl:param>
     <fo:inline xsl:use-attribute-sets="monospace.properties">
       <xsl:if test="@dir">
         <xsl:attribute name="direction">
           <xsl:choose>
             <xsl:when test="@dir = 'ltr' or @dir = 'lro'">ltr</xsl:when>
             <xsl:otherwise>rtl</xsl:otherwise>
           </xsl:choose>
         </xsl:attribute>
       </xsl:if>
       <xsl:apply-templates select="exsl:node-set($content)" mode="hyphenate"/>
     </fo:inline>
  </xsl:template>
  
  <xsl:template match="text()" mode="hyphenate" priority="2">
     <xsl:call-template name="string.subst">
       <xsl:with-param name="string">
         <xsl:call-template name="string.subst">
           <xsl:with-param name="string">
             <xsl:call-template name="string.subst">
               <xsl:with-param name="string" select="."/>
               <xsl:with-param name="target" select="'.'"/>
               <xsl:with-param name="replacement" select="'.&#x200B;'"/>
             </xsl:call-template>
           </xsl:with-param>
           <xsl:with-param name="target" select="'\'"/>
           <xsl:with-param name="replacement" select="'\&#x200B;'"/>
         </xsl:call-template>
       </xsl:with-param>
       <xsl:with-param name="target" select="'/'"/>
       <xsl:with-param name="replacement" select="'/&#x200B;'"/>
     </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="node()|@*" mode="hyphenate">
     <xsl:copy>
       <xsl:apply-templates select="node()|@*" mode="hyphenate"/>
     </xsl:copy>
  </xsl:template>
  
  <!-- <?custom-pagebreak?> inserts a page break at this point -->
  <xsl:template match="processing-instruction('custom-pagebreak')">
    <fo:block break-before='page'/>
  </xsl:template>
</xsl:stylesheet>
