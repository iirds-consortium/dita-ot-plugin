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

package org.iirds.rdf;

import static org.iirds.rdf.IirdsConstants.*;

import java.net.URL;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.vocabulary.ReasonerVocabulary;

/**
 * Constants related to iiRDS.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public final class RDFConstants {

	private static final String RDFS_PATH_CORE = "/iirds-core.rdf";
	private static final String RDFS_PATH_SOFTWARE = "/iirds-software.rdf";
	private static final String RDFS_PATH_MACHINERY = "/iirds-machinery.rdf";
	private static final String RDFS_PATH_HANDOVER = "/iirds-handover.rdf";

	private static final Model iiRDS_MODEL = createDefaultModel();

	public static final Resource NIL_RESOURCE = iiRDS_MODEL.createResource(NIL_RESOURCE_URI);
	public static final Resource PACKAGE_RESOURCE = iiRDS_MODEL.createResource(PACKAGE_CLASS_URI);
	public static final Resource DOCUMENT_RESOURCE = iiRDS_MODEL.createResource(DOCUMENT_CLASS_URI);
	public static final Resource DOCUMENTTYPE_RESOURCE = iiRDS_MODEL.createResource(DOCUMENTTYPE_CLASS_URI);
	public static final Resource TOPICTYPE_RESOURCE = iiRDS_MODEL.createResource(TOPICTYPE_CLASS_URI);
	public static final Resource INFORMATIONTYPE_RESOURCE = iiRDS_MODEL.createResource(INFORMATIONTYPE_CLASS_URI);
	public static final Resource INFORMATIONSUBJECT_RESOURCE = iiRDS_MODEL.createResource(INFORMATIONSUBJECT_CLASS_URI);
	public static final Resource TOPIC_RESOURCE = iiRDS_MODEL.createResource(TOPIC_CLASS_URI);
	public static final Resource FRAGMENT_RESOURCE = iiRDS_MODEL.createResource(FRAGMENT_CLASS_URI);
	public static final Resource EVENT_RESOURCE = iiRDS_MODEL.createResource(EVENT_CLASS_URI);
	public static final Resource PRODUCT_RESOURCE = iiRDS_MODEL.createResource(PRODUCTVARIANT_CLASS_URI);
	public static final Resource PRODUCTFEATURE_RESOURCE = iiRDS_MODEL.createResource(PRODUCTFEATURE_CLASS_URI);
	public static final Resource PRODUCTLIFECYCLE_RESOURCE = iiRDS_MODEL
			.createResource(PRODUCTLIFECYCLEPHASE_CLASS_URI);
	public static final Resource COMPONENT_RESOURCE = iiRDS_MODEL.createResource(COMPONENT_CLASS_URI);
	public static final Resource DIRECTORYNODE_RESOURCE = iiRDS_MODEL.createResource(DIRECTORYNODE_CLASS_URI);
	public static final Resource ROLE_RESOURCE = iiRDS_MODEL.createResource(ROLE_CLASS_URI);
	public static final Resource SKILLLEVEL_RESOURCE = iiRDS_MODEL.createResource(SKILLLEVEL_CLASS_URI);
	public static final Resource QUALIFICATION_RESOURCE = iiRDS_MODEL.createResource(QUALIFICATION_CLASS_URI);

	public static final Property HASDOCUMENTTYPE_PROPERTY = iiRDS_MODEL.createProperty(HASDOCUMENTTYPE_PROPERTY_URI);
	public static final Property HASTOPICTYPE_PROPERTY = iiRDS_MODEL.createProperty(HASTOPICTYPE_PROPERTY_URI);
	public static final Property HASIDENTITY_PROPERTY = iiRDS_MODEL.createProperty(IDENTIFIER_PROPERTY_URI);
	public static final Property TITLE_PROPERTY = iiRDS_MODEL.createProperty(TITLE_PROPERTY_URI);
	public static final Property HASABSTRACT_PROPERTY = iiRDS_MODEL.createProperty(HASABSTRACT_PROPERTY_URI);
	public static final Property FORMAT_PROPERTY = iiRDS_MODEL.createProperty(FORMAT_PROPERTY_URI);
	public static final Property LANGUAGE_PROPERTY = iiRDS_MODEL.createProperty(LANGUAGE_PROPERTY_URI);
	public static final Property RELATES_TO_PRODUCTVARIANT_PROPERTY = iiRDS_MODEL
			.createProperty(RELATESTOPRODUCTVARIANT_PROPERTY_URI);
	public static final Property HASSUBJECT_PROPERTY = iiRDS_MODEL.createProperty(HASSUBJECT_PROPERTY_URI);
	public static final Property RELATES_TO_QUALIFICATION_PROPERTY = iiRDS_MODEL
			.createProperty(RELATESTOQUALIFICATION_PROPERTY_URI);
	public static final Property RELATES_TO_EVENT_PROPERTY = iiRDS_MODEL.createProperty(RELATESTOEVENT_PROPERTY_URI);

	public static final Property RELATES_TO_NFORMATION_UNIT_PROPERTY = iiRDS_MODEL
			.createProperty(RELATESTOINFORMATIONUNIT_PROPERTY_URI);
	public static final Property HASFIRST_CHILD_PROPERTY = iiRDS_MODEL.createProperty(HASFIRST_CHILD_PROPERTY_URI);
	public static final Property HASNEXT_SIBLING_PROPERTY = iiRDS_MODEL.createProperty(HASNEXT_SIBLING_PROPERTY_URI);

	public static final Property IIRDSVERSION_PROPERTY = iiRDS_MODEL.createProperty(IIRDSVERSION_PROPERTY_URI);

	public static final Property DOMAININCLUDES_PROPERTY = iiRDS_MODEL
			.createProperty(IirdsConstants.SCHEMA_URI + "domainIncludes");
	public static final Property RANGEINCLUDES_PROPERTY = iiRDS_MODEL
			.createProperty(IirdsConstants.SCHEMA_URI + "rangeIncludes");

	public static final Property DESCRIPTION_PROPERTY = iiRDS_MODEL
			.createProperty(IirdsConstants.DESCRIPTION_PROPERTY_URI);

	private static Model rdfsModel = null;
	private static Model rdfsInfModel = null;

	private RDFConstants() {

	}

	public static Model createDefaultModel() {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix(iiRDS_PREFIX, iiRDS_URI);
		model.setNsPrefix("dcterms", IirdsConstants.DCTERMS_URI);
		model.setNsPrefix("vcard", IirdsConstants.VCARD_URI);
		model.setNsPrefix("iirdsSft", IirdsConstants.iiRDS_Sft_URI);
		model.setNsPrefix("iirdsMch", IirdsConstants.iiRDS_Mch_URI);
		model.setNsPrefix("iirdsHov", IirdsConstants.iiRDS_Hov_URI);
		return model;
	}

	public static Model readModels(URL... urls) {
		Model result = ModelFactory.createDefaultModel();
		for (URL url : urls) {
			Model model = ModelFactory.createDefaultModel();
			model.read(url.toExternalForm(), url.toExternalForm(), "RDF/XML");
			result = ModelFactory.createUnion(result, model);
		}
		return result;
	}

	public static synchronized Model getRDFSModel() {
		if (rdfsModel == null) {
			URL url_core = IirdsConstants.class.getResource(RDFS_PATH_CORE);
			URL url_sw = IirdsConstants.class.getResource(RDFS_PATH_SOFTWARE);
			URL url_mc = IirdsConstants.class.getResource(RDFS_PATH_MACHINERY);
			URL url_ho = IirdsConstants.class.getResource(RDFS_PATH_HANDOVER);
			if (url_core == null || url_sw == null || url_mc == null || url_ho == null) {
				throw new IllegalStateException("RDF schemes not found in class path");
			}
			Model model = readModels(url_core, url_sw, url_mc, url_ho);
			rdfsModel = model;
		}
		return rdfsModel;
	}

	public static synchronized Model getRDFSInfModel() {
		if (rdfsInfModel == null) {
			rdfsInfModel = createInfModel(ModelFactory.createDefaultModel());
		}
		return rdfsInfModel;
	}

	public static InfModel createInfModel(Model model) {
		Reasoner reasoner = RDFSRuleReasonerFactory.theInstance().create(null);
		reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel, ReasonerVocabulary.RDFS_SIMPLE);
		return ModelFactory.createInfModel(reasoner, getRDFSModel(), model);
	}

}
