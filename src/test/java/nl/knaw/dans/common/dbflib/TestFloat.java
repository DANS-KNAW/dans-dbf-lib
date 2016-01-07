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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Tests reading FLOAT fields.
 *
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestFloat
    extends BaseTestcase
{
    /**
     * Creates a new TestFloat object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestFloat(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    /**
     * No dBase III+, because it has no FLOAT type.
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        final Object[][] testParameters =
            new Object[][]
            {
                { Version.DBASE_4, "dbase4" },
                { Version.DBASE_5, "dbase5" },
                { Version.CLIPPER_5, "clipper5" },
                { Version.FOXPRO_26, "foxpro26" }
            };

        return Arrays.asList(testParameters);
    }

    @Test
    public void readFloat()
                   throws IOException, CorruptedTableException
    {
        final Table t1 = new Table(new File("src/test/resources/" + versionDirectory + "/types/FLOAT.DBF"));

        try
        {
            t1.open(IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = t1.recordIterator();

            Record r = recordIterator.next();

            assertEquals(1,
                         r.getNumberValue("FLOAT1").intValue());
            assertEquals(1234567890123450000L,
                         r.getNumberValue("FLOAT2").longValue());
            assertEquals(1.111111111111110000,
                         r.getNumberValue("FLOAT3").doubleValue(),
                         0.0);
            assertEquals(4.44444444,
                         r.getNumberValue("FLOAT4").doubleValue(),
                         0.0);

            r = recordIterator.next();
            assertEquals(9,
                         r.getNumberValue("FLOAT1").intValue());
            assertEquals(999999999999990000L,
                         r.getNumberValue("FLOAT2").longValue());
            assertEquals(9.999999999990000000,
                         r.getNumberValue("FLOAT3").doubleValue(),
                         0.0);
            assertEquals(9.99999999,
                         r.getNumberValue("FLOAT4").doubleValue(),
                         0.0);

            r = recordIterator.next();
            assertNull(r.getNumberValue("FLOAT1"));
            assertNull(r.getNumberValue("FLOAT2"));
            assertNull(r.getNumberValue("FLOAT3"));
            assertNull(r.getNumberValue("FLOAT4"));

            r = recordIterator.next();
            assertEquals(0,
                         r.getNumberValue("FLOAT1").intValue());

            // In Clipper5 maximum length 19 digits
            if (version == Version.CLIPPER_5)
            {
                assertEquals(1000000000000000000L,
                             r.getNumberValue("FLOAT2").longValue());
            }
            else
            {
                assertEquals(new BigInteger("10000000000000000000"),
                             (BigInteger) (r.getNumberValue("FLOAT2")));
            }

            assertEquals(5.555555555555560000,
                         r.getNumberValue("FLOAT3").doubleValue(),
                         0.0);
            assertEquals(0.00000000,
                         r.getNumberValue("FLOAT4").doubleValue(),
                         0.0);
        }
        finally
        {
            t1.close();
        }
    }

    @Test
    public void writeFloat()
                    throws IOException, DbfLibException
    {
        final Ranges ignoredRanges = new Ranges();
        ignoredRanges.addRange(0x01, 0x03); // modified
        ignoredRanges.addRange(0x1d, 0x1d); // language driver
        ignoredRanges.addRange(0x1e, 0x1f); // reserved
        ignoredRanges.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRanges.addRange(0x4c, 0x4f); // field description "address in memory"
        ignoredRanges.addRange(0x6c, 0x6f); // field description "address in memory"
        ignoredRanges.addRange(0x8c, 0x8f); // field description "address in memory"
        ignoredRanges.addRange(0xac, 0xaf); // field description "address in memory"
        ignoredRanges.addRange(0x25, 0x2a); // garbage at the end of Field Name field
        ignoredRanges.addRange(0x47, 0x4a); // garbage at the end of Field Name field

        /*
         * Garbage in Clipper 5, in other versions not meaningful.
         */
        ignoredRanges.addRange(0x32, 0x3f); // garbage
        ignoredRanges.addRange(0x52, 0x5f); // garbage
        ignoredRanges.addRange(0x67, 0x6a); // garbage
        ignoredRanges.addRange(0x72, 0x7f); // garbage
        ignoredRanges.addRange(0x87, 0x8a); // garbage
        ignoredRanges.addRange(0x92, 0x9f); // garbage
        ignoredRanges.addRange(0xa7, 0xaa); // garbage
        ignoredRanges.addRange(0xb2, 0xbf); // garbage

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/types", "FLOAT", version, ignoredRanges, null);
    }
}
