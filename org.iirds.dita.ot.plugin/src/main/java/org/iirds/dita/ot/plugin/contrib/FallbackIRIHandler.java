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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Resource;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.rdf.IRIUtils;

/**
 * Fallback implementation of {@link IRIHandler}. It provides UUID URNs.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class FallbackIRIHandler implements IRIHandler {

	Map<Pair<String, String>, String> mapping = new HashMap<>();

	@Override
	public String getName() {
		return "fallback";
	}

	@Override
	public String getInfoObjectIRI(ToCNode node) {
		return IRIUtils.generateUniqueIRI();
	}

	@Override
	public String getMetadataIRI(ToCNode node, String classURI, String label, String context) {
		Pair<String, String> key = Pair.of(classURI, label);
		String iri = mapping.get(key);
		if (iri != null) {
			return iri;
		}
		Resource r = Configuration.getDefault().getVocabularyFactory().getVocabularyTermByLabel(classURI, label, true);
		if (r != null) {
			iri = r.getURI();
		} else {
			iri = IRIUtils.generateUniqueIRI();
		}
		mapping.put(key, iri);
		return iri;
	}

	@Override
	public String getDocumentOrTopicIRI(ToCNode node) {
		return IRIUtils.generateUniqueIRI();
	}

	@Override
	public String getPackageIRI(ToCNode root) {
		return IRIUtils.generateUniqueIRI();
	}

	@Override
	public String toString() {
		return getName();
	}
}
