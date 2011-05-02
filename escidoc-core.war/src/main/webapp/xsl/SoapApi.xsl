<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output encoding="utf-8" method="xml"/>

	<!-- falls false soll alles ausgegeben werden, default = true -->
	<xsl:param name="checkVisible"/>
	<xsl:variable name="lower" select="'abcdefghijklmnopqrstuvwxyz'"/>
	<xsl:variable name="upper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
	<xsl:variable name="true" select="'true'"/>
	<xsl:variable name="false" select="'false'"/>
	<xsl:template match="/">
		<chapter>
			<title>
				Methods of Resource <xsl:value-of select="//mapping/resource/@name"/> for SOAP Interface
			</title>
			<!-- para role="pagebreak"/ -->
			<xsl:apply-templates select="//mapping/resource/descriptor/invoke"/>
		</chapter>
	</xsl:template>
	
	<xsl:template name="getArgumentList">
		<xsl:param name="i">1</xsl:param>
		<xsl:variable name="paramAttributeName">param<xsl:value-of select="$i"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@*[local-name() = $paramAttributeName]">
				<xsl:if test="$i > 1">, </xsl:if>
				<xsl:value-of select="@*[local-name() = $paramAttributeName]"/>
				<xsl:call-template name="getArgumentList">
					<xsl:with-param name="i" select="$i + 1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="getArgumentTypeList">
		<xsl:param name="i">1</xsl:param>
		<xsl:variable name="paramAttributeName">param<xsl:value-of select="$i"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@*[local-name() = $paramAttributeName]">
				<xsl:if test="$i > 1">, </xsl:if>
				<xsl:variable name="param">
					<xsl:value-of select="@*[local-name() = $paramAttributeName]"/>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="contains($param, 'PARAMETERS')">java.util.Map&lt;java.lang.String, java.lang.String[]&gt;</xsl:when>
					<xsl:otherwise>java.lang.String</xsl:otherwise>
				</xsl:choose>
				<xsl:call-template name="getArgumentTypeList">
					<xsl:with-param name="i" select="$i + 1"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise/>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="invoke">
		<!-- get values from servlet descriptor -->
		<xsl:variable name="method" select="@method"/>
		<xsl:variable name="httpMethod" select="@http"/>
		<xsl:variable name="url" select="../@uri"/>
		<xsl:variable name="resource">
			<xsl:value-of select="../../@name"/>
		</xsl:variable>
		<xsl:variable name="argumentTypeList">
			<xsl:call-template name="getArgumentTypeList"/>
		</xsl:variable>
		<xsl:variable name="methodSignature">
			<xsl:value-of select="$method"/>(<xsl:value-of select="$argumentTypeList"/>)</xsl:variable>
		<xsl:apply-templates select="//sect1[contains(@xreflabel,$resource)]/sect2[@xreflabel = $methodSignature]">
			<xsl:with-param name="method" select="$method"/>
			<xsl:with-param name="methodSignature" select="$methodSignature"/>
			<xsl:with-param name="httpMethod" select="$httpMethod"/>
			<xsl:with-param name="url" select="$url"/>
			<xsl:with-param name="available" select="documentation/@available"/>
			<xsl:with-param name="visible" select="documentation/@visible"/>
			<xsl:with-param name="paramAttributes" select="@*[starts-with(local-name(), 'param')]"/>
			<xsl:with-param name="numBodyParams" select="count(@*[starts-with(local-name(), 'param')][. = '${BODY}'])"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="sect2">
		<!-- values from servlet descriptor -->
		<xsl:param name="method"/>
		<xsl:param name="methodSignature"/>
		<xsl:param name="httpMethod"/>
		<xsl:param name="url"/>
		<xsl:param name="available"/>
		<xsl:param name="visible"/>
		<xsl:param name="paramAttributes"/>
		<xsl:param name="numBodyParams"/>
		<xsl:variable name="numParams" select="count(methodsynopsis/methodparam/parameter)"/>
		<xsl:variable name="escidoc_core.warning" select="variablelist/varlistentry[term/emphasis/text() = 'Escidoc_core.warning']//member"/>
		<xsl:variable name="escidoc_core.available" select="variablelist/varlistentry[term/emphasis/text() = 'Escidoc_core.available']//member"/>
		<xsl:variable name="escidoc_core.visible" select="variablelist/varlistentry[term/emphasis/text() = 'Escidoc_core.visible']//member"/>

		<xsl:variable name="isAvailable">
			<xsl:choose>
				<xsl:when test="string-length($available) > 0 and $available != 'SOAP'">
					<xsl:value-of select="$false"/>
				</xsl:when>
				<xsl:when test="string-length($escidoc_core.available) > 0 and $escidoc_core.available != 'SOAP'">
					<xsl:value-of select="$false"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$true"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="isVisible">
			<xsl:choose>
				<xsl:when test="string-length($visible) > 0 and $visible != 'true'">
					<xsl:value-of select="$false"/>
				</xsl:when>
				<xsl:when test="string-length($escidoc_core.visible) > 0 and $escidoc_core.visible != 'true'">
					<xsl:value-of select="$false"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$true"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- 
		<xsl:value-of select="$method"/>: visible=<xsl:value-of select="$isVisible"/>, available=<xsl:value-of select="$isAvailable"/>
		 -->
		<!-- falls veruegbar -->
		<!-- falls sichtbar -->
		<!-- nun anhand des Parameters prüfen ob doch alles aufgelistet werden soll -->
		<!-- es muss available sein und (visible oder checkVisible ist false); wenn checkVisible nicht gesetzt ist, ist checkVisible true -->
		<!-- Methoden auflisten -->
		<xsl:if test="$isAvailable = $true and ($isVisible = $true or $checkVisible = 'false')">
			<section>
				<title>
					<xsl:value-of select="$method"/>
					<xsl:if test="warning/title or $escidoc_core.warning"> (<xsl:value-of select="warning/title"/>
						<xsl:if test="warning/title and $escidoc_core.warning">, </xsl:if>
						<xsl:value-of select="$escidoc_core.warning"/>)</xsl:if>
				</title>
				
				<!-- es soll alles nach dem letzten variablelist-Element ausgegeben werden -->
				<xsl:copy-of select="*[local-name() != 'variablelist' and local-name() != 'warning' and preceding-sibling::variablelist]"/>

				<table frame="all">
					<title>
						<xsl:value-of select="$method"/> via Soap
					</title>
					<tgroup cols="5" align="left" colsep="1" rowsep="1">
						<colspec colname="c1"/>
						<colspec colname="c2"/>
						<colspec colname="c3"/>
						<colspec colname="c4"/>
						<colspec colname="c5"/>
						<spanspec spanname="hspan25" namest="c2" nameend="c5" align="left"/>
						<spanspec spanname="hspan35" namest="c3" nameend="c5" align="left"/>
						<thead>
							<row rowsep="1">
								<entry>Method Signature</entry>
								<entry spanname="hspan25">
									<xsl:value-of select="methodsynopsis/type"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="../../@name"/>Service.<xsl:value-of select="$methodSignature"/>
								</entry>
							</row>
						</thead>
						<!-- Parameter ausgeben -->
						<tbody>
							<row rowsep="1">
								<entry>
									<xsl:text> </xsl:text>
								</entry>
								<entry spanname="hspan25">
									<xsl:text> </xsl:text>
								</entry>
							</row>
							
									<xsl:for-each select="variablelist[title = 'Parameters']/varlistentry[term/varname]">
										<xsl:variable name="paramPosition" select="position()"/>
										<xsl:variable name="paramVariable" select="$paramAttributes[$paramPosition]"/>
										
											<row rowsep="1">
												<entry>
													<xsl:if test="$paramPosition = 1">Parameter</xsl:if>
													<xsl:text> </xsl:text>
												</entry>
												<entry spanname="hspan25">
													<emphasis><xsl:value-of select="term/varname"/></emphasis>:<xsl:text> </xsl:text>
													<xsl:value-of select="listitem/para"/>
												</entry>
											</row>
											
									</xsl:for-each>

							<row rowsep="1">
								<entry>
									<xsl:text> </xsl:text>
								</entry>
								<entry spanname="hspan25">
									<xsl:text> </xsl:text>
								</entry>
							</row>
							
							<row rowsep="1">
								<entry>Output</entry>
								<entry spanname="hspan25">
									<xsl:choose>
										<xsl:when test="variablelist[title = 'Parameters']/varlistentry[term/emphasis = 'return']">
											<xsl:value-of select="variablelist[title = 'Parameters']/varlistentry[term/emphasis = 'return']/listitem/para"/>
										</xsl:when>
										<xsl:otherwise>No return value</xsl:otherwise>
									</xsl:choose>
								</entry>
							</row>
							
							<row rowsep="1">
								<entry>
									<xsl:text> </xsl:text>
								</entry>
								<entry spanname="hspan25">
									<xsl:text> </xsl:text>
								</entry>
							</row>
							
							<xsl:if test="methodsynopsis/exceptionname">
								<row rowsep="1">
									<entry>Possible errors</entry>
									<entry spanname="hspan25">
										<xsl:apply-templates select="methodsynopsis/exceptionname"/>
									</entry>
								</row>
							</xsl:if>
						</tbody>
					</tgroup>
				</table>
				<para role="pagebreak"/>
			</section>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="exceptionname">
		<xsl:variable name="exception" select="."/>
		<xsl:variable name="tmpNode" select="//sect1[contains(@xreflabel, $exception)]/sect2"/>
		<member>
			<xsl:value-of select="$tmpNode/classsynopsis/fieldsynopsis[1]/code"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$tmpNode/classsynopsis/fieldsynopsis[2]/code"/>
			<xsl:text> </xsl:text> (caused by <xsl:value-of select="$exception"/>)
		</member>
	</xsl:template>
	

</xsl:stylesheet>
