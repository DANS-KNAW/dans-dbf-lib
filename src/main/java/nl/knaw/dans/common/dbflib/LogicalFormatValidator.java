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

import java.util.regex.Pattern;

/**
 * @author Jan van Mansum
 */
class LogicalFormatValidator
    extends AbstractDataValidator
{
    private static final Pattern booleanPattern = Pattern.compile("[YNTF ]");

    LogicalFormatValidator(final Field field)
    {
        super(field);
        assert field.getType() == Type.LOGICAL : "Can only be validator for LOGICAL fields";
    }

    /**
     * {@inheritDoc}
     * <p>
     * For a LOGICAL field a {@link Boolean}, or a {@link String} is acceptable. A
     * <code>String</code> is acceptable only if it contains one of Y, N, T, F. The String must not
     * contain leading or trailing spaces.
     */
    public void validate(final Object typedObject)
                  throws DbfLibException
    {
        if (typedObject instanceof Boolean)
        {
            return;
        }

        if (typedObject instanceof String)
        {
            final String booleanString = (String) typedObject;

            if (! booleanPattern.matcher(booleanString).matches())
            {
                throw new DataMismatchException("Boolean must be one of Y, N, T, F or a space");
            }

            return;
        }

        throw new DataMismatchException("Cannot write objects of type '" + typedObject.getClass().getName()
                                        + "' to a LOGICAL field");
    }
}
