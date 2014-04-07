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


When GeoFence is activated, GeoServer [service security](http://docs.geoserver.org/stable/en/user/security/service.html) is still active.


How to build ?
---------------

When building geOrchestra with GeoFence, the process will build the core and the UI of geofence, **plus a customised version of geoserver**, which is able to defer rules management to the geofence module.

#### Maven Profile
You can activate the build of geofence with the maven profile *-Pgeofence*.

The geofence build is managed within geoserver profiles, which means: you can't build geofence without building geoserver !

Ex:

    ./mvn clean install -Ptemplate -P-all -Pgeofence -Pgeoserver -Pgdal -Pjp2k -Pmonitor -Pinspire -Pwps -Pcss -Ppyramid -DskipTests

will build geofence and a geoserver (**geoserver-private-template.war** and **geofence-private-template.war**).

####Build process insight
- If the geofence profile is not activated, geoserver submodule will be built as a war, then exploded and compressed again with geOrchestra-geoserver specific stuff.
- If geofence is activated, the geoserver submodule will be built, then geofence geoserver will be built by overwritting the webapp and security modules of the built geoserver. Then, geOrchestra geoserver will be reconstructed on top of the geofence geoserver target war file.


System configuration
--------------------

The geOrchestra specific configuration is stored in the [config/defaults/geofence-webapp](config/defaults/geofence-webapp) folder.

You can see files that set:
* data base access,
* ldap access,
* ldap mapping definition,

Advanced geOrchestra users may override this configuration by adding those files in their own configuration folder (config/configurations/yourown) before editing them.

Howerver, **this is not recommended**, because this is highly error-prone when upgrading geOrchestra. Instead, you should take advantage of the [GenerateConfig script](https://github.com/georchestra/template/blob/328f39e1a7ffee2c8a74dd91f3c21565856e74a3/build_support/GenerateConfig.groovy#L125) which is located in the ```build_support``` folder of your own configuration.



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

* you also have to enable geoserver REST services (see http://docs.geoserver.org/stable/en/user/security/rest.html)


Start with GeoFence
--------------------

###Access to Geofence

Your **GeoFence** instance should be reachable at http://mygeorchestra/geofence/.

As it stands behind the security-proxy, only users belonging to the ```ROLE_ADMINISTRATOR``` group may access the admin UI.

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
Recommended reading: https://github.com/geosolutions-it/geofence/wiki/General-concepts

**Important:** GeoFence rules will fully overwrite **GeoServer** layer security rules, they are not complementary. Once you've installed GeoFence, all your GeoServer rules will become obsolete.


If you need to import your existing **GeoServer** rules into **GeoFence**, you can find a simple java application that can help you in https://github.com/georchestra/geofence/tree/georchestra/src/samples/georchestra2geofence.
This sample code allow you to import 
* groups from an [LDAP CSV export file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/groups.csv)
* rules from [GeoServer layers security file](https://github.com/georchestra/geofence/blob/georchestra/src/samples/georchestra2geofence/src/test/resources/layers.properties)

Note also that there is a [proposal](https://github.com/geosolutions-it/geofence/wiki/Proposal-%233:-GeoServer-Roles-to-GeoFence-groups-mapping) about using GeoServer Roles instead of users for authorization purposes through GeoFence


#### Dynamic GeoFencing

By default, **GeoFence** allows you to restrict layer visibility on a static geometry (aka "static geofencing").
You can add this constraint into the rule definition itself (rule type is "LIMIT").

In geOrchestra, you can extend this by setting a geometry to each user in LDAP (aka "dynamic geofencing"). If a user has a geometry in his LDAP definition, then all layers can be restricted to that geometry by defining only one rule.

**VERY Important:** Note that the geometry is defined as a WKT geometry. The WKT projection has to be the same as the native projection of the layer into GeoServer, otherwise the rule won't be correctly applied.


Mapping the Geometry fields in LDAP to GeoFence

See [LDAP module advanced configuration](https://github.com/geosolutions-it/geofence/wiki/LDAP-module#advanced-configuration) for general information on how to map LDAP attributes to Geofence.

Additionally to the general attributes, we may specify custom MetaData fields. Assuming that the LDAP user objects have two attributes called 'geometry1' and 'geometry2' we may do as follows ::
```
   <bean id="geofenceLdapUserAttributesMapper"
    class="it.geosolutions.geofence.ldap.dao.impl.GSUserAttributesMapper">
 	<property name="ldapAttributeMappings">
 		<map>
 			<entry key="id" value="uidNumber"/>
  			<entry key="username" value="uid"/>			
                        <entry key="email" value="mail"/>
			<entry key="name" value="cn"/>
			<entry key="surname" value="sn"/>    			
 			<entry key="password" value="userPassword"/>  
                        <entry key="metadata.geometry1" value="geometry1"/>    			    		
                        <entry key="metadata.geometry2" value="geometry2"/>    			    			    		
 		</map>
 	 </property>
   </bean>
```
These LDAP attributes now become available in geofence.


Now, to create a rule that restricts access to an area specified in the Metadata field of each user:

1. Create an ALLOW rule for the desired service, layer,.. Do not specify a user,
2. Open the 'Layer Details' form,
3. In the 'Allowed Area Metadata Field' textbox, fill in the name of the metadata field that contains the correct geometry (not including the 'metadata.' prefix here).


