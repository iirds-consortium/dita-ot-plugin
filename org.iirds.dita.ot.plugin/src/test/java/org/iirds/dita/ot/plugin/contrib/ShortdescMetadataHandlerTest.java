/*******************************************************************************
 * Copyright 2024-2026 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org 
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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.rdf.model.Model;
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
import org.xml.sax.InputSource;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
class ShortdescMetadataHandlerTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;
	ShortdescMetadataHandler handler;
	Model model;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(ShortdescMetadataHandlerTest.class));
		handler = new ShortdescMetadataHandler();
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
		File topicFile = new File(tmpDir, "topic_1.dita");
		URI topicURI = topicFile.toURI();
		Job job = createJob(tmpDir);
		Document document = job.getStore().getDocument(topicURI);
		ToCNode node = new ToCNode(new URI("topic_1.dita"));
		handler.extractMetadata(node, document);
		String shortdesc = node.getPropertyAsString(ShortdescMetadataHandler.META_SHORTDESC);
		assertEquals("Topic 1 has a short description", shortdesc);
	}

	@Test
	void testAddToModel() throws Exception {
		ToCNode node = new ToCNode();
		node.setURI(new URI("topic_1.dita"));
		node.setNavTitle("Topic 1");
		Resource topic = Factory.createTopic(model, "urn:topic1");
		node.setInformationUnit(topic);
		node.setProperty(ShortdescMetadataHandler.META_SHORTDESC, "Topic 1 has a short description");
		handler.addToModel(node, model);
		String shortdesc = InformationUnits.getAbstract(topic);
		assertEquals("Topic 1 has a short description", shortdesc);
	}


	static final String TOPIC = "class=\"- topic/topic \"";
	static final String SHORTDESC = "class=\"- topic/shortdesc \"";
	static final String MAP_SHORTDESC = "class=\"- topic/shortdesc map/shortdesc \"";
	static final String TOPICMETA = "class=\"- map/topicmeta \"";
	static final String TOPICREF = "class=\"- map/topicref \"";

	Document parse(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
	}

	String extract(String xml) throws Exception {
		ToCNode node = new ToCNode(new URI("test.dita"));
		handler.extractMetadata(node, parse(xml));
		return node.getPropertyAsString(ShortdescMetadataHandler.META_SHORTDESC);
	}

	@Test
	void testShortdescInsideAbstract() throws Exception {
		String xml = "<topic " + TOPIC + " id=\"t\"><title>T</title>"
				+ "<abstract class=\"- topic/abstract \"><shortdesc " + SHORTDESC + ">In the abstract</shortdesc>"
				+ "</abstract></topic>";
		assertEquals("In the abstract", extract(xml));
	}

	@Test
	void testNestedTopicShortdescIsNotTaken() throws Exception {
		String xml = "<topic " + TOPIC + " id=\"parent\"><title>Parent</title>"
				+ "<topic " + TOPIC + " id=\"child\"><title>Child</title>"
				+ "<shortdesc " + SHORTDESC + ">Describes the child</shortdesc></topic></topic>";
		assertNull(extract(xml), "A topic without its own shortdesc must not take the one of a nested topic");
	}

	@Test
	void testMapShortdescInTopicmeta() throws Exception {
		String xml = "<map class=\"- map/map \"><title>Map</title>"
				+ "<topicmeta " + TOPICMETA + "><shortdesc " + MAP_SHORTDESC + ">Describes the map</shortdesc>"
				+ "</topicmeta>"
				+ "<topicref " + TOPICREF + " href=\"a.dita\"><topicmeta " + TOPICMETA + "><shortdesc "
				+ MAP_SHORTDESC + ">Describes a topicref</shortdesc></topicmeta></topicref></map>";
		assertEquals("Describes the map", extract(xml));
	}

	@Test
	void testTopicrefShortdescIsNotTakenForTheMap() throws Exception {
		String xml = "<map class=\"- map/map \"><title>Map</title>"
				+ "<topicref " + TOPICREF + " href=\"a.dita\"><topicmeta " + TOPICMETA + "><shortdesc "
				+ MAP_SHORTDESC + ">Describes a topicref</shortdesc></topicmeta></topicref></map>";
		assertNull(extract(xml), "A map without its own shortdesc must not take the one of a topicref");
	}
}
