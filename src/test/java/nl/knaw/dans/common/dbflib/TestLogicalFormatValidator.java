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
 * Tests the LogicalFormatValidator.
 *
 * @author Jan van Mansum
 */
public class TestLogicalFormatValidator
{
    private final DataValidator validator = new LogicalFormatValidator(new Field("test", Type.LOGICAL));

    @Test
    public void shouldAcceptStringWithYNTF_or_Space()
                                             throws DbfLibException
    {
        validator.validate("Y");
        validator.validate("N");
        validator.validate("T");
        validator.validate("F");
        validator.validate(" ");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectStringWithA()
                                 throws DbfLibException
    {
        validator.validate("A");
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectTooLargeValue()
                                   throws DbfLibException
    {
        validator.validate("True");
    }

    @Test
    public void shouldAcceptBoolean()
                             throws DbfLibException
    {
        validator.validate(Boolean.TRUE);
        validator.validate(Boolean.FALSE);
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectNumber()
                            throws DbfLibException
    {
        validator.validate(123);
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectDate()
                          throws DbfLibException
    {
        validator.validate(Util.createDate(2009, 5, 29));
    }

    @Test(expected = DataMismatchException.class)
    public void shouldRejectByteArray()
                               throws DbfLibException
    {
        validator.validate(new byte[] { 0x01, 0x02 });
    }
}
