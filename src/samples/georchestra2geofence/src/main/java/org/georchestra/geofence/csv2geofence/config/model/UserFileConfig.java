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
package org.georchestra.geofence.csv2geofence.config.model;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class UserFileConfig {

    private Character fieldSeparator = ',';
    private Character stringSeparator = '"';

    private int userNameIndex;
    private String validUsernameRegEx;

    private Integer userFullNameIndex;
    private Integer userMailIndex;

    private int groupNameIndex;
    private String validGroupRegEx;

    private int operationTypeIndex;

    private boolean hasHeaders = true;

    /**
     * Separator between fields in a CSV file. Usually it's "'"
     */
    public Character getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(Character fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * Separator between groups.
     */
    public Character getStringSeparator() {
        return stringSeparator;
    }

    public void setStringSeparator(Character stringSeparator) {
        this.stringSeparator = stringSeparator;
    }


    /**
     * Regular expression that filters valid user.
     * Not matching users will be ignored.
     */
    public String getValidUsernameRegEx() {
        return validUsernameRegEx;
    }

    public void setValidUsernameRegEx(String validUsernameRegEx) {
        this.validUsernameRegEx = validUsernameRegEx;
    }

    /**
     * Regular expression that filters valid groups.
     * Not matching groups will be ignored.
     */
    public String getValidGroupRegEx() {
        return validGroupRegEx;
    }

    public void setValidGroupRegEx(String validGroupRegEx) {
        this.validGroupRegEx = validGroupRegEx;
    }

    /**
     * 1-based index of the CSV column containing the username.
     */
    public int getUserNameIndex() {
        return userNameIndex;
    }

    public void setUserNameIndex(int userNameIndex) {
        this.userNameIndex = userNameIndex;
    }

    /**
     * 1-based index of the CSV column containing the group list.
     */
    public int getGroupNameIndex() {
        return groupNameIndex;
    }

    public void setGroupNameIndex(int groupNameIndex) {
        this.groupNameIndex = groupNameIndex;
    }

    /**
     * Tells if the first CSV line contains headers and should be skipped.
     */
    public boolean isHasHeaders() {
        return hasHeaders;
    }

    public void setHasHeaders(boolean hasHeaders) {
        this.hasHeaders = hasHeaders;
    }

    /**
     * 1-based index of the CSV column containing the operation to be performed.
     */
    public int getOperationTypeIndex() {
        return operationTypeIndex;
    }

    public void setOperationTypeIndex(int operationTypeIndex) {
        this.operationTypeIndex = operationTypeIndex;
    }

    /**
     * 1-based index of the CSV column containing the user full name.
     */
    public Integer getUserFullNameIndex() {
        return userFullNameIndex;
    }

    public void setUserFullNameIndex(Integer userFullNameIndex) {
        this.userFullNameIndex = userFullNameIndex;
    }

    /**
     * 1-based index of the CSV column containing the user mail address.
     */
    public Integer getUserMailIndex() {
        return userMailIndex;
    }

    public void setUserMailIndex(Integer userMailIndex) {
        this.userMailIndex = userMailIndex;
    }


}
