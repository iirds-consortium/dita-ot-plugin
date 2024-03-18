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

package org.iirds.dita.ot.plugin.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.dita.dost.util.DitaClass;

/**
 * Represents a (potential) iiRDS DirectoryNode with optional InformationUnit in iiRDS as
 * extracted from the DITA input.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class ToCNode {
	URI uri;
	String title;
	String navtitle;

	String topicId;
	String contentHash;
	DitaClass ditaClass;
	ToCNode parent;
	Resource directoryNode;
	boolean isTopic = true;

	Resource informationUnit;
	String language;
	List<ToCNode> children = new ArrayList<>();
	Map<String, Object> properties = new HashMap<>();

	public ToCNode() {
	}

	public boolean hasContent() {
		return uri != null;
	}

	public boolean isTopic() {
		return this.isTopic;
	}

	public void setIsTopic() {
		this.isTopic = true;
	}

	public void setIsDocument() {
		this.isTopic = false;
	}

	public boolean isDocument() {
		return !this.isTopic;
	}

	public Resource getDirectoryNode() {
		return directoryNode;
	}

	public void setDirectoryNode(Resource directoryNode) {
		this.directoryNode = directoryNode;
	}

	public Resource getInformationUnit() {
		return informationUnit;
	}

	public void setInformationUnit(Resource informationUnit) {
		this.informationUnit = informationUnit;
	}

	public ToCNode getParent() {
		return parent;
	}

	public ToCNode getRoot() {
		ToCNode p = this;
		while (p.getParent() != null) {
			p = p.getParent();
		}
		return p;
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public String getPropertyAsString(String name) {
		Object o = properties.get(name);
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	public Map<String, Object> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public void setParent(ToCNode parent) {
		this.parent = parent;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	public DitaClass getDitaClass() {
		return ditaClass;
	}

	public void setDitaClass(DitaClass ditaClass) {
		this.ditaClass = ditaClass;
	}

	public ToCNode(URI uri) {
		setURI(uri);
	}

	public void addChild(ToCNode child) {
		children.add(child);
		child.setParent(this);
	}

	public List<ToCNode> getChildren() {
		return children;
	}

	public URI getURI() {
		return uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getNavTitle() {
		if (navtitle == null) {
			return title;
		}
		return navtitle;
	}

	public void setNavTitle(String navtitle) {
		this.navtitle = navtitle;
	}

	public String getContentHash() {
		return contentHash;
	}

	public void setContentHash(String contentHash) {
		this.contentHash = contentHash;
	}

	@Override
	public String toString() {
		return "ToCNode [uri=" + uri + ", title=" + title + ", navtitle=" + navtitle + ", topicId=" + topicId
				+ ", contentHash=" + contentHash + ", ditaClass=" + ditaClass + ", parent=" + parent
				+ ", directoryNode=" + directoryNode + ", informationUnit=" + informationUnit + ", language=" + language
				+ ", children=" + children + ", properties=" + properties + "]";
	}

}
