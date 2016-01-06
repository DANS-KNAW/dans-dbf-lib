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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests reading and writing boolean fields.
 *
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestBoolean
    extends BaseTestcase
{
    /**
     * Creates a new TestBoolean object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestBoolean(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    @Test
    public void readBoolean()
                     throws IOException, CorruptedTableException
    {
        final Table table = new Table(new File("src/test/resources/" + versionDirectory + "/types/BOOLEAN.DBF"));

        try
        {
            table.open(IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = table.recordIterator();

            Record r = recordIterator.next();
            assertEquals(true,
                         r.getBooleanValue("BOOLEAN"));

            r = recordIterator.next();
            assertEquals(false,
                         r.getBooleanValue("BOOLEAN"));
        }
        finally
        {
            table.close();
        }
    }

    @Test
    public void writeBoolean()
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
        ignoredRanges.addRange(0x25, 0x2a); // garbage
        ignoredRanges.addRange(0x32, 0x3f); // garbage
        ignoredRanges.addRange(0x48, 0x4a); // garbage
        ignoredRanges.addRange(0x52, 0x5f); // garbage

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/types", "BOOLEAN", version, ignoredRanges, null);
    }
}
