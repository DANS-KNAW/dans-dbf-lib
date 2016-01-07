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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests reading and writing memo fields.
 *
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestMemo
    extends BaseTestcase
{
    /**
     * Creates a new TestMemo object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestMemo(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    @Test
    public void readMemo()
                  throws FileNotFoundException, IOException, CorruptedTableException
    {
        final Table t1 = new Table(new File("src/test/resources/" + versionDirectory + "/types/MEMOTEST.DBF"));

        try
        {
            t1.open(IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = t1.recordIterator();

            Record r = recordIterator.next();
            assertEquals("m",
                         r.getStringValue("MEMO"));

            r = recordIterator.next();
            assertEquals(null,
                         r.getStringValue("MEMO"));

            r = recordIterator.next();
            assertEquals("This is a very long memo",
                         r.getStringValue("MEMO").substring(0, 24));

            r = recordIterator.next();

            assertEquals("1234567890",
                         r.getStringValue("MEMO").substring(0, 10));
        }
        finally
        {
            t1.close();
        }
    }

    @Test
    public void writeMemo()
                   throws IOException, DbfLibException
    {
        final Ranges ignoredRangesDbf = new Ranges();
        ignoredRangesDbf.addRange(0x01, 0x03); // modified
        ignoredRangesDbf.addRange(0x1d, 0x1d); // language driver
        ignoredRangesDbf.addRange(0x1e, 0x1f); // ???
        ignoredRangesDbf.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRangesDbf.addRange(0x34, 0x34); // work area id
        ignoredRangesDbf.addRange(0x4c, 0x4f); // field description "address in memory"
        ignoredRangesDbf.addRange(0x54, 0x54); // work area id

        if (version == Version.FOXPRO_26)
        {
            ignoredRangesDbf.addRange(0x78, 0x81); // block number in memo file
                                                   // (in some versions padded with zeros, in other versions with spaces)

            ignoredRangesDbf.addRange(0xba, 0xc3); // idem
            ignoredRangesDbf.addRange(0xdb, 0xe4); // idem
        }

        /*
         * Garbage in Clipper 5, in other versions not meaningful.
         */
        ignoredRangesDbf.addRange(0x26, 0x2a); // garbage
        ignoredRangesDbf.addRange(0x32, 0x3f); // garbage
        ignoredRangesDbf.addRange(0x46, 0x4a); // garbage
        ignoredRangesDbf.addRange(0x52, 0x5f); // garbage

        final Ranges ignoredRangesDbt = new Ranges();

        if (version == Version.DBASE_3)
        {
            ignoredRangesDbt.addRange(0x04, 0x1ff); // reserved/garbage
        }
        else if (version == Version.DBASE_4)
        {
            ignoredRangesDbt.addRange(0xcbf, 0xdff); // end of the block garbage
            ignoredRangesDbt.addRange(0x1005, 0x11ff); // end of the block garbage
        }
        else if (version == Version.DBASE_5)
        {
            ignoredRangesDbt.addRange(0x16, 0x1ff); // reserved/garbage
        }
        else if (version == Version.CLIPPER_5)
        {
            ignoredRangesDbt.addRange(0x04, 0x3ff); // reserved/garbage
            ignoredRangesDbt.addRange(0x402, 0x5ff); // reserved/garbage
            ignoredRangesDbt.addRange(0xe9c, 0xfff); // reserved/garbage
            ignoredRangesDbt.addRange(0x11fe, 0x11ff); // zero padding
        }
        else if (version == Version.FOXPRO_26)
        {
            ignoredRangesDbt.addRange(0x08, 0x0f); // file name (not written in FoxPro)
            ignoredRangesDbt.addRange(0xc7d, 0xdff); // garbage
            ignoredRangesDbt.addRange(0x1005, 0x11ff); // garbage
        }

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/types", "MEMOTEST", version, ignoredRangesDbf,
                                          ignoredRangesDbt);
    }
}
