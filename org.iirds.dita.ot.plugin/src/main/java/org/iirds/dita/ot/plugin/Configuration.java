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

package org.iirds.dita.ot.plugin;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.util.Job;
import org.iirds.dita.ot.plugin.contrib.DefaultIRIHandler;
import org.iirds.dita.ot.plugin.module.ContentHashBuilder;
import org.iirds.dita.ot.plugin.module.IirdsModelBuilderImpl;
import org.iirds.dita.ot.plugin.module.IirdsModelSerializerImpl;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.dita.ot.plugin.spi.IRIHandlerProvider;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider;
import org.iirds.rdf.RDFConstants;
import org.iirds.rdf.facade.VocabularyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The configuration context of the iiRDS processing in the DITA-OT
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
@SuppressWarnings("deprecation")
public class Configuration {
	static Logger logger = LoggerFactory.getLogger(Configuration.class);

	static final String PARAM_TRANSTYPE = "transtype";
	static final String PARAM_METADATAHANDLERS = "METADATAHANDLERS";
	static final String PARAM_IRIHANDLERS = "IRIHANDLERS";
	static final String PARAM_OUTEXT = "OUTEXT";
	static final String PARAM_CONTENTPATH = "CONTENTPATH";

	private static Configuration singleton = new Configuration(null, null);

	private final Job job;
	private IirdsModelSerializer iirdsModelSerializer;
	private TitleExtractor titleExtractor;
	private LanguageExtractor languageExtractor;
	private IirdsModelBuilder iirdsModelBuilder;
	private TopicTypeMapper topicTypeMapper;
	private IRIHandler iriHandler;
	private CompositeMetadataHandler iirdsMetadataHandler;
	private Map<String, IRIHandler> definedIriHandlers = null;
	private Map<String, IirdsMetadataHandler> definedMetadataHandlers = null;
	private ContentHashBuilder contentHashBuilder;
	private String metadataHandlerNames = "all";
	private String iriHandlerNames = "default";
	private String htmlExt = ".html";
	private String contentPath = "content";
	private VocabularyFactory vocabularyFactory;
	private Model model;
	private InfModel infModel;

	private Configuration(Job job, AbstractPipelineInput input) {
		singleton = this;
		this.job = job;
		this.topicTypeMapper = new TopicTypeMapper();
		this.iriHandler = null;
		this.iirdsModelSerializer = new IirdsModelSerializerImpl();
		this.iirdsMetadataHandler = null;

		configureByInput(input); 

		this.titleExtractor = new TitleExtractor();
		this.languageExtractor = new LanguageExtractor();
		this.iirdsModelBuilder = new IirdsModelBuilderImpl();
		this.contentHashBuilder = new ContentHashBuilder();
	}

	void configureByInput(@SuppressWarnings("deprecation") AbstractPipelineInput input) {
		if (input != null) {
			String ext = input.getAttribute(PARAM_OUTEXT);
			if (!StringUtils.isBlank(ext)) {
				logger.debug("HTML extension configured as " + ext);
				this.htmlExt = ext;
			}
			String metahandlers = input.getAttribute(PARAM_METADATAHANDLERS);
			if (!StringUtils.isBlank(metahandlers)) {
				logger.debug("iiRDS metadata handlers configured as " + metahandlers);
				this.metadataHandlerNames = metahandlers;
				this.iirdsMetadataHandler = null;
			}
			String ihandlers = input.getAttribute(PARAM_IRIHANDLERS);
			if (!StringUtils.isBlank(ihandlers)) {
				logger.debug("IRI handlers configured as " + ihandlers);
				this.iriHandlerNames = ihandlers;
				this.iriHandler = null;
			}
			String path = input.getAttribute(PARAM_CONTENTPATH);
			if (!StringUtils.isBlank(path)) {
				logger.debug("iiRDS package content path configurated as " + path);
				this.contentPath = path;
			}
		}
	}

	public ContentHashBuilder getContentHashBuilder() {
		return contentHashBuilder;
	}

	public static Configuration getDefault() {
		return singleton;
	}

	public static Configuration create(Job job, AbstractPipelineInput input) {
		return new Configuration(job, input);
	}

	synchronized void configureModel() {
		if (model == null) {
			model = RDFConstants.createDefaultModel();
			infModel = RDFConstants.createInfModel(model);
			vocabularyFactory = new VocabularyFactory(infModel);
		}
	}

	public synchronized Model getModel() {
		configureModel();
		return this.model;
	}

