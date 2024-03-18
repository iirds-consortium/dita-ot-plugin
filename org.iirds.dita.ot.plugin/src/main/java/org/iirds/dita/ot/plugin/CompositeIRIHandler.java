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

import org.iirds.dita.ot.plugin.contrib.DefaultIRIHandler;
import org.iirds.dita.ot.plugin.contrib.FallbackIRIHandler;
import org.iirds.dita.ot.plugin.model.ToCNode;
import org.iirds.dita.ot.plugin.spi.IRIHandler;

/**
 * A composite IRI handler. It consists of other IRI handlers added by
 * {@link #addIRIHandler(IRIHandler)}. The interface methods delegate to all member
 * handlers.
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class CompositeIRIHandler implements IRIHandler {

	private String name = "";
	private List<IRIHandler> handlers = new ArrayList<>();

	private FallbackIRIHandler fallback = new FallbackIRIHandler();

	public CompositeIRIHandler() {
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
	 * 
	 * @param name     the name of the this composite IRI handler
	 * @param handlers the handlers making up this composite metadata handler
	 */
	public CompositeIRIHandler(String name, IRIHandler... handlers) {
		setName(name);
		Arrays.stream(handlers).forEach(this::addIRIHandler);
	}

	/**
	 * @return unmodifiable list of metadata handlers composing this metadata
	 *         handler
	 */
	public List<IRIHandler> getIRIHandlers() {
		return Collections.unmodifiableList(handlers);
	}

	/**
	 * Adds metadata handlers
	 * 
	 * @param handlers the metadata handlers to add
	 */
	public void addIRIHandlers(Collection<IRIHandler> handlers) {
		handlers.forEach(this::addIRIHandler);
	}

	private int getInsertPosition() {
		for (int i = 0; i < this.handlers.size(); i++) {
			if (handlers.get(i) instanceof DefaultIRIHandler) {
				return i;
			}
		}
		return this.handlers.size();
	}

	/**
	 * Add an metadata handler
	 * 
	 * @param handler the metadata handler to add
	 */
	public void addIRIHandler(IRIHandler handler) {
		if (handler != null) {
			// make sure the new handler is located before the default handler
			int pos = getInsertPosition();
			this.handlers.add(pos, handler);
		}
	}

	@Override
	public String getInfoObjectIRI(ToCNode node) {
		for (IRIHandler handler : handlers) {
			String iri = handler.getInfoObjectIRI(node);
			if (iri != null) {
				return iri;
			}
		}
		return fallback.getInfoObjectIRI(node);
	}

	@Override
	public String getDocumentOrTopicIRI(ToCNode node) {
		for (IRIHandler handler : handlers) {
			String iri = handler.getDocumentOrTopicIRI(node);
			if (iri != null) {
				return iri;
			}
		}
		return fallback.getDocumentOrTopicIRI(node);
	}

	@Override
	public String getPackageIRI(ToCNode node) {
		for (IRIHandler handler : handlers) {
			String iri = handler.getPackageIRI(node);
			if (iri != null)
				return iri;
		}
		return fallback.getPackageIRI(node);
	}

	@Override
	public String getMetadataIRI(ToCNode node, String classURI, String label, String context) {
		for (IRIHandler handler : handlers) {
			String iri = handler.getMetadataIRI(node, classURI, label, context);
			if (iri != null)
				return iri;
		}
		return fallback.getMetadataIRI(node, classURI, label, context);
	}

}
