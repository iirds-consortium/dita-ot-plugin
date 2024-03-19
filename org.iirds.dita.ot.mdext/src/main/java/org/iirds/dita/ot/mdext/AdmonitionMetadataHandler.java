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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler implementation, which adds an iiRDS information subject to
 * the topic according the found {@code <note>} and specialized elements and
 * their {@code type} attribute values. 
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class AdmonitionMetadataHandler implements IirdsMetadataHandler {
	static final String META_NOTETYPE = "metadata/note/@type";
	static Logger logger = LoggerFactory.getLogger(AdmonitionMetadataHandler.class);

	@Override
	public String getName() {
		return "admonition";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting admonitions from {}", node.getNavTitle());
		Set<String> severities = new HashSet<>();
		List<Element> elements = XMLUtils.getChildElements(document.getDocumentElement(), Constants.TOPIC_NOTE, true);
		for (Element element : elements) {
			String severity = element.getAttribute("type");
			if (!StringUtils.isBlank(severity)) {
				severities.add(severity);
			}
		}
		node.setProperty(META_NOTETYPE, severities);
	}

	@Override
	public void addToModel(ToCNode node, Model model) {
		logger.info("Setting admonitions at {}", node.getNavTitle());
		@SuppressWarnings("unchecked")
		Set<String> severities = (Set<String>) node.getProperty(META_NOTETYPE);
		Resource topic = node.getInformationUnit();
		if (severities != null && !severities.isEmpty() && topic != null) {
			severities.forEach(s -> {
				if ("warning".equals(s)) {
					Resource r = model.getResource(IirdsConstants.iiRDS_URI + "Warning");
					InformationUnits.addInformationSubject(topic, r);
				} else if ("danger".equals(s)) {
					Resource r = model.getResource(IirdsConstants.iiRDS_URI + "Danger");
					InformationUnits.addInformationSubject(topic, r);
				} else if ("caution".equals(s)) {
					Resource r = model.getResource(IirdsConstants.iiRDS_URI + "Caution");
					InformationUnits.addInformationSubject(topic, r);
				} else if ("notice".equals(s)) {
					Resource r = model.getResource(IirdsConstants.iiRDS_URI + "Notice");
					InformationUnits.addInformationSubject(topic, r);
				}
			});
		}
	}

}
