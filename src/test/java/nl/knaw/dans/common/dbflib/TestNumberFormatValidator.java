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

import org.junit.Test;

/**
 * Tests the NumberFomatValidator.
 *
 * @author Jan van Mansum
 */
public class TestNumberFormatValidator
{
    private final DataValidator validator_5_2 = new NumberFormatValidator(new Field("test", Type.NUMBER, 5, 2));
    private final DataValidator validator_5_1 = new NumberFormatValidator(new Field("test", Type.NUMBER, 5, 1));
    private final DataValidator validator_5_0 = new NumberFormatValidator(new Field("test", Type.NUMBER, 5, 0));

    @Test
    public void shouldAcceptNumberStringOfCorrectFormat()
                                                 throws DbfLibException
    {
        validator_5_2.validate("1.34");
        validator_5_2.validate("12.45"); // max length
        validator_5_2.validate("0.23");
        validator_5_2.validate("-2.45"); // max length, with sign

        validator_5_1.validate("1.3");
        validator_5_1.validate("123.5"); // max length
        validator_5_1.validate("0.3");
        validator_5_1.validate("-23.5"); // max length, with sign

        validator_5_0.validate("1");
        validator_5_0.validate("12345"); // max length
        validator_5_0.validate("0");
        validator_5_0.validate("-2345"); // max length, with sign
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNumberStringOfTooManyDecimals()
                                                   throws DbfLibException
    {
        validator_5_2.validate("1.345");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNumberStringOfDecimalsWhereNoneExpected()
        throws DbfLibException
    {
        validator_5_0.validate("1.3");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNegativeNumberStringOfDecimalsWhereNoneExpected()
        throws DbfLibException
    {
        validator_5_0.validate("-1.3");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNumberStringOfTooFewDecimals()
                                                  throws DbfLibException
    {
        validator_5_2.validate("1.3");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectBoolean()
                             throws DbfLibException
    {
        validator_5_2.validate(Boolean.TRUE);
    }

    @Test
    public void shouldAcceptNumberThatFits()
                                    throws DbfLibException
    {
        validator_5_2.validate(1.3);
        validator_5_2.validate(1.34567890); // no problem: rounded
        validator_5_2.validate(12.34567890); // max number of int digits
        validator_5_2.validate(-2.34567890); // max number of int digits with sign

        validator_5_1.validate(1.3);
        validator_5_1.validate(1.34567890); // no problem: rounded
        validator_5_1.validate(123.34567890); // max number of int digits
        validator_5_1.validate(-23.34567890); // max number of int digits with sign

        validator_5_0.validate(1);
        validator_5_0.validate(1.34567890); // no problem: rounded
        validator_5_0.validate(12345.34567890); // max number of int digits
        validator_5_0.validate(-2345.34567890); // max number of int digits with sign
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectNumberWithTooManyIntDigits()
                                                throws DbfLibException
    {
        validator_5_2.validate(123.34567890); // max number of int digits
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectNumberWithTooManyIntDigitsWithSign()
        throws DbfLibException
    {
        validator_5_2.validate(-23.34567890); // max number of int digits
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectDate()
                          throws DbfLibException
    {
        validator_5_2.validate(Util.createDate(2009, 5, 29));
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectByteArray()
                               throws DbfLibException
    {
        validator_5_2.validate(new byte[] { 0x01, 0x02 });
    }
}
