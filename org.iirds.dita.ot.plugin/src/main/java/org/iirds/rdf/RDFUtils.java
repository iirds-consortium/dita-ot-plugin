/*******************************************************************************
 * Copyright 2024 Empolis Information Management GmbH
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

package org.iirds.rdf;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * RDF utilities
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class RDFUtils {

	private RDFUtils() {
	}

	public static boolean isLabelStatement(Statement stmt) {
		return RDFS.label.equals(stmt.getPredicate());
	}

	public static boolean isTypeStatement(Statement stmt) {
		return RDF.type.equals(stmt.getPredicate());
	}

	/**
	 * Get RDF type URIs of a resoruce
	 * 
	 * @param model
	 *            the RDF model
	 * @param resource
	 *            the resource
	 * @return set of type URIs, excluding those starting with
	 *         {@code http://www.w3.org/2000/01/rdf} and
	 *         {@code http://www.w3.org/1999/02/22-rdf}
	 */
	public static Set<String> getTypes(Model model, RDFNode resource) {
		Set<String> types = new HashSet<>();
		if (resource.isResource()) {
			for (StmtIterator iter = model.listStatements(resource.asResource(), RDF.type, (RDFNode) null); iter
					.hasNext();) {
				Statement stmt = iter.next();
				Resource typeResource = stmt.getResource();
				if (typeResource.isURIResource()) {
					String uri = typeResource.getURI();
					types.add(uri);
				}

			}
		} else if (resource.isLiteral()) {
			types.add("http://www.w3.org/2000/01/rdf-schema#Literal");

			String uri = resource.asLiteral().getDatatypeURI();
			if (uri != null) {
				// TODO: is this correct? specification says you can use
				// xsd: for XSD datatypes
				if (uri.startsWith("xsd:")) {
					uri = IirdsConstants.XSD_URI + uri.substring(4);
				}
				types.add(uri);
			}
		}
		return types;
	}

	/**
	 * Get a map of resources to a the type URIs (excluding Literals)
	 * 
	 * @param model
	 *            the model to analyze
	 * @return the map of Resource to set fo type URIs
	 */
	public static Map<Resource, Set<String>> getTypeMap(Model model) {
		Map<Resource, Set<String>> result = new HashMap<>();
		for (ResIterator iter = model.listSubjects(); iter.hasNext();) {
			Resource r = iter.next();
			result.put(r, getTypes(model, r));
		}
		return result;
	}

	public static String toString(Model model) {
		String format = "RDF/XML-ABBREV";
		StringWriter writer = new StringWriter();
		RDFWriter rdfWriter = model.getWriter(format);
		rdfWriter.setProperty("tab", "1");
		rdfWriter.setProperty("width", "80");
		rdfWriter.write(model, writer, "file:///");
		return writer.toString();
	}

	/**
	 * Get the property for a property URI
	 * 
	 * @param model the RDF model
	 * @param propertyURI the IRI of the property
	 * @return the property
	 */
	public static Property getProperty(Model model, String propertyURI) {
		return model.getProperty(propertyURI);
	}

	public static boolean hasAttribute(Resource resource, String propertyURI) {
		return resource.getProperty(getProperty(resource.getModel(), propertyURI)) != null;
	}
}
