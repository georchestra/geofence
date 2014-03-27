/*
 *  Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
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
package it.geosolutions.geofence.services;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Lists;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;

import it.geosolutions.geofence.services.dto.AccessInfo;
import it.geosolutions.geofence.services.dto.RuleFilter.IdNameFilter;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import it.geosolutions.geofence.core.dao.GSUserDAO;
import it.geosolutions.geofence.core.dao.LayerDetailsDAO;
import it.geosolutions.geofence.core.dao.UserGroupDAO;
import it.geosolutions.geofence.core.dao.RuleDAO;
import it.geosolutions.geofence.core.model.GSUser;
import it.geosolutions.geofence.core.model.LayerAttribute;
import it.geosolutions.geofence.core.model.LayerDetails;
import it.geosolutions.geofence.core.model.UserGroup;
import it.geosolutions.geofence.core.model.Rule;
import it.geosolutions.geofence.core.model.RuleLimits;
import it.geosolutions.geofence.core.model.enums.AccessType;
import it.geosolutions.geofence.core.model.enums.GrantType;
import it.geosolutions.geofence.services.dto.AuthUser;
import it.geosolutions.geofence.services.dto.RuleFilter;
import it.geosolutions.geofence.services.dto.RuleFilter.FilterType;
import it.geosolutions.geofence.services.dto.RuleFilter.NameFilter;
import it.geosolutions.geofence.services.dto.RuleFilter.SpecialFilterType;
import it.geosolutions.geofence.services.dto.ShortRule;
import it.geosolutions.geofence.services.exception.BadRequestServiceEx;
import it.geosolutions.geofence.services.util.AccessInfoInternal;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

/**
 *
 * <P>
 * <B>Note:</B> <TT>service</TT> and <TT>request</TT> params are usually set by
 * the client, and by OGC specs they are not case sensitive, so we're going to
 * turn all of them uppercase. See also {@link RuleAdminServiceImpl}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RuleReaderServiceImpl implements RuleReaderService {

    private final static Logger LOGGER = LogManager.getLogger(RuleReaderServiceImpl.class);

    private RuleDAO ruleDAO;
    private LayerDetailsDAO detailsDAO;
    private GSUserDAO userDAO;
    private UserGroupDAO userGroupDAO;
    private RuleCache ruleCache;

    @Autowired
    private MetricRegistry metricRegistry;
    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public List<ShortRule> getMatchingRules(
                    String userName, String profileName, String instanceName,
                    String service, String request,
                    String workspace, String layer) {

        return getMatchingRules(new RuleFilter(userName, profileName, instanceName, service, request, workspace, layer));
    }

    /**
     * <B>TODO: REFACTOR</B>
     *
     * @param filter
     * @return a plain List of the grouped matching Rules.
     */
    @Override
    public List<ShortRule> getMatchingRules(RuleFilter filter) {
        Map<UserGroup, List<Rule>> found = getRules(filter);

        Map<Long, Rule> sorted = new TreeMap<Long, Rule>();
        for (List<Rule> list : found.values()) {
            for (Rule rule : list) {
                sorted.put(rule.getId(), rule);
            }
        }

        List<Rule> plainList = new ArrayList<Rule>();
        for (Rule rule : sorted.values()) {
            plainList.add(rule);
        }

        return convertToShortList(plainList);
    }


    /**
     * @deprecated
     */
    @Override
    @Deprecated
    public AccessInfo getAccessInfo(String userName, String profileName, String instanceName, String service, String request, String workspace, String layer) {
        return getAccessInfo(new RuleFilter(userName, profileName, instanceName, service, request, workspace, layer));
    }

    @Override
    public AccessInfo getAccessInfo(RuleFilter filter) {
        LOGGER.info("Requesting access for " + filter);
        Map<UserGroup, List<Rule>> groupedRules = getRules(filter);

        AccessInfoInternal currAccessInfo = null;
        
        for (Entry<UserGroup, List<Rule>> ruleGroup : groupedRules.entrySet()) {
            UserGroup userGroup = ruleGroup.getKey();
            List<Rule> rules = ruleGroup.getValue();

            AccessInfoInternal accessInfo = resolveRuleset(rules, filter.getUser());
            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Filter " + filter + " on group " + userGroup + " has access " + accessInfo);
            }

            currAccessInfo = enlargeAccessInfo(currAccessInfo, accessInfo);
        }

        AccessInfo ret;

        if(currAccessInfo == null) {
            LOGGER.warn("No access for filter " + filter);
            // Denying by default
            ret = new AccessInfo(GrantType.DENY);
        } else {
            ret = currAccessInfo.toAccessInfo();
        }

        LOGGER.info("Returning " + ret + " for " + filter);
        return ret;
    }

    private AccessInfoInternal enlargeAccessInfo(AccessInfoInternal baseAccess, AccessInfoInternal moreAccess) {
        if(baseAccess == null) {
            if(moreAccess == null)
                return null;
            else if(moreAccess.getGrant() == GrantType.ALLOW)
                return moreAccess;            
            else
                return null;
        } else {
            if(moreAccess == null)
                return baseAccess;
            else if(moreAccess.getGrant() == GrantType.DENY)
                return baseAccess;
            else {
                // ok: extending grants
                AccessInfoInternal ret = new AccessInfoInternal(GrantType.ALLOW);

                ret.setCqlFilterRead(unionCQL(baseAccess.getCqlFilterRead(), moreAccess.getCqlFilterRead()));
                ret.setCqlFilterWrite(unionCQL(baseAccess.getCqlFilterWrite(), moreAccess.getCqlFilterWrite()));

                if(baseAccess.getDefaultStyle() == null || moreAccess.getDefaultStyle()==null)
                    ret.setDefaultStyle(null);
                else
                    ret.setDefaultStyle(baseAccess.getDefaultStyle()); // just pick one

                ret.setAllowedStyles(unionAllowedStyles(baseAccess.getAllowedStyles(), moreAccess.getAllowedStyles()));
                ret.setAttributes(unionAttributes(baseAccess.getAttributes(), moreAccess.getAttributes()));
                ret.setArea(unionGeometry(baseAccess.getArea(), moreAccess.getArea()));

                return ret;
            }
        }        
    }

    private String unionCQL(String c1, String c2) {
          if(c1 == null || c2 == null)
              return null;

          return "("+c1+") OR ("+c2+")";
    }

    private Geometry unionGeometry(Geometry g1, Geometry g2) {
          if(g1 == null || g2 == null)
              return null;

          return union(g1, g2);
    }

    private static Set<LayerAttribute> unionAttributes(Set<LayerAttribute> a0, Set<LayerAttribute> a1) {
        // TODO: check how geoserver deals with empty set

        if(a0 == null || a0.isEmpty())
            return Collections.EMPTY_SET;
//            return a1;
        if(a1==null || a1.isEmpty())
            return Collections.EMPTY_SET;
//            return a0;

        Set<LayerAttribute> ret = new HashSet<LayerAttribute>();
        // add both attributes only in a0, and enlarge common attributes
        for (LayerAttribute attr0 : a0) {
            LayerAttribute attr1 = getAttribute(attr0.getName(), a1);
            if(attr1 == null) {
                ret.add(attr0.clone());
            } else {
                LayerAttribute attr = attr0.clone();
                if(attr0.getAccess()==AccessType.READWRITE || attr1.getAccess()==AccessType.READWRITE)
                    attr.setAccess(AccessType.READWRITE);
                else if(attr0.getAccess()==AccessType.READONLY || attr1.getAccess()==AccessType.READONLY)
                    attr.setAccess(AccessType.READONLY);
                ret.add(attr);
            }
        }
        // now add attributes that are only in a1
        for (LayerAttribute attr1 : a1) {
            LayerAttribute attr0 = getAttribute(attr1.getName(), a0);
            if(attr0 == null) {
                ret.add(attr1.clone());

            }
        }

        return ret;
    }


    private static LayerAttribute getAttribute(String name, Set<LayerAttribute> set) {
        for (LayerAttribute layerAttribute : set) {
            if(layerAttribute.getName().equals(name) )
                return layerAttribute;
        }
        return null;
    }

    private static Set<String> unionAllowedStyles(Set<String> a0, Set<String> a1) {

        // if at least one of the two set is empty, the result will be an empty set,
        // that means styles are not restricted
        if(a0 == null || a0.isEmpty())
            return Collections.EMPTY_SET;

        if(a1==null || a1.isEmpty())
            return Collections.EMPTY_SET;

        Set<String> allowedStyles = new HashSet<String>();
        allowedStyles.addAll(a0);
        allowedStyles.addAll(a1);
        return allowedStyles;
    }

    private AccessInfoInternal resolveRuleset(List<Rule> ruleList, IdNameFilter user) {

        List<RuleLimits> limits = new ArrayList<RuleLimits>();
        AccessInfoInternal ret = null;

        for (Rule rule : ruleList) {
            if(ret != null)
                break;

            switch(rule.getAccess()) {
                case LIMIT:

                   RuleLimits rl = rule.getRuleLimits();
                   if(rl != null) {
                       LOGGER.info("Collecting limits: " + rl);
                       limits.add(rl);
                    } else
                       LOGGER.warn(rule + " has no associated limits");
                    break;

                case DENY:
                    ret = new AccessInfoInternal(GrantType.DENY);
                    break;

                case ALLOW:
                    ret = buildAllowAccessInfo(rule, limits, user); 
                    break;

                default:
                    throw new IllegalStateException("Unknown GrantType " + rule.getAccess());
            }
        }

//        if(ret == null) {
//            LOGGER.warn("No rule matching filter " + filter);
//            // Denying by default
//            ret = new AccessInfo(GrantType.DENY);
//        }

//        LOGGER.info("Returning " + ret + " for " + filter);
        return ret;
    }

