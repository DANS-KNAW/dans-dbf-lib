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
