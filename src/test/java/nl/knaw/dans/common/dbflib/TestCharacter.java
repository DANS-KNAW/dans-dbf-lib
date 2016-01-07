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

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests reading and writing character fields.
 *
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestCharacter
    extends BaseTestcase
{
    /**
     * Creates a new TestCharacter object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestCharacter(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    @Test
    public void readCharacter()
                       throws IOException, CorruptedTableException
    {
        final Table t1 = new Table(new File("src/test/resources/" + versionDirectory + "/types/CHARACTE.DBF"));

        try
        {
            t1.open(IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = t1.recordIterator();

            Record r = recordIterator.next();
            assertEquals("first char",
                         r.getStringValue("CHAR1"));
            assertEquals("first character",
                         r.getStringValue("CHAR2").substring(0, 15));

            r = recordIterator.next();

            String longString = "";
            longString = "here we are using for many versions the maximum length of character field, that is 254 characters ";
            longString += "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
            longString += "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx!!!";
            assertEquals("long char ",
                         r.getStringValue("CHAR1"));
            assertEquals(longString,
                         r.getStringValue("CHAR2"));

            r = recordIterator.next();
            assertEquals("          ",
                         r.getStringValue("CHAR1"));
            assertEquals("          ",
                         r.getStringValue("CHAR2").substring(0, 10));
        }
        finally
        {
            t1.close();
        }
    }

    @Test
    public void writeCharacter()
                        throws IOException, DbfLibException
    {
        final Ranges ignoredRanges = new Ranges();
        ignoredRanges.addRange(0x01, 0x03); // modified
        ignoredRanges.addRange(0x1d, 0x1d); // language driver
        ignoredRanges.addRange(0x1e, 0x1f); // reserved
        ignoredRanges.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRanges.addRange(0x4C, 0x4f); // field data address

        /*
         * Garbage in Clipper 5, in other versions not meaningful.
         */
        ignoredRanges.addRange(0x26, 0x2a); // garbage
        ignoredRanges.addRange(0x32, 0x3f); // garbage
        ignoredRanges.addRange(0x46, 0x4a); // garbage
        ignoredRanges.addRange(0x52, 0x5f); // garbage

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/types", "CHARACTE", version, ignoredRanges, null);
    }
}
