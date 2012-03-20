<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:srw="http://www.loc.gov/zing/srw/"
    xmlns:context="http://www.escidoc.de/schemas/context/0.7"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>

    <xsl:template match="context:context">
        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span8">
                    <div class="hero-unit" id="titlePanel">
                        <h1>Workspace: <xsl:value-of select="@xlink:title"/></h1>
                        <p>
                            <xsl:value-of select="*/prop:description"/>
                        </p>

                        <p>This Workspace is <xsl:value-of select="*/prop:public-status"
                                /> 
            and belongs to the organizational units 
            <xsl:for-each
                                select="*/prop:organizational-units/*">
                                <xsl:text> </xsl:text>
                                <xsl:call-template name="display">
                                    <xsl:with-param name="n" select="."/>
                                </xsl:call-template>
                            </xsl:for-each>.</p>
                    </div>


                    <div id="contentPanel">

                        <xsl:if
                            test="*/*[local-name()='admin-descriptor' and @name = 'org.escidoc']">
                            <iframe style="width:100%; height:200px; " frameborder="0">
                                <xsl:attribute name="src">
                                    <xsl:value-of
                                        select="*/*[local-name()='admin-descriptor' and @name = 'org.escidoc']/config/context/include"
                                    />
                                </xsl:attribute>
                                <xsl:attribute name="type">text/html</xsl:attribute>
                            </iframe>
                        </xsl:if>

                        <div class="containers">
                            <h4>Direct Members</h4>
                            <xsl:variable name="memberURL"><xsl:value-of select="/*/@xml:base"
                                    /><xsl:value-of select="/*/@xlink:href"
                                />/resources/members?maximumRecords=200&amp;query=top-level-containers=true</xsl:variable>
                            <xsl:variable name="memberResult" select="document($memberURL)"/>
                            <xsl:apply-templates
                                select="$memberResult/srw:searchRetrieveResponse/srw:records/srw:record">
                                <xsl:sort select="srw:recordPosition" data-type="number"
                                    order="ascending"/>
                            </xsl:apply-templates>
                        </div>
                        <div class="items">
                            <h4>Orphaned Items</h4>
                            <xsl:variable name="memberURL"><xsl:value-of select="/*/@xml:base"
                                    /><xsl:value-of select="/*/@xlink:href"
                                />/resources/members?maximumRecords=200&amp;query=top-level-items=true</xsl:variable>
                            <xsl:variable name="memberResult" select="document($memberURL)"/>
                            <xsl:apply-templates
                                select="$memberResult/srw:searchRetrieveResponse/srw:records/srw:record">
                                <xsl:sort select="srw:recordPosition" data-type="number"
                                    order="ascending"/>
                            </xsl:apply-templates>
                        </div>
                    </div>
                </div>

                <div id="subresourcePanel" class="span4">

                    <div id="accordion" class="hero-unit padding-min">
                        <div>
                            <h6>Properties</h6>
                        </div>
                        <div class="accordion-inner"
                            >
                            Type: <i><xsl:value-of select="context:properties/prop:type"/></i><br/>
                            State: <i><xsl:value-of select="context:properties/prop:public-status"/></i><br/>
                            Created by <xsl:call-template name="displayLink"><xsl:with-param name="n" select="./context:properties/srel:created-by"/></xsl:call-template> at <xsl:value-of select="./context:properties/prop:creation-date"/>.
                        </div>
                        <xsl:apply-templates select="md:admin-descriptors"/>
                        <div>
                            <h6>Relations <i class="icon-plus-sign"
                                    style="position:absolute; right:35px"
                                    onClick="alert('add something')"/></h6>
                        </div>
                        <div id="relationsPanel" class="accordion-inner"
                            >
                    Not implemented yet.
                    <!-- xsl:apply-templates select="*[local-name() = 'relations']"/ -->
                        </div>
                    </div>

                    <ul class="nav nav-list hero-unit padding-min">
                        <li class="nav-header">Subressources Technically</li>
                        <xsl:for-each select="*[@xlink:href]">
                            <li>
                                <xsl:call-template name="displayLink">
                                    <xsl:with-param name="n" select="."/>
                                </xsl:call-template>
                            </li>
                        </xsl:for-each>
                    </ul>

                </div>
            </div>
        </div>
    </xsl:template>
    
    
    

</xsl:stylesheet>
