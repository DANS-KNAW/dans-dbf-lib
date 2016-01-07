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

import java.io.File;
import java.util.Iterator;

/**
 * Tests that it is possible to read a Russian DBF if the correct charset is specified.
 *
 * @author Jan van Mansum.
 */
public class TestRussianCharset
{
    @Test
    public void shouldReadRussianDbf()
                              throws Exception
    {
        final Table table = new Table(new File("src/test/resources/russian_foxpro/rus.dbf"),
                                      "IBM866");

        table.open();

        try
        {
            final Iterator<Record> recordIterator = table.recordIterator();

            final Record record1 = getNextRecord(recordIterator);
            assertEquals("Рефакторинг. Улучшение существующего кода",
                         record1.getStringValue("BOOK").trim());
            assertEquals("Мартин Фаулер,Кент Бек,Джон Браун,Вильям Апдайк,Дон Робертс",
                         record1.getStringValue("AUTHOR").trim());
            assertEquals("Addison-Wesley Professional",
                         record1.getStringValue("PUBLISHER").trim());

            final Record record2 = getNextRecord(recordIterator);
            assertEquals("Регулярные выражения. Сборник рецептов",
                         record2.getStringValue("BOOK").trim());
            assertEquals("Ян Гойвертс,Стивет Ливетан",
                         record2.getStringValue("AUTHOR").trim());
            assertEquals("O'Reilly Media",
                         record2.getStringValue("PUBLISHER").trim());
        }
        finally
        {
            table.close();
        }
    }

    private static Record getNextRecord(Iterator<Record> recordIterator)
    {
        assertTrue("Missing record in dataset",
                   recordIterator.hasNext());

        return recordIterator.next();
    }
}
