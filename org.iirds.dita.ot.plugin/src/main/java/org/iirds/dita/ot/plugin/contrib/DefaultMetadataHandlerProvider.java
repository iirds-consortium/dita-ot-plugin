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

import java.util.ArrayList;
import java.util.List;

import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler;
import org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider;

/**
 * The default metadata handler provider implementation. It provides the
 * metadata handlers implemented in this DITA-OT plugin. It needs to be
 * configured in
 * {@code META-INF/services} file of the JAR according to the JAva SPI specifications
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class DefaultMetadataHandlerProvider implements IirdsMetadataHandlerProvider {

	@Override
	public List<IirdsMetadataHandler> create() {
		List<IirdsMetadataHandler> result = new ArrayList<>();
		result.add(new ShortdescMetadataHandler());
		result.add(new ProdnameMetadataHandler());
		result.add(new ComponentMetadataHandler());
		result.add(new AudienceMetadataHandler());
		result.add(new CopyrightMetadataHandler());
		result.add(new CritdatesMetadataHandler());
		result.add(new ProductPropertyHandler());
		result.add(new AudiencePropertyHandler());
		return result;
	}

}
