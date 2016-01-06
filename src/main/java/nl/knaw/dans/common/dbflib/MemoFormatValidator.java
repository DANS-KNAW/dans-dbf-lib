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
class MemoFormatValidator
    extends AbstractDataValidator
{
    MemoFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.MEMO : "Can only be validator for MEMO fields";
    }

    /**
     * {@inheritDoc}
     * <p>
     * For a MEMO field values of types {@link String}, {@link Boolean}, {@link Date} and
     * {@link Number} are acceptable.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof String
                || typedObject instanceof Boolean
                || typedObject instanceof Date
                || typedObject instanceof Number)
        {
            return;
        }

        throw new DataMismatchException("Cannot write value of type " + typedObject.getClass().getName());
    }
}
