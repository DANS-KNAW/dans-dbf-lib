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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests that the code used in the Usage page runs without throwing exceptions.
 *
 * @author Jan van Mansum
 */
public class TestUsageExamples
{
    private class DummyOutputStream
        extends OutputStream
    {
        @Override
        public void write(int b)
                   throws IOException
        {
        }
    }

    private static final File srcDir = new File("src/test/resources/dbase3plus/usage");
    private static final File outDir = new File("target/test-output/dbase3plus/usage");
    private static final String fileNameDbf = "MYTABLE.DBF";
    private static final String fileNameDbt = "MYTABLE.DBT";

    @Test
    public void test()
              throws IOException, DbfLibException
    {
        PrintStream out = System.out;

        try
        {
            /*
             * Sending the standard output to a black hole, because we do want the code to be easily
             * copy-pastable to the Usage page, but we don't want to see the output in this test.
             */
            System.setOut(new PrintStream(new DummyOutputStream()));

            UnitTestUtil.copyFile(new File(srcDir, fileNameDbf),
                                  outDir,
                                  fileNameDbf);
            UnitTestUtil.copyFile(new File(srcDir, fileNameDbt),
                                  outDir,
                                  fileNameDbt);

            Table table = new Table(new File(outDir, fileNameDbf));

            try
            {
                table.open(IfNonExistent.ERROR);

                List<Field> fields = table.getFields();

                for (final Field field : fields)
                {
                    System.out.println("Name:         " + field.getName());
                    System.out.println("Type:         " + field.getType());
                    System.out.println("Length:       " + field.getLength());
                    System.out.println("DecimalCount: " + field.getDecimalCount());
                    System.out.println();
                }

                Iterator<Record> iterator = table.recordIterator();

                while (iterator.hasNext())
                {
                    Record record = iterator.next();

                    Number nv = record.getNumberValue("NUMFLD");

                    // Convert to a primitive before comparing
                    if (nv != null && nv.intValue() == 2)
                    {
                        System.out.println("Well, what do you know? numfld was 2!");
                    }

                    // Get all the bytes from the memo field including
                    // "soft returns" (0x8d 0x0a)
                    byte[] memoData = record.getRawValue(new Field("MEMOFLD", Type.MEMO));

                    if (memoData == null)
                    {
                        System.out.println("No memo data");
                    }
                    else
                    {
                        System.out.println("Raw memo data: " + new String(memoData));
                    }

                    // Get the memo data as a String, with no soft returns.
                    String memoString = record.getStringValue("MEMOFLD");

                    System.out.println("Memo date as string: " + memoString);
                }

                // The hard way ...
                final Map<String, Value> map = new HashMap<String, Value>();
                map.put("NUMFLD",
                        new NumberValue(17));
                map.put("LOGICFLD",
                        new BooleanValue(true));
                map.put("CHARFLD",
                        new StringValue("This is a new string"));
                map.put("MEMOFLD",
                        new StringValue("This could be a very long string"));
                map.put("DATEFLD",
                        new DateValue(Calendar.getInstance().getTime()));

                Record record = new Record(map);
                table.addRecord(record);

                // The easy way ...
                // The values have to be of the appropriate type and in the same order as the
                // corresponding fields
                // in the list returned by Table.getFields();
                table.addRecord(18,
                                false,
                                "Another new string",
                                "Another long string",
                                Calendar.getInstance().getTime());

                // The even easier way ... (since beta 02)
                // The values do not have to be of the exact corresponding type as the database
                // field anymore.
                // Some conversions are done.
                table.addRecord("19", "T", 3.14159, 2.71828, "20090608");

                // Parses the string "19" as a NUMBER, Writes the String "T" (true) to a LOGICAL
                // field,
                // Writes pi to a CHARACTER field, writes e to a MEMO field and "20090608" to a DATE
                // field.

                // ... do your stuff
            }
            finally
            {
                table.close(); // don't forget to close it!
            }

            assertTrue(true);
        }
        finally
        {
            System.setOut(out);
        }
    }
}
