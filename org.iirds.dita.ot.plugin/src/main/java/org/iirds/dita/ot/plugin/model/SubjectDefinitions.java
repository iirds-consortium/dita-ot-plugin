package org.iirds.dita.ot.plugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubjectDefinitions {

	private static SubjectDefinitions defaultDefinitions = new SubjectDefinitions();

	List<SubjectDef> subjectdefs = new ArrayList<>();

	Map<String, SubjectDef> keysToSubjectsdefs = new HashMap<>();

	public static SubjectDefinitions getDefault() {
		return defaultDefinitions;
	}

	public void register(SubjectDef subject) {
		// TODO: overwrite, when key already exists or not?
		subjectdefs.add(subject);
		for (String key : subject.getKeys()) {
			keysToSubjectsdefs.put(key, subject);
		}
	}

	public Optional<SubjectDef> getSubjectDefByKey(String key) {
		return Optional.ofNullable(keysToSubjectsdefs.get(key));
	}

}
