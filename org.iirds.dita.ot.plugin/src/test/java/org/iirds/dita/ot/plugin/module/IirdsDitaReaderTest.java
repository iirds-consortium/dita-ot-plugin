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

package org.iirds.dita.ot.plugin.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.store.Store;
import org.dita.dost.store.StreamStore;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.test.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
class IirdsDitaReaderTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(IirdsDitaReaderTest.class));
	}

	Job createJob(File path) throws IOException {
		Store store = new StreamStore(path, xmlUtils);
		Job job = new Job(path, store);
		Configuration.create(job, null);
		return job;
	}

	@Test
	void testReadFile() throws Exception {
		File tmpDir = new File(getClass().getResource("/dita.temp/extractor").toURI());
		Job job = createJob(tmpDir);
		IirdsDitaReader reader = new IirdsDitaReader();
		reader.setJob(job);
		reader.setLogger(logger);
		File ditamapFile = new File(tmpDir, "test.ditamap");
		reader.read(ditamapFile);
		ToCNode toc = reader.getToC();
		assertEquals("Document title", toc.getTitle(), "Unexpected title");
		assertEquals("en", toc.getLanguage());
		assertEquals(2, toc.getChildren().size(), "Number or child ToC nodes is wrong");
		assertEquals("Topic w/o content", toc.getChildren().get(0).getTitle(), "Unexpected title");
		ToCNode topichead = toc.getChildren().get(1);
		assertEquals("Some topics", topichead.getNavTitle());
		assertEquals(2, topichead.getChildren().size());
		assertEquals("Topic 1", topichead.getChildren().get(0).getTitle(), "Unexpected title");
		assertEquals("Task 1", topichead.getChildren().get(1).getTitle(), "Unexpected title");
	}
}
