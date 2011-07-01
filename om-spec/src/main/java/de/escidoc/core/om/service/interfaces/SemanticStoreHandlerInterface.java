/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a semantic store handler of the business layer.
 *
 * @author Rozita Friedman
 */
public interface SemanticStoreHandlerInterface {

    /**
     * Retrieve a result of provided triple store query in a provided output format.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * Excecute a SPO query on the triple store. SPO is a simple RDF query  language, where queries consist of <ul>
     * <li>a <b>s</b>ubject</li> <li>a <b>p</b>redicate</li> <li>an an <b>o</b>bject</li> </ul> (see
     * http://www.fedora.info/download/2.2.1/userdocs/server/webservices/risearch/index.html)<br/> The query param
     * contains the query to execute and a format identifier for the desired output format and has to be created
     * according to schema "query.xsd".<br/>
     * <p/>
     * Only querys with a registered predicate or an asterisk ('*') instead of a predicate are allowed. The registered
     * predicates in the current release are published at http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations.xml
     * defined as RDF-Properties. Further releases will provide means to register additional predicates.<br/>
     * <p/>
     * The result of a query is filtered in order to deliver triples with registered predicates only. (in further
     * releases).<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The XML data is returned.</li> </ul> <br/>Valid output format identifiers are:<br/>
     * <ul> <li>N-Triples (http://www.w3.org/2001/sw/RDFCore/ntriples/)</li> <li>Notation 3
     * (http://www.w3.org/DesignIssues/Notation3.html)</li> <li>RDF/XML (http://www.w3.org/TR/rdf-syntax-grammar/)</li>
     * <li>Turtle (http://www.dajobe.org/2004/01/turtle/)</li> </ul> <br/><b>Example 1:</b><br/>
     * <p/>
     * <pre>&lt;param></pre>
     * <pre>   &lt;filter name=&quot;items&quot;&gt;</pre>
     * <pre>   &lt;query>&lt;info:fedora/escidoc:111&gt; * *&lt;/query></pre>
     * <pre>   &lt;format>N-Triples&lt;/format>        </pre>
     * <pre>&lt;/param></pre>
     * This request retrieves all triples which have &lt;info:fedora/escidoc:111&gt; as subject.
     * <p/>
     * <br/><b>Example 2:</b><br/>
     * <p/>
     * <pre>&lt;param></pre>
     * <pre>   &lt;query></pre>
     * <pre>      &amp;lt;info:fedora/escidoc:111&amp;gt; </pre>
     * <pre>      &amp;lt;http://www.escidoc.de/ontologies/mpdl-ontologies/ (remove line break)</pre>
     * <pre>                            content-relations#isMemberOf&amp;gt;</pre>
     * <pre>              *</pre>
     * <pre>   &lt;/query></pre>
     * <pre>   &lt;format>N-Triples&lt;/format></pre>
     * <pre>&lt;/param></pre>
     * This request retrieves all triples which have &lt;info:fedora/escidoc:111&gt; as subject and <i>isMemberOf</i>
     * from the registered ontologie http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations.xml as predicte.
     * Wrap the predicate in encoded tags (&amp;lt;/&amp;gt;) and separate the relative URIref via # from
     * namespace.<br/>
     * <p/>
     * The vocabular of the current supported ontologie namespace is defined in http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations.xml.<br/>
     * <p/>
     * The following list is a subset example of the defined vocabular. It is not avoidable to catch the whole set of
     * vocabulary from the namespace definition. <ul> <li>isRevisionOf</li> <li>isPartOf</li> <li>hasPart</li>
     * <li>isConstituentOf</li> <li>hasConstituent</li> <li>isMemberOf</li> <li>hasMember</li> <li>isSubsetOf</li>
     * <li>hasSubset</li> <li>isMemberOfCollection</li> <li>hasCollectionMember</li> <li>isDerivationOf</li>
     * <li>hasDerivation</li> <li>isDependentOf</li> <li>hasDependent</li> <li>isDescriptionOf</li>
     * <li>hasDescription</li> <li>isMetadataFor</li> <li>hasMetadata</li> <li>isAnnotationOf</li>
     * <li>hasAnnotation</li> <li>hasEquivalent</li> </ul> Use this vocabulary as SPO predicate with full namespace and
     * # as separator e.x.:<br/> http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#isMemberOf<br/>
     *
     * @param taskParam An xml structure containing the spo query and the expected output format. See description above
     *                  for details.
     * @return Returns XML representation of the query result.
     * @throws SystemException              TODO
     * @throws InvalidTripleStoreQueryException
     *                                      TODO
     * @throws InvalidTripleStoreOutputFormatException
     *                                      TODO
     * @throws InvalidXmlException          TODO
     * @throws MissingElementValueException TODO
     * @throws AuthenticationException      TODO
     * @throws AuthorizationException       TODO
     * @deprecated
     */
    @Deprecated
    @Validate(param = 0, resolver = "getSpoTaskParamSchemaLocation")
    String spo(final String taskParam) throws SystemException, InvalidTripleStoreQueryException,
        InvalidTripleStoreOutputFormatException, InvalidXmlException, MissingElementValueException,
        AuthenticationException, AuthorizationException;

}