//    private Geometry getUserArea(IdNameFilter userFilter) {
//        GSUser user = null;
//
//        if(userFilter.getType() == RuleFilter.FilterType.IDVALUE) {
//            user = userDAO.find(userFilter.getId());
//        } else if(userFilter.getType() == RuleFilter.FilterType.NAMEVALUE) {
//            user = getUserByName(userFilter.getName());
//        }
//
//        return user == null ? null :  user.getAllowedArea();
//    }

    private GSUser getUserByName(String userName) {
        Search search = new Search(GSUser.class);
        search.addFilterEqual("name", userName);
        List<GSUser> users = userDAO.search(search);
        if(users.size() > 1)
            throw new IllegalStateException("Found more than one user with name '"+userName+"'");

        return users.isEmpty() ? null : users.get(0);
    }

    private GSUser getFullUser(IdNameFilter filter) {
        Timer.Context timer = this.metricRegistry.timer(getClass().getName() + "_getFullUser(IdNameFilter)").time();
        try {
            switch(filter.getType()) {
                case IDVALUE:
                    return userDAO.getFull(filter.getId());
                case NAMEVALUE:
                    return userDAO.getFull(filter.getName());
                case DEFAULT:
                case ANY:
                    return null;
                default:
                    throw new IllegalStateException("Unknown filter type '"+filter+"'");
            }
        } finally {
            timer.stop();
        }
    }

    private UserGroup getUserGroup(IdNameFilter filter) {
        Timer.Context timer = this.metricRegistry.timer(getClass().getName() + "_getUserGroup(IdNameFilter)").time();
        try {
            Search search = new Search(UserGroup.class);

            switch(filter.getType()) {
                case IDVALUE:
                    search.addFilterEqual("id", filter.getId());
                    break;
                case NAMEVALUE:
                    search.addFilterEqual("name", filter.getName());
                    break;
                default:
                    return null;
            }

            List<UserGroup> groups = userGroupDAO.search(search);
            if(groups.size() > 1)
                throw new IllegalStateException("Found more than one userGroup '"+filter+"'");

            return groups.isEmpty() ? null : groups.get(0);
        } finally {
            timer.stop();
        }
    }



    private AccessInfoInternal buildAllowAccessInfo(Rule rule, List<RuleLimits> limits, IdNameFilter userFilter) {
        AccessInfoInternal accessInfo = new AccessInfoInternal(GrantType.ALLOW);

        Geometry area = intersect(limits);

//        Geometry userArea = getUserArea(userFilter);
//        area = intersect(area, userArea);

        LayerDetails details = rule.getLayerDetails();
        if(details != null ) {
            area = intersect(area, details.getArea());
            
            String metadataField = details.getAreaMetadataField();
            if (metadataField != null && !"".equals(metadataField)) {
            	
            	GSUser user = getFullUser(userFilter);
            	String str = (String) user.getMetadata().get(metadataField);
            	            	
                if (str != null) {
                	String wkt, srid;
                    if (str.indexOf("SRID=") != -1) {
                        String[] allowedAreaArray = str.split(";");

                        srid = allowedAreaArray[0].split("=")[1];
                        wkt = allowedAreaArray[1];
                    }
                    else {
                        srid = "4326";
                        wkt = str;
                    }
                    
                    MultiPolygon the_geom = null;
    				WKTReader wktReader = new WKTReader();
    			
					try {
    					Geometry geometry;
							geometry = wktReader.read(wkt);
						    					
    					if (geometry instanceof MultiPolygon) {
        					the_geom = (MultiPolygon) geometry;
        				} else if (geometry instanceof Polygon) {
        					GeometryFactory factory = new GeometryFactory();
        					the_geom = new MultiPolygon(
        							new Polygon[] { (Polygon) geometry }, factory);
        				}

        				if (the_geom != null) {
        					the_geom.setSRID(Integer.valueOf(srid).intValue());
        					area = intersect(area, the_geom);
        					if (LOGGER.isDebugEnabled()) {
        		                LOGGER.debug("Extracted from " + user.getName() + " geometry: " + the_geom.toString());
        		            }
        				}
					} catch (ParseException e) {
						LOGGER.error(e);
					}
    				
                }
            	
            	
            }

            accessInfo.setAttributes(details.getAttributes());
            accessInfo.setCqlFilterRead(details.getCqlFilterRead());
            accessInfo.setCqlFilterWrite(details.getCqlFilterWrite());
            accessInfo.setDefaultStyle(details.getDefaultStyle());
            accessInfo.setAllowedStyles(details.getAllowedStyles());
        }


        //        accessInfo.setArea(area);
        if (area != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Attaching an area to Accessinfo: " + area.getClass().getName() + " " + area.toString());
            }
//            accessInfo.setAreaWkt(area.toText());
            accessInfo.setArea(area);
        }

        return accessInfo;
    }

    private Geometry intersect(List<RuleLimits> limits) {
        Geometry g = null;
        for (RuleLimits limit : limits) {
            Geometry area = limit.getAllowedArea();
            if(area != null) {
                if( g == null) {
                    g = area;
                } else {
                    g = g.intersection(area);
                }
            }
        }
        return g;
    }

    private Geometry intersect(Geometry g1, Geometry g2) {
        if(g1!=null) {
            if(g2==null)
                return g1;
            else
                return g1.intersection(g2);
        } else {
            return g2;
        }
    }

    private Geometry union(Geometry g1, Geometry g2) {
        if(g1!=null) {
            if(g2==null)
                return g1;
            else
                return g1.union(g2);
        } else {
            return g2;
        }
    }

    //==========================================================================

