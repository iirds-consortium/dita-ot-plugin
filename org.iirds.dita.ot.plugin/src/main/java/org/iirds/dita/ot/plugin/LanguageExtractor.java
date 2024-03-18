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

package org.iirds.dita.ot.plugin;

import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.w3c.dom.Element;

/**
 * Extract the language from the XML of a node and store it at the node.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class LanguageExtractor {

	/**
	 * Extract the language from the element and store it at the node. It looks up
	 * the {@code xml:lang} attribute at the element. If not present it inherits the
	 * language from the {@link ToCNode#getParent() parent} node if available.
	 * 
	 * @param node    the node to set determine the language for
	 * @param element the XML element (usually the root element of the topic or map)
	 */
	public void extractLanguage(ToCNode node, Element element) {
		if (node.getLanguage() == null) {
			node.setLanguage(XMLUtils.getValue(element, "xml:lang"));
		} else if (node.getParent() != null) {
			node.setLanguage(node.getParent().getLanguage());
		}
	}

}
