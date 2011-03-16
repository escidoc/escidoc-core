<?xml version="1.0"?>
<!--
  ~ CDDL HEADER START
  ~
  ~ The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
  ~ only (the "License"). You may not use this file except in compliance with the License.
  ~
  ~ You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
  ~ for the specific language governing permissions and limitations under the License.
  ~
  ~ When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
  ~ license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
  ~ brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
  ~
  ~ CDDL HEADER END
  ~
  ~ Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
  ~ and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
  ~ terms.
  -->

<!DOCTYPE xsl:stylesheet [
<!ENTITY primary   'normalize-space(concat(primary/@sortas, primary[not(@sortas)]))'>

]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                xmlns:func="http://exslt.org/functions"
                xmlns:exslt="http://exslt.org/common"
                xmlns:l="http://docbook.sourceforge.net/xmlns/l10n/1.0"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                extension-element-prefixes="func exslt"
                exclude-result-prefixes="func exslt i l"
                xmlns:i="urn:cz-kosek:functions:index">

<!-- ********************************************************************
     $Id: autoidx-kosek.xsl 6910 2007-06-28 23:23:30Z xmldoc $
     ********************************************************************

     This file is part of the DocBook XSL Stylesheet distribution.
     See ../README or http://docbook.sf.net/ for copyright
     copyright and other information.

     ******************************************************************** -->

<xsl:param name="kosek.imported">
  <xsl:variable name="vendor" select="system-property('xsl:vendor')"/>
  <xsl:choose>
    <xsl:when test="contains($vendor, 'libxslt')">
      <xsl:message terminate="yes">
        <xsl:text>ERROR: the 'kosek' index method does not </xsl:text>
        <xsl:text>work with the xsltproc XSLT processor.</xsl:text>
      </xsl:message>
    </xsl:when>
    <xsl:otherwise>1</xsl:otherwise>
  </xsl:choose>
</xsl:param>

<!-- Returns index group code for given term  -->
<func:function name="i:group-index">
  <xsl:param name="term"/>
  
  <xsl:variable name="letters-rtf">
    <xsl:variable name="lang">
      <xsl:call-template name="l10n.language"/>
    </xsl:variable>
    
    <xsl:variable name="local.l10n.letters"
      select="($local.l10n.xml//l:i18n/l:l10n[@language=$lang]/l:letters)[1]"/>
    
    <xsl:variable name="l10n.letters"
      select="($l10n.xml/l:i18n/l:l10n[@language=$lang]/l:letters)[1]"/>
    
    <xsl:choose>
      <xsl:when test="count($local.l10n.letters) &gt; 0">
        <xsl:copy-of select="$local.l10n.letters"/>
      </xsl:when>
      <xsl:when test="count($l10n.letters) &gt; 0">
        <xsl:copy-of select="$l10n.letters"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>No "</xsl:text>
          <xsl:value-of select="$lang"/>
          <xsl:text>" localization of index grouping letters exists</xsl:text>
          <xsl:choose>
            <xsl:when test="$lang = 'en'">
              <xsl:text>.</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>; using "en".</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:message>
        
        <xsl:copy-of select="($l10n.xml/l:i18n/l:l10n[@language='en']/l:letters)[1]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="letters" select="exslt:node-set($letters-rtf)/*"/>
  
  <xsl:variable name="long-letter-index" select="$letters/l:l[. = substring($term,1,2)]/@i"/>
  <xsl:variable name="short-letter-index" select="$letters/l:l[. = substring($term,1,1)]/@i"/>
  <xsl:variable name="letter-index">
    <xsl:choose>
      <xsl:when test="$long-letter-index">
        <xsl:value-of select="$long-letter-index"/>
      </xsl:when>
      <xsl:when test="$short-letter-index">
        <xsl:value-of select="$short-letter-index"/>
      </xsl:when>
      <xsl:otherwise>0</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <func:result select="number($letter-index)"/>
</func:function>

<!-- Return index group letter for given group code -->
<func:function name="i:group-letter">
  <xsl:param name="index"/>

  <xsl:variable name="letters-rtf">
    <xsl:variable name="lang">
      <xsl:call-template name="l10n.language"/>
    </xsl:variable>
    
    <xsl:variable name="local.l10n.letters"
      select="($local.l10n.xml//l:i18n/l:l10n[@language=$lang]/l:letters)[1]"/>
    
    <xsl:variable name="l10n.letters"
      select="($l10n.xml/l:i18n/l:l10n[@language=$lang]/l:letters)[1]"/>
    
    <xsl:choose>
      <xsl:when test="count($local.l10n.letters) &gt; 0">
        <xsl:copy-of select="$local.l10n.letters"/>
      </xsl:when>
      <xsl:when test="count($l10n.letters) &gt; 0">
        <xsl:copy-of select="$l10n.letters"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>No "</xsl:text>
          <xsl:value-of select="$lang"/>
          <xsl:text>" localization of index grouping letters exists</xsl:text>
          <xsl:choose>
            <xsl:when test="$lang = 'en'">
              <xsl:text>.</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>; using "en".</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:message>
        
        <xsl:copy-of select="($l10n.xml/l:i18n/l:l10n[@language='en']/l:letters)[1]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  
  <xsl:variable name="letters" select="exslt:node-set($letters-rtf)/*"/>
  
  <func:result select="$letters/l:l[@i=$index][1]"/>
</func:function>

<xsl:key name="group-code"
         match="indexterm"
         use="i:group-index(&primary;)"/>

</xsl:stylesheet>
