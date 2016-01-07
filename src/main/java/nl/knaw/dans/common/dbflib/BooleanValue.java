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
 * Represents a boolean value in a record.
 *
 * @author Jan van Mansum
 */
public class BooleanValue
    extends Value
{
    /**
     * Creates a new <code>BooleanValue</code> object.
     *
     * @param booleanValue a boolean
     */
    public BooleanValue(final Boolean booleanValue)
    {
        super(booleanValue);
    }

    BooleanValue(final Field field, final byte[] rawValue)
    {
        super(field, rawValue);
    }

    @Override
    protected Object doGetTypedValue(final byte[] rawValue)
    {
        char c = (char) rawValue[0];

        if (c == ' ')
        {
            return null;
        }

        return (c == 'Y') || (c == 'y') || (c == 'T') || (c == 't');
    }

    @Override
    protected byte[] doGetRawValue(final Field field)
                            throws ValueTooLargeException
    {
        if (Boolean.TRUE.equals(typed))
        {
            return new byte[] { 'T' };
        }

        return new byte[] { 'F' };
    }
}
