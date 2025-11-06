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

import static org.dita.dost.util.URLUtils.stripFragment;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.util.Constants;
import org.dita.dost.util.DitaClass;
import org.dita.dost.util.Job.FileInfo;
import org.dita.dost.util.URLUtils;
import org.dita.dost.util.XMLUtils;
import org.dita.dost.writer.AbstractDomFilter;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.TitleExtractor;
import org.iirds.dita.ot.plugin.model.Ontology;
import org.iirds.dita.ot.plugin.model.OntologyClass;
import org.iirds.dita.ot.plugin.model.OntologyCommon;
import org.iirds.dita.ot.plugin.model.OntologyInstance;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads the DITA input file of the transformation and processes it to extract
 * iiRDS information
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class IirdsDitaReader extends AbstractDomFilter {

	/** Current file. */
	private URI filePath = null;

	private ToCNode toc = new ToCNode();
	private Ontology ontologgy = Ontology.getDefault();
	private Deque<OntologyCommon> ontostack = new ArrayDeque<>();

	private TitleExtractor titleExtractor = new TitleExtractor();

	public IirdsDitaReader() {
		super();
	}

	public ToCNode getToC() {
		return this.toc;
	}

	public void read(final File filename, URI fileURI) throws DITAOTException {
		this.filePath = fileURI;
		this.read(filename);
	}

	@Override
	public void read(final File filename) throws DITAOTException {
		if (this.filePath == null) {
			try {
				this.filePath = new URI(filename.getName());
			} catch (URISyntaxException e) {
				throw new DITAOTException(e);
			}
		}
		logger.info("Reading map {0}", filename);
		super.read(filename);
	}

	@Override
	public Document process(final Document doc) {
		Element map = doc.getDocumentElement();

		this.toc.setURI(this.filePath);

		if (org.dita.dost.util.Constants.TOPIC_TOPIC.matches(map)) {
			// when only a topic was used to transform to iiRDS
			return processTopic(doc);
		} else {
			// otherwise we assume the default: a map
			return processMap(doc);
		}
	}

	protected Document processTopic(final Document doc) {
		// we just read the topic
		IirdsTopicReader topicMetaReader = new IirdsTopicReader(this.toc);
		topicMetaReader.setJob(job);
		topicMetaReader.setLogger(logger);
		topicMetaReader.process(doc);
		return null;
	}

	protected Document processMap(final Document doc) {
		Element map = doc.getDocumentElement();
		String title = titleExtractor.getMapTitle(map);
		this.toc.setIsDocument();
		this.toc.setTitle(title);
		this.toc.setTopicId(map.getAttribute("id"));
		this.toc.setDitaClass(DitaClass.getInstance(map));
		this.toc.setContentHash(Configuration.getDefault().getContentHashBuilder().getHash(doc.getDocumentElement()));
		logger.debug("Content hash of map is " + this.toc.getContentHash());

		Configuration.getDefault().getLanguageExtractor().extractLanguage(toc, map);

		// run metadata extraction
		Configuration.getDefault().getIirdsMetadataHandler().extractMetadata(this.toc, doc);

		for (Element elem : XMLUtils.getChildElements(map)) {
			if (Constants.MAP_TOPICREF.matches(elem)) {
				handleTopicref(elem, this.toc);
			}
		}
		// don't update file!
		return null;
	}

	protected void handleTopicref(final Element topicref, ToCNode parentToCNode) {
		final Attr hrefAttr = topicref.getAttributeNode(Constants.ATTRIBUTE_NAME_HREF);
		final Attr copytoAttr = topicref.getAttributeNode(Constants.ATTRIBUTE_NAME_COPY_TO);
		final Attr scopeAttr = topicref.getAttributeNode(Constants.ATTRIBUTE_NAME_SCOPE);
		final Attr formatAttr = topicref.getAttributeNode(Constants.ATTRIBUTE_NAME_FORMAT);
		final Attr typeAttr = topicref.getAttributeNode(Constants.ATTRIBUTE_NAME_TYPE);

		ToCNode toCNode = null;
		// ignore resource only refs and topic groups (topic groups must nov
		// have titles)
		URI topicPath = null;
		logger.info("Handling topicref " + DitaClass.getInstance(topicref));
		if (!ignoreNavigation(topicref)) {
			logger.debug("...is not a group and not resource ony" + DitaClass.getInstance(topicref));

			// create ToC entry
			String title = Configuration.getDefault().getTitleExtractor().getNavTitle(topicref);
			logger.info("...navtitle is " + title);
			toCNode = new ToCNode();
			parentToCNode.addChild(toCNode);
			toCNode.setTitle(title);
			parentToCNode = toCNode;
			if (hrefAttr != null && isDitaFormat(formatAttr) && isLocalScope(scopeAttr)) {
				if (copytoAttr != null) {
					final URI copyToUri = stripFragment(URLUtils.toURI(copytoAttr.getNodeValue()));
					topicPath = job.tempDirURI.relativize(filePath.resolve(copyToUri));
				} else {
					final URI hrefUri = stripFragment(URLUtils.toURI(hrefAttr.getNodeValue()));
					topicPath = job.tempDirURI.relativize(filePath.resolve(hrefUri));
				}

				// save topic type
				if (typeAttr != null && !StringUtils.isEmpty(typeAttr.getValue())) {
					logger.debug("...type is " + typeAttr.getValue());
				}

				// look into the topic
				handleTopic(toCNode, topicPath);
			}
		}
		OntologyCommon ontoObject = handleSubjectSchemeElement(topicref);
		if (ontoObject != null) {
			ontostack.push(ontoObject);
		}
		try {
			for (Element elem : XMLUtils.getChildElements(topicref)) {
				if (Constants.MAP_TOPICREF.matches(elem)) {
					handleTopicref(elem, parentToCNode);
				}
			}
		} finally {
			if (ontoObject != null) {
				ontostack.pop();
			}
		}
	}

	OntologyClass getClassFromOntoStack() {
		for (Iterator<OntologyCommon> iter = ontostack.iterator(); iter.hasNext();) {
			OntologyCommon o = iter.next();
			if (o instanceof OntologyClass) {
				return (OntologyClass) o;
			}
		}
		return null;
	}

	protected OntologyCommon handleSubjectSchemeElement(final Element element) {
		if (isSubjectSchemeRelated(element)) {
			if (Constants.SUBJECTSCHEME_SUBJECTDEF.matches(element)) {
				String title = Configuration.getDefault().getTitleExtractor().getNavTitle(element);
				logger.info("...subject title is " + title);
				Optional<Element> mdElement = XMLUtils.getChildElement(element, Constants.MAP_TOPICMETA);
				if (mdElement.isPresent()) {
					Optional<Element> resElement = XMLUtils.getChildElement(mdElement.get(),
							Constants.TOPIC_RESOURCEID);
					if (resElement.isPresent()) {
						String appid = XMLUtils.getValue(resElement.get(), "appid");
						String appname = XMLUtils.getValue(resElement.get(), "appname");
						String keys = XMLUtils.getValue(element, "keys");
						logger.info("   appid=" + appid + ", appname=" + appname + ", keys=" + keys);
						if (StringUtils.equals(appname, "instance-iri")) {
							OntologyInstance instance = new OntologyInstance();
							instance.setIRI(appid);
							instance.setLabel(title);
							instance.setKeys(StringUtils.split(keys));
							OntologyClass parent = getClassFromOntoStack();
							if (parent != null) {
								instance.setOntologyClass(parent);
							}
							this.ontologgy.register(instance);
							return instance;
						} else if (StringUtils.equals(appname, "class-iri")) {
							OntologyClass instance = new OntologyClass();
							instance.setIRI(appid);
							instance.setLabel(title);
							instance.setKeys(StringUtils.split(keys));
							OntologyClass parent = getClassFromOntoStack();
							if (parent != null) {
								instance.setSuperClass(parent);
							}
							this.ontologgy.register(instance);
							return instance;
						}
					}
				}
			}
		}
		return null;
	}

	protected void handleTopic(ToCNode toCNode, URI topicPath) {
		final FileInfo fi = job.getFileInfo(topicPath);
		if (fi == null) {
			logger.error("File {0} was not found", new File(job.tempDir, topicPath.getPath()));
		} else {
			final URI targetFileName = job.tempDirURI.resolve(topicPath);
			File topicFile = new File(targetFileName);
			logger.info("...topic file is {0}", targetFileName);
			if (!topicFile.canRead()) {
				logger.error("File {0} is not readable or not exiting.", topicFile);
			} else {
				try {
					IirdsTopicReader topicMetaReader = new IirdsTopicReader(toCNode);
					topicMetaReader.setJob(job);
					topicMetaReader.setLogger(logger);
					topicMetaReader.read(topicFile);
					logger.debug("...setting topicPath at ToC entry to {0}", topicPath);
					toCNode.setURI(topicPath);
					logger.debug("Processing {0} done.", topicFile);
				} catch (DITAOTException e) {
					logger.error("Failed to process {0}", topicFile, e);
				}
			}
		}
	}

	protected boolean ignoreNavigation(final Element elem) {
		return isBackMatter(elem) || isTopicGroup(elem) || isResourceOnly(elem) || isFrontMatter(elem)
				|| isBookLists(elem);
	}

	protected boolean isSubjectSchemeRelated(final Element elem) {
		return Constants.SUBJECTSCHEME_SUBJECTDEF.matches(elem) || Constants.SUBJECTSCHEME_HASKIND.matches(elem)
				|| Constants.SUBJECTSCHEME_SUBJECTSCHEME.matches(elem)
				|| Constants.SUBJECTSCHEME_SUBJECTHEAD.matches(elem)
				|| Constants.SUBJECTSCHEME_HASNARROWER.matches(elem) || Constants.SUBJECTSCHEME_HASRELATED.matches(elem)
				|| Constants.SUBJECTSCHEME_HASINSTANCE.matches(elem);
	}

	protected boolean isBackMatter(final Element elem) {
		return Constants.BOOKMAP_BACKMATTER.matches(elem);
	}

	protected boolean isBookLists(final Element elem) {
		return Constants.BOOKMAP_BOOKLISTS.matches(elem);
	}

	protected boolean isFrontMatter(final Element elem) {
		return Constants.BOOKMAP_FRONTMATTER.matches(elem);
	}

	protected boolean isTopicGroup(final Element elem) {
		return Constants.MAPGROUP_D_TOPICGROUP.matches(elem);
	}

	private boolean isLocalScope(final Attr scopeAttr) {
		return scopeAttr == null || Constants.ATTR_SCOPE_VALUE_LOCAL.equals(scopeAttr.getNodeValue());
	}

	private boolean isDitaFormat(final Attr formatAttr) {
		return formatAttr == null || Constants.ATTR_FORMAT_VALUE_DITA.equals(formatAttr.getNodeValue())
				|| Constants.ATTR_FORMAT_VALUE_DITAMAP.equals(formatAttr.getNodeValue());
	}

	protected boolean isResourceOnly(final Element elem) {
		return elem != null && isResourceOnly(elem.getAttributeNode(Constants.ATTRIBUTE_NAME_PROCESSING_ROLE));
	}

	protected boolean isResourceOnly(final Attr resourceAttr) {
		return resourceAttr != null
				&& Constants.ATTR_PROCESSING_ROLE_VALUE_RESOURCE_ONLY.equals(resourceAttr.getNodeValue());
	}
}
