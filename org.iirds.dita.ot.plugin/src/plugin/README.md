# iiRDS Plugin for DITA Open Toolkit

* Generates an iiRDS 1.3 package from a map or a single topic
* The resulting file is ``<file name of map or topic>.iirds``
* DITA-OT transtype: ``iirds``
* Content is rendered as HTML5
* Parameters of the ``html5`` transtype can be used to customize HTML output
* Requires DITA-OT version >= 3.7.3

For more information, see [https://www.iirds.org/tools/dita-plugin/](https://www.iirds.org/tools/dita-plugin/)

## Copyright

Copyright 2024-2025 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org

This plugin is licensed under the Apache 2.0 license, see LICENSE file in the plugin distribution.


## Installation

There are several option how install the plugin:
* To install from download page, run ``dita install https://www.iirds.org/fileadmin/downloads/DITA/org.iirds.dita.package-1.1.0.zip`` 
* To install from DITA-OT plugin registry, run ``dita install org.iirds.dita.package``
* To install from downloaded plugin zip, run ``dita install org.iirds.dita.package-1.1.0.zip``. 
  if the zip file is not located in the current working directory, add the path to the zip file name, 


## Creating iiRDS Packages

Use the transtype ``iirds`` in the call of the ``dita`` command. 
Either a map or single topic can be provided as input to the transformation.

## Parameters

Use the following dita command line optional parameters to influence the iiRDS transformation:

* ``--args.outext`` defines the filename extension of HTML files in the iiRDS package (see also HTML5 plugin). Example: ``--args.outext=htm``
* ``--iirds.contentpath`` defines the name of the folder in the iiRDS package containing all content files. When not set, ``content`` is used. Example: ``--iirds.contentpath=files``
*  ``--iirds.metadatahandler`` defines which iiRDS metadata handlers to be used for metadata extraction from DITA. When not set, all known iiRDS metadata handlers are applied. The parameter value is a + separated list of iiRDS metadata handler names. Example: ``--iirds.metadatahandler=shortdesc+prodname``. 
* ``--iirds.irihandler`` defines which IRI handlers to be used for assigning IRIs to metadata, information units and information objects. The parameter value is a + separated list of IRI handler names.
Example: ``--iirds.irihandler=default``.


## Details

* iiRDS DirectoryNodes get created for the map and all topics
* A Document gets created for the input map (if map is provided as input)
* When a single topic gets transformed, no iiRDS DirectoryNodes get created
* iiRDS Topics get created and reference InformationObjects
* The default IRI handler works as follows:
  * Topic IRIs are URNs consisting of of the topic ID and a hash of the content after preprocessing and before  rendering 
  * Document IRIs are URNs containing the map ID
  * Package IRIs are URNs containing a UUID
  * Metadata IRIs are URNs containing a hash of the metadata class name and the extracted label from DITA
* The output directory will contain the generated iiRDS file (filename extension ``.iirds``)


### Metadata

In iiRDS the following metadata are set:

* The document type is set to "Operation Instructions"
* title and language get always extracted from the DITA files and assigned as iiRDS metadata
* The topic types of topics are derived from the ``class`` attribute value of the respective topic


#### iiRDS Metadata Handlers

The plugin comes with a number of predefined metadata handlers to extract additional metadata from DITA.

* ``shortdesc``: Use the content of ``shortdesc`` elements as ``iirds:has-abstract`` properties 
* ``prodname``: Use the contents of ``prodname`` elements as ``iirds:relates-to-product-variant`` properties
* ``component``:  Use the contents of ``component`` elements as ``iirds:relates-to-component`` properties
* ``product-p``: Use the content of the ``product`` root attribute  as ``iirds:relates-to-product-variant`` properties
* ``critdates``: Use the child elements of ``critdates`` element to set ``iirds:dateOfLastModification``, ``iirds:dateOfCreation``, ``iirds:dateOfEffect``, ``iirds:dateOfExpiry`` properties
*  ``copyright``: Use the ``copyright`` element to set ``iirds:rights`` properties
* ``audience``: Use the ``audience`` element to set ``iirds:relates-to-qualification`` properties
* ``audience-p``: Use the content of the ``audience`` root attribute as ``iirds:relates-to-qualification`` properties

When not explicitly specified on the command line via ``--iirds.metadatahandle`` parameter, all available metadata handlers are applied
to extract iiRDS metadata from DITA. Additional DITA-OT plugin might register further metadata handlers, they are also taken into account. 

### IRI Handlers

The plugin comes with one default IRI handler called 'default'.  Behavior, see section *Details*.
With parameter ``--iirds.irihandler`` the IRI handlers to be applied can be specified. When the parameter is not set, 'default' is used.
Several IRI handlers can be given in parameters build a sequence of IRI handlers. To obtain an IRI the plugins will call the handlers in the
given sequence. When a handler does not provide an IRI the next handler in the sequence is called.
In the case non of the handlers provided an IRI, a fallback mechanism provides an IRI anyway. The fallback generates unique UUID URNs for topics, 
documents, metadata, and information objects.


### Extension Points
The plugin defines three extension points:

* ``iirds.extractMetadata.pre``: ant target called to do some processing immediately before the metadata get extracted
* ``iirds.extractMetadata.post``: ant target called to do some post processing immediately after the metadata have been extracted.
  Implementations can access the generated ``metadata.rdf`` file.
* ``iirds.package.pre``: ant target called after transformation, immediately before packaging the transformed files and metadata to an
  iiRDS package.
 

## Implementing iiRDS Metadata and IRI Handlers

In order to extend the plugin by your own metadata or IRI handlers, follow these steps:

1. Create a new DITA-OT plugin. 
   It can hold all your extensions. 

2. Write the code for your handlers. Create a jar and put it into the plugin, e.g. in the
   folder ``lib``. See section **Writing the Code**.
   
4. Add a feature for your code into your plugin descriptor, e.g.,
   ``<feature extension="dita.conductor.lib.import" file="lib/myext.jar"/>``
   
5. Install your plugin into DITA-OT.   
   
   
### Writing the Code
It is recommended to have a look into the API docs provided and/or the sources of this plugin to understand the relevant interfaces.
For each handler write a Java class implementing either the interface ``org.iirds.dita.ot.plugin.spi.IRIHandler`` 
or ``org.iirds.dita.ot.plugin.spi.IirdsMetadataHandler``. The interfaces are delivered in file ``org.iirds.dita.package.jar`` 
of the plugin. 

In addition, service provider interfaces must be implemented:
``org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider`` and 
``org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider`` are used to register and provide all your handlers of your plugin.

Finally, the provider classes need to be published to the Java Service Provider Interface architecture. 
Create a text file ``META-INF/services/org.iirds.dita.ot.plugin.spi.IRIHandlerProvider`` or
 ``META-INF/services/org.iirds.dita.ot.plugin.spi.org.iirds.dita.ot.plugin.spi.IirdsMetadataHandlerProvider`` in your JAR file.
The files must just contain the names of the respective provider classes (one per line). For more information on Java SPI, 
see [Introduction to the Service Provider Interfaces](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html).


