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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dita.dost.util.Constants;
import org.dita.dost.util.DitaClass;
import org.dita.dost.util.XMLUtils;
import org.w3c.dom.Element;

/**
 * Utilities for metadata extraction and conversion to iiRDS.
 * 
 * @author Martin Kreutzer, Empolis Informaion Management GmbH
 *
 */
public class MetadataUtils {

	private MetadataUtils() {

	}

	public static Optional<Element> getElementByPath(Element root, DitaClass... path) {
		Optional<Element> current = Optional.ofNullable(root);
		for (DitaClass p : path) {
			if (!current.isPresent())
				return Optional.empty();
			current = XMLUtils.getChildElement(current.get(), p);
			if (!current.isPresent())
				return current;
		}
		return current;
	}

	/**
	 * Retrieve the elements that contain the meta information. For topics it is
	 * {@code <prolog>} for map it is {@code <topicmeta>}.
	 * 
	 * @param root the root element of topic or map
	 * @return the element that is the parent of all metadata elements
	 */
	public static Optional<Element> getMetadataHolderElement(Element root) {
		if (Constants.TOPIC_TOPIC.matches(root)) {
			return XMLUtils.getChildElement(root, Constants.TOPIC_PROLOG);
		} else {
			return XMLUtils.getChildElement(root, Constants.MAP_TOPICMETA);
		}

	}

	public static List<Element> getMetadataElements(Element root) {

		// topic case
		if (Constants.TOPIC_TOPIC.matches(root)) {
			Optional<Element> metadata = getElementByPath(root, Constants.TOPIC_PROLOG, Constants.TOPIC_METADATA);
			if (metadata.isPresent()) {
				return Collections.singletonList(metadata.get());
			}
		} else if (Constants.MAP_MAP.matches(root)) {
			Optional<Element> topicmeta = XMLUtils.getChildElement(root, Constants.MAP_TOPICMETA);
			if (topicmeta.isPresent()) {
				return XMLUtils.getChildElements(topicmeta.get(), Constants.TOPIC_METADATA);
			}
		}
		return Collections.emptyList();
	}

}
