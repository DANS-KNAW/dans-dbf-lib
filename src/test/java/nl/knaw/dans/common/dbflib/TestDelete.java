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

public class TestDelete
{
    @Test
    public void testDelete()
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
        assertFalse(table.getRecordAt(0).isMarkedDeleted());
        assertTrue(table.getRecordAt(1).isMarkedDeleted());
        assertFalse(table.getRecordAt(2).isMarkedDeleted());
        assertFalse(table.getRecordAt(3).isMarkedDeleted());

        table.deleteRecordAt(0);
        assertTrue(table.getRecordAt(0).isMarkedDeleted());
        assertTrue(table.getRecordAt(1).isMarkedDeleted());
        assertFalse(table.getRecordAt(2).isMarkedDeleted());
        assertFalse(table.getRecordAt(3).isMarkedDeleted());

        table.pack();

        assertFalse(table.getRecordAt(0).isMarkedDeleted());
        assertFalse(table.getRecordAt(1).isMarkedDeleted());
        assertEquals(2,
                     table.getRecordCount());

        table.close();
    }
}
