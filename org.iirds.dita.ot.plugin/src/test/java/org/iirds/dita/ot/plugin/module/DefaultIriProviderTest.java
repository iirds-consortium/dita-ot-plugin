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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.contrib.DefaultIRIHandler;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
class DefaultIriProviderTest {

	private DefaultIRIHandler provider;
	private ContentHashBuilder hasher;

	@BeforeEach
	void setup() {
		provider = new DefaultIRIHandler();
		hasher = new ContentHashBuilder();
	}

	private ToCNode setupToCNode(String path) throws Exception {
		URL url = getClass().getResource(path);
		Document doc = XMLUtils.getDocumentBuilder().parse(new File(url.toURI()));
		ToCNode node = new ToCNode(url.toURI());
		node.setTopicId(doc.getDocumentElement().getAttribute("id"));
		node.setContentHash(hasher.getHash(doc.getDocumentElement()));
		return node;
	}

	@Test
	void testGetDocumentOrTopicIRI() throws Exception {
		ToCNode node = setupToCNode("/dita.temp/extractor/test.ditamap");
		node.setIsDocument();
		String iri = provider.getDocumentOrTopicIRI(node);
		assertDoesNotThrow(() -> new URI(iri), "IRI is not a valid URI");
		assertTrue(iri.startsWith("urn:ditaid:"), "IRI should start with urn:ditaid: but was " + iri);
		assertTrue(iri.endsWith(":" + node.getContentHash()), "IRI must end with content hash");
		assertTrue(iri.contains(":" + URLEncoder.encode(node.getTopicId(), "utf-8") + ":"),
				"IRI must contain the topic ID");
	}

	@Test
	void testGetPackageIRI() throws Exception {
		ToCNode node = setupToCNode("/dita.temp/extractor/test.ditamap");
		node.setIsDocument();
		String iri = provider.getPackageIRI(node);
		assertDoesNotThrow(() -> new URI(iri), "IRI is not a valid URI");
		assertTrue(iri.startsWith("urn:uuid:"));
		assertDoesNotThrow(() -> UUID.fromString(iri.substring(9)), "UUID is not valid");
	}

}