	public synchronized Model getInfModel() {
		configureModel();
		return this.model;
	}

	public synchronized VocabularyFactory getVocabularyFactory() {
		configureModel();
		return this.vocabularyFactory;
	}

	synchronized void configureMetadataHandlers() {
		if (this.definedMetadataHandlers == null) {
			// load metadata handler definitions
			logger.debug("Loading iiRDS metadata handlers");
			this.definedMetadataHandlers = new HashMap<>();
			ServiceLoader<IirdsMetadataHandlerProvider> loader = ServiceLoader.load(IirdsMetadataHandlerProvider.class);
			loader.forEach(s -> {
				s.create().forEach(h -> {
					this.definedMetadataHandlers.put(h.getName(), h);
				});
			});
			logger.debug(MessageFormat.format("Found the following iiRDS metadata handlers {0} defined",
					definedMetadataHandlers.keySet()));
		}
		if (this.iirdsMetadataHandler == null) {
			CompositeMetadataHandler composite = new CompositeMetadataHandler("current");
			Arrays.stream(StringUtils.split(this.metadataHandlerNames, '+')).map(String::trim)
					.filter(s -> !"all".equals(s) && !s.isEmpty()).forEach(s -> {
						IirdsMetadataHandler h = this.definedMetadataHandlers.get(s);
						if (h != null) {
							composite.addMetadataHandler(h);
						} else {
							logger.error(MessageFormat.format("iiRDS metadata handler {0} is undefined", s));
						}
					});
			if (composite.getMetadataHandlers().isEmpty()) {
				// fallback: when there is nothing configured, use all known metadata
				// handlers
				composite.addMetadataHandlers(this.definedMetadataHandlers.values());
			}
			this.iirdsMetadataHandler = composite;
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format("Effective metadata handlers are: {0}", composite));
			}
		}
	}

	synchronized void configureIRIHandlers() {
		if (this.definedIriHandlers == null) {
			// load metadata handler definitions
			logger.debug("Loading IRI handlers");
			ServiceLoader<IRIHandlerProvider> loader = ServiceLoader.load(IRIHandlerProvider.class);
			this.definedIriHandlers = new HashMap<>();
			loader.forEach(s -> {
				IRIHandler h = s.create();
				this.definedIriHandlers.put(h.getName(), h);
			});
			if (logger.isDebugEnabled()) {
				logger.debug(MessageFormat.format("Found the following IRI handlers {0} defined",
						definedIriHandlers.keySet()));
			}
		}
		if (this.iriHandler == null) {
			CompositeIRIHandler composite = new CompositeIRIHandler("current");
			Arrays.stream(StringUtils.split(this.iriHandlerNames, '+')).map(String::trim)
					.filter(s -> !s.isEmpty()).forEach(s -> {
						IRIHandler h = this.definedIriHandlers.get(s);
						if (h != null) {
							composite.addIRIHandler(h);
						} else {
							logger.error(MessageFormat.format("IRI handler {0} is undefined", s));
						}
					});
			this.iriHandler = composite;
			if (logger.isDebugEnabled()) {
				if (composite.getIRIHandlers().isEmpty()) {
					logger.debug("There are no effective IRI handlers. Using fallback strategry to generated IRIs");
				} else {
					logger.debug(MessageFormat.format("Effective IRI handlers are: {0}", composite));
				}
			}
		}
	}

	public synchronized IirdsMetadataHandler getIirdsMetadataHandler() {
		configureMetadataHandlers();
		return this.iirdsMetadataHandler;
	}

	/**
	 * Get the configured IRIHandler
	 * 
	 * @return the IRI handler, never null
	 */
	public synchronized IRIHandler getIRIHandler() {
		configureIRIHandlers();
		return this.iriHandler;
	}

	public IirdsModelSerializer getIirdsModelSerializer() {
		return this.iirdsModelSerializer;
	}

	public IirdsModelBuilder getIirdsModelBuilder() {
		return this.iirdsModelBuilder;
	}

	public TitleExtractor getTitleExtractor() {
		return this.titleExtractor;
	}

	public LanguageExtractor getLanguageExtractor() {
		return this.languageExtractor;
	}

	public TopicTypeMapper getTopicTypeMapper() {
		return this.topicTypeMapper;
	}

	public Job getJob() {
		return this.job;
	}

	public String getContentPath() {
		return this.contentPath;
	}

	public String getHTMLExt() {
		return this.htmlExt;
	}

}
