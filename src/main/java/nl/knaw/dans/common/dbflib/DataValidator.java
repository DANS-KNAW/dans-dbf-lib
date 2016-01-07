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
 * Validates that the data specified is of the right type and format. What <em>is</em> the right
 * type and format depends on the implementing class. A <code>DataValidator</code> implementation is
 * tied to a specific field. It uses field's attributes (type, length and decimal count)to determine
 * if the data to be validated can be written to such a field.
 *
 * @author Jan van Mansum
 */
interface DataValidator
{
    /**
     * Returns normally if <code>typedObject</code> is of the right type and format, throws a
     * {@link DbfLibException} otherwise. Reasons for rejecting an object include: it is of
     * incompatible type (e.g., a <code>java.util.Date</code> object is passed into a LOGICAL data
     * validator, it is of the correct type but not of the correct format (e.g., a String is passed
     * to a CHARACTER data validator but the string is too long).
     *
     * @param typedObject the object to be validated
     * @throws DbfLibException if the object is rejected
     */
    void validate(Object typedObject)
           throws DbfLibException;
}
