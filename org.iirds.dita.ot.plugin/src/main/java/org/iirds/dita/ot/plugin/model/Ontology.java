package org.iirds.dita.ot.plugin.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Ontology {

	private Map<String, OntologyCommon> map = new HashMap<>();
	private static Ontology defaultOntology = new Ontology();

	public static Ontology getDefault() {
		return defaultOntology;
	}

	public void register(OntologyCommon object) {
		if (object.getIRI() != null) {
			map.put(object.getIRI(), object);
		}
	}

	public Optional<OntologyCommon> get(String iri) {
		return Optional.ofNullable(map.get(iri));
	}

	/**
	 * Return the first member matching the given key
	 * 
	 * @param key the key to search for
	 * @return the member or empty
	 */
	public Optional<OntologyCommon> getByKey(String key) {
		return map.values().stream().filter(c -> c.getKeys().contains(key)).findFirst();
	}

	@Override
	public String toString() {
		return "Ontology [map=" + map + "]";
	}
}