//    protected List<Rule> getRules(String userName, String profileName, String instanceName, String service, String request, String workspace, String layer) throws BadRequestServiceEx {
//        Search searchCriteria = new Search(Rule.class);
//        searchCriteria.addSortAsc("priority");
//
//        addCriteria(searchCriteria, userName, "gsuser");
//        addCriteria(searchCriteria, profileName, "profile");
//        addCriteria(searchCriteria, instanceName, "instance");
//
//        addStringMatchCriteria(searchCriteria, service==null?null:service.toUpperCase(), "service"); // see class' javadoc
//        addStringMatchCriteria(searchCriteria, request==null?null:request.toUpperCase(), "request"); // see class' javadoc
//        addStringMatchCriteria(searchCriteria, workspace, "workspace");
//        addStringMatchCriteria(searchCriteria, layer, "layer");
//
//        List<Rule> found = ruleDAO.search(searchCriteria);
//        return found;
//    }

    /**
     *
     * @return a Map having UserGroups as keys, and the list of matching Rules as values. The NULL key holds the rules for the DEFAULT group.
     */
    protected Map<UserGroup, List<Rule>> getRules(RuleFilter filter) throws BadRequestServiceEx {
        Timer.Context timer = this.metricRegistry.timer(getClass().getName() + "_getRules(RuleFilter)").time();
        try {
            // user can be null if
            // 1) id or name are defined in the filter, but the user has not been found in the db
            // 2) the user filter asks for ANY or DEFAULT
            GSUser filterUser = getFullUser(filter.getUser());

            // group can be null if
            // 1) id or name are defined in the filter, but the group has not been found in the db
            // 2) the group filter asks for ANY or DEFAULT
            UserGroup filterGroup = getUserGroup(filter.getUserGroup());


            Set<UserGroup> finalGroupFilter = new HashSet<UserGroup>();

            // If both user and group are defined in filter
            //   if user doensn't belong to group, no rule is returned
            //   otherwise assigned or default rules are searched for
            if(filterUser != null) {
                Set<UserGroup> assignedGroups = filterUser.getGroups();
                if(filterGroup != null) {
                    if( assignedGroups.contains(filterGroup)) {
    //                    IdNameFilter f = new IdNameFilter(filterGroup.getId());
                        finalGroupFilter = Collections.singleton(filterGroup);
                    } else {
                        LOGGER.warn("User does not belong to user group [FUser:"+filter.getUser()+"] [FGroup:"+filterGroup+"] [Grps:"+assignedGroups+"]");
                        return Collections.EMPTY_MAP; // shortcut here, in rder to avoid loading the rules
                    }
                } else {
                    // User set and found, group (ANY, DEFAULT or notfound):

                    if(filter.getUserGroup().getType() == FilterType.ANY) {
                        if( ! filterUser.getGroups().isEmpty()) {
                            finalGroupFilter = filterUser.getGroups();
                        } else {
                            filter.setUserGroup(SpecialFilterType.DEFAULT);
                        }
                    } else {
                        // group is DEFAULT or not found:
                        // no grouping, use requested filtering
                    }
                }
            } else {
                // user is null: then either:
                //  1) no filter on user was requested (ANY or DEFAULT)
                //  2) user has not been found
                if(filterGroup != null) {
                    finalGroupFilter.add(filterGroup);
                } else {
                    // group is ANY, DEFAULT or not found:
                    // no grouping, use requested filtering
                }
            }

            Map<UserGroup, List<Rule>> ret = new HashMap<UserGroup, List<Rule>>();

            if(finalGroupFilter.isEmpty()) {
                List<Rule> found = getRuleAux(filter, filter.getUserGroup());
                ret.put(null, found);
            } else {
                for (UserGroup userGroup : finalGroupFilter) {
                    IdNameFilter groupFilter = new IdNameFilter(userGroup.getId());
                    groupFilter.setIncludeDefault(true);
                    List<Rule> found = getRuleAux(filter, groupFilter);
                    ret.put(userGroup, found);
                }
            }

            if(LOGGER.isDebugEnabled()) {
                LOGGER.debug("Filter " + filter + " is matching the following Rules:");
                boolean ruleFound = false;
                for (Entry<UserGroup, List<Rule>> entry : ret.entrySet()) {
                    UserGroup ug = entry.getKey();
                    LOGGER.debug("    Group:"+ ug );
                    for (Rule rule : entry.getValue()) {
                        LOGGER.debug("    Group:"+ ug + " ---> " + rule);
                        ruleFound = true;
                    }
                }
                if( ! ruleFound)
                    LOGGER.debug("No rules matching filter " + filter);

            }

            return ret;
        } finally {
            timer.stop();
        }
    }

    protected List<Rule> getRuleAux(RuleFilter filter, IdNameFilter groupFilter) {
        Timer.Context timer = this.metricRegistry.timer(getClass().getName() + "_getRuleAux(RuleFilter)").time();
        try {
            TreeSet<Rule> results = new TreeSet<Rule>(PRIORITY_COMPARATOR);
            List<Rule> all = ruleCache.findAll();
            for (Rule rule : all) {
                boolean matchUser = rule.getGsuser() == null ||
                                   rule.getGsuser().getId().longValue() == filter.getUser().getId().longValue();
                boolean matchGroup = rule.getUserGroup() == null;
                if (groupFilter.getType() != FilterType.ANY) {
                    matchGroup |= rule.getUserGroup() != null && (
                            rule.getUserGroup().getId().longValue() == groupFilter.getId().longValue() ||
                            (rule.getUserGroup().getExtId() != null && rule.getUserGroup().getExtId().equals("-" + groupFilter.getId())));
                }
                boolean matchInstance = rule.getInstance() == null ||
                                       rule.getInstance().getName().equals(filter.getInstance().getName());
                boolean matchRequest = rule.getRequest() == null ||
                                      rule.getRequest().equals(filter.getRequest().getName());
                boolean matchWorkspace = rule.getWorkspace() == null ||
                                         rule.getWorkspace().equals(filter.getWorkspace().getName());
                boolean matchService = rule.getService() == null ||
                                         rule.getService().equals(filter.getService().getName());
                boolean matchLayer = rule.getLayer() == null || rule.getLayer().equals(filter.getLayer().getName());

                if (matchService && matchUser && matchGroup && matchInstance && matchRequest && matchWorkspace && matchLayer) {
                    results.add(rule);
                }
            }
            return Lists.newArrayList(results);
        } finally {
            timer.stop();
        }
    }
    private static final Comparator<Rule> PRIORITY_COMPARATOR = new Comparator<Rule>() {
        @Override
        public int compare(Rule o1, Rule o2) {
            long priority1 = o1.getPriority();
            long priority2 = o2.getPriority();
            return (priority1 < priority2) ? -1 : ((priority1 == priority2) ? 0 : 1);
        }
    };

    private void addCriteria(Search searchCriteria, String fieldName, IdNameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case IDVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName + ".id", filter.getId()));
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName + ".name", filter.getName()));
                break;

            default:
                throw new AssertionError();
        }
    }

    private void addStringCriteria(Search searchCriteria, String fieldName, NameFilter filter) {
        switch (filter.getType()) {
            case ANY:
                break; // no filtering

            case DEFAULT:
                searchCriteria.addFilterNull(fieldName);
                break;

            case NAMEVALUE:
                searchCriteria.addFilterOr(
                        Filter.isNull(fieldName),
                        Filter.equal(fieldName, filter.getName()));
                break;

            case IDVALUE:
            default:
                throw new AssertionError();
        }
    }

