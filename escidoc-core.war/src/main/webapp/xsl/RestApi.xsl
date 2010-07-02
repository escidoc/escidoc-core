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
				Methods of Resource <xsl:value-of select="//mapping/resource/@name"/> for REST Interface
			</title>
			<xsl:if test="//mapping/resource/descriptor/invoke[@http != 'POST']">
				<section>
					<title>Resource oriented Methods</title>
					<xsl:apply-templates select="//mapping/resource/descriptor/invoke[@http != 'POST']"/>
				</section>
			</xsl:if>
			<xsl:if test="//mapping/resource/descriptor/invoke[@http != 'POST'] and //mapping/resource/descriptor/invoke[@http = 'POST']">
				<para role="pagebreak"/>
			</xsl:if>
			<xsl:if test="//mapping/resource/descriptor/invoke[@http = 'POST']">
				<section>
					<title>Task oriented Methods</title>
					<xsl:apply-templates select="//mapping/resource/descriptor/invoke[@http = 'POST']"/>
				</section>
			</xsl:if>
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
				<xsl:when test="string-length($available) > 0 and $available != 'REST'">
					<xsl:value-of select="$false"/>
				</xsl:when>
				<xsl:when test="string-length($escidoc_core.available) > 0 and $escidoc_core.available != 'REST'">
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
						<xsl:value-of select="$method"/> via Rest
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
								<entry>HTTP Request</entry>
								<entry spanname="hspan25">
									<xsl:value-of select="$httpMethod"/>
									<xsl:text> </xsl:text>
									<xsl:call-template name="formatParameterName">
										<xsl:with-param name="text" select="$url"/>
									</xsl:call-template>
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
							<xsl:choose>
								<xsl:when test="$numParams - $numBodyParams = 0">
									<row rowsep="1">
										<entry>Input from Uri</entry>
										<entry spanname="hspan25">
											No input values
										</entry>
									</row>
								</xsl:when>
								<xsl:otherwise>
									<xsl:for-each select="variablelist[title = 'Parameters']/varlistentry[term/varname]">
										<xsl:variable name="paramPosition" select="position()"/>
										<xsl:variable name="paramVariable" select="$paramAttributes[$paramPosition]"/>
										<xsl:if test="$paramVariable != '${BODY}'">										
											<row rowsep="1">
												<entry>
													<xsl:if test="$paramPosition = 1">Input from Uri</xsl:if>
													<xsl:text> </xsl:text>
												</entry>
												<entry spanname="hspan25">
													<xsl:call-template name="formatParameterName">
														<xsl:with-param name="text" select="$paramVariable"/>
													</xsl:call-template>
													<xsl:value-of select="listitem/para"/>
												</entry>
											</row>
										</xsl:if>
									</xsl:for-each>
								</xsl:otherwise>
							</xsl:choose>
							
							<row rowsep="1">
								<entry>
									<xsl:text> </xsl:text>
								</entry>
								<entry spanname="hspan25">
									<xsl:text> </xsl:text>
								</entry>
							</row>
							
							<row rowsep="1">
								<entry>Input from Body</entry>
								<entry spanname="hspan25">
									<xsl:choose>
										<xsl:when test="$numBodyParams &lt; 1">No input values</xsl:when>
										<xsl:otherwise>
											<!-- body is expected to be the last parameter -->
											<xsl:value-of select="variablelist[title = 'Parameters']/varlistentry[term/varname][$numParams]/listitem/para"/>
										</xsl:otherwise>
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

	<xsl:template name="formatParameterName">
		<xsl:param name="text"/>
		<!-- '$' entfernen -->
		<xsl:variable name="tmp1">
			<xsl:call-template name="replaceText">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="replace" select="'$'"/>
				<xsl:with-param name="by" select="''"/>
			</xsl:call-template>
		</xsl:variable>
		<!-- '{' ersetzen -->
		<xsl:variable name="tmp2">
			<xsl:call-template name="replaceText">
				<xsl:with-param name="text" select="$tmp1"/>
				<xsl:with-param name="replace" select="'{'"/>
				<xsl:with-param name="by" select="'&lt;'"/>
			</xsl:call-template>
		</xsl:variable>
		<!-- '}' ersetzen -->
		<xsl:variable name="tmp3">
			<xsl:call-template name="replaceText">
				<xsl:with-param name="text" select="$tmp2"/>
				<xsl:with-param name="replace" select="'}'"/>
				<xsl:with-param name="by" select="'&gt;'"/>
			</xsl:call-template>
		</xsl:variable>
		<!-- alles in Kleinbuchstaben umwandeln -->
		<emphasis>
			<xsl:value-of select="translate($tmp3, $upper, $lower)"/>
		</emphasis>
	</xsl:template>
	<xsl:template name="replaceText">
		<xsl:param name="text"/>
		<xsl:param name="replace"/>
		<xsl:param name="by"/>
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text, $replace)"/>
				<xsl:value-of select="$by" disable-output-escaping="yes"/>
				<xsl:call-template name="replaceText">
					<xsl:with-param name="text" select="substring-after($text, $replace)"/>
					<xsl:with-param name="replace" select="$replace"/>
					<xsl:with-param name="by" select="$by"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
