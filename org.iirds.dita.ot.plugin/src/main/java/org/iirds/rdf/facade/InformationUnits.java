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

package org.iirds.rdf.facade;

import static org.iirds.rdf.IirdsConstants.DATEOFCREATION_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.DATEOFLASTMODIFICATION_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.DOCUMENT_CLASS_URI;
import static org.iirds.rdf.IirdsConstants.HASABSTRACT_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASCONTENTLIFECYCLESTATUS_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASDOCUMENTTYPE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASIDENTITY_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASPLANNINGTIME_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASRENDITION_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASSUBJECT_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.HASTOPICTYPE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.ISAPPLICABLEFORDOCUMENTTYPE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.ISPARTOFPACKAGE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.ISVERSIONOF_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.LANGUAGE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOCOMPONENT_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOEVENT_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOPRODUCTFEATURE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOPRODUCTLIFECYCLEPHASE_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOPRODUCTVARIANT_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RELATESTOQUALIFICATION_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.REQUIRESSUPPLY_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.RIGHTS_PROPERTY_URI;
import static org.iirds.rdf.IirdsConstants.TITLE_PROPERTY_URI;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.iirds.rdf.IirdsConstants;

/**
 * Facade to iiRDS InformationUnit
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class InformationUnits {

	private InformationUnits() {
	}

	public static List<RDFNode> getRelatedProductVariants(Resource unit) {
		return Factory.getPropertyObjects(unit, RELATESTOPRODUCTVARIANT_PROPERTY_URI);
	}

	public static List<RDFNode> getRelatedComponents(Resource unit) {
		return Factory.getPropertyObjects(unit, RELATESTOCOMPONENT_PROPERTY_URI);
	}

	public static List<RDFNode> getRelatedQualifications(Resource unit) {
		return Factory.getPropertyObjects(unit, RELATESTOQUALIFICATION_PROPERTY_URI);
	}

	public static void setVersionOf(Resource unit, Resource infoObject) {
		if (infoObject != null && unit != null) {
			unit.addProperty(unit.getModel().getProperty(ISVERSIONOF_PROPERTY_URI), infoObject);
		}
	}

	public static void addRelatedProductVariant(Resource unit, Resource product) {
		if (product != null) {
			unit.addProperty(unit.getModel().getProperty(RELATESTOPRODUCTVARIANT_PROPERTY_URI), product);
		}
	}

	public static void addRelatedComponent(Resource unit, Resource component) {
		if (component != null) {
			unit.addProperty(unit.getModel().getProperty(RELATESTOCOMPONENT_PROPERTY_URI), component);
		}
	}

	public static void addRelatedSupply(Resource unit, Resource product) {
		if (product != null) {
			unit.addProperty(unit.getModel().getProperty(REQUIRESSUPPLY_PROPERTY_URI), product);
		}
	}

	public static boolean isDocument(Resource unit) {
		return unit.hasProperty(RDF.type, unit.getModel().getResource(DOCUMENT_CLASS_URI));
	}

	public static void addDocumentType(Resource document, Resource doctype) {
		if (doctype != null) {
			document.addProperty(document.getModel().getProperty(
					isDocument(document) ? HASDOCUMENTTYPE_PROPERTY_URI : ISAPPLICABLEFORDOCUMENTTYPE_PROPERTY_URI),
					doctype);
		}
	}

	public static List<RDFNode> getDocumentTypes(Resource doc) {
		return Factory.getPropertyObjects(doc, HASDOCUMENTTYPE_PROPERTY_URI);
	}

	public static void addRights(Resource unit, String rights) {
		String t = StringUtils.trimToEmpty(rights);
		if (!StringUtils.isEmpty(t)) {
			unit.addProperty(unit.getModel().getProperty(RIGHTS_PROPERTY_URI), t);
		}
	}

	public static void setTitle(Resource unit, String title) {
		String t = StringUtils.trimToEmpty(title);
		if (!StringUtils.isEmpty(t)) {
			unit.addProperty(unit.getModel().getProperty(TITLE_PROPERTY_URI), t);
		}
	}

	public static void addInformationSubject(Resource unit, Resource subject) {
		if (subject != null) {
			unit.addProperty(subject.getModel().getProperty(HASSUBJECT_PROPERTY_URI), subject);
		}
	}

	public static void addRelatedProductFeature(Resource unit, Resource feature) {
		if (feature != null) {
			unit.addProperty(feature.getModel().getProperty(RELATESTOPRODUCTFEATURE_PROPERTY_URI), feature);
		}
	}

	public static void addEvent(Resource unit, Resource event) {
		if (event != null) {
			unit.addProperty(unit.getModel().getProperty(RELATESTOEVENT_PROPERTY_URI), event);
		}
	}

	public static void setTopicType(Resource topic, Resource topictype) {
		if (topictype != null) {
			topic.addProperty(topic.getModel().getProperty(HASTOPICTYPE_PROPERTY_URI), topictype);
		}
	}

	public static void addRelatedQualification(Resource unit, Resource qualification) {
		if (qualification != null) {
			unit.addProperty(qualification.getModel().getProperty(RELATESTOQUALIFICATION_PROPERTY_URI), qualification);
		}
	}

	public static void addProductLifeCyclePhase(Resource unit, Resource phase) {
		if (phase != null) {
			unit.addProperty(phase.getModel().getProperty(RELATESTOPRODUCTLIFECYCLEPHASE_PROPERTY_URI), phase);
		}
	}

	public static void addRendition(Resource unit, Resource rendition) {
		if (rendition != null) {
			unit.addProperty(rendition.getModel().getProperty(HASRENDITION_PROPERTY_URI), rendition);
		}
	}

	public static void addIdentity(Resource unit, Resource identity) {
		if (identity != null) {
			unit.addProperty(unit.getModel().getProperty(HASIDENTITY_PROPERTY_URI), identity);
		}
	}

	public static void addLanguage(Resource unit, String language) {
		if (language != null) {
			unit.addProperty(unit.getModel().getProperty(LANGUAGE_PROPERTY_URI), language);
		}
	}

	public static List<RDFNode> getContentLifecycleStatus(Resource unit) {
		return Factory.getPropertyObjects(unit, HASCONTENTLIFECYCLESTATUS_PROPERTY_URI);
	}

	public static void setContentLifecycleStatus(Resource unit, Resource status) {
		if (status != null) {
			Property prop = unit.getModel().getProperty(HASCONTENTLIFECYCLESTATUS_PROPERTY_URI);
			unit.addProperty(prop, status);
		}
	}

	public static void setPartOfPackage(Resource unit, Resource pack) {
		if (pack != null) {
			unit.addProperty(pack.getModel().getProperty(ISPARTOFPACKAGE_PROPERTY_URI), pack);
		}
	}

	public static void setAbstract(Resource unit, String value) {
		if (unit != null) {
			Statement node = unit.getProperty(unit.getModel().getProperty(HASABSTRACT_PROPERTY_URI));
			if (node != null) {
				// remove existing one
				unit.getModel().remove(node);
			}
			unit.addProperty(unit.getModel().getProperty(HASABSTRACT_PROPERTY_URI), value);
		}
	}

	public static String getAbstract(Resource unit) {
		Statement node = unit.getProperty(unit.getModel().getProperty(HASABSTRACT_PROPERTY_URI));
		return node != null ? node.getString() : null;
	}

	public static String getTitle(Resource unit) {
		Statement node = unit.getProperty(unit.getModel().getProperty(TITLE_PROPERTY_URI));
		return node != null ? node.getString() : null;
	}

	public static List<String> getRights(Resource unit) {
		List<RDFNode> nodes = Factory.getPropertyObjects(unit, RIGHTS_PROPERTY_URI);
		List<String> result = new ArrayList<>(nodes.size());
		nodes.stream().filter(RDFNode::isLiteral).forEach(n -> result.add(n.asLiteral().getString()));
		return result;

	}

	public static void addPlanningTime(Resource unit, Resource time) {
		if (time != null) {
			unit.addProperty(unit.getModel().getProperty(HASPLANNINGTIME_PROPERTY_URI), time);
		}
	}

	public static String getDateOfCreation(Resource unit) {
		Statement node = unit.getProperty(unit.getModel().getProperty(DATEOFCREATION_PROPERTY_URI));
		return node != null ? node.getString() : null;
	}

	public static String getDateOfLastModification(Resource unit) {
		Statement node = unit.getProperty(unit.getModel().getProperty(DATEOFLASTMODIFICATION_PROPERTY_URI));
		return node != null ? node.getString() : null;
	}

	public static void setDateOfCreation(Resource unit, String date) {
		if (date != null) {
			Statement node = unit.getProperty(unit.getModel().getProperty(DATEOFCREATION_PROPERTY_URI));
			if (node != null) {
				// remove existing one
				unit.getModel().remove(node);
			}
			unit.addProperty(unit.getModel().getProperty(DATEOFCREATION_PROPERTY_URI),
					unit.getModel().createTypedLiteral(date, org.iirds.rdf.IirdsConstants.XSD_URI + "dateTimeStamp"));
		}
	}

	public static void setDateOfLastModification(Resource unit, String date) {
		if (date != null) {
			Statement node = unit.getProperty(unit.getModel().getProperty(DATEOFLASTMODIFICATION_PROPERTY_URI));
			if (node != null) {
				// remove existing one
				unit.getModel().remove(node);
			}
			unit.addProperty(unit.getModel().getProperty(DATEOFLASTMODIFICATION_PROPERTY_URI),
					unit.getModel().createTypedLiteral(date, org.iirds.rdf.IirdsConstants.XSD_URI + "dateTimeStamp"));
		}
	}

	public static List<Resource> getDocuments(Model model) {
		return Factory.getResourcesOfType(model, IirdsConstants.DOCUMENT_CLASS_URI);
	}

	public static List<Resource> getTopics(Model model) {
		return Factory.getResourcesOfType(model, IirdsConstants.TOPIC_CLASS_URI);
	}

	public static List<Resource> getPackages(Model model) {
		return Factory.getResourcesOfType(model, IirdsConstants.PACKAGE_CLASS_URI);
	}

}
