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

package org.iirds.dita.ot.plugin.spi;

import org.iirds.dita.ot.plugin.model.ToCNode;

/**
 * Calculate IRIs for use in in iiRDS RDF
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public interface IRIHandler {

	/**
	 * The unique name of this IRI provider
	 * @return the name of this handler
	 */
	String getName();

	/**
	 * Generate IRI for a an iiRDS InformationObject. Implementation may use
	 * external services to lookup IRIs or use properties of the node
	 * 
	 * @param node the node for which to generated
	 * @return the generated IRI as String or null
	 */
	public String getInfoObjectIRI(ToCNode node);

	/**
	 * Generate IRI for a an iiRDS Topic or Document. Implementation may use
	 * external services to lookup IRIs or use properties of the node
	 * 
	 * @param node the node for which to generated
	 * @return the generated IRI as String or null
	 */
	public String getDocumentOrTopicIRI(ToCNode node);

	/**
	 * Generate IRI for a an iiRDS Package. Implementation may use external services
	 * to lookup IRIs or use properties of the node
	 * 
	 * @param node the node for which to generated (usually the root node)
	 * @return the generated IRI as String
	 */
	public String getPackageIRI(ToCNode node);

	/**
	 * Generate IRI for a a metadata string property. Implementation may use
	 * external services to lookup IRIs or use properties of the node
	 * 
	 * @param node     the node the metadata belongs to
	 * @param classURI the URI of the RDF metadata class
	 * @param label    something like a label of metadata value
	 * @param context  an additional context, usually the property name at the node
	 *                 hinting the source of the metadata value
	 * @return the generated IRI as String
	 */
	public String getMetadataIRI(ToCNode node, String classURI, String label, String context);

}
