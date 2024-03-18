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

import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.DitaClass;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.rdf.RDFConstants;

/**
 * Get the iiRDS topic type from a node. The node must have
 * {@link ToCNode#getDitaClass()} set.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class TopicTypeMapper {

	protected static Resource topictype(String uri) {
		return RDFConstants.getRDFSInfModel().getResource(uri);
	}

	public Resource getTopicType(ToCNode node) {
		DitaClass ditaClass = node.getDitaClass();
		if (ditaClass == null) {
			return topictype("http://iirds.tekom.de/iirds#GenericConcept");
		} else if (Constants.TASK_TASK.matches(ditaClass)) {
			return topictype("http://iirds.tekom.de/iirds#GenericTask");
		} else if (Constants.TROUBLESHOOTING_TROUBLESHOOTING.matches(ditaClass)) {
			return topictype("http://iirds.tekom.de/iirds#GenericTroubleshooting");
		} else if (Constants.REFERENCE_REFERENCE.matches(ditaClass)) {
			return topictype("http://iirds.tekom.de/iirds#GenericReference");
		} else {
			return topictype("http://iirds.tekom.de/iirds#GenericConcept");
		}
	}
}
