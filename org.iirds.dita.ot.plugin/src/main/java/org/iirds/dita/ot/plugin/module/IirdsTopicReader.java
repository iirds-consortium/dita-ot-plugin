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

import static org.dita.dost.util.Constants.TOPIC_TOPIC;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.util.DitaClass;
import org.dita.dost.util.XMLUtils;
import org.dita.dost.writer.AbstractDomFilter;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * Reads and DITA topic and extract information relevant to iiRDS processing.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class IirdsTopicReader extends AbstractDomFilter {

	private final ToCNode toc;

	private boolean sawTopic = false;

	/**
	 * Constructor.
	 * @param toc the node representing the topic to read
	 */
	public IirdsTopicReader(ToCNode toc) {
		this.toc = toc;
	}

	/**
	 * Read topic file and process the XML DOM via {@link #process(Document)}
	 * 
	 * @param filename the filename of the topic file
	 */
	@Override
	public void read(final File filename) throws DITAOTException {
		logger.info("Reading topic {0}", filename);
		super.read(filename);
	}

	/**
	 * Read DOM and process
	 */
	@Override
	public Document process(final Document doc) {
		process(doc.getDocumentElement());

		// run metadata extraction
		Configuration.getDefault().getIirdsMetadataHandler().extractMetadata(this.toc, doc);

		// don't overwrite topic
		return null;
	}

	protected void process(final Element elem) {
		if (!sawTopic && TOPIC_TOPIC.matches(elem)) {
			sawTopic = true;
			Configuration.getDefault().getLanguageExtractor().extractLanguage(toc, elem);

			// if there is a title, make it the navigation title:
			toc.setNavTitle(toc.getTitle());
			String title = Configuration.getDefault().getTitleExtractor().getTopicTitle(elem);
			if (!StringUtils.isBlank(title)) {
				toc.setTitle(title);
			}
			toc.setIsTopic();
			toc.setTopicId(elem.getAttribute("id"));
			toc.setDitaClass(DitaClass.getInstance(elem));
			toc.setContentHash(Configuration.getDefault().getContentHashBuilder().getHash(elem));
			logger.info("Content hash of topic is " + toc.getContentHash());
		}
		if (!sawTopic) {
			XMLUtils.getChildElements(elem).forEach(this::process);
		}
	}

}
