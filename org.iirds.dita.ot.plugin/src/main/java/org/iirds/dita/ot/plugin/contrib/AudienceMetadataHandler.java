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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.Constants;
import org.dita.dost.util.XMLUtils;
import org.iirds.dita.ot.plugin.Configuration;
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
 * Metadata extractor for {@code <audience>} element in DITA. It takes type as
 * role and experiencelevel as skill level
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class AudienceMetadataHandler implements IirdsMetadataHandler {
	static final String META_ROLE = "metadata/prolog/audience@type";
	static final String META_SKILL = "metadata/prolog/audience@skilllevel";
	static Logger logger = LoggerFactory.getLogger(AudienceMetadataHandler.class);

	@Override
	public String getName() {
		return "audience";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		try {
			logger.info("Extracting role/skill from {}", node.getNavTitle());
			Element root = document.getDocumentElement();
			Set<String> roles = new HashSet<>();
			Set<String> skills = new HashSet<>();
			Optional<Element> metadata = MetadataUtils.getMetadataHolderElement(root);
			if (metadata.isPresent()) {
				List<Element> audienceElements = XMLUtils.getChildElements(metadata.get(), Constants.TOPIC_AUDIENCE,
						true);
				for (Element audience : audienceElements) {
					String role = audience.getAttribute("type");
					if (!StringUtils.isBlank(role)) {
						roles.add(role.trim());
					}
					String skill = audience.getAttribute("experiencelevel");
					if (!StringUtils.isBlank(skill)) {
						skills.add(skill.trim());
					}
				}
			}
			if (!roles.isEmpty()) {
				node.setProperty(META_ROLE, roles);
			}
			if (!skills.isEmpty()) {
				node.setProperty(META_SKILL, skills);
			}
		} catch (RuntimeException e) {
			logger.error("Failed to extract role and skill from <audience>", e);
			throw e;
		}
	}

	@Override
	public void addToModel(ToCNode root, Model model) {
		logger.info("Setting role/skill at {}", root.getNavTitle());
		try {
			Configuration config = Configuration.getDefault();
			Resource infoUnit = root.getInformationUnit();
			if (infoUnit != null) {
				@SuppressWarnings("unchecked")
				Set<String> roles = (Set<String>) root.getProperty(META_ROLE);
				if (roles != null && !roles.isEmpty()) {
					for (String role : roles) {
						String iri = config.getIRIHandler().getMetadataIRI(root, IirdsConstants.ROLE_CLASS_URI, role,
								META_ROLE);
						Resource roleR = Factory.createRole(model, iri);
						Factory.setLabel(roleR, role, root.getLanguage());
						InformationUnits.addRelatedQualification(infoUnit, roleR);
					}
				}
				@SuppressWarnings("unchecked")
				Set<String> skills = (Set<String>) root.getProperty(META_SKILL);
				if (skills != null && !skills.isEmpty()) {
					for (String skill : skills) {
						String iri = config.getIRIHandler().getMetadataIRI(root, IirdsConstants.SKILLLEVEL_CLASS_URI,
								skill, META_SKILL);
						Resource skillR = Factory.createSkillLevel(model, iri);
						Factory.setLabel(skillR, skill, root.getLanguage());
						InformationUnits.addRelatedQualification(infoUnit, skillR);
					}
				}
			}
		} catch (RuntimeException e) {
			logger.error("Failed to set role and skill", e);
			throw e;
		}
	}

}
