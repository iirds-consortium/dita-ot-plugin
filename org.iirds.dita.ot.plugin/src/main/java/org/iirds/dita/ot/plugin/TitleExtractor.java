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

import static org.dita.dost.util.Constants.ATTRIBUTE_NAME_NAVTITLE;

import org.apache.commons.lang3.StringUtils;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Extract the title of a topic or map.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class TitleExtractor {

	/**
	 * Get the title of a map
	 * 
	 * @param map the map root element to analyze
	 * @return the title of the map
	 */
	public String getMapTitle(Element map) {
		final Element title = XMLUtils.getElementNode(map, Constants.TOPIC_TITLE);
		if (title != null) {
			return XMLUtils.getText(title);
		}
		final Attr titleAttr = map.getAttributeNode("title");
		if (titleAttr != null) {
			return titleAttr.getValue();
		}
		return null;
	}

	/**
	 * Get the title of a topic
	 * 
	 * @param topic the topic root element to analyze
	 * @return the title of the topic
	 */
	public String getTopicTitle(Element topic) {
		final Element title = XMLUtils.getElementNode(topic, Constants.TOPIC_TITLE);
		if (title != null) {
			return XMLUtils.getText(title);
		}
		return null;
	}

	/**
	 * Gets the title belonging to a topicref (or specialized topicref)
	 * element.Implementation looks first for 
	 * topicmeta/navtitle, then for linktext element, then for navtitle attribute
	 * 
	 * @param topicref the topicref element
	 * @return the tile or null
	 */
	public String getNavTitle(Element topicref) {

		// get metadata element
		Element topicmeta = XMLUtils.getElementNode(topicref, Constants.MAP_TOPICMETA);
		if (topicmeta != null) {

			// prefer navtitle element over navtitle attribute
			Element navtitle = XMLUtils.getElementNode(topicmeta, Constants.TOPIC_NAVTITLE);
			if (navtitle != null) {
				return StringUtils.normalizeSpace(XMLUtils.getText(navtitle));
			}
			Element linktext = XMLUtils.getElementNode(topicmeta, Constants.MAP_LINKTEXT);
			if (linktext != null) {
				return StringUtils.normalizeSpace(XMLUtils.getText(linktext));
			}
		}
		final Attr navAttr = topicref.getAttributeNode(ATTRIBUTE_NAME_NAVTITLE);
		if (navAttr != null) {
			return StringUtils.normalizeSpace(navAttr.getValue());
		}
		return null;
	}

}
