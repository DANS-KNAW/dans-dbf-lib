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
 * Thrown when trying to add a record that contains values that are too large for their designated
 * fields. Note that numbers that are considered too large only if the number of digits before the
 * decimal point exceeds the number number that fit in the field; too many decimal digits will
 * simply be rounded.
 * <p>
 * Note, too, that dBase III handles NUMBER values internally as floating point values. It
 * <em>will</em> display the values as found in the DBF file but if they are too large or contain
 * too many digits dBase will display an error message or round the value when trying to process it
 * (e.g. if you try to save the value again in the dBase program). The same is true for dBase IV and
 * V and FLOAT values.
 *
 * @author Jan van Mansum
 */
public class ValueTooLargeException
    extends DbfLibException
{
    private static final long serialVersionUID = -9205029469017433931L;

    ValueTooLargeException(final String message)
    {
        super(message);
    }
}
