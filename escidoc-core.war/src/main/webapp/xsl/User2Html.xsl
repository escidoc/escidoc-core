<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:srw="http://www.loc.gov/zing/srw/"
    xmlns:sr="http://www.escidoc.de/schemas/searchresult/0.8"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:ua="http://www.escidoc.de/schemas/useraccount/0.7"
    xmlns:pref="http://www.escidoc.de/schemas/preferences/0.1"
    xmlns:context="http://www.escidoc.de/schemas/context/0.7"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>

<!-- 
    <xsl:template match="ua:user-account">
        <div class="useraccount">
            <h3><xsl:value-of select="ua:properties/prop:name"/> (<xsl:value-of select="ua:properties/prop:login-name"/>)</h3>
            <xsl:apply-templates select="ua:properties/prop:active"/>
            <div id="accordion">
                <h3><a href="#">My Items</a></h3>
                </div>
        </div>
    </xsl:template>
-->
    
    <xsl:template match="ua:resources">
        <!--  id="accordion" style="max-height:200px;" -->
        <div>
            <xsl:call-template name="my-grants"/>
            <xsl:call-template name="my-containers"/>
            <xsl:call-template name="my-items"/>
            <xsl:apply-templates select="ua:preferences"/>
        </div>
    </xsl:template>
    
    <xsl:template name="my-grants">
        <div><h6>My Grants</h6></div>
        <div class="grants">
            <dl class="searchresponse hero-unit padding-min">
                <xsl:variable name="grantSearchURL"><xsl:value-of select="/*/@xml:base"/>/aa/grants?maximumRecords=200&amp;query=%22/properties/user/id%22=<xsl:value-of select="substring-after(/*/@xlink:href,'user-account/')"/></xsl:variable>
                <xsl:variable name="grantSearchResult" select="document($grantSearchURL)"/>
                <xsl:apply-templates select="$grantSearchResult/srw:searchRetrieveResponse/srw:records/srw:record"><xsl:sort select="srw:recordPosition" data-type="number" order="ascending"/></xsl:apply-templates>
            </dl>
        </div>
    </xsl:template>
    
    <xsl:template name="my-containers">
        <div><h6>My Collections</h6></div>
        <div class="containers">
            <dl class="searchresponse hero-unit padding-min">
                <xsl:variable name="itemSearchURL"><xsl:value-of select="/*/@xml:base"/>/ir/containers?maximumRecords=200&amp;query=%22/properties/created-by/id%22=<xsl:value-of select="substring-after(/*/@xlink:href,'user-account/')"/> and top-level-containers=true</xsl:variable>
                <xsl:variable name="itemSearchResult" select="document($itemSearchURL)"/>
                <xsl:apply-templates select="$itemSearchResult/srw:searchRetrieveResponse/srw:records/srw:record"><xsl:sort select="srw:recordPosition" data-type="number" order="ascending"/></xsl:apply-templates>
            </dl>
        </div>
    </xsl:template>
    
    <xsl:template name="my-items">
        <div><h6>My Items</h6></div>
        <div class="items">
            <dl class="searchresponse hero-unit padding-min">
                <xsl:variable name="itemSearchURL"><xsl:value-of select="/*/@xml:base"/>/ir/items?maximumRecords=200&amp;query=%22/properties/created-by/id%22=<xsl:value-of select="substring-after(/*/@xlink:href,'user-account/')"/></xsl:variable>
                <xsl:variable name="itemSearchResult" select="document($itemSearchURL)"/>
                <xsl:apply-templates select="$itemSearchResult/srw:searchRetrieveResponse/srw:records/srw:record"><xsl:sort select="srw:recordPosition" data-type="number" order="ascending"/></xsl:apply-templates>
            </dl>
        </div>
    </xsl:template>
    
    <xsl:template match="ua:preferences">
        <div><h6>My Preferences</h6></div>
        <div class="prefs">
            <xsl:variable name="prefsURL"><xsl:value-of select="/*/@xml:base"/><xsl:value-of select="@xlink:href"/></xsl:variable>
            <xsl:variable name="prefs" select="document($prefsURL)"/>
            <xsl:apply-templates select="$prefs/pref:preferences"/>
        </div>
    </xsl:template>
    
    <xsl:template match="pref:preferences">
        <table class="table table-bordered table-striped table-condensed">
            <xsl:apply-templates select="pref:preference"/>
        </table>
    </xsl:template>
    
    <xsl:template match="pref:preference">
        <tr class="preference"><td><xsl:value-of select="@name"/></td><td><xsl:value-of select="./text()"/></td></tr>
    </xsl:template>
    
    <xsl:template match="prop:login-name">
        (<xsl:value-of select="."/>)
    </xsl:template>
    
    <xsl:template match="prop:active">
        <xsl:if test=". != 'true'">
            <p class="active">This user account is not active.</p>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
