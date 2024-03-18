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

package org.iirds.rdf.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.iirds.rdf.IirdsConstants;

/**
 * Facade to iiRDS DirectoryNode
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class DirectoryNodes {

	private DirectoryNodes() {
	}

	public static void addChild(Resource parent, Resource child) {
		if (parent == null)
			return;
		Resource firstChild = getFirstChild(parent);
		if (firstChild == null) {
			addFirstChild(parent, child);
		} else {
			Resource last = firstChild;
			do {
				Resource next = getNextSibling(last);
				if (next == null) {
					addNextSibling(last, child);
				}
				last = next;
			} while (last != null);
		}
	}

	public static List<Resource> getChilden(Resource node) {
		List<Resource> result = new ArrayList<>();

		if (node != null) {
			Resource child = getFirstChild(node);
			while (child != null) {
				if (!IirdsConstants.NIL_RESOURCE_URI.equals(node.asNode().getURI())) {
					result.add(child);
					child = getNextSibling(child);
				} else {
					child = null;
				}
			}
		}
		return result;
	}

	public static Resource getFirstChild(Resource node) {
		return node == null ? null
				: node.getPropertyResourceValue(
						node.getModel().createProperty(IirdsConstants.HASFIRST_CHILD_PROPERTY_URI));
	}

	public static Resource getNextSibling(Resource node) {
		return node == null ? null
				: node.getPropertyResourceValue(
						node.getModel().createProperty(IirdsConstants.HASNEXT_SIBLING_PROPERTY_URI));
	}

	public static void setInformationUnit(Resource node, Resource unit) {
		if (unit != null) {
			node.addProperty(node.getModel().createProperty(IirdsConstants.RELATESTOINFORMATIONUNIT_PROPERTY_URI),
					unit);
		}
	}

	public static void setDirectoryType(Resource node, Resource nodeType) {
		if (nodeType != null) {
			node.addProperty(node.getModel().createProperty(IirdsConstants.HASDIRCTORYSTRUCTURETYPE_PROPERTY_URI),
					nodeType);
		}
	}

	public static void addFirstChild(Resource node, Resource childNode) {
		node.addProperty(node.getModel().createProperty(IirdsConstants.HASFIRST_CHILD_PROPERTY_URI), childNode);
	}

	public static void addNextSibling(Resource node, Resource siblingNode) {
		node.addProperty(node.getModel().createProperty(IirdsConstants.HASNEXT_SIBLING_PROPERTY_URI), siblingNode);
	}

	public static List<Resource> getDirecotryNodes(Model model) {
		return Factory.getResourcesOfType(model, IirdsConstants.DIRECTORYNODE_CLASS_URI);
	}

}
