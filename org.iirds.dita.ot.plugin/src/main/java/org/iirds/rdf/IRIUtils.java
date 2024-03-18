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

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods to generate IRIs
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public final class IRIUtils {

	private IRIUtils() {
	}

	/**
	 * Creates a unique IRI using the UUID scheme.
	 * 
	 * @return the IRI
	 */
	public static String generateUniqueIRI() {
		UUID uuid = UUID.randomUUID();
		return "urn:uuid:" + uuid.toString();
	}

	/**
	 * Create a unique IRI for the class URI and the label. When calling the method
	 * the same parameters again, the same IRI will be returned.
	 * 
	 * @param classURI the IRI if of the metadata class
	 * @param label    the label
	 * @return the IRI
	 */
	public static String generateIRIBasedOnLabel(String classURI, String label) {
		if (StringUtils.isEmpty(label)) {
			return generateUniqueIRI();
		}
		return "urn:md5:" + DigestUtils.md5Hex(classURI + "?label=" + label);
	}

}
