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

package org.iirds.rdf.facade;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VocabularyFactoryTest {

	Model model;
	VocabularyFactory factory;

	@BeforeEach
	void init() {
		model = RDFConstants.createDefaultModel();
		model = RDFConstants.createInfModel(model);
		factory = new VocabularyFactory(model);

	}

	@Test
	void testGetTermByLabel() {
		Model model = RDFConstants.createDefaultModel();
		model = RDFConstants.createInfModel(model);
		Resource r = factory.getVocabularyTermByLabel(IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI, "Maintenance",
				true);
		assertNotNull(r);
		r = factory.getVocabularyTermByLabel(IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI, "Maintenance", false);
		assertNull(r);
	}

}
