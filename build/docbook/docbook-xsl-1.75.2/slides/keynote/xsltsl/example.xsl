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

<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="doc"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:doc="http://xsltsl.org/xsl/documentation/1.0"
  xmlns:eg="http://xsltsl.org/example"
>

  <doc:reference xmlns="">
    <referenceinfo>
      <releaseinfo role="meta">
        $Id: example.xsl 3991 2004-11-10 06:51:55Z balls $
      </releaseinfo>
      <author>
        <surname>Ball</surname>
        <firstname>Steve</firstname>
      </author>
      <copyright>
        <year>2001</year>
        <holder>Steve Ball</holder>
      </copyright>
    </referenceinfo>

    <title>Example Stylesheet</title>

    <partintro>
      <section>
        <title>Introduction</title>

        <para>This module provides a template for adding stylesheet modules to the XSLT Standard Library.</para>
        <para>To add a new module to the library, follow these easy steps:</para>
        <orderedlist>
          <listitem>
            <para>Copy this file and replace its contents with the new module templates and documentation.</para>
          </listitem>
          <listitem>
            <para>Copy the corresponding test file in the <filename>test</filename> directory.  Replace its contents with tests for the new module.</para>
          </listitem>
          <listitem>
            <para>Add an include element in the <filename>stdlib.xsl</filename> stylesheet.</para>
          </listitem>
          <listitem>
            <para>Add an entry in the <filename>test/test.xml</filename> file.</para>
          </listitem>
          <listitem>
            <para>Add entries in the <filename>test/test.xsl</filename> stylesheet.</para>
          </listitem>
          <listitem>
            <para>Add an entry in the <filename>doc/build.xml</filename> file.</para>
          </listitem>
        </orderedlist>

        <para>The <filename>example.xsl</filename> stylesheet provides a more extensive example.</para>

      </section>
    </partintro>

  </doc:reference>

  <doc:template name="eg:example" xmlns="">
    <refpurpose>Template Example</refpurpose>

    <refdescription>
      <para>Provides a template for writing templates.  Replace this paragraph with a description of your template</para>
    </refdescription>

    <refparameter>
      <variablelist>
        <varlistentry>
          <term>text</term>
          <listitem>
            <para>The example string</para>
          </listitem>
        </varlistentry>
      </variablelist>
    </refparameter>

    <refreturn>
      <para>Returns nothing.</para>
    </refreturn>
  </doc:template>

  <xsl:template name="eg:example">
    <xsl:param name="text"/>
  </xsl:template>

</xsl:stylesheet>

