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

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

class Util
{
    static final int NR_OF_DIGITS_IN_YEAR = 4;

    private Util()
    {
        /*
         * Disallow instantiation.
         */
    }

    static int changeEndianness(final int integerValue)
    {
        boolean isNegative = false;
        int i = integerValue;

        if (i < 0)
        {
            isNegative = true;
            i &= 0x7fffffff;
        }

        int first = i >>> 24;

        if (isNegative)
        {
            first |= 0x80;
        }

        i = integerValue & 0x00ff0000;

        int second = i >>> 16;

        i = integerValue & 0x0000ff00;

        int third = i >>> 8;

        int fourth = integerValue & 0x000000ff;

        return (fourth << 24) + (third << 16) + (second << 8) + first;
    }

    static short changeEndianness(short shortValue)
    {
        boolean isNegative = false;
        short s = shortValue;

        if (s < 0)
        {
            isNegative = true;
            s &= 0x7fff;
        }

        int first = s >>> 8;

        if (isNegative)
        {
            first |= 0x80;
        }

        int second = s & 0x00ff;

        return (short) ((second << 8) + first);
    }

    static int changeEndiannessUnsignedShort(final int integerValue)
    {
        int third = (integerValue & 0x0000ff00) >>> 8;

        int fourth = integerValue & 0x000000ff;

        return (fourth << 8) + third;
    }

    static String stripExtension(final String fileName)
    {
        int pointIndex = fileName.lastIndexOf('.');

        if (pointIndex == -1 || pointIndex == 0 || pointIndex == fileName.length() - 1)
        {
            return fileName;
        }

        return fileName.substring(0, pointIndex);
    }

    /**
     * Given a .DBF file, returns the accompanying .DBT file (.FPT in FOxPro) or <tt>null</tt> if
     * there is none. The base name of the two files must match case sensitively. The case of the
     * characters in the file names' respective extension does not matter. However, if more than one
     * matching file is found (e.g., xxx.Dbt and xxx.dBt and xxx.DBT) <tt>null</tt> is returned.
     *
     * @param dbfFile the .DBF file
     * @return .DBT file or .FPT file
     */
    static File getMemoFile(final File dbfFile, final Version version)
    {
        if (! dbfFile.exists())
        {
            return null;
        }

        final String extension;

        if (version == Version.FOXPRO_26)
        {
            extension = ".fpt";
        }
        else
        {
            extension = ".dbt";
        }

        final String parentDirName = dbfFile.getParent();
        final File parentDir = new File(parentDirName);
        final String dbfBaseName = stripExtension(dbfFile.getName());

        final String[] candidates =
            parentDir.list(new FilenameFilter()
                {
                    public boolean accept(File aDir, String aName)
                    {
                        return dbfBaseName.equalsIgnoreCase(stripExtension(aName))
                               && (aName.toLowerCase().endsWith(extension));
                    }
                });

        if (candidates.length == 1)
        {
            return new File(parentDir, candidates[0]);
        }

        return null;
    }

    /**
     * Writes a <tt>java.lang.String</tt> to a <tt>java.io.DataOutput</tt>. The String is truncated
     * if it exceeds <tt>aLength</tt>. If it is shorter, the remaining bytes are filled with null
     * characters.
     *
     * @param dataOutput the <tt>java.io.DataOutput</tt> to write to
     * @param string the String to write
     * @param length the maximum length of the string
     * @throws java.io.IOException
     */
    static void writeString(final DataOutput dataOutput, final String string, final int length)
                     throws IOException
    {
        char[] charArray = new char[length + 1];
        int lengthString = string.length();
        int i = 0;

        charArray = string.toCharArray();

        for (i = 0; (i < length) && (i < lengthString); i++)
        {
            dataOutput.writeByte(charArray[i]);
        }

        for (; i < length; i++)
        {
            dataOutput.writeByte(0x00);
        }
    }

    static String readString(final DataInput dataInput, final int length)
                      throws IOException
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int c = 0;
        int read = 1; // at least one byte will be read

        while (((c = dataInput.readByte()) != 0) && read < length)
        {
            bos.write(c);
            ++read;
        }

        if (c != 0)
        {
            bos.write(c);
        }

        dataInput.skipBytes(length - read);

        return new String(bos.toByteArray());
    }

    static byte[] readStringBytes(final DataInput dataInput, final int length)
                           throws IOException
    {
        final byte[] array = new byte[length];
        dataInput.readFully(array);

        int index = 0;

        while (index != length && array[index] != 0)
        {
            ++index;
        }

        return Arrays.copyOf(array, index == 0 ? index + 1 : index);
    }

    /**
     * Creates a Date object with the specfied value and the time fields set to zero. Note that
     * month is zero-based. The <tt>java.util.Calendar</tt> class has constants for all the months.
     *
     * @param year the year
     * @param month zero-based month number
     * @param day one-based day number
     *
     * @return a <tt>java.util.Date</tt> object
     */
    static Date createDate(int year, int month, int day)
    {
        final Calendar cal = Calendar.getInstance();

        if (Integer.toString(year).length() > NR_OF_DIGITS_IN_YEAR)
        {
            throw new IllegalArgumentException("Year more than" + NR_OF_DIGITS_IN_YEAR + " digits long");
        }

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Returns the number of digits before the decimal point in a number.
     *
     * @param number the number
     * @return the number of digits
     */
    static int getNumberOfIntDigits(final Number number)
    {
        if (number instanceof Float
                || number instanceof Double
                || number instanceof Short
                || number instanceof Integer
                || number instanceof Long)
        {
            long longValue = number.longValue();

            if (longValue == 0)
            {
                return 1;
            }

            return (int) Math.floor(Math.log10(Math.abs(longValue))) + 1;
        }

        BigInteger bi = null;

        if (number instanceof BigDecimal)
        {
            bi = ((BigDecimal) number).toBigInteger();
        }

        if (number instanceof BigInteger)
        {
            bi = (BigInteger) number;
        }

        return bi.abs().toString().length();
    }

    /**
     * Returns the width in positions of the sign of <tt>aInteger</tt>.
     *
     * @param number
     * @return 1 if <tt>aInteger</tt> is negative, 0 otherwise
     *
     */
    static int getSignWidth(Number number)
    {
        if (number instanceof Float
                || number instanceof Double
                || number instanceof Short
                || number instanceof Integer
                || number instanceof Long)
        {
            return number.longValue() < 0 ? 1 : 0;
        }

        if (number instanceof BigDecimal)
        {
            return ((BigDecimal) number).signum() == -1 ? 1 : 0;
        }

        if (number instanceof BigInteger)
        {
            return ((BigInteger) number).signum() == -1 ? 1 : 0;
        }

        throw new IllegalArgumentException("Unsupported Number type");
    }

    static byte[] repeat(byte byteValue, int times)
    {
        byte[] result = new byte[times];

        for (int i = 0; i < result.length; ++i)
        {
            result[i] = byteValue;
        }

        return result;
    }

    static String createString(final byte[] bytes, final String charsetName)
    {
        try
        {
            return new String(bytes, charsetName);
        }
        catch (final UnsupportedEncodingException unsupportedEncodingException)
        {
            throw new RuntimeException("Programming error: found unsupported charset too late");
        }
    }

    static byte[] getStringBytes(final String string, final String charsetName)
    {
        try
        {
            return string.getBytes(charsetName);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException)
        {
            throw new RuntimeException("Programming error: found unsupported charset too late");
        }
    }
}
