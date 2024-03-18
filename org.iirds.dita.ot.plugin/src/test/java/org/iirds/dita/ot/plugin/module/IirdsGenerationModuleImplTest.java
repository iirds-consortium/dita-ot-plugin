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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.dita.dost.log.DITAOTLogger;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.PipelineHashIO;
import org.dita.dost.store.Store;
import org.dita.dost.store.StreamStore;
import org.dita.dost.util.Job;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.test.TestLogger;
import org.iirds.rdf.RDFConstants;
import org.iirds.rdf.facade.DirectoryNodes;
import org.iirds.rdf.facade.InformationUnits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
@SuppressWarnings("deprecation")
class IirdsGenerationModuleImplTest {

	DITAOTLogger logger;
	XMLUtils xmlUtils;
	Store store;

	@BeforeEach
	void init() {
		xmlUtils = new XMLUtils();
		logger = TestLogger.get(LoggerFactory.getLogger(IirdsGenerationModuleImplTest.class));
	}

	Job createJob(File path) throws IOException {
		Store store = new StreamStore(path, xmlUtils);
		Job job = new Job(path, store);
		Configuration.create(job, null);
		return job;
	}

	Model getModel(File rdfFile) {
		final Model[] models = new Model[1];
		assertDoesNotThrow(() -> models[0] = RDFConstants.readModels(rdfFile.toURI().toURL()),
				"RDF model cannot be read");
		return models[0];
	}

	@Test
	void testExecute() throws Exception {
		File outDir = Files.createTempDir();
		try {
			AbstractPipelineInput input = new PipelineHashIO();
			File tmpDir = new File(getClass().getResource("/dita.temp/extractor").toURI());
			Job job = createJob(tmpDir);
			job.setOutputDir(outDir);
			IirdsGenerationModuleImpl module = new IirdsGenerationModuleImpl();
			module.setLogger(logger);
			module.setJob(job);
			module.execute(input);

			File rdfFile = new File(new File(outDir, Constants.METAINF_DIRNAME), Constants.RDF_FILENAME);
			assertTrue(rdfFile.exists(), "File " + rdfFile + " has not been written");
			assertTrue(rdfFile.length() > 0L, "RDF file is empty");
			Model model = getModel(rdfFile);
			assertEquals(1, InformationUnits.getPackages(model).size());
			assertEquals(5, DirectoryNodes.getDirecotryNodes(model).size(), "Number of directory nodes is unexpected");
			assertEquals(1, InformationUnits.getDocuments(model).size());
			assertEquals(2, InformationUnits.getTopics(model).size());

		} finally {
			FileUtils.deleteDirectory(outDir);
		}
	}
}
