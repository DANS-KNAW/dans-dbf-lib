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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.io.File;
import java.math.BigInteger;

/**
 * Tests the utility functions in the <tt>Util</tt> class.
 *
 * @author Jan van Mansum
 */
public class TestUtil
{
    /**
     * Tests <tt>Util.changeEndianness(int)</tt>.
     */
    @Test
    public void changeEndianness_integer()
    {
        int le;
        int be;

        /*
         * Simple case.
         */
        le = (0x01 << 24) + (0x02 << 16) + (0x03 << 8) + 0x04;
        be = (0x04 << 24) + (0x03 << 16) + (0x02 << 8) + 0x01;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Negative bit set.
         */
        le = (0x80 << 24) + (0x02 << 16) + (0x03 << 8) + 0x04;
        be = (0x04 << 24) + (0x03 << 16) + (0x02 << 8) + 0x80;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Negative bit becomes set.
         */
        le = (0x04 << 24) + (0x03 << 16) + (0x02 << 8) + 0x80;
        be = (0x80 << 24) + (0x02 << 16) + (0x03 << 8) + 0x04;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Border cases.
         */
        assertEquals(0,
                     Util.changeEndianness(0));
        assertEquals(0xFFFFFFFF,
                     Util.changeEndianness(0xFFFFFFFF));
    }

    /**
     * Tests <tt>Util.changeEndianness(short)</tt>.
     */
    @Test
    public void changeEndianness_short()
    {
        short le;
        short be;

        /*
         * Simple case.
         */
        le = (0x01 << 8) + 0x02;
        be = (0x02 << 8) + 0x01;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Negative bit set.
         */
        le = (short) (0x80 << 8) + 0x01;
        be = (0x01 << 8) + 0x80;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Negative bit becomes set.
         */
        le = (0x01 << 8) + 0x80;
        be = (short) (0x80 << 8) + 0x01;
        assertEquals(be,
                     Util.changeEndianness(le));

        /*
         * Border cases.
         */
        assertEquals((short) 0,
                     Util.changeEndianness((short) 0));
        assertEquals((short) 0xFFFF,
                     Util.changeEndianness((short) 0xFFFF));
    }

    /**
     * Test a simple case for <tt>Util.getDbtFile</tt>.
     */
    @Test
    public void getDbtFile_simple_case()
    {
        File dbtFile = Util.getMemoFile(new File("src/test/resources/util/get_dbt_file1/x.DBF"),
                                        Version.DBASE_5);

        assertNotNull(".DBT file is null", dbtFile);
        assertEquals("x.DBT",
                     dbtFile.getName());
    }

    /**
     * See test method name.
     */
    @Test
    public void getDbtFile_returns_null_if_dbt_non_existent()
    {
        File dbtFile = Util.getMemoFile(new File("src/test/resources/util/get_dbt_file2/x.DBF"),
                                        Version.DBASE_5);

        assertNull("Found non-existing .DBT", dbtFile);
    }

    /**
     * See test method name.
     */
    @Test
    public void getDbtFile_returns_null_if_dbf_non_existent()
    {
        File dbtFile = Util.getMemoFile(new File("src/test/resources/util/get_dbt_file3/x.DBF"),
                                        Version.DBASE_5);
        assertNull("Found non-existing .DBT", dbtFile);
    }

    /**
     * See test method name.
     */
    @Test
    public void getDbtFile_if_no_directory()
    {
        File dbtFile = Util.getMemoFile(new File("x.DBF"),
                                        Version.DBASE_5);
        assertNull("Found non-existing .DBT", dbtFile);
    }

    /**
     * See test method name.
     */
    @Test
    public void getNumberOfDigits()
    {
        assertEquals(1,
                     Util.getNumberOfIntDigits(0));
        assertEquals(1,
                     Util.getNumberOfIntDigits(1));
        assertEquals(1,
                     Util.getNumberOfIntDigits(2));
        assertEquals(1,
                     Util.getNumberOfIntDigits(9));
        assertEquals(2,
                     Util.getNumberOfIntDigits(10));
        assertEquals(2,
                     Util.getNumberOfIntDigits(99));
        assertEquals(3,
                     Util.getNumberOfIntDigits(100));
        assertEquals(6,
                     Util.getNumberOfIntDigits(999999));
        assertEquals(7,
                     Util.getNumberOfIntDigits(1000000));
        assertEquals(7,
                     Util.getNumberOfIntDigits(-1000000));
        assertEquals(10,
                     Util.getNumberOfIntDigits(Integer.MAX_VALUE));
        assertEquals(50,
                     Util.getNumberOfIntDigits(new BigInteger("12345678901234567890123456789012345678901234567890")));

        assertEquals(1,
                     Util.getNumberOfIntDigits(0L));
        assertEquals(15,
                     Util.getNumberOfIntDigits(123456789012345L));
        assertEquals(15,
                     Util.getNumberOfIntDigits(-123456789012345L));
        assertEquals(19,
                     Util.getNumberOfIntDigits(Long.MAX_VALUE));

        assertEquals(15,
                     Util.getNumberOfIntDigits(new BigInteger("123456789012345")));
        assertEquals(15,
                     Util.getNumberOfIntDigits(new BigInteger("-123456789012345")));
        assertEquals(30,
                     Util.getNumberOfIntDigits(new BigInteger("123456789012345678901234567890")));
    }
}
