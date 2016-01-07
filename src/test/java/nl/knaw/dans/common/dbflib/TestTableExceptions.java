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

import org.junit.Test;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests table related exceptions.
 *
 * @author Vesa Åkerman
 * @author Jan van Mansum
 */
public class TestTableExceptions
{
    /**
     * Tests that <tt>java.io.FileNotFoundException</tt> is thrown when opening a non-existent
     * table.
     *
     * @throws IOException expected
     * @throws CorruptedTableException not expected
     */
    @Test(expected = FileNotFoundException.class)
    public void nonExistingFile()
                         throws IOException, CorruptedTableException
    {
        final Table table = new Table(new File("NONEXISTENT.DBF"));

        try
        {
            table.open(IfNonExistent.ERROR);
        }
        finally
        {
            table.close();
        }
    }

    // TODO: Would it not be more appropriate to throw a CorruptedTableException?
    /**
     * Tests that an <tt>java.io.EOFException</tt> occurs when opening an empty DBF.
     *
     * @throws IOException expected
     * @throws CorruptedTableException not expected
     */
    @Test(expected = EOFException.class)
    public void emptyFile()
                   throws IOException, CorruptedTableException
    {
        final File emptyFile = new File("src/test/resources/dbase3plus/tableExceptions/EMPTY.DBF");
        final Table table = new Table(emptyFile);

        try
        {
            table.open(IfNonExistent.ERROR);
        }
        finally
        {
            table.close();
        }
    }

    // TODO: Would it not be more appropriate to open the memo on table.open and throw a
    // CorruptedTableException right there if the DBT is not found?
    /**
     * Tests that a <tt>RuntimeException</tt> is thrown when the DBT of a DBF file is missing.
     *
     * @throws IOException not expected
     * @throws CorruptedTableException not expected
     */
    @Test(expected = RuntimeException.class)
    public void memoFileMissing()
                         throws IOException, CorruptedTableException
    {
        final File missingMemoDbf = new File("src/test/resources/dbase3plus/tableExceptions/MISSMEMO.DBF");
        final Table table = new Table(missingMemoDbf);

        try
        {
            table.open(IfNonExistent.ERROR);
            table.recordIterator().next();
        }
        finally
        {
            table.close();
        }
    }

    @Test(expected = RuntimeException.class)
    public void emptyMemoFile()
                       throws IOException, CorruptedTableException
    {
        final File memEmptyDbf = new File("src/test/resources/dbase3plus/tableExceptions/MEMEMPTY.DBF");
        final Table table = new Table(memEmptyDbf);

        try
        {
            table.open(IfNonExistent.ERROR);
            table.recordIterator().next();
        }
        finally
        {
            table.close();
        }
    }

    @Test(expected = RuntimeException.class)
    public void corruptedMemoFilePointer()
                                  throws IOException, CorruptedTableException
    {
        final File pntrErrorDbf = new File("src/test/resources/dbase3plus/tableExceptions/PNTRERR.DBF");
        final Table table = new Table(pntrErrorDbf);

        try
        {
            table.open(IfNonExistent.ERROR);
            table.recordIterator().next();
        }
        finally
        {
            table.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void directoryIsFile()
    {
        final File databaseDirectory = new File("src/test/resources/dbase3plus/tableExceptions/bogusDirectory");
        new Database(databaseDirectory, Version.DBASE_3);
    }

    @Test(expected = InvalidFieldLengthException.class)
    public void writeTooLongField()
                           throws IOException, DbfLibException
    {
        final File outputDir = new File("target/test-output/dbase3plus/exceptions");
        outputDir.mkdirs();

        final File tableFile = new File(outputDir, "FIELDTOOLONG.DBF");
        final List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("CHAR", Type.CHARACTER, 255, 0));

        final Table table = new Table(tableFile, Version.DBASE_3, fields);

        try
        {
            table.open(IfNonExistent.CREATE);

            table.addRecord("this text is not longer than the defined field length, but the field  "
                            + "length exceeds the maximum length of a character field");
        }
        finally
        {
            table.close();
        }
    }

    /**
     * In this test no exception is expected. Goal of this test is simply to test that 'addRecord'
     * method with parameters of field values goes through (this method is not tested in any of the
     * other unit tests)
     */
    @Test
    public void addRecordTest()
                       throws IOException, DbfLibException
    {
        final File outputDir = new File("target/test-output/dbase4/addrecordtest");
        outputDir.mkdirs();

        final File tableFile = new File(outputDir, "ADDRECORDS.DBF");

        final List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("INT", Type.NUMBER, 5, 0));
        fields.add(new Field("DEC", Type.NUMBER, 5, 2));
        fields.add(new Field("CHAR", Type.CHARACTER, 10));
        fields.add(new Field("LOGICAL", Type.LOGICAL, 1));
        fields.add(new Field("FLOAT", Type.FLOAT, 8, 2));

        final Table table = new Table(tableFile, Version.DBASE_4, fields);

        table.open(IfNonExistent.CREATE);

        try
        {
            table.addRecord(12345);
            table.addRecord(0, 12.34);
            table.addRecord(0, 99.99, "TEST", true, 777.77);
        }
        finally
        {
            table.close();
        }
    }
}
