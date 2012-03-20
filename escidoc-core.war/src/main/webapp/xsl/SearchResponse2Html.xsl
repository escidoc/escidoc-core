<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:srw="http://www.loc.gov/zing/srw/"
    xmlns:sr="http://www.escidoc.de/schemas/searchresult/0.8"
    xmlns:prop="http://escidoc.de/core/01/properties/"
    xmlns:srel="http://escidoc.de/core/01/structural-relations/"
    xmlns:md="http://www.escidoc.de/schemas/metadatarecords/0.5"
    xmlns:context="http://www.escidoc.de/schemas/context/0.7"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" version="1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    <xsl:output encoding="iso-8859-1" indent="yes" method="html"/>

    <xsl:template match="srw:searchRetrieveResponse">
        <dl class="searchresponse hero-unit padding-min">
            <h3>Search Response</h3>
            <xsl:apply-templates select="srw:records/srw:record"><xsl:sort select="srw:recordPosition" data-type="number" order="ascending"/></xsl:apply-templates>
            <xsl:call-template name="nextRecordPosition"><xsl:with-param name="n" select="."/></xsl:call-template>
        </dl>
    </xsl:template>
    
    <xsl:template match="srw:record">
        <div class="searchresult">
            <dt class="head">
                <span class="position"><xsl:value-of select="srw:recordPosition"/>. </span><span class="title"><xsl:call-template name="displayLink">
                    <xsl:with-param name="n" select="srw:recordData/sr:search-result-record/*[@xlink:title]"/>
                </xsl:call-template></span>
            </dt>
            <dd class="searchmd">
                <span class="score"><xsl:value-of select="srw:recordData/sr:search-result-record/sr:score"/></span>
                <xsl:variable name="text" select="srw:recordData/sr:search-result-record/sr:highlight/sr:search-hit/sr:text-fragment/sr:text-fragment-data"/>
                <xsl:variable name="start" select="srw:recordData/sr:search-result-record/sr:highlight/sr:search-hit/sr:text-fragment/sr:hit-word/sr:start-index"/>
                <xsl:variable name="end" select="srw:recordData/sr:search-result-record/sr:highlight/sr:search-hit/sr:text-fragment/sr:hit-word/sr:end-index"/>
                <span class="match">
                    <xsl:value-of select="substring($text,0,$start)"/>
                    <b><xsl:value-of select="substring($text,$start,$end - $start)"/></b>
                    <xsl:value-of select="substring($text,$end)"/>
                </span>
            </dd>
        </div>
    </xsl:template>
    
    <xsl:template name="nextRecordPosition">
        <xsl:param name="n"/>
        <xsl:if test="$n/srw:records/srw:record[1]/srw:recordPosition > 1">
            <a href="javascript:history.back()">Zurück</a>
        </xsl:if>
        <xsl:text> </xsl:text>
        <xsl:if test="$n/srw:nextRecordPosition">
            <a>
                <xsl:attribute name="href">?startRecord=<xsl:value-of select="$n/srw:nextRecordPosition"/></xsl:attribute>
                <xsl:text>Weiter</xsl:text>
            </a>
        </xsl:if>
    </xsl:template>

    <xsl:template name="displayLink">
        <xsl:param name="n"/>
        <xsl:choose>
            <xsl:when test="$n/@xlink:href">
            <a>
                <xsl:attribute name="href">
                    <!-- 
                    <xsl:if test="not(starts-with($n/@xlink:href, 'http'))">
                        <xsl:value-of select="/*/@xml:base"/>
                    </xsl:if>
                    -->
                    <xsl:value-of select="$n/@xlink:href"/>
                </xsl:attribute>
                <xsl:value-of select="$n/@xlink:title"/>
            </a>
            </xsl:when>
            <xsl:otherwise>Can not display link for <xsl:value-of select="local-name()"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
