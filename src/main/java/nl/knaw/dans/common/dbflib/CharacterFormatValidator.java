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

import java.util.Date;

/**
 * @author Jan van Mansum
 */
class CharacterFormatValidator
    extends AbstractDataValidator
{
    /**
     * Creates a new CharacterFormatValidator object.
     *
     * @param field the field for which to do validation
     */
    public CharacterFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.CHARACTER : "Can only be validator for CHARACTER fields";
    }

    /**
     * {@inheritDoc}
     *
     * For a CHARACTER field are acceptable: {@link String}, {@link Number},
     * {@link Date} and {@link Boolean} values.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof String || typedObject instanceof Number)
        {
            if (typedObject.toString().length() > field.getLength())
            {
                throw new ValueTooLargeException("Value too large to it in field");
            }

            return;
        }
        else if (typedObject instanceof Date)
        {
            if (field.getLength() < Type.DATE.getLength())
            {
                throw new ValueTooLargeException("Field too short to contain a date");
            }

            return;
        }
        else if (typedObject instanceof Boolean)
        {
            return;
        }

        throw new DataMismatchException("Cannot add value of type " + typedObject.getClass().getName());
    }
}
