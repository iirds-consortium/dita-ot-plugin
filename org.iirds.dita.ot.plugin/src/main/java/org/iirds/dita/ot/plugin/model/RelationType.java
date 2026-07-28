/*******************************************************************************
 * Copyright 2024-2026 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org 
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

import java.util.Optional;

import org.dita.dost.util.Constants;
import org.w3c.dom.Element;

public enum RelationType {
	INSTANCE_OF, KIND_OF, NARRORER_OF, PART_OF, RELATED_OF, CHILD_OF;

	public static Optional<RelationType> getByElement(Element element) {
		if (Constants.SUBJECTSCHEME_HASINSTANCE.matches(element)) {
			return Optional.of(RelationType.INSTANCE_OF);
		} else if (Constants.SUBJECTSCHEME_HASKIND.matches(element)) {
			return Optional.of(RelationType.KIND_OF);
		} else if (Constants.SUBJECTSCHEME_HASNARROWER.matches(element)) {
			return Optional.of(RelationType.NARRORER_OF);
		} else if (Constants.SUBJECTSCHEME_HASPART.matches(element)) {
			return Optional.of(RelationType.PART_OF);
		} else if (Constants.SUBJECTSCHEME_HASRELATED.matches(element)) {
			return Optional.of(RelationType.RELATED_OF);
		}
		return Optional.empty();
	}

}
