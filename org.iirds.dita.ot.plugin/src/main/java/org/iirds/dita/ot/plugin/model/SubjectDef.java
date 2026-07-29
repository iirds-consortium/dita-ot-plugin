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

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DITA subjectdef for metadata extraction
 *
 */
public class SubjectDef {

	@Override
	public String toString() {
		return "SubjectDef [label=" + label + ", ditaClass=" + ditaClass + ", keys=" + keys + ", appname=" + appname
				+ ", appid=" + appid + ", relations=" + relations + "]";
	}

	protected String label;
	protected String ditaClass;
	protected Set<String> keys = new HashSet<>();
	protected String appname;
	protected String appid;
	protected Map<RelationType, SubjectDef> relations = new EnumMap<>(RelationType.class);

	public String getDitaClass() {
		return ditaClass;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public void setDitaClass(String ditaClass) {
		this.ditaClass = ditaClass;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setKeys(String... keys) {
		if (keys != null) {
			this.keys = Set.of(keys);
		} else {
			this.keys = Collections.emptySet();
		}
	}

	public Map<RelationType, SubjectDef> getRelations() {
		return Collections.unmodifiableMap(this.relations);
	}

	public void addRelatedSubjectDef(RelationType relation, SubjectDef subject) {
		relations.put(relation, subject);
	}

	public SubjectDef getRelatedSubjectDef(RelationType relation) {
		return relations.get(relation);
	}

	public Set<String> getKeys() {
		return Collections.unmodifiableSet(this.keys);
	}
}
