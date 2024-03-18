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

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.iirds.rdf.IRIUtils;
import org.iirds.rdf.IirdsConstants;

/**
 * Helper class to retrieve vocabulary from the RDF model.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class VocabularyFactory {

	private Model model;

	public VocabularyFactory(Model model) {
		if (model == null) {
			throw new NullPointerException("Model must not be null");
		}
		this.model = model;

	}

	public Resource getVocabularyTermByLabel(String classURI, String label, boolean ignoreCase) {

		ResIterator iter = model.listSubjectsWithProperty(RDF.type, model.getResource(classURI));
		while (iter.hasNext()) {
			Resource subject = iter.next();
			StmtIterator stmtiter = subject.listProperties(RDFS.label);
			while (stmtiter.hasNext()) {
				Statement stmt = stmtiter.next();
				Literal l = stmt.getLiteral();
				if (ignoreCase) {
					if (StringUtils.equalsIgnoreCase(l.getString(), label)) {
						return subject;
					}
				} else if (StringUtils.equals(l.getString(), label)) {
					return subject;
				}
			}
		}
		return null;
	}

	public Resource createVocabularyTermByLabel(String classURI, String label, boolean ignoreCase) {
		Resource r = getVocabularyTermByLabel(classURI, label, ignoreCase);
		if (r == null) {
			r = model.getResource(IRIUtils.generateIRIBasedOnLabel(classURI, label));
			Factory.setType(r, classURI);
			Factory.setLabel(r, label, null);
		}
		return r;
	}

	public Resource getVocabularyTermByURI(String classURI, String uri) {
		Resource r = model.getResource(uri);
		if (!model.containsResource(r)) {
			Factory.setType(r, classURI);
		}
		return r;
	}

	public Resource getDirectoryNodeType(String uri) {
		return getVocabularyTermByURI(IirdsConstants.DIRECTORYNODETYPE_CLASS_URI, uri);
	}

	public Resource getTopicType(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.TOPICTYPE_CLASS_URI, label, ignoreCase);
	}

	public Resource getDocumentType(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.DOCUMENTTYPE_CLASS_URI, label, ignoreCase);
	}

	public Resource getPartyRole(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.PARTYROLE_CLASS_URI, label, ignoreCase);
	}

	public Resource getContentLifecycleStatusValue(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.CONTENTLIFECYCLESTATUSVALUE_CLASS_URI, label, ignoreCase);
	}

	public Resource getProduct(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.PRODUCTVARIANT_CLASS_URI, label, ignoreCase);
	}

	public Resource getComponent(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.COMPONENT_CLASS_URI, label, ignoreCase);
	}

	public Resource getInformationSubject(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.INFORMATIONSUBJECT_CLASS_URI, label, ignoreCase);
	}

	public Resource getProductFeature(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.PRODUCTFEATURE_CLASS_URI, label, ignoreCase);
	}

	public Resource getRole(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.ROLE_CLASS_URI, label, ignoreCase);
	}

	public Resource getSkillLevel(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.SKILLLEVEL_CLASS_URI, label, ignoreCase);
	}

	public Resource getProductLifecyclePhase(String label, boolean ignoreCase) {
		return createVocabularyTermByLabel(IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI, label, ignoreCase);
	}

}
