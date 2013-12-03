Geofence Module
==================

**GeoFence** handles [GeoServer](http://www.geoserver.org) authorization rules.
You can find offical documentation and sources of the project on their [github web page](https://github.com/geosolutions-it/geofence)

In few words, **GeoFence** is an Access Control List that overrides **Geoserver** security rules.
It provides new features as:
- rules by user, by roles or by group
- distinct rules on services (WMS,WFS,...)
- rules on request (getmap, getcapabilities)
- rules on a bounding box
- control feature attributes access
- rules on data CQL filters

Georchestra Integration
--------------------------
Build
You can activate the build of geofence with the maven profile -Pgeofence in the root pom.xml.
The build of geofence is managed within geoserver profile, you can't build geofence without building geoserver.

Ex: 
./mvn clean install -Ptemplate -P-all -Pgeofence -Pgeoserver -Pgdal -Pjp2k -Pmonitor -Pinspire -Pwps -Pcss -Ppyramid -DskipTests
will build geofence and a geoserver that 

Build Process
- If geofence profile is not activated
geoserver submodule will be build as a war, then exploded and compressed again with georchestra geoserver specifics stuff.
- If geofence is activated
geoserver submodule will be build, then geofence geoserver will be build overwritten webapp and security modules of the built geserver.
then georchestra geoserver will be reconstructed on top of the geofence geoserver target war file.

Configuration
--------------
Common configuration is stored in config/default folder.
You can see files that set data base access, ldap access, and some application properties.

You can override this configuration by adding those file in your own configuration folder (config/configurations).
