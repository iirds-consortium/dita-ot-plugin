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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.iirds.dita.ot.plugin.IirdsModelSerializer;
import org.iirds.rdf.IirdsConstants;

/**
 * @author Martin Kreutzer, Empolis Information Management GmbH
 */
public class IirdsModelSerializerImpl implements IirdsModelSerializer {

	protected List<Resource> getPrettyTypes(Model model) {
		List<Resource> result = new ArrayList<>();
		result.add(model.getResource(IirdsConstants.PACKAGE_CLASS_URI));
		// result.add(model.getResource(IirdsConstants.IDENTITYDOMAIN_CLASS_URI));
		result.add(model.getResource(IirdsConstants.DOCUMENT_CLASS_URI));
		result.add(model.getResource(IirdsConstants.TOPIC_CLASS_URI));
		result.add(model.getResource(IirdsConstants.FRAGMENT_CLASS_URI));
		// not this:
		// result.add(model.getResource(IirdsConstants.DIRECTORYNODE_CLASS_URI));
		return result;
	}

	protected RDFWriter getPrettyXMLWriter(Model iirds) {
		String format = "RDF/XML-ABBREV";
		List<Resource> pretty = getPrettyTypes(iirds);

		RDFWriter rdfWriter = iirds.getWriter(format);
		rdfWriter.setProperty("showXmlDeclaration", true);
		rdfWriter.setProperty("tab", "1");
		rdfWriter.setProperty("width", "120");
		rdfWriter.setProperty("prettyTypes", pretty.toArray(new Resource[] {}));
		return rdfWriter;
	}

	@Override
	public void generateRDFXML(Model model, Writer writer) {
		RDFWriter rdfWriter = getPrettyXMLWriter(model);
		rdfWriter.write(model, writer, "file:///");
	}

}
