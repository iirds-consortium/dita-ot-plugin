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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.rdf.IRIUtils;

/**
 * Default implementation of {@link IRIHandler}.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class DefaultIRIHandler implements IRIHandler {

	protected static final String DITAIDPREFIX = "urn:ditaid:";

	@Override
	public String getName() {
		return "default";
	}

	protected String encode(String part) {
		try {
			return URLEncoder.encode(part, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * The IRI of information objects follows the scheme {@value #DITAIDPREFIX} ":"
	 * &lt;id of topic or map&gt;. When no id present, then an UUID URN is returned.
	 */
	@Override
	public String getInfoObjectIRI(ToCNode node) {
		String id = node.getTopicId();
		if (StringUtils.isBlank(id)) {
			return IRIUtils.generateUniqueIRI();
		} else {
			return DITAIDPREFIX + encode(id);
		}
	}

	@Override
	public String getMetadataIRI(ToCNode node, String classURI, String label, String context) {
		Resource r = Configuration.getDefault().getVocabularyFactory().getVocabularyTermByLabel(classURI, label, true);
		if (r != null) {
			return r.getURI();
		}
		return IRIUtils.generateIRIBasedOnLabel(classURI, label);
	}

	/**
	 * The IRI of topic and maps follows the scheme {@value #DITAIDPREFIX} ":"
	 * &lt;id of topic or map&gt; ":" &lt;hash of content preprocessed by
	 * DITA-OT&gt;. When no id present, then an UUID URN is returned.
	 * 
	 * @see ToCNode#getContentHash()
	 */
	@Override
	public String getDocumentOrTopicIRI(ToCNode node) {
		String hash = node.getContentHash();
		String id = node.getTopicId();
		if (StringUtils.isBlank(hash) || StringUtils.isBlank(id)) {
			return IRIUtils.generateUniqueIRI();
		} else {
			return DITAIDPREFIX + encode(id) + ":" + hash;
		}
	}

	/**
	 * The IRI of a package is freshly generated UUID.
	 * 
	 * @see IRIUtils#generateUniqueIRI()
	 */
	@Override
	public String getPackageIRI(ToCNode root) {
		return IRIUtils.generateUniqueIRI();
	}

	@Override
	public String toString() {
		return getName();
	}
}
