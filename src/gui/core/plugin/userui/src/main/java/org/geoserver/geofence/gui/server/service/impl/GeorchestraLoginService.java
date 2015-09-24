package org.geoserver.geofence.gui.server.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.geoserver.geofence.gui.client.ApplicationException;
import org.geoserver.geofence.gui.client.model.Authorization;
import org.geoserver.geofence.gui.client.model.UserModel;
import org.geoserver.geofence.gui.server.service.ILoginService;
import org.springframework.stereotype.Component;

@Component("loginService")
public class GeorchestraLoginService implements ILoginService {

	private static String GEOR_USERNAME_HEADER = "sec-username";
    private static String GEOR_ROLES_HEADER = "sec-roles";

    public UserModel authenticate(String userName, String password,
			HttpServletRequest request) {

        String rolesStr = request.getHeader(GEOR_ROLES_HEADER);

		// Should not happen, contains at least ROLE_ANONYMOUS
		if (rolesStr == null) {
			throw new ApplicationException("Login failed");
		}
		String[] roles = rolesStr.split(";");
		boolean isAdmin = false;
		for (String r : roles) {
			if (r.equalsIgnoreCase("ROLE_ADMINISTRATOR")) {
				isAdmin = true;
			}
		}
        if (!isAdmin) {
            throw new ApplicationException("Login failed");
        }

        UserModel user = new UserModel();

        // the security-proxy will provide the user informations
        // in the headers
        user.setName(request.getHeader(GEOR_USERNAME_HEADER));
        // no need to know, no need to leave it insecurely stored in
        // the jvm memory.
        user.setPassword(null);
        // TODO:
        // Better management using the sec-roles ?
        // i.e. need to find a way to have a mapping between geOrchestra profiles
        // and GF's authorizations
        List<Authorization> guiAuths = Arrays.asList(Authorization.values());
        user.setGrantedAuthorizations(guiAuths);
        return user;
	}

}
