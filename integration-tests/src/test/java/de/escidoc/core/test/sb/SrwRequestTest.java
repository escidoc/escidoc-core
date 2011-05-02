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
package de.escidoc.core.test.sb;

import gov.loc.www.zing.srw.ExplainRequestType;
import gov.loc.www.zing.srw.service.ExplainPort;
import gov.loc.www.zing.srw.service.SRWPort;
import gov.loc.www.zing.srw.service.SRWSampleServiceLocator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author Rozita Friedman
 */
public class SrwRequestTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SrwRequestTest.class);

    protected SRWPort srwService;

    protected ExplainPort explainService;

    private final String location = "http://localhost:8080/srw/search/escidoc_all";

    // private String location =
    // "http://beta-tc.fiz-karlsruhe.de/srw/search/tc";
    @Before
    public void setUp() throws Exception {

        SRWSampleServiceLocator service = new SRWSampleServiceLocator();
        URL url = new URL(location);
        srwService = service.getSRW(url);
        explainService = service.getExplainSOAP(url);
    }

    /**
     * The method tests the Sb service SRW-Search by using http-request.
     *
     * @throws Exception any exception
     */
    @Ignore("test the Sb service SRW-Search by using http-request")
    @Test
    public void testSearchByRest() throws Exception {
        for (int i = 0; i < 1; i++) {
            HttpRequester requester = new HttpRequester(location);
            String response = requester.doGet("?query=escidoc.objid%3Descidoc:345&recordPacking=string");

        }
    }

    /**
     * The method tests the SRW-Search by using http-request.
     *
     * @throws Exception any exception
     */
    @Ignore("test the SRW-Search by using http-request")
    @Test
    public void notestSearchBySoapRequest() throws Exception {
        String soapPost =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<SOAP:Envelope xmlns:SOAP=\"http://schemas.xmlsoap.org/soap/envelope/\">" + "<SOAP:Body>"
                + "<ExplainSOAP:ExplainOperation xmlns:ExplainSOAP=\"http://www.LOGGER.gov/zing/srw/\">"
                + "</ExplainSOAP:ExplainOperation>" + "</SOAP:Body>" + "</SOAP:Envelope>";
        HttpRequester requester = new HttpRequester(location, "mih:11311");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(requester.doPost("", soapPost));
        }
    }

    /**
     * The method tests the Sb service SRW-Search by using SOAP.
     *
     * @throws Exception any exception
     */
    @Test
    public void testSearchBySoap() throws Exception {
        // SearchRetrieveRequestType request = new SearchRetrieveRequestType();
        // request.setQuery("escidoc.objid=escidoc:5301");
        // request.setVersion("1.1");
        // request.setRecordPacking("xml");
        // request.setMaximumRecords(new NonNegativeInteger("20"));
        // //request.setStartRecord(new PositiveInteger("1"));
        // SearchRetrieveResponseType response =
        // srwService.searchRetrieveOperation(request);
        ExplainRequestType request = new ExplainRequestType();
        request.setVersion("1.1");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(explainService.explainOperation(request).getVersion());
        }
    }

}
