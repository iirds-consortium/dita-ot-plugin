# iiRDS DITA OT Plugin

* ``org.iirds.dita.ot.plugin`` contains the maven project for the DITA OT iiRDS plugin
* ``org.iirds.dita.ot.mdext``  contains the maven project for a sample plugin which extends the functionality of the iiRDS plugin

For more information, see
* [README.md of iiRDS plugin](org.iirds.dita.ot.plugin/src/plugin/README.md)
* [README.md of sample extension plugin](org.iirds.dita.ot.mdext/src/plugin/README.md)
* https://www.iirds.org/tools/dita-plugin/

To install the iiRDS plugin via DITA OT plugin registry, run 
```dita install org.iirds.dita.package```
in your DITA OT installation

The plugins requires DITA OT version 3.7.x. It has not been tested with DITA OT 4.x.
