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
