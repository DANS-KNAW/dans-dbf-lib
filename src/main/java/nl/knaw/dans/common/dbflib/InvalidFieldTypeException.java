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
 * Thrown when trying to add a {@link Field} to a {@link Table} when it is not supported by the
 * {@link Version} specified.
 *
 * @author Vesa Åkerman
 */
public class InvalidFieldTypeException
    extends DbfLibException
{
    private static final long serialVersionUID = 1760090922907515668L;

    InvalidFieldTypeException(final String message)
    {
        super(message);
    }
}
