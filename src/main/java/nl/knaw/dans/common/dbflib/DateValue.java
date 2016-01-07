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
