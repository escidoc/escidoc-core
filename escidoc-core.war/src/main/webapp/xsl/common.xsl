<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml"/>
	<xsl:template match="xs:element">
		<xsl:param name="SUB"/>
		<xsl:choose>
			<xsl:when test="@name">
				<xsl:variable name="PATH">
					<xsl:value-of select="$SUB"/>/<xsl:value-of select="@name"/>
				</xsl:variable>
				<table frame="all" border="1">
					<title>
						<xsl:text>Element &lt;</xsl:text>
						<xsl:value-of select="@name|@ref"/>
						<xsl:text>&gt;</xsl:text>
					</title>
					<tgroup cols="12" align="left" colsep="1" rowsep="1">
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
						<spanspec spanname="create" namest="c5" nameend="c8" align="center"/>
						<spanspec spanname="create.input" namest="c5" nameend="c6" align="center"/>
						<spanspec spanname="create.output" namest="c7" nameend="c8" align="center"/>
						<spanspec spanname="update" namest="c9" nameend="c12" align="center"/>
						<spanspec spanname="update.input" namest="c9" nameend="c10" align="center"/>
						<spanspec spanname="update.output" namest="c11" nameend="c12" align="center"/>
						<thead>
							<row>
								<entry spanname="all">
									<xsl:value-of select="$PATH"/>
								</entry>
							</row>
							<row>
								<entry spanname="all">
									<xsl:text> </xsl:text>
								</entry>
							</row>
							<row>
								<entry rowsep="0" spanname="element"/>
								<entry rowsep="0" spanname="attribute"/>
								<entry spanname="create">create</entry>
								<entry spanname="update">update</entry>
							</row>
							<row>
								<entry spanname="element">Element</entry>
								<entry spanname="attribute">Attribute</entry>
								<entry spanname="create.input">input</entry>
								<entry spanname="create.output">output</entry>
								<entry spanname="update.input">input</entry>
								<entry spanname="update.output">output</entry>
							</row>
						</thead>
						<tbody>
							<row>
								<entry spanname="element">
									<xsl:value-of select="@name"/>
								</entry>
								<entry spanname="attribute"/>
								<entry spanname="create.input">
									<xsl:value-of select="xs:annotation/xs:documentation/create/input"/>
								</entry>
								<entry spanname="create.output">
									<xsl:value-of select="xs:annotation/xs:documentation/create/output"/>
								</entry>
								<entry spanname="update.input">
									<xsl:value-of select="xs:annotation/xs:documentation/update/input"/>
								</entry>
								<entry spanname="update.output">
									<xsl:value-of select="xs:annotation/xs:documentation/update/output"/>
								</entry>
							</row>
							<xsl:choose>
								<xsl:when test="@type='common-types:link'">
									<xsl:for-each select="//xs:import">
										<xsl:variable name="commonTypesVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/commontypes/')"/>
										<xsl:if test="$commonTypesVersion">
											<xsl:for-each select="document(concat('../xsd/common/',$commonTypesVersion,'/common-types.xsd'))//xs:complexType/xs:attribute">
												<row>
													<entry spanname="element"/>
													<entry spanname="attribute">
														<xsl:variable name="attrCom" select="./xs:annotation/xs:documentation/comment"/>
														<xsl:choose>
															<xsl:when test="$attrCom">
																<xsl:value-of select="@name|@ref"/> (see note <xsl:value-of select="position()"/>)
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="@name|@ref"/>
															</xsl:otherwise>
														</xsl:choose>
													</entry>
													<entry spanname="create.input">
														<xsl:value-of select="xs:annotation/xs:documentation/create/input"/>
													</entry>
													<entry spanname="create.output">
														<xsl:value-of select="xs:annotation/xs:documentation/create/output"/>
													</entry>
													<entry spanname="update.input">
														<xsl:value-of select="xs:annotation/xs:documentation/update/input"/>
													</entry>
													<entry spanname="update.output">
														<xsl:value-of select="xs:annotation/xs:documentation/update/output"/>
													</entry>
												</row>
											</xsl:for-each>
											<xsl:for-each select="document(concat('../xsd/common/',$commonTypesVersion,'/common-types.xsd'))//xs:attributeGroup[@name='eSciDocResourceLinkAttributes']/xs:attribute">
												<row>
													<entry spanname="element"/>
													<entry spanname="attribute">
														<xsl:variable name="attrCom" select="./xs:annotation/xs:documentation/comment"/>
														<xsl:choose>
															<xsl:when test="$attrCom">
																<xsl:value-of select="@name|@ref"/> (see note <xsl:value-of select="position()"/>)
															</xsl:when>
															<xsl:otherwise>
																<xsl:value-of select="@name|@ref"/>
															</xsl:otherwise>
														</xsl:choose>
													</entry>
													<entry spanname="create.input">
														<xsl:value-of select="xs:annotation/xs:documentation/create/input"/>
													</entry>
													<entry spanname="create.output">
														<xsl:value-of select="xs:annotation/xs:documentation/create/output"/>
													</entry>
													<entry spanname="update.input">
														<xsl:value-of select="xs:annotation/xs:documentation/update/input"/>
													</entry>
													<entry spanname="update.output">
														<xsl:value-of select="xs:annotation/xs:documentation/update/output"/>
													</entry>
												</row>
											</xsl:for-each>
										</xsl:if>
									</xsl:for-each>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="xs:complexType/xs:attributeGroup">
									<xsl:variable name="ref" select="xs:complexType/xs:attributeGroup/@ref"/>
										<xsl:for-each select="//xs:import">
											<xsl:variable name="commonTypesVersion" select="substring-after(@namespace,'/www.escidoc.de/schemas/commontypes/')"/>
											<xsl:if test="$commonTypesVersion">
											
											
												<xsl:for-each select="document(concat('../xsd/common/',$commonTypesVersion,'/common-types.xsd'))//xs:attributeGroup[@name=substring-after($ref,':')]/xs:attribute">
													<row>
														<entry spanname="element"/>
														<entry spanname="attribute">
															<xsl:variable name="attrCom" select="./xs:annotation/xs:documentation/comment"/>
															<xsl:choose>
																<xsl:when test="$attrCom">
																	<xsl:value-of select="@name|@ref"/> (see note <xsl:value-of select="position()"/>)
																</xsl:when>
																<xsl:otherwise>
																	<xsl:value-of select="@name|@ref"/>
																</xsl:otherwise>
															</xsl:choose>
														</entry>
														<entry spanname="create.input">
															<xsl:value-of select="xs:annotation/xs:documentation/create/input"/>
														</entry>
														<entry spanname="create.output">
															<xsl:value-of select="xs:annotation/xs:documentation/create/output"/>
														</entry>
														<entry spanname="update.input">
															<xsl:value-of select="xs:annotation/xs:documentation/update/input"/>
														</entry>
														<entry spanname="update.output">
															<xsl:value-of select="xs:annotation/xs:documentation/update/output"/>
														</entry>
													</row>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<xsl:for-each select="xs:complexType/xs:attribute">
										<row>
											<entry spanname="element"/>
											<entry spanname="attribute">
												<xsl:variable name="attrCom" select="./xs:annotation/xs:documentation/comment"/>
												<xsl:choose>
													<xsl:when test="$attrCom">
														<xsl:value-of select="@name|@ref"/> (see note <xsl:value-of select="position()"/>)
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="@name|@ref"/>
													</xsl:otherwise>
												</xsl:choose>
											</entry>
											<entry spanname="create.input">
												<xsl:value-of select="xs:annotation/xs:documentation/create/input"/>
											</entry>
											<entry spanname="create.output">
												<xsl:value-of select="xs:annotation/xs:documentation/create/output"/>
											</entry>
											<entry spanname="update.input">
												<xsl:value-of select="xs:annotation/xs:documentation/update/input"/>
											</entry>
											<entry spanname="update.output">
												<xsl:value-of select="xs:annotation/xs:documentation/update/output"/>
											</entry>
										</row>
									</xsl:for-each>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:variable name="comment" select="xs:annotation/xs:documentation/comment"/>
							<xsl:variable name="attrComment" select="xs:complexType/xs:attribute/xs:annotation/xs:documentation/comment"/>
							<xsl:choose>
								<xsl:when test="not($comment)"/>
								<xsl:otherwise>
									<row>
										<entry spanname="all">
											<xsl:text> </xsl:text>
										</entry>
									</row>
									<row>
										<entry spanname="all">
											<xsl:for-each select="xs:annotation/xs:documentation/comment">
												<para>
													<xsl:value-of select="."/>
												</para>
											</xsl:for-each>
										</entry>
									</row>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="not($attrComment)"/>
								<xsl:otherwise>
									<row>
										<entry spanname="all">
											<xsl:text> </xsl:text>
										</entry>
									</row>
									<row>
										<entry spanname="all">
											<xsl:for-each select="./xs:complexType/xs:attribute/xs:annotation/xs:documentation/comment">
												<para>
													<xsl:value-of select="position()"/>.
													 <xsl:value-of select="."/>
												</para>
											</xsl:for-each>
										</entry>
									</row>
								</xsl:otherwise>
							</xsl:choose>
						</tbody>
					</tgroup>
				</table>
				<xsl:apply-templates select="./*/*/xs:element">
					<xsl:with-param name="SUB" select="$PATH"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@ref">
				<xsl:variable name="ref" select="@ref"/>
				<xsl:apply-templates select="//xs:element[@name = substring-after($ref,':')]">
					<xsl:with-param name="SUB" select="$SUB"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>fehler</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
