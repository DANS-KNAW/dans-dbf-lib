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
 * Created {@link DataValidator} objects.
 *
 * @author Jan van Mansum
 */
class DataFormatValidatorFactory
{
    static final DataValidator doNothingValidator =
        new DataValidator()
        {
            public void validate(Object typedObject)
                          throws DbfLibException
            {
            }
        };

    static DataValidator createValidator(final Field field)
    {
        switch (field.getType())
        {
            case BINARY:
            case GENERAL:
                return doNothingValidator;

            case CHARACTER:
                return new CharacterFormatValidator(field);

            case DATE:
                return new DateFormatValidator(field);

            case FLOAT:
            case NUMBER:
                return new NumberFormatValidator(field);

            case LOGICAL:
                return new LogicalFormatValidator(field);

            case MEMO:
                return new MemoFormatValidator(field);

            default:
                assert false : "Programming error: Not all Types handled";
        }

        return null;
    }
}