//    /**
//     * Add criteria for <B>matching</B> names:
//     * <UL>
//     * <LI><STRIKE>null names will not be accepted: that is: user, profile, instance are required (note you can trick this check by setting empty strings)</STRIKE>a null param will match everything</LI>
//     * <LI>a valid string will match that specific value and any rules with that name set to null</LI>
//     * </UL>
//     * We're dealing with <TT><I>name</I></TT>s here, so <U>we'll suppose that the related object's name field is called "<TT>name</TT>"</U>.
//     */
//    protected void addCriteria(Search searchCriteria, String name, String fieldName) throws BadRequestServiceEx {
//        if (name == null)
//            return; // TODO: check desired behaviour
////            throw new BadRequestServiceEx(fieldName + " is null");
//
//        searchCriteria.addFilterOr(
//                Filter.isNull(fieldName),
//                Filter.equal(fieldName + ".name", name));
//    }
//
//    /**
//     * Add criteria for <B>matching</B>:
//     * <UL>
//     * <LI>null values will not add a constraint criteria</LI>
//     * <LI>any string will match that specific value and any rules with that field set to null</LI>
//     * </UL>
//     */
//    protected void addStringMatchCriteria(Search searchCriteria, String value, String fieldName) throws BadRequestServiceEx {
//        if(value != null) {
//            searchCriteria.addFilterOr(
//                    Filter.isNull(fieldName),
//                    Filter.equal(fieldName, value));
//        }
//    }

    // ==========================================================================

    @Override
    public AuthUser authorize(String username, String password) {
        Timer.Context timer = this.metricRegistry.timer(getClass().getName() + "_authorize(String)").time();
        try {
            GSUser user = getUserByName(username);
            if(user == null) {
                LOGGER.debug("User not found " + username);
                return null;
            }
            if(! user.getPassword().equals(password)) {
                LOGGER.debug("Bad pw for user " + username);
                return null;
            }

            return new AuthUser(username, user.isAdmin() ? AuthUser.Role.ADMIN : AuthUser.Role.USER);
        } finally {
            timer.stop();
        }
    }

    // ==========================================================================

    private List<ShortRule> convertToShortList(List<Rule> list) {
        List<ShortRule> shortList = new ArrayList<ShortRule>(list.size());
        for (Rule rule : list) {
            shortList.add(new ShortRule(rule));
        }

        return shortList;
    }

    // ==========================================================================

    public void setRuleDAO(RuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
        if (ruleDAO != null) {
            this.ruleCache = new RuleCache(ruleDAO, metricRegistry);
        }
    }

    public void setLayerDetailsDAO(LayerDetailsDAO detailsDAO) {
        this.detailsDAO = detailsDAO;
    }

    public void setGsUserDAO(GSUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setUserGroupDAO(UserGroupDAO profileDAO) {
        this.userGroupDAO = profileDAO;
    }

}
