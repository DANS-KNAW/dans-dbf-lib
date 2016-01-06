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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests that exceptions are thrown as expected when dealing with CHARACTER fields.
 *
 * @author Vesa Åkerman
 * @author Jan van Mansum
 */
@RunWith(Parameterized.class)
public class TestCharacterExceptions
    extends BaseTestcase
{
    private Table table;

    /**
     * Creates a new TestCharacterExceptions object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestCharacterExceptions(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    @Before
    public void setUp()
               throws IOException, CorruptedTableException, InvalidFieldTypeException, InvalidFieldLengthException
    {
        final String outputDir = "target/test-output/" + versionDirectory + "/exceptions";
        UnitTestUtil.recreateDirectory(outputDir);

        final File tableFile = new File(outputDir + "/WRITECHAR.DBF");

        final List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("CHAR1", Type.CHARACTER, 20, 0));
        fields.add(new Field("CHAR2", Type.CHARACTER, 254, 0));

        table = new Table(tableFile, version, fields);
        table.open(IfNonExistent.CREATE);
    }

    @After
    public void tearDown()
                  throws IOException
    {
        table.close();
    }

    @Test
    public void fitsComfortably()
                         throws IOException, DbfLibException
    {
        table.addRecord("Less than 20", "This is not at all long");
    }

    @Test
    public void fitsExactly()
                     throws IOException, DbfLibException
    {
        table.addRecord("This is exactly 20 c",
                        "This is exactly 254 characters, which is the limit for character fields in DBase products"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx!!!");
    }

    @Test(expected = ValueTooLargeException.class)
    public void firstFieldDoesNotFit()
                              throws IOException, DbfLibException
    {
        table.addRecord("This is more than 20 characters", "This long field is ok");
    }

    @Test(expected = ValueTooLargeException.class)
    public void secondFieldDoesNotFit()
                               throws IOException, DbfLibException
    {
        table.addRecord("This is ok",
                        "This is more than 254 characters, which is the limit for character fields in DBase products"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                        + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx!!!");
    }
}
