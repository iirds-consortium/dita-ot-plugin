package org.iirds.dita.ot.plugin.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class OntologyCommon {

	protected String iri;
	protected String label;
	protected final boolean isclass;
	protected Set<String> keys = new HashSet<>();

	protected OntologyCommon(boolean isclass) {
		this.isclass = isclass;
	}

	public boolean isClass() {
		return isclass;
	}

	public boolean isInstance() {
		return !isclass;
	}

	public String getIRI() {
		return iri;
	}

	public void setIRI(String iri) {
		this.iri = iri;
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

	public Set<String> getKeys() {
		return Collections.unmodifiableSet(this.keys);
	}

	@Override
	public int hashCode() {
		return Objects.hash(iri, isclass, label);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OntologyCommon other = (OntologyCommon) obj;
		return Objects.equals(iri, other.iri) && isclass == other.isclass && Objects.equals(label, other.label);
	}

}
