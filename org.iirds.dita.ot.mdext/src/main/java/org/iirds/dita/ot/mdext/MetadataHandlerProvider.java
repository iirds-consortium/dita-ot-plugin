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

import java.util.ArrayList;
import java.util.List;

import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider;

/**
 * iiRDS metadata handler service provider. It provides
 * {@link AdmonitionMetadataHandler} and {@link OtherMetadataHandler}.
 * <p>
 * The service provider is configured in file
 * {@code META-INF/services/org.iirds.dita.ot.plugin.spi} of the JAR artifact of
 * this project
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class MetadataHandlerProvider implements IirdsMetadataHandlerProvider {

	@Override
	public List<IirdsMetadataHandler> create() {
		List<IirdsMetadataHandler> list = new ArrayList<>(2);
		list.add(new AdmonitionMetadataHandler());
		list.add(new OtherMetadataHandler());
		return list;
	}
}
