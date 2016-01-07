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
