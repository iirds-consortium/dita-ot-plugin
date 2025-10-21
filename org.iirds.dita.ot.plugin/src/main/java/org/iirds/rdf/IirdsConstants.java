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

/**
 * iiRDS constants covering IRIs, namespaces. properties and resources
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public final class IirdsConstants {

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String iiRDS_URI = "http://iirds.tekom.de/iirds#";
	public static final String iiRDS_Mch_URI = "http://iirds.tekom.de/iirds/domain/machinery#";
	public static final String iiRDS_Sft_URI = "http://iirds.tekom.de/iirds/domain/software#";
	public static final String iiRDS_Hov_URI = "http://iirds.tekom.de/iirds/domain/handover#";

	/** The namespace prefix to use for iiRDS */
	public static final String iiRDS_PREFIX = "iirds";

	/** Information unit class URI */
	public static final String INFORMATIONUNIT_CLASS_URI = iiRDS_URI + "InformationUnit";

	/** Description property URI */
	public static final String DESCRIPTION_PROPERTY_URI = iiRDS_URI + "description";

	/** Topic class URI */
	public static final String TOPIC_CLASS_URI = iiRDS_URI + "Topic";
	/** Document class URI */
	public static final String DOCUMENT_CLASS_URI = iiRDS_URI + "Document";
	/** Package class URI */
	public static final String PACKAGE_CLASS_URI = iiRDS_URI + "Package";
	/** Fragment class URI */
	public static final String FRAGMENT_CLASS_URI = iiRDS_URI + "Fragment";
	/** Topic class URI */
	public static final String TOPICTYPE_CLASS_URI = iiRDS_URI + "TopicType";
	/** Document type class URI */
	public static final String DOCUMENTTYPE_CLASS_URI = iiRDS_URI + "DocumentType";
	/** Identity class URI */
	public static final String IDENTITY_CLASS_URI = iiRDS_URI + "Identity";
	/** IdentityDomain class URI */
	public static final String IDENTITYDOMAIN_CLASS_URI = iiRDS_URI + "IdentityDomain";
	/** Party class URI */
	public static final String PARTY_CLASS_URI = iiRDS_URI + "Party";
	/** PartyRole class URI */
	public static final String PARTYROLE_CLASS_URI = iiRDS_URI + "PartyRole";
	/** InformationObject class URI */
	public static final String INFORMATIONOBJECT_CLASS_URI = iiRDS_URI + "InformationObject";
	/** ContentLifeCycleStatus */
	public static final String CONTENTLIFECYCLESTATUS_CLASS_URI = iiRDS_URI + "ContentLifeCycleStatus";
	/** ContentLifeCycleStatusValue */
	public static final String CONTENTLIFECYCLESTATUSVALUE_CLASS_URI = iiRDS_URI + "ContentLifeCycleStatusValue";

	public static final String EVENT_CLASS_URI = iiRDS_URI + "Event";
	public static final String INFORMATIONSUBJECT_CLASS_URI = iiRDS_URI + "InformationSubject";
	public static final String INFORMATIONTYPE_CLASS_URI = iiRDS_URI + "InformationType";
	public static final String PRODUCTVARIANT_CLASS_URI = iiRDS_URI + "ProductVariant";
	public static final String PRODUCTFEATURE_CLASS_URI = iiRDS_URI + "ProductFeature";
	public static final String DOCUMENTATIONMETADATA_CLASS_URI = iiRDS_URI + "DocumentationMetadata";
	public static final String FUNCTIONALMETADATA_CLASS_URI = iiRDS_URI + "FunctionalMetadata";
	public static final String PRODUCTMETADATA_CLASS_URI = iiRDS_URI + "ProductMetadata";
	public static final String COMPONENT_CLASS_URI = iiRDS_URI + "Component";
	public static final String PRODUCTLIFECYCLEPHASE_CLASS_URI = iiRDS_URI + "ProductLifeCyclePhase";
	public static final String QUALIFICATION_CLASS_URI = iiRDS_URI + "Qualification";
	public static final String SKILLLEVEL_CLASS_URI = iiRDS_URI + "SkillLevel";
	public static final String ROLE_CLASS_URI = iiRDS_URI + "Role";
	public static final String SELECTOR_CLASS_URI = iiRDS_URI + "Selector";
	public static final String RENDITION_CLASS_URI = iiRDS_URI + "Rendition";
	public static final String RANGESELECTOR_CLASS_URI = iiRDS_URI + "RangeSelector";
	public static final String FRAGMENTSELECTOR_CLASS_URI = iiRDS_URI + "FragmentSelector";
	public static final String PLANNINGTIME_CLASS_URI = iiRDS_URI + "PlanningTime";
	public static final String SETUPTIME_CLASS_URI = iiRDS_URI + "SetupTime";
	public static final String DOWNTIME_CLASS_URI = iiRDS_URI + "DownTime";
	public static final String WORKINGTIME_CLASS_URI = iiRDS_URI + "WorkingTime";
	public static final String MAINTENANCEINTERVAL_CLASS_URI = iiRDS_URI + "MaintenanceInterval";
	public static final String LUBRICANT_CLASS_URI = iiRDS_Mch_URI + "Lubricant";
	public static final String SPAREPART_CLASS_URI = iiRDS_Mch_URI + "SparePart";
	public static final String OPERATINGSUPPLY_CLASS_URI = iiRDS_Mch_URI + "OperatingSupply";
	public static final String CONSUMABLESUPPLY_CLASS_URI = iiRDS_Mch_URI + "ConsumableSupply";
	public static final String HARDWARETOOL_CLASS_URI = iiRDS_Mch_URI + "HardwareTool";
	public static final String SUPPLY_CLASS_URI = iiRDS_URI + "Supply";

	/** Has Document type property URI */
	public static final String HASDOCUMENTTYPE_PROPERTY_URI = iiRDS_URI + "has-document-type";
	/** Has Topic type property URI */
	public static final String HASTOPICTYPE_PROPERTY_URI = iiRDS_URI + "has-topic-type";

	public static final String SOURCE_PROPERTY_URI = iiRDS_URI + "source";
	public static final String LANGUAGE_PROPERTY_URI = iiRDS_URI + "language";
	public static final String IDENTIFIER_PROPERTY_URI = iiRDS_URI + "identifier";
	public static final String HASIDENTITY_PROPERTY_URI = iiRDS_URI + "has-identity";
	public static final String HASIDENTITYDOMAIN_PROPERTY_URI = iiRDS_URI + "has-identity-domain";
	public static final String TITLE_PROPERTY_URI = iiRDS_URI + "title";
	public static final String EVENTTYPE_PROPERTY_URI = iiRDS_URI + "has-event-type";
	public static final String EVENTCODE_PROPERTY_URI = iiRDS_URI + "has-event-code";
	public static final String FORMAT_PROPERTY_URI = iiRDS_URI + "format";
	public static final String RIGHTS_PROPERTY_URI = iiRDS_URI + "rights";
	public static final String FORMATRESTRICTION_PROPERTY_URI = iiRDS_URI + "formatRestriction";
	public static final String RELATESTOPRODUCTFEATURE_PROPERTY_URI = iiRDS_URI + "relates-to-product-feature";
	public static final String RELATESTOPRODUCTVARIANT_PROPERTY_URI = iiRDS_URI + "relates-to-product-variant";
	public static final String RELATESTOCOMPONENT_PROPERTY_URI = iiRDS_URI + "relates-to-component";
	public static final String RELATESTOPRODUCTLIFECYCLEPHASE_PROPERTY_URI = iiRDS_URI
			+ "relates-to-product-lifecycle-phase";
	public static final String DATEOFEXPIRY_PROPERTY_URI = iiRDS_URI
			+ "dateOfExpiry";
	public static final String DATEOFEFFECT_PROPERTY_URI = iiRDS_URI
			+ "dateOfEffect";


	public static final String HASSUBJECT_PROPERTY_URI = iiRDS_URI + "has-subject";
	public static final String IIRDSVERSION_PROPERTY_URI = iiRDS_URI + "iiRDSVersion";
	public static final String RELATESTOEVENT_PROPERTY_URI = iiRDS_URI + "relates-to-event";
	public static final String HASRENDITION_PROPERTY_URI = iiRDS_URI + "has-rendition";
	public static final String HASSELECTOR_PROPERTY_URI = iiRDS_URI + "has-selector";
	public static final String HASPLANNINGTIME_PROPERTY_URI = iiRDS_URI + "has-planning-time";
	public static final String HASCONTENTLIFECYCLESTATUS_PROPERTY_URI = iiRDS_URI + "has-content-lifecycle-status";
	public static final String HASCONTENTLIFECYCLESTATUSVALUE_PROPERTY_URI = iiRDS_URI
			+ "has-content-lifecycle-status-value";
	public static final String HASVCARD_PROPERTY_URI = iiRDS_URI + "relates-to-vcard";
	public static final String RELATESTOQUALIFICATION_PROPERTY_URI = iiRDS_URI + "relates-to-qualification";
	public static final String REQUIRESSUPPLY_PROPERTY_URI = iiRDS_URI + "relates-to-supply";
	public static final String ATTRIBUTE_IIRDSPROPERTY_URI = iiRDS_URI + "iirdsAttribute";
	public static final String DATEOFCREATION_PROPERTY_URI = iiRDS_URI + "dateOfCreation";
	public static final String DATEOFLASTMODIFICATION_PROPERTY_URI = iiRDS_URI + "dateOfLastModification";
	public static final String ISREPLACEMENTOF_PROPERTY_URI = iiRDS_URI + "is-replacement-of";
	public static final String HASCOMPONENT_PROPERTY_URI = iiRDS_URI + "has-component";
	public static final String ISVERSIONOF_PROPERTY_URI = iiRDS_URI + "is-version-of";
	public static final String ISAPPLICABLEFORDOCUMENTTYPE_PROPERTY_URI = iiRDS_URI + "is-applicable-for-document-type";
	public static final String HASABSTRACT_PROPERTY_URI = iiRDS_URI + "has-abstract";
	public static final String ISPARTOFPACKAGE_PROPERTY_URI = iiRDS_URI + "is-part-of-package";
	public static final String HASSTARTSELECTOR_PROPERTY_URI = iiRDS_URI + "has-start-selector";
	public static final String HASENDSELECTOR_PROPERTY_URI = iiRDS_URI + "has-end-selector";
	public static final String RELATESTOPARTY_PROPERTY_URI = iiRDS_URI + "relates-to-party";
	public static final String HASPARTYROLE_PROPERTY_URI = iiRDS_URI + "has-party-role";
	public static final String DURATION_PROPERTY_URI = iiRDS_URI + "duration";
	public static final String REVISION_PROPERTY_URI = iiRDS_URI + "revision";
	public static final String FREQUENCY_PROPERTY_URI = iiRDS_URI + "frequency";
	public static final String DATEOFSTATUS_PROPERTY_URI = iiRDS_URI + "dateOfStatus";

	/* Navigation */
	public static final String DIRECTORYNODE_CLASS_URI = iiRDS_URI + "DirectoryNode";
	public static final String DIRECTORYNODETYPE_CLASS_URI = iiRDS_URI + "DirectoryNodeType";
	public static final String RELATESTOINFORMATIONUNIT_PROPERTY_URI = iiRDS_URI + "relates-to-information-unit";
	public static final String HASNEXT_SIBLING_PROPERTY_URI = iiRDS_URI + "has-next-sibling";
	public static final String HASFIRST_CHILD_PROPERTY_URI = iiRDS_URI + "has-first-child";
	public static final String HASDIRCTORYSTRUCTURETYPE_PROPERTY_URI = iiRDS_URI + "has-directory-structure-type";
	public static final String NIL_RESOURCE_URI = iiRDS_URI + "nil";

	/* information subject subclasses */
	public static final String FUNCTIONALITY_CLASS_URI = iiRDS_URI + "Functionality";
	public static final String TECHNICALDATA_CLASS_URI = iiRDS_URI + "TechnicalData";
	public static final String SAFETY_CLASS_URI = iiRDS_URI + "Safety";
	public static final String WARNINGMESSAGE_CLASS_URI = iiRDS_URI + "WarningMessage";
	public static final String TECHNICALOVERVIEW_CLASS_URI = iiRDS_URI + "TechnicalOverview";
	public static final String CONFORMITY_CLASS_URI = iiRDS_URI + "Conformity";
	public static final String COLLECTION_CLASS_URI = iiRDS_URI + "Collection";
	public static final String FORMALITY_CLASS_URI = iiRDS_URI + "Formality";
	public static final String PROCESS_CLASS_URI = iiRDS_URI + "Process";

	// new since iiRDS 1.3:
	public static final String HASDOCUMENTCATEGORY_PROPERTY_URI = iiRDS_Hov_URI+"has-document-category";	

	/** The iiRDS version */
	public static final String IIRDS_VERSION = "1.3";

	/**
	 * Table of Contents DirectoryNode type URI
	 */
	public static final String TOC_INSTANCE_URI = iiRDS_URI + "TableOfContents";

	// external
	public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";;
	public static final String VALUE_PROPERTY_URI = RDF_URI + "value";
	public static final String LABEL_PROPERTY_URI = RDFS_URI + "label";
	public static final String VCARD_URI = "http://www.w3.org/2006/vcard/ns#";
	public static final String DCTERMS_URI = "http://purl.org/dc/terms/";
	public static final String SCHEMA_URI = "http://schema.org/";
	public static final String COMFORMS_TO_PROPERTY_URI = DCTERMS_URI + "conformsTo";
	public static final String STANDARD_CLASS_URI = DCTERMS_URI + "Standard";
	public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema#";

	/* vcard */
	public static final String VCARD_KIND_CLASS_URI = VCARD_URI + "Kind";
	public static final String VCARD_INDIVIDUAL_CLASS_URI = VCARD_URI + "Individual";
	public static final String VCARD_ORGANIZATION_CLASS_URI = VCARD_URI + "Organization";

	/* fullname property name */
	public static final String VCARD_FULLNAME_PROPERTY_URI = VCARD_URI + "fn";

	/* email property name */
	public static final String VCARD_EMAIL_PROPERTY_URI = VCARD_URI + "hasEmail";

	/* iiRDS 1.2: */
	public static final String EXTERNALCLASSIFICATION_CLASS_URI = iiRDS_URI + "ExternalClassification";
	public static final String CLASSIFICATIONDOMAIN_CLASS_URI = iiRDS_URI + "ClassificationDomain";
	public static final String CLASSIFICATIONTYPE_CLASS_URI = iiRDS_URI + "ClassificationType";
	public static final String CLASSIFICATIONIDENTIFIER_PROPERTY_URI = iiRDS_URI + "classificationIdentifier";
	public static final String CLASSIFICATIONVERSION_PROPERTY_URI = iiRDS_URI + "classificationVersion";
	public static final String HASCLASSIFICATIONDOMAIN_PROPERTY_URI = iiRDS_URI + "has-classification-domain";
	public static final String HASEXTERNALCLASSIFICATION_PROPERTY_URI = iiRDS_URI + "has-external-classification";
	public static final String HASCLASSIFICATIONTYPE_PROPERTY_URI = iiRDS_URI + "has-classification-type";
	/* end iiRDS 1.2 */

	private IirdsConstants() {

	}

}
