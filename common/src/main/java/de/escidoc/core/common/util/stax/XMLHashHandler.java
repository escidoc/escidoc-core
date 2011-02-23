/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
/**
 * 
 */
package de.escidoc.core.common.util.stax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author FRS
 * 
 */
public class XMLHashHandler extends DefaultHandler {

    private StringBuffer string = null;

    private String hash = null;

    @Override
    public void characters(char[] ch, int start, int length)
        throws SAXException {
        StringBuilder cb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            cb.append(ch[i + start]);
        }

        String characters = cb.toString();
        if (characters.trim().length() != 0) {
            // problems with splited character data
            // string.append("#" + characters);
            string.append(characters);
        }
    }

    public String getHash() {
        return hash;
    }

    @Override
    public void startDocument() throws SAXException {
        string = new StringBuffer();
        string.append("begin");
    }

    @Override
    public void startElement(
        String uri, String localName, String qName, Attributes attributes)
        throws SAXException {

        String fqName = createFqName(uri, localName, qName);

        string.append('#');
        string.append(fqName);
        int length = attributes.getLength();
        SortedMap<String, String> atts = new TreeMap<String, String>();
        for (int i = 0; i < length; i++) {
            String curQName = attributes.getQName(i);
            String attName =
                    '{' + attributes.getURI(i) + '}' + attributes.getLocalName(i);
            if (!"xmlns:xml".equalsIgnoreCase(curQName)) {
                atts.put(attName, attributes.getValue(i));
            }
        }
        for (String s : atts.keySet()) {
            String name = s;
            string.append('#');
            string.append(name);
            string.append('=');
            string.append(atts.get(name));
        }
        // mark for begin of element content, either complex or simple
        string.append('#');
    }

    @Override
    public void endElement(
        final String uri, final String localName, final String qName)
        throws SAXException {
        string.append('#');
        string.append(createFqName(uri, localName, qName));
    }

    @Override
    public void endDocument() throws SAXException {
        string.append("#end");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.toString().getBytes());
            hash = new String(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            throw new SAXException("No such digest algorithm.", e);
        }
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException(e);
    }

    /**
     * Create full qualified name.
     * 
     * @param uri
     * @param localName
     * @param qName
     * @return full qualified name
     */
    private String createFqName(
        final String uri, final String localName, final String qName) {

        String fqName = '{' + uri + '}';
        if (localName != null && localName.length() > 0) {
            fqName += localName;
        }
        else {
            fqName += qName.split(":")[1];
        }

        return fqName;
    }
}
