/*******************************************************************************
 * Copyright 2024 
 * Empolis Information Management GmbH 
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
package org.dita.dost.util;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Proxy class to enable access package private methods of
 * {@link org.dita.dost.util.FilterUtils} .
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class FilterUtilsProxy {

	private static final FilterUtils impl = new FilterUtils(false);

	private FilterUtilsProxy() {
	}

	/**
	 * Get a map of groups to their values of a filter attribute
	 * 
	 * @param value the filter attribute value to analyze
	 * @return {@link FilterUtils#getGroups(String)}
	 */
	public static Map<QName, List<String>> getGroups(String value) {
		return impl.getGroups(value);
	}
}
