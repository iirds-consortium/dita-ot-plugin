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

import static org.iirds.rdf.IirdsConstants.RELATESTOPARTY_PROPERTY_URI;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.iirds.rdf.IRIUtils;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFUtils;

import com.ibm.icu.impl.number.parse.RequireDecimalSeparatorValidator;

/**
 *
 * Factory for iiRDS RDF model objects
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class Factory {

	private Factory() {

	}

	public static void setType(Resource resource, String classURI) {
		if (resource.getPropertyResourceValue(RDF.type) == null) {
			resource.addProperty(RDF.type, resource.getModel().getResource(classURI));
		}
	}
	
	public static void setSuperType(Resource resource, String classURI) {
		if (resource.getPropertyResourceValue(RDFS.subClassOf) == null) {
			resource.addProperty(RDFS.subClassOf, resource.getModel().getResource(classURI));
		}
	}
	

	public static Resource createTopic(Model model, String uri) {
		Resource topic = model.getResource(uri);
		setType(topic, IirdsConstants.TOPIC_CLASS_URI);
		return topic;
	}

	public static Resource createInfoObject(Model model, String uri) {
		Resource infoObject = model.getResource(uri);
		setType(infoObject, IirdsConstants.INFORMATIONOBJECT_CLASS_URI);
		return infoObject;
	}

	public static Resource createDocument(Model model, String uri) {
		Resource topic = model.getResource(uri);
		setType(topic, IirdsConstants.DOCUMENT_CLASS_URI);
		return topic;
	}

	public static Resource createPackage(Model model, String uri) {
		Resource topic = model.getResource(uri);
		setType(topic, IirdsConstants.PACKAGE_CLASS_URI);
		return topic;
	}

	public static Resource createComponent(Model model, String uri) {
		Resource component = model.getResource(uri);
		setType(component, IirdsConstants.COMPONENT_CLASS_URI);
		return component;
	}

	public static Resource createProductVariant(Model model, String uri) {
		Resource product = model.getResource(uri);
		setType(product, IirdsConstants.PRODUCTVARIANT_CLASS_URI);
		return product;
	}

	public static Resource createRendition(Model model) {
		Resource rendition = model.createResource();
		setType(rendition, IirdsConstants.RENDITION_CLASS_URI);
		return rendition;
	}

	public static Resource createRole(Model model, String uri) {
		Resource role = uri == null ? model.createResource() : model.getResource(uri);
		setType(role, IirdsConstants.ROLE_CLASS_URI);
		return role;
	}

	public static Resource createInformationSubject(Model model, String uri) {
		Resource subject = uri == null ? model.createResource() : model.getResource(uri);
		setType(subject, IirdsConstants.INFORMATIONSUBJECT_CLASS_URI);
		return subject;
	}

	public static Resource createSkillLevel(Model model, String uri) {
		Resource role = uri == null ? model.createResource() : model.getResource(uri);
		setType(role, IirdsConstants.SKILLLEVEL_CLASS_URI);
		return role;
	}

	public static Resource createEvent(Model model, String uri) {
		Resource event = uri == null ? model.createResource() : model.getResource(uri);
		setType(event, IirdsConstants.EVENT_CLASS_URI);
		return event;
	}

	public static Resource createEventType(Model model, String uri, String value) {
		Resource event = uri == null ? model.createResource() : model.getResource(uri);
		setLabel(event, value, null);
		return event;
	}

	public static Resource createEventCode(Model model, String uri, String value) {
		Resource event = uri == null ? model.createResource() : model.getResource(uri);
		setLabel(event, value, null);
		return event;
	}

	public static Resource createTopicType(Model model, String topicType) {
		String iri = IRIUtils.generateIRIBasedOnLabel(IirdsConstants.TOPICTYPE_CLASS_URI, topicType);
		Resource tt = model.createResource(iri);
		setLabel(tt, topicType, null);
		return tt;
	}

	public static Resource createDirectoryNode(Model model) {
		Resource node = model.createResource();
		setType(node, IirdsConstants.DIRECTORYNODE_CLASS_URI);
		return node;
	}

	public static Resource createParty(Model model, String uri) {
		Resource party = model.createResource(uri);
		setType(party, IirdsConstants.PARTY_CLASS_URI);
		return party;
	}

	public static Resource createIdentity(Model model, String domain, Resource party, String identifier) {
		Resource identity = model.createResource();
		setType(identity, IirdsConstants.IDENTITY_CLASS_URI);
		identity.addProperty(model.getProperty(IirdsConstants.IDENTIFIER_PROPERTY_URI), identifier);
		Resource identityDomain = model.createResource(domain);
		setType(identityDomain, IirdsConstants.IDENTITYDOMAIN_CLASS_URI);

		// make sure to have only one party per domain
		if (party != null && !RDFUtils.hasAttribute(identityDomain, IirdsConstants.RELATESTOPARTY_PROPERTY_URI)) {
			Factory.addParty(identityDomain, party);
		}
		identity.addProperty(model.getProperty(IirdsConstants.HASIDENTITYDOMAIN_PROPERTY_URI), identityDomain);
		return identity;
	}

	/**
	 * Creates a vcard:Organization resource
	 * 
	 * @param model the owning model
	 * @param uri   the IRI (optional)
	 * @param name  the name (optional)
	 * @return the resource
	 */
	public static Resource createOrganization(Model model, String uri, String name) {
		Resource r = uri == null ? model.createResource() : model.getResource(uri);
		setType(r, IirdsConstants.VCARD_ORGANIZATION_CLASS_URI);
		if (!StringUtils.isBlank(name)) {
			r.addProperty(model.getProperty(IirdsConstants.VCARD_FULLNAME_PROPERTY_URI), name);
		}
		return r;
	}

	/**
	 * Creates a vcard:Individual resource
	 * 
	 * @param model the owning model
	 * @param uri   the IRI (optional)
	 * @param name  the name (optional)
	 * @return the resource
	 */
	public static Resource createIndividual(Model model, String uri, String name) {
		Resource r = uri == null ? model.createResource() : model.getResource(uri);
		setType(r, IirdsConstants.VCARD_INDIVIDUAL_CLASS_URI);
		if (!StringUtils.isBlank(name)) {
			r.addProperty(model.getProperty(IirdsConstants.VCARD_FULLNAME_PROPERTY_URI), name);
		}
		return r;
	}

	public static Resource createProductLifecyclePhase(Model model, String uri) {
		Resource r = uri == null ? model.createResource() : model.getResource(uri);
		setType(r, IirdsConstants.PRODUCTLIFECYCLEPHASE_CLASS_URI);
		return r;
	}

	public static Resource createContentLifecycleStatus(Model model) {
		Resource r = model.createResource();
		setType(r, IirdsConstants.CONTENTLIFECYCLESTATUS_CLASS_URI);
		return r;
	}

	public static Resource createContentLifecycleStatusValue(Model model, String uri) {
		Resource r = model.createResource(uri);
		setType(r, IirdsConstants.CONTENTLIFECYCLESTATUSVALUE_CLASS_URI);
		return r;
	}

	public static void setEventType(Resource event, String eventtype) {
		if (eventtype != null) {
			event.addProperty(event.getModel().getProperty(IirdsConstants.EVENTTYPE_PROPERTY_URI),
					createEventType(event.getModel(), null, eventtype));
		}
	}

	public static void setEventType(Resource event, Resource eventtype) {
		if (eventtype != null) {
			event.addProperty(event.getModel().getProperty(IirdsConstants.EVENTTYPE_PROPERTY_URI), eventtype);
		}
	}

	public static void setEventCode(Resource event, String eventcode) {
		if (eventcode != null) {
			event.addProperty(event.getModel().getProperty(IirdsConstants.EVENTCODE_PROPERTY_URI),
					createEventCode(event.getModel(), null, eventcode));
		}
	}

	public static void addParty(Resource owner, Resource party) {
		owner.addProperty(owner.getModel().getProperty(RELATESTOPARTY_PROPERTY_URI), party);
	}

	public static void setEventCode(Resource event, Resource eventcode) {
		if (eventcode != null) {
			event.addProperty(event.getModel().getProperty(IirdsConstants.EVENTCODE_PROPERTY_URI), eventcode);
		}
	}

	public static void setLabel(Resource resource, String label, String language) {
		String l = StringUtils.trimToEmpty(label);
		if (!StringUtils.isBlank(l)) {
			Literal literal;
			if (StringUtils.isEmpty(language)) {
				literal = resource.getModel().createLiteral(l, language);
			} else {
				literal = resource.getModel().createLiteral(l);
			}
			resource.addProperty(RDFS.label, literal);
		}
	}

	public static List<RDFNode> getPropertyObjects(Model model, String subjectURI, String propertyURI) {
		Resource r = model.getResource(subjectURI);
		return getPropertyObjects(r, propertyURI);
	}

	public static List<RDFNode> getPropertyObjects(Resource subject, String propertyURI) {
		ArrayList<RDFNode> result = new ArrayList<>();
		for (StmtIterator iter = subject.listProperties(subject.getModel().createProperty(propertyURI)); iter
				.hasNext();) {
			result.add(iter.next().getObject());
		}
		return result;
	}

	public static String getLabel(Resource r, String lang) {
		List<RDFNode> nodes = getPropertyObjects(r, RDFS.label.getURI());
		for (RDFNode node : nodes) {
			if (StringUtils.equals(node.asLiteral().getLanguage(), lang)
					|| (StringUtils.isEmpty(lang) && StringUtils.isEmpty(node.asLiteral().getLanguage()))) {
				return node.asLiteral().getString();
			}
		}
		return null;
	}

	public static Resource createNil(Model model) {
		return model.getResource(IirdsConstants.NIL_RESOURCE_URI);
	}

	public static Resource createMaintenanceInterval(Model model, String uri) {
		Resource interval = uri != null ? model.createResource(uri) : model.createResource();
		setType(interval, IirdsConstants.MAINTENANCEINTERVAL_CLASS_URI);
		return interval;
	}

	public static Resource createWorkingTime(Model model, String uri) {
		Resource time = uri != null ? model.createResource(uri) : model.createResource();
		setType(time, IirdsConstants.WORKINGTIME_CLASS_URI);
		return time;
	}

	public static Resource createSetupTime(Model model, String uri) {
		Resource time = uri != null ? model.createResource(uri) : model.createResource();
		setType(time, IirdsConstants.SETUPTIME_CLASS_URI);
		return time;
	}

	public static Resource createDownTime(Model model, String uri) {
		Resource time = uri != null ? model.createResource(uri) : model.createResource();
		setType(time, IirdsConstants.DOWNTIME_CLASS_URI);
		return time;
	}

	public static List<Resource> getResourcesOfType(Model model, String typeURI) {
		List<Resource> result = new ArrayList<>();
		ResIterator iter = model.listSubjectsWithProperty(RDF.type, model.getResource(typeURI));
		iter.forEach(result::add);
		return result;
	}

}
