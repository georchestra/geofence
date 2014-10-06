GeoFence Module
==================

In geOrchestra, **GeoFence** is an optional module, which handles data security by **overriding** GeoServer's own data security ACL.

Compared to GeoServer's own [data security](http://docs.geoserver.org/stable/en/user/security/layer.html), it provides interesting new features, such as:
- distinct rules on services (WMS, WFS, WCS, ...),
- distinct rules on request types (GetMap, GetFeature, GetCapabilities, ...),
- feature attributes filtering,
- rules on data CQL filters,
- static geofencing, ie access is restricted to a group of users in a given area,  
- dynamic geofencing, based on a geometry stored in the user's LDAP record.


How to build ?
---------------

When building geOrchestra with GeoFence, the process will build the core and the UI of geofence, **plus a customised version of geoserver**, which is able to defer rules management to the geofence module.

#### Maven Profile

The geofence build is managed within geoserver profiles, which means: you can't build geofence without building geoserver !

To build GeoFence, you have to use the maven profile *-Pgeofence*, when building geoserver:

    ./mvn clean install -Ptemplate -P-all -Pgeoserver -Pgeofence -DskipTests

will produce two artifacts **geoserver-private-template.war** and **geofence-private-template.war**.

####Build process insight
- If the geofence profile is not activated, geoserver submodule will be built as a war, then exploded and compressed again with geOrchestra-geoserver specific stuff.
- If geofence is activated, the geoserver submodule will be built, then geofence geoserver will be built by overwritting the webapp and security modules of the built geoserver. Then, geOrchestra geoserver will be reconstructed on top of the geofence geoserver target war file.


System configuration
--------------------

