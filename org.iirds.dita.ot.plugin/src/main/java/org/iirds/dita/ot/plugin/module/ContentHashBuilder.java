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

package org.iirds.dita.ot.plugin.module;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generate a hash value of XML content
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class ContentHashBuilder {

	protected Set<String> excludeAttributes = new HashSet<>();

	public ContentHashBuilder() {
		excludeAttributes.add("xtrc");
		excludeAttributes.add("xtrf");
		excludeAttributes.add("class");
		excludeAttributes.add("xmlns:dita-ot");
	}

	/**
	 * Generates a hash of the content of an element. Some technical attribute
	 * (xtrc, xtrf, xnlns:dita-ot), XML comments, and XML processing instructions
	 * are not taken into account. Implementation uses SHA-256.
	 * 
	 * @param element the element for which to generate a hash
	 * @return the hash (hex-encoded)
	 */
	public String getHash(Element element) {
		MessageDigest md = DigestUtils.getSha256Digest();
		md.reset();
		update(md, element);
		return Hex.encodeHexString(md.digest());
	}

	protected void update(final MessageDigest md, String text) {
		md.update(text.getBytes());
	}

	List<Attr> getSortedAttributes(Element element) {
		NamedNodeMap atts = element.getAttributes();
		List<Attr> sorted = new ArrayList<>(atts.getLength());
		for (int i = 0; i < atts.getLength(); i++) {
			sorted.add((Attr) atts.item(i));
		}
		sorted.sort((a, b) -> a.getName().compareTo(b.getName()));
		return sorted;
	}

	protected void update(final MessageDigest md, final Node root) {
		if (root != null) {
			if (root.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE || root.getNodeType() == Node.ATTRIBUTE_NODE) {
				// don't put comments and processing instructions into account
				return;
			} else if (root.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) root;
				update(md, "<");
				update(md, elem.getTagName());
				// attributes:
				getSortedAttributes(elem).forEach(att -> {
					if (!excludeAttributes.contains(att.getName())) {
						update(md, " href='");
						update(md, att.getValue());
						update(md, "'");
					}
				});
			} else if (root.getNodeType() == Node.TEXT_NODE) {
				update(md, root.getNodeValue());
			}
			if (root.hasChildNodes()) {
				final NodeList list = root.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					final Node childNode = list.item(i);
					update(md, childNode);
				}
			}
			if (root.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) root;
				update(md, "</");
				update(md, elem.getTagName());
				update(md, ">");
			}
		}
	}
}
