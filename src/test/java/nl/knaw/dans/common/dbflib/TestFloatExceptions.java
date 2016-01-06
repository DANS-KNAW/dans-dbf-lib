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
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Test that float exceptions are thrown in the appropriate cases.
 *
 * @author Vesa Åkerman
 */
@RunWith(Parameterized.class)
public class TestFloatExceptions
    extends BaseTestcase
{
    private Table table;

    /**
     * Creates a new TestFloatExceptions object.
     *
     * @param aVersion test parameter
     * @param aVersionDirectory test parameter
     */
    public TestFloatExceptions(final Version aVersion, final String aVersionDirectory)
    {
        super(aVersion, aVersionDirectory);
    }

    /**
     * No dBase III+, because it has no FLOAT type.
     */
    @Parameters
    public static Collection<Object[]> data()
    {
        final Object[][] testParameters =
            new Object[][]
            {
                { Version.DBASE_4, "dbase4" },
                { Version.DBASE_5, "dbase5" },
                { Version.FOXPRO_26, "FoxPro26" }
            };

        return Arrays.asList(testParameters);
    }

    @Before
    public void setUp()
               throws IOException, CorruptedTableException, InvalidFieldTypeException, InvalidFieldLengthException
    {
        final String outputDir = "target/test-output/" + versionDirectory + "/exceptions";
        UnitTestUtil.recreateDirectory(outputDir);

        final File tableFile = new File(outputDir + "/WRITEFLOAT.DBF");

        final List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("FLOAT_1", Type.FLOAT, 20, 0));
        fields.add(new Field("FLOAT_2", Type.FLOAT, 20, 1));
        fields.add(new Field("FLOAT_3", Type.FLOAT, 20, 18));

        table = new Table(tableFile, version, fields);
        table.open(IfNonExistent.CREATE);
    }

    @After
    public void tearDown()
                  throws IOException
    {
        table.close();
    }

    @Test(expected = ValueTooLargeException.class)
    public void tooBigIntegerValue()
                            throws IOException, DbfLibException
    {
        table.addRecord(new BigInteger("99999999999999999999999"),
                        0.0,
                        0.0);
    }

    @Test(expected = ValueTooLargeException.class)
    public void tooBigDecimalValue()
                            throws IOException, DbfLibException
    {
        table.addRecord(0, 0.0, 9.999999999999999999);
    }
}
