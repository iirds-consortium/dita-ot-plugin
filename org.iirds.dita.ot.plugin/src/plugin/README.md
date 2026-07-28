# iiRDS Plugin for DITA Open Toolkit

Version 1.1.0, August 2026

* Generates an iiRDS 1.3 package from a map or a single topic
* The resulting file is named ``<map or topic filename>.iirds``
* DITA-OT transtype: ``iirds``
* Content is rendered as HTML5
* Parameters of the ``html5`` transtype can be used to customize HTML output
* Requires DITA-OT version >= 3.7.3; tested with versions 3.7.1 and 4.1.1

For more information, visit [https://www.iirds.org/tools/dita-plugin/](https://www.iirds.org/tools/dita-plugin/)

## Copyright

Copyright 2024–2026 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org

This plugin is licensed under the Apache 2.0 license. See the LICENSE file included in the plugin distribution.

## Installation

There are several options for installing the plugin:

* To install from the download page, run ``dita install https://www.iirds.org/fileadmin/downloads/DITA/org.iirds.dita.package-1.1.0.zip``
* To install from the DITA-OT plugin registry, run ``dita install org.iirds.dita.package``
* To install from a downloaded plugin ZIP file, run ``dita install org.iirds.dita.package-1.1.0.zip``.
  If the ZIP file is not located in the current working directory, include the path to the file.

## Creating iiRDS Packages

Use the transtype ``iirds`` when calling the ``dita`` command. Either a map or a single topic can be provided as input for the transformation.

## Parameters

The following optional ``dita`` command-line parameters can be used to configure the iiRDS transformation:

* ``--args.outext`` defines the filename extension of HTML files in the iiRDS package (see also the HTML5 plugin). Example: ``--args.outext=htm``
* ``--iirds.contentpath`` defines the name of the folder within the iiRDS package that contains all content files. Defaults to ``content`` if not set. Example: ``--iirds.contentpath=files``
* ``--iirds.metadatahandler`` defines which iiRDS metadata handlers to use for extracting metadata from DITA. When not set, all known iiRDS metadata handlers are applied. The value is a ``+``-separated list of metadata handler names. Example: ``--iirds.metadatahandler=shortdesc+prodname``
* ``--iirds.irihandler`` defines which IRI handlers to use for assigning IRIs to metadata, information units, and information objects. The value is a ``+``-separated list of IRI handler names. Example: ``--iirds.irihandler=default``

## Details

* iiRDS DirectoryNodes are created for the map and all topics
* A Document is created for the input map (when a map is provided as input)
* When a single topic is transformed, no iiRDS DirectoryNodes are created
* iiRDS Topics are created and reference InformationObjects
* The default IRI handler behaves as follows:
  * Topic IRIs are URNs consisting of the topic ID and a hash of the content after preprocessing and before rendering
  * Document IRIs are URNs containing the map ID
  * Package IRIs are URNs containing a UUID
  * Metadata IRIs are URNs containing a hash of the metadata class name and the label extracted from DITA
* The output directory will contain the generated iiRDS file (with the ``.iirds`` extension)

### Metadata

The following metadata are set in iiRDS:

* The document type is set to "Operation Instructions"
* Title and language are always extracted from the DITA files and assigned as iiRDS metadata
* Topic types are derived from the ``class`` attribute value of the respective topic

#### iiRDS Metadata Handlers

The plugin includes a set of predefined metadata handlers for extracting additional metadata from DITA:

* ``shortdesc``: Uses the content of ``shortdesc`` elements as ``iirds:has-abstract`` properties
* ``prodname``: Uses the content of ``prodname`` elements as ``iirds:relates-to-product-variant`` properties
* ``component``: Uses the content of ``component`` elements as ``iirds:relates-to-component`` properties
* ``product-p``: Uses the content of the ``product`` root attribute as ``iirds:relates-to-product-variant`` properties
* ``critdates``: Uses the child elements of the ``critdates`` element to set ``iirds:dateOfLastModification``, ``iirds:dateOfCreation``, ``iirds:dateOfEffect``, and ``iirds:dateOfExpiry`` properties
* ``copyright``: Uses the ``copyright`` element to set ``iirds:rights`` properties
* ``audience``: Uses the ``audience`` element to set ``iirds:relates-to-qualification`` properties
* ``audience-p``: Uses the content of the ``audience`` root attribute as ``iirds:relates-to-qualification`` properties
* **NEW in version 1.1.0:** ``subjectscheme``: Uses ``subjectdef`` elements referenced via ``keyref`` by ``data`` elements within ``topicmeta``

When not explicitly specified on the command line via the ``--iirds.metadatahandler`` parameter, all available metadata handlers are applied to extract iiRDS metadata from DITA. Additional DITA-OT plugins may register further metadata handlers, which are also taken into account.

For more details, refer to the documentation included with the plugin.

### IRI Handlers

The plugin includes one default IRI handler named ``default``. For details on its behavior, see the _Details_ section.

The ``--iirds.irihandler`` parameter can be used to specify which IRI handlers to apply. When the parameter is not set, the ``default`` handler is used.

Multiple IRI handlers can be specified as a sequence. When resolving an IRI, the plugin calls each handler in the specified order. If a handler does not provide an IRI, the next handler in the sequence is called.

If none of the handlers provides an IRI, a fallback mechanism generates unique UUID URNs for topics, documents, metadata, and information objects.

### Extension Points

The plugin defines three extension points:

* ``iirds.extractMetadata.pre``: An ant target called immediately before metadata extraction begins
* ``iirds.extractMetadata.post``: An ant target called immediately after metadata extraction is complete. Implementations can access the generated ``metadata.rdf`` file.
* ``iirds.package.pre``: An ant target called after transformation, immediately before the transformed files and metadata are packaged into an iiRDS package

## Implementing iiRDS Metadata and IRI Handlers

To extend the plugin with custom metadata or IRI handlers, follow these steps:

1. Create a new DITA-OT plugin to hold all your extensions.
2. Write the code for your handlers, create a JAR file, and place it in the plugin — for example, in the ``lib`` folder. See the **Writing the Code** section for details.
3. Add a feature entry for your code in the plugin descriptor, for example:
   ``<feature extension="dita.conductor.lib.import" file="lib/myext.jar"/>``
4. Install your plugin into DITA-OT.

### Writing the Code

It is recommended to review the provided API documentation and the source code of this plugin to understand the relevant interfaces.

For each handler, write a Java class implementing either the ``org.iirds.dita.ot.plugin.spi.IRIHandler`` or the ``org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler`` interface. These interfaces are provided in the ``org.iirds.dita.package.jar`` file included with the plugin.

In addition, the following service provider interfaces must be implemented: ``org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider`` and ``org.iirds.dita.ot.plugin.spi.IRIHandlerProvider`` are used to register and expose all handlers defined in your plugin.

Finally, the provider classes must be published using the Java Service Provider Interface (SPI) mechanism. Create a text file at ``META-INF/services/org.iirds.dita.ot.plugin.spi.IRIHandlerProvider`` or ``META-INF/services/org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider`` within your JAR file. Each file must contain the fully qualified name of the corresponding provider class, with one class name per line. For more information on Java SPI, see [Introduction to the Service Provider Interfaces](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html).

### Source Code

The plugin source code is available at [https://github.com/iirds-consortium/dita-ot-plugin](https://github.com/iirds-consortium/dita-ot-plugin).