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

import org.apache.jena.rdf.model.Resource;
import org.iirds.rdf.IirdsConstants;

/**
 * Facade to iiRDS Rendition
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class Renditions {

	private Renditions() {
	}

	public static void setSource(Resource rendition, String source) {
		rendition.addProperty(rendition.getModel().getProperty(IirdsConstants.SOURCE_PROPERTY_URI), source);
	}

	public static void setFormat(Resource rendition, String format) {
		if (format != null) {
			rendition.addProperty(rendition.getModel().getProperty(IirdsConstants.FORMAT_PROPERTY_URI), format);
		}
	}

}
