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
