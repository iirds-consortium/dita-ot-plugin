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
