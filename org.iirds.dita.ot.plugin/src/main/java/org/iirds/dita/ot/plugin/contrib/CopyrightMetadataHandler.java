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
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler that extracts iiRDS rights from {@code <copyright>} elements
 * of maps and topics.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class CopyrightMetadataHandler implements IirdsMetadataHandler {
	static final String PROP_COPYRIGHT = "metadata/prolog/copyright";
	static Logger logger = LoggerFactory.getLogger(CopyrightMetadataHandler.class);

	@Override
	public String getName() {
		return "copyright";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting copyright from {}", node.getNavTitle());
		try {
			Element root = document.getDocumentElement();
			Set<String> rights = new HashSet<>();
			Optional<Element> prolog = MetadataUtils.getMetadataHolderElement(root);
			if (prolog.isPresent()) {
				List<Element> copyrightElements = XMLUtils.getChildElements(prolog.get(), Constants.TOPIC_COPYRIGHT);
				for (Element copyrightElement : copyrightElements) {
					Optional<Element> yearElement = XMLUtils.getChildElement(copyrightElement,
							Constants.TOPIC_COPYRYEAR);
					Optional<Element> holderElement = XMLUtils.getChildElement(copyrightElement,
							Constants.TOPIC_COPYRHOLDER);
					StringBuilder sb = new StringBuilder();
					sb.append("Copyright");
					if (yearElement.isPresent()) {
						String year = yearElement.get().getAttribute("year");
						if (!StringUtils.isBlank(year)) {
							sb.append(' ').append(year);
						}
					}
					if (holderElement.isPresent()) {
						String holder = XMLUtils.getText(holderElement.get());
						if (!StringUtils.isBlank(holder)) {
							sb.append(' ').append(holder);
						}
					}
					if (yearElement.isPresent() || holderElement.isPresent()) {
						rights.add(sb.toString());
					}
				}
			}
			if (!rights.isEmpty()) {
				node.setProperty(PROP_COPYRIGHT, rights);
			}
		} catch (RuntimeException e) {
			logger.error("Failed to extract copyright from {}", node.getNavTitle());
			throw e;
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {
		Resource infoUnit = root.getInformationUnit();
		@SuppressWarnings("unchecked")
		Set<String> rights = (Set<String>) root.getProperty(PROP_COPYRIGHT);
		if (infoUnit != null && rights != null && !rights.isEmpty()) {
			for (String right : rights) {
				InformationUnits.addRights(infoUnit, right);
			}
		}
	}

}
