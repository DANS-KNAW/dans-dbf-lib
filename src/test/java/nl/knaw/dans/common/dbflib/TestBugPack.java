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

public class TestBugPack
{
    @Test
    public void deleteAndPack()
                       throws Exception
    {
        UnitTestUtil.copyFile(new File("src/test/resources/bug_pack/test.DBF"),
                              new File("target/bug_pack"),
                              "test.DBF");

        Table table = new Table(new File("target/bug_pack/test.DBF"));

        try
        {
            table.open();
            table.deleteRecordAt(0);
            table.pack();

            Record r0 = table.getRecordAt(0);
            Record r1 = table.getRecordAt(1);
            assertEquals("0",
                         r0.getStringValue("NAZWA"));
            assertEquals("1",
                         r1.getStringValue("NAZWA"));
        }
        finally
        {
            table.close();
        }
    }
}
