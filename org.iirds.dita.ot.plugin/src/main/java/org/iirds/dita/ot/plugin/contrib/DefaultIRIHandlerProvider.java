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

import org.iirds.dita.ot.plugin.spi.IRIHandler;
import org.iirds.dita.ot.plugin.spi.IRIHandlerProvider;

/**
 * The default IRI handler provider implementation. It provides {@link DefaultIRIHandler}.
 * It needs to be configured in
 * {@code META-INF/services} file of the JAR according to the JAva SPI specifications
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class DefaultIRIHandlerProvider implements IRIHandlerProvider {

	@Override
	public IRIHandler create() {
		return new DefaultIRIHandler();
	}

}
