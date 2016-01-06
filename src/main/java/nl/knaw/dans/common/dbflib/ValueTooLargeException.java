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
 * Thrown when trying to add a record that contains values that are too large for their designated
 * fields. Note that numbers that are considered too large only if the number of digits before the
 * decimal point exceeds the number number that fit in the field; too many decimal digits will
 * simply be rounded.
 * <p>
 * Note, too, that dBase III handles NUMBER values internally as floating point values. It
 * <em>will</em> display the values as found in the DBF file but if they are too large or contain
 * too many digits dBase will display an error message or round the value when trying to process it
 * (e.g. if you try to save the value again in the dBase program). The same is true for dBase IV and
 * V and FLOAT values.
 *
 * @author Jan van Mansum
 */
public class ValueTooLargeException
    extends DbfLibException
{
    private static final long serialVersionUID = -9205029469017433931L;

    ValueTooLargeException(final String message)
    {
        super(message);
    }
}
