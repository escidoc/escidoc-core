<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" />

    <xsl:param name="schemaLocationBase"/>
	<xsl:variable name="mumNote" as="xs:integer">0</xsl:variable>

	<xsl:template match="xs:element">
		<xsl:param name="SUB" />
		<xsl:choose>
			<xsl:when test="@name">
				<xsl:variable name="PATH">
					<xsl:value-of select="$SUB" />/<xsl:value-of select="@name" />
				</xsl:variable>
				<table frame="all" border="1">
					<title>
						<xsl:text>Element &lt;</xsl:text>
						<xsl:value-of select="@name|@ref" />
						<xsl:text>&gt;</xsl:text>
					</title>
					<tgroup cols="12" align="left" colsep="1"
						rowsep="1">
						<colspec colname="c1" />
						<colspec colname="c2" />
						<colspec colname="c3" />
						<colspec colname="c4" />
						<colspec colname="c5" />
						<colspec colname="c6" />
						<colspec colname="c7" />
						<colspec colname="c8" />
						<colspec colname="c9" />
						<colspec colname="c10" />
						<colspec colname="c11" />
						<colspec colname="c12" />
						<spanspec spanname="all" namest="c1"
							nameend="c12" align="left" />
						<spanspec spanname="element" namest="c1"
							nameend="c2" align="left" />
						<spanspec spanname="attribute" namest="c3"
							nameend="c4" align="left" />
						<spanspec spanname="create" namest="c5"
							nameend="c8" align="center" />
						<spanspec spanname="update" namest="c9"
							nameend="c12" align="center" />
						<thead>
							<row>
								<entry spanname="all">
									<xsl:value-of select="$PATH" />
								</entry>
							</row>
							<row>
								<entry spanname="all">
									<xsl:text></xsl:text>
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
						<tbody>
							<row>
								<entry spanname="element">
									<xsl:value-of select="@name" />
								</entry>
								<entry spanname="attribute" />
								<entry spanname="create">
									<xsl:value-of
										select="xs:annotation/xs:documentation/create" />
								</entry>
								<entry spanname="update">
									<xsl:value-of
										select="xs:annotation/xs:documentation/update" />
								</entry>
							</row>
							<xsl:choose>
								<!-- 
									Handle elements of type linkRequired, link, linkForCreate or readOnlyLink defined in common-types.xsd
								-->
								<xsl:when
									test="@type='common-types:linkRequired' or @type='common-types:link' or @type='common-types:linkForCreate' or @type='common-types:readOnlyLink'">
									<xsl:variable name="typeName"
										select="substring-after(@type,'common-types:')" />
									<xsl:for-each
										select="//xs:import">
										<xsl:variable
											name="commonTypesVersion"
											select="substring-after(@namespace,'/www.escidoc.de/schemas/commontypes/')" />
										<xsl:if
											test="$commonTypesVersion">
											<xsl:variable
												name="commonTypesPath"
												select="substring-after(@schemaLocation,$schemaLocationBase)" />
											<!-- select the addressed complex type -->
											<xsl:for-each
                        select="document(concat('../../../../../common/src/main/xsd/',$commonTypesPath))//xs:complexType[@name=$typeName]">
													<!-- Handle attribute groups of complex type -->
													<xsl:for-each
														select="./xs:attributeGroup">
														<xsl:variable
															name="attrGroupName"
															select="substring-after(@ref,'common:')" />
														<!-- Select the appropriate attribute group -->
														<xsl:for-each
															select="document(concat('../../../../../common/src/main/xsd/',$commonTypesPath))//xs:attributeGroup[@name=$attrGroupName]">
															<!-- Iterate over each attribute of the selected group -->
														    <xsl:for-each
																select="./xs:attribute">
																<row>
																	<entry spanname="element" />
																	<entry spanname="attribute">
																		<xsl:apply-templates select="@name|@ref">
																			<xsl:with-param name="attrCom" select="./xs:annotation/xs:documentation/comment" />
																		</xsl:apply-templates>
																	</entry>
																	<entry spanname="create">
																		<xsl:value-of select="xs:annotation/xs:documentation/create" />
																	</entry>
																	<entry spanname="update">
																		<xsl:value-of select="xs:annotation/xs:documentation/update" />
																	</entry>
																</row>
															</xsl:for-each>
														</xsl:for-each>
													</xsl:for-each>

													<!-- Handle all attributes of the selected complex type -->
													<xsl:for-each
														select="./xs:attribute">
														<row>
															<entry spanname="element" />
															<entry spanname="attribute">
																<xsl:apply-templates select="@name|@ref">
																	<xsl:with-param name="attrCom" select="./xs:annotation/xs:documentation/comment" />
																</xsl:apply-templates>
															</entry>
															<entry spanname="create">
																<xsl:value-of select="xs:annotation/xs:documentation/create" />
															</entry>
															<entry spanname="update">
																<xsl:value-of select="xs:annotation/xs:documentation/update" />
															</entry>
														</row>
													</xsl:for-each>
											</xsl:for-each>
										</xsl:if>
									</xsl:for-each>
								</xsl:when>

								<!-- 
									Handle all other elements
								-->
								<xsl:otherwise>
									<xsl:if
										test="xs:complexType/xs:attributeGroup">
										<xsl:variable name="ref"
											select="xs:complexType/xs:attributeGroup/@ref" />
										<xsl:for-each
											select="//xs:import">
											<xsl:variable
												name="commonTypesVersion"
												select="substring-after(@namespace,'/www.escidoc.de/schemas/commontypes/')" />
											<xsl:if
												test="$commonTypesVersion">
												<xsl:variable
													name="commonTypesPath"
													select="substring-after(@schemaLocation,$schemaLocationBase)" />
												<xsl:for-each
													select="document(concat('../../../../../common/src/main/xsd/',$commonTypesPath))//xs:attributeGroup[@name=substring-after($ref,':')]/xs:attribute">
													<row>
														<entry
															spanname="element" />
														<entry
															spanname="attribute">
															<xsl:apply-templates
																select="@name|@ref">
																<xsl:with-param
																	name="attrCom"
																	select="./xs:annotation/xs:documentation/comment" />
															</xsl:apply-templates>
														</entry>
														<entry
															spanname="create">
															<xsl:value-of
																select="xs:annotation/xs:documentation/create" />
														</entry>
														<entry
															spanname="update">
															<xsl:value-of
																select="xs:annotation/xs:documentation/update" />
														</entry>
													</row>
												</xsl:for-each>
											</xsl:if>
										</xsl:for-each>
									</xsl:if>
									<xsl:for-each
										select="xs:complexType/xs:attribute">
										<row>
											<entry spanname="element" />
											<entry
												spanname="attribute">
												<xsl:apply-templates
													select="@name|@ref">
													<xsl:with-param
														name="attrCom"
														select="./xs:annotation/xs:documentation/comment" />
												</xsl:apply-templates>
											</entry>

											<entry spanname="create">
												<xsl:value-of
													select="xs:annotation/xs:documentation/create" />
											</entry>
											<entry spanname="update">
												<xsl:value-of
													select="xs:annotation/xs:documentation/update" />
											</entry>
										</row>
									</xsl:for-each>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:variable name="comment"
								select="xs:annotation/xs:documentation/comment" />
							<xsl:variable name="attrComment"
								select="xs:complexType/xs:attribute/xs:annotation/xs:documentation/comment" />
							<xsl:choose>
								<xsl:when test="not($comment)" />
								<xsl:otherwise>
									<row>
										<entry spanname="all">
											<xsl:text></xsl:text>
										</entry>
									</row>
									<row>
										<entry spanname="all">
											<xsl:for-each
												select="xs:annotation/xs:documentation/comment">
												<para>
													<xsl:value-of
														select="." />
												</para>
											</xsl:for-each>
										</entry>
									</row>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:choose>
								<xsl:when test="not($attrComment)" />
								<xsl:otherwise>
									<row>
										<entry spanname="all">
											<xsl:text></xsl:text>
										</entry>
									</row>
									<row>
										<entry spanname="all">
											<xsl:for-each
												select="./xs:complexType/xs:attribute/xs:annotation/xs:documentation/comment">
												<para>
													<!-- 													<xsl:value-of select="position()"/>
													-->
													<xsl:value-of
														select="." />
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
					<xsl:with-param name="SUB" select="$PATH" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@ref">
				<xsl:variable name="ref" select="@ref" />
				<xsl:apply-templates
					select="//xs:element[@name = substring-after($ref,':')]">
					<xsl:with-param name="SUB" select="$SUB" />
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>fehler</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- attribute entry -->
	<xsl:template match="@name|@ref">
		<xsl:param name="attrCom" />
		<xsl:value-of select="." />
		<!-- 
			<xsl:choose>
			<xsl:when test="$attrCom">
			<xsl:value-of select="."/> (see note <xsl:value-of select="position()"/>)
			</xsl:when>
			<xsl:otherwise>
			<xsl:value-of select="."/>
			</xsl:otherwise>
			</xsl:choose>
		-->
	</xsl:template>

</xsl:stylesheet>
