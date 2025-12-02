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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.jena.rdf.model.Model;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.PipelineHashIO;
import org.dita.dost.store.Store;
import org.dita.dost.store.StreamStore;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.contrib.ComponentMetadataHandler;
import org.iirds.dita.ot.plugin.contrib.DefaultIRIHandler;
import org.iirds.dita.ot.plugin.test.TestLogger;
import org.iirds.rdf.RDFConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
class ConfigurationTest {

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
	void testCreateWithJob() {
		Configuration config = Configuration.create(job, null);
		assertEquals(".html", config.getHTMLExt());
		assertEquals("content", config.getContentPath());
		assertNotNull(config.getIirdsMetadataHandler());
		assertInstanceOf(CompositeMetadataHandler.class, config.getIirdsMetadataHandler());
		assertEquals(9, ((CompositeMetadataHandler) config.getIirdsMetadataHandler()).getMetadataHandlers().size());

		assertNotNull(config.getIRIHandler());
		assertInstanceOf(CompositeIRIHandler.class, config.getIRIHandler());
		CompositeIRIHandler composite = (CompositeIRIHandler) config.getIRIHandler();
		assertEquals(1, composite.getIRIHandlers().size());
		assertInstanceOf(DefaultIRIHandler.class, composite.getIRIHandlers().get(0));
	}

	@Test
	void testCreateWithJobInput() {
		PipelineHashIO input = new PipelineHashIO();
		input.setAttribute(Configuration.PARAM_CONTENTPATH, "html");
		input.setAttribute(Configuration.PARAM_OUTEXT, "xhtml");
		input.setAttribute(Configuration.PARAM_METADATAHANDLERS, " + critdates+component+all");
		input.setAttribute(Configuration.PARAM_IRIHANDLERS, "csv+default");
		Configuration config = Configuration.create(job, input);
		assertEquals("xhtml", config.getHTMLExt());
		assertEquals("html", config.getContentPath());
		assertNotNull(config.getIirdsMetadataHandler());
		assertInstanceOf(CompositeMetadataHandler.class, config.getIirdsMetadataHandler());
		CompositeMetadataHandler composite = (CompositeMetadataHandler) config.getIirdsMetadataHandler();
		assertEquals(2, composite.getMetadataHandlers().size());

		assertNotNull(config.getIRIHandler());
		assertInstanceOf(CompositeIRIHandler.class, config.getIRIHandler());
		CompositeIRIHandler compiri = (CompositeIRIHandler) config.getIRIHandler();
		assertEquals(1, compiri.getIRIHandlers().size());
		assertInstanceOf(DefaultIRIHandler.class, compiri.getIRIHandlers().get(0));
	}

	@Test
	void testCreateWithJobInputUnknownIriHandler() {
		PipelineHashIO input = new PipelineHashIO();
		input.setAttribute(Configuration.PARAM_IRIHANDLERS, "csv");
		Configuration config = Configuration.create(job, input);

		assertNotNull(config.getIRIHandler());
		assertInstanceOf(CompositeIRIHandler.class, config.getIRIHandler());
		CompositeIRIHandler compiri = (CompositeIRIHandler) config.getIRIHandler();
		assertEquals(0, compiri.getIRIHandlers().size());
	}

}
