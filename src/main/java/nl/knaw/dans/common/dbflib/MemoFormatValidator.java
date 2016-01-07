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
class MemoFormatValidator
    extends AbstractDataValidator
{
    MemoFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.MEMO : "Can only be validator for MEMO fields";
    }

    /**
     * {@inheritDoc}
     * <p>
     * For a MEMO field values of types {@link String}, {@link Boolean}, {@link Date} and
     * {@link Number} are acceptable.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof String
                || typedObject instanceof Boolean
                || typedObject instanceof Date
                || typedObject instanceof Number)
        {
            return;
        }

        throw new DataMismatchException("Cannot write value of type " + typedObject.getClass().getName());
    }
}
