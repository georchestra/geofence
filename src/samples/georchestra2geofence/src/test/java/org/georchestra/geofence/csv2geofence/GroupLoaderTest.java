/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 * 
 *  GPLv3 + Classpath exception
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.georchestra.geofence.csv2geofence;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import org.georchestra.geofence.csv2geofence.config.model.internal.GroupOp;
import org.georchestra.geofence.csv2geofence.impl.GroupFileLoader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fgravin
 */
public class GroupLoaderTest extends BaseTest {

    public GroupLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of load method, of class UserFileLoader.
     */
    @Test
    public void testLoad() throws Exception {
        System.out.println("load");
        Properties prop = new Properties();
        File configFile = loadFile("config.properties");
        prop.load(new FileInputStream(configFile));

        GroupFileLoader instance = new GroupFileLoader();
        File groupFile = loadFile("groups.csv");
        List<GroupOp> groupOps = instance.load(groupFile, prop);
        
        assertEquals(7, groupOps.size());
    }


}
