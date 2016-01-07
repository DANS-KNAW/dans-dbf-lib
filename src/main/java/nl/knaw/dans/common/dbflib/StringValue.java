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
import java.nio.charset.Charset;

/**
 * Represents a string value in a record (a CHARACTER or MEMO type field value).
 *
 * @author Jan van Mansum
 */
public class StringValue
    extends Value
{
    private final String charsetName;
    static final int MAX_CHARFIELD_LENGTH_DBASE = 253;

    /**
     * Creates a new StringValue object.
     *
     * @param stringValue aString
     * @param charsetName the character set to use when encoding and decoding this string value
     */
    public StringValue(final String stringValue, final String charsetName)
    {
        super(stringValue);
        this.charsetName = charsetName;

        Charset.forName(charsetName);
    }

    /**
     * Creates a new string value that uses the platform's default character set.
     *
     * @param stringValue
     */
    public StringValue(final String stringValue)
    {
        this(stringValue,
             Charset.defaultCharset().name());
    }

    StringValue(final Field field, final byte[] rawValue, final String charsetName)
    {
        super(field, rawValue);
        this.charsetName = charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(rawValue.length);

        for (int i = 0; i < rawValue.length; ++i)
        {
            if (lookingAtSoftReturn(rawValue, i))
            {
                ++i;

                continue;
            }

            bos.write(rawValue[i]);
        }

        return Util.createString(bos.toByteArray(),
                                 charsetName);
    }

    private static boolean lookingAtSoftReturn(final byte[] buffer, final int index)
    {
        return index < buffer.length - 1 && buffer[index] == (byte) 0x8d && buffer[index + 1] == (byte) 0x0a;
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
                            throws ValueTooLargeException
    {
        final int fieldLength = field.getLength();
        final byte[] stringBytes = Util.getStringBytes((String) typed, charsetName);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(fieldLength);

        try
        {
            byteArrayOutputStream.write(stringBytes);

            /*
             * Memo data has no maximum length, so we cannot fill up the rest of the field with
             * zero's, as with the other fields. The maximum length of the memo field refers to the
             * DBT entry pointer length in the DBF.
             */
            if (field.getType() != Type.MEMO)
            {
                byteArrayOutputStream.write(Util.repeat((byte) 0x00, fieldLength - stringBytes.length));
            }
        }
        catch (final IOException ioException)
        {
            assert false : "Writing to ByteArrayOutputStream should not cause an IOException";

            throw new RuntimeException(ioException);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
