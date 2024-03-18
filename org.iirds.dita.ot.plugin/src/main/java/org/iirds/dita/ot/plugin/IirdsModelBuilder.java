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

import org.apache.jena.rdf.model.Model;
import org.iirds.dita.ot.plugin.model.ToCNode;

/**
 * Builds an iiRDS model
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public interface IirdsModelBuilder {

	static final String PROP_IRI = "iri";
	static final String PROP_IO_IRI = "infoobject.iri";

	IirdsModelBuilder addInfoObjects(ToCNode root);

	IirdsModelBuilder addPackage(ToCNode root);

	IirdsModelBuilder addInformationUnits(ToCNode toc);

	IirdsModelBuilder addDirectory(ToCNode root);

	IirdsModelBuilder addMetadata(ToCNode root);

	IirdsModelBuilder completeModel(ToCNode root);

	Model getModel();

}
