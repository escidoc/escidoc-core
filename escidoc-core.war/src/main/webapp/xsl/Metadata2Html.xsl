<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:item="http://www.escidoc.de/schemas/item/0.10"
    xmlns:co="http://www.escidoc.de/schemas/components/0.9"
    xmlns:context="http://www.escidoc.de/schemas/context/0.7"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>

    <xsl:template match="/md:md-record|/context:admin-descriptor">
        <div class="well">
        <h2><i><xsl:value-of select="@name"/></i> Metadata</h2>
            <xsl:apply-templates mode="view-xml"/>
        <!-- 
            <hr/>
            <xsl:apply-templates mode="edit-xml"/>
        -->
        </div>
    </xsl:template>
    
    <xsl:template match="*" mode="view-xml">
        <xsl:param name="PATH"/>
        <xsl:variable name="ln" select="local-name()"/>
        <xsl:choose>
            <xsl:when test="child::*">
                <xsl:variable name="NAME">
                    <xsl:call-template name="toUpperCase">
                        <xsl:with-param name="s" select="$ln"/>
                    </xsl:call-template>
                </xsl:variable>
                <h4>
                    <xsl:if test="$PATH">
                        <xsl:value-of select="$PATH"/> /
                    </xsl:if>
                    <xsl:value-of select="$NAME"/>
                    <xsl:if test="@rdf:resource"> (<xsl:value-of select="@rdf:resource"/>)</xsl:if>
                </h4>
                <ul>
                    <xsl:apply-templates select="child::*[not(child::*)]" mode="view-xml"/>
                    <xsl:apply-templates select="child::*[child::*]" mode="view-xml"/>
                </ul>
            </xsl:when>
            <!-- xsl:when test="child::*">
                <h4><xsl:value-of select="$PATH"/></h4>
                <xsl:apply-templates mode="view">
                <xsl:with-param name="PATH" select="$PATH"/>
                <xsl:with-param name="PATH" select="concat($PATH,' - ',$NAME)"/>
                </xsl:apply-templates>
                </xsl:when -->
            <xsl:otherwise>
                <xsl:call-template name="toUpperCase">
                    <xsl:with-param name="s" select="substring($ln, 1, 1)"/>
                </xsl:call-template>
                <em><xsl:value-of select="substring($ln,2)"/>: </em><xsl:choose>
                    <xsl:when test="@rdf:resource">
                        <a>
                            <xsl:attribute name="href"><xsl:value-of select="@rdf:resource"
                            /></xsl:attribute>
                            <xsl:value-of select="@rdf:resource"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
                </xsl:choose>
                <br/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="*" mode="edit-xml">
        <xsl:variable name="ln" select="local-name()"/>
        <xsl:variable name="mdHref" select="parent::md-record/@xlink:href"/>
        <script type="text/javascript">
            function save(){
                var text = document.getElementById('mdtextarea').value;
                alert(text);
                
                // make PUT request
                var url = document.location.href;
                var body = text;

                var http = new XMLHttpRequest();
                http.open('PUT', url);
                http.send(body);
            }
        </script>
        <textarea id="mdtextarea" rows="25" cols="80" onclick="document.getElementById('mdsavebutton').style.visibility = 'visible'; return true;">
            <xsl:copy-of select="."/>
        </textarea>
        <br/>
        <button id="mdsavebutton" style="visibility: hidden;" onclick="save();"> Save </button>
    </xsl:template>

</xsl:stylesheet>
