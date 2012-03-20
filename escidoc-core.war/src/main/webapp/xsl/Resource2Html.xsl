<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:version="http://escidoc.de/core/01/properties/version/" 
    xmlns:release="http://escidoc.de/core/01/properties/release/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:context="http://www.escidoc.de/schemas/context/0.7"
    xmlns:grants="http://www.escidoc.de/schemas/grants/0.5"
    xmlns:ua="http://www.escidoc.de/schemas/useraccount/0.7"
    xmlns:ev="http://www.escidoc.de/schemas/versionhistory/0.3"
    xmlns:sm="http://www.escidoc.de/schemas/structmap/0.4"
    xmlns:co="http://www.escidoc.de/schemas/components/0.9"
    xmlns:pol="urn:oasis:names:tc:xacml:1.0:policy"
    xmlns:role="http://www.escidoc.de/schemas/role/0.5"
    xmlns:premis="http://www.loc.gov/standards/premis/v1"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>
    
    <xsl:include href="Metadata2Html.xsl"/>
    <xsl:include href="Item2Html.xsl"/>
    <xsl:include href="Context2Html.xsl"/>
    <xsl:include href="User2Html.xsl"/>
    <xsl:include href="SearchResponse2Html.xsl"/>
    
    <xsl:variable name="repositoryInfo" select="document('/adm/admin/get-repository-info')/properties"/>

    <!-- 
    <xsl:param name="LOGO">https://www.escidoc.org/jira/s/de_DEnbckeb/649/8/_/jira-logo-scaled.png</xsl:param>
    <xsl:if test="$LOGO">
        <img style="float:left;">
            <xsl:attribute name="src">
                <xsl:value-of select="$LOGO"/>
            </xsl:attribute>
        </img>
    </xsl:if>
    -->
    
    <xsl:template match="/">
        <html>
            <head>
                <title>eSciDoc<xsl:if test="*/@xlink:title != ''"> - <xsl:value-of
                            select="*/@xlink:title"/></xsl:if><xsl:if test="*/@xlink:href"
                            > - <xsl:value-of select="*/@xlink:href"/></xsl:if></title>
                <script src="http://code.jquery.com/jquery-1.7.1.js"></script>
                <script src="http://code.jquery.com/ui/1.8.17/jquery-ui.min.js"></script>

                <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
                <link href="/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet"/>
                <style type="text/css">
                    body {
                        padding-top: 60px;
                        padding-bottom: 40px;
                    }
                    footer {
                        border-top: 1px solid lightgray;
                        text-align: center;
                    }
                    .padding-min {
                        padding: 5px;
                    }
                    h6 {
                        border-bottom:1px solid grey;
                    }
                </style>
                <script src="/bootstrap/js/bootstrap.min.js"></script>

                <script>
                    $(function() {
                        $( "#accordion" ).accordion();
                    });
                </script>
            </head>
            <body>
                <div class="navbar navbar-fixed-top">
                    <div class="navbar-inner">
                        <div class="container-fluid">
                            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                                <span class="icon-bar"></span>
                            </a>
                            <a class="brand" href="#">eSciDoc Infrastructure</a>
                            <div class="nav-collapse">
                                <ul class="nav">
                                    <xsl:if test="/*/*/srel:context">
                                        <li><a>
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="/*/@xml:base"/>
                                                <xsl:value-of select="/*/*/srel:context/@xlink:href"/>
                                            </xsl:attribute>Workspace</a></li>
                                    </xsl:if>
                                    <li><a href="http://www.escidoc.org">eSciDoc Website</a></li>
                                    <li><a>
                                        <xsl:attribute name="href">mailto:<xsl:value-of select="$repositoryInfo/entry[@key = 'escidoc-core.admin-email']/text()"/>
                                        </xsl:attribute>Contact</a></li>
                                </ul>
                                <p class="navbar-text pull-right">
                                    <xsl:variable name="currentUserURL"><xsl:value-of select="/*/xml:base"/>/aa/user-account/current</xsl:variable>
                                    <xsl:variable name="USERACCOUNT" select="document($currentUserURL)"/>
                                    <xsl:choose>
                                        <xsl:when test="$USERACCOUNT/ua:user-account">
                                            <a href="/aa/user-account/current"><xsl:value-of select="$USERACCOUNT/ua:user-account/ua:properties/prop:name"/></a>
                                            <xsl:text> | </xsl:text>
                                            <a>
                                                <xsl:attribute name="href">/aa/logout?target=<xsl:value-of select="/*/@xml:base"/>
                                                    <xsl:value-of select="/*/@xlink:href"/></xsl:attribute>Logout</a>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <a>
                                                <xsl:attribute name="href">/aa/login?target=<xsl:value-of select="/*/@xml:base"/>
                                                    <xsl:value-of select="/*/@xlink:href"/></xsl:attribute>Login</a></xsl:otherwise>
                                    </xsl:choose>

                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <xsl:apply-templates/>
                
                <footer>eSciDoc Infrastructure Default View by <a href="http://www.fiz-karlsruhe.de">FIZ Karlsruhe</a></footer>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="*">
        <xsl:variable name="theBase">
            <xsl:value-of select="@xml:base"/>
        </xsl:variable>
        <!-- Base-URL is <xsl:value-of select="$theBase"/> -->
        
        <div class="container-fluid">
            <div class="row-fluid">
	           <div class="span8">
	               <div class="hero-unit" id="titlePanel">
	                   
        <h1 id="title">
            <xsl:value-of select="@xlink:title"/>
            <xsl:if test="parent::*">
			(
			<xsl:value-of select="local-name()"/>
            <xsl:choose>
                <xsl:when test="@objid">
					-
					<xsl:value-of select="@objid"/>
                </xsl:when>
                <xsl:when test="@name">
					-
					<xsl:value-of select="@name"/>
                </xsl:when>
                <xsl:when test="@id">
					-
					<xsl:value-of select="@id"/>
                </xsl:when>
            </xsl:choose>
			)
			</xsl:if>
        </h1>
        
        <p id="description">
            <xsl:if test="*/srel:content-model">
                is a <em><a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="/*/@xml:base"/>
                        <xsl:value-of select="*/srel:content-model/@xlink:href"/>
                    </xsl:attribute>
                    <xsl:value-of select="*/srel:content-model/@xlink:title"/>
                </a></em>
                <br/>
            </xsl:if>
            <xsl:apply-templates select="*/prop:login-name"/>
            <xsl:if test="*/prop:description">
                <xsl:value-of select="*/prop:description"/>
                <br/>
            </xsl:if>
            <xsl:apply-templates select="*/prop:active"/>
        </p>        
        
        </div>

	    <div id="contentPanel">
	        <!-- for grant -->
	        <xsl:apply-templates select="grants:properties" mode="content"/>
	        <!-- for role -->
	        <xsl:apply-templates select="role:scope|pol:Policy"/>
            <xsl:apply-templates select="sm:struct-map|co:components|ua:resources"/>
        </div>
        
	           </div>
                
        <div class="span4" id="subresourcePanel">
            <xsl:if test="*[local-name() = 'properties']">
            <div id="accordion" class="hero-unit padding-min">
                <div><h6>Properties</h6></div>
                <div id="propertiesPanel" class="accordion-inner">
                    
                    <!-- Display information from Context. -->
                    <xsl:if test="*/srel:context">
                        <xsl:variable name="CONTEXT" select="*/srel:context/@xlink:href"/>
                        <xsl:variable name="CONTEXT_HREF"><xsl:value-of select="$theBase"/><xsl:value-of select="$CONTEXT"/></xsl:variable>
                        <xsl:variable name="AD" select="document($CONTEXT_HREF)/*/*/*[@name='org.escidoc']"/>
                        <xsl:if test="$AD/config/apps/app[1]">
                            <div class="relatedApps">
                                <span class="primary">
                                    <a>
                                        <xsl:attribute name="href">
                                            <xsl:call-template name="appUriReplacements">
                                                <xsl:with-param name="uri" select="$AD/config/apps/app[1]"/>
                                                <xsl:with-param name="uriBase" select="$theBase"/>
                                            </xsl:call-template>
                                        </xsl:attribute>Open in related Application</a>
                                </span>
                                <!--
                                <span class="alternatives">
                                    <xsl:if test="$AD/config/apps/app[2]">
                                        <br/>Alternative applications:
                                        <xsl:variable name="altAppUri">
                                            <xsl:call-template name="appUriReplacements">
                                                <xsl:with-param name="uri" select="$AD/config/apps/app[2]"/>
                                                <xsl:with-param name="uriBase" select="$theBase"/>
                                            </xsl:call-template>
                                        </xsl:variable>
                                        <span class="entry">
                                            <a>
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="$altAppUri"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$altAppUri"/>
                                            </a>
                                        </span>
                                        <xsl:for-each select="$AD/config/apps/app[position() > 2]">, 
                                            <xsl:variable name="altAppUri2">
                                                <xsl:call-template name="appUriReplacements">
                                                    <xsl:with-param name="uri" select="."/>
                                                    <xsl:with-param name="uriBase" select="$theBase"/>
                                                </xsl:call-template>
                                            </xsl:variable>
                                            <span class="entry">
                                                <a>
                                                    <xsl:attribute name="href">
                                                        <xsl:value-of select="$altAppUri2"/>
                                                    </xsl:attribute>
                                                    <xsl:value-of select="$altAppUri2"/>
                                                </a>
                                            </span>
                                        </xsl:for-each>
                                    </xsl:if>
                                </span>
                                -->
                            </div>
                        </xsl:if>
                    </xsl:if>
                    
                    <!-- Display version information. -->
                    <xsl:if test="./*/prop:version">
                        <div class="timestamps">
                            <span>This version was modified at
                                <span id="version"><xsl:value-of select="./*/*[local-name() = 'version']/*[local-name() = 'date']" /></span>.
                            </span>
                            
                            <xsl:if test="./*/*[local-name() = 'version']/*[local-name() = 'number']/text() = ./*/*[local-name() = 'latest-version']/*[local-name() = 'number']/text()">
                                <span>It is the latest available version.</span>
                            </xsl:if>
                            
                            <xsl:if test="./*/*[local-name() = 'latest-release']">
                                <xsl:if test="not(./*/*[local-name() = 'latest-release']/*[local-name() = 'number']/text() = ./*/*[local-name() = 'version']/*[local-name() = 'number']/text())">
                                    <!-- not the latest release is shown -->
                                    <span>Latest public version is of
                                        <a>
                                            <xsl:attribute name="href">
                                                <xsl:value-of select="$theBase"/>
                                                <xsl:value-of select="./*/*[local-name() = 'latest-release']/@xlink:href"/>
                                            </xsl:attribute>
                                            <span id="release"><xsl:value-of select="./*/*[local-name() = 'latest-release']/*[local-name() = 'date']"/></span>
                                        </a>.</span>
                                </xsl:if>
                            </xsl:if>
                        </div>
                        
                    </xsl:if>
                    
                    <xsl:if test="./*/srel:created-by">
                        <div class="creation">
                            Created by <xsl:call-template name="displayLink"><xsl:with-param name="n" select="./*/srel:created-by"/></xsl:call-template> at <xsl:value-of select="./*/prop:creation-date"/>.
                        </div>
                    </xsl:if>
                    
                    <xsl:apply-templates select="*/prop:active"/>
                    <xsl:if test="/*[local-name() = 'container'] or /*[local-name() = 'item']">
                        <div id="history">
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of select="/*/@xml:base"/>
                                    <xsl:value-of select="/*/@xlink:href"/>/resources/version-history</xsl:attribute>
                                <xsl:text>History</xsl:text>
                            </a>
                        </div>
                    </xsl:if>
                </div>
                <xsl:apply-templates select="md:md-records"/>
                <div><h6>Relations <!-- i class="icon-plus-sign" style="position:absolute; right:35px" onClick="alert('add something')"></i --></h6></div>
                <div id="relationsPanel" class="accordion-inner">
                    Not implemented yet.
                    <!-- xsl:apply-templates select="*[local-name() = 'relations']"/ -->
                </div>
            </div>
            </xsl:if>
                
            <xsl:if test="/*[local-name() = 'container'] or /*[local-name() = 'item']">
                <xsl:call-template name="version-history"/>
            </xsl:if>
            
            <ul class="nav nav-list hero-unit padding-min">
                <li class="nav-header">Subressources Technically</li>
                <xsl:for-each select="*[@xlink:href]">
                    <li><xsl:call-template name="displayLink"><xsl:with-param name="n" select="."/></xsl:call-template></li>
                </xsl:for-each>
            </ul>
        </div>
        <!-- 
        <ul>
            <xsl:for-each select="./*[@xlink:href]">
                <xsl:call-template name="hrefedListItem"/>
            </xsl:for-each>
        </ul>
        
        <xsl:for-each select="./*[not(@xlink:href)]">
            <xsl:call-template name="keynval"/>
        </xsl:for-each>
        -->
            </div>
        </div>
        
    </xsl:template>
    
    <xsl:template match="grants:properties" mode="content">
        <div class="hero-unit padding-min">
            <p><xsl:call-template name="displayLink">
                <xsl:with-param name="n" select="srel:granted-to"/>
            </xsl:call-template> has role <xsl:call-template name="displayLink">
                <xsl:with-param name="n" select="srel:role"/>
            </xsl:call-template>
                <xsl:if test="srel:assigned-on">
                    on <xsl:call-template name="displayLink"><xsl:with-param name="n" select="srel:assigned-on"></xsl:with-param></xsl:call-template>
                </xsl:if>.</p>
            </div>        
    </xsl:template>
    
    <xsl:template name="version-history">
        <xsl:variable name="curV" select="/*/*[local-name() = 'properties']/prop:version/*[local-name() = 'number']/text()"/>
        <xsl:variable name="vhurl"><xsl:value-of select="/*/@xml:base"/><xsl:value-of select="*[local-name()='resources']/@xlink:href"/>/version-history</xsl:variable>
        <xsl:variable name="VH" select="document($vhurl)/ev:version-history"></xsl:variable>
        <xsl:apply-templates select="document($vhurl)/ev:version-history"><xsl:with-param name="curVersion" select="$curV"></xsl:with-param></xsl:apply-templates>        
    </xsl:template>
    
    <xsl:template match="ev:version-history">
        <xsl:param name="curVersion"/>
        <div class="hero-unit padding-min">
        <h6><i class="icon-chevron-down" data-toggle="collapse" data-target="#allVersions"></i>Versions</h6>
        <div class="collapse" id="allVersions">
        <ul class="nav nav-list">
            <xsl:for-each select="ev:version">
                <li>
                    <xsl:if test="ev:version-number = $curVersion">
                        <xsl:attribute name="class">active</xsl:attribute>
                    </xsl:if>
                    <i class="icon-chevron-down" data-toggle="collapse" style="float:left;position:relative; top:5px;">
                        <xsl:attribute name="data-target">#v<xsl:value-of select="ev:version-number"/></xsl:attribute>
                    </i>
                    <xsl:call-template name="displayLink"><xsl:with-param name="n" select="."></xsl:with-param></xsl:call-template>
                </li>
                <ul class="collapse">
                    <xsl:attribute name="id">v<xsl:value-of select="ev:version-number"/></xsl:attribute>
                    <xsl:value-of select="ev:comment"/> (<xsl:value-of select="@timestamp"/>).<br/>
                    <xsl:apply-templates select="ev:events/premis:event"/>
                </ul>
            </xsl:for-each>
        </ul>
        </div>
        </div>
                
    </xsl:template>
    
    <xsl:template match="premis:event">
        <xsl:value-of select="premis:eventType"/> by <xsl:call-template name="displayLink"><xsl:with-param name="n" select="premis:linkingAgentIdentifier"></xsl:with-param></xsl:call-template>.<br/>
    </xsl:template>
    
    <xsl:template match="md:md-records">
        <div><h6>Metadata <!-- i class="icon-plus-sign" style="position:absolute; right:35px" onClick="alert('add something')"></i --></h6></div>
        <div id="metadataPanel" class="accordion-inner">
            <ul>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>
    
    <xsl:template match="context:admin-descriptors">
        <div><h6>Admin Descriptors <!-- i class="icon-plus-sign" style="position:absolute; right:35px" onClick="alert('add something')"></i --></h6></div>
        <div id="admindescriptorPanel" class="accordion-inner">
            <ul>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>
    
    <xsl:template match="md:md-record|context:admin-descriptor|srel:container|srel:item">
        <xsl:call-template name="hrefedListItem"/>
    </xsl:template>
    
    <xsl:template match="sm:struct-map">
        <div class="members">
            <div class="header">Direct Members</div>
            <ul>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>
    
    <xsl:template match="co:component">
        <xsl:variable name="ID">c<xsl:value-of select="position()"/></xsl:variable>
        <xsl:for-each select="co:content">
            <xsl:call-template name="hrefedListItem"><xsl:with-param name="toggleId"><xsl:value-of select="$ID"/></xsl:with-param></xsl:call-template>
        </xsl:for-each>
        <ul class="collapse">
            <xsl:attribute name="id"><xsl:value-of select="$ID"/></xsl:attribute>
            <a>
                <xsl:attribute name="href"><xsl:value-of select="co:content/@xlink:href"/></xsl:attribute>
                Download
            </a><br/>
            Content Category: <xsl:value-of select="*/prop:content-category"/><br/>
            Mime Type: <xsl:value-of select="*/prop:mime-type"/><br/>
            Checksum: <xsl:call-template name="toLowerCase"><xsl:with-param name="s" select="*/prop:checksum-algorithm"/></xsl:call-template>:<xsl:value-of select="*/prop:checksum"/><br/>
            Visibility: <xsl:value-of select="*/prop:visibility"/><br/>
            <xsl:apply-templates select="md:md-records/md:md-record"/>
        </ul>
    </xsl:template>
    
    <xsl:template match="co:components">
        <div class="data">
            <xsl:variable name="thumb" select="co:component[starts-with(co:properties/prop:content-category,'thumb') or starts-with(co:properties/prop:content-category,'Thumb') or starts-with(co:properties/prop:content-category,'THUMB')]/co:properties/prop:content-category/text()"></xsl:variable>
            <xsl:if test="$thumb">
                <div class="hero-unit padding-min" style="float:right; text-align:center;">
                    <img style="float:left;">
                        <xsl:attribute name="src">
                            <xsl:value-of select="/*/@xml:base"/>
                            <xsl:value-of select="co:component[co:properties/prop:content-category = $thumb]/co:content/@xlink:href"/>
                        </xsl:attribute>
                        <xsl:attribute name="alt">Thumbnail</xsl:attribute>
                    </img><br/>
                    <xsl:value-of select="co:component[co:properties/prop:content-category = $thumb]/co:content/@xlink:title"/>
                </div>
            </xsl:if>
            
            <ul class="nav nav-list hero-unit padding-min">
                <li class="nav-header">Files</li>
                <xsl:apply-templates/>
            </ul>
        </div>
    </xsl:template>

    <xsl:template name="hrefedListItem">
        <xsl:param name="toggleId"/>
        <xsl:variable name="contentLink" select="@xlink:href"/>
        <li>
            <xsl:if test="$toggleId">
                <i class="icon-chevron-down" style="float:left;position:relative; top:5px;">
                    <xsl:attribute name="data-toggle">collapse</xsl:attribute>
                    <xsl:attribute name="data-target">#<xsl:value-of select="$toggleId"/></xsl:attribute>
                </i>
            </xsl:if>
            <a>
                <xsl:attribute name="href">
                    <xsl:if test="not(starts-with(@xlink:href, 'http'))">
                        <xsl:value-of select="/*/@xml:base"/>
                    </xsl:if>
                    <xsl:value-of select="@xlink:href"/>
                </xsl:attribute>
                
                <xsl:if test="@predicate">
                    <xsl:value-of select="@predicate"/>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@xlink:title">
                        <xsl:value-of
                            select="@xlink:title"/>
                    </xsl:when>
                    <xsl:when test="@objid">
                        <xsl:value-of select="@objid"/>
                    </xsl:when>
                    <xsl:when test="@name">
                        <xsl:value-of select="@name"/>
                    </xsl:when>
                    <xsl:when test="@id">
                        <xsl:value-of select="@id"/>
                    </xsl:when>
                </xsl:choose>
            </a>
            
        </li>
        
    </xsl:template>

    <xsl:template name="appUriReplacements">
        <xsl:param name="uri"/>
        <xsl:param name="uriBase"/>
    <xsl:call-template name="replace">
        <xsl:with-param name="string">
            <xsl:call-template name="replace">
                <xsl:with-param name="string">
                    <xsl:call-template name="replace">
                        <xsl:with-param name="string">
                            <xsl:call-template name="replace">
                                <xsl:with-param name="string">
                                    <xsl:value-of select="$uri"/>
                                </xsl:with-param>
                                <xsl:with-param name="pattern">${objid}</xsl:with-param>
                                <xsl:with-param name="replace"><xsl:value-of select="substring-after(@xlink:href, concat(local-name(.),'/'))"/></xsl:with-param>
                            </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="pattern">${resourcetype}</xsl:with-param>
                        <xsl:with-param name="replace"><xsl:call-template name="toUpperCase"><xsl:with-param name="s"><xsl:value-of select="local-name(.)"/></xsl:with-param></xsl:call-template></xsl:with-param>
                    </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="pattern">${baseuri}</xsl:with-param>
                <xsl:with-param name="replace"><xsl:value-of select="$uriBase"/></xsl:with-param>
            </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="pattern">${uri}</xsl:with-param>
        <xsl:with-param name="replace"><xsl:value-of select="$uriBase"/><xsl:value-of select="@xlink:href"/></xsl:with-param>
    </xsl:call-template>
    </xsl:template>
        
    <xsl:template name="replace">
        <xsl:param name="string"/>
        <xsl:param name="pattern"/>
        <xsl:param name="replace"/>
        <xsl:choose>
            <xsl:when test="contains($string, $pattern)">
                <xsl:value-of select="substring-before($string,$pattern)"/><xsl:value-of select="$replace"/><xsl:value-of select="substring-after($string,$pattern)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$string"/>
            </xsl:otherwise>
        </xsl:choose>
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

    <xsl:template match="exception">
        <h1>
            <xsl:value-of select="title"/>
        </h1>
        <blockquote>
            <P>
                <xsl:value-of select="class"/>
            </P>
            <xsl:variable name="UID">message<xsl:value-of select="count(ancestor::*)"
                /></xsl:variable>
            <P>
                <xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID"
                    />');</xsl:attribute>
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

            <xsl:variable name="UID2">stacktrace<xsl:value-of select="count(ancestor::*)"
                /></xsl:variable>
            <P>
                <xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID2"
                    />');</xsl:attribute>
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
                    <xsl:attribute name="onClick">toggleVisibility('<xsl:value-of select="$UID3"
                        />');</xsl:attribute>
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

    <xsl:template name="keynval">
        <ul>
            <b>
                <xsl:value-of select="local-name()"/>
				:
			</b>
            <xsl:choose>
                <xsl:when test="@xlink:href">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="@xlink:href"/>
                        </xsl:attribute>
                        <xsl:value-of select="@xlink:href"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="./text()"/>
                    <xsl:for-each select="@*">
                        <xsl:text> @</xsl:text>
                        <b>
                            <xsl:value-of select="local-name()"/>
                        </b>
                        <xsl:text>=</xsl:text>
                        <xsl:value-of select="."/>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
            <br/>
            <xsl:for-each select="./*">
                <xsl:call-template name="keynval"/>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template name="display">
        <xsl:param name="n"/>
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="@xlink:href"/>
            </xsl:attribute>
            <xsl:value-of select="@xlink:title"/>
        </a>
    </xsl:template>
    
    <xsl:template name="toUpperCase">
        <xsl:param name="s"/>
        <xsl:value-of
            select="translate($s,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
    </xsl:template>
    
    <xsl:template name="toLowerCase">
        <xsl:param name="s"/>
        <xsl:value-of
            select="translate($s,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    </xsl:template>

</xsl:stylesheet>
