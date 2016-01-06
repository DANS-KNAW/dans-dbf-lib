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
 * Validates that a Java Object can be serialized to a DBF NUMBER type field. Only Number and String
 * objects (within the field constraints) can be so serialized.
 *
 * @author Jan van Mansum
 */
class NumberFormatValidator
    extends AbstractDataValidator
{
    private final Pattern stringPattern;

    NumberFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.NUMBER || field.getType() == Type.FLOAT : "Can only be validator for NUMBER or FLOAT fields";

        /*
         * Build the pattern that the data must comply to if it is a String.
         */
        int beforeDecimalPointLength = field.getLength();
        String decimalPartPattern = "";

        if (field.getDecimalCount() > 0)
        {
            /*
             * Subtract one extra for the decimal point.
             */
            beforeDecimalPointLength -= field.getDecimalCount() - 1;

            decimalPartPattern = "\\.\\d{" + field.getDecimalCount() + "," + field.getDecimalCount() + "}";
        }

        String withSignAlternative = "";

        if (beforeDecimalPointLength > 1)
        {
            withSignAlternative = "\\-\\d{1," + (beforeDecimalPointLength - 1) + "}|";
        }

        final String withoutSignAlternative = "\\d{1," + beforeDecimalPointLength + "}";
        final String patternString = "(" + withSignAlternative + withoutSignAlternative + ")" + decimalPartPattern;

        stringPattern = Pattern.compile(patternString);
    }

    /**
     * {@inheritDoc}
     * <p>
     * For a NUMBER or FLOAT field a {@link Number} or a {@link String} is acceptable. A
     * <code>String</code> is only acceptable if it contains a valid number value, i.e. one that
     * fits and contains exactly the number of digits after the decimal point as specified in the
     * field definition. Anything else is rejected.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof Number)
        {
            final Number numberValue = (Number) typedObject;

            /*
             * Check if the number will fit in the field. Note that if the Number object contains
             * more decimals than the field specification it will be rounded.
             */
            final int nrPositionsForDecimals = field.getDecimalCount() == 0 ? 0 : field.getDecimalCount() + 1;

            if (Util.getSignWidth(numberValue) + Util.getNumberOfIntDigits(numberValue) > field.getLength()
                    - nrPositionsForDecimals)
            {
                throw new ValueTooLargeException("Number does not fit in the field '" + field.getName() + "': "
                                                 + numberValue);
            }

            return;
        }

        if (typedObject instanceof String)
        {
            String stringValue = (String) typedObject;
            stringValue = stringValue.trim();

            if (! stringPattern.matcher(stringValue).matches())
            {
                throw new DataMismatchException("The string '" + stringValue
                                                + "' does not contain a valid number, is too long or contains an incorrect number of decimals");
            }

            return;
        }

        throw new DataMismatchException("Cannot write objects of type '" + typedObject.getClass().getName()
                                        + "' to a NUMBER or FLOAT field");
    }
}
