package org.iirds.dita.ot.plugin.model;

import java.util.Objects;

public class OntologyInstance extends OntologyCommon {

	OntologyClass ontoClass = null;

	public OntologyInstance() {
		super(false);
	}

	public OntologyClass getOntologyClass() {
		return ontoClass;
	}

	public void setOntologyClass(OntologyClass ontoClass) {
		this.ontoClass = ontoClass;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(ontoClass);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OntologyInstance other = (OntologyInstance) obj;
		return Objects.equals(ontoClass, other.ontoClass);
	}

	@Override
	public String toString() {
		return "OntologyInstance [ontoClass=" + ontoClass + ", iri=" + iri + ", label=" + label + ", keys=" + keys
				+ "]";
	}

}
