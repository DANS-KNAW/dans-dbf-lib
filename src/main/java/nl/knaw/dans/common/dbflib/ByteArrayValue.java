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


/**
 * Represents a binary value in a record. The typed and untyped values are both the same byte array.
 *
 * @author Jan van Mansum
 */
public class ByteArrayValue
    extends Value
{
    /**
     * Creates a new <code>ByteArrayValue</code> object.
     *
     * @param byteArrayValue the byte array value
     */
    public ByteArrayValue(final byte[] byteArrayValue)
    {
        super((Object) byteArrayValue);
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        return rawValue;
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
                            throws ValueTooLargeException
    {
        /*
         * The 'typed' value IS a byte[] in this particular subclass.
         */
        return (byte[]) typed;
    }
}
