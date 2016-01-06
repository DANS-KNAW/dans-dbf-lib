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
 * Tests the DateFormatValidator.
 *
 * @author Jan van Mansum
 */
public class TestDateFormatValidator
{
    private final DataValidator validator = new DateFormatValidator(new Field("test", Type.DATE));

    @Test
    public void shouldAcceptEightDigitString()
                                      throws DbfLibException
    {
        validator.validate("12345678");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectEightDigitStringWithLeadingSpaces()
        throws DbfLibException
    {
        validator.validate(" 12345678");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectEightDigitStringWithTrailingSpaces()
        throws DbfLibException
    {
        validator.validate("12345678 ");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectSevenDigitString()
                                      throws DbfLibException
    {
        validator.validate("1234567");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNineDigitString()
                                     throws DbfLibException
    {
        validator.validate("123456789");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNotAllDigitString()
                                       throws DbfLibException
    {
        validator.validate("1234567a");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectBoolean()
                             throws DbfLibException
    {
        validator.validate(true);
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNumber()
                            throws DbfLibException
    {
        validator.validate(20091011);
    }

    @Test
    public void shouldAcceptDate()
                          throws DbfLibException
    {
        validator.validate(Util.createDate(2009, 6, 4));
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectByteArray()
                               throws DbfLibException
    {
        validator.validate(new byte[] { 0x09, 0x10, 0x11 });
    }
}
