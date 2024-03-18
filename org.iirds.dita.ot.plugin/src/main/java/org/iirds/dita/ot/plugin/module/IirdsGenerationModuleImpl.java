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

package org.iirds.dita.ot.plugin.module;

import static org.dita.dost.util.Constants.ANT_INVOKER_EXT_PARAM_TRANSTYPE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.dita.dost.exception.DITAOTException;
import org.dita.dost.module.AbstractPipelineModuleImpl;
import org.dita.dost.pipeline.AbstractPipelineInput;
import org.dita.dost.pipeline.AbstractPipelineOutput;
import org.dita.dost.util.Job;
import org.iirds.dita.ot.plugin.Configuration;
import org.iirds.dita.ot.plugin.IirdsModelBuilder;
import org.iirds.dita.ot.plugin.IirdsModelSerializer;
import org.iirds.dita.ot.plugin.model.ToCNode;

@SuppressWarnings("deprecation")
public class IirdsGenerationModuleImpl extends AbstractPipelineModuleImpl {

	/**
	 * Entry point to process DITA content (the input file) and its topics in case
	 * of a map for iiRDS generation
	 *
	 * @param input Input parameters and resources.
	 * @return null
	 * @throws DITAOTException in case of issues
	 * @author Martin Kreutzer, Empolis Information Management GmbH
	 * 
	 */
	@Override
	public AbstractPipelineOutput execute(final AbstractPipelineInput input) throws DITAOTException {
		@SuppressWarnings("unused")
		final String transtype = input.getAttribute(ANT_INVOKER_EXT_PARAM_TRANSTYPE);
		Configuration.create(job, input);
		// change to xml property
		final IirdsDitaReader mapReader = new IirdsDitaReader();
		mapReader.setLogger(logger);
		mapReader.setJob(job);
		try {
			final Job.FileInfo in = job.getFileInfo(fi -> fi.isInput).iterator().next();
			final File inputFile = new File(job.tempDirURI.resolve(in.uri));
			mapReader.read(inputFile, in.uri);
			Model model = generateModel(mapReader.getToC());
			generateRDF(model);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to extract and save iiRDS information");
			throw new DITAOTException("Failed to extract iiRDS information", e);
		}
		return null;
	}

	protected Model generateModel(ToCNode toc) {
		logger.info("Creating iiRDS model");
		IirdsModelBuilder modelBuilder = Configuration.getDefault().getIirdsModelBuilder();
		return modelBuilder.addPackage(toc).addInformationUnits(toc).addInfoObjects(toc).addDirectory(toc)
				.addMetadata(toc).completeModel(toc).getModel();
	}

	protected void generateRDF(Model model) throws IOException {
		File metainfDir = new File(job.getOutputDir(), Constants.METAINF_DIRNAME);
		File rdfFile = new File(metainfDir, Constants.RDF_FILENAME);
		logger.info("Saving iiRDS model to file to " + rdfFile);

		IirdsModelSerializer serializer = Configuration.getDefault().getIirdsModelSerializer();

		if (logger.isDebugEnabled()) {
			StringWriter writer = new StringWriter();
			serializer.generateRDFXML(model, writer);
			logger.debug(writer.getBuffer().toString());
		}

		logger.info("Saving iiRDS model to file to " + rdfFile);
		metainfDir.mkdirs();
		try (FileWriter writer = new FileWriter(rdfFile)) {
			serializer.generateRDFXML(model, writer);
			writer.flush();
		} catch (IOException e) {
			logger.error("Failed to write RDF file", e);
			throw e;
		}
	}
}
