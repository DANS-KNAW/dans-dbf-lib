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
import java.io.IOException;
import java.util.Iterator;

/**
 * Tests read only access to files.
 *
 * @author Simon Chenery
 */
@RunWith(Parameterized.class)
public class TestReadOnly
    extends BaseTestcase
{
    /**
     * Creates a new TestReadOnly object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestReadOnly(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    /**
     * Tests opening and reading from a read-only file.
     */
    @Test
    public void openReadOnly()
                      throws IOException, CorruptedTableException
    {
        Table table = null;

        try
        {
            final File inputDir = new File("src/test/resources/dbase3plus/cars_readonly");
            final File outputDir = UnitTestUtil.recreateDirectory("target/cars_readonly");

            /*
             * Create read-only copy of files.
             */
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                                  outputDir,
                                  "cars.dbf");
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                                  outputDir,
                                  "cars.dbt");

            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbf"));
            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbt"));

            table = new Table(new File(outputDir, "cars.dbf"));
            table.open("r", IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = table.recordIterator();

            /*
             * Check that DBF file really is read.
             */
            recordIterator.next();

            Record r = recordIterator.next();
            assertEquals(2000,
                         r.getNumberValue("YEAR"));

            r = recordIterator.next();

            /*
             * Check that Memo file is also read.
             */
            assertEquals("DESCRIPTION OF VOLKSAWGEN JETTA, YEAR MODEL 1990",
                         r.getStringValue("DESCR"));
        }
        finally
        {
            if (table != null)
            {
                table.close();
            }
        }
    }

    @Test(expected = IOException.class)
    public void deleteRecordReadOnly()
                              throws IOException, CorruptedTableException
    {
        Table table = null;

        try
        {
            final File inputDir = new File("src/test/resources/dbase3plus/cars_readonly");
            final File outputDir = UnitTestUtil.recreateDirectory("target/cars_readonly");

            /*
             * Create read-only copy of files.
             */
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                                  outputDir,
                                  "cars.dbf");
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                                  outputDir,
                                  "cars.dbt");

            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbf"));
            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbt"));

            table = new Table(new File(outputDir, "cars.dbf"));
            table.open("r", IfNonExistent.ERROR);
            table.deleteRecordAt(0);
        }
        finally
        {
            if (table != null)
            {
                table.close();
            }
        }
    }

    @Test(expected = IOException.class)
    public void addRecordReadOnly()
                           throws IOException, DbfLibException
    {
        Table table = null;

        try
        {
            final File inputDir = new File("src/test/resources/dbase3plus/cars_readonly");
            final File outputDir = UnitTestUtil.recreateDirectory("target/cars_readonly");

            /*
             * Create read-only copy of files.
             */
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                                  outputDir,
                                  "cars.dbf");
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                                  outputDir,
                                  "cars.dbt");

            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbf"));
            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbt"));

            table = new Table(new File(outputDir, "cars.dbf"));
            table.open("r", IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = table.recordIterator();
            Record r = recordIterator.next();
            table.addRecord(r);
        }
        finally
        {
            if (table != null)
            {
                table.close();
            }
        }
    }

    @Test(expected = IOException.class)
    public void updateRecordReadOnly()
                              throws IOException, DbfLibException
    {
        Table table = null;

        try
        {
            final File inputDir = new File("src/test/resources/dbase3plus/cars_readonly");
            final File outputDir = UnitTestUtil.recreateDirectory("target/cars_readonly");

            /*
             * Create read-only copy of files.
             */
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                                  outputDir,
                                  "cars.dbf");
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                                  outputDir,
                                  "cars.dbt");

            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbf"));
            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbt"));

            table = new Table(new File(outputDir, "cars.dbf"));
            table.open("r", IfNonExistent.ERROR);

            final Iterator<Record> recordIterator = table.recordIterator();
            Record r = recordIterator.next();
            table.updateRecordAt(2, r);
        }
        finally
        {
            if (table != null)
            {
                table.close();
            }
        }
    }

    @Test(expected = IOException.class)
    public void packReadOnly()
                      throws IOException, DbfLibException
    {
        Table table = null;

        try
        {
            final File inputDir = new File("src/test/resources/dbase3plus/cars_readonly");
            final File outputDir = UnitTestUtil.recreateDirectory("target/cars_readonly");

            /*
             * Create read-only copy of files.
             */
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                                  outputDir,
                                  "cars.dbf");
            UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                                  outputDir,
                                  "cars.dbt");

            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbf"));
            assertTrue(UnitTestUtil.setReadOnly(outputDir, "cars.dbt"));

            table = new Table(new File(outputDir, "cars.dbf"));
            table.open("r", IfNonExistent.ERROR);
            table.pack();
        }
        finally
        {
            if (table != null)
            {
                table.close();
            }
        }
    }
}
