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
 * Represents a boolean value in a record.
 *
 * @author Jan van Mansum
 */
public class BooleanValue
    extends Value
{
    /**
     * Creates a new <code>BooleanValue</code> object.
     *
     * @param booleanValue a boolean
     */
    public BooleanValue(final Boolean booleanValue)
    {
        super(booleanValue);
    }

    BooleanValue(final Field field, final byte[] rawValue)
    {
        super(field, rawValue);
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        char c = (char) rawValue[0];

        if (c == ' ')
        {
            return null;
        }

        return (c == 'Y') || (c == 'y') || (c == 'T') || (c == 't');
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
                            throws ValueTooLargeException
    {
        if (Boolean.TRUE.equals(typed))
        {
            return new byte[] { 'T' };
        }

        return new byte[] { 'F' };
    }
}
