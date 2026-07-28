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
package org.iirds.dita.ot.plugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of all subject definitions found from parsing
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class SubjectDefinitions {

	private static SubjectDefinitions defaultDefinitions = new SubjectDefinitions();

	List<SubjectDef> subjectdefs = new ArrayList<>();

	Map<String, SubjectDef> keysToSubjectsdefs = new HashMap<>();

	public static SubjectDefinitions getDefault() {
		return defaultDefinitions;
	}

	/**
	 * Register a subject definition
	 * 
	 * @param subject the definition
	 */
	public void register(SubjectDef subject) {
		// TODO: overwrite, when key already exists or not?
		subjectdefs.add(subject);
		for (String key : subject.getKeys()) {
			keysToSubjectsdefs.put(key, subject);
		}
	}

	/**
	 * Get subject definition by key
	 * 
	 * @param key the key to lookup
	 * @return the found subject definition or null when not found
	 */
	public Optional<SubjectDef> getSubjectDefByKey(String key) {
		return Optional.ofNullable(keysToSubjectsdefs.get(key));
	}

}
