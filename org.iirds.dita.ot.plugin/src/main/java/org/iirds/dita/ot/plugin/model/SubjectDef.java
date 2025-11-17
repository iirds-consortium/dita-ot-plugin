package org.iirds.dita.ot.plugin.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DITA subjectdef
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
