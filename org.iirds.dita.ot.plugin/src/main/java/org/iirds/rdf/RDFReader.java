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

package org.iirds.rdf;

import java.io.InputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public final class RDFReader {

	private RDFReader() {
	}

	/**
	 * Read and RDF file in RDF/XML notation and generates the RDF model.
	 * 
	 * @param in the input stream to read from
	 * @return the model
	 */
	public static Model readRDFModel(InputStream in) {
		Model model = ModelFactory.createDefaultModel();
		model.read(in, "", "RDF/XML");
		return model;
	}

}
