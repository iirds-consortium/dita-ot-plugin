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

import org.apache.jena.rdf.model.Model;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.w3c.dom.Document;

/**
 * Gets metadata from DITA content and updates the iiRDS RDF model with the found
 * metadata.
 * <p>
 * There are three stages:
 * </p>
 * <ol>
 * <li>Extract metadata from DITA and store it temporarily:
 * {@link #extractMetadata(ToCNode, Document)}</li>
 * <li>Set the metadata to the iiRDS information unit after all metadata
 * ahndlers have doen their extraction job:
 * {@link #addToModel(ToCNode, Model)}</li>
 * <li>Perform any metadata manipulations and completion of the iiRDS model
 * after all handlers have extracted and set their metadata to the RDF model:
 * {@link #completeModel(ToCNode, Model)}
 * </ol>
 * <p>
 * Metadata handlers might extract and set metadata or even just run any
 * post-processing to finalize the iiRDS RDF model.
 * </p>
 * <p>
 * Implementations should not store any state related information concerning an
 * exctraction in the instance. The instance must be re-usable for different
 * extractions. Store any state information in the {@link ToCNode} provided in
 * the instance methods called.
 * </p>
 * 
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public interface IirdsMetadataHandler {

	/**
	 * The name this metadata handler can be activated by from the {@code dita}
	 * command via parameters
	 * 
	 * @return the unique name, should be short and comprehensible to users of the
	 *         DITA OT.
	 */
	String getName();

	/**
	 * Extract metadata from an XML DOM of a topic or map. Implementations should
	 * store the extracted metadata via {@link ToCNode#setProperty(String, Object)}.
	 * The key of the property should reflect the element / attribute where the
	 * metadata come form in order to achieve unique keys for the properties.
	 * 
	 * @param node     the ToC node context
	 * @param document the DOM to extract from
	 */
	void extractMetadata(ToCNode node, Document document);

	/**
	 * Adds extracted metadata of a topic or map to the iiRDS RDF model.
	 * Implementations shall access the extracted metadata via
	 * {@link ToCNode#getProperty(String)}. The iiRDS {@code InformationUnit} has
	 * already been created when this method gets called and is accessible via
	 * {@link ToCNode#getInformationUnit()}
	 * 
	 * @param node  the ToC node context
	 * @param model the RDF model (graph) to work with and update
	 */
	void addToModel(ToCNode node, Model model);

	/**
	 * Perform any post-processes on the iiRDS RDF model, after all metadata
	 * handlers have done their extraction and model update jobs. It should be used
	 * to complete the model, e.g. in the case mandatory metadata are missing. The
	 * default implementation does nothing. This method gets called only for the
	 * root node (usually the document). The implementation is free to
	 * {@link ToCNode#getChildren() access} and manipulate any child nodes.
	 * 
	 * @param root  the ToC node context
	 * @param model the RDF model (graph) to work with
	 */
	default void completeModel(ToCNode root, Model model) {
	}
	
}
