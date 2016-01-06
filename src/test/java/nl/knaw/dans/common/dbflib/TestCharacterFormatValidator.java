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
 * Tests the CharacterFormatValidator.
 *
 * @author Jan van Mansum
 */
public class TestCharacterFormatValidator
{
    private final DataValidator validator5 = new CharacterFormatValidator(new Field("test5", Type.CHARACTER, 5));
    private final DataValidator validator7 = new CharacterFormatValidator(new Field("test7", Type.CHARACTER, 7));
    private final DataValidator validator8 = new CharacterFormatValidator(new Field("test8", Type.CHARACTER, 8));

    @Test
    public void shouldAcceptShortString()
                                 throws DbfLibException
    {
        validator5.validate("");
        validator5.validate("1");
        validator5.validate("12345");
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectTooLargeString()
                                    throws DbfLibException
    {
        validator5.validate("123456");
    }

    @Test
    public void shouldAcceptBoolean()
                             throws DbfLibException
    {
        validator5.validate(Boolean.TRUE);
        validator5.validate(Boolean.FALSE);
    }

    @Test
    public void shouldAcceptShortNumber()
                                 throws DbfLibException
    {
        validator5.validate(0);
        validator5.validate(1);
        validator5.validate(12345);
        validator5.validate(123.4);
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectLongInteger()
                                 throws DbfLibException
    {
        validator5.validate(123456);
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectLongDouble()
                                throws DbfLibException
    {
        validator5.validate(1234.5);
    }

    @Test(expected = ValueTooLargeException.class)
    public void shouldRejectDateFor7CharField()
                                       throws DbfLibException
    {
        validator7.validate(Util.createDate(2009, 05, 29));
    }

    @Test
    public void shouldAcceptDateFor8CharField()
                                       throws DbfLibException
    {
        validator8.validate(Util.createDate(2009, 05, 29));
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectByteArray()
                               throws DbfLibException
    {
        validator5.validate(new byte[] { 0x01, 0x02 });
    }
}
