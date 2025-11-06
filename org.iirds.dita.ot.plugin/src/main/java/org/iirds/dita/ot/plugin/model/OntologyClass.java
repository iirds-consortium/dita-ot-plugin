package org.iirds.dita.ot.plugin.model;

import java.util.Objects;

public class OntologyClass extends OntologyCommon {

	OntologyClass superClass = null;

	public OntologyClass() {
		super(true);
	}

	public OntologyClass getSuperClass() {
		return superClass;
	}

	public void setSuperClass(OntologyClass superClass) {
		this.superClass = superClass;
	}

	@Override
	public String toString() {
		return "OntologyClass [superClass=" + superClass + ", iri=" + iri + ", label=" + label + ", keys=" + keys + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(superClass);
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
		OntologyClass other = (OntologyClass) obj;
		return Objects.equals(superClass, other.superClass);
	}

}
