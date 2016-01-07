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

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.util.Iterator;

public class TestPack
{
    @Test
    public void testPack()
                  throws Exception
    {
        final File inputDir = new File("src/test/resources/dbase3plus/cars_del");
        final File outputDir = UnitTestUtil.recreateDirectory("target/cars_del");

        UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                              outputDir,
                              "cars.dbf");
        UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                              outputDir,
                              "cars.dbt");

        Table table = new Table(new File(outputDir, "cars.dbf"));

        table.open(IfNonExistent.ERROR);
        assertEquals("Stored record count before pack incorrect",
                     4,
                     table.getRecordCount());

        Iterator<Record> iterator = table.recordIterator();

        int counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count before pack incorrect (deleted records excluded) ", 3, counter);

        iterator = table.recordIterator(true);

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count before pack incorrect (deleted records included) ", 4, counter);

        table.pack();

        assertEquals("Stored record count after pack incorrect",
                     3,
                     table.getRecordCount());
        iterator = table.recordIterator();

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count after pack incorrect (deleted records excluded) ", 3, counter);

        iterator = table.recordIterator(true);

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count after pack incorrect (deleted records included) ", 3, counter);
    }

    @Test
    public void testPack2()
                   throws Exception
    {
        final File inputDir = new File("src/test/resources/dbase3plus/cars_del2");
        final File outputDir = UnitTestUtil.recreateDirectory("target/cars_del2");

        UnitTestUtil.copyFile(new File(inputDir, "cars.dbf"),
                              outputDir,
                              "cars.dbf");
        UnitTestUtil.copyFile(new File(inputDir, "cars.dbt"),
                              outputDir,
                              "cars.dbt");

        Table table = new Table(new File(outputDir, "cars.dbf"));

        table.open(IfNonExistent.ERROR);
        assertEquals("Stored record count before pack incorrect",
                     4,
                     table.getRecordCount());

        Iterator<Record> iterator = table.recordIterator();

        int counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count before pack incorrect (deleted records excluded) ", 0, counter);

        iterator = table.recordIterator(true);

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count before pack incorrect (deleted records included) ", 4, counter);

        table.pack();

        assertEquals("Stored record count after pack incorrect",
                     0,
                     table.getRecordCount());
        iterator = table.recordIterator();

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count after pack incorrect (deleted records excluded) ", 0, counter);

        iterator = table.recordIterator(true);

        counter = 0;

        while (iterator.hasNext())
        {
            iterator.next();
            ++counter;
        }

        assertEquals("Counted record count after pack incorrect (deleted records included) ", 0, counter);
    }
}
