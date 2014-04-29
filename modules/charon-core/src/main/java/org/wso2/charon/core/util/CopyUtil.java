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
package org.wso2.charon.core.util;

import org.wso2.charon.core.exceptions.CharonException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This is to create a deep copy of the object using java serialization.
 * SCIMObject instances have complex object graphs and hard to deep copy by
 * overriding clone method. Hence, using serialization to do the deep copy.
 */
public class CopyUtil {
    public static Object deepCopy(Object oldObject) throws CharonException {
        ObjectOutputStream objOutPutStream;
        ObjectInputStream objInputStream;
        Object newObject = null;
        try {
            //create byte array output stream
            ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream();
            //create object out put stream using above
            objOutPutStream = new ObjectOutputStream(byteArrayOutPutStream);
            //serialize the object and write it to the byte array out put stream
            objOutPutStream.writeObject(oldObject);

            //create a byte array input stream from the content of the byte array output stream
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    byteArrayOutPutStream.toByteArray());

            objInputStream = new ObjectInputStream(byteArrayInputStream);
            newObject = objInputStream.readObject();

        } catch (IOException e) {
            throw new CharonException("Error in serializing while creating a deep copy of the object");
        } catch (ClassNotFoundException e) {
            throw new CharonException("Error in de-serializing while creating a deep copy of the object");
        }
        return newObject;
    }
}
