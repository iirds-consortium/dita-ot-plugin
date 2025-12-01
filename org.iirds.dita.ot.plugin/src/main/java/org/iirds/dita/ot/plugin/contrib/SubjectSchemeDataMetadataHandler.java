package org.iirds.dita.ot.plugin.contrib;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.RelationType;
import org.iirds.dita.ot.plugin.model.SubjectDef;
import org.iirds.dita.ot.plugin.model.SubjectDefinitions;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubjectSchemeDataMetadataHandler implements IirdsMetadataHandler {

	static Logger logger = LoggerFactory.getLogger(SubjectSchemeDataMetadataHandler.class);

	static final String PROP_SUBJECTSCHEME = "metadata/subjectscheme";
	static final String INSTANCE_IRI = "instance-iri";
	static final String CLASS_IRI = "class-iri";

	@Override
	public String getName() {
		return "subjectscheme";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		List<SubjectDef> list = new ArrayList<>();
		node.setProperty(PROP_SUBJECTSCHEME, list);
		Optional<Element> metadata = MetadataUtils.getMetadataHolderElement(document.getDocumentElement());
		if (metadata.isPresent()) {
			List<Element> dataElements = XMLUtils.getChildElements(metadata.get(), Constants.TOPIC_DATA, true);
			for (Element data : dataElements) {
				String keyref = XMLUtils.getValue(data, Constants.ATTRIBUTE_NAME_KEYREF);
				if (keyref != null) {
					Optional<SubjectDef> subjectdef = SubjectDefinitions.getDefault().getSubjectDefByKey(keyref);
					if (subjectdef.isPresent()) {
						logger.info("Found " + subjectdef.get());
						list.add(subjectdef.get());
					}
				}
			}
		}
	}

	static boolean isClass(SubjectDef subjectdef) {
		return subjectdef != null && CLASS_IRI.equals(subjectdef.getAppname());
	}

	static SubjectDef findClassOf(SubjectDef subjectdef) {
		SubjectDef parent = subjectdef.getRelations().get(RelationType.KIND_OF);
		if (parent != null) {
			if (isClass(parent))
				return parent;
			return findClassOf(parent);
		}
		parent = subjectdef.getRelations().get(RelationType.CHILD_OF);
		if (parent != null) {
			if (isClass(parent))
				return parent;
			return findClassOf(parent);
		}
		parent = subjectdef.getRelations().get(RelationType.NARRORER_OF);
		if (parent != null) {
			if (isClass(parent))
				return parent;
			return findClassOf(parent);
		}
		return null;
	}

	boolean isType(SubjectDef clazz, String classUri) {
		return clazz != null && classUri.equals(clazz.getAppid());
	}

	@Override
	public void addToModel(ToCNode root, Model model) {

		// TODO: label language

		Resource infoUnit = root.getInformationUnit();
		@SuppressWarnings("unchecked")
		List<SubjectDef> concepts = (List<SubjectDef>) root.getProperty(PROP_SUBJECTSCHEME);
		if (concepts != null) {
			for (SubjectDef concept : concepts) {
				String iri = concept.getAppid();
				String appname = concept.getAppname();
				if (iri != null) {
					if (INSTANCE_IRI.equals(appname)) {
						SubjectDef clazz = findClassOf(concept);
						Resource res = infoUnit.getModel().createResource(concept.getAppid());
						// TODO: should we override any existing label?
						Factory.setLabel(res, concept.getLabel(), null);
						if (clazz != null) {
							Factory.setType(res, clazz.getAppid());
						}
						while (clazz != null) {
							SubjectDef superclazz = findClassOf(clazz);

							Resource type = infoUnit.getModel().getResource(clazz.getAppid());
							if (!RDFConstants.getRDFSModel().containsResource(type)) {

								// set label of type
								if (type.getProperty(RDFS.label) == null && !StringUtils.isEmpty(clazz.getLabel())) {
									// TODO: obey language if possible
									Factory.setLabel(type, clazz.getLabel(), null);
								}

								// set super class
								Statement stmt = type.getProperty(RDFS.subClassOf);
								if (superclazz != null && superclazz.getAppid() != null && stmt == null) {
									Factory.setSuperType(type, superclazz.getAppid());
								}
							}

							// now try to create relation to metadata
							if (isType(clazz, IirdsConstants.PRODUCTVARIANT_CLASS_URI)) {
								logger.info("Seting product variant at " + root.getTitle());
								InformationUnits.addRelatedProductVariant(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.PRODUCTFEATURE_CLASS_URI)) {
								logger.info("Seting product feature at " + root.getTitle());
								InformationUnits.addRelatedProductFeature(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.DOCUMENTTYPE_CLASS_URI)) {
								InformationUnits.addDocumentType(infoUnit, res);
								logger.info("Seting document type at " + root.getTitle());
								break;
							} else if (isType(clazz, IirdsConstants.DOCUMENTCATEGORY_CLASS_URI)) {
								logger.info("Setting document categtory at " + root.getTitle());
								InformationUnits.addDocumentCategory(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.SUPPLY_CLASS_URI)) {
								logger.info("Setting supply at " + root.getTitle());
								InformationUnits.addRelatedSupply(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.EVENT_CLASS_URI)) {
								logger.info("Setting event at " + root.getTitle());
								InformationUnits.addEvent(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.QUALIFICATION_CLASS_URI)) {
								logger.info("Setting qualification at " + root.getTitle());
								InformationUnits.addRelatedQualification(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.COMPONENT_CLASS_URI)) {
								logger.info("Setting component at " + root.getTitle());
								InformationUnits.addRelatedComponent(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.INFORMATIONSUBJECT_CLASS_URI)) {
								logger.info("Setting information subject at " + root.getTitle());
								InformationUnits.addInformationSubject(infoUnit, res);
								break;
							} else if (isType(clazz, IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI)) {
								logger.info("Setting product lifecycle phase at " + root.getTitle());
								InformationUnits.addProductLifeCyclePhase(infoUnit, res);
								break;
							}
							// TODO: handle other classes: events?
							//look at parent class:
							clazz = superclazz;
						}
					}
				}
			}
		}
	}
}
