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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.w3c.dom.Document;

/**
 * A composite metadata handler. It consists of other metadata handlers added by
 * {@link #addMetadataHandler(IirdsMetadataHandler)}. The methods delegate to
 * all member handler.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class CompositeMetadataHandler implements IirdsMetadataHandler {

	private String name = "";
	private List<IirdsMetadataHandler> handlers = new ArrayList<>();

	public CompositeMetadataHandler() {
		setName("composite");
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.handlers.toString();
	}

	/**
	 * Create a composite metadata handler and provide the handlers
	 * @param name the name of this handler
	 * @param handlers the handlers making up this composite metadata handler
	 */
	public CompositeMetadataHandler(String name, IirdsMetadataHandler... handlers) {
		setName(name);
		Arrays.stream(handlers).forEach(this::addMetadataHandler);
	}

	/**
	 * @return unmodifiable list of metadata handlers composing this metadata
	 *         handler
	 */
	public List<IirdsMetadataHandler> getMetadataHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	/**
	 * Adds metadata handlers
	 * 
	 * @param handlers the metadata handlers to add
	 */
	public void addMetadataHandlers(Collection<IirdsMetadataHandler> handlers) {
		handlers.forEach(this::addMetadataHandler);
	}

	/**
	 * Add an metadata handler
	 * 
	 * @param handler the metadata handler to add
	 */
	public void addMetadataHandler(IirdsMetadataHandler handler) {
		if (handler != null) {
			this.handlers.add(handler);
		}
	}

	@Override
	public void extractMetadata(ToCNode node, Document document) {
		handlers.forEach(h -> h.extractMetadata(node, document));

	}

	@Override
	public void addToModel(ToCNode node, Model model) {
		handlers.forEach(h -> h.addToModel(node, model));
	}

	@Override
	public void completeModel(ToCNode root, Model model) {
		handlers.forEach(h -> h.completeModel(root, model));
	}

}
