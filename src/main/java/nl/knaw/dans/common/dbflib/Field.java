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
 * Represents a field description in a table.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public class Field
{
    private final String name;
    private final Type type;
    private final int length;
    private final int decimalCount;
    private final DataValidator validator;

    /**
     * Creates a new Field object. If the specified type has a fixed size and decimal count, they
     * are used. otherwise size is initialized to 1 and decimal count to 0.
     *
     * @param name the name of the field
     * @param type the type of the field
     */
    public Field(final String name, final Type type)
    {
        this(name, type, 1, 0);
    }

    /**
     * Creates a new Field object. Decimal count is initialized to 0.
     *
     * @param name the name of the field
     * @param type the type of the field
     * @param length the length of the field
     */
    public Field(final String name, final Type type, final int length)
    {
        this(name, type, length, 0);
    }

    /**
     * Creates a new Field object. <code>length</code> and <code>decimalCount</code> do not apply to
     * all field types.
     *
     * @param name name of the field
     * @param type the type of the field
     * @param length the length of the field
     * @param decimalCount the decimal count of the field.
     */
    public Field(final String name, final Type type, final int length, final int decimalCount)
    {
        this.name = name;
        this.type = type;
        this.length = type.getLength() == -1 ? length : type.getLength();
        this.decimalCount = decimalCount;
        validator = DataFormatValidatorFactory.createValidator(this);
    }

    /**
     * Returns the name of the field.
     *
     * @return the name of the field
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the type of the field
     *
     * @return the type of the field
     */
    public Type getType()
    {
        return type;
    }

    /**
     * Returns the length of the field, or -1 if not applicable
     *
     * @return the length of the field
     */
    public int getLength()
    {
        return length;
    }

    /**
     * Returns the decimal count of the field, or -1 if not applicable
     *
     * @return the decimal count of the field
     */
    public int getDecimalCount()
    {
        return decimalCount;
    }

    void validateTypedValue(final Object aTypedValue)
                     throws DbfLibException
    {
        validator.validate(aTypedValue);
    }

    @Override
    public boolean equals(final Object other)
    {
        if (other instanceof Field)
        {
            final Field otherField = (Field) other;

            return decimalCount == otherField.decimalCount && length == otherField.length
                   && name.equals(otherField.name) && type == otherField.type;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        /*
         * Divide by 2 to avoid possible overflow. To reiterate equals/hashCode: hashCode determines
         * the subsets, equals the exact equality. a equals b => a.hash == b.hash.
         */
        return name.hashCode() / 2 + type.hashCode() / 2;
    }
}
