# iiRDS Sample Extension Plugin

This plugin serves to demonstrate how the iiRDS plugin can be extended by additional plugin.

The iiiRDS plugin provides extension mechanisms via Java SPI architecture and the extension point mechanism of DITA OT.

This sample plugin shows the usage of the extension mechanisms:

* Providing two additional metadata extractions via Java SPI
* Providing an additional handler to generate IRIs via Java SPI
* Providing additional post-processing steps via extension points defined by the iiRDS plugin


# Post-processing via DITA-OT extension points

The sample plugin implements the extension points ``iirds.extractMetadata.post`` and ``iirds.package.pre``. The extensions are ant tasks defined in ``build.xml`` and configured in ``plugin.xml``. Look into the files to learn more.


# Additional metadata extraction

The sample plugin implements two metadata extraction extension as Java code: ``AdmonitionMetadataHandler`` and `` ``OtherMetadadataHandler``.

Both classes implement the interface ``IirdsMetadataHandler`` which is defined in the iiRDS plugin code. Each extraction must have a unique name.

The extractions are registered by class ``MetadataHandlerProvider``. It implements interface ``IirdsMetadataHandlerProvider``. The ``create()`` method instantiates the extraction classes.

# Additional IRI handler

The sample plugin implements one IRI handler as Java code: ``CSVIriHandler``. The class implements the interface ``IRIHandler`` which is defined in the iiRDS plugin code. 

The IRI handler is registered by class ``CSVIriHandlerProvider``. It implements interface ``IRIHandlerProvider``. The ``create()`` method instantiates the IRI handler classes.

The implementation of the IRI handler looks up a CSV file named ``iri-mapping.csv`` in the input directory or when not found in the current working directory. It must be a CSV file compliant with RFC4180. See the source code for more details.