The geOrchestra specific configuration is stored in the [config/defaults/geofence-webapp](https://github.com/georchestra/georchestra/tree/master/config/defaults/geofence-webapp) folder.

You can see files that set:
* database access,
* ldap access,
* ldap mapping definition,

Advanced geOrchestra users may override this configuration by adding those files in their own configuration folder (config/configurations/yourown) before editing them.

Howerver, **this is not recommended**, because it is highly error-prone when upgrading geOrchestra. Instead, you should take advantage of the [GenerateConfig script](https://github.com/georchestra/template/blob/328f39e1a7ffee2c8a74dd91f3c21565856e74a3/build_support/GenerateConfig.groovy#L125) which is located in the ```build_support``` folder of your own configuration.



#### LDAP configuration

By default, GeoFence accesses your LDAP tree in order to fetch the users and groups in a local database.

**VERY Important :** all your LDAP users and groups **MUST** have a unique numeric identifier. By default, this attribute is (resp.)  **employeeNumber** and **ou**, but this can be changed in your configuration.

If you stick to the [provided LDAP files](https://github.com/georchestra/LDAP/blob/master/georchestra.ldif) and use the LDAPadmin module to administer your users and groups, you should not have any problem. 

If you use your own LDAP tree, or a custom application to manage your LDAP, please keep in mind the above rule.

For more information, please refer to the original GeoFence LDAP module [https://github.com/geosolutions-it/geofence/wiki/LDAP-module](documentation).

#### GeoServer DATA_DIR

Either you're using the provided [geoserver minimal data dir](https://github.com/georchestra/geoserver_minimal_datadir) (**branch geofence**), and you should be fine.

**Or**:

* it is required that you update the *data_dir_path*/security/auth/default/config.xml file in your geoserver DATA_DIR in order to make your **GeoServer** able to communicate with the **GeoFence**.
Namely, you have to change the className value, in order to refer to the geofence authentification provider :
  
          <className>it.geosolutions.geoserver.authentication.auth.GeofenceAuthenticationProvider</className>

Here's the diff: [georchestra/geoserver_minimal_datadir@2834a7](https://github.com/georchestra/geoserver_minimal_datadir/commit/2834a7579ec429686d544ec50da14f9abadf2612)

* you also have to enable geoserver REST services (see http://docs.geoserver.org/stable/en/user/security/rest.html)


Start with GeoFence
--------------------

###Access to Geofence

Your **GeoFence** instance should be reachable at http://mygeorchestra/geofence/.

As it stands behind the security-proxy, only users belonging to the ```ADMINISTRATOR``` group may access the admin UI.

###Web Interface

Once logged in the web interface, one can see a map and two tabs (instances and rules). 

Let's focus on the TabPanel first.

#### Instances

You can manage **several GeoServer instances** within the same GeoFence. 

If none is already setup, you have to create an instance for your own geOrchestra GeoServer in the instance tab. 

By default, GeoFence will look for an instance called *default-gs*, so it is **highly recommended** that you name your instance as such !

You will also have to specify a user that is ADMINISTRATOR of the geoserver (eg: your ```shared.privileged.geoserver.user```, which is "geoserver_privileged_user" by default, is the best candidate). 

When your geoserver instance is referenced is the geofence UI, it is able to fetch the workspaces and layers information from the geoserver catalog, using its REST API.


#### Rules

Here is the place where you can specify your own security rules.

We **highly recommend** that you read the basics of rules' creation now: https://github.com/geosolutions-it/geofence/wiki/General-concepts#rules

Let's state it again: GeoFence rules will fully overwrite **GeoServer** layer security rules, as they are not complementary. Once you've installed GeoFence, all your GeoServer rules will become obsolete.


If you need to import your existing **GeoServer** rules into **GeoFence**, you can rely on [georchestra2geofence](https://github.com/georchestra/geofence/tree/georchestra/src/samples/georchestra2geofence), a simple java application, which will import:
* groups from an [LDAP CSV export file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/groups.csv)
* rules from [GeoServer layers security file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/layers.properties)

Note also that there is a [proposal](https://github.com/geosolutions-it/geofence/wiki/Proposal-%233:-GeoServer-Roles-to-GeoFence-groups-mapping) about using GeoServer Roles instead of users for authorization purposes through GeoFence


#### Testing the rules

If you're testing the rules, please keep in mind that:
 * members of the ```ADMINISTRATOR``` LDAP group are almighty: they are granted access to all layers with no restrictions,
 * rules are applied with a small delay (approx 30 seconds),
 * your browser, and possibly proxies may be agressively caching requests.


#### Static / Dynamic GeoFencing

**GeoFence** allows you to restrict layer visibility on a static geometry (aka "static geofencing": the geometry is stored in the LIMITing rule) or on a dynamic geometry (aka "dynamic geofencing": the geometry is stored in the LDAP user record).


In both cases, the filtering geometry is stored as an EWKT (Extended Well-Known Text, eg: ```SRID=4326;POINT(-44.3 60.1)```). **The EWKT projection has to be the same as the native projection of the filtered layer(s) into GeoServer**. Otherwise the rule won't be correctly applied.


To setup Static GeoFencing: 
 * create a rule whose type is LIMIT, 
 * click on "layer details", 
 * then:
   * either paste an EWKT string in the "Allowed area" text box,
   * or click the "draw area" button, draw your shape, finish with a double click,
 * save the rule,
 * dont' forget that there must be an ALLOWing rule whose priority is inferior to the LIMITing rule.


To setup Dynamic GeoFencing:
 * check that your users in LDAP have an ```l``` field set to an EWKT string,
 * in geofence, create a rule for one layer whose type is ALLOW,
 * click on "layer details", 
 * in the "Allowed Area MetaData Field" enter the string "geometry" (without the quotes). No black magic here, just have a look at [this](https://github.com/georchestra/georchestra/blob/master/config/defaults/geofence-webapp/WEB-INF/classes/geofence-ldap.properties#L13) and also [this](https://github.com/georchestra/geofence/blob/6f9cb02852f0b875e0bcc8ce5d6e3cdf96d04256/src/gui/web/src/main/resources/applicationContext-ldap.xml#L70).
 * save the rule

Please keep in mind that users without an ```l``` field **will be granted access to the whole layer**, as the rule type is ALLOW. We would recommend adding an empty geometry to all the users to explicitly specify they have no access.


Caveats
--------

When GeoFence is activated:
 - the Catalog Mode is always set to HIDE for secured workspaces / layers,
 - GeoServer [service security](http://docs.geoserver.org/stable/en/user/security/service.html) is still active,
 - you cannot delegate workspace administration anymore,
 - an additional stylesheet is applied to GeoServer UI, which breaks some dropdowns in the embedded GeoWebCache config,
 - nested layergroups should be avoided, as they are known to break the capabilities, see [#746](https://github.com/georchestra/georchestra/issues/746) for more information,
 - more important: layergroups cannot be modified once created, see [#748](https://github.com/georchestra/georchestra/issues/748).


