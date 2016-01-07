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
import static org.junit.Assert.assertNull;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Tests reading and writing number values.
 *
 * @author Jan van Mansum
 */
@RunWith(Parameterized.class)
public class TestNumber
    extends BaseTestcase
{
    /**
     * Creates a new TestNumber object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestNumber(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    /**
     * No Clipper5, because 'Numeric' field in Clipper5 behaves like 'Float' field in dBaseIV and
     * dBaseV.
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        final Object[][] testParameters =
            new Object[][]
            {
                { Version.DBASE_3, "dbase3plus" },
                { Version.DBASE_4, "dbase4" },
                { Version.DBASE_5, "dbase5" },
                { Version.FOXPRO_26, "foxpro26" }
            };

        return Arrays.asList(testParameters);
    }

    /**
     * Tests reading fields with the maximum and minimum lengths and decimal count respectively.
     */
    @Test
    public strictfp void reading_maximal_and_minimal_values()
                                                     throws IOException, CorruptedTableException
    {
        final Table number = new Table(new File("src/test/resources/" + versionDirectory + "/types/NUMBER.DBF"));

        try
        {
            number.open();

            final List<Record> records = UnitTestUtil.createSortedRecordList(number.recordIterator(),
                                                                             "ID");

            final Record r0 = records.get(0);
            assertNull(r0.getNumberValue("ID"));
            assertNull(r0.getNumberValue("MININT"));
            assertNull(r0.getNumberValue("MAXINT"));
            assertNull(r0.getNumberValue("MAXDEC"));
            assertNull(r0.getNumberValue("MINDEC"));

            final Record r1 = records.get(1);
            assertEquals(1,
                         r1.getNumberValue("ID").intValue());
            assertEquals(1,
                         r1.getNumberValue("MININT").intValue());
            assertEquals(1111111111111111040L,
                         r1.getNumberValue("MAXINT").longValue());
            assertEquals(111.1111111111111,
                         r1.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(1.1,
                         r1.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r2 = records.get(2);
            assertEquals(2,
                         r2.getNumberValue("ID").intValue());
            assertEquals(2,
                         r2.getNumberValue("MININT").intValue());
            assertEquals(2222222222222222080L,
                         r2.getNumberValue("MAXINT").longValue());
            assertEquals(222.2222222222222,
                         r2.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(2.2,
                         r2.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r3 = records.get(3);
            assertEquals(3,
                         r3.getNumberValue("ID").intValue());
            assertEquals(3,
                         r3.getNumberValue("MININT").intValue());
            assertEquals(3333333333333332992L,
                         r3.getNumberValue("MAXINT").longValue());
            assertEquals(333.33333333333334,
                         r3.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(3.3,
                         r3.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r4 = records.get(4);
            assertEquals(4,
                         r4.getNumberValue("ID").intValue());
            assertEquals(4,
                         r4.getNumberValue("MININT").intValue());
            assertEquals(4444444444444444160L,
                         r4.getNumberValue("MAXINT").longValue());
            assertEquals(444.4444444444444,
                         r4.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(4.4,
                         r4.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r5 = records.get(5);
            assertEquals(5,
                         r5.getNumberValue("ID").intValue());
            assertEquals(5,
                         r5.getNumberValue("MININT").intValue());
            assertEquals(5555555555555555328L,
                         r5.getNumberValue("MAXINT").longValue());
            assertEquals(555.5555555555555,
                         r5.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(5.5,
                         r5.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r6 = records.get(6);
            assertEquals(6,
                         r6.getNumberValue("ID").intValue());
            assertEquals(6,
                         r6.getNumberValue("MININT").intValue());
            assertEquals(6666666666666599424L,
                         r6.getNumberValue("MAXINT").longValue());
            assertEquals(666.66666666666668,
                         r6.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(6.6,
                         r6.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r7 = records.get(7);
            assertEquals(7,
                         r7.getNumberValue("ID").intValue());
            assertEquals(7,
                         r7.getNumberValue("MININT").intValue());
            assertEquals(7777777777777777664L,
                         r7.getNumberValue("MAXINT").longValue());
            assertEquals(777.777777777779,
                         r7.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(7.7,
                         r7.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r8 = records.get(8);
            assertEquals(8,
                         r8.getNumberValue("ID").intValue());
            assertEquals(8,
                         r8.getNumberValue("MININT").intValue());
            assertEquals(8888888888888888320L,
                         r8.getNumberValue("MAXINT").longValue());
            assertEquals(888.8888888888888,
                         r8.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(8.8,
                         r8.getNumberValue("MINDEC").doubleValue(),
                         0.0);

            final Record r9 = records.get(9);
            assertEquals(9,
                         r9.getNumberValue("ID").intValue());
            assertEquals(9,
                         r9.getNumberValue("MININT").intValue());
            assertEquals(9999999999999998L,
                         r9.getNumberValue("MAXINT").longValue());
            assertEquals(999.9999999999998,
                         r9.getNumberValue("MAXDEC").doubleValue(),
                         0.0);
            assertEquals(9.9,
                         r9.getNumberValue("MINDEC").doubleValue(),
                         0.0);
        }
        finally
        {
            number.close();
        }
    }

    @Test
    public void writing_maximal_and_minimal_values()
                                            throws IOException, DbfLibException
    {
        Ranges ignoredRanges = new Ranges();
        ignoredRanges.addRange(0x01, 0x03); // modified date
        ignoredRanges.addRange(0x1d, 0x1d); // language driver
        ignoredRanges.addRange(0x1e, 0x1f); // reserved
        ignoredRanges.addRange(0x23, 0x2a); // ignore garbage after 0 terminator of field name
                                            // string

        ignoredRanges.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRanges.addRange(0x4c, 0x4f); // idem
        ignoredRanges.addRange(0x6c, 0x6f); // idem
        ignoredRanges.addRange(0x8c, 0x8f); // idem
        ignoredRanges.addRange(0xac, 0xaf); // idem

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/types", "NUMBER", version, ignoredRanges, null);
    }
}
