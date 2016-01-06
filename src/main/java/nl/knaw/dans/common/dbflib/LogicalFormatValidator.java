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

import java.util.regex.Pattern;

/**
 * @author Jan van Mansum
 */
class LogicalFormatValidator
    extends AbstractDataValidator
{
    private static final Pattern booleanPattern = Pattern.compile("[YNTF ]");

    LogicalFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.LOGICAL : "Can only be validator for LOGICAL fields";
    }

    /**
     * {@inheritDoc}
     * <p>
     * For a LOGICAL field a {@link Boolean}, or a {@link String} is acceptable. A
     * <code>String</code> is acceptable only if it contains one of Y, N, T, F. The String must not
     * contain leading or trailing spaces.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof Boolean)
        {
            return;
        }

        if (typedObject instanceof String)
        {
            final String booleanString = (String) typedObject;

            if (! booleanPattern.matcher(booleanString).matches())
            {
                throw new DataMismatchException("Boolean must be one of Y, N, T, F or a space");
            }

            return;
        }

        throw new DataMismatchException("Cannot write objects of type '" + typedObject.getClass().getName()
                                        + "' to a LOGICAL field");
    }
}
