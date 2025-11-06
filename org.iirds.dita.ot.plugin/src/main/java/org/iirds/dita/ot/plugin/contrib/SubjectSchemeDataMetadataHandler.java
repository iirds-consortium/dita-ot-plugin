package org.iirds.dita.ot.plugin.contrib;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.Ontology;
import org.iirds.dita.ot.plugin.model.OntologyClass;
import org.iirds.dita.ot.plugin.model.OntologyCommon;
import org.iirds.dita.ot.plugin.model.OntologyInstance;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubjectSchemeDataMetadataHandler implements IirdsMetadataHandler {

	static Logger logger = LoggerFactory.getLogger(SubjectSchemeDataMetadataHandler.class);

	static final String PROP_SUBJECTSCHEME = "metadata/subjectscheme";

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
		List<OntologyInstance> list = new ArrayList<>();
		node.setProperty(PROP_SUBJECTSCHEME, list);
		Optional<Element> metadata = MetadataUtils.getMetadataHolderElement(document.getDocumentElement());
		if (metadata.isPresent()) {
			List<Element> dataElements = XMLUtils.getChildElements(metadata.get(), Constants.TOPIC_DATA, true);
			for (Element data : dataElements) {
				String keyref = XMLUtils.getValue(data, Constants.ATTRIBUTE_NAME_KEYREF);
				if (keyref != null) {
					Optional<OntologyCommon> member = Ontology.getDefault().getByKey(keyref);
					if (member.isPresent()) {
						if (member.get().isInstance()) {
							OntologyInstance instance = (OntologyInstance) member.get();
							logger.info("Found " + instance);
							list.add(instance);
						}
					}
				}
			}
		}
	}

	boolean isType(OntologyClass clazz, String classUri) {
		while (clazz != null) {
			if (classUri.equals(clazz.getIRI())) {
				return true;
			}
			clazz = clazz.getSuperClass();
		}
		return false;
	}

	void setLabel(Resource resource, OntologyInstance concept) {
		if (concept.getLabel() != null) {
			resource.addLiteral(RDFS.label, concept.getLabel());
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {

		// TODO: label language

		// TODO: type hierarchy, not jut base types

		Resource infoUnit = root.getInformationUnit();

		@SuppressWarnings("unchecked")
		List<OntologyInstance> concepts = (List<OntologyInstance>) root.getProperty(PROP_SUBJECTSCHEME);
		if (concepts != null) {
			for (OntologyInstance concept : concepts) {
				String iri = concept.getIRI();
				OntologyClass clazz = concept.getOntologyClass();
				if (clazz != null) {
					if (isType(clazz, IirdsConstants.PRODUCTVARIANT_CLASS_URI)) {
						Resource product = Factory.createProductVariant(infoUnit.getModel(), iri);
						setLabel(product, concept);
						InformationUnits.addRelatedProductVariant(infoUnit, product);
					} else if (isType(clazz, IirdsConstants.DOCUMENTTYPE_CLASS_URI)) {
						Resource dt = infoUnit.getModel().getResource(iri);
						setLabel(dt, concept);
						InformationUnits.addDocumentType(dt, infoUnit);
					} else if (isType(clazz, IirdsConstants.COMPONENT_CLASS_URI)) {
						Resource product = Factory.createComponent(infoUnit.getModel(), iri);
						setLabel(product, concept);
						InformationUnits.addRelatedComponent(infoUnit, product);
					} else if (isType(clazz, IirdsConstants.INFORMATIONSUBJECT_CLASS_URI)) {
						Resource subject = Factory.createInformationSubject(infoUnit.getModel(), iri);
						setLabel(subject, concept);
						InformationUnits.addInformationSubject(infoUnit, subject);
					} else if (isType(clazz, IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI)) {
						Resource phase = Factory.createProductLifecyclePhase(infoUnit.getModel(), iri);
						setLabel(phase, concept);
						InformationUnits.addProductLifeCyclePhase(infoUnit, phase);
					}
				}
			}
		}
	}
}
