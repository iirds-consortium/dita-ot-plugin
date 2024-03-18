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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.facade.InformationUnits;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler that gets the first {@code <shortdesc>} element and creates
 * an iiRDS {@code has-abstract} properties from it.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class ShortdescMetadataHandler implements IirdsMetadataHandler {

	static final String META_SHORTDESC = "metadata/shortdesc";

	@Override
	public String getName() {
		return "shortdesc";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {

		List<Element> shortdesc = XMLUtils.getChildElements(document.getDocumentElement(), Constants.TOPIC_SHORTDESC,
				true);

		if (!shortdesc.isEmpty()) {
			node.setProperty(META_SHORTDESC, XMLUtils.getText(shortdesc.get(0)));
		}
	}

	@Override
	public void addToModel(ToCNode node, Model model) {
		Resource iu = node.getInformationUnit();
		String value = node.getPropertyAsString(META_SHORTDESC);
		if (iu != null && !StringUtils.isBlank(value)) {
			InformationUnits.setAbstract(iu, value);
		}
	}
}
