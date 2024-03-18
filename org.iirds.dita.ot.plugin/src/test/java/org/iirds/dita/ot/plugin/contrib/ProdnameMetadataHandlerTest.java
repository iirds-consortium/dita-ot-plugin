/*******************************************************************************
 * Copyright 2024 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.iirds.dita.ot.plugin.contrib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.store.Store;
import org.dita.dost.store.StreamStore;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.test.TestLogger;
import org.iirds.rdf.RDFConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
class ProdnameMetadataHandlerTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;
	ProdnameMetadataHandler handler;
	Model model;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(ProdnameMetadataHandlerTest.class));
		handler = new ProdnameMetadataHandler();
		model = RDFConstants.createInfModel(RDFConstants.createDefaultModel());
	}

	Job createJob(File path) throws IOException {
		Store store = new StreamStore(path, xmlUtils);
		Job job = new Job(path, store);
		return job;
	}

	@Test
	void testExtractMetadata() throws Exception {
		File tmpDir = new File(getClass().getResource("/dita.temp/extractor").toURI());
		File topicFile = new File(tmpDir, "test.ditamap");
		URI topicURI = topicFile.toURI();
		Job job = createJob(tmpDir);
		Document document = job.getStore().getDocument(topicURI);
		ToCNode node = new ToCNode(new URI("test.ditamap"));
		handler.extractMetadata(node, document);
		@SuppressWarnings("unchecked")
		Set<String> products = (Set<String>) node.getProperty(ProdnameMetadataHandler.PROP_PRODNAME);
		assertEquals(3, products.size());
		assertTrue(products.contains("Product 1"));
		assertTrue(products.contains("Product 2"));
		assertTrue(products.contains("Product 3"));
	}

	@Test
	void testAddToModel() throws Exception {
		ToCNode node = new ToCNode();
		node.setURI(new URI("topic_1.dita"));
		node.setNavTitle("Topic 1");
		Resource topic = Factory.createTopic(model, "urn:topic1");
		node.setInformationUnit(topic);
		node.setProperty(ProdnameMetadataHandler.PROP_PRODNAME, Collections.singleton("Prodeuct A"));
		assertEquals(0, InformationUnits.getRelatedProductVariants(topic).size());
		handler.addToModel(node, model);
		List<RDFNode> products = InformationUnits.getRelatedProductVariants(topic);
		assertEquals(1, products.size());
	}

}
