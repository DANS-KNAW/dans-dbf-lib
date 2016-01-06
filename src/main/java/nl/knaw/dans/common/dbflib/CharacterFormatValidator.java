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

import java.util.Date;

/**
 * @author Jan van Mansum
 */
class CharacterFormatValidator
    extends AbstractDataValidator
{
    /**
     * Creates a new CharacterFormatValidator object.
     *
     * @param field the field for which to do validation
     */
    public CharacterFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.CHARACTER : "Can only be validator for CHARACTER fields";
    }

    /**
     * {@inheritDoc}
     *
     * For a CHARACTER field are acceptable: {@link String}, {@link Number},
     * {@link Date} and {@link Boolean} values.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof String || typedObject instanceof Number)
        {
            if (typedObject.toString().length() > field.getLength())
            {
                throw new ValueTooLargeException("Value too large to it in field");
            }

            return;
        }
        else if (typedObject instanceof Date)
        {
            if (field.getLength() < Type.DATE.getLength())
            {
                throw new ValueTooLargeException("Field too short to contain a date");
            }

            return;
        }
        else if (typedObject instanceof Boolean)
        {
            return;
        }

        throw new DataMismatchException("Cannot add value of type " + typedObject.getClass().getName());
    }
}
