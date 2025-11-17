package org.iirds.dita.ot.plugin.module;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.RelationType;
import org.iirds.dita.ot.plugin.model.SubjectDef;
import org.iirds.dita.ot.plugin.model.SubjectDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Extracts subject definitions from a map.
 * 
 * @author kreu09
 *
 */
public class SubjectExtractor {
	static Logger logger = LoggerFactory.getLogger(SubjectExtractor.class);

	protected static boolean isRelationElement(final Element elem) {
		return Constants.SUBJECTSCHEME_HASKIND.matches(elem) || Constants.SUBJECTSCHEME_HASNARROWER.matches(elem)
				|| Constants.SUBJECTSCHEME_HASRELATED.matches(elem)
				|| Constants.SUBJECTSCHEME_HASINSTANCE.matches(elem);
	}

	protected static boolean isSubjectSchemeRelated(final Element elem) {
		return Constants.SUBJECTSCHEME_SUBJECTDEF.matches(elem) || Constants.SUBJECTSCHEME_HASKIND.matches(elem)
				|| Constants.SUBJECTSCHEME_SUBJECTSCHEME.matches(elem)
				|| Constants.SUBJECTSCHEME_SUBJECTHEAD.matches(elem)
				|| Constants.SUBJECTSCHEME_HASNARROWER.matches(elem) || Constants.SUBJECTSCHEME_HASRELATED.matches(elem)
				|| Constants.SUBJECTSCHEME_HASINSTANCE.matches(elem) || Constants.SUBJECTSCHEME_SCHEMEREF.matches(elem);
	}

	/**
	 * Extracts subject definitions from a map
	 * 
	 * @param doc the DOM of the map
	 */
	public void processMap(final Document doc) {
		Element map = doc.getDocumentElement();
		try {
			for (Element elem : XMLUtils.getChildElements(map)) {
				if (Constants.MAP_TOPICREF.matches(elem)) {
					handleTopicref(elem, Optional.empty(), Optional.empty());
				}
			}
		} finally {
		}
	}

	protected void handleTopicref(Element topicref, Optional<SubjectDef> parentSubject,
			Optional<RelationType> parentRelation) {
		Optional<SubjectDef> subject = subjectdefFromElement(topicref);
		Optional<RelationType> relationType = RelationType.getByElement(topicref);

		if (subject.isPresent()) {
			if (parentSubject.isPresent()) {
				subject.get().addRelatedSubjectDef(
						parentRelation.isPresent() ? parentRelation.get() : RelationType.CHILD_OF, parentSubject.get());
			}
			parentSubject = subject;
			subject.ifPresent(SubjectDefinitions.getDefault()::register);
		}
		try {
			for (Element elem : XMLUtils.getChildElements(topicref)) {
				if (Constants.MAP_TOPICREF.matches(elem)) {
					handleTopicref(elem, parentSubject, relationType.isEmpty() ? parentRelation : relationType);
				}
			}
		} finally {
		}
	}

	protected static Optional<SubjectDef> subjectdefFromElement(Element element) {
		if (Constants.SUBJECTSCHEME_SUBJECTDEF.matches(element)) {
			String title = Configuration.getDefault().getTitleExtractor().getNavTitle(element);
			logger.info("...subjectdef title is " + title);
			SubjectDef subject = new SubjectDef();
			subject.setLabel(title);
			subject.setKeys(StringUtils.split(XMLUtils.getValue(element, Constants.ATTRIBUTE_NAME_KEYS)));
			subject.setDitaClass(XMLUtils.getValue(element, Constants.ATTRIBUTE_NAME_CLASS));
			Optional<Element> mdElement = XMLUtils.getChildElement(element, Constants.MAP_TOPICMETA);
			if (mdElement.isPresent()) {
				Optional<Element> resElement = XMLUtils.getChildElement(mdElement.get(), Constants.TOPIC_RESOURCEID);
				if (resElement.isPresent()) {
					String appid = XMLUtils.getValue(resElement.get(), "appid");
					subject.setAppid(appid);
					String appname = XMLUtils.getValue(resElement.get(), "appname");
					subject.setAppname(appname);
				}
			}
			return Optional.of(subject);
		} else {
			return Optional.empty();
		}
	}

}
