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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Resource;
import org.iirds.rdf.IirdsConstants;

/**
 * Facade to iiRDS Party
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class Parties {

	private Parties() {
	}

	public static void addPartyRole(Resource party, String roleURI) {
		Resource role = party.getModel().getResource(roleURI);
		party.addProperty(party.getModel().getProperty(IirdsConstants.HASPARTYROLE_PROPERTY_URI), role);
	}

	public static void addVCard(Resource party, Resource vcard) {
		party.addProperty(party.getModel().getProperty(IirdsConstants.HASVCARD_PROPERTY_URI), vcard);
	}

	/**
	 * Adds an email address to a vCard resource
	 * 
	 * @param vCard the vCard resource
	 * @param email the mail address as String
	 */
	public static void addEMail(Resource vCard, String email) {
		if (vCard == null || StringUtils.isBlank(email)) {
			return;
		}
		if (!email.startsWith("mailto:")) {
			try {
				email = "mailto:" + URLEncoder.encode(email, "utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			}
		}
		Resource mailResource = vCard.getModel().createResource(email);
		vCard.addProperty(vCard.getModel().getProperty(IirdsConstants.VCARD_EMAIL_PROPERTY_URI), mailResource);

	}
}
