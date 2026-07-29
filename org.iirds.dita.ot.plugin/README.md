# iiRDS Plugin for DITA Open Toolkit

Version 1.1.0, August 2026

* Generates an iiRDS 1.3 package from a map or a single topic
* The resulting file is ``<file name of map or topic>.iirds``
* DITA-OT transtype: ``iirds``
* Content is rendered as HTML5
* Parameters of the ``html5`` transtype can be used to customize HTML output
* Requires DITA-OT version >= 3.7.3; tested with versions 3.7.1 and 4.1.1

For more information, see [https://github.com/iirds-consortium/dita-ot-plugin/tree/main/org.iirds.dita.ot.plugin/src/plugin](https://github.com/iirds-consortium/dita-ot-plugin/tree/main/org.iirds.dita.ot.plugin/src/plugin)

## Copyright

Copyright 2024–2026 Gesellschaft für Technische Kommunikation – tekom Deutschland e.V., https://iirds.org

This plugin is licensed under the Apache 2.0 license. See the LICENSE file included in the plugin distribution.


## Building the plugin
The plugin is implemented as a  __Maven__  project. Before building, ensure you have a  __JDK__  and  __Maven__  installed.


__Steps:__

1. Clone the repository or download its contents from  __GitHub__ .
2. Navigate to the ``org.iirds.dita.ot.plugin`` directory.
2. Run the following command: 
    ``maven clean install``

 