/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.utils.storage;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.DuplicateResourceException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.User;
import org.wso2.charon.core.schema.SCIMConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemroyUserManager implements UserManager {
    //following will be used when MT implemented properly.
    private String tenantDomain = null;
    private int tenantId = 0;

    //in memory user manager stores users
    ConcurrentHashMap<String, User> inMemoryUserList = new ConcurrentHashMap<String, User>();
    ConcurrentHashMap<String, Group> inMemoryGroupList = new ConcurrentHashMap<String, Group>();


    public InMemroyUserManager(int tenantId, String tenantDomain) {
        this.tenantId = tenantId;
        this.tenantDomain = tenantDomain;
    }

    /**
     * Obtains the user given the id.
     *
     * @param userId
     * @return
     */
    @Override
    public User getUser(String userId) throws CharonException {
        if (userId != null) {
            if (!inMemoryUserList.isEmpty() && inMemoryUserList.containsKey(userId)) {
                return inMemoryUserList.get(userId);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<User> listUsers() throws CharonException {
        if (!inMemoryUserList.isEmpty()) {
            List<User> returnedUsers = new ArrayList<User>();
            for (User user : inMemoryUserList.values()) {
                returnedUsers.add(user);
            }
            return returnedUsers;
        } else {
            throw new CharonException("User storage is empty");
        }
    }

    @Override
    public List<User> listUsersByAttribute(Attribute attribute) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersByFilter(String filter, String operation, String value)
            throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersBySort(String sortBy, String sortOrder) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsersWithPagination(int startIndex, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Update the user in full.
     *
     * @param user
     */
    @Override
    public User updateUser(User user) throws CharonException {
        if (!inMemoryUserList.isEmpty()) {
            //check if user exist in the system
            if (inMemoryUserList.containsKey(user.getId())) {
                //check for unique user name
                for (Map.Entry<String, User> userEntry : inMemoryUserList.entrySet()) {
                    if ((user.getUserName().equals(userEntry.getValue().getUserName()))
                        && !(user.getId()).equals(userEntry.getValue().getId())) {
                        String error = "Updated user name already exist in the system.";
                        //TODO:log error
                        throw new CharonException(error);
                    }
                }
                //remove existing user.
                inMemoryUserList.remove(user.getId());
                //add updated user
                inMemoryUserList.put(user.getId(), user);
            } else {
                String error = "Updating user with the id does not exist in the user store..";
                //TODO:log error
                throw new CharonException(error);
            }
        } else {
            String error = "No users exist in the user store..";
            //TODO:log error
            throw new CharonException(error);
        }
        return user;
    }

    /**
     * Update the user partially only with updated attributes.
     *
     * @param updatedAttributes
     */
    @Override
    public User updateUser(List<Attribute> updatedAttributes) {
        //TODO:should set last modified date
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * Delete the user given the user id.
     *
     * @param userId
     */
    @Override
    public void deleteUser(String userId) throws NotFoundException, CharonException {
        if (userId != null) {
            if (!inMemoryUserList.isEmpty() && inMemoryUserList.containsKey(userId)) {
                validateGroupsOnUserDelete(inMemoryUserList.get(userId));
                inMemoryUserList.remove(userId);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * Create user with the given user object.
     *
     * @param user
     */
    @Override
    public User createUser(User user) throws CharonException, DuplicateResourceException {

        if (!inMemoryUserList.isEmpty()) {
            for (Map.Entry<String, User> userEntry : inMemoryUserList.entrySet()) {
                if (user.getUserName().equals(userEntry.getValue().getUserName())) {
                    String error = "User already exist in the system.";
                    //TODO:log error
                    throw new CharonException(error);
                }
            }
            inMemoryUserList.put(user.getId(), user);
        } else {
            inMemoryUserList.put(user.getId(), user);
        }
        return user;
    }

    /**
     * ****************Group manipulation operations*******************
     */
    @Override
    public Group getGroup(String groupId) throws CharonException {
        if (groupId != null) {
            if (!inMemoryGroupList.isEmpty() && inMemoryGroupList.containsKey(groupId)) {
                return inMemoryGroupList.get(groupId);
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public List<Group> listGroups() throws CharonException {
        if (!inMemoryGroupList.isEmpty()) {
            List<Group> returnedGroups = new ArrayList<Group>();
            for (Group group : inMemoryGroupList.values()) {
                returnedGroups.add(group);
            }
            return returnedGroups;
        } else {
            throw new CharonException("User storage is empty");
        }
    }

    @Override
    public List<Group> listGroupsByAttribute(Attribute attribute) throws CharonException {
        return null;
    }

    @Override
    public List<Group> listGroupsByFilter(String filter, String operation, String value) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Group> listGroupsBySort(String sortBy, String sortOrder) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Group> listGroupsWithPagination(int startIndex, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Group createGroup(Group group) throws CharonException, DuplicateResourceException {
        if (!inMemoryGroupList.isEmpty()) {
            if (group.getExternalId() != null) {
                for (Group group1 : inMemoryGroupList.values()) {
                    if (group.getExternalId().equals(group1.getExternalId())) {
                        String error = "Group already exist in the system.";
                        //TODO:log error
                        throw new CharonException(error);
                    }
                }
            }
            validateMembersOnGroupCreate(group);
            inMemoryGroupList.put(group.getId(), group);
        } else {
            validateMembersOnGroupCreate(group);
            inMemoryGroupList.put(group.getId(), group);
        }
        return group;
    }

    @Override
    public Group updateGroup(Group oldGroup, Group group) throws CharonException {
        if (!inMemoryGroupList.isEmpty()) {
            if (inMemoryGroupList.containsKey(group.getId())) {
                if (group.getExternalId() != null) {
                    for (Group group1 : inMemoryGroupList.values()) {
                        if ((group.getExternalId().equals(group1.getExternalId())) &&
                            !(group.getId().equals(group1.getId()))) {
                            String error = "Group already exist in the system.";
                            //TODO:log error
                            throw new CharonException(error);
                        }
                    }
                }
                validateMembersOnGroupUpdate(group);
                inMemoryGroupList.remove(group.getId());
                inMemoryGroupList.put(group.getId(), group);
            } else {
                String error = "Updating user with the id does not exist in the user store..";
                //TODO:log error
                throw new CharonException(error);
            }
        } else {
            String error = "No users exist in the user store..";
            //TODO:log error
            throw new CharonException(error);
        }
        return group;
    }

    @Override
    public Group updateGroup(List<Attribute> attributes) throws CharonException {
        //TODO:should set last modified date
        return null;
    }

    @Override
    public void deleteGroup(String groupId) throws NotFoundException, CharonException {

        if (groupId != null) {
            if (!inMemoryGroupList.isEmpty() && inMemoryGroupList.containsKey(groupId)) {
                validateMembersOnGroupDelete(inMemoryGroupList.get(groupId));
                inMemoryGroupList.remove(groupId);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * ****************private methods*************************************
     */

    private void validateMembersOnGroupCreate(Group group) throws CharonException {
/*
        List<String> userIDs = group.getUserMembers();
        List<String> groupIDs = group.getGroupMembers();
        if (groupIDs != null && !groupIDs.isEmpty()) {
            for (String groupID : groupIDs) {
                if (!inMemoryGroupList.containsKey(groupID)) {
                    //throw exception
                    String error = "Group member: " + groupID + " doesn't exist in the system.";
                    throw new CharonException(error);
                }
            }
        }
        for (String userID : userIDs) {
            if (inMemoryUserList.containsKey(userID)) {
                User user = inMemoryUserList.get(userID);
                //update direct membership
                if (!user.isUserMemberOfGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId())) {
                    user.setGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId(), group.getDisplayName());
                    //TODO:set display name in group members taken from members

                }
            } else {
                //throw error.
                String error = "User member: " + userID + " doesn't exist in the system.";
                throw new CharonException(error);
            }
        }
*/
        checkForValidMembers(group);
        updateUserMembers(group);
        updateGroupMembers(group);
    }

    private void validateMembersOnGroupUpdate(Group group) throws CharonException {
        checkForValidMembers(group);
        updateUserMembers(group);
        updateGroupMembers(group);
        removeObsoleteRecordsOnGroupUpdate(group);
    }

    private void validateMembersOnGroupDelete(Group group) throws CharonException {
        //get user members and remove direct membership from them
        List<String> userMembers = group.getUserMembers();
        if (userMembers != null && !userMembers.isEmpty()) {
            for (String userMember : userMembers) {
                if (inMemoryUserList.containsKey(userMember)) {
                    User user = inMemoryUserList.get(userMember);
                    if (user.isUserMemberOfGroup(null, group.getId())) {
                        user.removeFromGroup(group.getId());
                    }
                }
            }
        }
        //see whether any other groups in which this group is a member.
        for (Group parentGroup : inMemoryGroupList.values()) {
            if (parentGroup.getMembers().contains(group.getId())) {
                parentGroup.removeMember(group.getId());
            }
        }

    }

    private void validateGroupsOnUserDelete(User user) throws CharonException {
        //get groups in which user is a direct member
        List<String> groupIds = user.getGroups();
        if (groupIds != null && !groupIds.isEmpty()) {
            for (String groupId : groupIds) {
                if (inMemoryGroupList.containsKey(groupId)) {
                    Group group = inMemoryGroupList.get(groupId);
                    group.removeMember(user.getId());
                }
            }
        }
    }

    //check if valid member

    private void checkForValidMembers(Group group) throws CharonException {
        List<String> members = group.getMembers();
        if (members != null && !members.isEmpty()) {
            for (String member : members) {
                if (!(inMemoryUserList.containsKey(member) || inMemoryGroupList.containsKey(member))) {
                    String error = "Member with id: " + member + " doesn't exist in the system.";
                    throw new CharonException(error);
                }
            }
        }
    }

    //update user members

    private void updateUserMembers(Group group) throws CharonException {
        List<String> members = group.getMembers();
        for (String member : members) {
            if (inMemoryUserList.containsKey(member)) {
                User user = inMemoryUserList.get(member);
                //update group with all details regarding member
                group.removeMember(member);
                Map<String, Object> valueProperties = new HashMap<String, Object>();
                valueProperties.put(SCIMConstants.CommonSchemaConstants.VALUE, user.getId());
                if (user.getDisplayName() != null) {
                    valueProperties.put(SCIMConstants.CommonSchemaConstants.DISPLAY, user.getDisplayName());
                }
                valueProperties.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.USER);
                group.setMember(valueProperties);
                //update user with details about the group it belongs to.
                if (user.isUserMemberOfGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId())) {
                    //update group details in user
                    user.removeFromGroup(group.getId());
                    user.setGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId(), group.getDisplayName());
                } else {
                    //add group details in user
                    user.setGroup(SCIMConstants.UserSchemaConstants.DIRECT_MEMBERSHIP, group.getId(), group.getDisplayName());
                }
            }
        }
    }

    //update group members

    private void updateGroupMembers(Group group) throws CharonException {
        List<String> members = group.getMembers();
        for (String member : members) {
            if (inMemoryGroupList.containsKey(member)) {
                Group memberGroup = inMemoryGroupList.get(member);
                group.removeMember(member);

                Map<String, Object> valueProperties = new HashMap<String, Object>();
                valueProperties.put(SCIMConstants.CommonSchemaConstants.VALUE, memberGroup.getId());
                if (memberGroup.getDisplayName() != null) {
                    valueProperties.put(SCIMConstants.CommonSchemaConstants.DISPLAY, memberGroup.getDisplayName());
                }
                valueProperties.put(SCIMConstants.CommonSchemaConstants.TYPE, SCIMConstants.GROUP);
                group.setMember(valueProperties);
            }
        }

    }

    //remove obsolete records on group update

    private void removeObsoleteRecordsOnGroupUpdate(Group newGroup) throws CharonException {
        for (User user : inMemoryUserList.values()) {
            if (user.isUserMemberOfGroup(null, newGroup.getId())) {
                if (!(newGroup.getGroupMembers().contains(user.getId()))) {
                    user.removeFromGroup(newGroup.getId());
                }
            }
        }
    }

}
