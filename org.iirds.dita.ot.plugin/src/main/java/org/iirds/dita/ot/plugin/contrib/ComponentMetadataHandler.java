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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler that extracts iiRDS components from {@code <component>}
 * elements of maps and topics.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class ComponentMetadataHandler implements IirdsMetadataHandler {
	static final String PROP_COMPONENT = "metadata/prolog/prodinfo/component";
	static Logger logger = LoggerFactory.getLogger(ComponentMetadataHandler.class);

	@Override
	public String getName() {
		return "component";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting component from {}", node.getNavTitle());
		try {
			Element root = document.getDocumentElement();
			Set<String> components = new HashSet<>();
			Optional<Element> metadata = MetadataUtils.getMetadataHolderElement(root);
			if (metadata.isPresent()) {
				List<Element> prodInfos = XMLUtils.getChildElements(metadata.get(), Constants.TOPIC_PRODINFO, true);
				for (Element prodInfo : prodInfos) {
					List<Element> componentElements = XMLUtils.getChildElements(prodInfo, Constants.TOPIC_COMPONENT);
					componentElements.forEach(component -> {
						String value = XMLUtils.getText(component);
						if (!StringUtils.isBlank(value)) {
							components.add(value.trim());
						}
					});
				}
			}

			if (!components.isEmpty()) {
				node.setProperty(PROP_COMPONENT, components);
			}
		} catch (

		RuntimeException e) {
			logger.error("Failed to extract component from {}", node.getNavTitle());
			throw e;
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {
		Configuration config = Configuration.getDefault();
		Resource infoUnit = root.getInformationUnit();
		@SuppressWarnings("unchecked")
		Set<String> components = (Set<String>) root.getProperty(PROP_COMPONENT);
		if (infoUnit != null && components != null && !components.isEmpty()) {
			for (String component : components) {
				String iri = config.getIRIHandler().getMetadataIRI(root, IirdsConstants.COMPONENT_CLASS_URI, component,
						PROP_COMPONENT);
				Resource compR = Factory.createComponent(infoUnit.getModel(), iri);
				Factory.setLabel(compR, component, root.getLanguage());
				InformationUnits.addRelatedComponent(infoUnit, compR);
			}
		}
	}

}
