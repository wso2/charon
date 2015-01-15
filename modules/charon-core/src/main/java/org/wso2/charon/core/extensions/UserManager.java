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
package org.wso2.charon.core.extensions;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.DuplicateResourceException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.User;

import java.util.List;

/**
 * This is the interface for UserManager extension.
 * An implementation can plugin their own user manager-(either LDAP based, DB based etc)
 * by implementing this interface and mentioning it in configuration.
 */
public interface UserManager extends Storage {

    /***************User Manipulation operations*******************/
    /**
     * Create user with the given user object.
     *
     * @param user User resource to be created in the user store of service provider.
     * @return newly created SCIM User resource sent back to the client in the response.
     */
    public User createUser(User user) throws CharonException, DuplicateResourceException;

    public User createUser(User user, boolean isBulkUserAdd) throws CharonException, DuplicateResourceException;
    /**
     * Obtains the user given the id.
     *
     * @param userId
     * @return
     */
    public User getUser(String userId) throws CharonException;

    public List<User> listUsers() throws CharonException;

    public List<User> listUsersByAttribute(Attribute attribute);

    public List<User> listUsersByFilter(String filter, String operation, String value)
            throws CharonException;

    public List<User> listUsersBySort(String sortBy, String sortOrder);

    public List<User> listUsersWithPagination(int startIndex, int count);

    /**
     * Update the user in full.
     *
     * @param user SCIM User object containing the updated attributes
     * @return return the full updates user
     */
    public User updateUser(User user) throws CharonException;

    /**
     * Update the user partially only with updated attributes.
     *
     * @param updatedAttributes : list of attributes to be updated
     * @return User : For a patch request, server can respond with either 200 ok + entire resource
     *         or 204 No content+appropriate response headers. But user manager should return the updated resource.
     */
    public User updateUser(List<Attribute> updatedAttributes);

    /**
     * Delete the user given the user id.
     *
     * @param userId
     */
    public void deleteUser(String userId) throws NotFoundException, CharonException;


    /* ****************Group manipulation operations********************/

    public Group createGroup(Group group) throws CharonException, DuplicateResourceException;

    public Group getGroup(String groupId) throws CharonException;

    public List<Group> listGroups() throws CharonException;

    public List<Group> listGroupsByAttribute(Attribute attribute) throws CharonException;

    public List<Group> listGroupsByFilter(String filter, String operation, String value) throws CharonException;

    public List<Group> listGroupsBySort(String sortBy, String sortOrder) throws CharonException;

    public List<Group> listGroupsWithPagination(int startIndex, int count);

    public Group updateGroup(Group oldGroup, Group newGroup) throws CharonException;

    public Group updateGroup(List<Attribute> attributes) throws CharonException;

    public void deleteGroup(String groupId) throws NotFoundException, CharonException;

}
