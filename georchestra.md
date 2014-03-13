Geofence Module
==================

**GeoFence** handles [GeoServer](http://www.geoserver.org) authorization rules.
You can find offical documentation and sources of the project on their [github web page](https://github.com/geosolutions-it/geofence).

In few words, **GeoFence** is an Access Control List that overrides **Geoserver** security rules.
It provides new features as:
- rules by users, by roles or by groups
- distinct rules on services (WMS,WFS,...)
- rules on request types (getmap, getcapabilities)
- rules on a bounding box
- controls feature attributes access
- rules on data CQL filters

geOrchestra Integration
--------------------------
###Build
**Important** : the geofence build will build the core and the ui of geofence webapp but will also build a customised geoserver that can defer rules management to geofence.

#### Maven Profile
You can activate the build of geofence with the maven profile *-Pgeofence* in the root pom.xml.
The build of geofence is managed within geoserver profile, you can't build geofence without building geoserver.

Ex:

    ./mvn clean install -Ptemplate -P-all -Pgeofence -Pgeoserver -Pgdal -Pjp2k -Pmonitor -Pinspire -Pwps -Pcss -Ppyramid -DskipTests
    
will build geofence and a geoserver (geoserver-private-template.war and geofence-private-template.war).

####Build Process
- If geofence profile is not activated
geoserver submodule will be build as a war, then exploded and compressed again with geOrchestra geoserver specifics stuff.
- If geofence is activated
geoserver submodule will be build, then geofence geoserver will be build by overwritting webapp and security modules of the built geserver. Then geOrchestra geoserver will be reconstructed on top of the geofence geoserver target war file.

Configuration
--------------
#### Official documentation
Follow official documentation to configure your geofence:
* [How to build GeoFence](https://github.com/geosolutions-it/geofence/wiki/Building-instructions)
* [How to configure GeoFence](https://github.com/geosolutions-it/geofence/wiki/WebApps-configuration)

#### geOrchestra Config
geOrchestra specific configuration is stored in config/default folder.
https://github.com/georchestra/georchestra/tree/add_geofence/config/defaults/geofence-webapp/WEB-INF/classes

You can see files that set
* data base access
* ldap access
* ldap mapping definition
* ui map configuration

You can override this configuration by adding those files in your own configuration folder (config/configurations).


#### LDAP configuration
Buy default, geofence works with LDAP activated. See offical LDAP module [https://github.com/geosolutions-it/geofence/wiki/LDAP-module](documentation).
**Important :** All your LDAP users need to have a numeric unique attribute to identify them into **GeoFence**. By default, this attribute is *employeeNumber* but can be changed in your configuration.

#### Geoserver DATA_DIR
* You have to update the security directory of your DATA_DIR in order to make your **GeoServer** able to communicate with the **GeoFence**.
Edit the file *data_dir_path*/security/auth/default/config.xml, and change the className value to refer geofence authentification provider :
  
          <className>it.geosolutions.geoserver.authentication.auth.GeofenceAuthenticationProvider</className>

* Enable REST services (see http://docs.geoserver.org/stable/en/user/security/rest.html)

Start with GeoFence
--------------------
###Access to Geofence
**GeoFence** is generally be reachable at http://mygeorchestra/geofence
**GeoFence** is behind the security-proxy and only users with role ROLE_ADMINISTRATOR can access to the admin ui.

###Web Interface
####Users and groups
Once logged in the web interface, You can see users and groups tabs, with data from your LDAP. Groups and users are on read only here. The LDAP access will be done each time to load the UI (which can be quite slow depending of the amount of users).
#### Instance
You can manage several geoserver instances within the same geofence. You have to create an instance of your geochestra geoserver in the instance tab. Buy default, geoserver instance name is *default-gs*. You need to specify a user that is ADMINISTRATOR of the geoserver when creating the instance.
#### Rules
You can specify you security rules in the rules tab.

**Important:** GeoFence rules will fully over write **GeoServer** security rules, they are not complementary. Once you've installed GeoFence, all your GeoServer rules will become obsoletes.

You would need to import your **GeoServer** rules into **GeoFence**. You can find a simple java application that can help you in this task in https://github.com/georchestra/geofence/tree/georchestra/src/samples/georchestra2geofence.
This sample code allow you to import 
* groups from an [LDAP CSV export file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/groups.csv)
* rules from [GeoServer layers security file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/layers.properties)

Note also that there is a [proposal](https://github.com/geosolutions-it/geofence/wiki/Proposal-%233:-GeoServer-Roles-to-GeoFence-groups-mapping) about using GeoServer Roles instead of users for authorization purposes through GeoFence

#### LDAP geometry rule
By default, **GeoFence** allow you to restrict a layer visibility on a geometry, you can add this constraint into the rule definition itself.

In geOrchestra, you can extend this by setting a geometry to each user in LDAP. If a user has a geometry in his LDAP definition, then all layers can be restricted to that geometry by defining only one rule.
This is explained here : [Documentation](https://github.com/NielsCharlier/geofence/wiki/Storing-Geometries-in-LDAP)

**Important:** Note that the geometry is defined as a WKT geometry. The projection has to be the same as the native projection of the layer into GeoServer, otherwise the rule won't be correctly applied.
