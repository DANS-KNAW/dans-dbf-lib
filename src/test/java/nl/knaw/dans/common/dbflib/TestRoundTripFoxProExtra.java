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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Does roundtrip tests of the library, i.e. touches all the functionality, but does not go very
 * deep. This roundtrip is only for FoxPro. The reason for adding this extra roundtrip is that the
 * other test files for Foxpro are files that are made with dBase (memo file is converted to format
 * that FoxPro uses). To make sure to cover possible differences between the .dbf files created with
 * dBase and the files created with FoxPro, we have another roundtrip test, using files that are
 * made with FoxPro.
 *
 * @author Vesa Åkerman
 */
public class TestRoundTripFoxProExtra
{
    /**
     * A short roundtrip of the library. Covers:
     * <ul>
     * <li>Opening a database</li>
     * <li>Retrieving the table names</li>
     * <li>Checking last modified date</li>
     * <li>Retrieving fields of one table</li>
     * <li>Retrieving records of one table</li>
     * </ul>
     */
    @Test
    public void reading()
                 throws FileNotFoundException, IOException, CorruptedTableException
    {
        final Database database =
            new Database(new File("src/test/resources/foxpro26/foxprospecial"), Version.FOXPRO_26);
        final Set<String> tableNames = database.getTableNames();

        assertEquals(1,
                     tableNames.size());
        assertTrue("FPROALL not found in 'short roundtrip' database",
                   tableNames.contains("FPROALL.DBF"));

        final Table t1 = database.getTable("FPROALL.DBF");

        try
        {
            t1.open(IfNonExistent.ERROR);

            assertEquals("Table name incorrect",
                         "FPROALL.DBF",
                         t1.getName());
            assertEquals("Last modified date incorrect",
                         Util.createDate(2009, Calendar.JUNE, 23),
                         t1.getLastModifiedDate());

            final List<Field> fields = t1.getFields();

            Iterator<Field> fieldIterator = fields.iterator();
            Map<String, Field> nameFieldMap = new HashMap<String, Field>();

            while (fieldIterator.hasNext())
            {
                Field f = fieldIterator.next();
                nameFieldMap.put(f.getName(),
                                 f);
            }

            final Field stringField = nameFieldMap.get("CHAR");
            assertEquals(Type.CHARACTER,
                         stringField.getType());
            assertEquals(stringField.getLength(),
                         20);

            final Field logicField = nameFieldMap.get("BOOLEAN");
            assertEquals(Type.LOGICAL,
                         logicField.getType());
            assertEquals(logicField.getLength(),
                         1);

            final Field idField = nameFieldMap.get("NUMERIC");
            assertEquals(Type.NUMBER,
                         idField.getType());
            assertEquals(idField.getLength(),
                         20);

            final Field floatField = nameFieldMap.get("FLOAT");
            assertEquals(Type.FLOAT,
                         floatField.getType());
            assertEquals(20,
                         floatField.getLength());

            final Field dateField = nameFieldMap.get("DATE");
            assertEquals(Type.DATE,
                         dateField.getType());
            assertEquals(8,
                         dateField.getLength());

            final Field memoField = nameFieldMap.get("MEMO");
            assertEquals(Type.MEMO,
                         memoField.getType());
            assertEquals(10,
                         memoField.getLength());

            final Field generalField = nameFieldMap.get("GENERAL");
            assertEquals(Type.GENERAL,
                         generalField.getType());
            assertEquals(10,
                         generalField.getLength());

            final Iterator<Record> recordIterator = t1.recordIterator();

            Record r = recordIterator.next();

            assertEquals("first character",
                         r.getStringValue("CHAR").trim());
            assertEquals(true,
                         r.getBooleanValue("BOOLEAN"));
            assertEquals(12.3456789012345000,
                         r.getNumberValue("NUMERIC").doubleValue(),
                         0.0);
            assertEquals(111.111111111111100,
                         r.getNumberValue("FLOAT").doubleValue(),
                         0.0);
            assertEquals(Util.createDate(1999, Calendar.JUNE, 10),
                         r.getDateValue("DATE"));
            assertEquals("This is a string of characters in memo file",
                         r.getStringValue("MEMO"));
            assertEquals(null,
                         r.getStringValue("GENERAL"));

            r = recordIterator.next();

            assertEquals("Another string",
                         r.getStringValue("CHAR").trim());
            assertEquals(false,
                         r.getBooleanValue("BOOLEAN"));
            assertEquals(100.00000000000000000,
                         r.getNumberValue("NUMERIC").doubleValue(),
                         0.0);
            assertEquals(10000.000000000000000,
                         r.getNumberValue("FLOAT").doubleValue(),
                         0.0);
            assertEquals(Util.createDate(1912, Calendar.DECEMBER, 12),
                         r.getDateValue("DATE"));
            assertEquals("This is another string of characters in memo",
                         r.getStringValue("MEMO"));
            assertEquals(null,
                         r.getStringValue("GENERAL"));

            r = recordIterator.next();

            assertEquals("Aa",
                         r.getStringValue("CHAR").trim());
            assertEquals(null,
                         r.getBooleanValue("BOOLEAN"));
            assertEquals(77.77777777777777000,
                         r.getNumberValue("NUMERIC").doubleValue(),
                         0.0);
            assertEquals(1010.101010101010000,
                         r.getNumberValue("FLOAT").doubleValue(),
                         0.0);
            assertEquals(Util.createDate(1901, Calendar.JANUARY, 1),
                         r.getDateValue("DATE"));
            assertEquals(null,
                         r.getStringValue("MEMO"));
            assertEquals(null,
                         r.getStringValue("GENERAL"));

            r = recordIterator.next();

            assertEquals("",
                         r.getStringValue("CHAR").trim());
            assertEquals(null,
                         r.getBooleanValue("BOOLEAN"));
            assertNull(r.getNumberValue("NUMERIC"));
            assertNull(r.getNumberValue("FLOAT"));
            assertNull(r.getDateValue("DATE"));
            assertNull(r.getStringValue("MEMO"));
            assertNull(r.getStringValue("GENERAL"));
        }
        finally
        {
            t1.close();
        }
    }

