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
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.FilterUtilsProxy;
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
 * Extracts products from the {@code product} attribute on the root element of
 * topics and and the root map. Groups names in the attribute value get ignored.
 * Only items outside of groups and group members are taken into account.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class ProductPropertyHandler implements IirdsMetadataHandler {
	static final String PROP_PRODUCT = "metadata/@product";
	static Logger logger = LoggerFactory.getLogger(ProductPropertyHandler.class);

	@Override
	public String getName() {
		return "product-p";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting product property from {}", node.getNavTitle());
		try {
			Element root = document.getDocumentElement();
			String attvalue = root.getAttribute(Constants.ATTRIBUTE_NAME_PRODUCT);
			if (StringUtils.isBlank(attvalue)) {
				return;
			}
			Set<String> products = new HashSet<>();
			Map<QName, List<String>> groups = FilterUtilsProxy.getGroups(attvalue);
			for (Map.Entry<QName, List<String>> group : groups.entrySet()) {
				products.addAll(group.getValue());
			}
			node.setProperty(PROP_PRODUCT, products);
		} catch (RuntimeException e) {
			logger.error("Failed to extract product property from {}", node.getNavTitle());
			throw e;
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {
		Configuration config = Configuration.getDefault();
		Resource infoUnit = root.getInformationUnit();
		@SuppressWarnings("unchecked")
		Set<String> prodnames = (Set<String>) root.getProperty(PROP_PRODUCT);
		if (infoUnit != null && prodnames != null && !prodnames.isEmpty()) {
			for (String prodname : prodnames) {
				String iri = config.getIRIHandler().getMetadataIRI(root, IirdsConstants.PRODUCTVARIANT_CLASS_URI,
						prodname, PROP_PRODUCT);
				Resource product = Factory.createProductVariant(infoUnit.getModel(), iri);
				Factory.setLabel(product, prodname, root.getLanguage());
				InformationUnits.addRelatedProductVariant(infoUnit, product);
			}
		}
	}

}
