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
