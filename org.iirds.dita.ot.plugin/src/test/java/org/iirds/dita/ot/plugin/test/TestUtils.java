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
package org.iirds.dita.ot.plugin.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dita.dost.util.Job;
import org.dita.dost.util.Job.FileInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Martin Kreutzer, Empolis Information Management GmbH
 *
 */
public class TestUtils {

	private static final DocumentBuilderFactory factory;
	static {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
	}

	public static Document loadXMLFile(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(xmlFile);
	}
	
	public static Document createDOM(String xml) {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
			return db.parse(is);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create DOM", e);
		}
	}

	public static Element createElement(String xml) {
		return createDOM(xml).getDocumentElement();
	}

	

	public static void createFileInfos(Job job, File dir, String path) throws Exception {
		if (!StringUtils.isEmpty(path)) {
			if (!path.endsWith("/")) {
				path+="/";
			}
		}
		for (File file : dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".list"))
					return false;
				return true;
			}
		})) {
			if (file.canRead()) {
				String ext = FilenameUtils.getExtension(file.getName());
				if (ext.equals("dita") | ext.equals("ditamap")) {
					FileInfo fileInfo = new FileInfo.Builder().uri(new URI(path + file.getName()))
							.file(new File(path + file.getName())).format(ext).build();
					job.add(fileInfo);
				} else
					continue;
			} else if (file.isDirectory()) {
				createFileInfos(job, file, path + file.getName());
			}
		}
	}
	
}
