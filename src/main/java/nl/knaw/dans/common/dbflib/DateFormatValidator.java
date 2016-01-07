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
import java.util.regex.Pattern;

/**
 * @author Jan van Mansum
 */
class DateFormatValidator
    extends AbstractDataValidator
{
    private final Pattern datePattern = Pattern.compile("\\d{8,8}");

    DateFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.DATE : "Can only be validator for DATE fields";
    }

    /**
     * {@inheritDoc}
     *
     * For DATE fields {@link Date} and {@link String} objects are acceptable. <code>String</code>
     * objects are trimmed before validating them and only the format of the date is checked, not
     * whether the date itself is valid (e.g., 20090229 will be accepted, even though it is
     * invalid).
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof Date)
        {
            return;
        }

        if (typedObject instanceof String)
        {
            final String dateString = (String) typedObject;

            if (! datePattern.matcher(dateString).matches())
            {
                throw new DataMismatchException("'" + typedObject
                                                + " is not in the correct date format for DBF (YYYYMMDD)");
            }

            return;
        }

        throw new DataMismatchException("Cannot write objects of type '" + typedObject.getClass().getName()
                                        + "' to a DATE field");
    }
}
