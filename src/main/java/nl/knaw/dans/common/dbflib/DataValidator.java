/*
 * Copyright 2009-2010 Data Archiving and Networked Services (DANS), Netherlands.
 *
 * This file is part of DANS DBF Library.
 *
 * DANS DBF Library is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * DANS DBF Library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with DANS DBF Library. If
 * not, see <http://www.gnu.org/licenses/>.
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
