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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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
import org.iirds.rdf.IirdsConstants;
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
class CritdatesMetadataHandlerTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;
	CritdatesMetadataHandler handler;
	Model model;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(CritdatesMetadataHandlerTest.class));
		handler = new CritdatesMetadataHandler();
		model = RDFConstants.createInfModel(RDFConstants.createDefaultModel());
	}

	Job createJob(File path) throws IOException {
		Store store = new StreamStore(path, xmlUtils);
		Job job = new Job(path, store);
		return job;
	}

	@Test 
	void testReformatDate() {
		assertEquals("2021-01-02",handler.reformatDate("2021-01-02"));
		assertEquals("2021-01-02",handler.reformatDate("01/02/2021"));
		assertEquals("2021-01-02",handler.reformatDate("02. Jan. 2021"));
		assertEquals("2021-01-02",handler.reformatDate("2. Jan. 2021"));
	}
	
	@Test
	void testExtractMetadata() throws Exception {
		File tmpDir = new File(getClass().getResource("/dita.temp/extractor").toURI());
		File topicFile = new File(tmpDir, "task_1.dita");
		URI topicURI = topicFile.toURI();
		Job job = createJob(tmpDir);
		Document document = job.getStore().getDocument(topicURI);
		ToCNode node = new ToCNode(new URI("task_1.dita"));
		node.setIsTopic();
		handler.extractMetadata(node, document);
		assertEquals("2021-02-28", node.getPropertyAsString(CritdatesMetadataHandler.META_CREATED));
		assertEquals("2021-08-12", node.getPropertyAsString(CritdatesMetadataHandler.META_GOLIVE));
		assertEquals("2021-09-12", node.getPropertyAsString(CritdatesMetadataHandler.META_REVISED));
		assertEquals("2030-01-01", node.getPropertyAsString(CritdatesMetadataHandler.META_EXPIRY));
	}

	@Test
	void testAddToModel() throws Exception {
		ToCNode node = new ToCNode();
		node.setURI(new URI("task_1.dita"));
		node.setNavTitle("Topic 1");
		Resource topic = Factory.createTopic(model, "urn:topic1");
		node.setInformationUnit(topic);
		node.setProperty(CritdatesMetadataHandler.META_CREATED, "2021-02-28");
		node.setProperty(CritdatesMetadataHandler.META_GOLIVE, "2021-08-12");
		node.setProperty(CritdatesMetadataHandler.META_REVISED, "2021-09-12");
		node.setProperty(CritdatesMetadataHandler.META_EXPIRY, "2030-01-01");
		handler.addToModel(node, model);
		List<RDFNode> status = InformationUnits.getContentLifecycleStatus(topic);
		assertEquals(1, status.size());
		assertEquals("2021-02-28T00:00:00", InformationUnits.getDateOfCreation(topic));
		assertEquals("2021-09-12T00:00:00", InformationUnits.getDateOfLastModification(topic));
		assertEquals("2030-01-01T23:59:59", status.get(0).asResource()
				.getProperty(model.getProperty(IirdsConstants.DATEOFEXPIRY_PROPERTY_URI)).getString());
		assertEquals("2021-08-12T00:00:00", status.get(0).asResource()
				.getProperty(model.getProperty(IirdsConstants.DATEOFEFFECT_PROPERTY_URI)).getString());
		assertEquals(IirdsConstants.iiRDS_URI + "Released",
				status.get(0).asResource()
						.getProperty(model.getProperty(IirdsConstants.HASCONTENTLIFECYCLESTATUSVALUE_PROPERTY_URI))
						.getResource().getURI());
	}

}
