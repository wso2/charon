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
package org.wso2.charon.utils.user.mgt;

import org.wso2.charon.core.attributes.Attribute;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.exceptions.DuplicateResourceException;
import org.wso2.charon.core.exceptions.NotFoundException;
import org.wso2.charon.core.extensions.UserManager;
import org.wso2.charon.core.objects.Group;
import org.wso2.charon.core.objects.SCIMObject;
import org.wso2.charon.core.objects.User;

import java.util.List;

public class FSBasedUserManager implements UserManager {

    /**
     * Obtains the user given the id.
     *
     * @param userId
     * @return
     */
    public User getUser(String userId) {
        //retrieves the user from the storage
        //builds up a SCIMUserObject.
        //returns

        //for the moment, returns an arbitrary user object.

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<User> listUsers() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public User updateUser(User user) {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * Update the user partially only with updated attributes.
     *
     * @param updatedAttributes
     */
    public User updateUser(List<Attribute> updatedAttributes) {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * Delete the user given the user id.
     *
     * @param userId
     */
    public void deleteUser(String userId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Create user with the given user object.
     *
     * @param user
     */
    public User createUser(User user) throws CharonException, DuplicateResourceException {
        //To change body of implemented methods use File | Settings | File Templates.
        return null;
    }

    /**
     * ****************Group manipulation operations*******************
     */
    @Override
    public Group getGroup(String groupId) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Group> listGroups() throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Group> listGroupsByAttribute(Attribute attribute) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Group updateGroup(Group oldGroup, Group group) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Group updateGroup(List<Attribute> attributes) throws CharonException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteGroup(String groupId) throws NotFoundException {

    }

    public SCIMObject getResource(String resourceId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
