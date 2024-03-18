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

package org.iirds.dita.ot.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.store.Store;
import org.dita.dost.store.StreamStore;
import org.dita.dost.util.Constants;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.contrib.ComponentMetadataHandler;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.test.TestLogger;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class TopicTypeMapperTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;
	ComponentMetadataHandler handler;
	Model model;
	Job job;
	File path;

	@BeforeEach
	void init() throws URISyntaxException, IOException {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(ConfigurationTest.class));
		handler = new ComponentMetadataHandler();
		model = RDFConstants.createInfModel(RDFConstants.createDefaultModel());
		path = new File(getClass().getResource("/dita.temp/extractor").toURI());
		store = new StreamStore(path, xmlUtils);
		job = new Job(path, store);
	}

	@Test
	void testGetTopicType() {
		TopicTypeMapper mapper = new TopicTypeMapper();
		ToCNode node = new ToCNode();
		node.setDitaClass(Constants.TOPIC_TOPIC);
		assertEquals(IirdsConstants.iiRDS_URI + "GenericConcept", mapper.getTopicType(node).getURI());

		node.setDitaClass(Constants.TASK_TASK);
		assertEquals(IirdsConstants.iiRDS_URI + "GenericTask", mapper.getTopicType(node).getURI());

		node.setDitaClass(Constants.REFERENCE_REFERENCE);
		assertEquals(IirdsConstants.iiRDS_URI + "GenericReference", mapper.getTopicType(node).getURI());

		node.setDitaClass(Constants.CONCEPT_CONCEPT);
		assertEquals(IirdsConstants.iiRDS_URI + "GenericConcept", mapper.getTopicType(node).getURI());

		node.setDitaClass(null);
		assertEquals(IirdsConstants.iiRDS_URI + "GenericConcept", mapper.getTopicType(node).getURI());

	}
}