    /**
     * Tests writing to a table.
     *
     * @throws IOException not expected
     * @throws CorruptedTableException not expected
     */
    @Test
    public void writing()
                 throws IOException, DbfLibException
    {
        final Ranges ignoredRangesDbf = new Ranges();
        ignoredRangesDbf.addRange(0x01, 0x03); // modified
        ignoredRangesDbf.addRange(0x1d, 0x1d); // language driver
        ignoredRangesDbf.addRange(0x1e, 0x1f); // reserved
        ignoredRangesDbf.addRange(0x2c, 0x2f); // field description "address in memory"
        ignoredRangesDbf.addRange(0x4c, 0x4f); // idem
        ignoredRangesDbf.addRange(0x6c, 0x6f); // idem
        ignoredRangesDbf.addRange(0x8c, 0x8f); // idem
        ignoredRangesDbf.addRange(0xac, 0xaf); // idem
        ignoredRangesDbf.addRange(0xcc, 0xcf); // idem
        ignoredRangesDbf.addRange(0xec, 0xef); // idem
        ignoredRangesDbf.addRange(0x34, 0x34); // work area id
        ignoredRangesDbf.addRange(0x54, 0x54); // work area id
        ignoredRangesDbf.addRange(0x74, 0x74); // work area id
        ignoredRangesDbf.addRange(0x94, 0x94); // work area id
        ignoredRangesDbf.addRange(0xb4, 0xb4); // work area id
        ignoredRangesDbf.addRange(0xd4, 0xd4); // work area id
        ignoredRangesDbf.addRange(0xf4, 0xf4); // work area id
        ignoredRangesDbf.addRange(0x150, 0x150); // different block size in input and output file
        ignoredRangesDbf.addRange(0x1aa, 0x1aa); // idem

        final Ranges ignoredRangesDbt = new Ranges();
        ignoredRangesDbt.addRange(0x00, 0x03); // different next available block
        ignoredRangesDbt.addRange(0x06, 0x07); // different block size
        ignoredRangesDbt.addRange(0x08, 0x0f); // file name not in original file
        ignoredRangesDbt.addRange(0x240, 0x5ff); // because of different block sizes, comparison can
                                                 // be done only till first memo

        UnitTestUtil.doCopyAndCompareTest("foxpro26/foxprospecial", "FPROALL", Version.FOXPRO_26, ignoredRangesDbf,
                                          ignoredRangesDbt);
    }
}
