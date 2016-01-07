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

import org.junit.After;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests reading all supported data types.
 *
 * @author Jan van Mansum
 * @author Vesa Åkerman
 */
public class TestReadingDataTypesSimpleCase
{
    private Table cars = null;
    private List<Record> records = null;
    private Record r0 = null;
    private Record r1 = null;
    private Record r2 = null;
    private Record r3 = null;

    @Before
    public void setUp()
               throws IOException, CorruptedTableException
    {
        cars = new Table(new File("src/test/resources/dbase3plus/cars/cars.dbf"));
        cars.open();

        records =
            UnitTestUtil.createSortedRecordList(cars.recordIterator(),
                                                "YEAR");
        r0 = records.get(0);
        r1 = records.get(1);
        r2 = records.get(2);
        r3 = records.get(3);
    }

    @After
    public void tearDown()
                  throws IOException
    {
        cars.close();
    }

    @Test
    public void definitions()
                     throws FileNotFoundException, IOException, CorruptedTableException
    {
        final List<Field> fields = cars.getFields();

        Iterator<Field> fieldIterator = fields.iterator();
        Map<String, Field> nameFieldMap = new HashMap<String, Field>();

        while (fieldIterator.hasNext())
        {
            Field f = fieldIterator.next();
            nameFieldMap.put(f.getName(),
                             f);
        }

        final Field nameField = nameFieldMap.get("NAME");
        assertEquals(nameField.getType(),
                     Type.CHARACTER);
        assertEquals(nameField.getLength(),
                     15);

        final Field yearField = nameFieldMap.get("YEAR");
        assertEquals(yearField.getType(),
                     Type.NUMBER);
        assertEquals(yearField.getLength(),
                     4);

        final Field dateField = nameFieldMap.get("PROD_DATE");
        assertEquals(dateField.getType(),
                     Type.DATE);
        assertEquals(dateField.getLength(),
                     8);

        final Field logicalField = nameFieldMap.get("SEDAN");
        assertEquals(logicalField.getType(),
                     Type.LOGICAL);
        assertEquals(logicalField.getLength(),
                     1);

        final Field ccField = nameFieldMap.get("CC");
        assertEquals(ccField.getType(),
                     Type.NUMBER);
        assertEquals(ccField.getLength(),
                     7);

        final Field memoField = nameFieldMap.get("DESCR");
        assertEquals(memoField.getType(),
                     Type.MEMO);
        assertEquals(memoField.getLength(),
                     10);
    }

    @Test
    public void number()
    {
        assertEquals(null,
                     r0.getNumberValue("YEAR"));
        assertEquals(0,
                     r1.getNumberValue("YEAR"));
        assertEquals(1990,
                     r2.getNumberValue("YEAR"));
        assertEquals(2000,
                     r3.getNumberValue("YEAR"));

        assertEquals(null,
                     r0.getNumberValue("CC"));
        assertEquals(0.0,
                     r1.getNumberValue("CC"));
        assertEquals(999.999,
                     r2.getNumberValue("CC"));
        assertEquals(333.444,
                     r3.getNumberValue("CC"));
    }

    @Test
    public void character()
    {
        assertEquals("PASSAT",
                     r1.getStringValue("NAME").toString().trim());
        assertEquals("",
                     r0.getStringValue("NAME").toString().trim());
        assertEquals("JETTA",
                     r2.getStringValue("NAME").toString().trim());
        assertEquals("POLO",
                     r3.getStringValue("NAME").toString().trim());
    }

    @Test
    public void logical()
    {
        assertEquals(null,
                     r0.getBooleanValue("SEDAN"));
        assertEquals(false,
                     r1.getBooleanValue("SEDAN"));
        assertEquals(true,
                     r2.getBooleanValue("SEDAN"));
        assertEquals(false,
                     r3.getBooleanValue("SEDAN"));
    }

    @Test
    public void date()
    {
        assertEquals(null,
                     r0.getDateValue("PROD_DATE"));
        assertEquals(Util.createDate(1977, Calendar.JANUARY, 01),
                     r1.getDateValue("PROD_DATE"));
        assertEquals(Util.createDate(1990, Calendar.FEBRUARY, 12),
                     r2.getDateValue("PROD_DATE"));
        assertEquals(Util.createDate(1901, Calendar.DECEMBER, 03),
                     r3.getDateValue("PROD_DATE"));
    }

    @Test
    public void memo()
    {
        assertEquals(null,
                     r0.getStringValue("DESCR"));
        assertEquals("THIS IS A DESCRIPTION OF PASSAT",
                     r1.getStringValue("DESCR").trim().substring(0, 31));
        assertEquals("DESCRIPTION OF VOLKSAWGEN JETTA, YEAR MODEL 1990",
                     r2.getStringValue("DESCR").trim());
        assertEquals(null,
                     r3.getStringValue("DESCR"));
    }
}
