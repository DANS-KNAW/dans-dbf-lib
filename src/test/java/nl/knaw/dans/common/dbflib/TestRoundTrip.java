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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

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
 * deep.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestRoundTrip
    extends BaseTestcase
{
    /**
     * Creates a new TestRoundTrip object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestRoundTrip(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

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
            new Database(new File("src/test/resources/" + versionDirectory + "/rndtrip"), version);
        final Set<String> tableNames = database.getTableNames();

        assertEquals(2,
                     tableNames.size());
        assertTrue("TABLE1 not found in 'short roundtrip' database",
                   tableNames.contains("TABLE1.DBF"));
        assertTrue("TABLE2 not found in 'short roundtrip' database",
                   tableNames.contains("TABLE2.DBF"));

        final Table t1 = database.getTable("TABLE1.DBF");

        try
        {
            t1.open(IfNonExistent.ERROR);

            assertEquals("Table name incorrect",
                         "TABLE1.DBF",
                         t1.getName());
            assertEquals("Last modified date incorrect",
                         Util.createDate(2009, Calendar.APRIL, 1),
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

            final Field idField = nameFieldMap.get("ID");
            assertEquals(Type.NUMBER,
                         idField.getType());
            assertEquals(idField.getLength(),
                         3);

            final Field stringField = nameFieldMap.get("STRFIELD");
            assertEquals(Type.CHARACTER,
                         stringField.getType());
            assertEquals(stringField.getLength(),
                         50);

            final Field logicField = nameFieldMap.get("LOGICFIELD");
            assertEquals(Type.LOGICAL,
                         logicField.getType());
            assertEquals(logicField.getLength(),
                         1);

            final Field dateField = nameFieldMap.get("DATEFIELD");
            assertEquals(Type.DATE,
                         dateField.getType());
            assertEquals(8,
                         dateField.getLength());

            final Field floatField = nameFieldMap.get("FLOATFIELD");
            assertEquals(Type.NUMBER,
                         floatField.getType());
            assertEquals(10,
                         floatField.getLength());

            final List<Record> records = UnitTestUtil.createSortedRecordList(t1.recordIterator(),
                                                                             "ID");
            final Record r0 = records.get(0);

            assertEquals(1,
                         r0.getNumberValue("ID"));
            assertEquals("String data 01",
                         r0.getStringValue("STRFIELD").trim());
            assertEquals(true,
                         r0.getBooleanValue("LOGICFIELD"));
            assertEquals(Util.createDate(1909, Calendar.MARCH, 18),
                         r0.getDateValue("DATEFIELD"));
            assertEquals(1234.56,
                         r0.getNumberValue("FLOATFIELD"));

            final Record r1 = records.get(1);

            assertEquals(2,
                         r1.getNumberValue("ID"));
            assertEquals("String data 02",
                         r1.getStringValue("STRFIELD").trim());

            /*
             * in Clipper 'false' value can be given as an empty field, and the method called here
             * returns then 'null' as the return value
             */
            if (version == Version.CLIPPER_5)
            {
                assertEquals(null,
                             r1.getBooleanValue("LOGICFIELD"));
            }
            else
            {
                assertEquals(false,
                             r1.getBooleanValue("LOGICFIELD"));
            }

            assertEquals(Util.createDate(1909, Calendar.MARCH, 20),
                         r1.getDateValue("DATEFIELD"));
            assertEquals(-23.45,
                         r1.getNumberValue("FLOATFIELD"));

            final Record r2 = records.get(2);

            assertEquals(3,
                         r2.getNumberValue("ID"));
            assertEquals("",
                         r2.getStringValue("STRFIELD").trim());
            assertEquals(null,
                         r2.getBooleanValue("LOGICFIELD"));
            assertEquals(null,
                         r2.getDateValue("DATEFIELD"));
            assertEquals(null,
                         r2.getNumberValue("FLOATFIELD"));

            final Record r3 = records.get(3);

            assertEquals(4,
                         r3.getNumberValue("ID"));
            assertEquals("Full5678901234567890123456789012345678901234567890",
                         r3.getStringValue("STRFIELD").trim());

            /*
             * in Clipper 'false' value can be given as an empty field, and the method called here
             * returns then 'null' as the return value
             */
            if (version == Version.CLIPPER_5)
            {
                assertEquals(null,
                             r3.getBooleanValue("LOGICFIELD"));
            }
            else
            {
                assertEquals(false,
                             r3.getBooleanValue("LOGICFIELD"));
            }

            assertEquals(Util.createDate(1909, Calendar.MARCH, 20),
                         r3.getDateValue("DATEFIELD"));
            assertEquals(-0.30,
                         r3.getNumberValue("FLOATFIELD"));
        }
        finally
        {
            t1.close();
        }

        final Table t2 = database.getTable("TABLE2.DBF");

        try
        {
            t2.open(IfNonExistent.ERROR);

            final List<Field> fields = t2.getFields();

            Iterator<Field> fieldIterator = fields.iterator();
            Map<String, Field> nameFieldMap = new HashMap<String, Field>();

            while (fieldIterator.hasNext())
            {
                Field f = fieldIterator.next();
                nameFieldMap.put(f.getName(),
                                 f);
            }

            final Field idField = nameFieldMap.get("ID2");
            assertEquals(Type.NUMBER,
                         idField.getType());
            assertEquals(idField.getLength(),
                         4);

            final Field stringField = nameFieldMap.get("MEMOFIELD");
            assertEquals(Type.MEMO,
                         stringField.getType());
            assertEquals(10,
                         stringField.getLength());

            final Iterator<Record> recordIterator = t2.recordIterator();
            final Record r = recordIterator.next();

            String declarationOfIndependence = "";

            declarationOfIndependence += "When in the Course of human events it becomes necessary for one people ";
            declarationOfIndependence += "to dissolve the political bands which have connected them with another and ";
            declarationOfIndependence += "to assume among the powers of the earth, the separate and equal station to ";
            declarationOfIndependence += "which the Laws of Nature and of Nature's God entitle them, a decent respect ";
            declarationOfIndependence += "to the opinions of mankind requires that they should declare the causes which ";
            declarationOfIndependence += "impel them to the separation.";
            declarationOfIndependence += "\r\n\r\n";
            declarationOfIndependence += "We hold these truths to be self-evident, that all men are created equal, ";
            declarationOfIndependence += "that they are endowed by their Creator with certain unalienable Rights, ";
            declarationOfIndependence += "that among these are Life, Liberty and the persuit of Happiness.";

            assertEquals(1,
                         r.getNumberValue("ID2"));
            assertEquals(declarationOfIndependence,
                         r.getStringValue("MEMOFIELD"));
        }
        finally
        {
            t2.close();
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
        ignoredRangesDbf.addRange(0x34, 0x34); // work area id
        ignoredRangesDbf.addRange(0x54, 0x54); // work area id
        ignoredRangesDbf.addRange(0x74, 0x74); // work area id
        ignoredRangesDbf.addRange(0x94, 0x94); // work area id
        ignoredRangesDbf.addRange(0xb4, 0xb4); // work area id
        ignoredRangesDbf.addRange(0xd4, 0xd4); // work area id
        ignoredRangesDbf.addRange(0x105, 0x10e); // block number in memo file
                                                 // (in some versions padded with zeros, in other versions with spaces)

        ignoredRangesDbf.addRange(0x161, 0x16a); // idem

        /*
         * in Clipper5 there is so much garbage in the header area that from the field definitions
         * on all the data is skipped
         */
        if (version == Version.CLIPPER_5)
        {
            ignoredRangesDbf.addRange(0x20, 0xdf); // reserved/garbage
        }

        final Ranges ignoredRangesDbt = new Ranges();

        if (version == Version.DBASE_3)
        {
            ignoredRangesDbt.addRange(0x04, 0x1ff); // reserved/garbage
            ignoredRangesDbt.addRange(0x432, 0x5ff); // zero padding beyond dbase eof bytes
        }
        else if (version == Version.DBASE_4)
        {
            ignoredRangesDbt.addRange(0x438, 0x5ff); // zero padding beyond dbase eof bytes
        }
        else if (version == Version.DBASE_5)
        {
            ignoredRangesDbt.addRange(0x16, 0x1ff); // reserved/garbage
        }
        else if (version == Version.CLIPPER_5)
        {
            ignoredRangesDbt.addRange(0x04, 0x3ff); // reserved/garbage
            ignoredRangesDbt.addRange(0x4f5, 0x5ff); // garbage beyond eof bytes
            ignoredRangesDbt.addRange(0x631, 0x7ff); // zero padding beyond eof bytes
        }
        else if (version == Version.FOXPRO_26)
        {
            ignoredRangesDbt.addRange(0x08, 0x0f); // file name (not written in FoxPro)
            ignoredRangesDbt.addRange(0x2f6, 0x2fb); // garbage
            ignoredRangesDbt.addRange(0x438, 0x4fb); // garbage
        }

        UnitTestUtil.doCopyAndCompareTest(versionDirectory + "/cars", "cars", version, ignoredRangesDbf,
                                          ignoredRangesDbt);
    }
}
