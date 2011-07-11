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
package de.escidoc.core.test.om.item;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.CountRepository;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.common.util.Environment;
import de.escidoc.core.test.common.util.SQLRenderer;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;
import etm.core.renderer.SimpleTextRenderer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Random;
import java.util.Vector;

/**
 * Measure performance of Item create.
 * 
 * @author Steffen Wagner
 */
public class ItemPerformanceIT extends ItemTestBase {

    private static final String LABEL_BASE = "EscidocTestBase:";

    private static final String LABEL_CREATE = LABEL_BASE + "create";

    private static final String LABEL_RETRIEVE = LABEL_BASE + "retrieve";

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPerformanceIT.class);

    private static EtmMonitor monitor;

    private static final int ITERATIONS = 40;

    private MeasurementRenderer renderer = null;

    private Environment testEv = null;

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        BasicEtmConfigurator.configure();
        monitor = EtmManager.getEtmMonitor();
        this.testEv = setTestEnviromentValues(new Environment());

        String databaseUrl = PropertiesProvider.getInstance().getProperty("performance.database.url");

        try {
            this.renderer =
                new SQLRenderer(PropertiesProvider.getInstance().getProperty("performance.database.driverClassName"),
                    databaseUrl, PropertiesProvider.getInstance().getProperty("performance.database.username"),
                    PropertiesProvider.getInstance().getProperty("performance.database.password"), this.testEv);
        }
        catch (final Exception e) {
            this.renderer = new SimpleTextRenderer();
        }

        LOGGER.info("\n====================================\n" + CountRepository.countResources()
            + " objects in repository\n" + "====================================");
    }

    /**
     * Measure create of minimal-item-01.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreate01() throws Exception {

        measureCreateMethod("item-minimal-for-create-01.xml");
    }

    /**
     * Measure create of minimal-item-02.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreate02() throws Exception {

        measureCreateMethod("item-minimal-for-create-02.xml");
    }

    /**
     * Measure create of minimal-item-03.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreate03() throws Exception {

        measureCreateMethod("item-minimal-for-create-03.xml");
    }

    /**
     * Measure create of minimal-item-04.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreate04() throws Exception {

        measureCreateMethod("item-minimal-for-create-04.xml");
    }

    /**
     * Measure create of Item with many components.
     * 
     * @throws Exception
     *             If creation failed.
     */
    @Test
    public void testCreate05() throws Exception {

        String templateName = "escidoc_item_with_many_components-01.xml";
        String itemXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", templateName);

        measureCreateMethod(itemXml, templateName);
    }

    /**
     * Measure retrieve of minimal-item-01.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve01() throws Exception {

        String templateName = "item-minimal-for-create-01.xml";
        String itemXml = getExampleTemplate(templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve of minimal-item-02.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve02() throws Exception {

        String templateName = "item-minimal-for-create-02.xml";
        String itemXml = getExampleTemplate(templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve of minimal-item-03.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve03() throws Exception {

        String templateName = "item-minimal-for-create-03.xml";
        String itemXml = getExampleTemplate(templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve of minimal-item-04.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve04() throws Exception {

        String templateName = "item-minimal-for-create-04.xml";
        String itemXml = getExampleTemplate(templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve of escidoc_item_with_3_components-01.xml.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve05() throws Exception {

        String templateName = "performance/escidoc_item_with_3_components-01.xml";
        String itemXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve of escidoc_item_with_many_components-01.xml.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieve06() throws Exception {

        String templateName = "escidoc_item_with_many_components-01.xml";
        String itemXml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", templateName);
        String newItemXml = create(itemXml);
        String objid = getObjidValue(newItemXml);

        measureRetrieveMethod(objid, templateName);
    }

    /**
     * Measure retrieve Items randomly.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveRandom() throws Exception {

        Vector<String> itemObjids = CountRepository.obtainObjidsOfItemsFromTripleStore();
        int noOfItems = itemObjids.size();
        Random r = new Random();
        String objid = null;
        monitor.reset(LABEL_CREATE);
        monitor.reset(LABEL_RETRIEVE);
        for (int i = 0; i < ITERATIONS; i++) {
            objid = itemObjids.get(r.nextInt(noOfItems));
            monitor.start();
            retrieve(objid);
            monitor.stop();
        }
        LOGGER.info("Item retrieve (" + objid + "):");
        this.testEv.setMethodParameter("random");
        monitor.render(this.renderer);
        monitor.render(new SimpleTextRenderer());
    }

    /**
     * Measure retrieve Items and afterwards one Component of the Item randomly. Measure time for Item and Component
     * retrieve.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveRandom2() throws Exception {

        Vector<String> itemObjids = CountRepository.obtainObjidsOfItemsFromTripleStore();
        int noOfItems = itemObjids.size();
        Random r = new Random();
        String objid = null;

        monitor.reset(LABEL_CREATE);
        monitor.reset(LABEL_RETRIEVE);
        for (int i = 0; i < ITERATIONS; i++) {
            objid = itemObjids.get(r.nextInt(noOfItems));
            monitor.start();
            String itemXml = retrieve(objid);
            monitor.stop();

            // retrieve component (if exists)
            Document itemDoc = EscidocAbstractTest.getDocument(itemXml);
            Node components = selectSingleNode(itemDoc, "/item/components/component");
            if (components != null) {
                String componentId = getObjidValue(itemDoc, "/item/components/component[1]");

                monitor.start();
                retrieveComponent(objid, componentId);
                monitor.stop();
            }

        }
        LOGGER.info("Item retrieve (" + objid + "):");
        this.testEv.setMethodParameter("random2");
        monitor.render(this.renderer);
        monitor.render(new SimpleTextRenderer());
    }

    /**
     * Measure the create Item method.
     * 
     * @param template
     *            Name of the template file.
     * @throws Exception
     *             Thrown if creating or measurement failed.
     */
    private void measureCreateMethod(final String template) throws Exception {

        String itemXml = getExampleTemplate(template);
        measureCreateMethod(itemXml, template);
    }

    /**
     * Measure the create Item method.
     * 
     * @param itemXml
     *            The XML of the Item.
     * @param templateName
     *            Name of the template file.
     * @throws Exception
     *             Thrown if creating or measurement failed.
     */
    private void measureCreateMethod(final String itemXml, final String templateName) throws Exception {
        monitor.reset(LABEL_CREATE);
        for (int i = 0; i < ITERATIONS; i++) {
            monitor.start();
            create(itemXml);
            monitor.stop();
        }
        LOGGER.info("Item create (" + templateName + "):");
        this.testEv.setMethodParameter(templateName);
        monitor.render(this.renderer);
        monitor.render(new SimpleTextRenderer());
    }

    /**
     * Measure the retrieve Item method.
     * 
     * @param objid
     *            Objid of Item.
     * @param templateName
     *            Name of the template file.
     * @throws Exception
     *             Thrown if retrieving or measurement failed.
     */
    private void measureRetrieveMethod(final String objid, final String templateName) throws Exception {
        monitor.reset(LABEL_CREATE);
        monitor.reset(LABEL_RETRIEVE);
        for (int i = 0; i < ITERATIONS; i++) {
            monitor.start();
            retrieve(objid);
            monitor.stop();
        }
        LOGGER.info("Item retrieve (" + objid + "):");
        this.testEv.setMethodParameter(templateName);
        monitor.render(this.renderer);
        monitor.render(new SimpleTextRenderer());
    }

    /**
     * Fill the Environment object with values.
     * 
     * @param te
     *            The Environment object.
     * @return The updated Environment.
     * @throws Exception
     *             Thrown if obtaining values failed.
     */
    private Environment setTestEnviromentValues(final Environment te) throws Exception {

        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            te.setHostname(localMachine.getHostName());
        }
        catch (final java.net.UnknownHostException uhe) {
            throw new Exception(uhe);
        }

        String v = obtainFrameworkVersion();

        te.setFwSeries(getFrameworkSeries(v));
        te.setFwBuild(v);

        te.setJavaVersion(System.getProperty("java.version"));
        te.setNoOfCpus(Runtime.getRuntime().availableProcessors());
        te.setMemory(Runtime.getRuntime().maxMemory());
        return te;
    }

    /**
     * Calculate framework series from build number.
     * 
     * @param buildNumber
     *            Build number
     * 
     * @return framework series
     */
    private String getFrameworkSeries(final String buildNumber) {

        String fwSeries = null;

        if (buildNumber.equals("trunk-SNAPSHOT")) {
            // trunk-SNAPSHOT
            fwSeries = "trunk";
        }
        else {
            // 1.3[.x][-SNAPSHOT|RC|?]
            fwSeries = buildNumber.substring(0, 3);
        }

        return fwSeries;
    }
}
