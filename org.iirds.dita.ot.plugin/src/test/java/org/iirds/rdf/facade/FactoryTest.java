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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.iirds.rdf.RDFConstants;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
class FactoryTest {

	@Test
	void testIsDocument() {
		Model model = RDFConstants.createDefaultModel();
		model = RDFConstants.createInfModel(model);

		Resource document = Factory.createDocument(model, "doc:1");
		assertTrue(InformationUnits.isDocument(document));

		Resource topic = Factory.createTopic(model, "topic:1");
		assertFalse(InformationUnits.isDocument(topic));
	}
}
