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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/**
 * Represents a number value in a record. If the <code>NumberValue</code> was initialized with a raw
 * value (by the library), it will return a typed value that is a subclass of {@link Number} and is
 * large enough to accommodate the value. It will try to be economical when doing this. For instance
 * it will choose an {@link Integer} over a {@link Long} if possible.
 *
 * @author Jan van Mansum
 */
public class NumberValue
    extends Value
{
    /*
     * Values less than 10 digits long can always be represented by an Integer, because
     * Integer.MAX_VALUE = 2 147 483 647. Note that numbers of length equal to
     * MAX_LENGTH_INTEGER are NOT represented by an integer, because they MAY be too large.
     */
    private static final int MAX_LENGTH_INTEGER = Integer.valueOf(Integer.MAX_VALUE).toString().length();

    /*
     * Analogous to MAX_LENGTH_INTEGER.
     */
    private static final int MAX_LENGTH_LONG = Long.valueOf(Long.MAX_VALUE).toString().length();

    /**
     * Creates a new NumberValue object.
     *
     * @param number a number
     */
    public NumberValue(final Number number)
    {
        super(number);
    }

    NumberValue(final Field field, final byte[] rawValue)
    {
        super(field, rawValue);
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        final String stringValue = new String(rawValue).trim();

        if (stringValue.isEmpty() || stringValue.equals("."))
        {
            return null;
        }

        final int decimalPointIndex = stringValue.indexOf('.');

        if (decimalPointIndex == -1)
        {
            if (stringValue.length() < MAX_LENGTH_INTEGER)
            {
                return Integer.parseInt(stringValue);
            }
            else if (stringValue.length() < MAX_LENGTH_LONG)
            {
                return Long.parseLong(stringValue);
            }
            else
            {
                return new BigInteger(stringValue);
            }
        }

        /*
         * Not sure yet what number of digits is safe to parse a value into a double. 14 seems to be
         * reasonably safe, but this needs to be proved.aField
         */
        if (stringValue.length() < 14)
        {
            return Double.parseDouble(stringValue);
        }

        /*
         * BigDecimal can hold anything.
         */
        return new BigDecimal(stringValue);
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
    {
        final Number number = (Number) typed;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try
        {
            byte[] bytes = null;

            if (field.getType() == Type.NUMBER || field.getType() == Type.FLOAT)
            {
                bytes = formatNumber(number, field).getBytes();
            }
            else
            {
                bytes = typed.toString().getBytes();
            }

            byteArrayOutputStream.write(bytes);
            byteArrayOutputStream.write(Util.repeat((byte) 0x00, field.getLength() - bytes.length));
        }
        catch (final IOException ioException)
        {
            assert false : "Programming error: writing to ByteOutputStream should never cause and IOException";

            throw new RuntimeException(ioException);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static Number convertIntegralToFractional(final Number integralNumber)
    {
        if (integralNumber instanceof BigInteger)
        {
            return new BigDecimal((BigInteger) integralNumber);
        }

        return integralNumber.doubleValue();
    }

    private static Number convertFractionalToIntegral(final Number fractionalNumber)
    {
        return Math.round(fractionalNumber.doubleValue());
    }

    private static boolean isIntegralNumber(final Number number)
    {
        return number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long
               || number instanceof BigInteger;
    }

    private static String formatNumber(final Number number, final Field field)
    {
        final String formatString =
            "%" + field.getLength() + (field.getDecimalCount() == 0 ? "d" : "." + field.getDecimalCount() + "f");

        Number num = number;

        if (field.getDecimalCount() > 0 && isIntegralNumber(number))
        {
            num = convertIntegralToFractional(number);
        }

        if (field.getDecimalCount() == 0 && ! isIntegralNumber(number))
        {
            num = convertFractionalToIntegral(number);
        }

        /*
         * Use the US Locale to force the use of a decimal point rather than a decimal comma.
         */
        return String.format(Locale.US, formatString, num);
    }
}
