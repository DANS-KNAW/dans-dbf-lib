/**
 * Copyright (C) 2009-2016 DANS - Data Archiving and  Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.common.dbflib;


/**
 * Enumerates the alternative actions to be taken if a file does not exist.
 *
 * @author Jan van Mansum
 */
public enum IfNonExistent
{
    /**
     * Signals that the file should be created.
     */
    CREATE,
    /**
     * Signals that the an exception should be raised.
     */
    ERROR, 
    /**
     * Signals that the call should be ignored.
     */
    IGNORE;
    /**
     * @return if this is the CREATE constant.
     */
    boolean isCreate()
    {
        return this == CREATE;
    }

    /**
     * @return if this is the IGNORE constant.
     */
    boolean isIgnore()
    {
        return this == IGNORE;
    }

    /**
     * @return if this is the ERROR constant.
     */
    boolean isError()
    {
        return this == ERROR;
    }
}
