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

package org.iirds.dita.ot.plugin.contrib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.rdf.IirdsConstants;
import org.iirds.rdf.facade.Factory;
import org.iirds.rdf.facade.InformationUnits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Metadata handler that extracts iiRDS metadata from {@code <critdates>} elements
 * of maps and topics. The {@code expiry} value is taken from {@code <created>}
 * and might be overridden by the latest {@code <revised>} element when present.
 * The {@code golive} value is taken from {@code <created>} element and might be
 * overridden by the latest {@code <revised>} element when present.
 * <p>
 * When {@code golive} or {@code expiry} has been found, a
 * {@code ContentLifeCycleStatus} resource carrying the dates with status
 * {@code Released} is added to the information unit when generating the iiRDS
 * model. The {@code dateOfStatus} is set to the date of the latest
 * {@code <revised>} element when present.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class CritdatesMetadataHandler implements IirdsMetadataHandler {

	static final String META_REVISED = "metadata/prolog/critdates/revised@modified";
	static final String META_CREATED = "metadata/prolog/critdates/created@date";
	static final String META_EXPIRY = "metadata/prolog/critdates/@expiry";
	static final String META_GOLIVE = "metadata/prolog/critdates/@golive";
	static Logger logger = LoggerFactory.getLogger(CritdatesMetadataHandler.class);

	static final String STARTTIME_APPENDIX = "T00:00:00";
	static final String ENDTIME_APPENDIX = "T23:59:59";

	SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat americanDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat germanMonthNameDateFormat = new SimpleDateFormat("dd. MMMM yyyy",Locale.GERMAN);
	SimpleDateFormat[] formats = { defaultDateFormat, americanDateFormat, germanMonthNameDateFormat };

	@Override
	public String getName() {
		return "critdates";
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Tries to parse a date string with difference formats
	 * 
	 * @param date the date string to parse
	 * @return the parse and reformat date in format yyyy-MM-dd or null, when the
	 *         date is not reformatable
	 */
	public String reformatDate(String date) {
		if (StringUtils.isBlank(date))
			return null;
		String trimmed = date.trim();
		for (SimpleDateFormat format : formats) {
			try {
				Date parsed = format.parse(trimmed);
				return defaultDateFormat.format(parsed);
			} catch (ParseException e) {
				// ignore, we try the next
			}
		}
		return null;
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		logger.info("Extracting copyright from {}", node.getNavTitle());
		try {
			Element root = document.getDocumentElement();
			Optional<Element> prolog = MetadataUtils.getMetadataHolderElement(root);
			if (prolog.isPresent()) {
				Optional<Element> critDatesElement = XMLUtils.getChildElement(prolog.get(), Constants.TOPIC_CRITDATES);
				if (critDatesElement.isPresent()) {
					String modified = null;
					String expiry = null;
					String golive = null;
					Optional<Element> createdElement = XMLUtils.getChildElement(critDatesElement.get(),
							Constants.TOPIC_CREATED);
					if (createdElement.isPresent()) {
						String date = reformatDate(createdElement.get().getAttribute("date"));
						if (!StringUtils.isBlank(date)) {
							node.setProperty(META_CREATED, date);
						}
						date = reformatDate(createdElement.get().getAttribute("expiry"));
						if (!StringUtils.isBlank(date)) {
							expiry = date;
						}
						date = reformatDate(createdElement.get().getAttribute("golive"));
						if (!StringUtils.isBlank(date)) {
							golive = date;
						}
					}
					List<Element> revisedElements = XMLUtils.getChildElements(critDatesElement.get(),
							Constants.TOPIC_REVISED);

					// look through all revision dates
					for (Element revisedElement : revisedElements) {
						String date = reformatDate(revisedElement.getAttribute("modified"));
						if (!StringUtils.isBlank(date) && (modified == null || date.compareTo(modified) > 0)) {
							modified = date;
							date = reformatDate(revisedElement.getAttribute("expiry"));
							if (!StringUtils.isBlank(date)) {
								expiry = date;
							}
							date = reformatDate(revisedElement.getAttribute("golive"));
							if (!StringUtils.isBlank(date)) {
								golive = date;
							}
						}
					}

					// save the dates
					if (modified != null) {
						node.setProperty(META_REVISED, modified);
					}
					if (expiry != null) {
						node.setProperty(META_EXPIRY, expiry);
					}
					if (golive != null) {
						node.setProperty(META_GOLIVE, golive);
					}
				}
			}
		} catch (

		RuntimeException e) {
			logger.error("Failed to extract copyright from {}", node.getNavTitle());
			throw e;
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {
		Resource infoUnit = root.getInformationUnit();
		String expiry = root.getPropertyAsString(META_EXPIRY);
		String created = root.getPropertyAsString(META_CREATED);
		String modified = root.getPropertyAsString(META_REVISED);
		String golive = root.getPropertyAsString(META_GOLIVE);
		if (infoUnit != null) {
			if (created != null) {
				InformationUnits.setDateOfCreation(infoUnit, created + STARTTIME_APPENDIX);
			}
			if (modified != null) {
				InformationUnits.setDateOfLastModification(infoUnit, modified + STARTTIME_APPENDIX);
			}
			if (expiry != null || golive != null) {
				Resource status = Factory.createContentLifecycleStatus(model);
				InformationUnits.setContentLifecycleStatus(infoUnit, status);
				status.addProperty(model.getProperty(IirdsConstants.HASCONTENTLIFECYCLESTATUSVALUE_PROPERTY_URI),
						infoUnit.getModel().getResource(IirdsConstants.iiRDS_URI + "Released"));
				if (modified != null) {
					status.addProperty(model.getProperty(IirdsConstants.DATEOFSTATUS_PROPERTY_URI),
							modified + STARTTIME_APPENDIX);
				}
				if (golive != null) {
					status.addProperty(model.getProperty(IirdsConstants.DATEOFEFFECT_PROPERTY_URI),
							golive + STARTTIME_APPENDIX);
				}
				if (expiry != null) {
					status.addProperty(model.getProperty(IirdsConstants.DATEOFEXPIRY_PROPERTY_URI),
							expiry + ENDTIME_APPENDIX);
				}
			}
		}
	}

}
