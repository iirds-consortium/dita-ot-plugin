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

package org.iirds.dita.ot.mdext;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.contrib.MetadataUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler implementation which extracts some metadata from
 * {@code <othermeta>} elements.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class OtherMetadataHandler implements IirdsMetadataHandler {
	static final String PROP_OTHERMETA = "metadata/othermeta";
	static Logger logger = LoggerFactory.getLogger(OtherMetadataHandler.class);

	@Override
	public String getName() {
		return "othermeta";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting othermeta metadata from {}", node.getNavTitle());
		List<Pair<String, String>> data = new ArrayList<>();
		for (Element metadata : MetadataUtils.getMetadataElements(document.getDocumentElement())) {
			List<Element> elements = XMLUtils.getChildElements(metadata, Constants.TOPIC_OTHERMETA, true);
			for (Element element : elements) {
				String name = element.getAttribute("name");
				String value = element.getAttribute("content");
				if (!StringUtils.isAnyBlank(name, value)) {
					data.add(Pair.of(name, value));
				}
			}
		}
		if (!data.isEmpty()) {
			node.setProperty(PROP_OTHERMETA, data);
		}
	}

	@Override
	public void addToModel(ToCNode node, Model model) {
		logger.info("Setting othermeta at {}", node.getNavTitle());
		IRIHandler iriHandler = Configuration.getDefault().getIRIHandler();
		@SuppressWarnings("unchecked")
		List<Pair<String, String>> pairs = (List<Pair<String, String>>) node.getProperty(PROP_OTHERMETA);
		Resource topic = node.getInformationUnit();

		if (pairs != null && !pairs.isEmpty() && topic != null) {
			pairs.forEach(s -> {
				if ("ProductVariant".equals(s.getKey())) {
					String iri = iriHandler.getMetadataIRI(node, IirdsConstants.PRODUCTVARIANT_CLASS_URI, s.getValue(),
							PROP_OTHERMETA);
					Resource r = Factory.createProductVariant(topic.getModel(), iri);
					Factory.setLabel(r, s.getValue(), node.getLanguage());
					InformationUnits.addRelatedProductVariant(topic, r);
				} else if ("Component".equals(s.getKey())) {
					String iri = iriHandler.getMetadataIRI(node, IirdsConstants.COMPONENT_CLASS_URI, s.getValue(),
							PROP_OTHERMETA);
					Resource r = Factory.createComponent(topic.getModel(), iri);
					Factory.setLabel(r, s.getValue(), node.getLanguage());
					InformationUnits.addRelatedComponent(topic, r);
				} else if ("InformationSubject".equals(s.getKey())) {
					String iri = iriHandler.getMetadataIRI(node, IirdsConstants.INFORMATIONSUBJECT_CLASS_URI,
							s.getValue(), PROP_OTHERMETA);
					Resource r = Factory.createInformationSubject(topic.getModel(), iri);
					Factory.setLabel(r, s.getValue(), node.getLanguage());
					InformationUnits.addInformationSubject(topic, r);
				} else if ("ProductLifeCyclePhase".equals(s.getKey())) {
					String iri = iriHandler.getMetadataIRI(node, IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI,
							s.getValue(), PROP_OTHERMETA);
					Resource r = Factory.createProductLifecyclePhase(topic.getModel(), iri);
					Factory.setLabel(r, s.getValue(), node.getLanguage());
					InformationUnits.addProductLifeCyclePhase(topic, r);
				}
			});
		}
	}
}
