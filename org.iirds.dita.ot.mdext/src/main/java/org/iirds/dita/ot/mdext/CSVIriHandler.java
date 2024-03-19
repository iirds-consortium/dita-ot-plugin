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

package org.iirds.dita.ot.mdext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.rdf.IirdsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link org.iirds.dita.ot.plugin.spi.IRIHandler} implementation using a
 * mapping of iiRDS class IRI and label to IRIs.
 * <p>
 * It looks up IRIs in a CSV file (format compliant with RFC4180). The CSV file
 * is named {@value CSVIriHandler#FILENAME}. It must be present in the input
 * directory or in the current working directory. The input directory has
 * priority.
 * <p>
 * The CVSV file must have at least three columns:
 * <ol>
 * <li>The full IRI of the iiRDS class</li>
 * <li>The key (label) to look up for the in the given iiRDS class scope</li>
 * <li>The resulting IRI</li>
 * </ol>
 * <p>
 * The unique name of the IRI handler is {@value #NAME}
 * 
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc4180">RFC4180</a>
 * @see CSVIriHandlerProvider
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class CSVIriHandler implements IRIHandler {

	static Logger logger = LoggerFactory.getLogger(CSVIriHandler.class);
	static final String NAME = "csv";

	public static final String FILENAME = "iri-mapping.csv";

	private Map<Pair<String, String>, String> iriMapping = new HashMap<>();
	private boolean inited = false;

	private void init() {
		try {
			if (!inited) {

				File dir = new File(Configuration.getDefault().getJob().getInputDir());
				File csvFile = new File(dir, FILENAME);
				if (!csvFile.canRead()) {
					csvFile = new File(FILENAME).getAbsoluteFile();
				}
				if (csvFile.canRead()) {
					loadCSV(csvFile);
				} else {
					logger.warn("CSV file {} not found or not readable", csvFile);
				}
			}
		} catch (RuntimeException e) {
			logger.error("Failed to initialize IRI mapping", e);
			throw (e);
		} finally {
			inited = true;
		}

	}

	private void processRecord(CSVRecord rec) {
		if (rec.size() >= 3) {
			Pair<String, String> key = Pair.of(rec.get(0), rec.get(1));
			iriMapping.put(key, rec.get(2));
		} else {
			logger.error("Failed to process record {}: {}. It must have three values", rec.getRecordNumber(),
					Arrays.asList(rec.values()));
		}

	}

	private void loadCSV(File csvFile) {
		if (csvFile.canRead()) {
			logger.info("Loading IRI mapping from CSV {}", csvFile);
			try (Reader reader = new FileReader(csvFile)) {
				CSVFormat.DEFAULT.parse(reader).forEach(this::processRecord);
				logger.info("Got mapping: {}", iriMapping);
			} catch (IOException e) {
				logger.error("Failed to read CSV file {}", csvFile, e);
			}
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	private String getMapping(String classURI, String label) {
		init();
		Pair<String, String> key = Pair.of(classURI, label);
		String iri = iriMapping.get(key);
		if (!StringUtils.isBlank(iri)) {
			logger.debug("Found IRI mapping class={} key={}: {}", classURI, label, iri);
			return iri;
		} else {
			logger.warn("No IRI mappging found for class={} key={}", classURI, label);
			return null;
		}
	}

	@Override
	public String getInfoObjectIRI(ToCNode node) {
		return getMapping(IirdsConstants.INFORMATIONOBJECT_CLASS_URI, node.getTopicId());
	}

	@Override
	public String getDocumentOrTopicIRI(ToCNode node) {
		if (node.isDocument()) {
			return getMapping(IirdsConstants.DOCUMENT_CLASS_URI, node.getTopicId());
		} else if (node.isTopic()) {
			return getMapping(IirdsConstants.TOPIC_CLASS_URI, node.getTopicId());
		}
		return null;
	}

	@Override
	public String getPackageIRI(ToCNode node) {
		return getMapping(IirdsConstants.PACKAGE_CLASS_URI, node.getTopicId());
	}

	@Override
	public String getMetadataIRI(ToCNode node, String classURI, String label, String context) {
		return getMapping(classURI, label);
	}

	@Override
	public String toString() {
		return getName();
	}

}
