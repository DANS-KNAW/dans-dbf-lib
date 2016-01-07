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
package nl.knaw.dans.common.dbflib.example;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import nl.knaw.dans.common.dbflib.CorruptedTableException;
import nl.knaw.dans.common.dbflib.DbfLibException;
import nl.knaw.dans.common.dbflib.Field;
import nl.knaw.dans.common.dbflib.IfNonExistent;
import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;
import nl.knaw.dans.common.dbflib.ValueTooLargeException;

/*
 * Usage: java -cp dans-dbf-lib-version.jar:dump-table.jar nl.knaw.dans.common.example.DumpTable <table file>
 */
public class DumpTable
{
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("One argument required: table to dump");
            System.exit(1);
        }

        final Table table = new Table(new File(args[0]));
        
        try
        {
            table.open(IfNonExistent.ERROR);

            final Format dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            System.out.println("TABLE PROPERTIES");
            System.out.println("Name          : " + table.getName());
            System.out.println("Last Modified : " + dateFormat.format(table.getLastModifiedDate()));
            System.out.println("--------------");
            System.out.println();
            System.out.println("FIELDS (COLUMNS)");

            final List<Field> fields = table.getFields();

            for(final Field field: fields)
            {
                System.out.println("  Name       : " + field.getName());
                System.out.println("  Type       : " + field.getType());
                System.out.println("  Length     : " + field.getLength());
                System.out.println("  Dec. Count : " + field.getDecimalCount());
                System.out.println();
            }
            
            System.out.println("--------------");
            System.out.println();
            System.out.println("RECORDS");

            final Iterator<Record> recordIterator = table.recordIterator();
            int count = 0;

            while(recordIterator.hasNext())
            {
                final Record record = recordIterator.next();
                System.out.println(count++);

                for(final Field field: fields)
                {
                    try
                    {
                        byte[] rawValue = record.getRawValue(field);
                        System.out.println(field.getName() + " : " + (rawValue == null ? "<NULL>" : new String(rawValue)));
                    }
                    catch(ValueTooLargeException vtle)
                    {
                        // Cannot happen :)
                    }
                }

                System.out.println();
            }

            System.out.println("--------------");
        }
        catch(IOException ioe)
        {
            System.out.println("Trouble reading table or table not found");
            ioe.printStackTrace();
        }
        catch(DbfLibException dbflibException)
        {
            System.out.println("Problem getting raw value");
            dbflibException.printStackTrace();
        }
        finally
        {
            try
            {
                table.close();
            } catch (IOException ex)
            {
                System.out.println("Unable to close the table");
            }
        }
    }
}
