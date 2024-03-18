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
 * Facade to iiRDS Package
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class Packages {

	private Packages() {
	}

	public static void setIIRDSVersion(Resource pack, String version) {
		if (version != null) {
			pack.addProperty(pack.getModel().getProperty( IirdsConstants.IIRDSVERSION_PROPERTY_URI), version);
		}
	}

	public static void setFormatRestriction(Resource pack, String restriction) {
		if (restriction != null) {
			pack.addProperty(pack.getModel().getProperty( IirdsConstants.FORMATRESTRICTION_PROPERTY_URI),
					restriction);
		}
	}

}
