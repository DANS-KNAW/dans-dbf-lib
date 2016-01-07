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
