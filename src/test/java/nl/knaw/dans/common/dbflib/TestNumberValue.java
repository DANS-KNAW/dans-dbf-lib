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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TestNumberValue
{
    /**
     * Test that the field specification does not limit the "kind" of numbers that can be formatted.
     * That is to say, the exact type of "typed value" stored is transparent to the user.
     */
    @Test
    public void formatsAnyNumberWithEqualsFieldSpecification()
                                                      throws Exception
    {
        final Value numberShort = new NumberValue(new Short((short) 1234));
        final Value numberInteger = new NumberValue(new Integer(1234));
        final Value numberLong = new NumberValue(new Long(1234));
        final Value numberBigInteger = new NumberValue(new BigInteger("1234"));

        final Value numberDouble1 = new NumberValue(new Double(1234.454));
        final Value numberDouble2 = new NumberValue(new Double(1234.547));
        final Value numberFloat = new NumberValue(new Float(1234.454));
        final Value numberBigDecimal = new NumberValue(new BigDecimal("1234.45"));

        final Field fieldIntegral = new Field("Integral", Type.NUMBER, 10, 0);
        final Field fieldFractional = new Field("Fractional", Type.NUMBER, 10, 2);

        /*
         * Make a String of the byte[] for easier comparison.
         */
        assertEquals("      1234",
                     new String(numberShort.getRawValue(fieldIntegral)));
        assertEquals("      1234",
                     new String(numberInteger.getRawValue(fieldIntegral)));
        assertEquals("      1234",
                     new String(numberLong.getRawValue(fieldIntegral)));
        assertEquals("      1234",
                     new String(numberBigInteger.getRawValue(fieldIntegral)));

        /*
         * Note that the double value is rounded.
         */
        assertEquals("      1234",
                     new String(numberDouble1.getRawValue(fieldIntegral)));
        assertEquals("      1235",
                     new String(numberDouble2.getRawValue(fieldIntegral)));
        assertEquals("      1234",
                     new String(numberFloat.getRawValue(fieldIntegral)));
        assertEquals("      1234",
                     new String(numberBigDecimal.getRawValue(fieldIntegral)));

        /*
         * Make a String of the byte[] for easier comparison.
         */
        assertEquals("   1234.00",
                     new String(numberShort.getRawValue(fieldFractional)));
        assertEquals("   1234.00",
                     new String(numberInteger.getRawValue(fieldFractional)));
        assertEquals("   1234.00",
                     new String(numberLong.getRawValue(fieldFractional)));
        assertEquals("   1234.00",
                     new String(numberBigInteger.getRawValue(fieldFractional)));

        /*
         * Note that the double value is rounded.
         */
        assertEquals("   1234.45",
                     new String(numberDouble1.getRawValue(fieldFractional)));
        assertEquals("   1234.55",
                     new String(numberDouble2.getRawValue(fieldFractional)));
        assertEquals("   1234.45",
                     new String(numberFloat.getRawValue(fieldFractional)));
        assertEquals("   1234.45",
                     new String(numberBigDecimal.getRawValue(fieldFractional)));
    }
}
