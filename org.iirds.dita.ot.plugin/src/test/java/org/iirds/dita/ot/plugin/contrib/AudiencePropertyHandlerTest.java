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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
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
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.iirds.dita.ot.plugin.model.RelationType;
import org.iirds.dita.ot.plugin.model.SubjectDef;
import org.iirds.dita.ot.plugin.model.SubjectDefinitions;
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
class AudiencePropertyHandlerTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;
	AudiencePropertyHandler handler;
	Model model;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(AudiencePropertyHandlerTest.class));
		handler = new AudiencePropertyHandler();
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
		@SuppressWarnings("unchecked")
		Set<String> roles = (Set<String>) node.getProperty(AudiencePropertyHandler.PROP_AUDIENCE);
		assertEquals(3, roles.size());
		assertTrue(roles.contains("dummy"));
		assertTrue(roles.contains("technician"));
		assertTrue(roles.contains("salesman"));
	}

	@Test
	void testAddToModel() throws Exception {
		ToCNode node = new ToCNode();
		node.setURI(new URI("topic_1.dita"));
		node.setNavTitle("Topic 1");
		Resource topic = Factory.createTopic(model, "urn:topic1");
		node.setInformationUnit(topic);
		node.setProperty(AudiencePropertyHandler.PROP_AUDIENCE, Collections.singleton("dummy"));
		assertEquals(0, InformationUnits.getRelatedQualifications(topic).size());
		handler.addToModel(node, model);
		List<RDFNode> qualis = InformationUnits.getRelatedQualifications(topic);
		assertEquals(1, qualis.size());
	}

	/** Registers a subject scheme with qualification > role > the given role instance. */
	static void registerRole(String key, String label, String iri) {
		SubjectDef qualification = subjectDef("iirds.Qualification", "qualification",
				IirdsConstants.QUALIFICATION_CLASS_URI, "class-iri", null);
		SubjectDef role = subjectDef("iirds.Role", "role", IirdsConstants.ROLE_CLASS_URI, "class-iri", qualification);
		SubjectDefinitions.getDefault().register(subjectDef(key, label, iri, "instance-iri", role));
	}

	static SubjectDef subjectDef(String key, String label, String appid, String appname, SubjectDef parent) {
		SubjectDef def = new SubjectDef();
		def.setKeys(key);
		def.setLabel(label);
		def.setAppid(appid);
		def.setAppname(appname);
		if (parent != null) {
			def.addRelatedSubjectDef(RelationType.CHILD_OF, parent);
		}
		return def;
	}

	List<String> qualificationsAfterAdding(String rolename) throws Exception {
		ToCNode node = new ToCNode();
		node.setURI(new URI("topic_1.dita"));
		Resource topic = Factory.createTopic(model, "urn:topic1");
		node.setInformationUnit(topic);
		node.setProperty(AudiencePropertyHandler.PROP_AUDIENCE, Collections.singleton(rolename));
		handler.addToModel(node, model);
		List<String> uris = new ArrayList<>();
		InformationUnits.getRelatedQualifications(topic).forEach(q -> uris.add(q.asResource().getURI()));
		return uris;
	}

	@Test
	void testRoleFromSubjectSchemeByLabel() throws Exception {
		registerRole("ed.role.proofreader", "Proofreader", "https://example.org/role#Proofreader");
		assertEquals(List.of("https://example.org/role#Proofreader"), qualificationsAfterAdding("proofreader"),
				"An audience value matching the navtitle of a role in the subject scheme takes its IRI");
		Resource role = model.getResource("https://example.org/role#Proofreader");
		assertEquals("Proofreader", role.getProperty(RDFS.label).getString());
		assertTrue(role.hasProperty(RDF.type, model.getResource(IirdsConstants.ROLE_CLASS_URI)));
	}

	@Test
	void testRoleFromSubjectSchemeByKey() throws Exception {
		registerRole("translator", "Language Expert", "https://example.org/role#Translator");
		assertEquals(List.of("https://example.org/role#Translator"), qualificationsAfterAdding("translator"),
				"An audience value matching the key of a role in the subject scheme takes its IRI");
	}

	@Test
	void testRoleOutsideTheSubjectSchemeKeepsItsGeneratedIRI() throws Exception {
		List<String> uris = qualificationsAfterAdding("Unscheduled Visitor");
		assertEquals(1, uris.size());
		assertTrue(uris.get(0).startsWith("urn:md5:"), "Fallback stays the label based IRI: " + uris);
	}

}
