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
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a Date value in a record.
 *
 * @author Jan van Mansum
 */
public class DateValue
    extends Value
{
    private static final int RECORD_DATE_LENGTH = 8;
    private static final Format dateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * Creates a new DateValue object.
     *
     * @param dateValue a {@link Date} object
     */
    public DateValue(final Date dateValue)
    {
        super(dateValue);
    }

    DateValue(final Field field, final byte[] rawValue)
    {
        super(field, rawValue);
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        final String yearString = new String(rawValue, 0, 4);
        final String monthString = new String(rawValue, 4, 2);
        final String dayString = new String(rawValue, 6, 2);
        final Calendar cal = Calendar.getInstance();

        if (yearString.trim().isEmpty())
        {
            return null;
        }
        else
        {
            final int year = Integer.parseInt(yearString);
            final int month = Integer.parseInt(monthString) - 1;
            final int day = Integer.parseInt(dayString);

            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        return cal.getTime();
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
                            throws ValueTooLargeException
    {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(RECORD_DATE_LENGTH);

        try
        {
            byteArrayOutputStream.write(dateFormat.format(typed).getBytes());
        }
        catch (final IOException ioException)
        {
            assert false : "Writing to ByteArrayOutputStream should not cause an IOException";

            /*
             * It should never happen, but if it does, we want to know about it.
             */
            throw new RuntimeException(ioException);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
