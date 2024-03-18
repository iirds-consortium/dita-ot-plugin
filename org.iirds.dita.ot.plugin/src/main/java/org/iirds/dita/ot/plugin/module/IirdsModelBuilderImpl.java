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

package org.iirds.dita.ot.plugin.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.IirdsModelBuilder;
import org.iirds.dita.ot.plugin.TopicTypeMapper;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.RDFUtils;
import org.iirds.rdf.facade.DirectoryNodes;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.iirds.rdf.facade.Packages;
import org.iirds.rdf.facade.Renditions;

/**
 * Builds an iiRDS model
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class IirdsModelBuilderImpl implements IirdsModelBuilder {

	static final String PROP_IRI = "iri";
	static final String PROP_IO_IRI = "infoobject.iri";

	Resource iirdsPackage = null;
	Resource document = null;
	TopicTypeMapper mapper = null;
	private IRIHandler provider = null;

	public IirdsModelBuilderImpl() {
		mapper = Configuration.getDefault().getTopicTypeMapper();
	}

	IRIHandler getIRIHandler() {
		if (this.provider == null) {
			provider = Configuration.getDefault().getIRIHandler();
		}
		return provider;
	}

	@Override
	public IirdsModelBuilder addInfoObjects(ToCNode root) {
		addInfoObjectNodes(root);
		return this;
	}

	@Override
	public IirdsModelBuilder addInformationUnits(ToCNode root) {
		if (root.isTopic()) {
			this.addTopicNodes(root);
		} else {
			this.document = Factory.createDocument(getInfModel(), getIRIHandler().getDocumentOrTopicIRI(root));
			root.setInformationUnit(this.document);
			if (!StringUtils.isBlank(root.getLanguage())) {
				InformationUnits.addLanguage(this.document, root.getLanguage());
			}
			root.getChildren().forEach(this::addTopicNodes);
		}
		return this;
	}

	@Override
	public IirdsModelBuilder addPackage(ToCNode node) {
		iirdsPackage = Factory.createPackage(getInfModel(), getIRIHandler().getPackageIRI(node));
		Packages.setIIRDSVersion(iirdsPackage, IirdsConstants.IIRDS_VERSION);
		return this;
	}

	@Override
	public IirdsModelBuilder addDirectory(ToCNode node) {
		if (node.getChildren().isEmpty()) {
			// when there are no children there is not need to create a directory
		} else {
			Resource root = addDirectoryNodes(node, null);
			if (root != null) {
				Resource nodeType = Configuration.getDefault().getVocabularyFactory()
						.getDirectoryNodeType(IirdsConstants.TOC_INSTANCE_URI);
				DirectoryNodes.setDirectoryType(root, nodeType);
				Resource nil = Factory.createNil(getInfModel());
				DirectoryNodes.addNextSibling(root, nil);
			}
		}
		return this;
	}

	@Override
	public IirdsModelBuilder addMetadata(ToCNode root) {
		IirdsMetadataHandler handler = Configuration.getDefault().getIirdsMetadataHandler();
		Deque<ToCNode> stack = new ArrayDeque<>();
		stack.add(root);
		while (!stack.isEmpty()) {
			ToCNode node = stack.pollFirst();
			handler.addToModel(node, getInfModel());
			stack.addAll(node.getChildren());
		}
		return this;
	}

	@Override
	public IirdsModelBuilder completeModel(ToCNode root) {
		Configuration.getDefault().getIirdsMetadataHandler().completeModel(root, getInfModel());
		addMissingDocumentTypes();
		return this;
	}

	protected void addMissingDocumentTypes() {
		List<Resource> documents = InformationUnits.getDocuments(getInfModel());
		documents.forEach(d -> {
			if (!RDFUtils.hasAttribute(d, IirdsConstants.HASDOCUMENTTYPE_PROPERTY_URI)) {
				InformationUnits.addDocumentType(d,
						getInfModel().createResource(IirdsConstants.iiRDS_URI + "OperatingInstructions"));
			}
		});
	}

	protected Model getInfModel() {
		return Configuration.getDefault().getModel();
	}

	protected Resource addDirectoryNodes(ToCNode node, Resource parent) {
		Resource entry = null;
		if (node.getNavTitle() != null) {
			entry = Factory.createDirectoryNode(getInfModel());
			Factory.setLabel(entry, node.getNavTitle(), null);
			if (node.hasContent()) {
				String topicIRI = node.getPropertyAsString(PROP_IRI);
				if (topicIRI != null) {
					Resource topic = getInfModel().getResource(topicIRI);
					DirectoryNodes.setInformationUnit(entry, topic);
				}
			}
			node.setDirectoryNode(entry);
			if (parent != null) {
				DirectoryNodes.addChild(parent, entry);
			}
			// use the created entry as new parent for further processing
			parent = entry;
		} else {
			// we did not create an entry
			entry = null;
		}

		// recurse for all children
		for (ToCNode child : node.getChildren()) {
			addDirectoryNodes(child, parent);
		}

		// close child list with nil node, if there is at least one child
		if (entry != null && DirectoryNodes.getFirstChild(entry) != null) {
			Resource nil = Factory.createNil(getInfModel());
			DirectoryNodes.addChild(entry, nil);
		}
		return parent;
	}

	protected void addInfoObjectNodes(ToCNode node) {
		if (node.getURI() != null) {
			String objIri = node.getPropertyAsString(PROP_IO_IRI);
			String iri = node.getPropertyAsString(PROP_IRI);
			if (iri != null && objIri != null) {
				Resource topic = getInfModel().getResource(iri);
				Resource infoObject = Factory.createInfoObject(getInfModel(), objIri);
				InformationUnits.setVersionOf(topic, infoObject);
			}
		}
		// handle children:
		node.getChildren().forEach(this::addInfoObjectNodes);
	}

	protected void addTopicNodes(ToCNode node) {
		if (node.hasContent()) {
			String iri = node.getPropertyAsString(PROP_IRI);
			String objIri = node.getPropertyAsString(PROP_IO_IRI);
			if (iri == null) {
				iri = getIRIHandler().getDocumentOrTopicIRI(node);
				node.setProperty(PROP_IRI, iri);
			}
			if (objIri == null) {
				objIri = getIRIHandler().getInfoObjectIRI(node);
				node.setProperty(PROP_IO_IRI, objIri);
			}

			Resource topic = Factory.createTopic(getInfModel(), iri);
			node.setInformationUnit(topic);
			InformationUnits.setTopicType(topic, mapper.getTopicType(node));
			InformationUnits.setPartOfPackage(topic, iirdsPackage);
			if (node.getTitle() != null) {
				InformationUnits.setTitle(topic, node.getTitle());
			}

			String language = getLanguageOrInherit(node);
			if (!StringUtils.isBlank(language)) {
				InformationUnits.addLanguage(topic, language);
			}

			// rendition
			if (node.getURI() != null) {
				// create rendition
				Resource rendition = Factory.createRendition(getInfModel());

				// TODO: encode or not encode path?
				String contentPath = prefixContentPath(replaceExtension(node.getURI().getPath()));
				Renditions.setSource(rendition, contentPath);
				String format = getFormat(contentPath);
				if (format != null) {
					Renditions.setFormat(rendition, format);
				}
				InformationUnits.addRendition(topic, rendition);
			}
		}

		// handle children:
		node.getChildren().forEach(this::addTopicNodes);

	}

	private String getLanguageOrInherit(ToCNode node) {
		if (node == null) {
			return null;
		}
		if (!StringUtils.isBlank(node.getLanguage())) {
			return node.getLanguage();
		}
		return getLanguageOrInherit(node.getParent());
	}

	protected String getFormat(String uri) {
		final String DEFAULT = "application/octet-stream";
		if (uri == null) {
			return DEFAULT;
		}
		String ext = StringUtils.lowerCase(StringUtils.substringAfterLast(uri, "."));
		if (ext == null) {
			return DEFAULT;
		} else if (ext.equals("ditamap")) {
			return "text/dita+xml";
		} else if (ext.equals("dita")) {
			return "text/dita+xml";
		} else if (uri.equals("iirds")) {
			return "application/iirds+zip";
		}
		Path p = Paths.get("file." + ext);
		try {
			String mime = Files.probeContentType(p);
			if (mime == null) {
				return DEFAULT;
			} else {
				return mime;
			}
		} catch (IOException e) {
			return DEFAULT;
		}
	}

	protected String prefixContentPath(String path) {
		String contentPath = Configuration.getDefault().getContentPath();
		if (!StringUtils.isBlank(contentPath) && path != null) {
			contentPath = FilenameUtils.separatorsToUnix(contentPath);
			String result = FilenameUtils.concat(contentPath, path);
			if (result != null) {
				return FilenameUtils.separatorsToUnix(result);
			}
		}
		return path;
	}

	protected String replaceExtension(String path) {
		String ext = Configuration.getDefault().getHTMLExt();
		if (!StringUtils.isBlank(ext) && path != null) {
			ext = StringUtils.strip(ext);
			if (!ext.startsWith(".")) {
				return FilenameUtils.removeExtension(path) + "." + ext;
			} else {
				return FilenameUtils.removeExtension(path) + ext;
			}
		}
		return path;
	}

	@Override
	public Model getModel() {
		return Configuration.getDefault().getModel();
	}

}
